package org.merchandise.servicelayer.interceptor;

import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;
import de.hybris.platform.servicelayer.user.daos.UserGroupDao;
import org.springframework.beans.factory.annotation.Required;

import java.util.HashSet;
import java.util.Set;

public class MerchandiseCustomersPrepareInterceptor implements PrepareInterceptor {
	private static final String MERCHANDISEGROUP = "merchandisegroup";
	private UserGroupDao userGroupDao;

	@Override
	public void onPrepare(final Object model, final InterceptorContext ctx) {
		if (model instanceof CustomerModel) {
			final CustomerModel customer = (CustomerModel) model;
			final UserGroupModel merchandise = userGroupDao.findUserGroupByUid(MERCHANDISEGROUP);

			if (Boolean.TRUE.equals(customer.getInternal())) {
				if (!customer.getGroups().contains(merchandise)) {
					final Set<PrincipalGroupModel> newGroups = new HashSet<>(customer.getGroups());
					newGroups.add(merchandise);
					customer.setGroups(newGroups);
				}
			} else {
				if (customer.getGroups().contains(merchandise)) {
					final Set<PrincipalGroupModel> newGroups = new HashSet<>(customer.getGroups());
					newGroups.remove(merchandise);
					customer.setGroups(newGroups);
				}
			}
		}
	}

	@Required
	public void setUserGroupDao(final UserGroupDao userGroupDao) {
		this.userGroupDao = userGroupDao;
	}
}
