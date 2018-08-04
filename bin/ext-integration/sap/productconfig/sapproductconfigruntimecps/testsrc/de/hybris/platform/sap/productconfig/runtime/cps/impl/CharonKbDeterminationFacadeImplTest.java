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
package de.hybris.platform.sap.productconfig.runtime.cps.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.cache.KnowledgeBaseHeadersCacheAccessService;
import de.hybris.platform.sap.productconfig.runtime.cps.client.KbDeterminationClient;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.common.CPSMasterDataKBHeaderInfo;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.common.CPSMasterDataKnowledgebaseKey;
import de.hybris.platform.sap.productconfig.runtime.cps.strategy.CommerceExternalConfigurationStrategy;
import de.hybris.platform.sap.productconfig.runtime.cps.strategy.impl.CommerceExternalConfigurationStrategyImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("javadoc")
@UnitTest
public class CharonKbDeterminationFacadeImplTest
{
	private static final Date kbDate = new Date(1500000000000L);
	private static final String kbName = "KBNAME";
	private static final String externalCfg = "{\"externalConfiguration\":{\"kbId\": \"4030\", \"kbKey\": {\"logsys\": \"IPCSELTEST\", \"name\": \""
			+ kbName + "\", \"version\": \"WEC\"  }},\"unitCodes\":{\"PCE\":\"PCE\"}}";
	private static final String externalCfgMatching = "{\"externalConfiguration\":{\"kbId\": \"234\", \"kbKey\": {\"logsys\": \"LOGSYS\", \"name\": \"KBNAME\", \"version\": \"1.0\"  }},\"unitCodes\":{\"PCE\":\"PCE\"}}";

	private static final String logsys = "LOGSYS";
	private static final String version = "1.0";
	private static final String productcode = "PRODUCT";
	private static final Integer KbId = Integer.valueOf(234);
	CharonKbDeterminationFacadeImpl classUnderTest = new CharonKbDeterminationFacadeImpl();
	private List<CPSMasterDataKBHeaderInfo> kbList;
	private CPSExternalConfiguration externalConfigStructured;
	private final CPSMasterDataKnowledgebaseKey kbKey = new CPSMasterDataKnowledgebaseKey();
	private final CPSMasterDataKnowledgebaseKey key = new CPSMasterDataKnowledgebaseKey();
	private final CommerceExternalConfigurationStrategy commerceExternalConfigurationStrategy = new CommerceExternalConfigurationStrategyImpl();


	@Mock
	private KbDeterminationClient client;

	private CPSMasterDataKBHeaderInfo kb;

	@Mock
	private KnowledgeBaseHeadersCacheAccessService knowledgeBasesCacheAccessService;


	@Before
	public void initialize()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest.setKnowledgeBasesCacheAccessService(knowledgeBasesCacheAccessService);
		kbList = new ArrayList<>();
		kb = new CPSMasterDataKBHeaderInfo();
		kb.setKey(key);
		kb.setId(KbId);
		key.setLogsys(logsys);
		key.setName(kbName);
		key.setVersion(version);

		kbList.add(kb);
		Mockito.when(knowledgeBasesCacheAccessService.getKnowledgeBases(productcode)).thenReturn(kbList);
		externalConfigStructured = new CPSExternalConfiguration();
		externalConfigStructured.setKbKey(kbKey);
		kbKey.setLogsys(logsys);
		kbKey.setName(kbName);
		kbKey.setVersion(version);
		classUnderTest.setCommerceExternalConfigurationStrategy(commerceExternalConfigurationStrategy);

	}

	@Test
	public void testConvertToString()
	{
		final String dateAsString = classUnderTest.convertToString(kbDate);
		assertEquals("2017-07-14", dateAsString);
	}

	@Test
	public void testObjectMapper()
	{
		assertNotNull(classUnderTest.getObjectMapper());
	}

	@Test
	public void testParseFromJSON()
	{
		final CPSExternalConfiguration parseFromJSON = classUnderTest.parseFromJSON(externalCfg);
		assertNotNull(parseFromJSON);
		assertEquals(kbName, parseFromJSON.getKbKey().getName());
	}

	@Test
	public void testResultAvailableFromExt()
	{
		assertTrue(classUnderTest.resultAvailable(kbList, externalConfigStructured));
	}

	@Test
	public void testResultAvailableFromExtNoKbKey()
	{
		externalConfigStructured.setKbKey(null);
		assertFalse(classUnderTest.resultAvailable(kbList, externalConfigStructured));
	}

	@Test
	public void testResultAvailableFromExtNoKbKeyFromRequest()
	{
		kbList.get(0).setKey(null);
		assertFalse(classUnderTest.resultAvailable(kbList, externalConfigStructured));
	}

	@Test
	public void testResultAvailableFromExtVersionDoesNotMatch()
	{
		externalConfigStructured.getKbKey().setVersion("NOT_EXISTING");
		assertFalse(classUnderTest.resultAvailable(kbList, externalConfigStructured));
	}

	@Test
	public void testResultAvailableFromExtLogsysDoesNotMatch()
	{
		externalConfigStructured.getKbKey().setLogsys("NOT_EXISTING");
		assertFalse(classUnderTest.resultAvailable(kbList, externalConfigStructured));
	}

	@Test
	public void testResultAvailableFromExtNameDoesNotMatch()
	{
		externalConfigStructured.getKbKey().setName("NOT_EXISTING");
		assertFalse(classUnderTest.resultAvailable(kbList, externalConfigStructured));
	}

	@Test
	public void testResultIdAvailable()
	{
		final Integer readKbId = classUnderTest.resultIdAvailable(productcode, kbDate, kbList);
		assertEquals(KbId, readKbId);

	}

	@Test(expected = IllegalStateException.class)
	public void testResultIdAvailableEmptyList()
	{
		classUnderTest.resultIdAvailable(productcode, kbDate, Collections.emptyList());
	}

	@Test
	public void testHasKbForExtConfig()
	{
		assertTrue(classUnderTest.hasKbForExtConfig(productcode, externalCfgMatching));
	}

	@Test
	public void testExtConfigurationStrategy()
	{
		assertEquals(commerceExternalConfigurationStrategy, classUnderTest.getCommerceExternalConfigurationStrategy());
	}


}
