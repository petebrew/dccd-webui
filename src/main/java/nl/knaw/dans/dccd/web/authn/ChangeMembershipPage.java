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

import java.io.Serializable;

import nl.knaw.dans.common.lang.user.User;
import nl.knaw.dans.dccd.application.services.DccdUserService;
import nl.knaw.dans.dccd.application.services.UserServiceException;
import nl.knaw.dans.dccd.model.DccdUser;
import nl.knaw.dans.dccd.web.DccdSession;
import nl.knaw.dans.dccd.web.HomePage;
import nl.knaw.dans.dccd.web.base.BasePage;

import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.protocol.http.RequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO rename, because it allows to change the state and then also contact the member!
 *
 * @author paulboon
 */
public class ChangeMembershipPage extends BasePage
{
	private static final Logger logger = LoggerFactory.getLogger(ChangeMembershipPage.class);
	private DccdUser userLogedIn = (DccdUser)((DccdSession) getSession()).getUser();

	// the account change to contact the member about
	public enum MessageType
	{
		ACTIVATION,
		DELETION
	};

	// model object; ContactInfo
	public class ContactInfo implements Serializable
	{
		private static final long serialVersionUID = 1L;
		private MessageType messageType = MessageType.ACTIVATION;
		private String messageSubject = "";
		private String messageText = "";

		private DccdUser user;

		public ContactInfo(DccdUser user, MessageType messageType)
		{
			this.user = user;
			this.messageType = messageType;
			setToDefault();
		}

		public DccdUser getUser()
		{
			return user;
		}

		public MessageType getMessageType()
		{
			return messageType;
		}

		public void setMessageType(MessageType messageType)
		{
			this.messageType = messageType;
		}

		public String getMessageSubject()
		{
			return messageSubject;
		}

		public void setMessageSubject(String messageSubject)
		{
			this.messageSubject = messageSubject;
		}

		public String getMessageText()
		{
			return messageText;
		}

		public void setMessageText(String messageText)
		{
			this.messageText = messageText;
		}


		// or should we use the MailComposer ?
		public void setToDefault()
		{
			messageText = getHeaderText();
			switch(messageType)
			{
			case ACTIVATION:
				setMessageSubject(getPage().getString("message.subject.activation"));
				StringResourceModel activationMsgModel =
					new StringResourceModel("message.text.activation", getPage(), new Model(this));
				messageText += activationMsgModel.getString();
				break;
			case DELETION:
				setMessageSubject(getPage().getString("message.subject.deletion"));
				StringResourceModel deletionMsgModel =
					new StringResourceModel("message.text.deletion", getPage(), new Model(this));
				messageText += deletionMsgModel.getString();
				break;
			}
			messageText += getFooterText();
		}

		public String getLoginUrl ()
		{
			// hmmm, is loginPage bookmarkable?
			return RequestUtils.toAbsolutePath((String) getPage().urlFor(LoginPage.class, null));//"<login url>";
		}

		private String getHeaderText()
		{
			StringResourceModel headerMsgModel =
				new StringResourceModel("message.text.header", getPage(), new Model(this));

			return headerMsgModel.getString(); //getString("message.text.header");//"<header>\n";
		}

		private String getFooterText()
		{
			StringResourceModel footerMsgModel =
				new StringResourceModel("message.text.footer", getPage(), new Model(this));
			return footerMsgModel.getString(); //getString("message.text.footer");//"\n<footer>";
		}
	}

	private ContactInfo contactInfo;

	public ChangeMembershipPage(DccdUser user, MessageType messageType) {
		super();
		contactInfo = new ContactInfo(user, messageType);
		init();
	}

	public void init()
	{
		add(new Label("contactTitle",
				new StringResourceModel("contact.title", this, new Model(contactInfo))));

		Form contactForm = new Form("contactForm", new CompoundPropertyModel(contactInfo));
		add(contactForm);
		contactForm.add(new Label("messageSubject"));
		contactForm.add(new TextArea("messageText"));

		// REFACTOR NOTE:	 only one submitLink per page 
	    // Send
		SubmitLink sendLink = new SubmitLink("send")
		{
 			private static final long serialVersionUID = -2201994791781159527L;

			public void onSubmit()
			{
                logger.debug("send: onSubmit executed");

                try
                {
					updateUser();
				}
                catch (UserServiceException eUpdate)
                {
					logger.error("User not updated, not sending mail", eUpdate);
					navigateBack();
				}

                // send the message
                try
                {
                	DccdUserService.getService().sendMailToUser(contactInfo.getUser(),
							contactInfo.getMessageSubject(),
							contactInfo.getMessageText());
				}
                catch (UserServiceException e)
                {
					e.printStackTrace();
					// Show error page?
					error("Unable to send mail");
					setResponsePage(this.getPage());
				}

				navigateBack();
            }
        };
        contactForm.add(sendLink);

        SubmitLink dontsendLink = new SubmitLink("dontsend")
        {
			private static final long serialVersionUID = -8709897650406949684L;

			@Override
            public void onSubmit()
            {
                logger.debug("don't send: onSubmit executed");
                try
                {
					updateUser();
				}
                catch (UserServiceException e)
                {
					logger.error("User not updated", e);
					navigateBack();
				}
				navigateBack();
            }
        };
        dontsendLink.setDefaultFormProcessing(false);
        contactForm.add(dontsendLink);

        SubmitLink cancelLink = new SubmitLink("cancel")
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
        contactForm.add(cancelLink);
	}

	private void updateUser() throws UserServiceException
	{
	    // Update user object
	    DccdUser user = contactInfo.getUser();
	    if (contactInfo.messageType == MessageType.DELETION)
	    {
	    	user.setState(User.State.BLOCKED);
	    }
	    else  if (contactInfo.messageType == MessageType.ACTIVATION)
	    {
	    	user.setState(User.State.ACTIVE);
	    }
	    // update the user in persistence layer
	    DccdUserService.getService().update(userLogedIn, user);

	    logger.debug("UserInfo updated");
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
}

