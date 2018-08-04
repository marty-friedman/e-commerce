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
package de.hybris.platform.cmssmarteditwebservices.structures.populator;

import static junit.framework.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.data.ComponentTypeAttributeData;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DropdownOptionsTypeAttributePopulatorTest {

	@InjectMocks
	private DropdownOptionsTypeAttributePopulator dropdownOptionsTypeAttributePopulator;

	@Mock
	private AttributeDescriptorModel attributeDescriptorModel;

	private ComponentTypeAttributeData target;

	private final String TYPE_QUALIFIER = "stringQualifier";
	private final List<String> OPTIONS_LIST = Arrays.asList( "Option1", "Option2" );

	@Before
	public void setUp()
	{
		dropdownOptionsTypeAttributePopulator.setQualifier( TYPE_QUALIFIER );
		dropdownOptionsTypeAttributePopulator.setOptionsList(OPTIONS_LIST);

		target = new ComponentTypeAttributeData();
	}

	@Test
	public void shouldPopulateOptions()
	{
		dropdownOptionsTypeAttributePopulator.populate( attributeDescriptorModel, target );

		assertEquals(target.getOptions().size(), 2);
		assertEquals(target.getOptions().get(0).getLabel(), ("se.cms." + TYPE_QUALIFIER + ".option." + OPTIONS_LIST.get(0)).toLowerCase());
		assertEquals(target.getOptions().get(1).getLabel(), ("se.cms." + TYPE_QUALIFIER + ".option." + OPTIONS_LIST.get(1)).toLowerCase());
		assertEquals(target.getOptions().get(0).getId(), OPTIONS_LIST.get(0));
		assertEquals(target.getOptions().get(1).getId(), OPTIONS_LIST.get(1));

	}
}