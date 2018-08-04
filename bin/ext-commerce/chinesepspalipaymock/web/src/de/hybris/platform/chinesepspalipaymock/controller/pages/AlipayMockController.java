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
package de.hybris.platform.chinesepspalipaymock.controller.pages;

import de.hybris.platform.chinesepaymentfacades.checkout.ChineseCheckoutFacade;
import de.hybris.platform.chinesepspalipaymock.controller.AbstractController;
import de.hybris.platform.chinesepspalipaymock.controller.AlipayMockControllerConstants;
import de.hybris.platform.chinesepspalipaymock.service.MockService;
import de.hybris.platform.chinesepspalipaymock.utils.imported.CSRFRequestDataValueProcessor;
import de.hybris.platform.chinesepspalipaymock.utils.imported.XSSFilterUtil;
import de.hybris.platform.chinesepspalipayservices.payment.DefaultAlipayPaymentService;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.enums.PaymentStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.site.BaseSiteService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping(value = "/checkout/multi/alipay/mock/gateway.do")
public class AlipayMockController extends AbstractController
{
	protected static final Logger LOG = Logger.getLogger(AlipayMockController.class);

	@Resource
	private MockService mockService;

	@Resource(name = "alipayPaymentService")
	private DefaultAlipayPaymentService defaultAlipayPaymentService;

	@Resource(name = "chineseCheckoutFacade")
	private ChineseCheckoutFacade chineseCheckoutFacade;

	@Resource(name = "baseSiteService")
	private BaseSiteService baseSiteService;

	/**
	 * Opens alipay mock landing page
	 *
	 * @param model
	 *           session content information
	 * @param request
	 *           the http request
	 * @return page the alipay mock landing page
	 * @throws UnsupportedEncodingException
	 *            throw UnsupportedEncodingException when request parameters contain unsupported encoding chars
	 */
	@SuppressWarnings("boxing")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String doGetGateWay(final Model model, final HttpServletRequest request) throws UnsupportedEncodingException
	{
		final Map<String, String[]> requestParamMap = request.getParameterMap();
		if (requestParamMap == null)
		{
			return AlipayMockControllerConstants.Pages.AlipayMockPage;
		}
		final String baseGateWay = request.getRequestURL().toString();
		model.addAttribute("baseGateWay", baseGateWay);

		final Map<String, String> clearParams = removeUselessValue(requestParamMap);
		this.setCSRFToken(clearParams, request);

		final String service = request.getParameter("service");
		if (service == null)
		{
			return AlipayMockControllerConstants.Pages.AlipayMockPage;
		}
		XSSFilterUtil.filter(service);
		final boolean signIsValid = isSignValid(clearParams);

		// TOMCAT does not accept "^" in URL parameter, hence encode it here
		if (StringUtils.isNotEmpty(clearParams.get("detail_data")))
		{
			model.addAttribute("detail_data", clearParams.get("detail_data"));
			clearParams.put("detail_data", URLEncoder.encode(clearParams.get("detail_data"), "UTF-8"));
		}

		model.addAttribute("signIsValid", Boolean.valueOf(signIsValid));
		model.addAttribute("params", clearParams);
		model.addAttribute("out_trade_no", clearParams.get("out_trade_no"));
		model.addAttribute("total_fee", clearParams.get("total_fee"));
		model.addAttribute("storefront", (StringUtils.substringBetween(request.getContextPath(), "/")));

		if ("refund_fastpay_by_platform_pwd".equals(service))
		{
			return AlipayMockControllerConstants.Pages.AlipayRefundPage;
		}

		return AlipayMockControllerConstants.Pages.AlipayMockPage;
	}

	/**
	 * Opens alipay refund landing page
	 *
	 * @param model
	 *           session content information
	 * @param request
	 *           the http request
	 * @return alipay refund landing page
	 */
	@RequestMapping(value = "/refund", method = RequestMethod.GET)
	public String view(final Model model, final HttpServletRequest request)
	{
		final Map<String, String[]> requestParamMap = request.getParameterMap();
		if (requestParamMap == null)
		{
			return AlipayMockControllerConstants.Pages.AlipayRefundTestPage;
		}
		final String baseGateWay = request.getRequestURL().toString();
		model.addAttribute("baseGateWay", baseGateWay);
		model.addAttribute("storefront", (StringUtils.substringBetween(request.getContextPath(), "/")));

		final Map<String, String> clearParams = removeUselessValue(requestParamMap);
		setCSRFToken(clearParams, request);

		model.addAttribute("baseSites", baseSiteService.getAllBaseSites());
		return AlipayMockControllerConstants.Pages.AlipayRefundTestPage;
	}

	/**
	 * Handles refunding process
	 *
	 * @param model
	 *           session content information
	 * @param request
	 *           the http request
	 * @param response
	 *           the http response
	 * @param orderCode
	 *           order code
	 * @param baseSite
	 *           base site name
	 * @return refund result
	 * @throws IOException
	 *            throw IOException when refund request failed
	 *
	 */
	@RequestMapping(value = "/refund", method = RequestMethod.POST)
	public @ResponseBody String doRefundRequest(final Model model, final HttpServletRequest request,
			final HttpServletResponse response, final String orderCode, final String baseSite) throws IOException
	{
		if (baseSite != null)
		{
			baseSiteService.setCurrentBaseSite(baseSite, false);
		}

		if (orderCode == null || orderCode.isEmpty())
		{
			return "Error : Please input order #";
		}
		else
		{
			try
			{
				final Optional<String> refundUrl = defaultAlipayPaymentService.getRefundRequestUrl(orderCode);
				if (refundUrl.isPresent())
				{
					return "redirect:" + refundUrl.get();
				}
			}
			catch (final ModelNotFoundException e) //NOSONAR
			{
				return "Error : order# '" + StringEscapeUtils.escapeHtml4(orderCode) + "' could not be found";
			}
		}
		return "Error : order# '" + StringEscapeUtils.escapeHtml4(orderCode) + "' cannot be refunded";
	}


	/**
	 * Handles verification
	 *
	 * @param response
	 *           the http response
	 * @throws IOException
	 *            throw IOException when outputting failed
	 */
	@RequestMapping(value = "/notify.verify")
	public void doNotifyVerify(final HttpServletResponse response) throws IOException
	{
		response.getWriter().print("true");
	}


	/**
	 * Handles direct pay
	 *
	 * @param model
	 *           session content information
	 * @param request
	 *           the http request
	 * @param response
	 *           the http response
	 * @throws IOException
	 *            throw IOException when handling direct pay failed
	 */
	@SuppressWarnings("boxing")
	@RequestMapping(value = "/directpay", method = RequestMethod.POST)
	public void doPostDirectPay(final Model model, final HttpServletRequest request, final HttpServletResponse response)
			throws IOException
	{
		doDirectPay(request, response);
	}

	/**
	 * Gets refund result
	 *
	 * @param model
	 *           session content information
	 * @param request
	 *           the http request
	 * @param response
	 *           the http response
	 * @return result of refund
	 * @throws IOException
	 *            throw IOException when refunding failed
	 */
	@RequestMapping(value = "/doRefund", method = RequestMethod.GET)
	public @ResponseBody String doRefund(final Model model, final HttpServletRequest request, final HttpServletResponse response)
			throws IOException
	{
		final Map<String, String[]> requestParamMap = request.getParameterMap();
		if (requestParamMap == null)
		{
			return "";
		}
		final Map<String, String> requestType = createRequestTypeMap(requestParamMap);
		final Map<String, String> clearParams = removeUselessValue(requestParamMap);
		final String errorCode = XSSFilterUtil.filter(requestType.get("error_code"));
		this.setCSRFToken(clearParams, request);
		final Map<String, String> notify = mockService.getRefundNotifyParams(clearParams, errorCode);
		mockService.handleRefundRequest(notify);

		final String action = request.getParameter("action");
		if (action == null)
		{
			return "";
		}
		String resultMessage = "";
		if ("notify".equals(action))
		{
			resultMessage = "Refund Finished!";
		}
		return resultMessage;
	}

	/**
	 * Gets direct pay result
	 *
	 * @param request
	 *           the http request
	 * @param response
	 *           the http response
	 * @return result of direct pay
	 * @throws IOException
	 *            throw IOException when direct pay failed
	 */
	@RequestMapping(value = "/directpay", method = RequestMethod.GET)
	public @ResponseBody String doGetDirectPay(final HttpServletRequest request, final HttpServletResponse response)
			throws IOException
	{
		doDirectPay(request, response);
		final String action = request.getParameter("action");
		if (action == null)
		{
			return "";
		}
		String resultMessage = "";
		if ("notify".equals(action))
		{
			resultMessage = "DirectPay Success!";
		}
		else if ("notify_error".equals(action))
		{
			resultMessage = "DirectPay Fails!";
		}
		return resultMessage;
	}

	protected void doDirectPay(final HttpServletRequest request, final HttpServletResponse response) throws IOException
	{
		final Map<String, String[]> requestParamMap = request.getParameterMap();
		if (requestParamMap == null)
		{
			return;
		}
		final Map<String, String> requestType = createRequestTypeMap(requestParamMap);
		final Map<String, String> clearParams = removeUselessValue(requestParamMap);
		this.setCSRFToken(clearParams, request);

		final String sign = mockService.getSign(clearParams);
		final boolean signIsValid = sign.equals(clearParams.get("sign"));
		if (signIsValid)
		{
			final String service = request.getParameter("service");
			if (service != null)
			{
				XSSFilterUtil.filter(service);
				if ("create_direct_pay_by_user".equals(service))
				{
					handleDirectPayRequest(clearParams, response, signIsValid, requestType);
				}
			}
		}
	}

	protected boolean isSignValid(final Map<String, String> requestMap)
	{
		final String generateSign = mockService.getSign(requestMap);
		return generateSign.equals(requestMap.get("sign"));
	}


	protected Map<String, String> createRequestTypeMap(final Map<String, String[]> params)
	{
		final Map<String, String> RequestType = new HashMap<>();
		RequestType.put("action", params.get("action")[0]);
		RequestType.put("trade_status", params.get("trade_status")[0]);
		RequestType.put("error_code", params.get("error_code")[0]);
		return RequestType;
	}

	protected Map<String, String> removeUselessValue(final Map<String, String[]> params)
	{
		final Map<String, String> clearMap = new HashMap<>();
		for (final String key : params.keySet()) // NOSONAR
		{
			if ("action".equalsIgnoreCase(key) || "trade_status".equalsIgnoreCase(key) || "error_code".equalsIgnoreCase(key))
			{
				continue;
			}

			final String value = params.get(key)[0];
			clearMap.put(key, value);
		}
		return clearMap;
	}

	protected void handleDirectPayRequest(final Map<String, String> params, final HttpServletResponse response,
			final boolean signIsValid, final Map<String, String> requestType) throws IOException
	{
		final String tradeStatus = XSSFilterUtil.filter(requestType.get("trade_status"));
		final String errorCode = XSSFilterUtil.filter(requestType.get("error_code"));
		final String action = XSSFilterUtil.filter(requestType.get("action"));
		LOG.info("Payment request");


		if ("notify".equalsIgnoreCase(action))
		{
			notify(params, tradeStatus);
		}
		else if ("notify_error".equalsIgnoreCase(action))
		{
			notifyError(params, errorCode);
		}
		else if ("return".equalsIgnoreCase(action))
		{
			returnResponse(response, params, tradeStatus);
		}

		else if (tradeStatus == null && signIsValid)
		{
			final String defaultTradeStatus = Registry.getMasterTenant().getConfig().getString("alipay.mock.default.trade.status",
					"WAIT_BUYER_PAY");
			notify(params, "WAIT_BUYER_PAY");
			if (!"WAIT_BUYER_PAY".equals(defaultTradeStatus))
			{
				notify(params, defaultTradeStatus);
			}
			if ("TRADE_SUCCESS".equals(defaultTradeStatus))
			{
				returnResponse(response, params, tradeStatus);
			}
		}
	}

	protected void notify(final Map<String, String> params, final String tradeStatus)
	{
		final Map<String, String> notify = mockService.getNotifyParams(params, tradeStatus);
		final String orderCode = mockService.handleNotifyRequest(notify);
		if (orderCode != null)
		{
			final OrderModel order = mockService.getOrderModelByCode(orderCode);
			if (PaymentStatus.PAID.equals(order.getPaymentStatus()))
			{
				chineseCheckoutFacade.deleteStockLevelReservationHistoryEntry(orderCode);
				mockService.publishSubmitOrderEvent(orderCode);
			}
		}
	}

	protected void notifyError(final Map<String, String> params, final String errorCode)
	{
		final Map<String, String> notify = mockService.getNotifyErrorParams(params, errorCode);
		mockService.handleNotifyErrorRequest(notify);
	}

	protected void returnResponse(final HttpServletResponse response, final Map<String, String> params, final String tradeStatus)
			throws IOException
	{
		mockService.stripOffCSRFToken(params);
		final String returnUrl = getReturnShopUrl(params, tradeStatus);
		response.sendRedirect(returnUrl); // NOSONAR
	}

	protected String getReturnShopUrl(final Map<String, String> params, final String tradeStatus)
	{
		final Map<String, String> notify = mockService.getReturnParams(params, tradeStatus);
		final String baseUrl = params.get("return_url");
		return baseUrl + "?" + mockService.createLinkString(notify);
	}



	protected void setCSRFToken(final Map<String, String> params, final HttpServletRequest request)
	{
		final CSRFRequestDataValueProcessor proc = new CSRFRequestDataValueProcessor();
		final Map<String, String> csrfHiddenField = proc.getExtraHiddenFields(request);
		params.putAll(csrfHiddenField);
	}



}
