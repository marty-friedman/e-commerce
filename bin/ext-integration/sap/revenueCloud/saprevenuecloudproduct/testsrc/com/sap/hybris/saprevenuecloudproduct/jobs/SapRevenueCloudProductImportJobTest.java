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
package com.sap.hybris.saprevenuecloudproduct.jobs;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import java.io.IOException;

import org.apache.commons.configuration.Configuration;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.sap.hybris.scpiconnector.data.ResponseData;
import com.sap.hybris.scpiconnector.httpconnection.CloudPlatformIntegrationConnection;


/**
 * JUnit test suite for {@link SapRevenueCloudProductImportJobTest}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SapRevenueCloudProductImportJobTest
{
	@Mock
	private CloudPlatformIntegrationConnection cloudPlatformIntegrationConnection;

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration configuration;

	@InjectMocks
	private SapRevenueCloudProductImportJob sapRevenueCloudProductImportJob;

	@Before
	public void setUp()
	{
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getString(any(String.class))).thenReturn("dummyurl");
	}

	@Test
	public void checkForSuccessIfResponseStatusIsOk() throws IOException
	{
		final ResponseData respData = new ResponseData();
		respData.setStatus(String.valueOf(HttpStatus.SC_OK));
		when(cloudPlatformIntegrationConnection.sendPost(any(String.class), any(Object.class))).thenReturn(respData);
		final PerformResult result = sapRevenueCloudProductImportJob.perform(new CronJobModel());
		assertEquals(result.getResult(), CronJobResult.SUCCESS);
		assertEquals(result.getStatus(), CronJobStatus.FINISHED);
	}

	@Test
	public void checkForFailureStatusForGenericException() throws IOException
	{
		final ResponseData respData = new ResponseData();
		respData.setStatus(String.valueOf(HttpStatus.SC_FORBIDDEN));
		when(cloudPlatformIntegrationConnection.sendPost(any(String.class), any(Object.class))).thenReturn(respData);
		final PerformResult result = sapRevenueCloudProductImportJob.perform(new CronJobModel());

		assertEquals(result.getResult(), CronJobResult.FAILURE);
		assertEquals(result.getStatus(), CronJobStatus.FINISHED);

	}

	@Test
	public void checkForErrorStatusForAllOtherException() throws IOException
	{
		when(cloudPlatformIntegrationConnection.sendPost(any(String.class), any(Object.class))).thenReturn(null);
		final PerformResult result = sapRevenueCloudProductImportJob.perform(new CronJobModel());
		assertEquals(result.getResult(), CronJobResult.ERROR);
		assertEquals(result.getStatus(), CronJobStatus.FINISHED);

	}

}
