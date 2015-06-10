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

import java.io.Serializable;

import nl.knaw.dans.dccd.model.DccdUser;
import nl.knaw.dans.dccd.model.Project;
import nl.knaw.dans.dccd.model.ProjectPermissionLevel;
import nl.knaw.dans.dccd.model.entities.Entity;
import nl.knaw.dans.dccd.web.DccdSession;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

/**
 * @author paulboon
 */
public class UnauthorizedDendroEntityPanel extends Panel
{
	private static final long serialVersionUID = -2780336250345184327L;

	public UnauthorizedDendroEntityPanel(String id, Project project, Entity entity)
	{
		super(id);

		// message:
		//You are currently authorised to view no more than the '<entity>' level of this project.
		//To view these details, you need permission on at least the '<level of current entity>' level.
		//Please contact the projects' manager <display name> to request permission: <mailto link>.

		DccdUser user = (DccdUser)((DccdSession) getSession()).getUser();
		ProjectPermissionLevel userPermission = project.getPermissionMetadata().getUserPermission(user.getId());
		DccdUser manager = project.getCreationMetadata().getUser();

		// Note should be the owner of the data
		add(new Label("permissionMessage",
				new StringResourceModel("permissionMessage", this,
						new Model(new Wrapper(userPermission, manager, entity))))
					.setEscapeModelStrings(false));
	}

	private class Wrapper implements Serializable
	{
		private static final long serialVersionUID = 4406598211194752798L;
		private ProjectPermissionLevel userPermissionLevel;
		private DccdUser manager;
		private Entity entity;
		Wrapper(ProjectPermissionLevel userPermissionLevel, DccdUser manager, Entity entity)
		{
			this.userPermissionLevel = userPermissionLevel;
			this.entity = entity;
			this.manager = manager;
		}
		public ProjectPermissionLevel getUserPermissionLevel()
		{
			return userPermissionLevel;
		}
		public DccdUser getManager()
		{
			return manager;
		}
		public Entity getEntity()
		{
			return entity;
		}

	}
}

