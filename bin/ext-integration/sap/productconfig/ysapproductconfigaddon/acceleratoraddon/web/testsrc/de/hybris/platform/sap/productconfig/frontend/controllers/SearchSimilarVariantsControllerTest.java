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
package de.hybris.platform.sap.productconfig.frontend.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.sap.productconfig.facades.ConfigurationVariantData;
import de.hybris.platform.sap.productconfig.facades.ConfigurationVariantFacade;
import de.hybris.platform.sap.productconfig.frontend.constants.SapproductconfigfrontendWebConstants;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;


@UnitTest
public class SearchSimilarVariantsControllerTest
{

	private SearchSimilarVariantsController classUnderTest;
	private Model model;

	@Mock
	private ConfigurationVariantFacade variantFacadeMock;
	private List<ConfigurationVariantData> variants;

	@Before
	public void setUp()
	{
		classUnderTest = new SearchSimilarVariantsController();
		MockitoAnnotations.initMocks(this);

		model = new ExtendedModelMap();
		variants = new ArrayList<ConfigurationVariantData>();
		given(variantFacadeMock.searchForSimilarVariants("config_123", "Product_123")).willReturn(variants);
		classUnderTest.setVariantFacade(variantFacadeMock);
	}

	@Test
	public void testGetViewName() throws CMSItemNotFoundException
	{
		final String viewName = classUnderTest.getViewName();
		assertEquals("addon:/ysapproductconfigaddon/pages/configuration/searchVariantsForAJAXRequests", viewName);
	}

	@Test
	public void testSearchVariant() throws CMSItemNotFoundException
	{
		classUnderTest.searchVariant("config_123", "Product_123", model);
		final List<ConfigurationVariantData> variantResult = (List<ConfigurationVariantData>) model.asMap().get(
				SapproductconfigfrontendWebConstants.VARIANT_SEARCH_RESULT_ATTRIBUTE);
		assertSame(variants, variantResult);
	}
}
