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
package de.hybris.platform.cmssmarteditwebservices.structures.service;

import de.hybris.platform.cms2.common.annotations.HybrisDeprecation;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeStructure;
import de.hybris.platform.cmssmarteditwebservices.data.StructureTypeMode;

import java.util.Map;
import java.util.Set;


/**
 * Represents meta-information about a <code>ComposedTypeModel</code> and the populators required to convert this
 * information to a <code>ComponentTypeData</code>.
 * @deprecated since version 6.5
 */
@Deprecated
@HybrisDeprecation(sinceVersion = "6.5")
public interface TypeStructure extends ComponentTypeStructure
{

	/**
	 * Get the attributes that should be considered by the ComponentTypeStructure for a given mode.
	 *
	 * @return the attributes or an empty set for a given mode
	 */
	Map<StructureTypeMode, Set<TypeAttributeStructure>> getAttributesByModeMap();
}
