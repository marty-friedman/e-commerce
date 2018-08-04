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
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.CharonFacade;
import de.hybris.platform.sap.productconfig.runtime.cps.CharonKbDeterminationFacade;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.pricing.PricingHandler;
import de.hybris.platform.sap.productconfig.runtime.cps.strategy.ExternalConfigurationFromVariantStrategy;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.sap.productconfig.runtime.interf.external.impl.ConfigurationImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("javadoc")
@UnitTest
public class CPSConfigurationProviderTest
{
	private static final String EXTERNAL_CONFIG = "external Config";

	private static final String CONFIG_ID = "configId";

	private static final String PRODUCT_CODE = "Product Code";

	private static final Date kbDate = new Date(System.currentTimeMillis());

	private static final Integer kbId = Integer.valueOf(124);

	CPSConfigurationProvider classUnderTest = new CPSConfigurationProvider();

	@Mock
	Converter<CPSConfiguration, ConfigModel> configModelConverter;

	@Mock
	CharonFacade charonFacade;

	@Mock
	PricingHandler pricingHandler;

	private final CPSConfiguration configuration = new CPSConfiguration();

	@Mock
	private CharonKbDeterminationFacade charonKbDeterminationFacade;

	@Mock
	private ExternalConfigurationFromVariantStrategy externalConfigurationFromVariantStrategy;


	private KBKey kbKey = null;

	private String kbName;

	private String kbLogsys;

	private String kbVersion;

	@Before
	public void initialize()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest.setConfigModelConverter(configModelConverter);
		classUnderTest.setCharonFacade(charonFacade);
		classUnderTest.setPricingHandler(pricingHandler);
		classUnderTest.setCharonKbDeterminationFacade(charonKbDeterminationFacade);
		classUnderTest.setExternalConfigurationFromVariantStrategy(externalConfigurationFromVariantStrategy);

		Mockito.when(charonKbDeterminationFacade.readKbIdForDate(Mockito.anyString(), Mockito.any())).thenReturn(kbId);
		kbName = "kb";
		kbLogsys = "WEFCLNT504";
		kbVersion = "1.0";

	}

	@Test
	public void testConfigurationConverter()
	{
		assertEquals(configModelConverter, classUnderTest.getConfigModelConverter());
	}

	@Test
	public void testGetConfiguration() throws ConfigurationEngineException
	{
		classUnderTest.retrieveConfigurationModel(CONFIG_ID);
		Mockito.verify(charonFacade).getConfiguration(CONFIG_ID);
		Mockito.verify(configModelConverter).convert(Mockito.any());
	}

	@Test
	public void testGetExternalConfiguration() throws ConfigurationEngineException
	{
		classUnderTest.retrieveExternalConfiguration(CONFIG_ID);
		Mockito.verify(charonFacade).getExternalConfiguration(CONFIG_ID);
	}

	@Test
	public void testCreateConfigurationFromExternal()
	{
		Mockito.when(charonFacade.createConfigurationFromExternal(EXTERNAL_CONFIG)).thenReturn(configuration);
		classUnderTest.createConfigurationFromExternalSource(null, EXTERNAL_CONFIG);
		Mockito.verify(charonFacade).createConfigurationFromExternal(EXTERNAL_CONFIG);
		Mockito.verify(configModelConverter).convert(configuration);
		Mockito.verify(pricingHandler).preparePricingDocumentInput(configuration);
	}

	@Test
	public void testCreateConfigurationFromExternal_som()
	{
		final Configuration extConfiguration = new ConfigurationImpl();
		kbKey = new KBKeyImpl(PRODUCT_CODE);
		extConfiguration.setKbKey(kbKey);
		Mockito.when(charonFacade.createConfigurationFromExternal(extConfiguration, kbId)).thenReturn(configuration);
		classUnderTest.createConfigurationFromExternalSource(extConfiguration);
		Mockito.verify(charonFacade).createConfigurationFromExternal(extConfiguration, kbId);
		Mockito.verify(configModelConverter).convert(configuration);
		Mockito.verify(pricingHandler).preparePricingDocumentInput(configuration);
	}

	@Test
	public void testReleaseSession()
	{
		classUnderTest.releaseSession(CONFIG_ID);
		Mockito.verify(charonFacade).releaseSession(CONFIG_ID);
	}

	@Test
	public void testCharonKbDeterminationFacade()
	{
		assertEquals(charonKbDeterminationFacade, classUnderTest.getCharonKbDeterminationFacade());
	}


	@Test
	public void testKbForDateExists()
	{
		assertFalse(classUnderTest.isKbForDateExists(PRODUCT_CODE, kbDate));
	}

	@Test
	public void testKbVersionExists()
	{
		final KBKey kbKey = new KBKeyImpl(PRODUCT_CODE);
		assertFalse(classUnderTest.isKbVersionExists(kbKey, EXTERNAL_CONFIG));
	}

	@Test
	public void testFindKbId()
	{
		kbKey = new KBKeyImpl(PRODUCT_CODE, kbName, kbLogsys, kbVersion);

		final Integer idFound = classUnderTest.findKbId(kbKey);
		assertEquals(kbId, idFound);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindKbIdNullKbKey()
	{
		classUnderTest.findKbId(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindKbIdNullProduct()
	{
		kbKey = new KBKeyImpl(null, kbName, kbLogsys, kbVersion);
		classUnderTest.findKbId(kbKey);
	}

	@Test
	public void testIsConfigureVariantSupported()
	{
		assertTrue(classUnderTest.isConfigureVariantSupported());
	}

	@Test
	public void testGetExternalConfigurationFromVariantStrategy()
	{
		assertEquals(externalConfigurationFromVariantStrategy, classUnderTest.getExternalConfigurationFromVariantStrategy());
	}

}
