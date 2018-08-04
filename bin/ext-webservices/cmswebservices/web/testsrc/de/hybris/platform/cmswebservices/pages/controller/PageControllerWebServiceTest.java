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
package de.hybris.platform.cmswebservices.pages.controller;


import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static java.util.Locale.ENGLISH;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.CatalogPageModel;
import de.hybris.platform.cms2.model.pages.CategoryPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.pages.ProductPageModel;
import de.hybris.platform.cmsfacades.pages.impl.DefaultPageFacade;
import de.hybris.platform.cmsfacades.util.models.BaseStoreModelMother;
import de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentPageModelMother;
import de.hybris.platform.cmsfacades.util.models.LanguageModelMother;
import de.hybris.platform.cmsfacades.util.models.PageTemplateModelMother;
import de.hybris.platform.cmsfacades.util.models.ProductPageModelMother;
import de.hybris.platform.cmsfacades.util.models.SiteModelMother;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.AbstractPageData;
import de.hybris.platform.cmswebservices.data.CatalogPageData;
import de.hybris.platform.cmswebservices.data.CategoryPageData;
import de.hybris.platform.cmswebservices.data.ContentPageData;
import de.hybris.platform.cmswebservices.data.PageListData;
import de.hybris.platform.cmswebservices.data.ProductPageData;
import de.hybris.platform.cmswebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;
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

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;


@NeedsEmbeddedServer(webExtensions =
{ CmswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class PageControllerWebServiceTest extends ApiBaseIntegrationTest
{

	private static final String BASE_PAGE_ENDPOINT = "/v1/sites/{siteId}/catalogs/{catalogId}/versions/{versionId}/pages";

	private static final String INEXISTENCE_PAGE = "inexistence_page";
	private static final String UPDATED_PAGE_UID = "updatedPageUid";
	private static final String PAGE3_TITLE_SUFFIX = "page3_pagetitle";
	private static final String PAGE2_TITLE_SUFFIX = "page2_pagetitle";
	private static final String PAGE1_TITLE_SUFFIX = "page1_pagetitle";
	private static final String PAGE1 = "page1";
	private static final String PAGE2 = "page2";
	private static final String PAGE3 = "page3";
	private static final String UID3 = "uid3";
	private static final String UID2 = "uid2";
	private static final String UID1 = "uid1";
	private static final String UIDINVALID1 = "invalidUid1";
	private static final String UIDINVALID2 = "invalidUid2";
	private static final String CONTENT_PAGE = "ContentPage";
	private static final String SORT_NAME = "name";
	private static final Boolean IS_DEFAULT_PAGE = true;
	private static final int PAGE_SIZE = 5;
	private static final int CURRENT_PAGE = 0;

	private static final String PAGE_LABEL = "pageLabel";
	private static final String PAGE_TITLE = "pageTitle";
	private static final String PAGE_NAME = "pageName";
	private static final String PAGE_UID = "pageUid";
	private static final String UPDATED_PAGE_TITLE = "updatedNameTitle";
	private static final String UPDATED_PAGE_NAME = "updatedPageName";
	private static final String UIDS_REQUEST_PARAMETER = "uids";
	
	private static final String MASK_QUERY_PARAMETER = "mask";
	private static final String TYPE_CODE_QUERY_PARAMETER = "typeCode";
	private static final String PAGE_SIZE_QUERY_PARAMETER = "pageSize";
	private static final String CURRENT_PAGE_QUERY_PARAMETER = "currentPage";
	private static final String SORT_QUERY_PARAMETER = "sort";

	@Resource
	private CatalogVersionModelMother catalogVersionModelMother;
	@Resource
	private ContentPageModelMother contentPageModelMother;
	@Resource
	private ProductPageModelMother productPageModelMother;
	@Resource
	private SiteModelMother siteModelMother;
	@Resource
	private BaseStoreModelMother baseStoreModelMother;
	@Resource
	private LanguageModelMother languageModelMother;
	@Resource
	private PageTemplateModelMother pageTemplateModelMother;

	private CatalogVersionModel catalogVersion;

	enum ValidationFields
	{
		MESSAGE("This field is required."), REASON("missing"), SUBJECTTYPE("parameter"), TYPE("ValidationError");
		String value;

		ValidationFields(final String value)
		{
			this.value = value;
		}
	}

	enum PageFields
	{
		UID("uid"), NAME("name"), TITLE("title"), DEFAULT_PAGE("defaultPage"), TYPECODE("typeCode"), LABEL("label"), TEMPLATE(
				"template");
		String property;

		private PageFields(final String property)
		{
			this.property = property;
		}
	}

	@Before
	public void setup()
	{
		siteModelMother.createNorthAmericaElectronicsWithAppleStagedCatalog();
		catalogVersion = catalogVersionModelMother.createAppleStagedCatalogVersionModel();
		pageTemplateModelMother.HomePage_Template(catalogVersion);
		createThreeContentPages();
	}

	@Test
	public void willLoadExpectedListOfContentPages()
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(BASE_PAGE_ENDPOINT, new HashMap<>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final PageListData entity = response.readEntity(PageListData.class);
		assertThat(entity.getPages().size(), is(3));

		final AbstractPageData page1 = getPageByUid(entity.getPages(), UID1);

		final Map<String, String> pageTitle1 = page1.getTitle();
		assertThat(pageTitle1.get(ENGLISH.toString()), is(PAGE1_TITLE_SUFFIX));
		assertThat(page1.getTypeCode(), is(CONTENT_PAGE));
		assertThat(page1.getTemplate(), is(PageTemplateModelMother.UID_HOME_PAGE));

		final AbstractPageData page2 = getPageByUid(entity.getPages(), UID2);

		final Map<String, String> pageTitle2 = page2.getTitle();
		assertThat(pageTitle2.get(ENGLISH.toString()), is(PAGE2_TITLE_SUFFIX));
		assertThat(page2.getTypeCode(), is(CONTENT_PAGE));
		assertThat(page2.getTemplate(), is(PageTemplateModelMother.UID_HOME_PAGE));

		final AbstractPageData page3 = getPageByUid(entity.getPages(), UID3);

		final Map<String, String> pageTitle3 = page3.getTitle();
		assertThat(pageTitle3.get(ENGLISH.toString()), is(PAGE3_TITLE_SUFFIX));
		assertThat(page3.getTypeCode(), is(CONTENT_PAGE));
		assertThat(page3.getTemplate(), is(PageTemplateModelMother.UID_HOME_PAGE));
	}

	@Test
	public void shouldGetOnePage()
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(BASE_PAGE_ENDPOINT, new HashMap<>())) //
				.path(UID1).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final AbstractPageData pageData = response.readEntity(AbstractPageData.class);

		assertEquals(pageData.getUid(), UID1);
	}

	@Test
	public void shouldNotGetPage_InexistenceUid()
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder()
				//
				.path(replaceUriVariablesWithDefaults(BASE_PAGE_ENDPOINT, new HashMap<>()))
				.path(INEXISTENCE_PAGE).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.NOT_FOUND, response);
	}


	// Get specific pages

	@Test
	public void shouldGetAllValidSpecificContentPages()
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(BASE_PAGE_ENDPOINT, new HashMap<>()))
				.queryParam(UIDS_REQUEST_PARAMETER, UID2 + "," + UID3).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final PageListData entity = response.readEntity(PageListData.class);
		assertThat(entity.getPages().size(), is(2));

		final AbstractPageData page2 = getPageByUid(entity.getPages(), UID2);

		final Map<String, String> pageTitle2 = page2.getTitle();
		assertThat(pageTitle2.get(ENGLISH.toString()), is(PAGE2_TITLE_SUFFIX));
		assertThat(page2.getTypeCode(), is(CONTENT_PAGE));
		assertThat(page2.getTemplate(), is(PageTemplateModelMother.UID_HOME_PAGE));

		final AbstractPageData page3 = getPageByUid(entity.getPages(), UID3);

		final Map<String, String> pageTitle3 = page3.getTitle();
		assertThat(pageTitle3.get(ENGLISH.toString()), is(PAGE3_TITLE_SUFFIX));
		assertThat(page3.getTypeCode(), is(CONTENT_PAGE));
		assertThat(page3.getTemplate(), is(PageTemplateModelMother.UID_HOME_PAGE));
	}

	@Test
	public void shouldGetOnlyTheValidSpecificContentPages()
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(BASE_PAGE_ENDPOINT, new HashMap<>()))
				.queryParam(UIDS_REQUEST_PARAMETER, UID2 + "," + UIDINVALID1).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final PageListData entity = response.readEntity(PageListData.class);
		assertThat(entity.getPages().size(), is(1));

		final AbstractPageData page2 = getPageByUid(entity.getPages(), UID2);

		final Map<String, String> pageTitle2 = page2.getTitle();
		assertThat(pageTitle2.get(ENGLISH.toString()), is(PAGE2_TITLE_SUFFIX));
		assertThat(page2.getTypeCode(), is(CONTENT_PAGE));
		assertThat(page2.getTemplate(), is(PageTemplateModelMother.UID_HOME_PAGE));

	}

	@Test
	public void shouldReturnEmpty_AllSpecifiedContentPagesInvalid()
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(BASE_PAGE_ENDPOINT, new HashMap<>()))
				.queryParam(UIDS_REQUEST_PARAMETER, UIDINVALID1 + "," + UIDINVALID2).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final PageListData entity = response.readEntity(PageListData.class);
		assertThat(entity.getPages().size(), is(0));

	}
	
	@Test
	public void shouldSearchForPagesContainingUID1()
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(BASE_PAGE_ENDPOINT, new HashMap<>())) //
				.queryParam(MASK_QUERY_PARAMETER, UID1) //
				.queryParam(PAGE_SIZE_QUERY_PARAMETER, PAGE_SIZE) //
				.queryParam(CURRENT_PAGE_QUERY_PARAMETER, CURRENT_PAGE) //
				.queryParam(SORT_QUERY_PARAMETER, SORT_NAME).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Response.Status.OK, response);

		final PageListData pages = response.readEntity(PageListData.class);

		assertThat(pages.getPagination().getCount(), is(1));
		assertThat(pages.getPagination().getTotalCount(), is(1L));
		assertThat(pages.getPagination().getPage(), is(0));

		final AbstractPageData pageData = pages.getPages().get(0);
		assertEquals(pageData.getUid(), UID1);
	}
	
	@Test
	public void shouldSearchForContentPagesContainingUID1()
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(BASE_PAGE_ENDPOINT, new HashMap<>())) //
				.queryParam(MASK_QUERY_PARAMETER, UID1) //
				.queryParam(TYPE_CODE_QUERY_PARAMETER, CONTENT_PAGE) //
				.queryParam(PAGE_SIZE_QUERY_PARAMETER, PAGE_SIZE) //
				.queryParam(CURRENT_PAGE_QUERY_PARAMETER, CURRENT_PAGE) //
				.queryParam(SORT_QUERY_PARAMETER, SORT_NAME).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Response.Status.OK, response);

		final PageListData pages = response.readEntity(PageListData.class);

		assertThat(pages.getPagination().getCount(), is(1));
		assertThat(pages.getPagination().getTotalCount(), is(1L));
		assertThat(pages.getPagination().getPage(), is(0));

		final AbstractPageData pageData = pages.getPages().get(0);
		assertEquals(pageData.getUid(), UID1);
		assertEquals(pageData.getTypeCode(), CONTENT_PAGE);
	}

	@Test
	public void shouldReturnEmptyListWhenSearchingForPages()
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(BASE_PAGE_ENDPOINT, new HashMap<>())) //
				.queryParam(MASK_QUERY_PARAMETER, UIDINVALID1) //
				.queryParam(PAGE_SIZE_QUERY_PARAMETER, PAGE_SIZE) //
				.queryParam(CURRENT_PAGE_QUERY_PARAMETER, CURRENT_PAGE) //
				.queryParam(SORT_QUERY_PARAMETER, SORT_NAME).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Response.Status.OK, response);

		final PageListData pages = response.readEntity(PageListData.class);

		assertThat(pages.getPagination().getCount(), is(0));
		assertThat(pages.getPagination().getTotalCount(), is(0L));
		assertThat(pages.getPagination().getPage(), is(0));
		assertThat(pages.getPages().size(), is(0));
	}
	
	// Create a Page

	@Test
	public void shouldCreateContentPage() throws JAXBException
	{
		final ContentPageData page = populatePage(ContentPageModel._TYPECODE, new ContentPageData());
		page.setLabel(PAGE_LABEL);
		page.setUid(null);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(BASE_PAGE_ENDPOINT, new HashMap<>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(marshallDto(page, ContentPageData.class), MediaType.APPLICATION_JSON));

		assertResponse(Status.CREATED, response);

		final ContentPageData entity = response.readEntity(ContentPageData.class);
		assertThat(entity,
				allOf(hasProperty(PageFields.UID.property, startsWith(DefaultPageFacade.DEFAULT_UID_PREFIX)),
						hasProperty(PageFields.NAME.property, is(PAGE_NAME)), //
						hasProperty(PageFields.LABEL.property, is(PAGE_LABEL)), //
						hasProperty(PageFields.TYPECODE.property, is(ContentPageModel._TYPECODE)),
						hasProperty(PageFields.TEMPLATE.property, is(PageTemplateModelMother.UID_HOME_PAGE)),
						hasProperty(PageFields.DEFAULT_PAGE.property, is(IS_DEFAULT_PAGE))));
	}

	@Test
	public void shouldCreateProductPage() throws JAXBException
	{
		final ProductPageData page = populatePage(ProductPageModel._TYPECODE, new ProductPageData());

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(BASE_PAGE_ENDPOINT, new HashMap<>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(marshallDto(page, ProductPageData.class), MediaType.APPLICATION_JSON));

		assertResponse(Status.CREATED, response);

		final ProductPageData entity = response.readEntity(ProductPageData.class);
		assertThat(entity,
				allOf(hasProperty(PageFields.UID.property, is(PAGE_UID)), //
						hasProperty(PageFields.NAME.property, is(PAGE_NAME)), //
						hasProperty(PageFields.TYPECODE.property, is(ProductPageModel._TYPECODE)),
						hasProperty(PageFields.TEMPLATE.property, is(PageTemplateModelMother.UID_HOME_PAGE)),
						hasProperty(PageFields.DEFAULT_PAGE.property, is(IS_DEFAULT_PAGE))));
	}

	@Test
	public void shouldCreateCatalogPage() throws JAXBException
	{
		final CatalogPageData page = populatePage(CatalogPageModel._TYPECODE, new CatalogPageData());

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(BASE_PAGE_ENDPOINT, new HashMap<>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(marshallDto(page, CatalogPageData.class), MediaType.APPLICATION_JSON));

		assertResponse(Status.CREATED, response);

		final CatalogPageData entity = response.readEntity(CatalogPageData.class);

		assertThat(entity,
				allOf(hasProperty(PageFields.UID.property, is(PAGE_UID)), //
						hasProperty(PageFields.NAME.property, is(PAGE_NAME)), //
						hasProperty(PageFields.TYPECODE.property, is(CatalogPageModel._TYPECODE)),
						hasProperty(PageFields.TEMPLATE.property, is(PageTemplateModelMother.UID_HOME_PAGE)),
						hasProperty(PageFields.DEFAULT_PAGE.property, is(IS_DEFAULT_PAGE))));
	}

	@Test
	public void shouldCreateCategoryPage() throws JAXBException
	{
		final CategoryPageData page = populatePage(CategoryPageModel._TYPECODE, new CategoryPageData());

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(BASE_PAGE_ENDPOINT, new HashMap<>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(marshallDto(page, CategoryPageData.class), MediaType.APPLICATION_JSON));

		assertResponse(Status.CREATED, response);

		final CategoryPageData entity = response.readEntity(CategoryPageData.class);
		assertThat(entity,
				allOf(hasProperty(PageFields.UID.property, is(PAGE_UID)), //
						hasProperty(PageFields.NAME.property, is(PAGE_NAME)), //
						hasProperty(PageFields.TYPECODE.property, is(CategoryPageModel._TYPECODE)),
						hasProperty(PageFields.TEMPLATE.property, is(PageTemplateModelMother.UID_HOME_PAGE)),
						hasProperty(PageFields.DEFAULT_PAGE.property, is(IS_DEFAULT_PAGE))));
	}

	@Test
	public void shouldNotCreatePage_ValidationErrors() throws JAXBException
	{
		final ContentPageData page = populatePage(ContentPageModel._TYPECODE, new ContentPageData());
		page.setUid(null);
		page.setTypeCode(null);
		page.setName(null);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(BASE_PAGE_ENDPOINT, new HashMap<>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(marshallDto(page, ContentPageData.class), MediaType.APPLICATION_JSON));

		assertResponse(Status.BAD_REQUEST, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);

		final ErrorWsDTO nameError = createRequiredFieldValidationObjectError(PageFields.NAME.property);
		final ErrorWsDTO typeCodeError = createRequiredFieldValidationObjectError(PageFields.TYPECODE.property);

		assertThat(errors.getErrors(), hasItems(samePropertyValuesAs(nameError), samePropertyValuesAs(typeCodeError)));
	}

	@Test
	public void shouldFailCreatePage_DuplicatePageUid() throws JAXBException
	{
		final ContentPageData page = populatePage(ContentPageModel._TYPECODE, new ContentPageData());
		page.setUid(UID1);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(BASE_PAGE_ENDPOINT, new HashMap<>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(marshallDto(page, ContentPageData.class), MediaType.APPLICATION_JSON));

		assertResponse(Status.BAD_REQUEST, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors().get(0).getSubject(), is(CMSItemModel.UID));
	}

	@Test
	public void shouldFailCreatePage_MissingPageTitle() throws JAXBException
	{
		final ContentPageData page = populatePage(ContentPageModel._TYPECODE, new ContentPageData());
		page.setTitle(null);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(BASE_PAGE_ENDPOINT, new HashMap<>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(marshallDto(page, ContentPageData.class), MediaType.APPLICATION_JSON));

		assertResponse(Status.BAD_REQUEST, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors().get(0).getSubject(), is(AbstractPageModel.TITLE));
	}

	@Test
	public void shouldUpdatePage() throws JAXBException
	{
		final ContentPageData page = populatePage(ContentPageModel._TYPECODE, new ContentPageData());
		page.setUid(UPDATED_PAGE_UID);
		page.setName(UPDATED_PAGE_NAME);
		page.setTitle(getLocalizedContent(UPDATED_PAGE_TITLE));
		page.setLabel(PAGE_LABEL);

		final Response response = getCmsManagerWsSecuredRequestBuilder()
				.path(replaceUriVariablesWithDefaults(BASE_PAGE_ENDPOINT, Maps.newHashMap())) //
				.path(UID1).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.put(Entity.entity(marshallDto(page, ContentPageData.class), MediaType.APPLICATION_JSON));

		assertResponse(Status.OK, response);
		final ContentPageData result = response.readEntity(ContentPageData.class);
		assertEquals(result.getName(), UPDATED_PAGE_NAME);
		assertEquals(result.getTitle().get(ENGLISH.toString()), UPDATED_PAGE_TITLE);
	}

	@Test
	public void shouldFailUpdatePage_DuplicateLabelForPrimaryPage() throws JAXBException
	{
		contentPageModelMother.SearchPage(catalogVersion);
		contentPageModelMother.DefaultHomePage(catalogVersion);
		contentPageModelMother.HomePage(catalogVersion);

		final ContentPageData page = new ContentPageData();
		page.setUid(ContentPageModelMother.UID_DEFAULT_HOMEPAGE);
		page.setName(ContentPageModelMother.NAME_HOMEPAGE);
		page.setTypeCode(ContentPageModel._TYPECODE);
		page.setTemplate(PageTemplateModelMother.UID_HOME_PAGE);
		page.setTitle(getLocalizedContent(PAGE_TITLE));
		page.setDefaultPage(Boolean.TRUE);
		page.setLabel(ContentPageModelMother.LABEL_SEARCHPAGE);

		final Response response = getCmsManagerWsSecuredRequestBuilder()
				.path(replaceUriVariablesWithDefaults(BASE_PAGE_ENDPOINT, Maps.newHashMap()))
				.path(ContentPageModelMother.UID_DEFAULT_HOMEPAGE).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.put(Entity.entity(marshallDto(page, ContentPageData.class), MediaType.APPLICATION_JSON));

		assertResponse(Status.BAD_REQUEST, response);

		final ErrorListWsDTO result = response.readEntity(ErrorListWsDTO.class);
		assertThat(result.getErrors(), iterableWithSize(1));
		assertThat(result.getErrors().get(0).getSubject(), is(ContentPageModel.LABEL));
	}

	@Test
	public void shouldGetDuplicatedUid_WhenUpdatePage() throws JAXBException
	{
		final ContentPageData page = populatePage(ContentPageModel._TYPECODE, new ContentPageData());
		page.setUid(UID2);

		final Response response = getCmsManagerWsSecuredRequestBuilder()
				//
				.path(replaceUriVariablesWithDefaults(BASE_PAGE_ENDPOINT, Maps.newHashMap())).path(UID1)
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.put(Entity.entity(marshallDto(page, ContentPageData.class), MediaType.APPLICATION_JSON));

		assertResponse(Status.BAD_REQUEST, response);
		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors().get(0).getSubject(), is(AbstractPageModel.UID));
	}

	@Test
	public void shouldGetValidationErrors_WhenUpdatePage() throws JAXBException
	{
		final ContentPageData page = populatePage(ContentPageModel._TYPECODE, new ContentPageData());
		page.setTitle(null);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(BASE_PAGE_ENDPOINT, Maps.newHashMap())).path(UID1)
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.put(Entity.entity(marshallDto(page, ContentPageData.class), MediaType.APPLICATION_JSON));

		assertResponse(Status.BAD_REQUEST, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors().get(0).getSubject(), is(AbstractPageModel.TITLE));
	}

	protected Map<String, String> getLocalizedContent(final String value)
	{
		final Map<String, String> localizedMap = new HashMap<>();
		localizedMap.put(ENGLISH.getLanguage(), value);
		return localizedMap;
	}

	protected ErrorWsDTO createRequiredFieldValidationObjectError(final String field)
	{
		final ErrorWsDTO error = new ErrorWsDTO();
		error.setMessage(ValidationFields.MESSAGE.value);
		error.setReason(ValidationFields.REASON.value);
		error.setSubject(field);
		error.setSubjectType(ValidationFields.SUBJECTTYPE.value);
		error.setType(ValidationFields.TYPE.value);
		return error;
	}

	protected <T extends AbstractPageData> T populatePage(final String pageType, final T page)
	{
		page.setUid(PAGE_UID);
		page.setName(PAGE_NAME);
		page.setTypeCode(pageType);
		page.setTemplate(PageTemplateModelMother.UID_HOME_PAGE);
		page.setTitle(getLocalizedContent(PAGE_TITLE));
		page.setDefaultPage(IS_DEFAULT_PAGE);
		return page;
	}

	protected void createThreeContentPages()
	{
		contentPageModelMother.somePage(catalogVersion, UID1, PAGE1);
		contentPageModelMother.somePage(catalogVersion, UID2, PAGE2);
		contentPageModelMother.somePage(catalogVersion, UID3, PAGE3);
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
	protected AbstractPageData getPageByUid(final List<AbstractPageData> items, final String uid)
	{
		return items.stream().filter(item -> item.getUid().equals(uid)).findAny().get();
	}
}
