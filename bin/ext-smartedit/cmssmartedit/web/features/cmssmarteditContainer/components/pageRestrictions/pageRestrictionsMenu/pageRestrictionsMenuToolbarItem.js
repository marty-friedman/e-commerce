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
angular.module('restrictionsMenuModule', ['cmsSmarteditServicesModule', 'restrictionsTableModule', 'restrictionsPageInfoModule', 'eventServiceModule', 'iframeClickDetectionServiceModule', 'pageRestrictionsModule', 'displayConditionsFacadeModule', 'smarteditServicesModule'])
    /**
     * @ngdoc directive
     * @name restrictionsMenuModule.directive:restrictionsMenuToolbarItem
     * @element restrictions-menu-toolbar-item
     * @restrict E
     *
     * @description
     * Component responsible for displaying the restriction menu.
     */
    .component('restrictionsMenuToolbarItem', {
        templateUrl: 'pageRestrictionsMenuToolbarItemTemplate.html',
        controller: ['assetsService', 'pageInfoService', 'pageListService', 'pageRestrictionsFacade', 'displayConditionsFacade',
            function(assetsService, pageInfoService, pageListService, pageRestrictionsFacade, displayConditionsFacade) {
                this.$onInit = function() {
                    pageInfoService.getPageUID().then(function(pageUID) {
                        pageRestrictionsFacade.getRestrictionsByPageUID(pageUID).then(function(restrictions) {
                            this.restrictions = restrictions;
                        }.bind(this));

                        pageListService.getPageById(pageUID).then(function(page) {

                            this.pageId = page && page.uid;
                            this.pageNameLabelI18nKey = "se.cms.label.page.name";
                            this.pageName = page && page.name;

                            displayConditionsFacade.isPagePrimary(this.pageId).then(function(isPrimary) {
                                this.pageIsPrimary = isPrimary;
                                if (!this.pageIsPrimary) {
                                    displayConditionsFacade.getPrimaryPageForVariationPage(this.pageId).then(function(primaryPageData) {
                                        this.associatedPrimaryPageName = primaryPageData.name;
                                    }.bind(this));
                                }
                            }.bind(this));

                            this.restrictionCriteria = pageRestrictionsFacade.getRestrictionCriteriaOptionFromPage(page);

                        }.bind(this));
                    }.bind(this));
                };
            }
        ]
    });
