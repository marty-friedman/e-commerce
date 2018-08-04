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

/**
 * Communication service to send tracking events to Profile
 */
public interface ProfileTrackingEventService {

    /**
     * Sends the tracking event to Profile
     * @param consentReference the user's consent reference
     * @param trackingEventDTO tracking event dto
     */
    void sendTrackingEvent(final String consentReference, final TrackingEvent trackingEventDTO);
}
