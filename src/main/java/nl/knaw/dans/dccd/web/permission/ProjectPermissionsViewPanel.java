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
package nl.knaw.dans.dccd.web.permission;

import java.util.ArrayList;

import nl.knaw.dans.dccd.model.Project;
import nl.knaw.dans.dccd.model.ProjectPermissionMetadata;
import nl.knaw.dans.dccd.model.UserPermission;
import nl.knaw.dans.dccd.web.DccdSession;

import org.apache.log4j.Logger;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

/**
 * Shows permissions of project (tridas information) to its maintainer
 *
 * @author paulboon
 */
public class ProjectPermissionsViewPanel extends Panel
{
	private static final long serialVersionUID = 3830373566713303047L;
	private static Logger logger = Logger.getLogger(ProjectPermissionsViewPanel.class);

	private boolean allowEdit = false;

	public ProjectPermissionsViewPanel(String id, IModel<Project> model, final boolean allowEdit)
	{
		super(id, model);
		this.allowEdit = allowEdit;

		init();
	}

	private void init()
	{
		IModel<Project> projectModel = (IModel<Project>) this.getDefaultModel();
		ProjectPermissionMetadata permissionMetadata = projectModel.getObject().getPermissionMetadata();

		// default permission level, not localized!
		// TODO get string from properties
		//String levelStr = permissionMetadata.getDefaultLevel().toString();
		//add(new Label("level", new Model(levelStr)));
        Label levelLabel = new Label("defaultLevel",
        		new StringResourceModel("permissionLevel.${defaultLevel}", this, new Model(permissionMetadata)));
        add(levelLabel);

		// Exceptions listview
		ArrayList<UserPermission> userPermissionslist = permissionMetadata.getUserPermissionsArrayList();

		logger.debug("number of permission exceptions: " + userPermissionslist.size());

		ListView view = new ListView("userPermissions", userPermissionslist)
		{
			private static final long serialVersionUID = -866756513164010573L;

			@Override
		    protected void populateItem(ListItem item)
			{
				// Casting ?
				UserPermission userPermission = (UserPermission)item.getModel().getObject();
				String permissionUserId = userPermission.getUserId();

				// Note: retrieving the complete user information in separate calls might be inefficient;
				// maybe have a special User Service for getting the list of users, given a list of id's

				// TODO permissionUserDisplayname
				item.add(new Label("permissionUserDisplayname", permissionUserId));
				item.add(new Label("permissionUserId", permissionUserId));

				//ProjectPermissionLevel permissionLevel = userPermission.getLevel();
				//item.add(new Label("permissionLevel", permissionLevel.toString()));
		        Label levelLabel = new Label("permissionLevel",
		        		new StringResourceModel("permissionLevel.${level}", this, new Model(userPermission)));
		        item.add(levelLabel);
			}
		};
		add(view);
		// explicitly hide when empty
		if (userPermissionslist.isEmpty())
			view.setVisible(false);

		// Edit permissions
		Link editPermissionsLink = new Link("editPermissionsLink", projectModel)
        {
			private static final long serialVersionUID = -3048238038023590306L;

			@Override
			public void onClick()
			{
				// Set this Page to return to
				((DccdSession)Session.get()).setRedirectPage(ProjectPermissionSettingsPage.class, getPage());

				// now navigate to edit page with given project;
				Project project = (Project) getModelObject();
				setResponsePage(new ProjectPermissionSettingsPage(project));
			}
		};
        add(editPermissionsLink);
        editPermissionsLink.setVisible(allowEdit);
	}
}


