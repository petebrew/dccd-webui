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

import nl.knaw.dans.common.wicket.components.upload.UploadStatus;
import nl.knaw.dans.common.wicket.components.upload.postprocess.IUploadPostProcess;
import nl.knaw.dans.common.wicket.components.upload.postprocess.UploadPostProcessException;
import nl.knaw.dans.dccd.application.services.TreeRingDataFileService;
import nl.knaw.dans.dccd.application.services.TreeRingDataFileServiceException;
import nl.knaw.dans.dccd.model.DccdTreeRingData;
import nl.knaw.dans.dccd.util.FileUtil;
import nl.knaw.dans.dccd.web.DccdSession;

import org.apache.log4j.Logger;
import org.apache.wicket.Session;

/**
 * Parse the uploaded treering data files
 *
 * @author paulboon
 *
 */
public class TreeRingDataUploadProcess implements IUploadPostProcess
{
	private static Logger logger = Logger.getLogger(TreeRingDataUploadProcess.class);
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

		logger.info("Upload Treering data file started...");

		Session session = Session.get();
		logger.debug("Session id: " + session.getId());
		CombinedUpload combinedUpload = ((DccdSession)session).getCombinedUpload();
		combinedUpload.clearTreeRingDataUploadWarnings();

		int fileCounter = 0;
		int numFiles = files.size();
		for(File file : files)
		{
			setStatus((int)Math.floor(100.0*((double)fileCounter)/(double)numFiles), file);
			try
			{
				logger.info("Reading file: " + file.getAbsolutePath());
				setStatus((int)Math.floor(100.0*((double)fileCounter+0.5)/(double)numFiles), file);

				// get the format and convert
				String formatString = combinedUpload.getFormat();
				DccdTreeRingData data = TreeRingDataFileService.load(file, formatString);

				// No store, but keep  the data for later use, after uploading!
				combinedUpload.addDccdTreeRingData(data);

				logger.debug("Added to the data list");// Data converted for range period: " + data.getRange());

				logger.debug("=== Request for Status after treeringdata upload === in thread: " + Thread.currentThread().getId());
				combinedUpload.getStatus();
			}
			catch (TreeRingDataFileServiceException e)
			{
				String errorMsg = "Failed loading file: " +
				FileUtil.getBasicFilename(file.getAbsolutePath());
				errorMsg += ", Cause: " + e.getMessage();

				// invalid, show an error page?
				// Validation failed?
				// Note: a reason/indication would be usefull!
				logger.error(errorMsg);
				status.setError(errorMsg);

				// TODO get strings from properties
				String warningMsg =	"File '" +
				FileUtil.getBasicFilename(file.getAbsolutePath()) +
				"' was not uploaded, because it appears not to be in the correct " +
				combinedUpload.getFormat() +
				" format.";
				combinedUpload.appendTreeRingDataUploadWarnings(warningMsg);

				throw new UploadPostProcessException(e);
				//break; // cancel remaining files
			}

			if (canceled)
			{
				// note: no rollback implemented, nothing is stored in a repository yet!
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
		// nothing stored in repository, so no rollback needed
	}
}
