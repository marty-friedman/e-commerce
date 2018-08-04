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
package de.hybris.platform.sap.productconfig.runtime.interf.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceSummaryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSeverity;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSource;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSourceSubType;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticGroupModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.PriceModelImpl;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class ConfigModelFactoryImplTest
{


	private ConfigModelFactoryImpl classUnderTest;

	@Before
	public void setUp()
	{
		classUnderTest = new ConfigModelFactoryImpl();
	}

	@Test
	public void testCreateInstanceOfConfigModel()
	{
		final ConfigModel configModel = classUnderTest.createInstanceOfConfigModel();
		assertNotNull(configModel);
		assertTrue(configModel instanceof ConfigModelImpl);
	}

	@Test
	public void testCreateInstanceOfInstanceModel()
	{
		final InstanceModel instanceModel = classUnderTest.createInstanceOfInstanceModel();
		assertNotNull(instanceModel);
		assertTrue(instanceModel instanceof InstanceModelImpl);
	}

	@Test
	public void testCreateInstanceOfCsticModel()
	{
		final CsticModel csticModel = classUnderTest.createInstanceOfCsticModel();
		assertNotNull(csticModel);
		assertTrue(csticModel instanceof CsticModelImpl);
	}

	@Test
	public void testCreateInstanceOfCsticValueModel()
	{
		final CsticValueModel csticValueModel = classUnderTest.createInstanceOfCsticValueModel(CsticModel.TYPE_STRING);
		assertNotNull(csticValueModel);
		assertTrue(csticValueModel instanceof CsticValueModelImpl);
		assertFalse(csticValueModel.isNumeric());
	}

	@Test
	public void testCreateInstanceOfCsticNumericValueModel()
	{
		CsticValueModel csticValueModel = classUnderTest.createInstanceOfCsticValueModel(CsticModel.TYPE_INTEGER);
		assertNotNull(csticValueModel);
		assertTrue(csticValueModel.isNumeric());

		csticValueModel = classUnderTest.createInstanceOfCsticValueModel(CsticModel.TYPE_FLOAT);
		assertNotNull(csticValueModel);
		assertTrue(csticValueModel.isNumeric());
	}

	@Test
	public void testCreateInstanceOfCsticGroupModel()
	{
		final CsticGroupModel csticGroupModel = classUnderTest.createInstanceOfCsticGroupModel();
		assertNotNull(csticGroupModel);
		assertTrue(csticGroupModel instanceof CsticGroupModelImpl);
	}

	@Test
	public void testCreateInstanceOfPriceModel()
	{
		final PriceModel priceModel = classUnderTest.createInstanceOfPriceModel();
		assertNotNull(priceModel);
		assertTrue(priceModel instanceof PriceModelImpl);
	}

	@Test
	public void testCreateInstanceOfPriceSummaryModel()
	{
		final PriceSummaryModel priceSummaryModel = classUnderTest.createInstanceOfPriceSummaryModel();
		assertNotNull(priceSummaryModel);
	}

	@Test
	public void testCreateInstanceOfProductConfigMessage()
	{
		final ProductConfigMessage message = classUnderTest.createInstanceOfProductConfigMessage("test message", "123",
				ProductConfigMessageSeverity.INFO, ProductConfigMessageSource.ENGINE);
		assertNotNull(message);
		assertEquals("test message", message.getMessage());
		assertEquals("123", message.getKey());
		assertSame(ProductConfigMessageSeverity.INFO, message.getSeverity());
		assertSame(ProductConfigMessageSource.ENGINE, message.getSource());
		assertSame(ProductConfigMessageSourceSubType.DEFAULT, message.getSourceSubType());
	}

	@Test
	public void testCreateModelInstanceWithEmptyTargetClassName()
	{
		final ConfigModel configModel = classUnderTest.createModelInstance("",
				"de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl");
		assertNotNull(configModel);
		assertTrue(configModel instanceof ConfigModelImpl);
	}

	@Test
	public void testCreateModelInstanceWithNullTargetClassName()
	{
		final ConfigModel configModel = classUnderTest.createModelInstance(null,
				"de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl");
		assertNotNull(configModel);
		assertTrue(configModel instanceof ConfigModelImpl);
	}

	@Test
	public void testCreateModelInstance()
	{
		final ConfigModel configModel = classUnderTest.createModelInstance(
				"de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl",
				"de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl");
		assertNotNull(configModel);
		assertTrue(configModel instanceof ConfigModelImpl);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateModelInstanceThrowsException()
	{
		classUnderTest.createModelInstance("classNotFount",
				"de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl");
	}

}
