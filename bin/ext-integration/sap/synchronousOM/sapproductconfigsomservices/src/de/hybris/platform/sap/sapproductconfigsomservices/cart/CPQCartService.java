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
package de.hybris.platform.sap.sapproductconfigsomservices.cart;

import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.sapordermgmtservices.cart.CartService;


/**
 * Service for updating cart data like Adding/updating cart entries for configurable product.
 *
 */
public interface CPQCartService extends CartService
{

	/**
	 * Adds a new configuration to the cart. A new item will be created, based on the root instance of the config model
	 *
	 * @param configModel
	 * @return Key of new item
	 */
	String addConfigurationToCart(ConfigModel configModel);

	/**
	 * Updates the configuration attached to an item
	 *
	 * @param key
	 *           Key of existing item
	 * @param configModel
	 *           Configuration
	 */
	void updateConfigurationInCart(String key, ConfigModel configModel);

}
