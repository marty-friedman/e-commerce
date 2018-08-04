/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 */

package de.hybris.platform.samlsinglesignon;

import de.hybris.platform.samlsinglesignon.model.SamlUserGroupModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;



public class DefaultSamlUserGroupDAO implements SamlUserGroupDAO
{
	protected static final String PARAM_NAME = "samlUserGroup";
	protected static final String QUERY_STRING = String.format("select {PK} from {%s} where {%s} = ?%s",
			SamlUserGroupModel._TYPECODE, SamlUserGroupModel.SAMLUSERGROUP, PARAM_NAME);

	private FlexibleSearchService flexibleSearchService;

	@Override
	public Optional<SamlUserGroupModel> findSamlUserGroup(final String samlUserGroup)
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(QUERY_STRING);
		query.addQueryParameter(PARAM_NAME, samlUserGroup);

		final List<SamlUserGroupModel> searchResult = getFlexibleSearchService().<SamlUserGroupModel> search(query).getResult();
		if (CollectionUtils.isNotEmpty(searchResult))
		{
			return Optional.of(searchResult.get(0));
		}
		else
		{
			return Optional.empty();
		}
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	protected FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}
}
