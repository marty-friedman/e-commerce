/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.customercouponservices.email.process.impl;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.customercouponservices.constants.CustomercouponservicesConstants;
import de.hybris.platform.customercouponservices.model.CouponNotificationModel;
import de.hybris.platform.customercouponservices.model.CouponNotificationProcessModel;
import de.hybris.platform.notificationservices.processor.Processor;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


/**
 * Processor to send coupon notification notification
 */
public class DefaultCouponNotificationEmailProcessor implements Processor
{
	private ModelService modelService;
	private BusinessProcessService businessProcessService;

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
	 * @return the modelService
	 */
	protected ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	@Override
	public void process(final CustomerModel customer, final Map<String, ? extends ItemModel> dataMap)
	{
		final LanguageModel language = (LanguageModel) dataMap.get(CustomercouponservicesConstants.LANGUAGE);
		final CouponNotificationModel couponNotification = (CouponNotificationModel) dataMap
				.get(CustomercouponservicesConstants.COUPON_NOTIFICATION);


		final CouponNotificationProcessModel couponNotificationProcessModel = (CouponNotificationProcessModel) getBusinessProcessService()
				.createProcess("couponNotificationEmailProcess-" + customer.getUid() + "-" + System.currentTimeMillis() + "-"
						+ Thread.currentThread().getId(), "couponNotificationEmailProcess");

		couponNotificationProcessModel.setLanguage(language);
		couponNotificationProcessModel.setCouponNotification(couponNotification);
		final ItemModel notifycationType = dataMap.get(CustomercouponservicesConstants.NOTIFICATION_TYPE);
		couponNotificationProcessModel
				.setNotificationType(notifycationType.getProperty(CustomercouponservicesConstants.NOTIFICATION_TYPE));
		getModelService().save(couponNotificationProcessModel);
		getBusinessProcessService().startProcess(couponNotificationProcessModel);
	}

}
