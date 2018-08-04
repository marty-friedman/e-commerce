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
package de.hybris.platform.sap.productconfig.facades.impl;

import de.hybris.platform.sap.productconfig.facades.SessionAccessFacade;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;


/**
 * Default implementation of {@link SessionAccessFacade}
 */
public class SessionAccessFacadeImpl implements SessionAccessFacade
{

	private SessionAccessService sessionAccessService;


	/**
	 * @param sessionAccessService
	 *           injects the underlying session access service
	 */
	public void setSessionAccessService(final SessionAccessService sessionAccessService)
	{
		this.sessionAccessService = sessionAccessService;
	}


	protected SessionAccessService getSessionAccessService()
	{
		return this.sessionAccessService;
	}


	@Override
	public void setConfigIdForCartEntry(final String cartEntryKey, final String configId)
	{
		sessionAccessService.setConfigIdForCartEntry(cartEntryKey, configId);

	}


	@Override
	public String getConfigIdForCartEntry(final String cartEntryKey)
	{
		return sessionAccessService.getConfigIdForCartEntry(cartEntryKey);
	}

	@Override
	public <T> T getUiStatusForCartEntry(final String cartEntryKey)
	{
		return sessionAccessService.getUiStatusForCartEntry(cartEntryKey);
	}

	@Override
	public void setUiStatusForCartEntry(final String cartEntryKey, final Object uiStatus)
	{
		sessionAccessService.setUiStatusForCartEntry(cartEntryKey, uiStatus);

	}


	@Override
	public void setUiStatusForProduct(final String productKey, final Object uiStatus)
	{
		sessionAccessService.setUiStatusForProduct(productKey, uiStatus);

	}


	@Override
	public <T> T getUiStatusForProduct(final String productKey)
	{
		return sessionAccessService.getUiStatusForProduct(productKey);
	}


	@Override
	public void removeUiStatusForCartEntry(final String cartEntryKey)
	{
		sessionAccessService.removeUiStatusForCartEntry(cartEntryKey);

	}


	@Override
	public void removeUiStatusForProduct(final String productKey)
	{
		sessionAccessService.removeUiStatusForProduct(productKey);
	}


	@Override
	public String getCartEntryForConfigId(final String configId)
	{
		return sessionAccessService.getCartEntryForConfigId(configId);
	}


	@Override
	public void setCartEntryForProduct(final String productKey, final String cartEntryKey)
	{
		sessionAccessService.setCartEntryForProduct(productKey, cartEntryKey);

	}


	@Override
	public String getCartEntryForProduct(final String productKey)
	{
		return sessionAccessService.getCartEntryForProduct(productKey);
	}


	@Override
	public void removeCartEntryForProduct(final String productKey)
	{
		sessionAccessService.removeCartEntryForProduct(productKey);

	}


	@Override
	public void removeConfigIdForCartEntry(final String cartEntryKey)
	{
		sessionAccessService.removeConfigIdForCartEntry(cartEntryKey);
	}

	@Override
	public String getSessionId()
	{
		return sessionAccessService.getSessionId();
	}

	@Override
	public ConfigModel getConfigurationModelEngineState(final String configId)
	{
		return sessionAccessService.getConfigurationModelEngineState(configId);
	}

}
