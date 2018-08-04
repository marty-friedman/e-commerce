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
package com.hybris.yprofile.eventtracking.rest;

import com.hybris.yprofile.consent.services.ConsentService;
import com.hybris.yprofile.eventtracking.services.RawEventEnricher;
import com.hybris.yprofile.common.Utils;
import com.hybris.yprofile.services.ProfileConfigurationService;
import de.hybris.platform.util.Config;

import java.io.IOException;
import java.util.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller("defaultEventsController")
@RequestMapping(value = "/events")
public class EventsController
{

    private static final Logger LOG = Logger.getLogger(EventsController.class);

    private static final String ACCESS_CONTROL_ALLOW_ORIGIN_HEADER = "Access-Control-Allow-Origin";
    private static final String ACCESS_CONTROL_ALLOW_ORIGIN_HEADER_VALUE_CONF_PROPERTY = "yprofileeventtrackingws.events_endpoint.ok_response.access_control_allow_origin_header_value";
    private static final String ACCESS_CONTROL_ALLOW_ORIGIN_HEADER_DEFAULT_VALUE = "*";
    private static final String EVENTS_ENDPOINT_ENABLED_CONF_PROPERTY = "yprofileeventtrackingws.events_endpoint.enabled";

    private final QueueChannel rawTrackingEventsChannel;

    private final RawEventEnricher rawEventEnricher;

    private ConsentService consentService;

    public EventsController(final QueueChannel rawTrackingEventsChannel, final RawEventEnricher rawEventEnricher, final ConsentService consentService)
    {
        this.rawTrackingEventsChannel = rawTrackingEventsChannel;
        this.rawEventEnricher = rawEventEnricher;
        this.consentService = consentService;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<String> trackEvent(final HttpServletRequest request, final HttpServletResponse response) throws IOException
    {
        if (Config.getBoolean(EVENTS_ENDPOINT_ENABLED_CONF_PROPERTY, true))
        {
            final String body = extractBody(request);
            if (LOG.isDebugEnabled())
            {
                LOG.debug("Events endpoint handling track event request with body: \n" + body);
            }

            if (isProfileTrackingPaused(request)) {
                LOG.debug("Profile tracking disabled");

            } else {

                final String payload = rawEventEnricher.enrich(body, request);

                forwardForProcessing(payload);
            }
        }
        else
        {
            if (LOG.isDebugEnabled())
            {
                LOG.debug("Events endpoint is disabled. Ignoring request.");
            }
        }

        return ok();
    }

    private boolean isProfileTrackingPaused(HttpServletRequest httpServletRequest) {

        Optional<Cookie> pauseProfileTrackingCookie = Utils.getCookie(httpServletRequest, ProfileConfigurationService.PROFILE_TRACKING_PAUSE);

        Optional<Cookie> profileTrackingConsentGivenCookie = Utils.getCookie(httpServletRequest, ConsentService.PROFILE_CONSENT_GIVEN);

        return !isProfileTrackingConsentGiven(profileTrackingConsentGivenCookie) || pauseProfileTrackingCookie.isPresent();
    }

    private boolean isProfileTrackingConsentGiven(Optional<Cookie> profileTrackingConsentGivenCookie) {
        return profileTrackingConsentGivenCookie.isPresent() && profileTrackingConsentGivenCookie.get().getValue().equals(Boolean.TRUE.toString());
    }

    protected String extractBody(final HttpServletRequest request) throws IOException
    {
        return IOUtils.toString(request.getReader());
    }

    protected void forwardForProcessing(final String payload)
    {
        final Message<String> message = new GenericMessage<String>(payload);
        rawTrackingEventsChannel.send(message);
    }

    protected ResponseEntity<String> ok()
    {
        final HttpHeaders headers = new HttpHeaders();
        withCorsHeaders(headers);
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

    protected HttpHeaders withCorsHeaders(final HttpHeaders headers)
    {
        headers.add(ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, Config.getString(ACCESS_CONTROL_ALLOW_ORIGIN_HEADER_VALUE_CONF_PROPERTY,
                ACCESS_CONTROL_ALLOW_ORIGIN_HEADER_DEFAULT_VALUE));
        return headers;
    }

    public ConsentService getConsentService() {
        return consentService;
    }
}
