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

import nl.knaw.dans.dccd.application.services.DccdUserService;
import nl.knaw.dans.dccd.authn.ChangePasswordMessenger;
import nl.knaw.dans.dccd.authn.ForgottenPasswordMailAuthentication;
import nl.knaw.dans.dccd.model.DccdUser;
import nl.knaw.dans.dccd.web.DccdSession;
import nl.knaw.dans.dccd.web.HomePage;
import nl.knaw.dans.dccd.web.error.ErrorPage;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
//import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangePasswordPage extends AbstractAuthenticationPage
{
    private static Logger logger = LoggerFactory.getLogger(ChangePasswordPage.class);

    public static final String PM_REQUEST_TIME = "requestTime";
    public static final String PM_REQUEST_TOKEN = "requestToken";
    public static final String PM_USER_ID = "userId";
    private static final String WI_CHANGE_PASSWORD_PANEL = "changePasswordPanel";


    /**
     * No-argument constructor for displaying the change password page for the current user.
     */
    public ChangePasswordPage()
    {
        super();

        final DccdUser currentUser = (DccdUser)((DccdSession) getSession()).getUser();
        if (currentUser == null)
        {
            logger.error(this.getClass().getSimpleName()
                    + " called without the user being logged in. Redirecting to HomePage.");
            throw new RestartResponseException(HomePage.class);
        }
        else
        {
            ChangePasswordMessenger messenger = new ChangePasswordMessenger(currentUser, false);
            init(messenger);
        }
    }

    /**
     * Constructor with PageParameters, called from a link in a mail, previously send to a user.
     *
     * @see ForgottenPasswordPage
     * @see #PM_USER_ID
     * @see #PM_REQUEST_TIME
     * @see #PM_REQUEST_TOKEN
     * @param params
     *        parameters from url previously send by mail
     */
    public ChangePasswordPage(PageParameters params)
    {
        super(params);
        final String userId = params.getString(PM_USER_ID);
        final String requestTime = params.getString(PM_REQUEST_TIME);
        final String requestToken = params.getString(PM_REQUEST_TOKEN);

        if (StringUtils.isBlank(userId) ||
        	StringUtils.isBlank(requestTime) ||
        	StringUtils.isBlank(requestToken))
        {
            logger.warn(this.getClass().getSimpleName() + " called with invalid parameters.");
            error("Invalid url.");
            throw new RestartResponseException(ErrorPage.class);
        }

        ForgottenPasswordMailAuthentication authentication = DccdUserService.getService().newForgottenPasswordMailAuthentication(userId, requestTime, requestToken);

        if (signIn(authentication))
        {
            ChangePasswordMessenger messenger = new ChangePasswordMessenger(authentication.getUser(), true);
            init(messenger);
        }
        else
        {
            logger.error("Mail user could not be signed in.");
            error("Invalid url: not authenticated.");
            throw new RestartResponseException(ErrorPage.class);
        }
    }

    private void init(ChangePasswordMessenger messenger)
    {
        add(new ChangePasswordPanel(WI_CHANGE_PASSWORD_PANEL, messenger));
    }
}
