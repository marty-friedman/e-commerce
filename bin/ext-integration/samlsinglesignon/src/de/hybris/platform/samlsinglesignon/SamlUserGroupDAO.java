/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 */

package de.hybris.platform.samlsinglesignon;


import de.hybris.platform.samlsinglesignon.model.SamlUserGroupModel;

import java.util.Optional;


/**
 * Data access object for {@link SamlUserGroupModel}
 */
public interface SamlUserGroupDAO
{
	/**
	 * Finds {@link SamlUserGroupModel} for given samlUserGroup (searches by {@link SamlUserGroupModel#SAMLUSERGROUP}).
	 * 
	 * @param samlUserGroup
	 *           the value to search by
	 * @return {@link SamlUserGroupModel} for given samlUserGroup.
	 */
	Optional<SamlUserGroupModel> findSamlUserGroup(String samlUserGroup);
}
