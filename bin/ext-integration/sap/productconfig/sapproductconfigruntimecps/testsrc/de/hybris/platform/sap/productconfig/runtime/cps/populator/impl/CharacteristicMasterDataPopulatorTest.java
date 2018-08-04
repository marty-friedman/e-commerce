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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.ConfigurationMasterDataService;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataCharacteristicContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSItem;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;



@SuppressWarnings("javadoc")
@UnitTest
public class CharacteristicMasterDataPopulatorTest
{
	CharacteristicMasterDataPopulator classUnderTest = new CharacteristicMasterDataPopulator();
	@Mock
	private ConfigurationMasterDataService masterDataService;
	private CPSCharacteristic source;
	private CsticModel target;
	private static final String kbId = "99";
	private static final String characteristicId = "CSTIC_ID";
	private static final String characteristicIdNumeric = "CSTIC_ID_NUMERIC";
	private CPSMasterDataCharacteristicContainer characteristicMasterData;
	private CPSMasterDataCharacteristicContainer characteristicMasterDataNumeric;
	private static final String description = "Language dependent description";
	private static final String date = "date";
	private static final int length = 3;
	private CPSCharacteristic sourceNumeric;
	private static final String csticTypeMasterDataFloat = "float";
	private static final int numberDecimals = 1;
	private static final int maxlength = 3;

	@Before
	public void initialize()
	{
		characteristicMasterData = new CPSMasterDataCharacteristicContainer();
		characteristicMasterData.setName(description);
		characteristicMasterDataNumeric = new CPSMasterDataCharacteristicContainer();
		characteristicMasterDataNumeric.setType(csticTypeMasterDataFloat);
		characteristicMasterDataNumeric.setNumberDecimals(Integer.valueOf(numberDecimals));
		characteristicMasterDataNumeric.setLength(Integer.valueOf(maxlength));

		MockitoAnnotations.initMocks(this);
		Mockito.when(masterDataService.getCharacteristic(kbId, characteristicId)).thenReturn(characteristicMasterData);
		classUnderTest.setMasterDataService(masterDataService);

		final CPSConfiguration config = new CPSConfiguration();
		config.setKbId(kbId);
		final CPSItem item = new CPSItem();
		item.setParentConfiguration(config);
		source = new CPSCharacteristic();
		source.setId(characteristicId);
		source.setParentItem(item);

		target = new CsticModelImpl();

		final CPSConfiguration configNum = new CPSConfiguration();
		configNum.setKbId(kbId);
		final CPSItem itemNum = new CPSItem();
		itemNum.setParentConfiguration(configNum);
		sourceNumeric = new CPSCharacteristic();
		sourceNumeric.setId(characteristicIdNumeric);
		sourceNumeric.setParentItem(itemNum);

		Mockito.when(masterDataService.getCharacteristic(kbId, characteristicIdNumeric))
				.thenReturn(characteristicMasterDataNumeric);
	}

	@Test
	public void testMasterDataService()
	{
		classUnderTest.setMasterDataService(masterDataService);
		assertEquals(masterDataService, classUnderTest.getMasterDataService());
	}

	@Test
	public void testPopulate()
	{
		classUnderTest.populate(source, target);
		assertEquals(description, target.getLanguageDependentName());
	}


	@Test
	public void testPopulateLengthNothingAssigned()
	{
		characteristicMasterData.setLength(null);
		classUnderTest.populate(source, target);
		assertEquals(0, target.getStaticDomainLength());
	}

	@Test
	public void testPopulateDecimals()
	{
		characteristicMasterData.setNumberDecimals(Integer.valueOf(length));
		classUnderTest.populate(source, target);
		assertEquals(length, target.getNumberScale());
	}

	@Test
	public void testPopulateValueType()
	{
		classUnderTest.populate(sourceNumeric, target);
		assertEquals(CsticModel.TYPE_FLOAT, target.getValueType());
	}

	@Test
	public void testPopulateValueTypeNothingInMasterData()
	{
		classUnderTest.populate(source, target);
		assertEquals(CsticModel.TYPE_STRING, target.getValueType());
	}

	@Test
	public void testPopulateValueTypeInt()
	{
		characteristicMasterDataNumeric.setType("int");
		classUnderTest.populate(sourceNumeric, target);
		assertEquals(CsticModel.TYPE_INTEGER, target.getValueType());
	}

	@Test
	public void testPopulateDecimalsNothingAssigned()
	{
		characteristicMasterData.setNumberDecimals(null);
		classUnderTest.populate(source, target);
		assertEquals(0, target.getNumberScale());
	}

	@Test
	public void testPopulateMaxFractions()
	{
		classUnderTest.populate(sourceNumeric, target);
		assertEquals(numberDecimals, target.getNumberScale());
	}

	@Test
	public void testPopulateMaxLength()
	{
		classUnderTest.populate(sourceNumeric, target);
		assertEquals(maxlength, target.getTypeLength());
	}

	@Test
	public void testNumberWithExponentEntryFieldMask()
	{
		final String exponentFieldMask = "___.____________E+SS";
		characteristicMasterDataNumeric.setEntryFieldMask(exponentFieldMask);
		classUnderTest.populate(sourceNumeric, target);
		assertEquals(exponentFieldMask, target.getEntryFieldMask());
	}

	@Test
	public void testGetValueType()
	{
		assertEquals(CsticModel.TYPE_DATE, classUnderTest.getValueType(date));
	}

	@Test
	public void testDateValueType()
	{
		characteristicMasterData.setType(date);
		classUnderTest.populate(source, target);
		assertEquals(CsticModel.TYPE_DATE, target.getValueType());
	}

}
