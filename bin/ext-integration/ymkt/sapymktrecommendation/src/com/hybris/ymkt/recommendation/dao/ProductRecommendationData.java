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
package com.hybris.ymkt.recommendation.dao;

/**
 * Product Recommendation
 */
public class ProductRecommendationData
{
	private final String productCode;

	public ProductRecommendationData(final String productCode)
	{
		this.productCode = productCode;
	}

	public String getProductCode()
	{
		return productCode;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("ProductRecommendationData [productCode=");
		builder.append(productCode);
		builder.append("]");
		return builder.toString();
	}

}
