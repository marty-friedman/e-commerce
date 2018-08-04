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
package com.sap.hybris.sec.eventpublisher.publisher.Impl;

import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sap.hybris.sec.eventpublisher.constants.EventpublisherConstants;
import com.sap.hybris.sec.eventpublisher.data.ResponseData;
import com.sap.hybris.sec.eventpublisher.httpconnection.SECHttpConnection;
import com.sap.hybris.sec.eventpublisher.publisher.Publisher;


public class HCIPublisher implements Publisher
{
	private static final Logger LOGGER = LogManager.getLogger(HCIPublisher.class);
	
	private ConfigurationService configurationService;
	private SECHttpConnection secHttpConnection;

	@Override
	public ResponseData createOrUpdateCustomer(final String customerJson) throws IOException
	{

		final String PATH_URL = getConfigurationService().getConfiguration().getString(EventpublisherConstants.HCI_PROJECT_PATH)
				+ getConfigurationService().getConfiguration().getString(EventpublisherConstants.CUSTOMER_PATH);

		ResponseData resData = null;
		try
		{
			resData = secHttpConnection.sendPost(PATH_URL, customerJson);
		}
		catch (final IOException ex)
		{
			LOGGER.error(ex);
			throw new IOException(ex);
		}
		return resData;

	}

	@Override
	public ResponseData createOrUpdateCustomerAddress(final String addressJson) throws IOException
	{

		final String PATH_URL = getConfigurationService().getConfiguration().getString(EventpublisherConstants.HCI_PROJECT_PATH)
				+ getConfigurationService().getConfiguration().getString(EventpublisherConstants.ADDRESS_PATH);


		ResponseData resData = null;
		try
		{
			if (LOGGER.isDebugEnabled())
			{
				LOGGER.debug("JSON ADDRESS VALUE: " + addressJson);
			}
			
			resData = secHttpConnection.sendPost(PATH_URL, addressJson);
		}
		catch (final IOException ex)
		{
			LOGGER.error(ex);
			throw new IOException(ex);
		}
		return resData;

	}

	@Override
	public ResponseData createOrUpdateOrder(final String orderJson) throws IOException
	{
		final String PATH_URL = getConfigurationService().getConfiguration().getString(EventpublisherConstants.HCI_PROJECT_PATH)
				+ getConfigurationService().getConfiguration().getString(EventpublisherConstants.ORDER_PATH);
		ResponseData resData = null;
		try
		{
			if(LOGGER.isDebugEnabled())
			{
				LOGGER.debug("JSON ORDER VALUE: " + orderJson);
			}
			
			resData = secHttpConnection.sendPost(PATH_URL, orderJson);
		}
		catch (final IOException ex)
		{
			LOGGER.error(ex);
			throw new IOException(ex);
		}
		return resData;
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

	/**
	 * @return the secHttpConnection
	 */
	public SECHttpConnection getSecHttpConnection()
	{
		return secHttpConnection;
	}

	/**
	 * @param secHttpConnection
	 *           the secHttpConnection to set
	 */
	public void setSecHttpConnection(final SECHttpConnection secHttpConnection)
	{
		this.secHttpConnection = secHttpConnection;
	}



}
