import {StorageService} from 'smartedit/services';

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
describe('inner storage service', () => {

	const gatewayProxy: any = jasmine.createSpyObj('gatewayProxy', ['initForService']);

	let storageService: StorageService;

	beforeEach(() => {
		storageService = new StorageService(gatewayProxy);
	});

	it('initialized by gatewayProxy', function() {
		expect(storageService.gatewayId).toBe('storage');
		expect(gatewayProxy.initForService).toHaveBeenCalledWith(storageService);
	});

	it('all functions are left empty', function() {
		expect(storageService.isInitialized).toBeEmptyFunction();
		expect(storageService.storeAuthToken).toBeEmptyFunction();
		expect(storageService.getAuthToken).toBeEmptyFunction();
		expect(storageService.removeAuthToken).toBeEmptyFunction();
		expect(storageService.removeAllAuthTokens).toBeEmptyFunction();
	});
});
