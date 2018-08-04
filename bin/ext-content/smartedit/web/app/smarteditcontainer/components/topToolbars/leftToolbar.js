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
angular.module('leftToolbarModule', ['yjqueryModule', 'authenticationModule', 'iframeClickDetectionServiceModule', 'resourceLocationsModule', 'iFrameManagerModule', 'smarteditServicesModule', 'languageSelectorModule', 'sessionServiceModule', 'eventServiceModule'])
    .directive('leftToolbar', function($location, $timeout, yjQuery, authenticationService, iframeClickDetectionService, iFrameManager, sharedDataService, sessionService, systemEventService, LANDING_PAGE_PATH) {
        return {
            templateUrl: 'leftToolbarTemplate.html',
            restrict: 'E',
            transclude: false,
            replace: true,

            scope: {
                imageRoot: '=?imageRoot'
            },

            link: function(scope) {

                scope.showLevel2 = false;
                scope.showLeftMenu = false;

                function resetLocation() {
                    sharedDataService.get("preview").then(function(preview) {
                        if (preview && preview.resourcePath) {
                            iFrameManager.setCurrentLocation(preview.resourcePath);
                        }
                    });
                }

                function getUsername() {
                    if (!scope.username) {
                        sessionService.getCurrentUserDisplayName().then(function(displayName) {
                            scope.username = displayName;
                        });
                    }
                }

                scope.configurationCenterReadPermissionKey = "smartedit.configurationcenter.read";

                scope.showToolbar = function($event) {
                    getUsername();
                    scope.showLevel2 = false;
                    $event.preventDefault();
                    scope.showLeftMenu = true;
                };

                scope.showSites = function() {
                    scope.closeLeftToolbar();

                    resetLocation();
                    // wait for the css closing animation to be completed
                    $timeout(function() {
                        $location.url(LANDING_PAGE_PATH);
                    }, 400);
                };

                scope.showCfgCenter = function($event) {
                    $event.preventDefault();
                    scope.showLevel2 = true;
                };

                scope.goBack = function() {
                    scope.showLevel2 = false;
                };

                scope.signOut = function() {
                    authenticationService.logout();
                    resetLocation();
                };

                yjQuery(document).bind("click", function(event) {
                    if (!document.querySelector('.se-left-menu').contains(event.target)) {
                        scope.closeLeftToolbar();
                    }
                });

                scope.closeLeftToolbar = function($event) {
                    if ($event) {
                        $event.preventDefault();
                    }
                    scope.showLeftMenu = false;
                    scope.goBack();
                };


                iframeClickDetectionService.registerCallback('leftToolbarClose', function() {
                    scope.closeLeftToolbar();
                });

                var unRegFn = systemEventService.registerEventHandler('OVERLAY_DISABLED', function() {
                    scope.closeLeftToolbar();
                });

                scope.$on('$destroy', function() {
                    unRegFn();
                });

            }
        };
    });
