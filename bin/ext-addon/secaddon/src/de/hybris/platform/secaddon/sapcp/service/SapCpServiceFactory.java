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
package de.hybris.platform.secaddon.sapcp.service;

import static com.google.common.base.Preconditions.checkArgument;
import static de.hybris.platform.secaddon.constants.SecaddonConstants.SAPCP_CLIENT_SCOPE;
import static de.hybris.platform.secaddon.constants.SecaddonConstants.SAPCP_CLIENT_URL;
import static de.hybris.platform.secaddon.constants.SecaddonConstants.SAPCP_OAUTH_CLIENTID;
import static de.hybris.platform.secaddon.constants.SecaddonConstants.SAPCP_OAUTH_CLIENTSECRET;
import static de.hybris.platform.secaddon.constants.SecaddonConstants.SAPCP_OAUTH_URL;
import static de.hybris.platform.secaddon.constants.SecaddonConstants.SAPCP_TENANT;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.secaddon.constants.SecaddonConstants;
import de.hybris.platform.secaddon.services.TicketServiceClient;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.yaasconfiguration.CharonFactory;

/**
 * 
 * SAP Cloud Platform Service Factory to provide the ability to access the
 * SEC endpoint [Ticket Service & Customer Service].
 *
 */
public class SapCpServiceFactory {

	private static final Logger LOG = LoggerFactory.getLogger(SapCpServiceFactory.class);
	private CharonFactory charonFactory;
	private ConfigurationService configurationService;

	public CharonFactory getCharonFactory() {
		return charonFactory;
	}

	@Required
	public void setCharonFactory(CharonFactory charonFactory) {
		this.charonFactory = charonFactory;
	}

	public ConfigurationService getConfigurationService() {
		return configurationService;
	}

	@Required
	public void setConfigurationService(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	public <T> T lookupService(final Class<T> serviceType) {

		checkArgument(serviceType != null, "serviceType must not be null");

		String appId = null;
		final Map<String, String> sapCpConfig = new HashMap<>();
		if (null == readProperty(SecaddonConstants.OAUTH_URL)) {
			throw new SystemException("Failed to find SAP CP oAuth credential configuration for the given serviceType :"
					+ serviceType.getSimpleName());
		}
		sapCpConfig.put(SAPCP_OAUTH_URL, readProperty(SecaddonConstants.OAUTH_URL));
		sapCpConfig.put(SAPCP_OAUTH_CLIENTID, readProperty(SecaddonConstants.OAUTH_CLIENTID));
		sapCpConfig.put(SAPCP_OAUTH_CLIENTSECRET, readProperty(SecaddonConstants.OAUTH_CLIENTSECRET));
		sapCpConfig.put(SAPCP_TENANT, readProperty(SecaddonConstants.TENANT));
		LOG.debug("OAuth credentials SAP CP Oauth url=%s", sapCpConfig.get(SAPCP_OAUTH_URL));
		sapCpConfig.put(SAPCP_CLIENT_SCOPE, readProperty(SecaddonConstants.TENANT));
		if (serviceType.isAssignableFrom(TicketServiceClient.class)) {
			sapCpConfig.put(SAPCP_CLIENT_URL, readProperty(SecaddonConstants.TICKET_CLIENT_URL));
			appId = readProperty(SecaddonConstants.TICKET_APP_ID);
			LOG.debug("Ticket Service configuration from properties file, client URL = %s",
					sapCpConfig.get(SAPCP_CLIENT_URL));
		} else {
			sapCpConfig.put(SAPCP_CLIENT_URL, readProperty(SecaddonConstants.BP_CLIENT_URL));
			appId = readProperty(SecaddonConstants.BP_APP_ID);
			LOG.debug("Customer Service configuration from properties file, client URL = %s",
					sapCpConfig.get(SAPCP_CLIENT_URL));
		}
		return getCharonFactory().client(appId, serviceType, sapCpConfig, builder -> builder.build());
	}

	/**
	 * Reading properties from configuration file
	 * 
	 * @param parameter
	 *            Key value
	 * @return corresponding value
	 */
	private String readProperty(String parameter) {
		return getConfigurationService().getConfiguration().getString(parameter);
	}

}
