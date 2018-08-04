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





import de.hybris.platform.chinaaccelerator.services.model.location.DistrictModel;

import java.util.List;


public interface DistrictService
{
	List<DistrictModel> getDistrictsByCityCode(final String cityCode);

	DistrictModel getDistrictByCode(final String districtCode);
}
