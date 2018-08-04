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
package de.hybris.platform.sap.productconfig.runtime.cps.cache.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.regioncache.CacheValueLoadException;
import de.hybris.platform.regioncache.key.CacheKey;
import de.hybris.platform.sap.productconfig.runtime.cps.client.KbDeterminationClient;
import de.hybris.platform.sap.productconfig.runtime.cps.client.KbDeterminationClientBase;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.common.CPSMasterDataKBHeaderInfo;
import de.hybris.platform.scripting.engine.internal.cache.impl.SimpleScriptCacheKey;
import de.hybris.platform.yaasconfiguration.service.YaasServiceFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.hybris.charon.exp.HttpException;

import rx.Observable;


@SuppressWarnings("javadoc")
@UnitTest
public class KnowledgeBaseHeadersCacheValueLoaderTest
{
	private static final String CPS_SERVICE_TENANT = "cps service tenant";
	private static final String CPS_SERVICE_URL = "cps service url";
	private static final String TENANT_ID = "tenantId";
	private static final String PRODUCT = "product";
	private KnowledgeBaseHeadersCacheValueLoader classUnderTest;
	private CacheKey paramCacheKey;
	private List<CPSMasterDataKBHeaderInfo> kbHeaders;
	@Mock
	private KbDeterminationClient client;
	@Mock
	private YaasServiceFactory yaasServiceFactory;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new KnowledgeBaseHeadersCacheValueLoader();
		classUnderTest.setClient(client);
		Mockito.when(yaasServiceFactory.lookupService(KbDeterminationClient.class)).thenReturn(client);
		classUnderTest.setYaasServiceFactory(yaasServiceFactory);
		paramCacheKey = new KnowledgeBaseHeadersCacheKey(PRODUCT, TENANT_ID, CPS_SERVICE_URL, CPS_SERVICE_TENANT);
		kbHeaders = new ArrayList<>();
	}

	@Test
	public void testLoadNotNull()
	{
		final Observable<List<CPSMasterDataKBHeaderInfo>> kbObservable = Observable.from(Arrays.asList(kbHeaders));
		Mockito.when(client.getKnowledgebases(PRODUCT)).thenReturn(kbObservable);
		final List<CPSMasterDataKBHeaderInfo> result = classUnderTest.load(paramCacheKey);
		Mockito.verify(client).getKnowledgebases(PRODUCT);
		assertNotNull(result);
		assertEquals(kbHeaders, result);
	}

	@Test(expected = CacheValueLoadException.class)
	public void testInvalidCacheKeyClass()
	{
		paramCacheKey = new SimpleScriptCacheKey("protocol", "path", TENANT_ID);
		classUnderTest.load(paramCacheKey);
	}

	@Test
	public void testGetClientCreated()
	{
		classUnderTest.setClient(null);
		final KbDeterminationClientBase result = classUnderTest.getClient();
		assertNotNull(result);
	}

	@Test
	public void testGetClientExisting()
	{
		final KbDeterminationClientBase result = classUnderTest.getClient();
		assertEquals(client, result);
	}

	@Test
	public void testGetKbHeadersFromService()
	{
		final Observable<List<CPSMasterDataKBHeaderInfo>> kbObservable = Observable.from(Arrays.asList(kbHeaders));
		Mockito.when(client.getKnowledgebases(PRODUCT)).thenReturn(kbObservable);
		classUnderTest.getKbHeadersFromService(PRODUCT);
		Mockito.verify(client).getKnowledgebases(PRODUCT);
	}

	@Test
	public void testGetKbHeadersFromServiceException()
	{
		final HttpException httpEx = new HttpException(Integer.valueOf(666), "Evil exception");
		Mockito.when(client.getKnowledgebases(PRODUCT)).thenThrow(httpEx);
		try
		{
			classUnderTest.getKbHeadersFromService(PRODUCT);
		}
		catch (final CacheValueLoadException ex)
		{
			assertEquals(httpEx, ex.getCause());
		}
	}
}
