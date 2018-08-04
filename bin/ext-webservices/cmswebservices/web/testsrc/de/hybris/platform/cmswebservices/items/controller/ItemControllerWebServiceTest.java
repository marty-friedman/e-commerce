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
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.cms2.model.contents.components.CMSParagraphComponentModel;
import de.hybris.platform.cms2.model.contents.containers.ABTestCMSComponentContainerModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cms2lib.model.components.FlashComponentModel;
import de.hybris.platform.cms2lib.model.components.ProductListComponentModel;
import de.hybris.platform.cmsfacades.items.validator.CreateComponentValidator;
import de.hybris.platform.cmsfacades.util.models.ABTestCMSComponentContainerModelMother;
import de.hybris.platform.cmsfacades.util.models.BaseStoreModelMother;
import de.hybris.platform.cmsfacades.util.models.CMSNavigationNodeModelMother;
import de.hybris.platform.cmsfacades.util.models.CMSSiteModelMother;
import de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentSlotForPageModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentSlotModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentSlotNameModelMother;
import de.hybris.platform.cmsfacades.util.models.FlashComponentModelMother;
import de.hybris.platform.cmsfacades.util.models.LanguageModelMother;
import de.hybris.platform.cmsfacades.util.models.LinkComponentModelMother;
import de.hybris.platform.cmsfacades.util.models.MediaFormatModelMother;
import de.hybris.platform.cmsfacades.util.models.MediaModelMother;
import de.hybris.platform.cmsfacades.util.models.PageTemplateModelMother;
import de.hybris.platform.cmsfacades.util.models.ParagraphComponentModelMother;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.AbstractCMSComponentData;
import de.hybris.platform.cmswebservices.data.CMSParagraphComponentData;
import de.hybris.platform.cmswebservices.data.ComponentItemListData;
import de.hybris.platform.cmswebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


@NeedsEmbeddedServer(webExtensions =
{ CmswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class ItemControllerWebServiceTest extends ApiBaseIntegrationTest
{
	private static final String ENDPOINT = "/v1/sites/{siteId}/catalogs/{catalogId}/versions/{versionId}/items";
	private static final String UIDS_REQUEST_PARAMETER = "uids";

	private static final String PAGE_ID = "uid-homepage";
	private static final String INVALID_PAGE_ID = "invalid_uid-homepage";
	private static final String INVALID_COMPONENT_ID = "invalid_uid-component";
	private static final Integer SLOT_POSITION = 2;

	private static final String NEW_CONTENT_HEADER_EN = "new-content-header";
	private static final String NEW_NAME_HEADER = "new-name-header";

	private static final String UID = "uid";
	private static final String INVALID_UID = "invalid_uid";

	private static final String PAGE_SIZE_PARAMETER = "pageSize";
	private static final String CURRENT_PAGE_PARAMETER = "currentPage";
	private static final String MASK_PARAMETER = "mask";
	private static final String SORT_PARAMETER = "sort";

	private static final String MEDIA_WIDESCREEN = "media-widescreen";
	private static final String MEDIA_DESKTOP = "media-desktop";
	private static final String MEDIA_TABLET = "media-tablet";
	private static final String MEDIA_MOBILE = "media-mobile";
	private static final String NAVIGATION_NODE_NAME = "navigation-node-name";
	private static final String NAVIGATION_NODE_UID = "navigation-node-uid";
	private static final String NAVIGATION_NODE_TITLE = "navigation-node-title";

	@Resource
	private CatalogVersionModelMother catalogVersionModelMother;
	@Resource
	private ContentSlotModelMother contentSlotModelMother;
	@Resource
	private ParagraphComponentModelMother paragraphComponentModelMother;
	@Resource
	private LinkComponentModelMother linkComponentModelMother;
	@Resource
	private ContentSlotForPageModelMother contentSlotForPageModelMother;
	@Resource
	private PageTemplateModelMother pageTemplateModelMother;
	@Resource
	private ContentSlotNameModelMother contentSlotNameModelMother;
	@Resource
	private BaseStoreModelMother baseStoreModelMother;
	@Resource
	private MediaFormatModelMother mediaFormatModelMother;
	@Resource
	private LanguageModelMother languageModelMother;
	@Resource
	private MediaModelMother mediaModelMother;
	@Resource
	private CMSSiteModelMother cmsSiteModelMother;
	@Resource
	private CMSNavigationNodeModelMother navigationNodeModelMother;

	private CatalogVersionModel catalogVersion;

	protected void createElectronicsSite()
	{
		cmsSiteModelMother.createSiteWithTemplate(ELECTRONICS);
	}

	protected void createEmptyAppleCatalog()
	{
		catalogVersion = catalogVersionModelMother.createAppleStagedCatalogVersionModel();
	}

	protected void createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraphAndLink()
	{
		createElectronicsSite();
		createEmptyAppleCatalog();
		contentSlotModelMother.createHeaderSlotWithParagraphAndLink(catalogVersion);
	}

	protected void createPagesOfComponents()
	{
		createElectronicsSite();
		createEmptyAppleCatalog();
		contentSlotModelMother.createPagesOfComponents(catalogVersion);
	}

	protected void createElectronicsSiteAndHomeAppleCatalogPageHeaderWithABTestContainer()
	{
		createElectronicsSite();
		createEmptyAppleCatalog();
		contentSlotModelMother.createHeaderSlotWithABTestParagraphsContainer(catalogVersion);
	}

	protected void createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraphWithoutRestriction()
	{
		// Create Electronics site to associate with Apple catalog
		createElectronicsSite();
		// Create empty Apple catalog
		createEmptyAppleCatalog();
		// Create homepage template
		final PageTemplateModel pageTemplate = pageTemplateModelMother.HomePage_Template(catalogVersion);
		// Create homepage page and content slot header
		contentSlotForPageModelMother.HeaderHomepage_ParagraphOnly(catalogVersion);
		// Create header content slot name with paragraph and no type restriction
		contentSlotNameModelMother.Header_without_restriction(pageTemplate);
	}

	protected void createElectronicsSiteAndHomeAppleCatalogPageHeaderWithFlashComponentWithoutRestriction()
	{
		// Create Electronics site to associate with Apple catalog
		createElectronicsSite();
		// Create empty Apple catalog
		createEmptyAppleCatalog();
		// Create homepage template
		final PageTemplateModel pageTemplate = pageTemplateModelMother.HomePage_Template(catalogVersion);
		// Create homepage page and content slot header
		contentSlotForPageModelMother.HeaderHomepage_FlashComponentOnly(catalogVersion);
		// Create header content slot name with link and no type restriction
		contentSlotNameModelMother.Header_without_restriction(pageTemplate);
	}

	protected void createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraph_WithTypeRestrictions()
	{
		// Create Electronics site to associate with Apple catalog
		createElectronicsSite();
		// Create empty Apple catalog
		createEmptyAppleCatalog();
		// Create homepage template
		final PageTemplateModel pageTemplate = pageTemplateModelMother.HomePage_Template(catalogVersion);
		// Create homepage page and content slot header
		contentSlotForPageModelMother.HeaderHomepage_ParagraphOnly(catalogVersion);
		// Create header content slot name with paragraph type restrictions
		contentSlotNameModelMother.Header(pageTemplate);
	}

	protected void createNavigationNodes()
	{
		final CMSNavigationNodeModel root = navigationNodeModelMother.createNavigationRootNode(catalogVersion);

		navigationNodeModelMother.createNavigationNode(NAVIGATION_NODE_NAME, NAVIGATION_NODE_UID, root, NAVIGATION_NODE_TITLE,
				catalogVersion);
	}

	@Test
	public void shouldGetAllItemsAndAllItemsArePresent_NoContainer() throws Exception
	{
		createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraphAndLink();

		final List<AbstractCMSComponentData> entities = executeGetAllItems();
		assertEquals(2, entities.size());

		final AbstractCMSComponentData headerParagraph = getComponentItemByUid(entities, ParagraphComponentModelMother.UID_HEADER);
		assertEquals(CMSParagraphComponentModel._TYPECODE, headerParagraph.getTypeCode());
		assertEquals(ParagraphComponentModelMother.UID_HEADER, headerParagraph.getUid());
		assertEquals(ParagraphComponentModelMother.NAME_HEADER, headerParagraph.getName());
		Assert.assertNull(headerParagraph.getSlotId());

		final AbstractCMSComponentData headerLink = getComponentItemByUid(entities,
				LinkComponentModelMother.UID_EXTERNAL_LINK);
		assertEquals(CMSLinkComponentModel._TYPECODE, headerLink.getTypeCode());
		assertEquals(LinkComponentModelMother.UID_EXTERNAL_LINK, headerLink.getUid());
		Assert.assertNull(headerLink.getSlotId());
	}

	@Test
	public void whenNoFitlerAndSortingByNameWillReturnFullFirstPage()
	{
		createPagesOfComponents();

		final ComponentItemListData page = executeGetPageOfItems(null, 2, 0, AbstractCMSComponentModel.NAME);
		final List<AbstractCMSComponentData> results = page.getComponentItems();
		assertThat("page 0 (no filter, name sort) failed to return a full page", results.size(), is(2));
		assertThat("page 0 (no filter, name sort) failed to return the maximum number of results",
				page.getPagination().getTotalCount(), is(5L));

		final String[] orderedUids = results.stream().map(e -> e.getUid()).collect(toList()).toArray(new String[]{});
		assertThat(orderedUids, Matchers.arrayContaining(ParagraphComponentModelMother.UID_HEADER+2,ParagraphComponentModelMother.UID_HEADER+5));

	}

	@Test
	public void whenNoFitlerAndSortingByNameWillReturnFullSecondPage()
	{
		createPagesOfComponents();

		final ComponentItemListData page = executeGetPageOfItems(null, 2, 1, AbstractCMSComponentModel.NAME);
		final List<AbstractCMSComponentData> results = page.getComponentItems();
		assertThat("page 1 (no filter, name sort) failed to return a full page", results.size(), is(2));
		assertThat("page 1 (no filter, name sort) failed to return the maximum number of results",
				page.getPagination().getTotalCount(), is(5L));

		final String[] orderedUids = results.stream().map(e -> e.getUid()).collect(toList()).toArray(new String[]{});
		assertThat(orderedUids, Matchers.arrayContaining(ParagraphComponentModelMother.UID_HEADER+4,ParagraphComponentModelMother.UID_HEADER+1));
	}

	@Test
	public void whenNoFitlerAndSortingByNameWillReturnPartialThirdPage()
	{
		createPagesOfComponents();

		final ComponentItemListData page = executeGetPageOfItems(null, 2, 2, AbstractCMSComponentModel.NAME);
		final List<AbstractCMSComponentData> results = page.getComponentItems();
		assertThat("page 2 (no filter, name sort) failed to return a list of 1", results.size(), is(1));
		assertThat("page 2 (no filter, name sort) failed to return the maximum number of results",
				page.getPagination().getTotalCount(), is(5L));

		final String[] orderedUids = results.stream().map(e -> e.getUid()).collect(toList()).toArray(new String[]{});
		assertThat(orderedUids, Matchers.arrayContaining(ParagraphComponentModelMother.UID_HEADER+3));
	}

	@Test
	public void whenFilterAndSortingByNameWillReturnFullFirstPage()
	{
		createPagesOfComponents();

		final ComponentItemListData page = executeGetPageOfItems("lukE", 2, 0, AbstractCMSComponentModel.NAME);
		final List<AbstractCMSComponentData> results = page.getComponentItems();
		assertThat("page 0 (filter, name sort) failed to return a full page", results.size(), is(2));
		assertThat("page 0 (filter, name sort) failed to return the maximum number of results",
				page.getPagination().getTotalCount(), is(3L));

		final String[] orderedUids = results.stream().map(e -> e.getUid()).collect(toList()).toArray(new String[]{});
		assertThat(orderedUids, Matchers.arrayContaining(ParagraphComponentModelMother.UID_HEADER+5,ParagraphComponentModelMother.UID_HEADER+1));

	}

	@Test
	public void whenFilterAndSortingByNameWillReturnPartialSecondPage()
	{
		createPagesOfComponents();

		final ComponentItemListData page = executeGetPageOfItems("lukE", 2, 1, AbstractCMSComponentModel.NAME);
		final List<AbstractCMSComponentData> results = page.getComponentItems();
		assertThat("page 1 (filter, name sort) failed to return a full page", results.size(), is(1));
		assertThat("page 1 (filter, name sort) failed to return the maximum number of results",
				page.getPagination().getTotalCount(), is(3L));

		final String[] orderedUids = results.stream().map(e -> e.getUid()).collect(toList()).toArray(new String[]{});
		assertThat(orderedUids, Matchers.arrayContaining(ParagraphComponentModelMother.UID_HEADER+3));
	}


	@Test
	public void shouldGetAllItemsFromCatalogWithContainerComponent_GetsContainerAndComponents() throws Exception
	{
		createElectronicsSiteAndHomeAppleCatalogPageHeaderWithABTestContainer();

		final List<AbstractCMSComponentData> entities = executeGetAllItems();
		assertEquals(3, entities.size());

		final AbstractCMSComponentData container = getComponentItemByUid(entities,
				ABTestCMSComponentContainerModelMother.UID_HEADER);
		assertEquals(ABTestCMSComponentContainerModel._TYPECODE, container.getTypeCode());
		assertEquals(ABTestCMSComponentContainerModelMother.UID_HEADER, container.getUid());

		final AbstractCMSComponentData headerParagraph = getComponentItemByUid(entities, ParagraphComponentModelMother.UID_HEADER);
		assertEquals(CMSParagraphComponentModel._TYPECODE, headerParagraph.getTypeCode());
		assertEquals(ParagraphComponentModelMother.UID_HEADER, headerParagraph.getUid());
		assertEquals(ParagraphComponentModelMother.NAME_HEADER, headerParagraph.getName());

		final AbstractCMSComponentData footerParagraph = getComponentItemByUid(entities, ParagraphComponentModelMother.UID_FOOTER);
		assertEquals(CMSParagraphComponentModel._TYPECODE, footerParagraph.getTypeCode());
		assertEquals(ParagraphComponentModelMother.UID_FOOTER, footerParagraph.getUid());
		assertEquals(ParagraphComponentModelMother.NAME_FOOTER, footerParagraph.getName());
	}


	@Ignore("Ignoring for now, as the mothers are creating components with the same modified time")
	@Test
	public void shouldGetAllItemsAndAllItemsArePresentOnTheRightOrder() throws Exception
	{
		createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraphAndLink();

		final List<AbstractCMSComponentData> entities = executeGetAllItems();
		assertEquals(2, entities.size());

		final AbstractCMSComponentData headerParagraph = entities.stream().findFirst().get();
		final AbstractCMSComponentData headerLink = entities.stream().skip(1).findFirst().get();

		Assert.assertTrue("The response should return a list in descending order by modification time. ", headerParagraph
				.getModifiedtime().after(headerLink.getModifiedtime()));
	}

	protected List<AbstractCMSComponentData> executeGetAllItems() throws Exception
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		return response.readEntity(ComponentItemListData.class).getComponentItems();
	}

	protected ComponentItemListData executeGetPageOfItems(final String mask, final int pageSize, final int currentPage, final String sort)
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>()))
				.queryParam(MASK_PARAMETER, mask)
				.queryParam(PAGE_SIZE_PARAMETER, String.valueOf(pageSize))
				.queryParam(CURRENT_PAGE_PARAMETER, String.valueOf(currentPage))
				.queryParam(SORT_PARAMETER, sort)
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		return response.readEntity(ComponentItemListData.class);
	}

	@Test
	public void shouldGetAllItemsButWithEmptyCollection() throws Exception
	{
		createElectronicsSite();
		createEmptyAppleCatalog();

		final List<AbstractCMSComponentData> entities = executeGetAllItems();
		assertThat(entities, empty());
	}

	@Test
	public void shouldGetOneItem() throws Exception
	{
		createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraphWithoutRestriction();

		final String endPoint = replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>());

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endPoint) //
				.path(ParagraphComponentModelMother.UID_HEADER).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final CMSParagraphComponentData entity = response.readEntity(CMSParagraphComponentData.class);
		assertEquals(CMSParagraphComponentModel._TYPECODE, entity.getTypeCode());
		assertEquals(ParagraphComponentModelMother.UID_HEADER, entity.getUid());
		assertEquals(ParagraphComponentModelMother.NAME_HEADER, entity.getName());
		assertEquals(ParagraphComponentModelMother.CONTENT_HEADER_EN, entity.getContent().get("en"));
	}

	@Test
	public void shouldReturnHttpStatus404DueToItemNotFound() throws Exception
	{
		createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraphWithoutRestriction();

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>()))
				.path(LinkComponentModelMother.UID_EXTERNAL_LINK).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.NOT_FOUND, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertEquals(1, errors.getErrors().size());
	}

	@Test
	public void shouldUpdateOneItemExceptUID() throws Exception
	{
		createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraphWithoutRestriction();

		//send updates to paragraphComponent
		final Map<String, String> localizedValueString = new HashMap<>();
		localizedValueString.put("en", NEW_CONTENT_HEADER_EN);

		final CMSParagraphComponentData componentData = new CMSParagraphComponentData();
		componentData.setTypeCode(CMSParagraphComponentModel._TYPECODE);
		componentData.setUid(ParagraphComponentModelMother.UID_HEADER);
		componentData.setContent(localizedValueString);
		componentData.setName(NEW_NAME_HEADER);

		final Map<String, String> uriVariables = new HashMap<>();
		uriVariables.put(UID, ParagraphComponentModelMother.UID_HEADER);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>()))
				.path(ParagraphComponentModelMother.UID_HEADER).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.put(Entity.entity(marshallDto(componentData, CMSParagraphComponentData.class), MediaType.APPLICATION_JSON));

		assertResponse(Status.NO_CONTENT, response);

		final Response responseGet = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>()))
				.path(ParagraphComponentModelMother.UID_HEADER).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, responseGet);

		final CMSParagraphComponentData entity = responseGet.readEntity(CMSParagraphComponentData.class);
		assertEquals(NEW_CONTENT_HEADER_EN, entity.getContent().get(Locale.ENGLISH.toLanguageTag()));
		assertEquals(NEW_NAME_HEADER, entity.getName());
	}

	@Test
	public void shouldRemoveAnItem() throws Exception
	{
		createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraphWithoutRestriction();

		//send updates to paragraphComponent
		final Map<String, String> localizedValueString = new HashMap<>();
		localizedValueString.put("en", NEW_CONTENT_HEADER_EN);

		final CMSParagraphComponentData componentData = new CMSParagraphComponentData();
		componentData.setTypeCode(CMSParagraphComponentModel._TYPECODE);
		componentData.setUid(ParagraphComponentModelMother.UID_HEADER);
		componentData.setContent(localizedValueString);
		componentData.setName(NEW_NAME_HEADER);

		Response responseGet = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())) //
				.path(ParagraphComponentModelMother.UID_HEADER).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, responseGet);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())) //
				.path(ParagraphComponentModelMother.UID_HEADER).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.delete();

		assertResponse(Status.NO_CONTENT, response);

		responseGet = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())) //
				.path(ParagraphComponentModelMother.UID_HEADER).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.NOT_FOUND, responseGet);
	}

	@Test
	public void shouldReturn404ErrorMessageOnInvalidUidForRemoveCmsComponent() throws Exception
	{
		createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraphWithoutRestriction();

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())) //
				.path(INVALID_UID).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.delete();

		assertResponse(Status.NOT_FOUND, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertEquals(errors.getErrors().get(0).getMessage(), "CMSComponent with id [" + INVALID_UID + "] not found.");
	}

	@Test
	public void shouldNotUpdateItemDueToNoTypeCodeProvided() throws Exception
	{
		createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraphWithoutRestriction();

		final String endpoint = replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>());

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endpoint) //
				.path(ParagraphComponentModelMother.UID_HEADER).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.put(Entity.entity(new CMSParagraphComponentData(), MediaType.APPLICATION_JSON));

		assertResponse(Status.BAD_REQUEST, response);
	}

	@Test
	public void shouldCreateNewComponentBasedOnType() throws Exception
	{
		createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraph_WithTypeRestrictions();

		final String uidNewComponent = "uid-new-component-added";
		final String nameNewComponent = "name-new-component-added";

		final CMSParagraphComponentData component = new CMSParagraphComponentData();
		component.setName(nameNewComponent);
		component.setSlotId(ContentSlotModelMother.UID_HEADER);
		component.setTypeCode(CMSParagraphComponentModel._TYPECODE);
		component.setUid(uidNewComponent);
		component.setPosition(SLOT_POSITION);
		component.setPageId(PAGE_ID);

		final Map<String, String> localizedValueString = new HashMap<>();
		localizedValueString.put("en", NEW_CONTENT_HEADER_EN);
		component.setContent(localizedValueString);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(marshallDto(component, CMSParagraphComponentData.class), MediaType.APPLICATION_JSON));

		assertResponse(Status.CREATED, response);

		final CMSParagraphComponentData entity = response.readEntity(CMSParagraphComponentData.class);
		assertEquals(uidNewComponent, entity.getUid());
		assertEquals(nameNewComponent, entity.getName());
		assertEquals(SLOT_POSITION, entity.getPosition());
		assertEquals(ContentSlotModelMother.UID_HEADER, entity.getSlotId());
		assertEquals(PAGE_ID, entity.getPageId());

		assertThat(response.getHeaderString(CmswebservicesConstants.HEADER_LOCATION), containsString(uidNewComponent));
	}

	@Test
	public void shouldCreateNewComponentBasedOnTypeWithNullContentSlotAndNullPosition() throws Exception
	{
		createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraph_WithTypeRestrictions();

		final String uidNewComponent = "uid-new-component-added";
		final String nameNewComponent = "name-new-component-added";

		final CMSParagraphComponentData component = new CMSParagraphComponentData();
		component.setName(nameNewComponent);
		component.setSlotId(null);
		component.setTypeCode(CMSParagraphComponentModel._TYPECODE);
		component.setUid(uidNewComponent);
		component.setPosition(null);
		component.setPageId(PAGE_ID);

		final Map<String, String> localizedValueString = new HashMap<>();
		localizedValueString.put("en", NEW_CONTENT_HEADER_EN);
		component.setContent(localizedValueString);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(marshallDto(component, CMSParagraphComponentData.class), MediaType.APPLICATION_JSON));

		assertResponse(Status.CREATED, response);

		final CMSParagraphComponentData entity = response.readEntity(CMSParagraphComponentData.class);

		assertEquals(uidNewComponent, entity.getUid());
		assertEquals(nameNewComponent, entity.getName());
		assertEquals(null, entity.getPosition());
		assertEquals(null, entity.getSlotId());
		assertEquals(PAGE_ID, entity.getPageId());

		assertThat(response.getHeaderString(CmswebservicesConstants.HEADER_LOCATION), containsString(uidNewComponent));
	}

	@Test
	public void shouldNotCreateNewComponentBasedOnTypeDueToUnkownSlotId() throws Exception
	{
		createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraph_WithTypeRestrictions();

		final String uidNewComponent = "uid-new-component-added";
		final String nameNewComponent = "name-new-component-added";
		final String someWrongSlotIDHeader = "unknown-slot-uid";

		final CMSParagraphComponentData component = new CMSParagraphComponentData();
		component.setName(nameNewComponent);
		component.setSlotId(someWrongSlotIDHeader);
		component.setTypeCode(CMSParagraphComponentModel._TYPECODE);
		component.setUid(uidNewComponent);
		component.setPosition(SLOT_POSITION);
		component.setPageId(PAGE_ID);

		final Map<String, String> localizedValueString = new HashMap<>();
		localizedValueString.put("en", NEW_CONTENT_HEADER_EN);
		component.setContent(localizedValueString);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(marshallDto(component, CMSParagraphComponentData.class), MediaType.APPLICATION_JSON));

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertEquals(1, errors.getErrors().size());
		assertEquals(CreateComponentValidator.SLOT_ID, errors.getErrors().get(0).getSubject());

		assertResponse(Status.BAD_REQUEST, response);
	}

	@Test
	public void shouldNotCreateNewComponentBasedOnType_TypeRestrictionValidation() throws Exception
	{
		createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraphWithoutRestriction();

		final String uidNewComponent = "uid-new-component-added";
		final String nameNewComponent = "name-new-component-added";

		final CMSParagraphComponentData component = new CMSParagraphComponentData();
		component.setName(nameNewComponent);
		component.setSlotId(ContentSlotModelMother.UID_HEADER);
		component.setTypeCode(ProductListComponentModel._TYPECODE);
		component.setUid(uidNewComponent);
		component.setPosition(SLOT_POSITION);
		component.setPageId(PAGE_ID);

		final Map<String, String> localizedValueString = new HashMap<>();
		localizedValueString.put("en", NEW_CONTENT_HEADER_EN);
		component.setContent(localizedValueString);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(marshallDto(component, CMSParagraphComponentData.class), MediaType.APPLICATION_JSON));

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertEquals(1, errors.getErrors().size());
		assertEquals(CreateComponentValidator.UID, errors.getErrors().get(0).getSubject());

		assertResponse(Status.BAD_REQUEST, response);
	}

	@Test
	public void shouldNotCreateNewComponent_InvalidSlotPosition() throws Exception
	{
		createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraph_WithTypeRestrictions();

		final String uidNewComponent = "uid-new-component-added";
		final String nameNewComponent = "name-new-component-added";

		final CMSParagraphComponentData component = new CMSParagraphComponentData();
		component.setName(nameNewComponent);
		component.setSlotId(ContentSlotModelMother.UID_HEADER);
		component.setTypeCode(CMSParagraphComponentModel._TYPECODE);
		component.setUid(uidNewComponent);
		component.setPosition(null);
		component.setPageId(PAGE_ID);

		final Map<String, String> localizedValueString = new HashMap<>();
		localizedValueString.put("en", NEW_CONTENT_HEADER_EN);
		component.setContent(localizedValueString);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(marshallDto(component, CMSParagraphComponentData.class), MediaType.APPLICATION_JSON));

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertEquals(1, errors.getErrors().size());
		assertEquals(CreateComponentValidator.POSITION, errors.getErrors().get(0).getSubject());

		assertResponse(Status.BAD_REQUEST, response);
	}

	@Test
	public void shouldNotCreateNewComponent_ContentIsEmpty() throws Exception
	{
		createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraph_WithTypeRestrictions();

		final String uidNewComponent = "uid-new-component-added";
		final String nameNewComponent = "name-new-component-added";

		final CMSParagraphComponentData component = new CMSParagraphComponentData();
		component.setName(nameNewComponent);
		component.setSlotId(ContentSlotModelMother.UID_HEADER);
		component.setTypeCode(CMSParagraphComponentModel._TYPECODE);
		component.setUid(uidNewComponent);
		component.setPosition(1);
		component.setPageId(PAGE_ID);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(marshallDto(component, CMSParagraphComponentData.class), MediaType.APPLICATION_JSON));

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertEquals(1, errors.getErrors().size());
		assertEquals("content", errors.getErrors().get(0).getSubject());

		assertResponse(Status.BAD_REQUEST, response);
	}

	@Test
	public void shouldNotCreateNewComponent_InvalidPageId() throws Exception
	{
		createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraph_WithTypeRestrictions();

		final String uidNewComponent = "uid-new-component-added";
		final String nameNewComponent = "name-new-component-added";

		final CMSParagraphComponentData component = new CMSParagraphComponentData();
		component.setName(nameNewComponent);
		component.setSlotId(ContentSlotModelMother.UID_HEADER);
		component.setTypeCode(CMSParagraphComponentModel._TYPECODE);
		component.setUid(uidNewComponent);
		component.setPosition(SLOT_POSITION);
		component.setPageId(INVALID_PAGE_ID);

		final Map<String, String> localizedValueString = new HashMap<>();
		localizedValueString.put("en", NEW_CONTENT_HEADER_EN);
		component.setContent(localizedValueString);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(marshallDto(component, CMSParagraphComponentData.class), MediaType.APPLICATION_JSON));

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertEquals(1, errors.getErrors().size());
		assertEquals(CreateComponentValidator.PAGE_ID, errors.getErrors().get(0).getSubject());

		assertResponse(Status.BAD_REQUEST, response);
	}

	/**
	 * Get the component with the matching uid.
	 *
	 * @param items
	 *           - list of component items
	 * @param uid
	 *           - the uid to search for
	 * @return the component with the matching uid
	 */
	protected AbstractCMSComponentData getComponentItemByUid(final List<AbstractCMSComponentData> items, final String uid)
	{
		return items.stream().filter(item -> item.getUid().equals(uid)).findAny().get();
	}

	@Test
	public void shouldGetComponentItem_SingleValidUidInUidsParameter() throws Exception
	{
		createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraphAndLink();

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())) //
				.queryParam(UIDS_REQUEST_PARAMETER, ParagraphComponentModelMother.UID_HEADER).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final ComponentItemListData entity = response.readEntity(ComponentItemListData.class);
		assertThat(entity.getComponentItems().size(), is(1));

		assertThat(entity.getComponentItems().get(0).getUid(), is(ParagraphComponentModelMother.UID_HEADER));
		assertThat(entity.getComponentItems().get(0).getName(), is(ParagraphComponentModelMother.NAME_HEADER));
		assertThat(entity.getComponentItems().get(0).getTypeCode(), is(CMSParagraphComponentModel._TYPECODE));
	}

	@Test
	public void shouldGetMultipleComponentItems_MultipleValidUidsInUidsParameter() throws Exception
	{
		createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraphAndLink();

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())) //
				.queryParam(UIDS_REQUEST_PARAMETER,
						ParagraphComponentModelMother.UID_HEADER + "," + LinkComponentModelMother.UID_EXTERNAL_LINK).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final ComponentItemListData entity = response.readEntity(ComponentItemListData.class);
		assertThat(entity.getComponentItems().size(), is(2));

		final List<AbstractCMSComponentData> sorted = entity.getComponentItems().stream()
				.sorted((cmsData1, cmsData2) -> cmsData1.getUid().compareTo(cmsData2.getUid())).collect(Collectors.toList());

		assertThat(sorted.get(0).getUid(), is(ParagraphComponentModelMother.UID_HEADER));
		assertThat(sorted.get(0).getName(), is(ParagraphComponentModelMother.NAME_HEADER));
		assertThat(sorted.get(0).getTypeCode(), is(CMSParagraphComponentModel._TYPECODE));

		assertThat(sorted.get(1).getUid(), is(LinkComponentModelMother.UID_EXTERNAL_LINK));
		assertThat(sorted.get(1).getTypeCode(), is(CMSLinkComponentModel._TYPECODE));
	}

	@Test
	public void shouldReturnEmptyObject_InvalidUidInUidsParameter() throws Exception
	{
		createElectronicsSiteAndHomeAppleCatalogPageHeaderWithParagraphAndLink();

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())) //
				.queryParam(UIDS_REQUEST_PARAMETER, INVALID_COMPONENT_ID).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final ComponentItemListData entity = response.readEntity(ComponentItemListData.class);
		assertThat(entity.getComponentItems(), empty());
	}

	@Test
	public void shouldGetOneItemWithUnsupportedType() throws Exception
	{
		createElectronicsSiteAndHomeAppleCatalogPageHeaderWithFlashComponentWithoutRestriction();

		final String endPoint = replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>());

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(endPoint) //
				.path(FlashComponentModelMother.UID_HEADER).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final AbstractCMSComponentData entity = response.readEntity(AbstractCMSComponentData.class);
		assertEquals(FlashComponentModel._TYPECODE, entity.getTypeCode());
		assertEquals(FlashComponentModelMother.UID_HEADER, entity.getUid());
		assertEquals(FlashComponentModelMother.NAME_HEADER, entity.getName());
	}

	@Test
	public void shouldUpdateOneItemWithUnsupportedType() throws Exception
	{
		createElectronicsSiteAndHomeAppleCatalogPageHeaderWithFlashComponentWithoutRestriction();

		//send updates to simpleCMSComponent
		final AbstractCMSComponentData componentData = new AbstractCMSComponentData();
		componentData.setTypeCode(FlashComponentModel._TYPECODE);
		componentData.setUid(FlashComponentModelMother.UID_HEADER);
		componentData.setName(NEW_NAME_HEADER);
		componentData.setVisible(Boolean.FALSE);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())) //
				.path(FlashComponentModelMother.UID_HEADER).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.put(Entity.entity(componentData, MediaType.APPLICATION_JSON));

		assertResponse(Status.NO_CONTENT, response);

		final Response responseGet = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())) //
				.path(FlashComponentModelMother.UID_HEADER).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, responseGet);

		final AbstractCMSComponentData entity = responseGet.readEntity(AbstractCMSComponentData.class);
		assertEquals(NEW_NAME_HEADER, entity.getName());
		assertFalse(entity.getVisible());
	}
}
