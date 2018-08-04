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
package de.hybris.platform.sap.productconfig.runtime.interf.model.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;

import java.math.BigDecimal;


/**
 * Immutable Object
 *
 */
public final class ZeroPriceModelImpl extends PriceModelImpl
{

	@Override
	public void setCurrency(final String currency)
	{
		throw new IllegalArgumentException("ZeroPriceModelImpl is immutable");
	}

	@Override
	public void setPriceValue(final BigDecimal priceValue)
	{
		throw new IllegalArgumentException("ZeroPriceModelImpl is immutable");
	}

	@Override
	public String getCurrency()
	{
		return "";
	}

	@Override
	public BigDecimal getPriceValue()
	{
		return BigDecimal.ZERO;
	}

	@Override
	@SuppressWarnings(
	{ "squid:S1182", "squid:S2975" })
	public PriceModel clone()
	{
		//We explicitly want the same instance when cloning this one, therefore hiding sonar check
		return this;
	}
}
