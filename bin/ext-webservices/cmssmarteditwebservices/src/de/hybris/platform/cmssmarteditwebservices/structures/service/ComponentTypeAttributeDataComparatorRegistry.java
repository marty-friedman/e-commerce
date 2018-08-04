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
import de.hybris.platform.cmssmarteditwebservices.structures.comparator.ComponentTypeAttributeDataComparator;

import java.util.Optional;
import java.util.Set;


/**
 * Registry that stores a collection of <code>ComponentTypeAttributeDataComparator</code> elements.
 * @deprecated since version 6.5
 */
@Deprecated
@HybrisDeprecation(sinceVersion = "6.5")
public interface ComponentTypeAttributeDataComparatorRegistry
{
	/**
	 * Gets the type attribute comparator given a type code and structure type mode.
	 *
	 * @param typecode
	 *           - the type code
	 * @param mode
	 *           - the structure type mode
	 * @return the matching type attribute comparator
	 */
	Optional<ComponentTypeAttributeDataComparator> getComparatorForTypecode(String typecode, StructureTypeMode mode);

	/**
	 * Gets all type attribute comparators
	 *
	 * @return all comparators
	 */
	Set<ComponentTypeAttributeDataComparator> getComparators();
}
