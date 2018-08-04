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

import {cacheAnnotationFactory, InViewElementObserver, PolyfillService, PriorityService, SmarteditBoostrapGateway, TestModeService} from 'smarteditcommons';
import {
	BootstrapService,
	ConfigurationExtractorService,
	DelegateRestService,
	DragAndDropCrossOrigin,
	FeatureService,
	NotificationMouseLeaveDetectionService,
	NotificationService,
	PageInfoService,
	PermissionsRegistrationService,
	PerspectiveService,
	PreviewService,
	ProductService,
	RestServiceFactory,
	SharedDataService,
	SiteService,
	StorageService,
	UrlService,
	WaitDialogService
} from 'smarteditcontainer/services';

/**
 * @ngdoc overview
 * @name smarteditServicesModule
 *
 * @description
 * Module containing all the services shared within the smartedit container application
 */
export const smarteditServicesModule = angular
	.module('smarteditServicesModule', [
		'seConstantsModule',
		'ngResource',
		'gatewayProxyModule',
		'operationContextServiceModule',
		'crossFrameEventServiceModule',
		'iFrameManagerModule',
		'browserServiceModule',
		'permissionServiceModule',
		'ngCookies',
		'sessionServiceModule',
		'functionsModule',
		'toolbarModule'
	])
	.service('smartEditBootstrapGateway', SmarteditBoostrapGateway)
	.service('configurationExtractorService', ConfigurationExtractorService)
	.service('bootstrapService', BootstrapService)
	.service('urlService', UrlService)
	.service('sharedDataService', SharedDataService)
	.service('pageInfoService', PageInfoService)
	.service('testModeService', TestModeService)
	.service('polyfillService', PolyfillService)
	.service('waitDialogService', WaitDialogService)
	.service('delegateRestService', DelegateRestService)
	.service('featureService', FeatureService)
	.service('restServiceFactory', RestServiceFactory)
	.service('perspectiveService', PerspectiveService)
	.service('previewService', PreviewService)
	.service('priorityService', PriorityService)
	.service('productService', ProductService)
	.service('siteService', SiteService)
	.service('notificationService', NotificationService)
	.service('notificationMouseLeaveDetectionService', NotificationMouseLeaveDetectionService)
	.service('dragAndDropCrossOrigin', DragAndDropCrossOrigin)
	.service('inViewElementObserver', InViewElementObserver)
	.service('storageService', StorageService)
	.service('permissionsRegistrationService', PermissionsRegistrationService)
	.factory('cacheAnnotation', cacheAnnotationFactory)
	.run((cacheAnnotation: any, previewService: any) => {
		'ngInject';
	});