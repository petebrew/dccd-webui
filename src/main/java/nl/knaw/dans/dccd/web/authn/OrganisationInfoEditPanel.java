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

import nl.knaw.dans.common.web.template.AbstractCommonStatelessForm;
import nl.knaw.dans.common.web.template.AbstractCommonStatelessPanel;
import nl.knaw.dans.common.web.template.CommonResources;
import nl.knaw.dans.dccd.application.services.DccdUserService;
import nl.knaw.dans.dccd.application.services.OrganisationBlockedWithUsersException;
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
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author paulboon
 */
public class OrganisationInfoEditPanel extends AbstractCommonStatelessPanel implements CommonResources
{
    private static Logger logger = LoggerFactory.getLogger(UserInfoEditPanel.class);

    private static final String WI_INFO_FORM = "organisationInfoForm";
    private static final String WI_UPDATE_BUTTON  = "update";
    private static final String WI_CANCEL_BUTTON  = "cancel";

    private static final long serialVersionUID = 2798115070952029278L;

    private final SwitchPanel parent;
    private final boolean enableModeSwitch;

    public OrganisationInfoEditPanel(final SwitchPanel parent, final String organisationId, final boolean enableModeSwitch)
    {
        super(SwitchPanel.SWITCH_PANEL_WI);
        this.parent = parent;
        this.enableModeSwitch = enableModeSwitch;
        init(organisationId);
    }

    private void init(final String organisationId)
    {
    	checkAllowed();

        DccdOrganisation organisation = null;
        try
        {
        	organisation = DccdUserService.getService().getOrganisationById(organisationId);
        }
        catch (UserServiceException e)
        {
            error("Organisation with id '" + organisationId + "' not found.");
        }

        if (organisation == null)
        {
            throw new RestartResponseException(new ErrorPage());
        }
        else
        {
            constructPanel(organisation);
        }
    }

    private void checkAllowed() {
        DccdUser sessionUser = (DccdUser)((DccdSession)Session.get()).getUser();
        boolean hasAdminRole = sessionUser.hasRole(DccdUser.Role.ADMIN);

        if (!hasAdminRole) {
        	// go to an errorpage, maybe not bad to give an obscure message!
        	logger.error("Unauthorized access, user needs Admin role");
			getSession().error("Failed to render page");
			// go to the error page!
			throw new RestartResponseException(ErrorPage.class);
        }
    }

    private void constructPanel(final DccdOrganisation organisation)
    {
        InfoForm infoForm = new InfoForm(WI_INFO_FORM, organisation);
        add(infoForm);
        //AjaxFormValidatingBehavior.addToAllFormComponents(infoForm, "onblur");
    }

    class InfoForm extends AbstractCommonStatelessForm
    {
        private static final long serialVersionUID = 7094054164645818316L;

        public InfoForm(final String wicketId, final DccdOrganisation organisation)
        {
            super(wicketId, new CompoundPropertyModel(organisation), false);

            addCommonFeedbackPanel();

	        add(new Label("id"));

			addWithComponentFeedback(new TextField(UserProperties.ADDRESS), new ResourceModel("organisation.address"));
			addWithComponentFeedback(new TextField(UserProperties.POSTALCODE), new ResourceModel("organisation.postalCode"));
			addWithComponentFeedback(new TextField(UserProperties.CITY), new ResourceModel("organisation.city"));
			addWithComponentFeedback(new TextField(UserProperties.COUNTRY), new ResourceModel("organisation.country"));

			// REFACTOR NOTE:	 only one submitLink per page 
            SubmitLink updateButton = new SubmitLink(WI_UPDATE_BUTTON);
            add(updateButton);

            SubmitLink cancelButton = new SubmitLink(WI_CANCEL_BUTTON)
            {

                private static final long serialVersionUID = 1L;

                @Override
                public void onSubmit()
                {
                    handleCancelButtonClicked();
                }
            };
            cancelButton.setDefaultFormProcessing(false);
            add(cancelButton);
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
        	// update the organisation
	        final DccdUser sessionUser = (DccdUser)((DccdSession)Session.get()).getUser();
	        final DccdOrganisation organisation = (DccdOrganisation) getModelObject();
			try
			{
				DccdUserService.getService().update(sessionUser, organisation);
				// go to non-edit mode
                if (enableModeSwitch)
                {
                    parent.switchMode();
                }
			}
			catch (OrganisationBlockedWithUsersException e)
			{
				logger.error("Organisation can not be deleted because is still has members");
				//OrganisationBlockedWithUsers.message
				StringResourceModel messageModel =
					new StringResourceModel("OrganisationBlockedWithUsers.message", this, new Model(organisation));
				fatal(messageModel.getString());
			}
			catch (UserServiceException e)
			{
				logger.error("Error while updating organisation: ", e);
				fatal("Error while updating organisation!");
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
