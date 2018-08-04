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
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
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
import de.hybris.platform.cockpit.session.impl.UISessionImpl;
import de.hybris.platform.core.Registry;
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
public class MultiCountryShowLocalizedPageActionTest extends ServicelayerTransactionalTest
{
	private static final String SHOW_PERSONALIZED_PAGE_AVAILABLE_ACTION_URL = "/cmscockpit/images/icon_func_show_localized_page.png";
	private static final String SHOW_PERSONALIZED_PAGE_UNAVAILABLE_ACTION_URL = "/cmscockpit/images/icon_func_show_localized_page_unavailable.png";

	private MultiCountryShowLocalizedPageAction multiCountryShowLocalizedPageAction;

	@Resource
	private CMSAdminSiteService cmsAdminSiteService;
	@Resource
	private ModelService modelService;
	@Resource
	private CMSAdminComponentService cmsAdminComponentService;
	@Resource
	private CMSAdminContentSlotService cmsAdminContentSlotService;
	@Resource
	private CatalogVersionService catalogVersionService;
	@Resource
	private CMSSiteService cmsSiteService;
	@Resource
	private CommonI18NService commonI18NService;
	@Resource
	private ConfigurationService configurationService;
	@Resource
	private UserService userService;

	@Mock
	private TypedObject typeObject;
	@Mock
	private AbstractPageModel pageModel;
	@Mock
	private UISessionImpl session;
	@Mock
	private CatalogBrowserArea catalogArea;
	@Mock
	private MultiCountryCockpitService cmsCockpitService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		// Create the action bean
		multiCountryShowLocalizedPageAction = new MultiCountryShowLocalizedPageAction();
		multiCountryShowLocalizedPageAction.setCatalogVersionService(catalogVersionService);
		multiCountryShowLocalizedPageAction.setCmsCockpitService(cmsCockpitService);
		multiCountryShowLocalizedPageAction.setCmsSiteService(cmsSiteService);
		multiCountryShowLocalizedPageAction.setCommonI18NService(commonI18NService);
		multiCountryShowLocalizedPageAction.setConfigurationService(configurationService);
		multiCountryShowLocalizedPageAction.setUserService(userService);
	}

	@Test
	public void shouldShowLocalizedAction()
	{
		final MultiCountryCmsPageBrowserModel browser = createExecution();

		// Sets so the page would be seens as global and is not displaying all localised pages in the view
		browser.setDefaultPage(true);
		browser.setTopLevel(true);
		browser.setShowLocalizePages(false);

		final ListViewAction.Context ctx = new ListViewAction.Context();

		given(pageModel.isCopyToCatalogsDisabled()).willReturn(Boolean.FALSE);
		given(typeObject.getObject()).willReturn(pageModel);
		given(cmsCockpitService.isLocalizationDisabledForPage(pageModel)).willReturn(false);

		multiCountryShowLocalizedPageAction.setConfigurationService(configurationService);
		multiCountryShowLocalizedPageAction.setCmsCockpitService(cmsCockpitService);

		ctx.setItem(typeObject);

		// Call the action
		final String imageUrl = multiCountryShowLocalizedPageAction.getImageURI(ctx);

		// Test so the correct image is returned
		assertThat(imageUrl).isNotNull();
		assertThat(imageUrl).isEqualTo(SHOW_PERSONALIZED_PAGE_AVAILABLE_ACTION_URL);
	}

	@Test
	public void shouldShowUnavailableLocalizedAction()
	{
		final MultiCountryCmsPageBrowserModel browser = createExecution();

		// Sets so the page would be seens as global and is displaying all localised pages in the view
		browser.setDefaultPage(true);
		browser.setTopLevel(true);
		browser.setShowLocalizePages(true);

		final ListViewAction.Context ctx = new ListViewAction.Context();

		given(pageModel.isCopyToCatalogsDisabled()).willReturn(Boolean.FALSE);
		given(typeObject.getObject()).willReturn(pageModel);
		given(cmsCockpitService.isLocalizationDisabledForPage(pageModel)).willReturn(false);

		multiCountryShowLocalizedPageAction.setConfigurationService(configurationService);
		multiCountryShowLocalizedPageAction.setCmsCockpitService(cmsCockpitService);

		ctx.setItem(typeObject);

		// Call the actions
		final String imageUrl = multiCountryShowLocalizedPageAction.getImageURI(ctx);

		// Test so the correct image is returned
		assertThat(imageUrl).isNotNull();
		assertThat(imageUrl).isEqualTo(SHOW_PERSONALIZED_PAGE_UNAVAILABLE_ACTION_URL);
	}

	@Test
	public void shouldNotShowLocalizedActionWhenLocaliseIsDisabled()
	{
		final MultiCountryCmsPageBrowserModel browser = createExecution();

		// Sets so the page would be seens as global and is not displaying all localised pages in the view
		browser.setDefaultPage(true);
		browser.setTopLevel(true);

		final ListViewAction.Context ctx = new ListViewAction.Context();

		given(pageModel.isCopyToCatalogsDisabled()).willReturn(Boolean.TRUE);
		given(typeObject.getObject()).willReturn(pageModel);
		given(cmsCockpitService.isLocalizationDisabledForPage(pageModel)).willReturn(true);

		multiCountryShowLocalizedPageAction.setConfigurationService(configurationService);
		multiCountryShowLocalizedPageAction.setCmsCockpitService(cmsCockpitService);

		ctx.setItem(typeObject);

		// Call the action
		final String imageUrl = multiCountryShowLocalizedPageAction.getImageURI(ctx);

		// Test so no image is returned
		assertThat(imageUrl).isNull();
	}

	@Test
	public void shouldNotShowLocalizedActionOnLocalPage()
	{
		final MultiCountryCmsPageBrowserModel browser = createExecution();

		// Sets so the page would be seens as local
		browser.setDefaultPage(true);
		browser.setTopLevel(false);
		browser.setIntermediateLevel(false);
		browser.setBottomLevel(true);

		given(cmsCockpitService.isLocalizationDisabledForPage(pageModel)).willReturn(false);

		multiCountryShowLocalizedPageAction.setConfigurationService(configurationService);
		multiCountryShowLocalizedPageAction.setCmsCockpitService(cmsCockpitService);
		final ListViewAction.Context ctx = new ListViewAction.Context();

		ctx.setItem(typeObject);

		// Call the action
		final String imageUrl = multiCountryShowLocalizedPageAction.getImageURI(ctx);

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

		given(catalogArea.getFocusedBrowser()).willReturn(browser);
		given(session.getCurrentPerspective()).willReturn(perspective);
		return browser;
	}

}
