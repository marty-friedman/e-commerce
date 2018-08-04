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
package de.hybris.platform.sap.productconfig.facades;

import de.hybris.platform.core.model.product.ProductModel;


/**
 * Utility service for variants of configurable products
 */
public interface ConfigurationVariantUtil
{
	/**
	 * Determines whether a product acts as a base product for variant products.
	 *
	 * @param productModel
	 *           productmodel which is tested for variants
	 * @return true if productmodel has variants
	 */
	boolean isCPQBaseProduct(final ProductModel productModel);
}
