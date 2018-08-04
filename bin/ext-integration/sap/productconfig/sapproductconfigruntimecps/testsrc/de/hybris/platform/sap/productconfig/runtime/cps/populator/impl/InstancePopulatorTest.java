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
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.sap.productconfig.runtime.cps.cache.MasterDataCacheAccessService;
import de.hybris.platform.sap.productconfig.runtime.cps.constants.SapproductconfigruntimecpsConstants;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristicGroup;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSItem;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSQuantity;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticGroupModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("javadoc")
@UnitTest
public class InstancePopulatorTest
{
	InstancePopulator classUnderTest = new InstancePopulator();

	private CPSItem source;
	private InstanceModel target;
	private static final String instanceId = "1";
	private static final String bomPosition = "10";
	private static final String instanceProdnr = "HT-1010";
	private static final String groupid = "GROUP 1";

	private CPSCharacteristicGroup characteristicGroup;

	private static final String kbId = "99";
	private static final String productId = "PRODUCT_ID";
	private static final String csticId = "CsticId";

	private static final String UOM = "PCE";

	private CPSCharacteristic characteristic;


	@SuppressWarnings("rawtypes")
	private final AbstractPopulatingConverter instanceConverter = new AbstractPopulatingConverter<CPSItem, InstanceModel>();
	@Mock
	private Converter<CPSCharacteristicGroup, CsticGroupModel> mockedGroupConverter;
	@Mock
	private Converter<CPSCharacteristic, CsticModel> mockedCsticConverter;

	@Mock
	private MasterDataCacheAccessService masterDataCacheAccessService;

	@Mock
	private I18NService i18NService;

	private final CPSMasterDataKnowledgeBaseContainer kbContainer = new CPSMasterDataKnowledgeBaseContainer();


	private final CPSQuantity quantity = new CPSQuantity();

	@SuppressWarnings("unchecked")
	@Before
	public void initialize()
	{
		MockitoAnnotations.initMocks(this);
		Mockito.when(i18NService.getCurrentLocale()).thenReturn(Locale.US);
		Mockito.when(masterDataCacheAccessService.getKbContainer(kbId, "en")).thenReturn(kbContainer);
		instanceConverter.setPopulators(Collections.singletonList(classUnderTest));
		instanceConverter.setTargetClass(InstanceModelImpl.class);
		classUnderTest.setInstanceModelConverter(instanceConverter);
		classUnderTest.setCharacteristicGroupConverter(mockedGroupConverter);
		classUnderTest.setCharacteristicConverter(mockedCsticConverter);
		classUnderTest.setMasterDataCacheAccessService(masterDataCacheAccessService);
		classUnderTest.setI18NService(i18NService);

		source = new CPSItem();
		source.setCharacteristicGroups(new ArrayList<>());
		source.setSubItems(new ArrayList<>());
		source.setCharacteristics(new ArrayList<>());
		source.setQuantity(quantity);
		source.setKey(productId);
		source.setId(instanceId);
		quantity.setUnit(UOM);
		final CPSConfiguration parentConfiguration = new CPSConfiguration();
		parentConfiguration.setKbId(kbId);
		parentConfiguration.setRootItem(source);
		source.setParentConfiguration(parentConfiguration);
		target = new InstanceModelImpl();

		//sub items
		final CPSItem subItem = new CPSItem();
		subItem.setId(instanceId);
		subItem.setParentItem(source);
		subItem.setParentConfiguration(parentConfiguration);
		subItem.setCharacteristicGroups(new ArrayList<>());
		subItem.setSubItems(new ArrayList<>());
		source.getSubItems().add(subItem);

		//groups
		characteristicGroup = new CPSCharacteristicGroup();
		characteristicGroup.setId(groupid);
		characteristicGroup.setParentItem(source);
		source.getCharacteristicGroups().add(characteristicGroup);

		//characteristics
		characteristic = new CPSCharacteristic();
		characteristic.setId(csticId);
		characteristic.setParentItem(source);
		characteristic.setPossibleValues(new ArrayList<>());
		characteristic.setValues(new ArrayList<>());
		source.getCharacteristics().add(characteristic);
	}

	@Test
	public void testCsticConverter()
	{
		assertEquals(mockedCsticConverter, classUnderTest.getCharacteristicConverter());
	}

	@Test
	public void testGroupModelConverter()
	{
		assertEquals(mockedGroupConverter, classUnderTest.getCharacteristicGroupConverter());
	}

	@Test
	public void testInstanceModelConverter()
	{
		assertEquals(instanceConverter, classUnderTest.getInstanceModelConverter());
	}

	@Test
	public void testPopulateId()
	{
		source.setId(instanceId);
		classUnderTest.populateCoreAttributes(source, target);
		assertNotNull(target);
		assertEquals(instanceId, target.getId());
	}

	@Test
	public void testPopulateBomPosition()
	{
		source.setBomPosition(bomPosition);
		classUnderTest.populateCoreAttributes(source, target);
		assertEquals(bomPosition, target.getPosition());
	}

	@Test
	public void testPopulateBomPositionName()
	{
		source.setKey(instanceProdnr);
		classUnderTest.populateCoreAttributes(source, target);
		assertEquals(instanceProdnr, target.getName());
	}

	@Test
	public void testPopulateComplete()
	{
		classUnderTest.populateCoreAttributes(source, target);
		assertFalse(target.isComplete());
		source.setComplete(true);
		classUnderTest.populateCoreAttributes(source, target);
		assertTrue(target.isComplete());
	}

	@Test
	public void testPopulateConsistent()
	{
		classUnderTest.populateCoreAttributes(source, target);
		assertFalse(target.isConsistent());
		source.setConsistent(true);
		classUnderTest.populateCoreAttributes(source, target);
		assertTrue(target.isConsistent());
	}

	@Test
	public void testPopulateSubItems()
	{
		classUnderTest.populate(source, target);
		assertNotNull(target);
		final List<InstanceModel> subInstances = target.getSubInstances();
		assertNotNull(subInstances);
		assertFalse(subInstances.isEmpty());
		assertEquals(1, subInstances.size());
		assertEquals(instanceId, subInstances.get(0).getId());
	}

	@Test
	public void testPopulateSubItemsNull()
	{
		source.setSubItems(null);
		classUnderTest.populate(source, target);
		assertNotNull(target);
		final List<InstanceModel> subInstances = target.getSubInstances();
		assertNotNull(subInstances);
		assertTrue(subInstances.isEmpty());
	}

	@Test
	public void testSubinstanceExistsInTarget()
	{
		final InstanceModel subinstance = new InstanceModelImpl();
		subinstance.setId(instanceId);
		target.getSubInstances().add(subinstance);
		assertEquals(1, target.getSubInstances().size());

		//The populator just adds to the list of target sub instances, so we expect 2 items now
		classUnderTest.populate(source, target);
		assertEquals(2, target.getSubInstances().size());
		assertEquals(instanceId, target.getSubInstances().get(0).getId());
	}

	@Test
	public void testPopulateGroups()
	{
		characteristicGroup.setParentItem(null);
		classUnderTest.populateGroups(source, target);
		final List<CsticGroupModel> csticGroups = target.getCsticGroups();
		assertNotNull(csticGroups);
		assertEquals(1, csticGroups.size());
	}

	@Test
	public void testPopulateDefaultAndNotDefaultGroups()
	{
		// non default group
		characteristicGroup.setParentItem(null);

		//default group
		final CPSCharacteristicGroup defaultCharacteristicGroup = new CPSCharacteristicGroup();
		defaultCharacteristicGroup.setId(SapproductconfigruntimecpsConstants.CPS_GENERAL_GROUP_ID);
		defaultCharacteristicGroup.setParentItem(null);
		source.getCharacteristicGroups().add(defaultCharacteristicGroup);

		final CsticGroupModel characteristicGroupModel = new CsticGroupModelImpl();
		characteristicGroupModel.setName(characteristicGroup.getId());
		final CsticGroupModel defaultCharacteristicGroupModel = new CsticGroupModelImpl();
		defaultCharacteristicGroupModel.setName(InstanceModel.GENERAL_GROUP_NAME);

		Mockito.when(mockedGroupConverter.convert(characteristicGroup)).thenReturn(characteristicGroupModel);
		Mockito.when(mockedGroupConverter.convert(defaultCharacteristicGroup)).thenReturn(defaultCharacteristicGroupModel);

		classUnderTest.populateGroups(source, target);

		final List<CsticGroupModel> csticGroups = target.getCsticGroups();
		assertNotNull(csticGroups);
		assertEquals(2, csticGroups.size());
		// default group has to be the first in the list
		assertEquals(InstanceModel.GENERAL_GROUP_NAME, csticGroups.get(0).getName());
		assertEquals(groupid, csticGroups.get(1).getName());
	}

	@Test
	public void testPopulateCstics()
	{
		classUnderTest.populateCstics(source, target);
		final List<CsticModel> characteristcis = target.getCstics();
		assertNotNull(characteristcis);
		assertEquals(1, characteristcis.size());
	}

	@Test
	public void testPopulateCsticsNull()
	{
		source.setCharacteristics(null);
		classUnderTest.populateCstics(source, target);
		final List<CsticModel> characteristcis = target.getCstics();
		assertNotNull(characteristcis);
		assertTrue(characteristcis.isEmpty());
	}

	@Test
	public void testPopulateGroupsKeysHaveBeenPushedDown()
	{
		final CPSConfiguration config = new CPSConfiguration();
		config.setKbId(kbId);

		source.setId(productId);
		config.setRootItem(source);

		source.setParentConfiguration(config);
		classUnderTest.populateGroups(source, target);
		classUnderTest.populateCstics(source, target);
		assertEquals(kbId, characteristicGroup.getParentItem().getParentConfiguration().getKbId());
		assertEquals(productId, characteristicGroup.getParentItem().getParentConfiguration().getRootItem().getId());
		assertEquals(kbId, characteristic.getParentItem().getParentConfiguration().getKbId());
	}

	@Test
	public void testPopulateTakesCareOfGroups()
	{
		classUnderTest.populate(source, target);
		final List<CsticGroupModel> csticGroups = target.getCsticGroups();
		assertNotNull(csticGroups);
		assertEquals(1, csticGroups.size());
	}

	@Test
	public void testMasterDataCacheAccessService()
	{
		assertEquals(masterDataCacheAccessService, classUnderTest.getMasterDataCacheAccessService());
	}

	@Test
	public void testI18NService()
	{
		assertEquals(i18NService, classUnderTest.getI18NService());
	}

	@Test(expected = NullPointerException.class)
	public void testPopulateRootUOMToMasterDataCacheNoParentConfiguration()
	{
		source.setParentConfiguration(null);
		classUnderTest.populateRootUOMToMasterDataCache(source);
	}

	@Test(expected = NullPointerException.class)
	public void testPopulateRootUOMToMasterDataCacheNoKbId()
	{
		source.getParentConfiguration().setKbId(null);
		classUnderTest.populateRootUOMToMasterDataCache(source);
	}

	@Test
	public void testPopulateUOMToMasterDataCache()
	{
		classUnderTest.populateUOMToMasterDataCache(source, source.getParentConfiguration());
		assertEquals(UOM, kbContainer.getRootUnitOfMeasure());
	}


}
