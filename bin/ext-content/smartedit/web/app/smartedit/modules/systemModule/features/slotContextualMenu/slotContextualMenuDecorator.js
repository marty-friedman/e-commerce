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
angular.module('slotContextualMenuDecoratorModule', [
        'smarteditServicesModule',
        'contextualMenuDecoratorModule',
        'eventServiceModule',
        'ui.bootstrap',
        'componentHandlerServiceModule',
        'permissionServiceModule'
    ])
    .constant('SHOW_SLOT_MENU', '_SHOW_SLOT_MENU')
    .constant('HIDE_SLOT_MENU', 'HIDE_SLOT_MENU')
    .constant('SHOW_SLOT_PADDING', 'SHOW_SLOT_PADDING')
    .constant('HIDE_SLOT_PADDING', 'HIDE_SLOT_PADDING')
    .controller('slotContextualMenuController', function($controller, $scope, $element, SHOW_SLOT_MENU, HIDE_SLOT_MENU, SHOW_SLOT_PADDING, HIDE_SLOT_PADDING, REFRESH_CONTEXTUAL_MENU_ITEMS_EVENT, smarteditroot, systemEventService, contextualMenuService, permissionService, componentHandlerService) {
        angular.extend(this, $controller('baseContextualMenuController', {
            $scope: $scope
        }));

        this.$onInit = function() {
            this.updateItems();
            this.showItems = false;

            this.permissionsObject = [{
                names: ["se.slot.not.external"],
                context: {
                    slotCatalogVersionUuid: this.componentAttributes.smarteditCatalogVersionUuid
                }
            }];

            permissionService.isPermitted(this.permissionsObject).then(function(isAllowed) {
                this.showItems = isAllowed;

                var showSlotMenuId = this.smarteditComponentId + SHOW_SLOT_MENU;
                this.showSlotMenuUnregFn = systemEventService.registerEventHandler(showSlotMenuId, this._showSlotMenu);
                this.hideSlotMenuUnregFn = systemEventService.registerEventHandler(HIDE_SLOT_MENU, this._hideSlotMenu);
                this.refreshContextualMenuUnregFn = systemEventService.registerEventHandler(REFRESH_CONTEXTUAL_MENU_ITEMS_EVENT, this.updateItems);

            }.bind(this));

        };

        this.triggerMenuItemAction = function(item, $event) {
            item.callback({
                componentType: this.smarteditComponentType,
                componentId: this.smarteditComponentId,
                componentAttributes: this.componentAttributes
            }, $event);
        };

        this._showSlotMenu = function(eventId, slotId) {
            if (this.smarteditComponentId === slotId) {
                this.remainOpenMap.slotMenuButton = true;
                systemEventService.sendEvent(SHOW_SLOT_PADDING);
            }
        }.bind(this);

        this._hideSlotMenu = function() {
            if (this.remainOpenMap.slotMenuButton) {
                delete this.remainOpenMap.slotMenuButton;
            }
            systemEventService.sendEvent(HIDE_SLOT_PADDING);
        }.bind(this);

        this.maxContextualMenuItems = 3;

        this.updateItems = function() {
            contextualMenuService.getContextualMenuItems({
                componentType: this.smarteditComponentType,
                componentId: this.smarteditComponentId,
                containerType: this.smarteditContainerType,
                containerId: this.smarteditContainerId,
                componentAttributes: this.componentAttributes,
                iLeftBtns: this.maxContextualMenuItems,
                element: $element
            }).then(function(newItems) {
                this.items = newItems;
            }.bind(this));
        }.bind(this);

        this.triggerMenuItemAction = function(item, $event) {
            item.callback({
                componentType: this.smarteditComponentType,
                componentId: this.smarteditComponentId,
                containerType: this.smarteditContainerType,
                containerId: this.smarteditContainerId,
                componentAttributes: this.componentAttributes,
                slotId: this.smarteditSlotId,
                slotUuid: this.smarteditSlotUuid,
                element: $element,
                //@deprecated since 6.4
                properties: JSON.stringify(this.componentAttributes)
            }, $event);
        }.bind(this);

        this.getItems = function() {
            return this.items;
        };

        this.$onDestroy = function() {
            if (this.showSlotMenuUnregFn) {
                this.showSlotMenuUnregFn();
            }
            if (this.hideSlotMenuUnregFn) {
                this.hideSlotMenuUnregFn();
            }
            if (this.refreshContextualMenuUnregFn) {
                this.refreshContextualMenuUnregFn();
            }
        };

        this.positionPanelHorizontally = function() {

            this.initialSlotMenuWidth = $element.find('.decorative-panel-area').outerWidth();
            var leftMarginPx = $element.find('.decorative-panel-area').css('margin-left') || '0px';
            var leftMargin = parseInt(leftMarginPx.replace('px', ''));
            var rightMostOffsetFromElement = $element.find('.decorative-panel-area').width() + leftMargin;
            var rightMostOffsetFromPage = $element.offset().left + rightMostOffsetFromElement;

            var isOnLeft = rightMostOffsetFromPage >= componentHandlerService.getFromSelector('body').width();
            if (isOnLeft) {
                var offset = $element.find('.decorative-panel-area').outerWidth() - $element.find('.yWrapperData').width();
                $element.find('.decorative-panel-area').css('margin-left', -offset);
                $element.find('.decorator-padding-left').css('margin-left', -offset);
            }

            var paddingSelector = isOnLeft ? '.decorator-padding-left' : '.decorator-padding-right';
            this.hidePadding();
            $element.find(paddingSelector).css('display', 'flex');
        };

        this.hidePadding = function() {
            if ($element) {
                $element.find('.decorator-padding-left').css('display', 'none');
                $element.find('.decorator-padding-right').css('display', 'none');
            }
        };

        this.$doCheck = function() {
            if ($element && $element.find('.decorative-panel-area').outerWidth() > 0 && this.initialSlotMenuWidth !== $element.find('.decorative-panel-area').outerWidth()) {
                this.positionPanelHorizontally();
            }
        };

        this.initialSlotMenuWidth = 0;

    })
    .directive('slotContextualMenu', function($timeout, SHOW_SLOT_PADDING, HIDE_SLOT_PADDING, systemEventService) {
        return {
            templateUrl: 'slotContextualMenuDecoratorTemplate.html',
            restrict: 'C',
            transclude: true,
            replace: false,
            controller: 'slotContextualMenuController',
            controllerAs: 'ctrl',
            scope: {},
            bindToController: {
                smarteditComponentId: '@',
                smarteditComponentType: '@',
                componentAttributes: '<',
                active: '='
            },
            link: function($scope, $element) {
                $scope.ctrl.positionPanelVertically = function() {
                    var decorativePanelArea = $element.find('.decorative-panel-area');
                    var decoratorPaddingContainer = $element.find('.decorator-padding-container');
                    var marginTop;
                    if ($element.offset().top <= decorativePanelArea.height()) {
                        marginTop = decoratorPaddingContainer.height();
                        decoratorPaddingContainer.css('margin-top', -(marginTop + decorativePanelArea.height()));
                    } else {
                        marginTop = -42;
                    }
                    decorativePanelArea.css('margin-top', marginTop);
                };

                $scope.ctrl.positionPanel = function() {
                    $scope.ctrl.positionPanelVertically();
                    $scope.ctrl.positionPanelHorizontally();
                };

                $scope.$watch('ctrl.active', function(isActive) {
                    $timeout(function() {
                        $scope.ctrl.hidePadding();
                        if (!!isActive) {
                            $scope.ctrl.positionPanel();
                            systemEventService.sendEvent('SLOT_CONTEXTUAL_MENU_ACTIVE');
                        }
                    });
                });

                systemEventService.registerEventHandler(HIDE_SLOT_PADDING, $scope.ctrl.hidePadding);
                systemEventService.registerEventHandler(SHOW_SLOT_PADDING, $scope.ctrl.positionPanel);

                $scope.$on("$destroy", function() {
                    systemEventService.unRegisterEventHandler(HIDE_SLOT_PADDING, $scope.ctrl.hidePadding);
                    systemEventService.unRegisterEventHandler(SHOW_SLOT_PADDING, $scope.ctrl.positionPanel);
                }.bind($scope));
            }
        };
    });
