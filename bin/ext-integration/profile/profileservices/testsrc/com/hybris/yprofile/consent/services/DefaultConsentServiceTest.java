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

import com.hybris.charon.exp.HttpException;
import com.hybris.yprofile.consent.cookie.EnhancedCookieGenerator;
import com.hybris.yprofile.consent.services.DefaultConsentService;
import com.hybris.yprofile.rest.clients.ConsentResponse;
import com.hybris.yprofile.rest.clients.ConsentServiceClient;
import com.hybris.yprofile.services.RetrieveRestClientStrategy;
import com.hybris.yprofile.services.ProfileConfigurationService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.Observable;
import rx.observers.TestSubscriber;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@UnitTest
public class DefaultConsentServiceTest {

    private static final String SITE_ID = "test";
    private static final String TENANT_ID = "tenant";

    private DefaultConsentService defaultConsentService;

    @Mock
    private ConsentServiceClient client;

    @Mock
    private ConsentResponse consentResponse;

    @Mock
    private EnhancedCookieGenerator cookieGenerator;

    @Mock
    private SessionService sessionService;

    @Mock
    private BaseSiteService baseSiteService;

    @Mock
    private UserService userService;

    @Mock
    private ProfileConfigurationService profileConfigurationService;

    @Mock
    private RetrieveRestClientStrategy retrieveRestClientStrategy;

    @Mock
    private BaseSiteModel baseSiteModel;

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);

        defaultConsentService = new DefaultConsentService();
        defaultConsentService.setSessionService(sessionService);
        defaultConsentService.setUserService(userService);
        defaultConsentService.setProfileConfigurationService(profileConfigurationService);
        defaultConsentService.setRetrieveRestClientStrategy(retrieveRestClientStrategy);
        defaultConsentService.setBaseSiteService(baseSiteService);
        defaultConsentService.setCookieGenerator(cookieGenerator);

        when(consentResponse.getId()).thenReturn("consent-reference-id");
        when(consentResponse.getLink()).thenReturn("consent-reference-link");

        when(baseSiteModel.getUid()).thenReturn(SITE_ID);

        when(retrieveRestClientStrategy.getConsentServiceRestClient()).thenReturn(client);
        when(profileConfigurationService.getYaaSTenant(SITE_ID)).thenReturn(TENANT_ID);
        when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSiteModel);
    }


    @Test
    public void assertWhenProfileAnonymousUserConsentIsNullShouldReturnFalse() throws Exception {

        Cookie cookie = new Cookie("anonymous-consents", "%5B%7B%22templateCode%22%3A%22PROFILE%22%2C%22templateVersion%22%3A1%2C%22consentState%22%3Anull%7D%5D");

        boolean result  = defaultConsentService.isProfileTrackingConsentGivenForAnonymousUser(Optional.ofNullable(cookie));

        assertFalse(result);
    }

    @Test
    public void assertWhenProfileAnonymousUserConsentIsWithdrawnShouldReturnFalse() throws Exception {

        Cookie cookie = new Cookie("anonymous-consents", "%5B%7B%22templateCode%22%3A%22PROFILE%22%2C%22templateVersion%22%3A1%2C%22consentState%22%3A%22WITHDRAWN%22%7D%5D");

        boolean result  = defaultConsentService.isProfileTrackingConsentGivenForAnonymousUser(Optional.ofNullable(cookie));

        assertFalse(result);
    }

    @Test
    public void assertWhenProfileAnonymousUserConsentIsGivenShouldReturnTrue() throws Exception {

        Cookie cookie = new Cookie("anonymous-consents", "%5B%7B%22templateCode%22%3A%22PROFILE%22%2C%22templateVersion%22%3A1%2C%22consentState%22%3A%22GIVEN%22%7D%5D");

        boolean result  = defaultConsentService.isProfileTrackingConsentGivenForAnonymousUser(Optional.ofNullable(cookie));

        assertTrue(result);
    }


    @Test
    public void assertWhenProfileLoggedInUserConsentIsNullShouldReturnFalse() throws Exception {

        Map<String, String> userConsents = new HashMap<>();
        userConsents.put("PROFILE", null);

        when(sessionService.getAttribute("user-consents")).thenReturn(userConsents);

        boolean result  = defaultConsentService.isProfileTrackingConsentGivenForLoggedInUser();

        assertFalse(result);
    }

    @Test
    public void assertWhenProfileLoggedInUserConsentIsWithdrawnShouldReturnFalse() throws Exception {

        Map<String, String> userConsents = new HashMap<>();
        userConsents.put("PROFILE", "WITHDRAWN");

        when(sessionService.getAttribute("user-consents")).thenReturn(userConsents);

        boolean result  = defaultConsentService.isProfileTrackingConsentGivenForLoggedInUser();

        assertFalse(result);
    }

    @Test
    public void assertWhenProfileLoggedInUserConsentIsGivenShouldReturnTrue() throws Exception {


        Map<String, String> userConsents = new HashMap<>();
        userConsents.put("PROFILE", "GIVEN");

        when(sessionService.getAttribute("user-consents")).thenReturn(userConsents);

        boolean result  = defaultConsentService.isProfileTrackingConsentGivenForLoggedInUser();

        assertTrue(result);
    }

    @Test
    public void getConsentReferenceIdWhenClientReturnValidConsentResponse(){

        when(client.getConsentReference("userId")).thenReturn(Observable.just(consentResponse));
        when(profileConfigurationService.isYaaSConfigurationPresentForBaseSiteId(SITE_ID)).thenReturn(true);

        TestSubscriber<ConsentResponse> testSubscriber = new TestSubscriber<>();
        defaultConsentService.generateConsentReferenceForUser("userId").subscribe(testSubscriber);

        ConsentResponse expectedResponse = new ConsentResponse();
        expectedResponse.setId("consent-reference-id");
        expectedResponse.setLink("consent-reference-link");

        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(consentResponse);
    }



    @Test(expected = HttpException.class)
    public void assertErrorWhenClientReturnException(){

        when(client.getConsentReference("userId")).thenThrow(new HttpException(403, "Forbidden"));
        when(profileConfigurationService.isYaaSConfigurationPresentForBaseSiteId(SITE_ID)).thenReturn(true);

        TestSubscriber<ConsentResponse> testSubscriber = new TestSubscriber<>();
        defaultConsentService.generateConsentReferenceForUser("userId").subscribe(testSubscriber);

        testSubscriber.assertError(HttpException.class);
    }

    @Test
    public void verifyGenerateConsentReferenceWhenNoCookieIsDefined(){

        HttpServletRequest request = mock(HttpServletRequest.class);
        Cookie[] cookies = new Cookie[]{new Cookie("some-cookie", "some value")};
        when(request.getCookies()).thenReturn(cookies);

        HttpServletResponse response = mock(HttpServletResponse.class);

        ConsentResponse consentResponse = mock(ConsentResponse.class);
        when(consentResponse.getId()).thenReturn("consent-reference-id");
        when(client.getConsentReference("userId")).thenReturn(Observable.just(consentResponse));
        when(profileConfigurationService.isYaaSConfigurationPresentForBaseSiteId(SITE_ID)).thenReturn(true);
        when(cookieGenerator.getCookieName()).thenReturn("baseSiteUid-consentReference");
        UserModel userModel = mock(UserModel.class);
        when(userModel.getUid()).thenReturn("userId");
        when(userService.getCurrentUser()).thenReturn(userModel);

        defaultConsentService.generateConsentReference(request, response);

        verify(client, times(1)).getConsentReference(anyString());
        verifyNoMoreInteractions(client);
    }

    @Test
    public void verifyDoNotGenerateConsentReferenceWhenCookieIsDefined(){

        HttpServletRequest request = mock(HttpServletRequest.class);
        Cookie[] cookies = new Cookie[]{new Cookie("some-cookie", "some value"), new Cookie("baseSiteUid-consentReference", "consent-reference-id")};
        when(request.getCookies()).thenReturn(cookies);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(cookieGenerator.getCookieName()).thenReturn("baseSiteUid-consentReference");
        defaultConsentService.generateConsentReference(request, response);

        verifyNoMoreInteractions(cookieGenerator);
        verifyNoMoreInteractions(client);
    }

    @Test
    public void verifyDoNotSetInvalidConsentReferenceInCookie(){

        HttpServletRequest request = mock(HttpServletRequest.class);
        Cookie[] cookies = new Cookie[]{new Cookie("some-cookie", "some value")};
        when(request.getCookies()).thenReturn(cookies);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(client.getConsentReference("userId")).thenReturn(Observable.just(null));
        when(profileConfigurationService.isYaaSConfigurationPresentForBaseSiteId(SITE_ID)).thenReturn(true);
        when(cookieGenerator.getCookieName()).thenReturn("baseSiteUid-consentReference");
        UserModel userModel = mock(UserModel.class);
        when(userModel.getUid()).thenReturn("userId");
        when(userService.getCurrentUser()).thenReturn(userModel);

        defaultConsentService.generateConsentReference(request, response);

        verify(client, times(1)).getConsentReference(anyString());
        verify(cookieGenerator, times(0)).addCookie(anyObject(), anyString());
        verifyNoMoreInteractions(cookieGenerator);
        verifyNoMoreInteractions(client);
    }

    @Test
    public void verifyDoNotRequestConsentReferenceWithInvalidYaaSConfiguration(){

        HttpServletRequest request = mock(HttpServletRequest.class);
        Cookie[] cookies = new Cookie[]{new Cookie("some-cookie", "some value")};
        when(request.getCookies()).thenReturn(cookies);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(profileConfigurationService.isYaaSConfigurationPresentForBaseSiteId(SITE_ID)).thenReturn(false);
        when(cookieGenerator.getCookieName()).thenReturn("baseSiteUid-consentReference");
        UserModel userModel = mock(UserModel.class);
        when(userModel.getUid()).thenReturn("userId");
        when(userService.getCurrentUser()).thenReturn(userModel);

        defaultConsentService.generateConsentReference(request, response);

        verify(client, times(0)).getConsentReference(anyString());
    }
}