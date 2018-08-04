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


import com.hybris.yprofile.consent.services.ConsentService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.user.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

@UnitTest
public class DefaultRawEventEnricherTest {

    private DefaultRawEventEnricher defaultRawEventEnricher;

    private static final String SITE_ID = "test";
    @Mock
    private ConsentService consentService;

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);

        defaultRawEventEnricher = new DefaultRawEventEnricher();
        defaultRawEventEnricher.setConsentService(consentService);
        defaultRawEventEnricher.setObjectMapper(new ObjectMapper());

    }

    @Test
    public void verifyRawEventEnriched()
    {

        HttpServletRequest request = setupHttpRequest();

        when(consentService.getConsentReferenceFromCookie(anyString(), anyObject())).thenReturn("consent-reference-id");

        String result = defaultRawEventEnricher.enrich(getJson(), request);

        assertTrue(result.contains("\"consent_reference\":\"consent-reference-id\""));
        assertTrue(result.contains("\"user_agent\":\"User-Agent\""));


        assertTrue(result.contains("\"timestamp\""));
        assertTrue(result.contains("\"idsite\":\"electronics\""));
        assertTrue(result.contains("\"accept\""));
        assertTrue(result.contains("\"accept_language\""));
        assertTrue(result.contains("\"referer\""));

    }



    private HttpServletRequest setupHttpRequest(){

        HttpServletRequest request = mock(HttpServletRequest.class);

        Cookie[] cookies = new Cookie[]{new Cookie("some-cookie", "some value"),
                new Cookie("baseSiteUid-consentReference", "consent-reference-id")};

        when(request.getCookies()).thenReturn(cookies);
        when(request.getHeader("User-Agent")).thenReturn("User-Agent");

        return request;
    }

    private String getJson(){
        return "{\"_viewts\":\"1461149581\",\"idsite\":\"electronics\",\"_refts\":\"0\",\"wma\":\"0\"," +
                "\"cvar\":\"{\\\"1\\\":[\\\"ec_id\\\",\\\"00001000\\\"],\\\"2\\\":[\\\"_pkp\\\",\\\"1\\\"],\\\"3\\\":[\\\"_pks\\\",\\\"1382080\\\"],\\\"4\\\":[\\\"_pkn\\\",\\\"EOS450'D + 18-55 IS Kit\\\"],\\\"5\\\":[\\\"_pkc\\\",\\\"\\\"]}\"," +
                "\"_idvc\":\"2\",\"dir\":\"0\",\"rec\":\"1\",\"revenue\":\"0\"," +
                "\"_idts\":\"1461149460\",\"java\":\"0\",\"_ects\":\"1461149557\"," +
                "\"_idn\":\"0\",\"gt_ms\":\"7130\",\"fla\":\"1\",\"gears\":\"0\"," +
                "\"res\":\"1920x1200\",\"qt\":\"0\"," +
                "\"urlref\":\"https:\\/\\/electronics.local:9002\\/yacceleratorstorefront\\/\"," +
                "\"cookie\":\"1\"," +
                "\"ec_items\":\"[[\\\"1382080\\\",\\\"EOS450D + 18-55 IS Kit\\\",[],\\\"574.88\\\",\\\"1\\\"]]\",\"ag\":\"0\"," +
                "\"realp\":\"0\",\"h\":\"14\",\"m\":\"8\"," +
                "\"url\":\"https:\\/\\/electronics.local:9002\\/yacceleratorstorefront\\/electronics\\/en\\/Open-Catalogue\\/Cameras\\/Digital-Cameras\\/Digital-SLR\\/EOS450D-%2B-18-55-IS-Kit\\/p\\/1382080\"," +
                "\"idgoal\":\"0\",\"r\":\"306416\",\"s\":\"51\",\"pdf\":\"1\"," +
                "\"eventtype\":\"ecommerce\",\"_id\":\"c35e7323191132e6\"}";
    }
}