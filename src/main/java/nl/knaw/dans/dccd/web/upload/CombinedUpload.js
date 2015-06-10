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
*******************************************************************************/
/*
 * Note: assume javascripts for jQuery, json and EasyUpload have been loaded!
 */

var g_combinedUploadStatus;
var g_canBrowseaway = false;

/*
 * Uses mechanism provided by EasyUpload to handle upload event
 */
registerUploadEventHandler(UPLOAD_EVENT_FINISHED, function (eventParams) 
{
	//alert("UPLOAD_EVENT_FINISHED fired!"); // called on successful upload
	handleCombinedChange();
});

/** Handles a change of the treeringdata filetype
 *
 * Note: when this code is reached the "measurements_filetype" element has not been created;
 * the DOM is not ready!
 * It would be nicer to add the handler in the scriptcode section and not from the html element,
 * But for now, that is what works...
 */
//document.getElementById("measurements_filetype").onChange = function () {
function changeType() 
{
	handleCombinedChange();
}

function changeLanguage() 
{
	// the selection for the TRiDaS language has changed!
	var tridasLanguage = document.getElementById("tridas_language");
	if (tridasLanguage) 
	{
		//alert(tridasLanguage.value);

		// check if the separator was selected
		if(tridasLanguage.value[0] == "-")	
		{
			//alert(tridasLanguage.value);
			// find the index of the selection
			index = tridasLanguage.options.selectedIndex;
			// select one above
			tridasLanguage.options[index-1].selected = true;
			//return;
		}

		// propagate change to the server...
		handleCombinedChange();
	}
}

function convertMessage(message)
{
	var result = "";
	if(typeof(message) != "undefined")
	{
		result = message;
	}
	return result;
}

function hasMessage(message)
{
	var result = true;

	if(typeof(message) == "undefined" || message.length == 0)
		result = false;

	return result;
}

/**
 * Just do what the Status tells us, no business logic here
 */
function combinedStatusUpdate(response)
{
	 //alert("response: " + response);
	// get object from JSON response
	var jobj = JSON.parse(response);
	g_combinedUploadStatus = jobj; // keep lasts status for reuse

	// update...
	//alert("readyToFinish: " + jobj.readyToFinish);
	//alert("message: " + jobj.message);
	//alert("warnings: " + convertMessage(jobj.tridasUploadWarnings));

	var tridasUploadPanel = document.getElementById("tridas_upload_panel_wrapper");
	if (tridasUploadPanel)
	{
		//alert("Tridas upload visible = " + jobj.tridasUploadVisibility);
		if(jobj.tridasUploadVisibility)
			jQuery("#tridas_upload_panel_wrapper").show();
		else
			jQuery("#tridas_upload_panel_wrapper").hide("slow");
	}

	var tridasCombinedUploadHints = document.getElementById("tridas_combined_upload_hints");
	if (tridasCombinedUploadHints) {
		tridasCombinedUploadHints.innerHTML = convertMessage(jobj.tridasUploadHints);
	}

	var tridasCombinedUploadWarnings = document.getElementById("tridas_combined_upload_warnings");
	if (tridasCombinedUploadWarnings) {
		tridasCombinedUploadWarnings.innerHTML = convertMessage(jobj.tridasUploadWarnings);
	}

	var tridasFilesUploadedMessage = document.getElementById("tridas_files_uploaded_message");
	if (tridasFilesUploadedMessage) {
		tridasFilesUploadedMessage.innerHTML = convertMessage(jobj.tridasFilesUploadedMessage);
	}
	
	var tridasLanguage = document.getElementById("tridas_language");
	if (tridasLanguage) {
		// unfortunately there is no direct selection mechanism based on the value
		for (var i=0; i < tridasLanguage.options.length; i++) {
			if (tridasLanguage.options[i].value == jobj.tridasLanguage)
				tridasLanguage.options[i].selected = true;
		}
	}
	
	//--- Value files (tree ring data)
	
	var treeRingDataUploadPanel = document.getElementById("measurements_upload_panel_wrapper");
	if (treeRingDataUploadPanel)
	{
		if(jobj.treeRingDataVisible)
			jQuery("#measurements_upload_panel_wrapper").show("slow");
		else
			jQuery("#measurements_upload_panel_wrapper").hide();
	}

	var valuesCombinedUploadWarnings = document.getElementById("values_combined_upload_warnings");
	if (valuesCombinedUploadWarnings) {
		valuesCombinedUploadWarnings.innerHTML = convertMessage(jobj.valuesUploadWarnings);
	}

	var valuesCombinedUploadErrors = document.getElementById("values_combined_upload_errors");
	if (valuesCombinedUploadErrors) {
		valuesCombinedUploadErrors.innerHTML = convertMessage(jobj.valuesUploadErrors);
	}

	var valueFilesUploadedMessage = document.getElementById("value_files_uploaded_message");
	if (valueFilesUploadedMessage) {
		valueFilesUploadedMessage.innerHTML = convertMessage(jobj.valueFilesUploadedMessage);
	}

	var measurementsFiletype = document.getElementById("measurements_filetype");
	if (measurementsFiletype) {
		// unfortunately there is no direct selection mechanism based on the value
		for (var i=0; i < measurementsFiletype.options.length; i++) {
			if (measurementsFiletype.options[i].value == jobj.selectedFormat)
				measurementsFiletype.options[i].selected = true;
		}
	}
	
	//--- Associated files
	
	var associatedFilesUploadPanel = document.getElementById("associatedfiles_upload_panel_wrapper");
	if (associatedFilesUploadPanel)
	{
		if(jobj.associatedFilesVisible)
			jQuery("#associatedfiles_upload_panel_wrapper").show("slow");
		else
			jQuery("#associatedfiles_upload_panel_wrapper").hide();
	}

	var associatedFilesCombinedUploadWarnings = document.getElementById("associatedfiles_combined_upload_warnings");
	if (associatedFilesCombinedUploadWarnings) {
		associatedFilesCombinedUploadWarnings.innerHTML = convertMessage(jobj.associatedFilesUploadWarnings);
	}
	
	var associatedFilesUploadedMessage = document.getElementById("associatedfiles_uploaded_message");
	if (associatedFilesUploadedMessage) {
		associatedFilesUploadedMessage.innerHTML = convertMessage(jobj.associatedFilesUploadedMessage);
	}

	var associatedFilesCombinedUploadErrors = document.getElementById("associatedfiles_combined_upload_errors");
	if (associatedFilesCombinedUploadErrors) {
		associatedFilesCombinedUploadErrors.innerHTML = convertMessage(jobj.associatedFilesUploadErrors);
	}
	
	//---
	
	var uploadHints = document.getElementById("upload_hints");
	if (uploadHints) {
		uploadHints.innerHTML = convertMessage(jobj.message);
	}

	var finishAndUploadButton = document.getElementById("finish_and_upload_button");
	if (finishAndUploadButton) {
		finishAndUploadButton.disabled = !jobj.readyToFinish;

		// change style, button <-> button_disabled
		if (jobj.readyToFinish) {
			jQuery('#finish_and_upload_button').removeClass('button_disabled').addClass('button');
		} else {
			jQuery('#finish_and_upload_button').removeClass('button').addClass('button_disabled');
		}
	}
	
	var finishButton = document.getElementById("finish_button");
	if (finishButton) {
		finishButton.disabled = !jobj.readyToFinish;

		// change style, button <-> button_disabled
		if (jobj.readyToFinish) {
			jQuery('#finish_button').removeClass('button_disabled').addClass('button');
		} else {
			jQuery('#finish_button').removeClass('button').addClass('button_disabled');
		}
	}

	var cancelButton = document.getElementById("combined_cancel_button");
	if (cancelButton) {
		cancelButton.disabled = !jobj.readyToCancel;

		// change style, button <-> button_disabled
		if (jobj.readyToCancel) {
			jQuery('#combined_cancel_button').removeClass('button_disabled').addClass('button');
		} else {
			jQuery('#combined_cancel_button').removeClass('button').addClass('button_disabled');
		}
	}
}

/**
 * Get the status from the server and update the (client) page content
 * TODO: use jQuery for Ajax request!
 */
function handleCombinedChange() {

	/* request for CombinedUploadStatusCommand */
	var requestUrl = COMBINED_UPLOAD_STATUS_REQUEST_URL;

	requestUrl += '?';

	var measurementsFiletype = document.getElementById("measurements_filetype");
	if (measurementsFiletype) {
		requestUrl += 'treeringdatafileformat=' + measurementsFiletype.value + '&';
	}

	var tridasLanguage = document.getElementById("tridas_language");
	if (tridasLanguage) {
		requestUrl += 'tridaslang=' + tridasLanguage.value + '&';
	}

	// end with the anticache
	requestUrl += 'anticache='+ escape(Math.random());

	// start request
	jQuery.ajax({
		type    : "GET",
		url     : requestUrl,
		cache   : false,
		success : function(response) { combinedStatusUpdate(response); }
	});
}

/* For browsing away */
window.onbeforeunload = function(event) {
	// Note: should not ask if we have pushed a (back, cancel or finish) button
	// g_canBrowseaway has been set if so.
	if (g_combinedUploadStatus.readyToCancel && !g_canBrowseaway)
		return "Do you really want to leave the upload procedure? This will discard the files you uploaded.";
	else
		return;	
}

 // fired when complete DOM is available
jQuery(document).ready(function(){
	   // initial update
	   handleCombinedChange();
});
