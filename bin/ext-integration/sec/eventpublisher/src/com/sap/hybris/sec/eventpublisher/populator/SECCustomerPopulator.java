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
package com.sap.hybris.sec.eventpublisher.populator;

import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.sap.hybris.sec.eventpublisher.constants.EventpublisherConstants;
import com.sap.hybris.sec.eventpublisher.dto.customer.Customer;
import com.sap.hybris.sec.eventpublisher.dto.customer.Metadata;
import com.sap.hybris.sec.eventpublisher.dto.customer.MetadataMixins;
import com.sap.hybris.sec.eventpublisher.dto.customer.Mixins;


/**
 *
 */
public class SECCustomerPopulator implements Populator<CustomerModel, Customer>
{


	private static final Logger LOGGER = LogManager.getLogger(SECCustomerPopulator.class);

	private CustomerNameStrategy customerNameStrategy;

	private ConfigurationService configurationService;


	@Override
	public void populate(final CustomerModel source, final Customer target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		populateCustomerFields(source, target);

	}

	protected void populateCustomerFields(final CustomerModel source, final Customer customerData)
	{
		final String customerSchemaUrl = getCustomerScemaURL();
		final String schemaAttribute = getConfigurationService().getConfiguration()
				.getString(EventpublisherConstants.SCHEMA_ATTRIBUTE);


		final Map hybrisCustomerMap = new HashMap();
		final String[] names = getCustomerNameStrategy().splitName(source.getName());
		final Mixins mixins = new Mixins();

		final MetadataMixins metaDataMixins = new MetadataMixins();
		final Metadata metadata = new Metadata();
		try
		{

			metaDataMixins.setAdditionalProperty(schemaAttribute, new URI(customerSchemaUrl));

			metadata.setMixins(metaDataMixins);
			customerData.setMetadata(metadata);

			final String customerAttributeId = getConfigurationService().getConfiguration()
					.getString(EventpublisherConstants.YAAS_ATTRIBUTE_CUSTOMER_ID);
			final String customerAttributeUid = getConfigurationService().getConfiguration()
					.getString(EventpublisherConstants.YAAS_ATTRIBUTE_CUSTOMER_UID);
			hybrisCustomerMap.put(customerAttributeId, source.getCustomerID());
			hybrisCustomerMap.put(customerAttributeUid, source.getUid());
			mixins.setAdditionalProperty(schemaAttribute, hybrisCustomerMap);
			customerData.setMixins(mixins);

			customerData.setContactEmail(source.getContactEmail());
			if (source.getTitle() != null && !StringUtils.isEmpty(source.getTitle()))
			{
				customerData.setTitle(source.getTitle().getCode());
			}


			if (names.length > 0 && !StringUtils.isEmpty(names[0]))
			{
				customerData.setFirstName(names[0]);
			}
			if (names.length > 1 && !StringUtils.isEmpty(names[1]))
			{
				customerData.setLastName(names[1]);
			}

			if (!StringUtils.isEmpty(source.getSessionCurrency()))
			{
				customerData.setPreferredCurrency(source.getSessionCurrency().getIsocode());
			}


			if (LOGGER.isDebugEnabled())
			{
				LOGGER.debug("Customer JSON:" + customerData.toString());
			}

		}

		catch (final URISyntaxException e)
		{
			LOGGER.error(e);
		}


	}


	protected String getCustomerScemaURL()
	{
		final String sUrl = getConfigurationService().getConfiguration().getString(EventpublisherConstants.YAAS_URL);
		final String tenant = getConfigurationService().getConfiguration().getString(EventpublisherConstants.YAAS_TENANAT);
		final String customerSchema = getConfigurationService().getConfiguration()
				.getString(EventpublisherConstants.YAAS_CUSTOMER_SCHEMA);
		final String customerSchemaUrl = sUrl + EventpublisherConstants.BACKSLASH + tenant + EventpublisherConstants.BACKSLASH
				+ customerSchema;
		return customerSchemaUrl;
	}

	/**
	 * @return the customerNameStrategy
	 */
	public CustomerNameStrategy getCustomerNameStrategy()
	{
		return customerNameStrategy;
	}

	/**
	 * @param customerNameStrategy
	 *           the customerNameStrategy to set
	 */
	public void setCustomerNameStrategy(final CustomerNameStrategy customerNameStrategy)
	{
		this.customerNameStrategy = customerNameStrategy;
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
