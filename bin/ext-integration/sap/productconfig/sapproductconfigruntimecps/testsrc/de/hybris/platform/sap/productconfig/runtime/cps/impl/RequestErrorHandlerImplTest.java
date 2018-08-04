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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingEngineException;

import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.hybris.charon.exp.ForbiddenException;
import com.hybris.charon.exp.HttpException;
import com.hybris.charon.exp.NotFoundException;
import com.hybris.charon.exp.ServiceUnavailableException;

import rx.Observable;


@SuppressWarnings("javadoc")
@UnitTest
public class RequestErrorHandlerImplTest
{
	private static final String MESSAGE_TEXT = "message";
	private RequestErrorHandlerImpl classUnderTest;

	@Mock
	HttpException ex;

	@Mock
	ForbiddenException forbiddenEx;

	@Mock
	HttpException exWithoutServerMessage;

	@Mock
	NotFoundException notFoundEx;

	@Mock
	ServiceUnavailableException unAvEx;

	@Mock
	private RuntimeException runtimeEx;

	@Mock
	private TimeoutException timeOutException;

	@Mock
	private NullPointerException nullPointerEx;


	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		final Observable<String> justMessage = Observable.just(MESSAGE_TEXT);
		Mockito.when(ex.getServerMessage()).thenReturn(justMessage);
		Mockito.when(forbiddenEx.getServerMessage()).thenReturn(justMessage);
		Mockito.when(notFoundEx.getServerMessage()).thenReturn(justMessage);
		Mockito.when(unAvEx.getServerMessage()).thenReturn(justMessage);
		Mockito.when(exWithoutServerMessage.getServerMessage()).thenReturn(null);
		Mockito.when(runtimeEx.getCause()).thenReturn(timeOutException);
		classUnderTest = new RequestErrorHandlerImpl();
	}

	@Test(expected = ConfigurationEngineException.class)
	public void testProcessUpdateConfigurationError() throws ConfigurationEngineException
	{
		classUnderTest.processGetConfigurationError(forbiddenEx);
	}

	@Test(expected = IllegalStateException.class)
	public void testProcessCreateDefaultConfigurationError()
	{
		classUnderTest.processCreateDefaultConfigurationError(unAvEx);
	}

	@Test(expected = IllegalStateException.class)
	public void testProcessGetConfigurationError()
	{
		classUnderTest.processCreateDefaultConfigurationError(notFoundEx);
	}

	@Test(expected = IllegalStateException.class)
	public void testProcessDeleteConfigurationError()
	{
		classUnderTest.processDeleteConfigurationError(notFoundEx);
	}

	@Test(expected = ConfigurationEngineException.class)
	public void testProcessGetExternalConfigurationError() throws ConfigurationEngineException
	{
		classUnderTest.processGetExternalConfigurationError(notFoundEx);
	}

	@Test(expected = IllegalStateException.class)
	public void testProcessCreateRuntimeConfigurationFromExternalError()
	{
		classUnderTest.processCreateRuntimeConfigurationFromExternalError(notFoundEx);
	}

	@Test(expected = PricingEngineException.class)
	public void testProcessCreatePricingDocumentError() throws PricingEngineException
	{
		classUnderTest.processCreatePricingDocumentError(notFoundEx);
	}

	@Test(expected = IllegalStateException.class)
	public void testProcessHasKbError()
	{
		classUnderTest.processHasKbError(notFoundEx);
	}

	@Test(expected = IllegalStateException.class)
	public void testIfNotFound()
	{
		classUnderTest.ifNotFoundThrowIllegalState(notFoundEx);
	}

	@Test
	public void testIfNotFoundGenericException()
	{
		final HttpException ex = new HttpException(Integer.valueOf(404), MESSAGE_TEXT);
		//In this case we don't expect a RT exception since the http exception doesn't indicate 'not found'
		classUnderTest.ifNotFoundThrowIllegalState(ex);
	}

	@Test(expected = ConfigurationEngineException.class)
	public void testCheckNotFound() throws ConfigurationEngineException
	{
		final HttpException ex = new NotFoundException(Integer.valueOf(404), MESSAGE_TEXT);
		classUnderTest.checkNotFound(ex);
	}

	@Test
	public void testCheckNotFoundGenericException() throws ConfigurationEngineException
	{

		//In this case we don't expect a RT exception since the http exception doesn't indicate 'not found'
		classUnderTest.checkNotFound(forbiddenEx);
	}

	@Test
	public void testTraceRequestError()
	{
		classUnderTest.logRequestError("pci", forbiddenEx);
		verify(forbiddenEx).getServerMessage();
	}

	@Test
	public void testTraceRequestErrorNoMessage()
	{
		assertEquals(RequestErrorHandlerImpl.NO_SERVER_MESSAGE, classUnderTest.getServerMessage(exWithoutServerMessage));
	}

	@Test(expected = PricingEngineException.class)
	public void testProcessCreatePricingDocumentRuntimeException() throws PricingEngineException
	{
		classUnderTest.processCreatePricingDocumentRuntimeException(runtimeEx);
	}

	@Test(expected = RuntimeException.class)
	public void testProcessCreatePricingDocumentRuntimeExceptionOtherCause() throws PricingEngineException
	{
		Mockito.when(runtimeEx.getCause()).thenReturn(nullPointerEx);
		classUnderTest.processCreatePricingDocumentRuntimeException(runtimeEx);
	}

	@Test(expected = RuntimeException.class)
	public void testProcessCreatePricingDocumentRuntimeExceptionNoCauseAtAll() throws PricingEngineException
	{
		Mockito.when(runtimeEx.getCause()).thenReturn(null);
		classUnderTest.processCreatePricingDocumentRuntimeException(runtimeEx);
	}

	@Test(expected = ConfigurationEngineException.class)
	public void testProcessConfigurationRuntimeException() throws ConfigurationEngineException
	{
		classUnderTest.processConfigurationRuntimeException(runtimeEx);
	}

	@Test(expected = RuntimeException.class)
	public void testProcessConfigurationRuntimeExceptionOtherCause() throws ConfigurationEngineException
	{
		Mockito.when(runtimeEx.getCause()).thenReturn(nullPointerEx);
		classUnderTest.processConfigurationRuntimeException(runtimeEx);
	}

	@Test(expected = RuntimeException.class)
	public void testProcessConfigurationRuntimeExceptionNoCauseAtAll() throws ConfigurationEngineException
	{
		Mockito.when(runtimeEx.getCause()).thenReturn(null);
		classUnderTest.processConfigurationRuntimeException(runtimeEx);
	}


}
