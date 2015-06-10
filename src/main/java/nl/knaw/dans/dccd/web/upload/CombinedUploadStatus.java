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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import nl.knaw.dans.dccd.application.services.TreeRingDataFileService;

import org.apache.log4j.Logger;
import org.apache.wicket.util.string.AppendingStringBuffer;
import org.apache.wicket.util.string.Strings;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Used by the UploadFilesPage where different types of files can be uploaded and combined
 *
 * @author paulboon
 *
 */
public class CombinedUploadStatus implements Serializable {
	private static final long serialVersionUID = 849972035896584353L;
	private static Logger logger = Logger.getLogger(CombinedUploadStatus.class);

	private boolean readyToFinish = false; // indicates if the combined upload ready to finish
	private boolean readyToCancel = false; // indicates if the combined upload can be canceled
	private String message = ""; // overall message
	// tridas
	private boolean tridasUploadVisible = true; // visibility of the upload component for tridas
	private Locale tridasLanguage = Locale.ENGLISH;
	private String tridasUploadHints = ""; 
	private String tridasUploadWarnings; 
	private String tridasFilesUploadedMessage = ""; // the Tridas files already uploaded
	// tree ring data
	private boolean treeRingDataVisible = true; // visibility of the all the treering data components (including the upload)
	private String treeRingDataUploadWarnings = ""; 
	private String treeRingDataUploadErrors = ""; 
	private String treeRingDataFilesUploadedMessage = ""; // the value (tree ring data) files already uploaded
	private TreeRingDataFileTypeSelection treeRingDataFileTypeSelection = new TreeRingDataFileTypeSelection();
	// associated files
	private boolean associatedFilesVisible = true;
	private String associatedFilesUploadWarnings = ""; 
	private String associatedFilesUploadedMessage = ""; // the associated files already uploaded
	private String associatedFilesUploadErrors = ""; 

	//--- Tree ring data

	public TreeRingDataFileTypeSelection getTreeRingDataFileTypeSelection()
	{
		return treeRingDataFileTypeSelection;
	}

	// The model class for the selection
	// maybe put this in a separate file?
	public class TreeRingDataFileTypeSelection implements Serializable
	{
		private static final long serialVersionUID = -6173650372149125063L;
		// Now use a nice enum instead of int's
		private String selection;

		TreeRingDataFileTypeSelection()
		{
			// assume we have at least one format, and use the first one in the list
			// TODO initialize elsewhere	
			selection = TreeRingDataFileService.getReadingFormats().get(0);
		}

		TreeRingDataFileTypeSelection(String selection)
		{
			this.selection = selection;
		}

		public String getSelection()
		{
			return selection;
		}

		public void setSelection(String selection)
		{
			this.selection = selection;
		}

		public List<TreeRingDataFileTypeSelection> getSelectionList ()
		{
			List<TreeRingDataFileTypeSelection> list = new ArrayList<TreeRingDataFileTypeSelection>();

			List<String> readingFormats = TreeRingDataFileService.getReadingFormats();
			for(String formatString : readingFormats)
			{
				list.add(new TreeRingDataFileTypeSelection(formatString));
			}
			return list;
		}
	};

	public String getSelectedFormat()
	{
		//return selectedFormat;
		return treeRingDataFileTypeSelection.getSelection();
	}

	public void setSelectedFormat(String selectedFormat)
	{
		//this.selectedFormat = selectedFormat;
		treeRingDataFileTypeSelection.setSelection(selectedFormat);
	}

	public String getSelectedFormatString()
	{
		//return formatSelectionStrings.get(selectedFormat);
		return treeRingDataFileTypeSelection.getSelection();
	}
	
	public String getTreeRingDataUploadWarnings()
	{
		return treeRingDataUploadWarnings;
	}

	public void setTreeRingDataUploadWarnings(String valuesUploadWarnings)
	{
		this.treeRingDataUploadWarnings = valuesUploadWarnings;
	}

	public String getTreeRingDataUploadErrors() {
		return treeRingDataUploadErrors;
	}

	public void setTreeRingDataUploadErrors(String valuesUploadErrors) {
		this.treeRingDataUploadErrors = valuesUploadErrors;
	}

	public String getTreeRingDataFilesUploadedMessage() {
		return treeRingDataFilesUploadedMessage;
	}

	public void setTreeRingDataFilesUploadedMessage(String valueFilesUploadedMessage) {
		this.treeRingDataFilesUploadedMessage = valueFilesUploadedMessage;
	}
	
	//--- Tridas

	public Locale getTridasLanguage() {
		return tridasLanguage;
	}

	public void setTridasLanguage(Locale tridasLanguage) {
		this.tridasLanguage = tridasLanguage;
	}

	public String getTridasUploadHints() {
		return tridasUploadHints;
	}

	public void setTridasUploadHints(String tridasUploadHints) {
		this.tridasUploadHints = tridasUploadHints;
	}

	public String getTridasUploadWarnings()
	{
		return tridasUploadWarnings;
	}

	public void setTridasUploadWarnings(String tridasUploadWarnings)
	{
		this.tridasUploadWarnings = tridasUploadWarnings;
	}

	public String getTridasFilesUploadedMessage() {
		return tridasFilesUploadedMessage;
	}

	public void setTridasFilesUploadedMessage(String tridasFilesUploadedMessage) {
		this.tridasFilesUploadedMessage = tridasFilesUploadedMessage;
	}

	//--- Associated files
	
	public String getAssociatedFilesUploadWarnings()
	{
		return associatedFilesUploadWarnings;
	}

	public void setAssociatedFilesUploadWarnings(String associatedFilesUploadWarnings)
	{
		this.associatedFilesUploadWarnings = associatedFilesUploadWarnings;
	}

	public String getAssociatedFilesUploadedMessage()
	{
		return associatedFilesUploadedMessage;
	}

	public void setAssociatedFilesUploadedMessage(String associatedFilesUploadedMessage)
	{
		this.associatedFilesUploadedMessage = associatedFilesUploadedMessage;
	}

	public String getAssociatedFilesUploadErrors()
	{
		return associatedFilesUploadErrors;
	}

	public void setAssociatedFilesUploadErrors(String associatedFilesUploadErrors)
	{
		this.associatedFilesUploadErrors = associatedFilesUploadErrors;
	}
	
	//---
	
	public boolean isReadyToCancel()
	{
		return readyToCancel;
	}

	public void setReadyToCancel(boolean readyToCancel)
	{
		this.readyToCancel = readyToCancel;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public boolean isReadyToFinish() {
		return readyToFinish;
	}

	public void setReadyToFinish(boolean readyToFinish)
	{
		this.readyToFinish = readyToFinish;
	}

	public boolean isTridasUploadVisible() {
		return tridasUploadVisible;
	}

	public void setTridasUploadVisible(boolean tridasUploadVisible) {
		this.tridasUploadVisible = tridasUploadVisible;
	}

	public boolean isTreeRingDataVisible() {
		return treeRingDataVisible;
	}

	public void setTreeRingDataVisible(boolean treeRingDataVisible) {
		this.treeRingDataVisible = treeRingDataVisible;
	}

	
	public boolean isAssociatedFilesVisible()
	{
		return associatedFilesVisible;
	}

	public void setAssociatedFilesVisible(boolean associatedFilesVisible)
	{
		this.associatedFilesVisible = associatedFilesVisible;
	}
	
	// Serialize to JSON
	// put here everything you need on the client side
	public JSONObject toJSONObject()
	{
		JSONObject jobj = new JSONObject();
		try
		{
			jobj.put("tridasUploadVisibility", isTridasUploadVisible());
			jobj.put("tridasLanguage", getTridasLanguage().getLanguage());
			jobj.put("tridasUploadHints", convertToHtmlUnicodeEscapes(getTridasUploadHints()));
			jobj.put("tridasUploadWarnings", convertToHtmlUnicodeEscapes(getTridasUploadWarnings()));
			jobj.put("tridasFilesUploadedMessage", convertToHtmlUnicodeEscapes(getTridasFilesUploadedMessage()));
			jobj.put("valuesUploadWarnings", convertToHtmlUnicodeEscapes(getTreeRingDataUploadWarnings()));
			jobj.put("valuesUploadErrors", convertToHtmlUnicodeEscapes(getTreeRingDataUploadErrors()));
			jobj.put("valueFilesUploadedMessage", convertToHtmlUnicodeEscapes(getTreeRingDataFilesUploadedMessage()));
			jobj.put("treeRingDataVisible", isTreeRingDataVisible());
			jobj.put("selectedFormat", getSelectedFormatString());
			jobj.put("associatedFilesVisible", isAssociatedFilesVisible());
			jobj.put("associatedFilesUploadedMessage", convertToHtmlUnicodeEscapes(getAssociatedFilesUploadedMessage()));
			jobj.put("associatedFilesUploadWarnings", convertToHtmlUnicodeEscapes(getAssociatedFilesUploadWarnings()));
			jobj.put("associatedFilesUploadErrors", convertToHtmlUnicodeEscapes(getAssociatedFilesUploadErrors()));			
			jobj.put("readyToFinish", isReadyToFinish());
			jobj.put("readyToCancel", isReadyToCancel());
			jobj.put("message", convertToHtmlUnicodeEscapes(getMessage()));
		}
		catch (JSONException e)
		{
			logger.error("Caught error while serializing CombinedUploadStatus object to JSON.", e);
			return jobj;
		}
		logger.debug("JSON: " + jobj);

		return jobj;
	}
	
	// Otherwise the Javascript JSON.parse gives a Syntax Error
	private String convertToHtmlUnicodeEscapes(String s)
	{
		if (s == null)
			return null;
			
		int len = s.length();
		final AppendingStringBuffer buffer = new AppendingStringBuffer((int)(len * 1.1));

		for (int i = 0; i < len; i++)
		{
			final char c = s.charAt(i);
			int ci = 0xffff & c;
			if (ci < 160)
			{
				// nothing special only 7 Bit
				buffer.append(c);
			}
			else
			{
				// Not 7 Bit use the unicode system
				buffer.append("&#");
				buffer.append(new Integer(ci).toString());
				buffer.append(';');
			}
		}
		
		return buffer.toString();
	}
}
