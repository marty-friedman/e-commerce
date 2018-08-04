/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.ruleengineservices.rule.strategies.impl.mappers;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.daos.ProductDao;
import de.hybris.platform.ruleengineservices.rule.strategies.RuleParameterValueMapperException;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ProductRuleParameterValueMapperTest
{
	private static final String ANY_STRING = "anyString";

	@Rule
	public final ExpectedException expectedException = ExpectedException.none(); //NOPMD

	@Mock
	private ProductDao productDao;

	@Mock
	private ProductModel product;

	@InjectMocks
	private final ProductRuleParameterValueMapper mapper = new ProductRuleParameterValueMapper();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void nullTestFromString()
	{
		//expect
		expectedException.expect(IllegalArgumentException.class);

		//when
		mapper.fromString(null);
	}

	@Test
	public void nullTestToString()
	{
		//expect
		expectedException.expect(IllegalArgumentException.class);

		//when
		mapper.toString(null);
	}

	@Test
	public void noProductFoundTest()
	{
		//given
		BDDMockito.given(productDao.findProductsByCode(Mockito.anyString())).willReturn(null);

		//expect
		expectedException.expect(RuleParameterValueMapperException.class);

		//when
		mapper.fromString(ANY_STRING);
	}

	@Test
	public void mappedProductTest()
	{
		//given
		final List<ProductModel> products = new ArrayList<ProductModel>();
		products.add(Mockito.mock(ProductModel.class));
		products.add(Mockito.mock(ProductModel.class));

		BDDMockito.given(productDao.findProductsByCode(Mockito.anyString())).willReturn(products);

		//when
		final ProductModel mappedProduct = mapper.fromString(ANY_STRING);

		//then
		Assert.assertTrue(products.contains(mappedProduct));
	}

	@Test
	public void mappedProductIsFirstFoundTest()
	{
		//given
		final List<ProductModel> products = new ArrayList<ProductModel>();
		products.add(Mockito.mock(ProductModel.class));
		products.add(Mockito.mock(ProductModel.class));

		BDDMockito.given(productDao.findProductsByCode(Mockito.anyString())).willReturn(products);

		//when
		final ProductModel mappedProduct = mapper.fromString(ANY_STRING);

		//then
		Assert.assertEquals(products.get(0), mappedProduct);
	}

	@Test
	public void objectToStringTest()
	{
		//given
		givenStringRepresentationAttribute();

		//when
		final String stringRepresentation = mapper.toString(product);

		//then
		Assert.assertEquals(ANY_STRING, stringRepresentation);
	}

	private void givenStringRepresentationAttribute()
	{
		BDDMockito.given(product.getCode()).willReturn(ANY_STRING);
	}
}
