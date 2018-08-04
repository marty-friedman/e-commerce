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

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.facades.ConfigurationAbstractOrderIntegrationHelper;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;
import de.hybris.platform.sap.productconfig.facades.populator.VariantOverviewPopulator;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.impl.CPQConfigurableChecker;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.variants.model.VariantProductModel;

import org.springframework.beans.factory.annotation.Required;


/**
 *
 */
public class ConfigurationAbstractOrderIntegrationHelperImpl implements ConfigurationAbstractOrderIntegrationHelper
{
	private ProductConfigurationService productConfigurationService;
	private VariantOverviewPopulator variantOverviewPopulator;
	private CPQConfigurableChecker cpqConfigurableChecker;

	@Override
	public ConfigurationOverviewData retrieveConfigurationOverviewData(final AbstractOrderModel orderModel, final int entryNumber)
	{
		final AbstractOrderEntryModel orderEntry = findEntry(orderModel, entryNumber);
		final String externalConfig = orderEntry.getExternalConfiguration();

		if (null == externalConfig && orderEntry.getProduct() instanceof VariantProductModel)
		{
			return getConfigurationOverviewDataForVariant(orderEntry, orderEntry.getProduct());
		}
		else
		{
			validateExternalConfig(orderEntry);
			return getConfigurationOverviewData(orderEntry);
		}
	}

	protected ConfigurationOverviewData getConfigurationOverviewData(final AbstractOrderEntryModel orderEntry)
	{
		ConfigurationOverviewData overviewData = null;
		final boolean isKbVersionExists = isKbVersionForEntryExisting(orderEntry);
		if (isKbVersionExists)
		{
			final KBKey kbKey = new KBKeyImpl(orderEntry.getProduct().getCode());
			final ConfigModel configModel = retrieveConfigModelAndDiscardSession(orderEntry.getExternalConfiguration(), kbKey);
			overviewData = prepareOverviewData(kbKey, configModel);
		}
		return overviewData;
	}

	@Override
	public boolean isReorderable(final AbstractOrderModel orderModel)
	{
		for (final AbstractOrderEntryModel orderEntry : orderModel.getEntries())
		{
			if (!isKbVersionForEntryExisting(orderEntry))
			{
				return false;
			}
		}
		return true;
	}

	protected boolean isKbVersionForEntryExisting(final AbstractOrderEntryModel orderEntry)
	{
		boolean isKbVersionForEntryExisting = true;
		if (getCpqConfigurableChecker().isCPQConfigurableProduct(orderEntry.getProduct()))
		{
			final KBKey kbKey = new KBKeyImpl(orderEntry.getProduct().getCode());
			isKbVersionForEntryExisting = getProductConfigurationService().hasKbForVersion(kbKey,
					orderEntry.getExternalConfiguration());
		}
		return isKbVersionForEntryExisting;
	}

	protected ConfigurationOverviewData getConfigurationOverviewDataForVariant(final AbstractOrderEntryModel entry,
			final ProductModel productModel)
	{
		final ConfigurationOverviewData configOverviewData = new ConfigurationOverviewData();
		getVariantOverviewPopulator().populate(productModel, configOverviewData);
		configOverviewData.setProductCode(entry.getProduct().getCode());

		return configOverviewData;
	}

	protected void validateExternalConfig(final AbstractOrderEntryModel orderEntry)
	{
		if (null == orderEntry.getExternalConfiguration())
		{
			throw new IllegalArgumentException(
					"No external configuration is attached to item, for " + orderEntry.getOrder().getItemtype() + " '"
							+ orderEntry.getOrder().getCode() + "' and entry number '" + orderEntry.getEntryNumber() + "'");
		}
	}

	protected ConfigModel retrieveConfigModelAndDiscardSession(final String externalConfig, final KBKey kbKey)
	{
		final ConfigModel configModel = getProductConfigurationService().createConfigurationFromExternal(kbKey, externalConfig);
		getProductConfigurationService().releaseSession(configModel.getId(), true);
		return configModel;
	}

	protected ConfigurationOverviewData prepareOverviewData(final KBKey kbKey, final ConfigModel configModel)
	{
		final ConfigurationOverviewData ovData = new ConfigurationOverviewData();
		ovData.setProductCode(kbKey.getProductCode());
		ovData.setId(configModel.getId());

		return ovData;
	}

	protected AbstractOrderEntryModel findEntry(final AbstractOrderModel orderModel, final int entryNumber)
	{
		AbstractOrderEntryModel orderEntry = null;
		for (final AbstractOrderEntryModel entry : orderModel.getEntries())
		{
			if (entry.getEntryNumber().intValue() == entryNumber)
			{
				orderEntry = entry;
				break;
			}
		}
		if (null == orderEntry)
		{
			throw new IllegalArgumentException("Could not find in " + orderModel.getItemtype() + "'" + orderModel.getCode()
					+ "' an item with number '" + entryNumber + "'");
		}
		return orderEntry;
	}

	protected ProductConfigurationService getProductConfigurationService()
	{
		return productConfigurationService;
	}

	/**
	 * @param productConfigurationService
	 */
	@Required
	public void setProductConfigurationService(final ProductConfigurationService productConfigurationService)
	{
		this.productConfigurationService = productConfigurationService;
	}

	protected VariantOverviewPopulator getVariantOverviewPopulator()
	{
		return variantOverviewPopulator;
	}

	/**
	 * @param variantOverviewPopulator
	 */
	public void setVariantOverviewPopulator(final VariantOverviewPopulator variantOverviewPopulator)
	{
		this.variantOverviewPopulator = variantOverviewPopulator;
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
