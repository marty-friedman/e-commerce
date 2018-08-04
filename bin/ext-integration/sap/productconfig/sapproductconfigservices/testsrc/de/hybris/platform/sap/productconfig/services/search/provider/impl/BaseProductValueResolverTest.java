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
package de.hybris.platform.sap.productconfig.services.search.provider.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class BaseProductValueResolverTest
{
	BaseProductValueResolver classUnderTest = new BaseProductValueResolver();
	@Mock
	private VariantProductModel variantProductModel;
	@Mock
	private ProductModel baseProductModel;
	@Mock
	private FieldNameProvider fieldNameProvider;
	private static String baseProductCode = "DRAGON_CAR";

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		Mockito.when(variantProductModel.getBaseProduct()).thenReturn(baseProductModel);
		Mockito.when(baseProductModel.getCode()).thenReturn(baseProductCode);
	}

	@Test
	public void testLoadData() throws FieldValueProviderException
	{
		final Optional<String> baseProduct = classUnderTest.loadData(null, null, variantProductModel);
		assertTrue(baseProduct.isPresent());
		assertEquals(baseProductCode, baseProduct.get());
	}

	@Test
	public void testLoadDataNoVariantProduct() throws FieldValueProviderException
	{
		final Optional<String> baseProduct = classUnderTest.loadData(null, null, baseProductModel);
		assertFalse(baseProduct.isPresent());
	}

	@Test(expected = NullPointerException.class)
	public void testLoadDataVariantProductDoesNotCarrayBase() throws FieldValueProviderException
	{
		Mockito.when(variantProductModel.getBaseProduct()).thenReturn(null);
		classUnderTest.loadData(null, null, variantProductModel);
	}


}
