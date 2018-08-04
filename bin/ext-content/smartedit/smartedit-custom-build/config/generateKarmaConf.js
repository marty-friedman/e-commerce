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
        targets: [], // only in bundle
        config: function(data, conf) {

            const lodash = require('lodash');
            const path = require('path');
            const paths = require('../paths');

            const pathsInBundle = global.smartedit.bundlePaths;

            const karmaSmartedit = require(path.resolve(pathsInBundle.external.generated.webpack.karmaSmartedit));
            const karmaSmarteditContainer = require(path.resolve(pathsInBundle.external.generated.webpack.karmaSmarteditContainer));

            const unitSmarteditConf = {
                coverageReporter: {
                    // specify a common output directory
                    dir: '../../jsTarget/test/smartedit/coverage/',
                    reporters: [{
                        type: 'html',
                        subdir: 'report-html'
                    }, {
                        type: 'cobertura',
                        subdir: '.',
                        file: 'cobertura.xml'
                    }]
                },

                junitReporter: {
                    outputDir: 'jsTarget/test/smartedit/junit/', // results will be saved as $outputDir/$browserName.xml
                    outputFile: 'testReport.xml', // if included, results will be saved as $outputDir/$browserName/$outputFile
                    suite: '' // suite will become the package name attribute in xml testsuite element
                },

                // list of files / patterns to load in the browser
                // TODO - switch to use bundle properties
                files: lodash.concat(
                    'node_modules/ckeditor/ckeditor.js',
                    paths.getSmarteditThirdpartiesDevFiles(),
                    global.smartedit.bundlePaths.test.unit.commonUtilModules,
                    //,
                    'jsTarget/templates.js',
                    'jsTarget/web/app/common/**/*.+(js|ts)',
                    'jsTarget/web/app/smartedit/directives/**/*.+(js|ts)',
                    'jsTarget/web/app/smartedit/services/**/*.+(js|ts)',
                    'jsTarget/web/app/smartedit/modules/systemModule/features/**/*.+(js|ts)',
                    'jsTarget/web/app/smartedit/modules/systemModule/services/toolbar/toolbar.js',
                    'test/unit/smartedit/unit/**/*.+(js|ts)',
                    //
                    {
                        pattern: 'web/webroot/static-resources/images/**/*',
                        watched: false,
                        included: false,
                        served: true
                    }
                ),

                // list of files to exclude
                exclude: [
                    'jsTarget/web/app/smartedit/smartedit.ts',
                    'jsTarget/web/app/smartedit/partialBackendMocks.js',
                    'jsTarget/web/app/smartedit/smarteditbootstrap.ts',
                    '**/index.ts',
                    '**/*.d.ts'
                ],

                webpack: karmaSmartedit
            };


            const unitSmarteditContainer = {

                coverageReporter: {
                    // specify a common output directory
                    dir: '../../jsTarget/test/smarteditContainer/coverage/',
                    reporters: [{
                        type: 'html',
                        subdir: 'report-html'
                    }, {
                        type: 'cobertura',
                        subdir: '.',
                        file: 'cobertura.xml'
                    }]
                },

                junitReporter: {
                    outputDir: '../../jsTarget/test/smarteditContainer/junit/', // results will be saved as $outputDir/$browserName.xml
                    outputFile: 'testReport.xml', // if included, results will be saved as $outputDir/$browserName/$outputFile
                    suite: '' // suite will become the package name attribute in xml testsuite element
                },

                // list of files / patterns to load in the browser
                files: lodash.concat(
                    'node_modules/ckeditor/ckeditor.js',
                    paths.getContainerThirdpartiesDevFiles(),
                    global.smartedit.bundlePaths.test.unit.commonUtilModules,
                    //,
                    'jsTarget/templates.js',
                    'jsTarget/web/app/common/**/*.+(js|ts)',
                    'jsTarget/web/app/smarteditcontainer/**/*.+(js|ts)',
                    'test/unit/smarteditcontainer/unit/**/*.+(js|ts)',
                    //
                    {
                        pattern: 'web/webroot/static-resources/images/**/*',
                        watched: false,
                        included: false,
                        served: true
                    }

                ),

                // list of files to exclude
                exclude: [
                    '**/index.ts',
                    '**/*.d.ts'
                ],

                webpack: karmaSmarteditContainer

            };


            conf.generateSmarteditKarmaConf.data = lodash.merge(unitSmarteditConf, conf.generateSmarteditKarmaConf.data);

            conf.generateSmarteditContainerKarmaConf.data = lodash.merge(unitSmarteditContainer, conf.generateSmarteditContainerKarmaConf.data);


            return conf;
        }
    };
};