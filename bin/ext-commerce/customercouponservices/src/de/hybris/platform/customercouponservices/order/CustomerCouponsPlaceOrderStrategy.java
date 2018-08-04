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
package de.hybris.platform.customercouponservices.order;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;


/**
 * A strategy defined for Customer Coupons after place order
 */
public interface CustomerCouponsPlaceOrderStrategy
{
	/**
	 * Remove the coupons from Customer. And reset the notification status.
	 *
	 * @param currentUser
	 *           the current user to remove from related user group
	 * @param order
	 *           the order to find the applied coupons
	 */
	void removeCouponsForCustomer(UserModel currentUser, OrderModel order);
	
	/**
	 * Redirect the continue url to the Open-Catalogue if there is coupon code 
	 * in continue url.
	 */
	void updateContinueUrl();
}
