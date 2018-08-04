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
package com.sap.hybris.saprevenuecloudcustomer.service.impl;

import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.io.IOException;

import com.sap.hybris.saprevenuecloudcustomer.constants.SaprevenuecloudcustomerConstants;
import com.sap.hybris.saprevenuecloudcustomer.dto.Customer;
import com.sap.hybris.saprevenuecloudcustomer.service.SapRevenueCloudCustomerPublicationService;
import com.sap.hybris.scpiconnector.data.ResponseData;
import com.sap.hybris.scpiconnector.httpconnection.CloudPlatformIntegrationConnection;


/**
 * Publishes customer data to Cloud Platform Integration
 */
public class DefaultSapRevenueCloudCustomerPublicationService implements SapRevenueCloudCustomerPublicationService
{

	private CloudPlatformIntegrationConnection cloudPlatformIntegrationConnection;
	private ConfigurationService configurationService;


	@Override
	public ResponseData publishCustomerToCloudPlatformIntegration(final Customer customerJson) throws IOException
	{
		return getCloudPlatformIntegrationConnection().sendPost(
				getConfigurationService().getConfiguration().getString(SaprevenuecloudcustomerConstants.CUSTOMER_IFLOW_URL),
				customerJson.toString());
	}

	/**
	 * @return the cloudPlatformIntegrationConnection
	 */
	public CloudPlatformIntegrationConnection getCloudPlatformIntegrationConnection()
	{
		return cloudPlatformIntegrationConnection;
	}


	/**
	 * @param cloudPlatformIntegrationConnection
	 *           the cloudPlatformIntegrationConnection to set
	 */
	public void setCloudPlatformIntegrationConnection(final CloudPlatformIntegrationConnection cloudPlatformIntegrationConnection)
	{
		this.cloudPlatformIntegrationConnection = cloudPlatformIntegrationConnection;
	}


	/**
	 * @return the configurationService
	 */
	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}


	/**
	 * @param configurationService
	 *           the configurationService to set
	 */
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

}
