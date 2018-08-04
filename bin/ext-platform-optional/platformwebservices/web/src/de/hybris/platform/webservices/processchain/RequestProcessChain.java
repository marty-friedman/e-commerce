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
package de.hybris.platform.webservices.processchain;

import de.hybris.platform.webservices.processchain.impl.DefaultRequestProcessChain;
import de.hybris.platform.webservices.processor.RequestProcessor;


/**
 * Chain abstract used at {@link RequestProcessor} side.
 */
public interface RequestProcessChain
{
	/**
	 * Method to perform the next element in the chain implementation {@link DefaultRequestProcessChain} from inside of
	 * the
	 * {@link RequestProcessor#doProcess(de.hybris.platform.webservices.processor.RequestProcessor.RequestType, Object, de.hybris.platform.webservices.AbstractResponseBuilder, RequestProcessChain)}
	 * .
	 */
	void doProcess();
}
