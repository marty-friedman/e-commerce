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
package de.hybris.platform.webservices.dto;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


/**
 *
 */
@XmlRootElement(name = "variantAttribute")
public class VariantAttributeDTO
{
	private String qualifier;
	private String value;

	public VariantAttributeDTO()
	{
		//no-arg constructor
	}

	public VariantAttributeDTO(final String qualifier, final Object value)
	{
		this.qualifier = qualifier;
		this.value = String.valueOf(value);
	}

	/**
	 * @return the name
	 */
	@XmlAttribute(name = "name")
	public String getQualifier()
	{
		return qualifier;
	}

	/**
	 * @param qualifier the qualifier to set
	 */
	public void setQualifier(final String qualifier)
	{
		this.qualifier = qualifier;
	}

	/**
	 * @return the value
	 */
	@XmlAttribute
	public String getValue()
	{
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(final String value)
	{
		this.value = value;
	}
}
