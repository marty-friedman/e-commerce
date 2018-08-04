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
package de.hybris.platform.sap.saprevenuecloudorder.facade;

import de.hybris.platform.subscriptionfacades.SubscriptionFacade;
import de.hybris.platform.subscriptionfacades.data.SubscriptionData;
import de.hybris.platform.subscriptionfacades.exceptions.SubscriptionFacadeException;


/**
 * Facade which provides functionality to manage subscriptions.
 */
public interface SapRevenueCloudSubscriptionFacade extends SubscriptionFacade

{
	/**
	 * cancel the subscription
	 *
	 * @param subscriptionData Subscription Data
	 *
	 * @return status
	 *
	 * @throws SubscriptionFacadeException if cancellation fails
	 */
	boolean cancelSubscription(final SubscriptionData subscriptionData) throws SubscriptionFacadeException;

	/**
	 * extends the subscription 
	 * 
	 * @param subscriptionData Subscription Data
	 * @return status
	 * @throws SubscriptionFacadeException if extension fails
	 */
	boolean extendSubscription(SubscriptionData subscriptionData) throws SubscriptionFacadeException;
}
