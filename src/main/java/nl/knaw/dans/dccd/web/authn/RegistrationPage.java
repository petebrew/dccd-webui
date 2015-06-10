/*******************************************************************************
 * Copyright 2015 DANS - Data Archiving and Networked Services
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package nl.knaw.dans.dccd.web.authn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.web.template.AbstractCommonStatelessForm;
import nl.knaw.dans.common.web.template.CommonResources;
import nl.knaw.dans.dccd.application.services.DccdUserService;
import nl.knaw.dans.dccd.application.services.UserServiceException;
import nl.knaw.dans.dccd.authn.OrganisationRegistration;
import nl.knaw.dans.dccd.authn.UserRegistration;
import nl.knaw.dans.dccd.common.web.validate.PasswordPolicyValidator;
import nl.knaw.dans.dccd.model.DccdOrganisation;
import nl.knaw.dans.dccd.model.DccdOrganisationImpl;
import nl.knaw.dans.dccd.search.DccdSB;
import nl.knaw.dans.dccd.web.DccdSession;
import nl.knaw.dans.dccd.web.HomePage;
import nl.knaw.dans.dccd.web.base.BasePage;
import nl.knaw.dans.dccd.web.error.ErrorPage;

import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author paulboon
 */
public class RegistrationPage extends BasePage implements CommonResources
{
    private static final long serialVersionUID  = 1L;
    private static final Logger logger = LoggerFactory.getLogger(RegistrationPage.class);

    private static final String REGISTRATION_FORM = "registrationForm";
    private static final String WI_REGISTER = "register";
    private static final String WI_CANCEL = "cancel";

    private final SubmitLink registerLink = new SubmitLink(WI_REGISTER);

    /**
     * Default constructor.
     */
    public RegistrationPage()
    {
        super();
        init();
    }

    /**
     * Registration form.
     *
     * @author Herman Suijs
     */
    public class RegistrationForm extends AbstractCommonStatelessForm
    {
        private static final long serialVersionUID = 1L;
        private String            paramUserId;
        //private String            paramDateTime;
        //private String            paramToken;

        List<DccdOrganisation> organisations;
        DccdOrganisation selectedOrganisation;
        DccdOrganisation newOrganisation;
        private boolean organisationEdit = false;

        final String NEW_ORG_ID = "<" + getString("newOrganisationSelection") + ">";

		//OrganisationDisplayPanel organisationPanel;
        Panel organisationPanel;

        /**
         * Default constructor.
         *
         * @param wicketId
         *        Wicket id.
         */
        public RegistrationForm(final String wicketId)
        {
            this(wicketId, new ApplicationUser());
        }

        public boolean isOrganisationEdit() {
			return organisationEdit;
		}

		public void setOrganisationEdit(boolean organisationEdit)
		{
			this.organisationEdit = organisationEdit;
		}

        private void initOrganisations ()
        {
        	// fill the list with all organisations available
            try
            {
            	// don't support registration with a BLOCKED organisation
    			organisations = DccdUserService.getService().getNonblockedOrganisations();
    		}
            catch (UserServiceException e)
            {
    			// We need organisations when registering
    			e.printStackTrace();
    			getSession().error("Failed to retrieve organisations"); // use resource?
    			// go to the error page!
    			throw new RestartResponseException(ErrorPage.class);
    		}
        }

        /**
         * Constructor with model.
         *
         * @param wicketId
         *        Wicket id
         * @param appUser
         *        UserBean
         */
        public RegistrationForm(final String wicketId, final ApplicationUser appUser)
        {
            super(wicketId, new CompoundPropertyModel(appUser), false);

            initOrganisations();

            addCommonFeedbackPanel();

            // Add field userid
            RequiredTextField userIdTextField = new RequiredTextField(ApplicationUser.USER_ID);
            userIdTextField.add(StringValidator.minimumLength(UserProperties.MINIMUM_USER_ID_LENGTH));
            // Avoid special characters that might need escaping 
            // when used by services like storing and searching
            userIdTextField.add(new PatternValidator(UserProperties.USER_ID_ALLOWED_CHARS_PATTERN));
            addWithComponentFeedback(userIdTextField, new ResourceModel(USER_USER_ID));
            
            // Add Password
            PasswordTextField password = new PasswordTextField(ApplicationUser.PASSWORD);
            addWithComponentFeedback(password, new ResourceModel(USER_PASSWORD));
            password.setRequired(true);
            password.setResetPassword(false);
            password.add(PasswordPolicyValidator.getInstance());

            // Add confirm password
            PasswordTextField confirmPassword = new PasswordTextField(ApplicationUser.CONFIRM_PASSWORD);
            addWithComponentFeedback(confirmPassword, new ResourceModel(USER_CONFIRM_PASSWORD));
            confirmPassword.setRequired(true);
            confirmPassword.setResetPassword(false);
            add(new EqualPasswordInputValidator(password, confirmPassword));

            FormComponent email = new RequiredTextField(ApplicationUser.EMAIL);
            addWithComponentFeedback(email.add(EmailAddressValidator.getInstance()), new ResourceModel(USER_EMAIL));
            addWithComponentFeedback(new TextField(ApplicationUser.TITLE), new ResourceModel(USER_TITLE));
            addWithComponentFeedback(new RequiredTextField(ApplicationUser.INITIALS), new ResourceModel(USER_INITIALS));
            addWithComponentFeedback(new TextField(ApplicationUser.PREFIXES), new ResourceModel(USER_PREFIXES));
            addWithComponentFeedback(new RequiredTextField(ApplicationUser.SURNAME), new ResourceModel(USER_SURNAME));
            addWithComponentFeedback(new TextField(ApplicationUser.FUNCTION), new ResourceModel(USER_FUNCTION));
            addWithComponentFeedback(new TextField(ApplicationUser.TELEPHONE), new ResourceModel(USER_TELEPHONE));
            addWithComponentFeedback(new TextField(UserProperties.DAI), new ResourceModel(USER_DAI));

            // Generate the list of organisation id's (strings) which are also the displayable names
			// and use those in the GUI choice
            List<String> organisationIds = new ArrayList<String>();
            for (DccdOrganisation organisation : organisations)
            {
            	organisationIds.add(organisation.getId());
            }
            // Sort alphabetically, but leave NEW_ORG_ID at the top!
            Collections.sort(organisationIds, new Comparator<String>()
            		{
		    	        public int compare(String s1, String s2)
		    	        {
		    	            return -(s1.compareTo(s2));
		    	        }
            		});
            organisationIds.add(0, NEW_ORG_ID);

            // Make sure there is nothing in it!
            appUser.setOrganization("");
	        DropDownChoice choice = new DropDownChoice("organisationChoice",
            							new PropertyModel(appUser, "organization"),
            							organisationIds)
	        {
				private static final long serialVersionUID = 1L;

				protected boolean wantOnSelectionChangedNotifications()
				{
                    return true;
                }

				//@Override
				//protected void onSelectionChanged(Object newSelection)
				//{
				//	String selectedId = (String)newSelection;
				//
				//	Panel newOrganisationPanel = getOrganisationPanel(selectedId);
				//	organisationPanel.replaceWith(newOrganisationPanel);
				//	organisationPanel = newOrganisationPanel;
				//}
            };
            // Use AJAX to update organisationPanel without page reload
            choice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
				private static final long serialVersionUID = 1L;
				protected void onUpdate(AjaxRequestTarget target) {
                    // update the panel
					ApplicationUser user = (ApplicationUser)getModelObject();
					logger.debug("Ajax update... for organisation: " + user.getOrganization());
					Panel newOrganisationPanel = getOrganisationPanel(user.getOrganization());
					organisationPanel.replaceWith(newOrganisationPanel);
					organisationPanel = newOrganisationPanel;
					organisationPanel.setOutputMarkupId(true);
					target.addComponent(organisationPanel);
                }
            });
            choice.setRequired(true);
            addWithComponentFeedback(choice, new ResourceModel(USER_ORGANIZATION));

            // initially there is no organisation info, only after selecting
            organisationPanel = new OrganisationDisplayPanel("organisationPanel");
            organisationPanel.setOutputMarkupId(true);  // Needed for Ajax to update it
            add(organisationPanel);

            add(registerLink);

            // REFACTOR NOTE:	 could be normal link
            SubmitLink cancelLink = new SubmitLink(WI_CANCEL)
            {
                private static final long serialVersionUID = -1205869652104297953L;

                @Override
                public void onSubmit()
                {
                    //setResponsePage(HomePage.class);
                    // get the previous page, and try to go back
                    Page page = ((DccdSession)Session.get()).getRedirectPage(RegistrationPage.class);
                    if (page != null)
                    {
                    	logger.debug("Back to Page class: " + page.getClass().getSimpleName());
                    	setResponsePage(page);
                    }
                    else
                    {
                    	// Homepage seems a good fallback
                    	setResponsePage(HomePage.class);
                    }
                }
            };
            cancelLink.setDefaultFormProcessing(false);
            add(cancelLink);
        }

        /**
         * construct a panel for the organisation; either a new one or existing one
         *
         * @param organisationId
         * @return The Panel
         */
        Panel getOrganisationPanel(String organisationId)
        {
			if (NEW_ORG_ID.equals(organisationId))
			{
				// start a new Organisation
				logger.debug("selected new Organisation");
				setOrganisationEdit(true);

				// create one if there is none, otherwise reuse existing
				if (null == newOrganisation)
					newOrganisation = new DccdOrganisationImpl();

				OrganisationEditPanel newOrganisationPanel =
	            	new OrganisationEditPanel("organisationPanel", newOrganisation);

				return newOrganisationPanel;
			}
			else
			{
				// use an existing organisation
				setOrganisationEdit(false);

				// we have the string from the organisationIds
				// now do the real selection
				// find the organistion with the given Id
	            for (DccdOrganisation organisation : organisations)
	            {
	            	if (organisationId.equals(organisation.getId()))
	            	{
	            		selectedOrganisation = organisation;
	            		logger.debug("found selected organisation with id: " + organisationId);
	            		break; // found
	            	}
	            }
	            // 'Update' the panel
	            OrganisationDisplayPanel newOrganisationPanel =
	            	new OrganisationDisplayPanel("organisationPanel", selectedOrganisation);

	            return newOrganisationPanel;
			}

        }

        /**
         * Execution after submit of the form.
         */
        @Override
        protected void onSubmit()
        {
            final ApplicationUser appUser = (ApplicationUser) getModelObject();
            UserRegistration userRegistration = new UserRegistration(appUser.getBusinessUser());
            paramUserId = userRegistration.getUserId();
            //paramDateTime = userRegistration.getRequestTimeAsString();
            //paramToken = userRegistration.getMailToken();

            // Note: Dccd does not use a validation Page!

			// Construct the url for the activation of member and/or organisation
			Map<String, String> parameterMap = new HashMap<String, String>();
			parameterMap.put("userId", paramUserId);
			parameterMap.put("inEditMode", "0"); // activation button is placed on non-edit page!
			parameterMap.put("enableModeSwitch", "1");
			final String activationUrl = createPageURL(MemberPage.class, parameterMap);
			userRegistration.setActivationUrl(activationUrl);
			logger.debug("activationUrl: " + activationUrl);

            if (isOrganisationEdit())
            {
            	logger.debug("new Organisation must now be registered");
            	assert(null != newOrganisation);
            	// Also register the new organisation
            	logger.debug("New organistation: " + newOrganisation.getId());
            	OrganisationRegistration organisationRegistration =
            		new OrganisationRegistration(newOrganisation);

            	userRegistration.setOrganisation(newOrganisation);
            	DccdUserService.getService().handleRegistrationRequest(userRegistration, organisationRegistration);

            	if (!organisationRegistration.isCompleted())
            	{
            		// something went wrong!
                	logger.debug("Could not complete organisation registration");
                    for (String stateKey : organisationRegistration.getAccumulatedStateKeys())
                    {
                        //error(getString(stateKey));
                    	// allow for substitution
                        error(getString(stateKey, new Model(organisationRegistration)));
                    }
            	}
            }
            else
            {
            	userRegistration.setOrganisation(selectedOrganisation);
            	userRegistration = DccdUserService.getService().handleRegistrationRequest(userRegistration);
            }

            if (userRegistration.isCompleted())
            {
                disableForm(new String[] {});
                info(getString("missionAccomplished", new Model(appUser)));
                //setResponsePage(new InfoPage(getString("registrationpage.header")));
                // use specific page for the confirmation
                setResponsePage(new RegistrationConfirmPage(appUser));
            }
            else
            {
            	logger.debug("Could not complete user registration");
                for (String stateKey : userRegistration.getAccumulatedStateKeys())
                {
                    error(getString(stateKey));
                }
            }
            logger.debug("End onSubmit: " + userRegistration.toString());
        }

    }

    /**
     * Initialize the same for every constructor.
     */
    private void init()
    {
    	// Make page stateless.
        setStatelessHint(true);

        // Add registration form.
        RegistrationForm registrationForm = new RegistrationForm(REGISTRATION_FORM);
        add(registrationForm);
        // AjaxFormValidatingBehavior.addToAllFormComponents(registrationForm, "onblur");
    }
}
