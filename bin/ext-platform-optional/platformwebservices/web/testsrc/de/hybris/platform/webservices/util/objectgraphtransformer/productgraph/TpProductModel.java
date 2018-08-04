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
package de.hybris.platform.webservices.util.objectgraphtransformer.productgraph;

import java.util.Collection;


public class TpProductModel
{

	private String code;
	private String name;
	private String ean;
	private String description;
	private String manufacturerName;
	private TpUnitModel unit;
	private Collection<TpMediaModel> thumbnails;


	/**
	 * @return the manufacturerName
	 */
	public String getManufacturerName()
	{
		return manufacturerName;
	}

	/**
	 * @param manufacturerName
	 *           the manufacturerName to set
	 */
	public void setManufacturerName(final String manufacturerName)
	{
		this.manufacturerName = manufacturerName;
	}


	/**
	 * @return the thumbnails
	 */
	public Collection<TpMediaModel> getThumbnails()
	{
		return thumbnails;
	}

	/**
	 * @param thumbnails
	 *           the thumbnails to set
	 */
	public void setThumbnails(final Collection<TpMediaModel> thumbnails)
	{
		this.thumbnails = thumbnails;
	}

	/**
	 * @return the unit
	 */
	public TpUnitModel getUnit()
	{
		return unit;
	}

	/**
	 * @param unit
	 *           the unit to set
	 */
	public void setUnit(final TpUnitModel unit)
	{
		this.unit = unit;
	}

	/**
	 * @return the code
	 */
	public String getCode()
	{
		return code;
	}

	/**
	 * @param code
	 *           the code to set
	 */
	public void setCode(final String code)
	{
		this.code = code;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name
	 *           the name to set
	 */
	public void setName(final String name)
	{
		this.name = name;
	}

	/**
	 * @return the ean
	 */
	public String getEan()
	{
		return ean;
	}

	/**
	 * @param ean
	 *           the ean to set
	 */
	public void setEan(final String ean)
	{
		this.ean = ean;
	}

	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * @param description
	 *           the description to set
	 */
	public void setDescription(final String description)
	{
		this.description = description;
	}
}
