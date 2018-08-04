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
package de.hybris.platform.sap.productconfig.facades.impl;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.sap.productconfig.facades.ConfigurationCartIntegrationFacade;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.KBKeyData;
import de.hybris.platform.sap.productconfig.facades.populator.ConfigurationOrderEntryProductInfoModelPopulator;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPricingOrderIntegrationService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link ConfigurationCartIntegrationFacade}
 */
public class ConfigurationCartIntegrationFacadeImpl extends ConfigurationBaseFacadeImpl
		implements ConfigurationCartIntegrationFacade
{
	private CartService cartService;
	private ModelService modelService;
	private CommerceCartService commerceCartService;
	private ProductConfigurationPricingOrderIntegrationService configurationPricingOrderIntegrationService;
	private ConfigurationOrderEntryProductInfoModelPopulator configInfoPopulator;

	private static final Logger LOG = Logger.getLogger(ConfigurationCartIntegrationFacadeImpl.class);

	@Override
	public String addConfigurationToCart(final ConfigurationData configContent) throws CommerceCartModificationException
	{
		final ProductModel product = getProductService().getProductForCode(configContent.getKbKey().getProductCode());

		final AbstractOrderEntryModel cartItem = getOrCreateCartItem(product, configContent);

		final String key = cartItem.getPk().toString();

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Added product '" + product.getCode() + "' with configId '" + configContent.getConfigId()
					+ "' to cart with quantity '" + cartItem.getQuantity() + "', referenced by cart entry PK '" + key + "'");
		}
		return key;
	}

	/**
	 * Creates a new entry in the session cart or returns the entry belonging to the current configuration and updates
	 * the price and its external configuration. The link between cart entry and configuration is established via
	 * {@link ConfigurationData#getCartItemPK()}
	 *
	 * @param product
	 * @param configData
	 *           DTO representation of configuration runtime instance
	 * @return Corresponding cart entry model
	 * @throws CommerceCartModificationException
	 */
	protected AbstractOrderEntryModel getOrCreateCartItem(final ProductModel product, final ConfigurationData configData)
			throws CommerceCartModificationException
	{
		final String pkString = configData.getCartItemPK();
		final PK cartItemPk = convertStringToPK(pkString);
		AbstractOrderEntryModel cartItem = findItemInCartByPK(cartItemPk);
		final CommerceCartParameter commerceCartParameter = new CommerceCartParameter();
		final CartModel cart = getCartService().getSessionCart();
		if (cartItem == null)
		{
			cartItem = createCartItem(product, configData, commerceCartParameter, cart);
		}
		else
		{
			updateCartItem(product, configData, cartItem, commerceCartParameter, cart);
		}
		addConfigAttributesToCartEntry(cartItem);
		return cartItem;
	}

	protected AbstractOrderEntryModel createCartItem(final ProductModel product, final ConfigurationData configData,
			final CommerceCartParameter commerceCartParameter, final CartModel cart) throws CommerceCartModificationException
	{
		AbstractOrderEntryModel cartItem;
		fillCommerceCartParameterForAddToCart(commerceCartParameter, cart, product,
				configData.getQuantity() == 0 ? 1 : configData.getQuantity(), product.getUnit(), true, configData.getConfigId());
		final CommerceCartModification commerceItem = getCommerceCartService().addToCart(commerceCartParameter);
		cartItem = commerceItem.getEntry();
		return cartItem;
	}

	protected void updateCartItem(final ProductModel product, final ConfigurationData configData,
			final AbstractOrderEntryModel cartItem, final CommerceCartParameter commerceCartParameter, final CartModel cart)
	{
		getConfigurationPricingOrderIntegrationService().updateCartEntryProduct(cartItem, product, configData.getConfigId());
		fillCommerceCartParameterForUpdate(commerceCartParameter, cart, configData.getConfigId());
		getConfigurationPricingOrderIntegrationService().updateCartEntryPrices(cartItem, true, commerceCartParameter);
		getConfigurationPricingOrderIntegrationService().updateCartEntryExternalConfiguration(commerceCartParameter, cartItem);
	}

	protected void addConfigAttributesToCartEntry(final AbstractOrderEntryModel entry)
	{
		final List<AbstractOrderEntryProductInfoModel> configInlineModels = new ArrayList<>();
		final String configId = getSessionAccessService().getConfigIdForCartEntry(entry.getPk().toString());
		final ConfigModel configModel = getConfigurationService().retrieveConfigurationModel(configId);

		getConfigInfoPopulator().populate(configModel, configInlineModels);
		linkEntryWithConfigInfos(entry, configInlineModels);

		getConfigurationPricingOrderIntegrationService().fillSummaryMap(entry);
		modelService.save(entry);
	}

	protected void linkEntryWithConfigInfos(final AbstractOrderEntryModel entry,
			final List<AbstractOrderEntryProductInfoModel> configInlineModels)
	{
		entry.setProductInfos(configInlineModels);
		for (final AbstractOrderEntryProductInfoModel infoModel : configInlineModels)
		{
			infoModel.setOrderEntry(entry);
		}
	}

	/* fills CommerceCartParameter Object for addToCart */
	protected void fillCommerceCartParameterForAddToCart(final CommerceCartParameter parameter, final CartModel cart,
			final ProductModel product, final long l, final UnitModel unit, final boolean forceNewEntry, final String configId)
	{
		parameter.setEnableHooks(true);
		parameter.setCart(cart);
		parameter.setProduct(product);
		parameter.setQuantity(l);
		parameter.setUnit(unit);
		parameter.setCreateNewEntry(forceNewEntry);
		parameter.setConfigId(configId);
	}


	/**
	 * @param parameter
	 * @param sessionCart
	 * @param configId
	 */
	protected void fillCommerceCartParameterForUpdate(final CommerceCartParameter parameter, final CartModel sessionCart,
			final String configId)
	{
		parameter.setEnableHooks(true);
		parameter.setCart(sessionCart);
		parameter.setConfigId(configId);

	}

	/**
	 * Converts a string to the primary key wrapping it
	 *
	 * @param pkString
	 * @return Primary key
	 */
	protected PK convertStringToPK(final String pkString)
	{
		final PK cartItemPk;
		if (pkString != null && !pkString.isEmpty())
		{
			cartItemPk = PK.parse(pkString);
		}
		else
		{
			cartItemPk = PK.NULL_PK;
		}
		return cartItemPk;
	}

	/**
	 * Searches the session cart for an entry specified by a primary key. In case nothing is found, null is returned.
	 *
	 * @param cartItemPk
	 *           Entry key
	 * @return Corresponding order entry model
	 */
	@Override
	public AbstractOrderEntryModel findItemInCartByPK(final PK cartItemPk)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Search for cartItem with PK '" + cartItemPk + "'");
		}

		if (cartItemPk == null || PK.NULL_PK.equals(cartItemPk))
		{
			return null;
		}

		final Optional<AbstractOrderEntryModel> cartEntry = getCartService().getSessionCart().getEntries().stream()
				.filter(entry -> entry.getPk().equals(cartItemPk) && !getModelService().isRemoved(entry)).findFirst();
		if (cartEntry.isPresent())
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("cartItem found for PK '" + cartItemPk + "'");
			}

			return cartEntry.get();
		}
		return null;
	}

	@Override
	public boolean isItemInCartByKey(final String key)
	{
		final PK cartItemPK = PK.parse(key);
		final AbstractOrderEntryModel item = findItemInCartByPK(cartItemPK);

		final boolean itemExistsInCart = item != null;

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Item with key '" + key + "' exists in cart: '" + itemExistsInCart + "'");
		}

		return itemExistsInCart;
	}

	@Override
	public String copyConfiguration(final String configId)
	{
		return copyConfiguration(configId, null);
	}

	@Override
	public String copyConfiguration(final String configId, final String productCode)
	{
		final ProductConfigurationService configurationService = getConfigurationService();
		// We do a copy of the configuration, as we want the CFG session
		// to stay in the hybris session for later reconfiguration
		final String externalConfiguration = configurationService.retrieveExternalConfiguration(configId);
		final ConfigModel configModel = configurationService.retrieveConfigurationModel(configId);
		String hybrisProductCode = productCode;
		if (hybrisProductCode == null)
		{
			hybrisProductCode = configModel.getRootInstance().getName();
		}
		final KBKey kbKey = new KBKeyImpl(hybrisProductCode);
		final ConfigModel newConfiguration = configurationService.createConfigurationFromExternal(kbKey, externalConfiguration);
		return newConfiguration.getId();
	}

	@Override
	public void resetConfiguration(final String configId)
	{
		getConfigurationService().releaseSession(configId);
	}

	@Override
	public ConfigurationData restoreConfiguration(final KBKeyData kbKey, final String cartEntryKey)
	{
		final PK cartItemPK = PK.parse(cartEntryKey);
		final AbstractOrderEntryModel item = findItemInCartByPK(cartItemPK);
		if (item == null)
		{
			LOG.warn("Probably multi-session issue: Item not found in cart for key: " + cartEntryKey);
			return null;
		}
		final String extConfig = item.getExternalConfiguration();
		ConfigModel configurationRuntimeModel;
		if (extConfig == null || extConfig.isEmpty())
		{
			configurationRuntimeModel = getConfigurationService().createDefaultConfiguration(
					new KBKeyImpl(kbKey.getProductCode(), kbKey.getKbName(), kbKey.getKbLogsys(), kbKey.getKbVersion()));
		}
		else
		{
			configurationRuntimeModel = getConfigurationService().createConfigurationFromExternal(
					new KBKeyImpl(kbKey.getProductCode(), kbKey.getKbName(), kbKey.getKbLogsys(), kbKey.getKbVersion()), extConfig);
		}

		return convert(kbKey, configurationRuntimeModel);
	}

	protected CartService getCartService()
	{
		return cartService;
	}

	/**
	 * @param cartService
	 *           injects the cart service for interaction with the cart
	 */
	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           injects the hybris model service
	 */
	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected CommerceCartService getCommerceCartService()
	{
		return commerceCartService;
	}

	/**
	 * @param commerceCartService
	 *           injects the commerce cart service
	 */
	@Required
	public void setCommerceCartService(final CommerceCartService commerceCartService)
	{
		this.commerceCartService = commerceCartService;
	}

	protected ConfigurationOrderEntryProductInfoModelPopulator getConfigInfoPopulator()
	{
		return configInfoPopulator;
	}

	/**
	 * @param configInfoPopulator
	 */
	public void setConfigInfoPopulator(final ConfigurationOrderEntryProductInfoModelPopulator configInfoPopulator)
	{
		this.configInfoPopulator = configInfoPopulator;
	}

	protected ProductConfigurationPricingOrderIntegrationService getConfigurationPricingOrderIntegrationService()
	{
		return configurationPricingOrderIntegrationService;
	}

	/**
	 * @param configurationPricingOrderIntegrationService
	 *           the configurationPricingOrderIntegrationService to set
	 */
	@Required
	public void setConfigurationPricingOrderIntegrationService(
			final ProductConfigurationPricingOrderIntegrationService configurationPricingOrderIntegrationService)
	{
		this.configurationPricingOrderIntegrationService = configurationPricingOrderIntegrationService;
	}


}
