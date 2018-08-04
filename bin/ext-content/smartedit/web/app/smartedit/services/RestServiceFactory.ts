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
import {IRestService, IRestServiceFactory} from 'smarteditcommons';
import {DelegateRestService, RestService} from 'smartedit/services';

/** @internal */
/* @ngInject */
export class RestServiceFactory implements IRestServiceFactory {

	constructor(private delegateRestService: DelegateRestService) {
	}

	get<T>(uri: string, identifier?: string): IRestService<T> {

		return new RestService<T>(this.delegateRestService, uri, identifier);
	}

}
