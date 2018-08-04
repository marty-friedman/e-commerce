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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.data.ComponentTypeAttributeData;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class ComponentTypeAttributeDataComparatorTest
{
	private final ComponentTypeAttributeDataComparator comparator = new ComponentTypeAttributeDataComparator();

	private ComponentTypeAttributeData source;
	private ComponentTypeAttributeData target;
	private final List<String> orderedValues = Arrays.asList("have", "a", "great", "day");

	@Before
	public void setUp() throws Exception
	{
		target = new ComponentTypeAttributeData();
		source = new ComponentTypeAttributeData();
		comparator.setOrderedAttributes(orderedValues);
		comparator.afterPropertiesSet();
	}

	@Test
	public void shouldComeBefore()
	{
		source.setQualifier("have");
		target.setQualifier("great");

		final int result = comparator.compare(source, target);

		assertThat(result, is(-1));
	}

	@Test
	public void shouldGoAfter()
	{
		source.setQualifier("day");
		target.setQualifier("great");

		final int result = comparator.compare(source, target);

		assertThat(result, is(1));
	}

	@Test
	public void shouldBeEqual()
	{
		source.setQualifier("day");
		target.setQualifier("day");

		final int result = comparator.compare(source, target);

		assertThat(result, is(0));
	}

}
