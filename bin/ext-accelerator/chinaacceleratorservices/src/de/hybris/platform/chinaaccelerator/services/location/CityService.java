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
package de.hybris.platform.chinaaccelerator.services.location;





import de.hybris.platform.chinaaccelerator.services.model.location.CityModel;

import java.util.List;


public interface CityService
{
	List<CityModel> getCitiesByRegionCode(final String regionIsocode);

	CityModel getCityForCode(final String cityCode);

}
