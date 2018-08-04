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
package de.hybris.platform.sap.productconfig.facades.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.sapmodel.model.ERPVariantProductModel;
import de.hybris.platform.variants.model.VariantTypeModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 *
 */
@UnitTest
public class ConfigurationVariantUtilImplTest
{
	private final ConfigurationVariantUtilImpl classUnderTest = new ConfigurationVariantUtilImpl();
	@Mock
	private ProductModel productModel;

	@Mock
	private VariantTypeModel variantTypeModel;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testIsCPQBaseProduct()
	{
		Mockito.when(productModel.getVariantType()).thenReturn(variantTypeModel);
		Mockito.when(variantTypeModel.getCode()).thenReturn(ERPVariantProductModel._TYPECODE);
		final boolean isCPQVariant = classUnderTest.isCPQBaseProduct(productModel);
		assertTrue(isCPQVariant);
	}

	@Test
	public void testIsCPQBaseProductNoVariant()
	{
		assertFalse(classUnderTest.isCPQBaseProduct(productModel));
	}

	@Test
	public void testIsCPQBaseProductWrongType()
	{
		Mockito.when(productModel.getVariantType()).thenReturn(variantTypeModel);
		Mockito.when(variantTypeModel.getCode()).thenReturn("UNKNOWN");
		assertFalse(classUnderTest.isCPQBaseProduct(productModel));
	}
}
