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
import {Page, Pageable, Payload} from 'smarteditcommons';
import {RestServiceFactory} from 'smarteditcontainer/services';
/*
 * internal service to proxy calls from inner RESTService to the outer restServiceFactory and the 'real' IRestService
 */
/** @internal */
/* @ngInject */
export class DelegateRestService {

	public gatewayId: string = "DelegateRestService";

	constructor(private restServiceFactory: RestServiceFactory, private gatewayProxy: any) {
		this.gatewayProxy.initForService(this);
	}

	delegateForVoid(methodName: string, params: string | Payload, uri: string, identifier?: string): angular.IPromise<void> {
		return this.restServiceFactory.get<any>(uri, identifier).getMethodForVoid(methodName)(params);
	}

	delegateForSingleInstance<T>(methodName: string, params: string | Payload, uri: string, identifier?: string): angular.IPromise<T> {
		return this.restServiceFactory.get<T>(uri, identifier).getMethodForSingleInstance(methodName)(params);
	}

	delegateForArray<T>(methodName: string, params: string | Payload, uri: string, identifier?: string): angular.IPromise<T[]> {
		return this.restServiceFactory.get<T>(uri, identifier).getMethodForArray(methodName)(params);
	}

	delegateForPage<T>(pageable: Pageable, uri: string, identifier?: string): angular.IPromise<Page<T>> {
		return this.restServiceFactory.get<T>(uri, identifier).page(pageable);
	}


}
