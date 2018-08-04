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
package de.hybris.platform.sap.sapcpicustomerexchange.service.impl;

import de.hybris.platform.sap.sapcpiadapter.service.SapCpiOAuthService;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiCustomer;
import de.hybris.platform.sap.sapcpiadapter.clients.SapCpiCustomerClient;
import de.hybris.platform.sap.sapcpicustomerexchange.service.SapCpiCustomerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import rx.Completable;


public class DefaultSapCpiCustomerService implements SapCpiCustomerService
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSapCpiCustomerService.class);

	private SapCpiCustomerClient client;
	private SapCpiOAuthService authService;





	/**
	 *
	 */
	public DefaultSapCpiCustomerService()
	{
		super();
	}





	/**
	 *
	 */
	public DefaultSapCpiCustomerService(final SapCpiCustomerClient client, final SapCpiOAuthService authService)
	{
		super();
		this.client = client;
		this.authService = authService;
	}





	/**
	 * @return the client
	 */
	public SapCpiCustomerClient getClient()
	{
		return client;
	}





	/**
	 * @param client
	 *           the client to set
	 */
	@Required
	public void setClient(final SapCpiCustomerClient client)
	{
		this.client = client;
	}





	/**
	 * @return the authService
	 */
	public SapCpiOAuthService getAuthService()
	{
		return authService;
	}





	/**
	 * @param authService
	 *           the authService to set
	 */
	@Required
	public void setAuthService(final SapCpiOAuthService authService)
	{
		this.authService = authService;
	}





	@Override
	public Completable createCustomer(final SapCpiCustomer customer)
	{
		return authService.getToken().flatMap(token -> {
			final String auth = "Bearer " + token;

			return client.createCustomer(auth, customer);
		}).toCompletable();
	}
}
