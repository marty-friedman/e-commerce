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
            'generateSmarteditKarmaConf',
            'generateSmarteditContainerKarmaConf',
            'generateSmarteditCommonsKarmaConf'
        ],
        config: function(data, conf) {

            const lodash = require('lodash');
            const path = require('path');

            const pathsInBundle = global.smartedit.bundlePaths;
            const karmaSmartedit = require(path.resolve(pathsInBundle.external.generated.webpack.karmaSmartedit));
            const karmaSmarteditContainer = require(path.resolve(pathsInBundle.external.generated.webpack.karmaSmarteditContainer));

            const randompicturecmscomponent = {
                singleRun: true,

                coverageReporter: {
                    // specify a common output directory
                    dir: 'jsTests/coverage/',
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
                    outputDir: 'jsTarget/tests/randompicturecmscomponent/junit/', // results will be saved as $outputDir/$browserName.xml
                    outputFile: 'testReport.xml' // if included, results will be saved as $outputDir/$browserName/$outputFile
                },

                // list of files / patterns to load in the browser
                files: lodash.concat(
                    pathsInBundle.test.unit.smarteditThirdPartyJsFiles,
                    pathsInBundle.test.unit.commonUtilModules, [
                        'jsTarget/web/features/randompicturecmscomponentcommons/**/*.+(js|ts)',
                        'jsTarget/web/features/randompicturecmscomponent/**/*.+(js|ts)',
                        'jsTarget/web/features/randompicturecmscomponent/templates.js',
                        'jsTests/tests/randompicturecmscomponent/unit/features/**/*.+(js|ts)'
                    ]
                ),

                // list of files to exclude
                exclude: [
                    'jsTarget/web/features/randompicturecmscomponent/randompicturecmscomponent.ts',
                    '**/*.d.ts',
                    '*.d.ts'
                ],

                webpack: karmaSmartedit
            };

            const randompicturecmscomponentContainer = {
                singleRun: true,

                coverageReporter: {
                    // specify a common output directory
                    dir: 'jsTests/coverage/',
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
                    outputDir: 'jsTarget/tests/randompicturecmscomponentContainer/junit/', // results will be saved as $outputDir/$browserName.xml
                    outputFile: 'testReport.xml' // if included, results will be saved as $outputDir/$browserName/$outputFile
                },

                // list of files / patterns to load in the browser
                files: lodash.concat(
                    pathsInBundle.test.unit.smarteditContainerUnitTestFiles,
                    pathsInBundle.test.unit.commonUtilModules, [
                        'jsTarget/web/features/randompicturecmscomponentcommons/**/*.+(js|ts)',
                        'jsTarget/web/features/randompicturecmscomponentContainer/**/*.+(js|ts)',
                        'jsTarget/web/features/randompicturecmscomponentContainer/templates.js',
                        'jsTests/tests/randompicturecmscomponentContainer/unit/features/**/*.+(js|ts)'
                    ]
                ),

                // list of files to exclude
                exclude: [
                    'jsTarget/web/features/randompicturecmscomponentContainer/randompicturecmscomponentcontainer.ts',
                    '**/*.d.ts',
                    '*.d.ts'
                ],
                webpack: karmaSmarteditContainer
            };

            const randompicturecmscomponentcommons = {
                singleRun: true,

                coverageReporter: {
                    // specify a common output directory
                    dir: 'jsTests/coverage/',
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
                    outputDir: 'jsTarget/tests/randompicturecmscomponentcommons/junit/', // results will be saved as $outputDir/$browserName.xml
                    outputFile: 'testReport.xml' // if included, results will be saved as $outputDir/$browserName/$outputFile
                },

                // list of files / patterns to load in the browser
                files: lodash.concat(
                    pathsInBundle.test.unit.smarteditThirdPartyJsFiles,
                    pathsInBundle.test.unit.commonUtilModules, [
                        'jsTarget/web/features/randompicturecmscomponentcommons/**/*.+(js|ts)',
                        'jsTarget/web/features/randompicturecmscomponentcommons/templates.js',
                        'jsTests/tests/randompicturecmscomponentcommons/unit/features/**/*.+(js|ts)'
                    ]
                ),

                // list of files to exclude
                exclude: [
                    '**/*.d.ts',
                    '*.d.ts'
                ],

                webpack: karmaSmarteditContainer
            };


            conf.generateSmarteditKarmaConf.data = lodash.merge(randompicturecmscomponent, conf.generateSmarteditKarmaConf.data);
            conf.generateSmarteditContainerKarmaConf.data = lodash.merge(randompicturecmscomponentContainer, conf.generateSmarteditContainerKarmaConf.data);

            // Commons is not available in bundle, lets take a copy of the container config to use for the commons
            conf.generateSmarteditCommonsKarmaConf = {
                dest: pathsInBundle.external.generated.karma.smarteditCommons,
                data: lodash.merge(lodash.cloneDeep(conf.generateSmarteditContainerKarmaConf.data), randompicturecmscomponentcommons)
            };
            conf.generateSmarteditCommonsKarmaConf.data.files = randompicturecmscomponentcommons.files;


            return conf;
        }
    };
};
