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
describe('controller resolve', function() {
    var EVENTS,
        LANDING_PAGE_PATH = '/',
        experienceService,
        $route,
        sharedDataService,
        catalogVersionPermissionService,
        $location, $q, $rootScope, $log,
        catalogAwareRouteResolverFunctions,
        systemEventService,
        setExperience;

    var nextExperience = {
        catalogDescriptor: {
            catalogId: 'catalog_id_1',
            catalogVersion: 'catalog_version_1'
        }
    };

    var prevExperience = {
        catalogDescriptor: {
            catalogId: 'catalog_id_2',
            catalogVersion: 'catalog_version_2'
        }
    };

    beforeEach(module('catalogAwareRouteResolverModule', function($provide) {
        EVENTS = {
            EXPERIENCE_UPDATE: 'experience_update_fake_event'
        };
        $provide.constant('EVENTS', EVENTS);
        $provide.constant('LANDING_PAGE_PATH', LANDING_PAGE_PATH);

        experienceService = jasmine.createSpyObj('experienceService', ['buildDefaultExperience']);

        $provide.value('experienceService', experienceService);

        $route = {
            current: {
                params: 'fake_current_params'
            }
        };
        $provide.value('$route', $route);

        sharedDataService = jasmine.createSpyObj('sharedDataService', ['get', 'set']);
        $provide.value('sharedDataService', sharedDataService);

        catalogVersionPermissionService = jasmine.createSpyObj('catalogVersionPermissionService', ['hasReadPermissionOnCurrent']);
        $provide.value('catalogVersionPermissionService', catalogVersionPermissionService);

        $location = jasmine.createSpyObj('$location', ['url']);
        $provide.value('$location', $location);

        systemEventService = jasmine.createSpyObj('systemEventService', ['sendAsynchEvent']);
        $provide.value('systemEventService', systemEventService);
    }));

    beforeEach(inject(function(_$rootScope_, _$q_, _$log_, _catalogAwareRouteResolverFunctions_) {
        $log = _$log_;
        $q = _$q_;
        $rootScope = _$rootScope_;
        catalogAwareRouteResolverFunctions = _catalogAwareRouteResolverFunctions_;

        var setExperienceData = catalogAwareRouteResolverFunctions.setExperience;
        var setExperienceFunction = setExperienceData[setExperienceData.length - 1];
        setExperience = function() {
            setExperienceFunction($log, $q, $route, $location, experienceService, sharedDataService, systemEventService, EVENTS, LANDING_PAGE_PATH, catalogVersionPermissionService);
        };
    }));

    describe('prepare experiences', function() {
        beforeEach(function() {
            experienceService.buildDefaultExperience.and.returnValue($q.when(nextExperience));
            sharedDataService.get.and.returnValue($q.when(prevExperience));
            sharedDataService.set.and.returnValue($q.when());
        });

        it('GIVEN any type of access (read/write) to catalog version WHEN setExperience is called THEN experienceService generates next experience AND previous experience is extracted  AND next experience is saved to sharedDataService', function() {
            // GIVEN
            catalogVersionPermissionService.hasReadPermissionOnCurrent.and.returnValue($q.when(true));

            // WHEN
            setExperience();
            $rootScope.$digest();

            //THEN
            expect(experienceService.buildDefaultExperience).toHaveBeenCalledWith($route.current.params);
            expect(sharedDataService.get).toHaveBeenCalledWith('experience');
            expect(sharedDataService.set).toHaveBeenCalledWith('experience', nextExperience);
        });
    });

    describe('prev and next experiences are not the same', function() {
        beforeEach(function() {
            experienceService.buildDefaultExperience.and.returnValue($q.when(nextExperience));
            sharedDataService.get.and.returnValue($q.when(prevExperience));
            sharedDataService.set.and.returnValue($q.when());
        });

        it('GIVEN read access to catalog version exists WHEN setExperience is called THEN experience update event is sent', function() {
            // GIVEN
            catalogVersionPermissionService.hasReadPermissionOnCurrent.and.returnValue($q.when(true));

            // WHEN
            setExperience();
            $rootScope.$digest();

            // THEN
            expect($location.url).not.toHaveBeenCalled();
            expect(systemEventService.sendAsynchEvent).toHaveBeenCalledWith(EVENTS.EXPERIENCE_UPDATE);
        });

        it('GIVEN read access to catalog version does not exist WHEN setExperience is called THEN redirect to landing page', function() {
            // GIVEN
            catalogVersionPermissionService.hasReadPermissionOnCurrent.and.returnValue($q.when(false));

            // WHEN
            setExperience();
            $rootScope.$digest();

            // THEN
            expect($location.url).toHaveBeenCalledWith(LANDING_PAGE_PATH);
        });
    });

    describe('prev and next experiences are the same', function() {
        beforeEach(function() {
            experienceService.buildDefaultExperience.and.returnValue($q.when(nextExperience));
            prevExperience = nextExperience;
            sharedDataService.get.and.returnValue($q.when(prevExperience));
            sharedDataService.set.and.returnValue($q.when());
        });

        it('GIVEN read access to catalog version exists WHEN setExperience is called THEN experience update event is not sent', function() {
            // GIVEN
            catalogVersionPermissionService.hasReadPermissionOnCurrent.and.returnValue($q.when(true));

            // WHEN
            setExperience();
            $rootScope.$digest();

            // THEN
            expect($location.url).not.toHaveBeenCalled();
            expect(systemEventService.sendAsynchEvent).not.toHaveBeenCalled();
        });

        it('GIVEN read access to catalog version does not exist WHEN setExperience is called THEN redirect to landing page', function() {
            // GIVEN
            catalogVersionPermissionService.hasReadPermissionOnCurrent.and.returnValue($q.when(false));

            // WHEN
            setExperience();
            $rootScope.$digest();

            // THEN
            expect($location.url).toHaveBeenCalledWith(LANDING_PAGE_PATH);
        });
    });
});
