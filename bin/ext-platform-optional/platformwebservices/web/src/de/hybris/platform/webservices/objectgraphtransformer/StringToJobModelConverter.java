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


public class StringToJobModelConverter implements PropertyInterceptor<String, JobDTO>
{

	@Override
	public JobDTO intercept(final PropertyContext ctx, final String code)
	{
		//		JobModel result = null;
		//		if (code != null)
		//		{
		//			final CronJobService cronJobService = ((YObjectGraphContext) ctx.getGraphContext()).getServices().getCronJobService();
		//			result = cronJobService.getJob(code);
		//		}
		//		return result;
		JobDTO result = null;
		if (code != null)
		{
			result = new JobDTO();
			result.setCode(code);
		}
		return result;
	}

}
