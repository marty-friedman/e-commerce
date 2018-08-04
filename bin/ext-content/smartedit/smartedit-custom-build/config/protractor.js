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
module.exports = function(grunt) {

    const e2eshardPath = global.smartedit.bundlePaths.build.util.e2eshardPath;
    const e2eshard = require(e2eshardPath)(grunt);
    
    return {
        targets: [
        
        ],
        config: function(data, conf) {
            
            
            const lodash = require('lodash');
            const paths = require('../paths');

            let optionSpecs = {
                options: {
                    args: {
                        specs: e2eshard.getSpecs(paths.tests.allE2e)
                    }
                }
            };

            lodash.defaultsDeep(conf.run, optionSpecs);
            lodash.defaultsDeep(conf.maxrun, optionSpecs);

            return conf;
        }
    };
};
