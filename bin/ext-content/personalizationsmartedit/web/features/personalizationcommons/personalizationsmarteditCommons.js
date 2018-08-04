angular.module('personalizationsmarteditCommons', [
        'personalizationcommonsTemplates',
        'yjqueryModule',
        'alertServiceModule',
        'languageServiceModule',
        'seConstantsModule',
        'l10nModule',
        'catalogServiceModule'
    ])
    .constant('PERSONALIZATION_MODEL_STATUS_CODES', {
        ENABLED: 'ENABLED',
        DISABLED: 'DISABLED'
    })
    .constant('PERSONALIZATION_VIEW_STATUS_MAPPING_CODES', {
        ALL: 'ALL',
        ENABLED: 'ENABLED',
        DISABLED: 'DISABLED'
    })
    .constant('PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING', {
        0: {
            borderClass: 'personalizationsmarteditComponentSelected0',
            listClass: 'personalizationsmarteditComponentSelectedList0'
        },
        1: {
            borderClass: 'personalizationsmarteditComponentSelected1',
            listClass: 'personalizationsmarteditComponentSelectedList1'
        },
        2: {
            borderClass: 'personalizationsmarteditComponentSelected2',
            listClass: 'personalizationsmarteditComponentSelectedList2'
        },
        3: {
            borderClass: 'personalizationsmarteditComponentSelected3',
            listClass: 'personalizationsmarteditComponentSelectedList3'
        },
        4: {
            borderClass: 'personalizationsmarteditComponentSelected4',
            listClass: 'personalizationsmarteditComponentSelectedList4'
        },
        5: {
            borderClass: 'personalizationsmarteditComponentSelected5',
            listClass: 'personalizationsmarteditComponentSelectedList5'
        },
        6: {
            borderClass: 'personalizationsmarteditComponentSelected6',
            listClass: 'personalizationsmarteditComponentSelectedList6'
        },
        7: {
            borderClass: 'personalizationsmarteditComponentSelected7',
            listClass: 'personalizationsmarteditComponentSelectedList7'
        },
        8: {
            borderClass: 'personalizationsmarteditComponentSelected8',
            listClass: 'personalizationsmarteditComponentSelectedList8'
        },
        9: {
            borderClass: 'personalizationsmarteditComponentSelected9',
            listClass: 'personalizationsmarteditComponentSelectedList9'
        },
        10: {
            borderClass: 'personalizationsmarteditComponentSelected10',
            listClass: 'personalizationsmarteditComponentSelectedList10'
        },
        11: {
            borderClass: 'personalizationsmarteditComponentSelected11',
            listClass: 'personalizationsmarteditComponentSelectedList11'
        },
        12: {
            borderClass: 'personalizationsmarteditComponentSelected12',
            listClass: 'personalizationsmarteditComponentSelectedList12'
        },
        13: {
            borderClass: 'personalizationsmarteditComponentSelected13',
            listClass: 'personalizationsmarteditComponentSelectedList13'
        },
        14: {
            borderClass: 'personalizationsmarteditComponentSelected14',
            listClass: 'personalizationsmarteditComponentSelectedList14'
        }
    })
    .run(function($rootScope, PERSONALIZATION_MODEL_STATUS_CODES) {
        $rootScope.PERSONALIZATION_MODEL_STATUS_CODES = PERSONALIZATION_MODEL_STATUS_CODES;
    })
    .filter('statusNotDeleted', function(personalizationsmarteditUtils) {
        return function(value) {
            if (angular.isArray(value)) {
                return personalizationsmarteditUtils.getVisibleItems(value);
            }
            return value;
        };
    })
    .factory('personalizationsmarteditUtils', function($q, $filter, PERSONALIZATION_MODEL_STATUS_CODES, PERSONALIZATION_VIEW_STATUS_MAPPING_CODES, PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING, l10nFilter, catalogService) {
        var utils = {};

        utils.pushToArrayIfValueExists = function(array, key, value) {
            if (value) {
                array.push({
                    "key": key,
                    "value": value
                });
            }
        };

        utils.getVariationCodes = function(variations) {
            if ((typeof variations === 'undefined') || (variations === null)) {
                return [];
            }
            var allVariationsCodes = variations.map(function(elem) {
                return elem.code;
            }).filter(function(elem) {
                return typeof elem !== 'undefined';
            });
            return allVariationsCodes;
        };

        utils.getVariationKey = function(customizationId, variations) {
            if (customizationId === undefined || variations === undefined) {
                return [];
            }

            var allVariationsKeys = variations.map(function(variation) {
                return {
                    "variationCode": variation.code,
                    "customizationCode": customizationId,
                    "catalog": variation.catalog,
                    "catalogVersion": variation.catalogVersion
                };
            });
            return allVariationsKeys;
        };

        utils.getSegmentTriggerForVariation = function(variation) {
            var triggers = variation.triggers || [];
            var segmentTriggerArr = triggers.filter(function(trigger) {
                return trigger.type === "segmentTriggerData";
            });

            if (segmentTriggerArr.length === 0) {
                return {};
            }

            return segmentTriggerArr[0];
        };

        utils.isPersonalizationItemEnabled = function(item) {
            return item.status === PERSONALIZATION_MODEL_STATUS_CODES.ENABLED;
        };

        utils.getEnablementTextForCustomization = function(customization, keyPrefix) {
            keyPrefix = keyPrefix || "personalization";
            if (utils.isPersonalizationItemEnabled(customization)) {
                return $filter('translate')(keyPrefix + '.customization.enabled');
            } else {
                return $filter('translate')(keyPrefix + '.customization.disabled');
            }
        };

        utils.getEnablementTextForVariation = function(variation, keyPrefix) {
            keyPrefix = keyPrefix || "personalization";

            if (utils.isPersonalizationItemEnabled(variation)) {
                return $filter('translate')(keyPrefix + '.variation.enabled');
            } else {
                return $filter('translate')(keyPrefix + '.variation.disabled');
            }
        };

        utils.getEnablementActionTextForVariation = function(variation, keyPrefix) {
            keyPrefix = keyPrefix || "personalization";

            if (utils.isPersonalizationItemEnabled(variation)) {
                return $filter('translate')(keyPrefix + '.variation.options.disable');
            } else {
                return $filter('translate')(keyPrefix + '.variation.options.enable');
            }
        };

        utils.getActivityStateForCustomization = function(customization) {
            if (customization.status === PERSONALIZATION_MODEL_STATUS_CODES.ENABLED) {
                if (moment().isBetween(new Date(customization.enabledStartDate), new Date(customization.enabledEndDate), 'minute', '[]')) {
                    return "status-active";
                } else {
                    return "status-ignore";
                }
            } else {
                return "status-inactive";
            }
        };

        utils.getActivityStateForVariation = function(customization, variation) {
            if (variation.enabled) {
                return utils.getActivityStateForCustomization(customization);
            } else {
                return "status-inactive";
            }
        };

        utils.isItemVisible = function(item) {
            return item.status !== 'DELETED';
        };

        utils.getVisibleItems = function(items) {
            return items.filter(function(item) {
                return utils.isItemVisible(item);
            });
        };

        utils.getValidRank = function(items, item, increaseValue) {
            var from = items.indexOf(item);
            var delta = increaseValue < 0 ? -1 : 1;

            var increase = from + increaseValue;

            while (increase >= 0 && increase < items.length && !utils.isItemVisible(items[increase])) {
                increase += delta;
            }

            increase = increase >= items.length ? items.length - 1 : increase;
            increase = increase < 0 ? 0 : increase;

            return items[increase].rank;
        };

        utils.getStatusesMapping = function() {
            var statusesMapping = [];

            statusesMapping.push({
                code: PERSONALIZATION_VIEW_STATUS_MAPPING_CODES.ALL,
                text: 'personalization.context.status.all',
                modelStatuses: [PERSONALIZATION_MODEL_STATUS_CODES.ENABLED, PERSONALIZATION_MODEL_STATUS_CODES.DISABLED]
            });

            statusesMapping.push({
                code: PERSONALIZATION_VIEW_STATUS_MAPPING_CODES.ENABLED,
                text: 'personalization.context.status.enabled',
                modelStatuses: [PERSONALIZATION_MODEL_STATUS_CODES.ENABLED]
            });

            statusesMapping.push({
                code: PERSONALIZATION_VIEW_STATUS_MAPPING_CODES.DISABLED,
                text: 'personalization.context.status.disabled',
                modelStatuses: [PERSONALIZATION_MODEL_STATUS_CODES.DISABLED]
            });

            return statusesMapping;
        };

        utils.getClassForElement = function(index) {
            var wrappedIndex = index % Object.keys(PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING).length;
            return PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING[wrappedIndex].listClass;
        };

        utils.getLetterForElement = function(index) {
            var wrappedIndex = index % Object.keys(PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING).length;
            return String.fromCharCode('a'.charCodeAt() + wrappedIndex).toUpperCase();
        };

        utils.getCommerceCustomizationTooltip = function(variation, prefix, suffix) {
            prefix = prefix || "";
            suffix = suffix || "\n";
            var result = "";
            angular.forEach(variation.commerceCustomizations, function(propertyValue, propertyKey) {
                result += prefix + $filter('translate')('personalization.modal.manager.commercecustomization.' + propertyKey) + ": " + propertyValue + suffix;
            });
            return result;
        };

        utils.getCommerceCustomizationTooltipHTML = function(variation) {
            return utils.getCommerceCustomizationTooltip(variation, "<div>", "</div>");
        };

        utils.isItemFromCurrentCatalog = function(item, seData) {
            var cd = seData.seExperienceData.catalogDescriptor;
            return item.catalog === cd.catalogId && item.catalogVersion === cd.catalogVersion;
        };

        utils.hasCommerceActions = function(variation) {
            return variation.numberOfCommerceActions > 0;
        };

        utils.getCatalogVersionNameByUuid = function(catalogVersionUuid) {
            var deferred = $q.defer();
            catalogService.getCatalogVersionByUuid(catalogVersionUuid).then(function(catalogVersion) {
                deferred.resolve(l10nFilter(catalogVersion.catalogName) + ' (' + catalogVersion.version + ')');
            });
            return deferred.promise;
        };

        utils.getAndSetCatalogVersionNameL10N = function(customization) {
            utils.getCatalogVersionNameByUuid(customization.catalog + '\/' + customization.catalogVersion).then(
                function successCallback(response) {
                    customization.catalogVersionNameL10N = response;
                });
        };

        return utils;

    }).factory('personalizationsmarteditMessageHandler', function(alertService) {

        var messageHandler = {};
        messageHandler.sendInformation = function(informationMessage) {
            alertService.showInfo(informationMessage);
        };

        messageHandler.sendError = function(errorMessage) {
            alertService.showDanger(errorMessage);
        };

        messageHandler.sendWarning = function(warningMessage) {
            alertService.showWarning(warningMessage);
        };

        messageHandler.sendSuccess = function(successMessage) {
            alertService.showSuccess(successMessage);
        };

        return messageHandler;

    }).factory('personalizationsmarteditCommerceCustomizationService', function() {
        var nonCommerceActionTypes = ['cxCmsActionData'];

        var ccService = {};
        var types = [];

        var isNonCommerceAction = function(action) {
            return nonCommerceActionTypes.some(function(val) {
                return val === action.type;
            });
        };

        var isCommerceAction = function(action) {
            return !isNonCommerceAction(action);
        };

        var isTypeEnabled = function(type, seConfigurationData) {
            return (seConfigurationData !== undefined && seConfigurationData !== null && seConfigurationData[type.confProperty] === true);
        };

        ccService.registerType = function(item) {
            var type = item.type;
            var exists = false;

            types.forEach(function(val) {
                if (val.type === type) {
                    exists = true;
                }
            });

            if (!exists) {
                types.push(item);
            }
        };

        ccService.getAvailableTypes = function(seConfigurationData) {
            return types.filter(function(item) {
                return isTypeEnabled(item, seConfigurationData);
            });
        };

        ccService.isCommerceCustomizationEnabled = function(seConfigurationData) {
            var at = ccService.getAvailableTypes(seConfigurationData);
            return at.length > 0;
        };

        ccService.getNonCommerceActionsCount = function(variation) {
            return (variation.actions || []).filter(isNonCommerceAction).length;
        };

        ccService.getCommerceActionsCountMap = function(variation) {
            var result = {};

            (variation.actions || [])
            .filter(isCommerceAction)
                .forEach(function(action) {
                    var typeKey = action.type.toLowerCase();

                    var count = result[typeKey];
                    if (count === undefined) {
                        count = 1;
                    } else {
                        count += 1;
                    }
                    result[typeKey] = count;
                });

            return result;
        };

        ccService.getCommerceActionsCount = function(variation) {
            return (variation.actions || [])
                .filter(isCommerceAction).length;
        };

        return ccService;
    })
    //To remove when angular-ui-select would be upgraded to version > 0.19
    .directive('uisOpenClose', ['$parse', '$timeout', function($parse, $timeout) {
        return {
            restrict: 'A',
            require: 'uiSelect',
            link: function(scope, element, attrs, $select) {
                $select.onOpenCloseCallback = $parse(attrs.uisOpenClose);

                scope.$watch('$select.open', function(isOpen, previousState) {
                    if (isOpen !== previousState) {
                        $timeout(function() {
                            $select.onOpenCloseCallback(scope, {
                                isOpen: isOpen
                            });
                        });
                    }
                });
            }
        };
    }])
    .directive('negate', [
        function() {
            return {
                require: 'ngModel',
                link: function(scope, element, attribute, ngModelController) {
                    ngModelController.$isEmpty = function(value) {
                        return !!value;
                    };

                    ngModelController.$formatters.unshift(function(value) {
                        return !value;
                    });

                    ngModelController.$parsers.unshift(function(value) {
                        return !value;
                    });
                }
            };
        }
    ])
    .directive('personalizationCurrentElement', [
        function() {
            return {
                restrict: 'A',
                link: function(scope, element, attrs) {
                    if (attrs.personalizationCurrentElement) {
                        scope.$eval(attrs.personalizationCurrentElement)(element);
                    }
                }
            };
        }
    ]);
