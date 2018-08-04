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
/* jshint unused:false, undef:false */
/*
    This scenarios have already been covered by e2e tests in the landingPageTest.js.
    Will analyze whether to fix these directive unit test or remove it altogether (Ticket has already been created). 
*/
xdescribe("synchronizeCatalogModule - ", function() {

    var parentScope, scope, element, ctrl, $q, l10nFilter;
    var synchronizationService;
    var confirmationModalService;

    beforeEach(module('cmssmarteditContainerTemplates'));
    beforeEach(module('ngMock'));

    beforeEach(function() {
        angular.module('alertServiceModule', []);
        angular.module('confirmationModalServiceModule', []);
        angular.module('pascalprecht.translate', []);
        angular.module('l10nModule', []);
    });

    beforeEach(module('synchronizationServiceModule', function($provide) {
        synchronizationService = jasmine.createSpyObj('synchronizationService', ['getCatalogSyncStatus', 'updateCatalogSync',
            'startAutoGetSyncData', 'stopAutoGetSyncData'
        ]);
        synchronizationService.getCatalogSyncStatus.and.callFake(function() {
            return $q.when([]);
        });
        synchronizationService.updateCatalogSync.and.callFake(function() {
            return $q.when([]);
        });
        $provide.value('synchronizationService', synchronizationService);
    }));

    beforeEach(module('confirmationModalServiceModule', function($provide) {
        confirmationModalService = jasmine.createSpyObj('confirmationModalService', ['confirm']);
        confirmationModalService.confirm.and.callFake(function() {
            return $q.when([]);
        });
        $provide.value('confirmationModalService', confirmationModalService);
    }));

    beforeEach(module('alertServiceModule', function($provide) {
        var alertService = jasmine.createSpyObj('alertService', ['pushAlerts']);
        $provide.value('alertService', alertService);
    }));

    beforeEach(module('pascalprecht.translate', function($provide) {
        var translateFilter = function(value) {
            return value;
        };
        $provide.value('translateFilter', translateFilter);
        l10nFilter = jasmine.createSpy('l10nFilter').and.callFake(function(stringsMap) {
            return stringsMap[Object.keys(stringsMap)[0]];
        });
        $provide.value('l10nFilter', l10nFilter);
    }));


    beforeEach(module('synchronizeCatalogModule'));
    beforeEach(inject(function($compile, $rootScope, _$q_) {
        parentScope = $rootScope.$new();
        $q = _$q_;
        window.smarteditJQuery.extend(parentScope, {
            ctrl: {
                catalog: {
                    catalogId: 'abc',
                    name: {
                        en: 'catalogName',
                        fr: 'nomDeCatalogue'
                    }
                }
            }
        });
        element = $compile('<synchronize-catalog data-catalog="ctrl.catalog"></synchronize-catalog>')(parentScope);
        parentScope.$digest();
        scope = element.isolateScope();
        ctrl = scope.ctrl;

    }));

    it('should call "get synchronization status" on initialization.', function() {
        expect(synchronizationService.getCatalogSyncStatus).toHaveBeenCalledWith({
            catalogId: 'abc',
            name: {
                en: 'catalogName',
                fr: 'nomDeCatalogue'
            }
        });
    });

    it('should call "sync update" when the client confirms sync.', function() {
        ctrl.syncCatalog();
        scope.$digest();
        expect(confirmationModalService.confirm).toHaveBeenCalledWith({
            description: 'sync.confirm.msg',
            title: 'sync.confirmation.title',
            descriptionPlaceholders: {
                catalogName: 'catalogName'
            }
        });
        expect(synchronizationService.updateCatalogSync).toHaveBeenCalledWith({
            catalogId: 'abc',
            name: {
                en: 'catalogName',
                fr: 'nomDeCatalogue'
            }
        });
    });

    it('should stop calling "get sync update" on destroy', function() {
        scope.$destroy();
        expect(synchronizationService.stopAutoGetSyncData).toHaveBeenCalledWith({
            catalogId: 'abc',
            name: {
                en: 'catalogName',
                fr: 'nomDeCatalogue'
            }
        });
    });
});
