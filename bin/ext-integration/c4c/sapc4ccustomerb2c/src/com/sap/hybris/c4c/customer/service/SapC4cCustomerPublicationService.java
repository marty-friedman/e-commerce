package com.sap.hybris.c4c.customer.service;
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

import java.io.IOException;

import com.sap.hybris.c4c.customer.dto.C4CCustomerData;


/**
 *
 */
public interface SapC4cCustomerPublicationService
{

	public void publishCustomerToCloudPlatformIntegration(C4CCustomerData customerJson) throws IOException;
}
