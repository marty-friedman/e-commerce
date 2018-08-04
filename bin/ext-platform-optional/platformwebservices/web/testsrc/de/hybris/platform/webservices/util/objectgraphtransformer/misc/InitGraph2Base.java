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

public class InitGraph2Base
{
	// NUMBER1: has getter and setter
	public Number getNumber1()
	{
		return null;
	}

	public void setNumber1(@SuppressWarnings("unused") final Number nmb)
	{
		//nop
	}

	// NUMBER2: has getter and setter
	public Number getNumber2()
	{
		return null;
	}

	public void setNumber2(@SuppressWarnings("unused") final Number nmb)
	{
		//nop
	}

	// NUMBER3: has getter and setter
	public Number getNumber3()
	{
		return null;
	}


	// NUMBER4: has only setter
	public void setNumber4(@SuppressWarnings("unused") final Number nmb)
	{
		//nop
	}

	// NUMBER5: has only setter
	public void setNumber5(@SuppressWarnings("unused") final Number nmb)
	{
		//nop
	}

	// NUMBER6: has getter and protected setter
	public Number getNumber6()
	{
		return null;
	}

	protected void setNumber6(@SuppressWarnings("unused") final Number nmb)
	{
		//nop
	}

}
