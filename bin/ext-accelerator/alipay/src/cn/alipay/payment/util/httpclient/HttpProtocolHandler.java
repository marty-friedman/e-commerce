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
package cn.alipay.payment.util.httpclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.IdleConnectionEvictor;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import de.hybris.platform.chinaaccelerator.services.alipay.PaymentConstants;


public class HttpProtocolHandler
{
	private static Logger LOG = Logger.getLogger(HttpProtocolHandler.class);

	private static String DEFAULT_CHARSET = "GBK";

	private final int defaultConnectionTimeout = 8000;

	private final int defaultSoTimeout = 30000;

	private final int defaultIdleConnTimeout = 60000;

	private final int defaultMaxConnPerHost = 30;

	private final int defaultMaxTotalConn = 80;

	private static final int defaultHttpConnectionManagerTimeout = 3 * 1000;


	private final PoolingHttpClientConnectionManager connectionManager;

	private static HttpProtocolHandler httpProtocolHandler = new HttpProtocolHandler();


	public static HttpProtocolHandler getInstance()
	{
		return httpProtocolHandler;
	}


	private HttpProtocolHandler()
	{
		connectionManager = new PoolingHttpClientConnectionManager();
		connectionManager.setDefaultMaxPerRoute(defaultMaxConnPerHost);
		connectionManager.setMaxTotal(defaultMaxTotalConn);
		
		final IdleConnectionEvictor ice = new IdleConnectionEvictor(connectionManager, defaultIdleConnTimeout, TimeUnit.SECONDS);
		ice.start();
	}

	public HttpResponse execute(final HttpRequest request)
	{
		HttpClientBuilder builder = HttpClientBuilder.create();
		builder.setConnectionManager(connectionManager).setConnectionManagerShared(true);
		final String charset = StringUtils.isEmpty(request.getCharset()) ? DEFAULT_CHARSET : request.getCharset();

		HttpRequestBase httpRequest = null;
		if (request.getMethod().equals(HttpRequest.METHOD_GET))
		{
			httpRequest = new HttpGet(request.getUrl());
			httpRequest.getParams().setParameter("http.protocol.credential-charset", charset);
		}
		else
		{
			httpRequest = new HttpPost(request.getUrl());
			try
			{
				((HttpPost) httpRequest).setEntity(new UrlEncodedFormEntity(Arrays.asList(request.getParameters())));
			}
			catch (final UnsupportedEncodingException e)
			{
				LOG.warn("error on execute http request");
				return null;
			}
			httpRequest.addHeader("Content-Type", "application/x-www-form-urlencoded; text/html; charset=" + charset);

		}

		httpRequest.setConfig(getHttpRequestConfig(request));
		httpRequest.setHeader("User-Agent", "Mozilla/4.0");
		final HttpResponse response = new HttpResponse();

	

		try
		{
			final CloseableHttpClient httpclient = builder.build();
			final CloseableHttpResponse httpResponse = httpclient.execute(httpRequest);
			if (request.getResultType().equals(HttpResultType.STRING))
			{
				response.setStringResult(EntityUtils.toString(httpResponse.getEntity()));
			}
			else if (request.getResultType().equals(HttpResultType.BYTES))
			{
				response.setByteResult(EntityUtils.toByteArray(httpResponse.getEntity()));
			}
			response.setResponseHeaders(httpResponse.getAllHeaders());
			httpclient.close();
		}
		catch (final Exception ex)
		{
			LOG.warn("error on execute http request");
			return null;
		}
		finally
		{
			httpRequest.releaseConnection();
		}
		return response;
	}

	protected String toString(final NameValuePair[] nameValues)
	{
		if (nameValues == null || nameValues.length == 0)
		{
			return "null";
		}

		final StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < nameValues.length; i++)
		{
			final NameValuePair nameValue = nameValues[i];

			if (i == 0)
			{
				buffer.append(nameValue.getName() + "=" + nameValue.getValue());
			}
			else
			{
				buffer.append("&" + nameValue.getName() + "=" + nameValue.getValue());
			}
		}

		return buffer.toString();
	}
	
	protected RequestConfig getHttpRequestConfig(final HttpRequest request)
	{
		final int connectionTimeout = request.getConnectionTimeout() > 0 ? request.getConnectionTimeout(): defaultConnectionTimeout;
		final int soTimeout = request.getTimeout() > 0? request.getTimeout(): defaultSoTimeout;
		return RequestConfig.custom().setSocketTimeout(soTimeout).setConnectTimeout(connectionTimeout)
				.setConnectionRequestTimeout(defaultHttpConnectionManagerTimeout).build();
	}
}