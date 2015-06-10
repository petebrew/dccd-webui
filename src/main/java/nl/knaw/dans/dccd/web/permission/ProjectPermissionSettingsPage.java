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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.knaw.dans.common.web.template.AbstractCommonStatelessForm;
import nl.knaw.dans.dccd.application.services.DataServiceException;
import nl.knaw.dans.dccd.application.services.DccdDataService;
import nl.knaw.dans.dccd.application.services.DccdUserService;
import nl.knaw.dans.dccd.application.services.UserServiceException;
import nl.knaw.dans.dccd.model.DccdUser;
import nl.knaw.dans.dccd.model.Project;
import nl.knaw.dans.dccd.model.ProjectPermissionLevel;
import nl.knaw.dans.dccd.model.ProjectPermissionMetadata;
import nl.knaw.dans.dccd.model.UserPermission;
import nl.knaw.dans.dccd.web.DccdSession;
import nl.knaw.dans.dccd.web.HomePage;
import nl.knaw.dans.dccd.web.base.BasePage;
import nl.knaw.dans.dccd.web.error.ErrorPage;
import nl.knaw.dans.dccd.web.user.SelectUserFromTablePanel;
import nl.knaw.dans.dccd.web.user.SelectUserPanel;

import org.apache.log4j.Logger;
import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;

/**
 * Wishlist
 * - Allready added users should be removed from the Autocompletion list
 *    because adding them is not needed
 * - Added user should become the new (highlighted) selection,
 *   so you can change the permission
 * - List should be sorted alphabetically on Surname
 * - Save button only enabled if there is some change
 * - When something has changed you get a warning dialog when browsing away (without save)
 *
 * @author paulboon
 */
public class ProjectPermissionSettingsPage extends BasePage
{
	private static Logger logger = Logger.getLogger(ProjectPermissionSettingsPage.class);

	public ProjectPermissionSettingsPage(Project project)
	{
		super();

		init(project);
	}

	private void init(Project project)
	{
		// TODO check if user logged in and allowed to edit project permissions

		Label titleLabel = new Label("page.title",
				new StringResourceModel("page.title", this, new Model(project)));
		add(titleLabel);

		PermissionSettingsForm permissionSettingsForm = new PermissionSettingsForm("permissionSettingsForm", new Model<Project>(project));
        add(permissionSettingsForm);
	}

	class PermissionSettingsForm extends AbstractCommonStatelessForm
	{
		private static final long serialVersionUID = -2342569412384945589L;
		public PermissionSettings settings;

		private AjaxSubmitLink memberAdd = null;
		private AjaxSubmitLink levelApply = null;
		private AjaxSubmitLink memberRemove = null;
		private ListMultipleChoice userPermissionsChoice = null;
			
		public PermissionSettingsForm(String wicketId, IModel<Project> model)
		{
			super(wicketId, model);

			Project project = model.getObject();
			settings = new PermissionSettings(project);

			ProjectPermissionMetadata permissionMetadata = project.getPermissionMetadata();
			ArrayList<ProjectPermissionLevel> defaultLevelList = new ArrayList<ProjectPermissionLevel>(Arrays.asList(ProjectPermissionLevel.values()));
			// Default level
			DropDownChoice levelsDefaultSelection = new DropDownChoice("levelsDefault",
					new PropertyModel(permissionMetadata, "defaultLevel"),
					defaultLevelList,
					new ChoiceRenderer()
					{
						private static final long serialVersionUID = 1885641435311756482L;

						public Object getDisplayValue(Object value)
						{
							// value is the object that the user selected
							ProjectPermissionLevel projectPermissionLevel = (ProjectPermissionLevel) value;
							return getString("permissionLevel." + projectPermissionLevel);
						}
					}
			);
			add(levelsDefaultSelection);
			levelsDefaultSelection.setOutputMarkupId(true);

			// List shows permissions set for the Project
			// Note that the userId is unique in this list
			// AJAX enabled, because after remove, add or level change, the list should be updated
			//
			// Get some inspiration from
			// http://blog.xebia.com/2008/03/25/wicket-and-list-choice-transfers/
			//final ListMultipleChoice userPermissionsChoice =
			userPermissionsChoice =
				new ListMultipleChoice("userPermissionsSelection",
						new PropertyModel(settings, "selectedUserPermissions"),
						new PropertyModel(settings, "userPermissions"),//userPermissions,
						new ChoiceRenderer()
						{
							private static final long serialVersionUID = 1885641435311756482L;
							public Object getDisplayValue(Object value)
							{
								// value is the object that the user selected
								UserPermission userPermission = (UserPermission) value;
								String userId = userPermission.getUserId();

								//StringResourceModel msgModel =
								//	new StringResourceModel("userPermission", getPage(), new Model(userPermission));
								// No, seems to complicated, lets build the string here...
								String displayName = "";

								// Note: when there are a lot of users in the list we will have a lot of requests
								try
								{
									DccdUser user = DccdUserService.getService().getUserById(userId);
									displayName = user.getDisplayName();
								}
								catch (UserServiceException e)
								{
									logger.error("Could not get user: " + userId);
									throw new RestartResponseException(ErrorPage.class);
								}

								String levelStr = getString("permissionLevel."+userPermission.getLevel());
								return displayName + " (" + userId + "): " + levelStr;
							}
					}

				);
			userPermissionsChoice.setOutputMarkupId(true);
		    add(userPermissionsChoice);
		    userPermissionsChoice.add(new AjaxFormComponentUpdatingBehavior("onchange")
	        {
				private static final long serialVersionUID = 1L;
				protected void onUpdate(final AjaxRequestTarget target)
	        	{
	        		logger.debug("UserPermissions Choice onchange");
	        		ajaxUpdateApplyAndRemoveButtons(target);
	        	}
	        });

		    // Member
		    // Note: No model
			final SelectUserPanel memberSelectPanel = new SelectUserPanel("memberSelectPanel") {
				private static final long serialVersionUID = 6355105513367220675L;
				@Override
				protected void onSelectionChanged(final AjaxRequestTarget target) {
					logger.debug("--- selection changed");
					// Not correctly working now
					if (isSelectionValid())
					{
						logger.debug("enable Add");
						setEnabledOnAjaxSubmitLink (memberAdd, true);
					}
					else
					{
						logger.debug("disable Add");
						setEnabledOnAjaxSubmitLink (memberAdd, false);
					}
					target.addComponent(memberAdd);
				}
			};
			memberSelectPanel.setOutputMarkupId(true);
			add(memberSelectPanel);

			// Using the Wicket Modal window with a panel to select and add
			// from a complete list of users
			final ModalWindow modalSelectDialog;
			add(modalSelectDialog = new ModalWindow("userTablePanel"));
			SelectUserFromTablePanel selectUserFromTablePanel = 
				new SelectUserFromTablePanel(modalSelectDialog.getContentId(), new Model(null)) 
			{
				private static final long	serialVersionUID	= 555736490530439492L;
			
				@Override
				protected void onSelectionChanged(AjaxRequestTarget target)
				{
					DccdUser tableUser = (DccdUser)this.getDefaultModelObject();
					logger.debug("user: " + tableUser.toString());
					addUser(target, tableUser.getId());
				}
			};
			modalSelectDialog.setContent(selectUserFromTablePanel);
			modalSelectDialog.setTitle(ProjectPermissionSettingsPage.this.getString("userAddDialogTitle"));
			modalSelectDialog.setCookieName("selectUserTableWindow");
			modalSelectDialog.setInitialWidth(400);
			modalSelectDialog.setInitialHeight(160);
			modalSelectDialog.setCloseButtonCallback(new ModalWindow.CloseButtonCallback()
			{
				private static final long	serialVersionUID	= 2111416153909973217L;

				public boolean onCloseButtonClicked(AjaxRequestTarget target)
			    {
			        return true;
			    }
			});
			/*
			modalSelectDialog.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
			{
			    public void onClose(AjaxRequestTarget target)
			    {
			        // could do something here!
			    }
			});
			*/
			// button to show the dialog
			AjaxLink selectButton = new IndicatingAjaxLink("selectButton")
			{
				private static final long	serialVersionUID	= 2360250336253032355L;

				@Override
				public void onClick(AjaxRequestTarget target)
				{
					modalSelectDialog.show(target);
				}	
			};
			add(selectButton);	

		    // Add
		    // use the member id and default level (or should we use ...?)
			memberAdd = new AjaxSubmitLink("memberAdd")
	        {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form)
				{
	                logger.debug("memberAdd.onSubmit executed");

	                String memberId = memberSelectPanel.getSelectedId();
	                if (memberId == null || memberId.isEmpty() || memberSelectPanel.getSelectedUser() == null)
	                {
	                	// no such user
	                	return; // ignore and do nothing
	                }

	                addUser(target, memberId);
	            }
	        };
	        add(memberAdd);
	        memberAdd.setOutputMarkupId(true);
			setEnabledOnAjaxSubmitLink (memberAdd, false);

			ArrayList<ProjectPermissionLevel> selectLevelList = new ArrayList<ProjectPermissionLevel>(Arrays.asList(ProjectPermissionLevel.values()));
	        // Change level selection
			DropDownChoice levelsSelection = new DropDownChoice("levelsSelection",
					new PropertyModel(settings, "selectedLevel"),
					selectLevelList,
					new ChoiceRenderer()
					{
						private static final long serialVersionUID = 1L;

						public Object getDisplayValue(Object value)
						{
							// value is the object that the user selected
							ProjectPermissionLevel projectPermissionLevel = (ProjectPermissionLevel) value;
							return getString("permissionLevel." + projectPermissionLevel);
						}
					}
			)
			{
				private static final long serialVersionUID = 1L;
				@Override
				protected CharSequence getDefaultChoice(Object arg0)
				{
					// use our own version of "Choose One"
					return "<option selected=\"selected\" value=\"\">" +
					getString("permissionLevel.null") +
					"</option>";
				}
			};

			add(levelsSelection);
			levelsSelection.setOutputMarkupId(true);
			levelsSelection.add(new AjaxFormComponentUpdatingBehavior("onchange")
	        {
				private static final long serialVersionUID = 1L;
				protected void onUpdate(final AjaxRequestTarget target)
	        	{
	        		logger.debug("Level Selection  onchange");
	        		ajaxUpdateApplyAndRemoveButtons(target);
	        	}
	        });

			// Remove
			memberRemove = new AjaxSubmitLink("memberRemove")
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form)
				{
	            	logger.debug("Remove onSubmit");
	                if (target != null)
	                {
	                	//List<UserPermission> selection = settings.getSelectedUserPermissions();
	                	// get the selection
	                	//logger.debug("selected # (" + selection.size() + ")");

	                	settings.removeSelectedUserPermissions();

	                	target.addComponent(userPermissionsChoice);

	                	ajaxUpdateApplyAndRemoveButtons(target);
	                }
				}
	        };
	        add(memberRemove);
	        memberRemove.setOutputMarkupId(true);

	        // Apply
	        levelApply = new AjaxSubmitLink("levelApply")
	        {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form)
				{
	                logger.debug("levelApply.onSubmit executed");
	                logger.debug("level: " + getString("permissionLevel." + settings.getSelectedLevel()) );

	                if(!settings.hasSelectedLevel())
	                	return; // no level to apply!

	                // show selection info
	                String info = "";
	                for (UserPermission userPermission : settings.selectedUserPermissions)
	                {
	                	info += "\t" + userPermission.getUserId() + "\n";
	                }
	                logger.debug("selected users ("+ settings.selectedUserPermissions.size() +"):\n" + info);

	                // change it
	                settings.changeSelectedUserPermissions();

					target.addComponent(userPermissionsChoice);
				}
	        };
	        add(levelApply);
	        levelApply.setOutputMarkupId(true);

            // Cancel
            final SubmitLink cancelButton = new SubmitLink("cancel")
            {
				private static final long serialVersionUID = 2522899807014485232L;

				@Override
                public void onSubmit()
                {
					logger.debug("Cancelled");
					DoneWithForm();
                }
            };
            cancelButton.setDefaultFormProcessing(false);
            add(cancelButton);
            cancelButton.setOutputMarkupId(true);

	        // Save
            SubmitLink saveButton = new SubmitLink("save")
            {
				private static final long serialVersionUID = -7937543222414927331L;

				@Override
                public void onSubmit()
                {
					logger.debug("Start saving...");

					// update repository
					try
					{
						DccdDataService.getService().updateProject(settings.getProject());
					}
					catch (DataServiceException e)
					{
						logger.error("Failed to update project", e);
						getSession().error("Failed to update project"); // use resource?
						throw new RestartResponseException(ErrorPage.class);
					}

					DoneWithForm();
				}
            };
            add(saveButton);

            // initial update for the buttons
            updateApplyAndRemoveButtons();
		}

		// Not doing anything!
		@Override
		protected void onSubmit()
		{
			logger.debug("Form onSubmit");
		}

		private void DoneWithForm()
		{
			// get the back page, if any
			// possibly the Project page
	        Page backPage = ((DccdSession)Session.get()).getRedirectPage(getPage().getClass());
	        if (backPage != null)
	        {
	        	if (backPage instanceof BasePage)
	        			((BasePage)backPage).refresh(); // page should reflect new premissions
	        	setResponsePage(backPage);
	        }
	        else
	        {
	        	// just go back to a new instance of HomePage
	        	setResponsePage(HomePage.class);
	        }
		}

		private void addUser(AjaxRequestTarget target, String memberId)
		{
            settings.setNewUserId(memberId);

            String userId = settings.getNewUserId();
            // use default level
            ProjectPermissionLevel level = settings.getProject().getPermissionMetadata().getDefaultLevel();
            UserPermission userPermission = new UserPermission(userId, level);
            settings.addUserPermission(userPermission);
            // make this the selection
            // BUT IT DOESNT WORK
            // THE COMPONENT DOESN'T SHOW THE SELECTION!
			settings.getSelectedUserPermissions().clear();
			settings.getSelectedUserPermissions().add(userPermission);

            target.addComponent(userPermissionsChoice);

            ajaxUpdateApplyAndRemoveButtons(target);
		}
		
		// enable or disable depending on the permissions available
		//
		// Note: AJAX scenario; so we call
		// target.addComponent(levelApply);
		// target.addComponent(memberRemove);
		private void ajaxUpdateApplyAndRemoveButtons(final AjaxRequestTarget target)
		{
			updateApplyAndRemoveButtons();
			target.addComponent(levelApply);
			target.addComponent(memberRemove);
		}

		// enable or disable depending on the permissions available
		private void updateApplyAndRemoveButtons()
		{
        	//if (settings.getUserPermissions().isEmpty())
            if (settings.selectedUserPermissions.isEmpty())
        	{
            	logger.debug("disable buttons");
        		// disable remove and apply
        		setEnabledOnAjaxSubmitLink (levelApply, false);
        		setEnabledOnAjaxSubmitLink (memberRemove, false);
        	}
        	else
        	{
            	logger.debug("enable buttons");
        		// enable remove and apply
        		setEnabledOnAjaxSubmitLink (memberRemove, true);

        		// apply only if there is a level to use
        		if(settings.hasSelectedLevel())
        		{
        			setEnabledOnAjaxSubmitLink (levelApply, true);
        		}
        	}
		}

		// for the links with the button style
		private void setEnabledOnAjaxSubmitLink (AjaxSubmitLink link, boolean enabled)
		{
			link.setEnabled(enabled);
			if (enabled)
				link.add(new SimpleAttributeModifier("class","button"));
			else
				link.add(new SimpleAttributeModifier("class","button_disabled"));
		}

		// for adding, removing and changing the project permissions
	    private class PermissionSettings implements Serializable
		{
			private static final long serialVersionUID = -5711405239228486287L;
			private Project project;
			List<UserPermission> selectedUserPermissions = new ArrayList<UserPermission>();//empty
			private ProjectPermissionLevel selectedLevel = null;//ProjectPermissionLevel.PROJECT; // default
			private String newUserId = "";


			PermissionSettings(final Project project)
			{
				this.project = project;
			}

			public Project getProject()
			{
				return project;
			}

			public List<UserPermission> getUserPermissions()
			{
				ProjectPermissionMetadata projectPermissionMetadata = project.getPermissionMetadata();
				return projectPermissionMetadata.getUserPermissionsArrayList();
			}

			public List<UserPermission> getSelectedUserPermissions()
			{
				return selectedUserPermissions;
			}

			public void setSelectedUserPermissions(
					List<UserPermission> selectedUserPermissions)
			{
				this.selectedUserPermissions = selectedUserPermissions;
			}

			public boolean hasSelectedLevel()
			{
				return (selectedLevel != null);
			}

			public ProjectPermissionLevel getSelectedLevel()
			{
				return selectedLevel;
			}

			public void setSelectedLevel(ProjectPermissionLevel selectedLevel)
			{
				this.selectedLevel = selectedLevel;
			}

			public String getNewUserId()
			{
				return newUserId;
			}

			public void setNewUserId(String newUserId)
			{
				this.newUserId = newUserId;
			}

			// TODO add or remove from selecton?
			public void removeSelectedUserPermissions()
			{
				logger.debug("removing selected user permissions (" + settings.selectedUserPermissions.size() +")");

                for (UserPermission userPermission : settings.selectedUserPermissions)
                {
                	logger.debug("removing: " + userPermission.getUserId());
                	//project.getPermissionMetadata().setUserPermission(userId, level)
                	project.getPermissionMetadata().removeUserPermission(userPermission.getUserId());
                }

                // clean up selection, after removal nothing is selected!
                settings.selectedUserPermissions.clear();
			}

			public void addUserPermission(UserPermission userPermission)
			{
				project.getPermissionMetadata().
						setUserPermission(userPermission.getUserId(), userPermission.getLevel());
			}

			public void changeSelectedUserPermissions()
			{
				ProjectPermissionLevel level = settings.getSelectedLevel();

                for (UserPermission userPermission : settings.selectedUserPermissions)
                {
                	logger.debug("changing: " + userPermission.getUserId());
                	project.getPermissionMetadata().setUserPermission(userPermission.getUserId(), level);
                }

			}
		}
	}
}
