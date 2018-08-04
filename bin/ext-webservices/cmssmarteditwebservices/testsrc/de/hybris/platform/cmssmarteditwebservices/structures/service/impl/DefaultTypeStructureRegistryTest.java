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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.data.ComponentTypeAttributeData;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeAttributeStructure;
import de.hybris.platform.cmssmarteditwebservices.data.StructureTypeMode;
import de.hybris.platform.cmssmarteditwebservices.structures.service.TypeAttributeStructure;
import de.hybris.platform.cmssmarteditwebservices.structures.service.TypeStructure;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static de.hybris.platform.cmssmarteditwebservices.data.StructureTypeMode.ADD;
import static de.hybris.platform.cmssmarteditwebservices.data.StructureTypeMode.DEFAULT;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTypeStructureRegistryTest
{

	private static final String TYPE_CODE = "TYPE_CODE";
	private static final String QUALIFIER_1 = "qualifier-1";
	private static final String QUALIFIER_2 = "qualifier-2";

	private final Set<TypeAttributeStructure> allTypeAttributeStructures = new HashSet<>();
	private final Set<TypeStructure> allTypeStructures = new HashSet<>();

	private final DefaultTypeStructureRegistry registry = new DefaultTypeStructureRegistry();

	@Mock
	private ComponentTypeAttributeStructure baseComponentTypeStructureAttributePrototype;
	@Mock
	private TypeAttributeStructure attr1;
	@Mock
	private TypeAttributeStructure attr2;


	@Mock
	private Populator<AttributeDescriptorModel, ComponentTypeAttributeData> basePopulator1;
	@Mock
	private Populator<AttributeDescriptorModel, ComponentTypeAttributeData> basePopulator2;
	@Mock
	private Populator<AttributeDescriptorModel, ComponentTypeAttributeData> populator3;
	@Mock
	private Populator<AttributeDescriptorModel, ComponentTypeAttributeData> populator4;
	@Mock
	private Populator<AttributeDescriptorModel, ComponentTypeAttributeData> populator5;

	@Before
	public void setup() throws Exception
	{
		when(attr1.getPopulators()).thenReturn(asList(populator3, populator4));
		when(attr2.getPopulators()).thenReturn(asList(populator5));

		when(baseComponentTypeStructureAttributePrototype.getPopulators()).thenReturn(asList(basePopulator1, basePopulator2));
		when(attr1.getTypecode()).thenReturn(TYPE_CODE);
		when(attr1.getQualifier()).thenReturn(QUALIFIER_1);
		when(attr1.getMode()).thenReturn(DEFAULT);

		when(attr2.getTypecode()).thenReturn(TYPE_CODE);
		when(attr2.getQualifier()).thenReturn(QUALIFIER_2);
		when(attr2.getMode()).thenReturn(ADD);

		allTypeAttributeStructures.add(attr1);
		allTypeAttributeStructures.add(attr2);

		registry.setAllTypeStructures(allTypeStructures);
		registry.setAllTypeAttributeStructures(allTypeAttributeStructures);
		registry.setBaseComponentTypeStructureAttributePrototype(baseComponentTypeStructureAttributePrototype);
		registry.afterPropertiesSet();
	}

	@Test
	public void testGetStructureModesContainsAllModesDefinedOnAttributesForType()
	{
		final Set<StructureTypeMode> structureModes = registry.getStructureModes(TYPE_CODE);
		assertThat(structureModes, iterableWithSize(2));
		assertThat(structureModes, containsInAnyOrder(DEFAULT, ADD));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetAttributeForAGivenModeShouldReturnOnlyTheAttributeDefinedOnThatSpecificMode()
	{
		final Set<TypeAttributeStructure> attributes = registry.getTypeAttributeStructures(TYPE_CODE, DEFAULT);
		assertThat(attributes, iterableWithSize(1));
		assertThat(attributes.stream().map(TypeAttributeStructure::getQualifier).collect(toList()),
				containsInAnyOrder(QUALIFIER_1));

		verify(attr1, times(1)).setPopulators(any(List.class));
		verify(attr1, times(1)).setPopulators(asList(basePopulator1, basePopulator2, populator3, populator4));
		verify(attr2, times(1)).setPopulators(any(List.class));
		verify(attr2, times(1)).setPopulators(asList(basePopulator1, basePopulator2, populator5));
	}
}
