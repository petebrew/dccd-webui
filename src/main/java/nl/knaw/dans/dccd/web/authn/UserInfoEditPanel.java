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
/*
 * Note by pboon: was in easy package nl.knaw.dans.easy.web.authn;
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.knaw.dans.common.web.template.AbstractCommonStatelessForm;
import nl.knaw.dans.common.web.template.AbstractCommonStatelessPanel;
import nl.knaw.dans.common.web.template.CommonResources;
import nl.knaw.dans.dccd.application.services.DccdUserService;
import nl.knaw.dans.dccd.application.services.UserServiceException;
import nl.knaw.dans.dccd.common.web.SwitchPanel;
import nl.knaw.dans.dccd.model.DccdOrganisation;
import nl.knaw.dans.dccd.model.DccdUser;
import nl.knaw.dans.dccd.web.DccdSession;
import nl.knaw.dans.dccd.web.HomePage;
import nl.knaw.dans.dccd.web.error.ErrorPage;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserInfoEditPanel extends AbstractCommonStatelessPanel implements CommonResources
{
    private static final long serialVersionUID = 2798115070952029278L;
    private static Logger logger = LoggerFactory.getLogger(UserInfoEditPanel.class);

    private static final String WI_USER_INFO_FORM = "userInfoForm";
    private static final String WI_UPDATE_BUTTON  = "update";
    private static final String WI_CANCEL_BUTTON  = "cancel";

    private final SwitchPanel parent;
    private final boolean enableModeSwitch;

	//private DccdUser userLogedIn = (DccdUser)((DccdSession) getSession()).getUser();
	//private boolean admin = (userLogedIn != null && userLogedIn.hasRole(DccdUser.Role.ADMIN));

    public UserInfoEditPanel(final SwitchPanel parent, final String userId, final boolean enableModeSwitch)
    {
        super(SwitchPanel.SWITCH_PANEL_WI);
        this.parent = parent;
        this.enableModeSwitch = enableModeSwitch;
        init(userId);
    }

    private void init(final String userId)
    {
        DccdUser user = null;
        try
        {
            user = DccdUserService.getService().getUserById(userId);
        }
        catch (UserServiceException e)
        {
            error("User with userId '" + userId + "' not found.");
        }

        if (user == null)
        {
            throw new RestartResponseException(new ErrorPage());
        }
        else
        {
            constructPanel(user);
        }
    }

    private void constructPanel(final DccdUser user)
    {
        UserInfoForm infoForm = new UserInfoForm(WI_USER_INFO_FORM, user);
        add(infoForm);
        //AjaxFormValidatingBehavior.addToAllFormComponents(infoForm, "onblur");
    }

    class UserInfoForm extends AbstractCommonStatelessForm
    {
        private static final long serialVersionUID = 7094054164645818316L;

		/**
		 * DccdUser wrapper for the role selection
		 * when 'user' is selected we remove the admin role,
		 * and when 'admin' is selected we add the admin role
		 */
        private class UserRoleSelection implements Serializable
        {
			private static final long serialVersionUID = -6400961849033593987L;
			private DccdUser user;

        	UserRoleSelection(final DccdUser user)
        	{
        		this.user = user;
        	}

        	public DccdUser.Role getRole()
        	{
        		if (user.hasRole(DccdUser.Role.ADMIN))
        			return DccdUser.Role.ADMIN;
        		else
        			return DccdUser.Role.USER;
        	}

        	public void setRole(DccdUser.Role role)
        	{
        		if (role.equals(DccdUser.Role.ADMIN))
        		{
        			user.addRole(DccdUser.Role.ADMIN);
        		}
        		else
        		{
        			user.removeRole(DccdUser.Role.ADMIN);
        		}
        	}

        	public List<DccdUser.Role> getSelections()
        	{
        		return Arrays.asList(new DccdUser.Role[] { DccdUser.Role.USER, DccdUser.Role.ADMIN});
        	}
        }

        public UserInfoForm(final String wicketId, final DccdUser user)
        {
            super(wicketId, new CompoundPropertyModel(user), false);

            addCommonFeedbackPanel();

            DccdUser sessionUser = (DccdUser)((DccdSession)Session.get()).getUser();
            boolean hasAdminRole = sessionUser.hasRole(DccdUser.Role.ADMIN);

			// role selection via Radio buttons
			UserRoleSelection selection = new UserRoleSelection(user);
			final List<DccdUser.Role> ROLES = selection.getSelections();
			RadioChoice roleChoice =
				new RadioChoice("role",
					new PropertyModel(selection, "role"), ROLES,
					new ChoiceRenderer() {
						private static final long serialVersionUID = 3994124725156529569L;
						public Object getDisplayValue(Object value) {
							// value is the object that the user selected
							DccdUser.Role t = (DccdUser.Role) value;
							// the property name
							String keyString = "user.roles." + t.name();
							logger.debug("key: " + keyString);
							// get the property value
							return getString(keyString);
						}
					}).setSuffix("");
			roleChoice.setVisible(hasAdminRole);
			add(roleChoice);
			// Maybe an Admin cannot remove his/her own admin rights?

			// User Status, not editable
			Label stateLabel = new Label("state",
			        new StringResourceModel("user.states.${state}", this, getModel()));
			add(stateLabel);
			stateLabel.setVisible(hasAdminRole);

            add(new Label(UserProperties.USER_ID));
            //add(new Label(UserProperties.COMMONNAME));
            add(new Label(UserProperties.DISPLAYNAME));

            FormComponent email = new RequiredTextField(UserProperties.EMAIL);
            addWithComponentFeedback(email.add(EmailAddressValidator.getInstance()), new ResourceModel("user.email"));

            addWithComponentFeedback(new TextField(UserProperties.TITLE), new ResourceModel(USER_TITLE));
            addWithComponentFeedback(new RequiredTextField(UserProperties.INITIALS), new ResourceModel(USER_INITIALS));
            addWithComponentFeedback(new TextField(UserProperties.PREFIXES), new ResourceModel(USER_PREFIXES));
            addWithComponentFeedback(new RequiredTextField(UserProperties.SURNAME), new ResourceModel(USER_SURNAME));
            addWithComponentFeedback(new TextField(UserProperties.FUNCTION), new ResourceModel(USER_FUNCTION));
            // Telephone
            // Note: when we use the TelephoneNumberValidator here, we also need to use it
            // on the registration page, where the initial user input is done.
            // even better would be if registration would use the same Form or Panel.
            //FormComponent telephone = new TextField(UserProperties.TELEPHONE);
            //telephone.add(TelephoneNumberValidator.instance());
            addWithComponentFeedback(new TextField(UserProperties.TELEPHONE), new ResourceModel(USER_TELEPHONE));
            addWithComponentFeedback(new TextField(UserProperties.DAI), new ResourceModel(USER_DAI));

            // Organistion
            add(new Label("organization", user.getOrganization()));

			// add selection of organisation, only for admin!
			// Generate the list of organisation id's (strings) which are also the displayable names
			// and use those in the GUI choice
			List<DccdOrganisation> organisations = retrieveOrganisations();
			List<String> organisationIds = new ArrayList<String>();
			for (DccdOrganisation organisation : organisations) {
				organisationIds.add(organisation.getId());
			}
			DropDownChoice organisationChoice = new DropDownChoice("organisationChoice",
					new PropertyModel(user, "organization"),
					organisationIds) {
			private static final long serialVersionUID = 1L;
			};
			organisationChoice.setVisible(hasAdminRole);
			organisationChoice.setRequired(true);
			addWithComponentFeedback(organisationChoice, new ResourceModel(USER_ORGANIZATION));

            SubmitLink updateButton = new SubmitLink(WI_UPDATE_BUTTON);
            add(updateButton);

            // REFACTOR NOTE: could be normal Link
            SubmitLink cancelButton = new SubmitLink(WI_CANCEL_BUTTON)
            {

                private static final long serialVersionUID = -1205869652104297953L;

                @Override
                public void onSubmit()
                {
                    handleCancelButtonClicked();
                }
            };
            cancelButton.setDefaultFormProcessing(false);
            add(cancelButton);
        }

        private List<DccdOrganisation> retrieveOrganisations () {
            try
            {
    			return DccdUserService.getService().getActiveOrganisations();
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

        @Override
        protected void onSubmit()
        {
            // handled by updateButton
        	logger.debug("UserInfoForm.onSubmit called");
        	handleUpdateButtonClicked();
        }

        private void handleUpdateButtonClicked()
        {
            final DccdUser user = (DccdUser) getModelObject();
            try
            {
                //User sessionUser = getSessionUser();
            	DccdUser sessionUser = (DccdUser)((DccdSession)Session.get()).getUser();

                // update the user in persistence layer
            	DccdUserService.getService().update(sessionUser, user);
                logger.debug("UserInfo updated");

                // The user we got back from the modelObject is not the same object as the
                // one we put in the CompoundPropertyModel (see constructor).
                // If the sessionUser is updating her own info we need to synchronize
                // the sessionUser on the updated user.
                if (sessionUser.getId().equals(user.getId()))
                {
                    sessionUser.synchronizeOn(user);
                    logger.debug("Session user updated. Synchronizing " + sessionUser + " on " + user);
                }

                if (enableModeSwitch)
                {
                    parent.switchMode();
                }
            }
            catch (UserServiceException e)
            {
                logger.error("Error while updating userInfo: :", e);
                fatal("Error while updating userInfo!");
            }
        }

        private void handleCancelButtonClicked()
        {
            if (enableModeSwitch)
            {
                parent.switchMode();
            }
            else
            {
                setResponsePage(HomePage.class);
            }
        }
    }
}
