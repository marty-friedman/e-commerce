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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.sap.productconfig.facades.ConfigPricing;
import de.hybris.platform.sap.productconfig.facades.PriceValueUpdateData;
import de.hybris.platform.sap.productconfig.facades.UniqueUIKeyGenerator;
import de.hybris.platform.sap.productconfig.facades.impl.ConfigPricingImpl;
import de.hybris.platform.sap.productconfig.facades.impl.ConfigPricingImplTest.DummyPriceDataFactory;
import de.hybris.platform.sap.productconfig.facades.impl.UniqueUIKeyGeneratorImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.CsticQualifier;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceValueUpdateModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.PriceModelImpl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;


/**
 *
 *
 */
@UnitTest
public class DeltaPricePopulatorTest
{
	private static final String NULL_KEY = "null-null.null.null";
	private DeltaPricePopulator deltaPricePopulator;
	PriceDataFactory priceDataFactory = new DummyPriceDataFactory();

	private final ConfigPricing configPricing = new ConfigPricingImpl();

	private final UniqueUIKeyGenerator uiKeyGenerator = new UniqueUIKeyGeneratorImpl();

	@Before
	public void setup()
	{
		deltaPricePopulator = new DeltaPricePopulator();
		configPricing.setPriceDataFactory(priceDataFactory);
		deltaPricePopulator.setConfigPricing(configPricing);
		deltaPricePopulator.setUiKeyGenerator(uiKeyGenerator);

	}

	@Test
	public void testPopulatePrice_null()
	{
		final PriceValueUpdateData target = new PriceValueUpdateData();
		final PriceValueUpdateModel source = new PriceValueUpdateModel();
		final CsticQualifier csticQualifier = new CsticQualifier();
		source.setCsticQualifier(csticQualifier);

		deltaPricePopulator.populate(source, target);
		assertEquals(NULL_KEY, target.getCsticUiKey());
		assertTrue(target.getPrices().isEmpty());
	}

	@Test
	public void testPopulatePrice_priceDefined()
	{
		final PriceValueUpdateData target = new PriceValueUpdateData();
		final PriceValueUpdateModel source = new PriceValueUpdateModel();
		final CsticQualifier csticQualifier = new CsticQualifier();
		csticQualifier.setInstanceId("instanceId");
		csticQualifier.setInstanceName("instanceName");
		csticQualifier.setGroupName("groupName");
		csticQualifier.setCsticName("csticName");
		final PriceModel priceModel = createPriceModel("EUR", BigDecimal.valueOf(1.2));
		source.setCsticQualifier(csticQualifier);
		final Map<String, PriceModel> valuePrices = new HashMap<String, PriceModel>();
		valuePrices.put("VALUE1", priceModel);
		source.setValuePrices(valuePrices);



		deltaPricePopulator.populate(source, target);
		assertEquals("instanceId-instanceName.groupName.csticName", target.getCsticUiKey());
		assertNotNull(target.getPrices().get("VALUE1"));

	}

	protected PriceModel createPriceModel(final String currency, final BigDecimal priceValue)
	{
		final PriceModel model = new PriceModelImpl();
		model.setCurrency(currency);
		model.setPriceValue(priceValue);
		return model;
	}

}
