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
/**
 * @ngdoc overview
 * @name genericEditorModule
 */
angular.module('genericEditorModule', ['yjqueryModule', 'genericEditorTabModule', 'smarteditServicesModule', 'functionsModule', 'eventServiceModule', 'coretemplates', 'translationServiceModule', 'languageServiceModule', 'experienceInterceptorModule', 'dateTimePickerModule', 'seBooleanModule', 'fetchEnumDataHandlerModule', 'seRichTextFieldModule', 'seValidationMessageParserModule', 'seDropdownModule', 'editorFieldMappingServiceModule', 'genericEditorTabServiceModule', 'genericEditorStackServiceModule', 'genericEditorBreadcrumbModule', 'yLoDashModule', 'seConstantsModule'])
    /**
     * @deprecated since 6.5 use GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT.
     * @ngdoc object
     * @name genericEditorModule.object:GENERIC_EDITOR_UNRELATED_VALIDATION_ERRORS_EVENT
     * @description
     * Event to notify GenericEditor about errors that can be relevant to it.
     * Any GenericEditor may receive notification from another GenericEditor that the latter received validation errors not relevant for themselves
     * In such a situation every GenericEditor will try and see if errors are relevant to them.
     */
    .constant('GENERIC_EDITOR_UNRELATED_VALIDATION_ERRORS_EVENT', 'UnrelatedValidationErrors')
    /**
     * @ngdoc object
     * @name genericEditorModule.object:GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT
     * @description
     * Event to notify GenericEditor about messages (errors, warnings) that can be relevant to it.
     * Any GenericEditor may receive notification from another GenericEditor that the latter received validation messages (errors, warnings) not relevant for themselves.
     * In such a situation every GenericEditor will try and see if messages are relevant to them.
     *
     * @param {Object} payload A map that contains the 'messages' and optional 'targetGenericEditorId' properties.
     * @param {Array} payload.messages List of validation messages (errors, warnings)
     * @param {String=} payload.targetGenericEditorID The id of target generic editor. Allows to send a list of messages to a specific generic editor. Otherwise the list of messages will be delivered to all generic editors.
     *
     * @example systemEventService.sendAsynchEvent(GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT, {messages: unrelatedValidationMessages, targetGenericEditorId: 'optional-id'});
     */
    .constant('GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT', 'UnrelatedValidationMessagesEvent')
    /**
     * @ngdoc object
     * @name genericEditorModule.object:GENERIC_EDITOR_LOADED_EVENT
     * @description
     * Event to notify subscribers that GenericEditor is loaded.
     */
    .constant('GENERIC_EDITOR_LOADED_EVENT', 'genericEditorLoadedEvent')
    .run(function(editorFieldMappingService) {
        editorFieldMappingService._registerDefaultFieldMappings();
    })
    /**
     * @ngdoc service
     * @name genericEditorModule.service:GenericEditor
     * @description
     * The Generic Editor is a class that makes it possible for SmartEdit users (CMS managers, editors, etc.) to edit components in the SmartEdit interface.
     * The Generic Editor class is used by the {@link genericEditorModule.directive:genericEditor genericEditor} directive.
     * The genericEditor directive makes a call either to a Structure API or, if the Structure API is not available, it reads the data from a local structure to request the information that it needs to build an HTML form.
     * It then requests the component by its type and ID from the Content API. The genericEditor directive populates the form with the data that is has received.
     * The form can now be used to edit the component. The modified data is saved using the Content API if it is provided else it would return the form data itself.
     * <br/><br/>
     * <strong>The structure and the REST structure API</strong>.
     * <br/>
     * The constructor of the {@link genericEditorModule.service:GenericEditor GenericEditor} must be provided with the pattern of a REST Structure API, which must contain the string  ":smarteditComponentType", or with a local data structure.
     * If the pattern, Structure API, or the local structure is not provided, the Generic Editor will fail. If the Structure API is used, it must return a JSON payload that holds an array within the attributes property.
     * If the actual structure is used, it must return an array. Each entry in the array provides details about a component property to be displayed and edited. The following details are provided for each property:
     *
     *<ul>
     * <li><strong>qualifier:</strong> Name of the property.
     * <li><strong>i18nKey:</strong> Key of the property label to be translated into the requested language.
     * <li><strong>editable:</strong> Boolean that indicates if a property is editable or not. The default value is true.
     * <li><strong>localized:</strong> Boolean that indicates if a property is localized or not. The default value is false.
     * <li><strong>required:</strong> Boolean that indicates if a property is mandatory or not. The default value is false.
     * <li><strong>cmsStructureType:</strong> Value that is used to determine which form widget (property editor) to display for a specified property.
     * The selection is based on an extensible strategy mechanism owned by {@link editorFieldMappingServiceModule.service:editorFieldMappingService editorFieldMappingService}.
     * <li><strong>cmsStructureEnumType:</strong> The qualified name of the Enum class when cmsStructureType is "Enum"
     * </li>
     * <ul><br/>
     * 
     * <b>Note:</b><br/>
     * The generic editor has a tabset within. This allows it to display complex types in an organized and clear way. By default, all fields are stored 
     * in the default tab, and if there is only one tab the header is hidden. The selection and configuration of where each field resides is
     * controlled by the {@link editorFieldMappingServiceModule.service:editorFieldMappingService editorFieldMappingService}. Similarly, the rendering 
     * of tabs can be customized with the {@link genericEditorTabServiceModule.service:genericEditorTabService genericEditorTabService}.
     * <br />
     * <br />
     * 
     * There are two options when you use the Structure API. The first option is to use an API resource that returns the structure object. 
     * The following is an example of the JSON payload that is returned by the Structure API in this case:
     * <pre>
     * {
     *     attributes: [{
     *         cmsStructureType: "ShortString",
     *         qualifier: "someQualifier1",
     *         i18nKey: 'i18nkeyForsomeQualifier1',
     *         localized: false
     *     }, {
     *         cmsStructureType: "LongString",
     *         qualifier: "someQualifier2",
     *         i18nKey: 'i18nkeyForsomeQualifier2',
     *         localized: false
     *    }, {
     *         cmsStructureType: "RichText",
     *         qualifier: "someQualifier3",
     *         i18nKey: 'i18nkeyForsomeQualifier3',
     *         localized: true,
     *         required: true
     *     }, {
     *         cmsStructureType: "Boolean",
     *         qualifier: "someQualifier4",
     *         i18nKey: 'i18nkeyForsomeQualifier4',
     *         localized: false
     *     }, {
     *         cmsStructureType: "DateTime",
     *         qualifier: "someQualifier5",
     *         i18nKey: 'i18nkeyForsomeQualifier5',
     *         localized: false
     *     }, {
     *         cmsStructureType: "Media",
     *         qualifier: "someQualifier6",
     *         i18nKey: 'i18nkeyForsomeQualifier6',
     *         localized: true,
     *         required: true
     *     }, {
     *         cmsStructureType: "Enum",
     *         cmsStructureEnumType:'de.mypackage.Orientation'
     *         qualifier: "someQualifier7",
     *         i18nKey: 'i18nkeyForsomeQualifier7',
     *         localized: true,
     *         required: true
     *     }]
     * }
     * </pre><br/>
     * The second option is to use an API resource that returns a list of structures. In this case, the generic editor will select the first element from the list and use it to display its attributes.
     * The generic editor expects the structures to be in one of the two fields below.  
     * <pre>
     * {
     *     structures: [{}, {}]
     * }
     *
     * or
     *
     * {
     *     componentTypes: [{}, {}]
     * }
     * </pre>
     * If the list has more than one element, the Generic Editor will throw an exception, otherwise it will get the first element on the list. 
     * The following is an example of the JSON payload that is returned by the Structure API in this case:
     * <pre>
     * {
     *     structures: [
     *         {
     *             attributes: [{
     *                 		cmsStructureType: "ShortString",
     *                 		qualifier: "someQualifier1",
     *                 		i18nKey: 'i18nkeyForsomeQualifier1',
     *                 		localized: false
     *             		}, {
     *                 		cmsStructureType: "LongString",
     *                 		qualifier: "someQualifier2",
     *                 		i18nKey: 'i18nkeyForsomeQualifier2',
     *                 		localized: false
     *         	   		}]
     *         }
     *     ]
     * }
     * </pre>
     * <pre>
     * {
     *     componentTypes: [
     *         {
     *             attributes: [{
     *                 		cmsStructureType: "ShortString",
     *                 		qualifier: "someQualifier1",
     *                 		i18nKey: 'i18nkeyForsomeQualifier1',
     *                 		localized: false
     *             		}, {
     *                 		cmsStructureType: "LongString",
     *                 		qualifier: "someQualifier2",
     *                 		i18nKey: 'i18nkeyForsomeQualifier2',
     *                 		localized: false
     *         	   		}]
     *         }
     *     ]
     * }
     * </pre>
     * The following is an example of the expected format of a structure:
     * <pre>
     *    [{
     *         cmsStructureType: "ShortString",
     *         qualifier: "someQualifier1",
     *         i18nKey: 'i18nkeyForsomeQualifier1',
     *         localized: false
     *     }, {
     *         cmsStructureType: "LongString",
     *         qualifier: "someQualifier2",
     *         i18nKey: 'i18nkeyForsomeQualifier2',
     *         editable: false,
     *         localized: false
     *    }, {
     *         cmsStructureType: "RichText",
     *         qualifier: "someQualifier3",
     *         i18nKey: 'i18nkeyForsomeQualifier3',
     *         localized: true,
     *         required: true
     *     }, {
     *         cmsStructureType: "Boolean",
     *         qualifier: "someQualifier4",
     *         i18nKey: 'i18nkeyForsomeQualifier4',
     *         localized: false
     *     }, {
     *         cmsStructureType: "DateTime",
     *         qualifier: "someQualifier5",
     *         i18nKey: 'i18nkeyForsomeQualifier5',
     *         editable: false,
     *         localized: false
     *     }, {
     *         cmsStructureType: "Media",
     *         qualifier: "someQualifier6",
     *         i18nKey: 'i18nkeyForsomeQualifier6',
     *         localized: true,
     *         required: true
     *     }, {
     *         cmsStructureType: "Enum",
     *         cmsStructureEnumType:'de.mypackage.Orientation'
     *         qualifier: "someQualifier7",
     *         i18nKey: 'i18nkeyForsomeQualifier7',
     *         localized: true,
     *         required: true
     *     }]
     * </pre>
     * 
     * <strong>The REST CRUD API</strong>, is given to the constructor of {@link genericEditorModule.service:GenericEditor GenericEditor}.
     * The CRUD API must support GET and PUT of JSON payloads.
     * The PUT method must return the updated payload in its response. Specific to the GET and PUT, the payload must fulfill the following requirements:
     * <ul>
     * 	<li>DateTime types: Must be serialized as long timestamps.</li>
     * 	<li>Media types: Must be serialized as identifier strings.</li>
     * 	<li>If a cmsStructureType is localized, then we expect that the CRUD API returns a map containing the type (string or map) and the map of values, where the key is the language and the value is the content that the type returns.</li>
     * </ul>
     *
     * The following is an example of a localized payload:
     * <pre>
     * {
     *    content: {
     * 		'en': 'content in english',
     * 		'fr': 'content in french',
     * 		'hi': 'content in hindi'
     * 	  }
     * }
     * </pre>
     *
     * <br/><br/>
     *
     * If a validation warning or error occurs, the PUT method of the REST CRUD API will return a validation warning/error object that contains an array of validation messages. The information returned for each validation message is as follows:
     * <ul>
     * 	<li><strong>subject:</strong> The qualifier that has the error</li>
     * 	<li><strong>message:</strong> The error message to be displayed</li>
     * 	<li><strong>type:</strong> The type of message returned. This is of the type ValidationError or Warning.</li>
     * 	<li><strong>language:</strong> The language the error needs to be associated with. If no language property is provided, a match with regular expression /(Language: \[)[a-z]{2}\]/g is attempted from the message property. As a fallback, it implies that the field is not localized.</li>
     * </ul>
     *
     * The following code is an example of an error response object:
     * <pre>
     * {
     *    errors: [{
     *		  subject: 'qualifier1',
     *		  message: 'error message for qualifier',
     *		  type: 'ValidationError'
     *	  }, {
     *		  subject: 'qualifier2',
     *		  message: 'error message for qualifier2 language: [fr]',
     *		  type: 'ValidationError'
     *    }, {
     *		  subject: 'qualifier3',
     *		  message: 'error message for qualifier2',
     *		  type: 'ValidationError'
     *    }, {
     *        subject: 'qualifier4',
     *        message: 'warning message for qualifier4',
     *        type: 'Warning'
     *    }]
     * }
     *
     * </pre>
     *
     * Whenever any sort of dropdown is used in one of the cmsStructureType widgets, it is advised using {@link genericEditorModule.service:GenericEditor#methods_refreshOptions refreshOptions method}. See this method documentation to learn more.
     *
     */
    .factory('GenericEditor', function(yjQuery, encode, GENERIC_EDITOR_UNRELATED_VALIDATION_ERRORS_EVENT, GENERIC_EDITOR_LOADED_EVENT, GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT, VALIDATION_MESSAGE_TYPES, EDITOR_PUSH_TO_STACK_EVENT, EDITOR_POP_FROM_STACK_EVENT, lodash, restServiceFactory, languageService, sharedDataService, systemEventService, sanitize, sanitizeHTML, copy, isBlank, isObjectEmptyDeep, $q, $log, $translate, $injector, seValidationMessageParser, editorFieldMappingService, genericEditorTabService, genericEditorStackService, resetObject, deepObjectPropertyDiff, CONTEXT_SITE_ID, CONTEXT_CATALOG, CONTEXT_CATALOG_VERSION) {

        var primitiveTypes = ["Boolean", "ShortString", "LongString", "RichText", "Date", "Dropdown"];

        editorFieldMappingService._registerDefaultFieldMappings();

        var validate = function(conf) {
            if (isBlank(conf.structureApi) && !conf.structure) {
                throw "genericEditor.configuration.error.no.structure";
            } else if (!isBlank(conf.structureApi) && conf.structure) {
                throw "genericEditor.configuration.error.2.structures";
            }
        };

        /**
         * @constructor
         */
        var GenericEditor = function(conf) {
            validate(conf);
            this.id = conf.id;
            this.inProgress = false;
            this.smarteditComponentType = conf.smarteditComponentType;
            this.smarteditComponentId = conf.smarteditComponentId;
            this.editorStackId = conf.editorStackId;
            this.updateCallback = conf.updateCallback;
            this.structure = conf.structure;
            if (conf.structureApi) {
                this.editorStructureService = restServiceFactory.get(conf.structureApi);
            }
            this.uriContext = conf.uriContext;
            if (conf.contentApi) {
                this.editorCRUDService = restServiceFactory.get(conf.contentApi);
            }
            this.initialContent = lodash.cloneDeep(conf.content);
            this.component = null;
            this.fields = [];
            this.languages = [];
            this.initialDirty = false;
            // Object containing all the fields and their non pristine states.
            this.fieldsNonPristineState = {};

            if (conf.customOnSubmit) {
                this.onSubmit = conf.customOnSubmit;
            }

            this._unregisterUnrelatedErrorsEvent = systemEventService.registerEventHandler(GENERIC_EDITOR_UNRELATED_VALIDATION_ERRORS_EVENT, this._handleLegacyUnrelatedValidationErrors.bind(this));
            this._unregisterUnrelatedMessagesEvent = systemEventService.registerEventHandler(GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT, this._handleUnrelatedValidationMessages.bind(this));
        };

        GenericEditor.prototype._finalize = function() {
            this._unregisterUnrelatedErrorsEvent();
            this._unregisterUnrelatedMessagesEvent();
            this.popEditorFromStack();
        };

        GenericEditor.prototype._setApi = function(api) {
            this.api = api;
        };

        GenericEditor.prototype._handleLegacyUnrelatedValidationErrors = function(key, validationMessages) {
            this._handleUnrelatedValidationMessages(key, {
                messages: validationMessages,
                sourceGenericEditorId: validationMessages.sourceGenericEditorId
            });
        };

        GenericEditor.prototype._handleUnrelatedValidationMessages = function(key, validationData) {
            if (validationData.targetGenericEditorId && validationData.targetGenericEditorId !== this.id) {
                return;
            }

            if (validationData.sourceGenericEditorId && validationData.sourceGenericEditorId === this.id) {
                return;
            }

            this.removeValidationMessages();
            this._displayValidationMessages(validationData.messages, true);
        };

        GenericEditor.prototype._isPrimitive = function(type) {
            return primitiveTypes.indexOf(type) > -1;
        };

        GenericEditor.prototype._getSelector = function(selector) {
            return yjQuery(selector);
        };

        GenericEditor.prototype.pushEditorToStack = function() {
            if (!this.editorStackId) {
                this.editorStackId = this.id;
            }

            systemEventService.sendAsynchEvent(EDITOR_PUSH_TO_STACK_EVENT, {
                editorId: this.id,
                editorStackId: this.editorStackId,
                component: this.component,
                componentType: this.smarteditComponentType
            });
        };

        GenericEditor.prototype.popEditorFromStack = function() {
            systemEventService.sendAsynchEvent(EDITOR_POP_FROM_STACK_EVENT, {
                editorStackId: this.editorStackId
            });
        };

        /**
         * @ngdoc method
         * @name genericEditorModule.service:GenericEditor#reset
         * @methodOf genericEditorModule.service:GenericEditor
         *
         * @description
         * Sets the content within the editor to its original state.
         */
        GenericEditor.prototype.reset = function() {
            //need to empty the searches for refreshOptions to enable resetting to pristine state
            this._getSelector('.ui-select-search').val('');
            this._getSelector('.ui-select-search').trigger('input');
            this.removeValidationMessages();
            this.component = resetObject(this.component, this.pristine);

            this.fields.forEach(function(field) {
                delete field.initiated;
            });


            this.tabs = [];

            /* 
             * need not to just build fieldsMap but to reassign it as well in case of a reset called
             * on a generic editor without content API: after save component is reset but if the structure
             * remains the same the generic fields will try to assign model[qualifier] to an obsolete component
             */
            this.fieldsMap = this.fields.reduce(function(seed, field) {
                var tab = editorFieldMappingService.getFieldTabMapping(field, this.structure);
                if (!tab) {
                    tab = genericEditorTabService.getComponentTypeDefaultTab(this.structure);
                }

                if (Object.keys(seed).indexOf(tab) === -1) {
                    seed[tab] = [];

                    this.tabs.push({
                        id: tab,
                        title: 'se.genericeditor.tab.' + tab + '.title',
                        templateUrl: 'genericEditorTabWrapperTemplate.html'
                    });
                }
                seed[tab].push(field);
                return seed;
            }.bind(this), {});


            this._switchToTabContainingQualifier();
            genericEditorTabService.sortTabs(this.tabs);

            if (this.componentForm) {
                this.componentForm.$setPristine();
            }
            return $q.when();
        };

        /*
         * Causes the genericEditor to switch to the tab containing a qualifier of the given name.
         */
        GenericEditor.prototype._switchToTabContainingQualifier = function() {
            if (!this.tabSelected && this.targetedQualifier) {
                this.tabs.forEach(function(tab) {
                    tab.active = !!this.fieldsMap[tab.id].some(function(field) {
                        return field.qualifier === this.targetedQualifier;
                    }.bind(this));
                }.bind(this));
                this.tabSelected = true;
            }
        };

        /**
         * Removes validation errors generated in frontend, not the ones sent by outside or server.
         * Removes errors only from fields, not tabs.
         */
        GenericEditor.prototype._removeFrontEndValidationMessages = function() {
            this.fields.forEach(function(field) {
                var messages = (field.messages || []).filter(function(message) {
                    return message.fromSubmit === undefined ? true : message.fromSubmit;
                });
                field.messages = messages.length ? messages : undefined;
                field.hasErrors = this._containsValidationMessageType(field.messages, VALIDATION_MESSAGE_TYPES.VALIDATION_ERROR);
                field.hasWarnings = this._containsValidationMessageType(field.messages, VALIDATION_MESSAGE_TYPES.WARNING);
            }.bind(this));
        };

        /**
         * Removes all validation (local, outside or server) errors from fieds and tabs.
         */
        GenericEditor.prototype.removeValidationMessages = function() {
            (this.tabs || []).forEach(function(tab) {
                tab.hasErrors = false;
            });

            this.fields.forEach(function(field) {
                field.messages = undefined;
                field.hasErrors = false;
                field.hasWarnings = false;
            }.bind(this));
        };

        /**
         *  fetch will:
         *  - return data if initialContent is provided
         *  - make a call to the CRUD API to return the payload if initialContent is not provided
         * 
         *  (In initialDirty is set to true, it is populated after loading and setting the content which will make the
         *   pristine and component states out of sync thus making the editor dirty)
         */
        GenericEditor.prototype.fetch = function() {
            if (!this.initialDirty) {
                return this.initialContent ? $q.when(this.initialContent) : (this.smarteditComponentId ? this.editorCRUDService.get({
                    identifier: this.smarteditComponentId
                }) : $q.when({}));
            }
            return $q.when({});
        };

        GenericEditor.prototype.sanitizeLoad = function(response) {
            this.fields.forEach(function(field) {
                if (field.localized === true && isBlank(response[field.qualifier])) {
                    response[field.qualifier] = {};
                }
            });
            return response;
        };

        GenericEditor.prototype.load = function() {
            var deferred = $q.defer();
            this.fetch().then(
                function(response) {
                    this.pristine = this.sanitizeLoad(response);
                    this.reset();

                    deferred.resolve();
                }.bind(this),
                function(failure) {
                    $log.error("GenericEditor.load failed");
                    $log.error(failure);
                    deferred.reject();
                }
            );
            return deferred.promise;
        };

        GenericEditor.prototype.getComponent = function() {
            return this.component;
        };

        GenericEditor.prototype.sanitizePayload = function(payload, fields) {

            var CMS_STRUCTURE_TYPE = {
                SHORT_STRING: "ShortString",
                LONG_STRING: "LongString"
            };

            fields.filter(function(field) {
                return (field.cmsStructureType === CMS_STRUCTURE_TYPE.LONG_STRING || field.cmsStructureType === CMS_STRUCTURE_TYPE.SHORT_STRING || typeof field.customSanitize === 'function');
            }).map(function(field) {
                return {
                    name: field.qualifier,
                    localized: !!field.localized,
                    customSanitize: field.customSanitize
                };
            }).forEach(function(fieldInfo) {

                if (typeof payload[fieldInfo.name] !== 'undefined' && fieldInfo.name in payload) {
                    if (fieldInfo.customSanitize) {
                        fieldInfo.customSanitize(payload[fieldInfo.name], sanitize);
                    } else {
                        if (fieldInfo.localized) {
                            var qualifierValueObject = payload[fieldInfo.name];
                            Object.keys(qualifierValueObject).forEach(function(locale) {
                                qualifierValueObject[locale] = sanitize(qualifierValueObject[locale]);
                            });
                        } else {
                            payload[fieldInfo.name] = sanitize(payload[fieldInfo.name]);
                        }
                    }
                }

            });

            return payload;
        };

        GenericEditor.prototype._fieldsAreUserChecked = function() {
            return this.fields.every(function(field) {
                var requiresUserCheck = false;
                for (var qualifier in field.requiresUserCheck) {
                    requiresUserCheck = requiresUserCheck || field.requiresUserCheck[qualifier];
                }
                return !requiresUserCheck || field.isUserChecked;
            });
        };

        /**
         * @ngdoc method
         * @name genericEditorModule.service:GenericEditor#preparePayload
         * @methodOf genericEditorModule.service:GenericEditor
         *
         * @description
         * Transforms the payload before POST/PUT to server
         *
         * @param {Object} the transformed payload
         */
        GenericEditor.prototype.preparePayload = function(originalPayload) {
            return $q.when(originalPayload);
        };

        GenericEditor.prototype.onSubmit = function() {
            var payload = copy(this.component);

            payload = this.sanitizePayload(payload, this.fields);

            if (this.smarteditComponentId) {
                payload.identifier = this.smarteditComponentId;
            }

            // if POST mode
            if (this.editorCRUDService && !this.smarteditComponentId) {
                // if we have a type field in the structure, use it for the type in the POST payload
                if (this.structure && this.structure.type) {
                    // if the user already provided a type field, lets be nice
                    if (!payload.type) {
                        payload.type = this.structure.type;
                    }
                }
            }

            return this.preparePayload(payload).then(function(preparedPayload) {
                var promise = this.editorCRUDService ? (this.smarteditComponentId ? this.editorCRUDService.update(preparedPayload) : this.editorCRUDService.save(preparedPayload)) : $q.when(preparedPayload);
                return promise.then(function(response) {
                    return {
                        payload: payload,
                        response: response
                    };
                });
            }.bind(this));
        };

        /**
         * @ngdoc method
         * @name genericEditorModule.service:GenericEditor#submit
         * @methodOf genericEditorModule.service:GenericEditor
         *
         * @description
         * Saves the content within the form for a specified component. If there are any validation errors returned by the CRUD API after saving the content, it will display the errors.
         */
        GenericEditor.prototype.submit = function() {
            var deferred = $q.defer();

            var cleanComponent = this.getComponent();

            // It's necessary to remove validation errors even if the form is not dirty. This might be because of unrelated validation errors
            // triggered in other tab.
            this.removeValidationMessages();
            this.hasFrontEndValidationErrors = false;

            if (!this._fieldsAreUserChecked()) {
                deferred.reject(true); // Mark this tab as "in error" due to front-end validation. 
                this.hasFrontEndValidationErrors = true;
            } else if (this.isValid(true)) {

                this.inProgress = true;
                /*
                 * upon submitting, server side may have been updated,
                 * since we PUT and not PATCH, we need to take latest of the fields not presented and send them back with the editable ones
                 */
                this.onSubmit().then(function(submitResult) {
                    // If we're doing a POST or PUT and the request returns non empty response, then this response is returned.
                    // Otherwise the payload for the request is returned.
                    if (submitResult.response) {
                        this.pristine = copy(submitResult.response);
                    } else {
                        this.pristine = copy(submitResult.payload);
                    }

                    delete this.pristine.identifier;

                    if (!this.smarteditComponentId && submitResult.response) {
                        this.smarteditComponentId = submitResult.response.uuid;
                    }
                    this.removeValidationMessages();

                    this.reset();
                    this.inProgress = false;
                    deferred.resolve(lodash.cloneDeep(this.pristine));
                    if (this.updateCallback) {
                        this.updateCallback(this.pristine, submitResult.response);
                    }
                }.bind(this), function(failure) {
                    this.removeValidationMessages();
                    this._displayValidationMessages(failure.data.errors, true);
                    var hasErrors = this._containsValidationMessageType(failure.data.errors, VALIDATION_MESSAGE_TYPES.VALIDATION_ERROR);
                    //send unrelated validation messages to any other listening genericEditor when no other errors
                    var unrelatedValidationMessages = this._collectUnrelatedValidationMessages(failure.data.errors);
                    if (unrelatedValidationMessages.length > 0) {

                        // send tab id in errors for the legacy event.
                        var unrelatedValidationErrors = lodash.cloneDeep(unrelatedValidationMessages);
                        unrelatedValidationErrors.sourceGenericEditorId = this.id;
                        systemEventService.sendAsynchEvent(GENERIC_EDITOR_UNRELATED_VALIDATION_ERRORS_EVENT, unrelatedValidationErrors);
                        systemEventService.sendAsynchEvent(GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT, {
                            messages: unrelatedValidationMessages,
                            sourceGenericEditorId: this.id
                        });
                        this.inProgress = false;
                        deferred.reject(hasErrors); // Marks this tab if it has errors.
                    } else {
                        this.inProgress = false;
                        deferred.reject(true); // Marks this tab as "in error".
                    }
                }.bind(this));
            } else {
                $log.error("GenericEditor.submit() - unable to submit form. Form is unexpectedly invalid.");
                deferred.reject(cleanComponent);
            }
            return deferred.promise;
        };

        GenericEditor.prototype._validationMessageBelongsToCurrentInstance = function(validationMessage) {
            return lodash.some(this.fields, function(field) {
                return field.qualifier === validationMessage.subject;
            });
        };

        GenericEditor.prototype._containsValidationMessageType = function(validationMessages, messageType) {
            return lodash.some(validationMessages, function(message) {
                return message.type === messageType && this._validationMessageBelongsToCurrentInstance(message);
            }.bind(this));
        };

        GenericEditor.prototype._isValidationMessageType = function(messageType) {
            return lodash.includes(lodash.values(VALIDATION_MESSAGE_TYPES), messageType);
        };

        /**
         * Displays validation errors for fields and changes error states for all tabs.
         */
        GenericEditor.prototype._displayValidationMessages = function(validationMessages, keepAllErrors) {
            validationMessages.filter(function(message) {
                return this._isValidationMessageType(message.type) && (keepAllErrors || message.isNonPristine);
            }.bind(this)).forEach(function(validationMessage) {

                validationMessage.type = validationMessage.type || VALIDATION_MESSAGE_TYPES.VALIDATION_ERROR;

                var field = this.fields.filter(function(element) {
                    return (element.qualifier === validationMessage.subject);
                })[0];

                if (field) {
                    if (field.messages === undefined) {
                        field.messages = [];
                    }

                    var message = lodash.merge(validationMessage, seValidationMessageParser.parse(validationMessage.message));
                    message.marker = field.localized ? message.language : field.qualifier;
                    message.type = validationMessage.type;
                    message.uniqId = encode(message);

                    if (field.messages.map(function(msg) {
                            return msg.uniqId;
                        }).indexOf(message.uniqId) === -1) {
                        field.messages.push(message);
                        if (message.type === VALIDATION_MESSAGE_TYPES.VALIDATION_ERROR) {
                            field.hasErrors = true;
                        } else if (message.type === VALIDATION_MESSAGE_TYPES.WARNING) {
                            field.hasWarnings = true;
                        }
                    }

                    //when a field is in error, we need to light up the internal tab containing it
                    var tabId = Object.keys(this.fieldsMap).find(function(tabId) {
                        return this.fieldsMap[tabId].some(function(_field) {
                            return field === _field;
                        });
                    }.bind(this));
                    if (tabId) {
                        this.tabs.find(function(tab) {
                            return tab.id === tabId;
                        }).hasErrors = true;
                    }
                }
            }.bind(this));

            return $q.when();
        };

        GenericEditor.prototype._collectUnrelatedValidationMessages = function(messages) {
            return messages.filter(function(message) {
                return this._isValidationMessageType(message.type) && !this._validationMessageBelongsToCurrentInstance(message);
            }.bind(this));
        };

        GenericEditor.prototype.fieldAdaptor = function(fields) {

            fields.forEach(function(field) {
                var fieldMapping = editorFieldMappingService.getEditorFieldMapping(field, this.structure);
                lodash.assign(field, fieldMapping);

                if (field.editable === undefined) {
                    field.editable = true;
                }

                if (!field.postfixText) {
                    var key = (this.smarteditComponentType ? this.smarteditComponentType.toLowerCase() : '') + '.' + field.qualifier.toLowerCase() + '.postfix.text';
                    var translated = $translate.instant(key);
                    field.postfixText = translated !== key ? translated : "";
                }

                field.smarteditComponentType = this.smarteditComponentType;
            }.bind(this));
            return fields;
        };

        /**
         * @ngdoc method
         * @name genericEditorModule.service:GenericEditor#refreshOptions
         * @methodOf genericEditorModule.service:GenericEditor
         *
         * @description
         * Is invoked by HTML field templates that update and manage dropdowns.
         *  It updates the dropdown list upon initialization (creates a list of one option) and when performing a search (returns a filtered list).
         *  To do this, the GenericEditor fetches an implementation of the  {@link FetchDataHandlerInterfaceModule.FetchDataHandlerInterface FetchDataHandlerInterface} using the following naming convention: 
         * <pre>"fetch" + cmsStructureType + "DataHandler"</pre>
         * @param {Object} field The field in the structure that requires a dropdown to be built.
         * @param {string} qualifier For a non-localized field, it is the actual field.qualifier. For a localized field, it is the ISO code of the language.
         * @param {string} search The value of the mask to filter the dropdown entries on.
         */

        GenericEditor.prototype.refreshOptions = function(field, qualifier, search) {
            var theHandlerObj = "fetch" + field.cmsStructureType + "DataHandler";
            var theIdentifier;
            var optionsIdentifier;

            if (field.localized) {
                theIdentifier = this.component[field.qualifier][qualifier];
                optionsIdentifier = qualifier;
            } else {
                theIdentifier = this.component[field.qualifier];
                optionsIdentifier = field.qualifier;
            }

            var objHandler = $injector.get(theHandlerObj);

            field.initiated = field.initiated || [];
            field.options = field.options || {};

            if (field.cmsStructureType === 'Enum') {
                field.initiated.push(optionsIdentifier);
            }
            if (field.initiated.indexOf(optionsIdentifier) > -1) {
                if (search.length > 2 || field.cmsStructureType === 'Enum') {
                    objHandler.findByMask(field, search).then(function(entities) {
                        field.options[optionsIdentifier] = entities;
                    });
                }
            } else if (theIdentifier) {
                objHandler.getById(field, theIdentifier).then(function(entity) {
                    field.options[optionsIdentifier] = [entity];
                    field.initiated.push(optionsIdentifier);
                }.bind(field));
            } else {
                field.initiated.push(optionsIdentifier);
            }
        };

        GenericEditor.prototype._buildComparable = function(source) {
            if (!source) {
                return source;
            }
            var comparable = {};

            this.fields.forEach(function(field) {
                var fieldValue = source[field.qualifier];
                if (field.localized) {
                    var sub = {};
                    angular.forEach(fieldValue, function(langValue, lang) {
                        if (langValue !== null) {
                            sub[lang] = _buildFieldComparable(langValue, field);
                        }
                    });
                    comparable[field.qualifier] = sub;
                } else {
                    comparable[field.qualifier] = _buildFieldComparable(fieldValue, field);
                }
            });

            //sometimes, such as in navigationNodeEntryEditor, we update properties not part of the fields and still want the editor to turn dirty
            angular.forEach(source, function(value, key) {
                var notDisplayed = !this.fields.some(function(field) {
                    return field.qualifier === key;
                }.bind(this));
                if (notDisplayed) {
                    comparable[key] = value;
                }
            }.bind(this));

            return comparable;
        };

        var _buildFieldComparable = function(fieldValue, field) {
            switch (field.cmsStructureType) {
                case 'RichText':
                    return fieldValue !== undefined ? sanitizeHTML(fieldValue) : null;
                case 'Boolean':
                    return fieldValue !== undefined ? fieldValue : false;
                default:
                    return fieldValue;
            }
        };

        /**
         * @ngdoc method
         * @name genericEditorModule.service:GenericEditor#isDirty
         * @methodOf genericEditorModule.service:GenericEditor
         *
         * @description
         * A predicate function that returns true if the editor is in dirty state or false if it not.
         * The state of the editor is determined by comparing the current state of the component with the state of the component when it was pristine.
         *
         * @return {Boolean} An indicator if the editor is in dirty state or not.
         */
        GenericEditor.prototype.isDirty = function() {
            //try to get away from angular.equals
            this.bcPristine = this._buildComparable(this.pristine);
            this.bcComp = this._buildComparable(this.component);
            return !angular.equals(this.bcPristine, this.bcComp);
        };

        /**
         * Evaluates the fields that are not in pristine state and populates this.fieldsNonPristineState object.
         */
        GenericEditor.prototype._populateFieldsNonPristineStates = function() {
            this.bcPristine = this._buildComparable(this.pristine);
            this.bcComp = this._buildComparable(this.component);
            this.fieldsNonPristineState = this._getFieldsNonPristineState(this.fieldsNonPristineState, this.bcPristine, this.bcComp);
        };

        /** 
         * Collects validation errors on all the form fields.
         * Returns the list of errors or empty list.
         * Each error contains the following properties:
         * type - VALIDATION_MESSAGE_TYPES
         * subject - the field qualifier.
         * message - error message.
         * fromSubmit - contains true if the error is related to submit operation, false otherwise.
         * isNonPristine - contains true if the field was modified (at least once) by the user, false otherwise.
         * language - optional language iso code.
         */
        GenericEditor.prototype._collectFrontEndValidationErrors = function(comesFromSubmit) {
            comesFromSubmit = comesFromSubmit || false;
            if (!this.component) {
                return [];
            }

            // first collect HTM5 errors
            var htmlvalidationErrors = this.fields.filter(function(field) {
                var formField = this.componentForm[field.qualifier];
                return !lodash.isNil(formField) && formField.$invalid === true;
            }.bind(this)).map(function(field) {
                return {
                    type: VALIDATION_MESSAGE_TYPES.VALIDATION_ERROR,
                    subject: field.qualifier,
                    message: 'se.editor.html.validation.error',
                    fromSubmit: comesFromSubmit,
                    isNonPristine: this.fieldsNonPristineState[field.qualifier]
                };
            }.bind(this));

            //then collect errors for required fields
            var requiredErrors = [];
            this.fields.filter(function(field) {
                    return field.required && field.editable;
                })
                .forEach(function(field) {
                    if (field.localized) {
                        this.requiredLanguages.forEach(function(language) {
                            if (!this.component[field.qualifier][language.isocode]) {
                                requiredErrors.push({
                                    type: VALIDATION_MESSAGE_TYPES.VALIDATION_ERROR,
                                    subject: field.qualifier,
                                    message: 'se.componentform.required.field',
                                    language: language.isocode,
                                    fromSubmit: comesFromSubmit,
                                    isNonPristine: this.fieldsNonPristineState[field.qualifier][language.isocode]
                                });
                            }
                        }.bind(this));
                    } else if (isObjectEmptyDeep(this.component[field.qualifier])) {
                        requiredErrors.push({
                            type: VALIDATION_MESSAGE_TYPES.VALIDATION_ERROR,
                            subject: field.qualifier,
                            message: 'se.componentform.required.field',
                            fromSubmit: comesFromSubmit,
                            isNonPristine: this.fieldsNonPristineState[field.qualifier]
                        });
                    }
                }.bind(this));

            return htmlvalidationErrors.concat(requiredErrors);
        };

        /**
         * Finds a diff between pristine and component using {@link functionsModule.deepObjectPropertyDiff deepObjectPropertyDiff} function
         * and merge the result with initialObject based on the following logic:
         * Do nothing if the initialObject's property is true, use the value from the diff otherwise.
         */
        GenericEditor.prototype._getFieldsNonPristineState = function(initialObject, pristine, component) {
            var nonPristineStateObj = deepObjectPropertyDiff(pristine, component);
            return lodash.mergeWith(lodash.cloneDeep(initialObject), nonPristineStateObj, function(prValue, cpValue) {
                if (!lodash.isPlainObject(prValue)) {
                    //Never revert true value (if the field was changed by the user the state stays the same)
                    return prValue === true ? true : cpValue;
                }
            });
        };

        /**
         * Check for html validation errors on all the form fields.
         * If so, assign an error to a field that is not pristine.
         * The seGenericEditorFieldError will render these errors, just like
         * errors we receive from the backend.
         * It also validates error states for tabs.
         */
        GenericEditor.prototype.isValid = function(comesFromSubmit) {
            comesFromSubmit = comesFromSubmit || false;
            var validationErrors = this._collectFrontEndValidationErrors(comesFromSubmit);
            this._removeFrontEndValidationMessages();
            this._displayValidationMessages(validationErrors, comesFromSubmit);
            this._validateTabsErrorStates();

            return validationErrors.length === 0;
        };

        /**
         * Changes error states of tabs based on whether the fields inside those tabs contain errors or not.
         */
        GenericEditor.prototype._validateTabsErrorStates = function() {
            this._getTabsByFieldsErrorState(false).forEach(function(tab) {
                this._setTabErrorState(tab.id, false);
            }.bind(this));

            this._getTabsByFieldsErrorState(true).forEach(function(tab) {
                this._setTabErrorState(tab.id, true);
            }.bind(this));
        };

        /**
         * Returns the list of tabs by error states.
         */
        GenericEditor.prototype._getTabsByFieldsErrorState = function(hasErrors) {
            return (this.tabs || []).filter(function(tab) {
                var tabsByErrorState = this.fieldsMap[tab.id].some(function(field) {
                    return field.hasErrors === hasErrors;
                });
                return tabsByErrorState;
            }.bind(this));
        };

        /**
         * Sets the error state for a tab based on tab id.
         */
        GenericEditor.prototype._setTabErrorState = function(tabId, hasErrors) {
            this.tabs.find(function(tab) {
                return tab.id === tabId;
            }).hasErrors = hasErrors;
        };

        GenericEditor.prototype.isSubmitDisabled = function() {
            return this.inProgress || !this.isDirty() || !this.isValid();
        };

        GenericEditor.prototype._getUriContext = function() {

            return this.uriContext ? $q.when(this.uriContext) : sharedDataService.get('experience').then(function(experience) {
                var uriContext = {};
                uriContext[CONTEXT_SITE_ID] = experience.siteDescriptor.uid;
                uriContext[CONTEXT_CATALOG] = experience.catalogDescriptor.catalogId;
                uriContext[CONTEXT_CATALOG_VERSION] = experience.catalogDescriptor.catalogVersion;
                return uriContext;
            });
        };

        /**
         * Conversion function in case the first attribute of the response is an array of type structures.  
         */
        GenericEditor.prototype._convertStructureArray = function(structure) {
            var structureArray = structure.structures || structure.componentTypes;
            if (lodash.isArray(structureArray)) {
                if (structureArray.length > 1) {
                    throw "GenericEditor.prototype.init: Invalid structure, multiple structures returned";
                }
                structure = structureArray[0];
            }
            return structure;
        };

        GenericEditor.prototype.init = function() {

            this.submitButtonText = 'se.componentform.actions.submit';
            this.cancelButtonText = 'se.componentform.actions.cancel';

            /**
             * @ngdoc object
             * @name genericEditorModule.object:genericEditorApi
             * @description
             * The generic editor's api object exposing public functionality
             */
            this._setApi({

                /**
                 * @ngdoc method
                 * @name setSubmitButtonText
                 * @methodOf genericEditorModule.object:genericEditorApi
                 * @description
                 * Overrides the i18n key used bfor the submit button
                 */
                setSubmitButtonText: function(_submitButtonText) {
                    this.submitButtonText = _submitButtonText;
                }.bind(this),

                /**
                 * @ngdoc method
                 * @name setCancelButtonText
                 * @methodOf genericEditorModule.object:genericEditorApi
                 * @description
                 * Overrides the i18n key used bfor the submit button
                 */
                setCancelButtonText: function(_cancelButtonText) {
                    this.cancelButtonText = _cancelButtonText;
                }.bind(this),

                /**
                 * @ngdoc method
                 * @name setAlwaysShowSubmit
                 * @methodOf genericEditorModule.object:genericEditorApi
                 * @description
                 * If set to true, will always show the submit button
                 */
                setAlwaysShowSubmit: function(_alwaysShowSubmit) {
                    this.alwaysShowSubmit = _alwaysShowSubmit;
                }.bind(this),
                /**
                 * @ngdoc method
                 * @name setAlwaysShowReset
                 * @methodOf genericEditorModule.object:genericEditorApi
                 * @description
                 * If set to true, will always show the reset button
                 */
                setAlwaysShowReset: function(_alwaysShowReset) {
                    this.alwaysShowReset = _alwaysShowReset;
                }.bind(this),
                /**
                 * @ngdoc method
                 * @name onReset
                 * @methodOf genericEditorModule.object:genericEditorApi
                 * @description
                 * To be executed after reset
                 */
                setOnReset: function(_onReset) {
                    this.onReset = _onReset;
                }.bind(this),
                /**
                 * @ngdoc method
                 * @name setPreparePayload
                 * @methodOf genericEditorModule.object:genericEditorApi
                 * @description
                 * Function that passes a preparePayload function to the editor in order to transform the payload prior to submitting (see {@link enericEditorModule.service:GenericEditor#preparePayload})
                 *
                 * @param {Object} preparePayload The function that takes the original payload as argument
                 */
                setPreparePayload: function(_preparePayload) {
                    this.preparePayload = _preparePayload;
                }.bind(this),
                /**
                 * @ngdoc method
                 * @name setUpdateCallback
                 * @methodOf genericEditorModule.object:genericEditorApi
                 * @description
                 * Function that passes an updateCallback function to the editor in order to perform an action upon successful submit. It is invoked with two arguments: the pristine object and the response from the server.
                 * @param {Object} updateCallback the callback invoked upon successful submit
                 */
                setUpdateCallback: function(_updateCallback) {
                    this.updateCallback = _updateCallback;
                }.bind(this),
                /**
                 * @ngdoc method
                 * @name updateComponent
                 * @methodOf genericEditorModule.object:genericEditorApi
                 * @description
                 * Function that updates the content of the generic editor without having to reinitialize
                 *
                 * @param {Object} component The component to replace the current model for the generic editor
                 */
                updateContent: function(component) {
                    this.component = copy(component);
                }.bind(this),

                /**
                 * @ngdoc method
                 * @name getContent
                 * @methodOf genericEditorModule.object:genericEditorApi
                 * @description
                 * copies of the current model
                 * @return {object} a copy
                 */
                getContent: function() {
                    return copy(this.component);
                }.bind(this),

                /**
                 * @ngdoc method
                 * @name onContentChange
                 * @methodOf genericEditorModule.object:genericEditorApi
                 * @description
                 * Function triggered everytime the current model changes
                 */
                onContentChange: function() {},

                /**
                 * @ngdoc method
                 * @name clearMessages
                 * @methodOf genericEditorModule.object:genericEditorApi
                 * @description
                 * Function that clears all validation messages in the editor
                 */
                clearMessages: function() {
                    this.removeValidationMessages();
                }.bind(this),

                /**
                 * @ngdoc method
                 * @name switchToTabContainingQualifier
                 * @methodOf genericEditorModule.object:genericEditorApi
                 * @description
                 * causes the genericEditor to switch to the tab containing a qualifier of the given name
                 * @param {Object} qualifier the qualifier contained in the tab we want to switch to
                 */
                switchToTabContainingQualifier: function(qualifier) {
                    this.targetedQualifier = qualifier;
                }.bind(this),

                // currently used by clone components to open editor in dirty mode
                considerFormDirty: function() {
                    this.initialDirty = true;
                }.bind(this),

                /**
                 * @ngdoc method
                 * @name isSubmitDisabled
                 * @methodOf genericEditorModule.object:genericEditorApi
                 * @description
                 * returns true to inform that the submit button delegated to the invoker should be disabled
                 * @return {boolean} true if submit is disabled
                 */
                isSubmitDisabled: function() {
                    return this.isSubmitDisabled();
                }.bind(this),

                /**
                 * @ngdoc method
                 * @name getLanguages
                 * @methodOf genericEditorModule.object:genericEditorApi
                 * @description
                 * Function that returns a promise resolving to language descriptors. If defined, will be resolved
                 * when the generic editor is initialized to override what languages are used for localized elements
                 * within the editor.
                 * @return {Promise} a promise resolving to language descriptors. Each descriptor provides the following
                 * language properties: isocode, nativeName, name, active, and required.
                 */
                getLanguages: function() {}
            });

            var deferred = $q.defer();

            var structurePromise = this.editorStructureService ? this.editorStructureService.get({
                smarteditComponentType: this.smarteditComponentType
            }) : $q.when(this.structure);

            structurePromise.then(
                function(structure) {
                    structure = this._convertStructureArray(structure);
                    this.structure = structure;

                    this._getUriContext().then(function(uriContext) {
                        var languagePromise = this.api.getLanguages() || languageService.getLanguagesForSite(uriContext[CONTEXT_SITE_ID]);
                        languagePromise.then(function(languages) {
                            this.languages = languages;
                            this.requiredLanguages = this.languages.filter(function(language) {
                                return language.required;
                            });

                            this.fields = this.fieldAdaptor(structure ? structure.attributes : []);
                            //for setting uri params into custom widgets
                            this.parameters = {
                                siteId: uriContext[CONTEXT_SITE_ID],
                                catalogId: uriContext[CONTEXT_CATALOG],
                                catalogVersion: uriContext[CONTEXT_CATALOG_VERSION]
                            };
                            this.load().then(function() {

                                // If initialDirty is set to true and if any initial content is provided, it is populated here which will make the
                                // pristine and component states out of sync thus making the editor dirty
                                if (this.initialDirty) {
                                    this.component = this.sanitizeLoad(this.initialContent || {});
                                }

                                this.pushEditorToStack();
                                systemEventService.sendAsynchEvent(GENERIC_EDITOR_LOADED_EVENT, this.id);
                                deferred.resolve();
                            }.bind(this), function() {
                                deferred.reject();
                            });
                        }.bind(this), function() {
                            $log.error("GenericEditor failed to fetch storefront languages");
                            deferred.reject();
                        });
                    }.bind(this));
                }.bind(this),
                function(e) {
                    $log.error("GenericEditor.init failed");
                    $log.error(e);
                    deferred.reject();
                });

            return deferred.promise;
        };

        return GenericEditor;

    })

    .controller('GenericEditorController', function($scope, GenericEditor, isBlank, generateIdentifier, yjQuery, $element, $attrs) {

        this.$onChanges = function() {
            if (this.editor) {
                this.editor._finalize();
            }

            this.editor = new GenericEditor({
                id: this.id || generateIdentifier(),
                smarteditComponentType: this.smarteditComponentType,
                smarteditComponentId: this.smarteditComponentId,
                editorStackId: this.editorStackId,
                structureApi: this.structureApi,
                structure: this.structure,
                contentApi: this.contentApi,
                updateCallback: this.updateCallback,
                content: this.content,
                uriContext: this.uriContext,
                customOnSubmit: this.customOnSubmit
            });

            this.editor.init();

            this.editor.showReset = isBlank($attrs.reset);
            this.editor.showSubmit = isBlank($attrs.submit);

            //#################################################################################################################

            if (typeof this.getApi === 'function') {
                /**
                 * @ngdoc method
                 * @name genericEditorModule.service:GenericEditor#getApi
                 * @methodOf genericEditorModule.service:GenericEditor
                 *
                 * @description
                 * Returns the generic editor's api object defining all public functionality
                 *
                 * @return {Object} api The {@link genericEditorModule.object:genericEditorApi GenericEditorApi} object
                 */
                this.getApi({
                    $api: this.editor.api
                });
            }

            var previousContent = angular.toJson(this.editor.api.getContent());

            this.$doCheck = function() {
                var newContent = angular.toJson(this.editor.api.getContent());
                if (previousContent !== newContent) {
                    previousContent = newContent;
                    this.editor.api.onContentChange();
                    this.editor._populateFieldsNonPristineStates();
                }
            }.bind(this);

            this.isDirty = function() {
                return this.editor ? this.editor.isDirty() : false;
            }.bind(this);

            this.reset = function() {
                if (this.editor.onReset) {
                    this.editor.onReset();
                }
                return this.editor.reset();
            }.bind(this);

            this.submit = function() {
                return this.editor.submit();
            }.bind(this);

            this.getComponent = function() {
                return this.editor.getComponent();
            }.bind(this);

            this.isValid = function() {
                return this.editor.isValid();
            }.bind(this);

            /*
             *  The generic editor wraps fields in "fieldsMap" that are instantiated after init
             *  So we only want to display the warning if fieldsMap exists (init is finished)
             *  but we still have no fields (holder is empty)
             *
             * @returns {boolean} True if we should display the disclaimer message to the user that either
             * the type is blacklisted or has no editable fields (there's no structure fields in technical terms)
             */
            this.showNoEditSupportDisclaimer = function() {
                return this.editor &&
                    this.editor.fieldsMap &&
                    Object.keys(this.editor.fieldsMap).length === 0;
            };

            /*
             * componentForm is normally accessed in $postLink, but in case of an $onChanges, we reaccess it here
             */
            if ($scope.componentForm) {
                this.editor.componentForm = $scope.componentForm;
            }
        };

        this.$onDestroy = function() {
            this.editor._finalize();
        };

        //FIXME : unregister event on destroy
        this.$postLink = function() {
            // Prevent enter key from triggering form submit
            yjQuery($element.find('.no-enter-submit')[0]).bind('keypress', function(key) {
                if (key.keyCode === 13) {
                    return false;
                }
            });
            this.editor.componentForm = $scope.componentForm;
        };

        this.showCommands = function() {
            return this.showCancel() || this.showSubmit();
        };

        this.showCancel = function() {
            return this.editor.alwaysShowReset || (this.editor.showReset === true && this.editor.isDirty() && this.editor.isValid());
        };

        this.showSubmit = function() {
            return this.editor.alwaysShowSubmit || (this.editor.showSubmit === true && this.editor.isDirty() && this.editor.isValid());
        };

        this.isSubmitDisabled = function() {
            return this.editor.isSubmitDisabled();
        };

    })
    /**
     * @ngdoc directive
     * @name genericEditorModule.directive:genericEditor
     * @scope
     * @restrict E
     * @element generic-editor
     *
     * @description
     * Component responsible for generating custom HTML CRUD form for any smarteditComponent type.
     *
     * The controller has a method that creates a new instance for the {@link genericEditorModule.service:GenericEditor GenericEditor}
     * and sets the scope of smarteditComponentId and smarteditComponentType to a value that has been extracted from the original DOM element in the storefront.
     *
     * @param {= String} id Id of the current generic editor.
     * @param {= String} smarteditComponentType The SmartEdit component type that is to be created, read, updated, or deleted.
     * @param {= String} smarteditComponentId The identifier of the SmartEdit component that is to be created, read, updated, or deleted.
     * @param {< String =} structureApi The data binding to a REST Structure API that fulfills the contract described in the  {@link genericEditorModule.service:GenericEditor GenericEditor} service. Only the Structure API or the local structure must be set.
     * @param {< String =} structure The data binding to a REST Structure JSON that fulfills the contract described in the {@link genericEditorModule.service:GenericEditor GenericEditor} service. Only the Structure API or the local structure must be set.
     * @param {= String} contentApi The REST API used to create, read, update, or delete content.
     * @param {= Object} content The model for the generic editor (the initial content when the component is being edited).
     * @param {< Object =} uriContext is an optional parameter and is used to pass the uri Params which can be used in making
     * api calls in custom widgets. It is an optional parameter and if not found, generic editor will find an experience in
     * sharedDataService and set this uriContext.
     * @param {= Function =} submit It exposes the inner submit function to the invoker scope. If this parameter is set, the directive will not display an inner submit button.
     * @param {= Function =} reset It exposes the inner reset function to the invoker scope. If this parameter is set, the directive will not display an inner cancel button.
     * @param {= Function =} isDirty Indicates if the the generic editor is in a pristine state (for example: has been modified).
     * @param {= Function =} isValid Indicates if all of the containing forms and controls in the generic editor are valid.
     * @param {& Function =} getApi Exposes the generic editor's api object
     * @param {= Function =} getComponent @deprecated since 6.5, use getComponent() method of getApi. Returns the current model for the generic editor.
     * @param {< Function =} updateCallback Callback called at the end of a successful submit. It is invoked with two arguments: the pristine object and the response from the server.
     * @param {= Function =} customOnSubmit It exposes the inner onSubmit function to the invoker scope. If the parameter is set, the inner onSubmit function is overridden by the custom function and the custom function must return a promise in the response format expected by the generic editor.
     * @param {< String =} editorStackId When working with nested components, a generic editor can be opened from within another editor. This parameter is used to specify the stack of nested editors. 
     */
    .component('genericEditor', {
        templateUrl: 'genericEditorTemplate.html',
        controller: 'GenericEditorController',
        controllerAs: 'ge',
        transclude: true,
        bindings: {
            id: '=',
            smarteditComponentId: '=',
            smarteditComponentType: '=?',
            structureApi: '<?',
            structure: '<?',
            contentApi: '=',
            content: '=',
            uriContext: '<?',
            submit: '=?',
            reset: '=?',
            isDirty: '=?',
            isValid: '=?',
            getApi: '&?',
            getComponent: '=?',
            updateCallback: '<?',
            customOnSubmit: '=?',
            editorStackId: '<?'
        }
    });
