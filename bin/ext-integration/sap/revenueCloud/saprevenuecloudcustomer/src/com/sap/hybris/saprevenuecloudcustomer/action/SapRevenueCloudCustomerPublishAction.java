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
package com.sap.hybris.saprevenuecloudcustomer.action;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.task.RetryLaterException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sap.hybris.saprevenuecloudcustomer.constants.SaprevenuecloudcustomerConstants;
import com.sap.hybris.saprevenuecloudcustomer.dto.Address;
import com.sap.hybris.saprevenuecloudcustomer.dto.Customer;
import com.sap.hybris.saprevenuecloudcustomer.dto.Market;
import com.sap.hybris.saprevenuecloudcustomer.service.SapRevenueCloudCustomerPublicationService;
import com.sap.hybris.saprevenuecloudproduct.model.SAPMarketToCatalogMappingModel;
import com.sap.hybris.scpiconnector.data.ResponseData;


/**
 *
 */
public class SapRevenueCloudCustomerPublishAction extends AbstractSimpleDecisionAction<BusinessProcessModel>
{
	private SapRevenueCloudCustomerPublicationService sapRevenueCloudCustomerPublicationService;
	private Populator customerPopulator;
	private ConfigurationService configurationService;
	private GenericDao<SAPMarketToCatalogMappingModel> sapMarketToCatalogMappingModelGenericDao;
	private static final Logger LOGGER = LogManager.getLogger(SapRevenueCloudCustomerPublishAction.class);


	@Override
	public Transition executeAction(final BusinessProcessModel process) throws RetryLaterException
	{
		final StoreFrontCustomerProcessModel customerProcess = (StoreFrontCustomerProcessModel) process;
		final CustomerModel customerModel = customerProcess.getCustomer();
		final Customer customerJson = new Customer();
		getCustomerPopulator().populate(customerModel, customerJson);
		//Add a default address while registering because country and email are mandatory in revenue cloud
		final Address address = new Address();
		address.setEmail(customerModel.getUid());
		final String defaultCountryCode = getConfigurationService().getConfiguration().getString(
				SaprevenuecloudcustomerConstants.DEFAULT_COUNTRY);
		address.setCountry(defaultCountryCode);
		final List<Address> addresses = new ArrayList<Address>();
		addresses.add(address);
		customerJson.setAddresses(addresses);
		customerJson.setMarkets(getMarkets(customerProcess));
		ResponseData response;
		try
		{
			response = getSapRevenueCloudCustomerPublicationService().publishCustomerToCloudPlatformIntegration(customerJson);
			if ("200".equals(response.getStatus()) || "201".equals(response.getStatus()))
			{
				final String responseContent = response.getResponseContent();
				final ObjectNode customerResponse = new ObjectMapper().readValue(responseContent, ObjectNode.class);
				customerModel.setRevenueCloudCustomerId(customerResponse.get("id").asText());
				getModelService().save(customerModel);
				return Transition.OK;
			}
			else
			{
				LOGGER.error("Failed to replicate customer with ID " + customerModel.getCustomerID());
				return Transition.NOK;
			}
		}
		catch (final IOException e)
		{
			LOGGER.error("Failed to Replicate Customer with ID " + customerModel.getCustomerID());
			LOGGER.error(e);
			return Transition.NOK;
		}
	}

	protected List<Market> getMarkets(final StoreFrontCustomerProcessModel process)
	{
		final List<Market> markets = new ArrayList<Market>();
		final Market market = new Market();
		final BaseStoreModel store = process.getStore();
		final CatalogModel catalog = store.getCatalogs().get(0);
		if (catalog != null)
		{
			final Optional<String> marketIdOpt = sapMarketToCatalogMappingModelGenericDao.find().stream()
					.filter(e -> e.getCatalog().equals(catalog)).map(c -> c.getMarketId()).findFirst();
			marketIdOpt.ifPresent(e -> {
				market.setMarketId(e);
				market.setActive("true");
				markets.add(market);
			});
		}
		return markets;
	}

	/**
	 * @return the customerPopulator
	 */
	public Populator getCustomerPopulator()
	{
		return customerPopulator;
	}

	/**
	 * @param customerPopulator
	 *           the customerPopulator to set
	 */
	public void setCustomerPopulator(final Populator customerPopulator)
	{
		this.customerPopulator = customerPopulator;
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

	/**
	 * @return the sapRevenueCloudCustomerPublicationService
	 */
	public SapRevenueCloudCustomerPublicationService getSapRevenueCloudCustomerPublicationService()
	{
		return sapRevenueCloudCustomerPublicationService;
	}

	/**
	 * @param sapRevenueCloudCustomerPublicationService
	 *           the sapRevenueCloudCustomerPublicationService to set
	 */
	public void setSapRevenueCloudCustomerPublicationService(
			final SapRevenueCloudCustomerPublicationService sapRevenueCloudCustomerPublicationService)
	{
		this.sapRevenueCloudCustomerPublicationService = sapRevenueCloudCustomerPublicationService;
	}

	/**
	 * @return the sapMarketToCatalogMappingModelGenericDao
	 */
	public GenericDao<SAPMarketToCatalogMappingModel> getSapMarketToCatalogMappingModelGenericDao()
	{
		return sapMarketToCatalogMappingModelGenericDao;
	}

	/**
	 * @param sapMarketToCatalogMappingModelGenericDao
	 *           the sapMarketToCatalogMappingModelGenericDao to set
	 */
	public void setSapMarketToCatalogMappingModelGenericDao(
			final GenericDao<SAPMarketToCatalogMappingModel> sapMarketToCatalogMappingModelGenericDao)
	{
		this.sapMarketToCatalogMappingModelGenericDao = sapMarketToCatalogMappingModelGenericDao;
	}

}
