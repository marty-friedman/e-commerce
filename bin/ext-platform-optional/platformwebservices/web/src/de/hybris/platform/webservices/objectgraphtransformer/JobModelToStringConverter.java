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
package de.hybris.platform.webservices.objectgraphtransformer;

import de.hybris.platform.cronjob.dto.JobDTO;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyInterceptor;


public class JobModelToStringConverter implements PropertyInterceptor<JobDTO, String>
{

	@Override
	public String intercept(final PropertyContext ctx, final JobDTO source)
	{
		return source.getCode();
	}

}
