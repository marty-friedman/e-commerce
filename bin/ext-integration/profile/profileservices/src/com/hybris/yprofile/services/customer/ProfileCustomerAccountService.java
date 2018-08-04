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
package com.hybris.yprofile.services.customer;

import com.hybris.yprofile.consent.services.ConsentService;
import com.hybris.yprofile.services.ProfileConfigurationService;
import com.hybris.yprofile.services.ProfileTransactionService;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.customer.impl.DefaultCustomerAccountService;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.session.impl.DefaultSessionTokenService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * yProfile implementation for the {@link CustomerAccountService} to send registration events.
 */
public class ProfileCustomerAccountService extends DefaultCustomerAccountService {

    private static final Logger LOG = Logger.getLogger(ProfileCustomerAccountService.class);

    private ProfileTransactionService profileTransactionService;
    private ConsentService consentService;
    private DefaultSessionTokenService defaultSessionTokenService;
    private ProfileConfigurationService profileConfigurationService;

    @Override
    public void register(final CustomerModel customerModel, final String password) throws DuplicateUidException
    {
        super.register(customerModel, password);

        try {

            if (!getProfileConfigurationService().isProfileTrackingPaused()) {
                String consentReferenceId = getConsentService().getConsentReferenceFromSession();
                String sessionId = getDefaultSessionTokenService().getOrCreateSessionToken();

                if (consentReferenceId != null) {
                    getProfileTransactionService().sendUserRegistrationEvent(customerModel, consentReferenceId, sessionId, getSiteId());
                }
            }
        } catch (Exception e){
            LOG.error("Error sending login event to yaas", e);
        }
    }

    protected String getSiteId() {
        return getBaseSiteService().getCurrentBaseSite().getUid();
    }

    public DefaultSessionTokenService getDefaultSessionTokenService() {
        return defaultSessionTokenService;
    }

    @Required
    public void setDefaultSessionTokenService(DefaultSessionTokenService defaultSessionTokenService) {
        this.defaultSessionTokenService = defaultSessionTokenService;
    }

    public ProfileTransactionService getProfileTransactionService() {
        return profileTransactionService;
    }

    @Required
    public void setProfileTransactionService(ProfileTransactionService profileTransactionService) {
        this.profileTransactionService = profileTransactionService;
    }

    public ConsentService getConsentService() {
        return consentService;
    }

    @Required
    public void setConsentService(ConsentService consentService) {
        this.consentService = consentService;
    }

    public ProfileConfigurationService getProfileConfigurationService() {
        return profileConfigurationService;
    }

    @Required
    public void setProfileConfigurationService(ProfileConfigurationService profileConfigurationService) {
        this.profileConfigurationService = profileConfigurationService;
    }
}
