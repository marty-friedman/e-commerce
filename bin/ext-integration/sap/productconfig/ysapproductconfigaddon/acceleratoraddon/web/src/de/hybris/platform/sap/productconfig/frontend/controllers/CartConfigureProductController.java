/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.frontend.controllers;

import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.KBKeyData;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;
import de.hybris.platform.sap.productconfig.frontend.constants.SapproductconfigfrontendWebConstants;
import de.hybris.platform.servicelayer.exceptions.BusinessException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Controller for Cart-Configuration integration
 */
@Controller
@RequestMapping()
public class CartConfigureProductController extends AbstractProductConfigController
{

	@Resource(name = "cartFacade")
	private CartFacade cartFacade;
	private static final Logger LOGGER = Logger.getLogger(CartConfigureProductController.class);

	/**
	 * Prepares a configuration session for the given cart item. This includes re-creation of the configuration sessions,
	 * in case it was already releases, as well as restoring of the UI-State.
	 *
	 * @param entryNumber
	 *           of the configurable cart item
	 * @param model
	 *           vie model
	 * @param request
	 *           http request
	 * @return view name
	 * @throws CommerceCartModificationException
	 */
	@RequestMapping(value = "cart/{entryNumber}/configuration/" + SapproductconfigfrontendWebConstants.CONFIGURATOR_TYPE)
	public String configureCartEntry(@PathVariable("entryNumber") final int entryNumber, final Model model,
			final HttpServletRequest request) throws CommerceCartModificationException
	{
		final CartData sessionCart = getCartFacade().getSessionCart();
		if (CollectionUtils.isEmpty(sessionCart.getEntries()))
		{
			// user clicked logout ==> cart empty
			return REDIRECT_PREFIX + ROOT;
		}
		OrderEntryData currentEntry;
		try
		{
			currentEntry = getOrderEntry(entryNumber, sessionCart);
		}
		catch (final BusinessException bex)
		{
			throw new CommerceCartModificationException("Could not find cart entry!", bex);
		}

		final String cartItemHandle = currentEntry.getItemPK();
		final String productCode = currentEntry.getProduct().getCode();

		final KBKeyData kbKey = new KBKeyData();
		kbKey.setProductCode(productCode);
		final UiStatus uiStatus = getUiStatusFromSession(cartItemHandle, kbKey, currentEntry.getProduct());
		if (uiStatus == null)
		{
			return REDIRECT_PREFIX + ROOT;
		}

		try
		{
			populateConfigurationModel(request, model, currentEntry, productCode);
		}
		catch (final CMSItemNotFoundException cnfe)
		{
			throw new CommerceCartModificationException("Root cause: CMSItemNotFoundException", cnfe);
		}

		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug("Retrieve content for cartEntry via GET ('" + cartItemHandle + "')");
			LOGGER.debug("Current Session: '" + getSessionAccessFacade().getSessionId() + "'");
		}

		return SapproductconfigfrontendWebConstants.CONFIG_PAGE_VIEW_NAME;
	}

	protected void populateConfigurationModel(final HttpServletRequest request, final Model model,
			final OrderEntryData currentEntry, final String productCode) throws CMSItemNotFoundException
	{
		model.addAttribute(WebConstants.BREADCRUMBS_KEY,
				getBreadcrumbBuilder().getBreadcrumbsForConfigFromCart(productCode, currentEntry.getEntryNumber()));
		populateCMSAttributes(model);

		if (model.containsAttribute(SapproductconfigfrontendWebConstants.CONFIG_ATTRIBUTE))
		{
			return;
		}

		final ProductData productData = populateProductData(productCode, model, request);
		final KBKeyData kbKey = createKBKeyForProduct(productData);

		UiStatus uiStatus = getUiStatusFromSession(currentEntry.getItemPK(), kbKey, productData);
		ifProductVariant(request, model, productData, kbKey);
		final ConfigurationData configData = reloadConfiguration(kbKey, uiStatus);

		// assume currentEntry != null
		configData.setCartItemPK(currentEntry.getItemPK());
		configData.setQuantity(currentEntry.getQuantity().longValue());

		model.addAttribute(SapproductconfigfrontendWebConstants.CONFIG_ATTRIBUTE, configData);

		final BindingResult errors = getBindingResultForConfigAndSaveUiStatus(configData, uiStatus);

		configData.setAutoExpand(true);

		final UiGroupData expandedGroup = getUiStateHandler().handleAutoExpand(configData, uiStatus);
		if (expandedGroup != null)
		{
			uiStatus.setGroupIdToDisplay(expandedGroup.getId());
			getUiStateHandler().compileGroupForDisplay(configData, uiStatus);
		}
		uiStatus = getUiStatusSync().extractUiStatusFromConfiguration(configData);
		getSessionAccessFacade().setUiStatusForProduct(configData.getKbKey().getProductCode(), uiStatus);
		getSessionAccessFacade().setCartEntryForProduct(configData.getKbKey().getProductCode(), currentEntry.getItemPK());
		getSessionAccessFacade().setConfigIdForCartEntry(currentEntry.getItemPK(), configData.getConfigId());

		getUiRecorder().recordUiAccessFromCart(configData, productCode);

		model.addAttribute(BindingResult.MODEL_KEY_PREFIX + SapproductconfigfrontendWebConstants.CONFIG_ATTRIBUTE, errors);

		getUiStateHandler().handleConflictSolverMessage(uiStatus, getUiStatusSync().getNumberOfConflicts(configData), model);
		getUiStateHandler().handleProductConfigMessages(configData, model);
	}

	/**
	 * Retrieves UI status based on a configuration attached to a cart entry
	 *
	 * @param cartItemHandle
	 * @param kbKey
	 * @return Null if no UI status could be created.
	 */
	protected UiStatus getUiStatusFromSession(final String cartItemHandle, final KBKeyData kbKey, final ProductData productData)
	{
		UiStatus uiStatus = getSessionAccessFacade().getUiStatusForCartEntry(cartItemHandle);

		//this shall happen only when the cart is restored or if variant in cart is switched to KMAT (clicking change cfg for variant in cart)
		if (uiStatus == null)
		{
			final String configId = getSessionAccessFacade().getConfigIdForCartEntry(cartItemHandle);
			final ConfigurationData confData = getConfigDataForRestoredProduct(kbKey, productData, configId, cartItemHandle);
			if (confData == null)
			{
				return null;
			}

			logModelmetaData(confData);

			getUiStatusSync().setInitialStatus(confData);
			uiStatus = getUiStatusSync().extractUiStatusFromConfiguration(confData);
			getSessionAccessFacade().setUiStatusForCartEntry(cartItemHandle, uiStatus);
		}
		return uiStatus;
	}

	/**
	 * Fetches a configuration which might already reside in the session (configId != null) or which needs to be created
	 * from the external configuration attached to a cart entry.
	 *
	 * @param kbKey
	 * @param configId
	 * @param cartItemHandle
	 * @return Null if no configuration could be created
	 */
	protected ConfigurationData getConfigDataForRestoredProduct(final KBKeyData kbKey, final ProductData productData,
			final String configId, final String cartItemHandle)
	{
		ConfigurationData confData;
		if (configId == null)
		{
			confData = this.loadNewConfiguration(kbKey, productData, cartItemHandle);
		}
		else
		{
			confData = this.getConfigData(kbKey, configId);
		}
		return confData;
	}

	protected CartFacade getCartFacade()
	{
		return cartFacade;
	}

	/**
	 * @param cartFacade
	 *           injects the cart facade for interacting with the cart
	 */
	public void setCartFacade(final CartFacade cartFacade)
	{
		this.cartFacade = cartFacade;
	}
}
