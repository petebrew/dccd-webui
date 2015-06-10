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
package nl.knaw.dans.dccd.web.download;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import nl.knaw.dans.common.lang.dataset.DatasetState;
//import nl.knaw.dans.common.lang.file.ZipFileItem;
import nl.knaw.dans.common.lang.file.ZipItem;
import nl.knaw.dans.common.lang.file.ZipUtil;
import nl.knaw.dans.common.lang.util.FileUtil;
import nl.knaw.dans.dccd.application.services.DataServiceException;
import nl.knaw.dans.dccd.application.services.DccdDataService;
import nl.knaw.dans.dccd.application.services.TreeRingDataFileService;
import nl.knaw.dans.dccd.application.services.TreeRingDataFileServiceException;
import nl.knaw.dans.dccd.model.AbstractDccdFileBinaryUnit;
import nl.knaw.dans.dccd.model.DccdAssociatedFileBinaryUnit;
import nl.knaw.dans.dccd.model.DccdOriginalFileBinaryUnit;
import nl.knaw.dans.dccd.model.DccdTreeRingData;
import nl.knaw.dans.dccd.model.DccdUser;
import nl.knaw.dans.dccd.model.Project;
import nl.knaw.dans.dccd.repository.xml.TridasSaveException;
import nl.knaw.dans.dccd.repository.xml.XMLFilesRepositoryService;
import nl.knaw.dans.dccd.tridas.TridasNamespacePrefixMapper;
import nl.knaw.dans.dccd.util.StringUtil;
import nl.knaw.dans.dccd.web.DccdSession;
import nl.knaw.dans.dccd.web.base.BasePage;
import nl.knaw.dans.dccd.web.error.ErrorPage;

import org.apache.log4j.Logger;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.target.resource.ResourceStreamRequestTarget;
import org.apache.wicket.util.resource.FileResourceStream;

/**
 * For downloading the files for a given project TODO: put all download file construction/convertion stuff in a separate class downloadService?
 * 
 * @author paulboon
 */
public class DownloadPage extends BasePage
{
	private static Logger			logger							= Logger.getLogger(DownloadPage.class);
	public static final String		TRIDAS_XML_CHARSET				= "UTF-8";								// maybe even on a global level?
	DownloadValuesSelection			downloadValuesSelection			= new DownloadValuesSelection();
	DownloadFileInclusionSelection	downloadFileInclusionSelection	= new DownloadFileInclusionSelection();
	DownloadAcceptLicenseSelection	downloadAcceptLicenseSelection	= new DownloadAcceptLicenseSelection();
	Button							downloadButton;
	Project							project;

	/**
	 * Construct page for download of given project
	 * 
	 * @param project
	 *        The project (data) to download
	 */
	public DownloadPage(Project project)
	{
		if (project == null)
			throw new IllegalArgumentException("Missing project");
		this.project = project;

		initPage();
	}
	
	/**
	 * Add all the Wicket components
	 */
	private void initPage()
	{
		DccdUser user = (DccdUser) ((DccdSession) getSession()).getUser();
		if (!project.isDownloadAllowed(user)) 
		{
			logger.error("Denied downloading project [" + project.getSid()+ 
					"] for user [" + user.getId() + "]");
			throw new RestartResponseException(ErrorPage.class);
		}
		
		// Maybe use a panel for the project info,
		// use SearchResultItemPanel as inspiration
		// TridasProject tridasProject = project.getTridas();
		String projectTitleStr = project.getTitle();// tridasProject.getTitle();
		add(new Label("project_title", projectTitleStr));

		Form form = new Form("form")
		{
			private static final long	serialVersionUID	= 6525777647823238585L;

			@Override
			protected void onSubmit()
			{
				// called when one of the buttons is clicked, but after the
				// button's onSubmit call
				logger.info("Form onSubmit is called");

				// show all ...
				logger.debug("Download Values Selection: " + downloadValuesSelection.getSelection());

				logger.debug("Download File Inclusion: ");
				logger.debug("  originalValuesFiles: " + downloadFileInclusionSelection.isOriginalValuesFiles());
				logger.debug("  associatedFiles: " + downloadFileInclusionSelection.isAssociatedFiles());
				logger.debug("  dccdAdminstrativeData: " + downloadFileInclusionSelection.isDccdAdminstrativeData());
				logger.debug("  dccdUsageComments: " + downloadFileInclusionSelection.isDccdUsageComments());

				logger.debug("Download accept license: " + downloadAcceptLicenseSelection.isAccepted());

				// when OK, we could contruct the file and start downloading it...
				if (!downloadAcceptLicenseSelection.isAccepted())
				{
					logger.warn("Download request ignored, because license was not accepted");
				}
				else
				{
					// Ok, continue
					logger.debug("Staring download...");
					download();
				}
			}
		};
		add(form);

		// Note: could check if Project has data, and if not
		// set fixed to 'none' and disable selection
		form.add(new DownloadValuesSelectionPanel("values_selection_panel", new Model(downloadValuesSelection)));

		// conversion warning
		Label conversionWarning = new Label("conversionWarning", new ResourceModel("conversionWarning_message"));
		conversionWarning.setEscapeModelStrings(false); // allow html
		if (project.getAdministrativeMetadata().getAdministrativeState() != DatasetState.DRAFT)
			conversionWarning.setVisible(false);
		form.add(conversionWarning);

		form.add(new DownloadFileInclusionSelectionPanel("file_inclusion_panel", new Model(downloadFileInclusionSelection)));

		// submit_download
		downloadButton = new Button("submit_download", new ResourceModel("submit_download_buttontext"));
		downloadButton.setOutputMarkupId(true);
		downloadButton.setEnabled(false); // disabled by default!
		form.add(downloadButton);
		// Note: button should react on license accept by AJAX

		form.add(new DownloadAcceptLicenseSelectionPanel("license_accept_panel", new Model(downloadAcceptLicenseSelection))
		{
			private static final long	serialVersionUID	= -8670768055531739942L;

			@Override
			protected void onAcceptChange(AjaxRequestTarget target, boolean accepted)
			{
				logger.debug("Overridden!");
				downloadButton.setEnabled(accepted);
				if (target != null)
				{
					target.addComponent(downloadButton); // downloadButton
				}
			}
		});

		form.add(new FeedbackPanel("feedback"));
	}

	/**
	 * Keep track if the license has been accepted or not
	 */
	public class DownloadAcceptLicenseSelection implements Serializable
	{
		private static final long	serialVersionUID	= -3326823575185401960L;
		private boolean				accepted			= false;

		public boolean isAccepted()
		{
			return accepted;
		}

		public void setAccepted(boolean accepted)
		{
			this.accepted = accepted;
		}

	}

	/**
	 * produce zip file and start it's download get all measurement series then convert them to DccdTreeRingData save to the specified fileformat and create a
	 * zip with everything in it.
	 */
	private void download()
	{
		// make sure we have the whole project,
		// because it might be only partially loaded
		try
		{
			// NOTE for refactoring; we only need an StoreId, not a whole project
			project = DccdDataService.getService().getProject(project.getStoreId());
		}
		catch (DataServiceException e)
		{
			e.printStackTrace();
			error(e.getMessage());
		}

		// check if we have what we want
		if (project == null || project.entityTree == null)
		{
			logger.warn("Nothing to download");
			return;
		}

		// get a temporary path/filename to use
		String basePath = System.getProperty("java.io.tmpdir");
		final File basePathFile;
		// make sure file is on disk and in the right location
		try
		{
			basePathFile = FileUtil.createTempDirectory(new File(basePath), "download");
			logger.debug("Created temp dir for download: " + basePathFile.getAbsolutePath());
		}
		catch (IOException e)
		{
			logger.error("Cancelling download; Could not create temp directory at: " + basePath);
			// go to the error page!
			throw new RestartResponseException(ErrorPage.class);
		}

		List<ZipItem> zipItems = new ArrayList<ZipItem>();

		// add tridas to the zip
		zipItems.add(getTridasZipItem(basePathFile));

		// get type of data
		String type = downloadValuesSelection.getSelection();

		// add the treering data if needed
		if (type != DownloadValuesSelection.SELECT_NONE)
		{
			String format = type;
			zipItems.addAll(getTreeRingDataZipItems(format, basePathFile));
		}

		// add original files
		if (downloadFileInclusionSelection.isOriginalValuesFiles())
		{
			zipItems.addAll(getOriginalFilesZipItems());
		}

		// add associated files
		if (downloadFileInclusionSelection.isAssociatedFiles())
		{
			zipItems.addAll(getAssociatedFilesZipItems());
		}

		// --- Construct the zip file and have it downloaded
		// Construct the filename
		String zipFileName = nl.knaw.dans.dccd.util.FileUtil.getSaveFilename(project.getTitle() + ".zip") ;
		File zipFilePath = new File(basePathFile.getAbsolutePath() + 
				File.separatorChar + zipFileName); // wrap name in File object

		try
		{
			// final File zipFile = ZipUtil.zipEasyFiles(zipFilePath, "ignored", zipItems);
			final File zipFile = ZipUtil.zipFiles(zipFilePath, zipItems);

			// after the zip file has bee downloaded we need to cleanup
			// by deleting the complete temp folder, also containing the zip file
			// Or some reason the FileResourceStream.close() was never called,
			// therfore the ResourceStreamRequestTarget.detach() is overriden

			ResourceStreamRequestTarget target = new ResourceStreamRequestTarget(new FileResourceStream(zipFile))
			{

				@Override
				public void detach(RequestCycle requestCycle)
				{
					super.detach(requestCycle);
					// System.out.println("DETACH ResourceStreamRequestTarget");
					cleanupDownload(basePathFile);
				}
			};

			target.setFileName(zipFile.getName());
			RequestCycle.get().setRequestTarget(target);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new RestartResponseException(ErrorPage.class);
		}
		finally
		{
			// cleanup all files
			// cleanupDownload(basePathFile);
			logger.debug("Download done!");
		}
	}

	/**
	 * Deletes the temp folder with all files being zipped and the resulting zip as well
	 * 
	 * @param basePathFile
	 */
	private void cleanupDownload(File basePathFile)
	{
		// Always try to delete the temporary files
		try
		{
			FileUtil.deleteDirectory(basePathFile);
			logger.info("Temp File(s) deleted");
		}
		catch (IOException e)
		{
			logger.error("Could not cleanup temporary download files at: " + basePathFile.getAbsolutePath());
			e.printStackTrace();
			// But don't throw an exception !
		}
	}

	private List<ZipItem> getOriginalFilesZipItems()
	{
		List<ZipItem> zipItems = new ArrayList<ZipItem>();

		// get the URL for the Original files and construct the zipItem
		List<DccdOriginalFileBinaryUnit> originalFileBinaryUnits = project.getOriginalFileBinaryUnits();
		for (DccdOriginalFileBinaryUnit unit : originalFileBinaryUnits)
		{
			zipItems.add(getZipItemFromDccdFileBinaryUnit(unit, "original"));
		}

		return zipItems;
	}

	private List<ZipItem> getAssociatedFilesZipItems()
	{
		List<ZipItem> zipItems = new ArrayList<ZipItem>();
		
		// get the URL for the Original files and construct the zipItem
		List<DccdAssociatedFileBinaryUnit> originalFileBinaryUnits = project.getAssociatedFileBinaryUnits();
		for (DccdAssociatedFileBinaryUnit unit : originalFileBinaryUnits)
		{
			ZipItem item = getZipItemFromDccdFileBinaryUnit(unit, "associated");
			zipItems.add(item);
		}

		return zipItems;
	}

	private ZipItem getZipItemFromDccdFileBinaryUnit(AbstractDccdFileBinaryUnit unit, String label)
	{
		String fileName = unit.getFileName();
		// We have the 'label' placed before the extension dot
		// Alternatively we could place files in a subfolder?
//		int dotPos = fileName.lastIndexOf('.');
//		fileName = fileName.substring(0, dotPos) + "_" + label + fileName.substring(dotPos);
		
		// Place in subfolder
		fileName = label + File.separator + fileName;

		String id = unit.getUnitId();
		// get the url
		URL fileURL = DccdDataService.getService().getFileURL(project.getSid(), id);
		logger.debug("URL: " + fileURL);

//		ZipFileItem zipItem = new ZipFileItem(fileName, fileURL);
		ZipItem zipItem = new ZipItem(fileName, fileURL);
		return zipItem;
	}

	private List<ZipItem> getTreeRingDataZipItems(String format, File basePathFile)
	{
		List<ZipItem> zipItems = new ArrayList<ZipItem>();

		if (project == null || project.getTridas() == null)
		{
			logger.warn("No data");
			return zipItems; // just an empty list
		}

		// construct the data to save
		DccdTreeRingData data = new DccdTreeRingData();
		data.setTridasProject(project.getTridas());

		// save the files to the given folder
		// note that it can be several files...and not only one
		// so the filename of DccdTreeRingData is not used
		final String subDirName = "values";
		try
		{
			List<String> filenames = TreeRingDataFileService.save(data, basePathFile, format);
			
			// Note: 
			// when filenames is empty, maybe show a message when there was nothing saved, 
			// because conversion not possible?
			
			logger.debug("Saved " + filenames.size() + " files to: [" + basePathFile.getPath() + "] with format: " + format);
			
			// now we have to "read" all those files and make ZipFileItems of them
			for (String filename: filenames)
			{
				File file = new File(basePathFile.getPath() + File.separatorChar + filename);

				logger.debug("zipping file: [" + file.getName() + "]");
				
				// Clean the filename, because when the TRiDaS titles contain newlines 
				// the conversion lib copies them into the filename as well
				String cleanFilename = StringUtil.cleanWhitespace(file.getName());

				// Place in subfolder
				String fileNameInZip = subDirName + File.separator + cleanFilename;
				
				// Put it in a zip
//				ZipFileItem zipItem = new ZipFileItem(fileNameInZip, file);
				ZipItem zipItem = new ZipItem(fileNameInZip, file);
				zipItems.add(zipItem);
			}
		}
		catch (TreeRingDataFileServiceException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return zipItems;
	}
	
	/**
	 * Produces the TriDaS file item for zipping Converts the project to TRiDaS xml file
	 * 
	 * @param basePathFile
	 * @return
	 */
	private ZipItem getTridasZipItem(File basePathFile)
	{
		File file = new File(basePathFile.getAbsolutePath() + File.separatorChar + 
				XMLFilesRepositoryService.constructTridasFilename(project));

		try
		{
			XMLFilesRepositoryService.saveToTridasXML(file, project);
		}
		catch (TridasSaveException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		ZipFileItem zipItem = new ZipFileItem(file.getName(), file);
		ZipItem zipItem = new ZipItem(file.getName(), file);
		return zipItem;
	}
}
