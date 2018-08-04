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
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.exceptions.ModelLoadingException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.tx.AfterSaveEvent;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.sec.eventpublisher.constants.EventpublisherConstants;
import com.sap.hybris.sec.eventpublisher.data.ResponseData;
import com.sap.hybris.sec.eventpublisher.dto.address.Address;
import com.sap.hybris.sec.eventpublisher.handler.AfterSaveEventHandler;
import com.sap.hybris.sec.eventpublisher.publisher.Publisher;


/**
 * Replicate the updated/created address to target
 */
public class AfterAddressSaveEventHandler implements AfterSaveEventHandler
{
	private static final Logger LOGGER = LogManager.getLogger(AfterAddressSaveEventHandler.class);

	private ModelService modelService;
	private Publisher hciPublisher;
	private Populator addressPopulator;

	@Override
	public void handleEvent(final AfterSaveEvent event)
	{
		final PK pk = event.getPk();
		try
		{

			if ((event.getType() == AfterSaveEvent.CREATE || event.getType() == AfterSaveEvent.UPDATE)
					&& (modelService.get(pk) instanceof AddressModel))
			{
				final AddressModel addressModel = (AddressModel) modelService.get(pk);

				if(checkGuestCustomer(addressModel)){
					createOrUpdateCustomerAddress(addressModel);
				}
			}
		}
		catch (URISyntaxException | IOException e)
		{
			LOGGER.error("Failed to replicate customer", e);
		}
		catch (final ModelLoadingException e)
		{
			LOGGER.error("Pk is not of itemModel: ", e);
		}
		
	}



	private boolean checkGuestCustomer(final AddressModel addressModel) throws URISyntaxException, IOException {
		if (addressModel.getOwner() != null && (addressModel.getOwner() instanceof CustomerModel))
		{
			final CustomerModel customerModel = (CustomerModel) addressModel.getOwner();
			if (!CustomerType.GUEST.equals(customerModel.getType()))
			{
				return true;
			}
		}
		return false;
	}



	/**
	 * @param addressModel
	 *
	 */
	private void createOrUpdateCustomerAddress(final AddressModel addressModel) throws URISyntaxException, IOException
	{

		final Address addressJson = new Address();

		getAddressPopulator().populate(addressModel, addressJson);

		final ResponseData resData = getHciPublisher().createOrUpdateCustomerAddress(addressJson.toString());
		final String resStatus = resData.getStatus();
		if (EventpublisherConstants.HCI_PUBLICATION_STATUS_CREATED.equals(resStatus)
				|| EventpublisherConstants.HCI_PUBLICATION_STATUS_OK.equals(resStatus))
		{
			LOGGER.info("Published Successfully");
		}

	}

	/**
	 * @return the addressPopulator
	 */
	public Populator getAddressPopulator()
	{
		return addressPopulator;
	}

	/**
	 * @param addressPopulator
	 *           the addressPopulator to set
	 */
	public void setAddressPopulator(final Populator addressPopulator)
	{
		this.addressPopulator = addressPopulator;
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
