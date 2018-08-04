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
/**
 * @ngdoc overview
 * @name treeModule
 * @description
 * <h1>This module deals with rendering and management of node trees</h1>
 */
angular.module("treeModule", ['ui.tree', 'includeReplaceModule', 'functionsModule', 'smarteditServicesModule', 'translationServiceModule', 'confirmationModalServiceModule', 'yLoDashModule', 'alertServiceModule'])
    .constant('treeConfig', {
        treeClass: 'angular-ui-tree',
        hiddenClass: 'angular-ui-tree-hidden',
        nodesClass: 'angular-ui-tree-nodes',
        nodeClass: 'angular-ui-tree-node',
        handleClass: 'angular-ui-tree-handle',
        placeholderClass: 'angular-ui-tree-placeholder',
        dragClass: 'angular-ui-tree-drag',
        dragThreshold: 3,
        levelThreshold: 30,
        defaultCollapsed: true,
        dragDelay: 200
    })
    /**
     * @ngdoc object
     * @name treeModule.object:Node
     * @description
     * A plain JSON object, representing the node of a tree managed by the {@link treeModule.directive:ytree ytree} directive.
     */
    /**
     * @ngdoc property
     * @name uid
     * @propertyOf treeModule.object:Node
     * @description
     * the unique identifier of a node for the given catalog. Optional upon posting.
     **/
    /**
     * @ngdoc property
     * @name name
     * @propertyOf treeModule.object:Node
     * @description
     * the non localized node name. Required upon posting.
     **/
    /**
     * @ngdoc property
     * @name parentUid
     * @propertyOf treeModule.object:Node
     * @description
     * the unique identifier of the parent node for the given catalog. Required upon posting.
     **/
    /**
     * @ngdoc property
     * @name hasChildren
     * @propertyOf treeModule.object:Node
     * @description
     * boolean specifying whether the retrieved node has children. This is read only and ignored upon saving.
     **/

    /**
    	 * @ngdoc service
    	 * @name treeModule.service:TreeService
    	 *
    	 * @description
    	 * A class to manage tree nodes through a REST API.
    	 * @constructs treeModule.TreeService
    	 * @param {string} nodeUri the REST entry point to handle tree nodes. it must support GET, POST, PUT and DELETE verbs:
    	 * - GET nodeUri?parentUid={parentUid} will return a list of children {@link treeModule.object:Node nodes}  wrapped in an object:
    	 * <pre>
    	    {
    		    navigationNodes:[{
    	            uid: "2",
    	            name: "node2",
    	            parentUid: "root"
    	            hasChildren: true
    	        }, {
    	            uid: "4",
    	            name: "node4",
    	            parentUid: "1",
    	            hasChildren: false
    	        }]
    		}
    	 * </pre>
    	 * - POST nodeUri takes a {@link treeModule.object:Node Node} payload and returns the final object.
    	 * - PUT nodeUri/{uid} takes a {@link treeModule.object:Node Node} payload and returns the final object.
    	 * - DELETE nodeUri/{uid} 
    	 */

    .factory("TreeService", function($q, $log, restServiceFactory, getDataFromResponse) {

        var TreeService = function(nodeUri) {
            if (nodeUri) {
                this.nodesRestService = restServiceFactory.get(nodeUri);
            }
        };

        /**
         * @ngdoc method
         * @name treeModule.service:TreeService#fetchChildren
         * @methodOf treeModule.service:TreeService
         * @description
         * Will fetch the children of a given node by querying GET nodeUri?parentUid={parentUid}
         * - Once the children retrieved, the node will be marked as "initiated" and subsequent calls will not hit the server.
         * - Each children will be given a ManyToOne reference to their parent.
         * - The parent nodes will be assigned its children through the "nodes" property.
         * @param {Object} parent the parent {@link treeModule.object:Node node} object the nodes of which we want to fetch
         */
        TreeService.prototype.fetchChildren = function(_parent) {
            _parent.nodes = _parent.nodes || [];
            if (_parent.initiated) {
                return $q.when(_parent.nodes);
            } else {
                return this.nodesRestService.get({
                    parentUid: _parent.uid
                }).then(function(response) {

                    _parent.initiated = true;

                    var children = getDataFromResponse(response);

                    if (!children) {
                        $log.error('No children found for node: ' + _parent.uid + ' while building yTree.');
                        return [];
                    }

                    children.forEach(function(child) {
                        child.parent = _parent;
                    });

                    Array.prototype.push.apply(_parent.nodes, children);
                    return children;
                });
            }

        };
        /**
         * @ngdoc method
         * @name treeModule.service:TreeService#saveNode
         * @methodOf treeModule.service:TreeService
         * @description
         * Will save a new node for the given parent by POSTing to nodeUri. The payload will only contain the parentUid and a generated name.
         * On the front end side the parent model will be marked as having children.
         * @param {Object} parent the parent {@link treeModule.object:Node node} object from which to create a child
         */
        TreeService.prototype.saveNode = function(_parent) {
            return this.nodesRestService.save({
                parentUid: _parent.uid,
                name: (_parent.name ? _parent.name : _parent.uid) + _parent.nodes.length
            }).then(function(response) {
                _parent.hasChildren = true;
                response.parent = _parent;
                return response;
            });
        };
        /**
         * @ngdoc method
         * @name treeModule.service:TreeService#removeNode
         * @methodOf treeModule.service:TreeService
         * @description
         * Will delete a node by sending DELETE to nodeUri/{uid}.
         * On the front end side the parent model "hasChildren" will be re-evaluated.
         * @param {Object} node the {@link treeModule.object:Node node} object to delete.
         */
        TreeService.prototype.removeNode = function(node) {
            return this.nodesRestService.remove({
                identifier: node.uid
            }).then(function() {
                var parent = node.parent;
                parent.hasChildren = parent.nodes.length > 1;
                return;
            });
        };

        return TreeService;
    })

    /**
     * @ngdoc controller
     * @name treeModule.controller:YTreeController
     * @description
     * Extensible controller of the {@link treeModule.directive:ytree ytree} directive
     */
    .controller("YTreeController", function($scope, $q, TreeService, _TreeDndOptions) {

        /**
         * @ngdoc method
         * @name treeModule.controller:YTreeController#collapseAll
         * @methodOf treeModule.controller:YTreeController
         * @description
         * Causes all the nodes of the tree to collapse.
         * It does not affect their "initiated" status though.
         */
        this.collapseAll = function() {
            $scope.$broadcast('angular-ui-tree:collapse-all');
        };

        this.expandAll = function() {
            $scope.$broadcast('angular-ui-tree:expand-all');
        };

        /**
         * @ngdoc method
         * @name treeModule.controller:YTreeController#hasChildren
         * @methodOf treeModule.controller:YTreeController
         * @description
         * Return a boolean to determine if the node is expandable or not by checking if a given node has children
         * @param {Object} handle the native {@link https://github.com/angular-ui-tree/angular-ui-tree angular-ui-tree} handle on a given {@link treeModule.object:Node node}
         */
        this.hasChildren = function(handle) {
            var nodeData = handle.$modelValue;
            return nodeData.hasChildren;
        };

        this.fetchData = function(nodeData) {
            return this.treeService.fetchChildren(nodeData);
        };

        /**
         * @ngdoc method
         * @name treeModule.controller:YTreeController#toggleAndfetch
         * @methodOf treeModule.controller:YTreeController
         * @description
         * Will toggle a {@link treeModule.object:Node node}, causing a fetch from server if expanding for the first time.
         * @param {Object} handle the native {@link https://github.com/angular-ui-tree/angular-ui-tree angular-ui-tree} handle on a given {@link treeModule.object:Node node}
         */
        this.toggleAndfetch = function(handle) {
            this.isDisabled = true;
            var nodeData = handle.$modelValue;
            if (handle.collapsed) {
                return this.fetchData(nodeData).then(function() {
                    handle.toggle();
                    this.isDisabled = false;
                }.bind(this));
            } else {
                handle.toggle();
                this.isDisabled = false;
                return $q.when();
            }

        };

        /**
         * @ngdoc method
         * @name treeModule.controller:YTreeController#refresh
         * @methodOf treeModule.controller:YTreeController
         * @description
         * Will refresh a node, causing it to expand after fetch if it was expanded before.
         */
        this.refresh = function(handle) {
            var nodeData = handle.$modelValue;
            nodeData.initiated = false;
            var previousCollapsed = handle.collapsed;
            return this.fetchData(nodeData).then(function() {
                if (!previousCollapsed && handle.collapsed) {
                    handle.toggle();
                }
            });
        };

        /**
         * @ngdoc method
         * @name treeModule.controller:YTreeController#refreshParent
         * @methodOf treeModule.controller:YTreeController
         * @description
         * Will refresh the parent of a node, causing it to expand after fetch if it was expanded before.
         */
        this.refreshParent = function(handle) {
            if (handle.$modelValue.parent.uid === this.root.uid) {
                this.fetchData(this.root);
            } else {
                this.refresh(handle.$parentNodeScope);
            }
        };
        /**
         * @ngdoc method
         * @name treeModule.controller:YTreeController#newChild
         * @methodOf treeModule.controller:YTreeController
         * @description
         * Will add a new child to the node referenced by this handle.
         * <br/>The child is added only if {@link treeModule.service:TreeService#methods_saveNode saveNode} from {@link treeModule.service:TreeService TreeService} is successful.
         * @param {Object} handle the native {@link https://github.com/angular-ui-tree/angular-ui-tree angular-ui-tree} handle on a given {@link treeModule.object:Node node}
         */
        this.newChild = function(handle) {

            var nodeData = handle.$modelValue ? handle.$modelValue : this.root;
            nodeData.nodes = nodeData.nodes || [];

            this.treeService.saveNode(nodeData).then(function(response) {
                (handle.collapsed ? this.toggleAndfetch(handle) : $q.when()).then(function() {
                    var elm = nodeData.nodes.find(function(node) {
                        return node.uid === response.uid;
                    });
                    if (!elm) { //if children list already initiated, one needs to push to the list on the ui side
                        nodeData.nodes.push(response);
                    }
                });
            }.bind(this));
        };

        /**
         * @ngdoc method
         * @name treeModule.controller:YTreeController#newSibling
         * @methodOf treeModule.controller:YTreeController
         * @description
         * Will add a new sibling to the node referenced by this handle.
         * <br/>The sibling is added only if {@link treeModule.service:TreeService#methods_saveNode saveNode} from {@link treeModule.service:TreeService TreeService} is successful.
         * @param {Object} handle the native {@link https://github.com/angular-ui-tree/angular-ui-tree angular-ui-tree} handle on a given {@link treeModule.object:Node node}
         */
        this.newSibling = function(handle) {

            var nodeData = handle.$modelValue;
            var parent = nodeData.parent;
            this.treeService.saveNode(parent).then(function(response) {
                parent.nodes.push(response);
            }.bind(this));

        };

        /**
         * @ngdoc method
         * @name treeModule.controller:YTreeController#remove
         * @methodOf treeModule.controller:YTreeController
         * @description
         * Will remove the node referenced by this handle.
         * <br/>The node is removed only if {@link treeModule.service:TreeService#methods_removeNode removeNode} from {@link treeModule.service:TreeService TreeService} is successful.
         * @param {Object} handle the native {@link https://github.com/angular-ui-tree/angular-ui-tree angular-ui-tree} handle on a given {@link treeModule.object:Node node}
         */
        this.remove = function(handle) {

            var nodeData = handle.$modelValue;
            this.treeService.removeNode(nodeData).then(function() {
                var parent = nodeData.parent;
                parent.nodes.splice(parent.nodes.indexOf(nodeData), 1);
                parent.initiated = false;
                delete parent.nodes;
                this.fetchData(parent);
            }.bind(this));

        };


        this.isRoot = function(handle) {
            var nodeData = handle.$modelValue;
            return nodeData.parent.uid === undefined;
        };

        this.displayDefaultTemplate = function() {
            return !this.removeDefaultTemplate;
        };

        this.onNodeMouseEnter = function(node) {
            node.mouseHovered = true;
        };

        this.onNodeMouseLeave = function(node) {
            node.mouseHovered = false;
        };

        /**
         * @ngdoc method
         * @name treeModule.controller:YTreeController#getNodeById
         * @methodOf treeModule.controller:YTreeController
         * @description
         * Will fetch from the existing tree the node whose identifier is the given nodeUid
         * @param {String} nodeUid the identifier of the node to fetched
         */
        this.getNodeById = function(nodeUid, nodeArray) {
            if (!nodeArray) {
                nodeArray = this.root.nodes;
            }
            if (nodeUid === this.rootNodeUid) {
                return this.root;
            } else {

                for (var i in nodeArray) {
                    if (nodeArray[i].uid === nodeUid) {
                        return nodeArray[i];
                    }
                    if (nodeArray[i].hasChildren) {
                        nodeArray[i].nodes = nodeArray[i].nodes || [];
                        var result = this.getNodeById(nodeUid, nodeArray[i].nodes);
                        if (result) {
                            return result;
                        }
                    }
                }

            }
        };

        this.$onInit = function() {
            this.treeOptions = new _TreeDndOptions(this.dragOptions);

            this.treeService = new TreeService(this.nodeUri);

            this.root = {
                uid: this.rootNodeUid
            };

            Object.keys(this.nodeActions).forEach(function(functionName) {
                this[functionName] = this.nodeActions[functionName].bind(this, this.treeService);
                this.nodeActions[functionName] = this.nodeActions[functionName].bind(this, this.treeService);
            }.bind(this));


            this.fetchData(this.root);
        };

    })
    .factory('_TreeDndOptions', function($translate, treeConfig, confirmationModalService, lodash, $q, alertService) {
        function TreeDndOptions(options) {
            this.dragEnabled = false;
            this.dragDelay = treeConfig.dragDelay;
            this.callbacks = {};

            if (lodash.isNil(options)) {
                return;
            }

            var optionsType = typeof options;
            if (optionsType !== 'object') {
                throw "Unexpected type for dragOptions, expected object but got " + optionsType;
            }

            if (options.hasOwnProperty('onDropCallback')) {
                var onDropCallbackType = typeof options.onDropCallback;
                if (onDropCallbackType !== 'function') {
                    throw "Unexpected type for onDropCallback, expected function but got " + onDropCallbackType;
                }
                this.dragEnabled = true;
                this.callbacks.dropped = function(event) {
                    if (event.source === null || event.dest === null) {
                        return;
                    }
                    var dndEvent = new yTreeDndEvent(event.source.nodeScope.$modelValue, event.dest.nodesScope.$modelValue, event.dest.index, event.source.nodeScope.$parentNodeScope, event.dest.nodesScope.$nodeScope);
                    options.onDropCallback(dndEvent);
                };
            }

            if (options.hasOwnProperty('beforeDropCallback')) {
                var beforeDropCallbackType = typeof options.beforeDropCallback;
                if (beforeDropCallbackType !== 'function') {
                    throw "Unexpected type for beforeDropCallback, expected function but got " + beforeDropCallbackType;
                }
                this.dragEnabled = true;
                this.callbacks.beforeDrop = function(event) {
                    if (event.source === null || event.dest === null) {
                        return true;
                    }
                    var dndEvent = new yTreeDndEvent(event.source.nodeScope.$modelValue, event.dest.nodesScope.$modelValue, event.dest.index);
                    var condition = options.beforeDropCallback(dndEvent);
                    return $q.when(condition).then(function(result) {
                        if (typeof result === 'object') {
                            if (result.hasOwnProperty('confirmDropI18nKey')) {
                                var message = {
                                    description: result.confirmDropI18nKey
                                };
                                return confirmationModalService.confirm(message);
                            }
                            if (result.hasOwnProperty('rejectDropI18nKey')) {
                                alertService.showDanger({
                                    message: result.rejectDropI18nKey
                                });
                                return false;
                            }
                            throw "Unexpected return value for beforeDropCallback does not contain confirmDropI18nKey nor rejectDropI18nKey: " + result;
                        }
                        return result;
                    });
                };
            }

            if (options.hasOwnProperty('allowDropCallback')) {
                var allowDropCallbackType = typeof options.allowDropCallback;
                if (allowDropCallbackType !== 'function') {
                    throw "Unexpected type for allowDropCallback, expected function but got " + allowDropCallbackType;
                }
                this.dragEnabled = true;
                this.callbacks.accept = function(sourceNodeScope, destNodesScope, destIndex) {
                    var dndEvent = new yTreeDndEvent(sourceNodeScope.$modelValue, destNodesScope.$modelValue, destIndex);
                    return options.allowDropCallback(dndEvent);
                };
            }

            /**
             * @ngdoc object
             * @name treeModule.object:yTreeDndEvent
             * @description
             * A plain JSON object, representing the event triggered when dragging and dropping nodes in the {@link treeModule.directive:ytree ytree} directive.
             *
             * @param {Object} sourceNode is the {@link treeModule.object:Node node} that is being dragged.
             * @param {Object} destinationNodes is the set of the destination's parent's children {@link treeModule.object:Node nodes}.
             * @param {Number} position is the index at which the {@link treeModule.object:Node node} was dropped.
             *
             **/
            /**
             * @ngdoc property
             * @name sourceNode
             * @propertyOf treeModule.object:yTreeDndEvent
             * @description
             * the {@link treeModule.object:Node node} being dragged
             **/
            /**
             * @ngdoc property
             * @name destinationNodes
             * @propertyOf treeModule.object:yTreeDndEvent
             * @description
             * array of siblings {@link treeModule.object:Node nodes} to the location drop location
             **/
            /**
             * @ngdoc property
             * @name position
             * @propertyOf treeModule.object:yTreeDndEvent
             * @description
             * the index at which {@link treeModule.object:Node node} was dropped amongst its siblings
             **/
            /**
             * @ngdoc property
             * @name sourceParentHandle
             * @propertyOf treeModule.object:yTreeDndEvent
             * @description
             * the  UI handle of the parent node of the source element
             **/
            /**
             * @ngdoc property
             * @name targetParentHandle
             * @propertyOf treeModule.object:yTreeDndEvent
             * @description
             * the UI handle of the targeted parent element
             **/
            function yTreeDndEvent(sourceNode, destinationNodes, position, sourceParentHandle, targetParentHandle) {
                this.sourceNode = sourceNode;
                this.destinationNodes = destinationNodes;
                this.position = position;
                this.sourceParentHandle = sourceParentHandle;
                this.targetParentHandle = targetParentHandle;
            }

        }
        return TreeDndOptions;
    })
    /**
    	 * @ngdoc directive
    	 * @name treeModule.directive:ytree
    	 * @scope
    	 * @restrict E
    	 * @element ytree
    	 *
    	 * @description
    	 * This directive renders a tree of nodes and manages CRUD operations around the nodes.
    	 * <br/>It relies on {@link https://github.com/angular-ui-tree/angular-ui-tree angular-ui-tree} third party library
    	 * <br/>Its behaviour is defined by {@link treeModule.controller:YTreeController YTreeController} controller
    	 * @param {String} nodeTemplateUrl an HTML node template to be included besides each node to enhance rendering and behaviour of the tree. This template may use the nodeActions defined hereunder.
    	 * @param {String} nodeUri the REST entry point to be used to manage the nodes (GET, POST, PUT and DELETE).
    	 * @param {Object} dragOptions a map of callback functions to customize the drag and drop behaviour of the tree by exposing the {@link treeModule.object:yTreeDndEvent yTreeDndEvent}.
    	 * @param {String} nodeActions a map of methods to be closure-bound to the {@link treeModule.controller:YTreeController controller} of the directive in order to manage the tree from the parent scope or from the optional node template.
    	 * <br/> All nodeActions methods must take {@link treeModule.service:TreeService treeService} instance as first parameter.
    	 * <br/> {@link treeModule.service:TreeService treeService} instance will then prebound in the closure made available in the node template or in the parent scope.
    	 * <br/> Example in a parent controller:
    	 * <pre>
    	 	this.actions = {

                myMethod: function(treeService, arg1, arg2) {
            		//some action expecting 'this' 
            		//to be the YTreeController
                    this.newChild(this.root.nodes[0]);
                }
            };
    	 * </pre>
    	 * passed to the directive through:
    	 * <pre>
    	 	<ytree data-node-uri='ctrl.nodeURI' data-node-template-url='ctrl.nodeTemplateUrl' data-node-actions='ctrl.actions'/>
    	 * </pre>
    	 * And in the HTML node template you may invoke it this way:
    	 * <pre>
    	 	<button data-ng-click="ctrl.myMethod('arg1', 'arg2')">my action</button>
    	 * </pre>
    	 * or from the parent controller:
    	 * <pre>
    	 	<button data-ng-click="ctrl.actions.myMethod('arg1', 'arg2')">my action</button>
    	 * </pre>
    	 */
    /**
     * @ngdoc object
     * @name treeModule.object:dragOptions
     * @description
     * A JSON object holding callbacks related to nodes drag and drop functionality in the {@link treeModule.directive:ytree ytree} directive.
     * Each callback exposes the {@link treeModule.object:yTreeDndEvent yTreeDndEvent}
     **/
    /**
     * @ngdoc property
     * @name onDropCallback
     * @propertyOf treeModule.object:dragOptions
     * @description
     * Callback function executed after the node is dropped.
     **/
    /**
     * @ngdoc property
     * @name beforeDropCallback
     * @propertyOf treeModule.object:dragOptions
     * @description
     * Callback function executed before drop. Return true allows drop, false rejects, and an object {confirmDropI18nKey: 'key'} opens a confirmation modal.
     **/
    /**
     * @ngdoc property
     * @name acceptDropCallback
     * @propertyOf treeModule.object:dragOptions
     * @description
     * Callback function executed when hovering over droppable slots, return true to allow, return false to block.
     **/
    .directive('ytree', function() {

        return {
            restrict: 'E',
            transclude: false,
            replace: false,
            templateUrl: 'treeTemplate.html',
            controller: 'YTreeController',
            controllerAs: 'ctrl',
            scope: {},
            bindToController: {
                nodeTemplateUrl: '=',
                nodeUri: '=',
                nodeActions: '=',
                rootNodeUid: '=',
                dragOptions: '=',
                removeDefaultTemplate: '=?',
                showAsList: '=?'
            }
        };
    });
