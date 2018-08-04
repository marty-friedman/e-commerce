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
package de.hybris.platform.sap.productconfig.frontend.util.impl;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.GroupStatusType;
import de.hybris.platform.sap.productconfig.facades.GroupType;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.frontend.UiCsticStatus;
import de.hybris.platform.sap.productconfig.frontend.UiGroupStatus;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;
import de.hybris.platform.sap.productconfig.frontend.controllers.AbstractProductConfigControllerBaseTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class UiStatusSyncTest extends AbstractProductConfigControllerBaseTest
{

	private UiStatusSync classUnderTest;

	@Before
	public void setup()
	{
		classUnderTest = new UiStatusSync();
	}

	@Test
	public void testStoreLastNoneConflictGroupId()
	{
		final UiStatus newUiState = new UiStatus();
		final ConfigurationData requestData = new ConfigurationData();
		requestData.setGroupIdToDisplay("group123");
		classUnderTest.storeLastNoneConflictGroupId(newUiState, requestData);
		assertEquals("group123", newUiState.getLastNoneConflictGroupId());
	}

	@Test
	public void testStoreLastNoneConflictGroupId_conflictGroup()
	{
		final UiStatus newUiState = new UiStatus();
		newUiState.setLastNoneConflictGroupId("group123");
		final ConfigurationData requestData = new ConfigurationData();
		requestData.setGroupIdToDisplay("CONFLICT_group123");
		classUnderTest.storeLastNoneConflictGroupId(newUiState, requestData);
		assertEquals("group123", newUiState.getLastNoneConflictGroupId());
	}

	@Test
	public void tesApplyUiStausToConfiguration()
	{
		final ConfigurationData configData = createEmptyConfigData();

		List<UiGroupData> groups = new ArrayList<>();
		final UiGroupData uiGroupData = createUiGroup("1", GroupStatusType.ERROR, true);
		final List<CsticData> cstics = new ArrayList<>();
		final CsticData cstic = new CsticData();
		cstic.setKey("ABC");
		cstic.setLongText("lorem ipsum");
		cstic.setShowFullLongText(false);
		cstics.add(cstic);
		uiGroupData.setCstics(cstics);
		groups.add(uiGroupData);

		groups.add(createUiGroup("2", GroupStatusType.WARNING, true));
		groups.add(createUiGroup("3", GroupStatusType.DEFAULT, true));
		configData.setGroups(groups);

		configData.setSpecificationTreeCollapsed(false);
		configData.setPriceSummaryCollapsed(true);

		final UiStatus uiStatus = new UiStatus();
		final List<UiGroupStatus> uiGroups = new ArrayList<>();
		final UiGroupStatus uiGroupStatus = createUiGroupStatus("1", false);

		final List<UiCsticStatus> csticsStatus = new ArrayList<>();
		final UiCsticStatus csticStatus = new UiCsticStatus();
		csticStatus.setId(cstics.get(0).getKey());
		csticStatus.setShowFullLongText(true);
		csticsStatus.add(csticStatus);
		uiGroupStatus.setCstics(csticsStatus);

		uiGroups.add(uiGroupStatus);
		uiGroups.add(createUiGroupStatus("2", true));
		uiGroups.add(createUiGroupStatus("3", false));

		uiStatus.setGroups(uiGroups);
		uiStatus.setPriceSummaryCollapsed(false);
		uiStatus.setSpecificationTreeCollapsed(true);

		//uiStatus.setGroupIdToDisplay("3");

		classUnderTest.applyUiStatusToConfiguration(configData, uiStatus);

		groups = configData.getGroups();
		assertEquals(Boolean.FALSE, Boolean.valueOf(groups.get(0).isCollapsed()));
		assertEquals(Boolean.TRUE, Boolean.valueOf(groups.get(1).isCollapsed()));
		assertEquals(Boolean.FALSE, Boolean.valueOf(groups.get(2).isCollapsed()));

		assertEquals(Boolean.FALSE, Boolean.valueOf(configData.isPriceSummaryCollapsed()));
		assertEquals(Boolean.TRUE, Boolean.valueOf(configData.isSpecificationTreeCollapsed()));

		assertEquals(Boolean.TRUE, Boolean.valueOf(groups.get(0).getCstics().get(0).isShowFullLongText()));
	}

	@Test
	public void tesApplyUiStausToConfigurationWithConflict()
	{
		final ConfigurationData configData = createEmptyConfigData();

		List<UiGroupData> groups = new ArrayList<>();
		final UiGroupData uiConflictData = createUiConflictGroupsWOCstics(new String[]
		{ "Conflict1" });
		groups.add(uiConflictData);

		final UiGroupData uiGroupData = createUiGroup("1", GroupStatusType.ERROR, true);
		final List<CsticData> cstics = new ArrayList<>();
		final CsticData cstic = new CsticData();
		cstic.setKey("ABC");
		cstic.setLongText("lorem ipsum");
		cstic.setShowFullLongText(false);
		cstics.add(cstic);
		uiGroupData.setCstics(cstics);
		groups.add(uiGroupData);

		groups.add(createUiGroup("2", GroupStatusType.WARNING, true));
		groups.add(createUiGroup("3", GroupStatusType.DEFAULT, true));
		configData.setGroups(groups);

		final UiStatus uiStatus = new UiStatus();
		final List<UiGroupStatus> uiGroups = new ArrayList<>();
		final UiGroupStatus uiGroupStatus = createUiGroupStatus("1", false);

		final List<UiCsticStatus> csticsStatus = new ArrayList<>();
		final UiCsticStatus csticStatus = new UiCsticStatus();
		csticStatus.setId(cstics.get(0).getKey());
		csticStatus.setShowFullLongText(true);
		csticsStatus.add(csticStatus);
		uiGroupStatus.setCstics(csticsStatus);

		uiGroups.add(uiGroupStatus);
		uiGroups.add(createUiGroupStatus("2", true));
		uiGroups.add(createUiGroupStatus("3", false));

		uiStatus.setGroups(uiGroups);
		uiStatus.setPriceSummaryCollapsed(false);
		uiStatus.setSpecificationTreeCollapsed(true);

		classUnderTest.applyUiStatusToConfiguration(configData, uiStatus);

		groups = configData.getGroups();
		assertEquals(GroupType.CONFLICT_HEADER, groups.get(0).getGroupType());
		assertEquals(Boolean.FALSE, Boolean.valueOf(groups.get(0).isCollapsed()));

		assertEquals(GroupType.CSTIC_GROUP, groups.get(1).getGroupType());
		assertEquals(Boolean.FALSE, Boolean.valueOf(groups.get(1).isCollapsed()));
		assertEquals(GroupStatusType.ERROR, groups.get(1).getGroupStatus());

		assertEquals(GroupType.CSTIC_GROUP, groups.get(2).getGroupType());
		assertEquals(Boolean.TRUE, Boolean.valueOf(groups.get(2).isCollapsed()));
		assertEquals(GroupStatusType.WARNING, groups.get(2).getGroupStatus());

		assertEquals(GroupType.CSTIC_GROUP, groups.get(3).getGroupType());
		assertEquals(Boolean.FALSE, Boolean.valueOf(groups.get(3).isCollapsed()));
		assertEquals(GroupStatusType.DEFAULT, groups.get(3).getGroupStatus());

		final UiGroupData uiGroupConflict = groups.get(0).getSubGroups().get(0);
		assertEquals(GroupType.CONFLICT, uiGroupConflict.getGroupType());
		assertEquals(Boolean.FALSE, Boolean.valueOf(uiGroupConflict.isCollapsed()));

	}

	@Test
	public void testStatusInitialUiGroupStatus()
	{
		final ConfigurationData configData = createEmptyConfigData();

		List<UiGroupData> groups = new ArrayList<>();
		groups.add(createUiGroup("1", GroupStatusType.ERROR, true));
		groups.add(createUiGroup("2", GroupStatusType.WARNING, true));
		groups.add(createUiGroup("3", GroupStatusType.DEFAULT, true));
		configData.setGroups(groups);

		classUnderTest.setInitialStatus(configData);

		groups = configData.getGroups();
		assertEquals(Boolean.FALSE, Boolean.valueOf(groups.get(0).isCollapsed()));
		assertEquals(Boolean.TRUE, Boolean.valueOf(groups.get(1).isCollapsed()));
		assertEquals(Boolean.TRUE, Boolean.valueOf(groups.get(2).isCollapsed()));

		assertEquals(Boolean.FALSE, Boolean.valueOf(configData.isPriceSummaryCollapsed()));
		assertEquals(Boolean.FALSE, Boolean.valueOf(configData.isSpecificationTreeCollapsed()));

		assertEquals("1", configData.getGroupIdToDisplay());
	}

	@Test
	public void testStatusInitialStatus_picture()
	{
		final ConfigurationData configData = createEmptyConfigData();
		final List<UiGroupData> groups = new ArrayList<>();
		configData.setGroups(groups);
		groups.add(createUiGroup("3", GroupStatusType.DEFAULT, true));

		classUnderTest.setInitialStatus(configData);

		assertEquals(Boolean.TRUE, Boolean.valueOf(configData.isHideImageGallery()));
	}

	@Test
	public void testStatusInitialUiGroupStatus_collapsedInSpecTree()
	{
		final ConfigurationData configData = createEmptyConfigData();

		List<UiGroupData> groups = new ArrayList<>();
		groups.add(createUiGroup("1", GroupStatusType.ERROR, true));
		final List<UiGroupData> subGroups = new ArrayList<>();
		subGroups.add(createUiGroup("1.1", GroupStatusType.ERROR, true));
		subGroups.add(createUiGroup("1.2", GroupStatusType.WARNING, true));
		groups.get(0).setSubGroups(subGroups);
		groups.add(createUiGroup("2", GroupStatusType.DEFAULT, true));
		configData.setGroups(groups);

		classUnderTest.setInitialStatus(configData);

		groups = configData.getGroups();
		assertEquals(Boolean.FALSE, Boolean.valueOf(groups.get(0).isCollapsedInSpecificationTree()));
		assertEquals(Boolean.FALSE, Boolean.valueOf(groups.get(1).isCollapsedInSpecificationTree()));
		assertEquals(Boolean.TRUE, Boolean.valueOf(groups.get(0).getSubGroups().get(0).isCollapsedInSpecificationTree()));
		assertEquals(Boolean.TRUE, Boolean.valueOf(groups.get(0).getSubGroups().get(1).isCollapsedInSpecificationTree()));
	}

	@Test
	public void testStatusInitialUiGroupStatus_nonConfigurable()
	{
		final ConfigurationData configData = createEmptyConfigData();

		List<UiGroupData> groups = new ArrayList<>();
		final UiGroupData csticGroup = createUiGroup("1", GroupStatusType.ERROR, true);
		csticGroup.setConfigurable(false);
		csticGroup.setCstics(createCsticsList());
		groups.add(csticGroup);
		groups.add(createUiGroup("2", GroupStatusType.WARNING, true));
		groups.add(createUiGroup("3", GroupStatusType.DEFAULT, true));
		configData.setGroups(groups);

		classUnderTest.setInitialStatus(configData);

		groups = configData.getGroups();
		assertEquals(Boolean.TRUE, Boolean.valueOf(groups.get(0).isCollapsed()));
		assertEquals(Boolean.FALSE, Boolean.valueOf(groups.get(0).getCstics().get(0).isShowFullLongText()));
		assertEquals(Boolean.FALSE, Boolean.valueOf(groups.get(1).isCollapsed()));
		assertEquals(Boolean.TRUE, Boolean.valueOf(groups.get(2).isCollapsed()));

		assertEquals(Boolean.FALSE, Boolean.valueOf(configData.isPriceSummaryCollapsed()));
		assertEquals(Boolean.FALSE, Boolean.valueOf(configData.isSpecificationTreeCollapsed()));
	}

	@Test
	public void testExtractUiStatusFromConfiguration_uiGroupsStatus()
	{
		final ConfigurationData configData = createEmptyConfigData();

		final List<UiGroupData> groups = new ArrayList<>();
		groups.add(createUiGroup("1", GroupStatusType.ERROR, true));
		groups.add(createUiGroup("2", GroupStatusType.WARNING, true));
		groups.add(createUiGroup("3", GroupStatusType.DEFAULT, false));
		configData.setGroups(groups);

		configData.setSpecificationTreeCollapsed(false);
		configData.setPriceSummaryCollapsed(true);

		final UiStatus uiStatus = classUnderTest.extractUiStatusFromConfiguration(configData);

		final List<UiGroupStatus> uiGroupsStatus = uiStatus.getGroups();

		assertEquals(Boolean.TRUE, Boolean.valueOf(uiGroupsStatus.get(0).isCollapsed()));
		assertEquals(Boolean.TRUE, Boolean.valueOf(uiGroupsStatus.get(1).isCollapsed()));
		assertEquals(Boolean.FALSE, Boolean.valueOf(uiGroupsStatus.get(2).isCollapsed()));

		assertEquals(Boolean.TRUE, Boolean.valueOf(uiStatus.isPriceSummaryCollapsed()));
		assertEquals(Boolean.FALSE, Boolean.valueOf(uiStatus.isSpecificationTreeCollapsed()));
	}

	@Test
	public void testExtractUiStatusFromConfiguration_uiCsticStatus()
	{
		final ConfigurationData configData = createEmptyConfigData();

		final UiGroupData uiGroup = createUiGroup("1", GroupStatusType.DEFAULT, true);
		final List<CsticData> cstics = createCsticsList();

		final CsticData cstic = cstics.get(0);
		cstic.setLongText("lorem ipsum");
		cstic.setShowFullLongText(true);
		uiGroup.setCstics(cstics);
		final List<UiGroupData> groups = new ArrayList<>();
		groups.add(uiGroup);
		configData.setGroups(groups);

		final UiStatus uiStatus = classUnderTest.extractUiStatusFromConfiguration(configData);
		final List<UiGroupStatus> uiGroupsStatus = uiStatus.getGroups();
		final List<UiCsticStatus> uiCsticStatus = uiGroupsStatus.get(0).getCstics();

		assertEquals(cstics.size(), uiCsticStatus.size());


	}

	@Test
	public void testExtractUiStatusFromConfiguration_conflictNumberStatus()
	{
		final ConfigurationData configData = createEmptyConfigData();
		List<UiGroupData> groups = createEmptyGroup();
		configData.setGroups(groups);

		UiStatus uiStatus = classUnderTest.extractUiStatusFromConfiguration(configData);
		int numberOfConflicts = uiStatus.getNumberOfConflictsToDisplay();

		assertEquals(0, numberOfConflicts);

		final String[] conflictsIds = new String[]
		{ "Conflict1", "Conflict2", "Conflict3" };
		final UiGroupData uiGroup = createUiConflictGroupsWOCstics(conflictsIds);
		final List<CsticData> cstics = createCsticsList();

		final CsticData cstic = cstics.get(0);
		cstic.setLongText("lorem ipsum");
		cstic.setShowFullLongText(true);
		uiGroup.setCstics(cstics);

		groups = new ArrayList<>();
		groups.add(uiGroup);
		configData.setGroups(groups);

		uiStatus = classUnderTest.extractUiStatusFromConfiguration(configData);
		numberOfConflicts = uiStatus.getNumberOfConflictsToDisplay();

		assertEquals(conflictsIds.length, numberOfConflicts);


	}

	protected UiGroupStatus createUiGroupStatus(final String id, final boolean collapsed)
	{
		final UiGroupStatus uiGroup = new UiGroupStatus();

		uiGroup.setId(id);
		uiGroup.setCollapsed(collapsed);
		uiGroup.setCollapsedInSpecificationTree(collapsed);

		return uiGroup;
	}

	@Test
	public void testUpdateUIStatusFromRequest_noChange()
	{
		final UiStatus oldUiSate = createUiStatusForSimpleTest();
		final ConfigurationData requestData = craeteConfigDataForSimpleTest(true, false, true);

		final UiStatus newUiSate = classUnderTest.updateUIStatusFromRequest(requestData, oldUiSate, null);
		assertNotNull("no new UI-State retruned", newUiSate);
		assertEquals("collapsed Group missing in new uiSate", 3, newUiSate.getGroups().size());
	}

	@Test
	public void testUpdateUIStatusFromRequest_inconsistentGroupsNoException()
	{
		final UiStatus oldUiSate = createUiStatusForSimpleTest();
		oldUiSate.setGroups(Collections.EMPTY_LIST);
		final ConfigurationData requestData = craeteConfigDataForSimpleTest(true, false, true);

		final UiStatus newUiSate = classUnderTest.updateUIStatusFromRequest(requestData, oldUiSate, null);
		assertNotNull("no new UI-State retruned", newUiSate);
	}

	@Test
	public void testUpdateUIStatusFromRequest_nullGroupsNoException()
	{
		final UiStatus oldUiSate = createUiStatusForSimpleTest();
		final ConfigurationData requestData = craeteConfigDataForSimpleTest(true, false, true);
		requestData.getGroups().add(new UiGroupData());

		final UiStatus newUiSate = classUnderTest.updateUIStatusFromRequest(requestData, oldUiSate, null);
		assertNotNull("no new UI-State retruned", newUiSate);
	}

	@Test
	public void testUpdateUIStatusFromRequest_inconsistentCsticNoException()
	{
		final UiStatus oldUiSate = createUiStatusForSimpleTest();
		oldUiSate.getGroups().get(0).getCstics().get(0).setId("changed");
		final ConfigurationData requestData = craeteConfigDataForSimpleTest(true, false, true);

		final UiStatus newUiSate = classUnderTest.updateUIStatusFromRequest(requestData, oldUiSate, null);
		assertNotNull("no new UI-State retruned", newUiSate);
	}

	@Test
	public void testUpdateUIStatusFromRequest_noOldState()
	{
		final ConfigurationData requestData = craeteConfigDataForSimpleTest(true, true, true);

		final UiStatus newUiSate = classUnderTest.updateUIStatusFromRequest(requestData, null, null);
		assertNotNull("no new UI-State retruned", newUiSate);
		assertEquals("collapsed Group missing in new uiSate", 3, newUiSate.getGroups().size());

	}

	@Test
	public void testUpdateUIStatusFromRequest_baseData()
	{
		final UiStatus oldUiSate = createUiStatusForSimpleTest();
		final ConfigurationData requestData = craeteConfigDataForSimpleTest(false, false, false);
		requestData.setPriceSummaryCollapsed(true);
		requestData.setSpecificationTreeCollapsed(false);
		requestData.setGroupIdToDisplay("group123");

		final UiStatus newUiSate = classUnderTest.updateUIStatusFromRequest(requestData, oldUiSate, null);
		assertTrue("isPriceSummaryCollapsed not mapped from old to new UI-State", newUiSate.isPriceSummaryCollapsed());
		assertFalse("isSpecificationTreeCollapsed not mapped from old to new UI-State", newUiSate.isSpecificationTreeCollapsed());
		assertEquals("id of last non conflict group not set", requestData.getGroupIdToDisplay(),
				newUiSate.getLastNoneConflictGroupId());
	}

	@Test
	public void testUpdateUIStatusFromRequest_expandCsticLongText()
	{
		final UiStatus oldUiSate = createUiStatusForSimpleTest();
		final ConfigurationData requestData = craeteConfigDataForSimpleTest(true, false, true);
		requestData.getGroups().get(0).getCstics().get(0).setShowFullLongText(true);

		final UiStatus newUiSate = classUnderTest.updateUIStatusFromRequest(requestData, oldUiSate, null);
		assertTrue("expand state of cstcic long text lost", newUiSate.getGroups().get(0).getCstics().get(0).isShowFullLongText());
	}

	@Test
	public void testUpdateUIStatusFromRequest_groupVisited()
	{
		final UiStatus oldUiSate = createUiStatusForSimpleTest();
		final ConfigurationData requestData = craeteConfigDataForSimpleTest(true, false, true);
		requestData.getGroups().get(0).setVisited(true);

		final UiStatus newUiSate = classUnderTest.updateUIStatusFromRequest(requestData, oldUiSate, null);
		assertTrue("visited state lost", newUiSate.getGroups().get(0).isVisited());
	}

	@Test
	public void testUpdateUIStatusFromRequest_groupVisitedSticky()
	{
		final UiStatus oldUiSate = createUiStatusForSimpleTest();
		oldUiSate.getGroups().get(0).setVisited(true);
		final ConfigurationData requestData = craeteConfigDataForSimpleTest(true, false, true);
		requestData.getGroups().get(0).setVisited(false);

		final UiStatus newUiSate = classUnderTest.updateUIStatusFromRequest(requestData, oldUiSate, null);
		assertTrue("visited state lost", newUiSate.getGroups().get(0).isVisited());
	}

	@Test
	public void testUpdateShowFullLongTextinUIStatusGroups_True()
	{
		final UiStatus uiState = createUiStatus(5, false);
		final List<UiGroupStatus> uiStatusGroups = uiState.getGroups();

		classUnderTest.updateShowFullLongTextinUIStatusGroups("group_2_cstic_3", true, uiStatusGroups);

		final UiCsticStatus csticToCheck = uiStatusGroups.get(1).getCstics().get(2);
		assertTrue("The cstic '" + csticToCheck.getId() + "' should have 'TRUE' as a value of 'showFullLongText': ",
				csticToCheck.isShowFullLongText());
	}

	@Test
	public void testUpdateShowFullLongTextinUIStatusGroups_False()
	{
		final UiStatus uiState = createUiStatus(5, true);
		final List<UiGroupStatus> uiStatusGroups = uiState.getGroups();

		classUnderTest.updateShowFullLongTextinUIStatusGroups("group_3_cstic_1", false, uiStatusGroups);

		final UiCsticStatus csticToCheck = uiStatusGroups.get(2).getCstics().get(0);
		assertFalse("The cstic '" + csticToCheck.getId() + "' should have 'FALSE' as a value of 'showFullLongText': ",
				csticToCheck.isShowFullLongText());
	}

	@Test
	public void testReplaceNewLineForLogStringWithoutNewLine()
	{
		final String strWithoutNewLine = "productCode";

		final String result = classUnderTest.replaceNewLineForLog(strWithoutNewLine);

		assertEquals(strWithoutNewLine, result);
	}

	@Test
	public void testReplaceNewLineForLogStringWithNewLine()
	{
		final String strWithoutNewLine = "productCode\nlog forging text";
		final String expectedString = "productCode_log forging text";

		final String result = classUnderTest.replaceNewLineForLog(strWithoutNewLine);

		assertEquals(expectedString, result);
	}


	private List<UiGroupData> createListOfUiGroupData(final int numberOfGroups, final boolean showFullLongtext)
	{
		final List<UiGroupData> groups = new ArrayList<>();

		for (int i = 0; i < numberOfGroups; i++)
		{
			final int index = i + 1;
			final UiGroupData uiGroupData = createUiGroupWithCstics(index, numberOfGroups, showFullLongtext);
			groups.add(uiGroupData);
		}

		return groups;
	}

	private UiGroupData createUiGroupWithCstics(final int groupId, final int numberOfUiGroups, final boolean showFullLongtext)
	{
		UiGroupData uiGroupData = new UiGroupData();
		for (int i = 0; i < numberOfUiGroups; i++)
		{
			final String id = "group_" + groupId;
			uiGroupData = createUiGroup(id, GroupStatusType.DEFAULT, true);

			final List<CsticData> cstics = createListOfCstics(id, numberOfUiGroups, showFullLongtext);
			uiGroupData.setCstics(cstics);
		}
		return uiGroupData;
	}

	private List<CsticData> createListOfCstics(final String groupId, final int numberOfUiGroups, final boolean showFullLongtext)
	{
		final List<CsticData> cstics = new ArrayList<>();
		for (int i = 0; i < numberOfUiGroups; i++)
		{
			final int index = i + 1;
			final CsticData cstic = new CsticData();
			cstic.setKey(groupId + "_cstic_" + index);
			cstic.setLongText("The long text for '" + cstic.getKey() + "'");
			cstic.setShowFullLongText(showFullLongtext);
			cstics.add(cstic);
		}
		return cstics;
	}

	protected UiStatus createUiStatus(final int numberOfGroups, final boolean showFullLongText)
	{
		final List<UiGroupStatus> groupStatusList = new ArrayList<>();
		boolean collapsed = false;
		for (int i = 0; i < numberOfGroups; i++)
		{
			final int index = i + 1;
			final UiGroupStatus uiGroup = createUiGroupStatus("group_" + index, collapsed);
			createCsticsForUiGroupStatus(uiGroup, showFullLongText);
			groupStatusList.add(uiGroup);
			collapsed = changedCollapsedvalue(collapsed);
		}

		final UiStatus oldUiSate = new UiStatus();
		oldUiSate.setGroups(groupStatusList);
		oldUiSate.setPriceSummaryCollapsed(false);
		oldUiSate.setSpecificationTreeCollapsed(true);
		return oldUiSate;
	}

	private boolean changedCollapsedvalue(boolean collapsed)
	{
		if (!collapsed)
		{
			collapsed = true;
		}
		else
		{
			collapsed = false;
		}
		return collapsed;
	}

	private void createCsticsForUiGroupStatus(final UiGroupStatus uiGroupStatus, final boolean showFullLongText)
	{
		final List<UiCsticStatus> csticsStatus = new ArrayList<>();
		for (int i = 0; i < 5; i++)
		{
			final UiCsticStatus csticStatus = new UiCsticStatus();
			final int index = i + 1;
			csticStatus.setId(uiGroupStatus.getId() + "_cstic_" + index);
			csticStatus.setShowFullLongText(showFullLongText);
			csticsStatus.add(csticStatus);
		}
		uiGroupStatus.setCstics(csticsStatus);
	}





	protected ConfigurationData craeteConfigDataForSimpleTest(final boolean includeG1, final boolean includeG2,
			final boolean includeG3)
	{
		final ConfigurationData requestData = createEmptyConfigData();
		final List<UiGroupData> groups = createCsticsGroup();
		if (includeG1)
		{
			groups.get(0).setId("1");
			groups.get(0).getCstics().get(0).setKey("cstic_1a");
		}
		else
		{
			groups.remove(0);
		}
		if (includeG2)
		{
			final UiGroupData uiGroup2 = createUiGroup("2", true);
			uiGroup2.setSubGroups(Collections.singletonList(createUiGroup("2.1", true)));
			groups.add(uiGroup2);
		}
		if (includeG3)
		{
			groups.add(createUiGroup("3", false));
		}
		requestData.setGroups(groups);
		requestData.setPriceSummaryCollapsed(false);
		requestData.setSpecificationTreeCollapsed(true);
		return requestData;
	}

	public UiGroupData createUiGroup(final String id, final boolean collapsed)
	{
		return createUiGroup(id, null, collapsed);
	}

	protected UiStatus createUiStatusForSimpleTest()
	{
		final UiGroupStatus uiGroup1Status = createUiGroupStatus("1", false);
		final UiCsticStatus csticStatus = new UiCsticStatus();
		csticStatus.setId("cstic_1a");
		csticStatus.setShowFullLongText(false);
		uiGroup1Status.setCstics(Collections.singletonList(csticStatus));

		final List<UiGroupStatus> groupStatusList = new ArrayList<>();
		groupStatusList.add(uiGroup1Status);
		final UiGroupStatus uiGroup2Status = createUiGroupStatus("2", true);
		uiGroup2Status.setSubGroups(Collections.singletonList(createUiGroupStatus("2.1", true)));
		groupStatusList.add(uiGroup2Status);
		groupStatusList.add(createUiGroupStatus("3", false));

		final UiStatus oldUiSate = new UiStatus();
		oldUiSate.setGroups(groupStatusList);
		oldUiSate.setPriceSummaryCollapsed(false);
		oldUiSate.setSpecificationTreeCollapsed(true);
		return oldUiSate;
	}
}
