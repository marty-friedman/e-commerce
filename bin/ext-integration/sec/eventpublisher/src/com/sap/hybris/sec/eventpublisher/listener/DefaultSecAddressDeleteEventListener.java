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
package com.sap.hybris.sec.eventpublisher.listener;


import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sap.hybris.sec.eventpublisher.constants.EventpublisherConstants;
import com.sap.hybris.sec.eventpublisher.dto.address.Address;
import com.sap.hybris.sec.eventpublisher.dto.address.Mixins;
import com.sap.hybris.sec.eventpublisher.dto.customer.Customer;
import com.sap.hybris.sec.eventpublisher.event.DefaultSecDeleteAddressEvent;
import com.sap.hybris.sec.eventpublisher.event.DefaultSecDeleteCustomerEvent;
import com.sap.hybris.sec.eventpublisher.handler.impl.AfterCustomerSaveEventHandler;
import com.sap.hybris.sec.eventpublisher.publisher.Publisher;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;


public class DefaultSecAddressDeleteEventListener extends AbstractEventListener<DefaultSecDeleteAddressEvent>{

	private static final Logger LOGGER = LogManager.getLogger(DefaultSecAddressDeleteEventListener.class);

	private Publisher hciPublisher;
	private ConfigurationService configurationService;

	@Override
	protected void onEvent(DefaultSecDeleteAddressEvent event) {
		Address addressJson = new Address();
		addressJson.setHybrisCustomerId(event.getCustomerId());	
		final Map hybrisCustomerAddressMap = new HashMap();
		final Mixins mixins = new Mixins();
		final String customerAddressid = getConfigurationService().getConfiguration()
				.getString(EventpublisherConstants.YAAS_ATTRIBUTE_CUSTOMER_ADDRESS_ID);
		hybrisCustomerAddressMap.put(customerAddressid, event.getAddressId());
		final String schemaAttribute = getConfigurationService().getConfiguration().getString(EventpublisherConstants.SCHEMA_ATTRIBUTE);
		mixins.setAdditionalProperty(schemaAttribute, hybrisCustomerAddressMap);
		addressJson.setMixins(mixins);		
		addressJson.setDelete(true);
		try {
			getHciPublisher().createOrUpdateCustomerAddress(addressJson.toString());
		} catch (IOException e) {
				LOGGER.error("Failed to replicate address", e);
			}	
		
	}


	public Publisher getHciPublisher() {
		return hciPublisher;
	}

	public void setHciPublisher(Publisher hciPublisher) {
		this.hciPublisher = hciPublisher;
	}


	public ConfigurationService getConfigurationService() {
		return configurationService;
	}


	public void setConfigurationService(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	
}
