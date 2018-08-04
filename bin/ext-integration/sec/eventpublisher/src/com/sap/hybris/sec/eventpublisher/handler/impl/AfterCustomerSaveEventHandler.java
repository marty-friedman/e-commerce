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
package com.sap.hybris.sec.eventpublisher.handler.impl;

import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.tx.AfterSaveEvent;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.sec.eventpublisher.constants.EventpublisherConstants;
import com.sap.hybris.sec.eventpublisher.data.ResponseData;
import com.sap.hybris.sec.eventpublisher.dto.customer.Customer;
import com.sap.hybris.sec.eventpublisher.handler.AfterSaveEventHandler;
import com.sap.hybris.sec.eventpublisher.publisher.Publisher;


/**
 * Replicate the updated/created customer to target
 */
public class AfterCustomerSaveEventHandler implements AfterSaveEventHandler
{
	private static final Logger LOGGER = LogManager.getLogger(AfterCustomerSaveEventHandler.class);

	private ModelService modelService;
	private Populator customerPopulator;
	private Publisher hciPublisher;

	@Override
	public void handleEvent(final AfterSaveEvent event)
	{
		final PK pk = event.getPk();
		if ((event.getType() == AfterSaveEvent.CREATE || event.getType() == AfterSaveEvent.UPDATE)
				&& (modelService.get(pk) instanceof CustomerModel))
		{
			final CustomerModel customerModel = (CustomerModel) modelService.get(pk);
			if (!StringUtils.isEmpty((customerModel.getCustomerID())) && !CustomerType.GUEST.equals(customerModel.getType()))
			{
				try
				{
					createOrUpdateCustomer(customerModel);
				}
				catch (URISyntaxException | IOException e)
				{
					LOGGER.error("Failed to replicate customer", e);
				}
			}
		}

	}

	/**
	 * @param endPoint
	 * @throws URISyntaxException
	 * @throws IOException
	 *
	 */
	protected void createOrUpdateCustomer(final CustomerModel customerModel) throws URISyntaxException, IOException
	{
		final Customer customerJson = new Customer();
		getCustomerPopulator().populate(customerModel, customerJson);

		final ResponseData resData = getHciPublisher().createOrUpdateCustomer(customerJson.toString());
		final String resStatus = resData.getStatus();
		if (EventpublisherConstants.HCI_PUBLICATION_STATUS_CREATED.equals(resStatus)
				|| EventpublisherConstants.HCI_PUBLICATION_STATUS_OK.equals(resStatus))
		{
			LOGGER.info("Published Successfully");
		}

	}

	/**
	 * @return the customerPopulator
	 */
	public Populator getCustomerPopulator()
	{
		return customerPopulator;
	}

	/**
	 * @param customerPopulator
	 *           the customerPopulator to set
	 */
	@Required
	public void setCustomerPopulator(final Populator customerPopulator)
	{
		this.customerPopulator = customerPopulator;
	}

	/**
	 * @return the hciPublisher
	 */
	public Publisher getHciPublisher()
	{
		return hciPublisher;
	}

	@Required
	public void setHciPublisher(final Publisher hciPublisher)
	{
		this.hciPublisher = hciPublisher;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

}
