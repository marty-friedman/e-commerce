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
package de.hybris.platform.cmssmarteditwebservices.structures.facade.impl;

import de.hybris.platform.cms2.common.annotations.HybrisDeprecation;
import de.hybris.platform.cmsfacades.data.ComponentTypeData;
import de.hybris.platform.cmssmarteditwebservices.structures.comparator.ComponentTypeAttributeDataComparator;
import de.hybris.platform.cmssmarteditwebservices.structures.facade.StructureFacade;
import de.hybris.platform.cmssmarteditwebservices.structures.service.TypeStructureService;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

/**
 * Default SmartEdit implementation of {@link StructureFacade} by providing field ordering, marking a field as
 * editable or not, and listing only the fields needed for a given mode.
 * @deprecated since version 6.5
 */
@Deprecated
@HybrisDeprecation(sinceVersion = "6.5")
public class DefaultStructureFacade implements StructureFacade
{

	private TypeStructureService typeStructureService;

	@Override
	public List<ComponentTypeData> getComponentTypesByCode(final String code)
	{
		return getTypeStructureService().getComponentTypesByCode(code);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The type attributes are ordered in the order specified by the associated {@link ComponentTypeAttributeDataComparator}.
	 */
	@Override
	public ComponentTypeData getComponentTypeByCodeAndMode(final String code, final String mode)
	{
		return getTypeStructureService().getComponentTypeByCodeAndMode(code, mode);
	}

	protected TypeStructureService getTypeStructureService()
	{
		return typeStructureService;
	}

	@Required
	public void setTypeStructureService(final TypeStructureService typeStructureService)
	{
		this.typeStructureService = typeStructureService;
	}

}
