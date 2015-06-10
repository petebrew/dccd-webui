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

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.dccd.application.services.DccdUserService;
import nl.knaw.dans.dccd.application.services.UserServiceException;
import nl.knaw.dans.dccd.model.DccdOrganisation;
import nl.knaw.dans.dccd.model.DccdUser;
import nl.knaw.dans.dccd.web.DccdSession;
import nl.knaw.dans.dccd.web.base.BasePage;
import nl.knaw.dans.dccd.web.error.ErrorPage;
import nl.knaw.dans.dccd.web.search.DccdSearchResultPanel;
import nl.knaw.dans.dccd.web.search.pages.LocationSearchResultPage;

import org.apache.log4j.Logger;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.link.Link;


/**
 * @author paulboon
 */
public class OrganisationListPage extends BasePage {
	private static Logger logger = Logger.getLogger(OrganisationListPage.class);

	private DccdUser userLoggedIn = (DccdUser)((DccdSession) getSession()).getUser();
	private boolean admin = (userLoggedIn != null && userLoggedIn.hasRole(DccdUser.Role.ADMIN));
	private OrganisationListPanel panel;
	private List<DccdOrganisation> organisations;

	public OrganisationListPage() {
		super();
		init();
	}

	@Override
	public void refresh() {
		userLoggedIn = (DccdUser)((DccdSession) getSession()).getUser();
		admin = (userLoggedIn != null && userLoggedIn.hasRole(DccdUser.Role.ADMIN));
		organisations = retrieveOrganisations();
		OrganisationListPanel newPanel = panel = new OrganisationListPanel("organisationListPanel", organisations);
		addOrReplace(newPanel);
		panel = newPanel;
	}

	private void init()
	{
		organisations = retrieveOrganisations();
		panel = new OrganisationListPanel("organisationListPanel", organisations);
		add(panel);
		
		// Link to page with locations of all active organisations that have a location
		add(new Link("fullMap") {
			private static final long	serialVersionUID	= 1L;
			@Override
			public void onClick()
			{
				// support back navigation
				((DccdSession)Session.get()).setRedirectPage(OrganisationsMapViewPage.class, getPage());
				setResponsePage(new OrganisationsMapViewPage());
			}	
		});
	}

	public boolean isAdmin() {
		return admin;
	}

	private List<DccdOrganisation> retrieveOrganisations() {
		List<DccdOrganisation> organisations = new ArrayList<DccdOrganisation>();

		try {
			if (isAdmin())
			{
				organisations = DccdUserService.getService().getAllOrganisations();
			}
			else
			{
				organisations = DccdUserService.getService().getActiveOrganisations();
			}
		} catch (UserServiceException e) {
			logger.error("Failed to retrieve organisations", e);
			getSession().error("Failed to retrieve organisations"); // use resource?
			// go to the error page!
			throw new RestartResponseException(ErrorPage.class);
		}
		return organisations;
	}
}

