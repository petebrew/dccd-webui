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
import nl.knaw.dans.dccd.authn.Authentication;
import nl.knaw.dans.dccd.authn.UsernamePasswordAuthentication;
import nl.knaw.dans.dccd.common.web.behavior.FocusOnLoadBehavior;
import nl.knaw.dans.dccd.web.DccdSession;
import nl.knaw.dans.dccd.web.HomePage;

import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LoginForm extends AbstractCommonStatelessForm
{

    /**
     * Constant for wicket id.
     */
    public static final String  WI_LOGIN         = "login";

    public static final String  INVALID_LOGIN    = "error.invalidLogin";

    /**
     * Logger.
     */
    private static final Logger logger           = LoggerFactory.getLogger(LoginForm.class);

    /**
     * Serial version UID.
     */
    private static final long   serialVersionUID = -3701737275804449456L;

    /**
     * Constructor with wicketId and UsernamePasswordAuthentication.
     *
     * @param wicketId
     *        id of this component
     * @param authentication
     *        messenger object for authentication
     */
    public LoginForm(final String wicketId, final UsernamePasswordAuthentication authentication)
    {
        //super(wicketId, new CompoundPropertyModel(authentication), false);
        super(wicketId, new CompoundPropertyModel(authentication));

		// When you have  markup with wicket:id="commonFeedbackPanel"
		//addCommonFeedbackPanel();

		add(new HiddenField("token"));//Messenger.PROP_TOKEN));

        RequiredTextField useridTextField = new RequiredTextField("userId");//Authentication.PROP_USER_ID);
        addWithComponentFeedback(useridTextField, new ResourceModel("user.userId"));//USER_USER_ID));
        useridTextField.add( new FocusOnLoadBehavior());

        PasswordTextField passwordTextField = new PasswordTextField("credentials");//Authentication.PROP_CREDENTIALS);
        passwordTextField.setRequired(true);
        addWithComponentFeedback(passwordTextField, new ResourceModel("user.password"));//USER_PASSWORD));
        add(new SubmitLink("login"));

        // REFACTOR NOTE:	 could be a normal link
        SubmitLink cancelLink = new SubmitLink("cancel") {
            private static final long serialVersionUID = -1L;

            @Override
            public void onSubmit()
            {
                //setResponsePage(HomePage.class);
                // get the previous page, and try to go back
                Page page = ((DccdSession)Session.get()).getRedirectPage(getPage().getClass());
                if (page != null)
                {
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

    @Override
    protected void onSubmit()
    {
        handleSubmit();
    }

    private void handleSubmit()
    {
        final UsernamePasswordAuthentication authentication = (UsernamePasswordAuthentication) getModelObject();
        logger.info("Login of user: " + authentication.getUserId());

        if (signIn(authentication))
        {
        	logger.info("Logged in; user is authenticated");

        	info(getString("user.welcome", new Model(authentication.getUser())));

        	// handle page redirection
            if (!getPage().continueToOriginalDestination())
            {
                setResponsePage(this.getApplication().getHomePage());
            	// NOTE: the PersonalBar is not updates if we go back to the previous page
            	// Maybe call a refresh() helps?
                /*
                // get the previous page, and try to go back
                Page page = ((DccdSession)Session.get()).getRedirectPage(getPage().getClass());
                if (page != null)
                {
                	//if (page instanceof BasePage) ((BasePage)page).refresh();
                	setResponsePage(page);
                }
                else
                {
                	// Homepage seems a good fallback
                	setResponsePage(HomePage.class);
                }
				*/
            }
        }
        else
        {
            logger.debug("Failed authenication for: " + authentication);
            for (String stateKey : authentication.getAccumulatedStateKeys())
            {
                error(getString(stateKey));
            }
            setResponsePage(this.getPage());
        }
    }

    /**
     * Sign the user in for the application.
     *
     * @param authentication
     *        Authentication messenger object
     * @return True if signIn is successful
     */
    boolean signIn(final Authentication authentication)
    {
        boolean signedIn = false;
        //AbstractAuthenticationPage authPage = (AbstractAuthenticationPage) getPage();
        LoginPage authPage = (LoginPage) getPage(); // this form is always on a LoginPage!
        signedIn = authPage.signIn(authentication);
        return signedIn;
    }
}
