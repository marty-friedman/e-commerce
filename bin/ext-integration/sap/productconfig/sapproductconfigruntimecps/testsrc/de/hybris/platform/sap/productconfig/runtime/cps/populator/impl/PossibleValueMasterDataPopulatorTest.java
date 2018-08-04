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
import static org.junit.Assert.assertNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.ConfigurationMasterDataService;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSItem;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSPossibleValue;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;



@SuppressWarnings("javadoc")
@UnitTest
public class PossibleValueMasterDataPopulatorTest
{
	PossibleValueMasterDataPopulator classUnderTest = new PossibleValueMasterDataPopulator();
	@Mock
	private ConfigurationMasterDataService masterDataService;
	private CPSPossibleValue source;
	private CsticValueModel target;
	private static final String kbId = "99";
	private static final String characteristicId = "CSTIC_ID";
	private static final String valueString = "VALUE_ID";
	private static final String valueName = "Language dependent value name";

	@Before
	public void initialize()
	{
		final CPSConfiguration config = new CPSConfiguration();
		config.setKbId(kbId);
		final CPSItem item = new CPSItem();
		item.setParentConfiguration(config);
		final CPSCharacteristic cstic = new CPSCharacteristic();
		cstic.setId(characteristicId);
		cstic.setParentItem(item);

		source = new CPSPossibleValue();
		source.setValueLow(valueString);
		source.setParentCharacteristic(cstic);

		target = new CsticValueModelImpl();
		target.setNumeric(true);

		MockitoAnnotations.initMocks(this);
		Mockito.when(masterDataService.getValueName(kbId, characteristicId, valueString)).thenReturn(valueName);
		classUnderTest.setMasterDataService(masterDataService);
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
		assertEquals(valueName, target.getLanguageDependentName());
	}

	@Test
	public void testPopulateNoValueIdProvided()
	{
		source.setValueLow(null);
		classUnderTest.populate(source, target);
		assertNull(valueName, target.getLanguageDependentName());
	}

}
