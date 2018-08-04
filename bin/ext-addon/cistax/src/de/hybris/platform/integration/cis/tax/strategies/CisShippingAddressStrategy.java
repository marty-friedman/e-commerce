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
package de.hybris.platform.integration.cis.tax.strategies;

import java.util.List;
import com.hybris.cis.client.shared.models.CisAddress;
import de.hybris.platform.core.model.order.AbstractOrderModel;


/**
 * Interface to determine the correct ship to and ship from addresses for taxation.
 */
public interface CisShippingAddressStrategy
{
	List<CisAddress> getAddresses(AbstractOrderModel abstractOrder);
}
