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
import de.hybris.platform.cmssmarteditwebservices.data.StructureTypeMode;

import java.util.Collection;
import java.util.Set;


/**
 * Registry that stores a collection of <code>StructureType</code> elements.
 * @deprecated since version 6.5
 */
@Deprecated
@HybrisDeprecation(sinceVersion = "6.5")
public interface TypeStructureRegistry
{

	/**
	 * Get a specific <code>StructureType</code> by its typecode.
	 *
	 * @param typecode
	 *           - the typecode of the element to retrieve from the registry.
	 * @return the element matching the typecode
	 */
	TypeStructure getTypeStructure(String typecode);

	/**
	 * Get all structure type modes defined for a given type code.
	 *
	 * @param typecode
	 *           - the type code
	 * @return all structure type modes
	 */
	Set<StructureTypeMode> getStructureModes(String typecode);

	/**
	 * Get all type attribute structures defined for a given typeCode and mode
	 * @param typeCode the type code specified on each attribute
	 * @param mode the mode in which the attribute was defined.
	 * @return a set of {@link TypeAttributeStructure}
	 */
	Set<TypeAttributeStructure> getTypeAttributeStructures(final String typeCode, final StructureTypeMode mode);
}
