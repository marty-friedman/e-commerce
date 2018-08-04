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

    /**
     * @ngdoc overview
     * @name generateWebpackConfig(C)
     * @description
     * # generateWebpackConfig Configuration
     * The default generateWebpackConfig configuration provides the following targets:
     * - generateProdSmarteditWebpackConfig
     * - generateProdSmarteditContainerWebpackConfig
     * - generateDevSmarteditWebpackConfig
     * - generateDevSmarteditContainerWebpackConfig
     * - generateKarmaSmarteditWebpackConfig
     * - generateKarmaSmarteditContainerWebpackConfig
     *
     * The webpack targets correspond the {@link generateTsConfig(C) tsConfig targets}
     *
     * For the default output file locations, see bundlePaths.external.generated.webpack
     *
     */

    return {
        targets: [
            'generateProdSmarteditWebpackConfig',
            'generateProdSmarteditContainerWebpackConfig',
            'generateDevSmarteditWebpackConfig',
            'generateDevSmarteditContainerWebpackConfig',
            'generateKarmaSmarteditWebpackConfig',
            'generateKarmaSmarteditContainerWebpackConfig',
        ],
        config: function(data, conf) {

            const paths = global.smartedit.bundlePaths;
            const lodash = require('lodash');
            const path = require('path');

            const webpackConfigTemplates = require('../templates').webpackConfigTemplates;


            // ======== PROD ========
            conf.generateProdSmarteditWebpackConfig = {
                awesomeTsConfigFile: paths.external.generated.tsconfig.prodSmartedit,
                dest: paths.external.generated.webpack.prodSmartedit,
                data: lodash.cloneDeep(webpackConfigTemplates.prodWebpackConfig)
            };
            conf.generateProdSmarteditContainerWebpackConfig = {
                awesomeTsConfigFile: paths.external.generated.tsconfig.prodSmarteditContainer,
                dest: paths.external.generated.webpack.prodSmarteditContainer,
                data: lodash.cloneDeep(webpackConfigTemplates.prodWebpackConfig)
            };


            // ======== DEV ========
            conf.generateDevSmarteditWebpackConfig = {
                awesomeTsConfigFile: paths.external.generated.tsconfig.devSmartedit,
                dest: paths.external.generated.webpack.devSmartedit,
                data: lodash.cloneDeep(webpackConfigTemplates.devWebpackConfig)
            };
            conf.generateDevSmarteditContainerWebpackConfig = {
                awesomeTsConfigFile: paths.external.generated.tsconfig.devSmarteditContainer,
                dest: paths.external.generated.webpack.devSmarteditContainer,
                data: lodash.cloneDeep(webpackConfigTemplates.devWebpackConfig)
            };

            let testCommons = lodash.cloneDeep(webpackConfigTemplates.devWebpackConfig);
            testCommons.resolve.modules.push(path.resolve(process.cwd(), paths.test.unit.root));
            testCommons.resolve.alias = testCommons.resolve.alias || {};
            testCommons.resolve.alias['testhelpers'] = path.resolve(process.cwd(), paths.test.unit.root);

            // ======== KARMA ========
            conf.generateKarmaSmarteditWebpackConfig = {
                awesomeTsConfigFile: paths.external.generated.tsconfig.karmaSmartedit,
                dest: paths.external.generated.webpack.karmaSmartedit,
                data: lodash.cloneDeep(testCommons)
            };
            conf.generateKarmaSmarteditContainerWebpackConfig = {
                awesomeTsConfigFile: paths.external.generated.tsconfig.karmaSmarteditContainer,
                dest: paths.external.generated.webpack.karmaSmarteditContainer,
                data: lodash.cloneDeep(testCommons)
            };

            return conf;
        }
    };
};
