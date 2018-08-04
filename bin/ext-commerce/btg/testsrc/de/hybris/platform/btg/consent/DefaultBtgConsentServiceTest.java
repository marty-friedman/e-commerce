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
package de.hybris.platform.btg.consent;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.btg.consent.impl.DefaultBtgConsentService;
import de.hybris.platform.btg.constants.BtgConstants;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.session.SessionService;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;


/**
 *
 *
 */
@UnitTest
public class DefaultBtgConsentServiceTest {
    protected static final String IGNORE_CONSENTS_PARAMETER = BtgConstants.IGNORE_CONSENT_CHECK_WHEN_NO_REQUIRED_CONSENT_TEMPLATE;

    private static final String FALSE_IGNORE_CONSENT_IGNORED = "Parameter " + IGNORE_CONSENTS_PARAMETER + " set to FALSE  was ignored";
    private static final String TRUE_IGNORE_CONSENT_IGNORED = "Parameter " + IGNORE_CONSENTS_PARAMETER + " set to TRUE  was ignored";

    @InjectMocks
    private BtgConsentService btgConsentService;

    @Mock
    private ConfigurationService configurationService;
    @Mock
    private Configuration configuration;
    @Mock
    private SessionService sessionService;


    private UserModel user1;

    @Before
    public void setUp() {
        btgConsentService = new DefaultBtgConsentService();
        MockitoAnnotations.initMocks(this);

        user1 = new CustomerModel();
        user1.setUid("1");

        when(configurationService.getConfiguration()).thenReturn(configuration);
    }

    @Test
    public void testUserHasActiveConsentNullSessionConsentsNullRequiredConsentsFalse() {
        //given
        setIgnoreConsentsCheckWhenRequiredConsentsNotSet(Boolean.FALSE);
        setRequiredConsentTemplatesParameter(null);
        setSessionUserConsents(null);

        //when
        final boolean result = btgConsentService.userHasActiveConsent(user1);

        //then
        assertFalse(FALSE_IGNORE_CONSENT_IGNORED, result);
    }

    @Test
    public void testUserHasActiveConsentNullSessionConsentsNullRequiredConsentsTrue() {
        //given
        setIgnoreConsentsCheckWhenRequiredConsentsNotSet(Boolean.TRUE);
        setRequiredConsentTemplatesParameter(null);
        setSessionUserConsents(null);

        //when
        final boolean result = btgConsentService.userHasActiveConsent(user1);

        //then
        assertTrue(TRUE_IGNORE_CONSENT_IGNORED, result);
    }

    @Test
    public void testUserHasActiveConsentEmptySessionConsentsNotEmptyRequired() {
        //given
        setIgnoreConsentsCheckWhenRequiredConsentsNotSet(Boolean.FALSE);
        setRequiredConsentTemplatesParameter("consent1");
        setSessionUserConsents(new HashMap<String, String>());

        //when
        final boolean result = btgConsentService.userHasActiveConsent(user1);

        //then
        assertFalse("When empty session consents should return False", result);
    }

    @Test
    public void testUserHasActiveConsentNullSessionConsentsNotEmptyRequired() {
        //given
        setIgnoreConsentsCheckWhenRequiredConsentsNotSet(Boolean.FALSE);
        setRequiredConsentTemplatesParameter("consent1");
        setSessionUserConsents(null);

        //when
        final boolean result = btgConsentService.userHasActiveConsent(user1);

        //then
        assertFalse("When null session consents should return False", result);
    }

    @Test
    public void testUserHasActiveConsentNullRequiredConsents() {
        //given
        setIgnoreConsentsCheckWhenRequiredConsentsNotSet(Boolean.FALSE);
        setRequiredConsentTemplatesParameter(null);

        //when
        final boolean result = btgConsentService.userHasActiveConsent(user1);

        //then
        assertFalse(FALSE_IGNORE_CONSENT_IGNORED, result);
    }

    @Test
    public void testUserHasActiveConsentEmptyRequiredConsents() {
        //given
        setIgnoreConsentsCheckWhenRequiredConsentsNotSet(Boolean.FALSE);
        setRequiredConsentTemplatesParameter(StringUtils.SPACE);

        //when
        final boolean result = btgConsentService.userHasActiveConsent(user1);

        //then
        assertFalse(FALSE_IGNORE_CONSENT_IGNORED, result);
    }

    @Test
    public void testUserHasActiveConsentEmptyRequiredConsentsEmptySessionConsents() {
        //given
        setIgnoreConsentsCheckWhenRequiredConsentsNotSet(Boolean.FALSE);
        setRequiredConsentTemplatesParameter(StringUtils.SPACE);
        setSessionUserConsents(new HashMap<>());

        //when
        final boolean result = btgConsentService.userHasActiveConsent(user1);

        //then
        assertFalse(FALSE_IGNORE_CONSENT_IGNORED, result);
    }

    @Test
    public void testUserHasActiveConsentIgnoreConsentParameterSetToFalse() {
        //given
        setIgnoreConsentsCheckWhenRequiredConsentsNotSet(Boolean.FALSE);

        //when
        final boolean result = btgConsentService.userHasActiveConsent(user1);

        //then
        assertFalse(FALSE_IGNORE_CONSENT_IGNORED, result);
    }

    @Test
    public void testUserHasActiveConsentIgnoreConsentParameterSetToTrue() {
        //given
        setIgnoreConsentsCheckWhenRequiredConsentsNotSet(Boolean.TRUE);

        //when
        final boolean result = btgConsentService.userHasActiveConsent(user1);

        //then
        assertTrue(TRUE_IGNORE_CONSENT_IGNORED, result);
    }

    @Test
    public void testUserHasActiveConsentOnlyOneConsentsInParameterIsInSessionGIVEN() {
        //given
        Map<String, String> userConsents = new HashMap<>();
        userConsents.put("consent1", BtgConstants.CONSENT_GIVEN);
        setSessionUserConsents(userConsents);
        setRequiredConsentTemplatesParameter("consent1");

        final boolean result = btgConsentService.userHasActiveConsent(user1);

        //then
        assertTrue("User should have Active Consent", result);
    }

    @Test
    public void testUserHasActiveConsentOnlyOneConsentsInParameterIsInSessionWITHDRAWN() {
        //given
        Map<String, String> userConsents = new HashMap<>();
        userConsents.put("consent1", BtgConstants.CONSENT_WITHDRAWN);
        setSessionUserConsents(userConsents);
        setRequiredConsentTemplatesParameter("consent1");

        final boolean result = btgConsentService.userHasActiveConsent(user1);

        //then
        assertFalse("User should have Active Consent", result);
    }

    @Test
    public void testUserHasActiveConsentAllConsentsFromParametersNotExistsInSession() {
        //given
        Map<String, String> userConsents = new HashMap<>();
        userConsents.put("consent1", BtgConstants.CONSENT_GIVEN);
        setSessionUserConsents(userConsents);
        setRequiredConsentTemplatesParameter("consent2,consent3,consent4");
        setIgnoreConsentsCheckWhenRequiredConsentsNotSet(Boolean.TRUE);

        //when
        final boolean result = btgConsentService.userHasActiveConsent(user1);

        //then
        assertTrue(TRUE_IGNORE_CONSENT_IGNORED, result);
    }

    @Test
    public void testUserHasActiveConsentInSessionOnlyNullValues() {
        //given
        Map<String, String> userConsents = new HashMap<>();
        userConsents.put("consent1", null);
        setSessionUserConsents(userConsents);
        setRequiredConsentTemplatesParameter("consent1");
        setIgnoreConsentsCheckWhenRequiredConsentsNotSet(Boolean.TRUE);

        //when
        final boolean result = btgConsentService.userHasActiveConsent(user1);

        //then
        assertFalse(TRUE_IGNORE_CONSENT_IGNORED, result);
    }

    @Test
    public void testUserHasActiveConsentNotAllRequiredConsentsHasGiven() {
        //given
        Map<String, String> userConsents = new HashMap<>();
        userConsents.put("consent1", BtgConstants.CONSENT_GIVEN);
        userConsents.put("consent2", BtgConstants.CONSENT_WITHDRAWN);
        setSessionUserConsents(userConsents);
        setRequiredConsentTemplatesParameter("consent1,consent2");

        //when
        final boolean result = btgConsentService.userHasActiveConsent(user1);

        //then
        assertFalse("User should not have Active Consent when only one of required consents is not GIVEN", result);
    }

    @Test
    public void testUserHasActiveConsentAllRequiredConsentsHasGiven() {
        //given
        Map<String, String> userConsents = new HashMap<>();
        userConsents.put("consent1", BtgConstants.CONSENT_GIVEN);
        userConsents.put("consent2", BtgConstants.CONSENT_GIVEN);
        userConsents.put("consent3", BtgConstants.CONSENT_GIVEN);
        setSessionUserConsents(userConsents);
        setRequiredConsentTemplatesParameter("consent1,consent2,consent3");

        //when
        final boolean result = btgConsentService.userHasActiveConsent(user1);

        //then
        assertTrue("User should have Active Consent because all required consents are GIVEN", result);
    }

    @Test
    public void testUserHasActiveConsentNoRequiredConsentsHasGiven() {
        //given
        Map<String, String> userConsents = new HashMap<>();
        userConsents.put("consent1", BtgConstants.CONSENT_WITHDRAWN);
        userConsents.put("consent2", BtgConstants.CONSENT_WITHDRAWN);
        userConsents.put("consent3", StringUtils.SPACE);
        setSessionUserConsents(userConsents);
        setRequiredConsentTemplatesParameter("consent1,consent2,consent3");

        //when
        final boolean result = btgConsentService.userHasActiveConsent(user1);

        //then
        assertFalse("User should not have Active Consent when none of required consents are not GIVEN", result);
    }

    private void setIgnoreConsentsCheckWhenRequiredConsentsNotSet(Boolean param) {
        when(configuration.getBoolean(IGNORE_CONSENTS_PARAMETER, Boolean.TRUE)).thenReturn(param);
    }

    private void setSessionUserConsents(Map<String, String> userConsents) {
        when(sessionService.getAttribute(BtgConstants.SESSION_USER_CONSENTS)).thenReturn(userConsents);
    }

    private void setRequiredConsentTemplatesParameter(String param) {
        when(configuration.getString(BtgConstants.REQUIRED_CONSENT_TEMPLATES)).thenReturn(param);
    }


}
