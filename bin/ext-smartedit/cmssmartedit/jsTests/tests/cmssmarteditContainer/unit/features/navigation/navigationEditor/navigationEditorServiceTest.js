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
describe('NavigationEditorService - ', function() {

    var navigationEditorService;
    var navigationRestService, $q, $rootScope;
    var uriParams = {
        siteId: 'siteId',
        catalogId: 'catalogId',
        catalogVersion: 'catalogVersion'
    };

    var node = {
        uid: "1",
        name: "node1",
        title: {
            en: "node1_en",
            fr: "node1_fr"
        },
        parentUid: "root",
        position: 0,
        parent: {
            uid: 'root',
        }
    };

    beforeEach(module('smarteditServicesModule', function($provide) {
        var restServiceFactory = jasmine.createSpyObj('restServiceFactory', ['get']);
        $provide.value('restServiceFactory', restServiceFactory);

        $provide.value('NAVIGATION_MANAGEMENT_RESOURCE_URI', 'NAVIGATION_MANAGEMENT_RESOURCE_URI');

        navigationRestService = jasmine.createSpyObj('navigationRestService', ['update']);
        restServiceFactory.get.and.returnValue(navigationRestService);
    }));

    beforeEach(module('navigationEditorServiceModule'));

    beforeEach(inject(function(_navigationEditorService_, _$q_, _$rootScope_) {
        navigationEditorService = _navigationEditorService_;
        $q = _$q_;
        $rootScope = _$rootScope_;
    }));

    it('GIVEN navigation rest service call fails WHEN I update a given node THEN it will return a rejected promise', function() {

        navigationRestService.update.and.returnValue($q.reject());

        var promise = navigationEditorService.updateNavigationNode(node, uriParams);
        $rootScope.$digest();

        expect(promise).toBeRejected();

    });

    it('GIVEN navigation rest service call succeeds WHEN I update a given node THEN it will return a resolved promise', function() {

        navigationRestService.update.and.returnValue($q.when(node));

        var promise = navigationEditorService.updateNavigationNode(node, uriParams);
        $rootScope.$digest();

        expect(navigationRestService.update).toHaveBeenCalledWith({
            identifier: '1',
            siteUID: 'siteId',
            catalogId: 'catalogId',
            catalogVersion: 'catalogVersion',
            parentUid: 'root',
            uid: '1',
            name: 'node1',
            title: {
                en: 'node1_en',
                fr: 'node1_fr'
            },
            position: 0
        });

        expect(promise).toBeResolved();
        expect(node.parent.initiated).toBe(false);

    });

});
