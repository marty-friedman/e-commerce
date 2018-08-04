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
describe('test GenericEditor class', function() {

    var componentForm;
    var $rootScope, $q, $httpBackend, $translate;
    var smarteditComponentType, smarteditComponentId, updateCallback, GenericEditor, sharedDataService, languageService, restServiceFactory, editorStructureService, editorCRUDService;
    var editorMediaService, fetchEnumDataHandler, systemEventServ, seValidationErrorParser;
    var sanitize, editorFieldMappingService, searchSelector;
    var CONTEXT_CATALOG, CONTEXT_CATALOG_VERSION;
    var GENERIC_EDITOR_UNRELATED_VALIDATION_ERRORS_EVENT, GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT, EDITOR_PUSH_TO_STACK_EVENT, EDITOR_POP_FROM_STACK_EVENT;
    var options = [{
        code: 'code1',
        label: 'label1'
    }, {
        code: 'code2',
        label: 'label2'
    }];
    var STOREFRONT_LANGUAGES = [{
        language: 'en',
        required: true
    }, {
        language: 'pl',
        required: true
    }, {
        language: 'it'
    }];

    beforeEach(module('eventServiceModule', function($provide) {
        systemEventServ = jasmine.createSpyObj('systemEventService', ['registerEventHandler', 'sendAsynchEvent']);
        $provide.value('systemEventService', systemEventServ);
    }));

    beforeEach(module('seValidationErrorParserModule', function($provide) {
        seValidationErrorParser = jasmine.createSpyObj('seValidationErrorParser', ['parse']);
        $provide.value('seValidationErrorParser', seValidationErrorParser);
    }));

    beforeEach(module('smarteditServicesModule', function($provide) {
        sharedDataService = jasmine.createSpyObj('sharedDataService', ['get']);

        sharedDataService.get.and.callFake(function() {
            return $q.when({
                siteDescriptor: {
                    uid: 'someSiteUid'
                },
                catalogDescriptor: {
                    catalogId: 'somecatalogId',
                    catalogVersion: 'someCatalogVersion'
                }
            });
        });

        $provide.value('sharedDataService', sharedDataService);
    }));

    beforeEach(module('fetchEnumDataHandlerModule', function($provide) {
        fetchEnumDataHandler = jasmine.createSpyObj('fetchEnumDataHandler', ['findByMask', 'getById']);

        fetchEnumDataHandler.findByMask.and.callFake(function() {
            return $q.when(options);
        });

        $provide.value('fetchEnumDataHandler', fetchEnumDataHandler);
    }));

    beforeEach(module('languageServiceModule', function($provide) {
        languageService = jasmine.createSpyObj('languageService', ['getLanguagesForSite', 'getBrowserLocale']);
        languageService.getLanguagesForSite.and.callFake(function() {
            return $q.when(STOREFRONT_LANGUAGES);
        });
        languageService.getBrowserLocale.and.returnValue('en_US');
        $provide.value('languageService', languageService);
    }));

    beforeEach(module('functionsModule', function($provide) {
        sanitize = jasmine.createSpy('sanitize');
        sanitize.and.returnValue('ESCAPED');
        $provide.value('sanitize', sanitize);
    }));

    beforeEach(module('editorFieldMappingServiceModule', function($provide) {
        editorFieldMappingService = jasmine.createSpyObj('editorFieldMappingService', ['getEditorFieldMapping', '_registerDefaultFieldMappings', 'getFieldTabMapping']);
        editorFieldMappingService.getEditorFieldMapping.and.callFake(function(type) {
            return {
                template: (type + 'Template')
            };
        });
        editorFieldMappingService.getEditorFieldMapping.and.callFake(function() {
            return null;
        });
        editorFieldMappingService.getFieldTabMapping.and.returnValue("default");

        $provide.value('editorFieldMappingService', editorFieldMappingService);
    }));

    beforeEach(module('genericEditorModule', function($provide) {

        smarteditComponentType = "smarteditComponentType";
        smarteditComponentId = "smarteditComponentId";
        updateCallback = function() {};

        editorStructureService = jasmine.createSpyObj('restService', ['getById', 'get', 'query', 'page', 'save', 'update', 'remove']);
        editorCRUDService = jasmine.createSpyObj('restService', ['getById', 'get', 'query', 'page', 'save', 'update', 'remove']);
        editorMediaService = jasmine.createSpyObj('restService', ['getById', 'get', 'query', 'page', 'save', 'update', 'remove']);

        restServiceFactory = jasmine.createSpyObj('restServiceFactory', ['get']);
        restServiceFactory.get.and.callFake(function(uri) {
            if (uri === '/cmswebservices/types/:smarteditComponentType') {
                return editorStructureService;
            } else if (uri === '/cmswebservices/cmsxdata/contentcatalog/staged/Media') {
                return editorMediaService;
            } else if (uri === '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items') {
                return editorCRUDService;
            }
        });

        $provide.value('restServiceFactory', restServiceFactory);

        $provide.constant("CONTEXT_CATALOG", "CURRENT_CONTEXT_CATALOG");
        $provide.constant("CONTEXT_CATALOG_VERSION", "CURRENT_CONTEXT_CATALOG_VERSION");

        $translate = jasmine.createSpyObj('$translate', ['instant']);

        $provide.value('$translate', $translate);

        componentForm = jasmine.createSpyObj('componentForm', ['$setPristine']);
        componentForm.$dirty = true;

    }));
    beforeEach(inject(function(_$rootScope_, _$q_, I18N_RESOURCE_URI, _GenericEditor_, _$httpBackend_, languageService, _CONTEXT_CATALOG_, _CONTEXT_CATALOG_VERSION_, _GENERIC_EDITOR_UNRELATED_VALIDATION_ERRORS_EVENT_, _GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT_, _EDITOR_POP_FROM_STACK_EVENT_, _EDITOR_PUSH_TO_STACK_EVENT_) {
        $q = _$q_;
        $rootScope = _$rootScope_;
        $httpBackend = _$httpBackend_;
        GenericEditor = _GenericEditor_;
        CONTEXT_CATALOG = _CONTEXT_CATALOG_;
        CONTEXT_CATALOG_VERSION = _CONTEXT_CATALOG_VERSION_;
        GENERIC_EDITOR_UNRELATED_VALIDATION_ERRORS_EVENT = _GENERIC_EDITOR_UNRELATED_VALIDATION_ERRORS_EVENT_;
        GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT = _GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT_;
        EDITOR_PUSH_TO_STACK_EVENT = _EDITOR_PUSH_TO_STACK_EVENT_;
        EDITOR_POP_FROM_STACK_EVENT = _EDITOR_POP_FROM_STACK_EVENT_;

        $httpBackend.whenGET(I18N_RESOURCE_URI + "/" + languageService.getBrowserLocale()).respond({});

        searchSelector = jasmine.createSpyObj('searchSelector', ['val', 'trigger']);

        spyOn(GenericEditor.prototype, '_getSelector').and.callFake(function(selectorValue) {
            if (selectorValue === '.ui-select-search') {
                return searchSelector;
            }
        });
    }));


    it('GenericEditor fails to initialize if neither structureApi nor structure are provided', function() {

        expect(function() {
            return new GenericEditor({
                smarteditComponentType: smarteditComponentType,
                smarteditComponentId: smarteditComponentId,
                updateCallback: updateCallback
            });
        }).toThrow("genericEditor.configuration.error.no.structure");

    });

    it('GenericEditor fails to initialize if both structureApi and structure are provided', function() {

        expect(function() {
            return new GenericEditor({
                smarteditComponentType: smarteditComponentType,
                smarteditComponentId: smarteditComponentId,
                structureApi: '/cmswebservices/types/:smarteditComponentType',
                structure: 'structure',
                updateCallback: updateCallback
            });
        }).toThrow("genericEditor.configuration.error.2.structures");

    });

    it('GenericEditor initializes fine with structure API', function() {

        var editor = new GenericEditor({
            smarteditComponentType: smarteditComponentType,
            smarteditComponentId: smarteditComponentId,
            structureApi: '/cmswebservices/types/:smarteditComponentType',
            contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items',
            updateCallback: updateCallback
        });

        expect(editor.smarteditComponentType).toBe(smarteditComponentType);
        expect(editor.smarteditComponentId).toBe(smarteditComponentId);
        expect(editor.updateCallback).toBe(updateCallback);
        expect(editor.component).toBeNull();
        expect(editor.fields).toEqual([]);
        expect(editor.editorStructureService).toBe(editorStructureService);
        expect(editor.editorCRUDService).toBe(editorCRUDService);
    });

    it('GenericEditor initializes fine with structure', function() {

        var editor = new GenericEditor({
            smarteditComponentType: smarteditComponentType,
            smarteditComponentId: smarteditComponentId,
            structure: 'structure',
            contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items',
            updateCallback: updateCallback
        });

        expect(editor.smarteditComponentType).toBe(smarteditComponentType);
        expect(editor.smarteditComponentId).toBe(smarteditComponentId);
        expect(editor.updateCallback).toBe(updateCallback);
        expect(editor.component).toBeNull();
        expect(editor.fields).toEqual([]);
        expect(editor.editorStructureService).toBeUndefined();
        expect(editor.structure).toBe('structure');
        expect(editor.editorCRUDService).toBe(editorCRUDService);
    });

    it('GenericEditor fetch executes get with identifier if identifier is set', function() {

        var editor = new GenericEditor({
            smarteditComponentType: smarteditComponentType,
            smarteditComponentId: smarteditComponentId,
            structure: 'structure',
            contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
        });

        editorCRUDService.get.and.returnValue($q.when("somedata"));

        editor.fetch().then(function(value) {
            expect(value).toBe("somedata");
        }, function() {
            expect().fail();
        });

        $rootScope.$digest();

        expect(editorCRUDService.get).toHaveBeenCalledWith({
            identifier: smarteditComponentId
        });

    });

    it('GenericEditor fetch executes return empty object if identifier is not set', function() {

        var editor = new GenericEditor({
            smarteditComponentType: smarteditComponentType,
            structure: 'structure',
            contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
        });

        editor.fetch().then(function(value) {
            expect(value).toEqual({});
        }, function() {
            expect().fail();
        });

        $rootScope.$digest();

        expect(editorCRUDService.get).not.toHaveBeenCalled();

    });

    it('calling reset() set component to prior pristine state and call $setPristine on the component form if componentForm is passed and set holders if not set yet', function() {

        var pristine = {
            a: '1',
            b: '2'
        };

        var fields = [{
            field: 'field1',
            initiated: true
        }, {
            field: 'field2',
            initiated: false
        }];

        var INPUT = 'input';

        var editor = new GenericEditor({
            smarteditComponentType: smarteditComponentType,
            smarteditComponentId: smarteditComponentId,
            structure: 'structure',
            contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items',
        });

        editor.fields = fields;
        editor.pristine = pristine;
        editor.componentForm = componentForm;
        editor.reset();

        expect(searchSelector.val).toHaveBeenCalledWith('');
        expect(searchSelector.trigger).toHaveBeenCalledWith(INPUT);
        expect(editor.fields).toEqual([{
            field: 'field1',
            messages: undefined,
            hasErrors: false,
            hasWarnings: false
        }, {
            field: 'field2',
            messages: undefined,
            hasErrors: false,
            hasWarnings: false
        }]);

        expect(editor.component).not.toBe(pristine);
        expect(editor.component).toEqualData(pristine);
        expect(componentForm.$setPristine).toHaveBeenCalled();

        expect(editor.fieldsMap).toEqual({
            "default": [editor.fields[0], editor.fields[1]]
        });

    });

    it('successful load will set component and pristine state and call reset and "localize null" null values of localized properties', function() {

        var data = {
            a: '1',
            b: '2',
            c: null,
            d: {
                en: 'something'
            },
            e: null
        };

        editorCRUDService.get.and.returnValue($q.when(data));
        var editor = new GenericEditor({
            smarteditComponentType: smarteditComponentType,
            smarteditComponentId: smarteditComponentId,
            structure: 'structure',
            contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
        });

        editor.fields = [{
            qualifier: "c",
            localized: true
        }, {
            qualifier: "d",
            localized: true
        }];
        spyOn(editor, 'reset').and.returnValue();

        editor.load();
        //for promises to actually resolve :
        $rootScope.$digest();

        expect(editorCRUDService.get).toHaveBeenCalledWith({
            identifier: 'smarteditComponentId'
        });

        expect(editor.pristine).toEqualData({
            a: '1',
            b: '2',
            c: {},
            d: {
                en: 'something'
            },
            e: null
        });
        expect(editor.reset).toHaveBeenCalled();

    });

    it('submit will do nothing if componentForm is not valid', function() {

        var editor = new GenericEditor({
            smarteditComponentType: smarteditComponentType,
            smarteditComponentId: smarteditComponentId,
            structure: 'structure',
            contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items',
            updateCallback: updateCallback
        });
        spyOn(editor, 'updateCallback').and.returnValue();
        spyOn(editor, 'reset').and.returnValue();
        spyOn(editor, 'removeValidationMessages').and.returnValue();
        spyOn(editor, '_displayValidationMessages').and.returnValue();

        spyOn(editor, 'isDirty').and.returnValue(true);
        spyOn(editor, 'isValid').and.returnValue(false);
        editor.componentForm = componentForm;
        editor.submit();

        // The errors should have been removed. This is necessary in case there was an associated error in a different tab.
        expect(editor.removeValidationMessages).toHaveBeenCalled();

        expect(editorCRUDService.update).not.toHaveBeenCalled();
        expect(editor.reset).not.toHaveBeenCalled();
        expect(editor.updateCallback).not.toHaveBeenCalled();
        expect(editor._displayValidationMessages).not.toHaveBeenCalled();
    });

    it('GIVEN generic editor with modified component WHEN submit is clicked and the backend returns empty response THEN submit function returns original payload', function() {
        // GIVEN
        editorCRUDService.update.and.returnValue($q.when(''));

        var editor = new GenericEditor({
            smarteditComponentType: smarteditComponentType,
            smarteditComponentId: smarteditComponentId,
            structure: 'structure',
            contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items',
            updateCallback: updateCallback
        });

        var refreshedData = {
            a: '1',
            b: '2',
            c: '3'
        };

        var originalPayload = refreshedData;

        var component = {
            a: '1',
            b: '4',
            c: '3'
        };

        editor.pristine = originalPayload;
        editor.component = component;
        editor.componentForm = componentForm;

        spyOn(editor, 'fetch').and.returnValue($q.when(refreshedData));
        spyOn(editor, 'updateCallback').and.returnValue();
        spyOn(editor, 'reset').and.returnValue();
        spyOn(editor, '_displayValidationMessages').and.returnValue();
        spyOn(editor, 'removeValidationMessages').and.returnValue();
        spyOn(editor, 'isDirty').and.returnValue(true);

        // WHEN
        var result = editor.submit();
        $rootScope.$digest();

        // THEN
        expect(result).toBeResolvedWithData({
            a: '1',
            b: '4',
            c: '3'
        });
    });

    it('submit will refresh the non editable fields values from server, call update, set pristine state, calls removeValidationMessages, reset and updateCallback if dirty and form valid', function() {

        editorCRUDService.update.and.returnValue($q.when(null)); //not listening to response anymore

        var editor = new GenericEditor({
            smarteditComponentType: smarteditComponentType,
            smarteditComponentId: smarteditComponentId,
            structure: 'structure',
            contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items',
            updateCallback: updateCallback
        });

        var pristine = {
            a: '1',
            b: '2',
            c: '3'
        };

        var component = {
            a: '1',
            b: '4',
            c: '3'
        };

        var fields = [{
            qualifier: 'a'
        }, {
            qualifier: 'b'
        }, {
            qualifier: 'c'
        }];

        editor.fields = fields;
        editor.pristine = pristine;
        editor.component = component;
        editor.componentForm = componentForm;

        spyOn(editor, 'updateCallback').and.returnValue();
        spyOn(editor, 'reset').and.returnValue();
        spyOn(editor, '_displayValidationMessages').and.returnValue();
        spyOn(editor, 'removeValidationMessages').and.returnValue();
        spyOn(editor, 'isDirty').and.returnValue(true);


        editor.submit();
        //for promises to actually resolve :
        $rootScope.$digest();

        expect(editorCRUDService.update).toHaveBeenCalledWith({
            a: '1',
            b: '4',
            c: '3',
            identifier: 'smarteditComponentId'
        });

        expect(editor.updateCallback).toHaveBeenCalledWith(editor.pristine, null);
        expect(editor.reset).toHaveBeenCalledWith();
        expect(editor.removeValidationMessages).toHaveBeenCalled();
        expect(editor._displayValidationMessages).toHaveBeenCalled();
        expect(editor.pristine).toEqual({
            a: '1',
            b: '4',
            c: '3'
        });
    });

    it('successful init will assign editing structure from API, fetch storefront languages and process it and call load ', function() {

        var fields = {
            attributes: [{
                qualifier: 'property1',
                cmsStructureType: 'ShortString'
            }, {
                qualifier: 'id',
                cmsStructureType: 'ShortString'
            }, {
                qualifier: 'type',
                cmsStructureType: 'ShortString'
            }, {
                qualifier: 'activationDate',
                cmsStructureType: 'DateTime'
            }]
        };

        var modifiedFields = [];

        var deferred = $q.defer();
        deferred.resolve(fields);
        editorStructureService.get.and.returnValue(deferred.promise);

        var editor = new GenericEditor({
            smarteditComponentType: smarteditComponentType,
            smarteditComponentId: smarteditComponentId,
            structureApi: '/cmswebservices/types/:smarteditComponentType',
            contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items',
            updateCallback: updateCallback
        });
        var deferred2 = $q.defer();
        deferred2.resolve();
        spyOn(editor, 'load').and.returnValue(deferred2.promise);

        spyOn(editor, 'fieldAdaptor').and.returnValue(modifiedFields);
        spyOn(editor, 'pushEditorToStack');

        editor.init().then(function() {
            expect(editor.fields).toEqualData(modifiedFields);
        }, function() {
            expect(editor).fail();
        });

        //for promises to actually resolve :
        $rootScope.$digest();

        expect(editorStructureService.get).toHaveBeenCalledWith({
            smarteditComponentType: 'smarteditComponentType'
        });
        expect(sharedDataService.get).toHaveBeenCalledWith('experience');
        expect(languageService.getLanguagesForSite).toHaveBeenCalledWith('someSiteUid');

        expect(editor.languages).toEqualData(STOREFRONT_LANGUAGES);
        expect(editor.fieldAdaptor).toHaveBeenCalledWith(fields.attributes);
        expect(editor.load).toHaveBeenCalled();
        expect(editor.pushEditorToStack).toHaveBeenCalled();
    });


    it('successful init will assign editing structure from local structure and process it and call load ', function() {

        var structure = {
            attributes: [{
                qualifier: 'property1',
                cmsStructureType: 'ShortString'
            }, {
                qualifier: 'id',
                cmsStructureType: 'ShortString'
            }, {
                qualifier: 'type',
                cmsStructureType: 'ShortString'
            }, {
                qualifier: 'activationDate',
                cmsStructureType: 'DateTime'
            }],
            category: 'TEST'
        };

        var tabId = 'testTab';

        var editor = new GenericEditor({
            smarteditComponentType: smarteditComponentType,
            smarteditComponentId: smarteditComponentId,
            structure: structure,
            contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items',
            updateCallback: updateCallback,
            id: tabId
        });
        var deferred2 = $q.defer();
        deferred2.resolve();
        spyOn(editor, 'load').and.returnValue(deferred2.promise);

        spyOn(editor, 'fieldAdaptor').and.callThrough();

        editor.init().then(function() {
            expect(editor.fields).toEqualData(structure.attributes);
        }, function() {
            expect(editor).fail();
        });

        //for promises to actually resolve :
        $rootScope.$digest();

        expect(editor.fieldAdaptor).toHaveBeenCalledWith(structure.attributes);
        expect(editor.load).toHaveBeenCalled();
    });


    it('fieldAdaptor will assign postfix text when a field qualifier defines a property', function() {

        var fields = [{
            qualifier: 'media',
            cmsStructureType: 'MediaContainer'
        }];

        var componentType = "simpleResponsiveBannerComponent";
        var editor = new GenericEditor({
            smarteditComponentType: componentType,
            smarteditComponentId: smarteditComponentId,
            structureApi: '/cmswebservices/types/:smarteditComponentType',
            contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
        });

        var result = "field can not be editable";
        $translate.instant.and.returnValue(result);

        var newFields = editor.fieldAdaptor(fields);

        expect(newFields[0].postfixText).toEqualData(result);
        expect($translate.instant).toHaveBeenCalledWith('simpleresponsivebannercomponent.media.postfix.text');
    });


    it('fieldAdaptor wont assign postfix text when a field qualifier does not define a property  ', function() {

        var fields = [{
            qualifier: 'media',
            cmsStructureType: 'MediaContainer'
        }];

        var componentType = "simpleResponsiveBannerComponent";
        var editor = new GenericEditor({
            smarteditComponentType: componentType,
            smarteditComponentId: smarteditComponentId,
            structureApi: '/cmswebservices/types/:smarteditComponentType',
            contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
        });

        var key = 'simpleresponsivebannercomponent.media.postfix.text';
        $translate.instant.and.returnValue(key);

        var newFields = editor.fieldAdaptor(fields);

        expect(newFields[0].postfixText).toEqualData('');
        expect($translate.instant).toHaveBeenCalledWith(key);
    });

    it('_isPrimitive returns true for "Boolean", "ShortString", "LongString", "RichText", "Date" types only', function() {

        var editor = new GenericEditor({
            smarteditComponentType: smarteditComponentType,
            smarteditComponentId: smarteditComponentId,
            structureApi: '/cmswebservices/types/:smarteditComponentType',
            contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
        });

        var isPrim = [];
        isPrim.push(editor._isPrimitive('Boolean'));
        isPrim.push(editor._isPrimitive('ShortString'));
        isPrim.push(editor._isPrimitive('LongString'));
        isPrim.push(editor._isPrimitive('RichText'));
        isPrim.push(editor._isPrimitive('Date'));
        isPrim.push(editor._isPrimitive('AnyNonPrimitiveType'));

        expect(isPrim).toEqual([true, true, true, true, true, false]);

    });

    it('GIVEN that cmsStructureType is "Enum", refreshOptions  will call fetchEnumDataHandler to fetch fetch full list of enums', function() {

        var field = {
            qualifier: 'property1',
            cmsStructureType: 'Enum'
        };

        var editor = new GenericEditor({
            smarteditComponentType: smarteditComponentType,
            smarteditComponentId: smarteditComponentId,
            structureApi: '/cmswebservices/types/:smarteditComponentType',
            contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
        });

        var component = {};
        editor.component = component;

        editor.refreshOptions(field, 'qualifier', 's');
        $rootScope.$digest();
        expect(fetchEnumDataHandler.getById).not.toHaveBeenCalled();
        expect(fetchEnumDataHandler.findByMask).toHaveBeenCalledWith(field, 's');
        expect(field.options).toEqual({
            property1: [{
                code: 'code1',
                label: 'label1'
            }, {
                code: 'code2',
                label: 'label2'
            }]
        });
        expect(field.initiated).toEqual(['property1']);

    });

    it('fieldAdaptor does not transform the fields if neither external nor urlLink are found', function() {

        var fields = [{
            qualifier: 'property1',
            cmsStructureType: 'ShortString',
        }];

        var editor = new GenericEditor({
            smarteditComponentType: smarteditComponentType,
            smarteditComponentId: smarteditComponentId,
            structureApi: '/cmswebservices/types/:smarteditComponentType',
            contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
        });
        var newFields = editor.fieldAdaptor(fields);

        expect(newFields).toEqualData(fields);

    });

    it('fieldAdaptor does not transform the fields if urlLink is not found', function() {

        var fields = [{
            qualifier: 'property1',
            cmsStructureType: 'ShortString',
        }, {
            qualifier: 'external',
            cmsStructureType: 'Boolean',
        }];

        var editor = new GenericEditor({
            smarteditComponentType: smarteditComponentType,
            smarteditComponentId: smarteditComponentId,
            structureApi: '/cmswebservices/types/:smarteditComponentType',
            contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
        });
        var newFields = editor.fieldAdaptor(fields);

        expect(newFields).toEqualData(fields);

    });

    it('fieldAdaptor does not transform the fields if external is not found', function() {

        var fields = [{
            qualifier: 'property1',
            cmsStructureType: 'ShortString',
        }, {
            qualifier: 'urlLink',
            cmsStructureType: 'ShortString',
        }];

        var editor = new GenericEditor({
            smarteditComponentType: smarteditComponentType,
            smarteditComponentId: smarteditComponentId,
            structureApi: '/cmswebservices/types/:smarteditComponentType',
            contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
        });
        var newFields = editor.fieldAdaptor(fields);

        expect(newFields).toEqualData(fields);

    });

    it('_displayValidationErrors will add errors messages and localization languages to the field', function() {
        seValidationErrorParser.parse.and.callFake(function(message) {
            return {
                message: message
            };
        });

        var validationErrors = [{
            "message": "This field cannot contain special characters",
            "reason": "missing",
            "subject": "field1",
            "subjectType": "parameter",
            "type": "ValidationError"
        }, {
            "message": "This field is required and must to be between 1 and 255 characters long.",
            "reason": "missing",
            "subject": "field2",
            "subjectType": "parameter",
            "type": "ValidationError"
        }];

        var fields = [{
            qualifier: 'field1'
        }, {
            qualifier: 'field2'
        }];

        var editor = new GenericEditor({
            smarteditComponentType: smarteditComponentType,
            smarteditComponentId: smarteditComponentId,
            updateCallback: updateCallback,
            structure: 'structure',
            contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
        });
        editor.fields = fields;
        editor.pristine = {};

        editor.reset();
        editor._displayValidationMessages(validationErrors, true);

        expect(fields[0].messages.length).toEqual(1);
        expect(fields[0].messages.length).toEqual(1);

        expect(fields[0].messages[0].message).toEqual("This field cannot contain special characters");
        expect(fields[0].messages[0].marker).toEqual("field1");

        expect(fields[1].messages[0].message).toEqual("This field is required and must to be between 1 and 255 characters long.");
        expect(fields[1].messages[0].marker).toEqual("field2");
    });

    it('_displayValidationMessages will add language from validation errors for the language property if the field is localized else will add the qualifier to the language property ', function() {
        seValidationErrorParser.parse.and.callFake(function(message) {
            var error = {};
            if (message === "This field cannot contain special characters. Language: [en]") {
                error.message = 'This field cannot contain special characters.';
                error.language = 'en';
            } else {
                error.message = message;
            }
            return error;
        });


        var validationErrors = [{
            "message": "This field cannot contain special characters. Language: [en]",
            "reason": "missing",
            "subject": "field1",
            "subjectType": "parameter",
            "type": "ValidationError"
        }, {
            "message": "This field is required and must to be between 1 and 255 characters long.",
            "reason": "missing",
            "subject": "field2",
            "subjectType": "parameter",
            "type": "ValidationError"
        }];

        var fields = [{
            qualifier: 'field1',
            localized: true
        }, {
            qualifier: 'field2'
        }];

        var editor = new GenericEditor({
            smarteditComponentType: smarteditComponentType,
            smarteditComponentId: smarteditComponentId,
            updateCallback: updateCallback,
            structure: 'structure',
            contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
        });
        editor.fields = fields;
        editor.pristine = {};

        editor.reset();

        editor._displayValidationMessages(validationErrors, true);

        expect(fields[0].messages.length).toEqual(1);
        expect(fields[0].messages.length).toEqual(1);

        expect(fields[0].messages[0].message).toEqual("This field cannot contain special characters.");
        expect(fields[0].messages[0].marker).toEqual("en");

        expect(fields[1].messages[0].message).toEqual("This field is required and must to be between 1 and 255 characters long.");
        expect(fields[1].messages[0].marker).toEqual("field2");

    });

    it('_displayValidationMessages will not show the message if it has already been added to the list of messages', function() {

        var validationErrors = [{
            "message": "This field cannot contain special characters. Language: [en]",
            "reason": "missing",
            "subject": "field1",
            "subjectType": "parameter",
            "type": "ValidationError"
        }, {
            "message": "This field is required and must to be between 1 and 255 characters long.",
            "reason": "missing",
            "subject": "field2",
            "subjectType": "parameter",
            "type": "ValidationError"
        }];

        var fields = [{
            qualifier: 'field1',
            localized: true,
            messages: [{
                "message": "This field cannot contain special characters. Language: [en]",
                "reason": "missing",
                "subject": "field1",
                "subjectType": "parameter",
                "type": "ValidationError",
                "uniqId": "eyJtZXNzYWdlIjoiVGhpcyBmaWVsZCBjYW5ub3QgY29udGFpbiBzcGVjaWFsIGNoYXJhY3RlcnMuIiwibGFuZ3VhZ2UiOiJlbiIsIm1hcmtlciI6ImVuIiwidHlwZSI6IlZhbGlkYXRpb25FcnJvciJ9"
            }]
        }, {
            qualifier: 'field2'
        }];

        var editor = new GenericEditor({
            smarteditComponentType: smarteditComponentType,
            smarteditComponentId: smarteditComponentId,
            updateCallback: updateCallback,
            structure: 'structure',
            contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
        });
        editor.fields = fields;
        editor.pristine = {};
        editor.reset();
        editor._displayValidationMessages(validationErrors, true);

        expect(fields[0].messages.length).toEqual(1);

        console.info(JSON.stringify(fields[0].messages[0]));
        expect(fields[0].messages[0]).toEqualData({
            "message": "This field cannot contain special characters.",
            "reason": "missing",
            "subject": "field1",
            "subjectType": "parameter",
            "language": "en",
            "marker": "en",
            "type": "ValidationError",
            "uniqId": "eyJtZXNzYWdlIjoiVGhpcyBmaWVsZCBjYW5ub3QgY29udGFpbiBzcGVjaWFsIGNoYXJhY3RlcnMuIiwicmVhc29uIjoibWlzc2luZyIsInN1YmplY3QiOiJmaWVsZDEiLCJzdWJqZWN0VHlwZSI6InBhcmFtZXRlciIsInR5cGUiOiJWYWxpZGF0aW9uRXJyb3IiLCJsYW5ndWFnZSI6ImVuIiwibWFya2VyIjoiZW4ifQ=="
        });

    });

    it('GIVEN a list of validationMessages WHEN _displayValidationErrors is called with keepAllErrors as false THEN it will filter out all fields that are pristine', function() {
        seValidationErrorParser.parse.and.callFake(function(message) {
            return {
                message: message
            };
        });

        var validationErrors = [{
            "message": "This field cannot contain special characters",
            "reason": "missing",
            "subject": "field1",
            "subjectType": "parameter",
            "type": "ValidationError",
            "fromSubmit": false,
            "isNonPristine": true
        }, {
            "message": "This field is required and must to be between 1 and 255 characters long.",
            "reason": "missing",
            "subject": "field2",
            "subjectType": "parameter",
            "type": "ValidationError",
            "fromSubmit": false,
            "isNonPristine": false
        }];

        var fields = [{
            qualifier: 'field1'
        }, {
            qualifier: 'field2'
        }];

        var editor = new GenericEditor({
            smarteditComponentType: smarteditComponentType,
            smarteditComponentId: smarteditComponentId,
            updateCallback: updateCallback,
            structure: 'structure',
            contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
        });
        editor.fields = fields;
        editor.pristine = {};

        editor.reset();
        editor._displayValidationMessages(validationErrors, false);

        expect(fields[0].messages.length).toEqual(1);
        expect(fields[0].messages[0].subject).toEqual("field1");

    });


    it('failed submit will remove existing validation errors and call _displayValidationMessages', function() {

        var failure = {
            "data": {
                "errors": [{
                    "message": "This field cannot contain special characters",
                    "reason": "missing",
                    "subject": "headline",
                    "subjectType": "parameter",
                    "type": "ValidationError"
                }, {
                    "message": "This field is required and must to be between 1 and 255 characters long.",
                    "reason": "missing",
                    "subject": "content",
                    "subjectType": "parameter",
                    "type": "ValidationError"
                }]
            }
        };
        editorCRUDService.update.and.returnValue($q.reject(failure));

        var editor = new GenericEditor({
            smarteditComponentType: smarteditComponentType,
            smarteditComponentId: smarteditComponentId,
            updateCallback: updateCallback,
            structure: 'structure',
            contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
        });

        var pristine = {
            a: '0',
            b: '1'
        };

        var component = {
            a: '1',
            b: '2'
        };

        var fields = [{
            qualifier: 'a',
        }, {
            qualifier: 'b'
        }];

        editor.pristine = pristine;
        editor.component = component;
        editor.fields = fields;
        editor.componentForm = componentForm;
        componentForm.$dirty = true;

        spyOn(editor, 'updateCallback').and.returnValue();
        spyOn(editor, 'reset').and.callThrough();
        spyOn(editor, '_displayValidationMessages').and.callThrough();
        spyOn(editor, 'removeValidationMessages').and.returnValue();

        editor.submit();
        //for promises to actually resolve :
        $rootScope.$digest();

        expect(editor.updateCallback).not.toHaveBeenCalled();
        expect(editor.reset).not.toHaveBeenCalledWith(componentForm);

        expect(editor.removeValidationMessages).toHaveBeenCalledWith();
        expect(editor._displayValidationMessages).toHaveBeenCalledWith(failure.data.errors, true);
    });

    it('GIVEN there are errors caused by an external editor WHEN submit is called THEN the editor must raise a GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT ', function() {
        // Arrange
        var failure = {
            "data": {
                "errors": [{
                    "message": "This field cannot contain special characters",
                    "reason": "missing",
                    "subject": "headline",
                    "subjectType": "parameter",
                    "type": "ValidationError"
                }, {
                    "message": "This field is required and must to be between 1 and 255 characters long.",
                    "reason": "missing",
                    "subject": "content",
                    "subjectType": "parameter",
                    "type": "ValidationError"
                }]
            }
        };

        failure.data.errors.sourceGenericEditorId = 'someId';

        var refreshedData = {
            a: '1',
            b: '2',
            c: '5'
        };

        var editor = new GenericEditor({
            id: 'someId',
            smarteditComponentType: smarteditComponentType,
            smarteditComponentId: smarteditComponentId,
            updateCallback: updateCallback,
            structure: 'structure',
            contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
        });

        editorCRUDService.update.and.returnValue($q.reject(failure));

        editor.componentForm = componentForm;
        componentForm.$dirty = true;

        editor.component = {};
        editor.component.someField = 'someFieldvalue';

        spyOn(editor, '_displayValidationMessages').and.callThrough();
        spyOn(editor, 'removeValidationMessages').and.returnValue();
        spyOn(editor, 'fetch').and.returnValue($q.when(refreshedData));

        // Act
        editor.submit();
        $rootScope.$digest(); //for promises to actually resolve

        // Assert
        expect(systemEventServ.sendAsynchEvent).toHaveBeenCalledWith("UnrelatedValidationErrors", failure.data.errors);
    });

    it('GIVEN there are errors in one or more fields in the current editor detected externally WHEN GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT handler is called THEN the editor must display those validation errors', function() {
        seValidationErrorParser.parse.and.callFake(function(message) {
            return {
                message: message
            };
        });

        // Arrange
        var failure = {
            "data": {
                "errors": [{
                    "message": "This field cannot contain special characters",
                    "reason": "missing",
                    "subject": "headline",
                    "subjectType": "parameter",
                    "type": "ValidationError"
                }, {
                    "message": "This field is required and must to be between 1 and 255 characters long.",
                    "reason": "missing",
                    "subject": "content",
                    "subjectType": "parameter",
                    "type": "ValidationError"
                }]
            }
        };

        var fields = [{
            qualifier: 'headline'
        }];

        var editor = new GenericEditor({
            smarteditComponentType: smarteditComponentType,
            smarteditComponentId: smarteditComponentId,
            updateCallback: updateCallback,
            structure: 'structure',
            contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
        });
        editor.id = 'some ID';
        editor.fields = fields;
        editor.pristine = {};

        spyOn(editor, 'isDirty').and.returnValue(false);
        spyOn(editor, 'removeValidationMessages');
        spyOn(editor, '_displayValidationMessages').and.callThrough();

        // Act
        editor.reset();
        editor._handleUnrelatedValidationMessages("some Key", {
            messages: failure.data.errors
        });

        // Assert
        expect(editor.removeValidationMessages).toHaveBeenCalled();
        expect(editor._displayValidationMessages).toHaveBeenCalledWith(failure.data.errors, true);
    });

    it('isDirty will sanitize before checking if pristine and component HTML are equal', function() {

        var pristine = {
            a: {
                en: '<h2>search</h2><p>Suggestions</p><ul>	<li>The</li>	<li>The</li>	<li>Test</li></ul>',
            },
            b: '1',
            c: '<h2>search</h2> \n<p>Suggestions</p><ul>\n<li>The</li><li>The</li><li>Test</li></ul>'
        };

        var component = {
            a: {
                en: '<h2>search</h2> \n<p>Suggestions</p><ul>\n<li>The</li><li>The</li><li>Test</li></ul>',
            },
            b: '1',
            c: '<h2>search</h2><p>Suggestions</p><ul>	<li>The</li>	<li>The</li>	<li>Test</li></ul>'
        };

        var fields = [{
            cmsStructureType: 'RichText',
            qualifier: 'a',
            localized: true
        }, {
            qualifier: 'b'
        }, {
            qualifier: 'c',
            cmsStructureType: 'RichText',
            localized: false
        }];

        var editor = new GenericEditor({
            smarteditComponentType: smarteditComponentType,
            smarteditComponentId: smarteditComponentId,
            updateCallback: updateCallback,
            structure: 'structure',
            contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
        });

        editor.pristine = pristine;
        editor.component = component;
        editor.fields = fields;

        editor.linkToStatus = {
            hasBoth: function() {
                return false;
            }
        };

        var result = editor.isDirty();
        expect(result).toEqual(false);

        pristine = {
            a: {
                en: '<h2>test1</h2> <p>test2</p>',
            }
        };

        component = {
            a: {
                en: '<h2>TEST2</h2> \n<p>test1</p>',
            }
        };

        fields = [{
            cmsStructureType: 'RichText',
            qualifier: 'a',
            localized: true
        }];

        editor.pristine = pristine;
        editor.component = component;
        editor.fields = fields;

        result = editor.isDirty();
        expect(result).toEqual(true);

    });

    it('isDirty will return true even for properties that are not fields', function() {

        var pristine = {
            a: '123 ',
            b: '0'
        };

        var component = {
            a: '123',
            b: ''
        };

        var editor = new GenericEditor({
            smarteditComponentType: smarteditComponentType,
            smarteditComponentId: smarteditComponentId,
            updateCallback: updateCallback,
            structure: 'structure',
            contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
        });

        editor.linkToStatus = {
            hasBoth: function() {
                return false;
            }
        };

        editor.pristine = pristine;
        editor.component = component;

        var result = editor.isDirty();
        expect(result).toEqual(true);
    });

    it('sanitizePayload will remove dangerous characters from a localized ShortString CMS component type when the user saves the form with data in the input', function() {

        var editor = new GenericEditor({
            smarteditComponentType: smarteditComponentType,
            smarteditComponentId: smarteditComponentId,
            updateCallback: updateCallback,
            structure: 'structure',
            contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
        });

        var payload = {
            headline: {
                en: '<h1>Foo bar</h1>h1>'
            }
        };

        var fields = [{
            qualifier: "headline",
            cmsStructureType: "ShortString",
            localized: true
        }];

        payload = editor.sanitizePayload(payload, fields);

        expect(sanitize.calls.count()).toBe(1);
        expect(sanitize.calls.argsFor(0)[0]).toBe('<h1>Foo bar</h1>h1>');
        expect(payload).toEqual({
            headline: {
                en: 'ESCAPED'
            }
        });

    });


    it('sanitizePayload will not remove dangerous characters from a ShortString CMS component type when the user saves the form with no data in the input', function() {

        var editor = new GenericEditor({
            smarteditComponentType: smarteditComponentType,
            smarteditComponentId: smarteditComponentId,
            updateCallback: updateCallback,
            structure: 'structure',
            contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
        });

        var payload = {
            id: undefined
        };

        var fields = [{
            qualifier: "id",
            cmsStructureType: "ShortString",
        }];

        payload = editor.sanitizePayload(payload, fields);

        // The function will not be called because the qualifier is undefined
        expect(sanitize.calls.count()).toBe(0);
        expect(payload).toEqual({
            id: undefined
        });

    });

    it('sanitizePayload will remove dangerous characters from a LongString CMS component type when the user saves the form with data in the textarea', function() {

        var editor = new GenericEditor({
            smarteditComponentType: smarteditComponentType,
            smarteditComponentId: smarteditComponentId,
            updateCallback: updateCallback,
            structure: 'structure',
            contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
        });

        var payload = {
            urlLink: "/pathwithxss/onclick='alert(1)'"
        };

        var fields = [{
            qualifier: "urlLink",
            cmsStructureType: "LongString"
        }];

        payload = editor.sanitizePayload(payload, fields);

        expect(sanitize.calls.count()).toBe(1);
        expect(sanitize.calls.argsFor(0)[0]).toBe("/pathwithxss/onclick='alert(1)'");
        expect(payload).toEqual({
            urlLink: "ESCAPED"
        });

    });

    it('sanitizePayload will not remove dangerous characters from a LongString CMS component type when the user saves the form with no data in the textarea', function() {

        var editor = new GenericEditor({
            smarteditComponentType: smarteditComponentType,
            smarteditComponentId: smarteditComponentId,
            updateCallback: updateCallback,
            structure: 'structure',
            contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
        });

        var payload = {
            metaDescription: undefined
        };

        var fields = [{
            qualifier: 'metaDescription',
            cmsStructureType: 'LongString'
        }];

        payload = editor.sanitizePayload(payload, fields);

        // The function will not be called because the qualifier is undefined
        expect(sanitize.calls.count()).toBe(0);
        expect(payload).toEqual({
            metaDescription: undefined
        });

    });

    it('_fieldsAreUserChecked WILL fail validation WHEN a required checkbox field is not checked', function() {

        var fields = [{
            qualifier: 'content',
            cmsStructureType: 'Paragraph',
            requiresUserCheck: {
                content: true
            },
            isUserChecked: false
        }];
        var componentType = "simpleResponsiveBannerComponent";
        var editor = new GenericEditor({
            smarteditComponentType: componentType,
            smarteditComponentId: smarteditComponentId,
            structureApi: '/cmswebservices/types/:smarteditComponentType',
            contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
        });
        editor.fields = fields;
        var valid = editor._fieldsAreUserChecked();
        expect(valid).toEqual(false);

    });

    it('_fieldsAreUserChecked WILL pass validation WHEN not required checkbox field is not checked', function() {
        var fields = [{
            qualifier: 'content',
            cmsStructureType: 'Paragraph',
            requiresUserCheck: {
                content: true
            },
            isUserChecked: true
        }];
        var componentType = "simpleResponsiveBannerComponent";
        var editor = new GenericEditor({
            smarteditComponentType: componentType,
            smarteditComponentId: smarteditComponentId,
            structureApi: '/cmswebservices/types/:smarteditComponentType',
            contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
        });
        editor.fields = fields;
        var valid = editor._fieldsAreUserChecked();
        expect(valid).toEqual(true);
    });


    it('submit WILL fail validation WHEN submit a not checked required checkbox field', function() {
        var fields = [{
            qualifier: 'content',
            cmsStructureType: 'Paragraph',
            requiresUserCheck: {
                content: true
            },
            isUserChecked: false
        }];
        var componentType = "simpleResponsiveBannerComponent";
        var editor = new GenericEditor({
            smarteditComponentType: componentType,
            smarteditComponentId: smarteditComponentId,
            structureApi: '/cmswebservices/types/:smarteditComponentType',
            contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
        });
        editor.fields = fields;
        editor.submit();

        expect(editor.hasFrontEndValidationErrors).toEqual(true);
    });

    it('submit WILL pass validation WHEN submit a checked required checkbox field', function() {
        var fields = [{
            qualifier: 'content',
            cmsStructureType: 'Paragraph',
            requiresUserCheck: {
                content: true
            },
            isUserChecked: true
        }];
        var componentType = "simpleResponsiveBannerComponent";
        var editor = new GenericEditor({
            smarteditComponentType: componentType,
            smarteditComponentId: smarteditComponentId,
            structureApi: '/cmswebservices/types/:smarteditComponentType',
            contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
        });
        editor.fields = fields;
        editor.component = {};

        editor.componentForm = componentForm;

        editor.submit();
        expect(editor.hasFrontEndValidationErrors).toEqual(false);
    });


    it('_convertStructureArray will properly convert the structures to a format that the GE can understand', function() {
        var fields = [{
            qualifier: 'content',
            cmsStructureType: 'Paragraph',
            requiresUserCheck: {
                content: true
            },
            isUserChecked: true
        }];

        var structures = {
            structures: [{
                attributes: fields
            }]
        };

        var componentTypes = {
            componentTypes: [{
                attributes: fields
            }]
        };

        var componentType = "simpleResponsiveBannerComponent";
        var editor = new GenericEditor({
            smarteditComponentType: componentType,
            smarteditComponentId: smarteditComponentId,
            structureApi: '/cmswebservices/types/:smarteditComponentType',
            contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
        });

        var structure = editor._convertStructureArray(structures);
        expect(structure.attributes).toEqual(fields);

        structure = editor._convertStructureArray(componentTypes);
        expect(structure.attributes).toEqual(fields);
    });

    it('WHEN an editor is finalized THEN it properly cleans up', function() {
        // GIVEN 
        var _unregisterUnrelatedErrorsEvent = jasmine.createSpy('_unregisterUnrelatedErrorsEvent');
        var _unregisterUnrelatedMessagesEvent = jasmine.createSpy('_unregisterUnrelatedMessagesEvent');
        systemEventServ.registerEventHandler.and.callFake(function(eventId) {
            if (eventId === GENERIC_EDITOR_UNRELATED_VALIDATION_ERRORS_EVENT) {
                return _unregisterUnrelatedErrorsEvent;
            } else if (eventId === GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT) {
                return _unregisterUnrelatedMessagesEvent;
            }
        });

        var editor = new GenericEditor({
            smarteditComponentType: smarteditComponentType,
            smarteditComponentId: smarteditComponentId,
            updateCallback: updateCallback,
            structure: 'structure',
            contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
        });

        spyOn(editor, 'popEditorFromStack');

        // WHEN 
        editor._finalize();

        // THEN
        expect(editor._unregisterUnrelatedErrorsEvent).toHaveBeenCalled();
        expect(editor._unregisterUnrelatedMessagesEvent).toHaveBeenCalled();
        expect(editor.popEditorFromStack).toHaveBeenCalled();
    });

    it('GIVEN no editorStackId was provided WHEN pushEditorToStack is called THEN it sends the right event with the right editor stack id', function() {
        // GIVEN 
        var EDITOR_ID = 'some editor id';
        var SAMPLE_COMPONENT = 'some sample component';
        var editor = new GenericEditor({
            id: EDITOR_ID,
            smarteditComponentType: smarteditComponentType,
            smarteditComponentId: smarteditComponentId,
            updateCallback: updateCallback,
            structure: 'structure',
            contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
        });
        editor.component = SAMPLE_COMPONENT;

        // WHEN 
        editor.pushEditorToStack();

        // THEN 
        expect(systemEventServ.sendAsynchEvent).toHaveBeenCalledWith(EDITOR_PUSH_TO_STACK_EVENT, {
            editorId: EDITOR_ID,
            editorStackId: EDITOR_ID,
            component: SAMPLE_COMPONENT,
            componentType: smarteditComponentType
        });
    });

    it('GIVEN an editorStackId was provided WHEN pushEditorToStack is called THEN it sends the right event with the right editor stack id', function() {
        // GIVEN 
        var STACK_ID = 'some stack id';
        var EDITOR_ID = 'some editor id';
        var SAMPLE_COMPONENT = 'some sample component';
        var editor = new GenericEditor({
            id: EDITOR_ID,
            smarteditComponentType: smarteditComponentType,
            smarteditComponentId: smarteditComponentId,
            updateCallback: updateCallback,
            editorStackId: STACK_ID,
            structure: 'structure',
            contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
        });
        editor.component = SAMPLE_COMPONENT;

        // WHEN 
        editor.pushEditorToStack();

        // THEN 
        expect(systemEventServ.sendAsynchEvent).toHaveBeenCalledWith(EDITOR_PUSH_TO_STACK_EVENT, {
            editorId: EDITOR_ID,
            editorStackId: STACK_ID,
            component: SAMPLE_COMPONENT,
            componentType: smarteditComponentType
        });
    });

    it('WHEN popEditorFromStack is called THEN it sends the right event', function() {
        // GIVEN 
        var STACK_ID = 'some stack id';
        var editor = new GenericEditor({
            smarteditComponentType: smarteditComponentType,
            smarteditComponentId: smarteditComponentId,
            updateCallback: updateCallback,
            editorStackId: STACK_ID,
            structure: 'structure',
            contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
        });

        // WHEN 
        editor.popEditorFromStack();

        // THEN 
        expect(systemEventServ.sendAsynchEvent).toHaveBeenCalledWith(EDITOR_POP_FROM_STACK_EVENT, {
            editorStackId: STACK_ID
        });
    });

    it('GIVEN empty initial Object WHEN _getFieldsNonPristineState is called THEN it will return an object containing the non pristine states of all the fields', function() {
        // GIVEN 
        var editor = new GenericEditor({
            smarteditComponentType: smarteditComponentType,
            smarteditComponentId: smarteditComponentId,
            structure: 'structure',
            contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
        });

        var initialObj = {};

        var pristine = {
            visible: true,
            restrictions: [],
            content: {
                de: 'aaaa',
                fr: "bbbb"
            },
            position: 2,
            slotId: "id",
            removedField: 1
        };

        var component = {
            visible: false,
            restrictions: ['dddd'],
            content: {
                de: 'aaaa',
                en: "cccc"
            },
            position: 54,
            slotId: "id",
            addedField: 2
        };

        // WHEN 
        var result = editor._getFieldsNonPristineState(initialObj, pristine, component);

        // THEN 
        var outputObj = {
            content: {
                de: false,
                en: true,
                fr: true
            },
            position: true,
            restrictions: true,
            slotId: false,
            visible: true,
            removedField: true,
            addedField: true
        };
        expect(result).toEqual(outputObj);

    });

    it('GIVEN non-empty initial object WHEN _getFieldsNonPristineState is called THEN it will return an object containing the non pristine states of all the fields merged to non-empty initial object', function() {
        // GIVEN 
        var editor = new GenericEditor({
            smarteditComponentType: smarteditComponentType,
            smarteditComponentId: smarteditComponentId,
            structure: 'structure',
            contentApi: '/cmswebservices/catalogs/' + CONTEXT_CATALOG + '/versions/' + CONTEXT_CATALOG_VERSION + '/items'
        });

        var initialObj = {
            content: {
                de: false,
                en: true
            },
            position: false,
            restrictions: true,
            slotId: false,
            removedField: true,
            addedField: true
        };

        var pristine = {
            visible: true,
            restrictions: [],
            content: {
                de: 'aaaa',
                fr: "bbbb",
                en: "cccc"
            },
            position: 2,
            slotId: "id",
            removedField: 1
        };

        var component = {
            visible: false,
            restrictions: ['dddd'],
            content: {
                de: 'aaaa',
                en: "cccc"
            },
            position: 54,
            slotId: "id",
            addedField: 2
        };

        // WHEN 
        var result = editor._getFieldsNonPristineState(initialObj, pristine, component);

        // THEN 
        var outputObj = {
            content: {
                de: false,
                en: true,
                fr: true
            },
            position: true,
            restrictions: true,
            slotId: false,
            visible: true,
            removedField: true,
            addedField: true
        };
        expect(result).toEqual(outputObj);

    });
});
