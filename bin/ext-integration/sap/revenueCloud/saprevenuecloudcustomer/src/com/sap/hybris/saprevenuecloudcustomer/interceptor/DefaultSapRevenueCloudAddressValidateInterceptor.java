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
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.sap.hybris.saprevenuecloudcustomer.dto.Address;
import com.sap.hybris.saprevenuecloudcustomer.dto.Customer;
import com.sap.hybris.saprevenuecloudcustomer.event.SapRevenueCloudCustomerUpdateEvent;
import com.sap.hybris.saprevenuecloudproduct.model.SAPRevenueCloudConfigurationModel;


/**
 * Updates already existing customer in Revenue Cloud.
 */
public class DefaultSapRevenueCloudAddressValidateInterceptor implements ValidateInterceptor<AddressModel>
{
	private EventService eventService;
	private Populator customerPopulator;
	private Populator addressPopulator;
	private GenericDao sapRevenueCloudConfigurationModelGenericDao;


	@Override
	public void onValidate(final AddressModel addressModel, final InterceptorContext ctx) throws InterceptorException
	{
		final SAPRevenueCloudConfigurationModel revenueCloudConfig = getRevenueCloudConfiguration();
		if (revenueCloudConfig == null || revenueCloudConfig.getReplicateCustomer() == null
				|| !revenueCloudConfig.getReplicateCustomer())
		{
			return;
		}

		if ((addressModel.getOwner().getClass() != CustomerModel.class))
		{
			return;
		}
		final CustomerModel customerModel = (CustomerModel) addressModel.getOwner();
		if (shouldReplicate(addressModel, customerModel, ctx))
		{
			final Customer customerJson = new Customer();
			getCustomerPopulator().populate(customerModel, customerJson);
			final Address newAddress = new Address();
			getAddressPopulator().populate(addressModel, newAddress);
			final List<Address> addressList = new ArrayList<Address>();
			addressList.add(newAddress);
			customerJson.setAddresses(addressList);
			final SapRevenueCloudCustomerUpdateEvent event = new SapRevenueCloudCustomerUpdateEvent();
			event.setCustomerJson(customerJson);
			getEventService().publishEvent(event);
		}
	}

	protected boolean shouldReplicate(final AddressModel addressModel, final CustomerModel customerModel,
			final InterceptorContext ctx)
	{
		//Replicate address only if customer has been replicated already and if it is default shipping address
		if (customerModel.getRevenueCloudCustomerId() != null && !customerModel.getRevenueCloudCustomerId().isEmpty()
				&& (customerModel.getDefaultShipmentAddress() == null || customerModel.getDefaultShipmentAddress() == addressModel))
		{
			return getChangeAttributesList().stream().anyMatch(attribute -> ctx.isModified(addressModel, attribute));
		}
		return false;
	}

	protected List<String> getChangeAttributesList()
	{
		final List<String> attributeList = new ArrayList<String>();
		attributeList.add(AddressModel.COUNTRY);
		attributeList.add(AddressModel.POSTALCODE);
		attributeList.add(AddressModel.REGION);
		attributeList.add(AddressModel.TOWN);
		attributeList.add(AddressModel.LINE1);
		attributeList.add(AddressModel.LINE2);
		attributeList.add(AddressModel.PHONE1);
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
