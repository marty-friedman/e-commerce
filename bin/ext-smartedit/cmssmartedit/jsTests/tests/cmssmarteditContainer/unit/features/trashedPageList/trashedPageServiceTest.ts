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
import 'jasmine';
import {TrashedPageService} from 'cmssmarteditcontainer/services/pages/TrashedPageService';

describe('TrashedPageService - ', () => {

	let trashedPageService: TrashedPageService;
	let cmsitemsRestService: any;
	let $rootScope: angular.IRootScopeService;

	const EXPECTED_TOTAL_COUNT = 9;

	let promise: Promise<any>;

	beforeEach(() => {

		const fixture = AngularUnitTestHelper.prepareModule('cmsSmarteditServicesModule')
			.mock('cmsitemsRestService', 'get').and.returnResolvedPromise({
				response: 'someResonse',
				pagination: {
					count: 3,
					page: 0,
					totalCount: EXPECTED_TOTAL_COUNT,
					totalPages: 1
				}
			})
			.service('trashedPageService');

		trashedPageService = fixture.service;
		cmsitemsRestService = fixture.mocks.cmsitemsRestService;
		$rootScope = fixture.injected.$rootScope;

		// WHEN
		promise = trashedPageService.getTrashedPagesCount({
			CONTEXT_CATALOG: 'someCatalog',
			CONTEXT_CATALOG_VERSION: 'someCatalogVersion'
		});
		$rootScope.$digest();

	});

	it('getTrashedPagesCount should call cmsitemsapi with the right parameters', () => {
		expect(cmsitemsRestService.get).toHaveBeenCalledWith({
			pageSize: 10,
			currentPage: 0,
			typeCode: 'AbstractPage',
			itemSearchParams: 'pageStatus:deleted',
			catalogId: 'someCatalog',
			catalogVersion: 'someCatalogVersion'
		});
	});

	it('getTrashedPagesCount should return the total number of trashed pages', () => {
		promise.then(function(totalCount) {
			expect(totalCount).toBe(EXPECTED_TOTAL_COUNT);
		});
	});

});
