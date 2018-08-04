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
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.ConfigurationMasterDataService;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSItem;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSValue;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;



@SuppressWarnings("javadoc")
@UnitTest
public class ValueMasterDataPopulatorTest
{
	ValueMasterDataPopulator classUnderTest = new ValueMasterDataPopulator();
	@Mock
	private ConfigurationMasterDataService masterDataService;
	private CPSValue source;
	private CsticValueModel target;
	private static final String kbId = "99";
	private static final String characteristicId = "CSTIC_ID";
	private static final String valueString = "VALUE_ID";
	private static final String valueName = "Language dependent value name";
	private CPSValue sourceNumeric;

	@Before
	public void initialize()
	{
		MockitoAnnotations.initMocks(this);

		final CPSConfiguration cpsConfig = new CPSConfiguration();
		cpsConfig.setKbId(kbId);
		final CPSItem cpsItem = new CPSItem();
		cpsItem.setParentConfiguration(cpsConfig);
		final CPSCharacteristic cspCstic = new CPSCharacteristic();
		cspCstic.setId(characteristicId);
		cspCstic.setParentItem(cpsItem);

		final CPSConfiguration cpsConfigNum = new CPSConfiguration();
		cpsConfigNum.setKbId(kbId);
		final CPSItem cpsItemNum = new CPSItem();
		cpsItemNum.setParentConfiguration(cpsConfigNum);
		final CPSCharacteristic cspCsticNum = new CPSCharacteristic();
		cspCsticNum.setId(characteristicId);
		cspCsticNum.setParentItem(cpsItemNum);

		source = new CPSValue();
		source.setParentCharacteristic(cspCstic);
		sourceNumeric = new CPSValue();
		sourceNumeric.setParentCharacteristic(cspCsticNum);
		target = new CsticValueModelImpl();

		source.setValue(valueString);
		sourceNumeric.setValue(null);

		classUnderTest.setMasterDataService(masterDataService);
		Mockito.when(masterDataService.getValueName(kbId, characteristicId, valueString)).thenReturn(valueName);
		Mockito.when(Boolean.valueOf(masterDataService.isCharacteristicNumeric(kbId, characteristicId))).thenReturn(Boolean.TRUE);
	}

	@Test
	public void testGetMasterDataService()
	{
		assertEquals(masterDataService, classUnderTest.getMasterDataService());
	}

	@Test
	public void testPopulateWithNotNullOrNotEmptyValue()
	{
		classUnderTest.populate(source, target);
		assertEquals(valueName, target.getLanguageDependentName());
	}

	@Test
	public void testPopulateWithNullValue()
	{
		classUnderTest.populate(sourceNumeric, target);
		assertTrue(StringUtils.isEmpty(target.getLanguageDependentName()));
	}

	@Test
	public void testPopulateWithEmptyValue()
	{
		sourceNumeric.setValue("");
		classUnderTest.populate(sourceNumeric, target);
		assertTrue(StringUtils.isEmpty(target.getLanguageDependentName()));
	}

	@Test
	public void testPopulateNumeric()
	{
		sourceNumeric.setValue("1.0");
		classUnderTest.populate(sourceNumeric, target);
		assertTrue(target.isNumeric());
	}

}
