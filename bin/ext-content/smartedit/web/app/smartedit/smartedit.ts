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
import * as lo from 'lodash';
import {instrument, Cloneable, IDragAndDropEvents, IFeatureService, IPageInfoService, IPerspectiveService, ISharedDataService, PolyfillService, TestModeService} from 'smarteditcommons';
import {smarteditServicesModule, DelegateRestService} from 'smartedit/services';
import 'smartedit/services/deprecatedSince-6.7';

const TOP_LEVEL_MODULE_NAME = 'smartedit';

export const smarteditModule: string = angular.module(TOP_LEVEL_MODULE_NAME, [
	smarteditServicesModule.name,
	'yjqueryModule',
	'configModule',
	'templateCacheDecoratorModule',
	'sakExecutorDecorator',
	'ui.bootstrap',
	'ngResource',
	'decoratorServiceModule',
	'smartEditContractChangeListenerModule',
	'alertsBoxModule',
	'ui.select',
	'httpAuthInterceptorModule',
	'httpErrorInterceptorServiceModule',
	'unauthorizedErrorInterceptorModule',
	'retryInterceptorModule',
	'resourceNotFoundErrorInterceptorModule',
	'experienceInterceptorModule',
	'gatewayFactoryModule',
	'renderServiceModule',
	'iframeClickDetectionServiceModule',
	'sanitizeHtmlInputModule',
	'resizeComponentServiceModule',
	'languageServiceModule',
	'slotContextualMenuDecoratorModule',
	'contextualMenuDecoratorModule',
	'crossFrameEventServiceModule',
	'pageSensitiveDirectiveModule',
	'seNamespaceModule',
	'experienceServiceModule',
	'componentHandlerServiceModule',
	'browserServiceModule'
])
	.config(($provide: angular.auto.IProvideService, readObjectStructureFactory: () => (arg: Cloneable) => Cloneable, $logProvider: angular.ILogProvider) => {
		'ngInject';

		instrument($provide, readObjectStructureFactory(), TOP_LEVEL_MODULE_NAME);

		$logProvider.debugEnabled(false);
	})
	.directive('html', () => {
		return {
			restrict: "E",
			replace: false,
			transclude: false,
			priority: 1000,
			link: ($scope: any, element: any) => {
				element.addClass('smartedit-html-container');
			}
		};
	})
	.controller('SmartEditController', angular.noop)
	.run((
		systemEventService: any,
		EVENTS: any,
		ID_ATTRIBUTE: string,
		OVERLAY_RERENDERED_EVENT: string,
		SMARTEDIT_DRAG_AND_DROP_EVENTS: IDragAndDropEvents,
		smartEditContractChangeListener: any,
		crossFrameEventService: any,
		perspectiveService: IPerspectiveService,
		languageService: any,
		restServiceFactory: any,
		gatewayFactory: any,
		renderService: any,
		decoratorService: any,
		featureService: IFeatureService,
		permissionService: any,
		resizeComponentService: any,
		seNamespace: any,
		experienceService: any,
		httpErrorInterceptorService: any,
		retryInterceptor: any,
		unauthorizedErrorInterceptor: any,
		resourceNotFoundErrorInterceptor: any,
		lodash: lo.LoDashStatic,
		delegateRestService: DelegateRestService,
		pageInfoService: IPageInfoService,
		browserService: any,
		polyfillService: PolyfillService,
		testModeService: TestModeService,
		sharedDataService: ISharedDataService
	) => {
		'ngInject';
		gatewayFactory.initListener();

		httpErrorInterceptorService.addInterceptor(retryInterceptor);
		httpErrorInterceptorService.addInterceptor(unauthorizedErrorInterceptor);
		httpErrorInterceptorService.addInterceptor(resourceNotFoundErrorInterceptor);

		smartEditContractChangeListener.onComponentsAdded((components: HTMLElement[], isEconomyMode: boolean) => {
			if (!isEconomyMode) {
				seNamespace.reprocessPage();
				resizeComponentService._resizeComponents(true);
				renderService._resizeSlots();
			}
			components.forEach((component) => renderService._createComponent(component));
			systemEventService.sendAsynchEvent(OVERLAY_RERENDERED_EVENT, {addedComponents: components});
		});

		smartEditContractChangeListener.onComponentsRemoved((components: {component: HTMLElement, parent: HTMLElement}[], isEconomyMode: boolean) => {
			if (!isEconomyMode) {
				seNamespace.reprocessPage();
				renderService._resizeSlots();
			}
			components.forEach((entry) => renderService._destroyComponent(entry.component, entry.parent));
			systemEventService.sendAsynchEvent(OVERLAY_RERENDERED_EVENT, {removedComponents: lodash.map(components, 'component')});
		});

		smartEditContractChangeListener.onComponentResized((component: any) => {
			seNamespace.reprocessPage();
			renderService._resizeSlots();
			renderService._updateComponentSizeAndPosition(component);
		});

		smartEditContractChangeListener.onComponentRepositioned((component: any) => {
			renderService._updateComponentSizeAndPosition(component);
		});

		smartEditContractChangeListener.onComponentChanged((component: any, oldAttributes: any) => {
			seNamespace.reprocessPage();
			renderService._resizeSlots();
			renderService._destroyComponent(component, component.parent, oldAttributes);
			renderService._createComponent(component);
		});

		smartEditContractChangeListener.onPageChanged((pageUUID: string) => {
			pageInfoService.getCatalogVersionUUIDFromPage().then((catalogVersionUUID: string) => {
				pageInfoService.getPageUID().then((pageUID: string) => {
					experienceService.updateExperiencePageContext(catalogVersionUUID, pageUID);
				});
			});
		});

		if (polyfillService.isEligibleForEconomyMode()) {
			systemEventService.registerEventHandler(SMARTEDIT_DRAG_AND_DROP_EVENTS.DRAG_DROP_START, function() {
				smartEditContractChangeListener.setEconomyMode(true);
			});

			systemEventService.registerEventHandler(SMARTEDIT_DRAG_AND_DROP_EVENTS.DRAG_DROP_END, function() {
				seNamespace.reprocessPage();
				resizeComponentService._resizeComponents(true);
				renderService._resizeSlots();
				smartEditContractChangeListener.setEconomyMode(false);
			});
		}

		systemEventService.registerEventHandler(EVENTS.AUTHORIZATION_SUCCESS, (evtId: string, evtData: any) => {
			if (evtData.userHasChanged) {
				perspectiveService.refreshPerspective();
			}
		});

		crossFrameEventService.subscribe(EVENTS.PAGE_CHANGE, () => {
			perspectiveService.refreshPerspective();
			languageService.registerSwitchLanguage();
		});

		smartEditContractChangeListener.initListener();

		// Feature registration
		featureService.register({
			key: 'se.emptySlotFix',
			nameI18nKey: 'se.emptyslotfix',
			enablingCallback: () => {
				resizeComponentService._resizeComponents(true);
			},
			disablingCallback: () => {
				resizeComponentService._resizeComponents(false);
			}
		});

		featureService.addDecorator({
			key: 'se.contextualMenu',
			nameI18nKey: 'contextualMenu'
		});

		featureService.addDecorator({
			key: 'se.slotContextualMenu',
			nameI18nKey: 'se.slot.contextual.menu'
		});

	}).name;
