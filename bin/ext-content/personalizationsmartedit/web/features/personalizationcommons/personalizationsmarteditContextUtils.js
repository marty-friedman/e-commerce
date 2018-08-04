angular.module('personalizationsmarteditContextUtilsModule', [])
    .factory('personalizationsmarteditContextUtils', function() {
        var self = this;

        var Personalization = function() { //NOSONAR
            this.enabled = false;
        };

        var Customize = function() { //NOSONAR
            this.enabled = false;
            this.selectedCustomization = null;
            this.selectedVariations = null;
            this.selectedComponents = null;
        };

        var CombinedView = function() { //NOSONAR
            this.enabled = false;
            this.selectedItems = null;
            this.customize = new Customize();
        };

        var SeData = function() { //NOSONAR
            this.pageId = null;
            this.seExperienceData = null;
            this.seConfigurationData = null;
            this.sePreviewData = null;
        };

        self.getContextObject = function() {
            return {
                personalization: new Personalization(),
                customize: new Customize(),
                combinedView: new CombinedView(),
                seData: new SeData()
            };
        };

        self.clearCustomizeContext = function(contexService) {
            var customize = contexService.getCustomize();
            customize.enabled = false;
            customize.selectedCustomization = null;
            customize.selectedVariations = null;
            customize.selectedComponents = null;
            contexService.setCustomize(customize);
        };

        self.clearCustomizeContextAndReloadPreview = function(iFrameUtils, contexService) {
            var selectedVariations = angular.copy(contexService.getCustomize().selectedVariations);
            self.clearCustomizeContext(contexService);
            if (angular.isObject(selectedVariations) && !angular.isArray(selectedVariations)) {
                iFrameUtils.clearAndReloadPreview();
            }
        };

        self.clearCombinedViewCustomizeContext = function(contextService) {
            var combinedView = contextService.getCombinedView();
            combinedView.customize.enabled = false;
            combinedView.customize.selectedCustomization = null;
            combinedView.customize.selectedVariations = null;
            combinedView.customize.selectedComponents = null;
            (combinedView.selectedItems || []).forEach(function(item) {
                delete item.highlighted;
            });
            contextService.setCombinedView(combinedView);
        };

        self.clearCombinedViewContext = function(contextService) {
            var combinedView = contextService.getCombinedView();
            combinedView.enabled = false;
            combinedView.selectedItems = null;
            contextService.setCombinedView(combinedView);
        };

        self.clearCombinedViewContextAndReloadPreview = function(iFrameUtils, contextService) {
            var cvEnabled = angular.copy(contextService.getCombinedView().enabled);
            var cvSelectedItems = angular.copy(contextService.getCombinedView().selectedItems);
            self.clearCombinedViewContext(contextService);
            if (cvEnabled && cvSelectedItems) {
                iFrameUtils.clearAndReloadPreview();
            }
        };

        return self;
    });
