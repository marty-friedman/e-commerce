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

import com.hybris.yprofile.common.Utils;
import com.hybris.yprofile.rest.clients.ProfileClient;
import com.hybris.yprofile.services.ProfileConfigurationService;
import com.hybris.yprofile.services.RetrieveRestClientStrategy;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.yaasconfiguration.model.YaasClientCredentialModel;
import de.hybris.platform.yaasconfiguration.model.YaasProjectModel;
import de.hybris.platform.yaasconfiguration.model.YaasServiceModel;
import de.hybris.platform.yaasconfiguration.service.YaasConfigurationService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

/**
 * Default implementation for the {@link ProfileConfigurationService}.
 */
public class DefaultProfileConfigurationService implements ProfileConfigurationService {

    private static final Logger LOG = Logger.getLogger(DefaultProfileConfigurationService.class);

    private YaasConfigurationService yaasConfigurationService;
    private BaseSiteService baseSiteService;
    private RetrieveRestClientStrategy retrieveRestClientStrategy;

    private SessionService sessionService;

    private boolean isProfileTrackingPaused;

    @Override
    public boolean isProfileTrackingPaused() {
        try {

            if (getSessionService().getAttribute(PROFILE_TRACKING_PAUSE) == null) {

                //default value
                setProfileTrackingPauseValue(isProfileTrackingPaused);
            }

            return (Boolean) getSessionService().getAttribute(PROFILE_TRACKING_PAUSE);

        } catch (Exception e) {
            LOG.warn("Error getting "+PROFILE_TRACKING_PAUSE+" from session", e);
        }

        return isProfileTrackingPaused;
    }

    @Override
    public void setProfileTrackingPauseValue(final boolean isProfileTrackingPaused) {

        try {
            getSessionService().setAttribute(PROFILE_TRACKING_PAUSE, isProfileTrackingPaused);
        } catch (Exception e) {
            LOG.warn("Error setting "+PROFILE_TRACKING_PAUSE+" in session with value " + isProfileTrackingPaused, e);
        }
    }

    @Override
    public boolean isYaaSConfigurationPresentForBaseSiteId(final String siteId){

        if (!getCurrentBaseSiteModel().isPresent()) {
            if (getBaseSiteForUID(siteId).isPresent()) {
                getBaseSiteService().setCurrentBaseSite(Utils.remapSiteId(siteId), true);
            } else {
                LOG.warn("Failed to load base site: '" + Utils.remapSiteId(siteId) + "'");
                return false;
            }
        }

        try {
            getRetrieveRestClientStrategy().getProfileRestClient();
        } catch (SystemException e){
            LOG.debug("Cannot retrieve YaaS Configuration for service: '" + ProfileClient.class.getSimpleName() + "' and base site: '" + Utils.remapSiteId(siteId) + "'",e);
            return false;
        }

        return true;
    }

    @Override
    public String getYaaSTenant(final String siteId) {
        return getYaasProject(siteId).isPresent() ? getYaasProject(siteId).get().getIdentifier() : StringUtils.EMPTY;
    }

    @Override
    public String getYaaSClientId(final String siteId) {
        return getYaasClientCredential(siteId).isPresent() ? getYaasClientCredential(siteId).get().getClientId() : StringUtils.EMPTY;
    }

    @Override
    public String getYaaSProfileTagUrl() {
        String profileTagUrl = StringUtils.EMPTY;
        try {
            if(ofNullable(getYaasConfigurationService().getYaasServiceForId(PROFILE_TAG_URL)).isPresent()) {
                profileTagUrl = getYaasConfigurationService().getYaasServiceForId(PROFILE_TAG_URL).getServiceURL();
            }
        } catch (ModelNotFoundException e){
            LOG.warn("Cannot retrieve YaaS Configuration for service: '" + PROFILE_TAG_URL + "'");
        }
        return profileTagUrl;
    }

    @Override
    public String getYaaSProfileTagConfigUrl() {
        String profileTagConfigUrl = StringUtils.EMPTY;
        try {
            if(ofNullable(getYaasConfigurationService().getYaasServiceForId(PROFILE_TAG_CONFIG_URL)).isPresent()) {
                profileTagConfigUrl = getYaasConfigurationService().getYaasServiceForId(PROFILE_TAG_CONFIG_URL).getServiceURL();
            }
        } catch (ModelNotFoundException e){
            LOG.warn("Cannot retrieve YaaS Configuration for service: '" + PROFILE_TAG_CONFIG_URL + "'");
        }
        return profileTagConfigUrl;
    }

    protected Optional<YaasProjectModel> getYaasProject(final String siteId) {

        if (isYaaSConfigurationPresentForBaseSiteId(siteId)) {
            return ofNullable(getYaasConfigurationService().getBaseSiteServiceMappingForId(siteId, getYaasServiceModel().get()).getYaasClientCredential().getYaasProject());
        }

        return empty();
    }

    protected Optional<YaasClientCredentialModel> getYaasClientCredential(final String siteId) {

        if (isYaaSConfigurationPresentForBaseSiteId(siteId)) {
            return ofNullable(getYaasConfigurationService().getBaseSiteServiceMappingForId(siteId, getYaasServiceModel().get()).getYaasClientCredential());
        }

        return empty();
    }

    protected Optional<YaasServiceModel> getYaasServiceModel()
    {
        try {
            return ofNullable(getYaasConfigurationService().getYaasServiceForId(ProfileClient.class.getSimpleName()));
        } catch (ModelNotFoundException e){
            LOG.warn("Cannot retrieve YaaS Configuration for service: '" + ProfileClient.class.getSimpleName() + "'");
        }

        return empty();
    }

    protected Optional<BaseSiteModel> getCurrentBaseSiteModel() {
        return ofNullable(getBaseSiteService().getCurrentBaseSite());
    }

    protected Optional<BaseSiteModel> getBaseSiteForUID(final String siteId) {
        return ofNullable(getBaseSiteService().getBaseSiteForUID(siteId));
    }

    public YaasConfigurationService getYaasConfigurationService() {
        return yaasConfigurationService;
    }

    @Required
    public void setYaasConfigurationService(YaasConfigurationService yaasConfigurationService) {
        this.yaasConfigurationService = yaasConfigurationService;
    }

    public BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    @Required
    public void setBaseSiteService(BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }

    public RetrieveRestClientStrategy getRetrieveRestClientStrategy() {
        return retrieveRestClientStrategy;
    }

    @Required
    public void setRetrieveRestClientStrategy(RetrieveRestClientStrategy retrieveRestClientStrategy) {
        this.retrieveRestClientStrategy = retrieveRestClientStrategy;
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    @Required
    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Required
    public void setProfileTrackingPaused(boolean profileTrackingPaused) {
        this.isProfileTrackingPaused = profileTrackingPaused;
    }
}
