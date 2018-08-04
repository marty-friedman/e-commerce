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
package de.hybris.platform.processengine.transformer.bpmnhybris.tests;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.processengine.definition.xml.Process;
import de.hybris.platform.processengine.transformer.bpmnhybris.BPMN2HybrisProcessTransformer;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.junit.Assert;
import org.junit.Test;


@IntegrationTest
public class BPMNProcess2HybrisProcessTest extends ServicelayerBaseTest
{

	@Resource
	BPMN2HybrisProcessTransformer bPMN2HybrisTransformer;

	@Test
	public void testSapReturnProcess() throws JAXBException, DatatypeConfigurationException, IOException
	{
		test("/test/sap-return-process.bpmn", "/test/sap-return-process.xml");
	}

	@Test
	public void testSapOmsOrderProcess() throws JAXBException, DatatypeConfigurationException, IOException
	{
		test("/test/sap-oms-order-process.bpmn", "/test/sap-oms-order-process.xml");
	}

	@Test
	public void testExclusive2Exclusive() throws JAXBException, DatatypeConfigurationException, IOException
	{
		test("/test/Exclusive2Exclusive2Join.bpmn", "/test/Exclusive2Exclusive2Join.xml");
	}

	@Test
	public void testSimpleTaskProcess() throws JAXBException, DatatypeConfigurationException, IOException
	{
		test("/test/Simple-Action-Process.bpmn", "/test/Simple-Action-Process.xml");
	}

	@Test
	public void testScriptTaskProcess() throws JAXBException, DatatypeConfigurationException, IOException
	{
		test("/test/Script-Task-Process.bpmn", "/test/Script-Task-Process.xml");
	}

	@Test
	public void testSimpleWaitProcess() throws JAXBException, DatatypeConfigurationException, IOException
	{
		test("/test/Simple-Wait-Process.bpmn", "/test/Simple-Wait-Process.xml");
	}

	@Test
	public void testWaitWithCaseProcess() throws JAXBException, DatatypeConfigurationException, IOException
	{
		test("/test/Wait-With-Case-Process.bpmn", "/test/Wait-With-Case-Process.xml");
	}

	@Test
	public void testJoinProcess() throws JAXBException, DatatypeConfigurationException, IOException
	{
		test("/test/Join-Process.bpmn", "/test/Join-Process.xml");
	}

	@Test
	public void testContextParameterProcess() throws JAXBException, DatatypeConfigurationException, IOException
	{
		test("/test/ContextParameter-Process.bpmn", "/test/ContextParameter-Process.xml");
	}

	@Test
	public void testActionProcess() throws JAXBException, DatatypeConfigurationException, IOException
	{
		test("/test/Simple-Action-Process.bpmn", "/test/Simple-Action-Process.xml");
	}

	@Test
	public void testWaitTimeoutProcess() throws JAXBException, DatatypeConfigurationException, IOException
	{
		test("/test/Wait-Timeout-Process.bpmn", "/test/Wait-Timeout-Process.xml");
	}

	@Test
	public void testWithoutDataObject() throws JAXBException, DatatypeConfigurationException, IOException
	{
		test("/test/WithoutDataObject.bpmn", "/test/WithoutDataObject.xml");
	}

	void test(final String bpmnProcessXmlFile, final String hybrisProcessXmlFile)
			throws JAXBException, DatatypeConfigurationException, IOException
	{
		// when
		final Process transformedHybrisProcess;
		try (final InputStream bpmnStream = this.getClass().getResourceAsStream(bpmnProcessXmlFile))
		{
			Assert.assertNotNull(bpmnProcessXmlFile + " doesnt exist", bpmnStream);
			transformedHybrisProcess = bPMN2HybrisTransformer.convertBpmnProcess2Hybris(bpmnStream);
		}

		// then
		final Process directHybrisProcess;
		try (final InputStream processStream = this.getClass().getResourceAsStream(hybrisProcessXmlFile))
		{
			Assert.assertNotNull(hybrisProcessXmlFile + " doesnt exist", processStream);
			directHybrisProcess = (Process) JAXBContext.newInstance(Process.class).createUnmarshaller()
					.unmarshal(new InputStreamReader(processStream, "UTF-8"));
		}

		Assert.assertTrue(new HybrisProcessComparisionUtil().compare(directHybrisProcess, transformedHybrisProcess));
	}
}
