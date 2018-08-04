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
package de.hybris.platform.samlsinglesignon;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.samlsinglesignon.constants.SamlsinglesignonConstants;
import de.hybris.platform.samlsinglesignon.model.SamlUserGroupModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default SSO service for getting/creating user
 */
public class DefaultSSOService implements SSOUserService
{
	protected static final String SSO_DATABASE_USERGROUP_MAPPING = "sso.database.usergroup.mapping";
	private static final Logger LOGGER = Logger.getLogger(DefaultSSOService.class);

	private ModelService modelService;
	private UserService userService;
	private SamlUserGroupDAO samlUserGroupDAO;

	@Override
	public UserModel getOrCreateSSOUser(final String id, final String name, final Collection<String> roles)
	{
		final SSOUserMapping userMapping = findMapping(roles);

		if (StringUtils.isEmpty(id) || StringUtils.isEmpty(name))
		{
			throw new IllegalArgumentException("User info must not be empty");
		}

		if (userMapping != null)
		{

			UserModel user = lookupExisting(id, userMapping);
			if (user == null)
			{
				user = createNewUser(id, name, userMapping);
			}
			adjustUserAttributes(user, userMapping);

			getModelService().save(user);

			return user;
		}
		else
		{
			throw new IllegalArgumentException("No SSO user mapping available for roles " + roles + " - cannot accept user " + id);
		}
	}

	/**
	 * create a new user
	 *
	 * @param id
	 *           to be used as the user Id
	 * @param name
	 *           name of the user
	 * @param userMapping
	 *           user mappings (groups and user type)
	 * @return a new user model
	 */
	protected UserModel createNewUser(final String id, final String name, final SSOUserMapping userMapping)
	{
		final UserModel user;
		user = getModelService().create(userMapping.type);
		user.setUid(id);
		user.setName(name);

		final String defaultPasswordEncoder = StringUtils
				.defaultIfEmpty(Config.getParameter(SamlsinglesignonConstants.SSO_PASSWORD_ENCODING),
						SamlsinglesignonConstants.MD5_PASSWORD_ENCODING);

		getUserService().setPassword(user, UUID.randomUUID().toString(), defaultPasswordEncoder);//should be default password but the token is encoded with md5
		return user;
	}

	/**
	 * Check if a user exists or not
	 *
	 * @param id
	 *           the user id to search for
	 * @param mapping
	 *           groups/user type
	 * @return return user model in case the user is found or null if not found
	 */
	protected UserModel lookupExisting(final String id, final SSOUserMapping mapping)
	{
		LOGGER.info(mapping);
		try
		{
			return getUserService().getUserForUID(id);
		}
		catch (final UnknownIdentifierException e)
		{
			LOGGER.warn(e);
			return null;
		}
	}

	/**
	 * Adjusting user groups
	 *
	 * @param user
	 *           the user to adjust the groups for
	 * @param mapping
	 *           the mapping which holds the groups
	 */
	protected void adjustUserAttributes(final UserModel user, final SSOUserMapping mapping)
	{
		user.setGroups(mapping.groups.stream().map(it -> getUserService().getUserGroupForUID(it)).collect(Collectors.toSet()));
	}

	/**
	 * Maps SSO usergroups to hybris type and groups. If property {@link #SSO_DATABASE_USERGROUP_MAPPING} is true, the
	 * mapping is taken from database. Otherwise it takes mapping from properties file.
	 *
	 * @param roles
	 *           Roles to map
	 * @return The mapping
	 */
	protected SSOUserMapping findMapping(final Collection<String> roles)
	{
		if (Config.getBoolean(SSO_DATABASE_USERGROUP_MAPPING, false))
		{
			return findMappingInDatabase(roles);
		}
		else
		{
			return findMappingInProperties(roles);
		}
	}

	protected SSOUserMapping findMappingInProperties(final Collection<String> roles)
	{
		SSOUserMapping mergedMapping = null;
		for (final String role : roles)
		{
			final SSOUserMapping mapping = getMappingForRole(role);
			if (mapping != null)
			{
				if (mergedMapping == null)
				{
					mergedMapping = new SSOUserMapping();
					mergedMapping.type = mapping.type;
				}
				if (Objects.equals(mapping.type, mergedMapping.type))
				{
					mergedMapping.groups.addAll(mapping.groups);
				}
				else
				{
					throw new IllegalArgumentException("SSO user cannot be configured due to ambigous type mappings (roles: " + roles
							+ ")");
				}
			}
		}
		return mergedMapping;
	}

	protected SSOUserMapping findMappingInDatabase(final Collection<String> roles)
	{
		final List<SamlUserGroupModel> userGroupModels = roles.stream().map(getSamlUserGroupDAO()::findSamlUserGroup)
				.filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());

		validateMappings(roles, userGroupModels);

		return performMapping(userGroupModels);
	}

	protected SSOUserMapping performMapping(final List<SamlUserGroupModel> userGroupModels)
	{
		final String userType = userGroupModels.get(0).getUserType().getCode();
		final Set<String> userGroups = userGroupModels.stream()//
				.flatMap(samlUserGroupModel -> samlUserGroupModel.getUserGroups().stream())//
				.map(PrincipalGroupModel::getUid).collect(Collectors.toSet());

		final SSOUserMapping ssoUserMapping = new SSOUserMapping();
		ssoUserMapping.setType(userType);
		ssoUserMapping.setGroups(userGroups);
		return ssoUserMapping;
	}


	protected void validateMappings(final Collection<String> roles, final List<SamlUserGroupModel> userGroupModels)
	{
		if (CollectionUtils.isEmpty(userGroupModels))
		{
			throw new IllegalArgumentException("Cannot find mapping for SSO user with roles: " + roles);
		}

		final Set<TypeModel> typeMappings = userGroupModels.stream().map(SamlUserGroupModel::getUserType)
				.collect(Collectors.toSet());
		final boolean moreThanOneTypeMappingExists = typeMappings.size() > 1;
		if (moreThanOneTypeMappingExists)
		{
			throw new IllegalArgumentException("SSO user cannot be configured due to ambiguous type mappings (roles: " + roles + ")");
		}
	}

	/**
	 * getting the mapping for roles
	 *
	 * @param role
	 *           the role to get the mapping for
	 * @return SSO user mapping object which has the user type and the groups
	 */
	protected SSOUserMapping getMappingForRole(final String role)
	{
		final Map<String, String> params = Registry.getCurrentTenantNoFallback().getConfig()
				.getParametersMatching("sso\\.mapping\\." + role + "\\.(.*)", true);
		if (MapUtils.isNotEmpty(params))
		{
			final SSOUserMapping mapping = new SSOUserMapping();
			mapping.type = params.get("usertype");
			mapping.groups = new LinkedHashSet<>(Arrays.asList(params.get("groups").split(",; ")));

			return mapping;
		}
		return null;
	}

	public static class SSOUserMapping
	{
		private String type;
		private Collection<String> groups = new LinkedHashSet<>();
		private Map<String, Object> parameters = new HashMap<>();

		public String getType()
		{
			return type;
		}

		public void setType(final String type)
		{
			this.type = type;
		}

		public Collection<String> getGroups()
		{
			return groups;
		}

		public void setGroups(final Collection<String> groups)
		{
			this.groups = groups;
		}

		public Map<String, Object> getParameters()
		{
			return parameters;
		}

		public void setParameters(final Map<String, Object> parameters)
		{
			this.parameters = parameters;
		}
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setSamlUserGroupDAO(final SamlUserGroupDAO samlUserGroupDAO)
	{
		this.samlUserGroupDAO = samlUserGroupDAO;
	}

	protected SamlUserGroupDAO getSamlUserGroupDAO()
	{
		return samlUserGroupDAO;
	}

}
