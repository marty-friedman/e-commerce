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
import de.hybris.platform.cmssmarteditwebservices.data.StructureTypeMode;
import de.hybris.platform.cmssmarteditwebservices.structures.comparator.ComponentTypeAttributeDataComparator;
import de.hybris.platform.cmssmarteditwebservices.structures.service.ComponentTypeAttributeDataComparatorRegistry;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import jersey.repackaged.com.google.common.collect.Lists;


/**
 * Default implementation of the <code>ComponentTypeAttributeDataComparatorRegistry</code>. This implementation uses
 * autowire-by-type to inject all beans implementing {@link ComponentTypeAttributeDataComparator}.
 * @deprecated since version 6.5
 */
@Deprecated
@HybrisDeprecation(sinceVersion = "6.5")
public class DefaultComponentTypeAttributeDataComparatorRegistry
implements ComponentTypeAttributeDataComparatorRegistry, InitializingBean
{
	@Autowired(required = false)
	private Set<ComponentTypeAttributeDataComparator> allComparators;

	private final Map<String, List<ComponentTypeAttributeDataComparator>> comparatorByCodeMap = new HashMap<>();

	@Override
	public Optional<ComponentTypeAttributeDataComparator> getComparatorForTypecode(final String typecode,
			final StructureTypeMode mode)
	{
		return getComparatorsForTypecode(typecode).stream().filter(comparator -> comparator.getMode().equals(mode)).findFirst();
	}

	/**
	 * Gets the list of comparators defined for a given type code.
	 *
	 * @param typecode
	 *           - the type code
	 * @return all comparators for a given type code
	 */
	protected List<ComponentTypeAttributeDataComparator> getComparatorsForTypecode(final String typecode)
	{
		return Optional.ofNullable(getComparatorByCodeMap().get(typecode)).orElse(Collections.emptyList());
	}

	@Override
	public Set<ComponentTypeAttributeDataComparator> getComparators()
	{
		return allComparators;
	}

	@Override
	public void afterPropertiesSet() throws Exception
	{
		if (Objects.isNull(getComparators()))
		{
			setComparators(new HashSet<>());
		}
		final BinaryOperator<List<ComponentTypeAttributeDataComparator>> mergeComparators = (source, target) -> {
			final List<ComponentTypeAttributeDataComparator> mergedList = Lists.newArrayList(source);
			mergedList.addAll(target);
			return mergedList;
		};

		getComparatorByCodeMap().putAll(getComparators().stream().collect(Collectors
				.toMap(comparator -> comparator.getTypecode(), comparator -> Lists.newArrayList(comparator), mergeComparators)));
	}

	public void setComparators(final Set<ComponentTypeAttributeDataComparator> allComparators)
	{
		this.allComparators = allComparators;
	}

	protected Map<String, List<ComponentTypeAttributeDataComparator>> getComparatorByCodeMap()
	{
		return comparatorByCodeMap;
	}

}
