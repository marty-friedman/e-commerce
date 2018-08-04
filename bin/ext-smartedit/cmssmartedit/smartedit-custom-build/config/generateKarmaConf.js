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
        targets: ['generateSmarteditKarmaConf', 'generateSmarteditContainerKarmaConf'],
        config: function(data, conf) {

            const lodash = require('lodash');
            const path = require('path');

            const paths = require('../../jsTests/paths');

            const karmaSmartedit = require(path.resolve(global.smartedit.bundlePaths.external.generated.webpack.karmaSmartedit));
            const karmaSmarteditContainer = require(path.resolve(global.smartedit.bundlePaths.external.generated.webpack.karmaSmarteditContainer));

            const cmssmartedit = {
                singleRun: true,

                coverageReporter: {
                    // specify a common output directory
                    dir: 'jsTarget/test/cmssmartedit/coverage/',
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
                    outputDir: 'jsTarget/test/cmssmartedit/junit/', // results will be saved as $outputDir/$browserName.xml
                    outputFile: 'testReport.xml' // if included, results will be saved as $outputDir/$browserName/$outputFile
                },

                files: paths.cmssmarteditKarmaConfFiles,

                proxies: {
                    '/cmssmartedit/images/': '/base/images/'
                },

                // list of files to exclude
                exclude: [
                    '**/requireLegacyJsFiles.js',
                    '**/cmssmarteditApp.ts',
                    '**/*.d.ts',
                    '*.d.ts'
                ],

                webpack: karmaSmartedit
            };


            const cmssmarteditContainer = {
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
                    }, ]
                },

                junitReporter: {
                    outputDir: 'jsTarget/test/cmssmarteditContainer/junit/', // results will be saved as $outputDir/$browserName.xml
                    outputFile: 'testReport.xml' // if included, results will be saved as $outputDir/$browserName/$outputFile
                },

                // list of files / patterns to load in the browser
                files: paths.cmssmarteditContainerKarmaConfFiles,

                proxies: {
                    '/cmssmartedit/images/': '/base/images/'
                },

                // list of files to exclude
                exclude: [
                    '**/requireLegacyJsFiles.js',
                    '**/cmssmarteditContainerApp.ts',
                    '**/*.d.ts',
                    '*.d.ts'
                ],

                webpack: karmaSmarteditContainer
            };


            conf.generateSmarteditKarmaConf.data = lodash.merge(cmssmartedit, conf.generateSmarteditKarmaConf.data);

            conf.generateSmarteditContainerKarmaConf.data = lodash.merge(cmssmarteditContainer, conf.generateSmarteditContainerKarmaConf.data);


            return conf;
        }
    };
};
