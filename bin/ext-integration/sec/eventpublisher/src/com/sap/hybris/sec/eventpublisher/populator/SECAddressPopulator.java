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

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
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

import com.sap.hybris.sec.eventpublisher.constants.EventpublisherConstants;
import com.sap.hybris.sec.eventpublisher.dto.address.Address;
import com.sap.hybris.sec.eventpublisher.dto.address.MetaDataMixins;
import com.sap.hybris.sec.eventpublisher.dto.address.Metadata;
import com.sap.hybris.sec.eventpublisher.dto.address.Mixins;


/**
 *
 */
public class SECAddressPopulator implements Populator<AddressModel, Address>
{

	private static final Logger LOGGER = LogManager.getLogger(SECAddressPopulator.class);


	private ConfigurationService configurationService;

	@Override
	public void populate(final AddressModel source, final Address target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		populateAddressFields(source, target);

	}

	/**
	 *
	 */
	protected void populateAddressFields(final AddressModel customerAddressModel, final Address customerAddressData)
	{
		final String customerSchemaUrl = getAddressScemaURL();

		final String schemaAttribute = getConfigurationService().getConfiguration()
				.getString(EventpublisherConstants.SCHEMA_ATTRIBUTE);

		final Map hybrisCustomerAddressMap = new HashMap();

		final Mixins mixins = new Mixins();

		final MetaDataMixins metaDataMixins = new MetaDataMixins();
		final Metadata metadata = new Metadata();

		final CustomerModel customerModel = (CustomerModel) customerAddressModel.getOwner();
		final String hybrisCustomerId = customerModel.getCustomerID();
		customerAddressData.setHybrisCustomerId(hybrisCustomerId);
		try
		{

			metaDataMixins.setAdditionalProperty(schemaAttribute, new URI(customerSchemaUrl));
			metadata.setMixins(metaDataMixins);
			customerAddressData.setMetadata(metadata);


			final String customerAddressid = getConfigurationService().getConfiguration()
					.getString(EventpublisherConstants.YAAS_ATTRIBUTE_CUSTOMER_ADDRESS_ID);
			hybrisCustomerAddressMap.put(customerAddressid, customerAddressModel.getPk().toString());
			mixins.setAdditionalProperty(schemaAttribute, hybrisCustomerAddressMap);
			customerAddressData.setMixins(mixins);


			final String countryIsoCode = customerAddressModel.getCountry() != null ? customerAddressModel.getCountry().getIsocode()
					: null;
			customerAddressData.setContactName(customerAddressModel.getFirstname() + " " + customerAddressModel.getLastname());
			customerAddressData.setCountry(countryIsoCode);
			customerAddressData.setStreet(customerAddressModel.getStreetname());
			customerAddressData.setStreetNumber(customerAddressModel.getStreetnumber());
			customerAddressData.setStreetAppendix(customerAddressModel.getStreetnumber());
			customerAddressData.setCity(customerAddressModel.getTown());
			customerAddressData.setZipCode(customerAddressModel.getPostalcode());
			customerAddressData.setExtraLine1(customerAddressModel.getLine1());
			customerAddressData.setExtraLine2(customerAddressModel.getLine2());
			final String regionIsoCode = customerAddressModel.getRegion() != null
					? customerAddressModel.getRegion().getIsocodeShort() : null;
			customerAddressData.setState(regionIsoCode);
			customerAddressData.setContactPhone(customerAddressModel.getPhone1());
			customerAddressData.setIsDefault(Boolean.TRUE);
			customerAddressData.setHybrisUid(customerModel.getUid());

			final boolean isBiliingAddress = customerAddressModel.getBillingAddress().booleanValue();
			final boolean isShippingAddress = customerAddressModel.getShippingAddress().booleanValue();

			if (isShippingAddress)
			{
				customerAddressData.getTags().add(EventpublisherConstants.SHIPPING_ADDRESS);
			}
			if (isBiliingAddress)
			{
				customerAddressData.getTags().add(EventpublisherConstants.BILLING_ADDRESS);
			}

			if (LOGGER.isDebugEnabled())
			{
				LOGGER.debug("Address JSON:" + customerAddressData.toString());
			}
		}
		catch (final URISyntaxException e)
		{
			LOGGER.error(e);
		}

	}

	/**
	 *
	 */
	protected String getAddressScemaURL()
	{
		final String sUrl = getConfigurationService().getConfiguration().getString(EventpublisherConstants.YAAS_URL);
		final String tenant = getConfigurationService().getConfiguration().getString(EventpublisherConstants.YAAS_TENANAT);
		final String customerAddressSchema = getConfigurationService().getConfiguration()
				.getString(EventpublisherConstants.YAAS_CUSTOMER_ADDRESS_SCHEMA);
		return sUrl + EventpublisherConstants.BACKSLASH + tenant + EventpublisherConstants.BACKSLASH + customerAddressSchema;
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
