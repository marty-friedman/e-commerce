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
(function() {
    angular.module("navigationEditorModule", [
            'functionsModule',
            'navigationEditorNodeServiceModule',
            'resourceLocationsModule',
            'navigationNodeEditorModalServiceModule',
            'navigationEntryItemServiceModule',
            'confirmationModalServiceModule',
            'resourceModule',
            'eventServiceModule',
            'yLoDashModule'
        ])
        .constant('NAVIGATION_NODE_ROOT_NODE_UID', 'root')
        .controller("navigationEditorController", function($q, $translate, lodash, URIBuilder, NAVIGATION_MANAGEMENT_RESOURCE_URI, NAVIGATION_NODE_ROOT_NODE_UID, TreeService, navigationEditorNodeService, navigationNodeEditorModalService, navigationEntryItemService, confirmationModalService, navigationNodeRestService, systemEventService, CONTEXT_SITE_ID, CONTEXT_CATALOG_VERSION, NODE_CREATION_EVENT) {

            this.$onInit = function() {

                this.readOnly = this.readOnly || false;

                if (!this.readOnly) {
                    this.dragOptions = {
                        onDropCallback: function(event) {
                            this.actions.dragAndDrop(event);
                        }.bind(this),
                        allowDropCallback: function(event) {
                            if (event.sourceNode.parent.uid !== event.destinationNodes[0].parent.uid) {
                                return false;
                            }

                            if (event.position < event.destinationNodes.length && !_sameNature(event.destinationNodes[event.position], event.sourceNode)) {
                                return false;
                            }
                            return true;
                        },
                        beforeDropCallback: function(event) {
                            if (event.sourceNode.parent.uid !== event.destinationNodes[0].parent.uid) {
                                return {
                                    confirmDropI18nKey: 'se.cms.navigationmanagement.navnode.confirmation'
                                };
                            } else {
                                return true;
                            }
                        }

                    };
                }

                this.readOnlyErrorKey = "navigation.in.readonly.mode";

                this.nodeTemplateUrl = 'navigationNodeRenderTemplate.html';

                this.nodeURI = new URIBuilder(NAVIGATION_MANAGEMENT_RESOURCE_URI).replaceParams(this.uriContext).build();

                this.rootNodeUid = this.rootNodeUid || NAVIGATION_NODE_ROOT_NODE_UID;

                var uriContext = this.uriContext;

                var dropdownItems = [{
                    key: 'se.cms.navigationmanagement.navnode.edit',
                    callback: function(handle) {
                        this.actions.editNavigationNode(handle);
                    }.bind(this)
                }, {
                    key: 'se.cms.navigationmanagement.navnode.removenode',
                    callback: function(handle) {
                        this.actions.removeItem(handle);
                    }.bind(this)
                }, {
                    key: 'se.cms.navigationmanagement.navnode.move.up',
                    condition: function(handle) {
                        return this.actions.isMoveUpAllowed(handle);
                    }.bind(this),
                    callback: function(handle) {
                        this.actions.moveUp(handle);
                    }.bind(this)
                }, {
                    key: 'se.cms.navigationmanagement.navnode.move.down',
                    condition: function(handle) {
                        return this.actions.isMoveDownAllowed(handle);
                    }.bind(this),
                    callback: function(handle) {
                        this.actions.moveDown(handle);
                    }.bind(this)
                }, {
                    key: 'se.cms.navigationmanagement.navnode.addchild',
                    condition: function(handle) {
                        return !handle.$modelValue.itemId;
                    },
                    callback: function(handle) {
                        this.actions.addNewChild(handle);
                    }.bind(this)
                }, {
                    key: 'se.cms.navigationmanagement.navnode.addsibling',
                    condition: function(handle) {
                        return !handle.$modelValue.itemId;
                    },
                    callback: function(handle) {
                        this.actions.addNewSibling(handle);
                    }.bind(this)
                }];

                //those functions will be closure bound inside ytree
                this.actions = {

                    isReadOnly: function() {
                        return this.readOnly;
                    }.bind(this),

                    hasChildren: function(treeService, handle) {
                        var nodeData = handle.$modelValue;
                        return nodeData.hasChildren || !lodash.isEmpty(nodeData.entries);
                    },

                    fetchData: function(treeService, nodeData) {
                        if (nodeData.initiated) {
                            return $q.when(nodeData.nodes);
                        } else {

                            //need to fetch entries of the node used as root since it was not initialized but only if it is not the absolute root
                            var promiseReturningTargetNodeEntries = (this.rootNodeUid === nodeData.uid && this.rootNodeUid !== NAVIGATION_NODE_ROOT_NODE_UID) ? navigationEditorNodeService.getNavigationNode(this.rootNodeUid, this.uriContext).then(function(response) {
                                lodash.assign(nodeData, response);
                                return response.entries;
                            }) : $q.when(nodeData.entries || []);

                            return promiseReturningTargetNodeEntries.then(function(entries) {
                                return navigationEntryItemService.finalizeNavigationEntries(entries, uriContext, true).then(function() {
                                    nodeData.nodes = [];
                                    entries.forEach(function(entry) {
                                        entry.parent = nodeData;
                                        nodeData.nodes.push(entry);
                                    });

                                    return treeService.fetchChildren(nodeData);
                                });
                            });
                        }

                    }.bind(this),


                    removeItem: function(treeService, handle) {

                        if (this.readOnly) {
                            throw this.readOnlyErrorKey;
                        }

                        var nodeData = handle.$modelValue;
                        var message = {};
                        message.description = nodeData.itemId ? "se.cms.navigationmanagement.navnode.removeentry.confirmation.message" : "se.cms.navigationmanagement.navnode.removenode.confirmation.message";
                        message.title = nodeData.itemId ? "se.cms.navigationmanagement.navnode.removeentry.confirmation.title" : "se.cms.navigationmanagement.navnode.removenode.confirmation.title";

                        confirmationModalService.confirm(message).then(function() {
                            if (!nodeData.itemId) {
                                this.remove(handle);
                            } else {

                                var parent = lodash.cloneDeep(nodeData.parent);
                                parent.entries = parent.entries
                                    .filter(function(entry) {
                                        return entry.id !== nodeData.id;
                                    })
                                    .map(function(entry) {
                                        delete entry.parent;
                                        delete entry.title;
                                        delete entry.id;
                                        return entry;
                                    });

                                var payload = angular.extend({
                                    identifier: parent.uid
                                }, uriContext, parent);
                                delete payload.parent;
                                delete payload.title;
                                delete payload.nodes;
                                navigationNodeRestService.update(payload).then(function() {
                                    var par = nodeData.parent;
                                    par.entries = par.entries
                                        .filter(function(entry) {
                                            return entry.id !== nodeData.id;
                                        })
                                        .map(function(entry, index) {
                                            entry.position = index;
                                            return entry;
                                        });
                                    this.refreshParentNode(handle);
                                }.bind(this));
                            }
                        }.bind(this));

                    },

                    performMove: function(treeService, nodeData, handle, refreshNodeItself) {
                        if (this.readOnly) {
                            throw this.readOnlyErrorKey;
                        }

                        return navigationEditorNodeService.updateNavigationNode(nodeData, uriContext).then(function() {

                            if (!handle) {
                                this.fetchData(this.root);
                            } else if (refreshNodeItself) {
                                this.refreshNode(handle);
                            } else {
                                this.refreshParentNode(handle);
                            }
                        }.bind(this));

                    },
                    dragAndDrop: function(treeService, event) {
                        var nodeData = event.sourceNode.itemId ? event.sourceNode.parent : event.sourceNode;
                        var destinationNodes = event.destinationNodes;

                        if (event.sourceNode.itemId) {
                            nodeData.entries = nodeData.nodes.filter(function(node) {
                                return node.itemId;
                            });
                        } else {
                            var offset = _recalculatePositionBasedOnNodesOfSameType(nodeData, destinationNodes, event.position);
                            var position = event.position - offset;

                            var destinationParent = (lodash.find(destinationNodes, function(node) {
                                return node.uid !== nodeData.uid;
                            })).parent;

                            if (_hasNotMoved(nodeData, event.position, destinationParent)) {
                                return;
                            }

                            nodeData.position = position;
                            nodeData.parentUid = destinationParent.uid;
                            nodeData.parent = destinationParent;
                        }
                        this.performMove(nodeData, event.targetParentHandle, true).then(function() {
                            if (event.sourceParentHandle !== event.targetParentHandle) {
                                this.refreshNode(event.sourceParentHandle);
                            }
                        }.bind(this));
                    },
                    moveUp: function(treeService, handle) {

                        if (this.readOnly) {
                            throw this.readOnlyErrorKey;
                        }

                        var nodeData = handle.$modelValue;
                        var parent = nodeData.parent;

                        if (nodeData.itemId) {
                            var pos = parent.entries.indexOf(nodeData);
                            var upperEntry = parent.entries[pos - 1];
                            parent.entries.splice(pos - 1, 2, nodeData, upperEntry);
                            this.performMove(parent, handle);
                        } else {
                            nodeData.position = parseInt(nodeData.position) - 1;
                            this.performMove(nodeData, handle);
                        }

                    },

                    moveDown: function(treeService, handle) {

                        if (this.readOnly) {
                            throw this.readOnlyErrorKey;
                        }

                        var nodeData = handle.$modelValue;
                        var parent = nodeData.parent;

                        if (nodeData.itemId) {
                            var pos = parent.entries.indexOf(nodeData);
                            var lowerEntry = parent.entries[pos + 1];
                            parent.entries.splice(pos, 2, lowerEntry, nodeData);
                            this.performMove(parent, handle);
                        } else {
                            nodeData.position = parseInt(nodeData.position) + 1;
                            this.performMove(nodeData, handle);
                        }

                    },

                    isMoveUpAllowed: function(treeService, handle) {

                        var nodeData = handle.$modelValue;
                        if (nodeData.itemId) {
                            return nodeData.parent.entries.indexOf(nodeData) > 0;
                        } else {
                            return parseInt(nodeData.position) !== 0;
                        }

                    },

                    isMoveDownAllowed: function(treeService, handle) {

                        var nodeData = handle.$modelValue;

                        if (nodeData.itemId) {
                            var entriesArrayLength = nodeData.parent.entries.length;

                            return nodeData.parent.entries.indexOf(nodeData) !== (entriesArrayLength - 1);
                        } else {
                            var nodesArrayLength = nodeData.parent.nodes.filter(function(node) {
                                return !node.itemId;
                            }).length;

                            return parseInt(nodeData.position) !== (nodesArrayLength - 1);

                        }
                    },

                    refreshNode: function(treeService, handle) {
                        return this.refresh(handle);
                    },
                    refreshParentNode: function(treeService, handle) {
                        return this.refreshParent(handle);
                    },

                    editNavigationNode: function(treeService, handle) {
                        var nodeData = handle.$modelValue;
                        var target = {};
                        //to differentiate between the edit of node and entry
                        if (handle.$modelValue.itemType) {
                            target.nodeUid = nodeData.parent.uid;
                            target.entryIndex = nodeData.parent.entries.indexOf(nodeData);
                        } else {
                            target.nodeUid = nodeData.uid;
                            target.entryIndex = undefined;
                        }
                        return navigationNodeEditorModalService.openNodeEditor(target, this.uriContext).then(function() {
                            var target;

                            if (nodeData.parent.uid === NAVIGATION_NODE_ROOT_NODE_UID) {
                                target = nodeData;
                            } else {
                                target = nodeData.parent;
                            }

                            return navigationEditorNodeService.getNavigationNode(target.uid, this.uriContext).then(function(refreshedNode) {
                                lodash.assign(target, refreshedNode);
                                if (nodeData.parent.uid === NAVIGATION_NODE_ROOT_NODE_UID) {
                                    return this.actions.refreshNode(handle);
                                } else {
                                    return this.actions.refreshParentNode(handle);
                                }
                            }.bind(this));
                        }.bind(this));
                    }.bind(this),


                    addTopLevelNode: function() {
                        return this.addNewChild().then(function() {
                            this.fetchData(this.rootNodeUid);
                        }.bind(this));
                    },

                    addNewChild: function(treeService, handle) {
                        return this.actions._expandIfNeeded(handle).then(function() {
                            return navigationNodeEditorModalService.openNodeEditor({
                                parentUid: handle ? handle.$modelValue.uid : this.rootNodeUid
                            }, this.uriContext);
                        }.bind(this));
                    }.bind(this),

                    addNewSibling: function(treeService, handle) {
                        var nodeData = handle.$modelValue;
                        return navigationNodeEditorModalService.openNodeEditor({
                            parentUid: nodeData.parent.uid
                        }, this.uriContext);
                    }.bind(this),

                    getDropdownItems: function() {
                        return dropdownItems;
                    },

                    _findNodeById: function(treeService, nodeUid) {
                        return this.getNodeById(nodeUid);
                    },
                    _expandIfNeeded: function(treeServic, handle) {
                        return handle && handle.collapsed ? this.toggleAndfetch(handle) : $q.when();
                    }

                };

                systemEventService.registerEventHandler(NODE_CREATION_EVENT, _nodeCreationEventHandler);
            };

            var _nodeCreationEventHandler = function(eventId, newNode) {
                var parent = this.actions._findNodeById(newNode.parentUid);
                if (parent && !parent.nodes.find(function(node) {
                        return node.uid === newNode.uid;
                    })) {
                    newNode.parent = parent;
                    parent.nodes = parent.nodes || [];
                    parent.nodes.push(newNode);
                    parent.hasChildren = parent.nodes.length > 0;
                }
                return $q.when();
            }.bind(this);



            this.$onDestroy = function() {
                systemEventService.unRegisterEventHandler(NODE_CREATION_EVENT, _nodeCreationEventHandler);
            };

        })
        /**
         * @ngdoc directive
         * @name navigationEditorModule.directive:navigationEditor
         * @scope
         * @restrict E
         * @element ANY
         *
         * @description
         * Navigation Editor directive used to display navigation editor tree
         * @param {Object} uriContext the {@link resourceLocationsModule.object:UriContext UriContext} necessary to perform operations
         * @param {Boolean} readOnly when true, no CRUD facility shows on the editor. OPTIONAL, default false.
         * @param {String} rootNodeUid the uid of the node to be taken as root, OPTIONAL, default "root"
         */
        .directive('navigationEditor', function() {

            return {
                restrict: 'E',
                transclude: false,
                replace: false,
                templateUrl: 'navigationEditorTemplate.html',
                controller: 'navigationEditorController',
                controllerAs: 'nav',
                scope: {},
                bindToController: {
                    uriContext: '<',
                    readOnly: '<?',
                    rootNodeUid: '<?'
                }
            };
        });

    function _isEntry(element) {
        return element.hasOwnProperty('itemId');
    }

    function _recalculatePositionBasedOnNodesOfSameType(source, destinationNodes, eventPosition) {
        var sourceIsEntry = _isEntry(source);
        var offset = 0;
        for (var i = 0; i < eventPosition; i++) {
            if (_isEntry(destinationNodes[i]) !== sourceIsEntry) {
                offset++;
            }
        }
        return offset;
    }

    function _hasNotMoved(source, destinationPosition, destinationParent) {
        return source.position === destinationPosition && source.parentUid === destinationParent.uid;
    }

    function _sameNature(source, target) {
        return (source.itemId && target.itemId) || (!source.itemId && !target.itemId);
    }


})();
