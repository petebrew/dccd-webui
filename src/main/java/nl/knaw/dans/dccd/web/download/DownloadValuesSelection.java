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
import java.util.List;

import nl.knaw.dans.dccd.application.services.TreeRingDataFileService;

import org.apache.log4j.Logger;

// Using format identifying strings from the TridasIO library
public class DownloadValuesSelection implements Serializable 
{
	private static final long serialVersionUID = 450681643587565258L;
	private static Logger logger = Logger.getLogger(DownloadValuesSelection.class);

	// assume the TridasIO would never have a format with that name
	public static final String SELECT_NONE = "format.select.none";
	
	private String formatselection = SELECT_NONE;

	public String getSelection() 
	{
		return formatselection;
	}

	public void setSelection(String selection) 
	{
		// TODO check if it is a valid format string
		this.formatselection = selection;
	}

	public static List<String> getTypeList() 
	{
		List<String> typeList = TreeRingDataFileService.getWritingFormats();
		// prepend the 'none' selection
		typeList.add(0, SELECT_NONE);
		
		return typeList;
	}
}
