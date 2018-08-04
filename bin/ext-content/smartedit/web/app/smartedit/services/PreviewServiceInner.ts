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
import {
	IPreviewService,
	UrlUtils
} from 'smarteditcommons';

/* @ngInject */
export class PreviewService extends IPreviewService {

	constructor(gatewayProxy: any, urlUtils: UrlUtils) {

		super(urlUtils);
		// gatewayId from IPreviewService
		gatewayProxy.initForService(this, IPreviewService.PROXY_METHODS);
	}

}

