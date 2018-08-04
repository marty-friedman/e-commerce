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
package de.hybris.platform.sap.saprevenuecloudorder.service;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.CancelSubscription;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.ExtendSubscription;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.Subscription;
import de.hybris.platform.subscriptionservices.model.BillingFrequencyModel;

import java.util.List;


/**
 * Service API that provides methods SAP RevenueCloud Subscription Orders
 */
public interface SapRevenueCloudSubscriptionService {

	/**
	 * fetch subscription details using customerId
	 *
	 * @param clientId
	 *           customerId
	 *
	 * @return {@link List} list of subscriptions
	 *
	 */
	List<Subscription> getSubscriptionsByClientId(String clientId);

	/**
	 * fetch subscription details using subscriptionId
	 *
	 * @param subscriptionsId
	 *           subscription code
	 *
	 * @return {@link Subscription } Subscription details
	 */
	Subscription getSubscriptionById(String subscriptionsId);

	/**
	 * cancel a subscription based on subscription code
	 *
	 * @return cancellation status
	 */
	String cancelSubscription(String code, CancelSubscription subscriptionCode);

	/**
	 * extend a subscription based on subscription code
	 *
	 * @return extension status
	 */
	String extendSubscription(String code, ExtendSubscription subscriptionCode);

	/**
	 * get billingFrequency for specific product
	 *
	 * @param productModel
	 *           - product model 
	 *
	 * @return {@link BillingFrequencyModel}
	 */
	BillingFrequencyModel getBillingFrequency(final ProductModel productModel);
}
