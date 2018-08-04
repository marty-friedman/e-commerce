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

import com.sap.custdev.projects.fbs.slc.dataloader.settings.IDataloaderConfiguration;
import com.sap.custdev.projects.fbs.slc.dataloader.settings.IDataloaderSource;
import com.sap.custdev.projects.fbs.slc.dataloader.settings.IDataloaderTarget;
import com.sap.custdev.projects.fbs.slc.dataloader.settings.IInitialDownloadConfiguration;


/**
 * Default implementation of the {@link IDataloaderConfiguration}.
 *
 * @deprecated since 6.5 - not required/used any more
 */
@SuppressWarnings("squid:S1133")
@Deprecated
public class DataloaderConfiguration implements IDataloaderConfiguration
{

	private IInitialDownloadConfiguration initialDownloadConfiguration;
	private IDataloaderSource source;
	private IDataloaderTarget target;

	/**
	 * @param initialDownloadConfiguration
	 *           datalod config
	 */
	public void setInitialDownloadConfiguration(final IInitialDownloadConfiguration initialDownloadConfiguration)
	{
		this.initialDownloadConfiguration = initialDownloadConfiguration;
	}

	@Override
	public IInitialDownloadConfiguration getInitialDownloadConfiguration()
	{
		return initialDownloadConfiguration;
	}


	/**
	 * @param source
	 *           dataload source
	 */
	public void setSource(final IDataloaderSource source)
	{
		this.source = source;
	}

	@Override
	public IDataloaderSource getSource()
	{
		return source;
	}


	/**
	 * @param target
	 *           datalod traget
	 */
	public void setTarget(final IDataloaderTarget target)
	{
		this.target = target;
	}

	@Override
	public IDataloaderTarget getTarget()
	{
		return target;
	}
}
