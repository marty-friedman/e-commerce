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

import de.hybris.platform.sap.productconfig.facades.CPQActionType;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.GroupType;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.facades.tracking.UiTrackingRecorder;
import de.hybris.platform.sap.productconfig.frontend.UiCsticStatus;
import de.hybris.platform.sap.productconfig.frontend.UiGroupStatus;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;
import de.hybris.platform.sap.productconfig.frontend.constants.SapproductconfigfrontendWebConstants;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * Used to sync the configuration DTO (which has request scope) with the UIStatus which we store in the session
 */
public class UiStatusSync
{

	private static final Logger LOG = Logger.getLogger(UiStatusSync.class);



	/**
	 * Updates the configuration DTO with the UI state (e.g. which group is collapsed/opened, which has been visited
	 * already). Takes care of the currently selected group and expands it
	 *
	 * @param configData
	 * @param uiStatus
	 *           UI status (session scope)
	 * @param selectedGroup
	 *           ID of the currently selected group
	 */
	public void applyUiStatusToConfiguration(final ConfigurationData configData, final UiStatus uiStatus,
			final String selectedGroup)
	{

		if (LOG.isDebugEnabled())
		{
			LOG.debug(" Apply UI status to congfig with [CONFIG_ID='" + configData.getConfigId() + "'");
		}

		applyUiStatusToConfiguration(configData, uiStatus);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Expand group '" + selectedGroup + "' for config data with [CONFIG_ID='" + configData.getConfigId() + "']");
		}

		expandGroupAndCollapseOther(configData, selectedGroup);
	}

	protected void expandGroupAndCollapseOther(final ConfigurationData configData, final String selectedGroup)
	{
		for (final UiGroupData uiGroup : configData.getGroups())
		{
			if (selectedGroup.equals(uiGroup.getId()))
			{
				uiGroup.setCollapsed(false);
			}
			else
			{
				uiGroup.setCollapsed(true);
			}
		}
	}

	/**
	 * Updates the configuration DTO with the UI state (e.g. which group is collapsed/opened, which has been visited
	 * already).
	 *
	 * @param configData
	 * @param uiStatus
	 *           UI status (session scope)
	 */
	public void applyUiStatusToConfiguration(final ConfigurationData configData, final UiStatus uiStatus)
	{
		final List<UiGroupStatus> uiGroupsStatus = uiStatus.getGroups();
		final List<UiGroupData> uiGroups = configData.getGroups();

		configData.setPriceSummaryCollapsed(uiStatus.isPriceSummaryCollapsed());
		configData.setSpecificationTreeCollapsed(uiStatus.isSpecificationTreeCollapsed());
		configData.setHideImageGallery(uiStatus.isHideImageGallery());
		configData.setQuantity(uiStatus.getQuantity());

		// If config-menu is displayed make sure that a group navigation also expands the config-menu node correctly
		// (e.g. for prev-/next-button clicked, view in configuration link, conflict link)
		final boolean isNavigationAction = isNavigationAction(configData.getCpqAction());
		if (!configData.isSingleLevel() && isNavigationAction && uiStatus.getGroupIdToDisplay() != null)
		{
			expandGroupInSpecTreeAndExpandGroup(uiStatus);
		}
		applyUiStatusToUiGroup(uiGroupsStatus, uiGroups);

	}

	protected boolean isNavigationAction(final CPQActionType cpqAction)
	{
		boolean isNavigationAction = false;
		if (cpqAction != null)
		{
			isNavigationAction = cpqAction.equals(CPQActionType.NAV_TO_CSTIC_IN_CONFLICT)
					|| cpqAction.equals(CPQActionType.NAV_TO_CSTIC_IN_GROUP) || cpqAction.equals(CPQActionType.PREV_BTN)
					|| cpqAction.equals(CPQActionType.NEXT_BTN);
		}
		return isNavigationAction;

	}

	/**
	 * Expand the group in spec tree and expand the group itself
	 *
	 * @param uiStatus
	 */
	protected void expandGroupInSpecTreeAndExpandGroup(final UiStatus uiStatus)
	{
		// Find the group to expand and set the collapse-state in spec tree to expanded
		final UiGroupStatus toggledGroup = toggleGroupInSpecTree(uiStatus.getGroupIdToDisplay(), uiStatus.getGroups(), true);
		// Expand the group itself as well if there was a group to toggle
		if (toggledGroup != null)
		{
			toggledGroup.setCollapsed(false);
		}
	}


	protected void applyUiStatusToUiGroup(final List<UiGroupStatus> uiGroupsStatus, final List<UiGroupData> uiGroups)
	{
		if (notNullAndNotEmpty(uiGroups))
		{
			for (int groupIdx = 0; groupIdx < uiGroups.size(); groupIdx++)
			{
				final UiGroupData uiGroup = uiGroups.get(groupIdx);
				final UiGroupStatus statusGroup = findStatusGroup(uiGroupsStatus, uiGroup, groupIdx);
				if (statusGroup != null)
				{
					applyUiStatusToUiGroup(statusGroup.getSubGroups(), uiGroup.getSubGroups());
					applyUiStatusToCstic(statusGroup.getCstics(), uiGroup.getCstics());
					uiGroup.setCollapsed(statusGroup.isCollapsed());
					uiGroup.setCollapsedInSpecificationTree(statusGroup.isCollapsedInSpecificationTree());
					uiGroup.setVisited(statusGroup.isVisited());
				}
				else if (uiGroup.getGroupType().equals(GroupType.CSTIC_GROUP) || uiGroup.getGroupType().equals(GroupType.INSTANCE))
				{
					uiGroup.setCollapsed(true);
				}
			}
		}
	}

	/**
	 * @param csticKey
	 * @param showFullLongText
	 * @param uiStatusGroups
	 */
	public void updateShowFullLongTextinUIStatusGroups(final String csticKey, final boolean showFullLongText,
			final List<UiGroupStatus> uiStatusGroups)
	{
		if (notNullAndNotEmpty(uiStatusGroups))
		{
			for (int index = 0; index < uiStatusGroups.size(); index++)
			{
				final UiGroupStatus statusGroup = uiStatusGroups.get(index);
				updateShowFullLongTextinUIStatusGroups(csticKey, showFullLongText, statusGroup.getSubGroups());
				updateShowFullLongTextInUiStatusCstics(csticKey, showFullLongText, statusGroup.getCstics());
			}
		}
	}

	protected void applyUiStatusToCstic(final List<UiCsticStatus> uiCsticsStatus, final List<CsticData> cstics)
	{
		if (notNullAndNotEmpty(cstics))
		{
			for (int csticIdx = 0; csticIdx < cstics.size(); csticIdx++)
			{
				final CsticData cstic = cstics.get(csticIdx);
				final UiCsticStatus statusCstic = findStatusCstic(uiCsticsStatus, cstic, csticIdx);
				if (statusCstic != null)
				{
					cstic.setShowFullLongText(statusCstic.isShowFullLongText());
				}
			}
		}
	}

	protected void updateShowFullLongTextInUiStatusCstics(final String csticKey, final boolean showFullLongText,
			final List<UiCsticStatus> uiCsticsStatus)
	{
		if (notNullAndNotEmpty(uiCsticsStatus))
		{
			for (int index = 0; index < uiCsticsStatus.size(); index++)
			{
				final UiCsticStatus statusCstic = uiCsticsStatus.get(index);

				if (statusCstic.getId().equals(csticKey))
				{
					statusCstic.setShowFullLongText(showFullLongText);
				}
			}
		}
	}

	/**
	 * Provides the configuration DTO with UI relevant settings valid in its initial state
	 *
	 * @param configData
	 */
	public void setInitialStatus(final ConfigurationData configData)
	{
		final List<UiGroupData> csticGroups = configData.getGroups();
		setInitialGroupStatus(csticGroups, 0);

		configData.setSpecificationTreeCollapsed(false);
		configData.setPriceSummaryCollapsed(false);
		configData.setHideImageGallery(true);
		configData.setGroupIdToDisplay(csticGroups.get(0).getId());
	}

	protected void setInitialGroupStatus(final List<UiGroupData> uiGroups, final int level)
	{
		final int subLevel = level + 1;
		boolean firstGroup = true;
		for (final UiGroupData uiGroup : uiGroups)
		{
			if (uiGroup.isConfigurable())
			{
				uiGroup.setCollapsed(!firstGroup);
				firstGroup = false;
			}
			else
			{
				uiGroup.setCollapsed(true);
			}

			uiGroup.setCollapsedInSpecificationTree(subLevel != 1);

			if (hasSubGroups(uiGroup))
			{
				setInitialGroupStatus(uiGroup.getSubGroups(), subLevel);
			}
			if (hasCstics(uiGroup))
			{
				setInitialCsticStatus(uiGroup.getCstics());
			}
		}
	}

	protected void setInitialCsticStatus(final List<CsticData> cstics)
	{
		for (final CsticData cstic : cstics)
		{
			cstic.setShowFullLongText(false);
		}
	}

	protected boolean hasSubGroups(final UiGroupData uiGroup)
	{
		final List<UiGroupData> subGroups = uiGroup.getSubGroups();
		return notNullAndNotEmpty(subGroups);
	}

	protected boolean notNullAndNotEmpty(final List subGroups)
	{
		return subGroups != null && !subGroups.isEmpty();
	}

	protected boolean hasCstics(final UiGroupData uiGroup)
	{
		final List<CsticData> cstics = uiGroup.getCstics();
		return notNullAndNotEmpty(cstics);
	}

	/**
	 * Updates UI status with the current state of the configuration DTO
	 *
	 * @param configData
	 * @return UI status which we put into the session to persist the currently selected group e.g.
	 */
	public UiStatus extractUiStatusFromConfiguration(final ConfigurationData configData)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Extract UI status from config with [CONFIG_ID='" + replaceNewLineForLog(configData.getConfigId()) + "']");
		}

		final UiStatus uiStatus = new UiStatus();
		uiStatus.setConfigId(configData.getConfigId());
		uiStatus.setNumberOfConflictsToDisplay(getNumberOfConflicts(configData));
		uiStatus.setPriceSummaryCollapsed(configData.isPriceSummaryCollapsed());
		uiStatus.setSpecificationTreeCollapsed(configData.isSpecificationTreeCollapsed());
		uiStatus.setHideImageGallery(configData.isHideImageGallery());
		uiStatus.setGroupIdToDisplay(configData.getGroupIdToDisplay());
		uiStatus.setQuantity(configData.getQuantity());
		final List<UiGroupStatus> uiGroups = new ArrayList<>();
		extractUiStatusFromUiGroup(configData.getGroups(), uiGroups, configData);
		if (configData.isAutoExpand())
		{
			uiStatus.setFirstErrorCsticId(configData.getFocusId());
		}
		uiStatus.setGroups(uiGroups);

		return uiStatus;
	}

	/**
	 * Determine number of conflicts by calculating the conflict groups
	 *
	 * @param configData
	 * @return Number of conflict groups
	 */
	public int getNumberOfConflicts(final ConfigurationData configData)
	{
		for (final UiGroupData group : configData.getGroups())
		{
			if (GroupType.CONFLICT_HEADER.equals(group.getGroupType()))
			{
				return group.getSubGroups().size();
			}

		}
		return 0;
	}


	/**
	 * Recursively checks a group hierarchy. If a group is found that matches the given group id its collapsed state is
	 * Toggled. If the toggle was an expand, all parent groups are expanded as well.
	 *
	 * @param groupIdToToggle
	 *           id of group to toggle
	 * @param groups
	 *           list of groups to check
	 * @param forceExpand
	 * @return the goup that was toggled, or <code>null</code> if no group with the given ID was found.
	 */
	protected UiGroupStatus toggleGroup(final String groupIdToToggle, final List<UiGroupStatus> groups, final boolean forceExpand)
	{

		UiGroupStatus toggledGroup = null;
		boolean foundToggledGroup = false;
		for (final UiGroupStatus uiGroup : groups)
		{
			if (groupIdToToggle.equals(uiGroup.getId()))
			{
				LOG.debug("Toggle group with id: '" + uiGroup.getId() + "' to collapsed=" + !(uiGroup.isCollapsed() || forceExpand));
				// toggle group itself
				uiGroup.setCollapsed(!(uiGroup.isCollapsed() || forceExpand));
				toggledGroup = uiGroup;
				foundToggledGroup = true;
			}
			else
			{
				final List<UiGroupStatus> subGroups = uiGroup.getSubGroups();
				if (notNullAndNotEmpty(subGroups))
				{
					toggledGroup = toggleGroup(groupIdToToggle, subGroups, forceExpand);
					foundToggledGroup = toggleParentIfNeeded(toggledGroup, uiGroup);
				}
			}
			if (foundToggledGroup)
			{
				break;
			}
		}
		return toggledGroup;
	}

	protected boolean toggleParentIfNeeded(final UiGroupStatus toggledGroup, final UiGroupStatus parentGroup)
	{
		boolean foundToggledGroup = false;
		if (toggledGroup != null && !toggledGroup.isCollapsed())
		{
			LOG.debug("Expand group with id: '" + parentGroup.getId() + "'");
			// if toggled child was expanded, make sure this group is expanded as well
			parentGroup.setCollapsed(false);
			foundToggledGroup = true;
		}
		return foundToggledGroup;
	}

	protected UiGroupStatus toggleGroupInSpecTree(final String groupIdToToggle, final List<UiGroupStatus> groups,
			final boolean forceExpand)
	{

		UiGroupStatus toggledGroup = null;
		for (final UiGroupStatus uiGroup : groups)
		{
			if (groupIdToToggle.equals(uiGroup.getId()))
			{
				LOG.debug("Toggle group in specification tree with id: '" + uiGroup.getId() + "' to collapsedInSpecificationTree='"
						+ !(uiGroup.isCollapsedInSpecificationTree() || forceExpand) + "'");
				// toggle group itself
				uiGroup.setCollapsedInSpecificationTree(!(uiGroup.isCollapsedInSpecificationTree() || forceExpand));
				toggledGroup = uiGroup;
			}
			else
			{
				final List<UiGroupStatus> subGroups = uiGroup.getSubGroups();
				if (notNullAndNotEmpty(subGroups))
				{
					toggledGroup = toggleGroupInSpecTree(groupIdToToggle, subGroups, forceExpand);
					toggleParentGroupInSpecTreeIfNeeded(toggledGroup, uiGroup);
				}
			}
			if (toggledGroup != null)
			{
				break;
			}
		}
		return toggledGroup;
	}

	protected void toggleParentGroupInSpecTreeIfNeeded(final UiGroupStatus toggledGroup, final UiGroupStatus parentGroup)
	{
		if (toggledGroup != null && !toggledGroup.isCollapsedInSpecificationTree())
		{
			LOG.debug("Expand group in specification tree with id: '" + parentGroup.getId() + "'");
			// if toggled child was expanded, make sure this group is expanded as well
			parentGroup.setCollapsedInSpecificationTree(false);
		}
	}

	protected void expandGroupAndCollapseOther(final String selectedGroup, final List<UiGroupStatus> groups)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Expand tab with id: '" + replaceNewLineForLog(selectedGroup) + "'");
		}

		for (final UiGroupStatus uiGroup : groups)
		{
			if (selectedGroup.equals(uiGroup.getId()))
			{
				uiGroup.setCollapsed(false);
			}
			else
			{
				uiGroup.setCollapsed(true);
			}
		}
	}

	protected void extractUiStatusFromUiGroup(final List<UiGroupData> uiGroups, final List<UiGroupStatus> uiGroupsStatus,
			final ConfigurationData configData)
	{
		for (final UiGroupData uiGroup : uiGroups)
		{
			final UiGroupStatus uiGroupStatus = new UiGroupStatus();
			final String groupId = uiGroup.getId();

			//mark: group has been visited
			if (configData.isSingleLevel())
			{
				uiGroupStatus.setVisited(uiGroup.isVisited() || !uiGroup.isCollapsed());
			}
			else
			{
				uiGroupStatus.setVisited(uiGroup.isVisited() || groupId.equals(configData.getGroupIdToDisplay()));
			}

			if (LOG.isDebugEnabled())
			{
				LOG.debug("UI group: '" + groupId + "' has been visited: '" + uiGroupStatus.isVisited() + "'");
			}

			uiGroupStatus.setId(groupId);
			uiGroupStatus.setCollapsed(uiGroup.isCollapsed());
			uiGroupStatus.setCollapsedInSpecificationTree(uiGroup.isCollapsedInSpecificationTree());
			if (hasSubGroups(uiGroup))
			{
				final List<UiGroupStatus> uiSubGroups = new ArrayList<>();
				extractUiStatusFromUiGroup(uiGroup.getSubGroups(), uiSubGroups, configData);
				uiGroupStatus.setSubGroups(uiSubGroups);
			}
			uiGroupsStatus.add(uiGroupStatus);

			if (hasCstics(uiGroup))
			{
				final List<UiCsticStatus> uiCsticsStatus = new ArrayList<>();
				extractUiStatusFromCstic(uiGroup.getCstics(), uiCsticsStatus);
				uiGroupStatus.setCstics(uiCsticsStatus);
			}
		}
	}

	protected void extractUiStatusFromCstic(final List<CsticData> cstics, final List<UiCsticStatus> uiCsticsStatus)
	{
		for (final CsticData cstic : cstics)
		{
			final UiCsticStatus uiCsticStatus = new UiCsticStatus();
			uiCsticStatus.setId(cstic.getKey());
			uiCsticStatus.setShowFullLongText(cstic.isShowFullLongText());
			uiCsticsStatus.add(uiCsticStatus);
		}
	}

	/**
	 * Apply user changes to the UI status (session) object. User might e.g. open a group which was not visited before
	 *
	 * @param requestData
	 * @param oldUiState
	 *           Previous version of the UI status
	 * @param uiTrackingRecorder
	 *           recorder for ui activities
	 * @return New version of the UI status
	 */
	public UiStatus updateUIStatusFromRequest(final ConfigurationData requestData, final UiStatus oldUiState,
			final UiTrackingRecorder uiTrackingRecorder)
	{
		UiStatus newUiState = oldUiState;
		if (oldUiState == null)
		{
			LOG.info("No old UI-State provided for config '" + replaceNewLineForLog(requestData.getConfigId())
					+ "' while updating configuration; creating new UI-State from request");
			newUiState = extractUiStatusFromConfiguration(requestData);
		}
		else
		{
			newUiState.setPriceSummaryCollapsed(requestData.isPriceSummaryCollapsed());
			newUiState.setSpecificationTreeCollapsed(requestData.isSpecificationTreeCollapsed());
			newUiState.setGroupIdToDisplay(requestData.getGroupIdToDisplay());
			storeLastNoneConflictGroupId(newUiState, requestData);

			if (LOG.isDebugEnabled())
			{
				LOG.debug("Group ID for display from request: '" + replaceNewLineForLog(requestData.getGroupIdToDisplay()) + "'");
			}

			updateGroupUIStatusFromRequestData(newUiState.getGroups(), requestData.getGroups());
		}

		handleCPQAction(requestData, newUiState, uiTrackingRecorder);

		final String selectedGroup = requestData.getSelectedGroup();
		if (notNullAndNotEmpty(selectedGroup))
		{
			expandGroupAndCollapseOther(selectedGroup, newUiState.getGroups());
		}

		return newUiState;
	}

	protected void storeLastNoneConflictGroupId(final UiStatus newUiState, final ConfigurationData requestData)
	{
		if (null != requestData.getGroupIdToDisplay()
				&& !requestData.getGroupIdToDisplay().startsWith(SapproductconfigfrontendWebConstants.CONFLICT_PREFIX))
		{
			newUiState.setLastNoneConflictGroupId(requestData.getGroupIdToDisplay());
		}
	}

	protected void handleCPQAction(final ConfigurationData requestData, final UiStatus oldUiState,
			final UiTrackingRecorder uiTrackingRecorder)
	{
		final CPQActionType action = requestData.getCpqAction();
		if (CPQActionType.TOGGLE_GROUP.equals(action) || CPQActionType.MENU_NAVIGATION.equals(action))
		{
			final UiGroupStatus toggledGroup = toggleGroup(requestData.getGroupIdToToggle(), oldUiState.getGroups(),
					requestData.isForceExpand());
			if (toggledGroup != null)
			{
				uiTrackingRecorder.recordGroupInteraction(requestData, toggledGroup.getId(), toggledGroup.isCollapsed());
			}
			requestData.setGroupIdToToggle("");
		}
		if (CPQActionType.MENU_NAVIGATION.equals(action))
		{
			final UiGroupStatus toggledGroup = toggleGroupInSpecTree(requestData.getGroupIdToToggleInSpecTree(),
					oldUiState.getGroups(), false);
			if (toggledGroup != null)
			{
				uiTrackingRecorder.recordMenuToggle(requestData, toggledGroup.getId(), toggledGroup.isCollapsedInSpecificationTree());
			}
			requestData.setGroupIdToToggleInSpecTree("");
		}
	}

	protected boolean notNullAndNotEmpty(final String id)
	{
		return id != null && !id.isEmpty();
	}

	protected void updateGroupUIStatusFromRequestData(final List<UiGroupStatus> uiSateGroups,
			final List<UiGroupData> requestGroups)
	{
		if (notNullAndNotEmpty(requestGroups))
		{
			for (int groupIdx = 0; groupIdx < requestGroups.size(); groupIdx++)
			{
				final UiGroupData requestGroup = requestGroups.get(groupIdx);
				final UiGroupStatus statusGroup = findStatusGroup(uiSateGroups, requestGroup, groupIdx);
				updateSingleStatusGroupFromRequest(requestGroup, statusGroup);
			}
		}
	}

	protected void updateSingleStatusGroupFromRequest(final UiGroupData requestGroup, final UiGroupStatus statusGroup)
	{
		if (statusGroup != null)
		{
			if (requestGroup.isVisited())
			{
				LOG.debug("Setting uiGroup='" + statusGroup.getId() + "' eas displayed on the UI, setting visited=true");
				statusGroup.setVisited(true);
			}
			updateGroupUIStatusFromRequestData(statusGroup.getSubGroups(), requestGroup.getSubGroups());
			updateCsticUIStatusFromRequestData(statusGroup.getCstics(), requestGroup.getCstics());
		}
		else
		{
			if (requestGroup.getId() != null && LOG.isDebugEnabled())
			{
				LOG.debug("UI Status is inconsistent. For UiGroup '" + requestGroup.getId()
						+ "' no corresponding UIStatusGroup was found!");
			}
		}
	}

	protected void updateCsticUIStatusFromRequestData(final List<UiCsticStatus> statusCstics, final List<CsticData> requestCstics)
	{
		if (notNullAndNotEmpty(requestCstics))
		{
			for (int csticIdx = 0; csticIdx < requestCstics.size(); csticIdx++)
			{
				final CsticData requestCstic = requestCstics.get(csticIdx);
				final UiCsticStatus statusCstic = findStatusCstic(statusCstics, requestCstic, csticIdx);
				if (statusCstic != null)
				{
					statusCstic.setShowFullLongText(requestCstic.isShowFullLongText());
				}
				else
				{
					LOG.debug("UI Status is inconsistent. For Cstic '" + requestCstic.getKey()
							+ "' no corresponding UIStatusCstic was found!");
				}
			}
		}
	}

	protected UiCsticStatus findStatusCstic(final List<UiCsticStatus> statusCstics, final CsticData requestCstic,
			final int csticIdx)
	{
		UiCsticStatus statusCsticToReturn = null;
		if (notNullAndNotEmpty(statusCstics) && notNullAndNotEmpty(requestCstic.getKey()))
		{
			statusCsticToReturn = findStatusCsticForExisting(statusCstics, requestCstic, csticIdx);
		}
		return statusCsticToReturn;
	}

	protected UiCsticStatus findStatusCsticForExisting(final List<UiCsticStatus> statusCstics, final CsticData requestCstic,
			final int csticIdx)
	{

		UiCsticStatus statusCsticToReturn = getStatusCstic(statusCstics, csticIdx);
		if (!uiStatusCsticMatchesUiCstic(requestCstic, statusCsticToReturn))
		{
			// full list scan
			statusCsticToReturn = null;
			for (final UiCsticStatus statusCstic : statusCstics)
			{
				if (uiStatusCsticMatchesUiCstic(requestCstic, statusCstic))
				{
					statusCsticToReturn = statusCstic;
					break;
				}
			}
		}
		return statusCsticToReturn;
	}

	protected UiCsticStatus getStatusCstic(final List<UiCsticStatus> statusCstics, final int csticIdx)
	{
		UiCsticStatus statusCsticToReturn = null;
		if (csticIdx < statusCstics.size())
		{
			// guess same index
			statusCsticToReturn = statusCstics.get(csticIdx);
		}
		return statusCsticToReturn;
	}

	protected boolean uiStatusCsticMatchesUiCstic(final CsticData cstic, final UiCsticStatus statusCstic)
	{
		return cstic != null && statusCstic != null && cstic.getKey().equals(statusCstic.getId());
	}

	protected UiGroupStatus findStatusGroup(final List<UiGroupStatus> uiStatusGroups, final UiGroupData requestGroup,
			final int groupIdx)
	{

		UiGroupStatus statusGroupToReturn = null;
		if (notNullAndNotEmpty(uiStatusGroups) && notNullAndNotEmpty(requestGroup.getId()))
		{
			statusGroupToReturn = findStatusGroupForExisting(uiStatusGroups, requestGroup, groupIdx);
		}
		return statusGroupToReturn;
	}

	protected UiGroupStatus findStatusGroupForExisting(final List<UiGroupStatus> uiSateGroups, final UiGroupData requestGroup,
			final int groupIdx)
	{
		UiGroupStatus statusGroupToReturn = null;
		if (groupIdx < uiSateGroups.size())
		{
			// guess same index
			statusGroupToReturn = uiSateGroups.get(groupIdx);
		}

		if (!uiStatusGroupMatchesUiGroup(requestGroup, statusGroupToReturn))
		{
			// full list scan
			statusGroupToReturn = null;
			for (final UiGroupStatus statusGroup : uiSateGroups)
			{
				if (uiStatusGroupMatchesUiGroup(requestGroup, statusGroup))
				{
					statusGroupToReturn = statusGroup;
					break;
				}
			}
		}
		return statusGroupToReturn;
	}

	protected boolean uiStatusGroupMatchesUiGroup(final UiGroupData uiGroup, final UiGroupStatus uiStatusGroup)
	{
		return uiGroup != null && uiStatusGroup != null && uiGroup.getId().equals(uiStatusGroup.getId());
	}

	protected String replaceNewLineForLog(final String str)
	{
		if (notNullAndNotEmpty(str))
		{
			return str.replace("\n", "_").replace("\r", "_");
		}
		return str;
	}

	public void updateNewUiStateFromOld(final UiStatus oldUiStatus, final UiStatus uiStatus)
	{
		uiStatus.setLastNoneConflictGroupId(oldUiStatus.getLastNoneConflictGroupId());

	}
}
