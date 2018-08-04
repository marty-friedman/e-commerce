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

            const personalizationpromotionssmarteditPaths = {
                "personalizationpromotionssmartedit/*": ["web/features/personalizationpromotionssmartedit/*"],
                "personalizationpromotionssmarteditcommons": ["web/features/personalizationpromotionssmarteditcommons"],
                "personalizationpromotionssmarteditcommons*": ["web/features/personalizationpromotionssmarteditcommons*"]
            };

            const personalizationpromotionssmarteditContainerPaths = {
                "personalizationpromotionssmarteditcontainer/*": ["web/features/personalizationpromotionssmarteditContainer/*"],
                "personalizationpromotionssmarteditcommons": ["web/features/personalizationpromotionssmarteditcommons"],
                "personalizationpromotionssmarteditcommons*": ["web/features/personalizationpromotionssmarteditcommons*"]
            };

            function addPersonalizationPromotionsSmarteditPaths(conf) {
                lodash.merge(conf.compilerOptions.paths, lodash.cloneDeep(personalizationpromotionssmarteditPaths));
            }

            function addPersonalizationPromotionsSmarteditContainerPaths(conf) {
                lodash.merge(conf.compilerOptions.paths, lodash.cloneDeep(personalizationpromotionssmarteditContainerPaths));
            }

            addPersonalizationPromotionsSmarteditPaths(conf.generateProdSmarteditTsConfig.data);
            addPersonalizationPromotionsSmarteditContainerPaths(conf.generateProdSmarteditContainerTsConfig.data);
            addPersonalizationPromotionsSmarteditPaths(conf.generateDevSmarteditTsConfig.data);
            addPersonalizationPromotionsSmarteditContainerPaths(conf.generateDevSmarteditContainerTsConfig.data);
            addPersonalizationPromotionsSmarteditPaths(conf.generateKarmaSmarteditTsConfig.data);
            addPersonalizationPromotionsSmarteditContainerPaths(conf.generateKarmaSmarteditContainerTsConfig.data);
            addPersonalizationPromotionsSmarteditContainerPaths(conf.generateIDETsConfig.data);
            addPersonalizationPromotionsSmarteditPaths(conf.generateIDETsConfig.data);

            return conf;
        }
    };
};
