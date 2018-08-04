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
describe('smartEditContractChangeListener in polyfill mode', function() {
    var $q;
    var $rootScope;
    var $interval;
    var systemEventService;
    var componentHandlerService;
    var CONTRACT_CHANGE_LISTENER_PROCESS_EVENTS;
    var CONTRACT_CHANGE_LISTENER_COMPONENT_PROCESS_STATUS;
    var SMARTEDIT_COMPONENT_PROCESS_STATUS = "smartEditComponentProcessStatus";
    var yjQuery;
    var isInExtendedViewPort;
    var smartEditContractChangeListener;
    var testModeService;
    var mutationObserverMock;
    var intersectionObserverMock;
    var mutationObserverCallback;
    var intersectionObserverCallback;
    var onComponentRepositionedCallback;
    var onComponentResizedCallback;
    var onComponentsAddedCallback;
    var onComponentsRemovedCallback;
    var onComponentChangedCallback;
    var resizeListener;
    var positionRegistry;
    var runIntersectionObserver;
    var parent;
    var directParent;
    var component0;
    var component1;
    var component2;
    var component2_1;
    var component3;
    var invisibleComponent;
    var nonProcessableComponent;
    var detachedComponent;
    var holder = {};

    var SECOND_LEVEL_CHILDREN;
    var INTERSECTIONS_MAPPING;
    var $document;

    var COMPONENT_CLASS;
    var UUID_ATTRIBUTE;
    var ID_ATTRIBUTE;
    var INITIAL_PAGE_UUID = 'INITIAL_PAGE_UUID';
    var ANY_PAGE_UUID = 'ANY_PAGE_UUID';
    var REPROCESS_TIMEOUT = 100;
    var BODY_TAG = 'body';
    var BODY = {};

    beforeEach(module('functionsModule'));

    beforeEach(module('smartEditContractChangeListenerModule', function() {
        window.elementResizeDetectorMaker = function() {
            return {
                uninstall: angular.noop
            };
        };
    }));

    angular.module('smartedit.commons', []);
    beforeEach(module('smartedit.commons', function($provide) {

        testModeService = jasmine.createSpyObj('testModeService', ['isE2EMode']);
        testModeService.isE2EMode.and.returnValue(true);
        $provide.value('testModeService', testModeService);
    }));

    beforeEach(module('componentHandlerServiceModule', function($provide) {

        var polyfillService = jasmine.createSpyObj('polyfillService', ['isEligibleForExtendedView']);
        polyfillService.isEligibleForExtendedView.and.returnValue(true);

        $provide.value('polyfillService', polyfillService);

        isInExtendedViewPort = jasmine.createSpy('isInExtendedViewPort');

        //we here give isInExtendedViewPort the same beahviour as isIntersecting
        isInExtendedViewPort.and.callFake(function(element) {
            var obj = INTERSECTIONS_MAPPING.find(function(obj) {
                return obj.target === element;
            });
            return obj ? obj.isIntersecting : false;
        });

        $provide.value('isInExtendedViewPort', isInExtendedViewPort);

        componentHandlerService = jasmine.createSpyObj('componentHandlerService', ['getFromSelector', 'getPageUUID', 'getClosestSmartEditComponent', 'isSmartEditComponent', 'getFirstSmartEditComponentChildren', 'getParent']);
        $provide.value('componentHandlerService', componentHandlerService);

        resizeListener = jasmine.createSpyObj('resizeListener', ['register', 'unregister', 'fix', 'dispose', 'init']);
        $provide.value('resizeListener', resizeListener);

        positionRegistry = jasmine.createSpyObj('positionRegistry', ['register', 'unregister', 'getRepositionedComponents', 'dispose']);
        $provide.value('positionRegistry', positionRegistry);

        yjQuery = jasmine.createSpyObj('yjQuery', ['contains']);
        yjQuery.contains.and.callFake(function(container, element) {
            if (container !== $document[0]) {
                throw "yjQuery.contains should have been the plain document object";
            }
            return element.name !== detachedComponent.name;
        });
        yjQuery.fn = {
            extend: function() {}
        };

        $provide.value("yjQuery", yjQuery);

        $provide.value("SMARTEDIT_COMPONENT_PROCESS_STATUS", SMARTEDIT_COMPONENT_PROCESS_STATUS);
    }));

    beforeEach(inject(function(_$q_, _$document_, _$rootScope_, _$interval_, _smartEditContractChangeListener_, _systemEventService_, _CONTRACT_CHANGE_LISTENER_PROCESS_EVENTS_, _CONTRACT_CHANGE_LISTENER_COMPONENT_PROCESS_STATUS_, _COMPONENT_CLASS_, _UUID_ATTRIBUTE_, _ID_ATTRIBUTE_) {
        $q = _$q_;
        $document = _$document_;
        $rootScope = _$rootScope_;
        $interval = _$interval_;
        systemEventService = _systemEventService_;
        CONTRACT_CHANGE_LISTENER_PROCESS_EVENTS = _CONTRACT_CHANGE_LISTENER_PROCESS_EVENTS_;
        CONTRACT_CHANGE_LISTENER_COMPONENT_PROCESS_STATUS = _CONTRACT_CHANGE_LISTENER_COMPONENT_PROCESS_STATUS_;
        smartEditContractChangeListener = _smartEditContractChangeListener_;

        mutationObserverMock = jasmine.createSpyObj('MutationObserver', ['observe', 'disconnect']);
        spyOn(smartEditContractChangeListener, '_newMutationObserver').and.callFake(function(callback) {
            mutationObserverCallback = callback;
            this.observe = angular.noop;
            this.disconnect = angular.noop;
            return mutationObserverMock;
        });

        intersectionObserverMock = jasmine.createSpyObj('IntersectionObserver', ['observe', 'unobserve', 'disconnect']);
        spyOn(smartEditContractChangeListener, '_newIntersectionObserver').and.callFake(function(callback) {
            intersectionObserverCallback = callback;
            return intersectionObserverMock;
        });
        intersectionObserverMock.observe.and.callFake(function(comp) {
            //run time intersectionObserver would indeed trigger a callback immediately after observing
            intersectionObserverCallback(INTERSECTIONS_MAPPING.filter(function(intersection) {
                return intersection.target === comp;
            }));
        });

        runIntersectionObserver = function(queue) {
            intersectionObserverCallback(queue);
            $rootScope.$digest();
        };

        onComponentRepositionedCallback = jasmine.createSpy('onComponentRepositioned');
        smartEditContractChangeListener.onComponentRepositioned(onComponentRepositionedCallback);

        onComponentResizedCallback = angular.noop;
        smartEditContractChangeListener.onComponentResized(onComponentResizedCallback);

        onComponentsAddedCallback = jasmine.createSpy('onComponentsAdded');
        smartEditContractChangeListener.onComponentsAdded(onComponentsAddedCallback);

        onComponentsRemovedCallback = jasmine.createSpy('onComponentsRemoved');
        smartEditContractChangeListener.onComponentsRemoved(onComponentsRemovedCallback);

        onComponentChangedCallback = jasmine.createSpy('onComponentChangedCallback');
        smartEditContractChangeListener.onComponentChanged(onComponentChangedCallback);

        COMPONENT_CLASS = _COMPONENT_CLASS_;
        UUID_ATTRIBUTE = _UUID_ATTRIBUTE_;
        ID_ATTRIBUTE = _ID_ATTRIBUTE_;
    }));

    beforeEach(function() {

        parent = jasmine.createSpyObj('parent', ['attr']);
        parent.nodeType = 1;
        parent.className = COMPONENT_CLASS;
        parent.attr.and.returnValue('parent');
        parent.name = 'parent';
        parent.contains = function() {
            return true;
        };
        parent.sourceIndex = 0;
        parent.dataset = {};

        directParent = jasmine.createSpyObj('directParent', ['attr']);
        directParent.nodeType = 1;
        directParent.attr.and.returnValue('directParent');
        directParent.name = 'directParent';
        directParent.contains = function() {
            return false;
        };
        directParent.sourceIndex = 1;
        directParent.dataset = {};

        component0 = jasmine.createSpyObj('component0', ['attr']);
        component0.nodeType = 1;
        component0.className = "nonSmartEditComponent";
        component0.attr.and.returnValue('component0');
        component0.name = 'component0';
        component0.contains = function() {
            return false;
        };
        component0.sourceIndex = 2;
        component0.dataset = {};

        component1 = jasmine.createSpyObj('component1', ['attr']);
        component1.nodeType = 1;
        component1.className = COMPONENT_CLASS;
        component1.attr.and.returnValue('component1');
        component1.name = 'component1';
        component1.contains = function() {
            return true;
        };
        component1.sourceIndex = 3;
        component1.dataset = {};

        component2_1 = jasmine.createSpyObj('component2_1', ['attr']);
        component2_1.nodeType = 1;
        component2_1.className = COMPONENT_CLASS;
        component2_1.attr.and.returnValue('component2_1');
        component2_1.name = 'component2_1';
        component2_1.contains = function() {
            return false;
        };
        component2_1.sourceIndex = 5;
        component2_1.dataset = {};

        component2 = jasmine.createSpyObj('component2', ['attr']);
        component2.nodeType = 1;
        component2.className = COMPONENT_CLASS;
        component2.attr.and.returnValue('component2');
        component2.name = 'component2';
        component2.contains = function(node) {
            return node === component2_1;
        };
        component2.sourceIndex = 4;
        component2.dataset = {};

        component3 = jasmine.createSpyObj('component3', ['attr']);
        component3.nodeType = 1;
        component3.className = COMPONENT_CLASS;
        component3.attr.and.returnValue('component3');
        component3.name = 'component3';
        component3.contains = function() {
            return false;
        };
        component3.sourceIndex = 6;
        component3.dataset = {};

        invisibleComponent = jasmine.createSpyObj('invisibleComponent', ['attr']);
        invisibleComponent.nodeType = 1;
        invisibleComponent.className = COMPONENT_CLASS;
        invisibleComponent.attr.and.returnValue('invisibleComponent');
        invisibleComponent.name = 'invisibleComponent';
        invisibleComponent.contains = function() {
            return false;
        };
        invisibleComponent.sourceIndex = 8;
        invisibleComponent.dataset = {};

        nonProcessableComponent = jasmine.createSpyObj('nonProcessableComponent', ['attr']);
        nonProcessableComponent.nodeType = 1;
        nonProcessableComponent.className = COMPONENT_CLASS;
        nonProcessableComponent.attr.and.returnValue('nonProcessableComponent');
        nonProcessableComponent.name = 'nonProcessableComponent';
        nonProcessableComponent.contains = function() {
            return false;
        };
        nonProcessableComponent.sourceIndex = 8;
        nonProcessableComponent.dataset = {};

        detachedComponent = jasmine.createSpyObj('detachedComponent', ['attr']);
        detachedComponent.nodeType = 1;
        detachedComponent.className = COMPONENT_CLASS;
        detachedComponent.attr.and.returnValue('detachedComponent');
        detachedComponent.name = 'detachedComponent';
        detachedComponent.contains = function() {
            return false;
        };
        detachedComponent.sourceIndex = 9;
        detachedComponent.dataset = {};

        var pageUUIDCounter = 0;
        componentHandlerService.getPageUUID.and.callFake(function() {
            pageUUIDCounter++;
            if (pageUUIDCounter === 1) {
                return $q.when(INITIAL_PAGE_UUID);
            } else if (pageUUIDCounter === 2) {
                return $q.when(ANY_PAGE_UUID);
            }
            return $q.when(null);
        });

        componentHandlerService.getFromSelector.and.callFake(function(arg) {
            if (arg === BODY_TAG) {
                return BODY;
            }
            return null;
        });

        componentHandlerService.isSmartEditComponent.and.callFake(function(node) {
            return node.className && node.className.split(/[\s]+/).indexOf(COMPONENT_CLASS) > -1;
        });

        componentHandlerService.getClosestSmartEditComponent.and.callFake(function(node) {
            if (node === parent || node === component1 || node === component2 || node === component2_1 || node === component3) {
                return [node];
            } else if (node === component0) {
                return [parent];
            } else {
                return [];
            }
        });

        componentHandlerService.getParent.and.callFake(function(node) {
            if (node === component2_1) {
                return [component2];
            } else if (node === component1 || node === component2 || node === component3) {
                return [parent];
            } else {
                return [];
            }
        });

        SECOND_LEVEL_CHILDREN = [component1];
        INTERSECTIONS_MAPPING = [{
            isIntersecting: true,
            target: component1 // child before 'parent'
        }, {
            isIntersecting: true,
            target: parent
        }, {
            isIntersecting: false,
            target: invisibleComponent
        }, {
            isIntersecting: true,
            target: nonProcessableComponent
        }];

        componentHandlerService.getFirstSmartEditComponentChildren.and.callFake(function(node) {
            if (node === BODY) {
                return [parent];
            } else if (node === parent) {
                return SECOND_LEVEL_CHILDREN;
            } else if (node === component2) {
                return [component2_1];
            } else if (node === component0) {
                return [component2]; // ok to just return array, slice is applied on it
            } else {
                return [];
            }
        });

        holder.canProcess = function(comp) {
            return comp !== nonProcessableComponent;
        };

        systemEventService.registerEventHandler(CONTRACT_CHANGE_LISTENER_PROCESS_EVENTS.PROCESS_COMPONENTS, function(eventId, components) {
            var result = components.map(function(component) {
                component.dataset[SMARTEDIT_COMPONENT_PROCESS_STATUS] = holder.canProcess(component) ? CONTRACT_CHANGE_LISTENER_COMPONENT_PROCESS_STATUS.PROCESS : CONTRACT_CHANGE_LISTENER_COMPONENT_PROCESS_STATUS.REMOVE;
                return component;
            });
            return $q.when(result);
        });

    });

    beforeEach(function() {
        smartEditContractChangeListener.initListener();
        $rootScope.$digest();
    });

    describe('DOM intersections', function() {

        it('should register resize and position listeners on existing visible smartedit components that are processable', function() {
            expect(resizeListener.init).toHaveBeenCalled();

            expect(resizeListener.register.calls.count()).toEqual(2);
            expect(resizeListener.register.calls.argsFor(0)[0]).toBe(parent);
            expect(resizeListener.register.calls.argsFor(1)[0]).toBe(component1);

            expect(resizeListener.fix.calls.count()).toEqual(1);
            expect(resizeListener.fix.calls.argsFor(0)[0]).toBe(parent);

            expect(positionRegistry.register.calls.count()).toEqual(2);
            expect(positionRegistry.register.calls.argsFor(0)[0]).toBe(parent);
            expect(positionRegistry.register.calls.argsFor(1)[0]).toBe(component1);

            expect(onComponentsAddedCallback.calls.count()).toBe(1);
            expect(onComponentsAddedCallback.calls.argsFor(0)[0]).toEqual([parent, component1]);
        });

        it('event with same intersections for components should not retrigger anything', function() {
            runIntersectionObserver(INTERSECTIONS_MAPPING);

            resizeListener.register.calls.reset();
            positionRegistry.register.calls.reset();
            onComponentsAddedCallback.calls.reset();
            onComponentsRemovedCallback.calls.reset();

            runIntersectionObserver(INTERSECTIONS_MAPPING);

            expect(resizeListener.register).not.toHaveBeenCalled();
            expect(positionRegistry.register).not.toHaveBeenCalled();
            expect(onComponentsAddedCallback).not.toHaveBeenCalled();
            expect(onComponentsRemovedCallback).not.toHaveBeenCalled();
        });

        it('when components are no longer visible, they are destroyed', function() {
            runIntersectionObserver(INTERSECTIONS_MAPPING);

            resizeListener.register.calls.reset();
            resizeListener.unregister.calls.reset();
            positionRegistry.register.calls.reset();
            positionRegistry.unregister.calls.reset();
            onComponentsAddedCallback.calls.reset();
            onComponentsRemovedCallback.calls.reset();
            resizeListener.fix.calls.reset();

            INTERSECTIONS_MAPPING.forEach(function(element) {
                element.isIntersecting = false;
            });

            runIntersectionObserver(INTERSECTIONS_MAPPING);

            expect(resizeListener.register).not.toHaveBeenCalled();
            expect(resizeListener.unregister).not.toHaveBeenCalled();

            expect(resizeListener.fix).not.toHaveBeenCalled();

            expect(positionRegistry.register).not.toHaveBeenCalled();
            expect(positionRegistry.unregister).not.toHaveBeenCalled();

            expect(onComponentsAddedCallback).not.toHaveBeenCalled();
            expect(onComponentsRemovedCallback.calls.count()).toBe(1);
            expect(onComponentsRemovedCallback.calls.argsFor(0)[0]).toEqual([{
                component: parent,
                parent: undefined
            }, {
                component: component1,
                parent: parent
            }]);
        });

    });

    describe('DOM mutations', function() {
        beforeEach(function() {
            resizeListener.fix.calls.reset();
            resizeListener.unregister.calls.reset();
            resizeListener.register.calls.reset();
            positionRegistry.unregister.calls.reset();
            positionRegistry.register.calls.reset();
            onComponentsAddedCallback.calls.reset();
        });

        it('should init the Mutation Observer and observe on body element with the expected configuration', function() {
            var expectedConfig = {
                attributes: true,
                attributeOldValue: true,
                childList: true,
                characterData: false,
                subtree: true
            };
            expect(mutationObserverMock.observe).toHaveBeenCalledWith(document.getElementsByTagName('body')[0], expectedConfig);
        });

        it('should be able to observe a page change and execute a registered page change callback', function() {
            // GIVEN
            var pageChangedCallback = jasmine.createSpy('callback');
            smartEditContractChangeListener.onPageChanged(pageChangedCallback);

            // WHEN
            var mutations = [{
                attributeName: 'class',
                type: 'attributes',
                target: {
                    tagName: 'BODY'
                }
            }];
            mutationObserverCallback(mutations);
            $rootScope.$digest();

            // THEN
            expect(pageChangedCallback.calls.argsFor(0)[0]).toEqual(ANY_PAGE_UUID);

            mutationObserverCallback(mutations);

            expect(pageChangedCallback.calls.count()).toBe(1);
        });

        it('when a parent and a child are in the same operation (can occur), the child is NOT ignored but is process AFTER the parent', function() {

            // WHEN
            Array.prototype.push.apply(INTERSECTIONS_MAPPING, [{
                isIntersecting: true,
                target: component2_1 // child before parent component2
            }, {
                isIntersecting: true,
                target: component2
            }, {
                isIntersecting: true,
                target: component3
            }, {
                isIntersecting: true,
                target: detachedComponent
            }]);

            SECOND_LEVEL_CHILDREN = [component1, component2, component3, invisibleComponent];
            var mutations = [{
                type: 'childList',
                addedNodes: [component2_1, component2, invisibleComponent, component3]
            }];
            mutationObserverCallback(mutations);
            $rootScope.$digest();

            // THEN
            expect(onComponentsAddedCallback.calls.count()).toBe(1);
            expect(onComponentsAddedCallback.calls.argsFor(0)[0]).toEqual([component2, component2_1, component3]);
        });

        it('should be able to observe sub tree of smartEditComponent component added', function() {

            // WHEN
            Array.prototype.push.apply(INTERSECTIONS_MAPPING, [{
                isIntersecting: true,
                target: component2
            }, {
                isIntersecting: true,
                target: component2_1
            }, {
                isIntersecting: true,
                target: component3
            }]);

            SECOND_LEVEL_CHILDREN = [component1, component2, component3, invisibleComponent];
            var mutations = [{
                type: 'childList',
                addedNodes: [component2, component3, invisibleComponent]
            }];
            mutationObserverCallback(mutations);
            $rootScope.$digest();

            // THEN
            expect(resizeListener.unregister.calls.count()).toEqual(0);

            expect(resizeListener.register.calls.count()).toEqual(3);
            expect(resizeListener.register.calls.argsFor(0)[0]).toBe(component2);
            expect(resizeListener.register.calls.argsFor(1)[0]).toBe(component2_1);
            expect(resizeListener.register.calls.argsFor(2)[0]).toBe(component3);

            expect(resizeListener.fix.calls.count()).toBe(2);
            expect(resizeListener.fix.calls.argsFor(0)[0]).toBe(parent);
            expect(resizeListener.fix.calls.argsFor(1)[0]).toBe(component2);

            expect(positionRegistry.register.calls.count()).toEqual(3);
            expect(positionRegistry.register.calls.argsFor(0)[0]).toBe(component2);
            expect(positionRegistry.register.calls.argsFor(1)[0]).toBe(component2_1);
            expect(positionRegistry.register.calls.argsFor(2)[0]).toBe(component3);

            expect(onComponentsAddedCallback.calls.count()).toBe(1);
            expect(onComponentsAddedCallback.calls.argsFor(0)[0]).toEqual([component2, component2_1, component3]);
        });

        it('should be able to observe sub tree of non smartEditComponent component added', function() {

            // WHEN
            Array.prototype.push.apply(INTERSECTIONS_MAPPING, [{
                isIntersecting: true,
                target: component2
            }, {
                isIntersecting: true,
                target: component2_1
            }, {
                isIntersecting: true,
                target: component3
            }]);

            SECOND_LEVEL_CHILDREN = [component1, component3, invisibleComponent];
            var mutations = [{
                type: 'childList',
                addedNodes: [component0, component3, invisibleComponent]
            }];
            mutationObserverCallback(mutations);
            $rootScope.$digest();

            // THEN

            expect(resizeListener.unregister.calls.count()).toEqual(0);

            expect(resizeListener.register.calls.count()).toEqual(3);
            expect(resizeListener.register.calls.argsFor(0)[0]).toBe(component2);
            expect(resizeListener.register.calls.argsFor(1)[0]).toBe(component2_1);
            expect(resizeListener.register.calls.argsFor(2)[0]).toBe(component3);

            expect(resizeListener.fix.calls.count()).toBe(2);
            expect(resizeListener.fix.calls.argsFor(0)[0]).toBe(parent);
            expect(resizeListener.fix.calls.argsFor(1)[0]).toBe(component2);

            expect(positionRegistry.register.calls.count()).toEqual(3);
            expect(positionRegistry.register.calls.argsFor(0)[0]).toBe(component2);
            expect(positionRegistry.register.calls.argsFor(1)[0]).toBe(component2_1);
            expect(positionRegistry.register.calls.argsFor(2)[0]).toBe(component3);

            expect(onComponentsAddedCallback.calls.count()).toBe(1);
            expect(onComponentsAddedCallback.calls.argsFor(0)[0]).toEqual([component2, component2_1, component3]);
        });

        it('should be able to observe sub tree of smartEditComponent (and parent) removed', function() {

            smartEditContractChangeListener.componentsQueue.push({
                component: component2,
                isIntersecting: true,
                processed: 'added',
                parent: parent
            });
            smartEditContractChangeListener.componentsQueue.push({
                component: component2_1,
                isIntersecting: true,
                processed: 'added',
                parent: component2
            });
            smartEditContractChangeListener.componentsQueue.push({
                component: component3,
                isIntersecting: true,
                processed: 'added',
                parent: parent
            });

            // WHEN
            Array.prototype.push.apply(INTERSECTIONS_MAPPING, [{
                isIntersecting: false,
                target: component2
            }, {
                isIntersecting: false,
                target: component2_1
            }, {
                isIntersecting: false,
                target: component3
            }]);
            SECOND_LEVEL_CHILDREN = [component1, component2, component3];

            intersectionObserverCallback(INTERSECTIONS_MAPPING);
            $rootScope.$digest();

            // THEN
            expect(resizeListener.register).not.toHaveBeenCalled();
            expect(resizeListener.unregister).not.toHaveBeenCalled();
            expect(positionRegistry.unregister).not.toHaveBeenCalled();

            expect(onComponentsRemovedCallback.calls.count()).toBe(1);
            expect(onComponentsRemovedCallback.calls.argsFor(0)[0]).toEqual([{
                component: component2,
                parent: parent
            }, {
                component: component2_1,
                parent: parent
            }, {
                component: component3,
                parent: parent
            }]);
        });

        it('should be able to stop all the listeners', function() {
            smartEditContractChangeListener.stopListener();

            expect(mutationObserverMock.disconnect).toHaveBeenCalled();
            expect(intersectionObserverMock.disconnect).toHaveBeenCalled();
            expect(resizeListener.dispose).toHaveBeenCalled();
            expect(positionRegistry.dispose).toHaveBeenCalled();
        });

        it('should call the componentRepositionedCallback when a component is repositioned after updating the registry', function() {
            positionRegistry.getRepositionedComponents.and.returnValue([component1]);

            $interval.flush(REPROCESS_TIMEOUT);

            expect(onComponentRepositionedCallback.calls.count()).toBe(1);
            expect(onComponentRepositionedCallback).toHaveBeenCalledWith(component1);
        });

        it('should cancel the repositionListener interval when calling stopListener', function() {
            positionRegistry.getRepositionedComponents.and.returnValue([]);

            var cancelSpy = spyOn($interval, 'cancel');

            smartEditContractChangeListener.stopListener();

            expect(cancelSpy.calls.count()).toBe(1);
        });

        it('should be able to observe a component change', function() {
            // WHEN
            var mutations = [{
                type: 'attributes',
                attributeName: UUID_ATTRIBUTE,
                target: component1,
                oldValue: 'random_uuid'
            }, {
                type: 'attributes',
                attributeName: ID_ATTRIBUTE,
                target: component1,
                oldValue: 'random_id'
            }];
            mutationObserverCallback(mutations);
            $rootScope.$digest();

            // THEN
            var expectedOldAttributes = {};
            expectedOldAttributes[UUID_ATTRIBUTE] = 'random_uuid';
            expectedOldAttributes[ID_ATTRIBUTE] = 'random_id';
            expect(onComponentChangedCallback.calls.argsFor(0)[0]).toEqual(component1, expectedOldAttributes);
        });
    });
});
