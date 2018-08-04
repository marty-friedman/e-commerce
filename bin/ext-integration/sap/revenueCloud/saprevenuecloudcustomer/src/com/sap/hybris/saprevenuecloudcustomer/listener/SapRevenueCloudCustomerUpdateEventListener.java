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
package com.sap.hybris.saprevenuecloudcustomer.listener;

import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sap.hybris.saprevenuecloudcustomer.dto.Customer;
import com.sap.hybris.saprevenuecloudcustomer.event.SapRevenueCloudCustomerUpdateEvent;
import com.sap.hybris.saprevenuecloudcustomer.service.SapRevenueCloudCustomerPublicationService;


/**
 * Event Listener to publish changes to customer data.
 */
public class SapRevenueCloudCustomerUpdateEventListener extends AbstractEventListener<SapRevenueCloudCustomerUpdateEvent>
{
	private SapRevenueCloudCustomerPublicationService sapRevenueCloudCustomerPublicationService;
	private static final Logger LOGGER = LogManager.getLogger(SapRevenueCloudCustomerUpdateEventListener.class);

	@Override
	protected void onEvent(final SapRevenueCloudCustomerUpdateEvent event)
	{
		final Customer customerJson = event.getCustomerJson();
		try
		{
			getSapRevenueCloudCustomerPublicationService().publishCustomerToCloudPlatformIntegration(customerJson);
		}
		catch (final IOException e)
		{
			LOGGER.error("Could not replciate customer.");
			LOGGER.error(e);
		}
	}

	/**
	 * @return the sapRevenueCloudCustomerPublicationService
	 */
	public SapRevenueCloudCustomerPublicationService getSapRevenueCloudCustomerPublicationService()
	{
		return sapRevenueCloudCustomerPublicationService;
	}

	/**
	 * @param sapRevenueCloudCustomerPublicationService
	 *           the sapRevenueCloudCustomerPublicationService to set
	 */
	public void setSapRevenueCloudCustomerPublicationService(
			final SapRevenueCloudCustomerPublicationService sapRevenueCloudCustomerPublicationService)
	{
		this.sapRevenueCloudCustomerPublicationService = sapRevenueCloudCustomerPublicationService;
	}

}
