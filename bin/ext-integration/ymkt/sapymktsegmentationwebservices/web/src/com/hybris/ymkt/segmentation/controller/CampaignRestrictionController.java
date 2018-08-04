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
package com.hybris.ymkt.segmentation.controller;

import de.hybris.platform.cmsfacades.data.OptionData;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hybris.ymkt.segmentation.facades.CampaignRestrictionPopulatorFacade;

import io.swagger.annotations.Api;


@Controller
@Api(tags = "CampaignRestriction")
public class CampaignRestrictionController
{
	@Resource(name = "campaignRestrictionPopulatorFacade")
	protected CampaignRestrictionPopulatorFacade campaignRestrictionPopulatorFacade;

	@RequestMapping(method = RequestMethod.GET, value = "/data/segmentation/campaign")
	@ResponseBody
	public String populateCampaignsDropdown(@RequestParam(required = false) final String mask,
			@RequestParam(required = true) final String currentPage, @RequestParam(required = true) final String pageSize)
			throws IOException
	{
		final List<OptionData> campaigns = this.campaignRestrictionPopulatorFacade.getCampaigns(mask, currentPage, pageSize);
		return new ObjectMapper().writeValueAsString(Collections.singletonMap("options", campaigns));
	}

	@RequestMapping(method = RequestMethod.GET, value = "/data/segmentation/campaign/{campaignId}")
	@ResponseBody
	public String setExistingCampaignDropdownValue(@PathVariable Optional<String> campaignId) throws IOException
	{
		if (campaignId.isPresent())
		{
			final OptionData campaignOption = this.campaignRestrictionPopulatorFacade.getCampaignById(campaignId.get());
			return new ObjectMapper().writeValueAsString(campaignOption);
		}
		return "";
	}
}