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
import {IRestService, Page, Pageable, Payload} from 'smarteditcommons';
import {DelegateRestService} from 'smartedit/services';

/** @internal */
/* @ngInject */
export class RestService<T> implements IRestService<T> {

	constructor(private delegateRestService: DelegateRestService, private uri: string, private identifier: string) {
	}

	getById(id: string): angular.IPromise<T> {
		return this.delegateRestService.delegateForSingleInstance("getById", id, this.uri, this.identifier);
	}
	get(searchParams: Payload): angular.IPromise<T> {
		return this.delegateRestService.delegateForSingleInstance("get", searchParams, this.uri, this.identifier);
	}
	update(payload: Payload): angular.IPromise<T> {
		return this.delegateRestService.delegateForSingleInstance("update", payload, this.uri, this.identifier);
	}
	save(payload: Payload): angular.IPromise<T> {
		return this.delegateRestService.delegateForSingleInstance("save", payload, this.uri, this.identifier);
	}
	query(searchParams: Payload): angular.IPromise<T[]> {
		return this.delegateRestService.delegateForArray("query", searchParams, this.uri, this.identifier);
	}
	page(pageable: Pageable): angular.IPromise<Page<T>> {
		return this.delegateRestService.delegateForPage<T>(pageable, this.uri, this.identifier);
	}
	remove(payload: Payload): angular.IPromise<void> {
		return this.delegateRestService.delegateForVoid("remove", payload, this.uri, this.identifier);
	}

}
