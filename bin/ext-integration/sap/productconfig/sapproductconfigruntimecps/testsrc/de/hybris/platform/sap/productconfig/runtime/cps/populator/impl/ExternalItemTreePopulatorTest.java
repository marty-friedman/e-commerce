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
package de.hybris.platform.sap.productconfig.runtime.cps.populator.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalItem;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalObjectKey;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalValue;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSQuantity;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.sap.productconfig.runtime.interf.external.impl.CharacteristicValueImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.external.impl.DummyConfigurationKD990SolImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@SuppressWarnings("javadoc")
@UnitTest
public class ExternalItemTreePopulatorTest
{

	private static final String isoCode1 = "PCE";
	private static final String sapCode1 = "ST";
	private static final String hybrisCode = "Pieces";
	private static final String isoCode2 = "PCE2";
	private static final String sapCodeUnknown = "UnknownSapCode";
	private ExternalItemTreePopulator classUnderTest;
	private final Configuration source = new DummyConfigurationKD990SolImpl();
	private final CPSExternalItem target = new CPSExternalItem();
	private final ObjectMapper objectMapper = new ObjectMapper();
	@Mock
	private UnitService unitService;
	private final Set<UnitModel> allUnits = new HashSet<>();
	@Mock
	private UnitModel unitWithSapCode;
	@Mock
	private UnitModel unitWithoutSapCode;
	@Mock
	private UnitModel unitWithSameSapCode;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		Mockito.when(unitService.getAllUnits()).thenReturn(allUnits);
		Mockito.when(unitWithSapCode.getCode()).thenReturn(isoCode1);
		Mockito.when(unitWithSapCode.getSapCode()).thenReturn(sapCode1);
		Mockito.when(unitWithoutSapCode.getCode()).thenReturn(hybrisCode);
		Mockito.when(unitWithSameSapCode.getCode()).thenReturn(isoCode2);
		Mockito.when(unitWithSameSapCode.getSapCode()).thenReturn(sapCode1);
		allUnits.add(unitWithSapCode);
		allUnits.add(unitWithoutSapCode);
		classUnderTest = Mockito.spy(new ExternalItemTreePopulator());
		classUnderTest.setUnitService(unitService);

	}


	@Test
	public void testPopulateCoreItemAttributes()
	{
		classUnderTest.populateCoreItemAttributes(source.getRootInstance(), target);
		assertTrue(target.isComplete());
		assertTrue(target.isConsistent());
		assertEquals("00000029", target.getId());
		final CPSExternalObjectKey objectKey = target.getObjectKey();
		assertEquals("KD990SOL", objectKey.getId());
		assertEquals("MARA", objectKey.getType());
		assertEquals("300", objectKey.getClassType());
		final CPSQuantity quantity = target.getQuantity();
		assertNull(quantity.getUnit());
		assertEquals(Double.valueOf("1.000"), quantity.getValue());
	}

	@Test
	public void testCreateExternalValue()
	{
		final CPSExternalValue result = classUnderTest.createExternalValue(source.getCharacteristicValues().get(0));
		assertNotNull(result);
		assertEquals("", result.getAuthor());
		assertEquals("4.00", result.getValue());
	}

	@Test
	public void testComputeCharacteristicKey()
	{
		final String result = classUnderTest.computeCharacteristicKey(source.getCharacteristicValues().get(0));
		assertEquals("00000029;CHHI_HX", result);
	}

	@Test
	public void testCreateExternalCharacteristic()
	{
		final CPSExternalCharacteristic result = classUnderTest
				.createExternalCharacteristic(source.getCharacteristicValues().get(0), new ArrayList<>());
		assertNotNull(result);
		assertNotNull(result.getValues());
		assertTrue(result.getValues().isEmpty());
		assertEquals("CHHI_HX", result.getId());
		assertTrue(result.isVisible());
	}

	@Test
	public void testProcessCharacteristicValues() throws JsonParseException, JsonMappingException, IOException
	{
		final String CHARACTERISTIC_VALUE_11 = "{\"instId\":\"00000030\",\"characteristic\":\"CHHI_BATT\",\"characteristicText\":\"Battery\",\"value\":\"T\",\"valueText\":\"Tesla2\",\"author\":\"\",\"invisible\":false}";
		source.addCharacteristicValue(objectMapper.readValue(CHARACTERISTIC_VALUE_11, CharacteristicValueImpl.class));
		final Map<String, List<CPSExternalCharacteristic>> result = classUnderTest
				.processCharacteristicValues(source.getCharacteristicValues());
		assertNotNull(result);
		assertEquals(3, result.size());
		assertEquals(8, result.get("00000029").size());
		assertEquals(3, result.get("00000030").size());
		assertEquals(1, result.get("00000031").size());
	}

	@Test
	public void testCreateExternalItem()
	{
		final CPSExternalItem result = classUnderTest.createExternalItem(source.getPartOfRelations().get(0));
		assertNotNull(result);
		assertEquals("0010", result.getBomPosition());
		assertEquals("", result.getBomPositionAuthor());
		final CPSExternalObjectKey objectKey = result.getBomPositionObjectKey();
		assertEquals("KD990WRES", objectKey.getId());
		assertEquals("MARA", objectKey.getType());
		assertEquals("300", objectKey.getClassType());
	}

	@Test
	public void testProcessInstancesAndPartsOfRelations()
	{
		final Map<String, List<CPSExternalItem>> result = classUnderTest
				.processInstancesAndPartsOfRelations(source.getPartOfRelations(), source.getInstances());
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals(2, result.get("00000029").size());
		assertEquals(1, result.get("00000030").size());
	}

	@Test
	public void testPopulate()
	{
		classUnderTest.populate(source, target);
		Mockito.verify(classUnderTest, Mockito.times(4)).populateCoreItemAttributes(Mockito.any(), Mockito.any());
		Mockito.verify(classUnderTest, Mockito.times(3)).createExternalItem(Mockito.any());
		Mockito.verify(classUnderTest, Mockito.times(11)).createExternalCharacteristic(Mockito.any(), Mockito.any());
		Mockito.verify(classUnderTest, Mockito.times(11)).createExternalValue(Mockito.any());
		Mockito.verify(classUnderTest, Mockito.times(4)).buildHierarchicalExternalConfiguration(Mockito.any(), Mockito.any(),
				Mockito.any());

		final List<CPSExternalItem> rootSubItems = target.getSubItems();
		assertEquals(2, rootSubItems.size());
		assertEquals(8, target.getCharacteristics().size());

		final CPSExternalItem subItem1 = rootSubItems.get(0);
		assertEquals(1, subItem1.getSubItems().size());
		assertEquals(2, subItem1.getCharacteristics().size());

		final CPSExternalItem subItem2 = rootSubItems.get(1);
		assertEquals(0, subItem2.getSubItems().size());
		assertEquals(1, subItem2.getCharacteristics().size());

		final CPSExternalItem subItem1subItem1 = rootSubItems.get(0).getSubItems().get(0);
		assertEquals(0, subItem1subItem1.getSubItems().size());
		assertEquals(0, subItem1subItem1.getCharacteristics().size());
	}

	@Test
	public void testUnitService()
	{
		assertEquals(unitService, classUnderTest.getUnitService());
	}

	@Test
	public void testConvertToIso()
	{
		final String isoUnitCode = classUnderTest.convertToIso(sapCode1);
		assertNotNull(isoUnitCode);
		assertEquals(isoCode1, isoUnitCode);
	}

	@Test
	public void testConvertToIsoNullSapCode()
	{
		assertNull(classUnderTest.convertToIso(null));
	}

	@Test
	public void testConvertToIsoBlankSapCode()
	{
		assertNull(classUnderTest.convertToIso(""));
	}

	@Test(expected = IllegalStateException.class)
	public void testConvertToIsoNoUniqueMatch()
	{
		allUnits.add(unitWithSameSapCode);
		classUnderTest.convertToIso(sapCode1);
	}

	@Test(expected = IllegalStateException.class)
	public void testConvertToIsoNoMatch()
	{

		classUnderTest.convertToIso(sapCodeUnknown);
	}
}
