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
import nl.knaw.dans.dccd.application.services.DccdUserService;
import nl.knaw.dans.dccd.application.services.OrganisationBlockedWithUsersException;
import nl.knaw.dans.dccd.application.services.UserServiceException;
import nl.knaw.dans.dccd.common.web.SwitchPanel;
import nl.knaw.dans.dccd.model.DccdOrganisation;
import nl.knaw.dans.dccd.model.DccdUser;
import nl.knaw.dans.dccd.web.DccdSession;
import nl.knaw.dans.dccd.web.error.ErrorPage;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author paulboon
 */
public class OrganisationInfoDisplayPanel extends AbstractCommonStatelessPanel
{
	private static Logger logger = LoggerFactory.getLogger(OrganisationInfoDisplayPanel.class);
	private static final long serialVersionUID = -6731210438732141564L;

    private static final String WR_EDIT_LINK     = "editLink";

    private final SwitchPanel parent;
    private final boolean enableModeSwitch;

	private DccdUser userLogedIn = (DccdUser)((DccdSession) getSession()).getUser();
	private boolean admin = (userLogedIn != null && userLogedIn.hasRole(DccdUser.Role.ADMIN));

	OrganisationStatusChangeForm statusChangeForm;

    public OrganisationInfoDisplayPanel(final SwitchPanel parent, final String organisationId, final boolean enableModeSwitch)
    {
        super(SwitchPanel.SWITCH_PANEL_WI);
        this.parent = parent;
        this.enableModeSwitch = enableModeSwitch;

        init(organisationId);
    }

    private void init(final String organisationId)
    {
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

	@Override
	protected void onBeforeRender()
	{
		String organisationId = ((DccdOrganisation)getDefaultModelObject()).getId();
		// We need to get organistation from server,
		// because the change request might have failed and our model is not the same
		// as in the repository anymore!
        try
        {
        	DccdOrganisation updatedOrganisation = DccdUserService.getService().getOrganisationById(organisationId);
        	setDefaultModel(new CompoundPropertyModel(updatedOrganisation));

        	OrganisationStatusChangeForm newStatusChangeForm = new OrganisationStatusChangeForm("organisationForm", updatedOrganisation);
    		newStatusChangeForm.setVisible(admin);
    		statusChangeForm.replaceWith(newStatusChangeForm);
    		statusChangeForm = newStatusChangeForm;
    		logger.debug("Updated organisation");
        }
        catch (UserServiceException e)
        {
            error("Organisation with id '" + organisationId + "' not found.");
        }

		super.onBeforeRender();
	}

    private void constructPanel(DccdOrganisation organisation)
    {
        super.setDefaultModel(new CompoundPropertyModel(organisation));

        add(new Label("id"));
        add(new Label(UserProperties.ADDRESS));
        add(new Label(UserProperties.POSTALCODE));
        add(new Label(UserProperties.CITY));
        add(new Label(UserProperties.COUNTRY));

		// The actions that change the user's status
		statusChangeForm = new OrganisationStatusChangeForm("organisationForm", organisation);
		statusChangeForm.setVisible(admin);
		add(statusChangeForm);

        // Edit
        Link modeSwitch = new Link(WR_EDIT_LINK)
        {
            private static final long serialVersionUID = -804946462543838511L;

            @Override
            public void onClick()
            {
            	logger.debug("Edit link clicked");
                parent.switchMode();
            }

        };
        modeSwitch.add(new Label(WR_EDIT_LINK, new ResourceModel(WR_EDIT_LINK)));
        modeSwitch.setVisible(admin && enableModeSwitch);
        add(modeSwitch);
    }

    class OrganisationStatusChangeForm extends AbstractCommonStatelessForm
    {
    	private static final long serialVersionUID = 1L;

    	public OrganisationStatusChangeForm(String wicketId, DccdOrganisation organisation)
    	{
    		super(wicketId, new CompoundPropertyModel(organisation), false);

    		addCommonFeedbackPanel();

			OrganisationStatusEditPanel organisationStatusEditPanel =
				new OrganisationStatusEditPanel("statePanel", organisation);
			add(organisationStatusEditPanel);
    	}

    	@Override
    	protected void onSubmit()
    	{
        	// update the organisation
	        final DccdUser sessionUser = (DccdUser)((DccdSession)Session.get()).getUser();
	        final DccdOrganisation organisation = (DccdOrganisation) getModelObject();

			try
			{
				DccdUserService.getService().update(sessionUser, organisation);
			}
			catch (OrganisationBlockedWithUsersException e)
			{
				logger.error("Organisation can not be deleted because is still has members");
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
    }
}


