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
import {IWaitDialogService} from 'smarteditcommons';

/** @internal */
/* @ngInject */
export class WaitDialogService extends IWaitDialogService {

	/** @internal */
	public gatewayId: string = "WaitDialogService";

	constructor(private gatewayProxy: any) {
		super();
		this.gatewayProxy.initForService(this);
	}
}
