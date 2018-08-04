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
package de.hybris.platform.sap.productconfig.runtime.cps;

import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.session.CookieHandler;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;


/**
 * Not needed when skywalker service runs on yaas
 */
public interface CharonFacade
{

	/**
	 * Create default configuration and handle cookies
	 *
	 * @param kbKey
	 *           key of the knowledgebase
	 * @return runtime configuration
	 */
	CPSConfiguration createDefaultConfiguration(KBKey kbKey);

	/**
	 * Updates configuration, sends cookies along with request. The cookies are handled internally and received from the
	 * {@link CookieHandler}
	 *
	 * @param configuration
	 *           runtime configuration that includes only the updates
	 * @return Did we actually do an update?
	 * @throws ConfigurationEngineException
	 *            when service call fails
	 */
	boolean updateConfiguration(CPSConfiguration configuration) throws ConfigurationEngineException;

	/**
	 * Gets configuration, sends cookies along with request. The cookies are handled internally and received from the
	 * {@link CookieHandler}
	 *
	 * @param configId
	 *           configuration id
	 * @return current state of the runtime configuration
	 * @throws ConfigurationEngineException
	 *            when service call fails
	 */
	CPSConfiguration getConfiguration(String configId) throws ConfigurationEngineException;

	/**
	 * Retrieves the external representation for a given runtime configuration
	 *
	 * @param configId
	 *           id of the runtime configuration
	 *
	 * @return external representation of the runtime configuration
	 * @throws ConfigurationEngineException
	 *            Service has failed, e.g. because session timed out
	 */
	String getExternalConfiguration(String configId) throws ConfigurationEngineException;

	/**
	 * Creates a new runtime configuration from the external representation of the configuration
	 *
	 * @param externalConfiguration
	 *           external representation of the configuration
	 * @return runtime configuration based on the external representation
	 */
	CPSConfiguration createConfigurationFromExternal(String externalConfiguration);



	/**
	 * Deletes the session for the specified runtime configuration on the client
	 *
	 * @param configId
	 *           id of the runtime configuration to be deleted
	 */
	void releaseSession(String configId);



	/**
	 * Creates a new runtime configuration from the external representation of the configuration and the KB ID, which is
	 * not part of the external configuration format
	 *
	 * @param externalConfiguration
	 *           external representation of the configuration originating from synchronous order management scenario
	 * @param kbid
	 *           KB ID
	 * @return Runtime configuration based on the external representation
	 */
	CPSConfiguration createConfigurationFromExternal(Configuration externalConfiguration, Integer kbid);

	/**
	 * Creates a new runtime configuration from the external typed representation of the configuration
	 *
	 * @param externalConfigStructured
	 *           The structured external configuration
	 * @return Runtime representation
	 */
	CPSConfiguration createConfigurationFromExternal(CPSExternalConfiguration externalConfigStructured);

}
