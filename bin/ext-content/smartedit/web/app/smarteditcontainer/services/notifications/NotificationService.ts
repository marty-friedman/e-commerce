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

import * as angular from 'angular';
import * as lo from 'lodash';
import {IConfiguration, INotificationService} from 'smarteditcommons';

/**
 * @ngdoc service
 * @name smarteditServicesModule.service:NotificationService
 * 
 * @description
 * The notification service is used to display visual cues to inform the user of the state of the application.
 */
/* @ngInject */
export class NotificationService implements INotificationService {

	private notifications: IConfiguration[];

	constructor(
		gatewayProxy: any,
		private lodash: lo.LoDashStatic,
		private systemEventService: any,
		private EVENT_NOTIFICATION_CHANGED: string,
		private $q: angular.IQService) {
		this.notifications = [];
		gatewayProxy.initForService(this, ['pushNotification', 'removeNotification', 'removeAllNotifications'], 'SE_NOTIFICATION_SERVICE_GATEWAY_ID');
	}

	pushNotification(configuration: IConfiguration): angular.IPromise<void> {
		this._validate(configuration);

		if (this.getNotification(configuration.id)) {
			throw new Error('notificationService.pushNotification: Notification already exists with ID "' + configuration.id + '"');
		}

		this.notifications.push(this.lodash.clone(configuration));

		this.systemEventService.sendAsynchEvent(this.EVENT_NOTIFICATION_CHANGED);

		return this.$q.when();
	}
	removeNotification(notificationId: string): angular.IPromise<void> {
		this.lodash.remove(this.notifications, (notification: IConfiguration) => notification.id === notificationId);
		this.systemEventService.sendAsynchEvent(this.EVENT_NOTIFICATION_CHANGED);
		return this.$q.when();
	}
	removeAllNotifications(): angular.IPromise<void> {
		this.notifications = [];
		this.systemEventService.sendAsynchEvent(this.EVENT_NOTIFICATION_CHANGED);
		return this.$q.when();
	}

	isNotificationDisplayed(notificationId: string): boolean {
		return !!this.getNotification(notificationId);
	}

	getNotification(notificationId: string): IConfiguration {
		return this.lodash.find(this.notifications, ['id', notificationId]);
	}

	getNotifications(): IConfiguration[] {
		const clonedNotifications = this.lodash.clone(this.notifications);
		return this.lodash.reverse(clonedNotifications);
	}

	private _validate(configuration: IConfiguration) {
		if (!configuration) {
			throw new Error('notificationService.pushNotification: Configuration is required');
		}

		if (this.lodash.isEmpty(configuration.id)) {
			throw new Error('notificationService.pushNotification: Notification ID cannot be undefined or null or empty');
		}

		if (!configuration.hasOwnProperty('template') && !configuration.hasOwnProperty('templateUrl')) {
			throw new Error('notificationService.pushNotification: Configuration must contain a template or template URL');
		}

		if (configuration.hasOwnProperty('template') && configuration.hasOwnProperty('templateUrl')) {
			throw new Error('notificationService.pushNotification: Configuration cannot contain both a template and template URL; use one or the other');
		}
	}
}