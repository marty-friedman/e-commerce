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
import {DelegateRestService} from 'smartedit/services';

describe('test DelegateRestService ', () => {

	let delegateRestService: DelegateRestService;

	const gatewayProxy: any = jasmine.createSpyObj<any>('gatewayProxy', ['initForService']);

	beforeEach(() => {
		delegateRestService = new DelegateRestService(gatewayProxy);
	});

	it('is initialized with gatewayProxy', function() {
		expect(gatewayProxy.initForService).toHaveBeenCalledWith(delegateRestService);
	});

	it('delegateForVoid is left unimplemented', function() {
		expect(delegateRestService.delegateForVoid).toBeEmptyFunction();
	});

	it('delegateForSingleInstance is left unimplemented', function() {
		expect(delegateRestService.delegateForSingleInstance).toBeEmptyFunction();
	});

	it('delegateForArray is left unimplemented', function() {
		expect(delegateRestService.delegateForArray).toBeEmptyFunction();
	});

	it('delegateForPage is left unimplemented', function() {
		expect(delegateRestService.delegateForPage).toBeEmptyFunction();
	});

});
