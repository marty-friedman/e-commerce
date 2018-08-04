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
describe('treeDndOptions', function() {
    var $q, $rootScope, _TreeDndOptions, confirmationModalService, $translate;

    beforeEach(function() {
        angular.module('ui.tree', []);
        angular.module('includeReplaceModule', []);
        angular.module('confirmationModalServiceModule', []);
        angular.module('alertServiceModule', []);
    });

    beforeEach(module("treeModule", function($provide) {
        $translate = jasmine.createSpy('$translate');
        $provide.value('$translate', $translate);
        confirmationModalService = jasmine.createSpyObj('confirmationModalService', ['confirm']);
        $provide.value('confirmationModalService', confirmationModalService);

        var alertService = jasmine.createSpyObj('alertService', ['showDanger']);
        $provide.value('alertService', alertService);
    }));

    beforeEach(inject(function(_$q_, __TreeDndOptions_, _$rootScope_) {
        $q = _$q_;
        _TreeDndOptions = __TreeDndOptions_;
        $rootScope = _$rootScope_;
    }));

    it('passing options that is of type not equal to object SHOULD throw an Error', function() {
        var options = 'test';
        expect(function() {
            new _TreeDndOptions(options);
        }).toThrow("Unexpected type for dragOptions, expected object but got string");
    });

    it('passing a map containing none of the DnD keys (beforeDropCallback, allowDropCallback and onDropCallback) SHOULD have dragEnabled set to False', function() {
        var options = {
            zzzzzz: 'zzzzzz'
        };
        var treeDndOptions = new _TreeDndOptions(options);
        expect(treeDndOptions.dragEnabled).toBe(false);
    });

    it('passing a map containing at least one of the callback functions(beforeDropCallback, allowDropCallback and onDropCallback) SHOULD have dragEnabled set to True', function() {
        var options = {
            beforeDropCallback: function() {}
        };
        var treeDndOptions = new _TreeDndOptions(options);
        expect(treeDndOptions.dragEnabled).toBe(true);
    });

    it('passing a map containing at least one of the callback functions(beforeDropCallback, allowDropCallback and onDropCallback) that is of type not equal to function SHOULD throw an Error', function() {
        var options = {
            beforeDropCallback: 'not a function'
        };
        expect(function() {
            new _TreeDndOptions(options);
        }).toThrow("Unexpected type for beforeDropCallback, expected function but got string");
    });

    it('passing a map containing with the DnD keys (beforeDropCallback, allowDropCallback and onDropCallback) SHOULD have set the accept, beforeDrop and dropped callbacks', function() {
        var options = {
            beforeDropCallback: function() {},
            onDropCallback: function() {},
            allowDropCallback: function() {}
        };
        var treeDndOptions = new _TreeDndOptions(options);
        expect(treeDndOptions.callbacks.accept).not.toBe(undefined);
        expect(treeDndOptions.callbacks.beforeDrop).not.toBe(undefined);
        expect(treeDndOptions.callbacks.dropped).not.toBe(undefined);
    });

    it('if dropped is called, onDropCallback SHOULD be called with the yTreeDndEvent', function() {

        var options = jasmine.createSpyObj('options', ['onDropCallback']);


        var event = {
            source: {
                nodeScope: {
                    $modelValue: 'source'
                }
            },
            dest: {
                index: 'index',
                nodesScope: {
                    $modelValue: ['dest']
                }
            }
        };
        var treeDndOptions = new _TreeDndOptions(options);

        treeDndOptions.callbacks.dropped(event);
        $rootScope.$digest();
        expect(options.onDropCallback).toHaveBeenCalledWith(jasmine.objectContaining({
            sourceNode: 'source',
            destinationNodes: ['dest'],
            position: 'index'
        }));
    });

    it('if accept is called, allowDropCallback SHOULD be called with the yTreeDndEvent ', function() {

        var options = jasmine.createSpyObj('options', ['allowDropCallback']);

        var sourceNodeScope = {
            $modelValue: 'source'
        };
        var destNodesScope = {
            $modelValue: ['dest']
        };
        var destIndex = 'index';

        var treeDndOptions = new _TreeDndOptions(options);

        treeDndOptions.callbacks.accept(sourceNodeScope, destNodesScope, destIndex);
        $rootScope.$digest();
        expect(options.allowDropCallback).toHaveBeenCalledWith(jasmine.objectContaining({
            sourceNode: 'source',
            destinationNodes: ['dest'],
            position: 'index'
        }));
    });

    it('if beforeDropCallback returns an object with key confirmDropI18nKey, a confirmation modal SHOULD open with the localized message', function() {


        var options = jasmine.createSpyObj('options', ['beforeDropCallback']);

        options.beforeDropCallback.and.returnValue({
            confirmDropI18nKey: 'description'
        });

        var event = {
            source: {
                nodeScope: {
                    $modelValue: 'source'
                }
            },
            dest: {
                index: 'index',
                nodesScope: {
                    $modelValue: ['dest']
                }
            }
        };
        var treeDndOptions = new _TreeDndOptions(options);

        treeDndOptions.callbacks.beforeDrop(event);
        $rootScope.$digest();
        expect(options.beforeDropCallback).toHaveBeenCalledWith(jasmine.objectContaining({
            sourceNode: 'source',
            destinationNodes: ['dest'],
            position: 'index'
        }));
        expect(confirmationModalService.confirm).toHaveBeenCalledWith({
            description: 'description'
        });
    });

    it('if beforeDropCallback returns a promise, then result of that promise SHOULD be returned', function() {

        var options = jasmine.createSpyObj('options', ['beforeDropCallback']);


        options.beforeDropCallback.and.returnValue($q.when(false));

        var event = {
            source: {
                nodeScope: {
                    $modelValue: 'source'
                }
            },
            dest: {
                index: 'index',
                nodesScope: {
                    $modelValue: ['dest']
                }
            }
        };
        var treeDndOptions = new _TreeDndOptions(options);
        var result = treeDndOptions.callbacks.beforeDrop(event);
        $rootScope.$digest();
        expect(options.beforeDropCallback).toHaveBeenCalledWith(jasmine.objectContaining({
            sourceNode: 'source',
            destinationNodes: ['dest'],
            position: 'index'
        }));
        expect(result).toBeResolvedWithData(false);

    });

});
