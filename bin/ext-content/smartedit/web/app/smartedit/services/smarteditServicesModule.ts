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

import {cacheAnnotationFactory, InViewElementObserver, PolyfillService, PriorityService, TestModeService} from 'smarteditcommons';
import {
	ComponentHandlerService,
	ContextualMenuService,
	DelegateRestService,
	DragAndDropCrossOrigin,
	FeatureService,
	NotificationMouseLeaveDetectionService,
	NotificationService,
	PageInfoService,
	PerspectiveService,
	PreviewService,
	RestServiceFactory,
	SharedDataService,
	StorageService,
	UrlService,
	WaitDialogService
} from 'smartedit/services';

/**
 * @ngdoc overview
 * @name smarteditServicesModule
 *
 * @description
 * Module containing all the services shared within the smartedit application
 */
export const smarteditServicesModule = angular
	.module('smarteditServicesModule', [
		'seConstantsModule',
		'ngResource',
		'gatewayProxyModule',
		'eventServiceModule',
		'crossFrameEventServiceModule',
		'permissionServiceModule',
		'functionsModule',
		'decoratorServiceModule'
	])
	.service('urlService', UrlService)
	.service('sharedDataService', SharedDataService)
	.service('componentHandlerService', ComponentHandlerService)
	.service('pageInfoService', PageInfoService)
	.service('testModeService', TestModeService)
	.service('polyfillService', PolyfillService)
	.service('waitDialogService', WaitDialogService)
	.service('delegateRestService', DelegateRestService)
	.service('restServiceFactory', RestServiceFactory)
	.service('previewService', PreviewService)
	.service('priorityService', PriorityService)
	.service('perspectiveService', PerspectiveService)
	.service('featureService', FeatureService)
	.service('notificationService', NotificationService)
	.service('notificationMouseLeaveDetectionService', NotificationMouseLeaveDetectionService)
	.service('storageService', StorageService)
	.service('contextualMenuService', ContextualMenuService)
	.service('dragAndDropCrossOrigin', DragAndDropCrossOrigin)
	.service('inViewElementObserver', InViewElementObserver)
	/**
	 * Maintaining for backwards compatibility, but use ContextualMenuService from now on
	 * @deprecated 6.5
	 */
	.factory('ContextualMenuService', ContextualMenuService)
	.factory('cacheAnnotation', cacheAnnotationFactory)
	.run((cacheAnnotation: any, notificationMouseLeaveDetectionService: any) => {
		'ngInject';
	});

