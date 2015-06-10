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
import nl.knaw.dans.dccd.authn.ChangePasswordMessenger;
import nl.knaw.dans.dccd.authn.SecurityUtil;
import nl.knaw.dans.dccd.common.web.validate.PasswordPolicyValidator;
import nl.knaw.dans.dccd.common.web.validate.UnEqualInputValidator;
import nl.knaw.dans.dccd.model.DccdUser;
import nl.knaw.dans.dccd.web.error.ErrorPage;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangePasswordPanel extends AbstractCommonStatelessPanel
{
    private static final String WI_CHANGE_PASSWORD_FORM = "changePasswordForm";
    private static final String WI_RANDOM_TOKEN = "token";
    private static final String WI_USER_ID = "user.id";
    private static final String WI_USER_DISPLAYNAME = "user.displayName";
    private static final String LABEL_OLD_PASSWORD = "label.oldPassword";
    private static final String LABEL_NEW_PASSWORD = "label.newPassword";
    private static final String LABEL_CONFIRM_PASSWORD = "label.confirmPassword";
    private static final String WI_UPDATE_BUTTON = "update";
    private static final String WI_CANCEL_BUTTON = "cancel";
    private static final String OLD_PASSWORD = "oldPassword";
    private static final String NEW_PASSWORD = "newPassword";
    private static final String CONFIRM_PASSWORD = "confirmPassword";

    private static Logger logger = LoggerFactory.getLogger(ChangePasswordPanel.class);

    private static final long serialVersionUID = 6320109414610346669L;

	//private DccdUser userLogedIn = (DccdUser)((DccdSession) getSession()).getUser();
	//private boolean admin = (userLogedIn != null && userLogedIn.hasRole(DccdUser.Role.ADMIN));

    public ChangePasswordPanel(String wicketId, ChangePasswordMessenger messenger)
    {
        super(wicketId);
        add(new ChangePasswordForm(WI_CHANGE_PASSWORD_FORM, messenger));
    }

    private class ChangePasswordForm extends AbstractCommonStatelessForm
    {
        private static final long serialVersionUID = 6204591036947047986L;

        /**
         * RandomString used as token against XSS attacks.
         */
        private final String      randomString;

        public ChangePasswordForm(String wicketId, ChangePasswordMessenger messenger)
        {
            super(wicketId, new CompoundPropertyModel(messenger), false);
            addCommonFeedbackPanel();

            this.randomString = SecurityUtil.getRandomString();
            messenger.setToken(this.randomString);

            add(new HiddenField(WI_RANDOM_TOKEN, new Model(this.randomString)));

            //add(new Label(WI_USER_ID, new StringResourceModel(WI_USER_ID, new Model(messenger))));
            add(new Label(WI_USER_ID, messenger.getUserId()));
            add(new Label(WI_USER_DISPLAYNAME, messenger.getUser().getDisplayName()));

            // old password
            FormComponent oldPassword = new PasswordTextField(OLD_PASSWORD).setRequired(true);
            addWithComponentFeedback(oldPassword, new ResourceModel(LABEL_OLD_PASSWORD));
            oldPassword.setVisible(!messenger.isMailContext());

            // new password
            FormComponent password = new PasswordTextField(NEW_PASSWORD).setRequired(true);
            password.add(PasswordPolicyValidator.getInstance());
            addWithComponentFeedback(password, new ResourceModel(LABEL_NEW_PASSWORD));

            // Confirm new password
            FormComponent confirmPassword = new PasswordTextField(CONFIRM_PASSWORD).setRequired(true);
            addWithComponentFeedback(confirmPassword, new ResourceModel(LABEL_CONFIRM_PASSWORD));

            // Validator for equal passwords
            add(new EqualPasswordInputValidator(password, confirmPassword));

            if (!messenger.isMailContext()) // no oldPassword on mail form variant
            {
                // Validator for unequal passwords. checks if old and new password are by incident identical.
                add(new UnEqualInputValidator(oldPassword, password));
            }

            // REFACTOR NOTE:	 only one submitLink per page 
            
            SubmitLink updateButton = new SubmitLink(WI_UPDATE_BUTTON)
            {
                private static final long serialVersionUID = -7665711352111965254L;

                @Override
                public void onSubmit()
                {
                    handleUpdateButtonClicked();
                }
            };
            add(updateButton);

            SubmitLink cancelButton = new SubmitLink(WI_CANCEL_BUTTON)
            {
                private static final long serialVersionUID = 8826482066530609209L;

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
        }

        private void handleUpdateButtonClicked()
        {
            // Check for a valid token
            if (randomString == null)
            {
                logger.warn("ChangePasswordForm is submitted without a valid token");
                error("Invalid form.");
                setResponsePage(ErrorPage.class);
                return;
            }

            final ChangePasswordMessenger messenger = (ChangePasswordMessenger) getModelObject();
            if (!this.randomString.equals(messenger.getToken()))
            {
                logger.warn("ChangePasswordForm is submitted with an invalid token. Expected: " + this.randomString
                        + ", got: " + messenger.getToken());
                error("Invalid form.");
                setResponsePage(ErrorPage.class);
                return; // NOPMD
            }

            DccdUserService.getService().changePassword(messenger);
            if (ChangePasswordMessenger.State.PasswordChanged.equals(messenger.getState()))
            {
                disableForm(new String[] {});
                // TODO localize
                //info("Your password change procedure has now been completed. You can use your new password to log in to EASY.");
                //setResponsePage(new InfoPage("Password change procedure - Completed"));

                // ? COULD logoff first ?
                //DccdSession session = ((DccdSession)Session.get());
                //session.setLoggedOff();

                //go to confirmationPage
                setResponsePage(new ChangePasswordConfirmPage((DccdUser)messenger.getUser()));
            }
            else
            {
                error(getString("state." + messenger.getState()));
            }
        }

        private void handleCancelButtonClicked()
        {
            //setResponsePage(new UserInfoPage(false, true));
        	// go back to where we came from, the memebrPage for now
        	setResponsePage(new MemberPage(false, true));
        }
    }
}
