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

import com.sap.custdev.projects.fbs.slc.dataloader.settings.IEccSetting;


/**
 * Default implementation of {@link IEccSetting}<br>
 * Immutable connection settings for initiaiting a dataload from a certain SAP backend.
 *
 * @deprecated since 6.6 - not required/used any more
 */
@SuppressWarnings("squid:S1133")
@Deprecated
public class EccSetting implements IEccSetting
{
	private final boolean loadBalanced;
	private final String sid;
	private final String messageServer;
	private final String group;

	/**
	 * Optional Constructor. Does not require a load balacing group. Usefull, when a concetion to a fixed host is
	 * required.
	 *
	 * @param loadBalanced
	 *           <code>true</code>, only if the backend uses a load balancer
	 * @param instance
	 *           instance number
	 * @param targetHost
	 *           target server host name
	 */
	public EccSetting(final boolean loadBalanced, final String instance, final String targetHost)
	{
		this(loadBalanced, instance, targetHost, null);
	}

	/**
	 * Default Constructor. Should be used when a connection via load balancer is desired.
	 *
	 * @param loadBalanced
	 *           <code>true</code>, only if the backend uses a load balancer
	 * @param sid
	 *           SAP System Id
	 * @param messageServer
	 *           message server host name
	 * @param group
	 *           load balancing group
	 */
	public EccSetting(final boolean loadBalanced, final String sid, final String messageServer, final String group)
	{
		super();
		this.loadBalanced = loadBalanced;
		this.sid = sid;
		this.messageServer = messageServer;
		this.group = group;
	}

	@Override
	public boolean isLoadBalanced()
	{
		return loadBalanced;
	}

	@Override
	public String getSid()
	{
		return sid;
	}

	@Override
	public String getMessageServer()
	{
		return messageServer;
	}

	@Override
	public String getGroup()
	{
		return group;
	}
}
