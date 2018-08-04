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
package de.hybris.platform.webservices.util.objectgraphtransformer.productgraph2;

public class Tp2ExtendedProductDTO extends Tp2SimpleProductDTO
{
	private String manufacturerName = null;


	public Tp2ExtendedProductDTO()
	{
		super();
	}

	public Tp2ExtendedProductDTO(final String code, final String ean)
	{
		super(code, ean);
	}

	public Tp2ExtendedProductDTO(final String code, final String ean, final String manufacturerName)
	{
		super(code, ean);
		this.manufacturerName = manufacturerName;
	}

	/**
	 * @return the manufacturerName
	 */
	public String getManufacturerName()
	{
		return manufacturerName;
	}

	/**
	 * @param manufacturerName the manufacturerName to set
	 */
	public void setManufacturerName(final String manufacturerName)
	{
		this.manufacturerName = manufacturerName;
	}



}
