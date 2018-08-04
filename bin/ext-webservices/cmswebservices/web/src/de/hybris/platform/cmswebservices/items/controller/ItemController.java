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

import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.API_VERSION;

import de.hybris.platform.cms2.common.annotations.HybrisDeprecation;
import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.header.LocationHeaderResource;
import de.hybris.platform.cmsfacades.items.ComponentItemFacade;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.AbstractCMSComponentData;
import de.hybris.platform.cmswebservices.data.ComponentItemListData;
import de.hybris.platform.cmswebservices.dto.PageableWsDTO;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.pagination.WebPaginationUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


/*
 * Suppress sonar warning (squid:S1166 | Exception handlers should preserve the original exceptions) : It is
 * perfectly acceptable not to handle "e" here
 */
@SuppressWarnings("squid:S1166")
/**
 * Controller to deal with component items
 *
 * @deprecated since 6.6. Please use {@link de.hybris.platform.cmswebservices.cmsitems.controller.CMSItemController}
 *             instead
 */
@Deprecated
@HybrisDeprecation(sinceVersion = "6.6")
@Controller
@IsAuthorizedCmsManager
@RequestMapping(API_VERSION + "/sites/{siteId}/catalogs/{catalogId}/versions/{versionId}/items")
public class ItemController
{
	static final String DEFAULT_FIELD_SET = "DEFAULT";

	@Resource
	private ComponentItemFacade componentFacade;

	@Resource
	private LocationHeaderResource locationHeaderResource;

	@Resource
	private DataMapper dataMapper;

	@Resource
	private WebPaginationUtils webPaginationUtils;

	@Deprecated
	@HybrisDeprecation(sinceVersion = "6.6")
	@RequestMapping(method = RequestMethod.GET, params = {})
	@ResponseBody
	@ApiOperation(
			value = "Get all component items",
			notes = "Find all components. By default, the result is ordered by modified time; most recently modified items first. "
					+ "Deprecated since 6.6, please use GET /v1/sites/{siteId}/cmsitems{?mask,typeCode,catalogId,catalogVersion,itemSearchParams,sort,pageSize,currentPage} instead.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "DTO which serves as a wrapper object that contains a list of ComponentItemListData never null", response = ComponentItemListData.class)
	})
	public ComponentItemListData getAllComponentItems(
			@ApiParam(value = "Response configuration (list of fields, which should be returned in the response)") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final List<AbstractCMSComponentData> componentItems = getComponentFacade().getAllComponentItems().stream() //
				.map(component -> getDataMapper().map(component, AbstractCMSComponentData.class)) //
				.collect(Collectors.toList());

		final ComponentItemListData componentDataList = new ComponentItemListData();
		componentDataList.setComponentItems(componentItems);
		return componentDataList;
	}

	@Deprecated
	@HybrisDeprecation(sinceVersion = "6.6")
	@RequestMapping(method = RequestMethod.GET, params = {"pageSize"})
	@ResponseBody
	@ApiOperation(
			value = "Get component items by page",
			notes = "Find a page of components. By default, the result is ordered by modified time; most recently modified items first. "
					+ "Deprecated since 6.6, please use GET /v1/sites/{siteId}/cmsitems{?mask,typeCode,catalogId,catalogVersion,itemSearchParams,sort,pageSize,currentPage} instead.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "DTO which serves as a wrapper object that contains a list of ComponentItemListData as well as pagination and sorting pertaining to the request; never null", response = ComponentItemListData.class)
	})
	public ComponentItemListData getComponentItemsByPage(
			@ApiParam(value = "The string value on which components will be filtered, business logic may choose to filter on the component name") @RequestParam(required = false) final String mask,
			@ApiParam(value = "The pageable data object containing the page request details", required = true) @ModelAttribute final PageableWsDTO pageableDto)
	{
		final PageableData pageableData = getDataMapper().map(pageableDto, PageableData.class);
		final SearchResult<de.hybris.platform.cmsfacades.data.AbstractCMSComponentData> pagedData = //
				getComponentFacade().findComponentByMask(mask, pageableData);

		final List<AbstractCMSComponentData> componentItems = pagedData.getResult().stream() //
				.map(component -> getDataMapper().map(component, AbstractCMSComponentData.class)) //
				.collect(Collectors.toList());

		final ComponentItemListData componentDataList = new ComponentItemListData();
		componentDataList.setComponentItems(componentItems);
		componentDataList.setPagination(getWebPaginationUtils().buildPagination(pagedData));
		return componentDataList;
	}

	@Deprecated
	@HybrisDeprecation(sinceVersion = "6.6")
	@RequestMapping(method = RequestMethod.GET, params = { "uids" })
	@ResponseBody
	@ApiOperation(
			value = "Get all component items for uids",
			notes = "Find components matching the given uids. Deprecated since 6.6, please use GET /v1/sites/{siteId}/cmsitems{?uuids} instead.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "DTO which serves as a wrapper object that contains a list of ComponentItemListData, never null.", response = ComponentItemListData.class)
	})
	public ComponentItemListData getAllComponentItemsForUids(
			@ApiParam(value = "List of uids representing the components to retrieve.", required = true) @RequestParam("uids") final String[] uids)
	{
		final List<String> listUids = Arrays.asList(uids);
		final ComponentItemListData componentDataList = new ComponentItemListData();

		componentDataList.setComponentItems(getComponentFacade().getAllComponentItems().stream() //
				.filter(component -> listUids.contains(component.getUid())) //
				.map(component -> getDataMapper().map(component, AbstractCMSComponentData.class))
				.collect(Collectors.toList()));
		return componentDataList;
	}

	@Deprecated
	@HybrisDeprecation(sinceVersion = "6.6")
	@RequestMapping(value = "/{componentId}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(
			value = "Get component item by uid",
			notes = "Get the component that matches the given component id. Deprecated since 6.6, "
					+ "please use GET /v1/sites/{siteId}/cmsitems/{uuids} instead.")
	@ApiResponses({
		@ApiResponse(code = 400, message = "When the item has not been found or if there is any conversion error"),
		@ApiResponse(code = 200, message = "Cms component data object", response = AbstractCMSComponentData.class)
	})
	public AbstractCMSComponentData getComponentItemByUid(
			@ApiParam(value = "Component identifier", required = true) @PathVariable final String componentId,
			@ApiParam(value = "Response configuration (list of fields, which should be returned in the response)") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields) throws CMSItemNotFoundException,
	ConversionException
	{
		final de.hybris.platform.cmsfacades.data.AbstractCMSComponentData componentData = //
				getComponentFacade().getComponentItemByUid(componentId);
		return getDataMapper().map(componentData, AbstractCMSComponentData.class);
	}

	@Deprecated
	@HybrisDeprecation(sinceVersion = "6.6")
	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	@ApiOperation(
			value = "Create and add new component",
			notes = "Create a new component. When the &nbsp;slotId&nbsp; and the &nbsp;positionId&nbsp; are specified in the request, the \n"
					+ "newly created component will be assigned to a content slot at the stated position. Deprecated since 6.6, "
					+ "please use POST /v1/sites/{siteId}/cmsitems instead.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "Cms component data object", response = AbstractCMSComponentData.class)
	})
	public AbstractCMSComponentData createAndAddNewComponent(
			@ApiParam(value = "Cms component data object", required = true) @RequestBody final AbstractCMSComponentData componentData,
			final HttpServletRequest request,
			final HttpServletResponse response)
	{
		try
		{
			final de.hybris.platform.cmsfacades.data.AbstractCMSComponentData convertedComponentData = //
					getDataMapper().map(componentData, de.hybris.platform.cmsfacades.data.AbstractCMSComponentData.class);
			final de.hybris.platform.cmsfacades.data.AbstractCMSComponentData componentItem = //
					getComponentFacade().addComponentItem(convertedComponentData);
			response.addHeader(CmswebservicesConstants.HEADER_LOCATION,
					getLocationHeaderResource().createLocationForChildResource(request, componentItem.getUid()));
			return getDataMapper().map(componentItem, AbstractCMSComponentData.class);
		}
		catch (final ValidationException e)
		{
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	@Deprecated
	@HybrisDeprecation(sinceVersion = "6.6")
	@RequestMapping(value = "/{componentId}", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiOperation(value = "Update component", notes = "Update a component. Deprecated since 6.6, please use PUT /v1/sites/{siteId}/cmsitems instead.")
	@ApiResponses({
		@ApiResponse(code = 400, message = "If there is any validation error (WebserviceValidationException) or if it cannot find the component (CMSItemNotFoundException).")
	})
	public void updateComponent(
			@ApiParam(value = "Component identifier", required = true) @PathVariable final String componentId, //
			@ApiParam(value = "Cms component data object", required = true) @RequestBody final AbstractCMSComponentData componentData) throws WebserviceValidationException,
	CMSItemNotFoundException
	{
		try
		{
			final de.hybris.platform.cmsfacades.data.AbstractCMSComponentData convertedComponentData = //
					getDataMapper().map(componentData, de.hybris.platform.cmsfacades.data.AbstractCMSComponentData.class);
			getComponentFacade().updateComponentItem(componentId, convertedComponentData);
		}
		catch (final ValidationException e)
		{
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	@Deprecated
	@HybrisDeprecation(sinceVersion = "6.6")
	@RequestMapping(value = "/{componentId}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiOperation(value = "Remove component", notes = "Remove a component from the system. "
			+ "Deprecated since 6.6, please use DELETE /v1/sites/{siteId}/cmsitems/{uuid} instead.")
	@ApiResponses({
		@ApiResponse(code = 400, message = "If it cannot find the component (CMSItemNotFoundException).")
	})
	public void removeComponent(
			@ApiParam(value = "Component identifier", required = true) @PathVariable final String componentId)
					throws CMSItemNotFoundException
	{
		getComponentFacade().removeComponentItem(componentId);
	}

	protected ComponentItemFacade getComponentFacade()
	{
		return componentFacade;
	}

	public void setComponentFacade(final ComponentItemFacade componentFacade)
	{
		this.componentFacade = componentFacade;
	}

	protected LocationHeaderResource getLocationHeaderResource()
	{
		return locationHeaderResource;
	}

	public void setLocationHeaderResource(final LocationHeaderResource locationHeaderResource)
	{
		this.locationHeaderResource = locationHeaderResource;
	}

	protected WebPaginationUtils getWebPaginationUtils()
	{
		return webPaginationUtils;
	}

	public void setWebPaginationUtils(final WebPaginationUtils webPaginationUtils)
	{
		this.webPaginationUtils = webPaginationUtils;
	}

	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}

	public void setDataMapper(final DataMapper dataMapper)
	{
		this.dataMapper = dataMapper;
	}
}
