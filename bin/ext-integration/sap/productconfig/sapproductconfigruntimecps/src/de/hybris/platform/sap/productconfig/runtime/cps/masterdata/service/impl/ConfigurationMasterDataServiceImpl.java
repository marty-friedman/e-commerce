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
package de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.impl;

import de.hybris.platform.sap.productconfig.runtime.cps.cache.MasterDataCacheAccessService;
import de.hybris.platform.sap.productconfig.runtime.cps.constants.SapproductconfigruntimecpsConstants;
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.ConfigurationMasterDataService;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataCharacteristicGroup;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataPossibleValue;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataPossibleValueSpecific;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataCharacteristicContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataCharacteristicSpecificContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataClassContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataProductContainer;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link ConfigurationMasterDataService}. Accesses a hybris cache via
 * {@link MasterDataCacheAccessService} for providing configuration related master data
 */
public class ConfigurationMasterDataServiceImpl implements ConfigurationMasterDataService
{

	private MasterDataCacheAccessService cacheAccessService;
	private I18NService i18NService;

	private static final String CSTIC_TYPE_STRING = "string";
	protected static final String CSTIC_TYPE_FLOAT = "float";
	protected static final String CSTIC_TYPE_INTEGER = "integer";

	@Override
	public String getItemName(final String kbId, final String id, final String type)
	{
		if (type == null)
		{
			throw new IllegalStateException("Type is null.");
		}
		if (SapproductconfigruntimecpsConstants.ITEM_TYPE_MARA.equals(type))
		{
			return getProductName(kbId, id);
		}
		if (SapproductconfigruntimecpsConstants.ITEM_TYPE_KLAH.equals(type))
		{
			return getClassName(kbId, id);
		}
		throw new IllegalStateException("Invalide type: " + type);
	}

	protected String getProductName(final String kbId, final String id)
	{
		return getProduct(kbId, id).getName();
	}

	@Override
	public boolean isProductMultilevel(final String kbId, final String id)
	{
		return getProduct(kbId, id).isMultilevel();
	}

	protected CPSMasterDataProductContainer getProduct(final String kbId, final String id)
	{
		final CPSMasterDataProductContainer product = cacheAccessService
				.getKbContainer(kbId, getI18NService().getCurrentLocale().getLanguage()).getProducts().get(id);
		if (product == null)
		{
			throw new IllegalStateException("Could not find product for: " + id);
		}
		return product;
	}

	protected CPSMasterDataClassContainer getClass(final String kbId, final String id)
	{
		final CPSMasterDataClassContainer product = cacheAccessService
				.getKbContainer(kbId, getI18NService().getCurrentLocale().getLanguage()).getClasses().get(id);
		if (product == null)
		{
			throw new IllegalStateException("Could not find class for: " + id);
		}
		return product;
	}

	protected String getClassName(final String kbId, final String id)
	{

		return getClass(kbId, id).getName();
	}

	@Override
	public String getGroupName(final String kbId, final String itemKey, final String itemType, final String groupId)
	{
		if (SapproductconfigruntimecpsConstants.ITEM_TYPE_MARA.equals(itemType))
		{
			return getGroup(kbId, itemKey, groupId).getName();
		}
		else if (SapproductconfigruntimecpsConstants.ITEM_TYPE_KLAH.equals(itemType))
		{
			return getClassName(kbId, itemKey);
		}
		else
		{
			throw new IllegalArgumentException("Item type not allowed: " + itemType);
		}
	}

	protected CPSMasterDataCharacteristicGroup getGroup(final String kbId, final String itemKey, final String groupId)
	{
		final CPSMasterDataProductContainer product = getProduct(kbId, itemKey);
		final CPSMasterDataCharacteristicGroup group = product.getGroups().get(groupId);
		if (group == null)
		{
			throw new IllegalStateException("Could not find group for: " + groupId);
		}
		return group;
	}

	@Override
	public CPSMasterDataCharacteristicContainer getCharacteristic(final String kbId, final String characteristicId)
	{
		final CPSMasterDataCharacteristicContainer csticContainer = getMasterData(kbId).getCharacteristics().get(characteristicId);
		if (csticContainer == null)
		{
			throw new IllegalStateException("Could not find characteristic for: " + characteristicId);
		}
		return csticContainer;
	}

	@Override
	public String getValueName(final String kbId, final String characteristicId, final String valueId)
	{
		final CPSMasterDataCharacteristicContainer cstic = getCharacteristic(kbId, characteristicId);
		if (!isCsticStringType(cstic))
		{
			// For cstics with a type different from "string", the value names are formatted in facade-layer.
			return null;
		}
		final CPSMasterDataPossibleValue value = cstic.getPossibleValueGlobals().get(valueId);
		if (value == null)
		{
			return valueId;
		}
		return value.getName();

	}

	@Override
	public CPSMasterDataKnowledgeBaseContainer getMasterData(final String kbId)
	{
		if (StringUtils.isEmpty(kbId))
		{
			throw new IllegalArgumentException("KbId is not provided, expecting a non-empty string");
		}
		return getCacheAccessService().getKbContainer(kbId, getI18NService().getCurrentLocale().getLanguage());
	}

	@Override
	public List<String> getGroupCharacteristicIDs(final String kbId, final String itemKey, final String itemType,
			final String groupId)
	{
		if (SapproductconfigruntimecpsConstants.ITEM_TYPE_MARA.equals(itemType))
		{
			return getGroup(kbId, itemKey, groupId).getCharacteristicIDs();
		}
		else if (SapproductconfigruntimecpsConstants.ITEM_TYPE_KLAH.equals(itemType))
		{
			return getClass(kbId, itemKey).getCharacteristicSpecifics().entrySet().stream().map(entry -> entry.getKey())
					.collect(Collectors.toList());
		}
		else
		{
			throw new IllegalArgumentException("Item type not allowed: " + itemType);
		}
	}

	@Override
	public String getValuePricingKey(final String kbId, final String productId, final String characteristicId,
			final String valueId)
	{
		final CPSMasterDataCharacteristicSpecificContainer csticSpecific = getMasterData(kbId).getProducts().get(productId)
				.getCstics().get(characteristicId);
		if (csticSpecific == null)
		{
			return null;
		}
		final CPSMasterDataPossibleValueSpecific value = csticSpecific.getPossibleValueSpecifics().get(valueId);
		if (value == null)
		{
			return null;
		}
		return value.getVariantConditionKey();

	}

	@Override
	public Set<String> getSpecificPossibleValueIds(final String kbId, final String productId, final String itemType,
			final String characteristicId)
	{
		if (SapproductconfigruntimecpsConstants.ITEM_TYPE_MARA.equals(itemType))
		{
			final CPSMasterDataProductContainer cpsMasterDataProductContainer = getMasterData(kbId).getProducts().get(productId);
			final CPSMasterDataCharacteristicSpecificContainer csticSpecific = cpsMasterDataProductContainer.getCstics()
					.get(characteristicId);
			if (csticSpecific == null)
			{
				return Collections.emptySet();
			}
			return csticSpecific.getPossibleValueSpecifics().keySet();
		}
		else if (SapproductconfigruntimecpsConstants.ITEM_TYPE_KLAH.equals(itemType))
		{
			return Collections.emptySet();
		}
		else
		{
			throw new IllegalArgumentException("Unknown item type: " + itemType);
		}
	}

	@Override
	public Set<String> getPossibleValueIds(final String kbId, final String characteristicId)
	{
		final CPSMasterDataCharacteristicContainer cstic = getMasterData(kbId).getCharacteristics().get(characteristicId);
		if (cstic == null)
		{
			return Collections.emptySet();
		}
		return cstic.getPossibleValueGlobals().keySet();
	}

	protected boolean isCsticStringType(final CPSMasterDataCharacteristicContainer cstic)
	{
		return CSTIC_TYPE_STRING.equals(cstic.getType());
	}

	/**
	 * @return the cacheAccessService
	 */
	public MasterDataCacheAccessService getCacheAccessService()
	{
		return cacheAccessService;
	}

	/**
	 * @param cacheAccessService
	 *           the cacheAccessService to set
	 */
	public void setCacheAccessService(final MasterDataCacheAccessService cacheAccessService)
	{
		this.cacheAccessService = cacheAccessService;
	}

	protected I18NService getI18NService()
	{
		return i18NService;
	}

	/**
	 * @param i18nService
	 *           the i18NService to set
	 */
	@Required
	public void setI18NService(final I18NService i18nService)
	{
		i18NService = i18nService;
	}


	protected boolean isCsticNumericType(final CPSMasterDataCharacteristicContainer csticContainer)
	{
		final String type = csticContainer.getType();
		return CSTIC_TYPE_FLOAT.equals(type) || CSTIC_TYPE_INTEGER.equals(type);
	}

	@Override
	public boolean isCharacteristicNumeric(final String kbId, final String csticId)
	{
		final CPSMasterDataCharacteristicContainer cstic = getMasterData(kbId).getCharacteristics().get(csticId);
		return isCsticNumericType(cstic);
	}

}
