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

import nl.knaw.dans.common.web.template.CommonResources;
import nl.knaw.dans.dccd.web.base.BasePage;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

/**
 * @author paulboon
 */
public class RegistrationConfirmPage extends BasePage implements CommonResources {

	public RegistrationConfirmPage(final ApplicationUser appUser)
    {
        super();
		init(appUser);
	}

	private void init(final ApplicationUser appUser) {
		//missionAccomplishedFeedback
		String message = getString("missionAccomplished", new Model(appUser));
		Label label = new Label("missionAccomplishedFeedback", message);
		//label.setEscapeModelStrings(FeedbackPanel.this.getEscapeModelStrings());
		add(label);
	}
}

