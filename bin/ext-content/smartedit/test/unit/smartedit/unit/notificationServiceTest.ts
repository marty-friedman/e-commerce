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
import {NotificationService} from 'smartedit/services';

describe('notificationService', () => {

	let notificationService: NotificationService;
	const gatewayProxy: any = jasmine.createSpyObj<any>('gatewayProxy', ['initForService']);
    /*
     * This setup method creates a mock for the gateway proxy. It is used to validate
     * that the notification service initializes itself properly to be proxied
     * across the two applications.
	 */

	beforeEach(() => {
		notificationService = new NotificationService(gatewayProxy);
	});

	describe('initialization', () => {
		it('extends the INotificationService', () => {
			expect(notificationService.pushNotification).toBeEmptyFunction();
			expect(notificationService.removeNotification).toBeEmptyFunction();
			expect(notificationService.removeAllNotifications).toBeEmptyFunction();
		});

		it('invokes the gatway proxy with the proper parameter values', () => {
			expect(gatewayProxy.initForService).toHaveBeenCalledWith(
				notificationService, ['pushNotification', 'removeNotification', 'removeAllNotifications'],
				'SE_NOTIFICATION_SERVICE_GATEWAY_ID');
		});
	});
});
