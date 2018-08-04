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
angular.module('navigationEditorNodeServiceModule', ['resourceModule', 'yLoDashModule', 'functionsModule'])
    .service('_nodeAncestryService', function(lodash) {

        this._fetchAncestors = function(sourceArray, uid) {
            var parent = sourceArray.find(function(element) {
                return element.uid === uid;
            });
            if (parent) {
                return [parent].concat(this._fetchAncestors(sourceArray, parent.parentUid));
            } else {
                return [];
            }
        };

        this.buildOrderedListOfAncestors = function(sourceArray, uid) {
            var ancestry = lodash.reverse(this._fetchAncestors(sourceArray, uid));
            var level = -1;
            return ancestry.map(function(node) {
                var nextLevel = ++level;
                return lodash.assign(lodash.cloneDeep(node), {
                    level: nextLevel,
                    formattedLevel: nextLevel === 0 ? "se.cms.navigationcomponent.management.node.level.root" : "se.cms.navigationcomponent.management.node.level.non.root"
                });
            });
        };
    })
    /**
     * @ngdoc service
     * @name navigationEditorNodeServiceModule.service:navigationEditorNodeService
     * @description
     * This service updates the navigation node by making REST call to the cmswebservices navigations API.
     */
    .service('navigationEditorNodeService', function($q, _nodeAncestryService, navigationNodeRestService, lodash, getDataFromResponse) {

        this.getNavigationNode = function(nodeUid, uriParams) {

            var payload = angular.extend({
                identifier: nodeUid
            }, uriParams);
            return navigationNodeRestService.get(payload);
        };

        /**
         * @ngdoc method
         * @name navigationEditorNodeServiceModule.service:navigationEditorNodeService#updateNavigationNode
         * @methodOf navigationEditorNodeServiceModule.service:navigationEditorNodeService
         *
         * @description
         * Updates a navigation node that corresponds to specific site UID, catalogId and catalogVersion. The request is sent
         * to the cmswebservices navigations API using a REST call.
         *
         * @param {Object} node The navigation node that needs to be updated.
         * @param {Object} uriParams the {@link resourceLocationsModule.object:UriContext UriContext} necessary to perform operations
         */
        this.updateNavigationNode = function(node, uriParams) {

            var payload = lodash.assign({
                identifier: node.uid
            }, node, uriParams);
            delete payload.parent;
            delete payload.nodes;

            payload.entries = payload.entries.map(function(_entry) {
                var clone = lodash.cloneDeep(_entry);
                delete clone.parent;
                return clone;
            });

            return navigationNodeRestService.update(payload).then(function() {
                node.parent.initiated = false;
                return;
            });
        };

        /**
         * @ngdoc method
         * @name navigationEditorNodeServiceModule.service:navigationEditorNodeService#getNavigationNodeAncestry
         * @methodOf navigationEditorNodeServiceModule.service:navigationEditorNodeService
         *
         * @description
         * Returns the list of nodes belonging to the ancestry of the node identified by its uid. This list includes the queried node as well.
         *
         * @param {Object} node The navigation node that needs to be updated.
         * @param {Object} uriParams the {@link resourceLocationsModule.object:UriContext UriContext} necessary to perform operations
         * @returns {Array} an array of {@link treeModule.object:Node nodes}
         */
        this.getNavigationNodeAncestry = function(nodeUid, uriParams) {

            var payload = lodash.assign({
                ancestorTrailFrom: nodeUid
            }, uriParams);
            return navigationNodeRestService.get(payload).then(function(response) {
                return _nodeAncestryService.buildOrderedListOfAncestors(getDataFromResponse(response), nodeUid);
            });
        };

    });
