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
package de.hybris.platform.sap.productconfig.runtime.interf;



/**
 * Retrieves an instance of the configuration provider according to the hybris application configuration.
 *
 * @deprecated since 6.5, replaced by {@link ProviderFactory}
 */
@SuppressWarnings("squid:S1133")
@Deprecated
public interface ConfigurationProviderFactory
{

	/**
	 * Retrieves an instance of the configuration provider according to the hybris application configuration.
	 *
	 * @deprecated since 6.5, use {@link ProviderFactory#getConfigurationProvider} instead
	 * @return configuration provider bean
	 */
	@SuppressWarnings("squid:S1133")
	@Deprecated
	ConfigurationProvider getProvider();

}
