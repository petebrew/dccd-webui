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

import java.util.ArrayList;
import java.util.List;

//import nl.knaw.dans.common.wicket.behavior.FormModificationDetectorBehavior;
import nl.knaw.dans.dccd.common.web.AjaxIndicatingSubmitLink;
import nl.knaw.dans.dccd.common.web.behavior.FormModificationDetectorBehavior;

import nl.knaw.dans.dccd.application.services.DataServiceException;
import nl.knaw.dans.dccd.application.services.DccdDataService;
import nl.knaw.dans.dccd.application.services.DccdProjectValidationService;
import nl.knaw.dans.dccd.application.services.ValidationErrorMessage;
import nl.knaw.dans.dccd.model.DccdUser;
import nl.knaw.dans.dccd.model.Project;
import nl.knaw.dans.dccd.model.ProjectPermissionLevel;
import nl.knaw.dans.dccd.model.entities.Entity;
import nl.knaw.dans.dccd.web.DccdSession;
import nl.knaw.dans.dccd.web.authn.LoginPage;
import nl.knaw.dans.dccd.web.base.BasePage;
import nl.knaw.dans.dccd.web.datapanels.DendroEntityPanel;
import nl.knaw.dans.dccd.web.entitytree.EntityTreePanel;
import nl.knaw.dans.dccd.web.entitytree.EntityUITreeNodeModel;
import nl.knaw.dans.dccd.web.error.ErrorPage;

import org.apache.log4j.Logger;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.AjaxIndicatorAppender;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.tree.BaseTree;
import org.apache.wicket.model.Model;
import org.tridas.schema.TridasProject;

/**
 *
 * @author paulboon
 */
public class ProjectEditPage extends BasePage
{
	private static Logger logger = Logger.getLogger(ProjectViewPage.class);
	final private static boolean editable = true;
	private Panel selectedDendroEntityPanel = null;
	private EntityTreePanel treePanel = null;
	private List<ValidationErrorMessage> messages = new ArrayList<ValidationErrorMessage>();
	private FeedbackPanel commonFeedbackPanel = null;
	
	public ProjectEditPage(final Project project)
	{
		initPage(project, "");
	}

	public ProjectEditPage(final Project project, final String entityId)
	{
		logger.info("Edit project and select entity: " + entityId);
		
		initPage(project, entityId);
	}

	/**
	 * Add all the Wicket view stuff, mostly panels
	 */
	private void initPage(final Project project, final String entityId)
	{
		redirectIfNotLoggedIn();

		// check if we are allowed to edit
		checkIfAllowed(project);

		// Set the content language
		// when you don't want some of the edit inputcontrols to use it, 
		// just don't set it and the default language will be used
		if (project != null)
		{
			String languageCode = project.getTridasLanguage().getLanguage();
			((DccdSession) getSession()).setContentLanguageCode(languageCode);
		}

		validateProject(project);

		Form form = new Form("edit_form")
		{
			private static final long	serialVersionUID	= -4154674813756800576L;

			@Override
			protected void onSubmit()
			{
				logger.debug("Edit form submit...");
				
				// empty, AjaxSubmit is doing all work
			}
		};
		add(form);

		// Notify unsaved changes, allows to cancel or proceed
		form.add(new FormModificationDetectorBehavior() {
			private static final long	serialVersionUID	= -1962259854012457283L;

			@Override
			protected String getDisplayMessage()
			{
				return getString("unsavedchanges_message");
			}
		});		
		
		// add common feedback panel 
		commonFeedbackPanel = new FeedbackPanel("commonFeedbackPanel");
		commonFeedbackPanel.setOutputMarkupId(true);
		commonFeedbackPanel.setEscapeModelStrings(false);
		form.add(commonFeedbackPanel);
	
		// Note: Maybe use a special Panel for displaying the Project Validation messages
		// Probably we won't have problems with disappearing messages after reload etc. 
		// But first test it with a a Label only filled with the entity name on validation...		
		
		// Panel for managing the project
		add(new ProjectEditStatusPanel("statusPanel", new Model(project)));

		// Language
		ProjectLanguagePanel projectLanguagePanel = new ProjectLanguagePanel("projectLanguagePanel", new Model<Project>(project));	
		add(projectLanguagePanel);
		projectLanguagePanel.setOutputMarkupId(true);
		
		// TODO disable save if there is nothing to save, or indicate that something has changed with '*' ?
		
		// The save buttons
		// AjaxLink's instead of normal Link, so we can update the content the Ajaxian way
		// Using our AjaxIndicatingSubmitLink instead of AjaxSubmitLink
		form.add(new AjaxIndicatingSubmitLink("save_top")
		{
			private static final long	serialVersionUID	= 1L;
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> submitForm)
			{
				logger.debug("Save top button on AJAX submit ");
				handleSubmit(target, project);
			}
			
			@Override
			protected void onError(final AjaxRequestTarget target, final Form form)
			{
				target.addComponent(commonFeedbackPanel);
			}
		});
		
		form.add(new AjaxIndicatingSubmitLink("save_bottom")
		{
			private static final long	serialVersionUID	= 1L;
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> submitForm)
			{
				logger.debug("Save bottom button on AJAX submit ");
				handleSubmit(target, project);
			}	
			@Override
			protected void onError(final AjaxRequestTarget target, final Form form)
			{
				target.addComponent(commonFeedbackPanel);
			}
		});
		
		// The tree of entities:
		//
		// Note: always all levels editable
		treePanel = createTreePanel(project);
		treePanel.setOutputMarkupId(true);
		add(treePanel);

		// change selected entity/panel
		if (entityId != null && entityId.length() > 0)
		{
			treePanel.selectEntity(entityId);
		}

		// Initialize entity_panel, assume root (project) is selected by default
		EntityUITreeNodeModel selectedNodeModel = treePanel.getSelectedModel();

		// get panel data if needed
		if (selectedNodeModel.getEntity().getTridasAsObject() == null)
		{
			// retrieve it
			try
			{
				DccdDataService.getService().retrieveEntity(project.getSid(),
						selectedNodeModel.getEntity());
			}
			catch (DataServiceException e)
			{
				logger.error("Failed to retrieve project entity", e);
				// e.printStackTrace();
				getSession().error("Failed to retrieve project information"); // use
																				// resource?
				// go to the error page!
				throw new RestartResponseException(ErrorPage.class);
			}
		}

		selectedDendroEntityPanel = selectedNodeModel.getDendroEntityPanel();
		selectedDendroEntityPanel.setOutputMarkupId(true);
		form.add(selectedDendroEntityPanel);
		
		updateSelectedEntityMessages();
	}

	
	private void checkIfAllowed(Project project)
	{
		DccdUser user = (DccdUser)((DccdSession) getSession()).getUser();
		if (project == null || !project.isManagementAllowed(user))
		{
			logger.error("Not allowed to edit project");
			getSession().error("Not allowed to edit project"); // use resource?
			throw new RestartResponseException(ErrorPage.class);
		}
	}
	
	private void handleSubmit(AjaxRequestTarget target, Project project)
	{
		// could force save
		//getSelectedEntity().setDirty(true);

		// Make sure the entityTree is up-to-date with all the titles
		project.updateEntityTree(); // maybe this should be done in the service for save?

		// Note that the saving only updates the affected data, this is handled in the service
		save(project);

		// Note that it validates the whole project, 
		// while it could be restricted to only the entity being edited/shown
//		validateProject(project);
		validateSelectedEntity();
		
		updateSelectedEntityMessages();

		// why do we need this?
		// the title does change without this update, just from the changed model I guess
		// TODO investigate this
		updateTreePanel(project);

		target.addComponent(commonFeedbackPanel);
		target.addComponent(treePanel);
	}
		
	private void save(Project project)
	{
		logger.debug("Start saving...");

		// update repository
		try
		{
			DccdDataService.getService().updateProject(project);
		}
		catch (DataServiceException e)
		{
			logger.error("Failed to update project", e);
			getSession().error("Failed to update project"); // use resource?
			throw new RestartResponseException(ErrorPage.class);
		}		
	}	
	
	private void validateProject(Project project)
	{
		messages = DccdProjectValidationService.getService().validate(project);
	}

	private void validateSelectedEntity()
	{
		String languageCode = ((DccdSession) getSession()).getContentLanguageCode();
		
		Entity selectedEntity = getSelectedEntity();
		List<ValidationErrorMessage> newSelectedEntityMessages = DccdProjectValidationService.getService().validate(selectedEntity, languageCode);
		
		List<ValidationErrorMessage> oldSelectedEntityMessages = getSelectedEntityMessages();

		// replace 'old' with 'new' messages	
		messages.removeAll(oldSelectedEntityMessages);
		messages.addAll(newSelectedEntityMessages);	
	}
		
	private void updateSelectedEntityMessages()
	{
		displayValidationErrorMesages(getSelectedEntityMessages());		
	}
	
	private List<ValidationErrorMessage> getSelectedEntityMessages()
	{
		// construct the list with only the messages for the selected entity
		List<ValidationErrorMessage> selectedEntityMessages = new ArrayList<ValidationErrorMessage>();		

		// Only The messages for the entity shown
		Entity selectedEntity = getSelectedEntity();
		String selectedEntityId = "";
		if (selectedEntity != null)
		{
			selectedEntityId = selectedEntity.getId();
		}
		else
		{
			// Note: maybe use the Project level
			logger.debug("NO selected entity");			
		}
		
		for(ValidationErrorMessage msg : messages)	
		{
			if (msg.getEntityId().contentEquals(selectedEntityId))
			{
				selectedEntityMessages.add(msg);
			}
		}
		
		return selectedEntityMessages;
	}
	
	// show validation errors, so users can edit and fix them
	private void displayValidationErrorMesages(List<ValidationErrorMessage> messages)
	{
		// show given messages
		for(ValidationErrorMessage msg : messages)
		{
			logger.debug("Validation error: " + msg.getMessage());
						
			String fieldName = msg.getFieldNameInUIStyle();
			
			// Note error messages are displayed in the feedbackpanel
			// We don't show the entity name, 
			// because it should be the one we are displaying on the page
			error (msg.getMessage() + 
					" <b>" + fieldName + "</b>");
		}
	}
	
	private Entity getSelectedEntity()
	{
		Entity selectedEntity = null;

		if (treePanel != null && treePanel.getSelectedModel() != null)
		{
			EntityUITreeNodeModel selectedNodeModel = treePanel.getSelectedModel();
			selectedEntity = selectedNodeModel.getEntity();
		}
		
		return selectedEntity;
	}
	
	// Not sure why we need this, 
	// but validation indication doesn't update when we don't call this
	// TODO investigate this
	private void updateTreePanel(Project project)
	{
		// Rebuild the tree 	
		EntityUITreeNodeModel selectedNodeModel = treePanel.getSelectedModel();
		String selectedEntityId  = selectedNodeModel.getEntity().getId();
		logger.debug("Selected in tree the entity: " + selectedEntityId);
		
		EntityTreePanel newTreePanel = createTreePanel(project);
		newTreePanel.setOutputMarkupId(true);
		treePanel.replaceWith(newTreePanel);
		treePanel = newTreePanel;
		treePanel.selectEntity(selectedEntityId);
	}
	
	private EntityTreePanel createTreePanel(final Project project)
	{
		EntityTreePanel treePanel = new EntityTreePanel("tree_panel",
				new Model(project), ProjectPermissionLevel.VALUES, editable)
		{
			private static final long serialVersionUID = 260065135518566021L;

			@Override
			public void onSelectionChanged(EntityUITreeNodeModel nodeModel,
					BaseTree tree, AjaxRequestTarget target)
			{
				logger.info("Tree selection changed to: "+ nodeModel.getEntity().getTitle());

				// Always reload entity from repository so unsaved changes are discarded
				retrieveSelectedEntity(project, nodeModel.getEntity());
				
				// switch panels
				// entity Panel must show changed entity
				// Note calling create and not get !
				DendroEntityPanel newSelectedDendroEntityPanel = nodeModel.createDendroEntityPanel();
				newSelectedDendroEntityPanel.setOutputMarkupId(true);
				selectedDendroEntityPanel.replaceWith(newSelectedDendroEntityPanel);
				selectedDendroEntityPanel = newSelectedDendroEntityPanel;
				target.addComponent(selectedDendroEntityPanel);

				// feedback must show messages for changed entity
				FeedbackPanel newCommonFeedbackPanel = new FeedbackPanel("commonFeedbackPanel");
				newCommonFeedbackPanel.setOutputMarkupId(true);
				newCommonFeedbackPanel.setEscapeModelStrings(false);
				commonFeedbackPanel.replaceWith(newCommonFeedbackPanel);
				commonFeedbackPanel = newCommonFeedbackPanel;
				target.addComponent(commonFeedbackPanel);			
				updateSelectedEntityMessages();			

				// the tree itself needs updating
				target.addComponent(this);

				// Note: we should make sure any changes on the previous form are being saved
				// or a notification has been given
				// this is done in the EntityTreePanel's LinkTree
				//
				// We have new form content, so reset/recalculate the modification detection
				target.appendJavascript(FormModificationDetectorBehavior.FORM_MODIFICATIONS_SAVED_JS);
			}
		};
		
		return treePanel;
	}
	
	private void retrieveSelectedEntity(Project project, Entity entity) 
	{
		try
		{
			DccdDataService.getService().retrieveEntity(
					project.getSid(), entity);
			// Note: only the Tridas object (reference) is replaced
		}
		catch (DataServiceException e)
		{
			logger.error("Failed to retrieve project entity", e);
			// e.printStackTrace();
			getSession().error(
					"Failed to retrieve project information"); // use
																// resource?
			// go to the error page!
			throw new RestartResponseException(ErrorPage.class);
		}
	}	
}
