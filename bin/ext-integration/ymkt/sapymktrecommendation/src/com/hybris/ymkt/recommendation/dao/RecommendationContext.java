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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import com.hybris.ymkt.recommendation.constants.SapymktrecommendationConstants;


/**
 * Recommendation Context
 */
public class RecommendationContext
{
	protected String cartItemDSType;
	protected boolean includeCart;
	protected boolean includeRecent;
	protected List<String> leadingCategoryIds = Collections.emptyList();
	protected String leadingItemDSType;
	protected String leadingItemType;
	protected String leadingProductId;
	protected String scenarioId;

	/**
	 * @return cartItemDSType
	 */
	public String getCartItemDSType()
	{
		return cartItemDSType;
	}

	/**
	 * @return the leadingCategoryId
	 */
	public List<String> getLeadingCategoryIds()
	{
		return leadingCategoryIds;
	}

	/**
	 * @return leadingItemDSType
	 */
	public String getLeadingItemDSType()
	{
		return leadingItemDSType;
	}

	/**
	 * @return list of leading item ids
	 */
	public List<String> getLeadingItemId()
	{
		if (SapymktrecommendationConstants.CATEGORY.equals(this.leadingItemType))
		{
			return this.getLeadingCategoryIds().stream().filter(StringUtils::isNotEmpty).collect(Collectors.toList());
		}
		if (SapymktrecommendationConstants.PRODUCT.equals(this.leadingItemType) && //
				StringUtils.isNotEmpty(this.getLeadingProductId()))
		{
			return Collections.singletonList(this.getLeadingProductId());
		}

		return Collections.emptyList();
	}

	/**
	 * @return leadingItemType
	 */
	public String getLeadingItemType()
	{
		return leadingItemType;
	}

	/**
	 * @return leadingProductId
	 */
	public String getLeadingProductId()
	{
		return leadingProductId;
	}

	/**
	 * @return recotype
	 */
	public String getScenarioId()
	{
		return scenarioId;
	}

	/**
	 * @return includeCart
	 */
	public boolean isIncludeCart()
	{
		return includeCart;
	}

	/**
	 * @return the includeRecent
	 */
	public boolean isIncludeRecent()
	{
		return includeRecent;
	}

	/**
	 * @param cartItemDSType
	 */
	public void setCartItemDSType(final String cartItemDSType)
	{
		this.cartItemDSType = cartItemDSType;
	}

	/**
	 * @param includeCart
	 */
	public void setIncludeCart(final boolean includeCart)
	{
		this.includeCart = includeCart;
	}

	/**
	 * @param includeRecent
	 *           the includeRecent to set
	 */
	public void setIncludeRecent(final boolean includeRecent)
	{
		this.includeRecent = includeRecent;
	}

	/**
	 * @param leadingCategoryIds
	 *           the leadingCategoryId to set
	 */
	public void setLeadingCategoryIds(final List<String> leadingCategoryIds)
	{
		this.leadingCategoryIds = leadingCategoryIds;
	}

	/**
	 * @param leadingItemDSType
	 */
	public void setLeadingItemDSType(final String leadingItemDSType)
	{
		this.leadingItemDSType = leadingItemDSType;
	}

	/**
	 * @param leadingItemType
	 */
	public void setLeadingItemType(final String leadingItemType)
	{
		this.leadingItemType = leadingItemType;
	}

	/**
	 * @param leadingProductId
	 */
	public void setLeadingProductId(final String leadingProductId)
	{
		this.leadingProductId = leadingProductId;
	}

	/**
	 * @param scenarioId
	 */
	public void setScenarioId(final String scenarioId)
	{
		this.scenarioId = scenarioId;
	}
}
