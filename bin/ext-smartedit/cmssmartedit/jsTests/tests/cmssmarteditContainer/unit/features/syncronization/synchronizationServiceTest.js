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
describe("sync service  - unit test", function() {
    var $httpProvider, synchronizationService, $q, $rootScope;
    var mockTimer, authenticationService, timerService;

    var theCatalog = {
        catalogId: "catalog",
        sourceVersion: 'sourceVersion',
        targetVersion: 'targetVersion'
    };
    var theCatalogGetStatus = {
        "date": "2016-02-12T16:08:29+0000",
        "status": "FINISHED"
    };
    var theCatalogUpdateStatus = {
        "date": "2016-02-12T17:09:29+0000",
        "status": "FINISHED"
    };

    var secondCatalog = {
        catalogId: 'second catalog'
    };
    var secondCatalogGetStatus = {
        "date": "2016-04-01T12:00:00+0000",
        "status": "PENDING"
    };

    beforeEach(module('configModule'));
    beforeEach(module('pascalprecht.translate'));

    beforeEach(function() {
        angular.module('resourceLocationsModule', []);
        angular.module('alertServiceModule', []);
        angular.module('confirmationModalServiceModule', []);
        angular.module('authenticationModule', []);
        angular.module('operationContextServiceModule', []);
    });

    beforeEach(module("synchronizationServiceModule", function($provide, _$httpProvider_) {
        $httpProvider = _$httpProvider_;

        $provide.value('operationContextService', {
            register: angular.noop
        });
        $provide.value('OPERATION_CONTEXT', {
            CMS: 'CMS'
        });
    }));

    beforeEach(module('authenticationModule', function($provide) {
        authenticationService = jasmine.createSpyObj('authenticationService', ['isAuthenticated']);
        authenticationService.isAuthenticated.and.callFake(function(url) {
            var test = "/cmswebservices";
            if (url === test) {
                return $q.when(true);
            } else {
                return $q.when(false);
            }
        });
        $provide.value('authenticationService', authenticationService);
    }));

    beforeEach(module('smarteditServicesModule', function($provide) {
        var restServiceFactory = jasmine.createSpyObj('restServiceFactory', ['get']);
        var restServiceForSync = jasmine.createSpyObj('searchRestService', ['get', 'update']);

        restServiceFactory.get.and.callFake(function() {
            return restServiceForSync;
        });

        restServiceForSync.update.and.callFake(function(value) {
            if (value.catalog === "catalog") {
                return $q.when(theCatalogUpdateStatus);
            }
        });

        restServiceForSync.get.and.callFake(function(value) {
            if (value.catalog === "catalog") {
                return $q.when(theCatalogGetStatus);
            } else if (value.catalog === "second catalog") {
                return $q.when(secondCatalogGetStatus);
            }
        });

        $provide.value('restServiceFactory', restServiceFactory);
    }));

    beforeEach(module('alertServiceModule', function($provide) {
        var alertService = jasmine.createSpyObj('alertService', ['pushAlerts']);
        $provide.value('alertService', alertService);
    }));

    beforeEach(module('timerModule', function($provide) {
        mockTimer = jasmine.createSpyObj('Timer', ['start', 'restart', 'stop']);
        timerService = jasmine.createSpyObj('timerService', ['createTimer']);
        timerService.createTimer.and.returnValue(mockTimer);

        $provide.value('timerService', timerService);
    }));

    beforeEach(inject(function(_$rootScope_, _synchronizationService_, _$q_) {
        $rootScope = _$rootScope_;
        $q = _$q_;
        synchronizationService = _synchronizationService_;
    }));


    it('should update sync status ', function() {

        var result = synchronizationService.updateCatalogSync(theCatalog);

        $rootScope.$digest();

        result.then(
            function(response) {
                expect(response.date).toEqual("2016-02-12T17:09:29+0000");
                expect(response.status).toEqual("FINISHED");
            }
        );
        $rootScope.$digest();
    });


    it('should get catalog sync status', function() {

        var result = synchronizationService.getCatalogSyncStatus(theCatalog);

        $rootScope.$digest();

        result.then(
            function(response) {
                expect(response.date).toEqual("2016-02-12T16:08:29+0000");
                expect(response.status).toEqual("FINISHED");
            }
        );
        $rootScope.$digest();
    });


    it('should call "get synchronization status" after interval.', function() {

        var callback = jasmine.createSpy('callback');

        synchronizationService.startAutoGetSyncData(theCatalog, callback);
        expect(timerService.createTimer).toHaveBeenCalledWith(jasmine.any(Function), jasmine.any(Number));

        var timerFn = timerService.createTimer.calls.argsFor(0)[0];
        timerFn();
        $rootScope.$digest();
        expect(callback.calls.count()).toBe(1);

        timerFn();
        $rootScope.$digest();
        expect(callback.calls.count()).toBe(2);
    });

    it('stopAutoGetSyncData should stop the timer', function() {
        // GIVEN
        var callback = jasmine.createSpy('callback');
        synchronizationService.startAutoGetSyncData(theCatalog, callback);
        expect(mockTimer.stop).not.toHaveBeenCalled();

        // WHEN
        synchronizationService.stopAutoGetSyncData(theCatalog);

        // THEN
        expect(mockTimer.stop).toHaveBeenCalled();
    });

    it('should stop calling "get sync update" on authentication failure', function() {
        // GIVEN
        var callback = jasmine.createSpy('callback');

        authenticationService.isAuthenticated.and.callFake(function() {
            return $q.when(false);
        });
        spyOn(synchronizationService, 'stopAutoGetSyncData').and.callThrough();

        synchronizationService.startAutoGetSyncData(secondCatalog, callback);
        var timerFn = timerService.createTimer.calls.argsFor(0)[0];
        timerFn();
        $rootScope.$digest();

        expect(synchronizationService.stopAutoGetSyncData).toHaveBeenCalled();
    });

    it('should continue calling "get sync update" on authentication success', function() {
        var callback = jasmine.createSpy('callback').and.returnValue($q.reject());

        spyOn(synchronizationService, 'stopAutoGetSyncData').and.callThrough();
        spyOn(synchronizationService, 'getCatalogSyncStatus').and.callThrough();

        synchronizationService.startAutoGetSyncData(theCatalog, callback);
        var timerFn = timerService.createTimer.calls.argsFor(0)[0];
        timerFn();
        $rootScope.$digest();

        expect(synchronizationService.stopAutoGetSyncData).not.toHaveBeenCalled();
        expect(synchronizationService.getCatalogSyncStatus).toHaveBeenCalled();
    });
});
