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
package de.hybris.platform.b2badmincockpit.session.impl;

import de.hybris.platform.b2badmincockpit.components.sectionpanel.B2BAdminSectionPanelRenderer;
import de.hybris.platform.cockpit.components.sectionpanel.AbstractSectionPanelModel;
import de.hybris.platform.cockpit.components.sectionpanel.SectionPanelLabelRenderer;
import de.hybris.platform.cockpit.components.sectionpanel.SectionPanelModel;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.session.impl.DefaultEditorAreaController;

public class B2BDefaultEditorAreaController extends DefaultEditorAreaController
{

	private transient B2BAdminSectionPanelRenderer sectionPanelRenderer;

	@Override
	protected SectionPanelLabelRenderer createSectionPanelLabelRenderer()
	{

		sectionPanelRenderer = getSectionPanelRenderer();
		sectionPanelRenderer.setEditorArea(this.getModel());
		return sectionPanelRenderer;
	}

	@Override
	public void updateLabel(final SectionPanelModel sectionPanelModel)
	{
		final TypedObject current = getModel().getCurrentObject();
		sectionPanelRenderer.setCurrentObject(current);
		super.updateLabel(sectionPanelModel);
		((AbstractSectionPanelModel) sectionPanelModel).refreshInfoContainer();
	}

	/**
	 * @return the sectionPanelRenderer
	 */
	public B2BAdminSectionPanelRenderer getSectionPanelRenderer()
	{
		return sectionPanelRenderer;
	}

	/**
	 * @param sectionPanelRenderer
	 *           the sectionPanelRenderer to set
	 */
	public void setSectionPanelRenderer(final B2BAdminSectionPanelRenderer sectionPanelRenderer)
	{
		this.sectionPanelRenderer = sectionPanelRenderer;
	}


}
