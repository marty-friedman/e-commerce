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
import * as angular from 'angular';
import {instrument, Cloneable, IFeatureService, IRestServiceFactory, IUrlService} from 'smarteditcommons';
import {smarteditServicesModule, BootstrapService, DelegateRestService, PermissionsRegistrationService, PerspectiveService, SharedDataService, StorageService} from 'smarteditcontainer/services';

import 'smarteditcontainer/services/deprecatedSince-6.7';

const TOP_LEVEL_MODULE_NAME = 'smarteditcontainer';

/** @internal */
/* @ngInject */
class SmarteditDefaultController {

	// Variables
	iFrameManager: any;
	experienceService: any;
	sharedDataService: SharedDataService;

	constructor(iFrameManager: any, experienceService: any, sharedDataService: SharedDataService) {
		this.iFrameManager = iFrameManager;
		this.experienceService = experienceService;
		this.sharedDataService = sharedDataService;

		this.setUpIFrame();
	}

	private setUpIFrame() {
		this.iFrameManager.applyDefault();
		this.iFrameManager.initializeCatalogPreview();
		(angular.element(document.querySelector('body')) as JQuery).addClass('is-storefront');
	}
}

export const smarteditContainerModule = angular
	.module(TOP_LEVEL_MODULE_NAME, [
		smarteditServicesModule.name,
		'configModule',
		'landingPageModule',
		'templateCacheDecoratorModule',
		'ngRoute',
		'ngResource',
		'ui.bootstrap',
		'coretemplates',
		'loadConfigModule',
		'iFrameManagerModule',
		'alertsBoxModule',
		'httpAuthInterceptorModule',
		'experienceInterceptorModule',
		'toolbarModule',
		'leftToolbarModule',
		'modalServiceModule',
		'catalogServiceModule',
		'catalogDetailsModule',
		'experienceSelectorButtonModule',
		'experienceSelectorModule',
		'inflectionPointSelectorModule',
		'paginationFilterModule',
		'resourceLocationsModule',
		'experienceServiceModule',
		'eventServiceModule',
		'perspectiveSelectorModule',
		'authorizationModule',
		'hasOperationPermissionModule',
		'l10nModule',
		'treeModule',
		'yInfiniteScrollingModule',
		'ySelectModule',
		'yHelpModule',
		'crossFrameEventServiceModule',
		'renderServiceModule',
		'systemAlertsModule',
		'yCollapsibleContainerModule',
		'seDropdownModule',
		'permissionServiceModule',
		'yNotificationPanelModule',
		'catalogAwareRouteResolverModule',
		'catalogVersionPermissionModule',
		'httpErrorInterceptorServiceModule',
		'unauthorizedErrorInterceptorModule',
		'resourceNotFoundErrorInterceptorModule',
		'nonvalidationErrorInterceptorModule',
		'previewErrorInterceptorModule',
		'retryInterceptorModule',
		'seConstantsModule',
		'pageSensitiveDirectiveModule',
		'yjqueryModule'
	])
	.config(($provide: angular.auto.IProvideService, readObjectStructureFactory: () => (arg: Cloneable) => Cloneable, LANDING_PAGE_PATH: string, STOREFRONT_PATH: string, STOREFRONT_PATH_WITH_PAGE_ID: string, $routeProvider: angular.route.IRouteProvider, $logProvider: angular.ILogProvider, catalogAwareRouteResolverFunctions: any) => {
		'ngInject';

		instrument($provide, readObjectStructureFactory(), TOP_LEVEL_MODULE_NAME);

		$routeProvider.when(LANDING_PAGE_PATH, {
			template: '<landing-page></landing-page>'
		})
			.when(STOREFRONT_PATH, {
				templateUrl: 'mainview.html',
				controller: 'defaultController',
				resolve: {
					setExperience: catalogAwareRouteResolverFunctions.setExperience
				}
			})
			.when(STOREFRONT_PATH_WITH_PAGE_ID, {
				templateUrl: 'mainview.html',
				controller: 'defaultController',
				resolve: {
					setExperience: catalogAwareRouteResolverFunctions.setExperience
				}
			})
			.otherwise({
				redirectTo: LANDING_PAGE_PATH
			});

		$logProvider.debugEnabled(false);
	})
	.run((
		$rootScope: angular.IRootScopeService,
		$log: angular.ILogService,
		$q: angular.IQService,
		DEFAULT_RULE_NAME: string,
		EVENTS: any,
		smartEditBootstrapGateway: any,
		toolbarServiceFactory: any,
		perspectiveService: PerspectiveService,
		gatewayFactory: any,
		loadConfigManagerService: any,
		bootstrapService: BootstrapService,
		iFrameManager: any,
		restServiceFactory: IRestServiceFactory,
		delegateRestService: DelegateRestService,
		sharedDataService: SharedDataService,
		urlService: IUrlService,
		featureService: IFeatureService,
		storageService: StorageService,
		renderService: any,
		closeOpenModalsOnBrowserBack: any,
		authorizationService: any,
		permissionService: any,
		httpErrorInterceptorService: any,
		unauthorizedErrorInterceptor: any,
		resourceNotFoundErrorInterceptor: any,
		nonValidationErrorInterceptor: any,
		previewErrorInterceptor: any,
		retryInterceptor: any,
		yjQuery: any,
		SMARTEDIT_IFRAME_WRAPPER_ID: string,
		permissionsRegistrationService: PermissionsRegistrationService
	) => {
		'ngInject';
		gatewayFactory.initListener();
		httpErrorInterceptorService.addInterceptor(retryInterceptor);
		httpErrorInterceptorService.addInterceptor(unauthorizedErrorInterceptor);
		httpErrorInterceptorService.addInterceptor(resourceNotFoundErrorInterceptor);
		httpErrorInterceptorService.addInterceptor(nonValidationErrorInterceptor);
		httpErrorInterceptorService.addInterceptor(previewErrorInterceptor);

		loadConfigManagerService.loadAsObject().then((configurations: any) => {
			sharedDataService.set('defaultToolingLanguage', configurations.defaultToolingLanguage);
		});

		const smartEditTitleToolbarService = toolbarServiceFactory.getToolbarService("smartEditTitleToolbar");

		smartEditTitleToolbarService.addItems([{
			key: 'topToolbar.leftToolbarTemplate',
			type: 'TEMPLATE',
			include: 'leftToolbarWrapperTemplate.html',
			priority: 1,
			section: 'left'
		}, {
			key: 'topToolbar.logoTemplate',
			type: 'TEMPLATE',
			include: 'logoTemplate.html',
			priority: 2,
			section: 'left'
		}, {
			key: 'topToolbar.deviceSupportTemplate',
			type: 'TEMPLATE',
			include: 'deviceSupportTemplate.html',
			priority: 1,
			section: 'right'
		}, {
			type: 'TEMPLATE',
			key: 'topToolbar.experienceSelectorTemplate',
			className: 'ySEPreviewSelector',
			include: 'experienceSelectorWrapperTemplate.html',
			priority: 1, // first in the middle
			section: 'middle'
		}]);

		const experienceSelectorToolbarService = toolbarServiceFactory.getToolbarService("experienceSelectorToolbar");

		experienceSelectorToolbarService.addItems([{
			key: "bottomToolbar.perspectiveSelectorTemplate",
			type: 'TEMPLATE',
			section: 'right',
			priority: 1,
			include: 'perspectiveSelectorWrapperTemplate.html'
		}]);

		function offSetStorefront() {
			// Set the storefront offset
			yjQuery(SMARTEDIT_IFRAME_WRAPPER_ID).css('padding-top', (yjQuery('.ySmartEditToolbars') as JQuery).height() + 'px');
		}

		smartEditBootstrapGateway.subscribe("reloadFormerPreviewContext", function() {
			offSetStorefront();
			const deferred = $q.defer();
			iFrameManager.initializeCatalogPreview();
			deferred.resolve();
			return deferred.promise;
		});
		smartEditBootstrapGateway.subscribe("loading", (eventId: string, data: any) => {
			const deferred = $q.defer();

			iFrameManager.setCurrentLocation(data.location);
			iFrameManager.showWaitModal();

			const smartEditBootstrapped = getBootstrapNamespace();
			delete smartEditBootstrapped[data.location];

			perspectiveService.clearActivePerspective();

			return deferred.promise;
		});
		smartEditBootstrapGateway.subscribe("bootstrapSmartEdit", (eventId: string, data: any) => {
			offSetStorefront();
			const deferred = $q.defer();
			const smartEditBootstrapped = getBootstrapNamespace();

			if (!smartEditBootstrapped[data.location]) {
				smartEditBootstrapped[data.location] = true;
				loadConfigManagerService.loadAsObject().then((configurations: any) => {
					bootstrapService.bootstrapSEApp(configurations);
					deferred.resolve();
				});
			} else {
				deferred.resolve();
			}
			return deferred.promise;
		});

		smartEditBootstrapGateway.subscribe("smartEditReady", function() {
			const deferred = $q.defer();
			deferred.resolve();

			iFrameManager.hideWaitModal();
			return deferred.promise;
		});

		$rootScope.$on('$routeChangeSuccess', function() {
			closeOpenModalsOnBrowserBack();
		});

		gatewayFactory.createGateway('accessTokens').subscribe("get", function() {
			return $q.when(storageService.getAuthTokens());
		});

		permissionService.registerDefaultRule({
			names: [DEFAULT_RULE_NAME],
			verify: (permissionNameObjs: any) => {
				const permisssionNames = permissionNameObjs.map((permissionName: any) => {
					return permissionName.name;
				});
				return authorizationService.hasGlobalPermissions(permisssionNames);
			}
		});

		// storefront actually loads twice all the JS files, including webApplicationInjector.js, smartEdit must be protected against receiving twice a smartEditBootstrap event
		function getBootstrapNamespace(): any {
			const smarteditWindow = window as any;
			if (smarteditWindow.smartEditBootstrapped) {
				smarteditWindow.smartEditBootstrapped = {};
			}
			return smarteditWindow.smartEditBootstrapped;
		}

		permissionsRegistrationService.registerRulesAndPermissions();
	}
	)
	.controller('defaultController', SmarteditDefaultController)
	.name;
