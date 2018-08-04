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
describe('treeService', function() {
    var $q, TreeService, restServiceFactory, nodesRestService;
    var nodeUri = "asdfasdf";

    var navigationNodeList = [{
        uid: "1",
        name: "node1",
        title: {
            en: "node1_en",
            fr: "node1_fr"
        },
        parentUid: "someUid"
    }, {
        uid: "2",
        name: "node2",
        title: {
            en: "node2_en",
            fr: "node2_fr"
        },
        parentUid: "someUid"
    }];

    beforeEach(module("treeModule", function($provide) {

        restServiceFactory = jasmine.createSpyObj('restServiceFactory', ['get']);
        $provide.value('restServiceFactory', restServiceFactory);

        nodesRestService = jasmine.createSpyObj('nodesRestService', ['get', 'save', 'remove']);
        restServiceFactory.get.and.returnValue(nodesRestService);

    }));

    beforeEach(inject(function(_$q_, _TreeService_) {
        $q = _$q_;
        TreeService = _TreeService_;
    }));

    it('WHEN parent is not initiated, THEN fetchChildren will retrieve its first level children, set cardinalities and mark the parent as initiated', function() {

        var response = {
            navigationNodes: navigationNodeList
        };

        var parent = {
            uid: "someUid"
        };
        nodesRestService.get.and.returnValue($q.when(response));
        var treeService = new TreeService(nodeUri);

        expect(treeService.fetchChildren(parent)).toBeResolvedWithData(navigationNodeList);

        expect(parent.nodes).toEqual(navigationNodeList);
        expect(parent.initiated).toBe(true);
        expect(nodesRestService.get).toHaveBeenCalledWith({
            parentUid: 'someUid'
        });
    });

    it('GIVEN parent does not have any child, WHEN parent is not initiated, THEN fetchChildren will retrieve empty array and mark the parent as initiated', function() {

        var response = {
            navigationNodes: []
        };
        var emptyArray = [];

        var parent = {
            uid: "someUid"
        };
        nodesRestService.get.and.returnValue($q.when(response));
        var treeService = new TreeService(nodeUri);

        expect(treeService.fetchChildren(parent)).toBeResolvedWithData(emptyArray);
        expect(parent.initiated).toBe(true);
        expect(nodesRestService.get).toHaveBeenCalledWith({
            parentUid: 'someUid'
        });
    });

    it('WHEN the nodesRestService response has a nodes array and some objects and String, THEN fetchChildren will still return the nodes array and filter everything else', function() {

        var response = {
            someString: '',
            navigationNodes: navigationNodeList,
            someObj: {}
        };

        var parent = {
            uid: "someUid"
        };
        nodesRestService.get.and.returnValue($q.when(response));
        var treeService = new TreeService(nodeUri);

        expect(treeService.fetchChildren(parent)).toBeResolvedWithData(navigationNodeList);

    });

    it('WHEN parent is initiated, THEN fetchChildren will simply return its nodes', function() {

        var parent = {
            uid: "someUid",
            initiated: true,
            nodes: navigationNodeList
        };

        var treeService = new TreeService(nodeUri);

        expect(treeService.fetchChildren(parent)).toBeResolvedWithData(parent.nodes);

        expect(nodesRestService.get).not.toHaveBeenCalled();
    });

    it('saveNode will require the creation of an empty node (only passing the parentUid) and set the parent.hasChildren to true and set the parent of the returned child', function() {

        var treeService = new TreeService(nodeUri);

        var parent = {
            uid: "someUid",
            hasChildren: "false",
            nodes: ['sdfad']
        };
        var someNode = {};

        nodesRestService.save.and.returnValue($q.when(someNode));

        var augmented = angular.copy(someNode);
        augmented.parent = parent;
        expect(treeService.saveNode(parent)).toBeResolvedWithData(augmented);

        expect(parent.hasChildren).toBe(true);
        expect(nodesRestService.save).toHaveBeenCalledWith({
            parentUid: 'someUid',
            name: 'someUid1'
        });
    });

    it('removeNode', function() {

        var parent = {
            uid: "asdfasd",
            hasChildren: true,
            nodes: ['sdfad']
        };
        var node = {
            uid: 'someUid'
        };

        node.parent = parent;
        parent.nodes.push(node);

        var treeService = new TreeService(nodeUri);

        nodesRestService.remove.and.returnValue($q.when());

        expect(treeService.removeNode(node)).toBeResolvedWithData(undefined);

        expect(parent.hasChildren).toBe(true);
        expect(nodesRestService.remove).toHaveBeenCalledWith({
            identifier: 'someUid'
        });
    });


});
