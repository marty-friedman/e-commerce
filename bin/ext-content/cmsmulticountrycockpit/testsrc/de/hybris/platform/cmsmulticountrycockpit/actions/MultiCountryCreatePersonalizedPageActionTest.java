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
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminComponentService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminContentSlotService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cmscockpit.session.impl.CatalogBrowserArea;
import de.hybris.platform.cmsmulticountrycockpit.services.MultiCountryCockpitService;
import de.hybris.platform.cmsmulticountrycockpit.session.impl.MultiCountryCmsCockpitPerspective;
import de.hybris.platform.cmsmulticountrycockpit.session.impl.MultiCountryCmsPageBrowserModel;
import de.hybris.platform.cmsmulticountrycockpit.utils.MultiCountryDummyExecution;
import de.hybris.platform.cockpit.components.listview.ListViewAction;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.services.SystemService;
import de.hybris.platform.cockpit.session.impl.UISessionImpl;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.sys.ExecutionsCtrl;

import javax.annotation.Resource;
import java.util.Collections;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;


@IntegrationTest
public class MultiCountryCreatePersonalizedPageActionTest extends ServicelayerTransactionalTest
{
	private static final String CREATE_PERSONALIZED_PAGE_AVAILABLE_ACTION_URL = "/cmscockpit/images/icon_func_new_personalized_page.png";
	private static final String CREATE_PERSONALIZED_PAGE_UNAVAILABLE_ACTION_URL = "/cmscockpit/images/icon_func_new_personalized_page_unavailable.png";

	private MultiCountryCreatePersonalizedPageAction multiCountryCreatePersonalizedPageAction;
	private CatalogVersionModel catalogVersion;
	private UserModel user;

	@Resource
	private CMSAdminSiteService cmsAdminSiteService;
	@Resource
	private ModelService modelService;
	@Resource
	private CMSAdminComponentService cmsAdminComponentService;
	@Resource
	private CMSAdminContentSlotService cmsAdminContentSlotService;
	@Resource
	private UserService userService;

	@Mock
	private CatalogVersionService catalogVersionService;
	@Mock
	private MultiCountryCockpitService cmsCockpitService;
	@Mock
	private TypedObject typeObject;
	@Mock
	private AbstractPageModel pageModel;
	@Mock
	private UISessionImpl session;
	@Mock
	private SystemService systemService;
	@Mock
	private CatalogBrowserArea catalogArea;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		// Create the action bean
		multiCountryCreatePersonalizedPageAction = new MultiCountryCreatePersonalizedPageAction();
		multiCountryCreatePersonalizedPageAction.setCatalogVersionService(catalogVersionService);

		final CatalogModel catalog = modelService.create(CatalogModel.class);
		catalog.setId("TestCatalog");

		catalogVersion = modelService.create(CatalogVersionModel.class);
		catalogVersion.setCatalog(catalog);
		catalogVersion.setVersion("TestVersion");

		user = modelService.create(UserModel.class);
		user.setUid("TestUser");
		user.setReadableCatalogVersions(Collections.singletonList(catalogVersion));
		user.setWritableCatalogVersions(Collections.singletonList(catalogVersion));

		modelService.saveAll(catalog, catalogVersion, user);
	}

	@Test
	public void shouldShowCreatePersonalizedPageAction()
	{
		final MultiCountryCmsPageBrowserModel browser = createExecution();

		// Sets so the page would be seens as global
		browser.setDefaultPage(true);
		browser.setTopLevel(true);

		final ListViewAction.Context ctx = new ListViewAction.Context();

		// unlocking package and allowing localize action
		given(pageModel.getCatalogVersion()).willReturn(catalogVersion);
		given(typeObject.getObject()).willReturn(pageModel);
		given(pageModel.getLockedBy()).willReturn(user);
		given(catalogVersionService.canWrite(catalogVersion, user)).willReturn(Boolean.TRUE);

		ctx.setItem(typeObject);

		// Call the action
		final String imageUrl = multiCountryCreatePersonalizedPageAction.getImageURI(ctx);

		// Test so the correct image is returned
		assertThat(imageUrl).isNotNull();
		assertThat(imageUrl).isEqualTo(CREATE_PERSONALIZED_PAGE_AVAILABLE_ACTION_URL);
	}

	@Test
	public void shouldShowUnavailableCreatePersonalizedPageAction()
	{
		final MultiCountryCmsPageBrowserModel browser = createExecution();

		// Sets so the page would be seens as global and is displaying all localised pages in the view
		browser.setDefaultPage(true);
		browser.setTopLevel(true);
		browser.setShowLocalizePages(true);

		final ListViewAction.Context ctx = new ListViewAction.Context();

		// Lock page for Anonymous and allow localise action
		final UserModel userOther = userService.getAnonymousUser();
		given(pageModel.getCatalogVersion()).willReturn(catalogVersion);
		given(typeObject.getObject()).willReturn(pageModel);
		given(pageModel.getLockedBy()).willReturn(userOther);
		given(catalogVersionService.canWrite(catalogVersion, user)).willReturn(Boolean.TRUE);

		ctx.setItem(typeObject);

		// Call the action
		final String imageUrl = multiCountryCreatePersonalizedPageAction.getImageURI(ctx);

		// Test so the correct image is returned
		assertThat(imageUrl).isNotNull();
		assertThat(imageUrl).isEqualTo(CREATE_PERSONALIZED_PAGE_UNAVAILABLE_ACTION_URL);
	}

	@Test
	public void shouldNotShowCreatePersonalizedPageAction()
	{

		final MultiCountryCmsPageBrowserModel browser = createExecution();

		// Sets so the page would be seens as global and is displaying all localised pages in the view
		browser.setDefaultPage(true);
		browser.setTopLevel(true);
		browser.setShowLocalizePages(true);

		final ListViewAction.Context ctx = new ListViewAction.Context();

		// Lock page for admin and allow localise action
		given(pageModel.getCatalogVersion()).willReturn(catalogVersion);
		given(typeObject.getObject()).willReturn(pageModel);
		given(pageModel.getLockedBy()).willReturn(user);
		given(catalogVersionService.canWrite(catalogVersion, user)).willReturn(Boolean.FALSE);

		ctx.setItem(typeObject);

		// Call the action
		final String imageUrl = multiCountryCreatePersonalizedPageAction.getImageURI(ctx);

		// Test so there is no image returned.
		assertThat(imageUrl).isNull();
	}

	@Test
	public void shouldShowCreatePersonalizedPageActionOnLocalPage()
	{
		final MultiCountryCmsPageBrowserModel browser = createExecution();

		// Sets so the page would be seens as local
		browser.setDefaultPage(true);
		browser.setTopLevel(false);
		browser.setIntermediateLevel(false);
		browser.setBottomLevel(true);

		final ListViewAction.Context ctx = new ListViewAction.Context();

		// Set localise property of page to false
		given(pageModel.getCatalogVersion()).willReturn(catalogVersion);
		given(typeObject.getObject()).willReturn(pageModel);
		given(catalogVersionService.canWrite(catalogVersion, user)).willReturn(Boolean.TRUE);

		ctx.setItem(typeObject);

		// Call the action
		final String imageUrl = multiCountryCreatePersonalizedPageAction.getImageURI(ctx);

		// Test so the correct image is returned
		assertThat(imageUrl).isNotNull();
		assertThat(imageUrl).isEqualTo(CREATE_PERSONALIZED_PAGE_AVAILABLE_ACTION_URL);
	}

	protected MultiCountryCmsPageBrowserModel createExecution()
	{
		// Creates a dummy execution, used by ZK
		final Execution execution = new MultiCountryDummyExecution(Registry.getCoreApplicationContext());
		execution.setAttribute("UICockpitSession", session);
		ExecutionsCtrl.setCurrent(execution);
		// Creating an empty Perspective with a browser
		final MultiCountryCmsCockpitPerspective perspective = new MultiCountryCmsCockpitPerspective();
		final MultiCountryCmsPageBrowserModel browser = new MultiCountryCmsPageBrowserModel(cmsAdminSiteService, cmsCockpitService,
				modelService, cmsAdminComponentService, cmsAdminContentSlotService);

		perspective.setBrowserArea(catalogArea);

		given(catalogArea.getFocusedBrowser()).willReturn(browser);
		given(session.getCurrentPerspective()).willReturn(perspective);
		given(systemService.getCurrentUser()).willReturn(user);
		given(session.getSystemService()).willReturn(systemService);

		return browser;
	}
}