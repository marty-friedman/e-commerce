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
package de.hybris.platform.sap.productconfig.frontend.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.sap.productconfig.facades.ConfigPricing;
import de.hybris.platform.sap.productconfig.facades.ConfigurationPricingFacade;
import de.hybris.platform.sap.productconfig.facades.PriceValueUpdateData;
import de.hybris.platform.sap.productconfig.facades.PricingData;
import de.hybris.platform.sap.productconfig.facades.SessionAccessFacade;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;
import de.hybris.platform.sap.productconfig.frontend.util.impl.JSONProviderFactory;
import de.hybris.platform.sap.productconfig.frontend.util.impl.UiStateHandler;

import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;


@SuppressWarnings("javadoc")
@UnitTest
public class PricingControllerTest
{
	private PricingController classUnderTest;
	private UiStatus uiStatus;
	@Mock
	private SessionAccessFacade sessionAccessFacade;
	@Mock
	private Model mockedModel;
	@Mock
	private ConfigurationPricingFacade mockedPricingFacade;
	private PricingData priceSummary;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = spy(new PricingController());
		classUnderTest.setSessionAccessFacade(sessionAccessFacade);
		classUnderTest.setUiStateHandler(new UiStateHandler());
		classUnderTest.setConfigPricingFacade(mockedPricingFacade);
		uiStatus = new UiStatus();
		uiStatus.setConfigId("123");

		given(sessionAccessFacade.getUiStatusForProduct("pCode")).willReturn(uiStatus);

		priceSummary = new PricingData();
		priceSummary.setBasePrice(ConfigPricing.NO_PRICE);
		priceSummary.setCurrentTotal(ConfigPricing.NO_PRICE);
		priceSummary.setSelectedOptions(ConfigPricing.NO_PRICE);
		doReturn("Include").when(classUnderTest).callLocalization(PricingController.VALUE_PRICES_INCLUDED);
	}

	@Test
	public void testUpdatePricing()
	{
		given(mockedPricingFacade.getValuePrices(any(List.class), eq("123"))).willReturn(Collections.emptyList());
		given(mockedPricingFacade.getPriceSummary("123")).willReturn(priceSummary);

		final String jsonString = classUnderTest.updatePricing("pCode");
		assertNotNull(jsonString);
		//try to parse the json string to make sure it's parsable
		assertNotNull(jsonToObject(jsonString));
	}

	@Test
	public void testUpdatePricing_uiStatusNull()
	{
		given(sessionAccessFacade.getUiStatusForProduct("pCode")).willReturn(null);
		final String jsonString = classUnderTest.updatePricing("pCode");
		assertNotNull(jsonString);
		//try to parse the json string to make sure it's parsable
		assertNotNull(jsonToObject(jsonString));
	}

	@Test
	public void testToJson()
	{
		final String jsonString = classUnderTest.toJson(priceSummary, Collections.emptyList());
		final JsonObject jsonObj = jsonToObject(jsonString);
		assertTrue(jsonObj.containsKey(PricingController.JSON_NAME_BASE_PRICE_VALUE));
		assertTrue(jsonObj.containsKey(PricingController.JSON_NAME_CURRENT_TOTAL_VALUE));
		assertTrue(jsonObj.containsKey(PricingController.JSON_NAME_SELECTED_OPTIONS_VALUE));
		assertTrue(jsonObj.getJsonArray(PricingController.JSON_NAME_VALUE_PRICE_ARRAY).isEmpty());
	}

	@Test
	public void testPriceSummmaryToJson()
	{
		priceSummary = createPriceSummary("EUR", "800.00", "1000.99", "200.99");
		final JsonObjectBuilder jsonBuilder = classUnderTest.toJson(priceSummary);
		final JsonObject jsonObj = jsonBuilder.build();
		assertEquals("EUR 800.00", jsonObj.getString(PricingController.JSON_NAME_BASE_PRICE_VALUE));
		assertEquals("EUR 1000.99", jsonObj.getString(PricingController.JSON_NAME_CURRENT_TOTAL_VALUE));
		assertEquals("EUR 200.99", jsonObj.getString(PricingController.JSON_NAME_SELECTED_OPTIONS_VALUE));
		assertFalse(jsonObj.containsKey(PricingController.JSON_NAME_PRICING_ERROR));
	}

	@Test
	public void testPriceSummmaryToJson_PricingError()
	{
		priceSummary = new PricingData();
		final JsonObjectBuilder jsonBuilder = classUnderTest.toJson(priceSummary);
		final JsonObject jsonObj = jsonBuilder.build();
		assertTrue(jsonObj.getBoolean(PricingController.JSON_NAME_PRICING_ERROR));
		assertTrue(jsonObj.containsKey(PricingController.JSON_NAME_BASE_PRICE_VALUE));
		assertTrue(jsonObj.containsKey(PricingController.JSON_NAME_CURRENT_TOTAL_VALUE));
		assertTrue(jsonObj.containsKey(PricingController.JSON_NAME_SELECTED_OPTIONS_VALUE));
	}

	@Test
	public void testValuePricesToJson()
	{
		final PriceData price = createPriceData("EUR", "200.99");
		final List<PriceValueUpdateData> valuePrices = createValuePrices(price, true);
		final JsonArrayBuilder arrayBuilder = classUnderTest.toJson(valuePrices);
		final JsonArray jsonArray = arrayBuilder.build();
		final JsonObject jsonValue = jsonArray.getJsonObject(0);
		assertEquals("cstic1", jsonValue.getString(PricingController.JSON_NAME_CSTIC_KEY));
		final JsonArray csticValues = jsonValue.getJsonArray(PricingController.JSON_NAME_CSTIC_VALUE_ARRAY);
		assertEquals("csticValue", csticValues.getJsonObject(0).getString(PricingController.JSON_NAME_CSTIC_VALUE_KEY));
		assertEquals("EUR 200.99", csticValues.getJsonObject(0).getString(PricingController.JSON_NAME_VALUE_PRICE));
	}

	@Test
	public void testValuePricesToJson_Included_ForDeltaPrices()
	{
		final PriceData price = createPriceData("EUR", "0.00");
		final List<PriceValueUpdateData> valuePrices = createValuePrices(price, true);
		final JsonArrayBuilder arrayBuilder = classUnderTest.toJson(valuePrices);
		final JsonArray jsonArray = arrayBuilder.build();
		final JsonObject jsonValue = jsonArray.getJsonObject(0);
		assertEquals("cstic1", jsonValue.getString(PricingController.JSON_NAME_CSTIC_KEY));
		final JsonArray csticValues = jsonValue.getJsonArray(PricingController.JSON_NAME_CSTIC_VALUE_ARRAY);
		assertEquals("csticValue", csticValues.getJsonObject(0).getString(PricingController.JSON_NAME_CSTIC_VALUE_KEY));
		assertEquals("Include", csticValues.getJsonObject(0).getString(PricingController.JSON_NAME_VALUE_PRICE));
	}

	@Test
	public void testValuePricesToJson_Included_ForAbsolutePrices()
	{
		final PriceData price = createPriceData("EUR", "0.00");
		final List<PriceValueUpdateData> valuePrices = createValuePrices(price, false);
		final JsonArrayBuilder arrayBuilder = classUnderTest.toJson(valuePrices);
		final JsonArray jsonArray = arrayBuilder.build();
		final JsonObject jsonValue = jsonArray.getJsonObject(0);
		assertEquals("cstic1", jsonValue.getString(PricingController.JSON_NAME_CSTIC_KEY));
		final JsonArray csticValues = jsonValue.getJsonArray(PricingController.JSON_NAME_CSTIC_VALUE_ARRAY);
		assertEquals("csticValue", csticValues.getJsonObject(0).getString(PricingController.JSON_NAME_CSTIC_VALUE_KEY));
		assertEquals("", csticValues.getJsonObject(0).getString(PricingController.JSON_NAME_VALUE_PRICE));
	}


	@Test
	public void testValuePricesWithNoConfigPriceToJson()
	{
		final PriceData price = ConfigPricing.NO_PRICE;
		final List<PriceValueUpdateData> valuePrices = createValuePrices(price, true);
		final JsonArrayBuilder arrayBuilder = classUnderTest.toJson(valuePrices);
		final JsonArray jsonArray = arrayBuilder.build();
		assertTrue(jsonArray.isEmpty());
	}

	@Test
	public void testCsticValueArrayToJsonFalse()
	{
		final JsonArrayBuilder csticValuesArrayBuilder = JSONProviderFactory.getJSONProvider().createArrayBuilder();
		final PriceValueUpdateData valuePrice = createPriceValueUpdateData("cstic1", "csticValue", ConfigPricing.NO_PRICE, true);
		final boolean atLeastOneValuePrice = classUnderTest.addValuePriceToCsticValueArray(csticValuesArrayBuilder, valuePrice);
		assertFalse(atLeastOneValuePrice);
	}

	@Test
	public void testCsticValueArrayToJsonTrue()
	{
		final JsonArrayBuilder csticValuesArrayBuilder = JSONProviderFactory.getJSONProvider().createArrayBuilder();
		final PriceValueUpdateData valuePrice = createPriceValueUpdateData("cstic1", "csticValue", createPriceData("EUR", "200.99"),
				true);
		final boolean atLeastOneValuePrice = classUnderTest.addValuePriceToCsticValueArray(csticValuesArrayBuilder, valuePrice);
		assertTrue(atLeastOneValuePrice);
	}

	protected List<PriceValueUpdateData> createValuePrices(final PriceData priceValue, final boolean showDeltaPrices)
	{
		final List<PriceValueUpdateData> valuePrices = new ArrayList<>();
		valuePrices.add(createPriceValueUpdateData("cstic1", "csticValue", priceValue, showDeltaPrices));

		return valuePrices;
	}

	protected PriceValueUpdateData createPriceValueUpdateData(final String csticKey, final String csticValueKey,
			final PriceData priceValue, final boolean showDeltaPrices)
	{
		final PriceValueUpdateData cstic1 = new PriceValueUpdateData();
		cstic1.setCsticUiKey(csticKey);
		final Map<String, PriceData> prices = new HashMap<>();
		prices.put(csticValueKey, priceValue);
		cstic1.setPrices(prices);
		cstic1.setShowDeltaPrices(showDeltaPrices);
		return cstic1;
	}

	protected PriceData createPriceData(final String currency, final String value)
	{
		final PriceData price = new PriceData();
		price.setFormattedValue(currency + " " + value);
		price.setValue(new BigDecimal(value));
		return price;
	}

	protected PricingData createPriceSummary(final String currecncy, final String basePrice, final String currentTotal,
			final String selectedOptions)
	{
		priceSummary = new PricingData();
		priceSummary.setBasePrice(createPriceData(currecncy, basePrice));
		priceSummary.setCurrentTotal(createPriceData(currecncy, currentTotal));
		priceSummary.setSelectedOptions(createPriceData(currecncy, selectedOptions));
		return priceSummary;
	}


	protected JsonObject jsonToObject(final String json)
	{
		final JsonReader jsonReader = Json.createReader(new StringReader(json));
		final JsonObject object = jsonReader.readObject();
		jsonReader.close();
		return object;
	}
}
