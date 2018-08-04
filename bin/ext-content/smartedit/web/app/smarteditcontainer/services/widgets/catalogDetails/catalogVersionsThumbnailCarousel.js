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
(function() {
    angular.module('catalogVersionsThumbnailCarouselModule', ['resourceLocationsModule'])
        .controller('catalogVersionsThumbnailCarouselController', function($location, STOREFRONT_PATH) {
            this.$onInit = function() {
                this.selectedVersion = this._getActiveVersion();
            };

            this.onClick = function() {
                $location.path(STOREFRONT_PATH
                    .replace(":siteId", this.siteId)
                    .replace(":catalogId", this.catalog.catalogId)
                    .replace(":catalogVersion", this.selectedVersion.version));
            };

            this._getActiveVersion = function() {
                return this.catalog.versions.find(function(catalogVersion) {
                    return catalogVersion.active;
                });
            };
        })

        /**
         * @ngdoc directive
         * @name catalogVersionsThumbnailCarouselModule.component:catalogVersionsThumbnailCarousel
         * @scope
         * @restrict E
         * @element catalog-versions-thumbnail-carousel
         *
         * @description
         * Component responsible for displaying a thumbnail of the provided catalog. When clicked,
         * it redirects to the storefront page for the catalog's active catalog version. 
         *
         * @param {< Object} catalog Object representing the current catalog. 
         * @param {< String} siteId The ID of the site associated with the provided catalog. 
         * */
        .component('catalogVersionsThumbnailCarousel', {
            templateUrl: 'catalogVersionsThumbnailCarouselTemplate.html',
            controller: 'catalogVersionsThumbnailCarouselController',
            bindings: {
                catalog: '<',
                siteId: '<'
            }
        });
})();
