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

import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.API_VERSION;

import de.hybris.platform.cms2.common.annotations.HybrisDeprecation;
import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.header.LocationHeaderResource;
import de.hybris.platform.cmsfacades.pages.PageFacade;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.AbstractPageData;
import de.hybris.platform.cmswebservices.data.PageListData;
import de.hybris.platform.cmswebservices.data.UidListData;
import de.hybris.platform.cmswebservices.dto.PageableWsDTO;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.pagination.WebPaginationUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
 * Controller to deal with AbstractPageModel objects
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping(API_VERSION + "/sites/{siteId}/catalogs/{catalogId}/versions/{versionId}/pages")
public class PageController
{
	private static Logger LOG = LoggerFactory.getLogger(PageController.class);

	@Resource
	private LocationHeaderResource locationHeaderResource;

	@Resource
	private PageFacade cmsPageFacade;

	@Resource
	private DataMapper dataMapper;

	@Resource
	private WebPaginationUtils webPaginationUtils;

	@Deprecated
	@HybrisDeprecation(sinceVersion = "6.6")
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Find all pages", notes = "Find all pages. Deprecated since 6.6, please use "
			+ "GET /v1/sites/{siteId}/cmsitems{?mask,typeCode,catalogId,catalogVersion,itemSearchParams,sort,pageSize,currentPage} instead.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "All pages", response = PageListData.class)
	})
	public PageListData findAllPages()
	{
		final PageListData pageListData = new PageListData();
		final List<AbstractPageData> convertedPages = getDataMapper().mapAsList(getCmsPageFacade().findAllPages(),
				AbstractPageData.class, null);
		pageListData.setPages(convertedPages);
		return pageListData;
	}

	@Deprecated
	@HybrisDeprecation(sinceVersion = "6.6")
	@RequestMapping(method = RequestMethod.GET, params =
{ "pageSize" })
	@ResponseBody
	@ApiOperation(value = "Find pages by mask", notes = "Find CMS Pages using a free mask search field. Deprecated since 6.6, "
			+ "please use GET /v1/sites/{siteId}/cmsitems{?mask,typeCode,catalogId,catalogVersion,itemSearchParams,sort,pageSize,currentPage} instead.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "DTO which serves as a wrapper object that contains a list of AbstractPageData; never null", response = PageListData.class)
	})
	@ApiImplicitParams({
		@ApiImplicitParam(name = "pageSize", value = "The maximum number of elements in the result list.", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "currentPage", value = "The requested page number", required = false, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "sort", value = "The string field the results will be sorted with", required = false, dataType = "string", paramType = "query")
	})
	public PageListData findPagesByMask(
			@ApiParam(value = "The string value on which CMS Pages will be filtered", required = false) @RequestParam(required = false) final String mask,
			@ApiParam(value = "The type code of a pages to be filtered", required = false) @RequestParam(required = false) final String typeCode,
			@ApiParam(value = "PageableWsDTO", required = true) @ModelAttribute final PageableWsDTO pageableDto)
	{
		final SearchResult<de.hybris.platform.cmsfacades.data.AbstractPageData> pageSearchResult = getCmsPageFacade()
				.findPagesByMaskAndTypeCode(mask, typeCode,
						Optional.of(pageableDto).map(pageableWsDTO -> getDataMapper().map(pageableWsDTO, PageableData.class)).get());

		final PageListData pages = new PageListData();
		pages.setPages(getDataMapper().mapAsList(pageSearchResult.getResult(), AbstractPageData.class, null));
		pages.setPagination(getWebPaginationUtils().buildPagination(pageSearchResult));
		return pages;
	}

	@Deprecated
	@HybrisDeprecation(sinceVersion = "6.6")
	@RequestMapping(method = RequestMethod.GET, params =
{ "uids" })
	@ResponseBody
	@ApiOperation(value = "Find pages by ids", notes = "Find specific pages. Deprecated since 6.6, please use "
			+ "GET /v1/sites/{siteId}/cmsitems{?uuids} instead.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "List of AbstractPageData", response = PageListData.class)
	})
	public PageListData findPagesByIds(
			@ApiParam(value = "List of identifier of the pages that we are looking for", required = true) @RequestParam("uids") final List<String> uids)
	{
		final List<de.hybris.platform.cmsfacades.data.AbstractPageData> pages = getCmsPageFacade().findAllPages().stream()
				.filter(page -> uids.contains(page.getUid())).collect(Collectors.toList());

		final PageListData pageListData = new PageListData();
		pageListData.setPages(getDataMapper().mapAsList(pages, AbstractPageData.class, null));
		return pageListData;
	}

	@Deprecated
	@HybrisDeprecation(sinceVersion = "6.6")
	@RequestMapping(value = "/{pageId}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Get page by uid", notes = "Get the page that matches the given page uid. Deprecated since 6.6, please use "
			+ "GET /v1/sites/{siteId}/cmsitems/{uuid} instead.")
	@ApiResponses({
		@ApiResponse(code = 400, message = "When the page cannot be found (CMSItemNotFoundException)."),
		@ApiResponse(code = 200, message = "Page data object", response = AbstractPageData.class)
	})
	public AbstractPageData getPageByUid(
			@ApiParam(value = "Page identifier", required = true) @PathVariable final String pageId) throws CMSItemNotFoundException
	{
		return getDataMapper().map(getCmsPageFacade().getPageByUid(pageId), AbstractPageData.class);
	}

	@Deprecated
	@HybrisDeprecation(sinceVersion = "6.6")
	@RequestMapping(method = RequestMethod.GET, params =
{ "typeCode", "defaultPage" })
	@ResponseBody
	@ApiOperation(value = "Find pages by type", notes = "Get all default or variation pages that matches the given page type. "
			+ "Deprecated since 6.6, please use "
			+ "GET /v1/sites/{siteId}/cmsitems{?mask,typeCode,catalogId,catalogVersion,itemSearchParams,sort,pageSize,currentPage} instead.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "All default or variation pages for a given page type", response = PageListData.class)
	})
	public PageListData findPagesByType(
			@ApiParam(value = "The type code of a page", required = true) @RequestParam("typeCode") final String typeCode,
			@ApiParam(value = "Setting this to true will find all default pages; otherwise find all variation pages", required = true) @RequestParam("defaultPage") final Boolean isDefaultPage)
	{
		final List<de.hybris.platform.cmsfacades.data.AbstractPageData> pages = getCmsPageFacade().findPagesByType(typeCode,
				isDefaultPage);

		final PageListData pageListData = new PageListData();
		pageListData.setPages(getDataMapper().mapAsList(pages, AbstractPageData.class, null));
		return pageListData;
	}

	@RequestMapping(value = "/{pageId}/variations", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Find variation pages", notes = "Get all variation pages uid for a given page.")
	@ApiResponses({
		@ApiResponse(code = 400, message = "When the pageId is invalid (CMSItemNotFoundException)."),
		@ApiResponse(code = 200, message = "All variation pages uid for a given page; empty if the given page is already a variation page; never null.", response = UidListData.class)
	})
	public UidListData findVariationPages(
			@ApiParam(value = "The page identifier", required = true) @PathVariable final String pageId) throws CMSItemNotFoundException
	{
		return convertToUidListData(getCmsPageFacade().findVariationPages(pageId));
	}

	@RequestMapping(value = "/{pageId}/fallbacks", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Find fallback pages", notes = "Get all fallback pages uid for a given page.")
	@ApiResponses({
		@ApiResponse(code = 400, message = "When the pageId is invalid (CMSItemNotFoundException)."),
		@ApiResponse(code = 200, message = "All fallback pages uid for a given page; empty if the given page is already a fallback page; never null", response = UidListData.class)
	})
	public UidListData findFallbackPages(
			@ApiParam(value = "The page identifier", required = true) @PathVariable final String pageId) throws CMSItemNotFoundException
	{
		return convertToUidListData(getCmsPageFacade().findFallbackPages(pageId));
	}

	protected UidListData convertToUidListData(final List<String> pageIds)
	{
		final UidListData pageData = new UidListData();
		pageData.setUids(pageIds);
		return pageData;
	}

	@Deprecated
	@HybrisDeprecation(sinceVersion = "6.6")
	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.CREATED)
	@ResponseBody
	@ApiOperation(value = "Create page", notes = "Create a new page. Deprecated since 6.6, please use "
			+ "POST /v1/sites/{siteId}/cmsitems instead.")
	@ApiResponses({
		@ApiResponse(code = 400, message = "If there is any validation error (WebserviceValidationException)."),
		@ApiResponse(code = 200, message = "Page data object", response = AbstractPageData.class)
	})
	public AbstractPageData createPage(
			@ApiParam(value = "Page data object", required = true) @RequestBody final AbstractPageData pageData,
			final HttpServletRequest request,
			final HttpServletResponse response)
	{
		try
		{
			final de.hybris.platform.cmsfacades.data.AbstractPageData convertedPageData = getDataMapper().map(pageData,
					de.hybris.platform.cmsfacades.data.AbstractPageData.class);
			final de.hybris.platform.cmsfacades.data.AbstractPageData createPage = getCmsPageFacade().createPage(convertedPageData);

			response.addHeader(CmswebservicesConstants.HEADER_LOCATION,
					getLocationHeaderResource().createLocationForChildResource(request, createPage.getUid()));

			return getDataMapper().map(createPage, AbstractPageData.class);
		}
		catch (final ValidationException e)
		{
			LOG.info("validation exception", e);
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	/**
	 *	@deprecated since 6.6
	 */
	@Deprecated
	@HybrisDeprecation(sinceVersion = "6.6")
	@RequestMapping(value = "/{pageId}", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@ApiOperation(value = "Update page", notes = "Update a page. Deprecated since 6.6, please use"
			+ "PUT /v1/sites/{siteId}/cmsitems/{uuid} instead.")
	@ApiResponses({
		@ApiResponse(code = 400, message = "If there is any validation error (WebserviceValidationException)."),
		@ApiResponse(code = 200, message = "Page data object", response = AbstractPageData.class)
	})
	public AbstractPageData updatePage(
			@ApiParam(value = "Page identifier", required = true) @PathVariable final String pageId,
			@ApiParam(value = "Page data object", required = true) @RequestBody final AbstractPageData pageData)
	{
		try
		{
			final de.hybris.platform.cmsfacades.data.AbstractPageData convertedPage = getDataMapper().map(pageData,
					de.hybris.platform.cmsfacades.data.AbstractPageData.class);
			final de.hybris.platform.cmsfacades.data.AbstractPageData updatedPage = getCmsPageFacade().updatePage(pageId,
					convertedPage);
			return getDataMapper().map(updatedPage, AbstractPageData.class);
		}
		catch (final ValidationException e)
		{
			LOG.info("valiation exception", e);
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

	protected PageFacade getCmsPageFacade()
	{
		return cmsPageFacade;
	}

	public void setCmsPageFacade(final PageFacade pageFacade)
	{
		this.cmsPageFacade = pageFacade;
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
