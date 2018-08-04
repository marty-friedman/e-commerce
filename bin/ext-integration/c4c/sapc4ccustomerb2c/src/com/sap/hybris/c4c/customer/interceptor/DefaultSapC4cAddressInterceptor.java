/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package com.sap.hybris.c4c.customer.interceptor;

import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

import java.util.ArrayList;
import java.util.List;

import com.sap.hybris.c4c.customer.dto.C4CCustomerData;
import com.sap.hybris.c4c.customer.event.SapC4cCustomerUpdateEvent;
import com.sap.hybris.c4c.customer.util.SapC4cCustomerUtils;


/**
 * Intercepts any changes in address and creates customer update event if any replicated relavant fields are changed
 */
public class DefaultSapC4cAddressInterceptor implements ValidateInterceptor<AddressModel>, RemoveInterceptor<AddressModel>
{

	private EventService eventService;
	private SapC4cCustomerUtils customerUtil;

	@Override
	public void onValidate(final AddressModel addressModel, final InterceptorContext ctx) throws InterceptorException
	{

		if (addressModel.getOwner() != null && addressModel.getOwner().getClass() != CustomerModel.class)
		{
			return;
		}
		final CustomerModel customerModel = (CustomerModel) addressModel.getOwner();
		C4CCustomerData customerData;

		final List<AddressModel> addressModels = new ArrayList<>(customerModel.getAddresses());

		if (ctx.isNew(addressModel))
		{
			addressModels.add(addressModel);
			customerData = getCustomerUtil().getCustomerDataForCustomer(customerModel, addressModels);
		}
		else
		{
			if (shouldReplicate(addressModel, ctx))
			{
				addressModels.remove(addressModel);
				addressModels.add(addressModel);
				customerData = getCustomerUtil().getCustomerDataForCustomer(customerModel, addressModels);
			}
			else
			{
				return;
			}
		}

		final SapC4cCustomerUpdateEvent event = new SapC4cCustomerUpdateEvent();
		event.setCustomerData(customerData);
		getEventService().publishEvent(event);
	}

	@Override
	public void onRemove(final AddressModel addressModel, final InterceptorContext ctx) throws InterceptorException
	{
		if ((addressModel.getOwner().getClass() != CustomerModel.class))
		{
			return;
		}
		final CustomerModel customerModel = (CustomerModel) addressModel.getOwner();

		final List<AddressModel> addressModels = new ArrayList<>(customerModel.getAddresses());
		addressModels.remove(addressModel);

		final C4CCustomerData customerData = getCustomerUtil().getCustomerDataForCustomer(customerModel, addressModels);

		final SapC4cCustomerUpdateEvent event = new SapC4cCustomerUpdateEvent();
		event.setCustomerData(customerData);
		getEventService().publishEvent(event);
	}

	/**
	 * Checks if any replicated relavant fields are modified and returns a boolean
	 */
	protected boolean shouldReplicate(final AddressModel addressModel, final InterceptorContext ctx)
	{

		boolean result = ctx.isModified(addressModel, AddressModel.EMAIL) || ctx.isModified(addressModel, AddressModel.COUNTRY)
				|| ctx.isModified(addressModel, AddressModel.STREETNAME) || ctx.isModified(addressModel, AddressModel.STREETNUMBER);

		if (!result)
		{
			result = ctx.isModified(addressModel, AddressModel.TOWN) || ctx.isModified(addressModel, AddressModel.DISTRICT)
					|| ctx.isModified(addressModel, AddressModel.PHONE1) || ctx.isModified(addressModel, AddressModel.PHONE2);
		}

		if (!result)
		{
			result = ctx.isModified(addressModel, AddressModel.CELLPHONE) || ctx.isModified(addressModel, AddressModel.POSTALCODE)
					|| ctx.isModified(addressModel, AddressModel.FAX) || ctx.isModified(addressModel, AddressModel.POBOX);
		}

		return result;
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
	 * @return the customerUtil
	 */
	public SapC4cCustomerUtils getCustomerUtil()
	{
		return customerUtil;
	}

	/**
	 * @param customerUtil
	 *           the customerUtil to set
	 */
	public void setCustomerUtil(final SapC4cCustomerUtils customerUtil)
	{
		this.customerUtil = customerUtil;
	}

}
