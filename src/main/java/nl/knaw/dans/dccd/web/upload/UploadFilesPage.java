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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import nl.knaw.dans.common.wicket.components.upload.EasyUpload;
import nl.knaw.dans.common.wicket.components.upload.EasyUploadConfig;
import nl.knaw.dans.common.wicket.components.upload.postprocess.unzip.UnzipPostProcess;
import nl.knaw.dans.dccd.util.FileUtil;
import nl.knaw.dans.dccd.util.LanguageProvider;
import nl.knaw.dans.dccd.web.DccdSession;
import nl.knaw.dans.dccd.web.base.BasePage;
import nl.knaw.dans.dccd.web.error.ErrorPage;
import nl.knaw.dans.dccd.web.search.pages.MyProjectsSearchResultPage;
import nl.knaw.dans.dccd.web.upload.CombinedUploadStatus.TreeRingDataFileTypeSelection;

import org.apache.log4j.Logger;
import org.apache.wicket.Page;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.Session;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.template.TextTemplateHeaderContributor;

/**
 * Upload page for several types of upload, but each with
 * TRiDaS (at least metadata) and some with treering data files in other formats.
 * TRiDaS is the xml file format and
 * the "TreeRingData" are the measureement values, stored in non-TRiDaS format.
 *
 * @author paulboon
 */
public class UploadFilesPage extends BasePage
{
	private static Logger logger = Logger.getLogger(UploadFilesPage.class);

	// CombinedUpload contains the data and logic for the upload process
	public CombinedUpload getCombinedUpload()
	{
		CombinedUpload combinedUpload = ((DccdSession)Session.get()).getCombinedUpload();
		return combinedUpload;
	}

	private String tempDir;
	//REFACTOR NOTE: 
	//	 only one submitLink per page 
	//	 for the others use normal Links with onClick
	private SubmitLink finishAndUploadButton; // submit!
	private SubmitLink finishButton; // submit!
	private SubmitLink cancelButton;
	//private SubmitLink backButton;
	private Model uploadHintsModel;
	private Label uploadHintsLabel;
	// Tridas
	private EasyUpload uploadTridas;
	private Label tridasFilesUploadedMessageLabel;
	private Model tridasFilesUploadedMessageModel;
	private Model tridasCombinedUploadHintsModel;
	private Label tridasCombinedUploadHintslabel;
	private Model tridasCombinedUploadWarningsModel;
	private Label tridasCombinedUploadWarningslabel;
	// Treering data
	private EasyUpload uploadTreeRingData;
	DropDownChoice fileTypeSelection;
	private Model treeRingDataCombinedUploadWarningsModel;
	private Label treeRingDataCombinedUploadWarningsLabel;
	private Model treeRingDataCombinedUploadErrorsModel;
	private Label treeRingDataCombinedUploadErrorsLabel;
	private Label treeRingDataFilesUploadedMessageLabel;
	private Model treeRingDataFilesUploadedMessageModel;
	
	// set the default type; or should it be part of the CombinedUpload?
	private TridasUploadType uploadType =  TridasUploadType.TRIDAS_WITH_DATA;//TridasUploadType.TRIDAS_ONLY; 

	public UploadFilesPage()
	{
		initPage();
	}

	public UploadFilesPage(TridasUploadType uploadType)
	{
		this.uploadType = uploadType;
		logger.debug("Upload page contructed with given type: " + uploadType);
		initPage();
	}

	public CombinedUploadStatus getStatus()
	{
		return getCombinedUpload().getStatus();
	}

	public void reset()
	{
		getCombinedUpload().reset();
	}

	public void store() throws CombinedUploadStoreException
	{
		getCombinedUpload().storeProject();
	}

	// need this to have a correct page after a 'browser page refresh'
	@Override
	protected void onBeforeRender()
	{
		// retrieve the status, used for the UI
        CombinedUploadStatus status = getStatus();

        logger.debug("Finish: " + status.isReadyToFinish() + ", Cancel: " + status.isReadyToCancel());
		Session session = Session.get();
		String sessionId = session.getId();
		logger.debug("=> Session Id: " + sessionId);
		logger.debug("=> Page Id: " + getId());

        //Note: measurements_filetype selection might be set here also

        // messages (Label models)
        uploadHintsModel.setObject(status.getMessage());
        // tridas
        tridasCombinedUploadHintsModel.setObject(status.getTridasUploadHints());
        tridasCombinedUploadWarningsModel.setObject(status.getTridasUploadWarnings());
		tridasFilesUploadedMessageModel.setObject(status.getTridasFilesUploadedMessage());
		// treeringdata
		treeRingDataCombinedUploadErrorsModel.setObject(status.getTreeRingDataUploadErrors());
		treeRingDataCombinedUploadWarningsModel.setObject(status.getTreeRingDataUploadWarnings());
		treeRingDataFilesUploadedMessageModel.setObject(status.getTreeRingDataFilesUploadedMessage());
		  
        // All switching is already done in the JS code on the client;
        // but when a page reload is done we need to get it right
        //
        if (getCombinedUpload().hasProject())
        {
        	// a tridas must have been succesfully uploaded
        	// when this has happened we hide the upload for another tridas
        	uploadTridas.setVisible(false);
        }

		// Note: Maybe make a Panel or Component group (container
        // for the different upload parts.
        // For now the wicket:enclosure makes sure the whole group is hidden
		if (uploadType == TridasUploadType.TRIDAS_ONLY)
		{
			// hide  value files upload
			uploadTreeRingData.setVisible(false);
			treeRingDataCombinedUploadWarningsLabel.setVisible(false);
			treeRingDataCombinedUploadErrorsLabel.setVisible(false);
			treeRingDataFilesUploadedMessageLabel.setVisible(false);
			fileTypeSelection.setVisible(false);
		}
		else
		{
			uploadTreeRingData.setVisible(true);
			treeRingDataCombinedUploadWarningsLabel.setVisible(true);
			treeRingDataCombinedUploadErrorsLabel.setVisible(true);
			treeRingDataFilesUploadedMessageLabel.setVisible(true);
			fileTypeSelection.setVisible(true);
		}

		// Note: switching on status.isAssociatedFilesVisible()
		// somehow does not produce the correct results...

		super.onBeforeRender();
	}

	@Override
	public String getTitle()
	{
		//ResourceModel titleModel = new ResourceModel ("page_title");
		return "Upload TRiDaS";
	}

	private void initPage()
	{
		redirectIfNotLoggedIn();
		
		logger.debug("Upload page constructor: " + Session.get().getId() + ", " + getId());
		
		// just to be sure we have a clean CombinedUpload
		reset(); 

		// Specify type of upload, so it can use this information when generating the status
		getCombinedUpload().setUploadType(uploadType);

		// get the system temp folder
		tempDir = System.getProperty("java.io.tmpdir");
		logger.info("using temp dir for upload: " + tempDir);

		Form form = new Form("form")
		{
			private static final long serialVersionUID = -941132362346269544L;

			@Override
			protected void onSubmit()
			{
				// empty
			}
		};
		add(form);

		// same configuration for both uploads
		EasyUploadConfig uploadConfig = new EasyUploadConfig(tempDir);
		uploadConfig.setAutoRemoveMessages(true);
		//uploadConfig.setAutoRemoveFiles(true);
		
		//--- Tridas
		// TODO put it in a Panel
		//
		// TRiDaS upload
		uploadTridas = new EasyUpload("tridas_upload_panel", uploadConfig)
		{
			private static final long serialVersionUID = 3910788086153129740L;

			@Override
			public void onReceivedFiles(Map<String, String> clientParams, String basePath, List<File> files)
			{
				// add to the original files
				getCombinedUpload().addOriginalFiles(files);
				
				// Always delete the temporary files
				FileUtil.deleteDirectory(new File(basePath));
				logger.info("Temp File(s) deleted");
				logger.debug("Tridas files upload done!");
			}
		};
		uploadTridas.registerPostProcess(TridasXMLUploadProcess.class);
		add(uploadTridas);

        tridasCombinedUploadHintsModel = new Model("");
        tridasCombinedUploadHintslabel = new Label("tridas_combined_upload_hints", tridasCombinedUploadHintsModel);
        tridasCombinedUploadHintslabel.setEscapeModelStrings(false);
        add(tridasCombinedUploadHintslabel);

        tridasCombinedUploadWarningsModel = new Model("");
        tridasCombinedUploadWarningslabel = new Label("tridas_combined_upload_warnings", tridasCombinedUploadWarningsModel);
        tridasCombinedUploadWarningslabel.setEscapeModelStrings(false);
        add(tridasCombinedUploadWarningslabel);

		tridasFilesUploadedMessageModel = new Model("");
		tridasFilesUploadedMessageLabel = new Label("tridas_files_uploaded_message", tridasFilesUploadedMessageModel);
		tridasFilesUploadedMessageLabel.setEscapeModelStrings(false);
		add(tridasFilesUploadedMessageLabel);
		
		// --- Tree ring data
		// TODO put it in a Panel
		//
		// upload external measurement values
		// Note: The GUI design says "Values" and the code will use "TreeRingData")
		uploadTreeRingData = new EasyUpload("measurements_upload_panel", uploadConfig)
		{
			private static final long serialVersionUID = 6954709169671339342L;

			@Override
			public void onReceivedFiles(Map<String, String> clientParams, String basePath, List<File> files)
			{
				// add to the original files
				getCombinedUpload().addOriginalFiles(files);

				// Always delete the temporary files
				FileUtil.deleteDirectory(new File(basePath));
				logger.info("Temp File(s) deleted");
				logger.debug("Value files upload done!");
			}
		};
		// Unzip (when needed) first
		uploadTreeRingData.registerPostProcess(UnzipPostProcess.class);
		uploadTreeRingData.registerPostProcess(TreeRingDataUploadProcess.class);
		add(uploadTreeRingData);

        treeRingDataCombinedUploadWarningsModel = new Model("");
        treeRingDataCombinedUploadWarningsLabel = new Label("values_combined_upload_warnings", treeRingDataCombinedUploadWarningsModel);
        treeRingDataCombinedUploadWarningsLabel.setEscapeModelStrings(false);
        add(treeRingDataCombinedUploadWarningsLabel);

        treeRingDataCombinedUploadErrorsModel = new Model("");
        treeRingDataCombinedUploadErrorsLabel = new Label("values_combined_upload_errors", treeRingDataCombinedUploadErrorsModel);
        treeRingDataCombinedUploadErrorsLabel.setEscapeModelStrings(false);
        add(treeRingDataCombinedUploadErrorsLabel);

		treeRingDataFilesUploadedMessageModel = new Model("");
		treeRingDataFilesUploadedMessageLabel = new Label("value_files_uploaded_message", treeRingDataFilesUploadedMessageModel);
		treeRingDataFilesUploadedMessageLabel.setEscapeModelStrings(false);
		add(treeRingDataFilesUploadedMessageLabel);
		
		//--- Associated files	
		add(new AssociatedFilesUploadSectionPanel("associated_files_upload_section_panel"));	
		
		//---		
		
		// TODO: should construct a selection box for the fileformats
		// based on the possible formats from the Status (A POJO) instead
		// of having it hardcoded in the html, as it is now!
		// The actual selection should then be set in the onBeforeRender.
		final CombinedUploadStatus status = getStatus();

		// Start with the most important languages
		List<Locale> topLocales = LanguageProvider.getLocalesForAllOfficialEULanguages();
		// then the complete list (excluding the top ones!)
		List<Locale> allLocales = LanguageProvider.getLocalesForAllLanguagesExcluding(topLocales);
		List<Locale> locales = new ArrayList<Locale>();
		locales.addAll(topLocales);
		// How do we get a separator between the top and the rest?
		// we  make Locale that display's a separator and handle that bogus selection
		locales.add(new Locale("---"));
		// Note: you now need to filter out this selection on he client, JavaScript code
		locales.addAll(allLocales);
		// Note: we could make a class like LocaleDropDown,
		// but one that follows the curent Locale for the display values etc.
		DropDownChoice tridasLanguageSelection = new DropDownChoice("tridas_language",
				new PropertyModel(status, "tridasLanguage"),
				locales,
				new ChoiceRenderer("displayName", "language")
		);
		add(tridasLanguageSelection);

		List<TreeRingDataFileTypeSelection> list = status.getTreeRingDataFileTypeSelection().getSelectionList();
		// use the ChoiceRenderer
		// then the ajax get request also uses a 'readable' name instead of an index in the list
		ChoiceRenderer choiceRenderer = new ChoiceRenderer<TreeRingDataFileTypeSelection>("selection", "selection");
		fileTypeSelection = new DropDownChoice("measurements_filetype",
				new PropertyModel(status, "treeRingDataFileTypeSelection"),
				list,
				choiceRenderer
		);
		add(fileTypeSelection);

        // add javascript libraries
	    add(HeaderContributor.forJavaScript(new ResourceReference(EasyUpload.class, "js/lib/json2.js")));
	    add(HeaderContributor.forJavaScript(new ResourceReference(EasyUpload.class, "js/lib/jquery-1.3.2.min.js")));
	    // for getting at the upload event handlers
	    add(HeaderContributor.forJavaScript(new ResourceReference(EasyUpload.class, "js/EasyUpload.js")));

	    // for the combined upload
	    add(HeaderContributor.forJavaScript(new ResourceReference(UploadFilesPage.class, "CombinedUpload.js")));
	    IModel variablesModel = new AbstractReadOnlyModel() {
			private static final long serialVersionUID = -7833455309187754952L;
			public Map<String, CharSequence> getObject() {
        	    Map<String, CharSequence> variables = new HashMap<String, CharSequence>(1);
                ResourceReference uploadStatusRef = new ResourceReference(CombinedUploadStatusCommand.RESOURCE_NAME);
                variables.put("combinedUploadStatusRequestURL", getPage().urlFor(uploadStatusRef));
                return variables;
        	  }
        };
        add(TextTemplateHeaderContributor.forJavaScript(UploadFilesPage.class, "CombinedUploadConfig.js", variablesModel));

    	uploadHintsModel = new Model("");
    	uploadHintsLabel = new Label("upload_hints", uploadHintsModel);
    	uploadHintsLabel.setEscapeModelStrings(false);
        add(uploadHintsLabel);
		
		// Actions (Buttons)
        /*
		backButton = new SubmitLink("back_button")
		{
			private static final long serialVersionUID = -8981750027400450695L;
			final String backMessage = "Do you really want to go back to the start of the upload procedure? " + 
			   "This will discard the files you uploaded.";   

			@Override
			public void onSubmit()
			{
				// logger.debug("Back onSubmit is called");
				back();
			}

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				String onclick = tag.getAttributes().getString("onclick");
				// Note when there is uploaded data to be discarded, we have readyToCancel==true
				final String jsString = 
					"if(g_combinedUploadStatus.readyToCancel){" + 
					 "if(!confirm('" + backMessage + "')) return false;" +
					 "else g_canBrowseaway=true;" +
				    "}else g_canBrowseaway=true;";					
				onclick = jsString  + onclick;
				tag.getAttributes().put("onclick", onclick);
			}
		};
		form.add(backButton);
        */
        
		//cancelButton = new Button("combined_cancel_button", new ResourceModel("combined_cancel_button"))
		cancelButton = new SubmitLink("combined_cancel_button")
		{
			private static final long serialVersionUID = 2646137560758648463L;
			final String cancelMessage = "Do you really want to restart this step of the the upload procedure? "+ 
			 "This will discard the files you uploaded.";		

			@Override
			public void onSubmit()
			{
				logger.debug("Cancel onSubmit is called");
				cancel();
			}

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				String onclick = tag.getAttributes().getString("onclick");
				final String jsString = 
					"if(g_combinedUploadStatus.readyToCancel){" + 
					 "if(!confirm('" + cancelMessage + "')) return false;" +
					 "else g_canBrowseaway=true;" +
				    "}else g_canBrowseaway=true;";					
				onclick = jsString  + onclick;
				tag.getAttributes().put("onclick", onclick);
			}
		};
		cancelButton.setOutputMarkupId(false);
		form.add(cancelButton);
		
		//finish_and_upload_button
		finishAndUploadButton = new SubmitLink("finish_and_upload_button")
		{
			private static final long serialVersionUID = -5706070001789062239L;

			@Override
			public void onSubmit()
			{
				//logger.debug("finishAndUploadButton onSubmit is called");
				// don't browse away but reset and be ready to upload again!				
				finish();
			}

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				String onclick = tag.getAttributes().getString("onclick");
				// disable the link when not ready to finish
				onclick = "if(g_combinedUploadStatus.readyToFinish)g_canBrowseaway=true;else return false; " + onclick;
				tag.getAttributes().put("onclick", onclick);
			}
		};
		finishAndUploadButton.setOutputMarkupId(false);
		form.add(finishAndUploadButton);
		
		finishButton = new SubmitLink("finish_button")
		{
			private static final long serialVersionUID = -5706070001789062239L;

			@Override
			public void onSubmit()
			{
				// logger.debug("Finish onSubmit is called");
				finish();
				
				// Redirect to the projects view page...
				//setResponsePage(new ProjectsResultPage());
				// My Projects 
				// TODO go to the project
				// get Projects from CombinedUpload and then if there is only one, go to the view page?
				//setResponsePage(new MyProjectsPage());
				setResponsePage(new MyProjectsSearchResultPage());				
			}

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				String onclick = tag.getAttributes().getString("onclick");
				// disable the link when not ready to finish
				onclick = "if(g_combinedUploadStatus.readyToFinish)g_canBrowseaway=true;else return false; " + onclick;
				tag.getAttributes().put("onclick", onclick);
			}
		};
		finishButton.setOutputMarkupId(false);
		form.add(finishButton);

		// Note: browsing/navigating away while there is uploaded data
		// is handled by a JavaScript window.onbeforeunload handler
	}

	// Cancel upload
	private void cancel()
	{
		// reset this page to it's initial state
		reset();
	}

	// Finish upload
	private void finish()
	{
		// GUI design:
		// Only available if there are no warnings above
		// Alert if user specified a file (using browse), but forgot to upload
		// it
		// The project(s) is/are actually created (will appear in My projects).
		// Leads to MyProjectsPage (in case uploadtype multiple TRiDaS) or
		// leads to ProjectPage (edit) otherwise.

		// should check if we can proceed:
		// - no upload going on, and we have all data
		// if so: store all uploaded data into repository
		// and navigate to the next page (not implemented now)

		logger.info("Number of Tridas files: " + getCombinedUpload().getNumProjects());

		//logger.info("Number of Treering Data  files: " + getTreeRingDataList().size());
		logger.info("Number of Treering Data files: " + getCombinedUpload().getNumTreeRingData());

		// Store...
		try {
			store();
		} catch (CombinedUploadStoreException e) {
			// go to the error page!
			throw new RestartResponseException(ErrorPage.class);
		}

		reset(); // needed! we have only one CombinedUpload per session
	}

	// Go back to intro upload page
	private void back()
	{
		// cancel all ongoing uploads, discard all data
		reset();

		// go back to the intro page
        // get the previous page, and try to go back
        Page page = ((DccdSession)Session.get()).getRedirectPage(UploadFilesPage.class);
        if (page != null && (page instanceof UploadIntroPage))
        {
        	setResponsePage(page);
        }
        else
        {
        	// just go back to a new instance of UploadIntroPage
        	setResponsePage(new UploadIntroPage());
        }
	}
}
