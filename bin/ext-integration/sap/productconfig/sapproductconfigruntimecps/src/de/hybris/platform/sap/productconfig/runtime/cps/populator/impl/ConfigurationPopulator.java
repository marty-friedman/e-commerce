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
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.ConfigurationMasterDataService;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConflict;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSItem;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.SolvableConflictModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.util.localization.Localization;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 *
 */
public class ConfigurationPopulator implements Populator<CPSConfiguration, ConfigModel>
{

	static final String SAPPRODUCTCONFIG_CPS_HEADER_CONFLICT_MESSAGE = "sapproductconfig.cps.header.conflict.message";

	private Converter<CPSItem, InstanceModel> instanceModelConverter;
	private Converter<CPSConflict, SolvableConflictModel> conflictModelConverter;

	private ConfigurationMasterDataService masterDataService;

	protected Converter<CPSItem, InstanceModel> getInstanceModelConverter()
	{
		return instanceModelConverter;
	}

	protected Converter<CPSConflict, SolvableConflictModel> getConflictModelConverter()
	{
		return conflictModelConverter;
	}

	/**
	 * @param instanceModelConverter
	 *           the instanceModelConverter to set
	 */
	@Required
	public void setInstanceModelConverter(final Converter<CPSItem, InstanceModel> instanceModelConverter)
	{
		this.instanceModelConverter = instanceModelConverter;
	}

	/**
	 * @param conflictModelConverter
	 *           the conflictModelConverter to set
	 */
	@Required
	public void setConflictModelConverter(final Converter<CPSConflict, SolvableConflictModel> conflictModelConverter)
	{
		this.conflictModelConverter = conflictModelConverter;
	}

	@Override
	public void populate(final CPSConfiguration source, final ConfigModel target)
	{
		populateCoreAttributes(source, target);
		populateRootItem(source, target);
		populateConflicts(source, target);
	}

	protected void populateConflicts(final CPSConfiguration source, final ConfigModel target)
	{
		if (CollectionUtils.isEmpty(source.getConflicts()))
		{
			return;
		}

		final List<SolvableConflictModel> solvableConflicts = new ArrayList<>();
		for (final CPSConflict conflict : source.getConflicts())
		{
			final SolvableConflictModel solvableConflict = getConflictModelConverter().convert(conflict);
			solvableConflicts.add(solvableConflict);
		}

		target.setSolvableConflicts(solvableConflicts);
	}

	protected void populateRootItem(final CPSConfiguration source, final ConfigModel target)
	{
		final CPSItem rootItem = source.getRootItem();
		target.setRootInstance(instanceModelConverter.convert(rootItem));
	}

	protected void populateCoreAttributes(final CPSConfiguration source, final ConfigModel target)
	{
		target.setComplete(source.isComplete());
		target.setConsistent(source.isConsistent());
		target.setId(source.getId());
		target.setKbId(source.getKbId());
		target.setName(source.getProductKey());

		final boolean multilevel = getMasterDataService().isProductMultilevel(source.getKbId(), source.getProductKey());
		target.setSingleLevel(!multilevel);
	}

	protected String callLocalization(final String key)
	{
		return Localization.getLocalizedString(key);
	}

	protected ConfigurationMasterDataService getMasterDataService()
	{
		return masterDataService;
	}

	/**
	 * @param masterDataService
	 *           the masterDataService to set
	 */
	@Required
	public void setMasterDataService(final ConfigurationMasterDataService masterDataService)
	{
		this.masterDataService = masterDataService;
	}

}
