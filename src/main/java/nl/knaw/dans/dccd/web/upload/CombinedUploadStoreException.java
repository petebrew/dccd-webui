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

public class CombinedUploadStoreException extends Exception {
	private static final long serialVersionUID = 6110168946717570500L;

	public CombinedUploadStoreException() {
		super();
	}

	public CombinedUploadStoreException(String message, Throwable cause) {
		super(message, cause);
	}

	public CombinedUploadStoreException(String message) {
		super(message);
	}

	public CombinedUploadStoreException(Throwable cause) {
		super(cause);
	}

}
