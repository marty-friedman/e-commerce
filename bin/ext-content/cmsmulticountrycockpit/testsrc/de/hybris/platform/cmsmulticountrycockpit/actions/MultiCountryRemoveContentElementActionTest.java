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
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.servicelayer.services.CMSPageLockingService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminComponentService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminContentSlotService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cmscockpit.session.impl.CatalogBrowserArea;
import de.hybris.platform.cmsmulticountrycockpit.services.MultiCountryCockpitService;
import de.hybris.platform.cmsmulticountrycockpit.session.impl.MultiCountryCmsCockpitPerspective;
import de.hybris.platform.cmsmulticountrycockpit.session.impl.MultiCountryCmsPageBrowserModel;
import de.hybris.platform.cmsmulticountrycockpit.utils.MultiCountryDummyExecution;
import de.hybris.platform.cockpit.components.listview.ListViewAction;
import de.hybris.platform.cockpit.model.listview.impl.SectionTableModel;
import de.hybris.platform.cockpit.model.meta.BaseType;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.services.SystemService;
import de.hybris.platform.cockpit.services.meta.TypeService;
import de.hybris.platform.cockpit.services.security.UIAccessRightService;
import de.hybris.platform.cockpit.session.BrowserSectionModel;
import de.hybris.platform.cockpit.session.impl.UISessionImpl;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.hmc.jalo.AccessManager;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.sys.ExecutionsCtrl;

import javax.annotation.Resource;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@IntegrationTest
public class MultiCountryRemoveContentElementActionTest extends ServicelayerTransactionalTest
{
	private static final String IMAGE_REMOVE_ACTION = "/cmscockpit/images/cnt_elem_remove_action.png";

	private MultiCountryRemoveContentElementAction multiCountryRemoveContentElementAction;

	@Resource
	private UserService userService;
	@Resource
	private ModelService modelService;
	@Resource
	private CMSAdminComponentService cmsAdminComponentService;
	@Resource
	private CMSAdminContentSlotService cmsAdminContentSlotService;
	@Resource
	private CMSAdminSiteService cmsAdminSiteService;

	@Mock
	private SectionTableModel sectionModel;
	@Mock
	private BrowserSectionModel browserModel;
	@Mock
	private CatalogVersionModel catalogVersionModel;
	@Mock
	private ContentSlotModel contentSlotModel;
	@Mock
	private AbstractCMSComponentModel abstractCMSComponentModel;
	@Mock
	private BaseType contentSlotBaseType;
	@Mock
	private BaseType componentBaseType;
	@Mock
	private TypedObject catalogVersionTypedObject;
	@Mock
	private TypedObject contentSlotTypedObject;
	@Mock
	private TypedObject componentTypedObject;
	@Mock
	private UISessionImpl session;
	@Mock
	private CatalogBrowserArea catalogArea;
	@Mock
	private SystemService systemService;
	@Mock
	private TypeService typeService;
	@Mock
	private MultiCountryCockpitService cmsCockpitService;
	@Mock
	private UIAccessRightService uiAccessRightService;
	@Mock
	private CMSPageLockingService cmsPageLockingService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		// Create the action bean
		multiCountryRemoveContentElementAction = new MultiCountryRemoveContentElementAction();

		final ApplicationContext mockApplicationContext = mock(ApplicationContext.class);
		when(mockApplicationContext.getBean("uiAccessRightService")).thenReturn(uiAccessRightService);
		when(mockApplicationContext.getBean("cmsPageLockingService")).thenReturn(cmsPageLockingService);

		// Creates a dummy execution, used by ZK
		final Execution execution = new MultiCountryDummyExecution(mockApplicationContext);
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
		given(session.getTypeService()).willReturn(typeService);
	}

	@Test
	public void shouldShowRemoveContentElementPageAction()
	{
		final ListViewAction.Context ctx = new ListViewAction.Context();

		final UserModel user = userService.getAnonymousUser();

		given(systemService.checkPermissionOn(ContentSlotModel._TYPECODE, AccessManager.CHANGE)).willReturn(Boolean.TRUE);

		given(sectionModel.getModel()).willReturn(browserModel);
		given(browserModel.getRootItem()).willReturn(contentSlotTypedObject);

		given(contentSlotTypedObject.getObject()).willReturn(contentSlotModel);
		given(contentSlotTypedObject.getType()).willReturn(contentSlotBaseType);

		given(componentTypedObject.getObject()).willReturn(abstractCMSComponentModel);
		given(componentTypedObject.getType()).willReturn(componentBaseType);

		given(contentSlotBaseType.getCode()).willReturn(ContentSlotModel._TYPECODE);

		given(componentBaseType.getCode()).willReturn(AbstractCMSComponentModel._TYPECODE);
		given(componentBaseType.isAssignableFrom(componentBaseType)).willReturn(Boolean.TRUE);

		given(systemService.checkPermissionOn(AbstractCMSComponentModel._TYPECODE, AccessManager.REMOVE)).willReturn(Boolean.TRUE);
		given(systemService.checkPermissionOn(ContentSlotModel._TYPECODE, AccessManager.REMOVE)).willReturn(Boolean.TRUE);

		given(cmsPageLockingService.isContentSlotLockedForUser(contentSlotModel, user)).willReturn(Boolean.FALSE);
		given(typeService.getBaseType(AbstractCMSComponentModel._TYPECODE)).willReturn(componentBaseType);
		given(cmsPageLockingService.isComponentLockedForUser(abstractCMSComponentModel, user)).willReturn(Boolean.FALSE);

		given(contentSlotModel.getCatalogVersion()).willReturn(catalogVersionModel);
		given(typeService.wrapItem(catalogVersionModel)).willReturn(catalogVersionTypedObject);
		given(uiAccessRightService.isWritable(contentSlotBaseType, catalogVersionTypedObject)).willReturn(Boolean.TRUE);
		given(abstractCMSComponentModel.getCatalogVersion()).willReturn(catalogVersionModel);
		given(uiAccessRightService.isWritable(componentBaseType, catalogVersionTypedObject)).willReturn(Boolean.TRUE);

		ctx.setItem(componentTypedObject);
		ctx.setModel(sectionModel);

		final String imageUrl = multiCountryRemoveContentElementAction.getImageURI(ctx);

		assertThat(imageUrl).isNotNull();
		assertThat(imageUrl).isEqualTo(IMAGE_REMOVE_ACTION);
	}

	@Test
	public void shouldNotShowRemoveContentElementPageActionNotEnabled()
	{
		final ListViewAction.Context ctx = new ListViewAction.Context();

		given(systemService.checkPermissionOn(ContentSlotModel._TYPECODE, AccessManager.CHANGE)).willReturn(Boolean.FALSE);

		ctx.setItem(componentTypedObject);

		final String imageUrl = multiCountryRemoveContentElementAction.getImageURI(ctx);

		assertThat(imageUrl).isNull();
	}
}
