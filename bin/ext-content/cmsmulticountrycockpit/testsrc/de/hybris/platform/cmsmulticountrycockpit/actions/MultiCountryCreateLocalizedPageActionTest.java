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
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.multicountry.service.CatalogLevelService;
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
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.sys.ExecutionsCtrl;

import javax.annotation.Resource;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;


@IntegrationTest
public class MultiCountryCreateLocalizedPageActionTest extends ServicelayerTransactionalTest
{
	private static final String CREATE_PERSONALIZED_PAGE_AVAILABLE_ACTION_URL = "/cmscockpit/images/icon_func_new_localized_page.png";
	private static final String CREATE_PERSONALIZED_PAGE_UNAVAILABLE_ACTION_URL = "/cmscockpit/images/icon_func_new_localized_page_unavailable.png";

	private MultiCountryCreateLocalizedPageAction multiCountryCreateLocalizedPageAction;

	@Resource
	private UserService userService;
	@Resource
	private CatalogVersionService catalogVersionService;
	@Resource
	private ModelService modelService;
	@Resource
	private CMSAdminComponentService cmsAdminComponentService;
	@Resource
	private CMSAdminContentSlotService cmsAdminContentSlotService;
	@Resource
	private CatalogLevelService cmsCatalogLevelService;
	@Resource
	private CommonI18NService commonI18NService;
	@Resource
	private ConfigurationService configurationService;
	@Resource
	private CMSAdminSiteService cmsAdminSiteService;

	@Mock
	private TypedObject typeObject;
	@Mock
	private AbstractPageModel pageModel;
	@Mock
	private UISessionImpl session;
	@Mock
	private CatalogBrowserArea catalogArea;
	@Mock
	private SystemService systemService;
	@Mock
	private MultiCountryCockpitService cmsCockpitService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		// Create the action bean
		multiCountryCreateLocalizedPageAction = new MultiCountryCreateLocalizedPageAction();
		multiCountryCreateLocalizedPageAction.setCatalogVersionService(catalogVersionService);
		multiCountryCreateLocalizedPageAction.setCmsCatalogLevelService(cmsCatalogLevelService);
		multiCountryCreateLocalizedPageAction.setCmsCockpitService(cmsCockpitService);
		multiCountryCreateLocalizedPageAction.setCommonI18NService(commonI18NService);
		multiCountryCreateLocalizedPageAction.setConfigurationService(configurationService);
		multiCountryCreateLocalizedPageAction.setUserService(userService);
	}

	@Test
	public void shouldShowCreateLocalPageAction()
	{
		final MultiCountryCmsPageBrowserModel browser = createExecution();

		// Sets so the page would be seen as global
		browser.setDefaultPage(true);
		browser.setTopLevel(true);

		final ListViewAction.Context ctx = new ListViewAction.Context();

		// unlocking package and allowing localize action
		final UserModel user = null;
		given(pageModel.isCopyToCatalogsDisabled()).willReturn(Boolean.FALSE);
		given(typeObject.getObject()).willReturn(pageModel);
		given(pageModel.getLockedBy()).willReturn(user);

		ctx.setItem(typeObject);

		// Call the action
		final String imageUrl = multiCountryCreateLocalizedPageAction.getImageURI(ctx);

		// Test so the correct image is returned
		assertThat(imageUrl).isNotNull();
		assertThat(imageUrl).isEqualTo(CREATE_PERSONALIZED_PAGE_AVAILABLE_ACTION_URL);
	}

	@Test
	public void shouldShowUnavailableCreateLocalPageAction()
	{
		final MultiCountryCmsPageBrowserModel browser = createExecution();

		// Sets so the page would be seens as global and is displaying all localised pages in the view
		browser.setDefaultPage(true);
		browser.setTopLevel(true);
		browser.setShowLocalizePages(true);

		final ListViewAction.Context ctx = new ListViewAction.Context();

		// Lock page for admin and allow localise action
		final UserModel user = userService.getAdminUser();
		given(pageModel.isCopyToCatalogsDisabled()).willReturn(Boolean.FALSE);
		given(typeObject.getObject()).willReturn(pageModel);
		given(pageModel.getLockedBy()).willReturn(user);

		ctx.setItem(typeObject);

		// Call the action
		final String imageUrl = multiCountryCreateLocalizedPageAction.getImageURI(ctx);

		// Test so the correct image is returned
		assertThat(imageUrl).isNotNull();
		assertThat(imageUrl).isEqualTo(CREATE_PERSONALIZED_PAGE_UNAVAILABLE_ACTION_URL);
	}

	@Test
	public void shouldNotShowCreateLocalPageActionWhenLocalizeIsDisabled()
	{
		final MultiCountryCmsPageBrowserModel browser = createExecution();

		// Sets so the page would be seens as global and is not displaying all localised pages in the view
		browser.setDefaultPage(true);
		browser.setTopLevel(true);

		final ListViewAction.Context ctx = new ListViewAction.Context();

		// Lock page for anonymous user and disable localise action
		final UserModel user = userService.getAnonymousUser();
		given(pageModel.isCopyToCatalogsDisabled()).willReturn(Boolean.TRUE);
		given(typeObject.getObject()).willReturn(pageModel);
		given(pageModel.getLockedBy()).willReturn(user);
		given(cmsCockpitService.isLocalizationDisabledForPage(pageModel)).willReturn(Boolean.TRUE);

		ctx.setItem(typeObject);

		// Call the action
		final String imageUrl = multiCountryCreateLocalizedPageAction.getImageURI(ctx);

		// Test so no image is returned
		assertThat(imageUrl).isNull();
	}

	@Test
	public void shouldNotShowCreateLocalPageActionOnLocalPage()
	{
		final MultiCountryCmsPageBrowserModel browser = createExecution();

		// Sets so the page would be seens as local
		browser.setDefaultPage(true);
		browser.setTopLevel(false);
		browser.setIntermediateLevel(false);
		browser.setBottomLevel(true);

		final ListViewAction.Context ctx = new ListViewAction.Context();

		// Set localise property of page to false
		given(pageModel.isCopyToCatalogsDisabled()).willReturn(Boolean.FALSE);
		given(typeObject.getObject()).willReturn(pageModel);
		ctx.setItem(typeObject);

		// Call the action
		final String imageUrl = multiCountryCreateLocalizedPageAction.getImageURI(ctx);

		// Test so no image is returned
		assertThat(imageUrl).isNull();
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

		final UserModel user = userService.getAnonymousUser();

		given(catalogArea.getFocusedBrowser()).willReturn(browser);
		given(session.getCurrentPerspective()).willReturn(perspective);
		given(systemService.getCurrentUser()).willReturn(user);
		given(session.getSystemService()).willReturn(systemService);

		return browser;
	}
}
