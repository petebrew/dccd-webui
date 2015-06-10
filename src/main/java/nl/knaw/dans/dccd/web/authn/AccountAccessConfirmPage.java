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
import nl.knaw.dans.common.web.template.CommonResources;
import nl.knaw.dans.dccd.authn.ForgottenPasswordMessenger;
import nl.knaw.dans.dccd.web.base.BasePage;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 * @author paulboon
 */
public class AccountAccessConfirmPage extends BasePage implements CommonResources {
//	private static final Logger logger = LoggerFactory.getLogger(AccountAccessConfirmPage.class);

	public AccountAccessConfirmPage(final ForgottenPasswordMessenger messenger)
    {
        super();
		init(messenger);
	}

	private void init(final ForgottenPasswordMessenger messenger) {
		//String message = getString("missionAccomplishedNonSpecific");
		//
		// when using messenger in the Model it does not replace ${email},
		// messenger.getEmail() is only non-null
		// if the input was with an email and not a username
		//logger.debug("constructing message with email: " + messenger.getEmail());
		User user = messenger.getUsers().get(0); // must have  at least one user
		Label label = new Label("missionAccomplishedFeedback",
				new StringResourceModel("missionAccomplished", this, new Model(user)));
		add(label);
	}

//	public AccountAccessConfirmPage(final ApplicationUser appUser)
//    {
//        super();
//		init(appUser);
//	}
//
//	private void init(final ApplicationUser appUser) {
//		//missionAccomplishedFeedback
//		String message = getString("missionAccomplished", new Model(appUser));
//		Label label = new Label("missionAccomplishedFeedback", message);
//		//label.setEscapeModelStrings(FeedbackPanel.this.getEscapeModelStrings());
//		add(label);
//	}
}

