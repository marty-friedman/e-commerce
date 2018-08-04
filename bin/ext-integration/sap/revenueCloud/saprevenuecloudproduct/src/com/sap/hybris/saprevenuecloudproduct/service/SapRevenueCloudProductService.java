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
package com.sap.hybris.saprevenuecloudproduct.service;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;


/**
 * Sap Reveunue Cloud Product Service interface
 */
public interface SapRevenueCloudProductService
{

	/**
	 * get the Subscription price for a specific price plan ID
	 *
	 * @param pricePlanId
	 *           - price plan ID for the {@link SubscriptionPricePlanModel}
	 *
	 * @return {@link SubscriptionPricePlanModel}
	 */
	SubscriptionPricePlanModel getSubscriptionPricePlanForId(final String pricePlanId, CatalogVersionModel catalogVersion);

}
