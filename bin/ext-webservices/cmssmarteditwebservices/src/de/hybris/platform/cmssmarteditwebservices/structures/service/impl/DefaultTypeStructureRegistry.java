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
import de.hybris.platform.cmsfacades.data.ComponentTypeAttributeData;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeAttributeStructure;
import de.hybris.platform.cmssmarteditwebservices.data.StructureTypeMode;
import de.hybris.platform.cmssmarteditwebservices.structures.service.TypeAttributeStructure;
import de.hybris.platform.cmssmarteditwebservices.structures.service.TypeStructure;
import de.hybris.platform.cmssmarteditwebservices.structures.service.TypeStructureRegistry;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.collect.Sets;

import static com.google.common.collect.Lists.newArrayList;


/**
 * Default implementation of the <code>TypeStructureRegistry</code>. This implementation uses autowire-by-type to inject
 * all beans implementing {@link TypeAttributeStructure}.
 * @deprecated since version 6.5
 */
@Deprecated
@HybrisDeprecation(sinceVersion = "6.5")
public class DefaultTypeStructureRegistry implements TypeStructureRegistry, InitializingBean
{
	@Autowired(required = false)
	private Set<TypeAttributeStructure> allTypeAttributeStructures;

	@Autowired(required = false)
	private Set<TypeStructure> allTypeStructures;

	private ComponentTypeAttributeStructure baseComponentTypeStructureAttributePrototype;

	private final Map<String, TypeStructure> structureTypeMap = new HashMap<>();

	private final Map<String, Map<StructureTypeMode, Set<TypeAttributeStructure>>> structureTypeAttributeMap = new HashMap<>();

	@Override
	public TypeStructure getTypeStructure(final String typecode)
	{
		return getStructureTypeMap().get(typecode);
	}

	@Override
	public Set<StructureTypeMode> getStructureModes(final String typecode)
	{
		return Collections.unmodifiableSet(
				Optional.ofNullable(getStructureTypeAttributeMap().get(typecode))
						.map(structureTypeModeMap -> structureTypeModeMap.keySet())
						.orElse(Sets.newHashSet())
				);
	}

	@Override
	public Set<TypeAttributeStructure> getTypeAttributeStructures(final String typeCode, final StructureTypeMode mode)
	{
		return Collections.unmodifiableSet( //
				Optional.ofNullable(getStructureTypeAttributeMap().get(typeCode)) //
						.map(structureTypeModeMap -> structureTypeModeMap.get(mode)) //
						.orElse(Sets.newHashSet()));
	}

	/**
	 * Suppress sonar warning (squid:S2095 | Resources should be closed ) : Stream.of() does not hold a resource.
	 */
	@Override
	public void afterPropertiesSet() throws Exception
	{
		if (Objects.isNull(getAllTypeAttributeStructures()))
		{
			setAllTypeAttributeStructures(new HashSet<>());
		}
		if (Objects.isNull(getAllTypeStructures()))
		{
			setAllTypeStructures(new HashSet<>());
		}
		getAllTypeAttributeStructures().stream().forEach(attributeType ->
				putOrUpdateStructureType(attributeType)
				);

		getAllTypeStructures().stream().forEach(
				typeStructure -> getStructureTypeMap().putIfAbsent(typeStructure.getTypecode(), typeStructure));



		//add the base populators to all attributes
		List<Populator<AttributeDescriptorModel, ComponentTypeAttributeData>> baseAttributePopulators = getBaseComponentTypeStructureAttributePrototype()
				.getPopulators();

		getStructureTypeAttributeMap()
				.entrySet()
				.forEach(
						typeCodeTostructureTypeModeMapEntry ->
						{
							Map<StructureTypeMode, Set<TypeAttributeStructure>> structureTypeModeMap = typeCodeTostructureTypeModeMapEntry
									.getValue();
							structureTypeModeMap
									.entrySet()
									.forEach(
											modeToTypeAttributeStructuresMapEntry ->
											{
												Set<TypeAttributeStructure> typeAttributeStructures = modeToTypeAttributeStructuresMapEntry
														.getValue();
												typeAttributeStructures
														.forEach(typeAttributeStructure ->
														{
															List<Populator<AttributeDescriptorModel, ComponentTypeAttributeData>> populators = newArrayList(baseAttributePopulators);
															populators.addAll(typeAttributeStructure.getPopulators());
															typeAttributeStructure.setPopulators(populators);
														});
											});

						});
	}

	/**
	 * If the map of component type attribute structures is empty, then add this element to the map. Otherwise, update the set of
	 * attributes for the mode found in the attribute by adding the new attribute.
	 * @param attribute
	 *           - the attribute type structure to process
	 */
	protected void putOrUpdateStructureType(final TypeAttributeStructure attribute)
	{
		getStructureTypeAttributeMap() //
				.computeIfAbsent(attribute.getTypecode(), typeCode -> new HashMap<>()) //
				.computeIfAbsent(attribute.getMode(), mode -> new HashSet<>()) //
				.add(attribute);
	}

	protected Map<String, Map<StructureTypeMode, Set<TypeAttributeStructure>>> getStructureTypeAttributeMap()
	{
		return structureTypeAttributeMap;
	}

	protected Set<TypeAttributeStructure> getAllTypeAttributeStructures()
	{
		return allTypeAttributeStructures;
	}

	public void setAllTypeAttributeStructures(final Set<TypeAttributeStructure> allTypeAttributeStructures)
	{
		this.allTypeAttributeStructures = allTypeAttributeStructures;
	}

	protected Set<TypeStructure> getAllTypeStructures()
	{
		return allTypeStructures;
	}

	public void setAllTypeStructures(final Set<TypeStructure> allTypeStructures)
	{
		this.allTypeStructures = allTypeStructures;
	}

	protected Map<String, TypeStructure> getStructureTypeMap()
	{
		return structureTypeMap;
	}

	@Required
	public void setBaseComponentTypeStructureAttributePrototype(
			ComponentTypeAttributeStructure baseComponentTypeStructureAttributePrototype)
	{
		this.baseComponentTypeStructureAttributePrototype = baseComponentTypeStructureAttributePrototype;
	}

	protected ComponentTypeAttributeStructure getBaseComponentTypeStructureAttributePrototype()
	{
		return baseComponentTypeStructureAttributePrototype;
	}
}
