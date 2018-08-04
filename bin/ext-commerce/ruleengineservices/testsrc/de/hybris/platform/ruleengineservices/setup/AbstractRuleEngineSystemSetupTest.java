/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.ruleengineservices.setup;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.ruleengineservices.model.RuleConditionDefinitionCategoryModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class AbstractRuleEngineSystemSetupTest extends ServicelayerTransactionalTest
{

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Resource
	private CommonI18NService commonI18NService;

	private TestSystemSetup setup;

	@Before
	public void setup()
	{
		setup = new TestSystemSetup();
		setup.setCommonI18NService(commonI18NService);
		setup.setImportService(importService);
	}

	@Test
	public void testImportUnusedLanguageFile()
	{
		try
		{
			commonI18NService.getLanguage("de");
			fail("Language for de should not be present. Please rewrite this test case to use another langauge not present in the system");
		}
		catch (final UnknownIdentifierException e)
		{
			// expected as German should not be present
		}

		// imports the generic and the english version (but should ignore the German one as the language is not present)
		setup.testImport("/ruleengineservices/test/setup/setup-with-unknown-language-test.impex", true, false);

		final Map<String, Object> queryParams = Collections.singletonMap("id",
				"setup-with-unknown-language-test");
		final FlexibleSearchQuery query = new FlexibleSearchQuery(
				"SELECT {pk} from {RuleConditionDefinitionCategory} where {id} = ?id", queryParams);

		// test import of language unspecific impex succeeded
		final SearchResult<RuleConditionDefinitionCategoryModel> search = flexibleSearchService.search(query);
		assertTrue("should have one imported entry", search.getResult().size() == 1);
		final RuleConditionDefinitionCategoryModel importedItem = search.getResult().get(0);

		// test that english impex got imported successfully
		assertEquals("should be english text", "setup with unknown language test", importedItem.getName(Locale.ENGLISH));

		// german impex file has been ignored as it would fail due to syntax errors
	}



	public class TestSystemSetup extends AbstractRuleEngineSystemSetup
	{
		public void testImport(final String fileName, final boolean errorIfMissing, final boolean legacyMode)
		{
			importImpexFile(fileName, errorIfMissing, legacyMode);
		}
	}
}
