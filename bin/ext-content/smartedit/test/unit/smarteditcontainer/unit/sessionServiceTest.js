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
describe('sessionService', function() {

    var $q,
        $rootScope,
        gatewayFactory,
        gatewayProxy,
        restServiceFactory,
        storageService,
        sessionService,
        SessionServiceInterface,
        cryptographicUtils,
        _whoAmIResource;

    var MOCKED_USERNAME = "mocked_username",
        SECOND_MOCKED_USERNAME = "second_mocked_username",
        MOCKED_USER_DATA = {
            displayName: 'MOCKED_DISPLAY_NAME',
            uid: 'MOCKED_UID'
        },
        WHO_AM_I_RESOURCE_URI = '/authorizationserver/oauth/whoami';

    beforeEach(module('gatewayFactoryModule', function($provide) {
        gatewayFactory = jasmine.createSpyObj('gatewayFactory', ['initListener']);
        $provide.value('gatewayFactory', gatewayFactory);
    }));

    beforeEach(module('gatewayProxyModule', function($provide) {
        gatewayProxy = jasmine.createSpyObj('gatewayProxy', ['initForService']);
        $provide.value('gatewayProxy', gatewayProxy);
    }));

    beforeEach(module('functionsModule', function($provide) {
        cryptographicUtils = jasmine.createSpyObj('cryptographicUtils', ['sha1Hash']);
        $provide.value('cryptographicUtils', cryptographicUtils);
    }));

    beforeEach(module('smarteditServicesModule', function($provide) {
        _whoAmIResource = jasmine.createSpyObj('_whoAmIResource', ['get']);
        restServiceFactory = jasmine.createSpyObj('restServiceFactory', ['get']);
        restServiceFactory.get.and.returnValue(_whoAmIResource);
        $provide.value('restServiceFactory', restServiceFactory);

        storageService = jasmine.createSpyObj('storageService', ['setItem', 'getItem']);
        $provide.value('storageService', storageService);
    }));

    beforeEach(module("sessionServiceModule"));

    beforeEach(inject(function(_$q_, _$rootScope_, _restServiceFactory_, _sessionService_, _SessionServiceInterface_, _storageService_, _cryptographicUtils_) {
        $q = _$q_;
        $rootScope = _$rootScope_;
        restServiceFactory = _restServiceFactory_;
        SessionServiceInterface = _SessionServiceInterface_;
        sessionService = _sessionService_;
        storageService = _storageService_;
        cryptographicUtils = _cryptographicUtils_;
    }));

    describe('initialization', function() {

        it('extends SessionServiceInterface', function() {

            expect(sessionService instanceof SessionServiceInterface).toBe(true);
        });

        it('initializes and invokes gatewayProxy', function() {
            expect(sessionService.gatewayId).toBe("SessionServiceId");
            expect(gatewayProxy.initForService).toHaveBeenCalledWith(sessionService, ['getCurrentUsername', 'getCurrentUserDisplayName', 'hasUserChanged', 'resetCurrentUserData', 'setCurrentUsername']);
        });

        it("creates a service factory based on the 'Who am I' Rest service", function() {
            expect(restServiceFactory.get).toHaveBeenCalledWith(WHO_AM_I_RESOURCE_URI);
        });

    });

    describe('getCurrentUserDisplayName()', function() {

        it('fetchs the user data through the "Who am I" service and returns user name when they are not cached yet', function() {

            // Given
            _whoAmIResource.get.and.returnValue($q.resolve(MOCKED_USER_DATA));

            // When
            sessionService.getCurrentUserDisplayName();
            $rootScope.$digest();

            // Assert
            expect(_whoAmIResource.get).toHaveBeenCalled();
            expect(restServiceFactory.get).toHaveBeenCalledWith(WHO_AM_I_RESOURCE_URI);
            expect(sessionService.getCurrentUserDisplayName()).toBeResolvedWithData(MOCKED_USER_DATA.displayName);

        });

        it('returns the cached user name without calling for the "Who am I" service', function() {

            // Given
            _whoAmIResource.get.and.returnValue($q.resolve(MOCKED_USER_DATA));

            // When
            sessionService.getCurrentUserDisplayName();
            $rootScope.$digest();
            _whoAmIResource.get.calls.reset();
            restServiceFactory.get.calls.reset();
            sessionService.getCurrentUserDisplayName();
            $rootScope.$digest();

            // Assert
            expect(_whoAmIResource.get).not.toHaveBeenCalled();
            expect(restServiceFactory.get).not.toHaveBeenCalledWith(WHO_AM_I_RESOURCE_URI);
            expect(sessionService.getCurrentUserDisplayName()).toBeResolvedWithData(MOCKED_USER_DATA.displayName);

        });

        it('fetchs the user data through the "Who am I" service and returns user name when the cache has been reset', function() {

            // Given
            _whoAmIResource.get.and.returnValue($q.resolve(MOCKED_USER_DATA));

            // When
            sessionService.getCurrentUserDisplayName();
            $rootScope.$digest();
            _whoAmIResource.get.calls.reset();
            restServiceFactory.get.calls.reset();
            sessionService.resetCurrentUserData();
            $rootScope.$digest();
            sessionService.getCurrentUserDisplayName();
            $rootScope.$digest();

            // Assert
            expect(_whoAmIResource.get).toHaveBeenCalled();
            expect(sessionService.getCurrentUserDisplayName()).toBeResolvedWithData(MOCKED_USER_DATA.displayName);

        });

    });

    describe('getCurrentUsername()', function() {

        it('fetchs the user data through the "Who am I" service and returns user uid when they are not cached yet', function() {

            // Given
            _whoAmIResource.get.and.returnValue($q.resolve(MOCKED_USER_DATA));

            // When
            sessionService.getCurrentUsername();
            $rootScope.$digest();

            // Assert
            expect(_whoAmIResource.get).toHaveBeenCalled();
            expect(restServiceFactory.get).toHaveBeenCalledWith(WHO_AM_I_RESOURCE_URI);
            expect(sessionService.getCurrentUsername()).toBeResolvedWithData(MOCKED_USER_DATA.uid);

        });

        it('returns the cached user uid without calling for the "Who am I" service', function() {

            // Given
            _whoAmIResource.get.and.returnValue($q.resolve(MOCKED_USER_DATA));

            // When
            sessionService.getCurrentUsername();
            $rootScope.$digest();
            _whoAmIResource.get.calls.reset();
            restServiceFactory.get.calls.reset();
            sessionService.getCurrentUsername();
            $rootScope.$digest();

            // Assert
            expect(_whoAmIResource.get).not.toHaveBeenCalled();
            expect(restServiceFactory.get).not.toHaveBeenCalledWith(WHO_AM_I_RESOURCE_URI);
            expect(sessionService.getCurrentUsername()).toBeResolvedWithData(MOCKED_USER_DATA.uid);

        });

        it('fetchs the user data through the "Who am I" service and returns user uid when the cache has been reset', function() {

            // Given
            _whoAmIResource.get.and.returnValue($q.resolve(MOCKED_USER_DATA));

            // When
            sessionService.getCurrentUsername();
            $rootScope.$digest();
            _whoAmIResource.get.calls.reset();
            restServiceFactory.get.calls.reset();
            sessionService.resetCurrentUserData();
            $rootScope.$digest();
            sessionService.getCurrentUsername();
            $rootScope.$digest();

            // Assert
            expect(_whoAmIResource.get).toHaveBeenCalled();
            expect(sessionService.getCurrentUsername()).toBeResolvedWithData(MOCKED_USER_DATA.uid);

        });

    });

    describe('hasUserChanged() & setCurrentUsername', function() {

        it('returns false on first user connection', function() {
            // Given
            sessionService.setCurrentUsername(null);
            spyOn(sessionService, 'getCurrentUsername').and.returnValue($q.when(MOCKED_USERNAME));
            storageService.getItem.and.returnValue($q.when(null));
            // Assert
            expect(sessionService.hasUserChanged()).toBeResolvedWithData(false);
        });

        it('returns false when the same user gets authenticated two times in a row', function() {
            // Given
            spyOn(sessionService, 'getCurrentUsername').and.returnValue($q.when(MOCKED_USERNAME));
            storageService.getItem.and.returnValue($q.when(MOCKED_USERNAME));
            cryptographicUtils.sha1Hash.and.returnValue(MOCKED_USERNAME);
            // Assert
            expect(sessionService.hasUserChanged()).toBeResolvedWithData(false);
        });

        it('returns true when a new user gets authenticated', function() {
            // Given
            spyOn(sessionService, 'getCurrentUsername').and.returnValue($q.when(SECOND_MOCKED_USERNAME));
            storageService.getItem.and.returnValue($q.when(MOCKED_USERNAME));
            cryptographicUtils.sha1Hash.and.returnValue(SECOND_MOCKED_USERNAME);
            // Assert
            expect(sessionService.hasUserChanged()).toBeResolvedWithData(true);
        });

    });

    describe('isUserLoggedIn() & resetCurrentUserData', function() {

        it('returns false when no user data have been previously cached', function() {

            // Assert
            expect(sessionService.isUserLoggedIn()).toBe(false);

        });

        it('returns true when user data have been previously cached', function() {

            // Given
            _whoAmIResource.get.and.returnValue($q.resolve(MOCKED_USER_DATA));

            // When
            sessionService.getCurrentUsername();
            $rootScope.$digest();

            // Assert
            expect(sessionService.isUserLoggedIn()).toBe(true);

        });

        it('returns false when the cached user data have been reset', function() {

            // Given
            _whoAmIResource.get.and.returnValue($q.resolve(MOCKED_USER_DATA));

            // When
            sessionService.getCurrentUsername();
            $rootScope.$digest();
            sessionService.resetCurrentUserData();
            $rootScope.$digest();

            // Assert
            expect(sessionService.isUserLoggedIn()).toBe(false);

        });

    });

});
