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
import de.hybris.platform.cmsfacades.types.service.ComponentTypeAttributeStructure;
import de.hybris.platform.cmssmarteditwebservices.data.StructureTypeMode;


/**
 * Represents meta-information about an <code>AttributeDescriptorModel</code> and the populators required to convert
 * this information to a <code>ComponentTypeAttributeData</code>.
 * @deprecated since version 6.5
 */
@Deprecated
@HybrisDeprecation(sinceVersion = "6.5")
public interface TypeAttributeStructure extends ComponentTypeAttributeStructure
{
	/**
	 * Get the mode identifying the <code>AttributeDescriptorModel</code>.
	 *
	 * @return the mode
	 */
	StructureTypeMode getMode();

	/**
	 * Set the mode identifying the <code>AttributeDescriptorModel</code>.
	 *
	 * @param mode
	 *           - the mode
	 */
	void setMode(StructureTypeMode mode);
}
