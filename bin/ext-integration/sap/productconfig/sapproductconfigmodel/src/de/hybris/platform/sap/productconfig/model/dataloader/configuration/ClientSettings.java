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


/**
 * Default implementation of {@link IClientSetting}<br>
 * Immutable connection settings for initiaiting a dataload from a certain SAP backend client.
 *
 * @deprecated since 6.6 - not required/used any more
 */
@SuppressWarnings("squid:S1133")
@Deprecated
public class ClientSettings implements IClientSetting
{
	private final String client;
	private final String password;
	private final String user;

	/**
	 * Default constructor
	 *
	 * @param client
	 *           SAP client
	 * @param user
	 *           user
	 * @param password
	 *           password
	 */
	public ClientSettings(final String client, final String user, final String password)
	{
		this.client = client;
		this.password = password;
		this.user = user;
	}

	@Override
	public String getClient()
	{
		return client;
	}

	@Override
	public String getPassword()
	{
		return password;
	}

	@Override
	public String getUser()
	{
		return user;
	}

}
