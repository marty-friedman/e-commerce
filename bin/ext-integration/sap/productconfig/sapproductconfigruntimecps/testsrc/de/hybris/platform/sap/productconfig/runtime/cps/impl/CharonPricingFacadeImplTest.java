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
package de.hybris.platform.sap.productconfig.runtime.cps.impl;

import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.RequestErrorHandler;
import de.hybris.platform.sap.productconfig.runtime.cps.client.PricingClient;
import de.hybris.platform.sap.productconfig.runtime.cps.client.PricingClientBase;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentInput;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingEngineException;
import de.hybris.platform.yaasconfiguration.service.YaasServiceFactory;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.hybris.charon.exp.HttpException;


@SuppressWarnings("javadoc")
@UnitTest
public class CharonPricingFacadeImplTest
{
	private CharonPricingFacadeImpl classUnderTest;
	@Mock
	private RequestErrorHandler errorHandler;

	@Mock
	private PricingClient client;

	@Mock
	private YaasServiceFactory yaasServiceFactory;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new CharonPricingFacadeImpl();
		classUnderTest.setRequestErrorHandler(errorHandler);
		classUnderTest.setYaasServiceFactory(yaasServiceFactory);
		Mockito.when(yaasServiceFactory.lookupService(PricingClient.class)).thenReturn(client);
	}

	@Test
	public void testGetClient()
	{
		classUnderTest.setClient(null);
		final PricingClientBase result = classUnderTest.getClient();
		assertNotNull(result);
	}

	@Test
	public void testCreatePricingDocumentErrorHandlerCalled() throws PricingEngineException
	{
		classUnderTest.setClient(client);
		final HttpException ex = new HttpException(Integer.valueOf(666), "something went horribly wrong");
		Mockito.doThrow(ex).when(client).createPricingDocument(Mockito.any());
		classUnderTest.createPricingDocument(new PricingDocumentInput());
		Mockito.verify(errorHandler).processCreatePricingDocumentError(ex);
	}
}
