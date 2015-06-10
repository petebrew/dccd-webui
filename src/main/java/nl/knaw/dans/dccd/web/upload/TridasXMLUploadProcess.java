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
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import nl.knaw.dans.common.lang.user.User;
import nl.knaw.dans.common.wicket.components.upload.UploadStatus;
import nl.knaw.dans.common.wicket.components.upload.postprocess.IUploadPostProcess;
import nl.knaw.dans.common.wicket.components.upload.postprocess.UploadPostProcessException;
import nl.knaw.dans.dccd.model.InternalErrorException;
import nl.knaw.dans.dccd.model.Project;
import nl.knaw.dans.dccd.repository.xml.TridasLoadException;
import nl.knaw.dans.dccd.repository.xml.XMLFilesRepositoryService;
import nl.knaw.dans.dccd.util.FileUtil;
import nl.knaw.dans.dccd.web.DccdSession;

import org.apache.log4j.Logger;
import org.apache.wicket.Session;

/**
 * Parse the uploaded Tridas file to a Project for later use, after uploading!
 *
 * @author paulboon
 *
 */
public class TridasXMLUploadProcess implements IUploadPostProcess
{
	private static Logger logger = Logger.getLogger(ProjectStoreProcess.class);
	private UploadStatus status = new UploadStatus("Store process");
 	private boolean canceled = false;

	public void cancel() throws UploadPostProcessException
	{
		canceled = true;
		logger.debug("canceled store");
	}

	public List<File> execute(List<File> files, File destPath,
			Map<String, String> clientParams) throws UploadPostProcessException
	{

		logger.info("Upload Tridas file started...");
		Session session = Session.get();
		logger.debug("Session id: " + session.getId());
		CombinedUpload combinedUpload = ((DccdSession)session).getCombinedUpload();

		int fileCounter = 0;
		int numFiles = files.size();
		for(File file : files)
		{
			setStatus((int)Math.floor(100.0*((double)fileCounter)/(double)numFiles), file);

			FileInputStream fis = null;
			try
			{
				logger.info("Reading file: " + file.getAbsolutePath());
				fis = new FileInputStream(file.getAbsolutePath());
				setStatus((int)Math.floor(100.0*((double)fileCounter+0.5)/(double)numFiles), file);

				// get the user(ID) that uploaded it
				User user = ((DccdSession)Session.get()).getUser();

				// parse TRiDaS file
				logger.info("Parse...");
				Project newProject = XMLFilesRepositoryService.createDendroProjectFromTridasXML(fis, user.getId());
				logger.info("Done");
				setStatus((int)Math.floor(100.0*((double)fileCounter+1.0)/(double)numFiles), file);

				// store the (original) xml filename
				newProject.setFileName(file.getName());

				// Note:  build the complete tree of entities, we can use for searching,
				// maybe this should be done by: XMLFilesRepositoryService.getDendroProjectFromTridasXML
				// and then copy the tree also?
				newProject.entityTree.buildTree(newProject.getTridas());

				// store the user(ID) that uploaded it
				newProject.setOwnerId(user.getId());

				combinedUpload.addProject(newProject);

				logger.debug("=== Request for Status after tridas upload === in thread: " + Thread.currentThread().getId());
				//logger.debug("UUID: " + combinedUpload.getUuid());
				combinedUpload.getStatus();

			}
			catch (FileNotFoundException e)
			{
				logger.error("Could not open file", e);
				// Note: looks like an internal error, because it should be readable
				// the web user can't do anything about it
				// upload failed for reasons beyond the reach of the user!
				throw (new InternalErrorException(e)); // cancel remaining files
			}
			catch (TridasLoadException e)
			{
				// Validation failed
				// Give feedback about the problem
				
				String errorMsg = "Failed validating file: " +
									FileUtil.getBasicFilename(file.getAbsolutePath());
				errorMsg += ", Cause: " + e.getMessage();
				// Only two reasons:
				// - Not conform the TRiDaS Standard  (also when not valid XML)
				// - More than one project in TRiDaS file
				//
				// ? linked exception ?
				// ? cause ?
				//Throwable cause = e.getCause();
				//if (cause != null && cause.getMessage() != null) {
				//	errorMsg +=	" Cause: " + cause.getMessage();
				//}
				logger.error(errorMsg);//, e);
				status.setError(errorMsg);
				// Note: the error message is no localized!
				// Maybe use specific exceptions and - depending on the type -
				// construct a localized message?

				//TODO get the strings from properties
				String warningMsg = "File '" +
				FileUtil.getBasicFilename(file.getAbsolutePath()) +
				"' was not uploaded, because it contains invalid TRiDaS XML. Please correct the file and upload it again.";

				// Add more detail information to the problem if we have it
				Throwable cause = e.getCause();
				if (cause instanceof JAXBException)
				{
					Throwable le = ((JAXBException)cause).getLinkedException();
					warningMsg += "\n Details: " + le.getMessage();
					//logger.debug("Linked: " + le.getMessage());
				}

				combinedUpload.setTridasUploadWarnings(warningMsg);

				throw new UploadPostProcessException(e);
				//break; // cancel remaining files
			}
			finally
			{
				// free resources
				if(fis != null)
					try
					{
						fis.close();
					}
					catch (IOException e)
					{
						logger.warn("Could not close inputstream");
					}
			}

			if (canceled)
			{
				// note: no rollback implemented, because no purge implemented!
				//rollBack();
				break;
			}

			fileCounter++;
		}// end for all files
		status.setFinished(true);

		return files;
	}

	public UploadStatus getStatus()
	{
		return status;
	}

	public boolean needsProcessing(List<File> files)
	{
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

	public void rollBack() throws UploadPostProcessException
	{
		// we are not storing into the repository, so no rollback needed
	}

}
