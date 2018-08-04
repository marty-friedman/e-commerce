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
package de.hybris.platform.entitlementservices.facades;

import com.hybris.services.entitlements.api.GrantData;
import de.hybris.platform.entitlementservices.data.EmsGrantData;
import de.hybris.platform.entitlementservices.exception.EntitlementFacadeException;

import java.util.Collection;

/**
 * EntitlementFacadeDecorator interface.
 */
public interface EntitlementFacadeDecorator
{
	/**
	 * Create Entitlement.
	 *
	 * @param emsGrantData
	 * the emsGrantData to create.
	 * @return id of created entitlement.
	 * @throws EntitlementFacadeException
	 *            in case creation has been failed.
	 */
	String createEntitlement(final EmsGrantData emsGrantData) throws EntitlementFacadeException;

    /**
     * Returns all entitlements for the current user.
     *
     * @return {@link Collection}<{@link GrantData}> the current user's entitlements
     */
    Collection<GrantData> getGrants(String userId) throws EntitlementFacadeException;
}
