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
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.wicket.components.upload.EasyUpload;
import nl.knaw.dans.common.wicket.components.upload.EasyUploadConfig;
import nl.knaw.dans.common.wicket.components.upload.postprocess.unzip.UnzipPostProcess;
import nl.knaw.dans.dccd.web.DccdSession;

import org.apache.wicket.Session;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

/**
 * @author paulboon
 */
public class AssociatedFilesUploadSectionPanel extends Panel
{
	private static final long	serialVersionUID	= 8563428942920101279L;
	
	private EasyUpload uploadAssociatedFiles;
	private Model associatedFilesCombinedUploadWarningsModel;
	private Label associatedFilesCombinedUploadWarningsLabel;
	private Model associatedFilesCombinedUploadErrorsModel;
	private Label associatedFilesCombinedUploadErrorsLabel;
	private Label associatedFilesUploadedMessageLabel;
	private Model associatedFilesUploadedMessageModel;
	private String tempDir;

	public AssociatedFilesUploadSectionPanel(String id)
	{
		super(id);
		
		init();
	}

	@Override
	protected void onBeforeRender()
	{
		// retrieve the status, used for the UI
        CombinedUploadStatus status = getStatus();
		
		associatedFilesCombinedUploadWarningsModel.setObject(status.getAssociatedFilesUploadWarnings());
		associatedFilesCombinedUploadErrorsModel.setObject(status.getAssociatedFilesUploadErrors());
		associatedFilesUploadedMessageModel.setObject(status.getAssociatedFilesUploadedMessage());        

		super.onBeforeRender();
	}	

	private void init()
	{
		// get the system temp folder
		tempDir = System.getProperty("java.io.tmpdir");
		//logger.info("using temp dir for upload: " + tempDir);

		EasyUploadConfig uploadConfig = new EasyUploadConfig(tempDir);
		uploadConfig.setAutoRemoveMessages(true);
	
		uploadAssociatedFiles =	new EasyUpload("associatedfiles_upload_panel", uploadConfig) 
		{
			private static final long	serialVersionUID	= 2335734296187344977L;
			@Override
			public void onReceivedFiles(Map<String, String> clientParams, String basePath, List<File> files)
			{
				//TODO processUploadedAssociatedFiles(files); 
				// get the Files and put them in a list...
				getCombinedUpload().addAssociatedFiles(files);
				//addAssociatedFiles(List<File> files)
				// note: no cleanup, where to do that?

				//logger.debug("Associated files upload done!");
			}	
		};
		// Unzip (when needed) first
		uploadAssociatedFiles.registerPostProcess(UnzipPostProcess.class);
		add(uploadAssociatedFiles);
		
		associatedFilesCombinedUploadWarningsModel = new Model("");
		associatedFilesCombinedUploadWarningsLabel  = new Label("associatedfiles_combined_upload_warnings", associatedFilesCombinedUploadWarningsModel);
		associatedFilesCombinedUploadWarningsLabel.setEscapeModelStrings(false);
		add(associatedFilesCombinedUploadWarningsLabel);

		associatedFilesCombinedUploadErrorsModel = new Model("");
		associatedFilesCombinedUploadErrorsLabel = new Label("associatedfiles_combined_upload_errors", associatedFilesCombinedUploadErrorsModel);
		associatedFilesCombinedUploadErrorsLabel.setEscapeModelStrings(false);
		add(associatedFilesCombinedUploadErrorsLabel);
		
		associatedFilesUploadedMessageModel = new Model("");
		associatedFilesUploadedMessageLabel = new Label("associatedfiles_uploaded_message", associatedFilesUploadedMessageModel);
		associatedFilesUploadedMessageLabel.setEscapeModelStrings(false);
		add(associatedFilesUploadedMessageLabel);		
	}
	
	private CombinedUploadStatus getStatus()
	{
		return getCombinedUpload().getStatus();
	}
	
	// CombinedUpload contains the data and logic for the upload process
	private CombinedUpload getCombinedUpload()
	{
		CombinedUpload combinedUpload = ((DccdSession)Session.get()).getCombinedUpload();
		return combinedUpload;
	}	
}

