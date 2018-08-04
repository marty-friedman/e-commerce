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
package de.hybris.platform.entitlementfacades.impl;

import de.hybris.platform.entitlementfacades.CoreEntitlementFacade;
import de.hybris.platform.entitlementfacades.data.EntitlementData;
import de.hybris.platform.entitlementfacades.entitlement.populator.GrantEntitlementPopulator;
import de.hybris.platform.entitlementservices.entitlement.EntitlementService;
import de.hybris.platform.entitlementservices.model.EntitlementModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.services.entitlements.api.EntitlementFacade;
import com.hybris.services.entitlements.api.GrantData;
import com.hybris.services.entitlements.api.exceptions.ValidationException;


/**
 * Default implementation of the {@link CoreEntitlementFacade} interface.
 */
public class DefaultCoreEntitlementFacade implements CoreEntitlementFacade
{

	private static final Logger LOG = Logger.getLogger(DefaultCoreEntitlementFacade.class);

	private EntitlementFacade grantFacade;

	private EntitlementService entitlementService;

	private Converter<EntitlementModel, EntitlementData> entitlementModelToDataConverter;

	private GrantEntitlementPopulator<GrantData, EntitlementData> grantEntitlementPopulator;

	@Nonnull
	@Override
	public Collection<EntitlementData> getUserGrants(final String userId)
	{
		final Collection<EntitlementData> result = new ArrayList<>();

		try
		{
			final Collection<GrantData> grants = grantFacade.getGrants(userId, null, null, null);
			final Map<String, EntitlementModel> models = new HashMap<>();

			for (final GrantData currentGrant : grants)
			{
				if (currentGrant == null)
				{
					continue; // NOSONAR (it's more readable to have 2 continue statements)
				}

				final String entitlementId = currentGrant.getEntitlementType();
				if (entitlementId == null)
				{
					LOG.error("Grant with id='" + currentGrant.getId() + "' has null entitlement type.");
					continue;
				}

				createGrantAndEntitlementData(userId, result, models, currentGrant, entitlementId);

			}
		}
		catch (final ValidationException e)
		{
			LOG.error("Cannot get grants for User with id='" + userId + "').", e);
		}
		return result;
	}

	protected void createGrantAndEntitlementData(final String userId, final Collection<EntitlementData> result, final Map<String, EntitlementModel> models,
			final GrantData currentGrant, final String entitlementId)
	{
		try
		{
			final EntitlementData entitlementData = createEntitlementData(models, entitlementId, currentGrant);
			grantEntitlementPopulator.populate(currentGrant, entitlementData);
			result.add(entitlementData);
		}
		catch (final ConversionException e)
		{
			LOG.error("Cannot populate EntitlementData with id='" + entitlementId + "'! "
					+ "EntitlementModel.id is given from GrantData with id='" + currentGrant.getId() + "' for User(id='" + userId
					+ "').", e);
		}
	}

	protected EntitlementData createEntitlementData(final Map<String, EntitlementModel> models, final String entitlementId,
			final GrantData currentGrant)
	{
		EntitlementData entitlementData;
		try
		{
			final EntitlementModel entitlementModel = getEntitlementById(entitlementId, models);
			entitlementData = entitlementModelToDataConverter.convert(entitlementModel);
		}
		catch (final ModelNotFoundException e)
		{
			entitlementData = new EntitlementData();
			entitlementData.setName(currentGrant.getEntitlementType());
		}
		return entitlementData;
	}

	protected EntitlementFacade getGrantFacade()
	{
		return grantFacade;
	}

	@Required
	public void setGrantFacade(final EntitlementFacade grantFacade)
	{
		this.grantFacade = grantFacade;
	}

	protected EntitlementService getEntitlementService()
	{
		return entitlementService;
	}

	@Required
	public void setEntitlementService(final EntitlementService entitlementService)
	{
		this.entitlementService = entitlementService;
	}

	protected GrantEntitlementPopulator<GrantData, EntitlementData> getGrantEntitlementPopulator()
	{
		return grantEntitlementPopulator;
	}

	@Required
	public void setGrantEntitlementPopulator(final GrantEntitlementPopulator<GrantData, EntitlementData> entitlementPopulator)
	{
		this.grantEntitlementPopulator = entitlementPopulator;
	}

	protected Converter<EntitlementModel, EntitlementData> getEntitlementModelToDataConverter()
	{
		return entitlementModelToDataConverter;
	}

	@Required
	public void setEntitlementModelToDataConverter(
			final Converter<EntitlementModel, EntitlementData> entitlementModelToDataConverter)
	{
		this.entitlementModelToDataConverter = entitlementModelToDataConverter;
	}

	protected EntitlementModel getEntitlementById(final String id, final Map<String, EntitlementModel> cache)
			throws ModelNotFoundException
	{
		final EntitlementModel entitlementModel;
		if (cache.containsKey(id))
		{
			entitlementModel = cache.get(id);
		}
		else
		{
			entitlementModel = entitlementService.getEntitlementForCode(id);
			cache.put(entitlementModel.getId(), entitlementModel);
		}
		return entitlementModel;
	}
}
