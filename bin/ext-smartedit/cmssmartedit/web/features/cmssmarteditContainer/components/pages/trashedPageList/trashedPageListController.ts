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
import {IUrlService} from 'smarteditcommons';

/* @ngInject */
export class TrashedPageListController {

	// --------------------------------------------------------------------------------------
	// Variables
	// --------------------------------------------------------------------------------------
	// site params
	siteUID: string;
	catalogId: string;
	catalogVersion: string;
	catalogName: string;
	uriContext: any;

	// dynamic paged list params
	trashedPageListConfig: any;
	mask: string;

	// --------------------------------------------------------------------------------------
	// Constructor
	// --------------------------------------------------------------------------------------
	constructor(
		private $routeParams: ng.route.IRouteParamsService,
		protected $location: ng.ILocationService,
		protected PAGE_LIST_PATH: string,
		private urlService: IUrlService,
		private catalogService: any,
		private cmsitemsUri: any
	) {
		this.initialize();
	}

	public reset(): void {
		this.mask = '';
	}

	public backToPagelist(): void {
		this.$location.path(this.PAGE_LIST_PATH
			.replace(":siteId", this.siteUID)
			.replace(":catalogId", this.catalogId)
			.replace(":catalogVersion", this.catalogVersion));
	}

	private initialize() {
		this.siteUID = this.$routeParams.siteId;
		this.catalogId = this.$routeParams.catalogId;
		this.catalogVersion = this.$routeParams.catalogVersion;
		this.uriContext = this.urlService.buildUriContext(this.siteUID, this.catalogId, this.catalogVersion);

		this.trashedPageListConfig = {
			sortBy: 'name',
			reversed: false,
			itemsPerPage: 10,
			displayCount: true
		};

		// set uri for retrieving trashed pages
		this.trashedPageListConfig.uri = this.cmsitemsUri;
		this.trashedPageListConfig.queryParams = {
			catalogId: this.catalogId,
			catalogVersion: this.catalogVersion,
			typeCode: 'AbstractPage',
			itemSearchParams: 'pageStatus:deleted'
		};

		this.trashedPageListConfig.keys = [{
			property: 'name',
			i18n: 'se.cms.pagelist.headerpagename',
			sortable: true
		}, {
			property: 'itemtype',
			i18n: 'se.cms.pagelist.headerpagetype',
			sortable: true
		}, {
			property: 'numberOfRestrictions',
			i18n: 'se.cms.pagelist.headerrestrictions'
		}, {
			property: 'modifiedtime',
			i18n: 'se.cms.trashedpagelist.trashed.date',
			sortable: true
		}, {
			property: 'syncStatus',
			i18n: 'se.cms.actionitem.page.sync'
		}];

		// injectedContext Object. This object is passed to the client-paged-list directive.
		this.trashedPageListConfig.injectedContext = {
			uriContext: this.uriContext
		};

		// set seach params
		this.mask = "";

		this.catalogService.getContentCatalogsForSite(this.siteUID).then((catalogs: any) => {
			this.catalogName = catalogs.filter((catalog: any) => {
				return catalog.catalogId === this.catalogId;
			})[0].name;
		});

		// renderers Object that contains custom HTML renderers for a given key
		this.trashedPageListConfig.renderers = {
			numberOfRestrictions() {
				return '<restrictions-page-list-icon data-number-of-restrictions="item.restrictions.length"/>';
			},
			modifiedtime() {
				return '<div>{{item.modifiedtime | date:"short"}}</div>';
			},
			syncStatus() {
				return '<div data-recompile-dom="item.reloadSynchIcon"><page-list-sync-icon data-uri-context="injectedContext.uriContext" data-page-id="item.uuid" /></div>';
			}
		};
	}
}

export const trashedPageListControllerModule = angular
	.module('trashedPageListControllerModule', ['smarteditServicesModule', 'catalogServiceModule', 'pageListTemplatePrinterModule', 'resourceLocationsModule'])
	.controller('trashedPageListController', TrashedPageListController);
