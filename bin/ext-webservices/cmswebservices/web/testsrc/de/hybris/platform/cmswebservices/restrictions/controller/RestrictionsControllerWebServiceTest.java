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
package de.hybris.platform.cmswebservices.restrictions.controller;

import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel;
import de.hybris.platform.cms2.model.restrictions.CMSCategoryRestrictionModel;
import de.hybris.platform.cms2.model.restrictions.CMSTimeRestrictionModel;
import de.hybris.platform.cms2.model.restrictions.CMSUserGroupRestrictionModel;
import de.hybris.platform.cmsfacades.data.CategoryRestrictionData;
import de.hybris.platform.cmsfacades.data.TimeRestrictionData;
import de.hybris.platform.cmsfacades.data.UserGroupRestrictionData;
import de.hybris.platform.cmsfacades.restrictions.impl.DefaultRestrictionFacade;
import de.hybris.platform.cmsfacades.util.models.*;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.AbstractRestrictionData;
import de.hybris.platform.cmswebservices.data.RestrictionListData;
import de.hybris.platform.cmswebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.Test;


@NeedsEmbeddedServer(webExtensions =
		{ CmswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class RestrictionsControllerWebServiceTest extends ApiBaseIntegrationTest
{
	private static final String FIELD_REQUIRED = "This field is required.";
	private static final String FIELD_NOT_ALLOWED = "The value provided is not allowed.";
	private static final String FIELD_DOES_NOT_EXIST = "The value provided does not exist";
	private static final String FIELD_ALREADY_EXIST = "The value provided is already in use.";
	private static final String INVALID_DATE_RANGE = "The dates and times provided are not valid. The Active until date/time must be after/later than the Active from date/time.";
	private static final String FIELD_MIN_VIOLATED = "The minimum allowable value was violated.";
	private static final String ENDPOINT = "/v1/sites/{siteId}/catalogs/{catalogId}/versions/{versionId}/restrictions";

	private static final String PAGE_SIZE_PARAMETER = "pageSize";
	private static final String CURRENT_PAGE_PARAMETER = "currentPage";
	private static final String MASK_PARAMETER = "mask";
	private static final String SORT_PARAMETER = "sort";
	private static final String PARAMS_PARAMETER = "params";
	private static final String SORT_NAME_ASC = AbstractRestrictionModel.NAME + ":ASC";
	private static final String ELECTRONIC_CATEGORY_RESTRICTION_NAME = "Electronic Category Restriction";
	private static final String BLACK_FRIDAY_TIME_RESTRICTION_NAME = "Black Friday Time Restriction";
	private static final String MANAGERS_ONLY_USER_GROUP_RESTRICTION = "Managers Only Restriction";

	private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");

	@Resource
	private CatalogVersionModelMother catalogVersionModelMother;
	@Resource
	private SiteModelMother siteModelMother;
	@Resource
	private CMSTimeRestrictionModelMother timeRestrictionModelMother;
	@Resource
	private CMSUserGroupRestrictionModelMother userGroupRestrictionModelMother;
	@Resource
	private CMSCategoryRestrictionModelMother categoryRestrictionModelMother;
	@Resource
	private CategoryModelMother categoryModelMother;
	@Resource
	private ProductCatalogModelMother productCatalogModelMother;

	@Before
	public void setUp()
	{
		siteModelMother.createNorthAmericaElectronicsWithAppleStagedCatalog();
		final CatalogVersionModel appleCatalogVersion = catalogVersionModelMother.createAppleStagedCatalogVersionModel();
		productCatalogModelMother.createStaged1And2AndOnlinePhoneProductCatalogModel();
		final CatalogVersionModel phoneCatalogVersion = catalogVersionModelMother.createPhoneOnlineCatalogVersionModel();
		timeRestrictionModelMother.today(appleCatalogVersion);
		timeRestrictionModelMother.tomorrow(appleCatalogVersion);
		timeRestrictionModelMother.nextWeek(appleCatalogVersion);
		userGroupRestrictionModelMother.cmsManager(appleCatalogVersion);
		categoryRestrictionModelMother.shoesWithSandalsAndHeels(appleCatalogVersion, phoneCatalogVersion);
	}

	@Test
	public void shouldPopulateRestrictionsDataCorrectly()
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		final RestrictionListData restrictionsList = response.readEntity(RestrictionListData.class);

		assertResponse(Status.OK, response);

		final AbstractRestrictionData today = restrictionsList.getRestrictions().stream()
				.filter(restriction -> restriction.getName().equals(CMSTimeRestrictionModelMother.NAME_TODAY)).findFirst().get();
		assertThat(today.getUid(), is(CMSTimeRestrictionModelMother.UID_TODAY));
		assertThat(today.getName(), is(CMSTimeRestrictionModelMother.NAME_TODAY));
		assertThat(today.getDescription(), containsString("Page only applies from"));
		assertThat(today.getTypeCode(), is(CMSTimeRestrictionModel._TYPECODE));
	}

	@Test
	public void shouldGetAllRestrictionsOrderedByName()
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final RestrictionListData restrictionsList = response.readEntity(RestrictionListData.class);
		final AbstractRestrictionData cmsmanager = restrictionsList.getRestrictions().get(0);
		assertThat(cmsmanager.getName(), is(CMSUserGroupRestrictionModelMother.NAME_CMSMANAGER));

		final AbstractRestrictionData nextWeek = restrictionsList.getRestrictions().get(1);
		assertThat(nextWeek.getName(), is(CMSTimeRestrictionModelMother.NAME_NEXT_WEEK));

		final AbstractRestrictionData shoes = restrictionsList.getRestrictions().get(2);
		assertThat(shoes.getName(), is(CMSCategoryRestrictionModelMother.NAME_SHOES));

		final AbstractRestrictionData today = restrictionsList.getRestrictions().get(3);
		assertThat(today.getName(), is(CMSTimeRestrictionModelMother.NAME_TODAY));

		final AbstractRestrictionData tomorrow = restrictionsList.getRestrictions().get(4);
		assertThat(tomorrow.getName(), is(CMSTimeRestrictionModelMother.NAME_TOMORROW));
	}

	@Test
	public void shouldGetAllRestrictions()
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final RestrictionListData restrictionsList = response.readEntity(RestrictionListData.class);
		assertThat(restrictionsList.getRestrictions().size(), is(5));
	}

	@Test
	public void shouldUpdateTimeRestriction() throws JAXBException, ParseException
	{
		final String modifiedName = "Modified";

		Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>()))
				.path(CMSTimeRestrictionModelMother.UID_TODAY)
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		final TimeRestrictionData modifiedRestriction = response.readEntity(TimeRestrictionData.class);

		modifiedRestriction.setName(modifiedName);
		response = getCmsManagerWsSecuredRequestBuilder()
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())) //
				.path(CMSTimeRestrictionModelMother.UID_TODAY).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.put(Entity.entity(marshallDto(modifiedRestriction, TimeRestrictionData.class), MediaType.APPLICATION_JSON));

		assertResponse(Status.OK, response);
		final TimeRestrictionData result = response.readEntity(TimeRestrictionData.class);
		assertThat(result.getName(), equalTo(modifiedName));
	}

	@Test
	public void shouldUpdateTimeRestrictionWithoutChangingName() throws JAXBException, ParseException
	{
		final Date activeFrom = simpleDateFormat.parse("2016-11-25 00:00:00");
		final Date activeUntil = simpleDateFormat.parse("2016-11-26 00:00:00");

		Response response = getTodayTimeRestriction();

		final TimeRestrictionData modifiedRestriction = response.readEntity(TimeRestrictionData.class);

		modifiedRestriction.setActiveFrom(activeFrom);
		modifiedRestriction.setActiveUntil(activeUntil);

		response = getCmsManagerWsSecuredRequestBuilder()
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())) //
				.path(CMSTimeRestrictionModelMother.UID_TODAY).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.put(Entity.entity(marshallDto(modifiedRestriction, TimeRestrictionData.class), MediaType.APPLICATION_JSON));

		assertResponse(Status.OK, response);
		final TimeRestrictionData result = response.readEntity(TimeRestrictionData.class);
		assertThat(result.getName(), equalTo(CMSTimeRestrictionModelMother.NAME_TODAY));
		assertThat(result.getActiveFrom().compareTo(activeFrom), equalTo(0));
		assertThat(result.getActiveUntil().compareTo(activeUntil), equalTo(0));
	}

	@Test
	public void shouldFailUpdateTimeRestrictionWithDuplicateName() throws JAXBException, ParseException
	{

		Response response = getTodayTimeRestriction();

		final TimeRestrictionData modifiedRestriction = response.readEntity(TimeRestrictionData.class);
		final Date activeFrom = simpleDateFormat.parse("2016-11-25 00:00:00");
		final Date activeUntil = simpleDateFormat.parse("2016-11-26 00:00:00");
		modifiedRestriction.setActiveFrom(activeFrom);
		modifiedRestriction.setActiveUntil(activeUntil);

		response = getCmsManagerWsSecuredRequestBuilder()
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())) //
				.path(CMSTimeRestrictionModelMother.UID_TOMORROW).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.put(Entity.entity(marshallDto(modifiedRestriction, TimeRestrictionData.class), MediaType.APPLICATION_JSON));

		assertResponse(Status.BAD_REQUEST, response);
	}

	@Test
	public void shouldFailUpdateTimeRestrictionWithChangedId() throws JAXBException, ParseException
	{

		Response response = getTodayTimeRestriction();

		final TimeRestrictionData modifiedRestriction = response.readEntity(TimeRestrictionData.class);
		modifiedRestriction.setUid("newID");

		response = getCmsManagerWsSecuredRequestBuilder()
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())) //
				.path(CMSTimeRestrictionModelMother.UID_TOMORROW).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.put(Entity.entity(marshallDto(modifiedRestriction, TimeRestrictionData.class), MediaType.APPLICATION_JSON));

		assertResponse(Status.BAD_REQUEST, response);
	}

	@Test
	public void shouldUpdateCategoryRestriction() throws JAXBException, ParseException
	{
		final String modifiedName = "Modified";

		Response response = getShoesCategoryRestriction();

		final CategoryRestrictionData modifiedRestriction = response.readEntity(CategoryRestrictionData.class);

		modifiedRestriction.setName(modifiedName);
		//Composite keys are generated thus we need to get the generated composite key for the sandals category, which is at the first index
		modifiedRestriction.setCategories(Arrays.asList(modifiedRestriction.getCategories().get(0)));

		response = getCmsManagerWsSecuredRequestBuilder()
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<String, String>())) //
				.path(CMSCategoryRestrictionModelMother.UID_SHOES).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.put(Entity.entity(marshallDto(modifiedRestriction, CategoryRestrictionData.class), MediaType.APPLICATION_JSON));

		assertResponse(Status.OK, response);
		final CategoryRestrictionData result = response.readEntity(CategoryRestrictionData.class);
		assertThat(result.getName(), equalTo(modifiedName));
		assertThat(result.getCategories().size(), equalTo(1));
		assertThat(result.getCategories().get(0), equalTo(modifiedRestriction.getCategories().get(0)));
		assertThat(result.getDescription(), containsString("Page only applies on categories:"));
		assertThat(result.isRecursive(), equalTo(false));
		assertThat(result.getTypeCode(), is(CMSCategoryRestrictionModel._TYPECODE));
	}

	@Test
	public void shouldFailUpdateCategoryRestrictionEmptyCategoryCodes() throws JAXBException, ParseException
	{
		final String modifiedName = "Modified";

		Response response = getShoesCategoryRestriction();

		final CategoryRestrictionData modifiedRestriction = response.readEntity(CategoryRestrictionData.class);

		modifiedRestriction.setName(modifiedName);
		modifiedRestriction.setCategories(new ArrayList<String>());

		response = getCmsManagerWsSecuredRequestBuilder()
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<String, String>())) //
				.path(CMSCategoryRestrictionModelMother.UID_SHOES).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.put(Entity.entity(marshallDto(modifiedRestriction, CategoryRestrictionData.class), MediaType.APPLICATION_JSON));

		assertResponse(Status.BAD_REQUEST, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors().size(), equalTo(1));
		assertThat(errors.getErrors().get(0).getSubject(), equalTo(CMSCategoryRestrictionModel.CATEGORIES));
		assertThat(errors.getErrors().get(0).getMessage(), equalTo(FIELD_MIN_VIOLATED));
	}

	@Test
	public void shouldFailUpdateCategoryRestrictionNonExistantCategoryCodes() throws JAXBException, ParseException
	{
		Response response = getShoesCategoryRestriction();

		final CategoryRestrictionData modifiedRestriction = response.readEntity(CategoryRestrictionData.class);
		modifiedRestriction.setCategories(Arrays.asList("fail"));

		response = getCmsManagerWsSecuredRequestBuilder()
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<String, String>())) //
				.path(CMSCategoryRestrictionModelMother.UID_SHOES).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.put(Entity.entity(marshallDto(modifiedRestriction, CategoryRestrictionData.class), MediaType.APPLICATION_JSON));

		assertResponse(Status.BAD_REQUEST, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors().size(), equalTo(1));
		assertThat(errors.getErrors().get(0).getSubject(), equalTo(CMSCategoryRestrictionModel.CATEGORIES));
		assertThat(errors.getErrors().get(0).getMessage(), equalTo(FIELD_DOES_NOT_EXIST));
	}

	@Test
	public void shouldFindTimeRestrictionById()
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>()))
				.path(CMSTimeRestrictionModelMother.UID_TODAY)
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final AbstractRestrictionData restrictionData = response.readEntity(AbstractRestrictionData.class);
		assertThat(restrictionData.getUid(), equalTo(CMSTimeRestrictionModelMother.UID_TODAY));
	}

	@Test
	public void shouldFindCategoryRestrictionById()
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<String, String>())) //
				.path(CMSCategoryRestrictionModelMother.UID_SHOES).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final CategoryRestrictionData restrictionData = response.readEntity(CategoryRestrictionData.class);
		assertThat(restrictionData.getTypeCode(), is(CMSCategoryRestrictionModel._TYPECODE));
		assertThat(restrictionData.getUid(), equalTo(CMSCategoryRestrictionModelMother.UID_SHOES));
		assertThat(restrictionData.getName(), is(CMSCategoryRestrictionModelMother.NAME_SHOES));
		assertThat(restrictionData.getDescription(), containsString("Page only applies on categories:"));
		assertThat(restrictionData.isRecursive(), equalTo(false));
		assertThat(restrictionData.getCategories().size(), equalTo(2));
	}

	@Test
	public void shouldNotFindRestriction_InexistenceId()
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())) //
				.path("nonExistantId").build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.NOT_FOUND, response);
	}

	@Test
	public void shouldCreateTimeRestriction() throws ParseException, JAXBException
	{
		final Date activeFrom = simpleDateFormat.parse("2016-11-25 00:00:00");
		final Date activeUntil = simpleDateFormat.parse("2016-11-26 00:00:00");

		final TimeRestrictionData timeRestriction = createTimeRestriction(BLACK_FRIDAY_TIME_RESTRICTION_NAME, activeFrom,
				activeUntil);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(marshallDto(timeRestriction, TimeRestrictionData.class), MediaType.APPLICATION_JSON));

		assertResponse(Status.CREATED, response);

		final TimeRestrictionData result = response.readEntity(TimeRestrictionData.class);
		assertThat(result.getUid(), containsString(DefaultRestrictionFacade.DEFAULT_UID_PREFIX));
		assertThat(result.getDescription(), containsString("Page only applies from"));
		assertThat(result.getName(), equalTo(BLACK_FRIDAY_TIME_RESTRICTION_NAME));
		assertThat(result.getActiveFrom().compareTo(activeFrom), equalTo(0));
		assertThat(result.getActiveUntil().compareTo(activeUntil), equalTo(0));

		assertThat(response.getHeaderString(CmswebservicesConstants.HEADER_LOCATION), containsString(result.getUid()));
	}

	@Test
	public void shouldCreateTimeAndCategoryRestrictionWithSameName() throws ParseException, JAXBException
	{
		final String name = "restriction-name";
		final Date activeFrom = simpleDateFormat.parse("2016-11-25 00:00:00");
		final Date activeUntil = simpleDateFormat.parse("2016-11-26 00:00:00");

		final TimeRestrictionData timeRestriction = createTimeRestriction(name, activeFrom, activeUntil);

		final Response responseTimeRestriction = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(marshallDto(timeRestriction, TimeRestrictionData.class), MediaType.APPLICATION_JSON));

		assertResponse(Status.CREATED, responseTimeRestriction);
		final TimeRestrictionData resultTimeRestriction = responseTimeRestriction.readEntity(TimeRestrictionData.class);

		final Response response = getShoesCategoryRestriction();

		final CategoryRestrictionData sampleRestrictionData = response.readEntity(CategoryRestrictionData.class);

		final CategoryRestrictionData categoryRestriction = createCategoryRestriction(name, false,
				sampleRestrictionData.getCategories());

		final Response responseCategoryRestriction = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<String, String>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(marshallDto(categoryRestriction, CategoryRestrictionData.class), MediaType.APPLICATION_JSON));

		assertResponse(Status.CREATED, responseCategoryRestriction);
		final CategoryRestrictionData resultCategoryRestriction = responseCategoryRestriction
				.readEntity(CategoryRestrictionData.class);

		assertThat(resultCategoryRestriction.getName(), equalTo(name));
		assertThat(resultTimeRestriction.getName(), equalTo(name));
	}

	@Test
	public void shouldFailCreateTimeRestrictionDuplicateName() throws ParseException, JAXBException
	{
		Response response = getTodayTimeRestriction();

		final TimeRestrictionData modifiedRestriction = response.readEntity(TimeRestrictionData.class);
		final Date activeFrom = simpleDateFormat.parse("2016-11-25 00:00:00");
		final Date activeUntil = simpleDateFormat.parse("2016-11-26 00:00:00");
		modifiedRestriction.setActiveFrom(activeFrom);
		modifiedRestriction.setActiveUntil(activeUntil);
		modifiedRestriction.setUid("newID");

		response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(marshallDto(modifiedRestriction, TimeRestrictionData.class), MediaType.APPLICATION_JSON));

		assertResponse(Status.BAD_REQUEST, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors().size(), equalTo(1));
		assertThat(errors.getErrors().get(0).getSubject(), equalTo(CMSTimeRestrictionModel.NAME));
		assertThat(errors.getErrors().get(0).getMessage(), equalTo(FIELD_ALREADY_EXIST));
	}

	@Test
	public void shouldFailCreateTimeRestrictionMissingRequiredFields() throws ParseException, JAXBException
	{
		final TimeRestrictionData timeRestriction = createTimeRestriction(null, null, null);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(marshallDto(timeRestriction, TimeRestrictionData.class), MediaType.APPLICATION_JSON));

		assertResponse(Status.BAD_REQUEST, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors().size(), equalTo(3));
		assertThat(errors.getErrors().get(0).getSubject(), equalTo(AbstractRestrictionModel.NAME));
		assertThat(errors.getErrors().get(0).getMessage(), equalTo(FIELD_REQUIRED));
		assertThat(errors.getErrors().get(1).getSubject(), equalTo(CMSTimeRestrictionModel.ACTIVEFROM));
		assertThat(errors.getErrors().get(1).getMessage(), equalTo(FIELD_REQUIRED));
		assertThat(errors.getErrors().get(2).getSubject(), equalTo(CMSTimeRestrictionModel.ACTIVEUNTIL));
		assertThat(errors.getErrors().get(2).getMessage(), equalTo(FIELD_REQUIRED));
	}

	@Test
	public void shouldFailCreateTimeRestrictionInvalidDateRange_BeforeDateGreaterThanAfterDate() throws ParseException,
			JAXBException
	{
		final Date activeFrom = simpleDateFormat.parse("2016-11-25 00:00:00");
		final Date activeUntil = simpleDateFormat.parse("2016-11-24 00:00:00");

		final TimeRestrictionData timeRestriction = createTimeRestriction(BLACK_FRIDAY_TIME_RESTRICTION_NAME, activeFrom,
				activeUntil);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(marshallDto(timeRestriction, TimeRestrictionData.class), MediaType.APPLICATION_JSON));

		assertResponse(Status.BAD_REQUEST, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors().size(), equalTo(1));
		assertThat(errors.getErrors().get(0).getSubject(), equalTo(CMSTimeRestrictionModel.ACTIVEUNTIL));
		assertThat(errors.getErrors().get(0).getMessage(), equalTo(INVALID_DATE_RANGE));
	}

	@Test
	public void shouldFailCreateTimeRestrictionInvalidDateRange_SameDate() throws ParseException, JAXBException
	{
		final Date activeFrom = simpleDateFormat.parse("2016-11-25 00:00:00");
		final Date activeUntil = simpleDateFormat.parse("2016-11-25 00:00:00");

		final TimeRestrictionData timeRestriction = createTimeRestriction(BLACK_FRIDAY_TIME_RESTRICTION_NAME, activeFrom,
				activeUntil);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(marshallDto(timeRestriction, TimeRestrictionData.class), MediaType.APPLICATION_JSON));

		assertResponse(Status.BAD_REQUEST, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors().size(), equalTo(1));
		assertThat(errors.getErrors().get(0).getSubject(), equalTo(CMSTimeRestrictionModel.ACTIVEUNTIL));
		assertThat(errors.getErrors().get(0).getMessage(), equalTo(INVALID_DATE_RANGE));
	}

	@Test
	public void shouldCreateCategoryRestriction() throws ParseException, JAXBException
	{
		Response response = getShoesCategoryRestriction();

		final CategoryRestrictionData sampleRestrictionData = response.readEntity(CategoryRestrictionData.class);
		final CategoryRestrictionData categoryRestriction = createCategoryRestriction(ELECTRONIC_CATEGORY_RESTRICTION_NAME, false,
				sampleRestrictionData.getCategories());

		response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<String, String>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(marshallDto(categoryRestriction, CategoryRestrictionData.class), MediaType.APPLICATION_JSON));

		assertResponse(Status.CREATED, response);

		final CategoryRestrictionData result = response.readEntity(CategoryRestrictionData.class);
		assertThat(result.getUid(), containsString(DefaultRestrictionFacade.DEFAULT_UID_PREFIX));
		assertThat(result.getName(), equalTo(ELECTRONIC_CATEGORY_RESTRICTION_NAME));
		assertThat(result.isRecursive(), equalTo(false));
		assertThat(result.getDescription(), containsString("Page only applies on categories:"));
		assertThat(result.getCategories().size(), equalTo(sampleRestrictionData.getCategories().size()));
		assertEquals(result.getCategories(), sampleRestrictionData.getCategories());

		assertThat(response.getHeaderString(CmswebservicesConstants.HEADER_LOCATION), containsString(result.getUid()));
	}

	@Test
	public void shouldFailCreateCategoryRestrictionMissingFields() throws ParseException, JAXBException
	{
		final CategoryRestrictionData categoryRestriction = createCategoryRestriction("test-name", false, null);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<String, String>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(marshallDto(categoryRestriction, CategoryRestrictionData.class), MediaType.APPLICATION_JSON));

		assertResponse(Status.BAD_REQUEST, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors().size(), equalTo(1));
		assertThat(errors.getErrors().get(0).getSubject(), equalTo(CMSCategoryRestrictionModel.CATEGORIES));
		assertThat(errors.getErrors().get(0).getMessage(), equalTo(FIELD_REQUIRED));
	}

	@Test
	public void shouldFailCreateCategoryRestrictionEmptyCategoryCodes() throws ParseException, JAXBException
	{
		final CategoryRestrictionData categoryRestriction = createCategoryRestriction(ELECTRONIC_CATEGORY_RESTRICTION_NAME, false,
				new ArrayList<>());

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<String, String>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(marshallDto(categoryRestriction, CategoryRestrictionData.class), MediaType.APPLICATION_JSON));

		assertResponse(Status.BAD_REQUEST, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors().size(), equalTo(1));
		assertThat(errors.getErrors().get(0).getSubject(), equalTo(CMSCategoryRestrictionModel.CATEGORIES));
		assertThat(errors.getErrors().get(0).getMessage(), equalTo(FIELD_MIN_VIOLATED));
	}

	@Test
	public void shouldFailCreateCategoryRestrictionNonExistantCategoryCodes() throws ParseException, JAXBException
	{
		final CategoryRestrictionData categoryRestriction = createCategoryRestriction(ELECTRONIC_CATEGORY_RESTRICTION_NAME, false,
				Arrays.asList("shouldfail"));

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<String, String>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(marshallDto(categoryRestriction, CategoryRestrictionData.class), MediaType.APPLICATION_JSON));

		assertResponse(Status.BAD_REQUEST, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors().size(), equalTo(1));
		assertThat(errors.getErrors().get(0).getSubject(), equalTo(CMSCategoryRestrictionModel.CATEGORIES));
		assertThat(errors.getErrors().get(0).getMessage(), equalTo(FIELD_DOES_NOT_EXIST));
	}

	@Test
	public void shouldCreateUserGroupRestriction() throws ParseException, JAXBException
	{
		//prepare
		Response response = getManagerUserGroupRestriction();

		final UserGroupRestrictionData sampleRestrictionData = response.readEntity(UserGroupRestrictionData.class);
		assertThat(sampleRestrictionData.getUserGroups().size(), is(2));

		final UserGroupRestrictionData userGroupRestriction = createUserGroupRestriction(MANAGERS_ONLY_USER_GROUP_RESTRICTION,
				false,
				sampleRestrictionData.getUserGroups());

		//execute
		response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(marshallDto(userGroupRestriction, UserGroupRestrictionData.class), MediaType.APPLICATION_JSON));

		//assert
		assertResponse(Status.CREATED, response);
		final UserGroupRestrictionData result = response.readEntity(UserGroupRestrictionData.class);
		assertThat(result.getUid(), containsString(DefaultRestrictionFacade.DEFAULT_UID_PREFIX));
		assertThat(result.getName(), equalTo(MANAGERS_ONLY_USER_GROUP_RESTRICTION));
		assertThat(result.isIncludeSubgroups(), is(false));
		assertThat(result.getDescription(), containsString("Page only applies on usergroups:"));
		assertThat(result.getUserGroups().size(), equalTo(sampleRestrictionData.getUserGroups().size()));
		assertEquals(result.getUserGroups(), sampleRestrictionData.getUserGroups());
		assertThat(response.getHeaderString(CmswebservicesConstants.HEADER_LOCATION), containsString(result.getUid()));
	}

	@Test
	public void shouldUpdateUserGroupRestriction() throws ParseException, JAXBException
	{
		//prepare
		final String modifiedName = "Modified";

		Response response = getManagerUserGroupRestriction();

		final UserGroupRestrictionData modifiedRestriction = response.readEntity(UserGroupRestrictionData.class);
		modifiedRestriction.setName(modifiedName);
		modifiedRestriction.setUserGroups(Arrays.asList(modifiedRestriction.getUserGroups().get(0)));

		//execute
		response = getCmsManagerWsSecuredRequestBuilder()
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())) //
				.path(CMSUserGroupRestrictionModelMother.UID_CMSMANAGER).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.put(Entity.entity(marshallDto(modifiedRestriction, UserGroupRestrictionData.class), MediaType.APPLICATION_JSON));

		//assert
		assertResponse(Status.OK, response);
		final UserGroupRestrictionData result = response.readEntity(UserGroupRestrictionData.class);
		assertThat(result.getName(), is(modifiedName));
		assertThat(result.getUserGroups().size(), is(1));
		assertThat(result.getUserGroups().get(0), is(modifiedRestriction.getUserGroups().get(0)));
		assertThat(result.getDescription(), containsString("Page only applies on usergroups:"));
		assertThat(result.isIncludeSubgroups(), is(false));
		assertThat(result.getTypeCode(), is(CMSUserGroupRestrictionModel._TYPECODE));
	}

	@Test
	public void shouldFailCreatingUserGroupRestrictionWithDuplicateName() throws ParseException, JAXBException
	{
		//prepare
		Response response = getManagerUserGroupRestriction();

		final UserGroupRestrictionData newRestrictionData = response.readEntity(UserGroupRestrictionData.class);
		newRestrictionData.setUserGroups(Arrays.asList(newRestrictionData.getUserGroups().get(0)));
		newRestrictionData.setUid("newUid");

		//execute
		response = getCmsManagerWsSecuredRequestBuilder()
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())).build()
				.accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(marshallDto(newRestrictionData, UserGroupRestrictionData.class), MediaType.APPLICATION_JSON));

		//assert
		assertResponse(Status.BAD_REQUEST, response);
		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);

		assertThat(errors.getErrors().size(), is(1));
		assertThat(errors.getErrors().get(0).getSubject(), is(CMSUserGroupRestrictionModel.NAME));
		assertThat(errors.getErrors().get(0).getMessage(), is(FIELD_ALREADY_EXIST));
	}

	@Test
	public void shouldFailUpdatingUserGroupRestrictionWithChangedUid() throws ParseException, JAXBException
	{
		//prepare
		Response response = getManagerUserGroupRestriction();

		final UserGroupRestrictionData newRestrictionData = response.readEntity(UserGroupRestrictionData.class);
		newRestrictionData.setUserGroups(Arrays.asList(newRestrictionData.getUserGroups().get(0)));
		newRestrictionData.setUid("newUid");

		//execute
		response = getCmsManagerWsSecuredRequestBuilder()
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>()))
				.path(CMSUserGroupRestrictionModelMother.UID_CMSMANAGER).build()
				.accept(MediaType.APPLICATION_JSON)
				.put(Entity.entity(marshallDto(newRestrictionData, UserGroupRestrictionData.class), MediaType.APPLICATION_JSON));

		//assert
		assertResponse(Status.BAD_REQUEST, response);
		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);

		assertThat(errors.getErrors().size(), is(1));
		assertThat(errors.getErrors().get(0).getSubject(), is(CMSUserGroupRestrictionModel.UID));
		assertThat(errors.getErrors().get(0).getMessage(), is(FIELD_NOT_ALLOWED));
	}

	@Test
	public void shouldFailCreatingUserGroupRestrictionWithEmptyKeys() throws ParseException, JAXBException
	{
		//prepare
		Response response = getManagerUserGroupRestriction();

		final UserGroupRestrictionData sampleRestrictionData = response.readEntity(UserGroupRestrictionData.class);
		final UserGroupRestrictionData userGroupRestriction = createUserGroupRestriction(MANAGERS_ONLY_USER_GROUP_RESTRICTION,
				false,
				new ArrayList<>());

		//execute
		response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(marshallDto(userGroupRestriction, UserGroupRestrictionData.class), MediaType.APPLICATION_JSON));


		assertResponse(Status.BAD_REQUEST, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors().size(), equalTo(1));
		assertThat(errors.getErrors().get(0).getSubject(), equalTo(CMSUserGroupRestrictionModel.USERGROUPS));
		assertThat(errors.getErrors().get(0).getMessage(), equalTo(FIELD_MIN_VIOLATED));
	}

	@Test
	public void shouldFailUpdatingUserGroupRestrictionWithEmptyKeys() throws ParseException, JAXBException
	{
		//prepare
		Response response = getManagerUserGroupRestriction();

		final UserGroupRestrictionData modifiedRestriction = response.readEntity(UserGroupRestrictionData.class);
		modifiedRestriction.setUserGroups(new ArrayList<>());

		//execute
		response = getCmsManagerWsSecuredRequestBuilder()
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())) //
				.path(CMSUserGroupRestrictionModelMother.UID_CMSMANAGER).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.put(Entity.entity(marshallDto(modifiedRestriction, UserGroupRestrictionData.class), MediaType.APPLICATION_JSON));

		//assert
		assertResponse(Status.BAD_REQUEST, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors().size(), equalTo(1));
		assertThat(errors.getErrors().get(0).getSubject(), equalTo(CMSUserGroupRestrictionModel.USERGROUPS));
		assertThat(errors.getErrors().get(0).getMessage(), equalTo(FIELD_MIN_VIOLATED));
	}

	@Test
	public void shouldFailUpdatingUserGroupRestrictionNonExistentKeys() throws ParseException, JAXBException
	{
		//prepare
		Response response = getManagerUserGroupRestriction();

		final UserGroupRestrictionData modifiedRestriction = response.readEntity(UserGroupRestrictionData.class);
		modifiedRestriction.setUserGroups(Arrays.asList("fake-uid-1", "fake-uid-2"));

		//execute
		response = getCmsManagerWsSecuredRequestBuilder()
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())) //
				.path(CMSUserGroupRestrictionModelMother.UID_CMSMANAGER).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.put(Entity.entity(marshallDto(modifiedRestriction, UserGroupRestrictionData.class), MediaType.APPLICATION_JSON));

		//assert
		assertResponse(Status.BAD_REQUEST, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors().size(), equalTo(2));
		assertThat(errors.getErrors().get(0).getSubject(), equalTo(CMSUserGroupRestrictionModel.USERGROUPS));
		assertThat(errors.getErrors().get(0).getMessage(), equalTo(FIELD_DOES_NOT_EXIST));
	}

	@Test
	public void shouldFindUserGroupRestrictionById()
	{
		//execute
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())) //
				.path(CMSUserGroupRestrictionModelMother.UID_CMSMANAGER).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		//assert
		assertResponse(Status.OK, response);

		final UserGroupRestrictionData restrictionData = response.readEntity(UserGroupRestrictionData.class);
		assertThat(restrictionData.getTypeCode(), is(CMSUserGroupRestrictionModel._TYPECODE));
		assertThat(restrictionData.getUid(), equalTo(CMSUserGroupRestrictionModelMother.UID_CMSMANAGER));
		assertThat(restrictionData.getName(), is(CMSUserGroupRestrictionModelMother.NAME_CMSMANAGER));
		assertThat(restrictionData.getDescription(), containsString("Page only applies on usergroups:"));
		assertThat(restrictionData.isIncludeSubgroups(), equalTo(false));
		assertThat(restrictionData.getUserGroups().size(), equalTo(2));
		assertThat(restrictionData.getUserGroups(),
				contains(UserGroupModelMother.CUSTOMER_GROUP_ID, UserGroupModelMother.CMSMANAGER_GROUP_ID));
	}

	@Test
	public void shouldFailCreateUserGroupRestrictionMissingFields() throws ParseException, JAXBException
	{
		//prepare
		final UserGroupRestrictionData userGroupRestriction = createUserGroupRestriction("test-name", false, null);

		//execute
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(marshallDto(userGroupRestriction, UserGroupRestrictionData.class), MediaType.APPLICATION_JSON));

		//assert
		assertResponse(Status.BAD_REQUEST, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors().size(), equalTo(1));
		assertThat(errors.getErrors().get(0).getSubject(), equalTo(CMSUserGroupRestrictionModel.USERGROUPS));
		assertThat(errors.getErrors().get(0).getMessage(), equalTo(FIELD_REQUIRED));
	}

	@Test
	public void shouldFailCreatingUserGroupRestrictionNonExistentKeys() throws ParseException, JAXBException
	{
		//prepare
		Response response = getManagerUserGroupRestriction();

		final UserGroupRestrictionData modifiedRestriction = response.readEntity(UserGroupRestrictionData.class);
		modifiedRestriction.setUserGroups(Arrays.asList("fake-uid-1", "fake-uid-2"));
		modifiedRestriction.setUid("newUid-01");
		modifiedRestriction.setName("newName-01");

		//execute
		response = getCmsManagerWsSecuredRequestBuilder()
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.post(Entity.entity(marshallDto(modifiedRestriction, UserGroupRestrictionData.class), MediaType.APPLICATION_JSON));

		//assert
		assertResponse(Status.BAD_REQUEST, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors().size(), equalTo(2));
		assertThat(errors.getErrors().get(0).getSubject(), equalTo(CMSUserGroupRestrictionModel.USERGROUPS));
		assertThat(errors.getErrors().get(0).getMessage(), equalTo(FIELD_DOES_NOT_EXIST));
	}


	@Test
	public void shouldSearchAndReturnFullFirstPage_noFilterAndSortByName()
	{
		final RestrictionListData restrictionsList = executeGetPageOfTimeRestrictions(null, 2, 0, SORT_NAME_ASC);

		assertThat("page 0 (no filter, no sort) failed to return a full page", //
				restrictionsList.getRestrictions().size(), equalTo(2));
		assertThat("page 0 (no filter, no sort) failed to return the maximum number of results",
				restrictionsList.getPagination().getTotalCount(), equalTo(3L));

		final List<String> orderedUids = restrictionsList.getRestrictions().stream().map(e -> e.getUid()).collect(toList());
		assertThat(orderedUids,
				contains(CMSTimeRestrictionModelMother.UID_NEXT_WEEK, CMSTimeRestrictionModelMother.UID_TODAY));
	}

	@Test
	public void shouldSearchAndReturnPartialSecondPage_noFilterAndSortByName()
	{
		final RestrictionListData restrictionsList = executeGetPageOfTimeRestrictions(null, 2, 1, SORT_NAME_ASC);

		assertThat("page 0 (no filter, name sort) failed to return a full page", //
				restrictionsList.getRestrictions().size(), equalTo(1));
		assertThat("page 0 (no filter, name sort) failed to return the maximum number of results",
				restrictionsList.getPagination().getTotalCount(), equalTo(3L));

		final String uid = restrictionsList.getRestrictions().stream().map(e -> e.getUid()).findFirst().get();
		assertThat(uid, equalTo(CMSTimeRestrictionModelMother.UID_TOMORROW));
	}

	@Test
	public void shouldSearchAndReturnFullFirstPage_withFilterAndSortByName()
	{
		final RestrictionListData restrictionsList = executeGetPageOfTimeRestrictions("tO", 2, 0, SORT_NAME_ASC);

		assertThat("page 0 (filter, name sort) failed to return a full page", //
				restrictionsList.getRestrictions().size(), equalTo(2));
		assertThat("page 0 (filter, name sort) failed to return the maximum number of results",
				restrictionsList.getPagination().getTotalCount(), equalTo(2L));

		final List<String> orderedUids = restrictionsList.getRestrictions().stream().map(e -> e.getUid()).collect(toList());
		assertThat(orderedUids, contains(CMSTimeRestrictionModelMother.UID_TODAY, CMSTimeRestrictionModelMother.UID_TOMORROW));
	}

	@Test
	public void shouldSearchAndReturnNoResult_withFilterAndSortByName()
	{
		final RestrictionListData restrictionsList = executeGetPageOfTimeRestrictions("test", 2, 0, SORT_NAME_ASC);

		assertThat("page 0 (filter, name sort) failed to return a full page", //
				restrictionsList.getRestrictions(), empty());
		assertThat("page 0 (filter, name sort) failed to return the maximum number of results",
				restrictionsList.getPagination().getTotalCount(), equalTo(0L));
	}

	protected RestrictionListData executeGetPageOfTimeRestrictions(final String mask, final int pageSize, final int currentPage,
			final String sort)
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>()))
				.queryParam(MASK_PARAMETER, mask) //
				.queryParam(PAGE_SIZE_PARAMETER, String.valueOf(pageSize)) //
				.queryParam(CURRENT_PAGE_PARAMETER, String.valueOf(currentPage)) //
				.queryParam(PARAMS_PARAMETER, "typeCode:" + CMSTimeRestrictionModel._TYPECODE)
				.queryParam(SORT_PARAMETER, sort).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		return response.readEntity(RestrictionListData.class);
	}

	protected Response getShoesCategoryRestriction()
	{
		return getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<String, String>())) //
				.path(CMSCategoryRestrictionModelMother.UID_SHOES).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();
	}

	protected Response getTodayTimeRestriction()
	{
		return getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>()))
				.path(CMSTimeRestrictionModelMother.UID_TODAY)
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();
	}

	protected Response getManagerUserGroupRestriction()
	{
		return getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())) //
				.path(CMSUserGroupRestrictionModelMother.UID_CMSMANAGER).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();
	}

	protected CategoryRestrictionData createCategoryRestriction(final String name, final boolean recursive,
			final List<String> categoryCodes)
	{
		final CategoryRestrictionData categoryRestriction = new CategoryRestrictionData();
		categoryRestriction.setTypeCode(CMSCategoryRestrictionModel._TYPECODE);
		categoryRestriction.setName(name);
		categoryRestriction.setRecursive(recursive);
		categoryRestriction.setCategories(categoryCodes);

		return categoryRestriction;
	}

	protected UserGroupRestrictionData createUserGroupRestriction(final String name, final boolean includeSubgroups,
			final List<String> userGroupKeys)
	{
		final UserGroupRestrictionData userGroupRestrictionData = new UserGroupRestrictionData();
		userGroupRestrictionData.setTypeCode(CMSUserGroupRestrictionModel._TYPECODE);
		userGroupRestrictionData.setName(name);
		userGroupRestrictionData.setIncludeSubgroups(includeSubgroups);
		userGroupRestrictionData.setUserGroups(userGroupKeys);
		return userGroupRestrictionData;
	}

	protected TimeRestrictionData createTimeRestriction(final String name, final Date activeFrom, final Date activeUntil)
	{
		final TimeRestrictionData timeRestriction = new TimeRestrictionData();
		timeRestriction.setTypeCode(CMSTimeRestrictionModel._TYPECODE);
		timeRestriction.setName(name);
		timeRestriction.setActiveFrom(activeFrom);
		timeRestriction.setActiveUntil(activeUntil);

		return timeRestriction;
	}

}
