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

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static de.hybris.platform.samlsinglesignon.SSOServiceTestConstants.ADMIN_GROUP;
import static de.hybris.platform.samlsinglesignon.SSOServiceTestConstants.EMPLOYEE_GROUP;
import static de.hybris.platform.samlsinglesignon.SSOServiceTestConstants.EMPLOYEE_TYPE_NAME;
import static de.hybris.platform.samlsinglesignon.SSOServiceTestConstants.FALSE;
import static de.hybris.platform.samlsinglesignon.SSOServiceTestConstants.SSO_PROPERTY_PREFIX;
import static de.hybris.platform.samlsinglesignon.SSOServiceTestConstants.SSO_USER_GROUP;
import static de.hybris.platform.samlsinglesignon.SSOServiceTestConstants.SSO_USER_GROUP_2;
import static de.hybris.platform.samlsinglesignon.SSOServiceTestConstants.SSO_USER_ID;
import static de.hybris.platform.samlsinglesignon.SSOServiceTestConstants.SSO_USER_NAME;
import static de.hybris.platform.samlsinglesignon.SSOServiceTestConstants.TRUE;
import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.samlsinglesignon.model.SamlUserGroupModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;

import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;



@IntegrationTest
public class DefaultSSOServiceTest extends ServicelayerTransactionalTest
{

	@Resource(name = "defaultSSOUserService")
	private DefaultSSOService defaultSSOService;
	@Resource
	private ModelService modelService;
	@Resource
	private UserService userService;
	@Resource
	private TypeService typeService;

	private TypeModel employeeType;
	private TypeModel customerType;
	private UserGroupModel employeeGroup;
	private UserGroupModel adminGroup;

	@Before
	public void setup()
	{
		employeeType = typeService.getTypeForCode(EmployeeModel._TYPECODE);
		customerType = typeService.getTypeForCode(CustomerModel._TYPECODE);
		employeeGroup = userService.getUserGroupForUID(EMPLOYEE_GROUP);
		adminGroup = userService.getUserGroupForUID(ADMIN_GROUP);
	}

	@Test
	public void shouldCreateSSOUser()
	{
		// given
		Config.setParameter(DefaultSSOService.SSO_DATABASE_USERGROUP_MAPPING, FALSE);
		createMappingInProperties(SSO_USER_GROUP, EMPLOYEE_TYPE_NAME, EMPLOYEE_GROUP);

		// when
		final UserModel user = defaultSSOService.getOrCreateSSOUser(SSO_USER_ID, SSO_USER_NAME, newHashSet(SSO_USER_GROUP));

		// then
		assertThat(user).isNotNull();
		assertThat(user.getUid()).isEqualTo(SSO_USER_ID);
		assertThat(user.getName()).isEqualTo(SSO_USER_NAME);
		assertThat(user.getGroups()).containsOnlyElementsOf(newArrayList(employeeGroup));
		assertThat(user.getItemtype()).isEqualTo(EMPLOYEE_TYPE_NAME);
		assertThat(user.getPasswordEncoding()).isEqualTo("md5");
	}

	@Test
	public void shouldGetSSOUser()
	{
		// given
		Config.setParameter(DefaultSSOService.SSO_DATABASE_USERGROUP_MAPPING, FALSE);
		createMappingInProperties(SSO_USER_GROUP, EMPLOYEE_TYPE_NAME, EMPLOYEE_GROUP);

		final EmployeeModel employeeModel = modelService.create(EmployeeModel.class);
		employeeModel.setUid(SSO_USER_ID);
		modelService.save(employeeModel);

		// when
		final UserModel user = defaultSSOService.getOrCreateSSOUser(SSO_USER_ID, SSO_USER_NAME, newHashSet(SSO_USER_GROUP));

		// then
		assertThat(user).isNotNull();
		assertThat(user.getUid()).isEqualTo(SSO_USER_ID);
		assertThat(user.getGroups()).containsOnlyElementsOf(newArrayList(employeeGroup));
	}


	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionWhenNoRoleFound()
	{
		// given
		Config.setParameter(DefaultSSOService.SSO_DATABASE_USERGROUP_MAPPING, FALSE);

		// when
		defaultSSOService.getOrCreateSSOUser(SSO_USER_ID, SSO_USER_NAME, newHashSet(SSO_USER_GROUP));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionWheAmbigousRoleFound()
	{
		// given
		Config.setParameter(DefaultSSOService.SSO_DATABASE_USERGROUP_MAPPING, FALSE);
		createMappingInProperties(SSO_USER_GROUP, EMPLOYEE_TYPE_NAME, EMPLOYEE_GROUP);
		createMappingInProperties(SSO_USER_GROUP_2, "Employee2", EMPLOYEE_GROUP);

		// when
		defaultSSOService.getOrCreateSSOUser(SSO_USER_ID, SSO_USER_NAME, newHashSet(SSO_USER_GROUP, SSO_USER_GROUP_2));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionWhenIdEmpty()
	{
		// given
		Config.setParameter(DefaultSSOService.SSO_DATABASE_USERGROUP_MAPPING, FALSE);
		createMappingInProperties(SSO_USER_GROUP, EMPLOYEE_TYPE_NAME, EMPLOYEE_GROUP);

		// when
		defaultSSOService.getOrCreateSSOUser(StringUtils.EMPTY, SSO_USER_NAME, newHashSet(SSO_USER_GROUP));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionWhenIdNameEmpty()
	{
		// given
		Config.setParameter(DefaultSSOService.SSO_DATABASE_USERGROUP_MAPPING, FALSE);
		createMappingInProperties(SSO_USER_GROUP, EMPLOYEE_TYPE_NAME, EMPLOYEE_GROUP);

		// when
		defaultSSOService.getOrCreateSSOUser(SSO_USER_ID, StringUtils.EMPTY, newHashSet(SSO_USER_GROUP));
	}


	@Test
	public void shouldUseMappingFromDatabase()
	{
		// given
		Config.setParameter(DefaultSSOService.SSO_DATABASE_USERGROUP_MAPPING, TRUE);
		createMappingInDatabase(SSO_USER_GROUP, employeeType, newHashSet(employeeGroup));

		// when
		final UserModel ssoUser = defaultSSOService.getOrCreateSSOUser(SSO_USER_ID, SSO_USER_NAME, newHashSet(SSO_USER_GROUP));

		// then
		assertThat(ssoUser).isNotNull();
		assertThat(ssoUser.getUid()).isEqualTo(SSO_USER_ID);
		assertThat(ssoUser.getName()).isEqualTo(SSO_USER_NAME);
		assertThat(ssoUser.getGroups()).containsOnly(employeeGroup);
		assertThat(ssoUser.getItemtype()).isEqualTo(EmployeeModel._TYPECODE);
	}

	@Test
	public void shouldMergeUserGroupsOfMultipleMappingsFromDatabase()
	{
		// given
		Config.setParameter(DefaultSSOService.SSO_DATABASE_USERGROUP_MAPPING, TRUE);
		createMappingInDatabase(SSO_USER_GROUP, employeeType, newHashSet(employeeGroup));
		createMappingInDatabase(SSO_USER_GROUP_2, employeeType, newHashSet(adminGroup));

		// when
		final UserModel ssoUser = defaultSSOService.getOrCreateSSOUser(SSO_USER_ID, SSO_USER_NAME,
				newHashSet(SSO_USER_GROUP, SSO_USER_GROUP_2));

		// then
		assertThat(ssoUser).isNotNull();
		assertThat(ssoUser.getGroups()).containsOnly(employeeGroup, adminGroup);
	}


	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfNoMappingInDatabaseExists()
	{
		// given
		Config.setParameter(DefaultSSOService.SSO_DATABASE_USERGROUP_MAPPING, TRUE);

		// when
		defaultSSOService.getOrCreateSSOUser(SSO_USER_ID, SSO_USER_NAME, newHashSet(SSO_USER_GROUP));

		// then - should throw exception
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailForAmbiguousMappingsInDatabase()
	{
		// given
		Config.setParameter(DefaultSSOService.SSO_DATABASE_USERGROUP_MAPPING, TRUE);
		createMappingInDatabase(SSO_USER_GROUP, employeeType, newHashSet(employeeGroup));
		createMappingInDatabase(SSO_USER_GROUP_2, customerType, newHashSet(employeeGroup));

		// when
		defaultSSOService.getOrCreateSSOUser(SSO_USER_ID, SSO_USER_NAME, newHashSet(SSO_USER_GROUP, SSO_USER_GROUP_2));

		// then - should throw exception
	}

	private void createMappingInProperties(final String ssoUserGroup, final String userType, final String groups)
	{
		Config.setParameter(SSO_PROPERTY_PREFIX + ssoUserGroup + ".usertype", userType);
		Config.setParameter(SSO_PROPERTY_PREFIX + ssoUserGroup + ".groups", groups);
	}

	private void createMappingInDatabase(final String ssoUserGroup, final TypeModel userType, final Set<UserGroupModel> userGroups)
	{
		final SamlUserGroupModel samlUserGroupModel = new SamlUserGroupModel();

		samlUserGroupModel.setSamlUserGroup(ssoUserGroup);
		samlUserGroupModel.setUserType(userType);
		samlUserGroupModel.setUserGroups(userGroups);

		modelService.save(samlUserGroupModel);
	}

}
