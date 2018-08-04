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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.cache.CPSSessionCache;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.NewCookie;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("javadoc")
@UnitTest
public class CookieHandlerImplTest
{
	CookieHandlerImpl classUnderTest = new CookieHandlerImpl();
	List<NewCookie> cookies = new ArrayList<>();

	private static final String configId = "cfgId";
	private static final String sessionCookieName = "JSESSIONID";
	private static final String sessionCookieValue = "928ABE7FA";
	private static final String cfCookieName = "__VCAP__";
	private static final String cfCookieValue = "98A5CDE6542";

	@Mock
	NewCookie sessionCookie;
	@Mock
	NewCookie cfCookie;

	@Mock
	CPSSessionCache cpsSessionCache;
	private List<String> cookieList = new ArrayList<>();


	@Before
	public void initialize()
	{
		MockitoAnnotations.initMocks(this);
		Mockito.when(sessionCookie.getName()).thenReturn(sessionCookieName);
		Mockito.when(sessionCookie.getValue()).thenReturn(sessionCookieValue);
		cookies.add(sessionCookie);
		Mockito.when(cfCookie.getName()).thenReturn(cfCookieName);
		Mockito.when(cfCookie.getValue()).thenReturn(cfCookieValue);

		cookies.add(cfCookie);
		cookieList = classUnderTest.convertToStringArray(cookies);
		Mockito.when(cpsSessionCache.getCookies(configId)).thenReturn(cookieList);
		classUnderTest.setCPSSessionCache(cpsSessionCache);
		classUnderTest.setCookies(configId, cookies);
	}


	public void testGetCookiesNotPresent()
	{
		assertNull(classUnderTest.getCookiesAsString("NOT_EXISTING"));
	}

	@Test
	public void testGetCookies()
	{
		final List<String> cookieList = classUnderTest.getCookiesAsString(configId);
		assertNotNull(cookieList);
		assertEquals(2, cookieList.size());
	}

	@Test
	public void testGetCookiesAsString()
	{
		final List<String> cookieListAsString = classUnderTest.getCookiesAsString(configId);
		assertNotNull(cookieListAsString);
		assertEquals(2, cookieListAsString.size());
	}

	@Test
	public void testConvertToStringArray()
	{
		final List<String> cookiesAsString = classUnderTest.convertToStringArray(cookies);
		assertNotNull(cookiesAsString);
		assertEquals(2, cookiesAsString.size());
	}

	@Test
	public void testCookie2String()
	{
		assertEquals(sessionCookieName + "=" + sessionCookieValue, classUnderTest.cookie2String(sessionCookie));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetCookiesNull()
	{
		classUnderTest.setCookies(configId, null);
	}


	@Test
	public void testRemoveCookies()
	{
		classUnderTest.removeCookies(configId);
		Mockito.verify(cpsSessionCache).removeCookies(configId);
	}

	@Test
	public void testCPSSessionCache()
	{

		assertEquals(cpsSessionCache, classUnderTest.getCPSSessionCache());

	}



}
