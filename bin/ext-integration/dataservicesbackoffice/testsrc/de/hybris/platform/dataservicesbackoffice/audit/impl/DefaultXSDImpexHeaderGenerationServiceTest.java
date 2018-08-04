/*
 * [y] hybris Platform
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package de.hybris.platform.dataservicesbackoffice.audit.impl;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map;

import javax.annotation.Resource;

import de.hybris.platform.audit.internal.config.AuditConfigService;
import de.hybris.platform.dataservicesbackoffice.audit.AuditReportXSDGenerationService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.dataservicesbackoffice.audit.ImpexHeaderGenerationService;
import de.hybris.platform.dataservicesbackoffice.audit.dto.TypeDto;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.impex.ImportConfig;
import de.hybris.platform.servicelayer.impex.ImportResult;
import de.hybris.platform.servicelayer.impex.ImportService;
import de.hybris.platform.servicelayer.impex.impl.StreamBasedImpExResource;
import de.hybris.platform.servicelayer.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@IntegrationTest
public class DefaultXSDImpexHeaderGenerationServiceTest extends ServicelayerBaseTest
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultXSDImpexHeaderGenerationServiceTest.class);
	private static final String TYPE_ADDRESS = "Address";
	private static final String TYPE_USER = "User";
	private static final String TYPE_TITLE = "Title";
	private static final String TYPE_MEDIA = "Media";
	private static final String TYPE_CURRENCY = "Currency";
	private static final String TYPE_USERGROUP = "UserGroup";
	private static final String UNIQUE_KEY = "[unique=true]";
	private static final String LANG_KEY = "[lang=en]";

	private static final String CONFIG_NAME = "userTestReport";
	private static final String CONFIG_FILE = "audit.test/dataservicesbackoffice-userreport-audit.xml";

	@Resource
	private ImpexHeaderGenerationService defaultXSDImpexHeaderGenerationService;

	@Resource
	private ImportService importService;

	@Resource
	private UserService userService;

	@Resource
	private AuditReportXSDGenerationService auditReportXSDGenerationService;

	@Resource
	private AuditConfigService auditConfigService;

	@Before
	public void setup() throws IOException
	{
		final String xml = IOUtils
				.toString(this.getClass().getClassLoader().getResourceAsStream(CONFIG_FILE), UTF_8);
		auditConfigService.storeConfiguration(CONFIG_NAME, xml);
	}

	@Test
	public void testGenerateXSDReportAgainstImpex() throws Exception
	{
		final Map<String, TypeDto> typeDto = defaultXSDImpexHeaderGenerationService
				.generateImpexHeaderFromXSD(new ByteArrayInputStream(
						auditReportXSDGenerationService.generate(CONFIG_NAME).getBytes()));

		final StringBuffer impex = new StringBuffer();

		impex.append("$catalog=catalogVersion(catalog(id),version)[unique=true,allownull=true]");

		typeDto.forEach((k, v) -> cookMockData(buildImpex(k, typeDto, impex), k));

		LOGGER.debug("Impex \n{}", impex);
		createImpex(impex);
		importImpexFile(this.getClass().getClassLoader().getResourceAsStream("report.impex"));
		final UserModel user = new UserModel();
		user.setUid("mockUser");
		final UserModel userPersisted = userService.getUserForUID("mockUUID");

		//User
		assertThat(userPersisted.getName()).isEqualTo("mockUser");
		assertThat(userPersisted.getUid()).isEqualTo("mockUUID");

		//Currency
		assertThat(userPersisted.getSessionCurrency().getIsocode()).isEqualTo("EUR");

		//Media
		assertThat(userPersisted.getProfilePicture().getCode()).isEqualTo("mockMedia");

		//UserGroup
		assertThat(userPersisted.getGroups().iterator().next().getUid()).isEqualTo("orgadmingroup");

		//Address
		assertThat(userPersisted.getDefaultPaymentAddress().getStreetname()).isEqualTo("mockStreet");
		assertThat(userPersisted.getDefaultPaymentAddress().getTown()).isEqualTo("mockTown");

		//Title
		assertThat(userPersisted.getDefaultPaymentAddress().getTitle().getName()).isEqualTo("Mock");

	}

	private StringBuffer buildImpex(final String type, final Map<String, TypeDto> typeDto, final StringBuffer impex)
	{
		impex.append("\n");
		impex.append("INSERT_UPDATE " + type + ";");
		doUserUsersGimmick(impex, type);

		typeDto.get(type).getElementList().forEach(ele ->
		{
			if (ele.getType() != null)
			{
				if (TYPE_ADDRESS.equals(type) && "owner".equals(ele.getName()))
				{
					impex.append(ele.getName() + "(&userId)");
				}
				else if (StringUtils.isNoneEmpty(getReference(typeDto.get(ele.getType()))))
				{
					impex.append(ele.getName() + "(" + getReference(typeDto.get(ele.getType())) + ")");
				}
			}
			else if (ele.isUnique() || ele.isLocalised())
			{
				if (ele.isUnique())
				{
					impex.append(ele.getName() + UNIQUE_KEY);
				}

				if (ele.isLocalised())
				{
					impex.append(ele.getName() + LANG_KEY);
				}
			}
			else
			{
				if (TYPE_ADDRESS.equals(type) && "streetname".equals(ele.getName()))
				{
					impex.append(ele.getName() + UNIQUE_KEY);
				}
				else
				{
					impex.append(ele.getName());
				}
			}

			impex.append(";");
		});

		doCataLogGimmick(impex, type);
		impex.append("\n");
		return impex;
	}

	private String getReference(final TypeDto typeDto)
	{
		final StringBuffer headerAttr = new StringBuffer();

		typeDto.getElementList().forEach(ele ->
		{
			if (ele.isUnique())
			{
				headerAttr.append(ele.getName());
			}
		});

		if (StringUtils.isEmpty(headerAttr))
		{
			headerAttr.append(typeDto.getElementList().get(0).getName());
		}

		return headerAttr.toString();
	}

	private void importImpexFile(final InputStream stream) throws Exception
	{
		final ImportConfig importConfig = new ImportConfig();
		importConfig.setScript(new StreamBasedImpExResource(stream, "UTF-8"));
		importConfig.setLegacyMode(false);
		importConfig.setValidationMode(ImportConfig.ValidationMode.STRICT);

		final ImportResult importResult = importService.importData(importConfig);
		if (importResult.isError())
		{
			fail("impexImport Failed");
		}
	}

	private void cookMockData(final StringBuffer impex, final String type)
	{
		if (TYPE_ADDRESS.equals(type))
		{
			impex.append(";mockStreet;mockTown;Mr;userId");
		}

		if (TYPE_USER.equals(type))
		{
			impex.append(";userId;mockUser;mockUUID;mockStreet;mockMedia;EUR;mockStreet;orgadmingroup");
		}

		if (TYPE_TITLE.equals(type))
		{
			impex.append(";Mr;Mock");
		}

		if (TYPE_MEDIA.equals(type))
		{
			impex.append(";mockMedia");
		}

		if (TYPE_CURRENCY.equals(type))
		{
			impex.append(";EUR;€");
		}

		if (TYPE_USERGROUP.equals(type))
		{
			impex.append(";orgadmingroup;Organization Admin;");
		}
	}

	private void doUserUsersGimmick(final StringBuffer impex, final String type)
	{
		if (TYPE_USER.equals(type))
		{
			impex.append("&userId;");
		}
	}

	private void doCataLogGimmick(final StringBuffer impex, final String type)
	{
		if (TYPE_MEDIA.equals(type))
		{
			impex.append("$catalog;");
		}
	}

	private void createImpex(StringBuffer content) throws Exception
	{
		final String dir = getClass().getResource("/").getFile();
		final OutputStream os = new FileOutputStream(dir + "/report.impex");
		final PrintStream printStream = new PrintStream(os);
		printStream.println(content);
		printStream.close();
	}
}
