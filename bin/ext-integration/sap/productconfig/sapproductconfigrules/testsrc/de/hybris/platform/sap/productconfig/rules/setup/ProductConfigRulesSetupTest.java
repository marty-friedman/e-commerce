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
package de.hybris.platform.sap.productconfig.rules.setup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.setup.SetupImpexService;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.impex.systemsetup.ImpExSystemSetup;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ProductConfigRulesSetupTest
{
	private ProductConfigRulesSetup classUnderTest;

	@Mock
	private SystemSetupContext context;

	@Mock
	private FlexibleSearchService flexibleSearchService;

	@Mock
	private SetupImpexService setupImpexService;

	@Mock
	private ImpExSystemSetup mockedImpexImporter;

	@Before
	public void setUp()
	{
		classUnderTest = new ProductConfigRulesSetup();
		MockitoAnnotations.initMocks(this);
		classUnderTest.setFlexibleSearchService(flexibleSearchService);
		classUnderTest.setSetupImpexService(setupImpexService);
		classUnderTest.setImpexImporter(mockedImpexImporter);
	}

	@Test
	public void testGetListOfLanguageFiles() throws Exception
	{
		final List<Path> localeFiles = classUnderTest.getListOfLanguageFiles(context);

		assertNotNull(localeFiles);
		assertTrue(localeFiles.size() > 0);

		assertTrue(localeFiles.stream().anyMatch(
				path -> path.toString().endsWith("sapproductconfigrules-impexsupport_en.properties")));
	}

	@Test
	public void testExtractLocaleOutOfFileName()
	{
		String fileName = "/sapproductconfigrules/resources/localization/sapproductconfigrules-impexsupport_en.properties";

		String locale = classUnderTest.extractLocaleOutOfFileName(fileName);
		assertEquals("en", locale);

		fileName = "sapproductconfigrules-impexsupport_en.properties";
		locale = classUnderTest.extractLocaleOutOfFileName(fileName);
		assertEquals("en", locale);

		fileName = "sapproductconfigrules-impexsupport_EN_en.properties";
		locale = classUnderTest.extractLocaleOutOfFileName(fileName);
		assertEquals("EN_en", locale);

		fileName = "sapproductconfigrules-impexsupport_de.properties";
		locale = classUnderTest.extractLocaleOutOfFileName(fileName);
		assertEquals("de", locale);

		fileName = "sapproductconfigrules-impexsupport.properties";
		locale = classUnderTest.extractLocaleOutOfFileName(fileName);
		assertNull(locale);
	}

	@Test
	public void getInitializationOptions()
	{
		final List<SystemSetupParameter> initializationOptions = classUnderTest.getInitializationOptions();
		assertNotNull(initializationOptions);
		assertTrue(initializationOptions.isEmpty());
	}

	@Test
	public void testImport() throws IOException
	{
		final SearchResult<Object> mockedSerchResult = Mockito.mock(SearchResult.class);
		given(flexibleSearchService.search(Mockito.anyString())).willReturn(mockedSerchResult);
		classUnderTest.processEssentialFiles(context);
		Mockito.verify(mockedImpexImporter).createAutoImpexProjectData(context);
		Mockito.verify(setupImpexService).importImpexFile(
				ProductConfigRulesSetup.RELATIVE_IMPEX_FOLDER + ProductConfigRulesSetup.IMPEX_ESSENTIAL_DEFINITIONS, true);
	}
}
