import * as angular from 'angular';
import './requireLegacyJsFiles';

import {IFeatureService} from 'smarteditcommons';

angular.module('personalizationsmarteditcontainermodule', [
	'personalizationsmarteditcontainerTemplates',
	'personalizationsmarteditContextServiceModule',
	'personalizationsmarteditRestServiceModule',
	'ui.bootstrap',
	'personalizationsmarteditCommons',
	'functionsModule',
	'personalizationsmarteditPreviewServiceModule',
	'personalizationsmarteditManagerModule',
	'personalizationsmarteditManagerViewModule',
	'personalizationsmarteditContextMenu',
	'featureServiceModule',
	'perspectiveServiceModule',
	'iFrameManagerModule',
	'personalizationsmarteditCombinedViewModule',
	'personalizationsmarteditSegmentViewModule',
	'personalizationsmarteditToolbarContextModule',
	'crossFrameEventServiceModule',
	'seConstantsModule',
	'personalizationsmarteditRulesAndPermissionsRegistrationModule',
	'gatewayFactoryModule',
	'yjqueryModule',
	'eventServiceModule',
	'personalizationsmarteditCustomizeViewModule',
	'languageServiceModule'
])
	.constant('PERSONALIZATION_PERSPECTIVE_KEY', 'personalizationsmartedit.perspective')
	.factory('personalizationsmarteditIFrameUtils', (
		$filter: angular.IFilterService,
		iFrameManager: any,
		personalizationsmarteditContextService: any,
		personalizationsmarteditPreviewService: any,
		personalizationsmarteditMessageHandler: any) => {
		'ngInject';

		const self = this;

		this.reloadPreview = (resourcePath: any, previewTicketId: any) => {
			iFrameManager.loadPreview(resourcePath, previewTicketId);
		};

		this.clearAndReloadPreview = () => {
			const previewTicketId = personalizationsmarteditContextService.getSeData().sePreviewData.previewTicketId;
			personalizationsmarteditPreviewService.removePersonalizationDataFromPreview(previewTicketId).then(() => {
				const previewData = personalizationsmarteditContextService.getSeData().sePreviewData;
				self.reloadPreview(previewData.resourcePath, previewData.previewTicketId);
			}, () => {
				personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.updatingpreviewticket'));
			});
		};

		return this;
	})
	.controller('topToolbarMenuController',
	function(
		$scope,
		personalizationsmarteditManager,
		personalizationsmarteditManagerView,
		personalizationsmarteditIFrameUtils,
		personalizationsmarteditContextService,
		personalizationsmarteditContextUtils,
		personalizationsmarteditCombinedView) {
		'ngInject';

		$scope.status = {
			isopen: false
		};

		$scope.preventDefault = function(oEvent: any) {
			oEvent.stopPropagation();
		};

		$scope.createCustomizationClick = function() {
			personalizationsmarteditManager.openCreateCustomizationModal();
		};

		$scope.managerViewClick = function() {
			personalizationsmarteditContextUtils.clearCombinedViewCustomizeContext(personalizationsmarteditContextService);
			personalizationsmarteditContextUtils.clearCustomizeContextAndReloadPreview(personalizationsmarteditIFrameUtils, personalizationsmarteditContextService);
			personalizationsmarteditContextUtils.clearCombinedViewContextAndReloadPreview(personalizationsmarteditIFrameUtils, personalizationsmarteditContextService);
			personalizationsmarteditManagerView.openManagerAction();
		};

		$scope.combinedViewClick = function() {
			personalizationsmarteditContextUtils.clearCustomizeContextAndReloadPreview(personalizationsmarteditIFrameUtils, personalizationsmarteditContextService);
			personalizationsmarteditCombinedView.openManagerAction();
		};
		$scope.isCustomizeCustomizationSelected = function() {
			return personalizationsmarteditContextService.getCustomize().selectedCustomization;
		};
		$scope.isCombinedViewCustomizationSelected = function() {
			return personalizationsmarteditContextService.getCombinedView().customize.selectedCustomization;
		};

	})
	.run((
		yjQuery: any,
		domain: any) => {
		'ngInject';

		const loadCSS = (href: string) => {
			const cssLink = yjQuery("<link rel='stylesheet' type='text/css' href='" + href + "'>");
			yjQuery("head").append(cssLink);
		};
		loadCSS(domain + "/personalizationsmartedit/css/style.css");

	})
	.run((
		PERSONALIZATION_PERSPECTIVE_KEY: string,
		personalizationsmarteditContextServiceReverseProxy: any,
		personalizationsmarteditContextService: any, // dont remove
		personalizationsmarteditContextModal: any, // dont remove
		featureService: IFeatureService,
		perspectiveService: any) => {
		'ngInject';

		featureService.addToolbarItem({
			toolbarId: 'experienceSelectorToolbar',
			key: 'personalizationsmartedit.container.pagecustomizations.toolbar',
			type: 'TEMPLATE',
			nameI18nKey: 'personalization.toolbar.pagecustomizations',
			priority: 4,
			section: 'left',
			include: 'personalizationsmarteditCustomizeViewWrapperTemplate.html',
			keepAliveOnClose: false,
			permissions: ['se.edit.page']
		});
		featureService.addToolbarItem({
			toolbarId: 'experienceSelectorToolbar',
			key: 'personalizationsmartedit.container.combinedview.toolbar',
			type: 'TEMPLATE',
			nameI18nKey: 'personalization.toolbar.combinedview.name',
			priority: 6,
			section: 'left',
			include: 'personalizationsmarteditCombinedViewMenuTemplate.html',
			keepAliveOnClose: false,
			permissions: ['se.read.page']
		});
		featureService.addToolbarItem({
			toolbarId: 'experienceSelectorToolbar',
			key: 'personalizationsmartedit.container.manager.toolbar',
			type: 'TEMPLATE',
			nameI18nKey: 'personalization.toolbar.library.name',
			priority: 8,
			section: 'left',
			include: 'personalizationsmarteditCustomizationManagMenuTemplate.html',
			keepAliveOnClose: false,
			permissions: ['se.edit.page']
		});
		featureService.register({
			key: 'personalizationsmartedit.context.service',
			nameI18nKey: 'personalization.context.service.name',
			descriptionI18nKey: 'personalization.context.service.description',
			enablingCallback: () => {
				const personalization = personalizationsmarteditContextService.getPersonalization();
				personalization.enabled = true;
				personalizationsmarteditContextService.setPersonalization(personalization);
			},
			disablingCallback: () => {
				const personalization = personalizationsmarteditContextService.getPersonalization();
				personalization.enabled = false;
				personalizationsmarteditContextService.setPersonalization(personalization);
			},
			permissions: ['se.edit.page']
		});

		perspectiveService.register({
			key: PERSONALIZATION_PERSPECTIVE_KEY,
			nameI18nKey: 'personalization.perspective.name',
			descriptionI18nKey: 'personalization.perspective.description',
			features: ['personalizationsmartedit.context.service',
				'personalizationsmartedit.container.pagecustomizations.toolbar',
				'personalizationsmartedit.container.manager.toolbar',
				'personalizationsmartedit.container.combinedview.toolbar',
				'personalizationsmarteditSharedSlot',
				'personalizationsmarteditComponentLightUp',
				'personalizationsmarteditCombinedViewComponentLightUp',
				'personalizationsmartedit.context.add.action',
				'personalizationsmartedit.context.edit.action',
				'personalizationsmartedit.context.delete.action',
				'personalizationsmartedit.context.info.action',
				'personalizationsmartedit.context.component.edit.action',
				'personalizationsmartedit.context.show.action.list',
				'se.contextualMenu',
				'se.emptySlotFix',
				'externalcomponentbutton',
				'personalizationsmarteditExternalComponentDecorator'
			],
			perspectives: [],
			permissions: ['se.personalization.open']
		});

	})
	.run((
		PERSONALIZATION_PERSPECTIVE_KEY: string,
		personalizationsmarteditContextUtils: any,
		personalizationsmarteditContextService: any,
		personalizationsmarteditIFrameUtils: any,
		EVENT_PERSPECTIVE_UNLOADING: any,
		crossFrameEventService: any) => {
		'ngInject';

		const clearAllContextsAndReloadPreview = () => {
			personalizationsmarteditContextUtils.clearCombinedViewCustomizeContext(personalizationsmarteditContextService);
			personalizationsmarteditContextUtils.clearCustomizeContextAndReloadPreview(personalizationsmarteditIFrameUtils, personalizationsmarteditContextService);
			personalizationsmarteditContextUtils.clearCombinedViewContextAndReloadPreview(personalizationsmarteditIFrameUtils, personalizationsmarteditContextService);
		};

		crossFrameEventService.subscribe(EVENT_PERSPECTIVE_UNLOADING, function(eventId: any, unloadingPerspective: string) {
			if (unloadingPerspective === PERSONALIZATION_PERSPECTIVE_KEY) {
				clearAllContextsAndReloadPreview();
			}
		});

	})
	.run((
		$q: angular.IQService,
		PERSONALIZATION_PERSPECTIVE_KEY: string,
		personalizationsmarteditContextService: any,
		personalizationsmarteditContextUtils: any,
		personalizationsmarteditMessageHandler: any,
		personalizationsmarteditRestService: any,
		personalizationsmarteditUtils: any,
		EVENTS: any,
		SWITCH_LANGUAGE_EVENT: any,
		smartEditBootstrapGateway: any,
		systemEventService: any,
		perspectiveService: any) => {
		'ngInject';

		const clearAllContexts = () => {
			personalizationsmarteditContextUtils.clearCombinedViewCustomizeContext(personalizationsmarteditContextService);
			personalizationsmarteditContextUtils.clearCustomizeContext(personalizationsmarteditContextService);
			personalizationsmarteditContextUtils.clearCombinedViewContext(personalizationsmarteditContextService);
		};

		systemEventService.registerEventHandler(EVENTS.EXPERIENCE_UPDATE, function() {
			clearAllContexts();
			return $q.when();
		});

		systemEventService.registerEventHandler(SWITCH_LANGUAGE_EVENT, function() {
			const combinedView = personalizationsmarteditContextService.getCombinedView();
			angular.forEach(combinedView.selectedItems, function(item: any) {
				personalizationsmarteditUtils.getAndSetCatalogVersionNameL10N(item.variation);
			});
			personalizationsmarteditContextService.setCombinedView(combinedView);
			return $q.when();
		});

		smartEditBootstrapGateway.subscribe("smartEditReady", (eventId: any, data: any) => {

			const oldPreviewTicketId = (personalizationsmarteditContextService.getSeData().sePreviewData || {}).previewTicketId;
			personalizationsmarteditContextService.refreshPreviewData().then(() => {
				const newPreviewTicketId = personalizationsmarteditContextService.getSeData().sePreviewData.previewTicketId;
				if (oldPreviewTicketId !== newPreviewTicketId) {
					personalizationsmarteditRestService.getPreviewTicket(newPreviewTicketId).then((newPreview: any) => {
						if (newPreview.variations.length === 0) {
							personalizationsmarteditRestService.getPreviewTicket(oldPreviewTicketId).then((oldPreview: any) => {
								if (oldPreview.variations.length > 0 && newPreview.variations.length === 0) {
									clearAllContexts();
								}
							}, () => {
								clearAllContexts(); // old preview ticket not found
							});
						}
					});
				}
			});

			personalizationsmarteditContextService.refreshExperienceData().then(() => {
				const experience = personalizationsmarteditContextService.getSeData().seExperienceData;
				const activePerspective = perspectiveService.getActivePerspective() || {};
				if (activePerspective.key === PERSONALIZATION_PERSPECTIVE_KEY && experience.pageContext.catalogVersionUuid !== experience.catalogDescriptor.catalogVersionUuid) {
					const warningConf = {
						message: 'personalization.warning.pagefromparent',
						timeout: -1
					};
					personalizationsmarteditMessageHandler.sendWarning(warningConf);
				}
			}).finally(() => {
				personalizationsmarteditContextService.applySynchronization();
			});
		});

	});
