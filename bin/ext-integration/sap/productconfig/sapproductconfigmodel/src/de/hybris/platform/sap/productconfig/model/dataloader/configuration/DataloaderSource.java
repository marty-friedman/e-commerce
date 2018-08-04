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

import com.sap.custdev.projects.fbs.slc.dataloader.settings.IClientSetting;
import com.sap.custdev.projects.fbs.slc.dataloader.settings.IDataloaderSource;
import com.sap.custdev.projects.fbs.slc.dataloader.settings.IEccSetting;


/**
 * Default implementation of {@link IDataloaderSource}. This object is immutable.
 *
 ** @deprecated since 6.6 - not required/used any more
 */
@SuppressWarnings("squid:S1133")
@Deprecated
public class DataloaderSource implements IDataloaderSource
{
	private final String rfcDestination;
	private final ClientSettings clientSetting;
	private final EccSetting eccSetting;
	private final String outboundDestinationName;

	/**
	 * Default Constructor
	 *
	 * @param params
	 *           parameters
	 */
	public DataloaderSource(final DataloaderSourceParameters params)
	{
		this.clientSetting = new ClientSettings(params.getClient(), params.getUser(), params.getPassword());
		if (params.isUseLoadBalance())
		{
			this.eccSetting = new EccSetting(params.isUseLoadBalance(), params.getSysId(), params.getMsgServer(),
					params.getLogonGroup());
		}
		else
		{
			this.eccSetting = new EccSetting(params.isUseLoadBalance(), params.getInstanceno(), params.getTargetHost());
		}
		this.rfcDestination = params.getClientRfcDestination();
		this.outboundDestinationName = params.getServerRfcDestination();
	}


	@Override
	public IClientSetting getClientSetting()
	{
		return clientSetting;
	}

	@Override
	public IEccSetting getEccSetting()
	{
		return eccSetting;
	}

	@Override
	public String getRfcDestination()
	{
		return rfcDestination;
	}

	/**
	 * @return outbound destination name
	 */
	public String getOutboundDestinationName()
	{
		return outboundDestinationName;
	}
}
