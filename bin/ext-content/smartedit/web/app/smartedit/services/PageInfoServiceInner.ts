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
import {IPageInfoService} from 'smarteditcommons';
import {ComponentHandlerService} from "smartedit/services/ComponentHandlerService";

/** @internal */
/* @ngInject */
export class PageInfoService extends IPageInfoService {

	/* @internal */
	constructor(gatewayProxy: any, private componentHandlerService: ComponentHandlerService) {
		super();
		gatewayProxy.initForService(this, ['getPageUID', 'getPageUUID', 'getCatalogVersionUUIDFromPage']);
	}

    /**
     * When the time comes to deprecate these 3 functions from componentHandlerService in the inner app, we will need
     * to migrate their implementations to here.
     */

	/* @proxy */
	getPageUID(): angular.IPromise<string> {
		return this.componentHandlerService.getPageUID();
	}

	/* @proxy */
	getPageUUID(): angular.IPromise<string> {
		return this.componentHandlerService.getPageUUID();
	}

	/* @proxy */
	getCatalogVersionUUIDFromPage(): angular.IPromise<string> {
		return this.componentHandlerService.getCatalogVersionUUIDFromPage();
	}


}
