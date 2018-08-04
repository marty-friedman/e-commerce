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
package de.hybris.platform.selectivecartfacades;

import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.selectivecartfacades.data.Wishlist2Data;

import java.util.List;


/**
 * Service to read and update {@link Wishlist2Data Wishlist2Data}s.
 *
 * @spring.bean selectiveCartFacade
 */
public interface SelectiveCartFacade
{
	/**
	 * get the wishlist2data for selectivecart of current user
	 *
	 * @return wishlist2data
	 */
	Wishlist2Data getWishlistForSelectiveCart();

	/**
	 * Remove the entry from wishlist
	 *
	 * @param productCode
	 *           the product's code
	 */
	void removeWishlistEntryForProduct(String productCode);

	/**
	 * 1. Remove the entry from wishlist
	 *
	 * 2. Add to cart as a cart entry
	 *
	 * @param productCode
	 *           the product's code
	 * @throws CommerceCartModificationException
	 *            when removing wish list entry error
	 */
	void addToCartFromWishlist(String productCode) throws CommerceCartModificationException;

	/**
	 * 1. Remove the entry from wishlist
	 *
	 * 2. Add to cart as a cart entry
	 *
	 * @throws CommerceCartModificationException
	 *            when the cart could not be modified
	 */
	void updateCartFromWishlist() throws CommerceCartModificationException;

	/**
	 * 1. Remove the entry from cart
	 *
	 * 2. Add the entry to wishlist
	 *
	 * @param orderEntry
	 *           the order entry data
	 * @throws CommerceCartModificationException
	 *            when the cart could not be modified
	 *
	 */
	void addToWishlistFromCart(OrderEntryData orderEntry) throws CommerceCartModificationException;

	/**
	 * 1. Remove the entry from cart
	 *
	 * 2. Add the entry to wishlist
	 *
	 * @param entryNumber
	 *           the entry number
	 * @throws CommerceCartModificationException
	 *            when the cart could not be modified
	 */
	void addToWishlistFromCart(Integer entryNumber) throws CommerceCartModificationException;

	/**
	 * 1. Remove the entry from cart
	 *
	 * 2. Add the entry to wishlist
	 *
	 * @param productCodes
	 *           product codes
	 * @throws CommerceCartModificationException
	 *            when the cart could not be modified
	 */
	void addToWishlistFromCart(final List<String> productCodes) throws CommerceCartModificationException;





	/**
	 * Get Order entries that are converted from Wishlist2EntryModel
	 *
	 * @return List<OrderEntryData> list of order entries
	 *
	 */
	List<OrderEntryData> getWishlistOrdersForSelectiveCart();
}
