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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.ConfigurationMasterDataService;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConflict;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSItem;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.SolvableConflictModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.SolvableConflictModelImpl;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("javadoc")
@UnitTest
public class ConfigurationPopulatorTest
{
	ConfigurationPopulator classUnderTest;
	private ConfigModel target;
	private CPSConfiguration source;
	private final CPSItem rootItem = new CPSItem();
	private static final String ROOT_ITEM_ID = "A";
	private static final String CFG_ID = "1";
	private static final String KB_ID = "99";
	private static final String PRODUCT_KEY = "pCode";

	@Mock
	private Converter<CPSItem, InstanceModel> mockedInstanceConverter;
	@Mock
	private ConfigurationMasterDataService masterDataService;
	@Mock
	private Converter<CPSConflict, SolvableConflictModel> conflictPopulator;

	@Before
	public void initialize()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = spy(new ConfigurationPopulator());
		doReturn("There are conflicting options, please change your configuration").when(classUnderTest)
				.callLocalization(ConfigurationPopulator.SAPPRODUCTCONFIG_CPS_HEADER_CONFLICT_MESSAGE);

		target = new ConfigModelImpl();
		source = new CPSConfiguration();
		rootItem.setId(ROOT_ITEM_ID);
		rootItem.setParentConfiguration(source);
		source.setRootItem(rootItem);

		classUnderTest.setInstanceModelConverter(mockedInstanceConverter);
		classUnderTest.setMasterDataService(masterDataService);
		classUnderTest.setConflictModelConverter(conflictPopulator);
		Mockito.when(Boolean.valueOf(masterDataService.isProductMultilevel(KB_ID, PRODUCT_KEY))).thenReturn(Boolean.FALSE);
		source.setId(CFG_ID);
		source.setKbId(KB_ID);
		source.setProductKey(PRODUCT_KEY);
	}

	@Test
	public void testPopulateId()
	{
		classUnderTest.populate(source, target);
		assertEquals(CFG_ID, target.getId());
	}

	@Test
	public void testPopulateKbId()
	{
		classUnderTest.populate(source, target);
		assertEquals(KB_ID, target.getKbId());
	}

	@Test
	public void testPopulateName()
	{
		classUnderTest.populate(source, target);
		assertEquals(PRODUCT_KEY, target.getName());
	}

	@Test
	public void testPopulateConsistent()
	{
		source.setConsistent(true);
		classUnderTest.populate(source, target);
		assertTrue(target.isConsistent());
	}

	@Test
	public void testPopulateComplete()
	{
		source.setComplete(true);
		classUnderTest.populate(source, target);
		assertTrue(target.isComplete());
	}

	@Test
	public void testPopulateRootItem()
	{
		rootItem.setParentConfiguration(null);
		given(mockedInstanceConverter.convert(rootItem)).willReturn(new InstanceModelImpl());
		classUnderTest.populate(source, target);
		final InstanceModel rootInstance = target.getRootInstance();
		assertNotNull(rootInstance);
	}

	@Test
	public void testKbIdAtSubItem()
	{
		classUnderTest.populate(source, target);
		final CPSItem cloudEngineItem = source.getRootItem();
		assertNotNull(cloudEngineItem);
		assertEquals(KB_ID, cloudEngineItem.getParentConfiguration().getKbId());
	}

	@Test
	public void testPopulateInConsistent()
	{
		source.setConsistent(false);
		classUnderTest.populateCoreAttributes(source, target);
		assertFalse(target.isConsistent());
	}

	@Test
	public void testPopulateConflicts()
	{
		final CPSConflict conflict = new CPSConflict();
		final String conflictId = "1";
		conflict.setId(conflictId);
		conflict.setExplanation("This is a conflict");
		conflict.setName("CONFLICT_1");
		conflict.setType(1);
		final List<CPSConflict> conflicts = new ArrayList<>();
		conflicts.add(conflict);
		source.setConflicts(conflicts);

		final SolvableConflictModel solvableConflict = new SolvableConflictModelImpl();
		solvableConflict.setId(conflictId);
		Mockito.when(conflictPopulator.convert(conflict)).thenReturn(solvableConflict);

		classUnderTest.populateConflicts(source, target);
		assertNotNull(target.getSolvableConflicts());
		assertEquals(conflictId, target.getSolvableConflicts().get(0).getId());
	}

	@Test
	public void testPopulateConflictsWithNullList()
	{
		source.setConflicts(null);
		classUnderTest.populateConflicts(source, target);
		assertNull(target.getSolvableConflicts());
	}

	@Test
	public void testPopulateConflictsWithEmptyList()
	{
		source.setConflicts(new ArrayList<>());
		classUnderTest.populateConflicts(source, target);
		assertNull(target.getSolvableConflicts());
	}




	@Test
	public void testPopulateSinglelevel()
	{
		Mockito.when(Boolean.valueOf(masterDataService.isProductMultilevel(KB_ID, PRODUCT_KEY))).thenReturn(Boolean.FALSE);
		classUnderTest.populate(source, target);
		assertTrue(target.isSingleLevel());
	}

	public void testPopulateMultilevel()
	{
		Mockito.when(Boolean.valueOf(masterDataService.isProductMultilevel(KB_ID, PRODUCT_KEY))).thenReturn(Boolean.TRUE);
		classUnderTest.populate(source, target);
		assertFalse(target.isSingleLevel());
	}

}
