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

import {TrashedPageListController} from '../../../../../../web/features/cmssmarteditContainer/components/pages/trashedPageList/trashedPageListController';

describe('TrashedPageListController - ', () => {

	let trashedPageListController: TrashedPageListController;
	let cmsitemsUri: any;
	let $rootScope: angular.IRootScopeService;

	const uriContext = "uriContext";

	beforeEach(() => {

		const fixture = AngularUnitTestHelper.prepareModule('trashedPageListControllerModule')
			.mock('catalogService', 'getContentCatalogsForSite').and.returnResolvedPromise([{
				catalogId: 'someCatalogId',
				name: 'Some Catalog Name'
			}, {
				catalogId: 'someOtherCatalogId',
				name: 'Some Other Catalog Name'
			}])
			.mock('urlService', 'buildUriContext').and.returnValue(uriContext)
			.mock('cmsitemsUri', '$get')
			.mockConstant('PAGE_LIST_PATH', '/abc::siteId:catalogId:catalogVersion')
			.controller('trashedPageListController', {
				$routeParams: {
					siteId: 'someSiteId',
					catalogId: 'someCatalogId',
					catalogVersion: 'someCatalogVersion'
				}
			});

		$rootScope = fixture.injected.$rootScope;

		cmsitemsUri = fixture.mocks.cmsitemsUri;
		trashedPageListController = fixture.controller;

		$rootScope.$digest();
	});

	it('init should set trashedPageListConfig with the right attributes', () => {
		expect(trashedPageListController.trashedPageListConfig.sortBy).toEqual('name');
		expect(trashedPageListController.trashedPageListConfig.reversed).toEqual(false);
		expect(trashedPageListController.trashedPageListConfig.itemsPerPage).toEqual(10);
		expect(trashedPageListController.trashedPageListConfig.displayCount).toEqual(true);
		expect(trashedPageListController.trashedPageListConfig.injectedContext).toEqual({
			uriContext
		});
		expect(trashedPageListController.trashedPageListConfig.uri).toEqual(cmsitemsUri);
		expect(trashedPageListController.trashedPageListConfig.queryParams).toEqual({
			catalogId: 'someCatalogId',
			catalogVersion: 'someCatalogVersion',
			typeCode: 'AbstractPage',
			itemSearchParams: 'pageStatus:deleted'
		});
	});

	it('should initialize with a catalog name to display and other site related params', () => {
		expect(trashedPageListController.siteUID).toEqual('someSiteId');
		expect(trashedPageListController.catalogId).toEqual('someCatalogId');
		expect(trashedPageListController.catalogVersion).toEqual('someCatalogVersion');
		expect(trashedPageListController.catalogName).toEqual('Some Catalog Name');
	});

	it('calling reset should clear the mask field', () => {
		trashedPageListController.mask = 'initial mask';
		trashedPageListController.reset();
		expect(trashedPageListController.mask).toEqual('');
	});

});
