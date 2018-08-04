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
module.exports = function() {
    
        return {
            targets: [
                'unitUtilsForBundle',
                'outerStyling',
                'webApplicationInjector',
                'common',
                'smarteditloader',
                'containerAdministrationModule',
                'smarteditcontainer',
                'smartEditSystemModule',
                'presmartedit',
                'smarteditThirdparties',
                'smarteditThirdpartiesDev',
                'containerThirdpartiesDev',
                'containerThirdparties',
                'commonTypes',
                'smarteditTypes',
                'smarteditcontainerTypes'
            ],
            config: function(data, baseConf) {
    
                var paths = require('../paths');
    
                baseConf.unitUtilsForBundle = {
                    src: [
                        'web/app/common/services/functions.js', //only reference function.js, all other dependencies for unit tests must be mocked
                        'web/app/common/services/jquery/yjQuery.js',
                        'web/app/common/services/resourceLocations.js',
                        'web/app/common/services/lodash/ylodash.js',
                        'web/app/common/services/genericEditor/dropdownPopulators/dropdownPopulatorInterface.js',
                        'web/app/common/services/templateCacheDecorator.js'
                    ],
                    dest: global.smartedit.bundlePaths.bundleRoot + '/test/unit/generated/unitUtils.js'
                };
    
                baseConf.outerStyling = {
                    src: [
                        paths.web.webroot.staticResources.smartEdit.css.temp.outerVendor,
                        paths.web.webroot.staticResources.smartEdit.css.temp.outerStyling
                    ],
                    dest: paths.web.webroot.staticResources.smartEdit.css.outerStyling
                };
    
                baseConf.webApplicationInjector = {
                    src: [
                        paths.thirdparties.dir + '/scriptjs/dist/script.min.js',
                        'jsTarget/web/webApplicationInjector.js',
                    ],
                    dest: 'jsTarget/webApplicationInjector.js'
                };
    
                baseConf.common = {
                    src: [
                        'jsTarget/web/app/common/**/*.js',
                    ],
                    dest: 'jsTarget/common.js' // TODO: Do we still need this? Aren't common files imported directly into other js files?
                };
    
                baseConf.smarteditloader = {
                    src: [
                        'jsTarget/web/app/common/**/*.js',
                        'jsTarget/templates.js',
                        'jsTarget/web/app/smarteditloader/**/*.js'
                    ],
                    dest: 'jsTarget/web/app/smarteditloader/smarteditloader_bundle.js'
                };
    
                baseConf.containerAdministrationModule = {
                    src: [
                        'jsTarget/web/app/smarteditcontainer/modules/administrationModule/**/*.js',
                    ],
                    dest: 'jsTarget/web/app/smarteditcontainer/modules/administrationModule_bundle.js'
                };
    
                baseConf.smarteditcontainer = {
                    src: [
                        'jsTarget/web/app/common/**/*.js',
                        'jsTarget/templates.js',
                        'jsTarget/web/app/smarteditcontainer/components/**/*.js',
                        'jsTarget/web/app/smarteditcontainer/dao/**/*.js',
                        'jsTarget/web/app/smarteditcontainer/services/**/*.js',
                    ],
                    dest: 'jsTarget/web/app/smarteditcontainer/smarteditcontainer_bundle.js'
                };
    
                baseConf.smartEditSystemModule = {
                    src: [
                        'jsTarget/web/app/smartedit/modules/systemModule/**/*.js',
                    ],
                    dest: 'jsTarget/web/app/smartedit/modules/systemModule_bundle.js'
                };
    
                baseConf.presmartedit = {
                    src: [
                        'jsTarget/templates.js',
                        'jsTarget/web/app/common/**/*.js',
                        'jsTarget/web/app/smartedit/directives/**/*.js',
                        'jsTarget/web/app/smartedit/services/**/*.js'
                    ],
                    dest: 'jsTarget/web/app/smartedit/presmartedit_bundle.js'
                };
    
                baseConf.smarteditThirdparties = {
                    src: paths.getSmarteditThirdpartiesFiles(),
                    dest: paths.web.webroot.staticResources.dir + '/dist/smartedit/js/prelibraries.js'
                };
    
                baseConf.smarteditThirdpartiesDev = {
                    src: paths.getSmarteditThirdpartiesDevFiles(),
                    dest: paths.web.webroot.staticResources.dir + '/dist/smartedit/js/prelibraries.js'
                };
    
                baseConf.containerThirdpartiesDev = {
                    src: paths.getContainerThirdpartiesDevFiles(),
    
                    dest: 'web/webroot/static-resources/dist/smartedit/js/thirdparties.js'
                };
    
                baseConf.containerThirdparties = {
                    src: paths.containerThirdpartiesFiles(),
                    dest: 'web/webroot/static-resources/dist/smartedit/js/thirdparties.js'
                };
    
                baseConf.smarteditcommonsTypes = {
                    flatten: true,
                    src: ['temp/types/common/**/*.d.ts'],
                    dest: global.smartedit.bundlePaths.bundleRoot + '/@types/smarteditcommons/index.d.ts'
                };
    
                baseConf.smarteditTypes = {
                    flatten: true,
                    src: ['temp/types/smartedit/**/*.d.ts'],
                    dest: global.smartedit.bundlePaths.bundleRoot + '/@types/smartedit/index.d.ts'
                };
    
                baseConf.smarteditcontainerTypes = {
                    flatten: true,
                    src: ['temp/types/smarteditcontainer/**/*.d.ts'],
                    dest: global.smartedit.bundlePaths.bundleRoot + '/@types/smarteditcontainer/index.d.ts'
                };
                return baseConf;
            }
        };
    
    };