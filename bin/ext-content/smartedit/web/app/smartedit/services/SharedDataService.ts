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
import {Cloneable, ISharedDataService} from 'smarteditcommons';


/** @internal */
/* tslint:disable:no-empty */
/* @ngInject */
export class SharedDataService implements ISharedDataService {

	// Constants
	readonly GATEWAY_ID = 'sharedData';

	// Variables
	gatewayId: string;

	constructor(gatewayProxy: any) {
		this.gatewayId = this.GATEWAY_ID;
		gatewayProxy.initForService(this);
	}

	get(key: string) {}
	set(key: string, value: Cloneable) {}
	update(key: string, modifyingCallback: (oldValue: any) => any) {}
}

