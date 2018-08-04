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
import static org.mockito.Mockito.doReturn;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.sap.productconfig.runtime.cps.client.KbDeterminationClient;
import de.hybris.platform.sap.productconfig.runtime.cps.client.MasterDataClient;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.yaasconfiguration.model.BaseSiteServiceMappingModel;
import de.hybris.platform.yaasconfiguration.model.YaasClientCredentialModel;
import de.hybris.platform.yaasconfiguration.model.YaasProjectModel;
import de.hybris.platform.yaasconfiguration.model.YaasServiceModel;
import de.hybris.platform.yaasconfiguration.service.YaasConfigurationService;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("javadoc")
@UnitTest
public class CPSCacheKeyGeneratorImplTest
{
	private static final String PRODUCT = "product";
	private static final String BASE_SITE_UID = "base site uid";
	private static final String SERVICE_ID = "service id";
	private static final String CPS_SERVICE_TENANT = "cps service tenant";
	private static final String CPS_SERVICE_URL = "cps service url";
	private static final String TENANT_ID = "tenant id";
	private static final String LANGUAGE = "language";
	private static final String KB_ID = "kb id";
	private CPSCacheKeyGeneratorImpl classUnderTest;

	@Mock
	private BaseSiteService baseSiteService;
	@Mock
	private BaseSiteModel baseSiteModel;
	@Mock
	private YaasConfigurationService yaasConfigurationService;
	@Mock
	private YaasServiceModel serviceModel;
	@Mock
	private BaseSiteServiceMappingModel mappingModel;
	@Mock
	private YaasClientCredentialModel credentialModel;
	@Mock
	private YaasProjectModel projectModel;


	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = Mockito.spy(new CPSCacheKeyGeneratorImpl());
		classUnderTest.setYaasConfigurationService(yaasConfigurationService);
		classUnderTest.setBaseSiteService(baseSiteService);
		doReturn(TENANT_ID).when(classUnderTest).getTenantId();
		Mockito.when(yaasConfigurationService.getYaasServiceForId(SERVICE_ID)).thenReturn(serviceModel);
		Mockito.when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSiteModel);
		Mockito.when(baseSiteModel.getUid()).thenReturn(BASE_SITE_UID);
		Mockito.when(yaasConfigurationService.getBaseSiteServiceMappingForId(BASE_SITE_UID, serviceModel)).thenReturn(mappingModel);
		Mockito.when(mappingModel.getYaasClientCredential()).thenReturn(credentialModel);
		Mockito.when(credentialModel.getYaasProject()).thenReturn(projectModel);
		Mockito.when(projectModel.getIdentifier()).thenReturn(CPS_SERVICE_TENANT);
		Mockito.when(serviceModel.getServiceURL()).thenReturn(CPS_SERVICE_URL);
	}

	@Test
	public void testCreateMasterDataCacheKey()
	{
		Mockito.when(yaasConfigurationService.getYaasServiceForId(MasterDataClient.class.getSimpleName())).thenReturn(serviceModel);
		final MasterDataCacheKey result = classUnderTest.createMasterDataCacheKey(KB_ID, LANGUAGE);
		assertNotNull(result);
		assertEquals(KB_ID, result.getKbId());
		assertEquals(LANGUAGE, result.getLang());
		assertEquals(TENANT_ID, result.getTenantId());
		assertEquals(CPS_SERVICE_URL, result.getCpsServiceUrl());
		assertEquals(CPS_SERVICE_TENANT, result.getCpsServiceTenant());
	}

	@Test
	public void testGetCPSServiceParameter()
	{
		final Pair<String, String> result = classUnderTest.getCPSServiceParameter(SERVICE_ID);
		assertEquals(CPS_SERVICE_URL, result.getLeft());
		assertEquals(CPS_SERVICE_TENANT, result.getRight());
	}

	@Test
	public void testCreateKnowledgeBaseHeadersCacheKey()
	{
		Mockito.when(yaasConfigurationService.getYaasServiceForId(KbDeterminationClient.class.getSimpleName()))
				.thenReturn(serviceModel);
		final KnowledgeBaseHeadersCacheKey result = classUnderTest.createKnowledgeBaseHeadersCacheKey(PRODUCT);
		assertNotNull(result);
		assertEquals(PRODUCT, result.getProduct());
		assertEquals(TENANT_ID, result.getTenantId());
		assertEquals(CPS_SERVICE_URL, result.getCpsServiceUrl());
		assertEquals(CPS_SERVICE_TENANT, result.getCpsServiceTenant());
	}
}
