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
package de.hybris.platform.sap.productconfig.testutil;

import de.hybris.platform.sap.productconfig.facades.impl.SessionAccessFacadeImpl;
import de.hybris.platform.sap.productconfig.service.testutil.DummySessionAccessService;



public class DummySessionAccessFacade extends SessionAccessFacadeImpl
{

	public DummySessionAccessFacade()
	{
		super.setSessionAccessService(new DummySessionAccessService());
	}

}
