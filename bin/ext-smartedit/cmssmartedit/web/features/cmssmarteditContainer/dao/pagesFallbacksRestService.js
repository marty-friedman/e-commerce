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
angular.module('pagesFallbacksRestServiceModule', ['resourceLocationsModule', 'smarteditServicesModule', 'yLoDashModule'])
    .service('pagesFallbacksRestService', function(restServiceFactory, lodash, PAGE_CONTEXT_SITE_ID, PAGE_CONTEXT_CATALOG, PAGE_CONTEXT_CATALOG_VERSION) {
        var PAGE_FALLBACKS_URI = '/cmswebservices/v1/sites/' + PAGE_CONTEXT_SITE_ID + '/catalogs/' + PAGE_CONTEXT_CATALOG + '/versions/' + PAGE_CONTEXT_CATALOG_VERSION + '/pages/:pageId/fallbacks';

        this.getFallbacksForPageId = function(pageId, params) {
            this._resource = this._resource || restServiceFactory.get(PAGE_FALLBACKS_URI);
            var extendedParams = lodash.assign({
                pageId: pageId
            }, params || {});

            return this._resource.get(extendedParams).then(function(response) {
                return response.uids;
            });
        };
    });
