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
package de.hybris.platform.selectivecartservices;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.wishlist2.model.Wishlist2EntryModel;
import de.hybris.platform.wishlist2.model.Wishlist2Model;

import java.util.Date;


/**
 * Service to read and update {@link Wishlist2Model Wishlist2Model}s.
 *
 * @spring.bean selectiveCartService
 */
public interface SelectiveCartService
{
	/**
	 * get wishlist for selective-cart and current user
	 *
	 * @return the wishlist entry
	 */
	Wishlist2Model getWishlistForSelectiveCart();

	/**
	 * get wishlist entry based on a product in given wishlist
	 *
	 * @param product
	 *           the given product
	 * @return the wishlist entry
	 */
	Wishlist2EntryModel getWishlistEntryForProduct(ProductModel product);

	/**
	 * get wishlist entry based on a product in given wishlist
	 *
	 * @param product
	 *           the given product
	 * @param wishlist
	 *           the given wishlist
	 * @return the wishlist entry
	 */
	Wishlist2EntryModel getWishlistEntryForProduct(ProductModel product, Wishlist2Model wishlist);

	/**
	 * remove wishlist entry based on a product in given wishlist
	 *
	 * @param product
	 *           the given product
	 * @param wishlist
	 *           the given wishlist
	 */
	void removeWishlistEntryForProduct(ProductModel product, Wishlist2Model wishlist);

	/**
	 * update the quantity of the give wishlist entry
	 *
	 * @param wishlistEntry
	 *           the given wishlist
	 * @param quantity
	 *           the total number of quantity
	 */
	void updateQuantityForWishlistEntry(Wishlist2EntryModel wishlistEntry, Integer quantity);


	/**
	 * create a wish list for current customer
	 *
	 * @return the wishlist for selective cart
	 */
	Wishlist2Model createWishlist();

	/**
	 * save wishlist entry based on a product in given wishlist and save addToCartTime also
	 *
	 * @param product
	 *           the given product
	 * @param wishlist
	 *           the given wishlist
	 * @param addToCartTime
	 *           the added to cart time for a wishlist entry
	 * @return saved wishlist entry
	 */
	Wishlist2EntryModel saveWishlistEntryForProduct(ProductModel product, Wishlist2Model wishlist, Date addToCartTime);

	/**
	 * update to the original addToCartTime when move the wishlistentry to cart
	 *
	 * @param cartCode
	 *           the cart code returned when modify cart
	 * @param entryNumber
	 *           the entry number returned when modify cart
	 * @param addToCartTime
	 *           the added to cart time for a wishlist entry
	 */
	void updateCartTimeForOrderEntry(final String cartCode, final int entryNumber, Date addToCartTime);
}
