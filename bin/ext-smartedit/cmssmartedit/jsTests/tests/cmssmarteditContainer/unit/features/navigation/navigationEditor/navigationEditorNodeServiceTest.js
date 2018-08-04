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
describe('navigationEditorNodeService - ', function() {

    var navigationEditorNodeService, navigationNodeRestService, navigationNodeAncestorsRestService;
    var $q, $rootScope;
    var uriParams = {
        siteId: 'siteId',
        catalogId: 'catalogId',
        catalogVersion: 'catalogVersion'
    };

    var node = {
        uid: "1",
        entries: [{
            itemId: "Item-ID-1.1",
            itemType: "CMSLinkComponent",
            navigationNodeUid: "1",
            uid: "1",
            name: "Entry 1.1",
            parent: {
                uid: "bla"
            }
        }, {
            itemId: "Item-ID-1.2",
            itemType: "CMSLinkComponent",
            navigationNodeUid: "1",
            uid: "2",
            name: "Entry 1.2",
            parent: {
                uid: "bla"
            }
        }, {
            itemId: "Item-ID-1.3",
            itemType: "CMSLinkComponent",
            navigationNodeUid: "1",
            uid: "3",
            name: "Entry 1.3",
            parent: {
                uid: "bla"
            }
        }],
        name: "node1",
        nodes: [],
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


    var nodes = [{
        uid: "1",
        entries: [],
        name: "node1",
        title: {
            en: "node1_en",
            fr: "node1_fr"
        },
        parentUid: "root",
        position: 0,
        hasChildren: true,
        hasEntries: true
    }, {
        uid: "2",
        entries: [],
        name: "node2",
        title: {
            en: "node2_en",
            fr: "node2_fr"
        },
        parentUid: "root",
        position: 1,
        hasChildren: true,
        hasEntries: false
    }, {
        uid: "4",
        entries: [],
        name: "node4",
        title: {
            "en": "nodeA",
            "fr": "nodeA"
        },
        parentUid: "1",
        position: 0,
        hasChildren: true,
        hasEntries: true
    }, {
        uid: "5",
        entries: [],
        name: "node5",
        title: {
            "en": "nodeB",
            "fr": "nodeB"
        },
        parentUid: "1",
        position: 1,
        hasChildren: false,
        hasEntries: false
    }, {
        uid: "6",
        entries: [],
        name: "node6",
        title: {
            "en": "nodeC",
            "fr": "nodeC"
        },
        parentUid: "2",
        position: 0,
        hasChildren: false,
        hasEntries: false
    }, {
        uid: "7",
        entries: [],
        name: "node7",
        title: {
            "en": "nodeC",
            "fr": "nodeC"
        },
        parentUid: "1",
        position: 2,
        hasChildren: false,
        hasEntries: false
    }, {
        uid: "8",
        entries: [],
        name: "node8",
        title: {
            "en": "nodeC",
            "fr": "nodeC"
        },
        parentUid: "4",
        position: 0,
        hasChildren: true,
        hasEntries: true
    }, {
        uid: "9",
        entries: [],
        name: "node9",
        title: {
            "en": "nodeC",
            "fr": "nodeC"
        },
        parentUid: "8",
        position: 0,
        hasChildren: false,
        hasEntries: false
    }];

    beforeEach(module('functionsModule'));

    beforeEach(module('navigationEditorNodeServiceModule', function($provide) {

        navigationNodeRestService = jasmine.createSpyObj('navigationNodeRestService', ['get', 'update']);
        $provide.value('navigationNodeRestService', navigationNodeRestService);

    }));

    beforeEach(inject(function(_navigationEditorNodeService_, _$q_, _$rootScope_) {
        navigationEditorNodeService = _navigationEditorNodeService_;
        $q = _$q_;
        $rootScope = _$rootScope_;
    }));

    it('GIVEN navigation rest service call fails WHEN I update a given node THEN it will return a rejected promise', function() {

        navigationNodeRestService.update.and.returnValue($q.reject());

        expect(navigationEditorNodeService.updateNavigationNode(node, uriParams)).toBeRejected();

    });

    it('GIVEN navigation rest service call succeeds WHEN I update a given node THEN it will return a resolved promise', function() {

        navigationNodeRestService.update.and.returnValue($q.when(node));

        expect(navigationEditorNodeService.updateNavigationNode(node, uriParams)).toBeResolved();

        expect(navigationNodeRestService.update).toHaveBeenCalledWith({
            identifier: '1',
            entries: [{
                itemId: 'Item-ID-1.1',
                itemType: 'CMSLinkComponent',
                navigationNodeUid: '1',
                uid: '1',
                name: 'Entry 1.1',
            }, {
                itemId: 'Item-ID-1.2',
                itemType: 'CMSLinkComponent',
                navigationNodeUid: '1',
                uid: '2',
                name: 'Entry 1.2',
            }, {
                itemId: 'Item-ID-1.3',
                itemType: 'CMSLinkComponent',
                navigationNodeUid: '1',
                uid: '3',
                name: 'Entry 1.3',
            }],
            siteId: 'siteId',
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

        expect(node.parent.initiated).toBe(false);

    });

    it('WHEN the server returns too many nodes for the ancestry THEN the service still filters, orders and formats the list', function() {

        navigationNodeRestService.get.and.returnValue($q.when({
            sompropertyName: nodes
        }));

        expect(navigationEditorNodeService.getNavigationNodeAncestry("8", uriParams)).toBeResolvedWithData([{
            uid: "1",
            entries: [],
            name: "node1",
            title: {
                en: "node1_en",
                fr: "node1_fr"
            },
            parentUid: "root",
            position: 0,
            hasChildren: true,
            hasEntries: true,
            level: 0,
            formattedLevel: 'se.cms.navigationcomponent.management.node.level.root'
        }, {
            uid: '4',
            entries: [],
            name: 'node4',
            title: {
                en: 'nodeA',
                fr: 'nodeA'
            },
            parentUid: '1',
            position: 0,
            hasChildren: true,
            hasEntries: true,
            level: 1,
            formattedLevel: 'se.cms.navigationcomponent.management.node.level.non.root'
        }, {
            uid: '8',
            entries: [],
            name: 'node8',
            title: {
                en: 'nodeC',
                fr: 'nodeC'
            },
            parentUid: '4',
            position: 0,
            hasChildren: true,
            hasEntries: true,
            level: 2,
            formattedLevel: 'se.cms.navigationcomponent.management.node.level.non.root'
        }]);

        expect(navigationNodeRestService.get).toHaveBeenCalledWith({
            ancestorTrailFrom: '8',
            siteId: 'siteId',
            catalogId: 'catalogId',
            catalogVersion: 'catalogVersion'
        });

    });


});
