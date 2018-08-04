/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.dataservicesbackoffice.audit.impl;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.audit.internal.config.AuditConfigService;
import de.hybris.platform.audit.internal.config.AuditReportConfig;
import de.hybris.platform.dataservicesbackoffice.audit.AuditReportXSDGenerationService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@IntegrationTest
public class AuditReportXSDGenerationServiceIntegrationTest extends ServicelayerTest
{
	private static final Logger LOGGER = LoggerFactory.getLogger(AuditReportXSDGenerationServiceIntegrationTest.class);

	@Resource
	private AuditReportXSDGenerationService auditReportXSDGenerationService;
	@Resource
	private AuditConfigService auditConfigService;

	@Test
	public void shouldGenerateXSDFromAuditReportConfig() throws JAXBException, IOException
	{
		final String configName = "userTestReport";
		loadConfigFromFile(configName, "audit.test/dataservicesbackoffice-userreport-audit.xml");
		final String myUserReports = auditReportXSDGenerationService.generate(configName);
		LOGGER.info(myUserReports);

		assertThat(myUserReports).isXmlEqualTo(loadFile("audit.test/dataservicesbackoffice-userreport.xsd"));
	}

	@Test
	public void shouldGenerateXSDWithUniqueAttributeIfNoDefinedInAuditConfig() throws JAXBException, IOException
	{
		final String configName = "uniqueAttributeTest";
		loadConfigFromFile(configName, "audit.test/dataservicesbackoffice-uniqueattribute-audit.xml");
		final String xsd = auditReportXSDGenerationService.generate(configName);
		LOGGER.info(xsd);

		assertThat(xsd).isXmlEqualTo(loadFile("audit.test/dataservicesbackoffice-uniqueattribute.xsd"));
	}

	@Test
	public void shouldGenerateXSDWithMandatoryAttributeIfNoDefinedInAuditConfig() throws JAXBException, IOException
	{
		final String configName = "mandatoryAttributeTest";
		loadConfigFromFile(configName, "audit.test/dataservicesbackoffice-mandatoryattribute-audit.xml");
		final String xsd = auditReportXSDGenerationService.generate(configName);
		LOGGER.info(xsd);

		assertThat(xsd).isXmlEqualTo(loadFile("audit.test/dataservicesbackoffice-mandatoryattribute.xsd"));
	}

	protected AuditReportConfig loadConfigFromFile(final String configName, final String file) throws IOException
	{
		auditConfigService.storeConfiguration(configName, loadFile(file));
		return auditConfigService.getConfigForName(configName);
	}

	protected String loadFile(final String file) throws IOException
	{
		final InputStream is = this.getClass().getClassLoader().getResourceAsStream(file);
		return IOUtils.toString(is, UTF_8).replaceAll("\n", "").replaceAll("\r", "");
	}
}
