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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.wicket.components.upload.UploadStatus;
import nl.knaw.dans.common.wicket.components.upload.postprocess.IUploadPostProcess;
import nl.knaw.dans.common.wicket.components.upload.postprocess.UploadPostProcessException;

import nl.knaw.dans.dccd.application.services.DccdDataService;
import nl.knaw.dans.dccd.application.services.DataServiceException;
import nl.knaw.dans.dccd.application.services.DccdSearchService;
import nl.knaw.dans.dccd.application.services.SearchServiceException;
import nl.knaw.dans.dccd.model.InternalErrorException;
import nl.knaw.dans.dccd.model.Project;
import nl.knaw.dans.dccd.repository.xml.TridasLoadException;
import nl.knaw.dans.dccd.repository.xml.XMLFilesRepositoryService;

import org.apache.log4j.Logger;

/** After uploading the tridas
 * it is converted to a Project, stored in the repository and
 * then the search engine is updated
 *
 * @author paulboon
 *
 */
public class ProjectStoreProcess implements IUploadPostProcess {
	private static Logger logger = Logger.getLogger(ProjectStoreProcess.class);
	private UploadStatus status = new UploadStatus("Store process");
 	private boolean canceled = false;

	public void cancel() throws UploadPostProcessException {
		canceled = true;
		logger.debug("canceled store");
	}

	public List<File> execute(List<File> files, File destPath,
			Map<String, String> clientParams) throws UploadPostProcessException {

		// do validation...
		logger.info("Store started...");

		int fileCounter = 0;
		int numFiles = files.size();
		for(File file : files)
		{
			setStatus((int)Math.floor(100.0*((double)fileCounter)/(double)numFiles), file);
			try {
				logger.info("Reading file: " + file.getAbsolutePath());
				FileInputStream fis = new FileInputStream(file.getAbsolutePath());
				setStatus((int)Math.floor(100.0*((double)fileCounter+0.33)/(double)numFiles), file);

				// parse TRiDaS file
				logger.info("Parse...");
				Project project = XMLFilesRepositoryService.getDendroProjectFromTridasXML(fis);
				logger.info("Done");
				setStatus((int)Math.floor(100.0*((double)fileCounter+0.66)/(double)numFiles), file);

				// store...
				logger.info("Store...");
				DccdDataService.getService().storeProject(project);
				logger.info("Done");
				setStatus((int)Math.floor(100.0*((double)fileCounter+1.0)/(double)numFiles), file);

				// update the search index
				// note: if we have a batch of files,
				// it would be better update them all at once
				try {
					DccdSearchService.getService().updateSearchIndex(project.getSid());
				} catch (SearchServiceException e) {
					logger.error("Failed to update the index, index not up-to-date anymore!");
					// data was ingested, but we really need a up-to-date index as well
					throw new UploadPostProcessException(e);
				}

			} catch (FileNotFoundException e) {
				logger.error("Could not open file", e);
				// Note: looks like an internal error, because it should be readable
				// the web user can't do anything about it
				// upload failed for reasons beyond the reach of the user!
				throw (new InternalErrorException(e)); // cancel remaining files
			} catch (TridasLoadException e) {
				// invalid, show an error page?
				// Validation failed?
				// Note: a reason/indication would be usefull!
				logger.error("Failed loading file: " + file.getAbsolutePath(), e);
				break; // cancel remaining files
			} catch (DataServiceException e) {
				logger.error("Failed to store project", e);
				//e.printStackTrace();
				throw new UploadPostProcessException(e);
				//break; // cancel remaining files
			}

			if (canceled) {
				// note: no rollback implemented, because no purge implemented!
				//rollBack();
				break;
			}

			fileCounter++;
		}// end for all files
		status.setFinished(true);

		return files;
	}

	public UploadStatus getStatus() {
		return status;
	}

	public boolean needsProcessing(List<File> files) {
		return true;
	}

	public void setStatus(int percent, File file)
	{
		if (percent < 0) percent = 0;
		if (percent > 100) percent = 100;
		// Note: the string should be language dependent
		status.setMessage("Storing '"+ file.getName() +"': "+ percent +"% ");
		status.setPercentComplete(percent);
	}

	public void rollBack() throws UploadPostProcessException {
		// TODO not sure what to do: should 'unstore' allready stored files?
	}

}

