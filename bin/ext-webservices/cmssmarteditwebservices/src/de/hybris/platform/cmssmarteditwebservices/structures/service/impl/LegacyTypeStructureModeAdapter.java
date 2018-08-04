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

import static java.util.stream.Collectors.toSet;

import de.hybris.platform.cmsfacades.data.ComponentTypeAttributeData;
import de.hybris.platform.cmsfacades.data.StructureTypeMode;
import de.hybris.platform.cmsfacades.types.service.AttributeModePopulatorsProvider;
import de.hybris.platform.cmsfacades.types.service.StructureTypeModeAttributePopulators;
import de.hybris.platform.cmsfacades.types.service.StructureTypeModeAttributeFilter;
import de.hybris.platform.cmsfacades.types.service.StructureTypeModeAttributeFilterProvider;
import de.hybris.platform.cmsfacades.types.service.impl.DefaultStructureTypeModeAttributeFilter;
import de.hybris.platform.cmssmarteditwebservices.structures.comparator.ComponentTypeAttributeDataComparator;
import de.hybris.platform.cmssmarteditwebservices.structures.service.TypeAttributeStructure;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;


/**
 * Implementation of the initializing Bean that autowires the {@link TypeAttributeStructure} and adapt them to be as part of the {@link AttributeModePopulatorsProvider}.
 * It converts the existing type x attributes x mode defined in the registry into an instance of the {@link StructureTypeModeAttributeFilter} class. 
 * Also, it will collect the the list of ordered attributes and adapt to the same {@link StructureTypeModeAttributeFilter} structure.
 */
public class LegacyTypeStructureModeAdapter implements InitializingBean
{
	@Autowired(required = false)
	private Set<TypeAttributeStructure> allTypeAttributeStructures;

	@Autowired(required = false)
	private Set<ComponentTypeAttributeDataComparator> allComparators;
	
	private AttributeModePopulatorsProvider attributeModePopulatorsProvider;
	
	private StructureTypeModeAttributeFilterProvider structureTypeModeAttributeFilterProvider;
	
	private TypeService typeService;

	@Override
	public void afterPropertiesSet() throws Exception
	{

		if (Objects.isNull(getAllTypeAttributeStructures()))
		{
			setAllTypeAttributeStructures(new HashSet<>());
		}
		if (Objects.isNull(getAllComparators()))
		{
			setAllComparators(new HashSet<>());
		}
		
		final Map<String, Map<String, Set<String>>> fullTypeModeAttributesMap = getTypeAttributesMap();
		final Map<String, Map<String, StructureTypeModeAttributeFilter>> registryTypeModeAttributeMap = getStructureTypeModeFromRegistry();

		// first step is to create a new mode, excluding all attributes for a given type and mode. 
		// that way we will make sure that the solution will work the same way as 6.4
		fullTypeModeAttributesMap.entrySet().stream().forEach(entry -> {
			final String typeCode = entry.getKey();
			entry.getValue().entrySet().forEach(modeAttribute -> 
					{
						final String mode = modeAttribute.getKey();
						final Set<String> excludes = modeAttribute.getValue();
						getStructureTypeModeAttributeFilterProvider().addStructureTypeModeAttributeFilter(
								newStructureTypeMode(
										(thisTypeCode, thisMode) -> Objects.equals(thisTypeCode, typeCode) && Objects.equals(thisMode.name(), mode),
										null,
										new ArrayList<>(excludes),
										null)
						);
					}
			);
		});
		
		// second step is to add yet another configuration per type and mode, but now including the attributes that were defined before. 
		// this will override the previous exclusion
		registryTypeModeAttributeMap.entrySet().stream().forEach(entry -> {
			final String typeCode = entry.getKey();
			entry.getValue().entrySet().forEach(modeAttribute -> 
					{
						final String mode = modeAttribute.getKey();
						final DefaultStructureTypeModeAttributeFilter structureTypeModeData = (DefaultStructureTypeModeAttributeFilter) modeAttribute.getValue();
						structureTypeModeData.setConstrainedBy((thisTypeCode, thisMode) -> Objects.equals(thisTypeCode, typeCode) && Objects.equals(thisMode.name(), mode));
						getStructureTypeModeAttributeFilterProvider().addStructureTypeModeAttributeFilter(structureTypeModeData);
					}
			);
		});
		

		getAllTypeAttributeStructures().stream().forEach(typeAttributeStructure -> getAttributeModePopulatorsProvider().addStructureTypeModeAttributePopulators(
						// contrainedBy
						(attribute, mode) -> Objects.equals(typeAttributeStructure.getTypecode(), attribute.getEnclosingType().getCode()) 
												&& Objects.equals(typeAttributeStructure.getQualifier(), attribute.getQualifier()) && Objects.equals(typeAttributeStructure.getMode().name(), mode.name()),
						// attributePopulators
						typeAttributeStructure.getPopulators()
		));
	}

	
	protected StructureTypeModeAttributeFilter newStructureTypeMode(final BiPredicate<String, StructureTypeMode> constrainedBy, 
			final List<String> includes, final List<String> excludes, final List<String> order)
	{
		final DefaultStructureTypeModeAttributeFilter structureTypeModeData = new DefaultStructureTypeModeAttributeFilter();
		structureTypeModeData.setConstrainedBy(constrainedBy);
		if (Objects.nonNull(includes)) {
			structureTypeModeData.setIncludes(includes);
		}
		if (Objects.nonNull(excludes)) {
			structureTypeModeData.setExcludes(excludes);
		}
		if (Objects.nonNull(order)) {
			structureTypeModeData.setOrder(order);
		}
		return structureTypeModeData;
	}

	/**
	 * Returns the attribute's qualifier that are defined in the registry
	 * @return a Map with the attributes by TypeCode
	 */
	protected Map<String, Map<String, StructureTypeModeAttributeFilter>> getStructureTypeModeFromRegistry()
	{
		final Map<String, Map<String, StructureTypeModeAttributeFilter>> inclusionMap = new HashMap<>();

		// add the attributes to be included
		getAllTypeAttributeStructures().stream().forEach(typeAttributeStructure ->
		{
			final Map<String, StructureTypeModeAttributeFilter> modes = inclusionMap.computeIfAbsent(typeAttributeStructure.getTypecode(), s -> new HashMap<>());
			final StructureTypeModeAttributeFilter structureTypeModeAttributeFilter = modes
					.computeIfAbsent(typeAttributeStructure.getMode().name(), s -> newStructureTypeMode(null, null, null, null));
			structureTypeModeAttributeFilter.getIncludes().add(typeAttributeStructure.getQualifier());
		});

		// updates the map with the attribute order
		getAllComparators().stream().forEach(typeComparator -> {
			final Map<String, StructureTypeModeAttributeFilter> modes = inclusionMap.computeIfAbsent(typeComparator.getTypecode(), s -> new HashMap<>());
			final StructureTypeModeAttributeFilter structureTypeModeAttributeFilter = modes
					.computeIfAbsent(typeComparator.getMode().name(), s -> newStructureTypeMode(null, null, null, null));
			structureTypeModeAttributeFilter.getOrder().addAll(typeComparator.getOrderedAttributes());
		});
		return inclusionMap;
	}

	/**
	 * Returns all the attributes defined in the registry per typeCode and mode. 
	 * @return a Map with the attribute's qualifier per typeCode and mode. 
	 */
	protected Map<String, Map<String, Set<String>>> getTypeAttributesMap()
	{
		final Map<String, Map<String, Set<String>>> exclusionMap = new HashMap<>();
		
		getAllTypeAttributeStructures().stream().forEach(typeAttributeStructure -> {
			
			final Map<String, Set<String>> modes = exclusionMap
					.computeIfAbsent(typeAttributeStructure.getTypecode(), typeCode -> new HashMap<>());
			
			modes.computeIfAbsent(typeAttributeStructure.getMode().name(), 
					s -> {
						final ComposedTypeModel composedType = getTypeService().getComposedTypeForCode(typeAttributeStructure.getTypecode());
						return Stream.of(composedType.getDeclaredattributedescriptors(), composedType.getInheritedattributedescriptors()) //
								.flatMap(Collection::stream) //
								.map(AttributeDescriptorModel::getQualifier) //
								.collect(toSet());
					});

		});
		return exclusionMap;
	}
	
	
	protected Set<TypeAttributeStructure> getAllTypeAttributeStructures()
	{
		return allTypeAttributeStructures;
	}

	public void setAllTypeAttributeStructures(final Set<TypeAttributeStructure> allTypeAttributeStructures)
	{
		this.allTypeAttributeStructures = allTypeAttributeStructures;
	}

	protected AttributeModePopulatorsProvider getAttributeModePopulatorsProvider()
	{
		return attributeModePopulatorsProvider;
	}

	@Required
	public void setAttributeModePopulatorsProvider(final AttributeModePopulatorsProvider attributeModePopulatorsProvider)
	{
		this.attributeModePopulatorsProvider = attributeModePopulatorsProvider;
	}

	protected StructureTypeModeAttributeFilterProvider getStructureTypeModeAttributeFilterProvider()
	{
		return structureTypeModeAttributeFilterProvider;
	}

	@Required
	public void setStructureTypeModeAttributeFilterProvider(final StructureTypeModeAttributeFilterProvider structureTypeModeAttributeFilterProvider)
	{
		this.structureTypeModeAttributeFilterProvider = structureTypeModeAttributeFilterProvider;
	}

	protected TypeService getTypeService()
	{
		return typeService;
	}

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}

	protected Set<ComponentTypeAttributeDataComparator> getAllComparators()
	{
		return allComparators;
	}

	public void setAllComparators(final Set<ComponentTypeAttributeDataComparator> allComparators)
	{
		this.allComparators = allComparators;
	}
}
