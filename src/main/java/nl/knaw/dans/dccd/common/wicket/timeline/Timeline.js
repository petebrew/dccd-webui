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

/**
 * timeline functionality 
 * 
 * Uses CHAP Links Library (timeline 2.5.1)
 * http://almende.github.io/chap-links-library/timeline.html
 */
var timeline = {
	// Works with only one per page, 
	// but should have a list with JS objects for each view and have a lookup by the markup id
	Instance : null,
	// Initialize the (single) viewer
	init : function(id, options) {
		this.Instance = new Timeline(id, options);
	},

	// Add a marker at the given location on the map
	addTimeMarker : function(from, to, info) {
		this.Instance.addTimeMarker(from, to, info);
	},

	addTimeMarkers : function(TimeMarkerList) {
		this.Instance.addTimeMarkers(TimeMarkerList);
	}

};

// we have options, but these are now specific for the implementation
function Timeline(id, options) {
	var timelineElement = document.getElementById(id);

	// implementation specific: CHAP Links Library (timeline 2.5.1)
	CLtimeline = new links.Timeline(timelineElement);

	tlPopup.init(CLtimeline);
	
	// Create a JSON data table
	data = [];

	// specify default options
	var default_options = {
		'showNavigation':true, // Note that scrolling the wrapper div is not nice now
		'selectable' : false, // NO selection works better with the popups
		//'showNavigation': true, // needs to be styled...
		'showCustomTime' : true,
		'showCurrentTime' : false, // most of us know what year it is
		'minHeight' : '300', // a default default

		'zoomMin' : '100000000000', // for events in years it should be several months

		//'width':  '100%',
		//'height': '300px',
		//'start': new Date(-500, 0, 1), 
		//'end': new Date(2050, 11, 31),

		// maybe disable clustering, sometimes we can't uncluster by zooming in?
		'cluster' : true, // always cluster won't hurt
		'axisOnTop' : true, // better when scrolling needed and consistent with optional grouping
		'editable' : false
	// we don't want editing, just viewing
	};
	// it can grow and a container could then add scrollbars, 
	// but we don't want it to shrink smaller then initially given height
	default_options['minHeight'] = timelineElement.clientHeight;

	// set given options
	options = options || {};
	for ( var opt in default_options)
		if (default_options.hasOwnProperty(opt) && !options.hasOwnProperty(opt))
			options[opt] = default_options[opt];

	// Draw our timeline with the created data and options
	CLtimeline.draw(data, options);

	/////////////////Functions///////////////////

	// from and to are strings representing dates
	// info is a string describing or labeling the marker
	this.addTimeMarker = function(from, to, info) {
		// assume that from and to are in milliseconds 
		// from 1970-01-01T00:00:00Z UTC (and originally iso-8601)
		// year zero exists (1BC), but maybe this is different for IE?

		//		var content = info;
		var idx = CLtimeline.getData().length;
		var content = "<div class=\"tlDataContent\">"
				+ info
				+ "</div>"
				+ "<div class=\"tlPopup-show\" onmousedown=\"tlPopup.show(event, "
				+ idx + ")\"></div>";

		var newEvent = {
			'start' : new Date(from),
			'end' : new Date(to),
			'content' : content
		};
		CLtimeline.addItem(newEvent, false);
	};

	this.addTimeMarkers = function(TimeMarkerList) {
		// Note: could translate whole list and then add items in one call
		for ( var i = 0; i < TimeMarkerList.length; i++) {
			var m = TimeMarkerList[i];
			this.addTimeMarker(m.from, m.to, m.info);
		}

		// always zoom in on data... not sure if I always want this
		// could be more efficient to calculate it on the server once only initially
		CLtimeline.setVisibleChartRangeAuto();
	};
}

/** popup (or balloon) for the timeline events
 * there is only one popup element and it will 'popup' when show is called
 * no jQuery, just plain JS.
 * 
 * This popup code is needed because the implementation of the timeline (CHAP Links) 
 * is lacking it. 
 * It might be possible to add this to the Github, 
 * therfore it's just plain Javascript code and no jQuery
 *  
 */
tlPopup = (function() {
	var dom; // (= {};) don't assign so it shows we need to initialize
	var timeline;
	
	
	function show(event, idx) {
		if (!dom || !timeline) return; // make sure it is initialized

		// toggle?
		if (dom.style.visibility === "visible") {
			hide();
			return;
		}

		var event = event || window.event;
		var x = 0;
		var y = 0;
		
		// handle scrolling
		if ((event.clientX || event.clientY) &&
				document.body &&
				document.body.scrollLeft!=null) {
			x = event.clientX + document.body.scrollLeft;
			y = event.clientY + document.body.scrollTop;
		}
		if ((event.clientX || event.clientY) &&
				document.compatMode=='CSS1Compat' && 
				document.documentElement && 
				document.documentElement.scrollLeft!=null) {
			x = event.clientX + document.documentElement.scrollLeft;
			y = event.clientY + document.documentElement.scrollTop;
		}
		if (event.pageX || event.pageY) {
			x = event.pageX;
			y = event.pageY;
		}
		
		var target = event.target || event.srcElement;
		if (target.nodeType == 3)
			target = target.parentNode; // defeat Safari bug?

		if (event.stopPropagation)
			event.stopPropagation();
		else
			event.cancelBubble = true;

		// get the event item with the dates etc.
		// NOTE we use the timeline variable
		//var item = CLtimeline.getItem(idx);
		var item = timeline.getItem(idx);
		var timerange = "<hr/><div>" + item.start + " <br/> " + item.end
				+ "</div>";

		// adjust bubble position -(22/2 + 6), +18
		dom.style.left = (x + -17).toString() + "px";
		dom.style.top = (y + 18).toString() + "px";
		//popup.innerHTML = target.innerHTML; // this must be done in a content div!
		//    popup.children[0].innerHTML = target.innerHTML; 
		// get the div before the taget div?
		//var prev = target.previousElementSibling; // but this does not work on old browsers
		var prev = target;
		do
			prev = prev.previousSibling;
		while (prev && prev.nodeType !== 1);
		dom.children[0].innerHTML = prev.innerHTML + timerange;

		dom.style.visibility = "visible";
	}

	function hide() {
		if (!dom) return; // make sure it is initialized
		dom.style.visibility = "hidden";
	}

	// call this after dom has been loaded
	function init(t) {
		timeline = t;
		if (dom) return; // prevent initializion of DOM element more then once
		
		// create element to display
		dom = document.createElement("div");
		dom.className = "tlPopup-container";

		// for the content, inner Html set i show function
		content = document.createElement("div");
		content.className = "tlPopup-content";
		dom.appendChild(content);
		// is this the first child

		// point
		point = document.createElement("div");
		point.className = "tlPopup-point";
		dom.appendChild(point);

		// set style
		dom.style.visibility = "hidden"; // don't show it yet
		dom.style.zIndex = "9999"; // and when you show it always on top (hopefully)

		// insert into page DOM
		document.body.insertBefore(dom, document.body.firstChild);

		document.body.onmousedown = function() {
			hide();
		};
		// try to support touch events
		//document.body.touchstart = function() {
		//	hide();
		//};

		// zoom/scroll should also hide
		document.body.onmousewheel = function() {
			hide();
		};
		// for FF
		document.body.onwheel = function() {
			hide();
		};
				
		// when clicking the popup, don't hide
		dom.onmousedown = function() {
			if (event.stopPropagation)
				event.stopPropagation();
			else
				event.cancelBubble = true;			
		};
	}

	// expose 'public' stuff
	return {
		init : init,
		show : show,
		hide : hide
	};
})(); // tlPopup
