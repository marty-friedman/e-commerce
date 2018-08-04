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
/* jshint unused:false, undef:false */
describe('restrictionService', function() {

    var restrictionsService, restrictionsRestService, typeStructureRestService;
    var structuresRestService, structureModeManagerFactory;
    var $q;

    var MOCK_RESTRICTIONS = unit.mockData.restrictions;
    var MOCK_RESTRICTION_ID = 'restriction-007';

    var MOCK_URI_CONTEXT = "/cmswebservices/v1/sites/apparel-uk/catalogs/apparel-ukContentCatalog/versions/Online/restrictions";
    var MOCK_STRUCTURE_URI = "/cmswebservices/v1/types?code=:smarteditComponentType&mode=EDIT";

    var MOCK_MODE_MANAGER = {
        validateMode: function(mode) {
            //empty;
        }
    };

    var MOCK_RESTRICTION = {
        type: "timeRestrictionData",
        description: "Page only applies from 10/4/16 3:02 PM to 10/5/16 3:02 PM",
        name: "My CMSTime restriction3",
        typeCode: "CMSTimeRestriction",
        uid: "restriction-007",
        activeFrom: "2016-10-04T19:02:19+0000",
        activeUntil: "2016-11-05T19:02:19+0000"
    };

    var TYPE_RESTRICTION_STRUCTURE = [{
        category: "RESTRICTION",
        code: "RESTRICTIONTYPE1"
    }, {
        category: "RESTRICTION",
        code: "RESTRICTIONTYPE2"
    }, {
        category: "RESTRICTION",
        code: "RESTRICTIONTYPE3"
    }];

    beforeEach(module('restrictionsServiceModule', function($provide) {

        restrictionsRestService = jasmine.createSpyObj('restrictionsRestService', ['get', 'getContentApiUri', 'getById']);
        restrictionsRestService.getContentApiUri.and.callFake(function() {
            return $q.when(MOCK_URI_CONTEXT);
        });
        restrictionsRestService.getById.and.callFake(function() {
            return $q.when(MOCK_RESTRICTION);
        });
        $provide.value('restrictionsRestService', restrictionsRestService);

        structuresRestService = jasmine.createSpyObj('structuresRestService', ['getUriForContext']);
        structuresRestService.getUriForContext.and.callFake(function() {
            return $q.when(MOCK_STRUCTURE_URI);
        });
        $provide.value('structuresRestService', structuresRestService);

        structureModeManagerFactory = jasmine.createSpyObj('structureModeManagerFactory', ['createModeManager']);
        structureModeManagerFactory.createModeManager.and.callFake(function() {
            return MOCK_MODE_MANAGER;
        });
        $provide.value('structureModeManagerFactory', structureModeManagerFactory);

        typeStructureRestService = jasmine.createSpyObj('typeStructureRestService', ['getStructuresByCategory']);
        typeStructureRestService.getStructuresByCategory.and.callFake(function() {
            return TYPE_RESTRICTION_STRUCTURE;
        });
        $provide.value('typeStructureRestService', typeStructureRestService);
    }));

    beforeEach(inject(function(_$q_, _restrictionsService_) {
        $q = _$q_;
        restrictionsService = _restrictionsService_;
    }));

    it('should return all restrictions', function() {
        restrictionsRestService.get.and.returnValue($q.when(MOCK_RESTRICTIONS));
        expect(restrictionsService.getAllRestrictions()).toBeResolvedWithData(MOCK_RESTRICTIONS);
        expect(restrictionsRestService.get).toHaveBeenCalledWith();
    });

    it('should get the structure API URI', function() {
        var mode = 'edit';
        var typeCode = 'CMSTimeRestriction';
        expect(restrictionsService.getStructureApiUri(mode, typeCode)).toBeResolvedWithData(MOCK_STRUCTURE_URI);
        expect(structuresRestService.getUriForContext).toHaveBeenCalledWith(mode, typeCode);
    });

    it('should get the content API URI', function() {
        expect(restrictionsService.getContentApiUri(MOCK_URI_CONTEXT)).toBeResolvedWithData(MOCK_URI_CONTEXT);
        expect(restrictionsRestService.getContentApiUri).toHaveBeenCalledWith(MOCK_URI_CONTEXT);
    });

    it('should get the restriction by id', function() {
        expect(restrictionsService.getById(MOCK_RESTRICTION_ID)).toBeResolvedWithData(MOCK_RESTRICTION);
        expect(restrictionsRestService.getById).toHaveBeenCalledWith(MOCK_RESTRICTION_ID);
    });

    it('should get restrictions based on page and type', function() {
        restrictionsRestService.get.and.returnValue($q.when(MOCK_RESTRICTION));
        var restrictionTypeCode = 'CMSTimeRestriction';
        var mask = "winter";
        var pageSize = 10;
        var currentPage = 2;

        expect(restrictionsService.getPagedRestrictionsForType(restrictionTypeCode, mask, pageSize, currentPage)).toBeResolvedWithData(MOCK_RESTRICTION);
        expect(restrictionsRestService.get).toHaveBeenCalledWith({
            pageSize: pageSize,
            currentPage: currentPage,
            mask: mask,
            sort: 'name:ASC',
            params: "typeCode:" + restrictionTypeCode
        });
    });
});
