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
package de.hybris.platform.sap.saprevenuecloudorder.populators;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import com.sap.hybris.saprevenuecloudproduct.model.SAPMarketToCatalogMappingModel;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.saprevenuecloudorder.constants.SaprevenuecloudorderConstants;
import de.hybris.platform.sap.saprevenuecloudorder.data.AspectData;
import de.hybris.platform.sap.saprevenuecloudorder.data.AspectsData;
import de.hybris.platform.sap.saprevenuecloudorder.data.Customer;
import de.hybris.platform.sap.saprevenuecloudorder.data.Market;
import de.hybris.platform.sap.saprevenuecloudorder.data.OrderItem;
import de.hybris.platform.sap.saprevenuecloudorder.data.PaymentData;
import de.hybris.platform.sap.saprevenuecloudorder.data.Price;
import de.hybris.platform.sap.saprevenuecloudorder.data.Product;
import de.hybris.platform.sap.saprevenuecloudorder.data.Quantity;
import de.hybris.platform.sap.saprevenuecloudorder.data.SubscriptionItem;
import de.hybris.platform.sap.saprevenuecloudorder.data.SubscriptionItemPrice;
import de.hybris.platform.sap.saprevenuecloudorder.data.SubscriptionOrder;
import de.hybris.platform.sap.saprevenuecloudorder.service.SapRevenueCloudSubscriptionService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.subscriptionservices.model.BillingPlanModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import de.hybris.platform.subscriptionservices.price.SubscriptionCommercePriceService;


public class DefaultSAPRevenueCloudSubscriptionOrderPopulator implements Populator<AbstractOrderModel, SubscriptionOrder>
{

	private static final Logger LOG = Logger.getLogger(DefaultSAPRevenueCloudSubscriptionOrderPopulator.class);

	private SubscriptionCommercePriceService commercePriceService;
	private GenericDao<SAPMarketToCatalogMappingModel> sapCatalogToMarketMappingGenericDao;
	private CMSSiteService cmsSiteService;
	private ConfigurationService configurationService;
	private SapRevenueCloudSubscriptionService sapRevenueCloudSubscriptionService;

	@Override
	public void populate(final AbstractOrderModel order, final SubscriptionOrder subscriptionOrder) throws ConversionException
	{
		final String owner = ((CustomerModel) order.getUser()).getUid();
		subscriptionOrder.setOwner(owner);
		final Market market = new Market();
		final CatalogModel catalog = order.getStore().getCatalogs().get(0);
		final String marketId = getMarketFromCatalog(catalog);
		market.setMarketId(marketId);
		subscriptionOrder.setMarket(market);
		final Customer customer = new Customer();
		final String id = ((CustomerModel) order.getUser()).getRevenueCloudCustomerId();
		customer.setCustomerNumber(id);
		subscriptionOrder.setCustomer(customer);
		// Payment Data
		final PaymentData paymentData = new PaymentData();
		paymentData.setPaymentMethod(
				getConfigurationService().getConfiguration().getString(SaprevenuecloudorderConstants.DEFAULT_PAYMENT_TYPE));
		subscriptionOrder.setPaymentData(paymentData);

		try
		{
			populateOrderItems(order, subscriptionOrder);
			LOG.debug(subscriptionOrder);
		}
		catch (final CMSItemNotFoundException e)
		{
			LOG.error(e);
		}
	}

	protected void populateOrderItems(final AbstractOrderModel order, final SubscriptionOrder subscriptionOrder)
			throws CMSItemNotFoundException
	{
		final List<OrderItem> orderItems = new ArrayList<OrderItem>();

		for (final AbstractOrderEntryModel orderEntry : order.getEntries())
		{
			if (orderEntry.getProduct().getSubscriptionCode().isEmpty())
			{
				continue;
			}
			final OrderItem orderItem = new OrderItem();
			orderItem.setItemType(SaprevenuecloudorderConstants.SUBSCRIPTIONITEM);

			// Product
			final Product product = new Product();
			product.setId((orderEntry.getProduct().getSubscriptionCode()));
			orderItem.setProduct(product);

			// Quantity
			final Quantity quantity = new Quantity();
			quantity.setValue(orderEntry.getProduct().getPriceQuantity().toString());
			quantity.setUnit(orderEntry.getProduct().getUnit().getCode());
			orderItem.setQuantity(quantity);

			// Price
			final Price price = new Price();
			final AspectData aspectsData = new AspectData();
			final SubscriptionItemPrice itemPrice = new SubscriptionItemPrice();
			getCmsSiteService().setCurrentSiteAndCatalogVersions((CMSSiteModel) order.getSite(), true);
			final SubscriptionPricePlanModel pricePlanModel = getCommercePriceService()
					.getSubscriptionPricePlanForProduct(orderEntry.getProduct());// Use this to fetch raetPlan ID
			itemPrice.setPriceObjectId(pricePlanModel.getPricePlanId());
			aspectsData.setSubscriptionItemPrice(itemPrice);
			price.setAspectData(aspectsData);
			orderItem.setPrice(price);
			BillingPlanModel billingPlanModel = orderEntry.getProduct().getSubscriptionTerm().getBillingPlan();
			populateSubscriptionDates(orderItem,billingPlanModel.getId());
			// Adding Order Item to the list
			orderItems.add(orderItem);
		}

		subscriptionOrder.setOrderItems(orderItems);
	}
	

	protected void populateSubscriptionDates(final OrderItem orderItem,String billingPlan)
	{
		final SubscriptionItem item = new SubscriptionItem();
		//set ValidFrom Date
		final TimeZone tzUTC = TimeZone.getTimeZone(ZoneId.of(SaprevenuecloudorderConstants.UTC));
		item.setValidFrom(
				ZonedDateTime.now().withZoneSameInstant(tzUTC.toZoneId()).format(SaprevenuecloudorderConstants.ISO8601_FORMATTER));
		//set validUntil Date
		final GregorianCalendar calendar = GregorianCalendar
				.from(ZonedDateTime.now().withZoneSameInstant(ZoneId.of(SaprevenuecloudorderConstants.UTC)));
		final String timeZone = getConfigurationService().getConfiguration()
				.getString(SaprevenuecloudorderConstants.DEFAULT_TIMEZONE);
		final TimeZone tzInAmerica = TimeZone.getTimeZone(timeZone);
		calendar.setTimeZone(tzInAmerica);
		//Set default subscription validity to 1 year
		//validUntilDate should be Day 1 of month(for calendar products) and time should be Midnight 12:00
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		
		// Default 1 year validity for all anniversary billing cycle products
		if(billingPlan.contains("anniversary")) {
			
			calendar.set(Calendar.YEAR, year + 1);
			calendar.set(Calendar.MONTH,month);
			calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
		}
		else
			{
			int validTillMonth = month+1;
			boolean setValidMonth = false;
			
			LinkedList<Integer> quarterList = new LinkedList<Integer>(Arrays.asList(1,4,7,10));
			LinkedList<Integer> halfYearList = new LinkedList<Integer>(Arrays.asList(1,7));
			
			switch (billingPlan)
			{
				case "calendar_monthly":
							validTillMonth = month + 1;
							break;
							
				case "calendar_quarterly":
						  for (Integer quarterMonth : quarterList) {
							if(month + 1 < quarterMonth)
							{
								validTillMonth = quarterMonth-1;
								setValidMonth = true;
								break;
							}
						  }
						  if(!setValidMonth)	
						  {
							  validTillMonth = quarterList.getFirst();
						  }
						  break;
						  
				case "calendar_half_yearly":
						  for (Integer halfYearMonth : halfYearList) {
								if(month +1 < halfYearMonth)
								{
									validTillMonth = halfYearMonth-1;
									setValidMonth = true;
								}
						  }
						  if(!setValidMonth)	
						  {
							  validTillMonth = halfYearList.getFirst();
						  }
						  break;
						  
				case "calendar_yearly":
						  year = year +1;
						  validTillMonth = 0;
						  break;
					
				default:
					LOG.warn(String.format("Unknown billing frequency  \"%s\"", billingPlan));
					break;
			}
			
			
				calendar.set(Calendar.YEAR, year + 1);
				calendar.set(Calendar.MONTH, validTillMonth);
				calendar.set(Calendar.DAY_OF_MONTH, 1);
			}
		
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		item.setValidTo(
				calendar.toZonedDateTime().withZoneSameInstant(ZoneId.of(SaprevenuecloudorderConstants.UTC))
						.format(SaprevenuecloudorderConstants.ISO8601_FORMATTER));
		final AspectsData dateAspectsData = new AspectsData();
		dateAspectsData.setSubscriptionItem(item);
		orderItem.setAspectsData(dateAspectsData);
	}


	protected String getMarketFromCatalog(final CatalogModel catalog)
	{
		try
		{
			final Optional<String> cmOpt = getSapCatalogToMarketMappingGenericDao().find().stream()
					.filter(e -> e.getCatalog().equals(catalog)).map(SAPMarketToCatalogMappingModel::getMarketId).findFirst();
			return cmOpt.orElse("");
		}
		catch (final NoSuchElementException exception)
		{
			LOG.error("No Mapping Market found for" + catalog + exception);
		}

		return "";
	}

	public GenericDao<SAPMarketToCatalogMappingModel> getSapCatalogToMarketMappingGenericDao()
	{
		return sapCatalogToMarketMappingGenericDao;
	}

	public void setSapCatalogToMarketMappingGenericDao(
			final GenericDao<SAPMarketToCatalogMappingModel> sapCatalogToMarketMappingGenericDao)
	{
		this.sapCatalogToMarketMappingGenericDao = sapCatalogToMarketMappingGenericDao;
	}


	public CMSSiteService getCmsSiteService()
	{
		return cmsSiteService;
	}

	public void setCmsSiteService(final CMSSiteService cmsSiteService)
	{
		this.cmsSiteService = cmsSiteService;
	}

	public SubscriptionCommercePriceService getCommercePriceService()
	{
		return commercePriceService;
	}

	public void setCommercePriceService(final SubscriptionCommercePriceService commercePriceService)
	{
		this.commercePriceService = commercePriceService;
	}

	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}


	public SapRevenueCloudSubscriptionService getSapRevenueCloudSubscriptionService() {
		return sapRevenueCloudSubscriptionService;
	}

	public void setSapRevenueCloudSubscriptionService(
			SapRevenueCloudSubscriptionService sapRevenueCloudSubscriptionService) {
		this.sapRevenueCloudSubscriptionService = sapRevenueCloudSubscriptionService;
	}


}
