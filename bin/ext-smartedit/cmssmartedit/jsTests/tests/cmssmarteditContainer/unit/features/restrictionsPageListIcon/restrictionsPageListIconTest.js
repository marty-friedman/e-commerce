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
describe('restrictionsPageListIcon', function() {

    var harness;

    beforeEach(function() {
        harness = AngularUnitTestHelper.prepareModule('restrictionsPageListIconModule')
            .withTranslations({
                'se.icon.tooltip.visibility': '{{numberOfRestrictions}} restrictions'
            })
            .directive('<restrictions-page-list-icon number-of-restrictions="numberOfRestrictions"/>', {
                numberOfRestrictions: 3
            });
    });

    it('should set the tooltip text', function() {
        expect(harness.element.find('img')).toHaveAttribute('data-uib-tooltip', '3 restrictions');
    });

    it('should set the image source', function() {
        expect(harness.element.find('img')).toHaveAttribute('data-ng-src', '/cmssmartedit/images/icon_restriction_small_blue.png');
    });

});
