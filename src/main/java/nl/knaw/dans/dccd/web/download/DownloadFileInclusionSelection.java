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

import java.io.Serializable;

public class DownloadFileInclusionSelection implements Serializable {
	//private static Logger logger = Logger.getLogger(DownloadFileInclusionSelection.class);
	private static final long serialVersionUID = 2685683359885707199L;
	private boolean originalValuesFiles = false;
	private boolean associatedFiles = false;
	private boolean dccdAdminstrativeData = false;
	private boolean dccdUsageComments = false;

	public boolean isOriginalValuesFiles() {
		return originalValuesFiles;
	}

	public void setOriginalValuesFiles(boolean originalValuesFiles) {
		this.originalValuesFiles = originalValuesFiles;
	}

	public boolean isAssociatedFiles() {
		return associatedFiles;
	}

	public void setAssociatedFiles(boolean associatedFiles) {
		this.associatedFiles = associatedFiles;
	}

	public boolean isDccdAdminstrativeData() {
		return dccdAdminstrativeData;
	}

	public void setDccdAdminstrativeData(boolean dccdAdminstrativeData) {
		this.dccdAdminstrativeData = dccdAdminstrativeData;
	}

	public boolean isDccdUsageComments() {
		return dccdUsageComments;
	}

	public void setDccdUsageComments(boolean dccdUsageComments) {
		this.dccdUsageComments = dccdUsageComments;
	}


}
