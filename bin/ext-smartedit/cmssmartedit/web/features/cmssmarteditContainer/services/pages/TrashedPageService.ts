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
import {cache} from 'smarteditcommons';

interface UriContext {
	CONTEXT_CATALOG: string;
	CONTEXT_CATALOG_VERSION: string;
}
/**
 * @ngdoc service
 * @name cmsSmarteditServicesModule.service:TrashedPageService
 *
 * @description
 * Used by pageListController
 */
/* @ngInject */
export class TrashedPageService {

	constructor(private cmsitemsRestService: any) {}

	/** 
	 * @ngdoc method
	 * @name cmsSmarteditServicesModule.service:TrashedPageService#getTrashedPagesCount
	 * @methodOf cmsSmarteditServicesModule.service:TrashedPageService
	 *
	 * @description
	 * Get the number of trashed pages
	 * 
	 * @returns {object} containing the total number of trashed pages
	 */
	@cache('Frequent', 3)
	getTrashedPagesCount(uriContext: UriContext) {

		const requestParams = {
			pageSize: 10,
			currentPage: 0,
			typeCode: 'AbstractPage',
			itemSearchParams: 'pageStatus:deleted',
			catalogId: uriContext.CONTEXT_CATALOG,
			catalogVersion: uriContext.CONTEXT_CATALOG_VERSION
		};
		return this.cmsitemsRestService.get(requestParams).then(function(result: any) {
			return result.pagination.totalCount;
		});
	}

}
