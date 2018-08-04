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
angular.module('seDropdownModule', [
        'smarteditServicesModule',
        'eventServiceModule',
        'optionsDropdownPopulatorModule',
        'uriDropdownPopulatorModule',
        'functionsModule',
        'seConstantsModule'
    ])
    .constant('LINKED_DROPDOWN', 'LinkedDropdown')
    .constant('CLICK_DROPDOWN', 'ClickDropdown')
    .constant('DROPDOWN_IMPLEMENTATION_SUFFIX', 'DropdownPopulator')

    /**
     * @ngdoc service
     * @name seDropdownModule.service:SEDropdownService
     *
     * @description
     * The SEDropdownService handles the initialization and the rendering of the {@link seDropdownModule.directive:seDropdown seDropdown} Angular component.
     */
    .factory('SEDropdownService', function(
        $q,
        $injector,
        isBlank,
        isFunctionEmpty,
        LINKED_DROPDOWN,
        CLICK_DROPDOWN,
        DROPDOWN_IMPLEMENTATION_SUFFIX,
        systemEventService,
        getKeyHoldingDataFromResponse,
        VALIDATION_MESSAGE_TYPES) {

        /**
         * @constructor
         */
        var SEDropdownService = function(conf) {
            this.field = conf.field;
            this.qualifier = conf.qualifier;
            this.model = conf.model;
            this.id = conf.id;
            this.onClickOtherDropdown = conf.onClickOtherDropdown;
            this.items = [];
            this.getApi = conf.getApi;
            this.setYSelectAPI = function($api) {
                this.ySelectAPI = $api;
            };

            /**
             * @ngdoc object
             * @name seDropdownModule.object:seDropdownApi
             * @description
             * The ySelector's api object exposing public functionality
             */
            this.$api = {
                /**
                 * @ngdoc method
                 * @name setResultsHeaderTemplateUrl
                 * @methodOf seDropdownModule.object:seDropdownApi
                 * @description
                 * A method that sets the URL of the template used to display results the dropdown. 
                 *
                 * @param {String} resultHeadersTemplateUrl The URL of the template used to display the dropdown result headers section.
                 */
                setResultsHeaderTemplateUrl: function(resultsHeaderTemplateUrl) {
                    this.resultsHeaderTemplateUrl = resultsHeaderTemplateUrl;
                }.bind(this),
                /**
                 * @ngdoc method
                 * @name setResultsHeaderTemplate
                 * @methodOf seDropdownModule.object:seDropdownApi
                 * @description
                 * A method that sets the template used to display results the dropdown. 
                 *
                 * @param {String} resultsHeaderTemplate The template used to display the dropdown result headers section.
                 */
                setResultsHeaderTemplate: function(resultsHeaderTemplate) {
                    this.resultsHeaderTemplate = resultsHeaderTemplate;
                }.bind(this)
            };
        };

        SEDropdownService.prototype._respondToChange = function(key, handle) {
            if (this.field.dependsOn && this.field.dependsOn.split(",").indexOf(handle.qualifier) > -1) {
                this.selection = handle.optionObject;
                if (this.reset) {
                    this.reset();
                }
            }
        };


        SEDropdownService.prototype._respondToOtherClicks = function(key, qualifier) {
            if (this.field.qualifier !== qualifier && typeof this.onClickOtherDropdown === "function") {
                this.onClickOtherDropdown(key, qualifier);
            }
        };

        /**
         * @ngdoc method
         * @name seDropdownModule.service:SEDropdownService#triggerAction
         * @methodOf seDropdownModule.service:SEDropdownService
         *
         * @description
         * Publishes an asynchronous event for the currently selected option
         */
        SEDropdownService.prototype.triggerAction = function() {
            var selectedObj = this.items.filter(function(option) {
                return option.id === this.model[this.qualifier];
            }.bind(this))[0];
            var handle = {
                qualifier: this.qualifier,
                optionObject: selectedObj
            };

            if (this.ySelectAPI) {
                this.ySelectAPI.setValidationState(getState(this.field));
            }

            systemEventService.sendAsynchEvent(this.eventId, handle);
        };

        SEDropdownService.prototype.onClick = function() {
            systemEventService.sendAsynchEvent(this.clickEventKey, this.field.qualifier);
        };

        /**
         * @ngdoc method
         * @name seDropdownModule.service:SEDropdownService#fetchAll
         * @methodOf seDropdownModule.service:SEDropdownService
         *
         * @description
         * Uses the configured implementation of {@link DropdownPopulatorInterfaceModule.DropdownPopulatorInterface DropdownPopulatorInterface}
         * to populate the seDropdown items using {@link DropdownPopulatorInterfaceModule.DropdownPopulatorInterface:populate populate}
         * 
         * @returns {Promise} A promise that resolves to a list of options to be populated
         */
        SEDropdownService.prototype.fetchAll = function(search) {
            return this.populator.populate({
                field: this.field,
                model: this.model,
                selection: this.selection,
                search: search
            }).then(function(options) {
                this.items = options;
                return this.items;
            }.bind(this));

        };

        /**
         * @ngdoc method
         * @name seDropdownModule.service:SEDropdownService#fetchEntity
         * @methodOf seDropdownModule.service:SEDropdownService
         *
         * @description
         * Uses the configured implementation of {@link DropdownPopulatorInterfaceModule.DropdownPopulatorInterface DropdownPopulatorInterface}
         * to populate a single item {@link DropdownPopulatorInterfaceModule.DropdownPopulatorInterface:getItem getItem}
         * 
         * @param {String} id The id of the option to fetch
         * 
         * @returns {Promise} A promise that resolves to the option that was fetched
         */
        SEDropdownService.prototype.fetchEntity = function(id) {
            return this.populator.getItem({
                field: this.field,
                id: id,
                model: this.model
            });
        };

        /**
         * @ngdoc method
         * @name seDropdownModule.service:SEDropdownService#fetchPage
         * @methodOf seDropdownModule.service:SEDropdownService
         * 
         * @param {String} search The search to filter options by
         * @param {Number} pageSize The number of items to be returned
         * @param {Number} currentPage The page to be returned
         *
         * @description
         * Uses the configured implementation of {@link DropdownPopulatorInterfaceModule.DropdownPopulatorInterface DropdownPopulatorInterface}
         * to populate the seDropdown items using {@link DropdownPopulatorInterfaceModule.DropdownPopulatorInterface:fetchPage fetchPage}
         * 
         * @returns {Promise} A promise that resolves to an object containing the array of items and paging information
         */
        SEDropdownService.prototype.fetchPage = function(search, pageSize, currentPage) {
            return this.populator.fetchPage({
                field: this.field,
                model: this.model,
                selection: this.selection,
                search: search,
                pageSize: pageSize,
                currentPage: currentPage
            }).then(function(page) {
                var holderProperty = getKeyHoldingDataFromResponse(page);
                page.results = page[holderProperty];

                delete page[holderProperty];
                this.items = page.results;
                return page;
            }.bind(this));
        };

        /**
         * @ngdoc method
         * @name seDropdownModule.service:SEDropdownService#init
         * @methodOf seDropdownModule.service:SEDropdownService
         *
         * @description
         * Initializes the seDropdown with a configured dropdown populator based on field attributes used when instantiating
         * the {@link  seDropdownModule.service:SEDropdownService}.
         */
        SEDropdownService.prototype.init = function() {
            initializeAPI(this);
            this.isMultiDropdown = this.field.collection ? this.field.collection : false;

            this.triggerAction = this.triggerAction.bind(this);

            var populatorName;

            this.eventId = (this.id || '') + LINKED_DROPDOWN;
            this.clickEventKey = (this.id || '') + CLICK_DROPDOWN;

            if (this.field.dependsOn) {
                systemEventService.registerEventHandler(this.eventId, this._respondToChange.bind(this));
            }

            systemEventService.registerEventHandler(this.clickEventKey, this._respondToOtherClicks.bind(this));

            if (this.field.options && this.field.uri) {
                throw "se.dropdown.contains.both.uri.and.options";
            } else if (this.field.options) {
                populatorName = "options" + DROPDOWN_IMPLEMENTATION_SUFFIX;
                this.isPaged = false;
            } else if (this.field.uri) {
                populatorName = "uri" + DROPDOWN_IMPLEMENTATION_SUFFIX;
                this.isPaged = this.field.paged ? this.field.paged : false;
            } else if (this.field.propertyType) {
                if ($injector.has(this.field.propertyType + DROPDOWN_IMPLEMENTATION_SUFFIX)) {
                    populatorName = this.field.propertyType + DROPDOWN_IMPLEMENTATION_SUFFIX;
                    this.isPaged = isPopulatorPaged(populatorName);
                } else {
                    throw "sedropdown.no.populator.found";
                }
            } else if ($injector.has(this.field.cmsStructureType + DROPDOWN_IMPLEMENTATION_SUFFIX)) {
                populatorName = this.field.cmsStructureType + DROPDOWN_IMPLEMENTATION_SUFFIX;
                this.isPaged = this.field.paged ? this.field.paged : false;
            } else {
                if ($injector.has(this.field.smarteditComponentType + this.field.qualifier + DROPDOWN_IMPLEMENTATION_SUFFIX)) {
                    populatorName = this.field.smarteditComponentType + this.field.qualifier + DROPDOWN_IMPLEMENTATION_SUFFIX;
                } else if ($injector.has(this.field.smarteditComponentType + DROPDOWN_IMPLEMENTATION_SUFFIX)) {
                    populatorName = this.field.smarteditComponentType + DROPDOWN_IMPLEMENTATION_SUFFIX;
                } else {
                    throw "se.dropdown.no.populator.found";
                }
                this.isPaged = isPopulatorPaged(populatorName);
            }

            this.populator = $injector.get(populatorName);

            this.fetchStrategy = {
                fetchEntity: this.fetchEntity.bind(this)
            };

            if (this.isPaged) {
                this.fetchStrategy.fetchPage = this.fetchPage.bind(this);
            } else {
                this.fetchStrategy.fetchAll = this.fetchAll.bind(this);
            }

            this.initialized = true;

        };

        var getState = function(field) {
            return (field.hasError) ? VALIDATION_MESSAGE_TYPES.VALIDATION_ERROR :
                (field.hasWarnings) ? VALIDATION_MESSAGE_TYPES.WARNING : undefined;
        };

        var isPopulatorPaged = function(populatorName) {
            var populator = $injector.get(populatorName);
            return populator.isPaged && populator.isPaged();
        };

        var initializeAPI = function(seDropdown) {
            if (typeof seDropdown.getApi === 'function') {
                seDropdown.getApi({
                    $api: seDropdown.$api
                });
            }
        }.bind(this);

        return SEDropdownService;

    })

    /**
     * @ngdoc directive
     * @name seDropdownModule.directive:seDropdown
     * @scope
     * @restrict E
     * @element se-dropdown
     *
     * @description
     * This directive generates a custom dropdown (standalone or dependent on another one) for the {@link genericEditorModule.service:GenericEditor genericEditor}.
     * It is an implementation of the PropertyEditorTemplate {@link editorFieldMappingServiceModule.service:PropertyEditorTemplate contract}.
     * <br/>{@link editorFieldMappingServiceModule.service:editorFieldMappingService editorFieldMappingService} maps seDropdown by default to the "EditableDropdown" cmsStructureType.
     * <br/>The dropdown will be configured and populated based on the field structure retrieved from the Structure API.
     * The following is an example of the 4 possible field structures that can be returned by the Structure API for seDropdown to work:
     * <pre>
     * [
     * ...
     * {
     *		cmsStructureType: "EditableDropdown",
     *		qualifier: "someQualifier1",
     *		i18nKey: 'i18nkeyForsomeQualifier1',
     *		idAttribute: "id",
     *		labelAttributes: ["label"],
     *		paged: false,
     *		options: [{
     *      	id: '1',
     *      	label: 'option1'
     *      	}, {
     *      	id: '2',
     *      	label: 'option2'
     *      	}, {
     *      	id: '3',
     *      	label: 'option3'
     *      }],
     * }, {
     *		cmsStructureType: "EditableDropdown",
     *		qualifier: "someQualifier2",
     *		i18nKey: 'i18nkeyForsomeQualifier2',
     *		idAttribute: "id",
     *		labelAttributes: ["label"],
     *		paged: false,
     *		uri: '/someuri',
     *		dependsOn: 'someQualifier1'
     * }, {
     *		cmsStructureType: "EditableDropdown",
     *		qualifier: "someQualifier2",
     *		i18nKey: 'i18nkeyForsomeQualifier2',
     *		idAttribute: "id",
     *		labelAttributes: ["label"],
     *		paged: false,
     * }, {
     *		cmsStructureType: "EditableDropdown",
     *		qualifier: "someQualifier3",
     *		i18nKey: 'i18nkeyForsomeQualifier3',
     *		idAttribute: "id",
     *		labelAttributes: ["label"],
     *		paged: false,
     *		propertyType: 'somePropertyType',
     * }
     * ...
     * ]
     * </pre>
     * 
     * <br/>If uri, options and propertyType are not set, then seDropdown will look for an implementation of {@link DropdownPopulatorInterfaceModule.DropdownPopulatorInterface DropdownPopulatorInterface} with the following AngularJS recipe name:
     * <pre>smarteditComponentType + qualifier + "DropdownPopulator"</pre>
     * and default to:
     * <pre>smarteditComponentType + "DropdownPopulator"</pre>
     * If no custom populator can be found, an exception will be raised.
     * <br/><br/>For the above example, since someQualifier2 will depend on someQualifier1, then if someQualifier1 is changed, then the list of options
     * for someQualifier2 is populated by calling the populate method of {@link uriDropdownPopulatorModule.service:uriDropdownPopulator uriDropdownPopulator}.
     * 
     * @param {= Object} field The field description of the field being edited as defined by the structure API described in {@link genericEditorModule.service:GenericEditor genericEditor}.
     * @param {Array =} field.options An array of options to be populated.
     * @param {String =} field.uri The uri to fetch the list of options from a REST call, especially if the dropdown is dependent on another one.
     * @param {String =} field.propertyType If a propertyType is defined, the seDropdown will use the populator associated to it with the following AngularJS recipe name : <pre>propertyType + "DropdownPopulator"</pre>.
     * @param {String =} field.dependsOn The qualifier of the parent dropdown that this dropdown depends on.
     * @param {String =} field.idAttribute The name of the id attribute to use when populating dropdown items.
     * @param {Array =} field.labelAttributes An array of attributes to use when determining the label for each item in the dropdown
     * @param {Boolean =} field.paged A boolean to determine if we are in paged mode as opposed to retrieving all items at once.
     * @param {= String} qualifier If the field is not localized, this is the actual field.qualifier, if it is localized, it is the language identifier such as en, de...
     * @param {= Object} model If the field is not localized, this is the actual full parent model object, if it is localized, it is the language map: model[field.qualifier].
     * @param {= String} id An identifier of the generated DOM element.
     * @param {< String =} itemTemplateUrl the path to the template that will be used to display items in both the dropdown menu and the selection.
     * @param {& Function =} getApi Exposes the seDropdown's api object
     */
    .directive('seDropdown', function($rootScope, SEDropdownService, CONTEXT_CATALOG, CONTEXT_CATALOG_VERSION) {
        return {
            templateUrl: 'seDropdownTemplate.html',
            restrict: 'E',
            transclude: true,
            replace: false,
            scope: {
                field: '=',
                qualifier: '=',
                model: '=',
                id: '=',
                itemTemplateUrl: '<?',
                getApi: '&?'
            },
            link: function($scope) {

                $scope.field.params = $scope.field.params || {};
                $scope.field.params.catalogId = $scope.field.params.catalogId || CONTEXT_CATALOG;
                $scope.field.params.catalogVersion = $scope.field.params.catalogVersion || CONTEXT_CATALOG_VERSION;

                $scope.onClickOtherDropdown = function() {
                    $scope.closeSelect();
                };

                $scope.closeSelect = function() {
                    var uiSelectCtrl = $scope.getUiSelectCtrl();
                    if (uiSelectCtrl) {
                        uiSelectCtrl.open = false;
                    }
                };

                $scope.getUiSelectCtrl = function() {
                    var uiSelectId = "#" + $scope.field.qualifier + "-selector";
                    return angular.element(uiSelectId).controller("uiSelect");
                };

                $scope.dropdown = new SEDropdownService({
                    field: $scope.field,
                    qualifier: $scope.qualifier,
                    model: $scope.model,
                    id: $scope.id,
                    onClickOtherDropdown: $scope.onClickOtherDropdown,
                    getApi: $scope.getApi
                });

                $scope.dropdown.init();

            }
        };
    });
