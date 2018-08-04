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
package de.hybris.platform.chinaaccelerator.services.customer.daos;

import de.hybris.platform.core.model.user.CustomerModel;

import java.util.List;


/**
 * CustomerDao
 */
public interface CustomerDao
{
	/**
	 * Find customer for given mobileNumber
	 *
	 * @param mobileNumber
	 *           of the customer
	 * @return the list of customer
	 */
	public List<CustomerModel> findCustomerByMobileNumber(final String mobileNumber);
}
