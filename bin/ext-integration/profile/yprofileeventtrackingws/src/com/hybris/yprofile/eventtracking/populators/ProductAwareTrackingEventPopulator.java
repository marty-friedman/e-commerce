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
import de.hybris.eventtracking.model.events.AbstractProductAwareTrackingEvent;
import de.hybris.eventtracking.model.events.AbstractTrackingEvent;
import de.hybris.eventtracking.services.populators.AbstractProductAwareTrackingEventPopulator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

public class ProductAwareTrackingEventPopulator extends AbstractProductAwareTrackingEventPopulator {

    private static final Logger LOG = Logger.getLogger(ProductAwareTrackingEventPopulator.class);

    public ProductAwareTrackingEventPopulator(ObjectMapper mapper) {
        super(mapper);
    }

    @Override
    public void populate(final Map<String, Object> trackingEventData, final AbstractTrackingEvent trackingEvent)
            throws ConversionException
    {
        final Map<String, Object> customVariablesPageScoped = getPageScopedCvar(trackingEventData);
        String categoryIds = null;
        String productPrice = null;
        if (customVariablesPageScoped != null)
        {
            final List<String> pkpData = (List) customVariablesPageScoped.get("2");
            productPrice = this.getCustomVariableValue(pkpData);

            final List<String> pkcData = (List) customVariablesPageScoped.get("5");
            categoryIds =  this.getCustomVariableValue(pkcData);

        }

        ((AbstractProductAwareTrackingEvent) trackingEvent).setCategoryId(categoryIds);
        ((AbstractProductAwareTrackingEvent) trackingEvent).setProductPrice(productPrice);
    }

    private static String getCustomVariableValue(final List<String> data) {
        if (data != null && !data.isEmpty())
        {
            try {
                return data.get(1);
            } catch (IndexOutOfBoundsException e){
                LOG.error("Invalid Custom Variable (cvar)", e);
            }

        }
        return null;
    }
}
