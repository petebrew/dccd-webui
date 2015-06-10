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

import nl.knaw.dans.common.web.template.AbstractCommonStatelessForm;
import nl.knaw.dans.dccd.application.services.DataServiceException;
import nl.knaw.dans.dccd.application.services.DccdDataService;
import nl.knaw.dans.dccd.application.services.DccdUserService;
import nl.knaw.dans.dccd.application.services.UserServiceException;
import nl.knaw.dans.dccd.model.DccdUser;
import nl.knaw.dans.dccd.model.Project;
import nl.knaw.dans.dccd.model.DccdUser.Role;
import nl.knaw.dans.dccd.web.DccdSession;
import nl.knaw.dans.dccd.web.HomePage;
import nl.knaw.dans.dccd.web.authn.MemberPage;
import nl.knaw.dans.dccd.web.base.BasePage;
import nl.knaw.dans.dccd.web.error.ErrorPage;
import nl.knaw.dans.dccd.web.search.pages.MyProjectsSearchResultPage;
import nl.knaw.dans.dccd.web.user.SelectManagerPanel;

import org.apache.log4j.Logger;
import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

/**
 */
public class ChangeProjectManagerPage extends BasePage
{
	private static Logger logger = Logger.getLogger(ChangeProjectManagerPage.class);
			
	public ChangeProjectManagerPage(Project project)
	{
		super();

		init(project);
	}

	private void init(Project project)
	{
		Label titleLabel = new Label("page.title",
				new StringResourceModel("page.title", this, new Model(project)));
		add(titleLabel);

		add(new ChangeProjectManagerForm("projectManagerForm", new Model<Project>(project)));

	}
	
	class ChangeProjectManagerForm extends AbstractCommonStatelessForm
	{
		private static final long	serialVersionUID	= 8183427934992179584L;
		private boolean refresh = false;
		private DccdUser user = null; 
		private boolean userIsManager = false;
		private boolean userIsAdmin = false;
		private DccdUser manager = null;
		
		private SubmitLink saveButton = null;
		
		public ChangeProjectManagerForm(String wicketId, IModel<Project> model)
		{
			super(wicketId, model);

			Project project = (Project) getModelObject();
			String managerId = project.getAdministrativeMetadata().getManagerId();
			
			// get the user
			user = (DccdUser) ((DccdSession) getSession()).getUser();
			
			// get the manager
			try
			{
				manager = DccdUserService.getService().getUserById(managerId);
			}
			catch (UserServiceException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
	
			userIsManager = manager.getId().equals(user.getId());
			userIsAdmin = user.hasRole(Role.ADMIN);
			// TODO test if we are allowed
			
			// message
			Label managerMessageLabel = new Label("managerMessage", getString("managerMessage"));
			add(managerMessageLabel);
			// only show this message to the manager (and not another with admin rights)
			if (!userIsManager) 
			{
				managerMessageLabel.setVisible(false);
			}
			
			//add(new Label("managerName", manager.getDisplayName()));	
	        Link memberLink = new Link("managerLink", model)
	        {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick()
				{
					// support back navigation
					((DccdSession)Session.get()).setRedirectPage(MemberPage.class, getPage());
					setResponsePage(new MemberPage(manager.getId(), false, true)); // can we always edit?
				}
			};
            add(memberLink);
            memberLink.add(new Label("managerDisplayname", manager.getDisplayName()));			
			
			// Autocomplete selection for other manager
			final SelectManagerPanel memberSelectPanel = new SelectManagerPanel("memberSelectPanel") 
			{
				private static final long	serialVersionUID	= 1L;
				@Override
				protected void onSelectionChanged(final AjaxRequestTarget target) 
				{
					logger.debug("--- selection changed");
					if (isSelectionValid())
					{
						logger.debug("enable Finish");
						saveButton.setEnabled(true);
						saveButton.add(new SimpleAttributeModifier("class","button"));
					}
					else
					{
						logger.debug("disable Finish");
						saveButton.setEnabled(false);
						saveButton.add(new SimpleAttributeModifier("class","button_disabled"));
					}
					target.addComponent(saveButton);
				}
			};
			add(memberSelectPanel);
			
			// REFACTOR NOTE:	 could be normal link
	        // Cancel
	        final SubmitLink cancelButton = new SubmitLink("cancel")
	        {
				private static final long serialVersionUID = 1L;
				@Override
	            public void onSubmit()
	            {
					logger.debug("Cancelled");
					doneWithForm();
	            }
	        };
	        cancelButton.setDefaultFormProcessing(false);
	        add(cancelButton);
	        cancelButton.setOutputMarkupId(true);

	        // Save
	        saveButton = new SubmitLink("save")
	        {
				private static final long serialVersionUID = 1L;
				@Override
	            public void onSubmit()
	            {
					logger.debug("Start Save...");
					
					Project project = (Project) getModelObject();
					DccdUser selectedUser = memberSelectPanel.getSelectedUser();
					project.getAdministrativeMetadata().setManagerId(selectedUser.getId());
					
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
					
					refresh = true;
					doneWithForm();
				}
	        };
	        add(saveButton);
	        saveButton.setOutputMarkupId(true);

	        // initially we have no valid selection, so the finish should be disabled
	        saveButton.setEnabled(false);
			saveButton.add(new SimpleAttributeModifier("class","button_disabled"));
		}

		@Override
		protected void onSubmit()
		{
			// TODO Auto-generated method stub
		}

		// navigate back, and refresh id requested
		private void doneWithForm()
		{
			if (userIsManager && !userIsAdmin)
			{
				// It's not ours anymore, so back to MyProjects and show it is gone
				//setResponsePage(MyProjectsPage.class);
				setResponsePage(MyProjectsSearchResultPage.class);
			}
			else
			{
				// note: should be admin
				
				// get the back page, if any
				// possibly the Project page
		        Page backPage = ((DccdSession)Session.get()).getRedirectPage(getPage().getClass());
		        if (backPage != null)
		        {
		        	if (refresh && backPage instanceof BasePage)
		        			((BasePage)backPage).refresh(); // page should reflect changes
		        	setResponsePage(backPage);
		        }
		        else
		        {
		        	// just go back to a new instance of HomePage
		        	setResponsePage(HomePage.class);
		        }
			}
			
		}
		
	}
}

