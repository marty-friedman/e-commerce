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

import static de.hybris.platform.cmssmarteditwebservices.constants.CmssmarteditwebservicesConstants.TYPE_CACHE_EXPIRATION;
import static de.hybris.platform.cmssmarteditwebservices.data.StructureTypeMode.DEFAULT;
import static de.hybris.platform.cmssmarteditwebservices.structures.service.impl.DefaultTypeStructureService
		.DEFAULT_EXPIRATION_TIME;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.data.ComponentTypeAttributeData;
import de.hybris.platform.cmsfacades.data.ComponentTypeData;
import de.hybris.platform.cmsfacades.data.StructureTypeCategory;
import de.hybris.platform.cmsfacades.types.ComponentTypeNotFoundException;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeAttributeStructure;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeStructure;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeStructureService;
import de.hybris.platform.cmssmarteditwebservices.data.StructureTypeMode;
import de.hybris.platform.cmssmarteditwebservices.structures.service.ComponentTypeAttributeDataComparatorRegistry;
import de.hybris.platform.cmssmarteditwebservices.structures.service.TypeAttributeStructure;
import de.hybris.platform.cmssmarteditwebservices.structures.service.TypeStructure;
import de.hybris.platform.cmssmarteditwebservices.structures.service.TypeStructureRegistry;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTypeStructureServiceTest
{
	private static final String CATEGORY = "category";
	private static final String CODE = "code";
	private static final String QUALIFIER = "qualifier";

	private static final String ABSTRACT_TYPECODE = "abstractType";
	private static final String ABSTRACT_TYPECODE2 = "abstractType2";
	private static final String ITEM_TYPECODE = "itemType";
	private static final String REGISTRY_TYPECODE = "registryType";
	private static final String ABSTRACT_QUALIFIER = "abs_qualifier";
	private static final String ABSTRACT_QUALIFIER2 = "abs_qualifier2";
	private static final String REGISTRY_QUALIFIER = "reg_qualifier";

	@InjectMocks
	private DefaultTypeStructureService service;

	@Mock
	private ConfigurationService configurationService;
	@Mock
	private TypeStructureRegistry typeStructureRegistry;
	@Mock
	private ComponentTypeStructureService componentTypeStructureService;
	@Mock
	private ComponentTypeAttributeDataComparatorRegistry componentTypeAttributeDataComparatorRegistry;
	@Mock
	private TypeService typeService;
	@Mock
	private Converter<ComponentTypeStructure, ComponentTypeData> componentTypeStructureConverter;

	@Mock
	private ComponentTypeStructure componentTypeStructure;
	@Mock
	private ComponentTypeStructure abstractComponentTypeStructure;
	@Mock
	private ComponentTypeStructure registryComponentTypeStructure;
	@Mock
	private ComposedTypeModel composedType;
	@Mock
	private ComposedTypeModel abstractComposedType;
	@Mock
	private ComposedTypeModel registryComposedType;
	@Mock
	private TypeStructure registryTypeStructure;
	@Mock
	private Configuration configuration;

	@Before
	public void setUp() throws Exception
	{
		when(configuration.getLong(TYPE_CACHE_EXPIRATION, DEFAULT_EXPIRATION_TIME)).thenReturn(360l);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(composedType.getCode()).thenReturn(ITEM_TYPECODE);
		when(composedType.getSuperType()).thenReturn(abstractComposedType);
		when(composedType.getAllSuperTypes()).thenReturn(Lists.newArrayList(abstractComposedType));
		when(abstractComposedType.getCode()).thenReturn(ABSTRACT_TYPECODE);
		when(registryComposedType.getCode()).thenReturn(REGISTRY_TYPECODE);

		when(typeService.getComposedTypeForCode(ITEM_TYPECODE)).thenReturn(composedType);
		when(typeService.getComposedTypeForCode(ABSTRACT_TYPECODE)).thenReturn(abstractComposedType);
		when(typeService.getComposedTypeForCode(REGISTRY_TYPECODE)).thenReturn(registryComposedType);

		final ComponentTypeAttributeStructure baseAttribute = getComponentTypeAttributeStructure(ITEM_TYPECODE, QUALIFIER);
		final ComponentTypeAttributeStructure baseAbstractAttribute = getComponentTypeAttributeStructure(ABSTRACT_TYPECODE, ABSTRACT_QUALIFIER);
		final ComponentTypeAttributeStructure baseAbstractAttribute2 = getComponentTypeAttributeStructure(ABSTRACT_TYPECODE2, ABSTRACT_QUALIFIER2);

		when(componentTypeStructure.getTypecode()).thenReturn(ITEM_TYPECODE);
		when(componentTypeStructure.getAttributes()).thenReturn(Sets.newHashSet(baseAttribute, baseAbstractAttribute, baseAbstractAttribute2));
		when(abstractComponentTypeStructure.getTypecode()).thenReturn(ABSTRACT_TYPECODE);
		when(abstractComponentTypeStructure.getAttributes()).thenReturn(Sets.newHashSet(baseAbstractAttribute));
		when(registryComponentTypeStructure.getTypecode()).thenReturn(REGISTRY_TYPECODE);
		when(registryComponentTypeStructure.getAttributes()).thenReturn(Sets.newHashSet(baseAttribute, baseAbstractAttribute));
		when(registryTypeStructure.getTypecode()).thenReturn(REGISTRY_TYPECODE);
		when(registryTypeStructure.getCategory()).thenReturn(StructureTypeCategory.COMPONENT);
		when(registryTypeStructure.getTypeDataClass()).thenReturn(Object.class);

		when(componentTypeStructureService.getComponentTypeStructure(ITEM_TYPECODE)).thenReturn(componentTypeStructure);
		when(componentTypeStructureService.getComponentTypeStructure(ABSTRACT_TYPECODE)).thenReturn(abstractComponentTypeStructure);
		when(componentTypeStructureService.getComponentTypeStructure(REGISTRY_TYPECODE)).thenReturn(registryComponentTypeStructure);
		when(componentTypeStructureService.getComponentTypeStructures()) //
		.thenReturn(Lists.newArrayList(componentTypeStructure, abstractComponentTypeStructure, registryComponentTypeStructure));

		doAnswer(invocationOnMock -> {
			final TypeStructure typeStructure = (TypeStructure) invocationOnMock.getArguments()[0];
			final ComponentTypeData componentTypeData = new ComponentTypeData();
			componentTypeData.setCode(typeStructure.getTypecode());
			componentTypeData.setAttributes(typeStructure.getAttributes().stream().map(componentTypeAttributeStructure -> {
				final ComponentTypeAttributeData attr = new ComponentTypeAttributeData();
				attr.setQualifier(componentTypeAttributeStructure.getQualifier());
				return attr;
			}).collect(toList()));
			if (Objects.nonNull(typeStructure.getCategory()))
			{
				componentTypeData.setCategory(typeStructure.getCategory().name());
			}
			return componentTypeData;
		}).when(componentTypeStructureConverter).convert(any());

		when(componentTypeAttributeDataComparatorRegistry.getComparatorForTypecode(any(), any())).thenReturn(Optional.empty());

		final TypeAttributeStructure abstractAttribute = new DefaultTypeAttributeStructure();
		abstractAttribute.setMode(DEFAULT);
		abstractAttribute.setQualifier(ABSTRACT_QUALIFIER);
		abstractAttribute.setTypecode(ABSTRACT_TYPECODE);

		final TypeAttributeStructure defaultAttribute = new DefaultTypeAttributeStructure();
		defaultAttribute.setMode(DEFAULT);
		defaultAttribute.setQualifier(QUALIFIER);
		defaultAttribute.setTypecode(ITEM_TYPECODE);

		final TypeAttributeStructure registryAttribute = new DefaultTypeAttributeStructure();
		registryAttribute.setMode(DEFAULT);
		registryAttribute.setQualifier(REGISTRY_QUALIFIER);
		registryAttribute.setTypecode(REGISTRY_TYPECODE);

		when(typeStructureRegistry.getStructureModes(ITEM_TYPECODE)).thenReturn(Sets.newHashSet(DEFAULT));
		when(typeStructureRegistry.getStructureModes(ABSTRACT_TYPECODE)).thenReturn(Sets.newHashSet(DEFAULT));
		when(typeStructureRegistry.getStructureModes(REGISTRY_TYPECODE)).thenReturn(Sets.newHashSet(DEFAULT));
		when(typeStructureRegistry.getTypeStructure(REGISTRY_TYPECODE)).thenReturn(registryTypeStructure);
		when(typeStructureRegistry.getTypeAttributeStructures(ABSTRACT_TYPECODE, DEFAULT)).thenReturn(Sets.newHashSet(abstractAttribute));
		when(typeStructureRegistry.getTypeAttributeStructures(ITEM_TYPECODE, DEFAULT)).thenReturn(Sets.newHashSet(defaultAttribute));
		when(typeStructureRegistry.getTypeAttributeStructures(REGISTRY_TYPECODE, DEFAULT))
		.thenReturn(Sets.newHashSet(registryAttribute));

		service.afterPropertiesSet();

	}

	protected ComponentTypeAttributeStructure getComponentTypeAttributeStructure(final String typeCode, final String qualifier)
	{
		final ComponentTypeAttributeStructure baseAttribute = new DefaultTypeAttributeStructure();
		baseAttribute.setQualifier(qualifier);
		baseAttribute.setTypecode(typeCode);
		baseAttribute.setPopulators(Lists.newArrayList());
		return baseAttribute;
	}

	@Test
	public void shouldFindAbstractTypeAttributes() throws ComponentTypeNotFoundException
	{
		final ComponentTypeData componentTypeData = service.getComponentTypeByCodeAndMode(ITEM_TYPECODE, DEFAULT.name());
		assertThat(componentTypeData.getAttributes(), iterableWithSize(3));
		assertThat(componentTypeData.getAttributes().stream().map(ComponentTypeAttributeData::getQualifier).collect(
				toList()), containsInAnyOrder(QUALIFIER, ABSTRACT_QUALIFIER, ABSTRACT_QUALIFIER2));
	}

	@Test
	public void shouldNotFindAbstractTypeAttributes_noAttributesDefined() throws ComponentTypeNotFoundException
	{
		final ComponentTypeData componentTypeData = service.getComponentTypeByCodeAndMode(ABSTRACT_TYPECODE, DEFAULT.name());
		assertThat(componentTypeData.getAttributes(), iterableWithSize(1));
		assertThat(componentTypeData.getAttributes().stream().map(ComponentTypeAttributeData::getQualifier).collect(
				toList()), containsInAnyOrder(ABSTRACT_QUALIFIER));
	}

	@Test
	public void shouldGetComponentsByCode() throws ComponentTypeNotFoundException
	{
		final List<ComponentTypeData> componentTypes = service.getComponentTypesByCode(ITEM_TYPECODE);
		assertThat(componentTypes, iterableWithSize(StructureTypeMode.values().length - 1));
		assertThat(componentTypes.get(0).getAttributes().stream().map(ComponentTypeAttributeData::getQualifier).collect(toList()),
				containsInAnyOrder(QUALIFIER, ABSTRACT_QUALIFIER, ABSTRACT_QUALIFIER2));
	}

	@Test
	public void shouldGetRegistryStructureByCode() throws ComponentTypeNotFoundException
	{
		final List<ComponentTypeData> componentTypes = service.getComponentTypesByCode(REGISTRY_TYPECODE);

		assertThat(componentTypes, iterableWithSize(StructureTypeMode.values().length - 1));
		assertThat(componentTypes, hasItem(allOf(hasProperty(CODE, equalTo(REGISTRY_TYPECODE)),
				hasProperty(CATEGORY, equalTo(StructureTypeCategory.COMPONENT.name())))));
	}

}
