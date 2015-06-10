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
package nl.knaw.dans.dccd.web.personalbar;

import nl.knaw.dans.dccd.web.DccdSession;
import nl.knaw.dans.dccd.web.authn.LoginPage;
import nl.knaw.dans.dccd.web.authn.RegistrationPage;

import org.apache.wicket.Session;
import org.apache.wicket.markup.html.link.PageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersonalBarPanelDefault extends Panel {
    private static final Logger logger = LoggerFactory.getLogger(PersonalBarPanelDefault.class);

	private static final long serialVersionUID = 8669663729878249736L;

	public static final String LOGIN = "login";

	public static final String REGISTER= "register";

	public PersonalBarPanelDefault(String id) {
		super(id);

		add(new PageLink(LOGIN, LoginPage.class){
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				// if we are already on the LoginPage, don't navigate...
				if (!(getPage() instanceof LoginPage))
				{
					logger.debug("Set Page to return to class: " + getPage().getClass().getSimpleName());
					((DccdSession)Session.get()).setRedirectPage(LoginPage.class, getPage());
				}
				super.onClick();
			}
		});

		add(new PageLink(REGISTER, RegistrationPage.class) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {

				// if we are already on the RegistrationPage, don't navigate...
				if (!(getPage() instanceof RegistrationPage))
				{
					logger.debug("Set Page to return to class: " + getPage().getClass().getSimpleName());
					((DccdSession)Session.get()).setRedirectPage(RegistrationPage.class, getPage());
				}
				super.onClick();
			}
		});
	}

	@Override
	protected boolean getStatelessHint()
	{
	    return false;
	}
}
