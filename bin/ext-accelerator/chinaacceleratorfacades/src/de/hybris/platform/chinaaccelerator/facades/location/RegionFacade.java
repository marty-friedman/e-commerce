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
package de.hybris.platform.chinaaccelerator.facades.location;

import de.hybris.platform.commercefacades.user.data.RegionData;

import java.util.List;


public interface RegionFacade
{
	List<RegionData> getRegionsForCountryCode(final String countryCode);

	RegionData getRegionByCountryAndCode(final String countryCode, final String regionCode);

}
