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
package de.hybris.platform.entitlementservices.entitlement.converters.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.entitlementservices.data.EmsGrantData;
import de.hybris.platform.entitlementservices.model.ProductEntitlementModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.util.ServicesUtil;

/**
 * Populate DTO {@link EmsGrantData} with data from {@link ProductEntitlementModel}
 */
public class ProductEntitlementEmsGrantPopulator<SOURCE extends ProductEntitlementModel, TARGET extends EmsGrantData>
		implements Populator<SOURCE, TARGET>
{
	@Override
	public void populate(final SOURCE source, final TARGET target) throws ConversionException
	{
		ServicesUtil.validateParameterNotNullStandardMessage("source", source);
		ServicesUtil.validateParameterNotNullStandardMessage("target", target);

		if (source.getEntitlement() != null) {
			target.setEntitlementType(source.getEntitlement().getId());
		}
		target.setConditionString(source.getConditionString());
		target.setConditionGeo(source.getConditionGeo());
		target.setConditionPath(source.getConditionPath());
		target.setTimeUnitStart(source.getTimeUnitStart());
		target.setTimeUnitDuration(source.getTimeUnitDuration());
		target.setProductEntitlementId(source.getId());
		target.setTimeUnit(source.getTimeUnit());
		target.setMaxQuantity(source.getQuantity());
	}
}
