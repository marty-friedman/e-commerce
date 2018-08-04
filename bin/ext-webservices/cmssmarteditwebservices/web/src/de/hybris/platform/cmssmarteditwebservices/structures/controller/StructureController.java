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
package de.hybris.platform.cmssmarteditwebservices.structures.controller;

import de.hybris.platform.cms2.common.annotations.HybrisDeprecation;
import de.hybris.platform.cmsfacades.data.ComponentTypeData;
import de.hybris.platform.cmsfacades.types.ComponentTypeNotFoundException;
import de.hybris.platform.cmssmarteditwebservices.dto.StructureListWsDTO;
import de.hybris.platform.cmssmarteditwebservices.dto.StructureWsDTO;
import de.hybris.platform.cmssmarteditwebservices.structures.facade.StructureFacade;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import static com.google.common.collect.Lists.newArrayList;
import static de.hybris.platform.cmssmarteditwebservices.constants.CmssmarteditwebservicesConstants.API_VERSION;
import static java.util.Arrays.asList;
import static java.util.Objects.nonNull;

/**
 * Controller to deal with structures for CMS types.
 * @deprecated since 6.5. Use cmswebservices/v1/types instead. 
 */
@Controller
@RequestMapping(API_VERSION + "/structures")
@Deprecated
@HybrisDeprecation(sinceVersion = "6.5")
public class StructureController
{
	@Resource
	private StructureFacade structureFacade;
	@Resource
	private DataMapper dataMapper;

	@RequestMapping(value = "/{code}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Get structure by code", notes = "Endpoint to retrieve the structures that matches the given type code for all defined structure type modes. Deprecated since 6.5, "
																			+ "please use GET cmswebservices/v1/types/{code} instead.")
	@ApiResponses({
			@ApiResponse(code = 400, message = "When the code provided does not match any existing type (ComponentTypeNotFoundException)."),
			@ApiResponse(code = 200, message = "All structures for the given type code. If a mode is specified, the list of structures should contain only one item.", response = StructureListWsDTO.class)
	})
	@HybrisDeprecation(sinceVersion = "6.5")
	public StructureListWsDTO getStructureByCode(
			@ApiParam(value = "The type code of the structure to retrieve", required = true) @PathVariable final String code) throws ComponentTypeNotFoundException
	{
		final List<StructureWsDTO> structureDtos = new ArrayList<>();
		getStructureFacade().getComponentTypesByCode(code).forEach(structureData -> {
			final StructureWsDTO structureDto = getDataMapper().map(structureData, StructureWsDTO.class);
			structureDtos.add(structureDto);
		});

		final StructureListWsDTO dtoList = new StructureListWsDTO();
		dtoList.setStructures(structureDtos);
		return dtoList;
	}

	@RequestMapping(value = "/{code}", method = RequestMethod.GET, params = { "mode" })
	@ResponseBody
	@ApiOperation(value = "Get structure by code and mode", notes = "Endpoint to retrieve the structures that matches the given type code. Deprecated since 6.5, "
																						+ "please use GET cmswebservices/v1/types{?code,mode} instead.")
	@ApiResponses({
			@ApiResponse(code = 400, message = "When the code provided does not match any existing type (ComponentTypeNotFoundException)."),
			@ApiResponse(code = 200, message = "All structures for the given type code. If a mode is specified, the list of structures should contain only one item.", response = StructureListWsDTO.class)
	})
	@HybrisDeprecation(sinceVersion = "6.5")
	public StructureListWsDTO getStructureByCodeAndMode(
			@ApiParam(value = "The type code of the structure to retrieve", required = true) @PathVariable final String code,
			@ApiParam(value = "The mode of the structure to retrieve", required = true) @RequestParam(value = "mode") final String mode) throws ComponentTypeNotFoundException
	{
		final ComponentTypeData structureData = getStructureFacade().getComponentTypeByCodeAndMode(code, mode);
		final StructureWsDTO structureDto = getDataMapper().map(structureData, StructureWsDTO.class);

		final StructureListWsDTO dtoList = new StructureListWsDTO();
		dtoList.setStructures(nonNull(structureDto) ? asList(structureDto) : newArrayList());
		return dtoList;
	}

	protected StructureFacade getStructureFacade()
	{
		return structureFacade;
	}

	public void setStructureFacade(final StructureFacade structureFacade)
	{
		this.structureFacade = structureFacade;
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
