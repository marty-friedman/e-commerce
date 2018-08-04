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
package com.sap.hybris.saprevenuecloudcustomer.interceptor;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.sap.hybris.saprevenuecloudcustomer.constants.SaprevenuecloudcustomerConstants;
import com.sap.hybris.saprevenuecloudcustomer.dto.Address;
import com.sap.hybris.saprevenuecloudcustomer.dto.Customer;
import com.sap.hybris.saprevenuecloudcustomer.event.SapRevenueCloudCustomerUpdateEvent;
import com.sap.hybris.saprevenuecloudproduct.model.SAPRevenueCloudConfigurationModel;


/**
 * Updates already existing customer in Revenue Cloud.
 */
public class DefaultSapRevenueCloudCustomerValidateInterceptor implements ValidateInterceptor<CustomerModel>
{
	private EventService eventService;
	private Populator customerPopulator;
	private ConfigurationService configurationService;
	private GenericDao sapRevenueCloudConfigurationModelGenericDao;



	@Override
	public void onValidate(final CustomerModel customerModel, final InterceptorContext ctx) throws InterceptorException
	{
		final SAPRevenueCloudConfigurationModel revenueCloudConfig = getRevenueCloudConfiguration();
		if (revenueCloudConfig == null || revenueCloudConfig.getReplicateCustomer() == null
				|| !revenueCloudConfig.getReplicateCustomer())
		{
			return;
		}
		if (shouldReplicate(customerModel, ctx))
		{
			final SapRevenueCloudCustomerUpdateEvent event = new SapRevenueCloudCustomerUpdateEvent();
			final Customer customerJson = new Customer();
			getCustomerPopulator().populate(customerModel, customerJson);
			if (customerJson.getAddresses() == null || customerJson.getAddresses().isEmpty())
			{
				//add default address in case no address added by user because country and email is mandatory in revenue cloud
				final Address address = new Address();
				address.setEmail(customerModel.getUid());
				final String defaultCountryCode = getConfigurationService().getConfiguration().getString(
						SaprevenuecloudcustomerConstants.DEFAULT_COUNTRY);
				address.setCountry(defaultCountryCode);
				final List<Address> addresses = new ArrayList<Address>();
				addresses.add(address);
				customerJson.setAddresses(addresses);
			}
			event.setCustomerJson(customerJson);
			getEventService().publishEvent(event);
		}
	}

	protected boolean shouldReplicate(final CustomerModel customerModel, final InterceptorContext ctx)
	{
		if ((customerModel.getRevenueCloudCustomerId() != null) && (customerModel.getClass() == CustomerModel.class))
		{
			return getChangeAttributesList().stream().anyMatch(attribute -> ctx.isModified(customerModel, attribute));
		}
		return false;
	}

	protected List<String> getChangeAttributesList()
	{
		final List<String> attributeList = new ArrayList<String>();
		attributeList.add(CustomerModel.NAME);
		attributeList.add(CustomerModel.UID);
		attributeList.add(CustomerModel.DEFAULTSHIPMENTADDRESS);
		return attributeList;
	}

	protected SAPRevenueCloudConfigurationModel getRevenueCloudConfiguration()
	{
		final Optional<SAPRevenueCloudConfigurationModel> revenueCloudConfigOpt = getSapRevenueCloudConfigurationModelGenericDao()
				.find().stream().findFirst();
		return revenueCloudConfigOpt.orElse(null);
	}

	/**
	 * @return the eventService
	 */
	public EventService getEventService()
	{
		return eventService;
	}

	/**
	 * @param eventService
	 *           the eventService to set
	 */
	public void setEventService(final EventService eventService)
	{
		this.eventService = eventService;
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
	public void setCustomerPopulator(final Populator customerPopulator)
	{
		this.customerPopulator = customerPopulator;
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
	 * @return the sapRevenueCloudConfigurationModelGenericDao
	 */
	public GenericDao getSapRevenueCloudConfigurationModelGenericDao()
	{
		return sapRevenueCloudConfigurationModelGenericDao;
	}

	/**
	 * @param sapRevenueCloudConfigurationModelGenericDao
	 *           the sapRevenueCloudConfigurationModelGenericDao to set
	 */
	public void setSapRevenueCloudConfigurationModelGenericDao(final GenericDao sapRevenueCloudConfigurationModelGenericDao)
	{
		this.sapRevenueCloudConfigurationModelGenericDao = sapRevenueCloudConfigurationModelGenericDao;
	}

}
