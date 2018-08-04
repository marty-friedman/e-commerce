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

import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.API_VERSION;

import de.hybris.platform.cms2.common.annotations.HybrisDeprecation;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.header.LocationHeaderResource;
import de.hybris.platform.cmsfacades.restrictions.RestrictionFacade;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.AbstractRestrictionData;
import de.hybris.platform.cmswebservices.data.NamedQueryData;
import de.hybris.platform.cmswebservices.data.RestrictionListData;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.pagination.WebPaginationUtils;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


/**
 * Controller to manage restrictions.
 *
 * @deprecated since 6.6. Please use {@link de.hybris.platform.cmswebservices.cmsitems.controller.CMSItemController}
 *             instead.
 */
@Deprecated
@HybrisDeprecation(sinceVersion = "6.6")
@Controller
@IsAuthorizedCmsManager
@RequestMapping(API_VERSION + "/sites/{siteId}/catalogs/{catalogId}/versions/{versionId}/restrictions")
public class RestrictionController
{
	private static final Logger LOG = LoggerFactory.getLogger(RestrictionController.class);

	@Resource
	private LocationHeaderResource locationHeaderResource;

	@Resource
	private RestrictionFacade restritionFacade;

	@Resource
	private DataMapper dataMapper;

	@Resource
	private WebPaginationUtils webPaginationUtils;

	@Deprecated
	@HybrisDeprecation(sinceVersion = "6.6")
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Find all restrictions", notes = "Find all restrictions. Deprecated since 6.6. Please use "
			+ "GET /v1/sites/{siteId}/cmsitems{?mask,typeCode,catalogId,catalogVersion,itemSearchParams,sort,pageSize,currentPage} instead.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "The list of restrictions", response = RestrictionListData.class)
	})
	public RestrictionListData findAllRestrictions()
	{
		final List<AbstractRestrictionData> restrictions = getDataMapper().mapAsList(getRestritionFacade().findAllRestrictions(),
				AbstractRestrictionData.class, null);

		final RestrictionListData restrictionListData = new RestrictionListData();
		restrictionListData.setRestrictions(restrictions);
		return restrictionListData;
	}

	@Deprecated
	@HybrisDeprecation(sinceVersion = "6.6")
	@RequestMapping(method = RequestMethod.GET, params = { "pageSize", "currentPage" })
	@ResponseBody
	@ApiOperation(value = "Find restrictions by page", notes = "Find a page of restrictions.  Deprecated since 6.6. Please use "
			+ "GET /v1/sites/{siteId}/cmsitems{?mask,typeCode,catalogId,catalogVersion,itemSearchParams,sort,pageSize,currentPage} instead.")
	@ApiResponses({
		@ApiResponse(code = 400, message = "If there are any validation errors (WebserviceValidationException)"),
		@ApiResponse(code = 200, message = "DTO which serves as a wrapper object that contains a list of {@link RestrictionListData} as well as pagination and sorting pertaining to the request; never null.", response = RestrictionListData.class)
	})
	@ApiImplicitParams({
		@ApiImplicitParam(name = "params", value = "The query parameter values containing the restriction type code.", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "currentpage", value = "The index of the requested page (index 0 means page 1).", required = false, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "pagesize", value = "The number of results per page.", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "sort", value = "The requested ordering for the search results.", required = true, dataType = "string", paramType = "query")
	})
	public RestrictionListData findRestrictionsByPage(
			@ApiParam(value = "The string value on which restrictions will be filtered, business logic may choose to filter on the restriction name.", required = false) @RequestParam(required = false) final String mask,
			@ApiParam(value = "The NamedQueryData", required = true) @ModelAttribute final NamedQueryData namedQuery) throws WebserviceValidationException
	{
		try
		{
			final de.hybris.platform.cmsfacades.data.NamedQueryData convertedNamedQuery = //
					getDataMapper().map(namedQuery, de.hybris.platform.cmsfacades.data.NamedQueryData.class);
			final SearchResult<de.hybris.platform.cmsfacades.data.AbstractRestrictionData> pagedRestrictionData = //
					getRestritionFacade().findRestrictionsByMask(mask, convertedNamedQuery);
			final List<AbstractRestrictionData> convertedRestrictions = getDataMapper().mapAsList(pagedRestrictionData.getResult(),
					AbstractRestrictionData.class, null);

			final RestrictionListData restrictionListData = new RestrictionListData();
			restrictionListData.setRestrictions(convertedRestrictions);
			restrictionListData.setPagination(getWebPaginationUtils().buildPagination(pagedRestrictionData));
			return restrictionListData;
		}
		catch (final ValidationException e)
		{
			LOG.info("valiation exception", e);
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	@Deprecated
	@HybrisDeprecation(sinceVersion = "6.6")
	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.CREATED)
	@ResponseBody
	@ApiOperation(value = "Create restriction", notes = "Create a new restriction. Deprecated since 6.6, please use "
			+ "POST /v1/sites/{siteId}/cmsitems instead.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "Restriction data", response = AbstractRestrictionData.class)
	})
	public AbstractRestrictionData createRestriction(
			@ApiParam(value = "Restriction data", required = true) @RequestBody final AbstractRestrictionData restrictionData,
			final HttpServletRequest request,
			final HttpServletResponse response)
	{
		try
		{
			final de.hybris.platform.cmsfacades.data.AbstractRestrictionData convertedRestriction = getDataMapper()
					.map(restrictionData, de.hybris.platform.cmsfacades.data.AbstractRestrictionData.class);
			final de.hybris.platform.cmsfacades.data.AbstractRestrictionData restriction = getRestritionFacade()
					.createRestriction(convertedRestriction);

			response.addHeader(CmswebservicesConstants.HEADER_LOCATION,
					getLocationHeaderResource().createLocationForChildResource(request, restriction.getUid()));

			return getDataMapper().map(restriction, AbstractRestrictionData.class);
		}
		catch (final ValidationException e)
		{
			LOG.info("valiation exception", e);
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	@Deprecated
	@HybrisDeprecation(sinceVersion = "6.6")
	@RequestMapping(value = "/{restrictionId}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Find restriction by id", notes = "Get a restriction that corresponds to a given restriction id. "
			+ "Deprecated since 6.6, please use GET /v1/sites/{siteId}/cmsitems/{uuid} instead.")
	@ApiResponses({
		@ApiResponse(code = 400, message = "When the corresponding restriction to the id does not exist (CMSItemNotFoundException)."),
		@ApiResponse(code = 200, message = "Restriction data", response = AbstractRestrictionData.class)
	})
	public AbstractRestrictionData findRestrictionById(
			@ApiParam(value = "The restriction identifier", required = true) @PathVariable final String restrictionId) throws CMSItemNotFoundException
	{
		return getDataMapper().map(getRestritionFacade().findRestrictionById(restrictionId), AbstractRestrictionData.class);
	}

	@Deprecated
	@HybrisDeprecation(sinceVersion = "6.6")
	@RequestMapping(value = "/{restrictionId}", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@ApiOperation(value = "Update restriction", notes = "Update a restriction. Deprecated since 6.6, please use"
			+ "PUT /v1/sites/{siteId}/cmsitems/{uuid} instead.")
	@ApiResponses({
			@ApiResponse(code = 400, message = "If there validation errors (WebserviceValidationException) "
					+ "or when the corresponding restriction to the id does not exist (CMSItemNotFoundException)."),
		@ApiResponse(code = 200, message = "Updated restriction", response = AbstractRestrictionData.class)
	})
	public AbstractRestrictionData updateRestriction(
			@ApiParam(value = "Restriction identifier", required = true) @PathVariable final String restrictionId,
			@ApiParam(value = "Restriction data", required = true) @RequestBody final AbstractRestrictionData restrictionData)
					throws WebserviceValidationException, CMSItemNotFoundException
	{
		try
		{
			final de.hybris.platform.cmsfacades.data.AbstractRestrictionData convertedRestriction = getDataMapper()
					.map(restrictionData, de.hybris.platform.cmsfacades.data.AbstractRestrictionData.class);
			final de.hybris.platform.cmsfacades.data.AbstractRestrictionData updatedRestriction = getRestritionFacade()
					.updateRestriction(restrictionId, convertedRestriction);
			return getDataMapper().map(updatedRestriction, AbstractRestrictionData.class);
		}
		catch (final ValidationException e)
		{
			LOG.info("validation exception", e);
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	protected LocationHeaderResource getLocationHeaderResource()
	{
		return locationHeaderResource;
	}

	public void setLocationHeaderResource(final LocationHeaderResource locationHeaderResource)
	{
		this.locationHeaderResource = locationHeaderResource;
	}

	public RestrictionFacade getRestritionFacade()
	{
		return restritionFacade;
	}

	public void setRestritionFacade(final RestrictionFacade restritionFacade)
	{
		this.restritionFacade = restritionFacade;
	}

	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}

	public void setDataMapper(final DataMapper dataMapper)
	{
		this.dataMapper = dataMapper;
	}

	protected WebPaginationUtils getWebPaginationUtils()
	{
		return webPaginationUtils;
	}

	public void setWebPaginationUtils(final WebPaginationUtils webPaginationUtils)
	{
		this.webPaginationUtils = webPaginationUtils;
	}

}
