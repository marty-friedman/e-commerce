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
package de.hybris.platform.sap.productconfig.runtime.cps.session.impl;

import de.hybris.platform.sap.productconfig.runtime.cps.cache.CPSSessionCache;
import de.hybris.platform.sap.productconfig.runtime.cps.session.CookieHandler;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.NewCookie;


/**
 * Default implementation of {@link CookieHandler}
 */
public class CookieHandlerImpl implements CookieHandler
{

	private CPSSessionCache cPSSessionCache;

	@Override
	public void setCookies(final String configId, final List<NewCookie> cookies)
	{
		if (cookies == null)
		{
			throw new IllegalArgumentException("We expect cookies at this point");
		}
		cPSSessionCache.setCookies(configId, convertToStringArray(cookies));
	}


	@Override
	public List<String> getCookiesAsString(final String configId)
	{
		final List<String> cookies = cPSSessionCache.getCookies(configId);
		if (cookies == null)
		{
			throw new IllegalStateException("Cookies not found for configuration id: " + configId);
		}
		return cookies;
	}

	/**
	 * @param cookies
	 * @return List of cookies as string, name and value separated by '='
	 */
	public List<String> convertToStringArray(final List<NewCookie> cookies)
	{
		final List<String> cookiesAsString = new ArrayList<>();
		cookies.stream().forEach(cookie -> cookiesAsString.add(cookie2String(cookie)));
		return cookiesAsString;
	}


	protected String cookie2String(final NewCookie cookie)
	{
		final StringBuilder cookieAsString = new StringBuilder(cookie.getName());
		return cookieAsString.append("=").append(cookie.getValue()).toString();
	}


	@Override
	public void removeCookies(final String configId)
	{
		cPSSessionCache.removeCookies(configId);

	}


	@Override
	public void setCookiesAsString(final String newConfigId, final List<String> cookieList)
	{
		cPSSessionCache.setCookies(newConfigId, cookieList);
	}





	/**
	 * @param cpsSessionCache
	 */
	public void setCPSSessionCache(final CPSSessionCache cpsSessionCache)
	{
		this.cPSSessionCache = cpsSessionCache;
	}


	protected CPSSessionCache getCPSSessionCache()
	{
		return cPSSessionCache;
	}


}
