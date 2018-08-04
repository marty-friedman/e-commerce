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
import * as angular from 'angular';
import {UrlService} from 'smartedit/services';

describe('test urlService ', () => {

	let gatewayProxy: jasmine.SpyObj<any>;
	let urlService: UrlService;

	beforeEach(angular.mock.module('functionsModule'));

	beforeEach(() => {
		const fixture = AngularUnitTestHelper.prepareModule('smarteditServicesModule')
			.mock('gatewayProxy', 'initForService')
			.mockConstant('CONTEXT_SITE_ID', 'some site id')
			.mockConstant('CONTEXT_CATALOG', 'some context catalog')
			.mockConstant('CONTEXT_CATALOG_VERSION', 'some catalog version')
			.mockConstant('PAGE_CONTEXT_SITE_ID', 'some page context site ID')
			.mockConstant('PAGE_CONTEXT_CATALOG', 'some page context catalog')
			.mockConstant('PAGE_CONTEXT_CATALOG_VERSION', 'some page context catalog version')
			.service('urlService');
		gatewayProxy = fixture.mocks.gatewayProxy;
		urlService = fixture.service;
	});

	it('openUrlInPopup function is left empty to enable proxying', function() {
		expect(urlService.openUrlInPopup).toBeEmptyFunction();
	});

	it('url service inits a private gateway', function() {
		expect(gatewayProxy.initForService).toHaveBeenCalledWith(urlService, ['openUrlInPopup', 'path']);
	});
});
