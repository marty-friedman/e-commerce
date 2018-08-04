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
package de.hybris.platform.webservices;

import javax.ws.rs.core.Response;



public class InternalServerErrorException extends YWebservicesException
{

	public InternalServerErrorException()
	{
		super();
	}

	public InternalServerErrorException(final String message, final Throwable cause)
	{
		super(message, cause);
	}

	public InternalServerErrorException(final String message)
	{
		super(message);
	}

	public InternalServerErrorException(final Throwable cause)
	{
		super(cause);
	}

	/*
	 * (non-Javadoc)
	 * @see de.hybris.platform.webservices.YWebservicesException#getResponseStatus()
	 */
	@Override
	public int getResponseStatus()
	{
		return Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
	}

}
