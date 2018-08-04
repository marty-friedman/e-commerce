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
import de.hybris.platform.sap.productconfig.runtime.cps.cache.MasterDataCacheAccessService;
import de.hybris.platform.sap.productconfig.runtime.cps.constants.SapproductconfigruntimecpsConstants;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristicGroup;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSItem;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSQuantity;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.I18NService;

import org.apache.log4j.Logger;

import com.google.common.base.Preconditions;


/**
 * Responsible to populate instances for the configuration runtime. Also orchestrates the conversion of dependent
 * objects (sub instances, groups).<br>
 * <br>
 * This populator breaks our standard pattern that distinguishes between runtime population (accessing static master
 * data in <b>read</b> mode when needed) and populating the master data caches themselves: Here we add the root items'
 * base unit of measure to the master data cache, as we don't get this information from the master data services.
 */
public class InstancePopulator implements Populator<CPSItem, InstanceModel>
{

	private Converter<CPSItem, InstanceModel> instanceModelConverter;
	private Converter<CPSCharacteristicGroup, CsticGroupModel> characteristicGroupConverter;
	private Converter<CPSCharacteristic, CsticModel> characteristicConverter;
	private MasterDataCacheAccessService masterDataCacheAccessService;
	private I18NService i18NService;

	private static final Logger LOG = Logger.getLogger(InstancePopulator.class);

	protected I18NService getI18NService()
	{
		return i18NService;
	}

	protected MasterDataCacheAccessService getMasterDataCacheAccessService()
	{
		return masterDataCacheAccessService;
	}

	/**
	 * @return the characteristicConverter
	 */
	public Converter<CPSCharacteristic, CsticModel> getCharacteristicConverter()
	{
		return characteristicConverter;
	}

	/**
	 * @param characteristicConverter
	 *           the characteristicConverter to set
	 */
	public void setCharacteristicConverter(final Converter<CPSCharacteristic, CsticModel> characteristicConverter)
	{
		this.characteristicConverter = characteristicConverter;
	}

	/**
	 * @return the characteristicGroupConverter
	 */
	public Converter<CPSCharacteristicGroup, CsticGroupModel> getCharacteristicGroupConverter()
	{
		return characteristicGroupConverter;
	}

	/**
	 * @param characteristicGroupConverter
	 *           the characteristicGroupConverter to set
	 */
	public void setCharacteristicGroupConverter(
			final Converter<CPSCharacteristicGroup, CsticGroupModel> characteristicGroupConverter)
	{
		this.characteristicGroupConverter = characteristicGroupConverter;
	}

	/**
	 * @return the instanceModelConverter
	 */
	public Converter<CPSItem, InstanceModel> getInstanceModelConverter()
	{
		return instanceModelConverter;
	}

	/**
	 * @param instanceModelConverter
	 *           the instanceModelConverter to set
	 */
	public void setInstanceModelConverter(final Converter<CPSItem, InstanceModel> instanceModelConverter)
	{
		this.instanceModelConverter = instanceModelConverter;
	}



	@Override
	public void populate(final CPSItem source, final InstanceModel target)
	{
		populateCoreAttributes(source, target);
		populateSubItems(source, target);
		populateGroups(source, target);
		populateCstics(source, target);
		populateRootUOMToMasterDataCache(source);
	}


	protected void populateRootUOMToMasterDataCache(final CPSItem source)
	{
		final CPSConfiguration parentConfiguration = source.getParentConfiguration();
		Preconditions.checkNotNull(parentConfiguration, "CPSConfiguration needs to be present");
		final CPSItem rootItem = parentConfiguration.getRootItem();
		if (source.getId().equals(rootItem.getId()))
		{
			populateUOMToMasterDataCache(source, parentConfiguration);
		}


	}

	protected void populateUOMToMasterDataCache(final CPSItem source, final CPSConfiguration parentConfiguration)
	{
		final String kbId = parentConfiguration.getKbId();
		Preconditions.checkNotNull(kbId, "KbId needs to be present");
		final CPSMasterDataKnowledgeBaseContainer kbContainer = getMasterDataCacheAccessService().getKbContainer(kbId,
				getI18NService().getCurrentLocale().getLanguage());
		Preconditions.checkNotNull(kbContainer, "We were not able to create master data KB container");
		final CPSQuantity quantity = source.getQuantity();
		if (quantity != null)
		{
			final String unit = quantity.getUnit();
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Writing to KB cache: " + kbId + ", " + unit);
			}
			kbContainer.setRootUnitOfMeasure(unit);
		}
	}

	protected void populateCoreAttributes(final CPSItem source, final InstanceModel target)
	{
		target.setId(source.getId());
		target.setPosition(source.getBomPosition());
		target.setName(source.getKey());
		target.setComplete(source.isComplete());
		target.setConsistent(source.isConsistent());
	}


	protected void populateSubItems(final CPSItem source, final InstanceModel target)
	{
		if (source.getSubItems() != null)
		{
			for (final CPSItem subitem : source.getSubItems())
			{
				final InstanceModel subInstance = instanceModelConverter.convert(subitem);
				target.getSubInstances().add(subInstance);
			}
		}
	}

	protected void populateGroups(final CPSItem source, final InstanceModel target)
	{
		for (final CPSCharacteristicGroup characteristicGroup : source.getCharacteristicGroups())
		{
			final String groupId = characteristicGroup.getId();

			// ignore characteristic groups with non-existing id
			if (groupId == null)
			{
				return;
			}

			final CsticGroupModel characteristicGroupModel = characteristicGroupConverter.convert(characteristicGroup);

			if (groupId.equalsIgnoreCase(SapproductconfigruntimecpsConstants.CPS_GENERAL_GROUP_ID))
			{
				target.getCsticGroups().add(0, characteristicGroupModel);
			}
			else
			{
				target.getCsticGroups().add(characteristicGroupModel);
			}
		}
	}

	protected void populateCstics(final CPSItem source, final InstanceModel target)
	{
		if (source.getCharacteristics() != null)
		{
			for (final CPSCharacteristic characteristic : source.getCharacteristics())
			{
				final CsticModel characteristicModel = characteristicConverter.convert(characteristic);
				target.addCstic(characteristicModel);
			}
		}
	}

	/**
	 * @param masterDataCacheAccessService
	 */
	public void setMasterDataCacheAccessService(final MasterDataCacheAccessService masterDataCacheAccessService)
	{
		this.masterDataCacheAccessService = masterDataCacheAccessService;
	}

	public void setI18NService(final I18NService i18NService)
	{
		this.i18NService = i18NService;

	}

}
