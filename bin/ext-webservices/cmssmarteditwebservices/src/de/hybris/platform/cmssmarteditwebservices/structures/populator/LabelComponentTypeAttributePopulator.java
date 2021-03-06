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
package de.hybris.platform.cmssmarteditwebservices.structures.populator;

import de.hybris.platform.cms2.common.annotations.HybrisDeprecation;
import de.hybris.platform.cmsfacades.data.ComponentTypeAttributeData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Populator that populates the labelAttributes field of the {@link ComponentTypeAttributeData} POJO.
 * @deprecated since version 6.5
 */
@Deprecated
@HybrisDeprecation(sinceVersion = "6.5")
public class LabelComponentTypeAttributePopulator implements Populator<AttributeDescriptorModel, ComponentTypeAttributeData>
{
	private List<String> labelAttributes;

	@Override
	public void populate(final AttributeDescriptorModel source, final ComponentTypeAttributeData target) throws ConversionException
	{
		target.setLabelAttributes(getLabelAttributes());
	}

	protected List<String> getLabelAttributes()
	{
		return labelAttributes;
	}

	@Required
	public void setLabelAttributes(final List<String> labelAttributes)
	{
		this.labelAttributes = labelAttributes;
	}
}
