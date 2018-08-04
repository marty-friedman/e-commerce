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
angular.module('sakExecutorDecorator', [
        'coretemplates',
        'componentHandlerServiceModule',
        'decoratorServiceModule',
        'yLoDashModule',
        'crossFrameEventServiceModule',
        'eventServiceModule',
        'seConstantsModule'
    ])
    .service('sakExecutor', function($log, $compile, yjQuery, lodash, polyfillService, componentHandlerService, decoratorService, systemEventService, ID_ATTRIBUTE, ELEMENT_UUID_ATTRIBUTE, SMARTEDIT_DRAG_AND_DROP_EVENTS) {
        var ATTR_DATA = 'data-';
        var ATTR_SMARTEDIT = 'smartedit';
        var ATTR_DATA_SMARTEDIT = ATTR_DATA + ATTR_SMARTEDIT;

        this.componentDecoratorEnabled = true;

        /*
         * Validates if a given attribute name present on the decorated element is eligible
         * to be added as a smartedit property.
         */
        var isValidSmartEditAttribute = function(nodeName) {
            return lodash.startsWith(nodeName, ATTR_DATA_SMARTEDIT) || lodash.startsWith(nodeName, ATTR_SMARTEDIT);
        };

        /*
         * Parses the attribute name by removing ATTR_DATA prefix and
         * converting to a camel case string representation.
         */
        var parseAttributeName = function(nodeName) {
            if (lodash.startsWith(nodeName, ATTR_DATA)) {
                nodeName = nodeName.substring(ATTR_DATA.length);
            }
            return lodash.camelCase(nodeName);
        };

        this._getElementIndex = function(element) {
            return this.scopes.findIndex(function(item) {
                return componentHandlerService.getFromSelector(item.element).attr(ELEMENT_UUID_ATTRIBUTE) === componentHandlerService.getFromSelector(element).attr(ELEMENT_UUID_ATTRIBUTE);
            });
        };


        // we can't listen to these events in the controller because they could be sent before the component compilation.
        systemEventService.registerEventHandler(SMARTEDIT_DRAG_AND_DROP_EVENTS.DRAG_DROP_END, function() {
            this.componentDecoratorEnabled = true;
            if (polyfillService.isEligibleForEconomyMode()) {
                this.scopes.forEach(function(scope) {
                    scope.scope.componentDecoratorEnabled = true;
                });
            }
        }.bind(this));
        systemEventService.registerEventHandler(SMARTEDIT_DRAG_AND_DROP_EVENTS.DRAG_DROP_START, function(eventId, smarteditComponentClosestToDraggedElement) {
            this.componentDecoratorEnabled = false;
            if (polyfillService.isEligibleForEconomyMode()) {
                var itemIndex = smarteditComponentClosestToDraggedElement ? this._getElementIndex(smarteditComponentClosestToDraggedElement) : -1;
                this.scopes.forEach(function(scope, index) {
                    if (itemIndex === -1 || itemIndex !== index) {
                        scope.scope.componentDecoratorEnabled = false;
                    }
                });
            }
        }.bind(this));

        this.scopes = [];

        this.getScopes = function() {
            return this.scopes;
        };

        var decoratorsCondition = polyfillService.isEligibleForEconomyMode() ? "data-ng-if='componentDecoratorEnabled'" : "";

        this.wrapDecorators = function(transcludeFn, smarteditComponentId, smarteditComponentType) {
            return decoratorService.getDecoratorsForComponent(smarteditComponentType, smarteditComponentId).then(function(decorators) {

                var template = "<div " + decoratorsCondition + " data-ng-transclude></div>";

                decorators.forEach(function(decorator) {
                    template = "<div " + decoratorsCondition + " class='" + decorator + " se-decorator-wrap' data-active='active' data-smartedit-component-id='{{smarteditComponentId}}' " +
                        "data-smartedit-component-type='{{smarteditComponentType}}' data-smartedit-container-id='{{smarteditContainerId}}' " +
                        "data-smartedit-container-type='{{smarteditContainerType}}' data-component-attributes='componentAttributes'>" + template;
                    template += "</div>";
                });

                if (polyfillService.isEligibleForEconomyMode()) {
                    template = "<div>" + template + "<div data-ng-if='!componentDecoratorEnabled' data-ng-transclude></div></div>";
                }

                return $compile(template, transcludeFn);
            });
        };

        this.registerScope = function(scope, element) {
            this.scopes.push({
                scope: scope,
                element: angular.copy(element)
            });
        };

        this.destroyScope = function(element) {
            var itemIndex = this._getElementIndex(element);
            if (itemIndex !== -1) {
                this.scopes[itemIndex].scope.$destroy();
                this.scopes.splice(itemIndex, 1);
            } else {
                $log.warn('sakExecutor::destroyScope failed to retrieve element:', yjQuery(element).attr(ID_ATTRIBUTE), yjQuery(element).attr(ELEMENT_UUID_ATTRIBUTE));
            }
        };

        this.prepareScope = function(scope, element) {
            this.registerScope(scope, element);
            var attributes = {};
            Array.prototype.slice.apply(element.get(0).attributes).forEach(function(node) {
                var attrName = node.nodeName;
                if (isValidSmartEditAttribute(attrName)) {
                    attrName = parseAttributeName(attrName);
                    attributes[attrName] = node.nodeValue;
                }
            });

            scope.componentAttributes = attributes;
        };

        this.isDecoratorEnabled = function() {
            return this.componentDecoratorEnabled;
        };
    })
    .controller('smartEditComponentXController', function($element, $scope, $transclude, $q, $compile, $rootScope, sakExecutor, crossFrameEventService, EVENT_PERSPECTIVE_CHANGED, EVENT_PERSPECTIVE_REFRESHED) {
        this.$postLink = function() {
            $scope.active = false;
            $scope.componentDecoratorEnabled = sakExecutor.isDecoratorEnabled();

            var compiledElement;
            var elementScope;

            var replayDecorators = function() {
                elementScope.$destroy();
                $element.get(0).removeChild(compiledElement.get(0));
                sakExecutor.wrapDecorators($transclude, $scope.smarteditComponentId, $scope.smarteditComponentType).then(function(compiled) {
                    elementScope = $scope.$new(false);
                    compiledElement = compiled(elementScope);
                    $element.append(compiledElement);
                });
                return $q.when();
            };

            this.unregisterPerspectiveChangeEvent = crossFrameEventService.subscribe(EVENT_PERSPECTIVE_CHANGED, replayDecorators);
            this.unregisterPerspectiveRefreshedEvent = crossFrameEventService.subscribe(EVENT_PERSPECTIVE_REFRESHED, replayDecorators);

            $transclude($scope, function() {
                sakExecutor.wrapDecorators($transclude, $scope.smarteditComponentId, $scope.smarteditComponentType).then(function(compiled) {
                    elementScope = $scope.$new(false);
                    compiledElement = compiled(elementScope);
                    $element.append(compiledElement);

                    sakExecutor.prepareScope($scope, $element);

                    var inactivateDecorator = function() {
                        $scope.active = false;
                    };

                    var activateDecorator = function() {
                        $scope.active = true;
                    };

                    // Register Event Listeners
                    $element.on("mouseenter", function() {
                        if (!sakExecutor.isDecoratorEnabled()) {
                            $rootScope.$apply(inactivateDecorator);
                            return;
                        }
                        $rootScope.$apply(activateDecorator);
                    });
                    $element.on("mouseleave", function() {
                        $rootScope.$apply(inactivateDecorator);
                    });
                });
            });
        };
        this.$onDestroy = function() {
            this.unregisterPerspectiveChangeEvent();
            this.unregisterPerspectiveRefreshedEvent();
        };
    })
    .directive('smartEditComponentX', function() {
        return {
            restrict: 'C',
            transclude: true,
            replace: false,
            scope: {
                smarteditComponentId: '@',
                smarteditComponentType: '@',
                smarteditContainerId: '@',
                smarteditContainerType: '@'
            },
            controller: 'smartEditComponentXController',
            controllerAs: 'ctrl'
        };
    });
