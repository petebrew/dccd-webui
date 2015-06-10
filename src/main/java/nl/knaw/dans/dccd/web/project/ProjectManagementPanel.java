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
package nl.knaw.dans.dccd.web.project;

import nl.knaw.dans.dccd.model.DccdUser;
import nl.knaw.dans.dccd.model.Project;
import nl.knaw.dans.dccd.web.DccdSession;
import nl.knaw.dans.dccd.web.permission.ProjectPermissionsViewPanel;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * @author paulboon
 */
public class ProjectManagementPanel extends Panel
{
	private static Logger logger = Logger.getLogger(ProjectManagementPanel.class);
	private static final long serialVersionUID = -3076815904707833893L;

	public ProjectManagementPanel(String id, IModel<Project> model)
	{
		super(id, model);

		init();
	}

	private void init()
	{
		IModel<Project> projectModel = (IModel<Project>) this.getDefaultModel();

		// allowDownload, allowEdit, allowPermissionChange
		boolean allowPermissionChange = false;

		// determine permission level for this project and
		// then determine what the user is allowed for
		DccdUser user = (DccdUser)((DccdSession) getSession()).getUser();
		Project project = projectModel.getObject();
		//ProjectPermissionLevel permissionLevel = project.getPermissionMetadata().getUserPermission(user.getId());
		//logger.debug("User: "+ user.getId() + " with permission level: " + permissionLevel);

		allowPermissionChange = project.isManagementAllowed(user);

		// add the panels

		add(new ProjectViewStatusPanel("statusPanel", projectModel));

		add(new ProjectPermissionsViewPanel("permissionsViewPanel", projectModel, allowPermissionChange));
	}
}

