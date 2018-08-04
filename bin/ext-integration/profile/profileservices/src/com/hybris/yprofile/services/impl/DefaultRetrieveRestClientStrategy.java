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
package com.hybris.yprofile.services.impl;

import com.hybris.yprofile.rest.clients.ConsentServiceClient;
import com.hybris.yprofile.rest.clients.ProfileClient;
import com.hybris.yprofile.services.RetrieveRestClientStrategy;
import de.hybris.platform.yaasconfiguration.service.YaasServiceFactory;
import org.springframework.beans.factory.annotation.Required;

public class DefaultRetrieveRestClientStrategy implements RetrieveRestClientStrategy {

    private YaasServiceFactory yaasServiceFactory;

    public ProfileClient getProfileRestClient() {
        return getYaasServiceFactory().lookupService(ProfileClient.class);
    }

    public ConsentServiceClient getConsentServiceRestClient() {
        return getYaasServiceFactory().lookupService(ConsentServiceClient.class);
    }

    public YaasServiceFactory getYaasServiceFactory() {
        return yaasServiceFactory;
    }

    @Required
    public void setYaasServiceFactory(YaasServiceFactory yaasServiceFactory) {
        this.yaasServiceFactory = yaasServiceFactory;
    }
}
