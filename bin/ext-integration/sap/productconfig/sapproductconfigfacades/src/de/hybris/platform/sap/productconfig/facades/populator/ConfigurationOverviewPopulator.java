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
package de.hybris.platform.sap.productconfig.facades.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.sap.productconfig.facades.ConfigOverviewFilter;
import de.hybris.platform.sap.productconfig.facades.filters.OverviewFilterList;
import de.hybris.platform.sap.productconfig.facades.overview.CharacteristicGroup;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;
import de.hybris.platform.sap.productconfig.facades.overview.FilterEnum;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ClassificationSystemCPQAttributesContainer;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Populates the data as required by the configuration overview page, using the runtime configuration as source. So the
 * {@link ConfigModel} is transformed into a {@link ConfigurationOverviewData} object.<br>
 * Will use the {@link ConfigurationOverviewInstancePopulator} to handle individual instances.
 *
 */
public class ConfigurationOverviewPopulator implements Populator<ConfigModel, ConfigurationOverviewData>
{

	private SessionAccessService sessionAccessService;

	private ConfigurationOverviewInstancePopulator configurationOverviewInstancePopulator;
	private OverviewFilterList overviewFilterList;

	/**
	 * @return the overviewFilterList
	 */
	public OverviewFilterList getOverviewFilterList()
	{
		return overviewFilterList;
	}

	/**
	 * @param overviewFilterList
	 *           the overviewFilterList to set
	 */
	public void setOverviewFilterList(final OverviewFilterList overviewFilterList)
	{
		this.overviewFilterList = overviewFilterList;
	}

	/**
	 * @return the configurationOverviewInstancePopulator
	 */
	public ConfigurationOverviewInstancePopulator getConfigurationOverviewInstancePopulator()
	{
		return configurationOverviewInstancePopulator;
	}

	/**
	 * @param configurationOverviewInstancePopulator
	 *           the configurationOverviewInstancePopulator to set
	 */
	public void setConfigurationOverviewInstancePopulator(
			final ConfigurationOverviewInstancePopulator configurationOverviewInstancePopulator)
	{
		this.configurationOverviewInstancePopulator = configurationOverviewInstancePopulator;
	}

	@Override
	public void populate(final ConfigModel source, final ConfigurationOverviewData target)
	{

		final List<CharacteristicGroup> groups = new ArrayList<>();
		final Map<String, ClassificationSystemCPQAttributesContainer> nameMap = getSessionAccessService().getCachedNameMap();
		final Collection<Map> options = fillOptions(target, nameMap);

		target.setId(source.getId());
		getConfigurationOverviewInstancePopulator().populate(source.getRootInstance(), groups, options);
		target.setGroups(groups);
	}

	protected Collection<Map> fillOptions(final ConfigurationOverviewData target,
			final Map<String, ClassificationSystemCPQAttributesContainer> nameMap)
	{
		final Collection<Map> options = new ArrayList<Map>();
		final HashMap<String, Object> optionsMap = new HashMap<String, Object>();
		options.add(optionsMap);

		List<FilterEnum> filterIds = target.getAppliedCsticFilters();
		if (filterIds == null)
		{
			filterIds = new ArrayList<FilterEnum>();
		}
		final List<ConfigOverviewFilter> filters = getOverviewFilterList().getAppliedFilters(filterIds);
		optionsMap.put(ConfigurationOverviewInstancePopulator.APPLIED_CSTIC_FILTERS, filters);
		optionsMap.put(ConfigurationOverviewInstancePopulator.HYBRIS_NAME_MAP, nameMap);

		Set<String> filteredOutGroups = target.getAppliedGroupFilters();
		if (filteredOutGroups == null)
		{
			filteredOutGroups = new HashSet<>();
		}
		optionsMap.put(ConfigurationOverviewInstancePopulator.APPLIED_GROUP_FILTERS, filteredOutGroups);
		return options;
	}


	/**
	 * @return the session access service
	 */
	protected SessionAccessService getSessionAccessService()
	{
		return sessionAccessService;
	}

	/**
	 * @param sessionAccessService
	 *           session access service
	 */
	public void setSessionAccessService(final SessionAccessService sessionAccessService)
	{
		this.sessionAccessService = sessionAccessService;
	}

}
