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
package de.hybris.platform.emsclient.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.entitlementservices.data.EmsGrantData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import com.hybris.services.entitlements.condition.ConditionData;

import org.apache.commons.lang.StringUtils;


/**
 * Converter implementation for {@link de.hybris.platform.entitlementfacades.data.EntitlementData} as source and
 * {@link com.hybris.services.entitlements.condition.ConditionData} as target type.
 */
public class EmsGrantDataPathConditionPopulator implements Populator<EmsGrantData, ConditionData>
{
	private static final String PATH_TYPE = "path";
	private static final String GRANT_PARAMETER_PATH = "path";

	@Override
	public void populate(final EmsGrantData source, final ConditionData target) throws ConversionException
	{
		ServicesUtil.validateParameterNotNullStandardMessage("source", source);
		ServicesUtil.validateParameterNotNullStandardMessage("target", target);

		final String conditionPath = source.getConditionPath();
		if (!StringUtils.isBlank(conditionPath))
		{
			target.setType(PATH_TYPE);
			target.setProperty(GRANT_PARAMETER_PATH, conditionPath);
		}
	}
}