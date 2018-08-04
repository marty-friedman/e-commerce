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
package de.hybris.platform.sap.productconfig.facades.populator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.facades.impl.ConfigurationVariantUtilImpl;
import de.hybris.platform.sap.sapmodel.model.ERPVariantProductModel;
import de.hybris.platform.variants.model.VariantTypeModel;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class CPQProductBasicPopulatorTest
{
	CPQProductBasicPopulator classUnderTest = new CPQProductBasicPopulator();

	@Mock
	private ProductModel productModel;
	@Mock
	private VariantTypeModel variantTypeModel;

	private final ProductData productData = new ProductData();

	@Test
	public void testPopulate()
	{
		MockitoAnnotations.initMocks(this);
		Mockito.when(productModel.getApprovalStatus()).thenReturn(ArticleApprovalStatus.APPROVED);

		classUnderTest.populatePurchasable(productModel, productData);
		assertEquals(Boolean.TRUE, productData.getPurchasable());
	}

	@Test
	public void testIsCPQBaseProduct()
	{
		classUnderTest.setConfigurationVariantUtil(new ConfigurationVariantUtilImpl());
		MockitoAnnotations.initMocks(this);
		Mockito.when(productModel.getVariantType()).thenReturn(variantTypeModel);
		Mockito.when(variantTypeModel.getCode()).thenReturn(ERPVariantProductModel._TYPECODE);
		final boolean isCPQVariant = classUnderTest.isCPQBaseProduct(productModel);
		assertTrue(isCPQVariant);
	}

}
