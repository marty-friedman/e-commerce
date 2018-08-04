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
package de.hybris.platform.sap.productconfig.model.dataloader.configuration;

import com.sap.custdev.projects.fbs.slc.dataloader.settings.IInitialDownloadConfiguration;


/**
 * Default implementation of {@link IInitialDownloadConfiguration}. This Object is immutable.
 *
 * @deprecated since 6.6 - not required/used any more
 */
@SuppressWarnings("squid:S1133")
@Deprecated
public class InitialDownloadConfiguration implements IInitialDownloadConfiguration
{

	private final String kbFilterFile;
	private final String materialsFilterFile;
	private final String conditionsFilterFile;

	/**
	 * Default Constructor
	 *
	 * @param kbFilterFile
	 *           knowledge base filter
	 * @param materialsFilterFile
	 *           material filter
	 * @param conditionsFilterFile
	 *           conditions filter
	 */
	public InitialDownloadConfiguration(final String kbFilterFile, final String materialsFilterFile,
			final String conditionsFilterFile)
	{
		this.kbFilterFile = kbFilterFile;
		this.materialsFilterFile = materialsFilterFile;
		this.conditionsFilterFile = conditionsFilterFile;
	}

	@Override
	public String getConditionsFilterFile()
	{
		return conditionsFilterFile;
	}

	@Override
	public String getMaterialsFilterFile()
	{
		return materialsFilterFile;
	}

	@Override
	public String getSceFilterFile()
	{
		return kbFilterFile;
	}

	@Override
	public boolean isConditionsFilterEnabled()
	{
		return conditionsFilterFile != null;
	}

	@Override
	public boolean isMaterialsFilterEnabled()
	{
		return materialsFilterFile != null;
	}

	@Override
	public boolean isSceFilterEnabled()
	{
		return kbFilterFile != null;
	}

	@Override
	public boolean downloadConditionData()
	{
		return true;
	}

	@Override
	public boolean downloadCustomizingData()
	{
		return true;
	}

	@Override
	public boolean downloadDictionaryData()
	{
		return true;
	}

	@Override
	public boolean downloadMaterialData()
	{
		return true;
	}

	@Override
	public boolean downloadSceData()
	{
		return true;
	}

}
