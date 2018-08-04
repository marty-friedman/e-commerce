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
package de.hybris.platform.customercouponservices.interceptor;

import de.hybris.platform.couponservices.interceptor.CouponInterceptorException;
import de.hybris.platform.couponservices.model.AbstractCouponModel;
import de.hybris.platform.customercouponservices.model.CustomerCouponModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

import java.util.Date;
import java.util.Objects;

import org.joda.time.DateTime;

import com.google.common.base.Preconditions;


public class CustomerCouponValidateInterceptor implements ValidateInterceptor<AbstractCouponModel>
{

	@Override
	public void onValidate(final AbstractCouponModel coupon, final InterceptorContext ctx) throws InterceptorException
	{
		Preconditions.checkArgument(Objects.nonNull(coupon), "Coupon model cannot be NULL here");

		final Date endDate = coupon.getEndDate();
		if (!(coupon instanceof CustomerCouponModel) && Objects.nonNull(endDate) && new DateTime(endDate).isBeforeNow())
		{
				throw new CouponInterceptorException("End date cannot be in the past");
		}

		final Date startDate = coupon.getStartDate();
		if (Objects.nonNull(startDate) && Objects.nonNull(endDate) && startDate.after(endDate))
		{
			throw new CouponInterceptorException("Illegal value of startDate or endDate: endDate should be after startDate.");
		}
	}

}
