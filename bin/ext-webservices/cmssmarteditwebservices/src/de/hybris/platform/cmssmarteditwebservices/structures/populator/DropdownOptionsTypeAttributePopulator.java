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
import de.hybris.platform.cmsfacades.data.OptionData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;

/**
 * This populator will populate a dropdown with default options.
 * @deprecated since version 6.5
 */
@Deprecated
@HybrisDeprecation(sinceVersion = "6.5")
public class DropdownOptionsTypeAttributePopulator implements Populator<AttributeDescriptorModel, ComponentTypeAttributeData>
{
	private List<String> optionsList;
	private String qualifier;

	@Override
	public void populate(final AttributeDescriptorModel source, final ComponentTypeAttributeData target) throws ConversionException{
		final List<OptionData> options = getOptionsList().stream()
				.map( optionId -> createOptionData(optionId))
				.collect(Collectors.toList());
		target.setOptions(options);
	}

	protected OptionData createOptionData( final String optionId ){
		final OptionData option = new OptionData();
		option.setId(optionId);
		option.setLabel(("se.cms." + getQualifier() + ".option." + optionId).toLowerCase());

		return option;
	}

	protected List<String> getOptionsList()
	{
		return optionsList;
	}

	@Required
	public void setOptionsList(final List<String> optionsList)
	{
		this.optionsList = optionsList;
	}

	protected String getQualifier(){
		return qualifier;
	}

	@Required
	public void setQualifier( final String qualifier ){
		this.qualifier = qualifier;
	}

}
