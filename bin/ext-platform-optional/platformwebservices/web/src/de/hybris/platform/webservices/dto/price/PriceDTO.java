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
package de.hybris.platform.webservices.dto.price;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "price")
public class PriceDTO
{
	private Double value = null;
	private String currency = null;
	private String symbol = null;

	/**
	 * @return the value
	 */
	@XmlAttribute
	public Double getValue()
	{
		return value;
	}

	/**
	 * @param value
	 *           the value to set
	 */
	public void setValue(final Double value)
	{
		this.value = value;
	}

	/**
	 * @return the currency
	 */
	@XmlAttribute
	public String getCurrency()
	{
		return currency;
	}

	/**
	 * @param currency
	 *           the currency to set
	 */
	public void setCurrency(final String currency)
	{
		this.currency = currency;
	}



	/**
	 * @return the currencySym
	 */
	@XmlAttribute
	public String getSymbol()
	{
		return symbol;
	}

	/**
	 * @param symbol
	 *           the currencySym to set
	 */
	public void setSymbol(final String symbol)
	{
		this.symbol = symbol;
	}


}
