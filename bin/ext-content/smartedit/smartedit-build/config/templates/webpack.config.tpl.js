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

    const path = require('path');
    const lodash = require('lodash');

    const baseWebpackConfig = {
        output: {
            path: path.resolve("./jsTarget/"),
            filename: '[name].js',
            sourceMapFilename: '[file].map'
        },
        resolve: {
            /*
             * module resolution from sources
             */
            modules: [
                path.resolve(process.cwd(), './jsTarget/web/app'),
                path.resolve(process.cwd(), './jsTarget/web/features')
            ],
            extensions: ['.ts', '.js']
        },
        module: {
            rules: [{ // Keep this as the first element in the array. It is reference in the webpack.js config
                test: /\.ts$/,
                loader: 'awesome-typescript-loader'
            }]
        },
        stats: {
            colors: true,
            modules: true,
            reasons: true,
            errorDetails: true
        },
        plugins: [{
            apply: (compiler) => { // fixes https://github.com/webpack-contrib/karma-webpack/issues/66
                compiler.plugin('done', (stats) => {
                    if (stats.compilation.errors.length > 0) {
                        throw new Error(stats.compilation.errors.map((err) => err.message || err));
                    }
                });
            }
        }],
        bail: true
    };

    const baseExternal = {
        "angular": "angular",
        "angular-route": "angular-route",
        "angular-translate": "angular-translate",
        "crypto-js": "CryptoJS",
        /*
         * module resolution of functions from d.ts in downstream extensions:
         * it is assumed they are found under the smarteditcommons namespace
         */
        "smarteditcommons": "smarteditcommons"
    };

    const devExternal = lodash.defaultsDeep({
        "jasmine": "jasmine",
        "testutils": "testutils",
        "angular-mocks": "angular-mocks"
    }, baseExternal);

    const prodWebpackConfig = Object.assign({
        devtool: 'none',
        externals: baseExternal
    }, baseWebpackConfig);


    const devWebpackConfig = Object.assign({
        devtool: 'source-map',
        externals: devExternal
    }, baseWebpackConfig);

    return {
        // if you change this object, please update the webpack.js in the bundel config
        devWebpackConfig: devWebpackConfig,
        prodWebpackConfig: prodWebpackConfig
    };
}();
