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

import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.wicket.components.DateTimeLabel;
import nl.knaw.dans.dccd.application.services.DataServiceException;
import nl.knaw.dans.dccd.application.services.DccdDataService;
import nl.knaw.dans.dccd.common.web.behavior.LinkConfirmationBehavior;
import nl.knaw.dans.dccd.model.DccdUser;
import nl.knaw.dans.dccd.model.Project;
import nl.knaw.dans.dccd.web.DccdSession;
import nl.knaw.dans.dccd.web.error.ErrorPage;
import nl.knaw.dans.dccd.web.search.pages.MyProjectsSearchResultPage;

import org.apache.log4j.Logger;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;

/**
 *
 * @author paulboon
 */
public class ProjectViewStatusPanel extends Panel
{
	private static final long serialVersionUID = -6592031952748603260L;
	private static Logger logger = Logger.getLogger(ProjectViewStatusPanel.class);

	public ProjectViewStatusPanel(String id, IModel<Project> model)
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
		// may take some time due to the validation
		IndicatingAjaxLink editLink = new IndicatingAjaxLink("project_edit", projectModel)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target)
			{
				Project project = (Project) getModelObject();
				setResponsePage(new ProjectEditPage(project));
			}
		};
		add(editLink);

		// link to archive this project
		// may take some time due to the validation
		IndicatingAjaxLink archiveLink = new IndicatingAjaxLink("project_archive", projectModel)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target)
			{
				// Set this Page to return to
				((DccdSession)Session.get()).setRedirectPage(ProjectArchivingPage.class, getPage());

				Project project = (Project) getModelObject();
				setResponsePage(new ProjectArchivingPage(project));
			}
		};
		add(archiveLink);

		Link deleteLink = new Link("project_delete", projectModel)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick()
			{
				Project project = (Project) getDefaultModelObject();
				logger.debug("delete project");
		
				try
				{
					DccdDataService.getService().deleteProject(project, (DccdUser) ((DccdSession) getSession()).getUser());
				}
				catch (DataServiceException e)
				{
					logger.error("Failed to delete project", e);
					getSession().error("Failed to delete project"); // use resource?
					throw new RestartResponseException(ErrorPage.class);
				}
							
				// this is where we get from
				//setResponsePage(new MyProjectsPage());
				setResponsePage(new MyProjectsSearchResultPage());
				
				/*
				// return to the project view,
				// ehhh that's where we are!
				// refresh then?
				Page page = getPage();
				if (page instanceof BasePage)
        			((BasePage)page).refresh(); // page should reflect new project status
				// But if it is deleted, we can see it when we have management permission??
				// maybe just go back (and to Home if there is no back)
				//Page backPage = ((DccdSession)Session.get()).getRedirectPage(getPage().getClass());
				 */
			}
		};
		add(deleteLink);
		// show dialog... confirmation alert
		deleteLink.add(new LinkConfirmationBehavior(getString("deleteConfirmationMessage")));

		// NOTE: unarchiving is available until versioning is implemented! 
		//
		// link to (UN)archive this project
		// may take some time due to the validation
		IndicatingAjaxLink unarchiveLink = new IndicatingAjaxLink("project_unarchive", projectModel)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target)
			{
				//
				logger.debug("Unarchiving project");
				
				Project project = (Project) getModelObject();
				
				try
				{
					DccdDataService.getService().unarchiveProject(project, 
							(DccdUser) ((DccdSession) getSession()).getUser());
				}
				catch (DataServiceException e)
				{
					logger.error("Failed to update project", e);
					getSession().error("Failed to update project"); // use resource?
					throw new RestartResponseException(ErrorPage.class);
				}				
				// reload the page with new status?
				//
				setResponsePage(new ProjectViewPage(project));
			}
		};
		add(unarchiveLink);
		Label unarchiveMsgLabel = new Label("unarchive_message", new ResourceModel("unarchive_message"));
		add(unarchiveMsgLabel);
		
		// Hide buttons when not in Draft !
		if(projectModel.getObject().getAdministrativeMetadata().getAdministrativeState() != DatasetState.DRAFT)
		{
			// Not Draft == Archived
			editLink.setVisible(false);
			archiveLink.setVisible(false);
			deleteLink.setVisible(false);
		}
		// DISABLE UNARCHIVING
		else
		{
			// Draft
			unarchiveLink.setVisible(false);
			unarchiveMsgLabel.setVisible(false);
		}
	}


}

