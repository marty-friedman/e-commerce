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

import static de.hybris.platform.cmssmarteditwebservices.data.StructureTypeMode.ADD;
import static de.hybris.platform.cmssmarteditwebservices.data.StructureTypeMode.DEFAULT;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmssmarteditwebservices.structures.comparator.ComponentTypeAttributeDataComparator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class DefaultComponentTypeAttributeDataComparatorRegistryTest
{
	private static final String INVALID = "invalid";
	private static final String TEST_TYPE_CODE = "testTypeCode";
	private static final String TEST_TYPE_CODE2 = "testTypeCode2";

	private final Set<ComponentTypeAttributeDataComparator> allComparators = new HashSet<>();
	private final DefaultComponentTypeAttributeDataComparatorRegistry registry = new DefaultComponentTypeAttributeDataComparatorRegistry();

	private ComponentTypeAttributeDataComparator comparator1;
	private ComponentTypeAttributeDataComparator comparator2;

	@Before
	public void setUp() throws Exception
	{
		comparator1 = new ComponentTypeAttributeDataComparator();
		comparator1.setTypecode(TEST_TYPE_CODE);
		comparator1.setMode(ADD);
		comparator2 = new ComponentTypeAttributeDataComparator();
		comparator2.setTypecode(TEST_TYPE_CODE2);
		comparator2.setMode(DEFAULT);

		allComparators.addAll(Arrays.asList(comparator1, comparator2));
		registry.setComparators(allComparators);
		registry.afterPropertiesSet();
	}

	@Test
	public void shouldPopulateComparatorsAfterPropertiesSet()
	{
		final Set<ComponentTypeAttributeDataComparator> comparators = registry.getComparators();

		assertThat(comparators, hasSize(2));
		assertThat(comparators, hasItems(comparator1, comparator2));
	}

	@Test
	public void shouldFindComparator()
	{
		final Optional<ComponentTypeAttributeDataComparator> comparator = registry.getComparatorForTypecode(TEST_TYPE_CODE, ADD);

		assertThat(comparator.isPresent(), equalTo(true));
	}

	@Test
	public void shouldNotFindComparator()
	{
		final Optional<ComponentTypeAttributeDataComparator> comparator = registry.getComparatorForTypecode(INVALID, DEFAULT);

		assertThat(comparator.isPresent(), equalTo(false));
	}
}
