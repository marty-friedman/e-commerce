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
package com.hybris.yprofile.eventtracking.populators;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hybris.eventtracking.model.events.AbstractTrackingEvent;
import de.hybris.eventtracking.services.constants.TrackingEventJsonFields;
import de.hybris.eventtracking.services.populators.AbstractTrackingEventGenericPopulator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Map;

public class AbstractTrackingEventPopulator extends AbstractTrackingEventGenericPopulator {


    public AbstractTrackingEventPopulator(ObjectMapper mapper) {
        super(mapper);
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return AbstractTrackingEvent.class.isAssignableFrom(clazz);
    }

    @Override
    public void populate(Map<String, Object> trackingEventData, AbstractTrackingEvent trackingEvent) throws ConversionException {

        trackingEvent.setConsentReference((String) trackingEventData.get(TrackingEventJsonFields.CONSENT_REFERENCE.getKey()));
        trackingEvent.setUserAgent((String)trackingEventData.get(TrackingEventJsonFields.USER_AGENT.getKey()));
        trackingEvent.setAccept((String)trackingEventData.get(TrackingEventJsonFields.ACCEPT.getKey()));
        trackingEvent.setAcceptLanguage((String) trackingEventData.get(TrackingEventJsonFields.ACCEPT_LANGUAGE.getKey()));
        trackingEvent.setReferer((String)trackingEventData.get(TrackingEventJsonFields.REFERER.getKey()));
    }
}
