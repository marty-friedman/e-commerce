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
package de.hybris.platform.selectivecartservices.daos;

import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.wishlist2.model.Wishlist2Model;


/**
 * Provide methods extends DefaultWishlist2Dao
 *
 * @see de.hybris.platform.wishlist2.impl.daos.impl.DefaultWishlist2Dao
 */
public interface SelectiveCartDao
{
	/**
	 * find wishlist by name for current user
	 */
	Wishlist2Model findWishlistByName(UserModel user, String name);

	/**
	 * @param cartCode
	 *           the cart code of given cart
	 * @param entryNumber
	 *           the entry number to be found
	 * @return the cart entry found
	 */
	CartEntryModel findCartEntryByCartCodeAndEntryNumber(String cartCode, Integer entryNumber);
}
