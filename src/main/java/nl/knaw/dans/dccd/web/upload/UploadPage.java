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

import nl.knaw.dans.dccd.application.services.DccdDataService;
import nl.knaw.dans.dccd.util.FileUtil;
import nl.knaw.dans.dccd.web.base.BasePage;

import org.apache.log4j.Logger;

/**
 * @author paulboon
 */
public class UploadPage extends BasePage {
	private static Logger logger = Logger.getLogger(UploadPage.class);

	public UploadPage() {
		// get the system temp folder
		String tempDir = System.getProperty("java.io.tmpdir");
		logger.info("using temp dir for upload: " + tempDir);

		EasyUpload upload = new EasyUpload("upload_panel", tempDir) {
			private static final long serialVersionUID = 4250633650804049018L;

			@Override
			public void onReceivedFiles(Map<String, String> clientParams, String basePath, List<File> files) {

				// Always delete the temporary files
				FileUtil.deleteDirectory(new File(basePath));
				logger.info("File(s) deleted");

				// make sure that files are re-read
				DccdDataService.reset();
			}
		};

		upload.registerPostProcess(TridasValidationProcess.class);
		upload.registerPostProcess(ProjectStoreProcess.class);
		add(upload);
	}

	//public UploadPage(IModel model) {
	//	super(model);
	//}
}

