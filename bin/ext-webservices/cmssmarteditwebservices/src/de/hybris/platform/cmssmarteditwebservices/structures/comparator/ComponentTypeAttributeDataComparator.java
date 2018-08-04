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
package de.hybris.platform.cmssmarteditwebservices.structures.comparator;

import de.hybris.platform.cms2.common.annotations.HybrisDeprecation;
import de.hybris.platform.cmsfacades.data.ComponentTypeAttributeData;
import de.hybris.platform.cmssmarteditwebservices.data.StructureTypeMode;

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;


/**
 * Comparator used to sort {@link ComponentTypeAttributeData} by comparing their qualifiers to a predefined list of
 * attributes which determines the ordering.
 * @deprecated since version 6.5
 */
@Deprecated
@HybrisDeprecation(sinceVersion = "6.5")
public class ComponentTypeAttributeDataComparator implements Comparator<ComponentTypeAttributeData>, InitializingBean
{
	private StructureTypeMode mode;
	private String typecode;
	private List<String> orderedAttributes;
	private Map<String, Integer> orderedAttributesMap;

	@Override
	public int compare(final ComponentTypeAttributeData source, final ComponentTypeAttributeData target)
	{
		final Integer sourceOrder = getAttributeOrder(source);
		final Integer targetOrder = getAttributeOrder(target);
		return sourceOrder.compareTo(targetOrder);
	}

	/**
	 * If there is no order for a given attribute, then its position number is {@code Integer.MAX_VALUE}
	 * @param attribute the attribute to calculate the position
	 * @return the position of the attribute in the final list
	 */
	protected Integer getAttributeOrder(final ComponentTypeAttributeData attribute)
	{
		return orderedAttributesMap.get(attribute.getQualifier()) == null ? Integer.MAX_VALUE : orderedAttributesMap.get(attribute.getQualifier());
	}

	public List<String> getOrderedAttributes()
	{
		return orderedAttributes;
	}

	@Required
	public void setOrderedAttributes(final List<String> orderedAttributes)
	{
		this.orderedAttributes = orderedAttributes;
	}

	/**
	 * Convert the <code>orderAttributes</code> list to a map where the key is the attribute and the value is the
	 * attribute's position in the list.
	 * <p>
	 * Suppress sonar warning (squid:S2095 | Resources should be closed ) : IntStream.range() does not hold a resource.
	 */
	@SuppressWarnings("squid:S2095")
	@Override
	public void afterPropertiesSet() throws Exception
	{
		orderedAttributesMap = IntStream.range(0, orderedAttributes.size())
				.mapToObj(index -> new AbstractMap.SimpleEntry<>(orderedAttributes.get(index), index))
				.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
	}

	public String getTypecode()
	{
		return typecode;
	}

	@Required
	public void setTypecode(final String typecode)
	{
		this.typecode = typecode;
	}

	public StructureTypeMode getMode()
	{
		return mode;
	}

	@Required
	public void setMode(final StructureTypeMode mode)
	{
		this.mode = mode;
	}
	
}
