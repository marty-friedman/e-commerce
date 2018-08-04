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
import * as lo from 'lodash';
import {IRestService, ISite} from 'smarteditcommons';
import {RestServiceFactory, SiteService} from 'smarteditcontainer/services';
import {promiseHelper, IExtensiblePromise, PromiseType} from 'testhelpers';


describe('siteService', () => {

	const lodash: lo.LoDashStatic = (window as any)._ || (window as any).smarteditLodash;
	const $q = promiseHelper.$q();

	const EVENTS = {
		AUTHORIZATION_SUCCESS: 'AUTHORIZATION_SUCCESS',
		LOGOUT: 'SE_LOGOUT_EVENT'
	};

	let siteService: SiteService;
	const restServiceFactory: jasmine.SpyObj<RestServiceFactory> = jasmine.createSpyObj<RestServiceFactory>('restServiceFactory', ['get']);
	const siteRestService: jasmine.SpyObj<IRestService<any>> = jasmine.createSpyObj<IRestService<any>>('siteRestService', ['get']);

	const systemEventService = jasmine.createSpyObj('systemEventService', ['registerEventHandler']);
	const operationContextService = jasmine.createSpyObj('operationContextService', ['register']);

	const OPERATION_CONTEXT = {};
	const SITES_RESOURCE_URI: string = 'some uri';

	const sitesDTO = {
		sites: [{
			contentCatalogs: ['electronicsContentCatalog', 'electronics-euContentCatalog', 'electronics-ukContentCatalog'],
			name: {
				en: 'Electronics Site'
			},
			previewUrl: '/yacceleratorstorefront?site=electronics-uk',
			uid: 'electronics-uk'
		}]
	};

	const sitesDTOByCatalogs = {
		sites: [{
			contentCatalogs: ['electronicsContentCatalog'],
			name: {
				en: 'Electronics Site'
			},
			previewUrl: '/yacceleratorstorefront?site=electronics',
			uid: 'electronics'
		}, {
			contentCatalogs: ['electronicsContentCatalog', 'electronics-euContentCatalog', 'electronics-ukContentCatalog'],
			name: {
				en: 'Electronics Site'
			},
			previewUrl: '/yacceleratorstorefront?site=electronics-uk',
			uid: 'electronics-uk'
		}, {
			contentCatalogs: ['electronicsContentCatalog', 'electronics-euContentCatalog'],
			name: {
				en: 'Electronics Site'
			},
			previewUrl: '/yacceleratorstorefront?site=electronics-eu',
			uid: 'electronics-eu'
		}]
	};

	const sitesDTOPromise = promiseHelper.buildPromise<any>('sitesDTOPromise', PromiseType.RESOLVES, sitesDTO) as IExtensiblePromise<ISite[]>;
	const sitesDTOByCatalogsPromise = promiseHelper.buildPromise<any>('sitesDTOByCatalogsPromise', PromiseType.RESOLVES, sitesDTOByCatalogs) as IExtensiblePromise<ISite[]>;

	beforeEach(() => {

		siteRestService.get.and.callFake((arg: any) => {
			if (lodash.isEmpty(arg)) {
				return sitesDTOPromise;
			} else if (arg && arg.catalogIds && lodash.isEqual(arg.catalogIds, ['electronicsContentCatalog', 'electronics-euContentCatalog', 'electronics-ukContentCatalog'].join(','))) {
				return sitesDTOByCatalogsPromise;
			}
			throw new Error("unexpected argument for siteRestService.get method: " + arg);
		});

		restServiceFactory.get.and.returnValue(siteRestService);

		systemEventService.registerEventHandler.and.returnValue(null);
		operationContextService.register.and.returnValue(null);

		siteService = new SiteService(restServiceFactory, systemEventService, operationContextService, OPERATION_CONTEXT, SITES_RESOURCE_URI, EVENTS, $q);
	});

	it('is initialized', () => {
		expect(restServiceFactory.get).toHaveBeenCalledWith(SITES_RESOURCE_URI);

		expect(operationContextService.register).toHaveBeenCalled();
		expect(operationContextService.register.calls.count()).toEqual(1);

		expect(systemEventService.registerEventHandler.calls.count()).toEqual(1);
		expect(systemEventService.registerEventHandler).toHaveBeenCalledWith(EVENTS.AUTHORIZATION_SUCCESS, jasmine.any(Function));
	});

	it('is calling getSites method', () => {
		const promise = siteService.getSites() as IExtensiblePromise<ISite[]>;
		expect(promise.value).toEqual(sitesDTOByCatalogs.sites);
		expect(siteRestService.get).toHaveBeenCalledWith({});
		expect(siteRestService.get).toHaveBeenCalledWith({catalogIds: ['electronicsContentCatalog', 'electronics-euContentCatalog', 'electronics-ukContentCatalog'].join(',')});
		expect(siteRestService.get.calls.count()).toEqual(2);
	});

	it('is calling getSiteById method', () => {
		const uid = 'electronics';
		siteRestService.get.calls.reset();
		const promise = siteService.getSiteById(uid) as IExtensiblePromise<ISite>;
		expect(promise.value).toEqual(sitesDTOByCatalogs.sites.find((site) => site.uid === uid));
		expect(siteRestService.get).toHaveBeenCalled();
		expect(siteRestService.get.calls.count()).toEqual(2);
	});
});
