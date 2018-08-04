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
package com.sap.hybris.sapcustomerb2b.inbound;

import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PersistenceOperation;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * PrepareInterceptor for the handling of SAP addresses insure that the address for all which are related to a SAP ERP
 * Customer are in sync
 */
public class DefaultSAPCustomerAddressConsistencyInterceptor implements PrepareInterceptor
{
	private FlexibleSearchService flexibleSearchService;

	@Override
	public void onPrepare(final Object model, final InterceptorContext context) throws InterceptorException
	{

		// Check whether the model is an address model and whether the model is modified
		if (model instanceof AddressModel && context.isModified(model))
		{
			final AddressModel source = (AddressModel) model;

			// Check whether the SAP customer ID is not null and check if the address is already queue to be saved
			if (source.getSapCustomerID() != null && !context.contains(source, PersistenceOperation.SAVE))
			{
				final String sapCustomerId = source.getSapCustomerID();

				// Get all related addresses
				List<AddressModel> relatedAddresses = getRelatedAddresses(sapCustomerId);

				// Update each address if they are different
				relatedAddresses.stream()
					.filter(a -> compareAddress(source, a))
					.forEach(a -> updateAddress(context, source, a));
			}
		}
	}

	/**
	 * Retrieves addresses which are related to sapCustomerId
	 * @param sapCustomerId
	 * @return
	 */
	protected List<AddressModel> getRelatedAddresses(final String sapCustomerId)
	{
		String query = "SELECT {PK} FROM {" + AddressModel._TYPECODE + "} WHERE {" + AddressModel.SAPCUSTOMERID + "} =?kunnr " +
				" and {" + AddressModel.DUPLICATE + "} = false";

		final FlexibleSearchQuery fsQuery = new FlexibleSearchQuery(query);
		fsQuery.addQueryParameter("kunnr", sapCustomerId);

		final SearchResult<AddressModel> searchResult = flexibleSearchService.search(fsQuery);
		if (searchResult != null)
		{
			return searchResult.getResult();
		}
		else
		{
			return Collections.emptyList();
		}
	}

	/**
	 * Updates an address to match a source
	 *
	 * @param context
	 * @param source Source address
	 * @param target target (related) address
	 */
	protected void updateAddress(final InterceptorContext context, final AddressModel source, final AddressModel target)
	{
		target.setStreetname(source.getStreetname());
		target.setStreetnumber(source.getStreetnumber());
		target.setPostalcode(source.getPostalcode());
		target.setTown(source.getTown());
		target.setCountry(source.getCountry());
		target.setRegion(source.getRegion());
		target.setPhone1(source.getPhone1());
		target.setFax(source.getFax());
		target.setPobox(source.getPobox());
		target.setCellphone(source.getCellphone());
		target.setDistrict(source.getDistrict());

		context.registerElementFor(target, PersistenceOperation.SAVE);
	}

	/**
	 * Compares two addresses to determine is they are different based on selected fields
	 *
	 * @param source
	 * @param target
	 * @return true if one or more fields are different, false is they are identical or it's the same PK
	 */
	protected boolean compareAddress(final AddressModel source, final AddressModel target)
	{
		if (source.getPk() == null || !source.getPk().equals(target.getPk()))
		{
			// Street name
			if (source.getStreetname() != null && !source.getStreetname().equals(target.getStreetname()) || (source
					.getStreetname() == null && target.getStreetname() != null))
			{
				return true;
			}

			// Street number
			if (source.getStreetnumber() != null && !source.getStreetnumber().equals(target.getStreetnumber()) || (source
					.getStreetnumber() == null && target.getStreetnumber() != null))
			{
				return true;
			}

			// Postal code
			if (source.getPostalcode() != null && !source.getPostalcode().equals(target.getPostalcode()) || (source
					.getPostalcode() == null && target.getPostalcode() != null))
			{
				return true;
			}

			// Town
			if (source.getTown() != null && !source.getTown().equals(target.getTown()) || (source.getTown() == null && target
					.getTown() != null))
			{
				return true;
			}

			// Country
			if (source.getCountry() != null && !source.getCountry().equals(target.getCountry()) || (source
					.getCountry() == null && target.getCountry() != null))
			{
				return true;
			}

			// Region
			if (source.getRegion() != null && !source.getRegion().equals(target.getRegion()) || (source
					.getRegion() == null && target.getRegion() != null))
			{
				return true;
			}

			// Phone 1
			if (source.getPhone1() != null && !source.getPhone1().equals(target.getPhone1()) || (source
					.getPhone1() == null && target.getPhone1() != null))
			{
				return true;
			}

			// Fax
			if (source.getFax() != null && !source.getFax().equals(target.getFax()) || (source.getFax() == null && target
					.getFax() != null))
			{
				return true;
			}

			// PO Box
			if (source.getPobox() != null && !source.getPobox().equals(target.getPobox()) || (source.getPobox() == null && target
					.getPobox() != null))
			{
				return true;
			}

			// Cellphone
			if (source.getCellphone() != null && !source.getCellphone().equals(target.getCellphone()) || (source
					.getCellphone() == null && target.getCellphone() != null))
			{
				return true;
			}

			// District
			if (source.getDistrict() != null && !source.getDistrict().equals(target.getDistrict()) || (source
					.getDistrict() == null && target.getDistrict() != null))
			{
				return true;
			}
		}

		return false;
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}
}
