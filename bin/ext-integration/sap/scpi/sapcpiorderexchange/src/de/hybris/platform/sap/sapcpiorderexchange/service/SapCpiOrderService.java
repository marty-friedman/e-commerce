/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.sapcpiorderexchange.service;

import de.hybris.platform.sap.sapcpiadapter.data.SapCpiOrder;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiOrderCancellation;
import de.hybris.platform.sap.sapcpiorderexchange.data.SapSendToSapCpiResult;
import rx.Single;


public interface SapCpiOrderService {

    Single<SapSendToSapCpiResult> sendOrder(SapCpiOrder sapCpiOrder);

    Single<SapSendToSapCpiResult> sendOrderCancellation(SapCpiOrderCancellation sapCpiOrderCancellation);

}
