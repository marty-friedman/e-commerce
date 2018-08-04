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
package de.hybris.platform.webservices.cache;

import de.hybris.platform.webservices.RestResource;



/**
 * @deprecated since ages
 *
 */
@Deprecated
public interface CachingStrategy<RESOURCE>
{
	/**
	 * UID returned by the getUID method should return String computed from RESOURCE's important values. getUID method is
	 * used to tell if any of RESOURCE's important values has changed
	 * 
	 * @return unique ID for given RESOURCE
	 */
	String getUID(RestResource resource, RESOURCE resourceValue);
}
