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
package com.hybris.yprofile.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hybris.yprofile.dto.TrackingEvent;
import com.hybris.yprofile.rest.clients.ProfileClient;
import com.hybris.yprofile.rest.clients.ProfileResponse;
import com.hybris.yprofile.common.Utils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * Implementation for {@link ProfileTrackingEventService}. Communication service to send tracking events to Profile
 */
public class DefaultProfileTrackingEventService implements ProfileTrackingEventService {
    private static final Logger LOG = Logger.getLogger(DefaultProfileTrackingEventService.class);
    private static final String NULL = "null";
    private static final String TRACKING_EVENT_TYPE = "piwik";

    private RetrieveRestClientStrategy retrieveRestClientStrategy;

    private ProfileConfigurationService profileConfigurationService;

    @Override
    public void sendTrackingEvent(final String consentReference, final TrackingEvent trackingEventDTO) {

        if (getProfileConfigurationService().isYaaSConfigurationPresentForBaseSiteId(Utils.remapSiteId(trackingEventDTO.getIdsite())) &&
                this.isValidConsentReference(consentReference)) {

            getClient().sendEvent(TRACKING_EVENT_TYPE,
                    consentReference,
                    trackingEventDTO.getUserAgent(),
                    trackingEventDTO.getAccept(),
                    trackingEventDTO.getAcceptLanguage(),
                    trackingEventDTO.getReferer(),
                    trackingEventDTO)
                    .subscribe(response -> this.logSuccess(response, trackingEventDTO),
                            error -> this.logError(trackingEventDTO, error));

        } else {
            LOG.warn("YaaS Configuration not found for site: '" + Utils.remapSiteId(trackingEventDTO.getIdsite()) + "' or Invalid consent reference: '" + consentReference + "'");
        }
    }

    protected static boolean isValidConsentReference(String consentReference) {
        return StringUtils.isNotBlank(consentReference) && !NULL.equals(consentReference);
    }

    protected static void logSuccess(final ProfileResponse result, final TrackingEvent trackingEventDTO){
        if (LOG.isDebugEnabled()) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            String event = trackingEventDTO.getAction_name();
            try {
                event = mapper.writeValueAsString(trackingEventDTO);
            } catch (JsonProcessingException e) {
                LOG.error("Encountered problem with json processing", e);
            }

            LOG.debug("Event " + event + " sent to yaas with response " + result);
        }
    }

    protected static void logError(final TrackingEvent trackingEventDTO, final Throwable error){
        LOG.error("Event tracking failed with dto" + trackingEventDTO.toString(), error);
    }

    protected ProfileClient getClient() {
        return getRetrieveRestClientStrategy().getProfileRestClient();
    }

    public ProfileConfigurationService getProfileConfigurationService() {
        return profileConfigurationService;
    }

    @Required
    public void setProfileConfigurationService(ProfileConfigurationService profileConfigurationService) {
        this.profileConfigurationService = profileConfigurationService;
    }

    public RetrieveRestClientStrategy getRetrieveRestClientStrategy() {
        return retrieveRestClientStrategy;
    }

    @Required
    public void setRetrieveRestClientStrategy(RetrieveRestClientStrategy retrieveRestClientStrategy) {
        this.retrieveRestClientStrategy = retrieveRestClientStrategy;
    }
}
