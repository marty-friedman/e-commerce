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

/**
 * Focuses on methods to retrieve yaas and profile configuration items
 */
public interface ProfileConfigurationService {

    /**
     * Cookie and Session attribute key
     */
    String PROFILE_TRACKING_PAUSE = "profile.tracking.pause";

    /**
     * Profile tag url identifier
     */
    String PROFILE_TAG_URL = "ProfileTagUrl";

    /**
     * Profile tag configuration url identifier
     */
    String PROFILE_TAG_CONFIG_URL = "ProfileTagConfigUrl";


    /**
     * checks whether the profile tracking is enabled
     *
     * @return
     */
    boolean isProfileTrackingPaused();


    /**
     * Stores in session if profile tracking is paused
     *
     * @param isProfileTrackingPause
     */
    void setProfileTrackingPauseValue(final boolean isProfileTrackingPause);

    /**
     * Checks if the Yaas configuration is present
     *
     * @param siteId base site identifier
     * @return true or false
     */
    boolean isYaaSConfigurationPresentForBaseSiteId(final String siteId);

    /**
     * Returns the project identifier in Yaas, alias "tenant"
     * @param siteId base site identifier
     * @return tenant identifier
     */
    String getYaaSTenant(final String siteId);

    /**
     * Returns the clientId from the client credential in Yaas
     * @param siteId base site identifier
     * @return clientId
     */
    String getYaaSClientId(final String siteId);


    /**
     * Returns the ProfileTag url in Yaas
     * @return ProfileTag url
     */
    String getYaaSProfileTagUrl();


    /**
     * Returns the ProfileTag configuration url in Yaas
     * @return ProfileTag configuration url
     */
    String getYaaSProfileTagConfigUrl();

}
