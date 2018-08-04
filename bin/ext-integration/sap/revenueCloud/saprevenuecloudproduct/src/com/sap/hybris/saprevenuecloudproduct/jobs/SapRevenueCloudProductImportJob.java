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

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hybris.saprevenuecloudproduct.constants.SaprevenuecloudproductConstants;
import com.sap.hybris.scpiconnector.data.ResponseData;
import com.sap.hybris.scpiconnector.exception.ScpiGenericException;
import com.sap.hybris.scpiconnector.httpconnection.CloudPlatformIntegrationConnection;


/**
 *
 */
public class SapRevenueCloudProductImportJob extends AbstractJobPerformable<CronJobModel>
{

	private final Logger LOG = LoggerFactory.getLogger(SapRevenueCloudProductImportJob.class);

	private CloudPlatformIntegrationConnection cloudPlatformIntegrationConnection;
	private ConfigurationService configurationService;

	@Override
	public PerformResult perform(final CronJobModel job)
	{
		try
		{


			final ResponseData reponse = getCloudPlatformIntegrationConnection()
					.sendPost(
							getConfigurationService().getConfiguration()
									.getString(SaprevenuecloudproductConstants.SAP_REVENUE_CLOUD_PRODUCT_INBOUND_SCPI_IFLOW_URL_KEY),
							StringUtils.EMPTY);
			//Check the response from scpi
			if (!String.valueOf(HttpStatus.SC_OK).equals(reponse.getStatus()))
			{
				throw new ScpiGenericException(reponse.getStatus(), reponse.getReason());
			}
			LOG.info("Successfully repicated the products from SAP Revenue Cloud");
		}
		catch (final ScpiGenericException e)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug(String.format(
						"Exception occured while making a product import call with scpi. SCPI responded with code [%s] and reason [%s]. Detaied error message is %s",
						e.getCode(), e.getReason(), e.getMessage()));
			}
			LOG.error(String.format(
					"Exception occured while making a product import call with scpi. SCPI responded with code [%s] and reason [%s].",
					e.getCode(), e.getReason()));
			return new PerformResult(CronJobResult.FAILURE, CronJobStatus.FINISHED);

		}
		catch (final Exception e)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.error("Error while importing the products from SAP Revenue Cloud" + e);
			}
			LOG.error("Error while importing the products from SAP Revenue Cloud" + e.getMessage());
			return new PerformResult(CronJobResult.ERROR, CronJobStatus.FINISHED);
		}
		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);

	}

	/**
	 * @return the cloudPlatformIntegrationConnection
	 */
	public CloudPlatformIntegrationConnection getCloudPlatformIntegrationConnection()
	{
		return cloudPlatformIntegrationConnection;
	}

	/**
	 * @param cloudPlatformIntegrationConnection
	 *           the cloudPlatformIntegrationConnection to set
	 */
	public void setCloudPlatformIntegrationConnection(final CloudPlatformIntegrationConnection cloudPlatformIntegrationConnection)
	{
		this.cloudPlatformIntegrationConnection = cloudPlatformIntegrationConnection;
	}

	/**
	 * @return the configurationService
	 */
	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * @param configurationService
	 *           the configurationService to set
	 */
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}



}
