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
package de.hybris.platform.sap.productconfig.services.impl;

import de.hybris.platform.catalog.enums.ProductInfoStatus;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceSummaryModel;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.data.CartEntryConfigurationAttributes;
import de.hybris.platform.sap.productconfig.services.intf.PricingService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPricingOrderIntegrationService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.tracking.TrackingRecorder;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link ProductConfigurationPricingOrderIntegrationService}.
 */

public class ProductConfigurationPricingOrderIntegrationServiceImpl implements ProductConfigurationPricingOrderIntegrationService
{

	private ProductConfigurationService configurationService;
	private PricingService pricingService;
	private SessionAccessService sessionAccessService;
	private TrackingRecorder recorder;
	private ModelService modelService;
	private CommerceCartService commerceCartService;

	private static final Logger LOG = Logger.getLogger(ProductConfigurationPricingOrderIntegrationServiceImpl.class);

	@Override
	public CartEntryConfigurationAttributes calculateCartEntryConfigurationAttributes(final AbstractOrderEntryModel entryModel)
	{
		final String cartEntryKey = entryModel.getPk().toString();
		final String productCode = entryModel.getProduct().getCode();
		final String externalConfiguration = entryModel.getExternalConfiguration();

		return calculateCartEntryConfigurationAttributes(cartEntryKey, productCode, externalConfiguration);

	}

	@Override
	public CartEntryConfigurationAttributes calculateCartEntryConfigurationAttributes(final String cartEntryKey,
			final String productCode, final String externalConfiguration)
	{
		final CartEntryConfigurationAttributes attributes = new CartEntryConfigurationAttributes();
		final ConfigModel configurationModel = ensureConfigurationInSession(cartEntryKey, productCode, externalConfiguration);

		final boolean isConfigurationConsistent = configurationModel.isConsistent() && configurationModel.isComplete();

		attributes.setConfigurationConsistent(Boolean.valueOf(isConfigurationConsistent));
		final int numberOfIssues = getConfigurationService().getTotalNumberOfIssues(configurationModel);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Number of issues: " + numberOfIssues);
		}
		attributes.setNumberOfErrors(Integer.valueOf(numberOfIssues));

		return attributes;
	}

	@Override
	public ConfigModel ensureConfigurationInSession(final String cartEntryKey, final String productCode,
			final String externalConfiguration)
	{
		final String configId = getSessionAccessService().getConfigIdForCartEntry(cartEntryKey);
		if (LOG.isDebugEnabled())
		{
			LOG.debug("ConfigID=" + configId + " is mapped to cartentry with PK=" + cartEntryKey);
		}
		ConfigModel configurationModel = null;
		if (configId != null)
		{
			configurationModel = getConfigurationService().retrieveConfigurationModel(configId);
		}

		if (configurationModel == null)
		{
			final KBKeyImpl kbKey = new KBKeyImpl(productCode);
			if (externalConfiguration == null)
			{
				// this means the item was put into the cart without touching
				// CPQ, e.g. through order forms
				// as this is not the standard process, log this in info level
				LOG.info(
						"No external configuration provided for cart entry key: " + cartEntryKey + ". Creating default configuration");
				configurationModel = getConfigurationService().createDefaultConfiguration(kbKey);
			}
			else
			{
				LOG.debug("Creating config model form external XML");
				configurationModel = getConfigurationService().createConfigurationFromExternal(kbKey, externalConfiguration,
						cartEntryKey);
			}
			getSessionAccessService().setConfigIdForCartEntry(cartEntryKey, configurationModel.getId());
		}
		return configurationModel;
	}

	@Override
	public boolean updateCartEntryBasePrice(final AbstractOrderEntryModel entry)
	{
		final String configId = getSessionAccessService().getConfigIdForCartEntry(entry.getPk().toString());
		final PriceModel currentTotalPrice = retrieveCurrentTotalPrice(configId);
		boolean cartEntryUpdated = false;
		if (currentTotalPrice != null && currentTotalPrice.hasValidPrice())
		{
			final Double newPrice = Double.valueOf(currentTotalPrice.getPriceValue().doubleValue());
			if (hasBasePriceChanged(entry, newPrice))
			{
				entry.setBasePrice(newPrice);
				LOG.debug("Base price: " + entry.getBasePrice() + " is set for the cart entry with pk: " + entry.getPk());
				cartEntryUpdated = true;
			}
		}
		return cartEntryUpdated;
	}

	protected boolean hasBasePriceChanged(final AbstractOrderEntryModel entry, final Double newPrice)
	{
		return !newPrice.equals(entry.getBasePrice());
	}


	@Override
	public boolean updateCartEntryPrices(final AbstractOrderEntryModel entry, final boolean calculateCart,
			final CommerceCartParameter passedParameter)
	{
		if (updateCartEntryBasePrice(entry))
		{
			//We need to persist both entities before cart calculation, otherwise
			//total calculation does not work (subsequent save calls restore the old state
			//because unsaved changes are present)
			getModelService().save(entry);
			getModelService().save(entry.getOrder());
			if (calculateCart)
			{
				if (passedParameter == null)
				{
					final CommerceCartParameter parameter = getParametersForCartUpdate(entry);
					getCommerceCartService().calculateCart(parameter);
				}
				else
				{
					getCommerceCartService().calculateCart(passedParameter);
				}
			}
			return true;
		}
		return false;
	}

	protected CommerceCartParameter getParametersForCartUpdate(final AbstractOrderEntryModel entry)
	{
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart((CartModel) entry.getOrder());
		parameter.setConfigId(getSessionAccessService().getConfigIdForCartEntry(entry.getPk().toString()));
		return parameter;
	}

	protected PriceModel retrieveCurrentTotalPrice(final String configId)
	{
		if (getPricingService().isActive())
		{
			final PriceSummaryModel priceSummary = getPricingService().getPriceSummary(configId);
			if (priceSummary == null)
			{
				return null;
			}
			return priceSummary.getCurrentTotalPrice();
		}
		else
		{
			return getConfigurationService().retrieveConfigurationModel(configId).getCurrentTotalPrice();
		}
	}

	@Override
	public boolean updateCartEntryExternalConfiguration(final CommerceCartParameter parameters,
			final AbstractOrderEntryModel entry)
	{
		final String xml = getConfigurationService().retrieveExternalConfiguration(parameters.getConfigId());
		entry.setExternalConfiguration(xml);
		getModelService().save(entry);
		getRecorder().recordUpdateCartEntry(entry, parameters);
		LOG.debug("Configuration with config ID " + parameters.getConfigId() + " set at cart entry " + entry.getPk().toString()
				+ ": " + xml);
		return true;
	}

	@Override
	public boolean updateCartEntryExternalConfiguration(final String externalConfiguration, final AbstractOrderEntryModel entry)
	{
		final String cartEntryKey = entry.getPk().toString();
		if (LOG.isDebugEnabled())
		{
			final String oldConfigId = getSessionAccessService().getConfigIdForCartEntry(cartEntryKey);
			LOG.debug("Removed old configId " + oldConfigId + " for cart entry " + cartEntryKey);
		}
		getSessionAccessService().removeConfigIdForCartEntry(cartEntryKey);
		final KBKey kbKey = new KBKeyImpl(entry.getProduct().getCode());
		final ConfigModel configurationModel = getConfigurationService().createConfigurationFromExternal(kbKey,
				externalConfiguration, cartEntryKey);
		getSessionAccessService().setConfigIdForCartEntry(cartEntryKey, configurationModel.getId());
		final String newExternalConfiguration = getConfigurationService().retrieveExternalConfiguration(configurationModel.getId());
		entry.setExternalConfiguration(newExternalConfiguration);
		LOG.debug("Configuration with config ID " + configurationModel.getId() + " set at cart entry " + cartEntryKey + ": "
				+ newExternalConfiguration);
		return true;
	}

	@Override
	public boolean updateCartEntryProduct(final AbstractOrderEntryModel entry, final ProductModel product, final String configId)
	{
		if (hasProductChangedForCartItem(product, entry))
		{
			getSessionAccessService().setConfigIdForCartEntry(entry.getPk().toString(), configId);
			entry.setProduct(product);
			return true;
		}
		return false;
	}

	protected boolean hasProductChangedForCartItem(final ProductModel product, final AbstractOrderEntryModel cartItem)
	{
		return !cartItem.getProduct().getCode().equals(product.getCode());
	}

	@Override
	public void fillSummaryMap(final AbstractOrderEntryModel entry)
	{
		final CartEntryConfigurationAttributes configurationAttributes = calculateCartEntryConfigurationAttributes(entry);
		final Map<ProductInfoStatus, Integer> statusSummaryMap = new HashMap<>();
		entry.setCpqStatusSummaryMap(statusSummaryMap);
		if (!configurationAttributes.getConfigurationConsistent().booleanValue())
		{
			statusSummaryMap.put(ProductInfoStatus.ERROR, configurationAttributes.getNumberOfErrors());
		}
	}



	protected ProductConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * @param configurationService
	 *           the configurationService to set
	 */
	@Required
	public void setConfigurationService(final ProductConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	protected PricingService getPricingService()
	{
		return pricingService;
	}

	/**
	 * @param pricingService
	 *           the pricingService to set
	 */
	@Required
	public void setPricingService(final PricingService pricingService)
	{
		this.pricingService = pricingService;
	}

	protected SessionAccessService getSessionAccessService()
	{
		return sessionAccessService;
	}

	/**
	 * @param sessionAccessService
	 *           the sessionAccessService to set
	 */
	@Required
	public void setSessionAccessService(final SessionAccessService sessionAccessService)
	{
		this.sessionAccessService = sessionAccessService;
	}

	protected TrackingRecorder getRecorder()
	{
		return recorder;
	}

	/**
	 * @param recorder
	 *           inject the CPQ tracking recorder for tracking CPQ events
	 */
	@Required
	public void setRecorder(final TrackingRecorder recorder)
	{
		this.recorder = recorder;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
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
	 *           the commerceCartService to set
	 */
	@Required
	public void setCommerceCartService(final CommerceCartService commerceCartService)
	{
		this.commerceCartService = commerceCartService;
	}
}
