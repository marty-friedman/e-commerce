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
package de.hybris.platform.cmswebservices.items.controller;

import static de.hybris.platform.cmsfacades.util.models.CMSSiteModelMother.TemplateSite.ELECTRONICS;
import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForPageModel;
import de.hybris.platform.cmsfacades.util.models.CMSSiteModelMother;
import de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentPageModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentSlotForPageModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentSlotModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentSlotNameModelMother;
import de.hybris.platform.cmsfacades.util.models.LinkComponentModelMother;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.AbstractCMSComponentData;
import de.hybris.platform.cmswebservices.data.CMSLinkComponentData;
import de.hybris.platform.cmswebservices.data.ComponentItemListData;
import de.hybris.platform.cmswebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBException;

import org.junit.Test;


@NeedsEmbeddedServer(webExtensions =
{ CmswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class ItemControllerLinkComponentWebServiceTest extends ApiBaseIntegrationTest
{
	private static final String ENDPOINT = "/v1/sites/{siteId}/catalogs/{catalogId}/versions/{versionId}/items";

	@Resource
	private CMSSiteModelMother cmsSiteModelMother;
	@Resource
	private CatalogVersionModelMother catalogVersionModelMother;
	@Resource
	private ContentSlotNameModelMother contentSlotNameModelMother;
	@Resource
	private ContentSlotForPageModelMother contentSlotForPageModelMother;
	@Resource
	private LinkComponentModelMother linkComponentModelMother;

	private CatalogVersionModel catalogVersion;

	protected void createElectronicsSiteAndAppleCatalog()
	{
		cmsSiteModelMother.createSiteWithTemplate(ELECTRONICS);
		catalogVersion = catalogVersionModelMother.createAppleStagedCatalogVersionModel();
	}

	protected void createElectronicsSiteAndWithLinkComponents()
	{
		createElectronicsSiteAndAppleCatalog();
		linkComponentModelMother.createCategoryLinkComponentModel(catalogVersion);
		linkComponentModelMother.createContentPageLinkComponentModel(catalogVersion);
		linkComponentModelMother.createProductLinkComponentModel(catalogVersion);
		linkComponentModelMother.createExternalLinkComponentModel(catalogVersion);
	}

	protected void createElectronicsSiteAndHomepageAppleCatalogPageFooterWithComponentTypeRestrictions()
	{
		createElectronicsSiteAndAppleCatalog();
		final ContentSlotForPageModel contentSlotForPage = contentSlotForPageModelMother.FooterHomepage_Empty(catalogVersion);

		// Create footer slot
		contentSlotNameModelMother.Footer(contentSlotForPage.getPage().getMasterTemplate());
	}

	@Test
	public void shouldGetAllLinkComponents()
	{
		createElectronicsSiteAndWithLinkComponents();

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final List<AbstractCMSComponentData> components = response.readEntity(ComponentItemListData.class).getComponentItems();
		assertThat(components,
				allOf(hasItem(hasProperty(CMSLinkComponentModel.UID, equalTo(LinkComponentModelMother.UID_CATEGORY_LINK))),
						hasItem(hasProperty(CMSLinkComponentModel.UID, equalTo(LinkComponentModelMother.UID_CONTENT_PAGE_LINK))),
						hasItem(hasProperty(CMSLinkComponentModel.UID, equalTo(LinkComponentModelMother.UID_PRODUCT_LINK))),
						hasItem(hasProperty(CMSLinkComponentModel.UID, equalTo(LinkComponentModelMother.UID_EXTERNAL_LINK)))));
	}

	@Test
	public void shouldGetOneLinkComponent()
	{
		createElectronicsSiteAndWithLinkComponents();

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())) //
				.path(LinkComponentModelMother.UID_CATEGORY_LINK).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final CMSLinkComponentData component = response.readEntity(CMSLinkComponentData.class);
		assertThat(component,
				allOf(
						hasProperty(CMSLinkComponentModel.UID, equalTo(LinkComponentModelMother.UID_CATEGORY_LINK)),
						hasProperty(CMSLinkComponentModel.CATEGORY, notNullValue()),
						hasProperty(CMSLinkComponentModel.LINKNAME, hasEntry("en", LinkComponentModelMother.NAME_CATEGORY_LINK)),
						hasProperty(CMSLinkComponentModel.EXTERNAL, is(false)),
						hasProperty(CMSLinkComponentModel.TARGET, is(false))));
	}

	@Test
	public void shouldRemoveOneLinkComponent()
	{
		createElectronicsSiteAndWithLinkComponents();

		final Response deleteResponse = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())) //
				.path(LinkComponentModelMother.UID_CATEGORY_LINK).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.delete();

		assertResponse(Status.NO_CONTENT, deleteResponse);

		final Response getResponse = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())) //
				.path(LinkComponentModelMother.UID_CATEGORY_LINK).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.NOT_FOUND, getResponse);
	}

	@Test
	public void shouldCreateExternalLinkComponent() throws JAXBException
	{
		createElectronicsSiteAndHomepageAppleCatalogPageFooterWithComponentTypeRestrictions();

		final CMSLinkComponentData component = new CMSLinkComponentData();
		component.setTypeCode(CMSLinkComponentModel._TYPECODE);
		component.setSlotId(ContentSlotModelMother.UID_FOOTER);
		component.setPosition(2);
		component.setPageId(ContentPageModelMother.UID_HOMEPAGE);
		component.setName("test-link-component");
		component.setUrl("http://help.hybris.com");
		component.setUid("uid-test-link");
		final Map<String, String> linkName = new HashMap<>();
		linkName.put("en", "name-test-link");
		component.setLinkName(linkName);
		component.setTarget(true);

		final Response createResponse = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(marshallDto(component, CMSLinkComponentData.class), MediaType.APPLICATION_JSON));

		assertResponse(Status.CREATED, createResponse);

		final Response getResponse = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())) //
				.path("uid-test-link").build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, getResponse);

		final CMSLinkComponentData entity = getResponse.readEntity(CMSLinkComponentData.class);
		assertThat(entity,
				allOf(hasProperty(CMSLinkComponentModel.UID, equalTo("uid-test-link")),
						hasProperty(CMSLinkComponentModel.URL, equalTo("http://help.hybris.com")),
						hasProperty(CMSLinkComponentModel.LINKNAME, hasEntry("en", "name-test-link")),
						hasProperty(CMSLinkComponentModel.EXTERNAL, is(true)), //
						hasProperty(CMSLinkComponentModel.TARGET, is(true))));
	}

	@Test
	public void shouldUpdateExternalLinkComponent() throws JAXBException
	{
		createElectronicsSiteAndWithLinkComponents();

		Response getResponse = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())) //
				.path(LinkComponentModelMother.UID_EXTERNAL_LINK).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, getResponse);

		final CMSLinkComponentData component = getResponse.readEntity(CMSLinkComponentData.class);
		component.setName("test-link-component");
		component.setUrl("http://help.hybris.com");
		component.setTarget(true);

		final Response updateResponse = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())) //
				.path(LinkComponentModelMother.UID_EXTERNAL_LINK).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.put(Entity.entity(marshallDto(component, CMSLinkComponentData.class), MediaType.APPLICATION_JSON));

		assertResponse(Status.NO_CONTENT, updateResponse);

		getResponse = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())) //
				.path(LinkComponentModelMother.UID_EXTERNAL_LINK).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, getResponse);

		final CMSLinkComponentData entity = getResponse.readEntity(CMSLinkComponentData.class);
		assertThat(entity,
				allOf(hasProperty(CMSLinkComponentModel.UID, equalTo(LinkComponentModelMother.UID_EXTERNAL_LINK)),
						hasProperty(CMSLinkComponentModel.URL, equalTo("http://help.hybris.com")),
						hasProperty(CMSLinkComponentModel.EXTERNAL, is(true)), //
						hasProperty(CMSLinkComponentModel.TARGET, is(true))));
	}

	@Test
	public void shouldUpdateToNewType() throws JAXBException
	{
		createElectronicsSiteAndWithLinkComponents();

		Response getResponse = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())) //
				.path(LinkComponentModelMother.UID_CATEGORY_LINK).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, getResponse);

		final CMSLinkComponentData component = getResponse.readEntity(CMSLinkComponentData.class);
		component.setCategory(null);
		component.setName("test-link-component");
		component.setUrl("http://help.hybris.com");
		component.setTarget(true);

		final Response updateResponse = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())) //
				.path(LinkComponentModelMother.UID_CATEGORY_LINK).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.put(Entity.entity(marshallDto(component, CMSLinkComponentData.class), MediaType.APPLICATION_JSON));

		assertResponse(Status.NO_CONTENT, updateResponse);

		getResponse = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())) //
				.path(LinkComponentModelMother.UID_CATEGORY_LINK).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, getResponse);

		final CMSLinkComponentData entity = getResponse.readEntity(CMSLinkComponentData.class);
		assertThat(entity,
				allOf(hasProperty(CMSLinkComponentModel.UID, equalTo(LinkComponentModelMother.UID_CATEGORY_LINK)),
						hasProperty(CMSLinkComponentModel.URL, equalTo("http://help.hybris.com")),
						hasProperty(CMSLinkComponentModel.EXTERNAL, is(true)), //
						hasProperty(CMSLinkComponentModel.TARGET, is(true))));
		assertNull( entity.getCategory() );
	}

}
