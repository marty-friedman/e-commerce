angular.module('personalizationsmarteditContextualMenuServiceModule', [
        'personalizationsmarteditContextServiceModule',
        'personalizationsmarteditComponentHandlerServiceModule',
        'personalizationsmarteditCommons',
        'yjqueryModule'
    ])
    .factory('personalizationsmarteditContextualMenuService', function(personalizationsmarteditContextService, personalizationsmarteditComponentHandlerService, yjQuery, personalizationsmarteditUtils) {

        var contextualMenuService = {};
        var contextPersonalization;
        var contextCustomize;
        var contextCombinedView;
        var contextSeData;

        var refreshContext = function() {
            contextPersonalization = personalizationsmarteditContextService.getPersonalization();
            contextCustomize = personalizationsmarteditContextService.getCustomize();
            contextCombinedView = personalizationsmarteditContextService.getCombinedView();
            contextSeData = personalizationsmarteditContextService.getSeData();
        };

        var isCustomizeObjectValid = function(customize) {
            return angular.isObject(customize.selectedCustomization) && angular.isObject(customize.selectedVariations) && !angular.isArray(customize.selectedVariations);
        };

        var isContextualMenuEnabled = function() {
            return isCustomizeObjectValid(contextCustomize) || (contextCombinedView.enabled && isCustomizeObjectValid(contextCombinedView.customize));
        };

        var isElementHighlighted = function(config) {
            if (contextCombinedView.enabled) {
                return yjQuery.inArray(config.componentAttributes.smarteditContainerSourceId, contextCombinedView.customize.selectedComponents) > -1;
            } else {
                return yjQuery.inArray(config.componentAttributes.smarteditContainerSourceId, contextCustomize.selectedComponents) > -1;
            }
        };

        var isSlotInCurrentCatalog = function(config) {
            var slot = personalizationsmarteditComponentHandlerService.getParentSlotForComponent(config.element);
            var catalogUuid = personalizationsmarteditComponentHandlerService.getCatalogVersionUuid(slot);
            var experienceCV = contextSeData.seExperienceData.catalogDescriptor.catalogVersionUuid;
            return experienceCV === catalogUuid;
        };

        var isComponentInCurrentCatalog = function(configProperties) {
            var experienceCV = contextSeData.seExperienceData.catalogDescriptor.catalogVersionUuid;
            var componentCV = configProperties.smarteditCatalogVersionUuid;
            return experienceCV === componentCV;
        };

        var isSelectedCustomizationFromCurrentCatalog = function() {
            var customization = contextCustomize.selectedCustomization || contextCombinedView.customize.selectedCustomization;
            if (customization) {
                return personalizationsmarteditUtils.isItemFromCurrentCatalog(customization, personalizationsmarteditContextService.getSeData());
            }
            return false;
        };

        var isCustomizationFromCurrentCatalog = function(config) {
            var items = contextCombinedView.selectedItems || [];
            var foundItem = items.filter(function(item) {
                return item.customization.code === config.smarteditPersonalizationCustomizationId && item.variation.code === config.smarteditPersonalizationVariationId;
            });
            foundItem = foundItem.shift();
            if (foundItem) {
                return personalizationsmarteditUtils.isItemFromCurrentCatalog(foundItem.customization, personalizationsmarteditContextService.getSeData());
            }
            return false;
        };

        contextualMenuService.isContextualMenuAddItemEnabled = function(config) {
            refreshContext();
            var isEnabled = isContextualMenuEnabled();
            isEnabled = isEnabled && (!isElementHighlighted(config));
            isEnabled = isEnabled && isSlotInCurrentCatalog(config);
            isEnabled = isEnabled && isSelectedCustomizationFromCurrentCatalog();
            return isEnabled;
        };

        contextualMenuService.isContextualMenuEditItemEnabled = function(config) {
            refreshContext();
            var isEnabled = contextPersonalization.enabled;
            var configProperties = angular.fromJson(config.properties);
            isEnabled = isEnabled && angular.isDefined(configProperties.smarteditPersonalizationActionId);
            isEnabled = isEnabled && isSlotInCurrentCatalog(config);
            isEnabled = isEnabled && (isSelectedCustomizationFromCurrentCatalog() || isCustomizationFromCurrentCatalog(configProperties));
            return isEnabled;
        };

        contextualMenuService.isContextualMenuDeleteItemEnabled = contextualMenuService.isContextualMenuEditItemEnabled;

        contextualMenuService.isContextualMenuShowActionListEnabled = function(config) {
            refreshContext();
            var configProperties = angular.fromJson(config.properties);
            var isEnabled = angular.isDefined(configProperties.smarteditPersonalizationActionId);
            isEnabled = isEnabled && contextCombinedView.enabled;
            isEnabled = isEnabled && !contextCombinedView.customize.selectedCustomization;
            return isEnabled;
        };

        contextualMenuService.isContextualMenuInfoItemEnabled = function() {
            refreshContext();
            var isEnabled = contextPersonalization.enabled;
            isEnabled = isEnabled && !angular.isObject(contextCustomize.selectedVariations);
            isEnabled = isEnabled || angular.isArray(contextCustomize.selectedVariations);
            isEnabled = isEnabled && !contextCombinedView.enabled;

            return isEnabled;
        };

        contextualMenuService.isContextualMenuEditComponentItemEnabled = function(config) {
            refreshContext();
            var isEnabled = contextPersonalization.enabled;
            var configProperties = angular.fromJson(config.properties);
            isEnabled = isEnabled && !contextCombinedView.enabled && isComponentInCurrentCatalog(configProperties);
            return isEnabled;
        };

        return contextualMenuService;
    });
