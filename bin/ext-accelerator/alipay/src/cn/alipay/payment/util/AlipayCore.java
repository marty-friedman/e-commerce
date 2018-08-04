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
package cn.alipay.payment.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

public class AlipayCore
{
	private static Logger LOG = Logger.getLogger(AlipayCore.class.getName());

	public static String buildMysign(final Map<String, String> sArray, final String key)
	{
		String prestr = createLinkString(sArray);
		prestr = prestr + key;
		final String mysign = AlipayMd5Encrypt.md5(prestr);
		return mysign;
	}

	public static Map<String, String> paraFilter(final Map<String, String> sArray)
	{

		final Map<String, String> result = new HashMap<String, String>();

		if (sArray == null || sArray.size() <= 0)
		{
			return result;
		}

		for (final String key : sArray.keySet())
		{
			final String value = sArray.get(key);
			if (value == null || value.equals("") || key.equalsIgnoreCase("sign") || key.equalsIgnoreCase("sign_type"))
			{
				continue;
			}
			result.put(key, value);
		}

		return result;
	}

	public static String createLinkString(final Map<String, String> params)
	{

		final List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);

		String prestr = "";

		for (int i = 0; i < keys.size(); i++)
		{
			final String key = keys.get(i);
			final String value = params.get(key);

			if (i == keys.size() - 1)
			{
				prestr = prestr + key + "=" + value;
			}
			else
			{
				prestr = prestr + key + "=" + value + "&";
			}
		}

		return prestr;
	}

	public static void logResult(final String sWord)
	{
		FileWriter writer = null;
		try
		{
			writer = new FileWriter("D:/alipay.log");
			writer.write(sWord);
		}
		catch (final IOException e)
		{
			LOG.error(e.getMessage(),e);
		}
		finally
		{
			if (writer != null)
			{
				try
				{
					writer.close();
				}
				catch (final IOException e)
				{
					LOG.error("Close writer fails");
				}
			}
		}
	}
}
