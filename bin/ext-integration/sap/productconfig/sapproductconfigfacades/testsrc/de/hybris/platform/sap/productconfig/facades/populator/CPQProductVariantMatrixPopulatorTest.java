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

import static org.junit.Assert.assertFalse;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;

import org.junit.Test;


/**
 *
 */
@UnitTest
public class CPQProductVariantMatrixPopulatorTest
{

	CPQProductVariantMatrixPopulator classUnderTest = new CPQProductVariantMatrixPopulator();

	@Test
	public void testPopulateVariantAttributes()
	{
		final ProductData productData = new ProductData();
		productData.setMultidimensional(Boolean.TRUE);
		classUnderTest.populateVariantAttributes(productData);
		assertFalse(productData.getMultidimensional().booleanValue());
	}
}
