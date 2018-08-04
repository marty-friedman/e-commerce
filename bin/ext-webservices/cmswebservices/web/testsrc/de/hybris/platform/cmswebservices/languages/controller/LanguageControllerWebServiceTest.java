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
package de.hybris.platform.cmswebservices.languages.controller;

import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cmsfacades.util.models.LanguageModelMother;
import de.hybris.platform.cmsfacades.util.models.SiteModelMother;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.LanguageListData;
import de.hybris.platform.cmswebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;


@NeedsEmbeddedServer(webExtensions =
{ CmswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class LanguageControllerWebServiceTest extends ApiBaseIntegrationTest
{
	private static final String INVALID = "invalid";
	private static final String GET_ALL_LANGUAGES = "/v1/sites/{siteId}/languages";

	@Resource
	private SiteModelMother siteModelMother;

	protected void createElectronicsSiteWithEmptyAppleCatalog()
	{
		siteModelMother.createElectronicsWithAppleCatalog();
	}

	@Test
	public void shouldGetAllLanguagesFromElectronicsSite() throws Exception
	{
		createElectronicsSiteWithEmptyAppleCatalog();

		final Map<String, String> uriVariables = new HashMap<>();
		uriVariables.put(CmswebservicesConstants.URI_SITE_ID, SiteModelMother.ELECTRONICS);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(GET_ALL_LANGUAGES, uriVariables)).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final LanguageListData entity = response.readEntity(LanguageListData.class);
		assertEquals(1, entity.getLanguages().size());
		assertEquals(LanguageModelMother.CODE_EN.toLowerCase(), entity.getLanguages().get(0).getIsocode());
	}

	@Test
	public void shouldReturnNotFoundResponseCode_InvalidSiteId() throws Exception
	{
		createElectronicsSiteWithEmptyAppleCatalog();

		final Map<String, String> uriVariables = new HashMap<>();
		uriVariables.put(CmswebservicesConstants.URI_SITE_ID, INVALID);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(GET_ALL_LANGUAGES, uriVariables)).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.NOT_FOUND, response);
	}
}
