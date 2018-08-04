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
package com.hybris.yprofile.consent.services;


import com.hybris.yprofile.consent.cookie.EnhancedCookieGenerator;
import com.hybris.yprofile.rest.clients.ConsentResponse;
import com.hybris.yprofile.rest.clients.ConsentServiceClient;
import com.hybris.yprofile.services.RetrieveRestClientStrategy;
import com.hybris.yprofile.services.ProfileConfigurationService;
import com.hybris.yprofile.common.Utils;
import de.hybris.eventtracking.model.cookie.ProfileConsentCookie;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import rx.Observable;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.*;

import static java.util.Optional.ofNullable;

/**
 * Implementation for {@link ConsentService}. Service is responsible to generate and provide the consent reference.
 */
public class DefaultConsentService implements ConsentService {
    private static final Logger LOG = Logger.getLogger(DefaultConsentService.class);

    private static final String CONSENT_REFERENCE_SESSION_ATTR_KEY = "consent-reference";
    private static final String CONSENT_REFERENCE_TOKEN_SESSION_ATTR_KEY = "consent-reference-token";
    private static final String CONSENT_REFERENCE_TOKEN_COOKIE_NAME = "yaas-consent-reference-token";
    private static final String CONSENT_REFERENCE_COOKIE_NAME_SUFFIX = "-consentReference";

    private EnhancedCookieGenerator cookieGenerator;

    private SessionService sessionService;

    private UserService userService;

    private BaseSiteService baseSiteService;

    private RetrieveRestClientStrategy retrieveRestClientStrategy;

    private ProfileConfigurationService profileConfigurationService;

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String UTF_8 = "UTF-8";


    @Override
    public boolean isProfileTrackingConsentGiven(final HttpServletRequest request){
        if (isAnonymousUser()) {
            return isProfileTrackingConsentGivenForAnonymousUser(Utils.getCookie(request, ConsentService.ANONYMOUS_CONSENTS));
        } else {
            return isProfileTrackingConsentGivenForLoggedInUser();
        }
    }

    @Override
    public boolean isProfileTrackingConsentGivenForLoggedInUser() {
        boolean track = false;

        try {
            Map<String, String> userConsents = getSessionService().getAttribute(USER_CONSENTS);
            if(userConsents != null
                    && userConsents.containsKey(PROFILE_CONSENT)
                    && CONSENT_GIVEN.equals(userConsents.get(PROFILE_CONSENT))) {
                track = true;
            }
        }catch(Exception ex)
        {
            LOG.warn("Error while processing user consents", ex);
        }
        return track;
    }

    protected boolean isAnonymousUser() {
        return (getUserService().isAnonymousUser(getUserService().getCurrentUser())
                || isUserSoftLoggedIn()
                || getSessionService().getAttribute(USER_CONSENTS) == null);
    }

    protected boolean isUserSoftLoggedIn(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication instanceof RememberMeAuthenticationToken);
    }

    @Override
    public boolean isProfileTrackingConsentGivenForAnonymousUser(final Optional<Cookie> anonymousConsentCookie) {

        boolean track = false;

        try {
            if (anonymousConsentCookie.isPresent()) {
                final List<ProfileConsentCookie> profileConsentCookieList;

                profileConsentCookieList = new ArrayList(Arrays
                        .asList(mapper.readValue(URLDecoder.decode(anonymousConsentCookie.get().getValue(), UTF_8), ProfileConsentCookie[].class)));

                track = profileConsentCookieList.stream().filter(x-> PROFILE_CONSENT.equals(x.getTemplateCode()))
                        .map(k->CONSENT_GIVEN.equals(k.getConsentState())).reduce(true, (x, y)-> x && y);
            }

        }catch (Exception ex){
            LOG.warn("Error while processing anonymous consents", ex);
        }
        return track;
    }


    @Override
    public void generateConsentReference(final HttpServletRequest request, final HttpServletResponse response)
    {
        generateConsentReference(request, response, true);
    }

    @Override
    public void generateConsentReference(final HttpServletRequest request, final HttpServletResponse response, final boolean shouldGenerateConsentReference) {
        String consentReferenceId = getConsentReferenceFromCookie(getSiteId(), request);
        String token = getConsentReferenceTokenFromCookie(request);

        if (consentReferenceId == null && shouldGenerateConsentReference) {

            try {
                ConsentResponse consentResponse = generateConsentReferenceForUser(getUserId()).toBlocking().first();

                if (consentResponse != null) {
                    consentReferenceId = consentResponse.getId();
                    token = consentResponse.getConsentReferenceToken();

                    addConsentReferenceCookie(response, consentReferenceId, token);
                }
            } catch (Exception e){
                LOG.error("Error requesting consent ref", e);
            }

        }

        storeConsentReferenceInSession(consentReferenceId, token);
    }

    protected String getUserId(){

        String userId = null;

        UserModel user = getUserService().getCurrentUser();

        if (!getUserService().isAnonymousUser(user)){
            return user.getUid();
        }
        return StringUtils.trimToEmpty(userId);
    }

    @Override
    public String getConsentReferenceFromCookie(final String siteId, final HttpServletRequest request) {

        final String consentReferenceCookieName = siteId + CONSENT_REFERENCE_COOKIE_NAME_SUFFIX;
        Optional<Cookie> cookie = Utils.getCookie(request, consentReferenceCookieName);
        if (cookie.isPresent()) {
            return cookie.get().getValue();
        }
        return null;
    }


    protected String getConsentReferenceTokenFromCookie(final HttpServletRequest request) {

        final String consentReferenceTokenCookieName = CONSENT_REFERENCE_TOKEN_COOKIE_NAME;
        Optional<Cookie> cookie = Utils.getCookie(request, consentReferenceTokenCookieName);
        if (cookie.isPresent()) {
            return cookie.get().getValue();
        }
        return null;
    }

    protected void addConsentReferenceCookie(final HttpServletResponse response, final  String consentReferenceId, final String token) {
        if (consentReferenceId == null){
            return;
        }

        try {
            final String consentReferenceCookieName = getSiteId() + CONSENT_REFERENCE_COOKIE_NAME_SUFFIX;
            Utils.setCookie(cookieGenerator, response, consentReferenceCookieName, consentReferenceId);
            Utils.setCookie(cookieGenerator, response, CONSENT_REFERENCE_TOKEN_COOKIE_NAME, token);
        } catch (Exception e) {
            LOG.error("Error setting consent reference id cookie", e);
        }
    }

    protected void storeConsentReferenceInSession(final String consentReferenceId, final String token) {

        setAttributeInSession(CONSENT_REFERENCE_SESSION_ATTR_KEY, consentReferenceId);
        setAttributeInSession(CONSENT_REFERENCE_TOKEN_SESSION_ATTR_KEY, token);
    }

    @Override
    public void setProfileConsentCookie(final HttpServletRequest request, final HttpServletResponse response, final boolean consent){

        Optional<Cookie> cookie = Utils.getCookie(request, PROFILE_CONSENT_GIVEN);

        setAttributeInSession(PROFILE_CONSENT_GIVEN, consent);

        if (!cookie.isPresent()) {
            Utils.setCookie(cookieGenerator, response, PROFILE_CONSENT_GIVEN, Boolean.toString(consent));
            return;
        }

        if (!cookie.get().getValue().equals(Boolean.toString(consent))){
            Utils.setCookie(cookieGenerator, response, PROFILE_CONSENT_GIVEN, Boolean.toString(consent));
            return;
        }
    }

    @Override
    public String getConsentReferenceFromSession(){
        if (getSessionService().getAttribute(PROFILE_CONSENT_GIVEN) != null &&
                Boolean.TRUE.equals(getSessionService().getAttribute(PROFILE_CONSENT_GIVEN)) ){
            return getSessionService().getAttribute(CONSENT_REFERENCE_SESSION_ATTR_KEY);
        }

        return null;
    }

    @Override
    public Observable<ConsentResponse> generateConsentReferenceForUser(final String userId) {

        if(getProfileConfigurationService().isYaaSConfigurationPresentForBaseSiteId(getSiteId())) {

            return getClient().getConsentReference(userId).map(
                    consentResponse -> {
                        logSuccess(consentResponse);
                        return consentResponse;
                    })
                    .doOnError(error -> logError(error));

        } else {
            LOG.warn("YaaS Configuration not found");
            return Observable.just(null);
        }
    }


    protected void setAttributeInSession(final String key, final Object value){

        if (value == null){
            return;
        }

        try {
            getSessionService().setAttribute(key, value);

        } catch (Exception e) {
            LOG.warn("Error setting " + key + " in session", e);
        }

    }

    protected String getSiteId(){
        return getCurrentBaseSiteModel().isPresent() ? getCurrentBaseSiteModel().get().getUid() : StringUtils.EMPTY;
    }

    protected Optional<BaseSiteModel> getCurrentBaseSiteModel() {
        return ofNullable(getBaseSiteService().getCurrentBaseSite());
    }

    protected static void logSuccess(ConsentResponse consentResponse){
        if (LOG.isDebugEnabled()) {
            LOG.debug("Consent reference retrieved successfully. consent-reference: " + consentResponse);
        }
    }

    protected static void logError(Throwable error){
        LOG.error("Error requesting consent reference", error);
    }

    protected ConsentServiceClient getClient() {
        return getRetrieveRestClientStrategy().getConsentServiceRestClient();
    }

    protected EnhancedCookieGenerator getCookieGenerator() {
        return cookieGenerator;
    }

    @Required
    public void setCookieGenerator(EnhancedCookieGenerator cookieGenerator) {
        this.cookieGenerator = cookieGenerator;
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    @Required
    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public UserService getUserService() {
        return userService;
    }

    @Required
    public void setUserService(UserService userService) {
        this.userService = userService;
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

    public BaseSiteService getBaseSiteService()
    {
        return baseSiteService;
    }

    @Required
    public void setBaseSiteService(final BaseSiteService baseSiteService)
    {
        this.baseSiteService = baseSiteService;
    }
}

