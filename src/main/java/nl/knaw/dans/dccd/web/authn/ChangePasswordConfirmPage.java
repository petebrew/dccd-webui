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

import nl.knaw.dans.dccd.model.DccdUser;
import nl.knaw.dans.dccd.web.base.BasePage;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.model.Model;

/**
 * @author paulboon
 */
public class ChangePasswordConfirmPage extends BasePage
{
	public ChangePasswordConfirmPage(final DccdUser user)
    {
        super();
		init(user);
	}

	private void init(final DccdUser user)
	{

		Form form = new Form("form");
		add(form);

		//missionAccomplishedFeedback
		String message = getString("missionAccomplished", new Model(user));
		Label label = new Label("missionAccomplishedFeedback", message);
		//label.setEscapeModelStrings(FeedbackPanel.this.getEscapeModelStrings());
		form.add(label);

		// Note: only show login button if not logged in yet.
		// Or maybe the password change logs user out automatically?
		//
		// We are logged in and don't show a login button
		/*
		form.add(new SubmitLink("login")
		{
			private static final long serialVersionUID = -423818350428416268L;

			@Override
			public void onSubmit() {
				setResponsePage(LoginPage.class);
			}
		});
		*/
	}
}

