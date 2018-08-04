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
package de.hybris.platform.sap.productconfig.services.strategies.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartMergingException;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.services.impl.CPQConfigurableChecker;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPricingOrderIntegrationService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;



@UnitTest
public class ProductConfigCommerceCartMergingStrategyImplTest
{
	private ProductConfigCommerceCartMergingStrategyImpl classUnderTest;
	private final static String defaultExtConfig = "<xml>default configuration</xml>";
	private final static String extConfig1 = "<xml>confProduct_1</xml>";
	private final static String extConfig2 = "<xml>confProduct_2</xml>";
	private final static String extConfig3 = "<xml>confProduct_3</xml>";
	private final static String extConfig3_2 = "<xml>confProduct_3_2</xml>";
	private final static String confProd1 = "confProduct_1";
	private final static String confProd2 = "confProduct_2";
	private final static String confProd3 = "confProduct_3";


	@Mock
	private ModelService mockedModelService;
	@Mock
	private ProductConfigurationPricingOrderIntegrationService configurationPricingOrderIntegrationService;

	@Mock
	private CPQConfigurableChecker cpqConfigurableChecker;

	private AbstractOrderEntryModel entryToChange;
	private Map<String, List<String>> extConfigEntries;

	@Before
	public void setUp()
	{
		classUnderTest = new ProductConfigCommerceCartMergingStrategyImpl();
		MockitoAnnotations.initMocks(this);
		classUnderTest.setModelService(mockedModelService);
		classUnderTest.setConfigurationPricingOrderIntegrationService(configurationPricingOrderIntegrationService);
		classUnderTest.setCpqConfigurableChecker(cpqConfigurableChecker);

		when(cpqConfigurableChecker.isCPQConfigurableProduct(any(ProductModel.class))).thenReturn(true);

		extConfigEntries = new HashMap<>();
	}




	public static class DummyCartEntry extends AbstractOrderEntryModel
	{

		/**
		 *
		 */
		public DummyCartEntry()
		{
			super();
		}

		/**
		 * @param externalConfig
		 * @param product
		 */
		public DummyCartEntry(final String externalConfig, final ProductModel product, final Long qty)
		{
			super();
			this.externalConfig = externalConfig;
			this.product = product;
			this.quantity = qty;
		}

		private String externalConfig;
		private ProductModel product;
		private Long quantity;



		@Override
		public Long getQuantity()
		{
			return this.quantity;
		}

		@Override
		public void setQuantity(final Long quantity)
		{
			this.quantity = quantity;
		}

		@Override
		public ProductModel getProduct()
		{
			return product;
		}

		@Override
		public void setProduct(final ProductModel product)
		{
			this.product = product;
		}

		@Override
		public String getExternalConfiguration()
		{
			return externalConfig;
		}

		@Override
		public void setExternalConfiguration(final String value)
		{
			externalConfig = value;
		}
	}


	@Test
	public void testExchangeExternalConfiguration() throws CommerceCartMergingException
	{

		entryToChange = new DummyCartEntry(defaultExtConfig, createProductModel(confProd2), Long.valueOf(2L));
		final CartModel cart = createCartEntriesSimple();
		extConfigEntries = classUnderTest.collectCartEntriesByProductCodeAndQuantity(cart);
		assertEquals("Map should have three entries", 3, extConfigEntries.size());

		final Answer answer = mockedUpdateCartExternalConfiguration();
		Mockito.doAnswer(answer).when(configurationPricingOrderIntegrationService).updateCartEntryExternalConfiguration(extConfig2,
				entryToChange);

		classUnderTest.exchangeExternalConfigurationAndUpdateEntryBasePrice(entryToChange, extConfigEntries);
		assertFalse("Default external config must not be attached to entryToChange",
				entryToChange.getExternalConfiguration().equals(defaultExtConfig));
	}

	protected Answer mockedUpdateCartExternalConfiguration()
	{
		final Answer answer = new Answer()
		{
			@Override
			public Object answer(final InvocationOnMock invocation) throws Throwable
			{
				final Object[] args = invocation.getArguments();
				final String extCfg = (String) args[0];
				final DummyCartEntry entryToChange = (DummyCartEntry) args[1];
				entryToChange.setExternalConfiguration(extCfg);
				return null;
			}
		};
		return answer;
	}

	protected CartModel createCartEntriesSimple()
	{
		final CartModel cart = new CartModel();
		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		final DummyCartEntry firstEntry = new DummyCartEntry(extConfig1, createProductModel(confProd1), Long.valueOf(1L));
		entries.add(firstEntry);
		final DummyCartEntry secondEntry = new DummyCartEntry(extConfig2, createProductModel(confProd2), Long.valueOf(2L));
		entries.add(secondEntry);
		final DummyCartEntry thirdEntry = new DummyCartEntry(extConfig3, createProductModel(confProd3), Long.valueOf(3L));
		entries.add(thirdEntry);
		cart.setEntries(entries);
		return cart;
	}


	protected CartModel createCartEntriesMultiSameQty()
	{
		final CartModel cart = new CartModel();
		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		final DummyCartEntry firstEntry = new DummyCartEntry(extConfig1, createProductModel(confProd1), Long.valueOf(1L));
		entries.add(firstEntry);
		final DummyCartEntry secondEntry = new DummyCartEntry(extConfig2, createProductModel(confProd2), Long.valueOf(2L));
		entries.add(secondEntry);
		final DummyCartEntry thirdEntry = new DummyCartEntry(extConfig3, createProductModel(confProd3), Long.valueOf(3L));
		entries.add(thirdEntry);
		final DummyCartEntry thirdEntry_two = new DummyCartEntry(extConfig3_2, createProductModel(confProd3), Long.valueOf(3L));
		entries.add(thirdEntry_two);

		cart.setEntries(entries);
		return cart;
	}


	protected CartModel createCartEntriesMultiDifferentQty()
	{
		final CartModel cart = new CartModel();
		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		final DummyCartEntry firstEntry = new DummyCartEntry(extConfig1, createProductModel(confProd1), Long.valueOf(1L));
		entries.add(firstEntry);
		final DummyCartEntry secondEntry = new DummyCartEntry(extConfig2, createProductModel(confProd2), Long.valueOf(2L));
		entries.add(secondEntry);
		final DummyCartEntry thirdEntry = new DummyCartEntry(extConfig3, createProductModel(confProd3), Long.valueOf(1L));
		entries.add(thirdEntry);
		final DummyCartEntry thirdEntry_two = new DummyCartEntry(extConfig3_2, createProductModel(confProd3), Long.valueOf(3L));
		entries.add(thirdEntry_two);

		cart.setEntries(entries);
		return cart;
	}


	protected List<AbstractOrderEntryModel> createList(final AbstractOrderEntryModel entry)
	{
		final List<AbstractOrderEntryModel> list = new ArrayList<>();
		list.add(entry);
		return list;
	}

	protected void addToMap(final Map<String, List<AbstractOrderEntryModel>> map,
			final List<AbstractOrderEntryModel> listOfEntries)
	{
		map.put(createKey(listOfEntries.get(0)), listOfEntries);
	}

	protected String createKey(final AbstractOrderEntryModel entry)
	{
		final StringBuilder key = new StringBuilder();
		key.append(entry.getProduct().getCode());
		key.append(entry.getQuantity());
		return key.toString();
	}

	@Test
	public void testExchangeExternalConfigurationMultipleEntriesSameQty() throws CommerceCartMergingException
	{
		entryToChange = new DummyCartEntry(defaultExtConfig, createProductModel(confProd3), Long.valueOf(3L));
		final Answer answer = mockedUpdateCartExternalConfiguration();
		Mockito.doAnswer(answer).when(configurationPricingOrderIntegrationService).updateCartEntryExternalConfiguration(extConfig3,
				entryToChange);

		final CartModel cart = createCartEntriesMultiSameQty();
		extConfigEntries = classUnderTest.collectCartEntriesByProductCodeAndQuantity(cart);
		assertEquals("Map should have three entries", 3, extConfigEntries.size());
		assertEquals("Product3 should have two entries", 2, extConfigEntries.get(createKey(entryToChange)).size());

		classUnderTest.exchangeExternalConfigurationAndUpdateEntryBasePrice(entryToChange, extConfigEntries);
		assertEquals("entryToChange gets extConfig3 with first call.", extConfig3, entryToChange.getExternalConfiguration());

		Mockito.doAnswer(answer).when(configurationPricingOrderIntegrationService)
				.updateCartEntryExternalConfiguration(extConfig3_2, entryToChange);
		classUnderTest.exchangeExternalConfigurationAndUpdateEntryBasePrice(entryToChange, extConfigEntries);
		assertEquals("entryToChange gets extConfig3_2 with second call.", extConfig3_2, entryToChange.getExternalConfiguration());
	}

	@Test
	public void testExchangeExternalConfigurationMultipleEntriesDifferentQty() throws CommerceCartMergingException
	{
		entryToChange = new DummyCartEntry(defaultExtConfig, createProductModel(confProd3), Long.valueOf(1L));
		final Answer answer = mockedUpdateCartExternalConfiguration();
		Mockito.doAnswer(answer).when(configurationPricingOrderIntegrationService).updateCartEntryExternalConfiguration(extConfig3,
				entryToChange);

		final CartModel cart = createCartEntriesMultiDifferentQty();
		extConfigEntries = classUnderTest.collectCartEntriesByProductCodeAndQuantity(cart);
		assertEquals("Map should have four entries", 4, extConfigEntries.size());
		classUnderTest.exchangeExternalConfigurationAndUpdateEntryBasePrice(entryToChange, extConfigEntries);
		assertEquals("entryToChange gets extConfig3 with first call.", extConfig3, entryToChange.getExternalConfiguration());

		entryToChange = new DummyCartEntry(defaultExtConfig, createProductModel(confProd3), Long.valueOf(3L));
		Mockito.doAnswer(answer).when(configurationPricingOrderIntegrationService)
				.updateCartEntryExternalConfiguration(extConfig3_2, entryToChange);

		classUnderTest.exchangeExternalConfigurationAndUpdateEntryBasePrice(entryToChange, extConfigEntries);
		assertEquals("entryToChange gets extConfig3_2 with second call.", extConfig3_2, entryToChange.getExternalConfiguration());
	}



	@Test(expected = CommerceCartMergingException.class)
	public void testExchangeExternalConfigurationNoEntryFound() throws CommerceCartMergingException
	{
		entryToChange = new DummyCartEntry(defaultExtConfig, createProductModel(confProd1), Long.valueOf(1L));
		classUnderTest.exchangeExternalConfigurationAndUpdateEntryBasePrice(entryToChange, extConfigEntries);
	}

	protected ProductModel createProductModel(final String productCode)
	{
		final ProductModel productMock = Mockito.mock(ProductModel.class);
		given(productMock.getCode()).willReturn(productCode);
		return productMock;
	}

}
