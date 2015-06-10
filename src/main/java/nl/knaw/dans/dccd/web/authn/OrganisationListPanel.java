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

import java.util.List;

import nl.knaw.dans.dccd.model.DccdOrganisation;
import nl.knaw.dans.dccd.model.DccdUser;
import nl.knaw.dans.dccd.web.DccdSession;

import org.apache.wicket.Session;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.StringResourceModel;

/**
 * @author paulboon
 */
public class OrganisationListPanel extends Panel
{
	private static final long serialVersionUID = -7774888997801564924L;

	final DccdUser userLogedIn = (DccdUser)((DccdSession) getSession()).getUser();
	private boolean admin = (userLogedIn != null && userLogedIn.hasRole(DccdUser.Role.ADMIN));
	private List<DccdOrganisation> organisations;

	public OrganisationListPanel(String id, List<DccdOrganisation> organisations)
	{
		super(id);
		this.organisations = organisations;
		init();
	}

	private void init()
	{
		// table header for the status, we use that to hide/show the column headers
		Label statusNameLabel = new Label("statusName", getString("statusName"));
		statusNameLabel.setVisible(isAdmin());
		add(statusNameLabel);

		ListView listview = new ListView("organisations", organisations)
		{
			private static final long serialVersionUID = -2586057114892746638L;

			protected void populateItem(ListItem item)
			{
		    	final DccdOrganisation organisation = (DccdOrganisation) item.getModelObject();

		        //item.add(new Label("organisation", organisation.getId()));
		        item.add(new Label("city", organisation.getCity()));
		        item.add(new Label("country", organisation.getCountry()));

		        // admin should also see: status
		        //Label statusLabel = new Label("status", organisation.getState().name());
		        Label statusLabel = new Label("status",
		        		new StringResourceModel("organisation.states.${state}", this, item.getModel()));
		        item.add(statusLabel);
		        statusLabel.setVisible(isAdmin());

		        Link organisationLink = new Link("organisationLink", item.getModel())
                {
					private static final long serialVersionUID = -1667473636220984570L;

					@Override
					public void onClick()
					{
						// support back navigation
						((DccdSession)Session.get()).setRedirectPage(OrganisationPage.class, getPage());
						// now navigate to view page
						setResponsePage(new OrganisationPage(organisation.getId(), false, isAdmin()));
					}
				};
                item.add(organisationLink);
                organisationLink.add(new Label("organisation", organisation.getId()));
		    }
		};
		add(listview);
	}

	public boolean isAdmin()
	{
		return admin;
	}
}

