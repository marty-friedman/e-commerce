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
package de.hybris.platform.cmssmarteditwebservices.structures;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static de.hybris.platform.cms2.model.contents.CMSItemModel.NAME;
import static de.hybris.platform.cms2.model.restrictions.CMSTimeRestrictionModel.ACTIVEFROM;
import static de.hybris.platform.cms2.model.restrictions.CMSTimeRestrictionModel.ACTIVEUNTIL;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cms2.model.restrictions.CMSTimeRestrictionModel;
import de.hybris.platform.cmsfacades.data.ComponentTypeAttributeData;
import de.hybris.platform.cmsfacades.data.ComponentTypeData;
import de.hybris.platform.cmsfacades.data.StructureTypeMode;
import de.hybris.platform.cmsfacades.types.ComponentTypeFacade;
import de.hybris.platform.cmsfacades.types.ComponentTypeNotFoundException;
import de.hybris.platform.cmssmarteditwebservices.structures.comparator.ComponentTypeAttributeDataComparator;
import de.hybris.platform.cmssmarteditwebservices.structures.service.TypeAttributeStructure;
import de.hybris.platform.cmssmarteditwebservices.structures.service.impl.LegacyTypeStructureModeAdapter;
import de.hybris.platform.cmssmarteditwebservices.structures.service.impl.DefaultTypeAttributeStructure;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.servicelayer.ServicelayerTest;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

@IntegrationTest
public class ComponentTypeFacadeIntegrationTest extends ServicelayerTest
{
	@Resource
	private ComponentTypeFacade componentTypeFacade;

	@Resource
	private LegacyTypeStructureModeAdapter cmsLegacyTypeStructureModeAdapter;
	
	@Before
	public void setup() throws Exception
	{
		cmsLegacyTypeStructureModeAdapter.setAllComparators(newHashSet(
				getComponentTypeAttributeDataComparator(CMSTimeRestrictionModel._TYPECODE,
						de.hybris.platform.cmssmarteditwebservices.data.StructureTypeMode.DEFAULT,
						newArrayList(ACTIVEFROM, ACTIVEUNTIL, NAME))
		));

		// custom nonEditablePopulator
		final Populator<AttributeDescriptorModel, ComponentTypeAttributeData> nonEditablePopulator = (attributeDescriptorModel, componentTypeAttributeData) -> {
			componentTypeAttributeData.setEditable(false);
		};
		
		cmsLegacyTypeStructureModeAdapter.setAllTypeAttributeStructures(newHashSet(
				getTypeAttributeStructure(CMSTimeRestrictionModel._TYPECODE,
						NAME,
						de.hybris.platform.cmssmarteditwebservices.data.StructureTypeMode.DEFAULT,
						newArrayList(nonEditablePopulator)),
				getTypeAttributeStructure(CMSTimeRestrictionModel._TYPECODE,
						ACTIVEFROM,
						de.hybris.platform.cmssmarteditwebservices.data.StructureTypeMode.DEFAULT,
						newArrayList(nonEditablePopulator)), 
				getTypeAttributeStructure(CMSTimeRestrictionModel._TYPECODE,
						ACTIVEUNTIL,
						de.hybris.platform.cmssmarteditwebservices.data.StructureTypeMode.DEFAULT,
						newArrayList(nonEditablePopulator))
		));

		cmsLegacyTypeStructureModeAdapter.afterPropertiesSet();
	}

	private TypeAttributeStructure getTypeAttributeStructure(final String typeCode, final String qualifier,
			final de.hybris.platform.cmssmarteditwebservices.data.StructureTypeMode mode,
			final List<Populator<AttributeDescriptorModel, ComponentTypeAttributeData>> populators)
	{
		final TypeAttributeStructure timeRestrictionName = new DefaultTypeAttributeStructure();
		timeRestrictionName.setTypecode(typeCode);
		timeRestrictionName.setMode(mode);
		timeRestrictionName.setQualifier(qualifier);
		timeRestrictionName.setPopulators(populators);
		return timeRestrictionName;
	}

	private ComponentTypeAttributeDataComparator getComponentTypeAttributeDataComparator(final String typeCode, final de.hybris.platform.cmssmarteditwebservices.data.StructureTypeMode mode, final List<String> order) throws Exception
	{
		final ComponentTypeAttributeDataComparator comparator = new ComponentTypeAttributeDataComparator();
		comparator.setTypecode(typeCode);
		comparator.setMode(mode);
		comparator.setOrderedAttributes(order);
		comparator.afterPropertiesSet();
		return comparator;
	}

	@Test
	public void testModificationOnCMsTimeRestrictionOnThisExtensionShouldModifyOrder() throws ComponentTypeNotFoundException
	{
		final ComponentTypeData componentTypeData = componentTypeFacade.getComponentTypeByCodeAndMode(
				CMSTimeRestrictionModel._TYPECODE, StructureTypeMode.DEFAULT.name());
		assertThat(componentTypeData.getAttributes().size(), greaterThanOrEqualTo(3));
		final List<String> attributes = componentTypeData.getAttributes().stream().map(ComponentTypeAttributeData::getQualifier)
				.collect(toList());
		// check if order is correct
		assertThat(attributes, hasItems(ACTIVEFROM, ACTIVEUNTIL, NAME));
	}
			
}
