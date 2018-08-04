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
package de.hybris.platform.sap.saprevenuecloudorder.facade.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.saprevenuecloudproduct.service.SapRevenueCloudProductService;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.sap.saprevenuecloudorder.constants.SaprevenuecloudorderConstants;
import de.hybris.platform.sap.saprevenuecloudorder.facade.SapRevenueCloudSubscriptionFacade;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.CancelSubscription;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.ExtendSubscription;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.MetaData;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.Subscription;
import de.hybris.platform.sap.saprevenuecloudorder.service.SapRevenueCloudSubscriptionService;
import de.hybris.platform.sap.saprevenuecloudorder.util.SapRevenueCloudSubscriptionUtil;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.subscriptionfacades.data.SubscriptionData;
import de.hybris.platform.subscriptionfacades.data.SubscriptionPricePlanData;
import de.hybris.platform.subscriptionfacades.exceptions.SubscriptionFacadeException;
import de.hybris.platform.subscriptionfacades.impl.DefaultSubscriptionFacade;
import de.hybris.platform.subscriptionservices.enums.SubscriptionStatus;
import de.hybris.platform.subscriptionservices.model.BillingFrequencyModel;
import de.hybris.platform.subscriptionservices.model.BillingPlanModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionTermModel;


public class DefaultSapRevenueCloudSubscriptionFacade extends DefaultSubscriptionFacade
		implements SapRevenueCloudSubscriptionFacade
{

	private static final Logger LOG = Logger.getLogger(DefaultSapRevenueCloudSubscriptionFacade.class);

	private SapRevenueCloudSubscriptionService sapRevenueCloudSubscriptionService;
	private UserService userService;
	private FlexibleSearchService flexibleSearchService;
	private BusinessProcessService businessProcessService;
	private SapRevenueCloudProductService sapRevenueCloudProductService;
	private Populator<SubscriptionPricePlanModel, SubscriptionPricePlanData> pricePlanOneTimeChargePopulator;
	private Populator<SubscriptionPricePlanModel, SubscriptionPricePlanData> pricePlanRecurringChargePopulator;
	private Populator<ProductModel, ProductData> productUrlPopulator;
	private CMSSiteService cmsSiteService;
	private BaseStoreService baseStoreService;
	private ConfigurationService configurationService;

	@Override
	public Collection<SubscriptionData> getSubscriptions() throws SubscriptionFacadeException
	{
		final String customerId = ((CustomerModel) userService.getCurrentUser()).getRevenueCloudCustomerId();
		final List<Subscription> subscriptions = sapRevenueCloudSubscriptionService.getSubscriptionsByClientId(customerId).stream()
				.sorted(Comparator.comparing(Subscription::getCreatedAt, Comparator.reverseOrder())).collect(Collectors.toList());
		LOG.info(String.format("Customer [%s] subscriptions with descending creation date:", customerId));
		subscriptions.stream().forEach(entry -> LOG.info(entry.getSubscriptionId()));
		final List<SubscriptionData> result = new ArrayList<>();
		for (final Subscription sapSubscription : subscriptions)
		{
			final SubscriptionData hybrisSubscription = new SubscriptionData();
			hybrisSubscription.setCustomerId(customerId);
			hybrisSubscription.setId(sapSubscription.getSubscriptionId());
			populateStatus(sapSubscription, hybrisSubscription);
			final String ratePlanId = sapSubscription.getSnapshots().get(0).getItems().get(0).getRatePlan().getId();
			final SubscriptionPricePlanModel pricePlanModel = getSapRevenueCloudProductService()
					.getSubscriptionPricePlanForId(ratePlanId, getCmsSiteService().getCurrentCatalogVersion());
			hybrisSubscription.setName(pricePlanModel.getProduct().getName());
			final ProductData productData = new ProductData();
			getProductUrlPopulator().populate(pricePlanModel.getProduct(), productData);
			hybrisSubscription.setProductUrl(productData.getUrl());
			hybrisSubscription.setStartDate(SapRevenueCloudSubscriptionUtil.formatDate(sapSubscription.getValidFrom()));
			if (sapSubscription.getValidUntil() != null && !sapSubscription.getValidUntil().isEmpty())
			{
				hybrisSubscription.setEndDate(SapRevenueCloudSubscriptionUtil.formatDate(sapSubscription.getValidUntil()));
			}
			hybrisSubscription.setDocumentNumber(sapSubscription.getDocumentNumber());
			result.add(hybrisSubscription);
		}
		return result;
	}

	@Override
	public SubscriptionData getSubscription(final String subscriptionId) throws SubscriptionFacadeException
	{
		final Subscription sapSubscription = sapRevenueCloudSubscriptionService.getSubscriptionById(subscriptionId);
		if (sapSubscription == null)
		{
			return new SubscriptionData();
		}
		final SubscriptionData subscriptionDetails = new SubscriptionData();
		subscriptionDetails.setDescription(
				"SAP Subscription For Product: " + sapSubscription.getSnapshots().get(0).getItems().get(0).getProduct().getCode());
		subscriptionDetails.setId(sapSubscription.getSubscriptionId());
		subscriptionDetails.setProductCode(sapSubscription.getSnapshots().get(0).getItems().get(0).getProduct().getCode());
		subscriptionDetails.setCustomerId(sapSubscription.getCustomer().getId());
		subscriptionDetails.setStartDate(SapRevenueCloudSubscriptionUtil.formatDate(sapSubscription.getValidFrom()));
		if (sapSubscription.getValidUntil() != null && !sapSubscription.getValidUntil().isEmpty())
		{
			subscriptionDetails.setEndDate(SapRevenueCloudSubscriptionUtil.formatDate(sapSubscription.getValidUntil()));
			subscriptionDetails.setValidTillDate(sapSubscription.getValidUntil());
		}
		subscriptionDetails.setVersion(sapSubscription.getMetaData().getVersion());
		populateStatus(sapSubscription, subscriptionDetails);
		final String ratePlanId = sapSubscription.getSnapshots().get(0).getItems().get(0).getRatePlan().getId();
		subscriptionDetails.setRatePlanId(ratePlanId);
		final CatalogVersionModel currentCatalog = getCmsSiteService().getCurrentCatalogVersion();
		final SubscriptionPricePlanModel pricePlanModel = getSapRevenueCloudProductService()
				.getSubscriptionPricePlanForId(ratePlanId, currentCatalog);
		final ProductModel productModel = pricePlanModel.getProduct();
		subscriptionDetails.setName(pricePlanModel.getProduct().getName());
		final ProductData productData = new ProductData();
		getProductUrlPopulator().populate(productModel, productData);
		subscriptionDetails.setProductUrl(productData.getUrl());

		final SubscriptionPricePlanData pricePlanData = new SubscriptionPricePlanData();
		populatePricePlan(pricePlanModel, pricePlanData);
		BillingFrequencyModel billingFrequencyModel = getSapSubscriptionService().getBillingFrequency(productModel);
		subscriptionDetails.setBillingFrequency(billingFrequencyModel.getNameInCart());
		String billingFrequencyTerm = billingFrequencyModel.getCode();
		BillingPlanModel billingPlanModel = productModel.getSubscriptionTerm().getBillingPlan();
		switch (billingPlanModel.getId())
				{
					case "calendar_monthly":
						subscriptionDetails.setContractFrequency("Months");
						break;
					case "anniversary_monthly":
						subscriptionDetails.setContractFrequency("Months");
						break;
					case "calendar_quarterly":
						subscriptionDetails.setContractFrequency("Quarters");
						break;
					case "anniversary_quarterly":
						subscriptionDetails.setContractFrequency("Quarters");
						break;
					case "calendar_half_yearly":
						subscriptionDetails.setContractFrequency("Half-Years");
						break;
					case "anniversary_half_yearly":
						subscriptionDetails.setContractFrequency("Half-Years");
						break;
					case "calendar_yearly":
						subscriptionDetails.setContractFrequency("Years");
						break;
					case "anniversary_yearly":
						subscriptionDetails.setContractFrequency("Years");
						break;
					default:
						LOG.warn(String.format("Unknown frequency code \"%s\"", billingFrequencyTerm));
						break;
				}
		
		subscriptionDetails.setPricePlan(pricePlanData);
		return subscriptionDetails;
	}

	@Override
	public boolean cancelSubscription(final SubscriptionData subscriptionData) throws SubscriptionFacadeException
	{
		final MetaData metaData = new MetaData();
		metaData.setVersion(subscriptionData.getVersion());
		final CancelSubscription cancelSubsciption = new CancelSubscription();
		cancelSubsciption.setCancellationReason(
				configurationService.getConfiguration().getString(SaprevenuecloudorderConstants.CANCELLATION_REASON));
		cancelSubsciption.setMetaData(metaData);
		final CatalogModel currentCatalog = getBaseStoreService().getCurrentBaseStore().getCatalogs().get(0);
		final SubscriptionPricePlanModel pricePlanModel = getRatePlanId(subscriptionData.getRatePlanId(),
				currentCatalog.getActiveCatalogVersion());
		BillingPlanModel billingPlanModel = pricePlanModel.getProduct().getSubscriptionTerm().getBillingPlan();
		final String requestedCancellationDate = calculateCancellationDate(billingPlanModel.getId(), subscriptionData.getValidTillDate());
		cancelSubsciption.setRequestedCancellationDate(requestedCancellationDate);
		sapRevenueCloudSubscriptionService.cancelSubscription(subscriptionData.getId(), cancelSubsciption);
		return true;
	}

	@Override
	public boolean extendSubscription(final SubscriptionData subscriptionData) throws SubscriptionFacadeException
	{
		final ExtendSubscription extendSubscription = new ExtendSubscription();
		final MetaData metaData = new MetaData();
		metaData.setVersion(subscriptionData.getVersion());
		extendSubscription.setMetaData(metaData);
		if (subscriptionData.getUnlimited().booleanValue())
		{
			extendSubscription.setUnlimited("true");
		}
		else
		{
		extendSubscription.setUnlimited("false");	
		final CatalogModel currentCatalog = getBaseStoreService().getCurrentBaseStore().getCatalogs().get(0);
		final SubscriptionPricePlanModel pricePlanModel = getRatePlanId(subscriptionData.getRatePlanId(),
				currentCatalog.getActiveCatalogVersion());
		BillingPlanModel billingPlanModel = pricePlanModel.getProduct().getSubscriptionTerm().getBillingPlan();
		extendSubscription.setExtensionDate(
				calculateExtensionDate(billingPlanModel.getId(), subscriptionData.getValidTillDate(), subscriptionData.getExtendedPeriod()));
		}
		getSapSubscriptionService().extendSubscription(subscriptionData.getId(), extendSubscription);
		return true;
	}


	protected SubscriptionPricePlanModel getRatePlanId(final String ratePlanId, final CatalogVersionModel currentCatalog)
	{
		return getSapRevenueCloudProductService().getSubscriptionPricePlanForId(ratePlanId, currentCatalog);
	}

	protected void populatePricePlan(final SubscriptionPricePlanModel pricePlanModel,
			final SubscriptionPricePlanData pricePlanData)
	{
		if (pricePlanModel != null)
		{
			pricePlanData.setName(pricePlanModel.getName());
			getPricePlanOneTimeChargePopulator().populate(pricePlanModel, pricePlanData);
			getPricePlanRecurringChargePopulator().populate(pricePlanModel, pricePlanData);
		}
	}

	protected void populateSubscriptionTerm(final ProductModel productModel, final SubscriptionData subscriptionData)
	{
		final SubscriptionTermModel subscriptionTerm = productModel.getSubscriptionTerm();
		if (subscriptionTerm != null)
		{
			final BillingPlanModel billingPlan = subscriptionTerm.getBillingPlan();
			if (billingPlan != null && billingPlan.getBillingFrequency() != null)
			{
				subscriptionData.setBillingFrequency(billingPlan.getBillingFrequency().getNameInCart());
			}
		}
	}

	protected void populateStatus(final Subscription sapSubscription, final SubscriptionData subscriptionData)
	{
		SubscriptionStatus status = SubscriptionStatus.ACTIVE;
		if (sapSubscription.getCancellationReason() != null && !sapSubscription.getCancellationReason().isEmpty())
		{
			status = SubscriptionStatus.CANCELLED;
		}
		subscriptionData.setStatus(status);
	}

	protected String calculateCancellationDate(final String billingPlanId, final String validUntildate)
	{
		final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
		final String validUntilDate = validUntildate;
		final SimpleDateFormat formatter = new SimpleDateFormat(DATE_TIME_PATTERN);
		String cancellationDate = "";
		try
		{
			final Date expirationDate = formatter.parse(validUntilDate);
			final SimpleDateFormat format = new SimpleDateFormat(SaprevenuecloudorderConstants.YYYY_MM_DD);
			final String validUntilDateinString = format.format(expirationDate);
			final Date currentDate = Date.from(ZonedDateTime.now().toInstant());
			
			Calendar validTillDate = GregorianCalendar.getInstance();
			validTillDate.setTime(expirationDate);
			int validTillDay = validTillDate.get(Calendar.DAY_OF_MONTH);
			
			LOG.debug(
					"current Date - " + currentDate + "comparing dates " + currentDate.before(format.parse(validUntilDateinString)));
		final GregorianCalendar calendar = GregorianCalendar
					.from(ZonedDateTime.now().withZoneSameInstant(ZoneId.of(SaprevenuecloudorderConstants.UTC)));
			int month = calendar.get(Calendar.MONTH);
			int year = calendar.get(Calendar.YEAR);
			int today = calendar.get(Calendar.DAY_OF_MONTH);
			int newValidTillMonth = 0;
			boolean setValidMonth = false;
			
			
			LinkedList<Integer> quarterList = new LinkedList<Integer>(Arrays.asList(1,4,7,10));
			LinkedList<Integer> halfYearList = new LinkedList<Integer>(Arrays.asList(1,7));
			
			if(billingPlanId.contains("anniversary")) {
				
				switch (billingPlanId)
				{
					case "anniversary_monthly":
						 if(validTillDay < today) {
							 newValidTillMonth = month;
						 }
						 else
						 {
							 newValidTillMonth = month + 1;
						 }
						 break;
				
					default:
						LOG.warn(String.format("Unknown billing frequency  \"%s\"", billingPlanId));
						break;
					
				}
				calendar.set(Calendar.YEAR, year);
				calendar.set(Calendar.MONTH,newValidTillMonth);
				calendar.set(Calendar.DAY_OF_MONTH,validTillDay);
			}
			else
				{
				switch (billingPlanId)
				{
					case "calendar_monthly":
								newValidTillMonth = month + 1;
								break;
								
					case "calendar_quarterly":
							  for (Integer quarterMonth : quarterList) {
								if(month + 1 < quarterMonth)
								{
									newValidTillMonth = quarterMonth-1;
									setValidMonth = true;
									break;
								}
							  }
							  if(!setValidMonth)	
							  {
								  newValidTillMonth = quarterList.getFirst();
							  }
							  break;
							  
					case "calendar_half_yearly":
							  for (Integer halfYearMonth : halfYearList) {
									if(month +1 < halfYearMonth)
									{
										newValidTillMonth = halfYearMonth-1;
										setValidMonth = true;
									}
							  }
							  if(!setValidMonth)	
							  {
								  newValidTillMonth = halfYearList.getFirst();
								  year = year + 1;
							  }
							  break;
							  
					case "calendar_yearly":
							newValidTillMonth = 0;
							year = year+1;
							  break;
						
					default:
						LOG.warn(String.format("Unknown billing frequency  \"%s\"", billingPlanId));
						break;
				}
				calendar.set(Calendar.MONTH, newValidTillMonth);
				calendar.set(Calendar.YEAR, year);
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				}
			
			cancellationDate = format.format(calendar.getTime());
			LOG.debug("Calculated Extension Date : " + cancellationDate);
		}
		catch (final ParseException e)
		{
			LOG.info("Exception while parsing dates :" + e);
		}

		return cancellationDate;
	}

	protected String calculateExtensionDate(final String billingPlanId, final String validUntildate,
			final String extensionPeriod)
	{
		final SimpleDateFormat dateFormatter = new SimpleDateFormat(SaprevenuecloudorderConstants.YYYY_MM_DD);
		String extensionDate = "";
		Integer extensionTerm = Integer.parseInt(extensionPeriod);
		try
		{
			final String validTilldate = validUntildate;
			final GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTime(dateFormatter.parse(validTilldate));
			LOG.debug("ValidDate of subscription : " + validTilldate);
			final int month = calendar.get(Calendar.MONTH);
			int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
			int newValidTillMonth = 0;
			
			switch (billingPlanId)
			{
			    case "anniversary_monthly":
				case "calendar_monthly":
					newValidTillMonth = month + extensionTerm;
					break;
				case "anniversary_quarterly":	
				case "calendar_quarterly":
					newValidTillMonth = month + (extensionTerm *3);
					break;
				case "anniversary_half_yearly":
				case "calendar_half_yearly":
					newValidTillMonth = month + (extensionTerm *6);
					break;
				case "anniversary_yearly":
				case "calendar_yearly":
					newValidTillMonth = month + (extensionTerm *12);
					break;
				default:
					LOG.warn(String.format("Unknown frequency code \"%s\"", billingPlanId));
					break;
			}
			
			
			calendar.set(Calendar.MONTH, newValidTillMonth);
			if(billingPlanId.contains("calendar"))
			{
				calendar.set(Calendar.DAY_OF_MONTH, 1);
			}
			else
				calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			final TimeZone defaultTZ = TimeZone
					.getTimeZone(configurationService.getConfiguration().getString(SaprevenuecloudorderConstants.DEFAULT_TIMEZONE));
			calendar.setTimeZone(defaultTZ);
			extensionDate = calendar.toZonedDateTime().withZoneSameInstant(ZoneId.of(SaprevenuecloudorderConstants.UTC))
					.format(SaprevenuecloudorderConstants.ISO8601_FORMATTER);
			LOG.debug("Calculated Extension Date : " + extensionDate);

		}
		catch (final ParseException e)
		{
			LOG.info("Exception while parsing dates :" + e);
		}

		return extensionDate;
	}


	//Method to fetch Product RatePlan
	protected BillingFrequencyModel getBillingFrequencyPlan(final String ratePlanId, final CatalogVersionModel currentCatalog)
	{
		final SubscriptionPricePlanModel pricePlanModel = getSapRevenueCloudProductService()
				.getSubscriptionPricePlanForId(ratePlanId, currentCatalog);
		BillingFrequencyModel billingFrequency = null;
		final SubscriptionTermModel subscriptionTerm = pricePlanModel.getProduct().getSubscriptionTerm();
		if (subscriptionTerm != null)
		{
			final BillingPlanModel billingPlan = subscriptionTerm.getBillingPlan();
			if (billingPlan != null && billingPlan.getBillingFrequency() != null)
			{
				billingFrequency = billingPlan.getBillingFrequency();
			}
		}
		return billingFrequency;
	}


	@Override
	public List<ProductData> getUpsellingOptionsForSubscription(final String productCode)
	{
		return Lists.emptyList();
	}

	protected SapRevenueCloudSubscriptionService getSapSubscriptionService()
	{
		return sapRevenueCloudSubscriptionService;
	}

	@Required
	public void setSapSubscriptionService(final SapRevenueCloudSubscriptionService sapRevenueCloudSubscriptionService)
	{
		this.sapRevenueCloudSubscriptionService = sapRevenueCloudSubscriptionService;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}


	protected FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	protected BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	@Required
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}

	/**
	 * @return the sapRevenueCloudProductService
	 */
	public SapRevenueCloudProductService getSapRevenueCloudProductService()
	{
		return sapRevenueCloudProductService;
	}

	/**
	 * @param sapRevenueCloudProductService
	 *           the sapRevenueCloudProductService to set
	 */
	public void setSapRevenueCloudProductService(final SapRevenueCloudProductService sapRevenueCloudProductService)
	{
		this.sapRevenueCloudProductService = sapRevenueCloudProductService;
	}

	/**
	 * @return the pricePlanOneTimeChargePopulator
	 */
	public Populator<SubscriptionPricePlanModel, SubscriptionPricePlanData> getPricePlanOneTimeChargePopulator()
	{
		return pricePlanOneTimeChargePopulator;
	}

	/**
	 * @param pricePlanOneTimeChargePopulator
	 *           the pricePlanOneTimeChargePopulator to set
	 */
	public void setPricePlanOneTimeChargePopulator(
			final Populator<SubscriptionPricePlanModel, SubscriptionPricePlanData> pricePlanOneTimeChargePopulator)
	{
		this.pricePlanOneTimeChargePopulator = pricePlanOneTimeChargePopulator;
	}

	/**
	 * @return the pricePlanRecurringChargePopulator
	 */
	public Populator<SubscriptionPricePlanModel, SubscriptionPricePlanData> getPricePlanRecurringChargePopulator()
	{
		return pricePlanRecurringChargePopulator;
	}

	/**
	 * @param pricePlanRecurringChargePopulator
	 *           the pricePlanRecurringChargePopulator to set
	 */
	public void setPricePlanRecurringChargePopulator(
			final Populator<SubscriptionPricePlanModel, SubscriptionPricePlanData> pricePlanRecurringChargePopulator)
	{
		this.pricePlanRecurringChargePopulator = pricePlanRecurringChargePopulator;
	}

	/**
	 * @return the productUrlPopulator
	 */
	public Populator<ProductModel, ProductData> getProductUrlPopulator()
	{
		return productUrlPopulator;
	}

	/**
	 * @param productUrlPopulator
	 *           the productUrlPopulator to set
	 */
	public void setProductUrlPopulator(final Populator<ProductModel, ProductData> productUrlPopulator)
	{
		this.productUrlPopulator = productUrlPopulator;
	}

	/**
	 * @return the cmsSiteService
	 */
	public CMSSiteService getCmsSiteService()
	{
		return cmsSiteService;
	}

	/**
	 * @param cmsSiteService
	 *           the cmsSiteService to set
	 */
	public void setCmsSiteService(final CMSSiteService cmsSiteService)
	{
		this.cmsSiteService = cmsSiteService;
	}

	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	@Override
	public BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	@Override
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}


}
