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
angular.module('navigationEditorServiceModule', ['smarteditServicesModule', 'resourceLocationsModule'])
    .factory('navigationEditorService', function(NAVIGATION_MANAGEMENT_RESOURCE_URI, restServiceFactory) {
        var navigationRestService = restServiceFactory.get(NAVIGATION_MANAGEMENT_RESOURCE_URI);

        return {

            getNavigationNode: function(nodeUid, uriParams) {
                return navigationRestService.get({
                    identifier: nodeUid,
                    siteUID: uriParams.siteId,
                    catalogId: uriParams.catalogId,
                    catalogVersion: uriParams.catalogVersion
                }).then(function() {});
            },

            updateNavigationNode: function(node, uriParams) {
                var parent = node.parent;
                return navigationRestService.update({
                    identifier: node.uid,
                    siteUID: uriParams.siteId,
                    catalogId: uriParams.catalogId,
                    catalogVersion: uriParams.catalogVersion,
                    parentUid: node.parentUid,
                    uid: node.uid,
                    name: node.name,
                    title: node.title,
                    position: node.position
                }).then(function() {
                    parent.initiated = false;
                    return;
                });
            }

        };

    });
