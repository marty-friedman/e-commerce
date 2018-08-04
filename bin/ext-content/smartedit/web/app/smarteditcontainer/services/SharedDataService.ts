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
import {Cloneable, ISharedDataService} from 'smarteditcommons';

/** @internal */
/* @ngInject */
export class SharedDataService implements ISharedDataService {

	public gatewayId: string = 'sharedData';

	// Variables
	private $q: angular.IQService;
	private storage: {[id: string]: any} = {};

	constructor($q: angular.IQService, gatewayProxy: any) {
		this.$q = $q;
		gatewayProxy.initForService(this);
	}
	get(name: string) {
		return this.storage[name];
	}
	set(name: string, value: Cloneable): void {
		this.storage[name] = value;
	}
	update(name: string, modifyingCallback: (oldValue: any) => any): angular.IPromise<void> {
		return this.get(name).then((oldValue: any) => {
			return this.$q.when(modifyingCallback(oldValue)).then((newValue: any) => {
				return this.set(name, newValue);
			});
		});
	}
}

