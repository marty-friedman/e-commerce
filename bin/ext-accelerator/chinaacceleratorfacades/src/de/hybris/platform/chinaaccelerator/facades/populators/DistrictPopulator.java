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
package de.hybris.platform.chinaaccelerator.facades.populators;



import de.hybris.platform.chinaaccelerator.facades.data.DistrictData;
import de.hybris.platform.chinaaccelerator.services.model.location.DistrictModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.springframework.util.Assert;


/**
 *
 */
public class DistrictPopulator implements Populator<DistrictModel, DistrictData>
{
	@Override
	public void populate(final DistrictModel source, final DistrictData target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setName(source.getName());
		target.setCode(source.getCode());
	}
}
