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

import com.hybris.yprofile.dto.TrackingEvent;
import com.hybris.yprofile.rest.clients.ProfileClient;
import com.hybris.yprofile.rest.clients.ProfileResponse;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.mockito.Mockito.*;

@UnitTest
public class DefaultProfileTrackingEventServiceTest {

    private static final String APP_ID = "test";
    private static final String TENANT_ID = "tenant";
    private static final String CONSENT_REFERENCE = "consent-reference-test";
    private static final String USER_AGENT = "some user agent";
    private static final String ACCEPT = "accept header";
    private static final String ACCEPT_LANGUAGE = "en-US,en;q=0.8";
    private static final String REFERER = "referer header";
    private static final String SITE_ID = "test";

    private DefaultProfileTrackingEventService trackingEventService;

    @Mock
    private ProfileClient client;

    @Mock
    private TrackingEvent trackingEvent;

    @Mock
    private ProfileResponse response;

    @Mock
    private ProfileConfigurationService profileConfigurationService;

    @Mock
    private RetrieveRestClientStrategy retrieveRestClientStrategy;

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);

        trackingEventService = new DefaultProfileTrackingEventService();
        trackingEventService.setRetrieveRestClientStrategy(retrieveRestClientStrategy);
        trackingEventService.setProfileConfigurationService(profileConfigurationService);

        when(retrieveRestClientStrategy.getProfileRestClient()).thenReturn(client);
        when(profileConfigurationService.getYaaSTenant(SITE_ID)).thenReturn(TENANT_ID);

        when(trackingEvent.getUserAgent()).thenReturn(USER_AGENT);
        when(trackingEvent.getAcceptLanguage()).thenReturn(ACCEPT_LANGUAGE);
        when(trackingEvent.getAccept()).thenReturn(ACCEPT);
        when(trackingEvent.getReferer()).thenReturn(REFERER);
        when(trackingEvent.getIdsite()).thenReturn(SITE_ID);
    }

    @Test
    public void verifySendTrackingToYaasWithValidConsentReference() {

        when(client.sendEvent("piwik", CONSENT_REFERENCE, USER_AGENT, ACCEPT, ACCEPT_LANGUAGE, REFERER, trackingEvent)).thenReturn(Observable.just(response));
        when(profileConfigurationService.isYaaSConfigurationPresentForBaseSiteId(SITE_ID)).thenReturn(true);

        trackingEventService.sendTrackingEvent(CONSENT_REFERENCE, trackingEvent);
        verify(client, times(1)).sendEvent(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyObject());

        TestSubscriber<ProfileResponse> testSubscriber = new TestSubscriber<>();
        client.sendEvent("piwik", CONSENT_REFERENCE, USER_AGENT, ACCEPT, ACCEPT_LANGUAGE, REFERER, trackingEvent).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(response);

    }

    @Test
    public void verifyDoNotSendTrackingToYaasWithInvalidConsentReference() {

        when(client.sendEvent("piwik", CONSENT_REFERENCE, USER_AGENT, ACCEPT, ACCEPT_LANGUAGE, REFERER, trackingEvent)).thenReturn(Observable.just(response));
        when(profileConfigurationService.isYaaSConfigurationPresentForBaseSiteId(SITE_ID)).thenReturn(true);

        trackingEventService.sendTrackingEvent(null, trackingEvent);
        verify(client, times(0)).sendEvent(anyString(),anyString(), anyString(), anyString(), anyString(), anyString(), anyObject());

        trackingEventService.sendTrackingEvent("", trackingEvent);
        verify(client, times(0)).sendEvent(anyString(),anyString(), anyString(), anyString(), anyString(), anyString(), anyObject());

    }

    @Test
    public void verifyDoNotSendTrackingToYaasWithInvalidYaaSConfiguration() {

        when(client.sendEvent("piwik", CONSENT_REFERENCE, USER_AGENT, ACCEPT, ACCEPT_LANGUAGE, REFERER, trackingEvent)).thenReturn(Observable.just(response));
        when(profileConfigurationService.isYaaSConfigurationPresentForBaseSiteId(SITE_ID)).thenReturn(false);

        trackingEventService.sendTrackingEvent(CONSENT_REFERENCE, trackingEvent);
        verify(client, times(0)).sendEvent(anyString(),anyString(), anyString(), anyString(), anyString(), anyString(), anyObject());
    }

}