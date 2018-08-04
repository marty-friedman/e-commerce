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
angular.module('perspectiveSelectorModule', [
        'yjqueryModule',
        'iframeClickDetectionServiceModule',
        'smarteditServicesModule',
        'eventServiceModule',
        'crossFrameEventServiceModule',
        'yPopoverModule',
        'seConstantsModule'
    ])
    .constant('isE2eTestingActive', false)
    .controller('PerspectiveSelectorController', function($log, yjQuery, perspectiveService, iframeClickDetectionService, $scope, $document, systemEventService, EVENT_PERSPECTIVE_ADDED, EVENT_PERSPECTIVE_CHANGED, ALL_PERSPECTIVE, EVENTS, crossFrameEventService, isE2eTestingActive) {
        var perspectives = [];
        var displayedPerspectives = [];
        var activePerspective = null;
        var showHotkeyTooltip = false;

        var unRegOverlayDisabledFn;
        var unRegPerspectiveAddedFn;
        var unRegPerspectiveChgFn;
        var unRegAuthSuccess;

        var closeDropdown = function() {
            this.isOpen = false;
        }.bind(this);

        var onPerspectiveAdded = function() {
            perspectiveService.getPerspectives().then(function(result) {
                perspectives = result;
                displayedPerspectives = this._filterPerspectives(perspectives);
            }.bind(this));
        }.bind(this);

        this.refreshPerspectives = function() {
            perspectiveService.getPerspectives().then(function(result) {
                perspectives = result;
                refreshHotkeyTooltip();
                this._refreshActivePerspective();
                displayedPerspectives = this._filterPerspectives(perspectives);
            }.bind(this));
        };

        this.isOpen = false;

        this.$onInit = function() {
            iframeClickDetectionService.registerCallback('perspectiveSelectorClose', closeDropdown);

            unRegOverlayDisabledFn = systemEventService.registerEventHandler('OVERLAY_DISABLED', closeDropdown);
            unRegPerspectiveAddedFn = systemEventService.registerEventHandler(EVENT_PERSPECTIVE_ADDED, onPerspectiveAdded);
            unRegPerspectiveChgFn = crossFrameEventService.subscribe(EVENT_PERSPECTIVE_CHANGED, this.refreshPerspectives.bind(this));
            unRegAuthSuccess = systemEventService.registerEventHandler(EVENTS.AUTHORIZATION_SUCCESS, function(evtId, evtData) {
                if (evtData.userHasChanged) {
                    onPerspectiveAdded();
                }
            }.bind(this));

            onPerspectiveAdded();

            $document.on('click', function(event) {
                if (yjQuery(event.target).parents('.ySEPerspectiveSelector').length <= 0 && this.isOpen) {
                    closeDropdown();
                    $scope.$apply();
                }
            }.bind(this));
        };

        this.$onDestroy = function() {
            unRegOverlayDisabledFn();
            unRegPerspectiveAddedFn();
            unRegPerspectiveChgFn();
            unRegAuthSuccess();
        };

        this.selectPerspective = function(choice) {
            try {
                perspectiveService.switchTo(choice);
                closeDropdown();
            } catch (e) {
                $log.error("selectPerspective() - Cannot select perspective.", e);
            }
        };

        this.getDisplayedPerspectives = function() {
            return displayedPerspectives;
        };

        this.getActivePerspectiveName = function() {
            return activePerspective ? activePerspective.nameI18nKey : '';
        };

        this.isHotkeyTooltipVisible = function() {
            return showHotkeyTooltip;
        };

        this._filterPerspectives = function(perspectives) {
            return perspectives.filter(function(perspective) {
                var isActivePerspective = activePerspective && (perspective.key === activePerspective.key);
                var isAllPerspective = perspective.key === ALL_PERSPECTIVE;

                return !isActivePerspective && (!isAllPerspective || isE2eTestingActive);
            });
        };

        var refreshHotkeyTooltip = function() {
            perspectiveService.isEmptyPerspectiveActive().then(function(isPreviewModeActive) {
                showHotkeyTooltip = !isPreviewModeActive;
            });
        };

        this._refreshActivePerspective = function() {
            activePerspective = perspectiveService.getActivePerspective();
        };
    })
    .component('perspectiveSelector', {
        templateUrl: 'perspectiveSelectorWidgetTemplate.html',
        controller: 'PerspectiveSelectorController'
    });
