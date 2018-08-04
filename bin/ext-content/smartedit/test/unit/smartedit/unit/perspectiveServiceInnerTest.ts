import {PerspectiveService} from 'smartedit/services';

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
describe('inner perspectiveService', () => {

	const gatewayProxy = jasmine.createSpyObj('gatewayProxy', ['initForService']);
	let perspectiveService: PerspectiveService;
	beforeEach(() => {
		perspectiveService = new PerspectiveService(gatewayProxy);
	});

	it('initializes and invokes gatewayProxy', () => {
		expect(perspectiveService.gatewayId).toBe("perspectiveService");
		expect(gatewayProxy.initForService).toHaveBeenCalledWith(perspectiveService);
	});

	it('register is left unimplemented', () => {
		expect(perspectiveService.register).toBeEmptyFunction();
	});

	it('isEmptyPerspectiveActive is left unimplemented', () => {
		expect(perspectiveService.isEmptyPerspectiveActive).toBeEmptyFunction();
	});
});
