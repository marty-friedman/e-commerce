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
package com.hybris.instore.widgets;

import org.springframework.web.context.WebApplicationContext;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.http.ExecutionImpl;
import org.zkoss.zk.ui.sys.ExecutionsCtrl;
import org.zkoss.zk.ui.sys.WebAppCtrl;

import javax.servlet.ServletContext;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

public class ZkMockingTools
{
	private ZkMockingTools()
	{
	}

	/**
	 * Mock Zk environment
	 */
	public static void mockZkEnvironment()
	{
		final Execution execMock = mock(ExecutionImpl.class);
		final Desktop deskMock = mock(Desktop.class);
		when(execMock.getDesktop()).thenReturn(deskMock);
		final WebApp webAppMock = mock(WebApp.class, withSettings().extraInterfaces(WebAppCtrl.class));
		when(deskMock.getWebApp()).thenReturn(webAppMock);
		final ServletContext scMock = mock(ServletContext.class);
		when(webAppMock.getNativeContext()).thenReturn(scMock);
		when(webAppMock.getServletContext()).thenReturn(scMock);
		final WebApplicationContext wacMock = mock(WebApplicationContext.class);
		when(scMock.getAttribute(anyString())).thenReturn(wacMock);
		ExecutionsCtrl.setCurrent(execMock);
	}
}
