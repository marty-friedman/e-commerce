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
package com.hybris.yprofile.populators;

import com.hybris.yprofile.common.Utils;
import com.hybris.yprofile.dto.OrderLineItem;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.util.TaxValue;

import java.util.Collection;

public class OrderLineItemPopulator implements Populator<AbstractOrderEntryModel, OrderLineItem> {

    @Override
    public void populate(AbstractOrderEntryModel abstractOrderEntryModel, OrderLineItem orderLineItem) throws ConversionException {

        orderLineItem.setPos(abstractOrderEntryModel.getEntryNumber());
        orderLineItem.setRef(abstractOrderEntryModel.getProduct().getCode());
        orderLineItem.setType(abstractOrderEntryModel.getProduct().getItemtype());
        orderLineItem.setUnit(abstractOrderEntryModel.getUnit() != null ? abstractOrderEntryModel.getUnit().getCode() : "");
        orderLineItem.setPrice_list(Utils.formatDouble(abstractOrderEntryModel.getBasePrice()));
        orderLineItem.setPrice_effective(Utils.formatDouble(abstractOrderEntryModel.getTotalPrice()));
        orderLineItem.setCurrency(abstractOrderEntryModel.getOrder() != null ? (abstractOrderEntryModel.getOrder().getCurrency() != null ? abstractOrderEntryModel.getOrder().getCurrency().getIsocode() : "") : "");
        orderLineItem.setQuantity(abstractOrderEntryModel.getQuantity());

        final Collection<TaxValue> taxValues = abstractOrderEntryModel.getTaxValues();
        if (taxValues != null) {
            for (TaxValue tv : taxValues) {
                orderLineItem.setTaxAmount(Utils.formatDouble(tv.getValue()));
                break;
            }
        }
    }
}
