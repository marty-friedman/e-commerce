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
import * as lodash from 'lodash';
import {IPreviewData, IPreviewService, IRestServiceFactory} from "smarteditcommons";
import {PreviewService} from 'smarteditcontainer/services';
import {promiseHelper, PromiseType} from 'testhelpers';

describe('previewService', () => {

	const mockSaveResult = {
		ticketId: "xyz",
		resourcePath: "abc"
	};
	const mockUpdatedUrl = 'someUpdatedUrl';
	const mockResourcePath = 'mockResourcePath';
	const mockPreviewUri = 'bla';

	// ======= Injected mocks =======
	let $log: jasmine.SpyObj<angular.ILogService>;
	let loadConfigManagerService: jasmine.SpyObj<any>;
	let restServiceFactory: jasmine.SpyObj<IRestServiceFactory>;
	let gatewayProxy: jasmine.SpyObj<any>;
	let mockUrlUtils: jasmine.SpyObj<any>;
	const getAbsoluteURL: () => string = () => mockResourcePath;
	const PREVIEW_RESOURCE_URI: string = mockPreviewUri;
	const $q: angular.IQService = promiseHelper.$q();
	const lo: lodash.LoDashStatic = (window as any)._ || (window as any).smarteditLodash;

	// ======= Common mocks =======
	let rsf: jasmine.SpyObj<any>;

	// Service being tested
	let previewService: IPreviewService;

	// === SETUP ===
	beforeEach(() => {
		$log = jasmine.createSpyObj('$log', ['debug', 'error']);
		loadConfigManagerService = jasmine.createSpyObj('loadConfigManagerService', ['loadAsObject']);
		restServiceFactory = jasmine.createSpyObj('restServiceFactory', ['get']);
		gatewayProxy = jasmine.createSpyObj('gatewayProxy', ['initForService']);

		rsf = jasmine.createSpyObj('rsf', ['save']);
		restServiceFactory.get.and.returnValue(rsf);

		mockUrlUtils = jasmine.createSpyObj('mockUrlUtils', ['updateUrlParameter']);
		mockUrlUtils.updateUrlParameter.and.callFake(() => mockUpdatedUrl);

		previewService = new PreviewService(
			$log,
			$q,
			loadConfigManagerService,
			PREVIEW_RESOURCE_URI,
			restServiceFactory,
			lo,
			getAbsoluteURL,
			gatewayProxy,
			mockUrlUtils
		);
	});

	describe('createPreview & updateUrlWithNewPreviewTicketId', () => {

		const experience: IPreviewData = {
			catalog: "catalog",
			catalogVersion: "catalogVersion",
			language: "language",
			resourcePath: "resourcePath",
			pageId: "pageId"
		};

		const configuration = {
			domain: "https://somedomain:999/",
			previewTicketURI: "somePreviewURI"
		};

		describe('Handling rejected promises', () => {

			it('Cannot load configuration', () => {
				const failurePromise: angular.IPromise<any> = promiseHelper.buildPromise('alpha', PromiseType.REJECTS);
				loadConfigManagerService.loadAsObject.and.callFake(() => failurePromise);
				expect(() => {
					previewService.createPreview(experience);
				}).toThrow();
			});

			it('Post to preview failed', () => {
				const resetServicePromise: angular.IPromise<any> = promiseHelper.buildPromise('restService', PromiseType.REJECTS);
				rsf.save.and.returnValue(resetServicePromise);

				expect(() => {
					previewService.createPreview(experience);
				}).toThrow();
			});

		});

		describe('Other', () => {

			let configurationPromise: angular.IPromise<any>;

			beforeEach(() => {
				configurationPromise = promiseHelper.buildPromise('config', PromiseType.RESOLVES, configuration);
				loadConfigManagerService.loadAsObject.and.callFake(() => configurationPromise);

				const savePromise: angular.IPromise<any> = promiseHelper.buildPromise('save', PromiseType.RESOLVES, mockSaveResult);
				rsf.save.and.returnValue(savePromise);

			});

			describe('validation', () => {
				it('validatePreviewDataAttributes fails when require attributes are missing', () => {
					const invalidPreviewData = lo.cloneDeep(experience);
					delete invalidPreviewData.catalog;
					expect(() => previewService.createPreview(invalidPreviewData)).toThrow();
					expect(rsf.save.calls.count()).toBe(0);
				});
			});

			describe('getRestService()', () => {

				it('Will use configuration defined previewAPI URL and create cached instance on first call', () => {
					expect(() => previewService.createPreview(experience)).not.toThrow();
					expect(restServiceFactory.get).toHaveBeenCalledWith(configuration.previewTicketURI);
					expect(restServiceFactory.get.calls.count()).toBe(1);
				});

				it('Will use default previewAPI URL and use cached service instance', () => {
					configurationPromise = promiseHelper.buildPromise('config', PromiseType.RESOLVES, {});
					loadConfigManagerService.loadAsObject.and.callFake(() => configurationPromise);

					expect(() => previewService.createPreview(experience)).not.toThrow();
					expect(restServiceFactory.get.calls.count()).toBe(1);
					expect(() => previewService.createPreview(experience)).not.toThrow();
					expect(restServiceFactory.get.calls.count()).toBe(1);
					expect(restServiceFactory.get).toHaveBeenCalledWith(mockPreviewUri);
				});
			});

			describe('createPreview', () => {
				it('Proper preview data is requested and proper response returned', () => {
					(expect(previewService.createPreview(experience)) as any).toBeResolvedWithData({
						previewTicketId: mockSaveResult.ticketId,
						resourcePath: mockSaveResult.resourcePath
					});
				});
			});

			describe('updateUrlWithNewPreviewTicketId', () => {
				it('Properly updates url', () => {
					// basically just making sure that updateUrl was called
					(expect(previewService.updateUrlWithNewPreviewTicketId("/storefront", experience)) as any).toBeResolvedWithData(mockUpdatedUrl);
				});
			});

		});

	});
});

