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
package de.hybris.platform.sap.productconfig.facades.impl;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.facades.ConfigurationVariantUtil;
import de.hybris.platform.sap.sapmodel.model.ERPVariantProductModel;
import de.hybris.platform.variants.model.VariantTypeModel;


/**
 * Default implementaion of the {@link ConfigurationVariantUtil}.
 */
public class ConfigurationVariantUtilImpl implements ConfigurationVariantUtil
{
	@Override
	public boolean isCPQBaseProduct(final ProductModel productModel)
	{
		final VariantTypeModel variantType = productModel.getVariantType();
		if (variantType != null)
		{
			return variantType.getCode().equals(ERPVariantProductModel._TYPECODE);
		}
		return false;
	}
}
