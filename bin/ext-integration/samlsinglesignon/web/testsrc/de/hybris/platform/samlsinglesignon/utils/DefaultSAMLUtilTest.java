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
package de.hybris.platform.samlsinglesignon.utils;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.samlsinglesignon.constants.SamlsinglesignonConstants;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.util.Config;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.saml.SAMLCredential;

import java.util.Arrays;


@UnitTest
public class DefaultSAMLUtilTest extends ServicelayerTest
{

	private SAMLUtil samlUtil;

	private static final String SSO_USERID_KEY = "userIdKey";
	private static final String SSO_FIRSTNAME_KEY = "firstName";
	private static final String SSO_LASTNAME_KEY = "lastName";
	private static final String SSO_CUSTOM_KEY = "custom";

	@Before
	public void setup()
	{
		Config.setParameter(SamlsinglesignonConstants.SSO_USERID_KEY, SSO_USERID_KEY);
		Config.setParameter(SamlsinglesignonConstants.SSO_FIRSTNAME_KEY, SSO_FIRSTNAME_KEY);
		Config.setParameter(SamlsinglesignonConstants.SSO_LASTNAME_KEY, SSO_LASTNAME_KEY);

		samlUtil = new DefaultSAMLUtil();
	}

	@Test
	public void shouldGetUserId()
	{
		SAMLCredential credential = Mockito.mock(SAMLCredential.class);

		Mockito.when(credential.getAttributeAsString(SSO_USERID_KEY)).thenReturn("id");

		Assert.assertEquals("id", samlUtil.getUserId(credential));
	}

	@Test
	public void shouldReturnEmptyForGetUserIdWhenNoSSOKey()
	{
		SAMLCredential credential = Mockito.mock(SAMLCredential.class);

		Mockito.when(credential.getAttributeAsString(SSO_USERID_KEY)).thenReturn(null);

		Assert.assertEquals(StringUtils.EMPTY, samlUtil.getUserId(credential));
	}

	@Test
	public void shouldGetUserName()
	{
		SAMLCredential credential = Mockito.mock(SAMLCredential.class);

		Mockito.when(credential.getAttributeAsString(SSO_FIRSTNAME_KEY)).thenReturn("first");
		Mockito.when(credential.getAttributeAsString(SSO_LASTNAME_KEY)).thenReturn("last");

		Assert.assertEquals("first last", samlUtil.getUserName(credential));
	}

	@Test
	public void shouldGetCustomAttribute()
	{
		SAMLCredential credential = Mockito.mock(SAMLCredential.class);

		Mockito.when(credential.getAttributeAsString(SSO_CUSTOM_KEY)).thenReturn("custom_value");

		Assert.assertEquals("custom_value", samlUtil.getCustomAttribute(credential, SSO_CUSTOM_KEY));
	}

	@Test
	public void shouldGetCustomAttributesList()
	{
		SAMLCredential credential = Mockito.mock(SAMLCredential.class);

		Mockito.when(credential.getAttributeAsStringArray(SSO_CUSTOM_KEY)).thenReturn(new String[]{"custom_value1", "custom_value2"});

		Assert.assertEquals(Arrays.asList("custom_value1", "custom_value2"),
				samlUtil.getCustomAttributes(credential, SSO_CUSTOM_KEY));
	}

}
