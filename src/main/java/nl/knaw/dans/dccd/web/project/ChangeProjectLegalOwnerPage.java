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
import nl.knaw.dans.dccd.model.DccdUser;
import nl.knaw.dans.dccd.model.Project;
import nl.knaw.dans.dccd.web.DccdSession;
import nl.knaw.dans.dccd.web.HomePage;
import nl.knaw.dans.dccd.web.authn.OrganisationPage;
import nl.knaw.dans.dccd.web.base.BasePage;
import nl.knaw.dans.dccd.web.error.ErrorPage;
import nl.knaw.dans.dccd.web.user.SelectOrganisationPanel;

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
public class ChangeProjectLegalOwnerPage extends BasePage
{
	private static Logger logger = Logger.getLogger(ChangeProjectLegalOwnerPage.class);
			
	public ChangeProjectLegalOwnerPage(Project project)
	{
		super();

		init(project);
	}

	private void init(Project project)
	{
		DccdUser user = (DccdUser) ((DccdSession) getSession()).getUser();; 
		// TODO test for admin rights ...
		
		Label titleLabel = new Label("page.title",
				new StringResourceModel("page.title", this, new Model(project)));
		add(titleLabel);

		add(new ChangeProjectOwnerForm("projectOwnerForm", new Model<Project>(project)));
	}
	
	class ChangeProjectOwnerForm extends AbstractCommonStatelessForm
	{
		private boolean refresh = false;
		private boolean userIsAdmin = false;
		
		private SubmitLink saveButton = null;

		public ChangeProjectOwnerForm(String wicketId, IModel<Project> model)
		{
			super(wicketId, model);

			Project project = (Project) getModelObject();
			final String legalOwnerId = project.getAdministrativeMetadata().getLegalOwnerOrganisationId();
				
	        Link ownerLink = new Link("ownerLink", model)
	        {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick()
				{
					// support back navigation
					((DccdSession)Session.get()).setRedirectPage(OrganisationPage.class, getPage());
					setResponsePage(new OrganisationPage(legalOwnerId, false, true)); // can we always edit?
				}
			};
            add(ownerLink);
            ownerLink.add(new Label("ownerDisplayname", legalOwnerId));			
			
			// Autocomplete selection for other manager
			final SelectOrganisationPanel ownerSelectPanel = new SelectOrganisationPanel("ownerSelectPanel") 
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
			add(ownerSelectPanel);       
			
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

					String selectedId = ownerSelectPanel.getSelectedId();
					project.getAdministrativeMetadata().setLegalOwnerOrganisationId(selectedId);
					
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

		private static final long	serialVersionUID	= 1841500461815595448L;

		@Override
		protected void onSubmit()
		{
			// TODO Auto-generated method stub
		}
		
		// navigate back, and refresh id requested
		private void doneWithForm()
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

