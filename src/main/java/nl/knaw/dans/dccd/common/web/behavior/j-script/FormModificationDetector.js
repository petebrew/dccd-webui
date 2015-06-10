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
//FormModificationDetector.js

var formOnRender;
var detectFormModification = true;
var prevOnLoad;
var form_id = "${form_id}";
var last_form_id = null; // hack to prevent recusrion

if (window.onload)
	prevOnLoad = window.onload;

function setDetectFormModification(flag) {
	detectFormModification = flag;
	//console.debug("detection set to = " + detectFormModification);
}

function getFormWatched() {
	return document.getElementById(form_id);
}

function setFormOnRender() {
	var formWatched = getFormWatched();
	if (formWatched == null) {
		return;
	}
	formOnRender = Wicket.Form.doSerialize(formWatched);
}

window.onload = function() {
	var formWatched = getFormWatched();
	if (form_id != last_form_id){
		last_form_id = form_id;
	} else {
		alert("recursive onload "+form_id);
		return;
	}
	if (formWatched == null) {
		return;
	}
	if (prevOnLoad)
		prevOnLoad();
	var prevOnSubmit;
	if (formWatched.onsubmit)
		prevOnSubmit = formWatched.onsubmit;

	formWatched.onsubmit = function() {
		//console.debug("on submit called");		
		if (prevOnSubmit)
			prevOnSubmit();
// Why would we need to set the detection to false after a submit?
// if we have further changes (AJAX), we want to detect that, don't we?
//		setDetectFormModification(false);
//formOnRender = Wicket.Form.doSerialize(formWatched);
setFormOnRender();
		
		return true;
	};

	// retrieve the form values. 'Wicket.Form.doSerialize'
	// function is defined in wicket-ajax.js.
	formOnRender = Wicket.Form.doSerialize(formWatched);
	//console.debug(formOnRender);
	
	last_form_id = null;
}

var prevOnBeforeUnload;
if (window.onbeforeunload)
	prevOnBeforeUnload = window.onbeforeunload;

// Before the windown unloads, check for any modifications
window.onbeforeunload = function(event) {
	//console.debug("on beforeunload called");
	var formWatched = getFormWatched();
	if (formWatched == null) {
		//console.debug("Nothing watched!");
		return;
	}
	if (prevOnBeforeUnload)
		prevOnBeforeUnload();
	
	//console.debug("detection = " + detectFormModification);

	if (detectFormModification) {
		formBeforeSubmit = Wicket.Form.doSerialize(formWatched);
		//console.debug(formBeforeSubmit);
		
		if (formOnRender != formBeforeSubmit) {
			return "${message}";
		}
	}
};
