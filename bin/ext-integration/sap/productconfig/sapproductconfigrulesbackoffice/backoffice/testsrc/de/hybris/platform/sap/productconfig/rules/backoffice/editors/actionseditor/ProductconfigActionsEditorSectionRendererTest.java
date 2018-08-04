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
package de.hybris.platform.sap.productconfig.rules.backoffice.editors.actionseditor;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.rules.backoffice.constants.SapproductconfigrulesbackofficeConstants;
import de.hybris.platform.sap.productconfig.rules.model.ProductConfigSourceRuleModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ProductconfigActionsEditorSectionRendererTest
{

	private ProductconfigActionsEditorSectionRenderer classUnderTest;

	protected static final String RULE_CODE = "TEST_RULE";

	@Mock
	ProductconfigProductCodeExtractor productCodeExtractor;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new ProductconfigActionsEditorSectionRenderer();
		classUnderTest.setProductCodeExtractor(productCodeExtractor);
		final List<String> productCodeList = new ArrayList<String>();
		productCodeList.add("PRODUCT_1");
		productCodeList.add("PRODUCT_2");
		given(productCodeExtractor.retrieveProductCodeList(Mockito.any())).willReturn(productCodeList);
	}

	@Test
	public void testGetEditorId()
	{
		final String editorId = classUnderTest.getEditorId();
		assertEquals(ProductconfigActionsEditorSectionRenderer.PRODUCTCONFIG_ACTIONS_EDITOR_ID, editorId);
	}

	@Test
	public void testAddProductCodeListToParameters()
	{
		final Map<Object, Object> parameters = new HashMap<Object, Object>();
		final ProductConfigSourceRuleModel model = new ProductConfigSourceRuleModel();
		model.setCode(RULE_CODE);

		classUnderTest.addProductCodeListToParameters(model, parameters);

		final List<String> retrievedProductCodeList = (List<String>) parameters
				.get(SapproductconfigrulesbackofficeConstants.PRODUCT_CODE_LIST);

		assertEquals(2, retrievedProductCodeList.size());
		assertEquals("PRODUCT_1", retrievedProductCodeList.get(0));
		assertEquals("PRODUCT_2", retrievedProductCodeList.get(1));
	}
}
