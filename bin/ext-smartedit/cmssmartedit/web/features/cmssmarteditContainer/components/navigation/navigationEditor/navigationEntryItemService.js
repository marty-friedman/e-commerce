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
angular.module('navigationEntryItemServiceModule', ['yLoDashModule', 'smarteditServicesModule', 'itemTitleAbstractPageHandlerServiceModule', 'itemTitleAbstractCMSComponentHandlerServiceModule', 'itemTitleMediaHandlerServiceModule'])

    /**
     * @ngdoc service
     * @name navigationEntryItemServiceModule.service:navigationEntryItemService
     * @description
     * This service is used to retrieve component items by making a REST call to the items API.
     */
    .factory('navigationEntryItemService', function($q, $injector, $log, lodash, generateIdentifier) {

        var HANDLER_PREFIX = 'itemTitle';
        var HANDLER_SUFFIX = 'HandlerService';

        return {

            /**
             * @ngdoc method
             * @name navigationEntryItemServiceModule.service:navigationEntryItemService#finalizeNavigationEntries
             * @methodOf navigationEntryItemServiceModule.service:navigationEntryItemService
             *
             * @description
             * Assigns a generated id if not set
             * Assigns titles or name to entries based on the titles calculated form their respective entries by means of item super type specific strategies
             * 
             * @param {Object} entries A list of entries associated to a node.
             * @param {Object} uriParams The object that contains site UID, catalogId and catalogVersion.
             * @param {Boolean} setName when true, the calculated title is set in the name property, if false in the title property. Default value is false;
             * 
             */
            finalizeNavigationEntries: function(entries, uriParams, setName) {

                var promises = [];

                entries.forEach(function(entry) {

                    var clone = lodash.cloneDeep(entry);
                    delete clone.parent;
                    entry.id = entry.id || generateIdentifier();

                    if (!entry.itemSuperType) {
                        return;
                    }
                    var itemHandlerServiceStrategy = HANDLER_PREFIX + entry.itemSuperType + HANDLER_SUFFIX;
                    if (!$injector.has(itemHandlerServiceStrategy)) {
                        $log.error("handler not found for " + itemHandlerServiceStrategy);
                        return;
                    }

                    promises.push($injector.get(itemHandlerServiceStrategy).getItemTitleById(entry.itemId, uriParams).then(function(itemInfo) {
                        if (setName) {
                            this.name = itemInfo.title;
                        } else {
                            this.title = itemInfo.title;
                        }
                        this.itemType = itemInfo.itemType;
                    }.bind(entry)));
                });

                return $q.all(promises);
            }
        };

    });
