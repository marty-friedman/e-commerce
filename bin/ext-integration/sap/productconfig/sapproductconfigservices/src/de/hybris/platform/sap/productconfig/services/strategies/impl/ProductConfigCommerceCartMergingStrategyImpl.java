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
package de.hybris.platform.sap.productconfig.services.strategies.impl;

import de.hybris.platform.commerceservices.order.CommerceCartMergingException;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceCartMergingStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.services.impl.CPQConfigurableChecker;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPricingOrderIntegrationService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * CPQ implemntation of the {@link DefaultCommerceCartMergingStrategy}.<br>
 * Calls super logic, however also ensures that configurable products are merged proper and that the external
 * configuration is not lost.
 */
public class ProductConfigCommerceCartMergingStrategyImpl extends DefaultCommerceCartMergingStrategy
{

	private static final Logger LOG = Logger.getLogger(ProductConfigCommerceCartMergingStrategyImpl.class);
	private ProductConfigurationPricingOrderIntegrationService configurationPricingOrderIntegrationService;
	private CPQConfigurableChecker cpqConfigurableChecker;

	@Override
	public void mergeCarts(final CartModel fromCart, final CartModel toCart, final List<CommerceCartModification> modifications)
			throws CommerceCartMergingException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("ProductConfig before mergeCarts, fromCart=" + fromCart.getGuid() + "; toCart=" + toCart.getGuid());
		}

		final Map<String, List<String>> extConfigsBeforeMerge = collectCartEntriesByProductCodeAndQuantity(fromCart);
		final Map<PK, AbstractOrderEntryModel> toCartEntriesBeforeMerge = collectCartEntriesByPk(toCart);
		super.mergeCarts(fromCart, toCart, modifications);
		final int changeCounter = reApplyExtConfigsAfterMerge(toCart, toCartEntriesBeforeMerge, extConfigsBeforeMerge);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Restored " + changeCounter + " configurations in cart " + toCart.getGuid()
					+ (changeCounter > 0 ? "; saving cart" : "; not saving cart"));
		}

		if (changeCounter > 0)
		{
			getModelService().save(toCart);
		}

		LOG.debug("ProductConfig after mergeCarts");
	}

	/**
	 * Puts the cart entries into a map with PK as key.
	 *
	 * @param cart
	 *           cart to be processed
	 * @return map of cart entries by PK
	 */
	protected Map<PK, AbstractOrderEntryModel> collectCartEntriesByPk(final CartModel cart)
	{
		final Map<PK, AbstractOrderEntryModel> map = new HashMap<>(cart.getEntries().size());
		for (final AbstractOrderEntryModel entry : cart.getEntries())
		{
			map.put(entry.getPk(), entry);
		}
		return map;
	}

	/**
	 * Puts the cart entries into a map with Product code and quantity as key.
	 *
	 * @param cart
	 *           cart to be processed
	 * @return map of cart entries by Product code and quantity
	 */
	protected Map<String, List<String>> collectCartEntriesByProductCodeAndQuantity(final CartModel cart)
	{
		final Map<String, List<String>> map = new HashMap<>(cart.getEntries().size());

		for (final AbstractOrderEntryModel entry : cart.getEntries())
		{
			final ProductModel product = entry.getProduct();
			if (getCpqConfigurableChecker().isCPQConfigurableProduct(product))
			{
				final String key = createKeyForCartEntry(entry);
				List<String> configurablesEntries;
				if (map.containsKey(key))
				{
					configurablesEntries = addToConfigListForMultipleOccurences(map, entry.getExternalConfiguration(), key);
				}
				else
				{
					configurablesEntries = Collections.singletonList(entry.getExternalConfiguration());

				}

				map.put(key, configurablesEntries);
			}
		}
		return map;
	}

	protected void exchangeExternalConfigurationAndUpdateEntryBasePrice(final AbstractOrderEntryModel entryToChange,
			final Map<String, List<String>> extConfigsBeforeMerge) throws CommerceCartMergingException
	{
		final List<String> list = extConfigsBeforeMerge.get(createKeyForCartEntry(entryToChange));
		if (CollectionUtils.isEmpty(list))
		{
			throw new CommerceCartMergingException("Cannot exchange External configuration at restored cart entry");
		}

		getConfigurationPricingOrderIntegrationService().updateCartEntryExternalConfiguration(list.get(0), entryToChange);
		getConfigurationPricingOrderIntegrationService().updateCartEntryBasePrice(entryToChange);

		if (list.size() > 1)
		{
			list.remove(0);
		}
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Exchanged external configuration at cart entry " + entryToChange.getPk() + " (Product: "
					+ entryToChange.getProduct().getCode() + ", Quantity: " + entryToChange.getQuantity().toString() + ")");
		}
	}


	protected String createKeyForCartEntry(final AbstractOrderEntryModel entryToChange)
	{
		final StringBuilder key = new StringBuilder();
		key.append(entryToChange.getProduct().getCode());
		key.append(entryToChange.getQuantity());
		return key.toString();
	}


	protected int reApplyExtConfigsAfterMerge(final CartModel toCart,
			final Map<PK, AbstractOrderEntryModel> toCartEntriesBeforeMerge, final Map<String, List<String>> extConfigsBeforeMerge)
			throws CommerceCartMergingException
	{

		int changeCounter = 0;

		for (final AbstractOrderEntryModel entry : toCart.getEntries())
		{
			final ProductModel product = entry.getProduct();
			if (getCpqConfigurableChecker().isCPQConfigurableProduct(product)
					&& !toCartEntriesBeforeMerge.containsKey(entry.getPk()))
			{
				exchangeExternalConfigurationAndUpdateEntryBasePrice(entry, extConfigsBeforeMerge);
				changeCounter++;
			}
		}
		return changeCounter;
	}


	protected <T> List<T> addToConfigListForMultipleOccurences(final Map<String, List<T>> missingConfigs, final T entry,
			final String key)
	{
		List<T> entryList;
		// 1% case - wrap the singletonList into a fully arraylist, if required
		entryList = missingConfigs.get(key);
		if (entryList.size() == 1)
		{
			final List<T> newConfigList = new ArrayList<>(entryList.size() + 1);
			newConfigList.addAll(entryList);
			entryList = newConfigList;
		}
		entryList.add(entry);
		return entryList;
	}



	protected CommerceCartParameter createCommerceCartParameterForCalculateCart(final CartModel sessionCart)
	{
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(sessionCart);
		return parameter;
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

	protected CPQConfigurableChecker getCpqConfigurableChecker()
	{
		return this.cpqConfigurableChecker;
	}

	/**
	 * Set helper, to check if the related product is CPQ configurable
	 *
	 * @param cpqConfigurableChecker
	 *           configurator checker
	 */
	@Required
	public void setCpqConfigurableChecker(final CPQConfigurableChecker cpqConfigurableChecker)
	{
		this.cpqConfigurableChecker = cpqConfigurableChecker;
	}
}
