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
 * Default implementation of the {@link PriceModel}
 */
public class PriceModelImpl extends BaseModelImpl implements PriceModel
{
	private BigDecimal priceValue;
	private String currency;

	@Override
	public void setCurrency(final String currency)
	{
		this.currency = currency;
	}

	@Override
	public String getCurrency()
	{
		return currency;
	}

	@Override
	public BigDecimal getPriceValue()
	{
		return priceValue;
	}

	@Override
	public void setPriceValue(final BigDecimal priceValue)
	{
		this.priceValue = priceValue;
	}

	@Override
	public int hashCode()
	{
		int result = super.hashCode();
		result = PRIME * result + ((currency == null) ? 0 : currency.hashCode());
		result = PRIME * result + ((priceValue == null) ? 0 : priceValue.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final PriceModelImpl other = (PriceModelImpl) obj;
		if (!super.equals(other))
		{
			return false;
		}
		if (currency == null)
		{
			if (other.currency != null)
			{
				return false;
			}
		}
		else if (!currency.equals(other.currency))
		{
			return false;
		}
		if (priceValue == null)
		{
			if (other.priceValue != null)
			{
				return false;
			}
		}
		else if (!priceValue.equals(other.priceValue))
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		final StringBuilder builder = new StringBuilder(50);
		builder.append("[PriceModelImpl [priceValue=");
		builder.append(priceValue);
		builder.append(", currency=");
		builder.append(currency);
		builder.append("]]");
		return builder.toString();
	}

	@Override
	public boolean hasValidPrice()
	{
		boolean hasPrice = true;
		if (hasNoPrice() || hasNoCurrency(getCurrency()))
		{
			hasPrice = false;
		}
		return hasPrice;
	}

	protected boolean hasNoCurrency(final String currency)
	{
		return currency == null || currency.isEmpty();
	}

	protected boolean hasNoPrice()
	{
		return PriceModel.NO_PRICE.equals(this) || getPriceValue() == null;
	}
}
