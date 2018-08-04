/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
describe('personalizationsmarteditShowComponentInfoListModule', function() {
    var mockModules = {};
    setupMockModules(mockModules); // jshint ignore:line

    var $componentController, personalizationsmarteditUtils, personalizationsmarteditMessageHandler, rootScope;


    var mockActions = {
        actions: [{
            actionCatalog: '1234',
            actionCatalogVersion: 'Staged'
        }, {
            actionCatalog: '1234',
            actionCatalogVersion: 'Online'
        }],
        pagination: {
            count: 2,
            page: 0,
            totalCount: 2,
            totalPages: 1
        }
    };

    beforeEach(module('personalizationsmarteditCommons', function($provide) {
        personalizationsmarteditUtils = jasmine.createSpyObj('personalizationsmarteditUtils', ['getAndSetCatalogVersionNameL10N']);
        $provide.value("personalizationsmarteditUtils", personalizationsmarteditUtils);

        personalizationsmarteditMessageHandler = jasmine.createSpyObj('personalizationsmarteditMessageHandler', ['sendError']);
        $provide.value("personalizationsmarteditMessageHandler", personalizationsmarteditMessageHandler);
    }));

    beforeEach(module('personalizationsmarteditRestServiceModule', function($provide) {
        mockModules.personalizationsmarteditRestService = jasmine.createSpyObj('personalizationsmarteditRestService', ['getCxCmsAllActionsForContainer']);
        $provide.value('personalizationsmarteditRestService', mockModules.personalizationsmarteditRestService);
    }));

    beforeEach(module('personalizationsmarteditShowComponentInfoListModule'));

    beforeEach(inject(function(_$componentController_, _$q_, _$rootScope_) {
        $componentController = _$componentController_;
        rootScope = _$rootScope_;

        mockModules.personalizationsmarteditRestService.getCxCmsAllActionsForContainer.and.callFake(function() {
            var deferred = _$q_.defer();
            deferred.resolve(
                mockActions
            );
            return deferred.promise;
        });

        mockModules.componentHandlerService.getFromSelector.and.callFake(function() {
            return [{
                getAttribute: function() {}
            }];
        });
    }));

    describe('Component API', function() {

        it('should have proper api when initialized without parameters', function() {
            var ctrl = $componentController('personalizationsmarteditShowComponentInfoList', null);

            expect(ctrl.isContainerIdEmpty).not.toBeDefined();
            expect(ctrl.actions).not.toBeDefined();
            expect(ctrl.moreCustomizationsRequestProcessing).not.toBeDefined();
            expect(ctrl.$onInit).toBeDefined();
        });
        it('should have proper api when initialized with parameters', function() {
            var bindings = {
                component: {
                    containerId: '1234'
                }
            };

            var ctrl = $componentController('personalizationsmarteditShowComponentInfoList', null, bindings);
            ctrl.$onInit();

            expect(ctrl.component.containerId).toEqual('1234');
            expect(ctrl.isContainerIdEmpty).toBeDefined();
            expect(ctrl.moreCustomizationsRequestProcessing).toBe(false);
            expect(ctrl.$onInit).toBeDefined();
        });

        it('should have actions when initialized with parameters', function() {
            var bindings = {
                component: {
                    containerId: '1234'
                }
            };

            var ctrl = $componentController('personalizationsmarteditShowComponentInfoList', null, bindings);
            ctrl.$onInit();

            ctrl.addMoreItems();
            rootScope.$digest();

            expect(ctrl.component.containerId).toEqual('1234');
            expect(ctrl.isContainerIdEmpty).toBeDefined();
            expect(ctrl.pagination.totalCount).toEqual(2);
            expect(ctrl.actions.length).toEqual(2);
            expect(ctrl.moreCustomizationsRequestProcessing).toBe(false);
        });
    });
});
