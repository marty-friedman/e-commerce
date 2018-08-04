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
package de.hybris.platform.sap.sapordermgmtservices.cart;

import de.hybris.platform.commercefacades.order.data.CartData;


/**
 * Service for checking, retrieving session cart held in the SAP back end
 */
public interface CartCheckoutBaseService
{


	/**
	 * Retrieves the session cart held in the SAP back end
	 *
	 * @return Current session cart
	 */
	public abstract CartData getSessionCart();

	/**
	 * Checks if a session cart exists held in the SAP back end
	 *
	 * @return Does the session cart exist?
	 */
	public abstract boolean hasSessionCart();

	/**
	 * Returns session cart, sorted in inverted order if required
	 *
	 * @param recentlyAddedFirst
	 *           If true, recently added items will be returned first (Standard sorting will be inverted)
	 * @return Session Cart
	 */
	CartData getSessionCart(boolean recentlyAddedFirst);

	/**
	 * Removes the existing session cart and releases the underlying LO-API session in SD. Afterwards, the cart is
	 * initial
	 */
	void removeSessionCart();

}
