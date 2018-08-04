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
package de.hybris.platform.sap.sapordermgmtcfgfacades.hook;

import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProviderFactory;
import de.hybris.platform.sap.sapordermgmtb2bfacades.hook.SapCartFacadeHook;
import de.hybris.platform.sap.sapproductconfigsomservices.prodconf.ProductConfigurationService;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Release session before cart entry update
 */
public class CPQSapCartFacadeHook implements SapCartFacadeHook
{

	private ProductConfigurationService productConfigurationService;
	private ConfigurationProviderFactory configurationProviderFactory;

	@Override
	public void beforeCartEntryUpdate(final long quantity, final long entryNumber, final List<OrderEntryData> entries)
	{
		String itemKey = "";
		for (final OrderEntryData entry : entries)
		{
			if (entry.getEntryNumber().longValue() == entryNumber)
			{
				itemKey = entry.getItemPK();
			}
		}
		if (quantity == 0)
		{
			final String configId = getProductConfigurationService().getGetConfigId(itemKey);
			if (configId != null)
			{
				getConfigurationProvider().releaseSession(configId);
			}
		}

	}

	ConfigurationProvider getConfigurationProvider()
	{
		return getConfigurationProviderFactory().getProvider();
	}

	/**
	 * @return the configurationProviderFactory
	 */
	public ConfigurationProviderFactory getConfigurationProviderFactory()
	{
		return configurationProviderFactory;
	}


	/**
	 * @param configurationProviderFactory
	 *           the configurationProviderFactory to set
	 */
	@Required
	public void setConfigurationProviderFactory(final ConfigurationProviderFactory configurationProviderFactory)
	{
		this.configurationProviderFactory = configurationProviderFactory;
	}

	/**
	 * @return the productConfigurationService
	 */
	public ProductConfigurationService getProductConfigurationService()
	{
		return productConfigurationService;
	}


	/**
	 * @param productConfigurationService
	 *           the productConfigurationService to set
	 */
	@Required
	public void setProductConfigurationService(final ProductConfigurationService productConfigurationService)
	{
		this.productConfigurationService = productConfigurationService;
	}

}
