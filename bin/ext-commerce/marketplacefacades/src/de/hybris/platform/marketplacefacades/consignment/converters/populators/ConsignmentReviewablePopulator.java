/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.marketplacefacades.consignment.converters.populators;

import de.hybris.platform.commercefacades.order.data.ConsignmentData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.marketplacefacades.vendor.CustomerVendorReviewFacade;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import org.springframework.beans.factory.annotation.Required;


/**
 * A populator for setting 'reviewable' of ConsignmentData
 */
public class ConsignmentReviewablePopulator implements Populator<ConsignmentModel, ConsignmentData>
{

	private CustomerVendorReviewFacade customerVendorReviewFacade;

	@Override
	public void populate(ConsignmentModel source, ConsignmentData target) throws ConversionException
	{
		ServicesUtil.validateParameterNotNullStandardMessage("source", source);
		ServicesUtil.validateParameterNotNullStandardMessage("target", target);

		target.setReviewable(!getCustomerVendorReviewFacade().postedReview(source.getCode()));
	}


	protected CustomerVendorReviewFacade getCustomerVendorReviewFacade()
	{
		return customerVendorReviewFacade;
	}

	@Required
	public void setCustomerVendorReviewFacade(CustomerVendorReviewFacade customerVendorReviewFacade)
	{
		this.customerVendorReviewFacade = customerVendorReviewFacade;
	}

}
