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
package de.hybris.platform.cmsmulticountrycockpit.actions;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cockpit.components.listview.ListViewAction;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.fest.assertions.Assertions.assertThat;


@IntegrationTest
public class MultiCountryDeepCopyPageActionTest extends ServicelayerTransactionalTest
{
	private static final String BUTTON_ICON = "/cmscockpit/images/icon_cmspage_copy_ok.png";
	private static final String BUTTON_ICON_DISABLED = "/cmscockpit/images/icon_cmspage_copy_off.png";

	private MultiCountryDeepCopyPageAction multiCountryDeepCopyPageAction;

	@Mock
	private TypedObject typeObject;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		// Create the action bean
		multiCountryDeepCopyPageAction = new MultiCountryDeepCopyPageAction();
	}

	@Test
	public void shouldShowDeepCopyPageAction()
	{
		final ListViewAction.Context ctx = new ListViewAction.Context();
		ctx.setItem(typeObject);

		// Call the action
		final String imageUrl = multiCountryDeepCopyPageAction.getImageURI(ctx);

		// Test so the correct image is returned
		assertThat(imageUrl).isNotNull();
		assertThat(imageUrl).isEqualTo(BUTTON_ICON);
	}

	@Test
	public void shouldShowDisableDeepCopyPageAction()
	{
		final ListViewAction.Context ctx = new ListViewAction.Context();

		// Call the action
		final String imageUrl = multiCountryDeepCopyPageAction.getImageURI(ctx);

		// Test so the correct image is returned
		assertThat(imageUrl).isNotNull();
		assertThat(imageUrl).isEqualTo(BUTTON_ICON_DISABLED);
	}
}
