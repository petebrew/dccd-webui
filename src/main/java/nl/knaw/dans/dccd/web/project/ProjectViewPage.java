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
import java.util.List;

import nl.knaw.dans.dccd.application.services.DataServiceException;
import nl.knaw.dans.dccd.application.services.DccdDataService;
import nl.knaw.dans.dccd.model.DccdAssociatedFileBinaryUnit;
import nl.knaw.dans.dccd.model.DccdUser;
import nl.knaw.dans.dccd.model.Project;
import nl.knaw.dans.dccd.model.ProjectPermissionLevel;
import nl.knaw.dans.dccd.model.entities.Entity;
import nl.knaw.dans.dccd.model.entities.ObjectEntity;
import nl.knaw.dans.dccd.model.entities.ProjectEntity;
import nl.knaw.dans.dccd.web.DccdSession;
import nl.knaw.dans.dccd.web.authn.LoginPage;
import nl.knaw.dans.dccd.web.base.BasePage;
import nl.knaw.dans.dccd.web.datapanels.OpenAccessDendroEntityPanel;
import nl.knaw.dans.dccd.web.download.DownloadPage;
import nl.knaw.dans.dccd.web.entitytree.EntityTreePanel;
import nl.knaw.dans.dccd.web.entitytree.EntityUITreeNodeModel;
import nl.knaw.dans.dccd.web.entitytree.TreeLevelPanel;
import nl.knaw.dans.dccd.web.error.ErrorPage;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.tree.BaseTree;
import org.apache.wicket.model.Model;

/**
 * Show Project information and it's sub-Entities information as a 'tree' of
 * tables The knowledge bout the tree structure is coded in this Panel!
 *
 * @author paulboon
 */
public class ProjectViewPage extends BasePage
{
	private static Logger logger = Logger.getLogger(ProjectViewPage.class);
	final private static boolean editable = false;
	private Panel selectedDendroEntityPanel = null;
	private TreeLevelPanel treeLevelPanel = null;
	private ProjectManagementPanel managementPanel = null;
	private ProjectManagerOwnerPanel managerOwnerPanel = null;
	private EntityTreePanel entityTreePanel = null;
	private ProjectLanguagePanel projectLanguagePanel = null;
	private AssociatedFilesInfoPanel associatedFilesInfoPanel = null;
	
	private ProjectPermissionLevel permissionLevel = null;
	private Project project;
	private DccdUser user = null;

	// Store ID for the project
	public static final String SID_PARAM_KEY = "sid";
	// streamId, for the entity inside the Project 
	public static final String STREAMID_PARAM_KEY = "streamid";
	
	/**
	 * Makes the project view page bookmarkable
	 * 
	 * But as lona as the application doesn't use it internally, users won't see the url's
	 * Users of the RESTfull API could guess them though. 
	 * 
	 * @param params
	 */
	public ProjectViewPage(PageParameters params) {
		super(params);
		logger.debug("Construct with params");
		
		Project project = null;
		String entityIdStr = ""; // just a project; no specific entity
		
		if(params.containsKey(SID_PARAM_KEY))
		{
			// Note for not being logged in:
			// Instead of the redirecting done in initPage, 
			// we could do something different when the user is not logged in as a member
			// For instance show a public information page just like the search result. 
			
			String sid = params.getString(SID_PARAM_KEY);
		
			// retrieve project 
			try {
				// are we sure we can't have injection attacks via the sid string?
				project = DccdDataService.getService().getProject(sid);
			} catch (DataServiceException e) {
				logger.error("Failed to retrieve project", e);
				error("Failed to retrieve project information");
				throw new RestartResponseException(ErrorPage.class);
			}
			
			if(params.containsKey(STREAMID_PARAM_KEY)) {
				entityIdStr = params.getString(STREAMID_PARAM_KEY);
			}
		}
		
		initPage(project, entityIdStr);
	}

	// Note: optional values are show as empty strings when not available
	//
	public ProjectViewPage(Project project)
	{
		logger.debug("Construct with project");

		// this.setModelObject(project);
		initPage(project, "");
	}

	// the view will start showing the given entity as the selected one
	public ProjectViewPage(Project project, String entityId)
	{
		logger.debug("Construct with project and enity");

		logger.info("View project and select entity: " + entityId);

		// this.setModelObject(project);
		initPage(project, entityId);
	}

	@Override
	public void refresh()
	{
		logger.debug("Refresh...");

		// get project from repo
		try
		{
			project = DccdDataService.getService().getProject(project.getSid());
		}
		catch (DataServiceException e)
		{
			logger.error("Failed to retrieve project", e);
			error("Failed to retrieve project information");
			throw new RestartResponseException(ErrorPage.class);
		}

		// Should rebuild the whole page...
		// create new components and call addOrReplace
		// Note: only the ProjectManagementPanel is upgdated now
		// TODO update all panels
		ProjectManagementPanel newManagementPanel = new ProjectManagementPanel(
				"managementPanel", new Model<Project>(project));
		addOrReplace(newManagementPanel);
		managementPanel = newManagementPanel;
		
		ProjectManagerOwnerPanel newManagerOwnerPanel = new ProjectManagerOwnerPanel(
				"managerOwnerPanel", new Model<Project>(project));
		addOrReplace(newManagerOwnerPanel);
		managerOwnerPanel = newManagerOwnerPanel;
		
		//ProjectLanguagePanel newProjectLanguagePanel = new ProjectLanguagePanel("projectLanguagePanel", new Model<Project>(project));	
		//addOrReplace(newProjectLanguagePanel);
		//projectLanguagePanel = newProjectLanguagePanel;
		
		AssociatedFilesInfoPanel newAssociatedFilesInfoPanel = new AssociatedFilesInfoPanel("associatedFilesInfoPanel", new Model<Project>(project));
		addOrReplace(newAssociatedFilesInfoPanel);
		associatedFilesInfoPanel = newAssociatedFilesInfoPanel;
	}

	
	/**
	 * Add all the Wicket view stuff, mostly panels
	 */
	private void initPage(Project project, String entityId)
	{
		// Check for project not to be null
		if (project == null)
		{
			logger.error("project was null");
			throw new RestartResponseException(ErrorPage.class);
		}
		
		this.project = project;

		redirectIfNotLoggedIn();

		logger.debug("Initializing view for Project: " + project.getSid()
				+ ", language: " + project.getTridasLanguage().getLanguage());

		// determine user and permission level for this project
		user = (DccdUser) ((DccdSession) getSession()).getUser();
		permissionLevel = getEffectivePermissionLevel();

		// make sure we are allowed to view (whole or part) 
		logger.debug("status: " +  project.getAdministrativeMetadata().getAdministrativeState());
		logger.debug("management allowance: " + project.isManagementAllowed(user));
		if (!project.isViewingAllowed(user))
		{
			logger.error("Denied viewing project [" + project.getSid()+ 
					"] for user [" + user.getId() + "]");
			throw new RestartResponseException(ErrorPage.class);	
		}
		
		// make sure we have a entity tree
		if (project.entityTree.getProjectEntity() == null)
		{
			// get project from application layer
			try
			{
				DccdDataService.getService().getProjectEntityTree(project);
			}
			catch (DataServiceException e)
			{
				logger.error("Failed to retrieve project entity", e);
				getSession().error("Failed to retrieve project information"); // use
																				// resource?
				throw new RestartResponseException(ErrorPage.class);
			}
		}

		// Associated Files Info
		associatedFilesInfoPanel = new AssociatedFilesInfoPanel("associatedFilesInfoPanel", new Model<Project>(project));
		add(associatedFilesInfoPanel);
		associatedFilesInfoPanel.setOutputMarkupId(true);
		
		// Language
		projectLanguagePanel = new ProjectLanguagePanel("projectLanguagePanel", new Model<Project>(project));	
		add(projectLanguagePanel);
		projectLanguagePanel.setOutputMarkupId(true);

		// Panel for managing the project
		managementPanel = new ProjectManagementPanel("managementPanel",
				new Model<Project>(project));
		add(managementPanel);
		managementPanel.setOutputMarkupId(true);
		managementPanel.setVisible(isManagementAllowed());

		// link to downloading this project
		// TODO, only show if we have permission!
		Link downloadLink = new Link("project_download", new Model(project))
		{
			private static final long serialVersionUID = -8529867515829697707L;

			@Override
			public void onClick()
			{
				// now navigate to edit page with given project;
				Project selected = (Project) getModelObject();
				setResponsePage(new DownloadPage(selected));
			}
		};
		add(downloadLink);
		downloadLink.setVisible(project.isDownloadAllowed(user));
		
		// showing manager and legal owner (organisation)
		managerOwnerPanel = new ProjectManagerOwnerPanel("managerOwnerPanel", new Model<Project>(project));
		add(managerOwnerPanel);		

		// The tree of entities
		entityTreePanel = createEntityTreePanel();
		add(entityTreePanel);

		// change selected entity/panel
		if (entityId != null && entityId.length() > 0)
		{
			entityTreePanel.selectEntity(entityId);
		}

		// Initial tree level panel
		List<String> pathList = entityTreePanel.getSelectionPathAsStringList();
		treeLevelPanel = new TreeLevelPanel("tree_level_panel", new Model(
				(Serializable) pathList))
		{
			private static final long serialVersionUID = -4997738223830344484L;

			@Override
			public void onSelectionChanged(int levelsUp,
					AjaxRequestTarget target)
			{
				entityTreePanel.MoveSelectionUp(levelsUp, target);
			}
		};
		treeLevelPanel.setOutputMarkupId(true);
		add(treeLevelPanel);

		// Initialize entity_panel, assume root (project) is selected by default
		EntityUITreeNodeModel selectedNodeModel = entityTreePanel
				.getSelectedModel();

		// get panel data if needed
		if (selectedNodeModel.getEntity().getTridasAsObject() == null)
		{
			// retieve it
			try
			{
				DccdDataService.getService().retrieveEntity(project.getSid(),
						selectedNodeModel.getEntity());
			}
			catch (DataServiceException e)
			{
				logger.error("Failed to retrieve project entity", e);
				getSession().error(
						"Failed to retrieve project information on "
								+ selectedNodeModel.getName()); // use resource?
				throw new RestartResponseException(ErrorPage.class);
			}
		}
		selectedDendroEntityPanel = getEntityPanel(selectedNodeModel);
		selectedDendroEntityPanel.setOutputMarkupId(true);
		add(selectedDendroEntityPanel);
	}

	private EntityTreePanel createEntityTreePanel()
	{

		EntityTreePanel panel = new EntityTreePanel("tree_panel", new Model(
				project), permissionLevel, editable)
		{
			private static final long serialVersionUID = -4181814359087743891L;

			@Override
			public void onSelectionChanged(EntityUITreeNodeModel nodeModel,
					BaseTree tree, AjaxRequestTarget target)
			{
				logger.info("tree selection changed");

				// switch panels
				// get panel data if needed
				if (nodeModel.getEntity().getTridasAsObject() == null)
				{
					// retieve it
					Project project = (Project) getDefaultModelObject();
					try
					{
						DccdDataService.getService().retrieveEntity(
								project.getSid(), nodeModel.getEntity());
					}
					catch (DataServiceException e)
					{
						logger.error("Failed to retrieve project entity", e);
						getSession().error(
								"Failed to retrieve project information"); // use
																			// resource?
						throw new RestartResponseException(ErrorPage.class);
					}
				}

				Panel currentPanel = getEntityPanel(nodeModel);
				currentPanel.setOutputMarkupId(true);
				selectedDendroEntityPanel.replaceWith(currentPanel);
				target.addComponent(currentPanel, selectedDendroEntityPanel
						.getMarkupId());
				selectedDendroEntityPanel = currentPanel;
				target.addComponent(currentPanel);

				List<String> pathList = getSelectionPathAsStringList();
				logger.info("path list: " + pathList);

				TreeLevelPanel newTreeLevelPanel = new TreeLevelPanel(
						"tree_level_panel", new Model((Serializable) pathList))
				{
					private static final long serialVersionUID = 5565247849537999426L;

					@Override
					public void onSelectionChanged(int levelsUp,
							AjaxRequestTarget target)
					{
						entityTreePanel.MoveSelectionUp(levelsUp, target);
					}
				};
				newTreeLevelPanel.setOutputMarkupId(true);
				treeLevelPanel.replaceWith(newTreeLevelPanel);
				target.addComponent(newTreeLevelPanel, treeLevelPanel
						.getMarkupId());
				treeLevelPanel = newTreeLevelPanel;
				target.addComponent(newTreeLevelPanel);
			}
		};
		return panel;
	}

	// determine if the entity is permitted
	private boolean hasPermission(Entity entity)
	{
		boolean permission = false;

		if(entity.isPermittedBy(permissionLevel))
			permission = true;

		return permission;
	}

	private boolean isManagementAllowed()
	{
		return project.isManagementAllowed(user);
	}

	private ProjectPermissionLevel getEffectivePermissionLevel()
	{
		return project.getEffectivePermissionLevel(user);
	}

	// get the Panel for the given node
	private Panel getEntityPanel(EntityUITreeNodeModel nodeModel)
	{
		Panel panel = nodeModel.getDendroEntityPanel();

		// Construct special panel for levels without permission
		//
		// Note: maybe we can add these to the tree nodes?
		// and then this code below can be removed
		Entity entity = nodeModel.getEntity();
		if (!hasPermission(entity))
		{
			// check for open access first...
			// For now: just assume Object and project have it
			if (entity instanceof ProjectEntity
					|| entity instanceof ObjectEntity)
			{
				panel = new OpenAccessDendroEntityPanel("entity_panel",
						new Model((Serializable) entity.getTridasAsObject()));
			}
			else
			{
				panel = new UnauthorizedDendroEntityPanel("entity_panel",
						project, entity);
			}
		}
		return panel;
	}
}
