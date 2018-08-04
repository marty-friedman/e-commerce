/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.b2badmincockpit.wizards.impl;

import de.hybris.platform.b2b.services.B2BItemService;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.user.TitleModel;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zul.ListModelList;


public class B2BAddressOrganizationWizardPage extends AbstractB2BOrganizationWizardPage
{

	private B2BItemService b2bItemService;

	@Autowired
	public void setB2BItemService(final B2BItemService b2bItemService)
	{
		this.b2bItemService = b2bItemService;
	}

	public ListModelList getTitles()
	{
		@SuppressWarnings("deprecated")
		final List<TitleModel> titleModels = b2bItemService.findAllItems(TitleModel.class);
		final ListModelList titles = new ListModelList();
		titles.addAll(titleModels);

		return titles;
	}

	public ArrayList<String> getCountries()
	{

		@SuppressWarnings("deprecated")
		final List<CountryModel> countryModels = b2bItemService.findAllItems(CountryModel.class);
		final ArrayList<String> isoCodes = new ArrayList<String>();

		for (final CountryModel country : countryModels)
		{
			isoCodes.add(country.getIsocode());
		}

		return isoCodes;
	}
}
