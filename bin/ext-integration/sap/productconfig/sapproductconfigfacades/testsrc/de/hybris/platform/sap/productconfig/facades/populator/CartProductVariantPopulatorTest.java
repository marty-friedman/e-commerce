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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.ConfigurationInfoData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.converters.populator.ProductClassificationPopulator;
import de.hybris.platform.commercefacades.product.data.BaseOptionData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.sapmodel.model.ERPVariantProductModel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


@UnitTest
public class CartProductVariantPopulatorTest
{

	private CartProductVariantPopulator classUnderTest;
	private CartModel source;
	private CartData target;
	private List<OrderEntryData> targetEntryList;
	private OrderEntryData targetEntry;
	private OrderEntryData targetEntry2;
	private OrderEntryData targetEntry3;
	private final Integer entryNo1 = Integer.valueOf(1);
	private final Integer entryNo2 = Integer.valueOf(2);
	private final Integer entryNo3 = Integer.valueOf(3);
	private ProductModel productModelKmat;
	private ProductModel productModelVariant;
	private ProductModel productModel;
	private final FeatureProvider featureProvider = new FeatureProvider();

	@Mock
	private AbstractOrderEntryModel sourceEntry;
	@Mock
	private AbstractOrderEntryModel sourceEntry2;
	@Mock
	private AbstractOrderEntryModel sourceEntry3;
	@Mock
	private ProductClassificationPopulator<ProductModel, ProductData> productPopulatorMock;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		source = new CartModel();

		final List<AbstractOrderEntryModel> entryList = new ArrayList();
		entryList.add(sourceEntry);
		entryList.add(sourceEntry2);
		entryList.add(sourceEntry3);

		target = new CartData();
		targetEntryList = new ArrayList();
		target.setEntries(targetEntryList);
		targetEntry = new OrderEntryData();
		targetEntry.setEntryNumber(entryNo1);
		targetEntry.setItemPK("123");
		targetEntryList.add(targetEntry);
		targetEntry2 = new OrderEntryData();
		targetEntry2.setEntryNumber(entryNo2);
		targetEntry2.setItemPK("456");
		targetEntry2.setProduct(new ProductData());
		targetEntry2.getProduct().setBaseOptions(new ArrayList<>());
		targetEntry2.getProduct().getBaseOptions().add(new BaseOptionData());
		targetEntryList.add(targetEntry2);
		targetEntry3 = new OrderEntryData();
		targetEntry3.setEntryNumber(entryNo3);
		targetEntry3.setItemPK("789");
		targetEntryList.add(targetEntry3);

		source.setEntries(entryList);

		productModel = new ProductModel();
		productModel.setCode("Non-Configurable Product");
		productModelKmat = new ProductModel();
		productModelKmat.setCode("Configurable Product");
		productModelVariant = new ERPVariantProductModel();
		productModelVariant.setCode("Product Variant");

		classUnderTest = new CartProductVariantPopulator();
		mockClassificationPopulator();
		classUnderTest.setFeatureProvider(featureProvider);
	}

	protected void mockClassificationPopulator()
	{
		final Answer answer = new Answer<Object>()
		{
			@Override
			public Object answer(final InvocationOnMock invocation) throws Throwable
			{
				final Object[] args = invocation.getArguments();
				final ProductData data = (ProductData) args[1];
				data.setClassifications(VariantOverviewPopulatorTest.mockClassifications());
				return null;
			}
		};
		Mockito.doAnswer(answer).when(productPopulatorMock).populate(Mockito.any(ProductModel.class),
				Mockito.any(ProductData.class));
		classUnderTest.setClassificationPopulator(productPopulatorMock);
	}

	@Test
	public void testPopulate_Max2()
	{
		assertFalse(target.getEntries().get(1).getProduct().getBaseOptions().isEmpty());
		final PK value = initializeSourceItem();
		final int numberOfMaxCstics = 2;
		classUnderTest.setMaxNumberOfDisplayedCsticsInCart(numberOfMaxCstics);
		classUnderTest.populate(source, target);
		final OrderEntryData targetEntryProductVariant = target.getEntries().get(1);
		assertTrue("ItemPK not set", targetEntryProductVariant.getItemPK().equals(value.toString()));
		final List<ConfigurationInfoData> configInfoDataList = targetEntryProductVariant.getConfigurationInfos();
		assertEquals("2 ConfigurationInfoData entries should be returned", 2, configInfoDataList.size());
		assertEquals("Second entry's cstic: ", VariantOverviewPopulatorTest.CSTIC_ENGINE,
				configInfoDataList.get(1).getConfigurationLabel());
		assertEquals("Second entry's cstic's value: ", VariantOverviewPopulatorTest.VALUE_HYBRID,
				configInfoDataList.get(1).getConfigurationValue());
		assertTrue(targetEntryProductVariant.getProduct().getBaseOptions().isEmpty());
	}

	@Test
	public void testPopulate_Max4()
	{
		assertFalse(target.getEntries().get(1).getProduct().getBaseOptions().isEmpty());
		final PK value = initializeSourceItem();
		final int numberOfMaxCstics = 4;
		classUnderTest.setMaxNumberOfDisplayedCsticsInCart(numberOfMaxCstics);
		classUnderTest.populate(source, target);
		final OrderEntryData targetEntryProductVariant = target.getEntries().get(1);
		assertTrue("ItemPK not set", targetEntryProductVariant.getItemPK().equals(value.toString()));
		final List<ConfigurationInfoData> configInfoDataList = targetEntryProductVariant.getConfigurationInfos();
		assertEquals("3 ConfigurationInfoData entries should be returned", 3, configInfoDataList.size());
		assertEquals("Second entry's cstic: ", VariantOverviewPopulatorTest.CSTIC_ENGINE,
				configInfoDataList.get(1).getConfigurationLabel());
		assertEquals("Second entry's cstic's value: ", VariantOverviewPopulatorTest.VALUE_HYBRID,
				configInfoDataList.get(1).getConfigurationValue());
		assertEquals("Third entry's cstic: ", VariantOverviewPopulatorTest.CSTIC_ACC,
				configInfoDataList.get(2).getConfigurationLabel());
		assertEquals("Third entry's cstic's values: ", "Advanced Radio 3000; Cup Holder; Navigation System",
				configInfoDataList.get(2).getConfigurationValue());
		assertTrue(targetEntryProductVariant.getProduct().getBaseOptions().isEmpty());
	}

	private PK initializeSourceItem()
	{
		final PK value = PK.fromLong(123);
		Mockito.when(sourceEntry.getPk()).thenReturn(value);
		Mockito.when(sourceEntry.getProduct()).thenReturn(productModel);
		Mockito.when(sourceEntry.getEntryNumber()).thenReturn(entryNo1);

		final PK value2 = PK.fromLong(456);
		Mockito.when(sourceEntry2.getPk()).thenReturn(value2);
		Mockito.when(sourceEntry2.getProduct()).thenReturn(productModelVariant);
		Mockito.when(sourceEntry2.getEntryNumber()).thenReturn(entryNo2);

		final PK value3 = PK.fromLong(789);
		Mockito.when(sourceEntry3.getPk()).thenReturn(value3);
		Mockito.when(sourceEntry3.getProduct()).thenReturn(productModelKmat);
		Mockito.when(sourceEntry3.getEntryNumber()).thenReturn(entryNo3);

		return value2;
	}
}
