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
package de.hybris.platform.sap.productconfig.facades.strategy;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.services.impl.CPQConfigurableChecker;
import de.hybris.platform.variants.model.VariantTypeModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ProductConfigAddToCartStrategyTest
{

	private ProductConfigAddToCartStrategy classUnderTest;
	private CommerceCartParameter params;
	private ProductModel product;
	private CartModel cart;

	@Mock
	private CPQConfigurableChecker cpqConfigurableChecker;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		classUnderTest = new ProductConfigAddToCartStrategy();
		classUnderTest.setCpqConfigurableChecker(cpqConfigurableChecker);
		createValidEntity();

	}

	private void createValidEntity()
	{
		params = new CommerceCartParameter();
		cart = new CartModel();
		params.setCart(cart);
		product = new ProductModel();
		params.setProduct(product);
		params.setQuantity(1);

		when(cpqConfigurableChecker.isCPQConfigurableProduct(any())).thenReturn(true);
	}

	@Test
	public void testValidate_OK() throws CommerceCartModificationException
	{
		classUnderTest.validateAddToCart(params);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidate_cartNull() throws CommerceCartModificationException
	{
		params.setCart(null);
		classUnderTest.validateAddToCart(params);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidate_productNull() throws CommerceCartModificationException
	{
		params.setProduct(null);
		classUnderTest.validateAddToCart(params);
	}

	@Test(expected = CommerceCartModificationException.class)
	public void testValidate_quantityZero() throws CommerceCartModificationException
	{
		params.setQuantity(0);
		classUnderTest.validateAddToCart(params);
	}

	@Test(expected = CommerceCartModificationException.class)
	public void testValidate_variant() throws CommerceCartModificationException
	{
		when(cpqConfigurableChecker.isCPQConfigurableProduct(any())).thenReturn(false);
		product.setVariantType(new VariantTypeModel());
		classUnderTest.validateAddToCart(params);
	}
}
