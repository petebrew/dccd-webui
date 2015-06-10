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

import nl.knaw.dans.common.wicket.components.DateTimeLabel;
import nl.knaw.dans.dccd.application.services.DataServiceException;
import nl.knaw.dans.dccd.application.services.DccdDataService;
import nl.knaw.dans.dccd.model.Project;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;

/**
 *
 * @author paulboon
 */
public class ProjectEditStatusPanel extends Panel
{
	private static final long serialVersionUID = -6592031952748603260L;
	private static Logger logger = Logger.getLogger(ProjectEditStatusPanel.class);

	public ProjectEditStatusPanel(String id, IModel<Project> model)
	{
		super(id, model);
		init();
	}

	//	Shows status to its maintainer (or admin)
	private void init()
	{
		IModel<Project> projectModel = (IModel<Project>) this.getDefaultModel();

		add(new Label("project_status_value",
				new StringResourceModel(
						"datasetState.${administrativeMetadata.administrativeState}", this,
						projectModel)));

		add(new DateTimeLabel("project_status_date", getString("dateTimeFormat"), new PropertyModel(projectModel, "administrativeMetadata.lastStateChange")));

		// buttons

		// link to editing this project
		Link viewLink = new Link("project_view", projectModel)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick()
			{
				Project project = (Project) getModelObject();
			
				// Force project reload, so we don't get unsaved changes
				try
				{
					project = DccdDataService.getService().getProject(project.getSid());
				}
				catch (DataServiceException e)
				{
					logger.error("Failed to retrieve project", e);
					// ignoring it, view will show edited version!
				}
				
				setResponsePage(new ProjectViewPage(project));
			}
		};
		add(viewLink);
	}
}

