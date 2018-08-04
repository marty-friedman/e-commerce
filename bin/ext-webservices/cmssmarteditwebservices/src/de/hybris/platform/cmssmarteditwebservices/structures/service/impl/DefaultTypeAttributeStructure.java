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
package de.hybris.platform.cmssmarteditwebservices.structures.service.impl;

import de.hybris.platform.cms2.common.annotations.HybrisDeprecation;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeAttributeStructure;
import de.hybris.platform.cmsfacades.types.service.impl.DefaultComponentTypeAttributeStructure;
import de.hybris.platform.cmssmarteditwebservices.data.StructureTypeMode;
import de.hybris.platform.cmssmarteditwebservices.structures.service.TypeAttributeStructure;


/**
 * Default implementation of <code>StructureAttributeType</code>. This is a simple POJO implementation.
 * @deprecated since version 6.5
 */
@Deprecated
@HybrisDeprecation(sinceVersion = "6.5")
public class DefaultTypeAttributeStructure extends DefaultComponentTypeAttributeStructure implements TypeAttributeStructure
{
	private StructureTypeMode mode = StructureTypeMode.BASE;

	public DefaultTypeAttributeStructure()
	{
		super();
	}

	public DefaultTypeAttributeStructure(final ComponentTypeAttributeStructure type)
	{
		super(type);
	}

	public DefaultTypeAttributeStructure(final ComponentTypeAttributeStructure type, final StructureTypeMode mode)
	{
		super(type);
		this.mode = mode;
	}

	@Override
	public StructureTypeMode getMode()
	{
		return mode;
	}

	@Override
	public void setMode(final StructureTypeMode mode)
	{
		this.mode = mode;
	}
}
