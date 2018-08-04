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
angular.module("smartEditContractChangeListenerModule", [
        'yjqueryModule',
        'yLoDashModule',
        'timerModule',
        'componentHandlerServiceModule',
        'resizeListenerModule',
        'positionRegistryModule',
        'crossFrameEventServiceModule',
        'eventServiceModule',
        'functionsModule',
        'smarteditServicesModule',
        'seConstantsModule'
    ])
    /*
     * interval at which manual listening/checks are executed
     * So far it is only by repositionListener
     * (resizeListener delegates to a self-contained third-party library and DOM mutations observation is done in native MutationObserver)
     */
    .constant("REPROCESS_TIMEOUT", 100)

    .constant("PROCESS_QUEUE_POLYFILL_INTERVAL", 250)

    .constant("CONTRACT_CHANGE_LISTENER_INTERSECTION_OBSERVER_OPTIONS", {
        // The root to use for intersection.
        // If not provided, use the top-level document’s viewport.
        root: null,

        // Same as margin, can be 1, 2, 3 or 4 components, possibly negative lengths.  
        // If an explicit root element is specified, components may be percentages of the
        // root element size. If no explicit root element is specified, using a percentage
        // is an error.
        rootMargin: '1000px',

        // Threshold(s) at which to trigger callback, specified as a ratio, or list of
        // ratios, of (visible area / total area) of the observed element (hence all
        // entries must be in the range [0, 1]). Callback will be invoked when the visible
        // ratio of the observed element crosses a threshold in the list.
        threshold: 0
    })

    .constant("CONTRACT_CHANGE_LISTENER_PROCESS_QUEUE_THROTTLE", 500)

    /*
     * service that allows specifying callbacks for all events affecting elements of the smartEdit storefront contract:
     * - page identifier change
     * - smartEdit components added or removed
     * - contract bound attributes of smartEdit components changes
     * - smartEdit components repositioned
     * - smartEdit components resized
     */
    .service("smartEditContractChangeListener", function(
        $log,
        $q,
        $injector,
        $interval,
        $rootScope,
        $document,
        yjQuery,
        lodash,
        isInDOM,
        componentHandlerService,
        resizeListener,
        positionRegistry,
        crossFrameEventService,
        systemEventService,
        polyfillService,
        compareHTMLElementsPosition,
        isInExtendedViewPort,
        REPROCESS_TIMEOUT,
        TYPE_ATTRIBUTE,
        ID_ATTRIBUTE,
        UUID_ATTRIBUTE,
        CONTRACT_CHANGE_LISTENER_PROCESS_EVENTS,
        CONTRACT_CHANGE_LISTENER_COMPONENT_PROCESS_STATUS,
        CONTRACT_CHANGE_LISTENER_INTERSECTION_OBSERVER_OPTIONS,
        CONTRACT_CHANGE_LISTENER_PROCESS_QUEUE_THROTTLE,
        SMARTEDIT_COMPONENT_PROCESS_STATUS,
        PROCESS_QUEUE_POLYFILL_INTERVAL,
        testModeService) {

        /*
         * list of smartEdit component attributes the change of which we observe to trigger an onComponentChanged event
         */
        var smartEditAttributeNames = [TYPE_ATTRIBUTE, ID_ATTRIBUTE, UUID_ATTRIBUTE];

        /*
         * Mutation object (return in a list of mutations in mutation event) can be of different types.
         * We are here only interested in type attributes (used for onPageChanged and onComponentChanged events) and childList (used for onComponentAdded events)
         */
        var MUTATION_TYPES = {
            CHILD_LIST: {
                NAME: "childList",
                ADD_OPERATION: "addedNodes",
                REMOVE_OPERATION: "removedNodes"
            },
            ATTRIBUTES: {
                NAME: "attributes"
            }
        };

        /*
         * Of all the DOM node types we only care for 1 and 2 (Element and Attributes)
         */
        var NODE_TYPES = {
            ELEMENT: 1,
            ATTRIBUTE: 2,
            TEXT: 3
        };

        /*
         * This is the configuration passed to the MutationObserver instance
         */
        var MUTATION_OBSERVER_OPTIONS = {
            /*
             * enables observation of attribute mutations
             */
            attributes: true,
            /*
             * instruct the observer to keep in store the former values of the mutated attributes
             */
            attributeOldValue: true,
            /*
             * enables observation of addition and removal of nodes
             */
            childList: true,
            characterData: false,
            /*
             * enables recursive lookup without which only addition and removal of DIRECT children of the observed DOM root would be collected
             */
            subtree: true
        };

        /*
         * unique instance of a MutationObserver on the body (enough since subtree:true)
         */
        var mutationObserver;

        /*
         * unique instance of a IntersectionObserver
         */
        var intersectionObserver;

        /*
         * unique instance of a custom listener for repositioning invoking the positionRegistry
         */
        var repositionListener;

        /*
         * holder of the current value of the page since previous onPageChanged event
         */
        var currentPage;

        /*
         * Component state values
         * 'added' when _componentsAddedCallback was called
         * 'destroyed' when _componentsRemovedCallback was called
         */
        var COMPONENT_STATE = {
            ADDED: 'added',
            DESTROYED: 'destroyed'
        };

        var enableExtendedView = false;

        /*
         * nullable callbacks provided to smartEditContractChangeListener for all the observed events
         */
        this._componentsAddedCallback = null;
        this._componentsRemovedCallback = null;
        this._componentResizedCallback = null;
        this._componentRepositionedCallback = null;
        this._onComponentChangedCallback = null;
        this._pageChangedCallback = null;

        /*
         * Queue used to process components when intersecting the viewport
         * {Array.<{isIntersecting: Boolean, parent: DOMElement, processed: COMPONENT_STATE}>}
         */
        this.componentsQueue = [];

        /*
         * Method used in mutationObserverCallback that extracts from mutations the list of nodes added
         * The nodes are returned within a pair along with their nullable closest smartEditComponent parent
         */
        var aggregateAddedOrRemovedNodesAndTheirParents = function(mutations, type) {
            var entries = lodash.flatten(mutations.filter(function(mutation) {
                //only keep mutations of type childList and [added/removed]Nodes
                return mutation.type === MUTATION_TYPES.CHILD_LIST.NAME && mutation[type] && mutation[type].length;
            }).map(function(mutation) {

                //the mutated child may not be smartEditComponent, in such case we return their first level smartEditComponent children
                var children = lodash.flatten(Array.prototype.slice.call(mutation[type])
                    .filter(function(node) {
                        return node.nodeType === NODE_TYPES.ELEMENT;
                    })
                    .map(function(child) {
                        return componentHandlerService.isSmartEditComponent(child) ? child : Array.prototype.slice.call(componentHandlerService.getFirstSmartEditComponentChildren(child));
                    })).sort(compareHTMLElementsPosition());

                // nodes are returned in pairs with their nullable parent
                var parents = componentHandlerService.getClosestSmartEditComponent(mutation.target);

                return children.map(function(node) {
                    return {
                        node: node,
                        parent: parents.length ? parents[0] : null
                    };
                });
            }));

            /*
             * Despite MutationObserver specifications it so happens that sometimes,
             * depending on the very way a parent node is added with its children,
             * parent AND children will appear in a same mutation. We then must only keep the parent
             * Since the parent will appear first, the filtering lodash.uniqWith will always return the parent as opposed to the child which is what we need
             */

            return lodash.uniqWith(entries, function(entry1, entry2) {
                return entry1.node.contains(entry2.node) || entry2.node.contains(entry1.node);
            });
        };

        /*
         * Method used in mutationObserverCallback that extracts from mutations the list of nodes the smartEdit contract attributes of which have changed
         * The nodes are returned within a pair along with their map of changed attributes
         */
        var aggregateMutationsOnSmartEditAttributes = function(mutations) {
            return mutations.filter(function(mutation) {
                return mutation.target && mutation.target.nodeType === NODE_TYPES.ELEMENT && componentHandlerService.isSmartEditComponent(mutation.target) && mutation.type === MUTATION_TYPES.ATTRIBUTES.NAME && smartEditAttributeNames.indexOf(mutation.attributeName) > -1;
            }).reduce(function(seed, mutation) {
                var targetEntry = seed.find(function(entry) {
                    return entry.node === mutation.target;
                });
                if (!targetEntry) {
                    targetEntry = {
                        node: mutation.target,
                        oldAttributes: {}
                    };
                    seed.push(targetEntry);
                }
                targetEntry.oldAttributes[mutation.attributeName] = mutation.oldValue;
                return seed;
            }, []);
        };

        /*
         * Methods used in mutationObserverCallback that determines whether the smartEdit contract page identifier MAY have changed in the DOM
         */
        var mutationsHasPageChange = function(mutations) {
            return mutations.find(function(mutation) {
                return mutation.type === MUTATION_TYPES.ATTRIBUTES.NAME && mutation.target.tagName === "BODY" && mutation.attributeName === "class";
            });
        };

        /*
         * convenience method to invoke a callback on a node and recursively on all its smartEditComponent children
         */
        var applyToSelfAndAllChildren = function(node, callback) {
            callback(node);
            Array.prototype.slice.call(componentHandlerService.getFirstSmartEditComponentChildren(node)).forEach(function(component) {
                applyToSelfAndAllChildren(component, callback);
            });
        };

        var repairParentResizeListener = function(parent) {
            if (parent) {
                //the adding of a component is likely to destroy the DOM added by the resizeListener on the parent, it needs be restored
                /*
                 * since the DOM hierarchy is processed in order, by the time we need repair the parent,
                 * it has already been processed so we can rely on its process status to know whether it is eligible
                 */
                var parentObj = this.componentsQueue[this._getComponentIndexInQueue(parent)];
                if (parentObj && parentObj.processed === COMPONENT_STATE.ADDED && isInDOM(parent)) {
                    resizeListener.fix(parent);
                    this._componentResizedCallback(parent);
                }
            }
        }.bind(this);

        /*
         * when a callback is executed we make sure that angular is synchronized since it is occurring outside the life cycle
         */
        var executeCallback = function(callback) {
            $rootScope.$evalAsync(callback);
        };

        /*
         * callback executed by the mutation observer every time mutations occur.
         * repositioning and resizing are not part of this except that every time a smartEditComponent is added,
         * it is registered within the positionRegistry and the resizeListener 
         */
        var mutationObserverCallback = function(mutations) {
            $log.debug(mutations);

            if (this._pageChangedCallback && mutationsHasPageChange(mutations)) {
                componentHandlerService.getPageUUID().then(function(newPageUUID) {
                    if (currentPage !== newPageUUID) {
                        executeCallback(this._pageChangedCallback.bind(undefined, newPageUUID));
                    }
                    currentPage = newPageUUID;
                }.bind(this));
            }
            if (this._componentsAddedCallback) {
                aggregateAddedOrRemovedNodesAndTheirParents(mutations, MUTATION_TYPES.CHILD_LIST.ADD_OPERATION).forEach(function(childAndParent) {
                    applyToSelfAndAllChildren(childAndParent.node, intersectionObserver.observe.bind(intersectionObserver));
                }.bind(this));
            }
            aggregateAddedOrRemovedNodesAndTheirParents(mutations, MUTATION_TYPES.CHILD_LIST.REMOVE_OPERATION).forEach(function(childAndParent) {
                applyToSelfAndAllChildren(childAndParent.node, function(node) {
                    var componentIndex = this._getComponentIndexInQueue(node);
                    if (componentIndex !== -1) {
                        if (!this.economyMode) {
                            repairParentResizeListener(childAndParent.parent);
                        }
                        this._removeComponents([{
                            isIntersecting: false,
                            component: node
                        }]);
                    }
                }.bind(this));
            }.bind(this));

            if (this._onComponentChangedCallback) { //TODO: are we missing tests here?
                aggregateMutationsOnSmartEditAttributes(mutations).forEach(function(entry) {
                    //the onComponentChanged is called with the mutated smartEditComponent subtree and the map of old attributes
                    executeCallback(this._onComponentChangedCallback.bind(undefined, entry.node, entry.oldAttributes));
                }.bind(this));
            }

        };

        /*
         * wrapping for test purposes
         */
        this._newMutationObserver = function(callback) {
            return new MutationObserver(callback);
        };

        /*
         * wrapping for test purposes
         */
        this._newIntersectionObserver = function(callback) {
            return new IntersectionObserver(callback, CONTRACT_CHANGE_LISTENER_INTERSECTION_OBSERVER_OPTIONS);
        };

        /*
         * Add the given entry to the componentsQueue
         * The components in the queue are sorted according to their position in the DOM
         * so that the adding of components is done to have parents before children
         */
        this._addToComponentQueue = function(entry) {
            var componentIndex = this._getComponentIndexInQueue(entry.target);
            if (componentIndex !== -1) {
                this.componentsQueue[componentIndex].isIntersecting = entry.isIntersecting;
            } else if (isInDOM(entry.target)) {
                this.componentsQueue.push({
                    component: entry.target,
                    isIntersecting: entry.isIntersecting,
                    processed: null,
                    oldProcessedValue: null,
                    parent: componentHandlerService.getParent(entry.target)[0]
                });
            }
        };

        this._getComponentIndexInQueue = function(component) {
            return this.componentsQueue.findIndex(function(obj) {
                return component === obj.component;
            });
        };

        /*
         * for e2e test purposes
         */
        this._componentsQueueLength = function() {
            return this.componentsQueue.length;
        };

        this.isExtendedViewEnabled = function() {
            return enableExtendedView;
        };

        /**
         * Set the 'economyMode' to true for better performance.
         * In economyMode, resize/position listeners are not present, and the current economyMode value is passed to the add /remove callbacks.
         */
        this.setEconomyMode = function(_mode) {
            this.economyMode = _mode;

            if (!this.economyMode) {
                // reactivate
                Array.prototype.slice.apply(componentHandlerService.getFirstSmartEditComponentChildren(componentHandlerService.getFromSelector('body'))).forEach(function(firstLevelComponent) {
                    applyToSelfAndAllChildren(firstLevelComponent, function(node) {
                        this._registerSizeAndPositionListeners(node);
                    }.bind(this));
                }.bind(this));
            }
        };

        /*
         * initializes and starts all Intersection/DOM listeners:
         * - Intersection of smartEditComponents with the viewport
         * - DOM mutations on smartEditComponents and page identifier (by Means of native MutationObserver)
         * - smartEditComponents repositioning (by means of querying, with an interval, the list of repositioned components from the positionRegistry)
         * - smartEditComponents resizing (by delegating to the injected resizeListener)
         */
        this.initListener = function() {

            enableExtendedView = polyfillService.isEligibleForExtendedView();

            try {
                componentHandlerService.getPageUUID().then(function(pageUUID) {
                    currentPage = pageUUID;
                    if (this._pageChangedCallback) {
                        executeCallback(this._pageChangedCallback.bind(undefined, currentPage));
                    }
                }.bind(this));
            } catch (e) {
                //case when the page that has just loaded is an asynchronous one
            }

            systemEventService.registerEventHandler(CONTRACT_CHANGE_LISTENER_PROCESS_EVENTS.RESTART_PROCESS, function() {
                this._processQueue();
                return $q.when();
            }.bind(this));

            // Intersection Observer not able to re-evaluate components that are not intersecting but going in ant out of the extended viewport.
            if (enableExtendedView) {
                $interval(function() {
                    this._processQueue();
                }.bind(this), PROCESS_QUEUE_POLYFILL_INTERVAL);
            }

            if (!mutationObserver) {
                mutationObserver = this._newMutationObserver(mutationObserverCallback.bind(this));
                mutationObserver.observe(document.body, MUTATION_OBSERVER_OPTIONS);

                if (!intersectionObserver) {
                    // Intersection Observer is used to observe intersection of components with the viewport.
                    // each time the 'isIntersecting' property of an entry (SmartEdit component) changes, the Intersection Callback is called.
                    // we are using the componentsQueue to hold the components references and their isIntersecting value.
                    intersectionObserver = this._newIntersectionObserver(function(entries) {
                        entries.forEach(function(entry) {
                            this._addToComponentQueue(entry);
                        }.bind(this));
                        // A better approach would be to process each entry individually instead of processing the whole queue, but a bug Firefox v.55 prevent us to do so.
                        this._processQueue();
                    }.bind(this));
                }

                // Observing all SmartEdit components that are already in the page.
                // Note that when an element visible in the viewport is removed, the Intersection Callback is called so we don't need to use the Mutation Observe to oberser removal of Nodes.
                Array.prototype.slice.apply(componentHandlerService.getFirstSmartEditComponentChildren(componentHandlerService.getFromSelector('body'))).forEach(function(firstLevelComponent) {
                    applyToSelfAndAllChildren(firstLevelComponent, intersectionObserver.observe.bind(intersectionObserver));
                }.bind(this));

                this._startExpendableListeners();
            }
        };

        // Processing the queue with throttling in production to avoid scrolling lag when there is a lot of components in the page.
        // No throttling when e2e mode is active
        this._processQueue = function() {
            if (testModeService.isE2EMode()) {
                this._rawProcessQueue();
            } else {
                this._throttledProcessQueue();
            }
        }.bind(this);

        this.isIntersecting = function(obj) {
            if (!isInDOM(obj.component)) {
                return false;
            }
            return enableExtendedView ? isInExtendedViewPort(obj.component) : obj.isIntersecting;
        };


        // for each component in the componentsQueue, we use the 'isIntersecting' and 'processed' values to add or remove it.
        // An intersecting component that was not already added is added, and a non intersecting component that was added is removed (happens when scrolling, resizing the page, zooming, opening dev-tools)
        // the 'PROCESS_COMPONENTS' promise is RESOLVED when the component can be added or removed, and it is REJECTED when the component can't be added but could be removed.
        this._rawProcessQueue = function() {
            systemEventService.sendSynchEvent(CONTRACT_CHANGE_LISTENER_PROCESS_EVENTS.PROCESS_COMPONENTS, lodash.map(this.componentsQueue, 'component')).then(function(response) {

                var addedComponents = [];
                var removedComponents = [];
                this.componentsQueue.forEach(function(obj) {
                    var processStatus = response.find(function(component) {
                        return component === obj.component;
                    }).dataset[SMARTEDIT_COMPONENT_PROCESS_STATUS];
                    if (processStatus === CONTRACT_CHANGE_LISTENER_COMPONENT_PROCESS_STATUS.PROCESS) {
                        if (obj.processed !== COMPONENT_STATE.ADDED && this.isIntersecting(obj)) {
                            addedComponents.push(obj);
                        } else if (obj.processed === COMPONENT_STATE.ADDED && !this.isIntersecting(obj)) {
                            removedComponents.push(obj);
                        }
                    } else if (processStatus === CONTRACT_CHANGE_LISTENER_COMPONENT_PROCESS_STATUS.REMOVE) {
                        if (obj.processed === COMPONENT_STATE.ADDED) {
                            removedComponents.push(obj);
                        }
                    }
                    obj.oldProcessedValue = obj.processed;
                }.bind(this));

                addedComponents.forEach(function(queueObj) {
                    queueObj.processed = COMPONENT_STATE.ADDED;
                }.bind(this));
                removedComponents.forEach(function(queueObj) {
                    queueObj.processed = COMPONENT_STATE.DESTROYED;
                }.bind(this));

                // If the intersection observer returns mutlple time the same components in the callback (happen when doing a drag and drop or sfBuilder.actions.rerenderComponent)
                // we will have these same components in BOTH addedComponents and removedComponents, hence we must first call _removeComponents and then _addComponents (in this order).
                if (removedComponents.length) {
                    this._removeComponents(removedComponents);
                }
                if (addedComponents.length) {
                    addedComponents.sort(compareHTMLElementsPosition('component'));
                    this._addComponents(addedComponents);
                }
                if (!this.economyMode) {
                    lodash.chain(addedComponents.concat(removedComponents)).filter(function(obj) {
                        return obj.oldProcessedValue === null || !isInDOM(obj.component);
                    }).map('parent').compact().uniq().value().forEach(function(parent) {
                        repairParentResizeListener(parent);
                    });
                }
            }.bind(this));
        };

        this._throttledProcessQueue = lodash.throttle(this._rawProcessQueue, CONTRACT_CHANGE_LISTENER_PROCESS_QUEUE_THROTTLE);

        this._addComponents = function(componentsObj) {
            if (this._componentsAddedCallback) {
                executeCallback(this._componentsAddedCallback.bind(undefined, lodash.map(componentsObj, 'component'), this.economyMode));
            }
            if (!this.economyMode) {
                componentsObj.filter(function(queueObj) {
                    return queueObj.oldProcessedValue === null;
                }).forEach(function(queueObj) {
                    this._registerSizeAndPositionListeners(queueObj.component);
                }.bind(this));
            }
        };

        this._removeComponents = function(componentsObj) {
            componentsObj.filter(function(queueObj) {
                return !isInDOM(queueObj.component);
            }).forEach(function(queueObj) {
                if (!this.economyMode) {
                    this._unregisterSizeAndPositionListeners(queueObj.component);
                }
                this.componentsQueue.splice(this._getComponentIndexInQueue(queueObj.component), 1);
            }.bind(this));
            if (this._componentsRemovedCallback) {
                var removedComponents = componentsObj.map(function(obj) {
                    return lodash.pick(obj, ['component', 'parent']);
                });
                executeCallback(this._componentsRemovedCallback.bind(undefined, removedComponents, this.economyMode));
            }
        };

        this._registerSizeAndPositionListeners = function(component) {
            if (this._componentRepositionedCallback) {
                positionRegistry.register(component);
            }
            if (this._componentResizedCallback) {
                resizeListener.register(component, this._componentResizedCallback.bind(undefined, component));
            }
        };

        this._unregisterSizeAndPositionListeners = function(component) {
            if (this._componentRepositionedCallback) {
                positionRegistry.unregister(component);
            }
            if (this._componentResizedCallback) {
                resizeListener.unregister(component);
            }
        };

        /*
         * stops and clean up all listeners
         */
        this.stopListener = function() {
            // Stop listening for DOM mutations
            if (mutationObserver) {
                mutationObserver.disconnect();
            }

            intersectionObserver.disconnect();

            mutationObserver = null;

            this._stopExpendableListeners();
        };

        this._stopExpendableListeners = function() {
            // Stop listening for DOM resize
            resizeListener.dispose();
            // Stop listening for DOM repositioning
            if (repositionListener) {
                $interval.cancel(repositionListener);
                repositionListener = null;
            }
            positionRegistry.dispose();
        };


        this._startExpendableListeners = function() {

            resizeListener.init();

            if (this._componentRepositionedCallback) {
                repositionListener = $interval(function() {
                    positionRegistry.getRepositionedComponents().forEach(function(component) {
                        this._componentRepositionedCallback(component);
                    }.bind(this));

                }.bind(this), REPROCESS_TIMEOUT);
            }
        };

        /*
         * registers a unique callback to be executed every time a smarteditComponent node is added to the DOM
         * it is executed only once per subtree of smarteditComponent nodes being added
         * the callback is invoked with the root node of a subtree
         */
        this.onComponentsAdded = function(callback) {
            this._componentsAddedCallback = callback;
        };

        /*
         * registers a unique callback to be executed every time a smarteditComponent node is removed from the DOM
         * it is executed only once per subtree of smarteditComponent nodes being removed
         * the callback is invoked with the root node of a subtree and its parent
         */
        this.onComponentsRemoved = function(callback) {
            this._componentsRemovedCallback = callback;
        };

        /*
         * registers a unique callback to be executed every time at least one of the smartEdit contract attributes of a smarteditComponent node is changed
         * the callback is invoked with the mutated node itself and the map of old attributes
         */
        this.onComponentChanged = function(callback) {
            this._onComponentChangedCallback = callback;
        };

        /*
         * registers a unique callback to be executed every time a smarteditComponent node is resized in the DOM
         * the callback is invoked with the resized node itself
         */
        this.onComponentResized = function(callback) {
            this._componentResizedCallback = callback;
        };

        /*
         * registers a unique callback to be executed every time a smarteditComponent node is repositioned (as per Node.getBoundingClientRect()) in the DOM
         * the callback is invoked with the resized node itself
         */
        this.onComponentRepositioned = function(callback) {
            this._componentRepositionedCallback = callback;
        };

        /*
         * registers a unique callback to be executed:
         * - upon bootstrapping smartEdit IF the page identifier is available
         * - every time the page identifier is changed in the DOM (see componentHandlerService.getPageUUID())
         * the callback is invoked with the new page identifier read from componentHandlerService.getPageUUID()
         */
        this.onPageChanged = function(callback) {
            this._pageChangedCallback = callback;
        };
    });
