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

import de.hybris.eventtracking.model.events.AbstractTrackingEvent;
import de.hybris.eventtracking.publisher.csv.model.TrackingEventCsvData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class AbstractTrackingEventToCsvDataPopulator implements Populator<AbstractTrackingEvent, TrackingEventCsvData> {

    @Override
    public void populate(final AbstractTrackingEvent source, final TrackingEventCsvData target) throws ConversionException {

        target.setConsentReference(source.getConsentReference());
        target.setUserAgent(source.getUserAgent());
        target.setAccept(source.getAccept());
        target.setAcceptLanguage(source.getAcceptLanguage());
        target.setReferer(source.getReferer());
    }
}
