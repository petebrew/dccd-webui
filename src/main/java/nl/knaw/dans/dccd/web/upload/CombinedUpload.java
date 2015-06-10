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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import nl.knaw.dans.common.lang.util.FileUtil;
import nl.knaw.dans.dccd.application.services.DataServiceException;
import nl.knaw.dans.dccd.application.services.DccdDataService;
import nl.knaw.dans.dccd.application.services.TreeRingDataFileService;
import nl.knaw.dans.dccd.model.DccdTreeRingData;
import nl.knaw.dans.dccd.model.EntityTree;
import nl.knaw.dans.dccd.model.Project;
import nl.knaw.dans.dccd.model.ProjectAssociatedFileDetector;
import nl.knaw.dans.dccd.model.ProjectTreeRingDataFileDetector;
import nl.knaw.dans.dccd.model.entities.DerivedSeriesEntity;
import nl.knaw.dans.dccd.model.entities.Entity;
import nl.knaw.dans.dccd.model.entities.MeasurementSeriesEntity;
import nl.knaw.dans.dccd.model.entities.ValuesEntity;
import nl.knaw.dans.dccd.util.CaseInsensitiveComparator;
import nl.knaw.dans.dccd.util.StringUtil;

import org.apache.log4j.Logger;
import org.tridas.interfaces.ITridasSeries;
import org.tridas.schema.TridasDerivedSeries;
import org.tridas.schema.TridasGenericField;
import org.tridas.schema.TridasMeasurementSeries;
import org.tridas.schema.TridasValue;
import org.tridas.schema.TridasValues;

/**
 * Manages the information about the combined upload
 * of Tridas, measurements and associated files
 *
 * Refactoring note:
 * If the warnings need more work on the rendering; make UploadWarnings into an enum or class
 * and keep List<UploadWarning>
 *
 * @author paulboon
 *
 */
public class CombinedUpload implements Serializable
{
	private static final long serialVersionUID = 8688138305422723358L;
	private static Logger logger = Logger.getLogger(CombinedUpload.class);

	private Properties properties; // used for the message strings
	// tridas
	private TridasUploadType uploadType = TridasUploadType.TRIDAS_WITH_DATA; // default
	private List<Project> projectList = new ArrayList<Project>(); //empty
	private Locale tridasLanguage = Locale.ENGLISH;
	private String tridasUploadWarnings = null;
	// treering data
	private List<DccdTreeRingData> treeRingDataList = new ArrayList<DccdTreeRingData>(); // empty
	// assume we have at least one format, and use the first one in the list
	// TODO initialize elsewhere	
	private String formatString = TreeRingDataFileService.getReadingFormats().get(0);
	private String treeRingDataUploadWarnings = null;
	// associated files
	List<File> associatedFiles =  new ArrayList<File>(); //empty
	private String associatedFilesUploadWarnings = null;
	// original files
	private File orgfilesTempDirectory = null;

	public CombinedUpload()
	{
		loadProperties();
	}
	
	public File getOrgfilesTempDirectory()
	{
		final String FOLDER_PREFIX = "dccd_upload_original_files";
		
		if (orgfilesTempDirectory == null)
		{
			// construct tmp folder nane
			// for storing original files
			// get the system temp folder
			String tempDir = System.getProperty("java.io.tmpdir");
			//String orgFilesDir = tempDir + File.pathSeparator + 
			//					"dccd-upload-original-files-"+ Session.get().getId();
			//?
			// make sure file is on disk and in the right location
			
			try
			{
				orgfilesTempDirectory = FileUtil.createTempDirectory( new File(tempDir), FOLDER_PREFIX);
			}
			catch (IOException e)
			{
				logger.error("Could not create folder for original files", e);
				// What to do about it?
			}
			
			logger.info("using temp dir for original files: " + orgfilesTempDirectory.getPath());
		}
		
		return orgfilesTempDirectory;
	}
	
	public void addOriginalFiles(List<File> files)
	{
		for(File file: files)
		{
			addOriginalFile(file);
		}
	}

	public File findOriginalFile(String filename)
	{
		File fileFound = null;
		File[] listFiles = orgfilesTempDirectory.listFiles();
		for(int i = 0; i < listFiles.length; i++)
		{
			if(listFiles[i].getName().compareToIgnoreCase(filename) == 0) 
			{
				fileFound = listFiles[i];
				break;
			}
		}

		return fileFound; 
	}
	
	// store in temp folder for later use when storing in repository
	public void addOriginalFile(File file)
	{
		File dstFolder = getOrgfilesTempDirectory();
		String dtsFilename = dstFolder.getPath() + File.separator + file.getName();
		String srcFilename = file.getPath();
		// copy, with overwriting ?
		try
		{
			nl.knaw.dans.dccd.util.FileUtil.copyFile(srcFilename, dtsFilename);
			logger.debug("Copied original file to: " + dtsFilename);
			// could add it to a list, but we know that 
			// everything in the folder is an original 
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	/**
	 * Read message strings from xml properties files
	 * Note: we need this because the CombinedUpload is not a Wicket component
	 *
	 * Note2:
	 * Should also select the file based on the locale setting,
	 * possibly with common.lang.ResourceLocator
	 * BUT IT IS NOT!
	 * And then get the string for the messages from the properties,
	 * with keys like "no_files_uploaded" etc.
	 *
	 * Also when the language changes during a session, we should reload the properties
	 *
	 */
	private void loadProperties()
	{
		properties = new Properties();
		try
		{
		    InputStream inStream = getClass().getResourceAsStream("CombinedUpload.properties");//"CombinedUpload.xml");
		    if( inStream == null )
		    {
		         //TODO: throw some error
		    }
		    //properties.loadFromXML(inStream);
		    properties.load(inStream);
		}
		catch (IOException exception)
		{
		    ///TODO: throw exception
		}
	}

	/**
	 * Remove all data and start fresh
	 */
	public synchronized void reset()
	{
		cleanupFiles();
		
		// clear the lists with uploaded information
		projectList.clear();
		treeRingDataList.clear();
		associatedFiles.clear();
		
		// clear warnings
		clearTridasUploadWarnings();
		clearTreeRingDataUploadWarnings();
		clearAssociatedFilesUploadWarnings();
	}

	//--- TreeRingDataUploadWarnings
	
	public String getTreeRingDataUploadWarnings()
	{
		if (treeRingDataUploadWarnings == null)
			return "";
		else
			return treeRingDataUploadWarnings;
	}

	public void setTreeRingDataUploadWarnings(String warnings)
	{
		treeRingDataUploadWarnings = warnings;
	}

	public void appendTreeRingDataUploadWarnings(String warnings)
	{
		if (hasTreeRingDataUploadWarnings())
			treeRingDataUploadWarnings.concat("<br/>" + warnings);
		else
			setTreeRingDataUploadWarnings(warnings);
	}

	public void clearTreeRingDataUploadWarnings()
	{
		treeRingDataUploadWarnings = null;
	}

	public boolean hasTreeRingDataUploadWarnings()
	{
		return treeRingDataUploadWarnings != null;
	}

	//--- AssociatedFilesUploadWarnings

	public String getAssociatedFilesUploadWarnings()
	{
		if (associatedFilesUploadWarnings == null)
			return "";
		else
			return associatedFilesUploadWarnings;
	}

	public void setAssociatedFilesUploadWarnings(String warnings)
	{
		associatedFilesUploadWarnings = warnings;
	}

	public void appendAssociatedFilesUploadWarnings(String warnings)
	{
		if (hasAssociatedFilesUploadWarnings())
			associatedFilesUploadWarnings.concat("<br/>" + warnings);
		else
			setAssociatedFilesUploadWarnings(warnings);
	}

	public void clearAssociatedFilesUploadWarnings()
	{
		associatedFilesUploadWarnings = null;
	}

	public boolean hasAssociatedFilesUploadWarnings()
	{
		return associatedFilesUploadWarnings != null;
	}

	//--- TridasUploadWarnings
	
	private void clearTridasUploadWarnings()
	{
		tridasUploadWarnings=null;
	}

	public boolean hasTridasUploadWarnings()
	{
		return tridasUploadWarnings != null;
	}

	public String getTridasUploadWarnings()
	{
		if (tridasUploadWarnings == null)
			return "";
		else
			return tridasUploadWarnings;
	}

	public void setTridasUploadWarnings(String warnings)
	{
		tridasUploadWarnings = warnings;
	}
	
	public Locale getTridasLanguage()
	{
		return tridasLanguage;
	}

	public void setTridasLanguage(Locale tridasLanguage)
	{
		this.tridasLanguage = tridasLanguage;
	}

	public TridasUploadType getUploadType()
	{
		return uploadType;
	}

	public void setUploadType(TridasUploadType uploadType)
	{
		this.uploadType = uploadType;
	}

	public boolean hasProject()
	{
		return (!projectList.isEmpty());
	}

	public Project getProject()
	{
		if (!projectList.isEmpty())
			return projectList.get(0);
		else
			return null;
	}
	
	/**
	 * Adds new project, forcing to only one by removing any previously added
	 */
	public synchronized void addProject(Project project)
	{
		// assume one and only one project allowed
		projectList.clear();
		clearTridasUploadWarnings(); // maybe we should do this elsewhere

		// check for multiple references in the tridas
		// not efficient... because we call getProjectExternalDataFileNames again!
		List<String> duplicates = StringUtil.getDuplicatesIgnoreCase(ProjectTreeRingDataFileDetector.getProjectTreeRingDataFileNames(project));		
		if (duplicates.size() == 0)
		{
			// OK
			logger.debug("Project added to the upload");
			projectList.add(project); // last one survives!
		}
		else
		{
			// we have duplicates, construct a "Double reference warning"
			String duplicatesString = StringUtil.constructCommaSeparatedString(duplicates);
			String warning =
			"File '" +
			project.getFileName() +
			"' was not uploaded, because contains more than one reference to the file(s) '" +
			duplicatesString +
			"'. Please correct the file and upload it again.";
			setTridasUploadWarnings(warning);

			logger.debug("uploaded tridas was discarded, because of duplicate references to treering data files");
		}
	}

	/**
	 * Add a new tree ring dataset, and takes care of duplicates
	 *
	 * @param data
	 */
	public synchronized void addDccdTreeRingData(DccdTreeRingData data)
	{
		// allow endless growing...

		DccdTreeRingData foundData = null;
		// first check if we have one with the same filename!
		for(DccdTreeRingData addedData : treeRingDataList)
		{
			if (addedData.getFileName().compareToIgnoreCase(data.getFileName()) == 0)
			{
				// found
				foundData = addedData;
				// assume adding is always done with this function
				// and there are no more items with the same filename
				break;
			}
		}
		// remove the one with the same filename (if found)
		if (foundData != null)
		{
			//File '<filename>' was already uploaded; the previous version was overwritten.
			String warning = "File '" +
			data.getFileName() +
			"' was already uploaded; the previous version was overwritten.";
			appendTreeRingDataUploadWarnings(warning);
			treeRingDataList.remove(foundData);

			logger.info(warning);
		}

		treeRingDataList.add(data);
	}

	//--- Associated files
	
	public synchronized void addAssociatedFiles(List<File> files)
	{
		clearAssociatedFilesUploadWarnings();
		
		if (!hasProject())
		{
			logger.warn("Failed to add associated files, because there was no tridas project uploaded");
			return; 
		}
		List<String> projectAssociatedFileNames = ProjectAssociatedFileDetector.getProjectAssociatedFileNames(projectList.get(0));
		
		// need to sort first for binarySearch...
		Collections.sort(projectAssociatedFileNames, new CaseInsensitiveComparator());
		logger.debug("allowed: " + projectAssociatedFileNames);

		for (File file : files)
		{
			logger.debug("Associated file: \"" + file.getName() + "\"");
			//logger.debug("\t at: \"" + file.getAbsolutePath() + "\"");
			
			// TODO check if file is wanted
			//projectAssociatedFileNames.
			int index = Collections.binarySearch(projectAssociatedFileNames, file.getName(), new CaseInsensitiveComparator());	
			
			if (index < 0)
			{
				// not found, so discard
				logger.debug("associated file - NOT FOUND");
				// TODO add a warning, for discarding this file
				// ??? Maybe do this in getStatus ???
			}
			else
			{
				// OK, we found it
				logger.debug("associated file - FOUND");
			
				// first check if not allready there
				// do we have a file with the same name
				File fileWithSameName = null;
				for (File associatedFile : associatedFiles)
				{
					if (associatedFile.getName().compareToIgnoreCase(file.getName())== 0)
					{
						// found file with same name
						fileWithSameName = associatedFile;
						break;
					}
				}
				// if found: delete and remove from the list
				if (fileWithSameName != null)
				{
					logger.debug("Replacing previously uploaded file: " + fileWithSameName.getName());
					
					fileWithSameName.delete();
					associatedFiles.remove(fileWithSameName);
					// TODO add a warning, for double upload and discarding the previous version
					String warning = "File '" +
					file.getName() +
					"' was already uploaded; the previous version was overwritten.";
					appendAssociatedFilesUploadWarnings(warning);
				}
				
				associatedFiles.add(file);
			}
		}
	}
	
	private List<String> getNamesOfAssociatedFilesNotUploaded()
	{
		// Make sure we have a project
		if (!hasProject()) 
		{
			logger.warn("No project uploaded");
			return Collections.emptyList();
		}
		
		List<String> resultList = ProjectAssociatedFileDetector.getProjectAssociatedFileNames(getProject());
		Collections.sort(resultList, new CaseInsensitiveComparator());// for binarySearch
		
		// remove all instances we already have!
		for(File file : associatedFiles)
		{
			int index = Collections.binarySearch(resultList, file.getName(), new CaseInsensitiveComparator());
			if (index >= 0)
			{
				resultList.remove(index); // note that list remains sorted
			}
		}		
		
		return resultList;
	}
	
	// Note the construction of the file list could be made more general
	// might then also be used in the Search result?
	private String getAssociatedFilesUploadedMessage()
	{
		if (associatedFiles.isEmpty())
			return "";

		final int NUMBER_OF_FILES_TO_TRUNCATE_TO = Integer.MAX_VALUE;// practically disabling truncation, was 10;
		final String TRUNCATION_INDICATOR = "...";

		// just a comma separated list
		StringBuilder sbFiles = new StringBuilder();

		for (int i=0; i < associatedFiles.size(); i++)
		{
			if (sbFiles.length() > 0) sbFiles.append(", ");

			// truncate if needed at 10 files
			if (i >= NUMBER_OF_FILES_TO_TRUNCATE_TO) {
				sbFiles.append(TRUNCATION_INDICATOR);
				break;
			}

			File file = associatedFiles.get(i);
			String filename = file.getName();
			sbFiles.append(filename);
		}
		return (String)properties.getProperty("currentfilesLabel") + sbFiles.toString();
	}
	
	//---
	
	public synchronized int getNumProjects()
	{
		return projectList.size();
	}

	public synchronized int getNumTreeRingData()
	{
		return treeRingDataList.size();
	}

	public synchronized String getFormat()
	{
		return formatString;
	}

	public synchronized void setFormat(String formatString)
	{
		this.formatString = formatString;
	}

	/**
	 * Checks the combination of the Tridas (Project) with the treering data files
	 * Used for updating the status.
	 *
	 * Strategy for getting th right messages:
	 *  Create two lists:
	 *  A) all valuefiles that tridas wants
	 *  B) all value files geupload
	 * Then remove corresponding items from both lists;
	 * uploaded files that are also frfered to by the tridas
	 * What remains:
	 * A^) valuefiles needed but not uploaded yet
	 * B^) value files uploaded but not needed (no ref. from tridas)
	 *
	 * @param fileNamesNeeded An output parameter!,
	 * after the call it contains the filenames still needed
	 * @param fileNamesLoaded An output parameter!,
	 * after the call it contains the filenames loaded but not found in the tridas
	 *
	 * @return true if combining is possible, false otherwise
	 */
	private boolean checkCombination(List<String> fileNamesNeeded,
									 List<String> fileNamesLoaded)
	{
		fileNamesNeeded.clear();
		if (hasProject())
		{
			// just the first one, for now we only have one!
			fileNamesNeeded.addAll(ProjectTreeRingDataFileDetector.getProjectTreeRingDataFileNames(getProject()));		
		}

		// build list of filenames
		fileNamesLoaded.clear();
		for (DccdTreeRingData data : treeRingDataList)
		{
			fileNamesLoaded.add(data.getFileName());
		}

		// for each filename see if there is an uploaded treering data file
		// Note: must use iterators for removing elements inside the loop
		Iterator<String> i_needed = fileNamesNeeded.iterator();
		while (i_needed.hasNext())
		{
			String nameNeeded = i_needed.next();
			// find it in the uploaded list
			Iterator<String> i_loaded = fileNamesLoaded.iterator();
			while (i_loaded.hasNext())
			{
				String nameLoaded = i_loaded.next();
				if (nameNeeded.compareToIgnoreCase(nameLoaded) == 0)
				{
					// found!
					// remove both
					i_loaded.remove();
					i_needed.remove();
					break;
				}
			}
		}

		// what is left in the list, did not match!
		// - fileNamesNeeded = files not uploaded
		// - fileNamesLoaded = files not in tridas
		return (fileNamesNeeded.isEmpty() && fileNamesLoaded.isEmpty());
	}

	/**
	 * Use when you only want to know if it checks, and no futher information
	 * @return
	 */
	private boolean checkCombination()
	{
		//assume one and only one project
		if (!hasProject()) return false; // nothing to combine!

		List<String> treeRingDataFilesMissing = new ArrayList<String>();
		List<String> treeRingDataFilesUnNeeded = new ArrayList<String>();
		return checkCombination(treeRingDataFilesMissing, treeRingDataFilesUnNeeded);
	}

	// Just one file now, but later we could have batch upload
	// and then we need to handle possible large number of files
	private String getTridasFilesUploadedMessage()
	{
		if (!hasProject())
			return "";

		// just a comma separated list
		StringBuilder sbTridasFiles = new StringBuilder();
		for (Project project : projectList)
		{
			if (sbTridasFiles.length() > 0) sbTridasFiles.append(", ");
			String filename = project.getFileName();
			sbTridasFiles.append(filename);
		}
		return (String)properties.getProperty("currentfileLabel") + sbTridasFiles.toString();
	}

	private String getTreeRingDataFilesUploadedMessage()
	{
		if (treeRingDataList.isEmpty())
			return "";

		final int NUMBER_OF_FILES_TO_TRUNCATE_TO = Integer.MAX_VALUE;// practically disabling truncation, was 10;
		final String TRUNCATION_INDICATOR = "...";

		// just a comma separated list
		StringBuilder sbTreeRingDataFiles = new StringBuilder();

		for (int i=0; i < treeRingDataList.size(); i++)
		{

			if (sbTreeRingDataFiles.length() > 0) sbTreeRingDataFiles.append(", ");

			// truncate if needed
			if (i >= NUMBER_OF_FILES_TO_TRUNCATE_TO) {
				sbTreeRingDataFiles.append(TRUNCATION_INDICATOR);
				break;
			}

			DccdTreeRingData treeringdata = treeRingDataList.get(i);
			String filename = treeringdata.getFileName();
			sbTreeRingDataFiles.append(filename);
		}
		return (String)properties.getProperty("currentfilesLabel") + sbTreeRingDataFiles.toString();
	}

	/**
	 * Determine the current status of the combined upload;
	 * if we have everything to finish the upload and
	 * sets the messages (warnings, hints and errors)
	 *
	 * @return The status
	 */
	public synchronized CombinedUploadStatus getStatus()
	{
		CombinedUploadStatus status = new CombinedUploadStatus();

		// set the selected language and format
		status.setTridasLanguage(tridasLanguage);
		status.setSelectedFormat(formatString);
		status.setTridasUploadVisible(!hasProject()); // hide when we have a Project
		status.setTreeRingDataVisible(hasReferencesToTreeRingData()); // only show if we need treeRingData
		status.setAssociatedFilesVisible(hasReferencesToAssociatedFiles());
		
		// construct the filename lists for the uploaded Tridas files
		status.setTridasFilesUploadedMessage(getTridasFilesUploadedMessage());
		status.setTreeRingDataFilesUploadedMessage(getTreeRingDataFilesUploadedMessage());
		
		int numTridasFiles = projectList.size();
		int numTreeringFiles = treeRingDataList.size();
		boolean hasNoTreeringFiles = (numTreeringFiles == 0);
		boolean hasNoTridasFiles = (numTridasFiles == 0);
		boolean hasNothingUploaded = (hasNoTreeringFiles && hasNoTridasFiles);

		// we can only cancel if there is at least one file uploaded, no mater what type
		status.setReadyToCancel((numTridasFiles > 0 || numTreeringFiles > 0));

		// check if the files correspond and nothing is left to load
		List<String> treeRingDataFilesMissing = new ArrayList<String>();
		List<String> treeRingDataFilesUnNeeded = new ArrayList<String>();
		boolean canCombineWithTreeRingData = checkCombination(treeRingDataFilesMissing, treeRingDataFilesUnNeeded);
		boolean hasTreeRingDataFilesUnNeeded = (treeRingDataFilesUnNeeded.size() > 0);// loaded but not needed 
		boolean hasTreeRingDataFilesMissing = (treeRingDataFilesMissing.size() > 0);

		// generate the file lists strings for the messages
		String treeRingDataFilesMissingSummerasingString = StringUtil.constructCommaSeparatedString(treeRingDataFilesMissing);
		String treeRingDataFilesUnNeededSummarisingString = StringUtil.constructCommaSeparatedString(treeRingDataFilesUnNeeded);

		if (hasTreeRingDataFilesUnNeeded)
		{
			logger.debug("Removing unneeded data...");
			removeTreeRingDataFiles(treeRingDataFilesUnNeeded);
			// update the list of files uploaded
			status.setTreeRingDataFilesUploadedMessage(getTreeRingDataFilesUploadedMessage());
			// append Unexpected files warning, uploaded but not referenced in TRiDaS
			String unexpectedMsg =(String)properties.getProperty("treering_data_discarded") +
			"<br/>"+
			treeRingDataFilesUnNeededSummarisingString +
			"<br/>"+
			(String)properties.getProperty("treering_data_discarded_explanation");
			appendTreeRingDataUploadWarnings(unexpectedMsg);
		}

		// construct the messages
		if (hasNothingUploaded)
		{
			// nothing uploaded
			if (hasTridasUploadWarnings())
			{
				logger.debug("Tridas Upload Warnings: " + getTridasUploadWarnings());
				status.setTridasUploadWarnings(getTridasUploadWarnings());
			}
			status.setTridasUploadHints((String)properties.getProperty("no_tridas_file"));
			status.setTreeRingDataUploadWarnings((String)properties.getProperty("no_treering_data"));
		}
		else if (hasNoTreeringFiles)
		{
			// hint for Expected files error
			status.setMessage((String)properties.getProperty("finish_upload_hint"));

			// if we need them because not all treering data values are uploaded
			if (hasTreeRingDataFilesMissing)
			{
				status.setTreeRingDataUploadErrors((String)properties.getProperty("treering_data_needed") +
						treeRingDataFilesMissingSummerasingString +
						"<br/>" +
						(String)properties.getProperty("treering_data_needed_explanation")
				);
			}
			status.setTreeRingDataUploadWarnings(getTreeRingDataUploadWarnings());
		}
		else if (hasNoTridasFiles)
		{
			// no tridas, but we do have treering data
			// Note: This should not happen in the lates design onas spevcified on the Trac:
			// http://trac.dans.knaw.nl/dccd/wiki/UploadFilesPage
			logger.error("No TRiDaS, but uploaded treering data allready should not be possible");
		}
		else
		{
			// we have both tridas and treering data
			if (hasTreeRingDataFilesMissing)
			{
				// Note: only an error if there where some files uploaded;
				// if nothing has been uploaded it looks more like a Hint?
				status.setTreeRingDataUploadErrors((String)properties.getProperty("treering_data_needed") +
						treeRingDataFilesMissingSummerasingString +
						"<br/>" +
						(String)properties.getProperty("treering_data_needed_explanation")
				);
				// hint for Expected files error
				status.setMessage((String)properties.getProperty("finish_upload_hint"));
			}
			status.setTreeRingDataUploadWarnings(getTreeRingDataUploadWarnings());
		}

		//--- Associated files
		// only possible if Tridas has been uploaded allready (only then the upload is visible)
		boolean hasUploadedAllAssociatedFiles = true;
		List<String> namesOfAssociatedFilesNotUploaded = getNamesOfAssociatedFilesNotUploaded();
		if (!namesOfAssociatedFilesNotUploaded.isEmpty()) 
		{
			hasUploadedAllAssociatedFiles = false;
			
			status.setAssociatedFilesUploadErrors((String)properties.getProperty("associated_files_needed") +
					StringUtil.constructCommaSeparatedString(namesOfAssociatedFilesNotUploaded) +
					"<br/>" +
					(String)properties.getProperty("associated_files_needed_explanation")
			);
			// hint for Expected files error
			//status.setMessage((String)properties.getProperty("finish_upload_hint"));		
		}
		// just copy the warnings
		status.setAssociatedFilesUploadWarnings(getAssociatedFilesUploadWarnings());			
		status.setAssociatedFilesUploadedMessage(getAssociatedFilesUploadedMessage());
		
		//---
		
		if (hasNothingUploaded)
		{
			// no data, nothing to finish
			status.setReadyToFinish(false);
		}
		else
		{
			// only allow finish if combination is ok
			status.setReadyToFinish((!hasTridasUploadWarnings())  && 
									canCombineWithTreeRingData &&
									hasUploadedAllAssociatedFiles
									);
		}

		
		//logger.debug("Uploaded files: " + numTridasFiles +
		//			" Tridas and " + numTreeringFiles + " Treering data files" +
		//			", Missing measurement files: " + treeRingDataFilesMissingSummerasingString +
		//			", Missing Tridas references: " + treeRingDataFilesUnNeededSummarisingString);

		return status;
	}

	public boolean hasReferencesToTreeRingData()
	{
		boolean treeRingDataNeeded = false;
		if (hasProject())
		{
			logger.debug("Look for tree ring data files");
			Project project = getProject();
			if (!ProjectTreeRingDataFileDetector.getProjectTreeRingDataFileNames(project).isEmpty())				
			{
				treeRingDataNeeded = true;
				logger.debug("Found TreeRingDataFile references");
			}
		}
		return treeRingDataNeeded;
	}

	public boolean hasReferencesToAssociatedFiles()
	{
		boolean result = false;
		if (hasProject())
		{
			logger.debug("Look for associated files!");
			Project project = getProject();
			if (!ProjectAssociatedFileDetector.getProjectAssociatedFileNames(project).isEmpty())
			{
				result = true;
			}
		}
		return result;
	}
	
	/**
	 * Note: after we removed them we cannot report about them?
	 */
	private void removeTreeRingDataFiles(List<String> filenames)
	{
		for (String filename : filenames)
		{
			removeTreeRingDataFile(filename);
		}
	}

	private void removeTreeRingDataFile(String filename)
	{		
		// remove from the list of uploaded treering data files
		Iterator<DccdTreeRingData> iter = treeRingDataList.iterator();
		while (iter.hasNext())
		{
			DccdTreeRingData data = iter.next();
			if (data.getFileName().compareTo(filename) == 0) {
				iter.remove();
			}
		}
	}

	/**
	 * Store the project with the tree ring data added
	 * Also updates the search index
     *
	 * TODO place this in a Service
	 */
	public synchronized void storeProject() throws CombinedUploadStoreException
	{
		// assume one and only one project
		if (!hasProject())
			return;

		Project project = getProject();

		project.setTridasLanguage(tridasLanguage);
		logger.debug("Setting project tridas language: " + tridasLanguage.getLanguage());

		// treering data (converted)
		if (checkCombination())
		{
			// Combine the treering data with the project
			addDataToProject(project);
		}
		else
		{
			logger.error("Unable to store project, because the data could not be combined");
			throw new CombinedUploadStoreException("Unable to store project, because the data could not be combined");
		}

		// original files
		try
		{
			addOriginalFiles(project);
		}
		catch (IOException e)
		{
			logger.error("Unable to store project, because the original files could not be added", e);
			throw new CombinedUploadStoreException(e);
		}

		// associated files
		try
		{
			addAssociatedFiles(project);
		}
		catch (IOException e)
		{
			logger.error("Unable to store project, because the associated files could not be added", e);
			throw new CombinedUploadStoreException(e);
		}

		// TODO handle this in the business/services, 
		// because now we need to remember in the interface that we need to do this 
		// However, not all cases need this, for instance when editing only a small part
		// We should have a separate store function for that, or an option indication the tree update!
		//
		// Value entities might have been added => 
		// Recreate the whole entity tree no matter what is already there!
		EntityTree entityTree = project.entityTree;
		entityTree.buildTree(project.getTridas());
		
		// Store the project
		logger.info("Store...");
		try
		{
			DccdDataService.getService().storeProject(project);
		}
		catch (DataServiceException e)
		{
			logger.error("Failed to store project", e);
			throw new CombinedUploadStoreException(e);
		}
		logger.info("Done storing");
	}

	// Make associated files available for storage
	private void addAssociatedFiles(Project project) throws IOException
	{		
		for (File file : associatedFiles)
		{
			project.addAssociatedFile(file);
		}
	}
	
	// Make original files available for storage
	private void addOriginalFiles(Project project) throws IOException
	{		
		logger.debug("Project original filename: " + project.getFileName());
		File orgProjectFile = findOriginalFile(project.getFileName());
		if (orgProjectFile != null)
		{
			logger.debug("found file: " + orgProjectFile);
			project.addOriginalFile(orgProjectFile);
		}
		else
		{
			logger.debug("Could not find original file: " + project.getFileName());
		}
	
		// get all the filenames of the the uploaded treering data (value) files
		// Note: don't use the ProjectTreeRingDataFileDetector.getProjectTreeRingDataFileNames
		// because it won't find uploaded files; the indicator has changed
		List<String> treeRingDataFileNames = new ArrayList<String>();
		for(DccdTreeRingData data : treeRingDataList)
		{
			treeRingDataFileNames.add(data.getFileName());
		}
		
		// Note maybe just do this once (after tridas upload) and keep the result
		// Note what if the format was changed and we have files from several formats...?
		// I think it doesn't matter now, the format is only used for conversion!
		for(String treeRingDataFileName : treeRingDataFileNames)
		{
			logger.debug("Project original value filename: " + treeRingDataFileName);
			File treeRingDataFile = findOriginalFile(treeRingDataFileName);
			if (treeRingDataFile != null)
			{
				logger.debug("found file: " + treeRingDataFile);
				project.addOriginalFile(treeRingDataFile);
			}
			else
			{
				logger.debug("Could not find original file: " + treeRingDataFileName);
			}
		}
		
	}

	private void cleanupFiles()
	{
		deleteAssociatedFilesFromTempFolder();
		deleteOriginalFilesFromTempFolder();	
	}
	
	private void deleteOriginalFilesFromTempFolder()
	{
		if (orgfilesTempDirectory != null) 
		{
			try
			{
				// wipe complete folder
				FileUtil.deleteDirectory(orgfilesTempDirectory);
				orgfilesTempDirectory = null;
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	// Note maybe the upload control already does this?
	private void deleteAssociatedFilesFromTempFolder()
	{
		for (File file : associatedFiles)
		{
			file.delete();
			logger.debug("deleted: " + file.getPath());
			// Note the file can be deeply nested when it comes from a zip file
			// but we don't try to delete those
		}
	}
	
	/**
	 * Add's all treering data to the Project's MeasurementSeriesEntity's
	 * The caller is responsible for checking first!
	 *
	 */
	private void addDataToProject (Project project)
	{
		// should we check first, or just proceed and see if we can manage?

		// construct the project, using the uploaded treering data
		List<MeasurementSeriesEntity> list = project.getMeasurementSeriesEntities();
		
		// but only series with external file ref's
		for(MeasurementSeriesEntity measurementSeries : list)
		{
			// check the genericFields
			TridasMeasurementSeries tridasSeries = (TridasMeasurementSeries)measurementSeries.getTridasAsObject();
			if (tridasSeries.isSetGenericFields())
			{
				// Ok we have potential candidates
				List<TridasGenericField> fields = tridasSeries.getGenericFields();
				for(TridasGenericField field : fields)
				{
					if (field.isSetValue() && field.isSetName() &&
							ProjectTreeRingDataFileDetector.isTreeRingDataFileIndicator(field.getName()))						
					{
						// We need to get the treering data for this series!
						DccdTreeRingData dataFound = findTreeRingData(field.getValue());
						if (dataFound == null)
						{
							logger.error("treering data not found for: "+ field.getValue());
							// adding fails.., but we should not get here
							// throw exception hard!
							throw new RuntimeException("Failed to add treering data to the project");
						}

						addDataToMeasurementSeriesEntity (measurementSeries, dataFound);
						
						// Change fieldname to indicate the file has been uploaded" 
						field.setName(Project.DATAFILE_INDICATOR_UPLOADED);
						
						break; // only one field should match, the first in this case!
					}
				}
			}
		}
		
		// Derived series
		// TODO refactor into two functions
		List<DerivedSeriesEntity> derivedList = project.getDerivedSeriesEntities();
		
		// but only series with external file ref's
		for(DerivedSeriesEntity derivedSeries : derivedList)
		{
			// check the genericFields
			TridasDerivedSeries tridasSeries = (TridasDerivedSeries)derivedSeries.getTridasAsObject();
			if (tridasSeries.isSetGenericFields())
			{
				// Ok we have potential candidates
				List<TridasGenericField> fields = tridasSeries.getGenericFields();
				for(TridasGenericField field : fields)
				{
					if (field.isSetValue() && field.isSetName() &&
							ProjectTreeRingDataFileDetector.isTreeRingDataFileIndicator(field.getName()))						
					{
						// We need to get the treering data for this series!
						DccdTreeRingData dataFound = findTreeRingData(field.getValue());
						if (dataFound == null)
						{
							logger.error("treering data not found for: "+ field.getValue());
							// adding fails.., but we should not get here
							// throw exception hard!
							throw new RuntimeException("Failed to add treering data to the project");
						}

						addDataToDerivedSeriesEntity (derivedSeries, dataFound);

						// Change fieldname to indicate the file has been uploaded" 
						field.setName(Project.DATAFILE_INDICATOR_UPLOADED);

						break; // only one field should match, the first in this case!
					}
				}
			}
		}
		
	}

	/**
	 * Find the treering data that was uploaded from the file with the given name
	 *
	 * Note: if there is more than one we don't find the others, only the first!
	 *
	 * @return The data object, null if not found
	 */
	private DccdTreeRingData findTreeRingData(String filename)
	{
		DccdTreeRingData dataFound = null;

		for (DccdTreeRingData data : treeRingDataList)
		{
			if (data.getFileName().compareToIgnoreCase(filename) == 0)
			{
				dataFound = data;
				break; // found!
			}
		}

		return dataFound;
	}

	/**
	 * add's the the treering data to the MeasurementSeriesEntity
	 * Used when combining all the project data
	 *
	 * @param measurementSeries
	 * @param treeringData
	 */
	private void addDataToMeasurementSeriesEntity (MeasurementSeriesEntity measurementSeries,
													DccdTreeRingData treeringData)
	{
		List<TridasValues> tridasValuesList = treeringData.getTridasValuesForMeasurementSeries();

		// Note that each TridasValues instance has a group (or list) of value instances
		logger.debug("Found groups of values: " + tridasValuesList.size());
		
		if (tridasValuesList.isEmpty())
		{
			logger.warn("NO measurement series values found in uploaded TreeRingData: " + treeringData.getFileName());
			return; // nothing to do
		}
		
		addValuesToSeriesEntity ((Entity)measurementSeries, tridasValuesList);
	}

	private void addDataToDerivedSeriesEntity (DerivedSeriesEntity derivedSeries,
													DccdTreeRingData treeringData)
	{
		List<TridasValues> tridasValuesList = treeringData.getTridasValuesForDerivedSeries();
		
		// Note that each TridasValues instance has a group (or list) of value instances
		logger.debug("Found groups of values: " + tridasValuesList.size());
		
		if (tridasValuesList.isEmpty())
		{
			logger.warn("NO derived series values found in uploaded TreeRingData: " + treeringData.getFileName());
			return; // nothing to do
		}
		
		addValuesToSeriesEntity ((Entity)derivedSeries, tridasValuesList);
	}
	
	private void addValuesToSeriesEntity (Entity series,
			List<TridasValues> tridasValuesList)
	{
		// Get a list of all the (empty) values subelements 
		// and fill them with the given ones, then if any are left create new ones

		// Get all 'empty' values (placeholders) and try to fill those
		List<TridasValues> placeholderTridasValues = getEmptyTridasValues(series);
		
		// calculate the number of values to add to the placeholders
		int numberOfValuesToAdd = tridasValuesList.size();
		int numberOfPlaceholders = placeholderTridasValues.size();
		int numberOfValuesToAddToPlaceholders = numberOfValuesToAdd; // only fill what we have
		if (numberOfValuesToAdd > numberOfPlaceholders)
			numberOfValuesToAddToPlaceholders = numberOfPlaceholders; // fill all placeholders
		
		// add to placeholders
		for(int i=0; i < numberOfValuesToAddToPlaceholders; i++)
		{
			placeholderTridasValues.get(i).getValues().addAll(tridasValuesList.get(i).getValues());
		}
		
		// if there is any left to add, create new entities for them
		if (numberOfValuesToAdd > numberOfPlaceholders)
		{
			for(int i=numberOfPlaceholders; i < numberOfValuesToAdd; i++)
			{
				TridasValues tridasValues = tridasValuesList.get(i);
				ValuesEntity valuesEntity = new ValuesEntity(tridasValues);
				series.getDendroEntities().add(valuesEntity);
				
				// Add to the tridas
				// Note: we can't use series.connectTridasObjectTree(), 
				// because that would duplicate existing values
				Object tridasAsObject = series.getTridasAsObject();
				// should implement ITridasSeries
				((ITridasSeries)tridasAsObject).getValues().add(tridasValues);
			}
		}
	}
	
	private List<TridasValues> getEmptyTridasValues(Entity series)
	{
		List<TridasValues> emptyValues = new ArrayList<TridasValues>();
		
		// assume series entity and filter subentities for Values entity
		List<Entity> subEntities = series.getDendroEntities();
		for(Entity subEntity : subEntities)
		{
			if (subEntity instanceof ValuesEntity)
			{
				TridasValues valuesFromEntity = (TridasValues) subEntity.getTridasAsObject();
				if (!valuesFromEntity.isSetValues())
				{
					emptyValues.add(valuesFromEntity);
				}
			}
		}
		
		return emptyValues;
	}
}
