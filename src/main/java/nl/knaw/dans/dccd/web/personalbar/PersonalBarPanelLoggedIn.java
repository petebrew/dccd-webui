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

import nl.knaw.dans.common.lang.user.User;
import nl.knaw.dans.dccd.web.DccdSession;
import nl.knaw.dans.dccd.web.authn.LogoffLink;
import nl.knaw.dans.dccd.web.authn.MemberListPage;
import nl.knaw.dans.dccd.web.authn.MemberPage;
import nl.knaw.dans.dccd.web.search.pages.MyProjectsSearchResultPage;
import nl.knaw.dans.dccd.web.upload.UploadFilesPage;
import nl.knaw.dans.dccd.web.upload.UploadIntroPage;

import org.apache.wicket.Session;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersonalBarPanelLoggedIn extends Panel
{
	private static final long serialVersionUID = 5381049337031959123L;
	private static Logger logger = LoggerFactory.getLogger(PersonalBarPanelLoggedIn.class);
	public static final String LOGOFF = "logoff";

	public PersonalBarPanelLoggedIn(String id) {
		super(id);
		// we are logged in, so there must be a user
		User user = ((DccdSession)Session.get()).getUser();
		add(new Label("displayName", new PropertyModel(user, "displayName")));

		add(new MyPersonalInfoLink("myPersonalInfoLink"));
		add(new LogoffLink(LOGOFF));

		//add(new BookmarkablePageLink("myProjectsLink", MyProjectsPage.class));
		add(new BookmarkablePageLink("myProjectsLink", MyProjectsSearchResultPage.class));//
		
		//add(new BookmarkablePageLink("uploadLink", UploadIntroPage.class));
		// No intro, but go directly to the tridas file upload
		add(new BookmarkablePageLink("uploadLink", UploadFilesPage.class));
		
		// Remark: Possibly this should go in a Management Bar
		add(new BookmarkablePageLink("membersLink", MemberListPage.class));
		//add(new BookmarkablePageLink("organisationsLink", OrganisationListPage.class));
	}

	@Override
	protected boolean getStatelessHint()
	{
	    return false;
	}

	private class MyPersonalInfoLink extends Link
	{
	    private static final long serialVersionUID = -3483350187675904481L;

        MyPersonalInfoLink(String id)
        {
            super(id);
        }

        @Override
        public void onClick()
        {
			// if we are already on the MemberPage, don't navigate...
			if (!(getPage() instanceof MemberPage))
			{
				logger.debug("Set Page to return to class: " + getPage().getClass().getSimpleName());
				((DccdSession)Session.get()).setRedirectPage(MemberPage.class, getPage());
			}

			// Open Memberpage; in display mode and allow swichable to edit
        	setResponsePage(new MemberPage(false, true));
        }
	}
}
