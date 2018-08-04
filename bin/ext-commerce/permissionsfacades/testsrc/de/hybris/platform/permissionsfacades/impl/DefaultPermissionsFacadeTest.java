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
package de.hybris.platform.permissionsfacades.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultPermissionsFacadeTest
{
	private static String PRINCIPAL_UID = "principalUid";

	private final DefaultPermissionsFacade defaultPermissionsFacade = new DefaultPermissionsFacade();

	@Mock
	private FlexibleSearchService flexibleSearchService;

	@Mock
	private TypeService typeService;

	@Mock
	ComposedTypeModel composedType;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		defaultPermissionsFacade.setFlexibleSearchService(flexibleSearchService);
		defaultPermissionsFacade.setTypeService(typeService);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCalculateTypesPermissionsForNullPrincipalUid()
	{
		//when
		defaultPermissionsFacade.calculateTypesPermissions(null, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCalculateTypesPermissionsForNullTypeList()
	{
		//when
		defaultPermissionsFacade.calculateTypesPermissions(PRINCIPAL_UID, null, Collections.EMPTY_LIST);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCalculateTypesPermissionsForNullPermissionNames()
	{
		//when
		defaultPermissionsFacade.calculateTypesPermissions(PRINCIPAL_UID, Collections.EMPTY_LIST, null);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void testCalculateAttributesPermissionsWithWrongAttributeName()
	{
		//given
		Mockito.when(typeService.getComposedTypeForCode("Item")).thenReturn(composedType);
		Mockito.when(composedType.getInheritedattributedescriptors()).thenReturn(Collections.EMPTY_LIST);
		Mockito.when(composedType.getDeclaredattributedescriptors()).thenReturn(Collections.EMPTY_LIST);

		//when
		defaultPermissionsFacade.calculateAttributesPermissions(PRINCIPAL_UID,
				Collections.singletonList("Item.notExistingAttribute"), Collections.EMPTY_LIST);
	}


	@Test(expected = UnknownIdentifierException.class)
	public void testCalculateAttributesPermissionsWithWrongAttributeNameFormat()
	{
		//when
		defaultPermissionsFacade.calculateAttributesPermissions(PRINCIPAL_UID, Collections.singletonList("wrongAttributeName"),
				Collections.EMPTY_LIST);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void testCalculateAttributesPermissionsWithWrongAttributeNameFormat1()
	{
		//when
		defaultPermissionsFacade.calculateAttributesPermissions(PRINCIPAL_UID,
				Collections.singletonList("wrong.attribute.name.format"), Collections.EMPTY_LIST);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCalculateAttributesPermissionsForNullPrincipalUid()
	{
		//when
		defaultPermissionsFacade.calculateAttributesPermissions(null, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCalculateAttributesPermissionsForNullTypesAttributes()
	{
		//when
		defaultPermissionsFacade.calculateAttributesPermissions(PRINCIPAL_UID, null, Collections.EMPTY_LIST);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testCalculateAttributesPermissionsForNullPermissionNames()
	{
		//when
		defaultPermissionsFacade.calculateAttributesPermissions(PRINCIPAL_UID, Collections.EMPTY_LIST, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCalculateGlobalPermissionsForNullPrincipalUid()
	{
		//when
		defaultPermissionsFacade.calculateGlobalPermissions(null, Collections.EMPTY_LIST);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCalculateGlobalPermissionsForNullPermissionNames()
	{
		//when
		defaultPermissionsFacade.calculateGlobalPermissions(PRINCIPAL_UID, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCalculateCatalogPermissionsForNullPrincipalUid()
	{
		//when
		defaultPermissionsFacade.calculateCatalogPermissions(null, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCalculateCatalogPermissionsForNullCatalogList()
	{
		//when
		defaultPermissionsFacade.calculateCatalogPermissions(null, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCalculateCatalogPermissionsForNullCatalogVersionList()
	{
		//when
		defaultPermissionsFacade.calculateCatalogPermissions(null, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
	}
}
