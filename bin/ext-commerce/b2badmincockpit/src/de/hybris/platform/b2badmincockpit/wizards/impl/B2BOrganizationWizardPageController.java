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
package de.hybris.platform.b2badmincockpit.wizards.impl;

import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.enums.B2BPeriodRange;
import de.hybris.platform.b2b.model.B2BBudgetModel;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.model.B2BCreditLimitModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.b2b.services.B2BItemService;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.cockpit.helpers.ModelHelper;
import de.hybris.platform.cockpit.services.values.ValueHandlerException;
import de.hybris.platform.cockpit.wizards.Message;
import de.hybris.platform.cockpit.wizards.Wizard;
import de.hybris.platform.cockpit.wizards.WizardPage;
import de.hybris.platform.cockpit.wizards.exception.WizardConfirmationException;
import de.hybris.platform.cockpit.wizards.impl.DefaultPageController;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.tx.Transaction;
import de.hybris.platform.util.StandardDateRange;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zul.ListModelSet;


public class B2BOrganizationWizardPageController extends DefaultPageController
{

	private final static Logger LOG = Logger.getLogger(B2BOrganizationWizardPageController.class);
	@SuppressWarnings("deprecation")
	private B2BItemService b2bItemService;
	private ModelService modelService;
	private ModelHelper modelHelper;
	private B2BUnitService b2bUnitService;
	private B2BCustomerService b2bCustomerService;
	private UserService userService;
	private ListModelSet userGroups = new ListModelSet();
	private Set<String> attributeSets;
	private B2BUnitModel unit;
	private CurrencyModel currency;
	private B2BCostCenterModel costCenter;
	private SessionService sessionService;



	protected ModelHelper getModelHelper()
	{
		return modelHelper;
	}

	public void setModelHelper(final ModelHelper modelHelper)
	{
		this.modelHelper = modelHelper;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected B2BCustomerService getB2bCustomerService()
	{
		return b2bCustomerService;
	}

	@Autowired
	public void setB2bCustomerService(final B2BCustomerService b2bCustomerService)
	{
		this.b2bCustomerService = b2bCustomerService;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Autowired
	public void setUserService(final UserService defaultUserService)
	{
		this.userService = defaultUserService;
	}

	protected B2BUnitService getB2bUnitService()
	{
		return b2bUnitService;
	}

	@Autowired
	public void setB2bUnitService(final B2BUnitService b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}

	protected B2BItemService getB2bItemService()
	{
		return b2bItemService;
	}

	@Autowired
	public void setb2bItemService(final B2BItemService b2bItemService)
	{
		this.b2bItemService = b2bItemService;
	}

	public SessionService getSessionService()
	{
		return sessionService;
	}

	@Autowired
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	@Override
	public void done(final Wizard wizard, final WizardPage page) throws WizardConfirmationException
	{
		createOrganization(wizard);
	}

	protected void createUnit(final Wizard wizard)
	{
		final HashMap<String, Object> commonAttributes = (HashMap<String, Object>) getAttributesForPage("common", wizard);

		unit = getModelService().create(B2BUnitModel.class);
		unit.setUid((String) commonAttributes.get("uid"));
		unit.setName((String) commonAttributes.get("name"));
		unit.setDescription((String) commonAttributes.get("description"));
		saveModel(unit);
	}

	protected void createAddress(final Wizard wizard)
	{
		final HashMap<String, Object> addressAttributes = (HashMap<String, Object>) getAttributesForPage("address", wizard);
		final AddressModel address = getModelService().create(AddressModel.class);

		TitleModel title = new TitleModel();

		@SuppressWarnings("deprecation")
		final List<TitleModel> titleModels = b2bItemService.findAllItems(TitleModel.class);
		for (final TitleModel titleModel : titleModels)
		{
			if (titleModel.getName().equals(addressAttributes.get("title")))
			{
				title = titleModel;
			}
		}
		address.setTitle(title);

		address.setFirstname((String) addressAttributes.get("firstName"));
		address.setLastname((String) addressAttributes.get("lastName"));
		address.setCompany((String) addressAttributes.get("company"));
		address.setLine1((String) addressAttributes.get("line1"));
		address.setLine2((String) addressAttributes.get("line2"));
		address.setTown((String) addressAttributes.get("town"));
		address.setPostalcode((String) addressAttributes.get("postalCode"));
		address.setPhone1((String) addressAttributes.get("phone1"));

		CountryModel country = new CountryModel();
		@SuppressWarnings("deprecation")
		final List<CountryModel> countryModels = b2bItemService.findAllItems(CountryModel.class);
		for (final CountryModel countryModel : countryModels)
		{
			if (countryModel.getIsocode().equals(addressAttributes.get("country")))
			{
				country = countryModel;
				break;
			}
		}
		address.setCountry(country);
		address.setOwner(unit);
		unit.setBillingAddress(address);

		saveModel(address);
		saveModel(unit);
	}

	protected void createB2BAdmin(final Wizard wizard)
	{
		final HashMap<String, Object> b2bAdminAttributes = (HashMap<String, Object>) getAttributesForPage("b2bAdmin", wizard);
		final UserGroupModel b2bAdminGroup = getUserService().getUserGroupForUID(B2BConstants.B2BADMINGROUP);

		// create the admin
		final B2BCustomerModel b2bAdmin = getModelService().create(B2BCustomerModel.class);
		b2bAdmin.setUid((String) b2bAdminAttributes.get("uid"));
		b2bAdmin.setEmail((String) b2bAdminAttributes.get("email"));

		//add name if provided.
		if (b2bAdminAttributes.get("name") != null && !b2bAdminAttributes.get("name").equals(""))
		{
			b2bAdmin.setName((String) b2bAdminAttributes.get("name"));
		}

		// assign the admin to the admingroup
		b2bAdmin.setGroups(Collections.<PrincipalGroupModel> singleton(b2bAdminGroup));

		//set password & unit
		getB2bCustomerService().addMember(b2bAdmin, unit);
		getB2bUnitService().setCurrentUnit(b2bAdmin, unit);
		getUserService().setPasswordWithDefaultEncoding(b2bAdmin, (String) b2bAdminAttributes.get("password"));

		saveModel(b2bAdmin);
		saveModel(unit);
	}

	protected void createAccountManager(final Wizard wizard)
	{
		final HashMap<String, Object> b2bAccountManagerAttributes = (HashMap<String, Object>) getAttributesForPage(
				"accountManager", wizard);
		final EmployeeModel accountManager = (EmployeeModel) userService.getUserForUID((String) b2bAccountManagerAttributes
				.get("uid"));

		unit.setAccountManager(accountManager);
		saveModel(unit);

	}

	protected void createCostCenter(final Wizard wizard)
	{
		final HashMap<String, Object> b2bCostCenterAttributes = (HashMap<String, Object>) getAttributesForPage("costCenter", wizard);
		costCenter = getModelService().create(B2BCostCenterModel.class);

		costCenter.setUnit(unit);
		costCenter.setCode((String) b2bCostCenterAttributes.get("uid"));
		costCenter.setName((String) b2bCostCenterAttributes.get("name"));
		currency = getCurrencyModel((String) b2bCostCenterAttributes.get("currency"));
		costCenter.setCurrency(currency);

		saveModel(costCenter);
		saveModel(unit);
	}

	protected void createBudget(final Wizard wizard)
	{
		final HashMap<String, Object> b2bBudgetAttributes = (HashMap<String, Object>) getAttributesForPage("budget", wizard);
		final B2BBudgetModel budget = getModelService().create(B2BBudgetModel.class);

		//set budget value
		final BigDecimal budgetBigDecimal = new BigDecimal((String) b2bBudgetAttributes.get("budget"));
		budget.setBudget(budgetBigDecimal);

		//set budget code
		budget.setCode((String) b2bBudgetAttributes.get("uid"));

		final Date start = (Date) b2bBudgetAttributes.get("startDate");
		final Date end = (Date) b2bBudgetAttributes.get("endDate");

		final StandardDateRange dateRange = new StandardDateRange(start, end);
		budget.setDateRange(dateRange);

		//set currency
		budget.setCurrency(currency);

		//add budget to cost center
		final ListModelSet budgetSet = new ListModelSet();
		budgetSet.add(budget);
		costCenter.setBudgets(budgetSet);

		//set unit and save.
		budget.setUnit(unit);
		saveModel(budget);
		saveModel(costCenter);
		saveModel(unit);
	}


	protected void createCreditLimit(final Wizard wizard)
	{
		final HashMap<String, Object> b2bCreditLimitAttributes = (HashMap<String, Object>) getAttributesForPage("creditLimit",
				wizard);
		final B2BCreditLimitModel b2bCreditLimit = getModelService().create(B2BCreditLimitModel.class);

		b2bCreditLimit.setCurrency(currency);
		b2bCreditLimit.setAmount(new BigDecimal((String) b2bCreditLimitAttributes.get("creditLimit")));
		b2bCreditLimit.setCode((String) b2bCreditLimitAttributes.get("uid"));

		final B2BPeriodRange[] b2bPeriodRanges = B2BPeriodRange.values();

		for (final B2BPeriodRange period : b2bPeriodRanges)
		{
			if (b2bCreditLimitAttributes.get("period").equals(period.toString()))
			{
				b2bCreditLimit.setDateRange(period);
				break;
			}
		}

		unit.setCreditLimit(b2bCreditLimit);
		saveModel(b2bCreditLimit);
		saveModel(unit);
	}

	protected void createApprovalProcess(final Wizard wizard)
	{
		final HashMap<String, Object> b2bApprovalAttributes = (HashMap<String, Object>) getAttributesForPage("approval", wizard);
		unit.setApprovalProcessCode((String) b2bApprovalAttributes.get("approval"));
		saveModel(unit);
	}

	protected void createOrganization(final Wizard wizard)
	{
		final Transaction tx = Transaction.current();
		boolean success = false;

		tx.begin();

		try
		{
			createUnit(wizard);
			createAddress(wizard);
			createB2BAdmin(wizard);
			createAccountManager(wizard);
			createCostCenter(wizard);
			createBudget(wizard);
			createCreditLimit(wizard);
			createApprovalProcess(wizard);
			success = true;
		}
		finally
		{
			if (success)
			{
				tx.commit();
			}
			else
			{
				tx.rollback();
			}
		}
	}

	protected ListModelSet getUserGroups()
	{
		final Set<B2BUserGroupModel> groups = b2bItemService.findAllB2BUserGroups();

		userGroups.addAll(groups);

		return userGroups;
	}

	public void setUserGroups(final ListModelSet userGroups)
	{
		this.userGroups = userGroups;
	}

	/*
	 * Helper method that returns the CurrencyModel specified by the parameter name (eg. 'US Dollar').
	 */
	protected CurrencyModel getCurrencyModel(final String name)
	{
		@SuppressWarnings("deprecation")
		final List<CurrencyModel> modelList = b2bItemService.findAllItems(CurrencyModel.class);

		for (final CurrencyModel model : modelList)
		{
			if (model.getName().equals(name))
			{
				return model;
			}
		}

		return null;
	}

	@Override
	public boolean validate(final Wizard wizard, final WizardPage page)
	{
		final AbstractB2BOrganizationWizardPage currentPage = (AbstractB2BOrganizationWizardPage) page;
		final List<Message> validationMessages = new ArrayList<Message>();

		// validate current page
		validationMessages.addAll(currentPage.validate());
		// only set message for attributes not set by prior step, as the messages in prior step are more specific
		for (final Message message : currentPage.validateAttributes())
		{
			final boolean hasAlreadyMsgForAttribute = CollectionUtils.exists(validationMessages, new Predicate()
			{
				@Override
				public boolean evaluate(final Object object)
				{
					return "uid".equals(((Message) object).getComponentId());
				}
			});

			if (!hasAlreadyMsgForAttribute)
			{
				validationMessages.add(message);
			}
		}

		//checks to see if all of the uids entered in the wizard are unique.
		for (final Iterator wizardPageIterator = getAttributeSets().iterator(); wizardPageIterator.hasNext();)
		{
			final String pageId = (String) wizardPageIterator.next();

			final Map<String, Object> otherPageAttributes = getAttributesForPage(pageId, wizard);

			if ((currentPage.getAttribute("uid") != null) && !currentPage.getId().equals(pageId) && otherPageAttributes != null
					&& (currentPage.getAttribute("uid").equals(otherPageAttributes.get("uid"))))
			{
				currentPage.setAttribute("uid", "");
				validationMessages.add(new Message(Message.ERROR, "The Uid you selected already exists.", "uid"));
			}
		}

		final boolean isValid = validationMessages.isEmpty();

		if (!isValid)
		{
			for (final Iterator messageIterator = validationMessages.iterator(); messageIterator.hasNext();)
			{
				final Message message = (Message) messageIterator.next();
				wizard.addMessage(message);
			}
		}

		return isValid;
	}

	protected Map<String, Object> getAttributesForPage(final String pageId, final Wizard wizard)
	{
		return (Map<String, Object>) wizard.getWizardContext().getAttribute(pageId);
	}

	/**
	 * @return the attributeSets
	 */
	protected Set<String> getAttributeSets()
	{
		return attributeSets;
	}

	/**
	 * @param attributeSets
	 *           the attributeSets to set
	 */
	public void setAttributeSets(final Set<String> attributeSets)
	{
		this.attributeSets = attributeSets;
	}

	protected void saveModel(final ItemModel model)
	{
		try
		{
			getModelHelper().saveModel(model, true, true);
		}
		catch (final ValueHandlerException e)
		{
			LOG.error("There was an error saving model: " + model.getItemtype() + " " + model.toString(), e);
		}
	}
}
