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
            'rules'
        ],
        config: function(data, conf) {
            return {
                options: {
                    module: "commonjs",
                    moduleResolution: "node",
                    target: "es5",
                    sourceMap: false,
                    "lib": [
                        "dom",
                        "es5",
                        "scripthost",
                        "es2015",
                        "es2015.iterable"
                    ],
                    rootDir: global.smartedit.bundlePaths.bundleRoot + "/config/tslint/rules/"
                },
                rules: {
                    src: global.smartedit.bundlePaths.bundleRoot + "/config/tslint/rules/*.ts",
                    outDir: global.smartedit.bundlePaths.bundleRoot + "/config/tslint/rules/generated/"
                }
            };
        }
    };

};
