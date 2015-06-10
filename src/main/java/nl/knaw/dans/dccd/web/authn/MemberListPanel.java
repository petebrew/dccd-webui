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
public class MemberListPanel extends Panel
{
	private static final long serialVersionUID = -1056624848726431703L;
	final DccdUser userLogedIn = (DccdUser)((DccdSession) getSession()).getUser();
	private boolean admin = (userLogedIn != null && userLogedIn.hasRole(DccdUser.Role.ADMIN));
	private List<DccdUser> users;

	public MemberListPanel(String id, List<DccdUser> users)
	{
		super(id);
		this.users = users;
		init();
	}

	private void init()
	{
		// table header for the status, we use that to hide/show the column headers
		Label statusNameLabel = new Label("statusName", getString("statusName"));
		statusNameLabel.setVisible(isAdmin());
		add(statusNameLabel);

		// TODO Use JavaScript to make whole row's selectable and not only the link
		ListView listview = new ListView("members", users)
		{
			private static final long serialVersionUID = -125010145075865463L;

			protected void populateItem(ListItem item)
			{
		    	final DccdUser user = (DccdUser) item.getModelObject();
		        //item.add(new Label("surname", user.getSurname()));
		        item.add(new Label("displayname", user.getDisplayName()));
		        item.add(new Label("organisation", user.getOrganization()));

		        // admin should also see : username, role, status
		        item.add(new Label("username", user.getId()));

		        String roleString = getString("user.roles.USER");
		        if (user.hasRole(DccdUser.Role.ADMIN))
		        	roleString = getString("user.roles.ADMIN");
		        item.add(new Label("role", roleString));//user.getRolesString()));

		        Label statusLabel = new Label("status",
		        		new StringResourceModel("user.states.${state}", this, item.getModel()));
		        item.add(statusLabel);
		        statusLabel.setVisible(isAdmin());

		        Link memberLink = new Link("memberLink", item.getModel())
		        {
					private static final long serialVersionUID = 5695381457227084943L;

					@Override
					public void onClick()
					{
						// support back navigation
						((DccdSession)Session.get()).setRedirectPage(MemberPage.class, getPage());
						// now navigate to view page
						setResponsePage(new MemberPage(user.getId(), false, canEdit(user)));
					}
				};
	            item.add(memberLink);
	            memberLink.add(new Label("surname", user.getSurname()));
		    }
		};
		add(listview);
	}

	public boolean isAdmin()
	{
		return admin;
	}

	public boolean canEdit(DccdUser user)
	{
		boolean enableEdit = false;
	    if (userLogedIn != null &&
	    	(userLogedIn.getId().equals(user.getId()) ||
	    	userLogedIn.hasRole(DccdUser.Role.ADMIN)))
	    {
	    	enableEdit =  true;
	    }
	    return enableEdit;
	}
}

