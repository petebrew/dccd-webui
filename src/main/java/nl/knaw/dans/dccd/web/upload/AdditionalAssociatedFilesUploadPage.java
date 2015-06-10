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
package nl.knaw.dans.dccd.web.upload;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.wicket.components.upload.EasyUpload;
import nl.knaw.dans.common.wicket.components.upload.EasyUploadConfig;
import nl.knaw.dans.common.wicket.components.upload.postprocess.unzip.UnzipPostProcess;
import nl.knaw.dans.dccd.application.services.DataServiceException;
import nl.knaw.dans.dccd.application.services.DccdDataService;
import nl.knaw.dans.dccd.model.DccdAssociatedFileBinaryUnit;
import nl.knaw.dans.dccd.model.DccdUser;
import nl.knaw.dans.dccd.model.Project;
import nl.knaw.dans.dccd.web.DccdSession;
import nl.knaw.dans.dccd.web.base.BasePage;
import nl.knaw.dans.dccd.web.error.ErrorPage;
import nl.knaw.dans.dccd.web.project.ProjectViewPage;

import org.apache.log4j.Logger;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.Session;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.tridas.schema.TridasFile;

/**
 * A separate page for only uploading additional associated files 
 * to an already uploaded project. 
 * 
 * - Only allow upload of files with a unique name (ignoring case)
 *   if it was already there we can't overwrite it (might be published etc.)
 *   if it is in the additional file being uploaded, discard it (not the first?)
 * - Add 'file' elements in the TRiDaS
 *   although 'file' elements can be in the TRiDaS on all entity levels, 
 *   only add them on the topmost Project level 
 * 
 * @author paulboon
 *
 */
public class AdditionalAssociatedFilesUploadPage extends BasePage
{

	private static Logger logger = Logger.getLogger(AdditionalAssociatedFilesUploadPage.class);
	private ListView<File> uploadedFilesView;
	// get the system temp folder
	private String tempDir = System.getProperty("java.io.tmpdir");
	
	public List<File> getUploadedFiles() {
		// We can't place the uploaded files in a member variable because of the EasyUpload control. 
		// can't explain it though... but it has something to do with the Wicket serialization/deserialisation
		// We need to place it in the session and there already is a CombinedUpload, so lets use that. 
		// Note that it would be better to use a special AssocUpload object in the session 
		// when we want to track more then the files.  
		return ((DccdSession)Session.get()).getCombinedUpload().associatedFiles;
	}

	public AdditionalAssociatedFilesUploadPage(IModel<Project> model) {
		super(model);
		initPage();
	}
	
	private void initPage()
	{
		// make sure we start fresh		
		cleanUp();

		Project project = (Project) getDefaultModelObject();
		if (project == null)
			throw new IllegalArgumentException("Missing project");
			
		// test if I am allowed to change the project (owner or admin!)
		DccdUser user = (DccdUser) ((DccdSession) getSession()).getUser();
		if (!project.isManagementAllowed(user))
			throw new RestartResponseException(ErrorPage.class);
		
		// show minimal project info
		add(new Label("project_title", new PropertyModel(project, "title")));
		// need id as well
		
		// then list all associated files already there
		// you are allowed to see those
		List<DccdAssociatedFileBinaryUnit> units = project.getAssociatedFileBinaryUnits();
		logger.debug("# of associated files = " + units.size());
		add(new ListView<DccdAssociatedFileBinaryUnit>("stored_files", units)
		{
	        public void populateItem(final ListItem<DccdAssociatedFileBinaryUnit> item)
	        {
				final DccdAssociatedFileBinaryUnit unit = item.getModelObject();
				item.add(new Label("stored_files.filename", unit.getFileName()));
	        }
		});
		
		// list of uploaded on this page
		uploadedFilesView = new ListView<File>("uploaded_files", new PropertyModel(this, "uploadedFiles"))
		{
			private static final long serialVersionUID = -8928838129580404599L;

			public void populateItem(final ListItem<File> item)
	        {
				final File file = item.getModelObject();
				item.add(new Label("uploaded_files.filename", file.getName()));
	        }
		};	
		uploadedFilesView.setOutputMarkupId(true);
		add(uploadedFilesView);

		
		// upload additional ones
		logger.info("using temp dir for upload: " + tempDir);
		EasyUploadConfig uploadConfig = new EasyUploadConfig(tempDir);
		uploadConfig.setAutoRemoveMessages(true);
		EasyUpload uploadAssociatedFiles =	new EasyUpload("associatedfiles_upload_panel", uploadConfig) 
		{
			private static final long serialVersionUID = -3682853163107499006L;

			@Override
			public void onReceivedFiles(Map<String, String> clientParams, String basePath, List<File> files)
			{
				logger.debug("Associated files upload done!");
				// get the Files and put them in a list...
				addAssociatedFiles(files);
			}	
		};
		// Unzip (when needed) first
		uploadAssociatedFiles.registerPostProcess(UnzipPostProcess.class);
		add(uploadAssociatedFiles);
	
		Form<Project> form = new Form<Project>("form")
		{
			private static final long serialVersionUID = 2395669492108652762L;

			@Override
			protected void onSubmit()
			{
				// empty
			}
		};
		add(form);

		SubmitLink cancelButton = new SubmitLink("cancel_button")
		{
			private static final long serialVersionUID = 4664735638750828620L;

			@Override
			public void onSubmit()
			{
				logger.debug("Cancel onSubmit is called");
				cancel();
			}

		};
		cancelButton.setOutputMarkupId(false);
		form.add(cancelButton);
		SubmitLink finishButton = new SubmitLink("finish_button")
		{
			private static final long serialVersionUID = 6288043958187838779L;

			@Override
			public void onSubmit()
			{
				logger.debug("Finish onSubmit is called");
				if (getUploadedFiles().isEmpty()) 
				{
					logger.debug("Nothing to store");
					return; // Do nothing!
				}

				finish();
			}

		};
		finishButton.setOutputMarkupId(false);
		form.add(finishButton);
		
        // add javascript libraries
	    add(HeaderContributor.forJavaScript(new ResourceReference(EasyUpload.class, "js/lib/json2.js")));
	    add(HeaderContributor.forJavaScript(new ResourceReference(EasyUpload.class, "js/lib/jquery-1.3.2.min.js")));
	    
	    // for getting at the upload event handlers
	    add(HeaderContributor.forJavaScript(new ResourceReference(EasyUpload.class, "js/EasyUpload.js")));
	    // the page specific stuff
	    add(HeaderContributor.forJavaScript(new ResourceReference(UploadFilesPage.class, "AdditionalFilesUpload.js")));
	}
	
	private void addAssociatedFiles(List<File> files)
	{
		// only add what we want
		for (File file : files)
		{	
			// ignore directories (potentially from a zip)
			if (!file.isFile())
			{
				logger.debug("Skipping: \"" + file.getName() + "\" Not a file");
				continue;
			}
			
			// check if we have a file with the same name already, if so discard it
			// should indicate with a message
			if (hasUploadedFileWithSameName(file.getName()))
			{
				logger.debug("Skipping: \"" + file.getName() + "\" Allready uploaded");				
				continue;
			}

			// check if it is already in the stored project
			if (hasFileWithSameNameInProject(file.getName()))
			{
				logger.debug("Skipping: \"" + file.getName() + "\" Allready stored in project");				
				continue;
			}
			
			getUploadedFiles().add(file);
		}
	}
	
	private boolean hasUploadedFileWithSameName(String name)
	{
		for (File file : getUploadedFiles())
		{
			//logger.debug("Associated file: \"" + file.getName() + "\"");
			if(file.getName().equalsIgnoreCase(name))
				return true; // done
		}
		
		// found no match
		return false;
	}
	
	private boolean hasFileWithSameNameInProject(String name)
	{
		Project project = (Project) getDefaultModelObject();
		List<DccdAssociatedFileBinaryUnit> units = project.getAssociatedFileBinaryUnits();
		for (DccdAssociatedFileBinaryUnit unit: units)
		{
			//logger.debug("Associated file: \"" + file.getName() + "\"");
			if(unit.getFileName().equalsIgnoreCase(name))
				return true; // done
		}
		
		// found no match
		return false;
	}
	
	// Cancel upload
	private void cancel()
	{
		// reset this page to it's initial state
		//uploadedFiles.clear();
		// just leave this page
		 doneWithForm();
	}

	// Finish upload
	private void finish()
	{
		store();
		
		// then leave the page.
		doneWithForm(true);
	}
	
	private void store()
	{
		Project project = (Project) getDefaultModelObject();
				
		// add all the files to the project...?
		for (File file : getUploadedFiles())
		{
			try {
				// add file data
				project.addAssociatedFile(file);
				
				// add file element to TRiDaS Project entity
				TridasFile tFile = new TridasFile();
				tFile.setHref(file.getName());
				project.getTridas().getFiles().add(tFile);
				
			} catch (IOException e) {
				e.printStackTrace();
				cleanUp();
				// go to the error page!
				throw new RestartResponseException(ErrorPage.class);
			}
		}
						 
		// Store the project
		logger.info("Store... (update project)");
		try
		{
			DccdDataService.getService().updateProject(project);
		}
		catch (DataServiceException e)
		{
			logger.error("Failed to store project", e);
			e.printStackTrace();
			cleanUp();
			// go to the error page!
			throw new RestartResponseException(ErrorPage.class);
		}
		logger.info("Done storing");
	}
	
	// navigate back, and refresh if requested
	private void doneWithForm()
	{
		doneWithForm(false);
	}
	
	private void doneWithForm(boolean refresh)
	{	
		// cleanup if we can	
		cleanUp();

        // Not a refresh but reload is needed here to update the project entity panel
		// refresh of the view page would be better, but ... is more work
        Project project = (Project) getDefaultModelObject();
		try
		{
			project = DccdDataService.getService().getProject(project.getSid());
		}
		catch (DataServiceException e)
		{
			logger.error("Failed to retrieve project after update", e);
			// go to the error page!
			throw new RestartResponseException(ErrorPage.class);
		}
		setResponsePage(new ProjectViewPage(project));
	}
	
	private void cleanUp()
	{
		// remove any File objects from the session
		getUploadedFiles().clear();
	}
}
