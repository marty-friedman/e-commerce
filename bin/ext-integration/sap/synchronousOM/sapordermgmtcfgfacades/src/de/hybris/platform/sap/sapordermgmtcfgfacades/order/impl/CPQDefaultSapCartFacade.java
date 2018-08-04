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
package de.hybris.platform.sap.sapordermgmtcfgfacades.order.impl;

import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProviderFactory;
import de.hybris.platform.sap.sapordermgmtb2bfacades.order.impl.DefaultSapCartFacade;

import org.springframework.beans.factory.annotation.Required;


/**
 * Implementation for {@link CartFacade}. Provide ConfigurationProvider through ConfigurationProviderFactory.
 *
 */
public class CPQDefaultSapCartFacade extends DefaultSapCartFacade
{
	private ConfigurationProviderFactory configurationProviderFactory;

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

}
