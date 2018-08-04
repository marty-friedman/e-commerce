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
import de.hybris.platform.cmsfacades.data.ComponentTypeData;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeAttributeStructure;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeStructure;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeStructureService;
import de.hybris.platform.cmssmarteditwebservices.data.StructureTypeMode;
import de.hybris.platform.cmssmarteditwebservices.structures.comparator.ComponentTypeAttributeDataComparator;
import de.hybris.platform.cmssmarteditwebservices.structures.service.*;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.type.TypeService;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import static com.google.common.collect.Maps.newHashMap;
import static de.hybris.platform.cmssmarteditwebservices.constants.CmssmarteditwebservicesConstants.TYPE_CACHE_EXPIRATION;


/**
 * Default implementation of the {@link TypeStructureService} interface.
 * @deprecated since version 6.5
 */
@Deprecated
@HybrisDeprecation(sinceVersion = "6.5")
public class DefaultTypeStructureService implements TypeStructureService, InitializingBean
{
	protected static final Long DEFAULT_EXPIRATION_TIME = 360l;
	private TypeStructureRegistry typeStructureRegistry;
	private ComponentTypeStructureService componentTypeStructureService;
	private ComponentTypeAttributeDataComparatorRegistry componentTypeAttributeDataComparatorRegistry;
	private TypeService typeService;
	private Converter<ComponentTypeStructure, ComponentTypeData> componentTypeStructureConverter;
	private ConfigurationService configurationService;

	private Supplier<Map<String, Map<StructureTypeMode, ComponentTypeData>>> componentTypeDataSupplierMap = initializeInternalStructureMap(DEFAULT_EXPIRATION_TIME);

	@Override
	public List<ComponentTypeData> getComponentTypesByCode(final String typeCode)
	{

		return Optional.ofNullable(getComponentTypeDataSupplierMap().get().get(typeCode)) //
				.orElse(newHashMap()) //
				.entrySet() //
				.stream() //
				.filter(entry -> entry.getKey() != StructureTypeMode.BASE) //
				.map(entry -> entry.getValue()) //
				.filter(value -> value != null)
				.collect(Collectors.toList());
	}

	@Override
	public ComponentTypeData getComponentTypeByCodeAndMode(final String typeCode, final String mode)
	{
		final StructureTypeMode structureTypeMode = StructureTypeMode.valueOf(mode);
		return Optional.ofNullable(getComponentTypeDataSupplierMap().get().get(typeCode)) //
				.orElse(newHashMap()).get(structureTypeMode);
	}

	/**
	 * Internal method to initialize the Map that will serve as a data store for this service.
	 *
	 * @return the supplier method to get the {@code Map<String, Map<StructureTypeMode, ComponentTypeData>>}
	 */
	protected Supplier<Map<String, Map<StructureTypeMode, ComponentTypeData>>> initializeComponentTypeDataSupplier()
	{
		return () -> {
			final Map<String, Map<StructureTypeMode, ComponentTypeData>> componentTypeDataMap;

			// get all types that are defined
			componentTypeDataMap = getComponentTypeStructureService().getComponentTypeStructures() //
					.stream() //
					.map(ComponentTypeStructure::getTypecode) //
					.collect(Collectors.toMap(typeCode -> typeCode, typeCode -> getComponentTypesByCodeInternal(typeCode), (o1, o2) -> o1));
			return componentTypeDataMap;
		};
	}

	/**
	 * Internal method to build the <code>Map<StructureTypeMode, ComponentTypeData></code> for a given type code.
	 *
	 * @param typeCode
	 *           the Item Type Code
	 * @return a map where the keys are the {@code StructureTypeMode} and the values are {@code ComponentTypeData}
	 */
	protected Map<StructureTypeMode, ComponentTypeData> getComponentTypesByCodeInternal(final String typeCode)
	{
		final Map<StructureTypeMode, ComponentTypeData> structureTypeModeMap = new HashMap<>();

		// merge with the information from this extension's registry
		final TypeStructure baseTypeStructure = new DefaultTypeStructure(getComponentTypeStructureService().getComponentTypeStructure(typeCode));
		// merge baseTypeStructure with type structures from this registry
		mergeBaseAndRegistryStructures(typeCode, baseTypeStructure);
		// merge baseTypeStructure with attributes from this registry
		mergeBaseAndRegistryAttributes(typeCode, baseTypeStructure);

		// update the map
		Arrays.stream(StructureTypeMode.values()) //
		.forEach(structureTypeMode -> structureTypeModeMap.put(structureTypeMode,
				convertTypeStructureData(structureTypeMode, baseTypeStructure)));

		return structureTypeModeMap;
	}


	/**
	 * Merges the BASE attributes (cmsfacades) with the ones defined in the registry
	 *
	 * @param typeCode
	 *           the starting type to look for attributes
	 * @param baseTypeStructure
	 *           the base type structure defined in the base (cmsfacades)
	 */
	protected void mergeBaseAndRegistryAttributes(final String typeCode, final TypeStructure baseTypeStructure)
	{
		final Map<StructureTypeMode, Set<TypeAttributeStructure>> baseTypeStructureAttributeMap = baseTypeStructure
				.getAttributesByModeMap();

		// do some adjustments based on the requirements
		// put attributes for the modes on the registry, applying attributes from the registry
		// first pass with the default attributes
		final Set<TypeAttributeStructure> defaultRegistryAttributes = collectRegistryAttributes(typeCode,
				StructureTypeMode.DEFAULT);
		Arrays.stream(StructureTypeMode.values()) //
		.forEach(mode -> {
			if (!hasMode(typeCode, mode))
			{
				baseTypeStructureAttributeMap.put(mode,
						mergeBaseAndRegistryAttributes(typeCode, mode, baseTypeStructure.getAttributes(), defaultRegistryAttributes));
			}
		});

		// initiate Map with the base attributes in ALL modes
		getTypeStructureRegistry().getStructureModes(typeCode) //
		.stream().forEach(mode -> {
			baseTypeStructureAttributeMap.put(mode, mergeBaseAndRegistryAttributes(typeCode, mode, baseTypeStructure.getAttributes(),
					collectRegistryAttributes(typeCode, mode)));
		});
	}

	/**
	 * Merges the BASE structure (cmsfacades) and populators with the ones defined in the registry
	 *
	 * @param typeCode
	 *           the starting type to look for attributes
	 * @param baseTypeStructure
	 *           the base type structure defined in the base (cmsfacades)
	 */
	protected void mergeBaseAndRegistryStructures(final String typeCode, final TypeStructure baseTypeStructure)
	{
		final TypeStructure typeStructure = getTypeStructureRegistry().getTypeStructure(typeCode);
		if (Objects.nonNull(typeStructure))
		{
			baseTypeStructure.getPopulators().addAll(typeStructure.getPopulators());
			baseTypeStructure.setTypeDataClass(typeStructure.getTypeDataClass());
			baseTypeStructure.setCategory(typeStructure.getCategory());
		}
	}

	/**
	 * Get all attributes defined for this type and for any other type
	 *
	 * @param typeCode
	 *           the starting type to look for attributes
	 * @param mode
	 *           the mode in which the attributes were defined
	 */
	protected Set<TypeAttributeStructure> collectRegistryAttributes(final String typeCode, final StructureTypeMode mode)
	{
		final Set<TypeAttributeStructure> registryAttributes = new HashSet<>();
		// add the attributes defined for the type
		registryAttributes.addAll(getTypeStructureRegistry().getTypeAttributeStructures(typeCode, mode));

		// add the attributes defined for all super types
		final ComposedTypeModel composedType = getTypeService().getComposedTypeForCode(typeCode);
		composedType.getAllSuperTypes() //
		.stream() //
		.forEach(superType -> registryAttributes
				.addAll(getTypeStructureRegistry().getTypeAttributeStructures(superType.getCode(), mode)));
		return registryAttributes;
	}


	protected boolean hasMode(final String typeCode, final StructureTypeMode mode)
	{
		return getTypeStructureRegistry().getStructureModes(typeCode).contains(mode);
	}

	/**
	 * Merges the BASE attributes (cmsfacades) with the ones defined in the registry
	 *
	 * @param typeCode
	 *           the starting type to look for attributes
	 * @param mode
	 *           the MODE in which the attribute set will be created
	 * @param baseAttributes
	 *           the base attributes
	 * @param registryAttributes
	 *           the attributes defined for this registry, which should be a subset of the base attributes
	 * @return the merged attribute set
	 */
	protected Set<TypeAttributeStructure> mergeBaseAndRegistryAttributes(final String typeCode, 
			final StructureTypeMode mode,
			final Set<ComponentTypeAttributeStructure> baseAttributes, final Set<TypeAttributeStructure> registryAttributes)
	{
		final Set<TypeAttributeStructure> attributes = baseAttributes //
				.stream() //
				.map(baseAttribute -> {
					final DefaultTypeAttributeStructure attribute = new DefaultTypeAttributeStructure(baseAttribute);
					registryAttributes.stream() //
					.filter(registryAttribute -> registryAttribute.getQualifier().equals(baseAttribute.getQualifier())) //
					.findFirst() //
					.ifPresent(registryAttribute -> attribute.getPopulators().addAll(registryAttribute.getPopulators()));
					attribute.setMode(mode);
					return attribute;
				}) //
				.collect(Collectors.toSet());

		final Set<String> registryQualifiers = registryAttributes.stream() //
				.map(TypeAttributeStructure::getQualifier) //
				.collect(Collectors.toSet());
		
		final Set<String> registryTypes = registryAttributes.stream() //
				.map(TypeAttributeStructure::getTypecode) //
				.collect(Collectors.toSet());

		// if the attributes are defined in this registry, regardless of the level in the hierarchy
		// then we need to remove the extra attributes, otherwise return all attributes from base
		if (registryQualifiers.size() == 0)
		{
			return attributes;
		}
		else
		{

			final Predicate<TypeAttributeStructure> isAttributePresentInRegistry = 
					typeAttributeStructure -> registryQualifiers.contains(typeAttributeStructure.getQualifier());

			final Predicate<TypeAttributeStructure> isAttributeTypePresentInRegistry =
					typeAttributeStructure -> registryTypes.contains(typeAttributeStructure.getTypecode());

			return attributes.stream() //
					.filter(isAttributePresentInRegistry.or(isAttributeTypePresentInRegistry.negate())) //
					.collect(Collectors.toSet());
		}
	}

	/**
	 * Convert the {@link TypeStructure} into {@link ComponentTypeData}
	 *
	 * @param mode
	 *           the mode for this current type structure view.
	 * @param baseTypeStructure
	 *           the base type attribute modified
	 * @return
	 */
	protected ComponentTypeData convertTypeStructureData(final StructureTypeMode mode, final TypeStructure baseTypeStructure)
	{
		final TypeStructure typeStructure = new DefaultTypeStructure(baseTypeStructure);
		// clear attributes before adding the ones related to this mode
		typeStructure.getAttributes().clear();
		typeStructure.getAttributes()
		.addAll(baseTypeStructure.getAttributesByModeMap().get(mode) //
				.stream() //
				.map(typeAttributeStructure -> new DefaultTypeAttributeStructure(typeAttributeStructure, mode)) //
				.collect(Collectors.toList()));
		// sets the requested MODE again on each attribute
		final ComponentTypeData componentTypeData = getComponentTypeStructureConverter().convert(typeStructure);
		componentTypeData.getAttributes() //
		.stream() //
		.forEach(componentTypeAttributeData -> componentTypeAttributeData.setMode(mode.name()));

		// sort the attributes
		getAttributeComparator(typeStructure.getTypecode(), mode)
		.ifPresent(comparator -> componentTypeData.getAttributes().sort(comparator));

		return componentTypeData;
	}

	/**
	 * Gets the {@link ComponentTypeAttributeDataComparator} for the given type code and mode. If no comparator is found
	 * matching the mode for the specified type code, return the comparator for the {@link StructureTypeMode#DEFAULT}
	 * mode instead. If no comparator is found matching the type code, return {@link Optional#empty()}.
	 *
	 * @param code
	 *           - the type code
	 * @param mode
	 *           - the structure type mode
	 * @return the comparator for the given type code and mode; never <tt>null</tt>
	 */
	protected Optional<ComponentTypeAttributeDataComparator> getAttributeComparator(final String code,
			final StructureTypeMode mode)
	{
		final ComposedTypeModel composedType = getTypeService().getComposedTypeForCode(code);

		Optional<ComponentTypeAttributeDataComparator> comparator = getComponentTypeAttributeDataComparatorRegistry()
				.getComparatorForTypecode(code, mode);
		if (!comparator.isPresent())
		{
			comparator = getComponentTypeAttributeDataComparatorRegistry().getComparatorForTypecode(code, StructureTypeMode.DEFAULT);
		}

		if (!comparator.isPresent() && composedType.getSuperType() != null)
		{
			comparator = getAttributeComparator(composedType.getSuperType().getCode(), mode);
		}

		return comparator;
	}

	protected Supplier<Map<String, Map<StructureTypeMode, ComponentTypeData>>> getComponentTypeDataSupplierMap()
	{
		return componentTypeDataSupplierMap;
	}

	protected Supplier<Map<String, Map<StructureTypeMode, ComponentTypeData>>> initializeInternalStructureMap(final Long expirationTime)
	{
		return componentTypeDataSupplierMap =
				Suppliers.memoizeWithExpiration(initializeComponentTypeDataSupplier(),
						expirationTime,
						TimeUnit.MINUTES);
	}

	@Override
	public void afterPropertiesSet() throws Exception
	{
		this.componentTypeDataSupplierMap = initializeInternalStructureMap(getConfigurationService().getConfiguration().getLong(TYPE_CACHE_EXPIRATION, DEFAULT_EXPIRATION_TIME));
	}

	protected TypeStructureRegistry getTypeStructureRegistry()
	{
		return typeStructureRegistry;
	}

	@Required
	public void setTypeStructureRegistry(final TypeStructureRegistry typeStructureRegistry)
	{
		this.typeStructureRegistry = typeStructureRegistry;
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

	protected ComponentTypeAttributeDataComparatorRegistry getComponentTypeAttributeDataComparatorRegistry()
	{
		return componentTypeAttributeDataComparatorRegistry;
	}

	@Required
	public void setComponentTypeAttributeDataComparatorRegistry(
			final ComponentTypeAttributeDataComparatorRegistry componentTypeAttributeDataComparatorRegistry)
	{
		this.componentTypeAttributeDataComparatorRegistry = componentTypeAttributeDataComparatorRegistry;
	}

	protected ComponentTypeStructureService getComponentTypeStructureService()
	{
		return componentTypeStructureService;
	}

	@Required
	public void setComponentTypeStructureService(final ComponentTypeStructureService componentTypeStructureService)
	{
		this.componentTypeStructureService = componentTypeStructureService;
	}

	protected Converter<ComponentTypeStructure, ComponentTypeData> getComponentTypeStructureConverter()
	{
		return componentTypeStructureConverter;
	}

	@Required
	public void setComponentTypeStructureConverter(
			final Converter<ComponentTypeStructure, ComponentTypeData> componentTypeStructureConverter)
	{
		this.componentTypeStructureConverter = componentTypeStructureConverter;
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}
}
