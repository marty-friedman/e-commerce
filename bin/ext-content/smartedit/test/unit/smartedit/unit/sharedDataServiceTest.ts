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
import {SharedDataService} from 'smartedit/services';

describe('test sharedDataService', function() {

	// --------------------------------------------------------------------------------------
	// Variables
	// --------------------------------------------------------------------------------------
	let gatewayProxy: jasmine.SpyObj<any>;
	let sharedDataService: SharedDataService;

	beforeEach(angular.mock.module('functionsModule'));

	beforeEach(() => {
		const fixture = AngularUnitTestHelper.prepareModule('smarteditServicesModule')
			.mock('gatewayProxy', 'initForService')
			.service('sharedDataService');
		gatewayProxy = fixture.mocks.gatewayProxy;
		sharedDataService = fixture.service;
	});

	it('set function is left empty to enable proxying', () => {
		expect(sharedDataService.set).toBeEmptyFunction();
	});

	it('get function is left empty to enable proxying', () => {
		expect(sharedDataService.get).toBeEmptyFunction();
	});

	it('shared data service inits a private gateway', () => {
		expect(gatewayProxy.initForService).toHaveBeenCalledWith(sharedDataService);
	});
});
