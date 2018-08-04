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

import com.hybris.yprofile.rest.clients.ProfileClient;
import com.hybris.yprofile.services.RetrieveRestClientStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.yaasconfiguration.service.YaasConfigurationService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
public class DefaultProfileConfigurationServiceTest {

    public static final String MY_SITE = "mySite";
    private DefaultProfileConfigurationService defaultProfileConfigurationService;

    @Mock
    private YaasConfigurationService yaasConfigurationService;
    @Mock
    private BaseSiteService baseSiteService;
    @Mock
    private RetrieveRestClientStrategy retrieveRestClientStrategy;


    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);

        defaultProfileConfigurationService = new DefaultProfileConfigurationService();
        defaultProfileConfigurationService.setBaseSiteService(baseSiteService);
        defaultProfileConfigurationService.setYaasConfigurationService(yaasConfigurationService);
        defaultProfileConfigurationService.setRetrieveRestClientStrategy(retrieveRestClientStrategy);
    }


    @Test
    public void assertYaasConfigurationIsPresentForCurrentSiteAndProfileService(){

        BaseSiteModel mySite = mock(BaseSiteModel.class);
        when(mySite.getUid()).thenReturn(MY_SITE);

        when(baseSiteService.getCurrentBaseSite()).thenReturn(mySite);

        ProfileClient profileClient = mock(ProfileClient.class);
        when(retrieveRestClientStrategy.getProfileRestClient()).thenReturn(profileClient);

        boolean result = defaultProfileConfigurationService.isYaaSConfigurationPresentForBaseSiteId(MY_SITE);

        assertTrue(result);
    }


    @Test
    public void assertYaasConfigurationIsPresentForGivenSiteAndProfileService(){

        BaseSiteModel mySite = mock(BaseSiteModel.class);
        when(mySite.getUid()).thenReturn(MY_SITE);

        when(baseSiteService.getCurrentBaseSite()).thenReturn(null);
        when(baseSiteService.getBaseSiteForUID(MY_SITE)).thenReturn(mySite);


        ProfileClient profileClient = mock(ProfileClient.class);
        when(retrieveRestClientStrategy.getProfileRestClient()).thenReturn(profileClient);

        boolean result = defaultProfileConfigurationService.isYaaSConfigurationPresentForBaseSiteId(MY_SITE);

        assertTrue(result);
    }


    @Test
    public void assertYaasConfigurationIsNotPresentForInvalidSite(){

        when(baseSiteService.getCurrentBaseSite()).thenReturn(null);
        when(baseSiteService.getBaseSiteForUID(MY_SITE)).thenReturn(null);


        ProfileClient profileClient = mock(ProfileClient.class);
        when(retrieveRestClientStrategy.getProfileRestClient()).thenReturn(profileClient);

        boolean result = defaultProfileConfigurationService.isYaaSConfigurationPresentForBaseSiteId(MY_SITE);

        assertFalse(result);
    }

    @Test
    public void assertYaasConfigurationIsNotPresentForMissingSiteAndServiceMappging(){

        BaseSiteModel mySite = mock(BaseSiteModel.class);
        when(mySite.getUid()).thenReturn(MY_SITE);

        when(baseSiteService.getCurrentBaseSite()).thenReturn(mySite);

        when(retrieveRestClientStrategy.getProfileRestClient()).thenThrow(new SystemException("Error"));

        boolean result = defaultProfileConfigurationService.isYaaSConfigurationPresentForBaseSiteId(MY_SITE);

        assertFalse(result);
    }
}