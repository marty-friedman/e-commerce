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
package com.sap.hybris.saprevenuecloudcustomer.service;

import java.io.IOException;

import com.sap.hybris.saprevenuecloudcustomer.dto.Customer;
import com.sap.hybris.scpiconnector.data.ResponseData;


/**
 * Service to publish Revenue Cloud Customer
 */
public interface SapRevenueCloudCustomerPublicationService
{
	/**
	 * Publishes Customer Json data to the configured Cloud Platform Integration iFlow
	 *
	 * @param customerJson
	 *           Customer Json object
	 *
	 * @throws IOException
	 *            if unable to publish.
	 *
	 * @return {@link ResponseData}
	 *
	 */
	public ResponseData publishCustomerToCloudPlatformIntegration(Customer customerJson) throws IOException;
}
