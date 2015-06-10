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


import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import nl.knaw.dans.dccd.web.DccdSession;

import org.apache.log4j.Logger;
import org.apache.wicket.Application;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.DynamicWebResource;
import org.apache.wicket.protocol.http.WebRequest;
import org.json.JSONObject;

public class CombinedUploadStatusCommand extends DynamicWebResource {
	private static Logger logger = Logger.getLogger(CombinedUploadStatusCommand.class);
	private static final long serialVersionUID = 6337579647970283887L;
	public static final String RESOURCE_NAME = "combinedUploadStatus";

	public static final String TREERINGDATAFILEFORMAT_ARGUMENT = "treeringdatafileformat";
	public static final String TRIDASLANGUAGE_ARGUMENT = "tridaslang";

	public String getTreeringDataFileFormat()
	{
		RequestCycle rc = RequestCycle.get();

		// find argument
		HttpServletRequest req = ((WebRequest)rc.getRequest()).getHttpServletRequest();
		String[] arg = req.getParameterValues(TREERINGDATAFILEFORMAT_ARGUMENT);
		if (arg == null || (arg != null && arg.length == 0)) {
			//throw new MissingResourceException("Missing argument", TREERINGDATAFILEFORMAT_ARGUMENT, TREERINGDATAFILEFORMAT_ARGUMENT);
			logger.debug("No argument: " + TREERINGDATAFILEFORMAT_ARGUMENT);
			return null; // nothing is set
		}

		return arg[0];
	}

	public String getTridasLanguage()
	{
		RequestCycle rc = RequestCycle.get();

		// find argument
		HttpServletRequest req = ((WebRequest)rc.getRequest()).getHttpServletRequest();
		String[] arg = req.getParameterValues(TRIDASLANGUAGE_ARGUMENT);
		if (arg == null || (arg != null && arg.length == 0)) {
			//throw new MissingResourceException("Missing argument", TRIDASLANGUAGE_ARGUMENT, TRIDASLANGUAGE_ARGUMENT);
			logger.debug("No argument: " + TRIDASLANGUAGE_ARGUMENT);
			return null; // nothing is set
		}

		return arg[0];
	}

	public void registerAsSharedResource(Application application) {
		application.getSharedResources().add(RESOURCE_NAME, this);
	}

	@Override
	protected ResourceState getResourceState() {
		this.setCacheable(false);
		return new CombinedUploadStatusResourceState(); // the inner class
	}

	private class CombinedUploadStatusResourceState extends DynamicWebResource.ResourceState {
		//private JSONArray responseWriter = new JSONArray();
		private JSONObject responseWriter;

		public CombinedUploadStatusResourceState() {
			// pass the selected data file format from the webclient to the CombinedUpload

			Session session = Session.get();
			String sessionId = session.getId();
			logger.debug("Status request ====> Session Id: " + sessionId);
			CombinedUpload combinedUpload = ((DccdSession)session).getCombinedUpload();

			String selectedFormatString = getTreeringDataFileFormat();
			// if no format was given, don't set it
			if (selectedFormatString != null) {
				logger.debug("Treering datafile format: " + selectedFormatString);
				combinedUpload.setFormat(selectedFormatString);
			}

			String tridasLanguageString = getTridasLanguage();
			// if no language was given, don't set it
			if (tridasLanguageString != null) {
				logger.debug("TRiDaS language: " + tridasLanguageString);
				Locale locale = new Locale(tridasLanguageString);
				combinedUpload.setTridasLanguage(locale);
			}

			// create JSON response
			CombinedUploadStatus status = combinedUpload.getStatus();
			responseWriter = status.toJSONObject();
		}

		/**
		 * @see org.apache.wicket.markup.html.DynamicWebResource.ResourceState#getContentType()
		 */
		public String getContentType() {
			return "text/json";
		}

		/**
		 * @see org.apache.wicket.markup.html.DynamicWebResource.ResourceState#getLength()
		 */
		public int getLength() {
			return responseWriter.toString().length();
		}

		/**
		 * @see org.apache.wicket.markup.html.DynamicWebResource.ResourceState#getData()
		 */
		public byte[] getData() {
			//LOG.debug("Send upload status update = "+ responseWriter.toString());
			return responseWriter.toString().getBytes();
		}
	}
}
