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
package de.hybris.platform.sap.productconfig.runtime.cps.populator.impl;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalItem;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalObjectKey;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalValue;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSQuantity;
import de.hybris.platform.sap.productconfig.runtime.interf.external.CharacteristicValue;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Instance;
import de.hybris.platform.sap.productconfig.runtime.interf.external.PartOfRelation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;


/**
 * Populates the CPS external representation for the root item and subsequent external subitems
 */
public class ExternalItemTreePopulator implements Populator<Configuration, CPSExternalItem>
{

	protected static final String CHARACTERISTIC_KEY_SEPARATOR = ";";
	private UnitService unitService;

	/**
	 * @return the unitService
	 */
	protected UnitService getUnitService()
	{
		return unitService;
	}

	@Override
	public void populate(final Configuration source, final CPSExternalItem target)
	{
		populateCoreItemAttributes(source.getRootInstance(), target);
		buildHierarchicalExternalConfiguration(target,
				processInstancesAndPartsOfRelations(source.getPartOfRelations(), source.getInstances()),
				processCharacteristicValues(source.getCharacteristicValues()));
	}

	protected void buildHierarchicalExternalConfiguration(final CPSExternalItem target,
			final Map<String, List<CPSExternalItem>> subItemMap,
			final Map<String, List<CPSExternalCharacteristic>> characteristicMap)
	{
		final String instanceId = target.getId();
		List<CPSExternalCharacteristic> characteristicList = characteristicMap.get(instanceId);
		if (characteristicList == null)
		{
			characteristicList = new ArrayList<>();
		}
		target.setCharacteristics(characteristicList);

		List<CPSExternalItem> subItemList = subItemMap.get(instanceId);
		if (subItemList == null)
		{
			subItemList = new ArrayList<>();
		}
		target.setSubItems(subItemList);

		for (final CPSExternalItem subItem : subItemList)
		{
			buildHierarchicalExternalConfiguration(subItem, subItemMap, characteristicMap);
		}
	}

	protected void populateSubItems(final List<Instance> instances, final Map<String, CPSExternalItem> subItemMap)
	{
		for (final Instance subInstance : instances)
		{
			final CPSExternalItem item = subItemMap.get(subInstance.getId());
			if (item != null)
			{
				populateCoreItemAttributes(subInstance, item);
			}
		}

	}

	protected Map<String, List<CPSExternalItem>> processInstancesAndPartsOfRelations(final List<PartOfRelation> partOfRelations,
			final List<Instance> instances)

	{
		final Map<String, List<CPSExternalItem>> subItemsMap = new HashMap<>();
		final Map<String, CPSExternalItem> flatSubItems = new HashMap<>();
		for (final PartOfRelation partOfRelation : partOfRelations)
		{
			final String parentItemId = partOfRelation.getParentInstId();
			List<CPSExternalItem> subtItemsList = subItemsMap.get(parentItemId);
			if (subtItemsList == null)
			{
				subtItemsList = new ArrayList<>();
				subItemsMap.put(parentItemId, subtItemsList);
			}
			final CPSExternalItem externalItem = createExternalItem(partOfRelation);
			subtItemsList.add(externalItem);
			flatSubItems.put(partOfRelation.getInstId(), externalItem);
		}
		populateSubItems(instances, flatSubItems);
		return subItemsMap;
	}

	protected CPSExternalItem createExternalItem(final PartOfRelation partOfRelation)
	{
		final CPSExternalItem item = new CPSExternalItem();
		final CPSExternalObjectKey bomPositionObjectKey = new CPSExternalObjectKey();
		bomPositionObjectKey.setId(partOfRelation.getObjectKey());
		bomPositionObjectKey.setType(partOfRelation.getObjectType());
		bomPositionObjectKey.setClassType(partOfRelation.getClassType());
		item.setBomPositionObjectKey(bomPositionObjectKey);
		item.setBomPosition(partOfRelation.getPosNr());
		item.setBomPositionAuthor(partOfRelation.getAuthor());
		return item;
	}

	protected Map<String, List<CPSExternalCharacteristic>> processCharacteristicValues(
			final List<CharacteristicValue> characteristicValues)
	{
		final Map<String, List<CPSExternalCharacteristic>> characteristicMap = new HashMap<>();
		fillCharacteristicMap(characteristicMap, characteristicValues);
		return characteristicMap;
	}

	protected void fillCharacteristicMap(final Map<String, List<CPSExternalCharacteristic>> characteristicMap,
			final List<CharacteristicValue> characteristicValues)
	{
		final Map<String, List<CPSExternalValue>> valueMap = new HashMap<>();
		for (final CharacteristicValue value : characteristicValues)
		{
			List<CPSExternalCharacteristic> characteristicList = characteristicMap.get(value.getInstId());
			if (characteristicList == null)
			{
				characteristicList = new ArrayList<>();
				characteristicMap.put(value.getInstId(), characteristicList);
			}

			final String characteristicKey = computeCharacteristicKey(value);
			List<CPSExternalValue> valueList = valueMap.get(characteristicKey);
			if (valueList == null)
			{
				valueList = new ArrayList<>();
				valueMap.put(characteristicKey, valueList);
				final CPSExternalCharacteristic characteristic = createExternalCharacteristic(value, valueList);
				characteristicList.add(characteristic);
			}
			valueList.add(createExternalValue(value));

		}
	}

	protected CPSExternalCharacteristic createExternalCharacteristic(final CharacteristicValue value,
			final List<CPSExternalValue> valueList)
	{
		final CPSExternalCharacteristic characteristic = new CPSExternalCharacteristic();
		characteristic.setId(value.getCharacteristic());
		characteristic.setValues(valueList);
		characteristic.setVisible(!value.isInvisible());
		return characteristic;
	}

	protected String computeCharacteristicKey(final CharacteristicValue value)
	{
		return new StringBuilder().append(value.getInstId()).append(CHARACTERISTIC_KEY_SEPARATOR).append(value.getCharacteristic())
				.toString();
	}

	protected CPSExternalValue createExternalValue(final CharacteristicValue value)
	{
		final CPSExternalValue externalValue = new CPSExternalValue();
		externalValue.setAuthor(value.getAuthor());
		externalValue.setValue(value.getValue());
		return externalValue;
	}

	protected void populateCoreItemAttributes(final Instance source, final CPSExternalItem target)
	{
		target.setComplete(source.isComplete());
		target.setConsistent(source.isConsistent());
		target.setId(source.getId());

		final CPSExternalObjectKey objectKey = new CPSExternalObjectKey();
		objectKey.setType(source.getObjectType());
		objectKey.setId(source.getObjectKey());
		objectKey.setClassType(source.getClassType());
		target.setObjectKey(objectKey);
		target.setObjectKeyAuthor(source.getAuthor());

		final CPSQuantity quantity = new CPSQuantity();
		quantity.setUnit(convertToIso(source.getQuantityUnit()));
		quantity.setValue(Double.valueOf(source.getQuantity()));
		target.setQuantity(quantity);
	}

	protected String convertToIso(final String quantityUnit)
	{
		if (StringUtils.isEmpty(quantityUnit))
		{
			return null;
		}
		final List<String> isoCodes = getUnitService().getAllUnits().stream()//
				.filter(unitModel -> quantityUnit.equals(unitModel.getSapCode()))//
				.map(unitModel -> unitModel.getCode())//
				.collect(Collectors.toList());
		final int size = isoCodes.size();
		if (size == 0)
		{
			throw new IllegalStateException("No unit for SAP code available: " + quantityUnit);
		}
		if (size > 1)
		{
			throw new IllegalStateException("Multiple matches found for SAP code: " + quantityUnit);
		}
		return isoCodes.get(0);
	}

	/**
	 * @param unitService
	 *           hybris unit service, used for doing mapping from SAP unit code to ISO code
	 */
	public void setUnitService(final UnitService unitService)
	{
		this.unitService = unitService;
	}

}
