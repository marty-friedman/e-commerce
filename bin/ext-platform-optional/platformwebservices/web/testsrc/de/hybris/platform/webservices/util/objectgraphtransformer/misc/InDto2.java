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
package de.hybris.platform.webservices.util.objectgraphtransformer.misc;

import de.hybris.platform.webservices.util.objectgraphtransformer.GraphNode;


@GraphNode(target = InDto2.class)
public class InDto2 extends InDto1
{
	private String anotherValue;

	/**
	 * @return the anotherValue
	 */
	public String getAnotherValue()
	{
		return anotherValue;
	}

	/**
	 * @param anotherValue the anotherValue to set
	 */
	public void setAnotherValue(final String anotherValue)
	{
		this.anotherValue = anotherValue;
	}


}
