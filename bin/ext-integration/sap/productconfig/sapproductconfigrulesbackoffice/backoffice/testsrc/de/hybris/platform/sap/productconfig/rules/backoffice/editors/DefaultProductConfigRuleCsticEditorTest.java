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
package de.hybris.platform.sap.productconfig.rules.backoffice.editors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.rules.backoffice.constants.SapproductconfigrulesbackofficeConstants;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class DefaultProductConfigRuleCsticEditorTest extends BaseProductConfigRuleParameterEditorTest
{

	private DefaultProductConfigRuleCsticEditor classUnderTest;

	@Override
	@Before
	public void setUp()
	{
		super.setUp();
		classUnderTest = new DefaultProductConfigRuleCsticEditor();
		classUnderTest.setParameterProviderService(parameterProviderService);
	}


	@Test
	public void testGetPossibleValuesForConditionParameter()
	{
		final List<Object> possibleValues = classUnderTest.getPossibleValuesForConditionParameter(productModel);
		assertEquals(3, possibleValues.size());
		assertTrue(possibleValues.contains(CSTIC_1));
		assertTrue(possibleValues.contains(CSTIC_2));
		assertTrue(possibleValues.contains(CSTIC_3));
	}

	@Test
	public void testGetPossibleValuesForActionParameter()
	{
		final List<String> productCodeList = new ArrayList<String>();
		productCodeList.add(PRODUCT_CODE);
		productCodeList.add(PRODUCT_CODE2);
		final List<Object> possibleValues = classUnderTest.getPossibleValuesForActionParameter(productCodeList);
		assertEquals(4, possibleValues.size());
		assertTrue(possibleValues.contains(CSTIC_1));
		assertTrue(possibleValues.contains(CSTIC_2));
		assertTrue(possibleValues.contains(CSTIC_3));
		assertTrue(possibleValues.contains(CSTIC_4));
	}

	@Test
	public void testGetPossibleValues_ProductModel()
	{
		final List<Object> possibleValues = classUnderTest.getPossibleValues(context);
		assertEquals(3, possibleValues.size());
	}

	@Test
	public void testGetPossibleValues_ProductCodeList()
	{
		final List<String> productCodeList = new ArrayList<String>();
		productCodeList.add(PRODUCT_CODE);
		productCodeList.add(PRODUCT_CODE2);
		parameters.put(SapproductconfigrulesbackofficeConstants.REFERENCE_SEARCH_CONDITION_PRODUCT_CODE_LIST, productCodeList);

		final List<Object> possibleValues = classUnderTest.getPossibleValues(context);
		assertEquals(4, possibleValues.size());
	}

	@Test
	public void testAddValuesForProductCode()
	{
		final List<Object> values = new ArrayList<Object>();
		classUnderTest.addValuesForProductCode(values, PRODUCT_CODE);
		assertEquals(3, values.size());
		assertTrue(values.contains(CSTIC_1));
		assertTrue(values.contains(CSTIC_2));
		assertTrue(values.contains(CSTIC_3));

		classUnderTest.addValuesForProductCode(values, PRODUCT_CODE2);
		assertEquals(4, values.size());
		assertTrue(values.contains(CSTIC_1));
		assertTrue(values.contains(CSTIC_2));
		assertTrue(values.contains(CSTIC_3));
		assertTrue(values.contains(CSTIC_4));
	}
}
