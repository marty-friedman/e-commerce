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
package de.hybris.platform.secaddon.services;

import static com.google.common.base.Preconditions.checkArgument;

import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.yaasconfiguration.model.YaasClientCredentialModel;
import de.hybris.platform.yaasconfiguration.model.YaasServiceModel;
import de.hybris.platform.yaasconfiguration.service.YaasConfigurationService;
import de.hybris.platform.yaasconfiguration.service.impl.DefaultYaasServiceFactory;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 *
 * SEC YaaS Service Factory to provide the ability to get defined specific client property
 *
 */
public class SecYaasServiceFactory extends DefaultYaasServiceFactory
{

	private static final Logger LOG = LoggerFactory.getLogger(SecYaasServiceFactory.class);

	private YaasConfigurationService yaasConfigurationService;

	public Map<String, String> getMap(final Class serviceType)
	{
		Map<String, String> yaasConfig = null;
		checkArgument(serviceType != null, "serviceType must not be null");

		try
		{
			// Get configured YaasService for the given identifier
			final YaasServiceModel serviceModel = getYaasConfigurationService().getYaasServiceForId(serviceType.getSimpleName());

			LOG.debug("Found the YaaS service configuration for the given serviceType {}", serviceType.getSimpleName());

			final YaasClientCredentialModel yaasClientCredential = lookupCurrentCredential(serviceModel);

			if (null == yaasClientCredential)
			{
				throw new SystemException("Failed to find Yaas client credential configuration for the given serviceType :"
						+ serviceType.getSimpleName());
			}

			// Build the configuration based on given yaasClientCredential and service information
			yaasConfig = getYaasConfigurationService().buildYaasConfig(yaasClientCredential, serviceType);
		}
		catch (final ModelNotFoundException exp)
		{
			throw new SystemException(
					"Failed to find YaaS service configuration for the given serviceType :" + serviceType.getSimpleName());
		}
		return yaasConfig;
	}

	protected YaasConfigurationService getYaasConfigurationService()
	{
		return yaasConfigurationService;
	}

	@Override
	@Required
	public void setYaasConfigurationService(final YaasConfigurationService yaasConfigurationService)
	{
		super.setYaasConfigurationService(yaasConfigurationService);
		this.yaasConfigurationService = yaasConfigurationService;
	}
}
