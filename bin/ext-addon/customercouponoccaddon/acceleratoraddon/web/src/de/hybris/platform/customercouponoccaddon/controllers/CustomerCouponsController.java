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
package de.hybris.platform.customercouponoccaddon.controllers;

import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.customercouponfacades.CustomerCouponFacade;
import de.hybris.platform.customercouponfacades.emums.AssignCouponResult;
import de.hybris.platform.customercouponfacades.strategies.CustomerNotificationPreferenceCheckStrategy;
import de.hybris.platform.customercouponoccaddon.constants.ErrorConstants;
import de.hybris.platform.customercouponoccaddon.dto.CustomerCouponSearchPageWsDTO;
import de.hybris.platform.customercouponoccaddon.exceptions.CouponClaimingException;
import de.hybris.platform.customercouponoccaddon.exceptions.NoAccessException;
import de.hybris.platform.customercouponservices.model.CustomerCouponModel;
import de.hybris.platform.util.Config;
import de.hybris.platform.webservicescommons.errors.exceptions.NotFoundException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.pagination.WebPaginationUtils;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


/**
 * APIs for my coupons.
 */
@Controller
@RequestMapping(value = "/{baseSiteId}/my-account/coupons")
@Api(tags = "Customer Coupons")
public class CustomerCouponsController
{

	private static final String MAX_PAGE_SIZE_KEY = "webservicescommons.pagination.maxPageSize";

	@Resource(name = "customerCouponFacade")
	private CustomerCouponFacade customerCouponFacade;

	@Resource(name = "customerNotificationPreferenceCheckStrategy")
	private CustomerNotificationPreferenceCheckStrategy customerNotificationPreferenceCheckStrategy;

	@Resource(name = "dataMapper")
	private DataMapper dataMapper;

	@Resource(name = "webPaginationUtils")
	private WebPaginationUtils webPaginationUtils;


	@ResponseBody
	@Secured(
	{ "ROLE_CUSTOMERGROUP" })
	@RequestMapping(method = RequestMethod.GET)
	@ApiOperation(value = "Gets all customer coupons of current customer", notes = "Returns the customer coupon list of current customer.")
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "currentPage", value = "the current result page requested", defaultValue = "0", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "pageSize", value = "the number of results returned per page", defaultValue = "10", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "sort", value = "sorting method applied to the return results", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "needsTotal", value = "whether to calculate the coupon totals", defaultValue = "true", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "baseSiteId", value = "base site identifier", required = true, dataType = "String", paramType = "path"),
			@ApiImplicitParam(name = "lang", value = "specifies the language", required = false, dataType = "String", paramType = "query")
		})
	public CustomerCouponSearchPageWsDTO coupons(@RequestParam final Map<String, String> parameters)
	{
		final SearchPageData searchPageData = getWebPaginationUtils().buildSearchPageData(parameters);
		recalculatePageSize(searchPageData);

		return getDataMapper().map(getCustomerCouponFacade().getPaginatedCoupons(searchPageData),
				CustomerCouponSearchPageWsDTO.class);
	}

	@ResponseStatus(code = HttpStatus.OK)
	@Secured(
	{ "ROLE_CUSTOMERGROUP" })
	@RequestMapping(value = "/{code}/claim", method = RequestMethod.PATCH)
	@ApiOperation(value = "Claims a customer coupon", notes = "Claims a customer coupon by coupon code.")
	@ApiBaseSiteIdParam
	public void claimCoupon(@ApiParam(value = "coupon code", required = true) @PathVariable final String code)
	{
		final AssignCouponResult result = getCustomerCouponFacade().grantCouponAccessForCurrentUser(code);
		if (result == AssignCouponResult.ASSIGNED)
		{
			throw new CouponClaimingException(ErrorConstants.CLAIMED_MESSAGE, CouponClaimingException.CLAIMED, code);
		}
		else if (result == AssignCouponResult.INEXISTENCE)
		{
			throw new NotFoundException(ErrorConstants.NOT_EXIST_MESSAGE, ErrorConstants.NOT_EXIST_REASON, code);
		}
	}

	@ResponseStatus(code = HttpStatus.OK)
	@Secured(
	{ "ROLE_CUSTOMERGROUP" })
	@RequestMapping(value = "/{code}/notification/subscribe", method = RequestMethod.PATCH)
	@ApiOperation(value = "Subscribes to a coupon notification", notes = "Subscribes to a notification for the specific customer coupon.")
	@ApiBaseSiteIdParam
	public void subscribeNotification(
			@ApiParam(value = "coupon code", required = true) @PathVariable final String code)
	{
		validateCoupon(code);
		getCustomerCouponFacade().saveCouponNotification(code);
		getCustomerNotificationPreferenceCheckStrategy().checkCustomerNotificationPreference();
	}

	@ResponseStatus(code = HttpStatus.OK)
	@Secured(
	{ "ROLE_CUSTOMERGROUP" })
	@RequestMapping(value = "/{code}/notification/unsubscribe", method = RequestMethod.PATCH)
	@ApiOperation(value = "Unsubscribes from the coupon notification", notes = "Unsubscribes from the notification for the specific customer coupon.")
	@ApiBaseSiteIdParam
	public void unsubscribeNotification(
			@ApiParam(value = "coupon code", required = true) @PathVariable final String code)
	{
		validateCoupon(code);
		getCustomerCouponFacade().removeCouponNotificationByCode(code);
	}

	protected void validateCoupon(final String code)
	{
		if (getCustomerCouponFacade().getValidCouponForCode(code) == null)
		{
			throw new NotFoundException(ErrorConstants.NOT_EXIST_MESSAGE, ErrorConstants.NOT_EXIST_REASON, code);
		}
		if (!getCustomerCouponFacade().isCouponOwnedByCurrentUser(code))
		{
			throw new NoAccessException(ErrorConstants.NOT_OWNED_MESSAGE, NoAccessException.NO_ACCESS, code,
					CustomerCouponModel._TYPECODE);
		}
	}

	protected void recalculatePageSize(final SearchPageData searchPageData)
	{
		int pageSize = searchPageData.getPagination().getPageSize();
		if (pageSize <= 0)
		{
			final int maxPageSize = Config.getInt(MAX_PAGE_SIZE_KEY, 1000);
			pageSize = getWebPaginationUtils().getDefaultPageSize();
			pageSize = pageSize > maxPageSize ? maxPageSize : pageSize;
			searchPageData.getPagination().setPageSize(pageSize);
		}
	}

	protected CustomerCouponFacade getCustomerCouponFacade()
	{
		return customerCouponFacade;
	}

	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}

	protected CustomerNotificationPreferenceCheckStrategy getCustomerNotificationPreferenceCheckStrategy()
	{
		return customerNotificationPreferenceCheckStrategy;
	}

	protected WebPaginationUtils getWebPaginationUtils()
	{
		return webPaginationUtils;
	}

}
