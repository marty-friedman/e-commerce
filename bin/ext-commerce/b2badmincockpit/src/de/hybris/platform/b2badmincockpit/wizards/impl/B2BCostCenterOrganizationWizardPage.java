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

import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.services.B2BCostCenterService;
import de.hybris.platform.b2b.services.B2BItemService;
import de.hybris.platform.cockpit.wizards.Message;
import de.hybris.platform.core.model.c2l.CurrencyModel;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.ListModelList;


/**
 *
 */
public class B2BCostCenterOrganizationWizardPage extends AbstractB2BOrganizationWizardPage
{

	@SuppressWarnings("deprecation")
	private B2BItemService b2bItemService;
	private ListModelList currencies;
	private B2BCostCenterService b2bCostCenter;

	/**
	 * @return the b2bCostCenter
	 */
	protected B2BCostCenterService getB2bCostCenter()
	{
		return b2bCostCenter;
	}

	/**
	 * @param b2bCostCenter
	 *           the b2bCostCenter to set
	 */
	@Autowired
	public void setB2bCostCenter(final B2BCostCenterService b2bCostCenter)
	{
		this.b2bCostCenter = b2bCostCenter;
	}

	public ListModelList getCurrencies()
	{
		@SuppressWarnings("deprecation")
		final List<CurrencyModel> currencyModels = b2bItemService.findAllItems(CurrencyModel.class);
		currencies = new ListModelList();
		currencies.addAll(currencyModels);

		return currencies;
	}

	/**
	 * @return the b2bItemService
	 */
	protected B2BItemService getB2bItemService()
	{
		return b2bItemService;
	}

	/**
	 * @param b2bItemService
	 *           the itemService to set
	 */
	@Autowired
	public void setb2bItemService(final B2BItemService b2bItemService)
	{
		this.b2bItemService = b2bItemService;
	}

	@Override
	public void renderView(final Component parent)
	{
		//custom render code
	}

	@Override
	public List<Message> validate()
	{
		final List<Message> validationMessages = new ArrayList<Message>();
		final String costCenterUid = (String) getAttribute("uid");
		// validate uid
		if (StringUtils.isNotBlank(costCenterUid))
		{
			final B2BCostCenterModel costCenter = (B2BCostCenterModel) getB2bCostCenter().getCostCenterForCode(costCenterUid);

			if (costCenter != null)
			{
				validationMessages.add(new Message(Message.ERROR, "The ID you selected does already exist.", "uid"));
			}
		}

		return validationMessages;
	}
}
