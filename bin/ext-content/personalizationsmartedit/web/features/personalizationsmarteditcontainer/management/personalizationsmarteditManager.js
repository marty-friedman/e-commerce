angular.module('personalizationsmarteditManagerModule', [
        'modalServiceModule',
        'coretemplates',
        'ui.select',
        'confirmationModalServiceModule',
        'functionsModule',
        'personalizationsmarteditCommons',
        'eventServiceModule',
        'personalizationsmarteditDataFactory',
        'sliderPanelModule',
        'seConstantsModule',
        'yjqueryModule'
    ])
    .constant('CUSTOMIZATION_VARIATION_MANAGEMENT_TABS_CONSTANTS', {
        BASIC_INFO_TAB_NAME: 'basicinfotab',
        BASIC_INFO_TAB_FORM_NAME: 'form.basicinfotab',
        TARGET_GROUP_TAB_NAME: 'targetgrptab',
        TARGET_GROUP_TAB_FORM_NAME: 'form.targetgrptab'
    })
    .constant('CUSTOMIZATION_VARIATION_MANAGEMENT_BUTTONS', {
        CONFIRM_OK: 'confirmOk',
        CONFIRM_CANCEL: 'confirmCancel',
        CONFIRM_NEXT: 'confirmNext'
    })
    .constant('CUSTOMIZATION_VARIATION_MANAGEMENT_SEGMENTTRIGGER_GROUPBY', {
        CRITERIA_AND: 'AND',
        CRITERIA_OR: 'OR'
    })
    .factory('personalizationsmarteditManager', function($controller, modalService, MODAL_BUTTON_ACTIONS, MODAL_BUTTON_STYLES, CUSTOMIZATION_VARIATION_MANAGEMENT_BUTTONS) {

        var manager = {};

        manager.openCreateCustomizationModal = function() {
            return modalService.open({
                title: 'personalization.modal.customizationvariationmanagement.title',
                templateUrl: 'personalizationsmarteditCustomizationManagTemplate.html',
                controller: ['$scope', 'modalManager', function($scope, modalManager) {
                    $scope.modalManager = modalManager;
                    angular.extend(this, $controller('personalizationsmarteditManagerController', {
                        $scope: $scope
                    }));
                }],
                buttons: [{
                    id: CUSTOMIZATION_VARIATION_MANAGEMENT_BUTTONS.CONFIRM_CANCEL,
                    label: 'personalization.modal.customizationvariationmanagement.button.cancel',
                    style: MODAL_BUTTON_STYLES.SECONDARY
                }],
                size: 'lg modal_bigger sliderPanelParentModal'
            });
        };

        manager.openEditCustomizationModal = function(customizationCode, variationCode) {
            return modalService.open({
                title: 'personalization.modal.customizationvariationmanagement.title',
                templateUrl: 'personalizationsmarteditCustomizationManagTemplate.html',
                controller: ['$scope', 'modalManager', function($scope, modalManager) {
                    $scope.customizationCode = customizationCode;
                    $scope.variationCode = variationCode;
                    $scope.modalManager = modalManager;
                    angular.extend(this, $controller('personalizationsmarteditManagerController', {
                        $scope: $scope
                    }));
                }],
                buttons: [{
                    id: 'confirmCancel',
                    label: 'personalization.modal.customizationvariationmanagement.button.cancel',
                    style: MODAL_BUTTON_STYLES.SECONDARY
                }],
                size: 'lg modal_bigger sliderPanelParentModal'
            });
        };

        return manager;
    })
    .controller('personalizationsmarteditManagerController', function($scope, hitch, $q, $log, personalizationsmarteditRestService, personalizationsmarteditMessageHandler, personalizationsmarteditUtils, personalizationsmarteditDateUtils, confirmationModalService, $filter, MODAL_BUTTON_ACTIONS, CUSTOMIZATION_VARIATION_MANAGEMENT_TABS_CONSTANTS, CUSTOMIZATION_VARIATION_MANAGEMENT_BUTTONS, systemEventService, CUSTOMIZATION_VARIATION_MANAGEMENT_SEGMENTTRIGGER_GROUPBY, PERSONALIZATION_DATE_FORMATS, PERSONALIZATION_MODEL_STATUS_CODES, personalizationsmarteditCommerceCustomizationService, customizationDataFactory, DATE_CONSTANTS) { //NOSONAR
        var self = this;

        var getVariationsForCustomization = function(customizationCode) {
            var filter = {
                includeFullFields: true
            };

            return personalizationsmarteditRestService.getVariationsForCustomization(customizationCode, filter);
        };

        var createCommerceCustomizationData = function(variations) {
            variations.forEach(function(variation) {
                variation.commerceCustomizations = personalizationsmarteditCommerceCustomizationService.getCommerceActionsCountMap(variation);
                variation.numberOfCommerceActions = personalizationsmarteditCommerceCustomizationService.getCommerceActionsCount(variation);
                delete variation.actions; //no more use for this property and it existence may be harmful
            });
        };

        self.isModallDirty = false;

        $scope.form = {};

        $scope.customization = {
            code: '',
            description: '',
            rank: 0,
            variations: [],
            active: false
        };
        $scope.activeTabNumber = 0;
        $scope.tabsArr = [{
            name: CUSTOMIZATION_VARIATION_MANAGEMENT_TABS_CONSTANTS.BASIC_INFO_TAB_NAME,
            active: true,
            disabled: false,
            heading: $filter('translate')("personalization.modal.customizationvariationmanagement.basicinformationtab"),
            template: 'personalizationsmarteditCustVarManagBasicInfoTemplate.html',
            formName: CUSTOMIZATION_VARIATION_MANAGEMENT_TABS_CONSTANTS.BASIC_INFO_TAB_FORM_NAME,
            isDirty: function() {
                return $scope.form.basicinfotab && $scope.form.basicinfotab.$dirty;
            },
            setPristine: function() {
                $scope.form.basicinfotab.$setPristine();
            },
            isValid: function() {
                return $scope.form.basicinfotab && $scope.form.basicinfotab.$valid;
            },
            setEnabled: function(enabled) {
                if (enabled) {
                    $scope.tabsArr[1].disabled = false;
                    $scope.modalManager.enableButton(CUSTOMIZATION_VARIATION_MANAGEMENT_BUTTONS.CONFIRM_NEXT);
                } else {
                    $scope.tabsArr[1].disabled = true;
                    $scope.modalManager.disableButton(CUSTOMIZATION_VARIATION_MANAGEMENT_BUTTONS.CONFIRM_NEXT);
                }
            },
            onConfirm: function() {
                $scope.activeTabNumber = 1;
            },
            onCancel: function() {
                self.onCancel();
            }
        }, {
            name: CUSTOMIZATION_VARIATION_MANAGEMENT_TABS_CONSTANTS.TARGET_GROUP_TAB_NAME,
            active: false,
            disabled: true,
            heading: $filter('translate')("personalization.modal.customizationvariationmanagement.targetgrouptab"),
            template: 'personalizationsmarteditCustVarManagTargetGrpTemplate.html',
            formName: CUSTOMIZATION_VARIATION_MANAGEMENT_TABS_CONSTANTS.TARGET_GROUP_TAB_FORM_NAME,
            isDirty: function() {
                return ($scope.form.targetgrptab && $scope.form.targetgrptab.$dirty) || $scope.edit.variationsListDirty;
            },
            setPristine: function() {
                $scope.form.targetgrptab.$setPristine();
            },
            isValid: function() {
                var isVariationListValid = personalizationsmarteditUtils.getVisibleItems($scope.customization.variations).length > 0;
                var isInVariationEditingMode = angular.isDefined($scope.edit.selectedVariation);
                return ($scope.form.targetgrptab && $scope.form.targetgrptab.$valid) && isVariationListValid && !isInVariationEditingMode;
            },
            setEnabled: function(enabled) {
                if (enabled) {
                    $scope.modalManager.enableButton(CUSTOMIZATION_VARIATION_MANAGEMENT_BUTTONS.CONFIRM_OK);
                } else {
                    $scope.modalManager.disableButton(CUSTOMIZATION_VARIATION_MANAGEMENT_BUTTONS.CONFIRM_OK);
                }
            },
            onConfirm: function() {
                self.onSave();
            },
            onCancel: function() {
                self.onCancel();
            }
        }];

        $scope.edit = {
            code: '',
            name: '',
            variationsListChanged: false,
            selectedTab: $scope.tabsArr[0],
            variationsLoaded: false,
            viewDateFormat: DATE_CONSTANTS.MOMENT_FORMAT,
            datetimeConfigurationEnabled: false
        };

        $scope.editMode = angular.isDefined($scope.customizationCode);

        if ($scope.editMode) {
            var filter = {
                code: $scope.customizationCode
            };
            personalizationsmarteditRestService.getCustomization(filter).then(function successCallback(response) {
                $scope.customization = response;

                $scope.customization.enabledStartDate = personalizationsmarteditDateUtils.formatDate($scope.customization.enabledStartDate);
                $scope.customization.enabledEndDate = personalizationsmarteditDateUtils.formatDate($scope.customization.enabledEndDate);
                $scope.customization.statusBoolean = ($scope.customization.status === PERSONALIZATION_MODEL_STATUS_CODES.ENABLED);

                if ($scope.customization.enabledStartDate || $scope.customization.enabledEndDate) {
                    $scope.edit.datetimeConfigurationEnabled = true;
                }

                if (angular.isDefined($scope.variationCode)) {

                    getVariationsForCustomization($scope.customizationCode).then(function successCallback(response) {
                        createCommerceCustomizationData(response.variations);
                        $scope.customization.variations = response.variations;
                        $scope.edit.variationsLoaded = true;

                        var filteredCollection = $scope.customization.variations.filter(function(elem) {
                            return elem.code === $scope.variationCode;
                        });

                        if (filteredCollection.length > 0) {

                            $scope.activeTabNumber = 1;
                            $scope.edit.selectedTab = $scope.tabsArr[1];

                            var selVariation = filteredCollection[0];
                            $scope.edit.selectedVariation = selVariation;
                        }

                    }, function errorCallback() {
                        personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.gettingsegments'));
                    });
                } else {
                    $scope.edit.selectedTab = $scope.tabsArr[0];
                }
            }, function errorCallback() {
                personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.gettingcomponents'));
            });
        }

        $scope.selectTab = function(tab) {
            $scope.edit.selectedTab = tab;
            $scope.activeTabNumber = $scope.tabsArr.indexOf(tab);
            switch (tab.name) {
                case CUSTOMIZATION_VARIATION_MANAGEMENT_TABS_CONSTANTS.BASIC_INFO_TAB_NAME:
                    $scope.modalManager.removeButton(CUSTOMIZATION_VARIATION_MANAGEMENT_BUTTONS.CONFIRM_OK);
                    if (!$scope.modalManager.getButton(CUSTOMIZATION_VARIATION_MANAGEMENT_BUTTONS.CONFIRM_NEXT)) {
                        $scope.modalManager.addButton({
                            id: CUSTOMIZATION_VARIATION_MANAGEMENT_BUTTONS.CONFIRM_NEXT,
                            label: 'personalization.modal.customizationvariationmanagement.basicinformationtab.button.next'
                        });
                    }
                    break;
                case CUSTOMIZATION_VARIATION_MANAGEMENT_TABS_CONSTANTS.TARGET_GROUP_TAB_NAME:
                    $scope.modalManager.removeButton(CUSTOMIZATION_VARIATION_MANAGEMENT_BUTTONS.CONFIRM_NEXT);
                    if (!$scope.modalManager.getButton(CUSTOMIZATION_VARIATION_MANAGEMENT_BUTTONS.CONFIRM_OK)) {
                        $scope.modalManager.addButton({
                            id: CUSTOMIZATION_VARIATION_MANAGEMENT_BUTTONS.CONFIRM_OK,
                            label: 'personalization.modal.customizationvariationmanagement.targetgrouptab.button.submit',
                            action: MODAL_BUTTON_ACTIONS.CLOSE
                        });
                    }
                    break;
                default:
                    break;
            }
        };

        self.onSave = function() {
            if ($scope.customization.enabledStartDate) {
                $scope.customization.enabledStartDate = personalizationsmarteditDateUtils.formatDate($scope.customization.enabledStartDate, PERSONALIZATION_DATE_FORMATS.MODEL_DATE_FORMAT);
            } else {
                $scope.customization.enabledStartDate = undefined;
            }

            if ($scope.customization.enabledEndDate) {
                $scope.customization.enabledEndDate = personalizationsmarteditDateUtils.formatDate($scope.customization.enabledEndDate, PERSONALIZATION_DATE_FORMATS.MODEL_DATE_FORMAT);
            } else {
                $scope.customization.enabledEndDate = undefined;
            }

            if ($scope.editMode) {
                personalizationsmarteditRestService.updateCustomizationPackage($scope.customization).then(function successCallback() {
                    systemEventService.sendSynchEvent('CUSTOMIZATIONS_MODIFIED', {});
                    customizationDataFactory.refreshData();
                    personalizationsmarteditMessageHandler.sendSuccess($filter('translate')('personalization.info.updatingcustomization'));
                }, function errorCallback() {
                    personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.updatingcustomization'));
                });
            } else {
                personalizationsmarteditRestService.createCustomization($scope.customization).then(function successCallback() {
                    systemEventService.sendSynchEvent('CUSTOMIZATIONS_MODIFIED', {});
                    personalizationsmarteditMessageHandler.sendSuccess($filter('translate')('personalization.info.creatingcustomization'));
                }, function errorCallback() {
                    personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.creatingcustomization'));
                });
            }

        };

        self.onCancel = function() {
            var deferred = $q.defer();
            if (self.isModallDirty) {
                confirmationModalService.confirm({
                    description: $filter('translate')('personalization.modal.customizationvariationmanagement.targetgrouptab.cancelconfirmation')
                }).then(function() {
                    $scope.modalManager.dismiss();
                    deferred.resolve();
                }, function() {
                    deferred.reject();
                });
            } else {
                $scope.modalManager.dismiss();
                deferred.resolve();
            }

            return deferred.promise;
        };

        self.init = function() {
            $scope.modalManager.setDismissCallback(self.onCancel);

            $scope.modalManager.setButtonHandler(hitch(this, function(buttonId) {
                switch (buttonId) {
                    case CUSTOMIZATION_VARIATION_MANAGEMENT_BUTTONS.CONFIRM_OK:
                        return $scope.edit.selectedTab.onConfirm();
                    case CUSTOMIZATION_VARIATION_MANAGEMENT_BUTTONS.CONFIRM_NEXT:
                        return $scope.edit.selectedTab.onConfirm();
                    case CUSTOMIZATION_VARIATION_MANAGEMENT_BUTTONS.CONFIRM_CANCEL:
                        return $scope.edit.selectedTab.onCancel();
                    default:
                        $log.error($filter('translate')('personalization.modal.customizationvariationmanagement.targetgrouptab.invalidbuttonid'), buttonId);
                        break;
                }
            }));

            $scope.resetDateTimeConfiguration = function() {
                $scope.customization.enabledStartDate = undefined;
                $scope.customization.enabledEndDate = undefined;
            };

            $scope.$watch(hitch(this, function() {
                var isSelectedTabDirty = $scope.edit.selectedTab.isDirty();
                var isSelectedTabValid = $scope.edit.selectedTab.isValid();
                return {
                    isDirty: isSelectedTabDirty,
                    isValid: isSelectedTabValid
                };
            }), hitch(this, function(obj) {
                if (obj.isDirty) {
                    self.isModallDirty = true;
                    if (obj.isValid) {
                        $scope.edit.selectedTab.setEnabled(true);
                    } else {
                        $scope.edit.selectedTab.setEnabled(false);
                    }
                } else if ($scope.editMode) {
                    if (obj.isValid) {
                        $scope.edit.selectedTab.setEnabled(true);
                    } else {
                        $scope.edit.selectedTab.setEnabled(false);
                    }
                } else {
                    self.isModallDirty = false;
                    $scope.edit.selectedTab.setEnabled(false);
                }
            }), true);

            $scope.$watch('customization.variations', function() {
                if ($scope.edit.variationsLoaded || !$scope.editMode) {
                    if ($scope.edit.variationsListDirty === false) {
                        $scope.edit.variationsListDirty = true;
                    } else if ($scope.edit.variationsListDirty === undefined) {
                        $scope.edit.variationsListDirty = false;
                    }
                }
            }, true);

            $scope.$watch('customization.enabledEndDate', function() {
                if ($scope.form.basicinfotab) {
                    $scope.form.basicinfotab.date_to_key.$validate();
                }
                $scope.isEndDateInThePast = personalizationsmarteditDateUtils.isDateInThePast($scope.customization.enabledEndDate);
            }, true);

            $scope.$watch('edit.selectedTab', function() {
                if ($scope.editMode && !$scope.edit.variationsLoaded && ($scope.edit.selectedTab && $scope.edit.selectedTab.name === CUSTOMIZATION_VARIATION_MANAGEMENT_TABS_CONSTANTS.TARGET_GROUP_TAB_NAME)) {

                    getVariationsForCustomization($scope.customizationCode).then(function successCallback(response) {
                        createCommerceCustomizationData(response.variations);
                        $scope.customization.variations = response.variations;
                        $scope.edit.variationsLoaded = true;
                    }, function errorCallback() {
                        personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.gettingsegments'));
                    });
                }
            }, true);
        };
    })
    .directive('uniquetargetgroupname',
        function(isBlank) {
            var isNameTheSameAsEditedTargetGroup = function(scope, targetGroupName) {
                return scope.edit.selectedVariation && targetGroupName === scope.edit.selectedVariation.code;
            };

            return {
                require: "ngModel",
                scope: false,
                link: function(scope, element, attributes, ctrl) {
                    ctrl.$validators.uniquetargetgroupname = function(modelValue) {
                        if (isBlank(modelValue) || isNameTheSameAsEditedTargetGroup(scope, modelValue)) {
                            return true;
                        } else {
                            return scope.customization.variations.filter(function(e) {
                                return e.code === modelValue;
                            }).length === 0;
                        }
                    };
                }
            };
        }
    );
