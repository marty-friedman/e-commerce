/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.entitlementservices.facades.impl;

import de.hybris.platform.entitlementservices.data.EmsGrantData;
import de.hybris.platform.entitlementservices.facades.EntitlementFacadeDecorator;

import java.util.Collection;
import java.util.Collections;

import com.hybris.services.entitlements.api.GrantData;


/**
 * DefaultEntitlementFacadeDecorator class. Facade to Entitlements and Metering service.
 */
public class DefaultEntitlementFacadeDecorator implements EntitlementFacadeDecorator
{

	@Override
	public String createEntitlement(final EmsGrantData emsGrantData)
	{
		return "";
	}

	@Override
	public Collection<GrantData> getGrants(final String userId)
	{
		return Collections.emptyList();
	}
}
