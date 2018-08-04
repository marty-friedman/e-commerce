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
describe('authorizationService', function() {
    var DUMMY_USERNAME = 'dummy_username';

    var READ_PERMISSION_NAME = 'smartedit.configurationcenter.read';
    var WRITE_PERMISSION_NAME = 'smartedit.configurationcenter.write';
    var DELETE_PERMISSION_NAME = 'smartedit.configurationcenter.delete';
    var UNKNOWN_PERMISSION_NAME = 'smartedit.configurationcenter.unknown';

    var $rootScope, $q, authorizationService, permissionsResource,
        restServiceFactory, sessionService,
        USER_GLOBAL_PERMISSIONS_RESOURCE_URI;

    /*
     * This setup method provides a dummy value for the Global Permissions REST API endpoint
     * used when creating the mock REST service used to test the AuthorizationService.
     */
    beforeEach(module('resourceLocationsModule', function($provide) {
        USER_GLOBAL_PERMISSIONS_RESOURCE_URI = 'USER_GLOBAL_PERMISSIONS_RESOURCE_URI';
        $provide.constant('USER_GLOBAL_PERMISSIONS_RESOURCE_URI', USER_GLOBAL_PERMISSIONS_RESOURCE_URI);
    }));

    /*
     * This setup method create a mock REST service (resource) and a mock REST service
     * factory. The mocked factory is used to return the mocked service, so it can be used
     * by the AthorizationService in the tests. This makes it possible to simulate calls to
     * the backend by providing mock data as a response.
     * 
     * A single mocked REST service is created for the Global Permissions API.
     */
    beforeEach(module('smarteditServicesModule', function($provide) {
        permissionsResource = jasmine.createSpyObj('permissionsResource', ['get']);

        restServiceFactory = jasmine.createSpyObj('restServiceFactory', ['get']);
        restServiceFactory.get.and.returnValue(permissionsResource);
        $provide.value('restServiceFactory', restServiceFactory);
    }));

    /*
     * This setup method creates a mock session service, so it can be used by the
     * AuthorizationService to obtain mock username values.
     */
    beforeEach(module('sessionServiceModule', function($provide) {
        sessionService = jasmine.createSpyObj('sessionService', ['getCurrentUsername']);
        $provide.value('sessionService', sessionService);
    }));

    beforeEach(module('authorizationModule'));

    beforeEach(inject(function(_$rootScope_, _$q_, _authorizationService_) {
        $rootScope = _$rootScope_;
        $q = _$q_;
        authorizationService = _authorizationService_;
    }));

    describe('initialization', function() {
        it('gets a REST service for the ' + USER_GLOBAL_PERMISSIONS_RESOURCE_URI + ' URI', function() {
            expect(restServiceFactory.get).toHaveBeenCalledWith(USER_GLOBAL_PERMISSIONS_RESOURCE_URI);
        });
    });

    describe('canPerformOperation', function() {
        it('throws an error when the permissionNames string is empty', function() {
            expect(function() {
                authorizationService.canPerformOperation('');
            }).toThrow('permissionNames cannot be empty');
        });

        it('calls the new hasGlobalPermissions method with an array of the values from its string parameter', function() {
            // Given
            var permissionNames = READ_PERMISSION_NAME + ',' + WRITE_PERMISSION_NAME;

            spyOn(authorizationService, 'hasGlobalPermissions');

            // When
            authorizationService.canPerformOperation(permissionNames);

            // Then
            expect(authorizationService.hasGlobalPermissions).toHaveBeenCalledWith(permissionNames.split(','));
        });
    });

    describe('hasGlobalPermissions', function() {
        it('throws an error when the permissionNames parameter is not an array', function() {
            expect(function() {
                authorizationService.hasGlobalPermissions('not.an.array');
            }).toThrow('permissionNames must be an array');
        });

        it('throws an error when the permissionNames array is empty', function() {
            expect(function() {
                authorizationService.hasGlobalPermissions([]);
            }).toThrow('permissionNames cannot be empty');
        });

        it('does not query the Global Permissions REST API when it is unable to get the principal identifier', function() {
            // Given
            sessionService.getCurrentUsername.and.returnValue($q.when(null));

            // When
            authorizationService.hasGlobalPermissions([READ_PERMISSION_NAME]);

            // Then
            expect(permissionsResource.get).not.toHaveBeenCalled();
        });

        it('returns false when the query to the Global Permission REST API fails', function() {
            // Given
            sessionService.getCurrentUsername.and.returnValue($q.when(DUMMY_USERNAME));

            permissionsResource.get.and.returnValue($q.reject('unable.to.get.permissions'));

            // When
            var result = authorizationService.hasGlobalPermissions([READ_PERMISSION_NAME]);

            // Then
            expect(result).toBeResolvedWithData(false);
        });

        it('queries the Global Permissions REST API with the principal identifier and the permission names as a CSV string', function() {
            // Given
            var permissionNames = [READ_PERMISSION_NAME, WRITE_PERMISSION_NAME];

            sessionService.getCurrentUsername.and.returnValue($q.when(DUMMY_USERNAME));

            // When
            authorizationService.hasGlobalPermissions(permissionNames);

            $rootScope.$apply();

            // Then
            expect(permissionsResource.get).toHaveBeenCalledWith(jasmine.objectContaining({
                user: DUMMY_USERNAME,
                permissionNames: permissionNames.join(',')
            }));
        });

        it('returns false when one permission is checked and is denied', function() {
            // Given
            var permissionNames = [DELETE_PERMISSION_NAME];
            var response = {
                id: 'global',
                permissions: [{
                    key: DELETE_PERMISSION_NAME,
                    value: 'false'
                }]
            };

            sessionService.getCurrentUsername.and.returnValue($q.when(DUMMY_USERNAME));

            permissionsResource.get.and.returnValue($q.when(response));

            // When
            var result = authorizationService.hasGlobalPermissions(permissionNames);

            // Then
            expect(result).toBeResolvedWithData(false);
        });

        it('returns true when one permission is checked and is granted', function() {
            // Given
            var permissionNames = [READ_PERMISSION_NAME];
            var response = {
                id: 'global',
                permissions: [{
                    key: READ_PERMISSION_NAME,
                    value: 'true'
                }]
            };

            sessionService.getCurrentUsername.and.returnValue($q.when(DUMMY_USERNAME));

            permissionsResource.get.and.returnValue($q.when(response));

            // When
            var result = authorizationService.hasGlobalPermissions(permissionNames);

            // Then
            expect(result).toBeResolvedWithData(true);
        });

        it('returns false when one of the multiple permissions checked is denied', function() {
            // Given
            var permissionNames = [READ_PERMISSION_NAME, DELETE_PERMISSION_NAME, WRITE_PERMISSION_NAME];
            var response = {
                id: 'global',
                permissions: [{
                    key: READ_PERMISSION_NAME,
                    value: 'true'
                }, {
                    key: DELETE_PERMISSION_NAME,
                    value: 'false'
                }, {
                    key: WRITE_PERMISSION_NAME,
                    valeu: 'true'
                }]
            };

            sessionService.getCurrentUsername.and.returnValue($q.when(DUMMY_USERNAME));

            permissionsResource.get.and.returnValue($q.when(response));

            // When
            var result = authorizationService.hasGlobalPermissions(permissionNames);

            // Then
            expect(result).toBeResolvedWithData(false);
        });

        it('should return true if all of the multiple permissions checked are granted', function() {
            // Given
            var permissionNames = [READ_PERMISSION_NAME, WRITE_PERMISSION_NAME];
            var response = {
                id: 'global',
                permissions: [{
                    key: READ_PERMISSION_NAME,
                    value: 'true'
                }, {
                    key: WRITE_PERMISSION_NAME,
                    value: 'true'
                }]
            };

            sessionService.getCurrentUsername.and.returnValue($q.when(DUMMY_USERNAME));

            permissionsResource.get.and.returnValue($q.when(response));

            // When
            var result = authorizationService.hasGlobalPermissions(permissionNames);

            // Then
            expect(result).toBeResolvedWithData(true);
        });

        it('should return false if a new requested permission is passed that does not exist in the permissions object returned from the API', function() {
            // Given
            var permissionNames = [READ_PERMISSION_NAME, UNKNOWN_PERMISSION_NAME];
            var response = {
                id: 'global',
                permissions: [{
                    key: READ_PERMISSION_NAME,
                    value: 'true'
                }]
            };

            sessionService.getCurrentUsername.and.returnValue($q.when(DUMMY_USERNAME));

            permissionsResource.get.and.returnValue($q.when(response));

            // When
            var result = authorizationService.hasGlobalPermissions(permissionNames);

            // Then
            expect(result).toBeResolvedWithData(false);
        });
    });
});
