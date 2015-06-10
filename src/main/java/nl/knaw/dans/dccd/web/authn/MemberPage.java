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

import nl.knaw.dans.common.lang.user.User;
import nl.knaw.dans.dccd.application.services.DccdUserService;
import nl.knaw.dans.dccd.application.services.UserServiceException;
import nl.knaw.dans.dccd.common.web.SwitchPanel;
import nl.knaw.dans.dccd.model.DccdUser;
import nl.knaw.dans.dccd.web.DccdSession;
import nl.knaw.dans.dccd.web.HomePage;
import nl.knaw.dans.dccd.web.base.BasePage;
import nl.knaw.dans.dccd.web.error.ErrorPage;

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

/**
 * @author paulboon
 */
public class MemberPage extends BasePage {
    private static Logger logger = LoggerFactory.getLogger(MemberPage.class);

    public static final String PM_USERID = "userId";
    public static final String PM_IN_EDIT_MODE = "inEditMode";
    public static final String PM_ENABLE_MODESWITCH = "enableModeSwitch";
    private static final String WI_USER_INFO_PANEL = "userInfoPanel";
    private static final String WI_PAGE_HEADER  = "page.header";
    // Used when client views her own info.
    private static final String RI_PERSONAL_POSTFIX = "personal.title.postfix";
    // Used when displayed user is not on the session user.
    //private static final String RI_USER_POSTFIX = "user.title.postfix";
    private final String displayedUserId;

    private Page backPage;
    private Label pageHeaderLabel;

    /**
     * No-argument constructor for displaying information on the current user, switch to edit-mode is enabled.
     */
    public MemberPage()
    {
        this(true, true);
    }

    /**
     * Displays information on the current user.
     *
     * @param inEditMode
     *        start in edit-mode (<code>true</code>) or in display-mode (<code>false</code>)
     * @param enableModeSwitch
     *        enable switching between edit-mode and display-mode: <code>true</code> for allowing the switch,
     *        <code>false</code> otherwise
     */
    public MemberPage(boolean inEditMode, boolean enableModeSwitch)
    {
        super();

        // No user given, therefore use the current (logged in) user
        User currentUser = ((DccdSession) getSession()).getUser();

        if (currentUser == null)
        {
            logger.error(this.getClass().getName()
                    + " called without the user being logged in. Redirecting to HomePage.");
            throw new RestartResponseException(HomePage.class);
        }
        else
        {
            displayedUserId = currentUser.getId();
            //isFirstLogin = currentUser.isFirstLogin();
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
    public MemberPage(PageParameters params)
    {
        super();
        //isFirstLogin = false;
        displayedUserId = params.getString(PM_USERID);//params.getKey(PM_USERID);
        boolean inEditMode = params.getBoolean(PM_IN_EDIT_MODE);
        boolean enableModeSwitch = params.getBoolean(PM_ENABLE_MODESWITCH);
        logger.debug("constructing member page with userId: " + displayedUserId);
        init(inEditMode, enableModeSwitch);
    }

    /**
     * Displays information on the user with the given userId.
     *
     * @param userId
     *        the id of the user to show info on
     * @param inEditMode
     *        start in edit-mode (<code>true</code>) or in display-mode (<code>false</code>)
     * @param enableModeSwitch
     *        enable switching between edit-mode and display-mode: <code>true</code> for allowing the switch,
     *        <code>false</code> otherwise
     */
    public MemberPage(String userId, boolean inEditMode, boolean enableModeSwitch)
    {
        super();
        displayedUserId = userId;
        //isFirstLogin = false;
        init(inEditMode, enableModeSwitch);
    }

    public String getPageTitlePostfix()
    {
        String pageTitlePostfix = "";

		// always show user being displayed here
		try {
			User user = DccdUserService.getService().getUserById(this.displayedUserId);
			pageTitlePostfix = getString(RI_PERSONAL_POSTFIX, new Model(user));
		} catch (UserServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    // Get 'personal information' or 'user information' as title postfix, depending on displayed user.
        /*
        final User user = ((DccdSession) getSession()).getUser();
        if (user != null && user.getId().equals(displayedUserId))
        {
            pageTitlePostfix = getLocalizer().getString(RI_PERSONAL_POSTFIX, this);
        }
        else
        {
            pageTitlePostfix = getLocalizer().getString(RI_USER_POSTFIX, this);
        }
        */
        return pageTitlePostfix;
    }

    @Override
	protected void onBeforeRender()
    {
    	// User might have been edited
		Label newPageHeaderLabel = new Label(WI_PAGE_HEADER, getPageTitlePostfix());
		pageHeaderLabel.replaceWith(newPageHeaderLabel);
		pageHeaderLabel = newPageHeaderLabel;

		super.onBeforeRender();
    }

    // same for all constructors.
    private void init(final boolean inEditMode, final boolean enableModeSwitch)
    {
    	if (isRedirectedIfNotLoggedIn())
    	{
    		return;
    	}

        checkAllowed(inEditMode, enableModeSwitch);

        // get the back page, if any
        backPage = ((DccdSession)Session.get()).getRedirectPage(getPage().getClass());

    	// The back button or link
		Link backButton = new Link("backButton", new ResourceModel("backButton"))
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick()
			{
				// go back to the previous page
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
		// Change label if the previous pages was the MemberList
		if (backPage != null && backPage instanceof MemberListPage)
		{
			backButtonLabelString = getString("backButtonWhenToMemberList");
		}
		backButton.add(new Label("backButtonLabel", backButtonLabelString));

		pageHeaderLabel = new Label(WI_PAGE_HEADER, getPageTitlePostfix());
        add(pageHeaderLabel);

        add(new SwitchPanel(WI_USER_INFO_PANEL, inEditMode)
        {
            private static final long serialVersionUID = -5561111015378292565L;

            @Override
            public Panel getDisplayPanel()
            {
                return new UserInfoDisplayPanel(this, displayedUserId, enableModeSwitch);
            }

            @Override
            public Panel getEditPanel()
            {
                return new UserInfoEditPanel(this, displayedUserId, enableModeSwitch);
            }
        });
    }

    private boolean isLoggedIn() {
    	return ((DccdSession)Session.get()).isLoggedIn();
    }

    public String getDisplayedUserId() {
		return displayedUserId;
	}

    private boolean isRedirectedIfNotLoggedIn()
    {
    	boolean isRedirect = false;
        if (!isLoggedIn()) {
        	logger.debug("User not logged in; Redirecting to login page");

        	// redirect to login page and enable the login to return here
        	getPage().redirectToInterceptPage(new LoginPage());
        	isRedirect = true;
        }
        return isRedirect;
    }

    private void checkAllowed(final boolean inEditMode, final boolean enableModeSwitch)
    {
        final DccdUser userLoggedIn = (DccdUser)((DccdSession) getSession()).getUser();
		final boolean isAdmin = (userLoggedIn != null && userLoggedIn.hasRole(DccdUser.Role.ADMIN));
		final boolean isMember = (userLoggedIn != null && userLoggedIn.getId().equals(displayedUserId));

        // Only admin or the (own) member can
        // can edit or switch to edit!
		if ((inEditMode || enableModeSwitch) && !(isAdmin || isMember) )
		{
        	// go to an errorpage, maybe not bad to give an obscure message!
        	logger.error("Unauthorized access, user need to be logged and have the right priviliges or edit his/her own page");
			getSession().error("Failed to render page");
			// go to the error page!
			throw new RestartResponseException(ErrorPage.class);
		}
    }
}
