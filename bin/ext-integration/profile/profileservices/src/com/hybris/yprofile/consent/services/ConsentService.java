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

import com.hybris.yprofile.rest.clients.ConsentResponse;
import rx.Observable;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * ConsentService interface. Service is responsible to generate and provide the consent reference.
 */
public interface ConsentService {


    /**
     * Session attribute for logged in users
     */
    String USER_CONSENTS = "user-consents";

    /**
     * Cookie for anonymous user
     */
    String ANONYMOUS_CONSENTS = "anonymous-consents";

    /**
     * Consent code for Profile
     */
    String PROFILE_CONSENT = "PROFILE";

    /**
     * Expected consent value
     */
    String CONSENT_GIVEN = "GIVEN";

    /**
     * Cookie and Session attribute key
     */
    String PROFILE_CONSENT_GIVEN = "profile.consent.given";

    /**
     * Checks if the user (logged In or Anonymous) has granted the consent to allow profile tracking
     *
     * @param request the http request
     * @return true or false
     */
    boolean isProfileTrackingConsentGiven(final HttpServletRequest request);

    /**
     * Checks if the Log In User has granted the consent to allow profile tracking
     * The consents for Log In Users are stored in Session
     *
     * @return true or false
     */
    boolean isProfileTrackingConsentGivenForLoggedInUser();


    /**
     * Saves the profile consent separately in a cookie and session attribute
     * @param request http request
     * @param response http reponse
     * @param consent true or false
     */
    void setProfileConsentCookie(final HttpServletRequest request, final HttpServletResponse response, final boolean consent);

    /**
     * Checks if the Anonymous User has granted the consent to allow profile tracking
     * The consents for Anonymous Users are stored in a Cookie
     *
     * @param anonymousConsentCookie
     * @return true or false
     */
    boolean isProfileTrackingConsentGivenForAnonymousUser(final Optional<Cookie> anonymousConsentCookie);

    /**
     * Executes the rest call to the consent service to get the consent reference for a given user
     *
     * @param userId the user id
     * @return the consent service response
     */
    Observable<ConsentResponse> generateConsentReferenceForUser(String userId);

    /**
     * Generates the consent reference for the user and stores it both in a cookie
     * and in the session
     *
     * @param request Http request
     * @param response Http response with the consent reference cookie
     */
    void generateConsentReference(final HttpServletRequest request, final HttpServletResponse response);

    /**
     * Generates the consent reference for the user and stores it both in a cookie
     * and in the session
     * @param request Http request
     * @param response Http response with the consent reference cookie
     * @param shouldGenerateConsentReference should generate consent reference
     */
    void generateConsentReference(final HttpServletRequest request, final HttpServletResponse response, final boolean shouldGenerateConsentReference);

    /**
     * Fetches the consent reference from session
     * @return consent reference
     */
    String getConsentReferenceFromSession();

    /**
     * Fetches the consent reference from cookie
     * @param siteId base site identifier
     * @param request http request
     * @return consent reference
     */
    String getConsentReferenceFromCookie(final String siteId, final HttpServletRequest request);
}
