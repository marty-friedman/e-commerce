/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
module.exports = function() {

    return {
        config: function(data, conf) {
            const lodash = require('lodash');

            const personalizationsmarteditPaths = {
                "personalizationsmartedit/*": ["web/features/personalizationsmartedit/*"],
                "personalizationcommons": ["web/features/personalizationcommons"],
                "personalizationcommons*": ["web/features/personalizationcommons*"]
            };

            const personalizationsmarteditContainerPaths = {
                "personalizationsmarteditcontainer/*": ["web/features/personalizationsmarteditcontainer/*"],
                "personalizationcommons": ["web/features/personalizationcommons"],
                "personalizationcommons*": ["web/features/personalizationcommons*"]
            };

            function addPersonalizationSmarteditPaths(conf) {
                lodash.merge(conf.compilerOptions.paths, lodash.cloneDeep(personalizationsmarteditPaths));
            }

            function addPersonalizationSmarteditContainerPaths(conf) {
                lodash.merge(conf.compilerOptions.paths, lodash.cloneDeep(personalizationsmarteditContainerPaths));
            }

            addPersonalizationSmarteditPaths(conf.generateProdSmarteditTsConfig.data);
            addPersonalizationSmarteditContainerPaths(conf.generateProdSmarteditContainerTsConfig.data);
            addPersonalizationSmarteditPaths(conf.generateDevSmarteditTsConfig.data);
            addPersonalizationSmarteditContainerPaths(conf.generateDevSmarteditContainerTsConfig.data);
            addPersonalizationSmarteditPaths(conf.generateKarmaSmarteditTsConfig.data);
            addPersonalizationSmarteditContainerPaths(conf.generateKarmaSmarteditContainerTsConfig.data);
            addPersonalizationSmarteditContainerPaths(conf.generateIDETsConfig.data);
            addPersonalizationSmarteditPaths(conf.generateIDETsConfig.data);

            return conf;
        }
    };
};
