angular.module('personalizationsmarteditManagerModule')
    .service('personalizationsmarteditTriggerService', function() {
        var triggerService = {};

        var DEFAULT_TRIGGER = 'defaultTriggerData';
        var SEGMENT_TRIGGER = 'segmentTriggerData';
        var EXPRESSION_TRIGGER = 'expressionTriggerData';

        var GROUP_EXPRESSION = 'groupExpressionData';
        var SEGMENT_EXPRESSION = 'segmentExpressionData';
        var NEGATION_EXPRESSION = 'negationExpressionData';

        var CONTAINER_TYPE = 'container';
        var ITEM_TYPE = 'item';
        var DROPZONE_TYPE = 'dropzone';

        var supportedTypes = [DEFAULT_TRIGGER, SEGMENT_TRIGGER, EXPRESSION_TRIGGER];
        var actions = [{
            id: 'AND',
            name: 'personalization.modal.customizationvariationmanagement.targetgrouptab.expression.and'
        }, {
            id: 'OR',
            name: 'personalization.modal.customizationvariationmanagement.targetgrouptab.expression.or'
        }, {
            id: 'NOT',
            name: 'personalization.modal.customizationvariationmanagement.targetgrouptab.expression.not'
        }];

        var isElementOfType = function(element, type) {
            return angular.isDefined(element) ? (element.type === type) : false;
        };

        var isDropzone = function(element) {
            return isElementOfType(element, DROPZONE_TYPE);
        };

        var isItem = function(element) {
            return isElementOfType(element, ITEM_TYPE);
        };

        var isContainer = function(element) {
            return isElementOfType(element, CONTAINER_TYPE);
        };

        var isEmptyContainer = function(element) {
            return isContainer(element) && element.nodes.length === 0;
        };

        var isNotEmptyContainer = function(element) {
            return isContainer(element) && element.nodes.length > 0;
        };

        var isNegation = function(element) {
            return isContainer(element) && element.operation.id === 'NOT';
        };

        var isDefaultData = function(form) {
            return form.isDefault;
        };

        var isExpressionData = function(element) {
            return element.operation.id === 'NOT' || element.nodes.some(function(item) {
                return !isItem(item);
            });
        };

        var isValidExpression = function(element) {
            if (!element) {
                return false;
            }
            if (isContainer(element)) {
                return element.nodes && element.nodes.length > 0 && element.nodes.every(isValidExpression);
            } else {
                return typeof element.selectedSegment !== 'undefined';
            }
        };

        var isSupportedTrigger = function(trigger) {
            return supportedTypes.indexOf(trigger.type) >= 0;
        };

        var isDefaultTrigger = function(trigger) {
            return isElementOfType(trigger, DEFAULT_TRIGGER);
        };

        var isSegmentTrigger = function(trigger) {
            return isElementOfType(trigger, SEGMENT_TRIGGER);
        };

        var isExpressionTrigger = function(trigger) {
            return isElementOfType(trigger, EXPRESSION_TRIGGER);
        };

        var isGroupExpressionData = function(expression) {
            return isElementOfType(expression, GROUP_EXPRESSION);
        };

        var isSegmentExpressionData = function(expression) {
            return isElementOfType(expression, SEGMENT_EXPRESSION);
        };

        var isNegationExpressionData = function(expression) {
            return isElementOfType(expression, NEGATION_EXPRESSION);
        };

        var isDefault = function(triggers) {
            return (triggers && triggers.find(isDefaultTrigger)) ? true : false;
        };

        var getExpressionAsString = function(expressionContainer) {
            var retStr = "";
            if (expressionContainer === undefined) {
                return retStr;
            }

            var currOperator = isNegation(expressionContainer) ? "AND" : expressionContainer.operation.id;
            retStr += isNegation(expressionContainer) ? " NOT " : "";
            retStr += "(";

            expressionContainer.nodes.forEach(function(element, index) {
                if (isDropzone(element)) {
                    retStr += " [] ";
                } else {
                    retStr += (index > 0) ? " " + currOperator + " " : "";
                    retStr += isItem(element) ? element.selectedSegment.code : getExpressionAsString(element);
                }
            });

            retStr += ")";

            return retStr;
        };

        //------------------------ FORM DATA -> TRIGGER ---------------------------

        var buildSegmentsForTrigger = function(element) {
            return element.nodes.filter(isItem).map(function(item) {
                return item.selectedSegment;
            });
        };

        var buildExpressionForTrigger = function(element) {
            if (isNegation(element)) {
                var negationElements = Array.from(element.nodes, buildExpressionForTrigger);

                return {
                    type: NEGATION_EXPRESSION,
                    element: {
                        type: GROUP_EXPRESSION,
                        operator: 'AND',
                        elements: negationElements
                    }
                };
            } else if (isContainer(element)) {
                var groupElements = Array.from(element.nodes, buildExpressionForTrigger);
                return {
                    type: GROUP_EXPRESSION,
                    operator: element.operation.id,
                    elements: groupElements
                };
            } else {
                return {
                    type: SEGMENT_EXPRESSION,
                    code: element.selectedSegment.code
                };
            }
        };

        var buildDefaultTrigger = function() {
            return {
                type: DEFAULT_TRIGGER
            };
        };

        var buildExpressionTrigger = function(element) {
            var expression = buildExpressionForTrigger(element);

            return {
                type: EXPRESSION_TRIGGER,
                expression: expression
            };
        };

        var buildSegmentTrigger = function(element) {
            var groupBy = element.operation.id;
            var segments = buildSegmentsForTrigger(element);
            return {
                type: SEGMENT_TRIGGER,
                groupBy: groupBy,
                segments: segments
            };
        };

        var mergeTriggers = function(triggers, trigger) {
            if (!angular.isDefined(triggers)) {
                return [trigger];
            }

            var index = triggers.findIndex(function(t) {
                return t.type === trigger.type;
            });
            if (index >= 0) {
                trigger.code = triggers[index].code;
            }

            //remove other instanced of supported types (there can be only one) but maintain unsupported types
            var result = triggers.filter(function(t) {
                return !isSupportedTrigger(t);
            });
            result.push(trigger);
            return result;
        };

        var buildTriggers = function(form, existingTriggers) {
            var trigger = {};
            form = form || {};

            if (isDefaultData(form)) {
                trigger = buildDefaultTrigger();
            } else if (form.expression && form.expression.length > 0) {
                var element = form.expression[0];
                if (isExpressionData(element)) {
                    trigger = buildExpressionTrigger(element);
                } else {
                    trigger = buildSegmentTrigger(element);
                }
            }

            return mergeTriggers(existingTriggers, trigger);
        };

        //------------------------ TRIGGER -> FORM DATA ---------------------------

        var buildContainer = function(actionId) {
            var action = actions.find(function(a) {
                return a.id === actionId;
            });
            return {
                type: CONTAINER_TYPE,
                operation: action,
                nodes: []
            };
        };

        var buildItem = function(value) {
            return {
                type: ITEM_TYPE,
                operation: '',
                selectedSegment: {
                    code: value
                },
                nodes: []
            };
        };

        var getBaseData = function() {
            var data = buildContainer('AND');
            return [data];
        };

        var buildExpressionFromTrigger = function(expression) {
            var data = {};
            if (isGroupExpressionData(expression)) {
                data = buildContainer(expression.operator);
                data.nodes = expression.elements.map(function(item) {
                    return buildExpressionFromTrigger(item);
                });
            } else if (isNegationExpressionData(expression)) {
                data = buildContainer('NOT');
                var element = buildExpressionFromTrigger(expression.element);

                if (isGroupExpressionData(expression.element) && expression.element.operator === 'AND') {
                    data.nodes = element.nodes;
                } else {
                    data.nodes.push(element);
                }
            } else if (isSegmentExpressionData(expression)) {
                data = buildItem(expression.code);
            }
            return data;
        };

        var buildSegmentTriggerData = function(trigger) {
            var data = buildContainer(trigger.groupBy);

            trigger.segments.forEach(function(segment) {
                data.nodes.push(buildItem(segment.code));
            });
            return [data];
        };

        var buildExpressionTriggerData = function(trigger) {
            var data = buildExpressionFromTrigger(trigger.expression);
            return [data];
        };

        var buildData = function(triggers) {
            var trigger = {};
            var data = getBaseData();
            if (triggers && triggers.length > 0) {
                trigger = triggers.find(isSupportedTrigger);
            }
            if (isDefaultTrigger(trigger)) { // jshint ignore:line
                //nothing to do here
                //we leave baseData - it will be used if user removes default trigger
            } else if (isExpressionTrigger(trigger)) {
                data = buildExpressionTriggerData(trigger);
            } else if (isSegmentTrigger(trigger)) {
                data = buildSegmentTriggerData(trigger);
            }
            return data;
        };

        //------------------------ PUBLIC ---------------------------
        triggerService.isContainer = isContainer;
        triggerService.isNotEmptyContainer = isNotEmptyContainer;
        triggerService.isEmptyContainer = isEmptyContainer;
        triggerService.isItem = isItem;
        triggerService.isDropzone = isDropzone;
        triggerService.isValidExpression = isValidExpression;
        triggerService.buildTriggers = buildTriggers;
        triggerService.buildData = buildData;
        triggerService.isDefault = isDefault;
        triggerService.getExpressionAsString = getExpressionAsString;

        triggerService.actions = actions;

        return triggerService;
    })
    .controller('personalizationsmarteditManagerTargetGrpController', function($scope, isBlank, $filter, yjQuery, personalizationsmarteditUtils, personalizationsmarteditTriggerService, personalizationsmarteditMessageHandler, confirmationModalService, CUSTOMIZATION_VARIATION_MANAGEMENT_SEGMENTTRIGGER_GROUPBY, PERSONALIZATION_MODEL_STATUS_CODES, $timeout) {

        var self = this;
        $scope.edit.expression = [];
        $scope.edit.isDefault = false;
        $scope.edit.showExpression = true;

        this.sliderPanelConfiguration = {
            modal: {
                showDismissButton: true,
                title: "personalization.modal.customizationvariationmanagement.targetgrouptab.slidingpanel.title",
                cancel: {
                    label: "personalization.modal.customizationvariationmanagement.targetgrouptab.cancelchanges",
                    onClick: function() {
                        self.cancelChangesClick();
                    }
                },
                dismiss: {
                    onClick: function() {
                        self.cancelChangesClick();
                    }
                },
                save: {}
            },
            cssSelector: "#y-modal-dialog"
        };

        var clearEditedVariationDetails = function() {
            $scope.edit.code = '';
            $scope.edit.name = '';
            $scope.edit.expression = [];
            $scope.edit.isDefault = false;
            $scope.edit.showExpression = true;
        };

        this.addVariationClick = function() {
            var triggers = personalizationsmarteditTriggerService.buildTriggers($scope.edit);

            $scope.customization.variations.push({
                code: $scope.edit.code,
                name: $scope.edit.name,
                enabled: true,
                status: PERSONALIZATION_MODEL_STATUS_CODES.ENABLED,
                triggers: triggers,
                rank: $scope.customization.variations.length,
                isNew: true
            });

            clearEditedVariationDetails();
            $scope.toggleSliderFullscreen(false);
            $timeout((function() {
                self.hideSliderPanel();
            }), 0);
        };

        this.submitChangesClick = function() {

            var triggers = personalizationsmarteditTriggerService.buildTriggers($scope.edit, $scope.edit.selectedVariation.triggers || []);
            $scope.edit.selectedVariation.triggers = triggers;

            $scope.edit.selectedVariation.name = $scope.edit.name;
            $scope.edit.selectedVariation = undefined;
            $scope.toggleSliderFullscreen(false);
        };

        this.cancelChangesClick = function() {
            if ($scope.isVariationSelected()) {
                $scope.edit.selectedVariation = undefined;
            } else {
                clearEditedVariationDetails();

                $timeout((function() {
                    self.hideSliderPanel();
                }), 0);
            }
            $scope.toggleSliderFullscreen(false);
        };

        $scope.removeVariationClick = function(variation) {
            confirmationModalService.confirm({
                description: 'personalization.modal.manager.targetgrouptab.deletevariation.content'
            }).then(function() {
                if (variation.isNew) {
                    $scope.customization.variations.splice($scope.customization.variations.indexOf(variation), 1);
                } else {
                    variation.status = "DELETED";
                }
                $scope.edit.selectedVariation = undefined;
                $scope.recalculateRanksForVariations();
            });
        };

        $scope.setVariationRank = function(variation, increaseValue, $event, firstOrLast) {
            if (firstOrLast) {
                $event.stopPropagation();
            } else {
                var from = $scope.customization.variations.indexOf(variation);
                var to = personalizationsmarteditUtils.getValidRank($scope.customization.variations, variation, increaseValue);
                var variationsArr = $scope.customization.variations;
                if (to >= 0 && to < variationsArr.length) {
                    variationsArr.splice(to, 0, variationsArr.splice(from, 1)[0]);
                    $scope.recalculateRanksForVariations();
                }
            }
        };

        $scope.toogleVariationActive = function(variation) {
            variation.enabled = !variation.enabled;
            variation.status = variation.enabled ? PERSONALIZATION_MODEL_STATUS_CODES.ENABLED : PERSONALIZATION_MODEL_STATUS_CODES.DISABLED;
        };

        $scope.recalculateRanksForVariations = function() {
            $scope.customization.variations.forEach(function(part, index) {
                $scope.customization.variations[index].rank = index;
            });
        };

        $scope.editVariationAction = function(variation) {
            $scope.edit.selectedVariation = variation;
        };

        this.canSaveVariation = function() {
            return !isBlank($scope.edit.name) && ($scope.edit.isDefault || personalizationsmarteditTriggerService.isValidExpression($scope.edit.expression[0]));
        };

        $scope.isVariationSelected = function() {
            return angular.isDefined($scope.edit.selectedVariation);
        };

        $scope.getSegmentLenghtForVariation = function(variation) {
            var segments = personalizationsmarteditUtils.getSegmentTriggerForVariation(variation).segments || [];
            return segments.length;
        };

        $scope.getEnablementTextForVariation = function(variation) {
            return '(' + personalizationsmarteditUtils.getEnablementTextForVariation(variation, 'personalization.modal.customizationvariationmanagement.targetgrouptab') + ')';
        };

        $scope.getActivityActionTextForVariation = function(variation) {
            if (variation.enabled) {
                return $filter('translate')('personalization.modal.customizationvariationmanagement.targetgrouptab.variation.options.disable');
            } else {
                return $filter('translate')('personalization.modal.customizationvariationmanagement.targetgrouptab.variation.options.enable');
            }
        };

        $scope.getActivityStateForCustomization = function(customization) {
            return personalizationsmarteditUtils.getActivityStateForCustomization(customization);
        };

        $scope.getActivityStateForVariation = function(customization, variation) {
            return personalizationsmarteditUtils.getActivityStateForVariation(customization, variation);
        };

        $scope.getCommerceCustomizationText = function(variation) {
            var result = "";
            angular.forEach(variation.commerceCustomizations, function(propertyValue, propertyKey) {
                result += propertyValue + ' ' + $filter('translate')('personalization.modal.customizationvariationmanagement.targetgrouptab.commercecustomization.' + propertyKey);
            });
            return result;
        };

        $scope.toggleSliderFullscreen = function(enableFullscreen) {
            var modalObject = angular.element(".sliderPanelParentModal");
            var className = "modal-fullscreen";
            if (modalObject.hasClass(className) || enableFullscreen === false) {
                modalObject.removeClass(className);
            } else {
                modalObject.addClass(className);
            }
            $timeout((function() {
                yjQuery(window).resize();
            }), 0);
        };

        $scope.confirmDefaultTrigger = function(isDefault) {
            if (isDefault && personalizationsmarteditTriggerService.isValidExpression($scope.edit.expression[0])) {
                confirmationModalService.confirm({
                    description: 'personalization.modal.manager.targetgrouptab.defaulttrigger.content'
                }).then(function() {
                    $scope.edit.showExpression = false;
                }, function() {
                    $scope.edit.isDefault = false;
                });
            } else {
                $scope.edit.showExpression = !isDefault;
            }
        };

        $scope.isDefaultVariation = function(variation) {
            return personalizationsmarteditTriggerService.isDefault(variation.triggers);
        };

        var setSliderConfigForAdd = function() {
            self.sliderPanelConfiguration.modal.save.label = "personalization.modal.customizationvariationmanagement.targetgrouptab.addvariation";
            self.sliderPanelConfiguration.modal.save.isDisabledFn = function() {
                return !self.canSaveVariation();
            };
            self.sliderPanelConfiguration.modal.save.onClick = function() {
                self.addVariationClick();
            };
        };

        var setSliderConfigForEditing = function() {
            self.sliderPanelConfiguration.modal.save.label = "personalization.modal.customizationvariationmanagement.targetgrouptab.savechanges";
            self.sliderPanelConfiguration.modal.save.isDisabledFn = function() {
                return !self.canSaveVariation();
            };
            self.sliderPanelConfiguration.modal.save.onClick = function() {
                self.submitChangesClick();
            };
        };

        $scope.$watch('customization.statusBoolean', function() {
            $scope.customization.status = $scope.customization.statusBoolean ? PERSONALIZATION_MODEL_STATUS_CODES.ENABLED : PERSONALIZATION_MODEL_STATUS_CODES.DISABLED;
        }, true);

        $scope.$watch('edit.selectedVariation', function(variation) {
            if (variation) {
                setSliderConfigForEditing();
                $scope.edit.code = variation.code;
                $scope.edit.name = variation.name;
                $scope.edit.isDefault = personalizationsmarteditTriggerService.isDefault(variation.triggers);
                $scope.edit.showExpression = !$scope.edit.isDefault;

                $timeout((function() {
                    self.showSliderPanel();
                }), 0);
            } else {
                clearEditedVariationDetails();

                setSliderConfigForAdd();

                $timeout((function() {
                    self.hideSliderPanel();
                }), 0);
            }
        }, true);

    });
