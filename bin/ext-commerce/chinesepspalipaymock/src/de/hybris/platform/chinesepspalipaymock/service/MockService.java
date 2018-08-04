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
package de.hybris.platform.chinesepspalipaymock.service;

import de.hybris.platform.chinesepaymentservices.order.service.ChineseOrderService;
import de.hybris.platform.chinesepspalipayservices.alipay.AlipayConfiguration;
import de.hybris.platform.chinesepspalipayservices.alipay.AlipayUtil;
import de.hybris.platform.chinesepspalipayservices.constants.PaymentConstants;
import de.hybris.platform.chinesepspalipayservices.data.AlipayRawDirectPayErrorInfo;
import de.hybris.platform.chinesepspalipayservices.data.AlipayRawDirectPayNotification;
import de.hybris.platform.chinesepspalipayservices.data.AlipayRefundData;
import de.hybris.platform.chinesepspalipayservices.data.AlipayRefundNotification;
import de.hybris.platform.chinesepspalipayservices.order.AlipayOrderService;
import de.hybris.platform.chinesepspalipayservices.strategies.AlipayHandleResponseStrategy;
import de.hybris.platform.chinesepspalipayservices.strategies.AlipayPaymentTransactionStrategy;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.enums.PaymentStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.strategies.impl.EventPublishingSubmitOrderStrategy;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.Config;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Deals with mock alipay service related models using existing DAOs
 */
public class MockService
{

	private static final Logger LOG = Logger.getLogger(MockService.class.getName());

	private static final String CHECK_RESPONSE = "chinesepspalipaymock.check.response";
	private static final String CHECK_TRADE_STATUS = "chinesepspalipaymock.check.tradestatus";

	private AlipayConfiguration alipayConfiguration;
	private AlipayPaymentTransactionStrategy alipayPaymentTransactionStrategy;
	private ModelService modelService;
	private AlipayOrderService alipayOrderService;
	private ChineseOrderService chineseOrderService;
	private AlipayHandleResponseStrategy alipayHandleResponseStrategy;
	private EventPublishingSubmitOrderStrategy eventPublishingSubmitOrderStrategy;

	/**
	 * Generates alipay mock URL's parameters
	 *
	 * @param params
	 *           the parameters used for generating URL's parameters
	 * @return alipay mock URL's parameters
	 */
	public String createLinkString(final Map<String, String> params)
	{
		return AlipayUtil.createLinkString(params);
	}

	/**
	 * Generates the notification map for notifying trade status
	 *
	 * @param params
	 *           the parameters used for generating notification map
	 * @param tradeStatus
	 *           the tradeStatus used for setting alipay trade status
	 * @return notification map
	 */
	public Map<String, String> getNotifyParams(final Map<String, String> params, final String tradeStatus)
	{
		final Map<String, String> notify = new HashMap<>(params);

		notify.remove("error_notify_url");
		notify.remove("notify_url");
		notify.remove("return_url");
		notify.remove("service");
		notify.put("trade_status", tradeStatus);
		notify.put("trade_no", params.get("out_trade_no"));
		notify.put("notify_id", getNotifyId());
		notify.put("notify_time", getNotifyTime());
		notify.put("notify_type", "trade_status_sync");
		notify.put("sign_type", "MD5");
		notify.put("sign", getSign(notify));

		return notify;
	}


	/**
	 * Generates the notification map for notifying error messages
	 *
	 * @param params
	 *           the parameters used for generating notification map
	 * @param errorCode
	 *           the error code used for setting alipay error messages
	 * @return notification map
	 */
	public Map<String, String> getNotifyErrorParams(final Map<String, String> params, final String errorCode)
	{
		final Map<String, String> notifyError = new HashMap<>();

		notifyError.put("notify_id", getNotifyId());
		notifyError.put("notify_time", getNotifyTime());
		notifyError.put("partner", params.get("partner"));
		notifyError.put("out_trade_no", params.get("out_trade_no"));
		notifyError.put("error_code", errorCode);
		notifyError.put("return_url", params.get("error_notify_url"));
		notifyError.put("buyer_id", params.get("buyer_id"));
		notifyError.put("seller_email", params.get("seller_email"));
		notifyError.put("seller_id", params.get("seller_id"));
		notifyError.put("sign_type", "MD5");
		notifyError.put("sign", getSign(notifyError));

		return notifyError;
	}


	/**
	 * Generates the notification map for returning trade status to storefront page
	 *
	 * @param params
	 *           the parameters used for generating notification map
	 * @param tradeStatus
	 *           the tradeStatus used for setting alipay trade status
	 * @return notification map
	 */
	public Map<String, String> getReturnParams(final Map<String, String> params, final String tradeStatus)
	{
		final Map<String, String> returnParams = new HashMap<>(params);

		returnParams.remove("error_notify_url");
		returnParams.remove("notify_url");
		returnParams.remove("return_url");
		returnParams.remove("service");
		returnParams.put("is_success", "T");
		returnParams.put("trade_status", tradeStatus);
		returnParams.put("notify_id", getNotifyId());
		returnParams.put("notify_time", getNotifyTime());
		returnParams.put("notify_type", "trade_status_sync");
		returnParams.put("trade_no", params.get("out_trade_no"));
		returnParams.put("sign_type", "MD5");
		returnParams.put("sign", getSign(returnParams));

		return returnParams;
	}

	/**
	 * Generates the refund notification map
	 *
	 * @param params
	 *           the parameters used for generating notification map
	 * @param errorCode
	 *           the error code used for setting refund error code
	 * @return notification map contain refund information
	 * @throws UnsupportedEncodingException
	 *            throw when encoding is not supported
	 */
	public Map<String, String> getRefundNotifyParams(final Map<String, String> params, final String errorCode)
			throws UnsupportedEncodingException
	{
		final Map<String, String> notify = new HashMap<>();
		notify.put("notify_time", getNotifyTime());
		notify.put("notify_type", "batch_refund_notify");
		notify.put("notify_id", getNotifyId());
		notify.put("sign_type", "MD5");

		notify.put("batch_no", params.get("batch_no"));
		notify.put("success_num", params.get("batch_num"));

		final String detailData = URLDecoder.decode(params.get("detail_data"), "UTF-8");
		final String refundAmount = StringUtils.split(detailData, "^")[1];
		final String baseResultDetails = StringUtils.split(detailData, "^")[0] + "^" + refundAmount + "^" + errorCode;
		final String refundAlipayAccount = "Mock@Alipay.com";
		final String refundAlipayId = "MockAlipayId";

		if ("SUCCESS".equalsIgnoreCase(errorCode))
		{
			notify.put("result_details", baseResultDetails + "$" + refundAlipayAccount + "^" + refundAlipayId + "^0.1^" + errorCode);
		}
		else
		{
			notify.put("result_details", baseResultDetails);
		}

		notify.put("sign", getSign(notify));

		return notify;
	}


	/**
	 * Generates the payment response body
	 *
	 * @param params
	 *           the parameters used for generating payment response body
	 * @return the payment response body
	 */
	public String getPaymentStatusRequest(final Map<String, String> params)
	{
		final String tradeStatus = Config.getParameter(CHECK_TRADE_STATUS);
		final String outTradeNo = params.get("out_trade_no");
		final String totalFee = Registry.getMasterTenant().getConfig().getString("test.amount", "0.01");
		final String tradeNo = params.get("out_trade_no");


		final Map<String, String> trade = new HashMap<>();
		trade.put("out_trade_no", outTradeNo);
		trade.put("trade_status", tradeStatus);
		trade.put("total_fee", totalFee);

		final String signTrade = getSign(trade);
		final StringBuilder result = new StringBuilder();
		final boolean tradeSuccess = Boolean.parseBoolean(Config.getParameter(CHECK_RESPONSE));
		if (tradeSuccess)
		{
			result.append("<?xml version=\"1.0\" encoding=\"utf-8\"?><alipay><is_success>T</is_success>");
			result.append("<request><param name=\"_input_charset\">utf-8</param><param name=\"service\">single_trade_query</param><param name=\"partner\">"
					+ xssEncode(alipayConfiguration.getWapPartner())
					+ "</param><param name=\"out_trade_no\">"
					+ xssEncode(outTradeNo)
					+ "</param></request>");

			result.append("<response><trade>");
			result.append("<out_trade_no>" + xssEncode(outTradeNo) + "</out_trade_no>");
			result.append("<trade_no>" + xssEncode(tradeNo) + "</trade_no>");
			result.append("<trade_status>" + xssEncode(tradeStatus) + "</trade_status>");
			result.append("<total_fee>" + xssEncode(totalFee) + "</total_fee>");
			result.append("</trade></response>");
			result.append("<sign_type>MD5</sign_type>");
			result.append("<sign>" + signTrade + "</sign>");
			result.append("</alipay>");
		}
		else
		{
			result.append("<?xml version=\"1.0\" encoding=\"utf-8\"?><alipay><is_success>F</is_success>");
			result.append("<error>TRADE_IS_NOT_EXIST</error>");
			result.append("</alipay>");
		}
		return result.toString();
	}


	public String getCancelPaymentRequest()
	{
		return "<?xml version=\"1.0\" encoding=\"utf-8\"?><alipay><is_success>T</is_success></alipay>";
	}

	/**
	 * Generates signature
	 *
	 * @param Params
	 *           the parameters used for generating signature
	 * @return the signature
	 */
	public String getSign(final Map<String, String> Params)
	{
		final String key = alipayConfiguration.getWebKey();
		final Map<String, String> sParaNew = AlipayUtil.paraFilter(Params);
		stripOffCSRFToken(sParaNew);
		return AlipayUtil.buildMysign(sParaNew, key, "MD5");
	}

	/**
	 * Removes the CSRF token
	 *
	 * @param params
	 *           the parameters used for removing the CSRF token
	 * @return the parameters without the CSRF token
	 */
	public void stripOffCSRFToken(final Map<String, String> params)
	{
		if (params != null)
		{
			params.remove("CSRFToken");
		}
	}

	/**
	 * Gets order by order code
	 *
	 * @param orderCode
	 *           order code
	 * @return the OrderModel
	 */
	public OrderModel getOrderModelByCode(final String orderCode)
	{
		final OrderModel orderModel = getAlipayOrderService().getOrderByCode(orderCode);
		if (orderModel == null)
		{
			throw new UnknownIdentifierException("Order not found for current user in current BaseStore");
		}

		return orderModel;

	}

	/**
	 * Mocks handling of notification request
	 *
	 * @param unifyResponseMap
	 *           the mocked response from alipay
	 * @return the order code
	 */
	public String handleNotifyRequest(final Map<String, String> unifyResponseMap)
	{
		String orderCode = null;
		try
		{
			orderCode = handleNotification(unifyResponseMap);
		}
		catch (final IOException e) //NOSONAR
		{
			LOG.error("Problem in handling Alipay's direct pay notify message");
		}
		return orderCode;
	}

	/**
	 * Mocks handling of refund request
	 *
	 * @param unifyResponseMap
	 *           the mock response from alipay
	 * @return the order code
	 */
	public String handleRefundRequest(final Map<String, String> unifyResponseMap)
	{
		final String orderCode = null;

		try
		{
			handleRefundNotification(unifyResponseMap);
		}
		catch (final IOException e) //NOSONAR
		{
			LOG.error("Problem in handling Alipay's refund notify message");
		}
		return orderCode;
	}

	/**
	 * Mocks handling of notification error request
	 *
	 * @param responseMap
	 *           the mock response from alipay
	 */
	public void handleNotifyErrorRequest(final Map<String, String> responseMap)
	{
		final String orderCode = responseMap.get(PaymentConstants.ErrorHandler.OUT_TRADE_NO);
		final OrderModel orderModel = alipayOrderService.getOrderByCode(orderCode);

		final Map<String, String> camelCaseMap = convertKey2CamelCase(responseMap);

		final AlipayRawDirectPayErrorInfo alipayRawDirectPayErrorInfo = new AlipayRawDirectPayErrorInfo();

		try
		{
			BeanUtils.populate(alipayRawDirectPayErrorInfo, camelCaseMap);

			getAlipayPaymentTransactionStrategy().updateForError(orderModel, alipayRawDirectPayErrorInfo);
		}
		catch (IllegalAccessException | InvocationTargetException e)//NOSONAR
		{
			LOG.error("Handle error notify failes");
		}
	}

	/**
	 * Mocks handling of submitting order event
	 *
	 * @param orderCode
	 *           the order code
	 */
	public void publishSubmitOrderEvent(final String orderCode)
	{

		final OrderModel orderModel = getOrderModelByCode(orderCode);
		if (orderModel == null)
		{
			LOG.error("cannot get order when publish submit order event");
		}
		else
		{
			if (OrderStatus.CREATED.equals(orderModel.getStatus()))
			{
				getEventPublishingSubmitOrderStrategy().submitOrder(orderModel);
			}
		}
	}

	protected String handleNotification(final Map<String, String> responseMap) throws IOException
	{
		final AlipayRawDirectPayNotification alipayRawDirectPayNotification = new AlipayRawDirectPayNotification();
		final Map<String, String> camelCaseMap = convertKey2CamelCase(responseMap);
		try
		{
			BeanUtils.populate(alipayRawDirectPayNotification, camelCaseMap);
		}
		catch (IllegalAccessException | InvocationTargetException e)//NOSONAR
		{
			LOG.error("Handle notify info from Alipay fails");
		}
		final TransactionStatus transactionStatus = PaymentConstants.TransactionStatusMap.getAlipaytohybris().get(
				alipayRawDirectPayNotification.getTradeStatus());
		if (null == transactionStatus)
		{
			LOG.warn("no TransactionStatus found, no payment transaction be created, WAIT_BUYER_PAY: Transaction awaits user payment.");
			return null;
		}
		final String tradeStatus = transactionStatus.name();
		final String orderCode = alipayRawDirectPayNotification.getOutTradeNo();
		final OrderModel orderModel = getOrderModelByCode(orderCode);
		getAlipayPaymentTransactionStrategy().updateForNotification(orderModel, alipayRawDirectPayNotification);

		if (TransactionStatus.ACCEPTED.name().equals(tradeStatus) || TransactionStatus.REVIEW.name().equals(tradeStatus)
				|| TransactionStatus.FINISHED.name().equals(tradeStatus))
		{
			orderModel.setPaymentStatus(PaymentStatus.PAID);
		}
		else
		{
			if (TransactionStatus.CLOSED.name().equals(tradeStatus) && !PaymentStatus.PAID.equals(orderModel.getPaymentStatus())
					&& !OrderStatus.COMPLETED.equals(orderModel.getStatus()))
			{
				orderModel.setStatus(OrderStatus.CANCELLED);
			}
			else
			{
				orderModel.setPaymentStatus(PaymentStatus.NOTPAID);
			}
		}

		getModelService().save(orderModel);

		return orderModel.getCode();
	}

	protected Map<String, String> convertKey2CamelCase(final Map<String, String> snakeCaseMap)
	{
		final Map<String, String> camelCaseMap = new LinkedHashMap<>();
		for (final Map.Entry<String, String> entry : snakeCaseMap.entrySet())
		{
			final String value = entry.getValue();
			final String key = entry.getKey();
			String camelKey = WordUtils.capitalizeFully(key, new char[]
			{ '_' }).replaceAll("_", "");
			camelKey = WordUtils.uncapitalize(camelKey);
			camelCaseMap.put(camelKey, value);
		}
		return camelCaseMap;
	}

	protected void handleRefundNotification(final Map<String, String> responseMap) throws IOException
	{
		AlipayRefundNotification alipayRefundNotification = new AlipayRefundNotification();
		alipayRefundNotification = (AlipayRefundNotification) getAlipayHandleResponseStrategy().camelCaseFormatter(responseMap,
				alipayRefundNotification);
		final List<AlipayRefundData> alipayRefundData = getAlipayHandleResponseStrategy().getAlipayRefundDataList(
				alipayRefundNotification);
		final Map<OrderModel, Boolean> refundStatus = alipayPaymentTransactionStrategy
				.updateForRefundNotification(alipayRefundData);


		refundStatus.keySet().forEach((orderModel) -> {
			if (refundStatus.get(orderModel))
			{
				orderModel.setPaymentStatus(PaymentStatus.REFUNDED);
			}
			getChineseOrderService().updateOrderForRefund(orderModel, refundStatus.get(orderModel));
		});
	}

	protected static String xssEncode(final String value)
	{
		return (value == null) ? null : xssfilter(value);
	}

	protected static String xssfilter(final String value)
	{
		if (value == null)
		{
			return null;
		}
		String sanitized = value;
		sanitized = sanitized.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
		sanitized = sanitized.replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");
		sanitized = sanitized.replaceAll("'", "&#39;");
		sanitized = sanitized.replaceAll("eval\\((.*)\\)", "");
		sanitized = sanitized.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
		return sanitized;
	}



	protected static String getNotifyTime()
	{
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}

	protected static String getNotifyId()
	{
		return AlipayUtil.encrypt("MD5", String.valueOf(System.currentTimeMillis()));
	}

	protected static List<NameValuePair> generateNameValuePair(final Map<String, String> properties)
	{
		final List<NameValuePair> nameValuePair = new ArrayList<>();
		for (final Map.Entry<String, String> entry : properties.entrySet())
		{
			nameValuePair.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}

		return nameValuePair;
	}

	@Required
	public void setAlipayConfiguration(final AlipayConfiguration alipayConfiguration)
	{
		this.alipayConfiguration = alipayConfiguration;
	}

	protected AlipayConfiguration getAlipayConfiguration()
	{
		return alipayConfiguration;
	}

	protected AlipayPaymentTransactionStrategy getAlipayPaymentTransactionStrategy()
	{
		return alipayPaymentTransactionStrategy;
	}

	@Required
	public void setAlipayPaymentTransactionStrategy(final AlipayPaymentTransactionStrategy alipayPaymentTransactionStrategy)
	{
		this.alipayPaymentTransactionStrategy = alipayPaymentTransactionStrategy;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected AlipayOrderService getAlipayOrderService()
	{
		return alipayOrderService;
	}

	@Required
	public void setAlipayOrderService(final AlipayOrderService alipayOrderService)
	{
		this.alipayOrderService = alipayOrderService;
	}

	protected ChineseOrderService getChineseOrderService()
	{
		return chineseOrderService;
	}

	@Required
	public void setChineseOrderService(final ChineseOrderService chineseOrderService)
	{
		this.chineseOrderService = chineseOrderService;
	}

	protected AlipayHandleResponseStrategy getAlipayHandleResponseStrategy()
	{
		return alipayHandleResponseStrategy;
	}

	@Required
	public void setAlipayHandleResponseStrategy(final AlipayHandleResponseStrategy alipayHandleResponseStrategy)
	{
		this.alipayHandleResponseStrategy = alipayHandleResponseStrategy;
	}

	protected EventPublishingSubmitOrderStrategy getEventPublishingSubmitOrderStrategy()
	{
		return eventPublishingSubmitOrderStrategy;
	}

	@Required
	public void setEventPublishingSubmitOrderStrategy(final EventPublishingSubmitOrderStrategy eventPublishingSubmitOrderStrategy)
	{
		this.eventPublishingSubmitOrderStrategy = eventPublishingSubmitOrderStrategy;
	}

}
