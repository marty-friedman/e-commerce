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

// Bundling app as legacy script

import './requireLegacyJsFiles';
import './'; // do not remove - import of barrel file that is used to inject featureExtensions modules imports

import * as angular from 'angular';

import {IFeatureService, IPerspectiveService, IUrlService} from 'smarteditcommons';

import {AssetsService} from 'cmscommons';
import {trashedPageListControllerModule} from 'cmssmarteditcontainer/components/pages/trashedPageList/trashedPageListController';
import {cmsSmarteditServicesModule} from 'cmssmarteditcontainer/services/cmsSmarteditServicesModule';
import 'cmssmarteditcontainer/services/deprecatedSince-6.7';


/* @ngInject */
class NavigationController {

	// Variables
	uriContext: any;
	catalogName: string;
	catalogVersion: string;
	readOnly: boolean;

	constructor($routeParams: any, urlService: IUrlService, permissionService: any, CONTEXT_CATALOG_VERSION: string, CONTEXT_SITE_ID: string, catalogService: any) {
		this.uriContext = urlService.buildUriContext($routeParams.siteId, $routeParams.catalogId, $routeParams.catalogVersion);
		this.catalogName = "";
		this.catalogVersion = this.uriContext[CONTEXT_CATALOG_VERSION];

		permissionService.isPermitted([{
			names: ['se.edit.navigation']
		}]).then((isPermissionGranted: boolean) => {
			this.readOnly = !isPermissionGranted;
		}, function(e: any) {
			throw e;
		});

		catalogService.getContentCatalogsForSite(this.uriContext[CONTEXT_SITE_ID]).then((catalogs: any) => {
			this.catalogName = catalogs.filter((catalog: any) => {
				return catalog.catalogId === $routeParams.catalogId;
			})[0].name;
		});
	}

}

angular.module('cmssmarteditContainer', [
	'experienceInterceptorModule',
	'resourceLocationsModule',
	'cmssmarteditContainerTemplates',
	'componentMenuModule',
	'cmscommonsTemplates',
	'restrictionsMenuModule',
	'pageInfoMenuModule',
	'editorModalServiceModule',
	'genericEditorModule',
	'eventServiceModule',
	'catalogDetailsModule',
	'synchronizeCatalogModule',
	'pageListLinkModule',
	'pageListControllerModule',
	'navigationEditorModule',
	'slotRestrictionsServiceModule',
	'cmsDragAndDropServiceModule',
	'seMediaFieldModule',
	'seMediaContainerFieldModule',
	'editorFieldMappingServiceModule',
	'navigationNodeEditorModule',
	'entrySearchSelectorModule',
	'pageRestrictionsModule',
	'restrictionsEditorModule',
	'yActionableSearchItemModule',
	'seNavigationNodeSelector',
	'pageSyncMenuToolbarItemModule',
	'synchronizationPollingServiceModule',
	'productSelectorModule',
	'categorySelectorModule',
	'clonePageWizardServiceModule',
	'cmsLinkToSelectModule',
	'permissionServiceModule',
	'rulesAndPermissionsRegistrationModule',
	'catalogServiceModule',
	'experienceServiceModule',
	'smarteditServicesModule',
	'singleActiveCatalogAwareItemSelectorModule',
	'productCatalogDropdownPopulatorModule',
	'productDropdownPopulatorModule',
	'categoryDropdownPopulatorModule',
	'cmsItemDropdownModule',
	'catalogAwareRouteResolverModule',
	'catalogVersionPermissionModule',
	'componentRestrictionsEditorModule',
	'pageRestrictionsEditorModule',
	'displayConditionsEditorModule',
	'linkToggleModule',
	'functionsModule',
	'componentVisibilityAlertServiceModule',
	'cmsGenericEditorConfigurationServiceModule',
	'clonePageItemModule',
	'deletePageItemModule',
	'editPageItemModule',
	'syncPageItemModule',
	'deletePageToolbarItemModule',
	cmsSmarteditServicesModule.name,
	trashedPageListControllerModule.name
])
	.config((PAGE_LIST_PATH: string, TRASHED_PAGE_LIST_PATH: string, NAVIGATION_MANAGEMENT_PAGE_PATH: string, $routeProvider: ng.route.IRouteProvider, catalogAwareRouteResolverFunctions: any) => {
		'ngInject';
		$routeProvider.when(PAGE_LIST_PATH, {
			templateUrl: 'pageListTemplate.html',
			controller: 'pageListController',
			controllerAs: 'pageListCtl',
			resolve: {
				setExperience: catalogAwareRouteResolverFunctions.setExperience
			}
		});
		$routeProvider.when(TRASHED_PAGE_LIST_PATH, {
			templateUrl: 'trashedpageListTemplate.html',
			controller: 'trashedPageListController',
			controllerAs: 'trashedPageListCtl',
			resolve: {
				setExperience: catalogAwareRouteResolverFunctions.setExperience
			}
		});
		$routeProvider.when(NAVIGATION_MANAGEMENT_PAGE_PATH, {
			templateUrl: 'navigationTemplate.html',
			controller: 'navigationController',
			controllerAs: 'nav',
			resolve: {
				setExperience: catalogAwareRouteResolverFunctions.setExperience
			}
		});
	})
	.controller('navigationController', NavigationController)
	.run(
	/* jshint -W098*/
	/*need to inject for gatewayProxy initialization of componentVisibilityAlertService*/
	($log: angular.ILogService,
		$rootScope: angular.IRootScopeService,
		$routeParams: any,
		NAVIGATION_MANAGEMENT_PAGE_PATH: string,
		ComponentService: any,
		systemEventService: any,
		catalogDetailsService: any,
		featureService: IFeatureService,
		perspectiveService: IPerspectiveService,
		assetsService: AssetsService,
		editorFieldMappingService: any,
		genericEditorTabService: any,
		cmsDragAndDropService: any,
		editorModalService: any,
		clonePageWizardService: any,
		CATALOG_DETAILS_COLUMNS: any,
		componentVisibilityAlertService: any,
		cmsGenericEditorConfigurationService: any) => {
		'ngInject';
		// Configure generic editor 
		cmsGenericEditorConfigurationService.setDefaultEditorFieldMappings();
		cmsGenericEditorConfigurationService.setDefaultTabFieldMappings();
		cmsGenericEditorConfigurationService.setDefaultTabsConfiguration();

		featureService.addToolbarItem({
			toolbarId: 'experienceSelectorToolbar',
			key: 'se.cms.componentMenuTemplate',
			type: 'HYBRID_ACTION',
			nameI18nKey: 'se.cms.componentmenu.btn.label.addcomponent',
			descriptionI18nKey: 'cms.toolbaritem.componentmenutemplate.description',
			priority: 1,
			section: 'left',
			iconClassName: 'hyicon hyicon-addlg se-toolbar-menu-ddlb--button__icon',
			callback: () => {
				systemEventService.sendSynchEvent('ySEComponentMenuOpen', {});
			},
			include: 'componentMenuWrapperTemplate.html',
			permissions: ['se.add.component'],
			keepAliveOnClose: true
		});

		featureService.addToolbarItem({
			toolbarId: 'experienceSelectorToolbar',
			key: 'se.cms.restrictionsMenu',
			type: 'HYBRID_ACTION',
			nameI18nKey: 'se.cms.restrictions.toolbar.menu',
			priority: 2,
			section: 'left',
			iconClassName: 'hyicon hyicon-restrictions se-toolbar-menu-ddlb--button__icon',
			include: 'pageRestrictionsMenuToolbarItemWrapperTemplate.html',
			permissions: ['se.read.restriction']
		});

		featureService.addToolbarItem({
			toolbarId: 'experienceSelectorToolbar',
			key: 'se.cms.pageInfoMenu',
			type: 'HYBRID_ACTION',
			nameI18nKey: 'se.cms.pageinfo.menu.btn.label',
			descriptionI18nKey: 'cms.toolbarItem.pageInfoMenu.description',
			priority: 3,
			section: 'left',
			iconClassName: 'hyicon hyicon-info se-toolbar-menu-ddlb--button__icon',
			include: 'pageInfoMenuToolbarItemWrapperTemplate.html',
			permissions: ['se.read.page']
		});

		featureService.addToolbarItem({
			toolbarId: 'experienceSelectorToolbar',
			key: 'se.cms.clonePageMenu',
			type: 'ACTION',
			nameI18nKey: 'se.cms.clonepage.menu.btn.label',
			iconClassName: 'hyicon hyicon-clone se-toolbar-menu-ddlb--button__icon',
			callback: () => {
				clonePageWizardService.openClonePageWizard();
			},
			priority: 4,
			section: 'left',
			permissions: ['se.clone.page']
		});

		featureService.addToolbarItem({
			toolbarId: 'experienceSelectorToolbar',
			key: 'se.cms.pageSyncMenu',
			nameI18nKey: 'se.cms.toolbaritem.pagesyncmenu.name',
			type: 'TEMPLATE',
			include: 'pageSyncMenuToolbarItemWrapperTemplate.html',
			priority: 5,
			section: 'left',
			permissions: ['se.sync.page']
		});

		featureService.addToolbarItem({
			toolbarId: 'experienceSelectorToolbar',
			key: 'deletePageMenu',
			nameI18nKey: 'se.cms.actionitem.page.trash',
			type: 'TEMPLATE',
			include: 'deletePageToolbarItemWrapperTemplate.html',
			priority: 6,
			section: 'left',
			permissions: ['se.delete.page.menu']
		});

		catalogDetailsService.addItems([{
			include: 'pageListLinkTemplate.html'
		}, {
			include: 'navigationEditorLinkTemplate.html'
		}]);

		catalogDetailsService.addItems([{
			include: 'catalogDetailsSyncTemplate.html'
		}], CATALOG_DETAILS_COLUMNS.RIGHT);

		featureService.register({
			key: 'se.cms.html5DragAndDrop.outer',
			nameI18nKey: 'se.cms.dragAndDrop.name',
			descriptionI18nKey: 'se.cms.dragAndDrop.description',
			enablingCallback: () => {
				cmsDragAndDropService.register();
				cmsDragAndDropService.apply();
			},
			disablingCallback: () => {
				cmsDragAndDropService.unregister();
			}
		});

		perspectiveService.register({
			key: 'se.cms.perspective.basic',
			nameI18nKey: 'se.cms.perspective.basic.name',
			descriptionI18nKey: 'se.cms.perspective.basic.description',
			features: ['se.contextualMenu', 'se.cms.dragandropbutton', 'se.cms.remove', 'se.cms.edit', 'se.cms.componentMenuTemplate', 'se.cms.restrictionsMenu', 'se.cms.clonePageMenu', 'se.cms.pageInfoMenu', 'se.emptySlotFix', 'se.cms.html5DragAndDrop', 'disableSharedSlotEditing', 'sharedSlotDisabledDecorator', 'se.cms.html5DragAndDrop.outer', 'externalComponentDecorator', 'externalcomponentbutton', 'externalSlotDisabledDecorator', 'clonecomponentbutton', 'deletePageMenu'],
			perspectives: []
		});

		/* Note: For advance edit mode, the ordering of the entries in the features list will determine the order the buttons will show in the slot contextual menu */
		perspectiveService.register({
			key: 'se.cms.perspective.advanced',
			nameI18nKey: 'se.cms.perspective.advanced.name',
			descriptionI18nKey: 'se.cms.perspective.advanced.description',
			features: ['se.slotContextualMenu', 'se.slotSyncButton', 'se.slotSharedButton', 'se.slotContextualMenuVisibility', 'se.contextualMenu', 'se.cms.dragandropbutton', 'se.cms.remove', 'se.cms.edit', 'se.cms.componentMenuTemplate', 'se.cms.restrictionsMenu', 'se.cms.clonePageMenu', 'se.cms.pageInfoMenu', 'se.cms.pageSyncMenu', 'se.emptySlotFix', 'se.cms.html5DragAndDrop', 'se.cms.html5DragAndDrop.outer', 'syncIndicator', 'externalSlotDisabledDecorator', 'externalComponentDecorator', 'externalcomponentbutton', 'clonecomponentbutton', 'slotUnsharedButton', 'deletePageMenu'],
			perspectives: []
		});

	}
	);
