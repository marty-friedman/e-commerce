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
import de.hybris.platform.processengine.transformer.bpmnhybris.Hybris2BPMNProcessTransformer;
import de.hybris.platform.processengine.transformer.bpmnhybris.model.Definitions;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.junit.Assert;
import org.junit.Test;

import www.omg.org.spec.bpmn.model.TProcess;
import www.omg.org.spec.bpmn.model.TRootElement;


@IntegrationTest
public class Hybris2BPMAndBack2HybrisTest extends ServicelayerBaseTest
{
	@Resource
	Hybris2BPMNProcessTransformer hybris2BPMNTransformer;

	@Resource
	BPMN2HybrisProcessTransformer bPMN2HybrisTransformer;

	@Test
	public void testSapReturnProces() throws JAXBException, DatatypeConfigurationException, IOException
	{
		test("/test/sap-return-process.xml");
	}

	@Test
	public void testSapOmsOrderProces() throws JAXBException, DatatypeConfigurationException, IOException
	{
		test("/test/sap-oms-order-process.xml");
	}

	@Test
	public void testSimpleTaskProcess() throws JAXBException, DatatypeConfigurationException, IOException
	{
		test("/test/Script-Task-Process.xml");
	}

	@Test
	public void testSimpleWaitProcess() throws JAXBException, DatatypeConfigurationException, IOException
	{
		test("/test/Simple-Wait-Process.xml");
	}

	@Test
	public void testWaitWithCaseProcess() throws JAXBException, DatatypeConfigurationException, IOException
	{
		test("/test/Wait-With-Case-Process.xml");
	}

	@Test
	public void testJoinProcess() throws JAXBException, DatatypeConfigurationException, IOException
	{
		test("/test/Join-Process.xml");
	}

	@Test
	public void testContextParameterProcess() throws JAXBException, DatatypeConfigurationException, IOException
	{
		test("/test/ContextParameter-Process.xml");
	}

	@Test
	public void testActionProcess() throws JAXBException, DatatypeConfigurationException, IOException
	{
		test("/test/Simple-Action-Process.xml");
	}

	@Test
	public void testWaitTimeoutProcess() throws JAXBException, DatatypeConfigurationException, IOException
	{
		test("/test/Wait-Timeout-Process.xml");
	}

	@Test
	public void testWithoutDataObject() throws JAXBException, DatatypeConfigurationException, IOException
	{
		test("/test/WithoutDataObject.xml");
	}

	void test(final String hybrisProcessXmlFile) throws JAXBException, DatatypeConfigurationException, IOException
	{
		// when
		final Definitions definitions;
		try (final InputStream is = this.getClass().getResourceAsStream(hybrisProcessXmlFile))
		{
			Assert.assertNotNull(hybrisProcessXmlFile + " doesn't exist", is);
			definitions = hybris2BPMNTransformer.getProcess(new InputStreamReader(is, "UTF-8"));
		}

		final JAXBElement<? extends TRootElement> rootElements = definitions.getRootElement().get(0);
		final TProcess process = (TProcess) rootElements.getValue();


		// then
		final Process transformedHybrisProcess = bPMN2HybrisTransformer.generateHybrisProcess(process);
		final Process directHybrisProcess;
		try (final InputStream is = this.getClass().getResourceAsStream(hybrisProcessXmlFile))
		{
			Assert.assertNotNull(hybrisProcessXmlFile + " doesn't exist", is);
			directHybrisProcess = (Process) JAXBContext.newInstance(Process.class).createUnmarshaller()
					.unmarshal(new InputStreamReader(is, "UTF-8"));
		}

		Assert.assertTrue(new HybrisProcessComparisionUtil().compare(directHybrisProcess, transformedHybrisProcess));
	}
}
