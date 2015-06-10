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

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.knaw.dans.common.lang.user.User;
import nl.knaw.dans.dccd.application.services.DccdUserService;
import nl.knaw.dans.dccd.application.services.UserServiceException;
import nl.knaw.dans.dccd.common.web.SwitchPanel;
import nl.knaw.dans.dccd.model.DccdUser;
import nl.knaw.dans.dccd.web.DccdSession;
import nl.knaw.dans.dccd.web.HomePage;
import nl.knaw.dans.dccd.web.base.BasePage;
import nl.knaw.dans.dccd.web.error.ErrorPage;

/**
 * @author paulboon
 */
public class OrganisationPage extends BasePage
{
    private static Logger logger = LoggerFactory.getLogger(OrganisationPage.class);

    public static final String PM_ORGANISATIONID = "organisationId";
    public static final String PM_IN_EDIT_MODE = "inEditMode";
    public static final String PM_ENABLE_MODESWITCH = "enableModeSwitch";
    private static final String WI_INFO_PANEL = "organisationInfoPanel";
    private static final String WI_PAGE_HEADER = "page.header";

    // Used when client views her own info.
    //private static final String RI_PERSONAL_POSTFIX = "personal.title.postfix";
    // Used when displayed user is not on the session user.
    //private static final String RI_USER_POSTFIX = "user.title.postfix";
    private static final String RI_ORGANISATION_POSTFIX = "organisation.title.postfix";

    private final String organisationId;
    private Page backPage;

	/**
     * No-argument constructor for displaying information on the current organisation,
     * edit and switch to edit-mode is disabled.
     */
    public OrganisationPage()
    {
        this(false, false);
    }

    public String getOrganisationId() {
		return organisationId;
	}

    /**
     * Displays information on the current organisation.
     *
     * @param inEditMode
     *        start in edit-mode (<code>true</code>) or in display-mode (<code>false</code>)
     * @param enableModeSwitch
     *        enable switching between edit-mode and display-mode: <code>true</code> for allowing the switch,
     *        <code>false</code> otherwise
     */
    public OrganisationPage(boolean inEditMode, boolean enableModeSwitch)
    {
        super();
        User currentUser = ((DccdSession) getSession()).getUser();

        if (currentUser == null)
        {
            logger.error(this.getClass().getName()
                    + " called without the user being logged in. Redirecting to HomePage.");
            throw new RestartResponseException(HomePage.class);
        }
        else
        {
            organisationId = currentUser.getOrganization();
            init(inEditMode, enableModeSwitch);
        }
    }

    /**
     * Displays information on a user.
     *
     * @param params
     *        parameters to use
     * @see #PM_USERID
     * @see #PM_IN_EDIT_MODE
     * @see #PM_ENABLE_MODESWITCH
     * @see #UserInfoPage(String, boolean, boolean)
     */
    public OrganisationPage(PageParameters params)
    {
        super();
        organisationId = params.getKey(PM_ORGANISATIONID);
        boolean inEditMode = params.getBoolean(PM_IN_EDIT_MODE);
        boolean enableModeSwitch = params.getBoolean(PM_ENABLE_MODESWITCH);
        init(inEditMode, enableModeSwitch);
    }

    /**
     * Displays information on the organisation with the given Id.
     *
     * @param userId
     *        the id of the organisation to show info on
     * @param inEditMode
     *        start in edit-mode (<code>true</code>) or in display-mode (<code>false</code>)
     * @param enableModeSwitch
     *        enable switching between edit-mode and display-mode: <code>true</code> for allowing the switch,
     *        <code>false</code> otherwise
     */
    public OrganisationPage(String organisationId, boolean inEditMode, boolean enableModeSwitch)
    {
        super();
        this.organisationId = organisationId;
        init(inEditMode, enableModeSwitch);
    }

    /**
     * Get 'personal information' or 'user information' as title postfix, depending on displayed user.
     */
    public String getPageTitlePostfix()
    {
        String pageTitlePostfix = getString(RI_ORGANISATION_POSTFIX, new Model(this));
        return pageTitlePostfix;
    }

    // same for all constructors.
    private void init(final boolean inEditMode, final boolean enableModeSwitch)
    {
    	checkAllowed(inEditMode, enableModeSwitch);

        // get the back page, if any
    	backPage = ((DccdSession)Session.get()).getRedirectPage(getPage().getClass());

        add(new Label(WI_PAGE_HEADER, getPageTitlePostfix()));

    	// The back button or link
		Link backButton = new Link("backButton", new ResourceModel("backButton"))
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick()
			{
		        // get the previous page, and try to go back
		        Page page = backPage;//((DccdSession)Session.get()).getRedirectPage(getPage().getClass());
		        if (page != null)
		        {
		        	if (page instanceof BasePage)
	        			((BasePage)page).refresh();

		        	setResponsePage(page);
		        }
		        else
		        {
		        	// just go back to a new instance of HomePage
		        	setResponsePage(HomePage.class);
		        }
			}
		};
		add(backButton);
		String backButtonLabelString = getString("backButton");
		// Change label if the previous pages was the OrganisationList
		if (backPage != null)
		{
			if (backPage instanceof OrganisationListPage)
			{
				backButtonLabelString = getString("backButtonWhenToOrganisationList");
			}
			else if (backPage instanceof MemberPage)
			{
				String userId = ((MemberPage)backPage).getDisplayedUserId();
				try
				{
					User user = DccdUserService.getService().getUserById(userId);
					backButtonLabelString = getString("backButtonWhenToMember", new Model(user));
				}
				catch (UserServiceException e)
				{
					logger.error("Could not retrieve user with id: " + userId, e);
				}
			}
		}
		backButton.add(new Label("backButtonLabel", backButtonLabelString));

        add(new SwitchPanel(WI_INFO_PANEL, inEditMode)
        {
			private static final long serialVersionUID = 4267404548233573503L;

			@Override
            public Panel getDisplayPanel()
            {
                return new OrganisationInfoDisplayPanel(this, organisationId, enableModeSwitch);
            }

            @Override
            public Panel getEditPanel()
            {
                return new OrganisationInfoEditPanel(this, organisationId, enableModeSwitch);
            }
        });
    }

    private void checkAllowed(final boolean inEditMode, final boolean enableModeSwitch)
    {
        final DccdUser userLoggedIn = (DccdUser)((DccdSession) getSession()).getUser();
		final boolean isAdmin = (userLoggedIn != null && userLoggedIn.hasRole(DccdUser.Role.ADMIN));

        // Only admin can edit or switch to edit!
		if ((inEditMode || enableModeSwitch) && !(isAdmin) )
		{
        	// go to an errorpage, maybe not bad to give an obscure message!
        	logger.error("Unauthorized access, user need to be logged and have the right priviliges");
			getSession().error("Failed to render page");
			// go to the error page!
			throw new RestartResponseException(ErrorPage.class);
		}
    }
}
