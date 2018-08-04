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
angular.module('catalogAwareRouteResolverModule', [])
    .constant('catalogAwareRouteResolverFunctions', {
        /**
         * This function initializes new experience based on route params. It will redirect current user to the landing page
         * if the user doesn't have a read permission to the current catalog version. If the user has read permission for the
         * catalog version then EVENTS.EXPERIENCE_UPDATE is sent, but only when the experience has been changed.
         *
         * This function can be assigned to the resolve property of any route.
         */
        setExperience: ['$log', '$q', '$route', '$location', 'experienceService', 'sharedDataService', 'systemEventService', 'EVENTS', 'LANDING_PAGE_PATH', 'catalogVersionPermissionService',
            function($log, $q, $route, $location, experienceService, sharedDataService, systemEventService, EVENTS, LANDING_PAGE_PATH, catalogVersionPermissionService) {
                var experienceUpdated = function(prev, next) {
                    return (prev === undefined ||
                        (next.catalogDescriptor.catalogId !== prev.catalogDescriptor.catalogId) ||
                        (next.catalogDescriptor.catalogVersion !== prev.catalogDescriptor.catalogVersion));
                };

                var prepareExperiences = function() {
                    return experienceService.buildDefaultExperience($route.current.params).then(function(nextExperience) {
                        return sharedDataService.get('experience').then(function(previousExperience) {
                            return sharedDataService.set('experience', nextExperience).then(function() {
                                return $q.when({
                                    previousExperience: previousExperience,
                                    nextExperience: nextExperience
                                });
                            });
                        });
                    }, function(buildError) {
                        $log.error("the provided path could not be parsed: " + $location.url());
                        $log.error(buildError);
                        $location.url(LANDING_PAGE_PATH);
                    });
                };

                var verifyCatalogReadPermission = function() {
                    return catalogVersionPermissionService.hasReadPermissionOnCurrent().then(function(hasReadPermission) {
                        if (!hasReadPermission) {
                            $location.url(LANDING_PAGE_PATH);
                        }
                    }, function() {
                        $location.url(LANDING_PAGE_PATH);
                    });
                };

                return prepareExperiences().then(function(experiences) {
                    return verifyCatalogReadPermission().then(function() {
                        if (experienceUpdated(experiences.previousExperience, experiences.nextExperience)) {
                            return systemEventService.sendAsynchEvent(EVENTS.EXPERIENCE_UPDATE);
                        }
                    });
                });
            }
        ]
    });
