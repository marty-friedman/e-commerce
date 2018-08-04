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
package com.hybris.yprofile.eventtracking.services;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.hybris.yprofile.common.Utils;
import com.hybris.yprofile.consent.services.ConsentService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.Map;

public class DefaultRawEventEnricher implements RawEventEnricher
{


    private static final String TIMESTAMP = "timestamp";
    private static final String IDSITE = "idsite";
    private static final String BASE_SITE_ID = "base_site_id";
    private static final String CONSENT_REFERENCE = "consent_reference";
    private static final String USER_AGENT = "user_agent";
    private static final String ACCEPT = "accept";
    private static final String ACCEPT_LANGUAGE = "accept_language";
    private static final String REFERER = "referer";

    private static final Logger LOG = Logger.getLogger(DefaultRawEventEnricher.class);


    private ConsentService consentService;

    private ObjectMapper objectMapper;


    /**
     * @see com.hybris.yprofile.eventtracking.services.RawEventEnricher#enrich(java.lang.String,
     *      javax.servlet.http.HttpServletRequest)
     */
    @Override
    public String enrich(final String json, final HttpServletRequest request)
    {

        try {
            final Map<String, Object> sourceData = getObjectMapper().readValue(json, Map.class);

            sourceData.put(TIMESTAMP, getCurrentTimestamp());

            final String siteId = getSiteId(sourceData);
            sourceData.put(BASE_SITE_ID, siteId);
            sourceData.put(CONSENT_REFERENCE, getConsentService().getConsentReferenceFromCookie(siteId, request));
            sourceData.put(USER_AGENT, getUserAgent(request));
            sourceData.put(ACCEPT, getAccept(request));
            sourceData.put(ACCEPT_LANGUAGE, getAcceptLanguage(request));
            sourceData.put(REFERER, getReferer(request));

            final String jsonString =  getObjectMapper().writeValueAsString(sourceData);
            LOG.debug("Enriched Json: " + jsonString);
            return jsonString;
        } catch (IOException e) {
            LOG.warn("Unexpected error occurred parsing json. " + e.getMessage(), e);
            return json;
        }

    }


    protected String getCurrentTimestamp() {
        return Long.toString(System.currentTimeMillis() / 1000); // seconds since Unix epoch
    }

    protected String getSiteId(final Map<String, Object> sourceData){
        final String siteId = StringUtils.trimToEmpty((String) sourceData.get(IDSITE));
        return Utils.remapSiteId(siteId);
    }

    protected String getUserAgent(HttpServletRequest request) {
        return StringUtils.trimToEmpty(request.getHeader(HttpHeaders.USER_AGENT));
    }

    protected String getAccept(HttpServletRequest request) {
        return StringUtils.trimToEmpty(request.getHeader(HttpHeaders.ACCEPT));
    }

    protected String getAcceptLanguage(HttpServletRequest request) {
        return StringUtils.trimToEmpty(request.getHeader(HttpHeaders.ACCEPT_LANGUAGE));
    }

    protected String getReferer(HttpServletRequest request) {
        return StringUtils.trimToEmpty(request.getHeader(HttpHeaders.REFERER));
    }

    @Required
    public void setObjectMapper(final ObjectMapper objectMapper)
    {
        objectMapper.configure( JsonParser.Feature.ALLOW_COMMENTS, true);
        this.objectMapper = objectMapper;

    }

    public ObjectMapper getObjectMapper()
    {
        return objectMapper;
    }

    public ConsentService getConsentService() {
        return consentService;
    }

    @Required
    public void setConsentService(ConsentService consentService) {
        this.consentService = consentService;
    }
}
