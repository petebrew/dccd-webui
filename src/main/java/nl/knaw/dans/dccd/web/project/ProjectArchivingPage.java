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
import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.web.template.AbstractCommonStatelessForm;
import nl.knaw.dans.dccd.application.services.DataServiceException;
import nl.knaw.dans.dccd.application.services.DccdDataService;
import nl.knaw.dans.dccd.application.services.DccdProjectValidationService;
import nl.knaw.dans.dccd.application.services.UIMapper;
import nl.knaw.dans.dccd.application.services.ValidationErrorMessage;
import nl.knaw.dans.dccd.model.DccdUser;
import nl.knaw.dans.dccd.model.Project;
import nl.knaw.dans.dccd.web.DccdSession;
import nl.knaw.dans.dccd.web.HomePage;
import nl.knaw.dans.dccd.web.base.BasePage;
import nl.knaw.dans.dccd.web.error.ErrorPage;

import org.apache.log4j.Logger;
import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

/**
 * @author paulboon
 */
public class ProjectArchivingPage extends BasePage
{
	private static Logger logger = Logger.getLogger(ProjectArchivingPage.class);
	ArchivingAcceptLicenseSelection acceptLicenseSelection =
		new ArchivingAcceptLicenseSelection();

	public ProjectArchivingPage(Project project)
	{
		super();

		init(project);
	}

	private void init(Project project)
	{
		// TODO check if user logged in and allowed to archive project

		Label titleLabel = new Label("page.title",
				new StringResourceModel("page.title", this, new Model(project)));
		add(titleLabel);

		add(new ProjectArchivingForm("projectArchivingForm", new Model<Project>(project)));
	}

	class ProjectArchivingForm extends AbstractCommonStatelessForm
	{
		private static final long serialVersionUID = 1039582985209925433L;

		private boolean projectTridasValid = false;
		List<ValidationErrorMessage> errorMessages = new ArrayList<ValidationErrorMessage>();
			
		public ProjectArchivingForm(String wicketId, IModel<Project> model)
		{
			super(wicketId, model);

			Project project = (Project) getModelObject();

			validateProjectTridas(project);
			
			// validation results
			String invalidProjectTridasMessage = "The project seems not to be entirely complete/correct. " + 
			 									 "<br/> Please cancel and correct the problem(s) cited below.";
			Label invalidLabel = new Label("invalid", invalidProjectTridasMessage);
			add(invalidLabel);
			invalidLabel.setEscapeModelStrings(false);
			invalidLabel.setVisible(!isValid());
			
			// Note: a common feedback panel did not work; the error's cann't be set in the form constructor
			
			// TODO have a listView with <ul><li></li></ul> etc.
			String validationMessagesText = ""; 
			validationMessagesText = validationMessagesText + "<ul>";
			for(ValidationErrorMessage msg : errorMessages)
			{
				logger.debug("Validation error: " + msg.getMessage());
								
				// Note message needs to be improved for the UI
				String fieldName = msg.getFieldNameInUIStyle();
				String entityName = UIMapper.getEntityLabelString(msg.getClassName());
			
				// Note error messages are displayed in the feedbackpanel
				// TODO have a special function for constructing the 'on screen' messages
				// see also ProjectEditPage
				validationMessagesText = validationMessagesText +
						"<li>" + msg.getMessage() + 
						" <b>" + fieldName + "</b> in " + 
						" <b>" + entityName + "</b><br/>" +
						"</li>";
			}
			validationMessagesText = validationMessagesText + "</ul>";
			
			Label validationFeedback = new Label("validationFeedback", validationMessagesText);
			add(validationFeedback);
			validationFeedback.setEscapeModelStrings(false);
			validationFeedback.setVisible(!isValid());
			
	        // Cancel
	        final SubmitLink cancelButton = new SubmitLink("cancel")
	        {
				private static final long serialVersionUID = 1L;
				@Override
	            public void onSubmit()
	            {
					logger.debug("Cancelled");

					CancelForm();
	            }
	        };
	        cancelButton.setDefaultFormProcessing(false);
	        add(cancelButton);
	        cancelButton.setOutputMarkupId(true);

	        // Finish
	        final SubmitLink finishButton = new SubmitLink("finish")
	        {
				private static final long serialVersionUID = 1L;
				@Override
	            public void onSubmit()
	            {
					logger.debug("Start Finish...");

					// check if archiving is allowed
					if (!acceptLicenseSelection.isAccepted())
					{
						logger.warn("disallowing attempt to archive without license accception");
						return;
					}

					Project project = (Project) getModelObject();

					try
					{
						DccdDataService.getService().archiveProject(project, 
								(DccdUser) ((DccdSession) getSession()).getUser());
					}
					catch (DataServiceException e)
					{
						logger.error("Failed to update project", e);
						getSession().error("Failed to update project"); // use resource?
						throw new RestartResponseException(ErrorPage.class);
					}
					
					// Set Page to return to, maybe not needed
					Page backPage = ((DccdSession)Session.get()).getRedirectPage(getPage().getClass());
					((DccdSession)Session.get()).setRedirectPage(ArchivingConfirmPage.class, backPage);
					
					// Go to confirmation page
					setResponsePage(new ArchivingConfirmPage(project));
				}
	        };
	        add(finishButton);
	        finishButton.setOutputMarkupId(true);
	        //finishButton.setEnabled(false); // only enabled if validated and accepted!
	        setEnabledOnSubmitLink (finishButton, false);

			add(new ArchivingAcceptLicensePanel("license_accept_panel", new Model(acceptLicenseSelection))
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void onAcceptChange(AjaxRequestTarget target, boolean accepted)
				{
					logger.debug("Accept");
					
					if (isValid()) 
					{						
						setEnabledOnSubmitLink (finishButton, accepted);
						if (target != null)
						{
							target.addComponent(finishButton);
						}
					}
				}
			});		
		}

		@Override
		protected void onSubmit()
		{
			// TODO Auto-generated method stub
		}

		// for the links with the button style
		private void setEnabledOnSubmitLink (SubmitLink link, boolean enabled)
		{
			link.setEnabled(enabled);
			if (enabled)
				link.add(new SimpleAttributeModifier("class","button"));
			else
				link.add(new SimpleAttributeModifier("class","button_disabled"));
		}

		private boolean isValid()
		{
			return (projectTridasValid);
		}

		// validate Tridas with DCCD extra restrictions
		private void validateProjectTridas(Project project)
		{						
			// validate for DCCD
			errorMessages = DccdProjectValidationService.getService().validate(project);

			// There could be TRiDaS schema issues, so just to be sure!
			if (errorMessages.isEmpty()) 
			{
				errorMessages = DccdProjectValidationService.getService().validateAgainstTridasSchema(project);				
			}
			
			
			// indicate if it's valid
			if (errorMessages.isEmpty())
				projectTridasValid = true;
			else
				projectTridasValid = false;
		}
	}
	
	private void CancelForm()
	{
		// get the back page, if any
		// possibly the Project page
        Page backPage = ((DccdSession)Session.get()).getRedirectPage(getPage().getClass());
        if (backPage != null)
        {
        	//if (backPage instanceof BasePage)
        	//		((BasePage)backPage).refresh(); // page should reflect changes
        	setResponsePage(backPage);
        }
        else
        {
        	// just go back to a new instance of HomePage
        	setResponsePage(HomePage.class);
        }
	}
	/**
	 * Keep track if the license has been accepted or not
	 */
	public class ArchivingAcceptLicenseSelection implements Serializable {
		private static final long serialVersionUID = -3326823575185401960L;
		private boolean accepted = false; // not accepted by default

		public boolean isAccepted() {
			return accepted;
		}

		public void setAccepted(boolean accepted) {
			this.accepted = accepted;
		}
	}
}

