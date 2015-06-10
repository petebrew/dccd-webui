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

import nl.knaw.dans.dccd.application.services.DccdUserService;
import nl.knaw.dans.dccd.application.services.UserServiceException;
import nl.knaw.dans.dccd.model.DccdOrganisation;
import nl.knaw.dans.dccd.model.DccdUser;
import nl.knaw.dans.dccd.model.Project;
import nl.knaw.dans.dccd.model.DccdUser.Role;
import nl.knaw.dans.dccd.web.DccdSession;

import org.apache.log4j.Logger;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * @author dev
 */
public class ProjectManagerOwnerPanel extends Panel
{
	private static Logger logger = Logger.getLogger(ProjectManagerOwnerPanel.class);
	private static final long serialVersionUID = 9108237951668232101L;

	public ProjectManagerOwnerPanel(String id, IModel<Project> model)
	{
		super(id, model);

		init();
	}

	private void init()
	{
		IModel<Project> projectModel = (IModel<Project>) this.getDefaultModel();
		Project project = projectModel.getObject();
		DccdUser user = (DccdUser) ((DccdSession) getSession()).getUser();

		// manager
		DccdUser manager = null;
		String managerId = project.getAdministrativeMetadata().getManagerId();
		String managerName = "";
		try
		{
			manager = DccdUserService.getService().getUserById(managerId);
			managerName = manager.getDisplayName();
		}
		catch (UserServiceException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		add(new Label("managerName", managerName));

		// Change button for manager
		Link managerChangeLink = new Link("managerChange", projectModel)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick()
			{
				// Set this Page to return to
				((DccdSession)Session.get()).setRedirectPage(ChangeProjectManagerPage.class, getPage());

				Project project = (Project) getModelObject();
				setResponsePage(new ChangeProjectManagerPage(project));
			}
		};
		add(managerChangeLink);
		// Manager or Admin can change it, 
		// But should user from same org see it?
		if (!project.isManagementAllowed(user))
			managerChangeLink.setVisible(false);
		
		// legal Owner 
		String legalOwnerId = project.getAdministrativeMetadata().getLegalOwnerOrganisationId();
		String legalOwnerName = "";
		try
		{
			DccdOrganisation organisation = DccdUserService.getService().getOrganisationById(legalOwnerId);
			legalOwnerName = organisation.getId();
		}
		catch (UserServiceException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		Label legalOwnerLabel = new Label("legalOwnerName", legalOwnerName);
		add(legalOwnerLabel);
		
		// Change button for legal owner
		Link ownerChangeLink = new Link("ownerChange", projectModel)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick()
			{
				// Set this Page to return to
				((DccdSession)Session.get()).setRedirectPage(ChangeProjectLegalOwnerPage.class, getPage());

				Project project = (Project) getModelObject();
				setResponsePage(new ChangeProjectLegalOwnerPage(project));
			}
		};
		add(ownerChangeLink);
		
		
		// Only logged in admin can see this
		if (user == null ||  
			!user.hasRole(Role.ADMIN))
		{
			legalOwnerLabel.setVisible(false);
			ownerChangeLink.setVisible(false);
		}
		
	}
}

