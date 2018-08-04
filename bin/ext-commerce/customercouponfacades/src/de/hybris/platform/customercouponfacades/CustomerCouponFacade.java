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
package de.hybris.platform.customercouponfacades;

import de.hybris.platform.commercefacades.voucher.exceptions.VoucherOperationException;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.customercouponfacades.customercoupon.data.CustomerCouponData;
import de.hybris.platform.customercouponfacades.customercoupon.data.CustomerCouponSearchPageData;
import de.hybris.platform.customercouponfacades.emums.AssignCouponResult;
import de.hybris.platform.customercouponfacades.impl.DefaultCustomerCouponFacade;

import java.util.List;


/**
 * Customer Coupon Facade {@link DefaultCustomerCouponFacade}
 */
public interface CustomerCouponFacade
{

	/**
	 * Get CouponData from pageableData
	 *
	 * @param pageableData
	 *           the pageable data
	 * @return paged list of coupon data
	 */
	de.hybris.platform.commerceservices.search.pagedata.SearchPageData<CustomerCouponData> getPagedCouponsData(
			final PageableData pageableData);

	/**
	 * Add custom to the coupon group. When custom alreay in coupon group that will return a alert msg.
	 *
	 * @param couponCode
	 *           coupon group code
	 * @return the result of add custom to coupon group
	 */
	AssignCouponResult grantCouponAccessForCurrentUser(final String couponCode);

	/**
	 *
	 * @return List<CouponData> list of coupon data
	 */
	List<CustomerCouponData> getCouponsData();


	/**
	 * Add coupon notification
	 *
	 * @param couponCode
	 *           coupon code
	 */
	void saveCouponNotification(final String couponCode);

	/**
	 * Remove coupon notification
	 *
	 * @param couponCode
	 *           coupon code
	 */
	void removeCouponNotificationByCode(final String couponCode);

	/**
	 * get assignable customer coupons data
	 *
	 * @param text
	 *           search text
	 * @return List<CustomerCouponData> list of coupon data
	 */
	List<CustomerCouponData> getAssignableCustomerCoupons(final String text);

	/**
	 * get assigned customer coupons data
	 *
	 * @param text
	 *           search text
	 * @return List<CustomerCouponData> list of coupon data
	 */
	List<CustomerCouponData> getAssignedCustomerCoupons(final String text);

	/**
	 * release the specific coupon for current customer
	 *
	 * @param couponCode
	 *           the specific coupon code
	 * @throws VoucherOperationException
	 *            when release voucher field
	 *
	 */
	void releaseCoupon(final String couponCode) throws VoucherOperationException;

	/**
	 * get CustomerCouponData by id
	 *
	 * @param couponId
	 *           the coupon id
	 * @return the correct CouponData get by code
	 */
	CustomerCouponData getCustomerCouponForCode(final String couponId);

	/**
	 * Check if the given coupon is owned by current user.
	 *
	 * @param couponCode
	 *           the given customer coupon code
	 * @return true is accessible or otherwise
	 */
	boolean isCouponOwnedByCurrentUser(String couponCode);

	/**
	 * Get paginated coupons
	 *
	 * @param searchPageData
	 *           the searchPageData
	 * @return paginated of CustomerCouponData
	 */
	CustomerCouponSearchPageData getPaginatedCoupons(SearchPageData searchPageData);

	/**
	 * Get valid(active = true, endDate > now, startDate != null) CustomerCouponData for given code
	 *
	 * @param code
	 *           the given code
	 * @return the valid CustomerCouponData
	 */
	CustomerCouponData getValidCouponForCode(final String code);
}
