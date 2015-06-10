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

import java.util.HashMap;
import java.util.Map;

import nl.knaw.dans.common.lang.user.User;
import nl.knaw.dans.common.web.template.AbstractCommonStatelessForm;
import nl.knaw.dans.dccd.application.services.DccdUserService;
import nl.knaw.dans.dccd.authn.ForgottenPasswordMessenger;
import nl.knaw.dans.dccd.common.web.validate.RequireOneValidator;
import nl.knaw.dans.dccd.web.DccdSession;
import nl.knaw.dans.dccd.web.HomePage;
import nl.knaw.dans.dccd.web.base.BasePage;
import nl.knaw.dans.dccd.web.error.ErrorPage;

import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author paulboon
 */
public class AccountAccessPage extends BasePage
{
	private static final Logger logger = LoggerFactory.getLogger(AccountAccessPage.class);
    private static final String WI_FORGOTTEN_PASSWORD_FORM = "accountaccessForm";
    private static final String WI_RANDOM_TOKEN = "token";
    private static final String LABEL_USERID = "user.userId";
    private static final String LABEL_EMAIL  = "user.email";
    private static final String WI_REQUEST_BUTTON = "request";
    private static final String WI_CANCEL_BUTTON = "cancel";
    private static final String USERID = "userId";
    private static final String EMAIL = "email";

	public AccountAccessPage()
	{
		super();
		init();
	}

    public class AccountAccessForm extends AbstractCommonStatelessForm
    {
		private static final long serialVersionUID = -3996581038983383700L;
	    private final String randomString;

        public AccountAccessForm(String wicketId, ForgottenPasswordMessenger messenger)
        {
            super(wicketId, new CompoundPropertyModel(messenger), false);
            this.randomString = messenger.getMailToken();
            addCommonFeedbackPanel();

            add(new HiddenField(WI_RANDOM_TOKEN, new Model(messenger.getRandomString())));

            // userId
            FormComponent userId = new TextField(USERID);
            addWithComponentFeedback(userId, new ResourceModel(LABEL_USERID));

            // email
            FormComponent email = new TextField(EMAIL);
            email.add(EmailAddressValidator.getInstance());
            addWithComponentFeedback(email, new ResourceModel(LABEL_EMAIL));

            RequireOneValidator requireOne = new RequireOneValidator(userId, email);
            add(requireOne);

            // REFACTOR NOTE:	 only one submitLink per page 
		    // Send
			SubmitLink sendLink = new SubmitLink(WI_REQUEST_BUTTON)
			{
	 			private static final long serialVersionUID = -2201994791781159527L;

				public void onSubmit()
				{
	                logger.debug("send: onSubmit executed");
                    handleRequestButtonClicked();
	            }
	        };
	        add(sendLink);

	        SubmitLink cancelLink = new SubmitLink(WI_CANCEL_BUTTON)
	        {
				private static final long serialVersionUID = -1337881068950633502L;

				@Override
	            public void onSubmit()
	            {
	                logger.debug("cancel: onSubmit executed");
					navigateBack();
	            }
	        };
	        cancelLink.setDefaultFormProcessing(false);
	        add(cancelLink);
		}

		@Override
		protected void onSubmit()
		{
			// TODO Auto-generated method stub
		}

		private void navigateBack()
		{
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

        private void handleRequestButtonClicked()
        {
            // Check for a valid token
            if (randomString == null)
            {
                logger.warn("ForgottenPasswordForm is submitted without a valid token");
                error("Invalid form.");
                setResponsePage(ErrorPage.class);
                return;
            }

            final ForgottenPasswordMessenger messenger = (ForgottenPasswordMessenger) getModelObject();

            if (!this.randomString.equals(messenger.getMailToken()))
            {
                logger.warn("ForgottenPasswordForm is submitted with an invalid token. Expected: " + this.randomString
                        + ", got: " + messenger.getMailToken());
                error("Invalid form.");
                setResponsePage(ErrorPage.class);
                return;
            }

            // for updating with url, we need extra data
            Map<String, String> paras = new HashMap<String, String>();
            paras.put(ChangePasswordPage.PM_REQUEST_TIME, messenger.getRequestTimeAsString());
            paras.put(ChangePasswordPage.PM_REQUEST_TOKEN, messenger.getMailToken());
            messenger.setUserIdParamKey(ChangePasswordPage.PM_USER_ID);
            final String updateURL = createPageURL(ChangePasswordPage.class, paras);
            messenger.setUpdateURL(updateURL);

            DccdUserService.getService().handleForgottenPasswordRequest(messenger);

            if (messenger.isCompleted())
            {
                for (User user : messenger.getUsers())
                {
                    info(getString(messenger.getStateKey(), new Model(user)));

                }
                this.disableForm(new String[]{});
                //setResponsePage(new InfoPage("Password change procedure - Instructions sent "));

                // TODO go to confirmation page, give all users...?
                // or give first user... or none....just testing now?
                setResponsePage(new AccountAccessConfirmPage(messenger));
            }
            else
            {
                error(getString(messenger.getStateKey()));
            }
        }
    }

	private void init()
	{
	    ForgottenPasswordMessenger messenger = new ForgottenPasswordMessenger();
	       add(new AccountAccessForm(WI_FORGOTTEN_PASSWORD_FORM, messenger));
	}
}
