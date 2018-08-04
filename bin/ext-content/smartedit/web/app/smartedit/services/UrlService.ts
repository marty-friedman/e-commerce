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
import {IUrlService} from 'smarteditcommons';

/** @internal */
/* @ngInject */
export class UrlService extends IUrlService {

	public gatewayId: string = "UrlService";

	constructor(private gatewayProxy: any, PAGE_CONTEXT_SITE_ID: string, PAGE_CONTEXT_CATALOG: string, PAGE_CONTEXT_CATALOG_VERSION: string, CONTEXT_SITE_ID: string, CONTEXT_CATALOG: string, CONTEXT_CATALOG_VERSION: string) {
		super(PAGE_CONTEXT_SITE_ID, PAGE_CONTEXT_CATALOG, PAGE_CONTEXT_CATALOG_VERSION, CONTEXT_SITE_ID, CONTEXT_CATALOG, CONTEXT_CATALOG_VERSION);
		this.gatewayProxy.initForService(this, ['openUrlInPopup', 'path']);
	}
}
