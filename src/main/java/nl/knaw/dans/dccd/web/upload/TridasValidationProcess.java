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

import nl.knaw.dans.dccd.model.InternalErrorException;
import nl.knaw.dans.dccd.repository.xml.TridasLoadException;
import nl.knaw.dans.dccd.repository.xml.XMLFilesRepositoryService;
import nl.knaw.dans.dccd.util.FileUtil;

import org.apache.log4j.Logger;

//TODO: LB20090923: Het idee van een een postprocessor is dat deze een percentage
// terug geeft.

public class TridasValidationProcess implements IUploadPostProcess {
	private static Logger logger = Logger.getLogger(TridasValidationProcess.class);
	private UploadStatus status = new UploadStatus("Validation process");
 	private boolean canceled = false;


	public void cancel() throws UploadPostProcessException {
		canceled = true;
		logger.debug("canceled validation");
	}

	public List<File> execute(List<File> files, File destPath,
			Map<String, String> clientParams) throws UploadPostProcessException {

		// do validation...
		logger.info("Validation started...");

		/*
		// initialize validation, Schema validation in JAXB 2.0 is performed using JAXP 1.3
		Schema schema = null;
		try {
			SchemaFactory sf = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
			//schema = sf.newSchema(ClassLoader.getSystemResource("tridas.xsd"));
			// Note: couldn't get this working on the server
			// resource loading probably has to do with the classpath...
			//
			// ? http://www.tridas.org/1.2/tridas.xsd
			//URL schemaUrl = new URL("http://www.tridas.org/1.2/tridas.xsd");
			URL schemaUrl = Thread.currentThread().getContextClassLoader().getResource("tridas.xsd");

			schema = sf.newSchema(schemaUrl);
		} catch (IllegalArgumentException e) {
			logger.error("Could not initialize schema validation");//, e);
			throw (new DccdInternalErrorException(e));
		} catch (SAXException e) {
			// this is an internal error, the schema should be available!
			logger.error("Could not initialize schema validation");//, e);
			throw (new DccdInternalErrorException(e));
		}
		*/

		for(File file : files)
		{
			// validate TRiDaS!
			try {
				FileInputStream fis = new FileInputStream(file.getAbsolutePath());
				// next call throws exception if not valid
				//
				// note: now all tridas is parsed, maybe there is a faster procedure?
				// If so, make that a member of this class...
				XMLFilesRepositoryService.getDendroProjectFromTridasXML(fis); // Validates!
			} catch (FileNotFoundException e) {
				logger.error("Could not open file for validation", e);
				// Note: looks like an internal error, because it should be readable
				// the web user can't do anything about it
				// upload failed for reasons beyond the reach of the user!
				throw (new InternalErrorException(e)); // cancel remaining files
			} catch (TridasLoadException e) {
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
				throw new UploadPostProcessException(e);
				//break; // cancel remaining files
			}

			if (canceled) {
				break;
			}

		} // end for all files

		return files;
	}

	public UploadStatus getStatus() {
		return status;
	}

	public boolean needsProcessing(List<File> files) {
		return true;
	}

	public void rollBack() throws UploadPostProcessException {
		// it's only validation, no stores, so rollback not needed
	}

}
