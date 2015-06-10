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
package nl.knaw.dans.dccd.common.wicket.timeline;

import java.util.List;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.value.ValueMap;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Timeline extends Panel 
{
	private static final Logger	logger	= LoggerFactory.getLogger(Timeline.class);
	private static final long serialVersionUID = -3408341332497634438L;
	// our own JavaScript code
	private static final ResourceReference TIMELINE_JS = new ResourceReference(Timeline.class, "Timeline.js");

	public Timeline(String id, IModel<List<TimeMarker>> model) 
	{
		super(id, model);

		this.setOutputMarkupId(true);
		add(new TimelineBehavior());
	}
	
	class TimelineBehavior extends AbstractBehavior
	{
		private static final long serialVersionUID = 7529245448709549402L;
	
		@Override
		public void renderHead(IHeaderResponse response) {
			// load and or init js (and css) needed
			// the libraries we need
			// NOTE should use timeline-min.js for performance improvement
			response.renderJavascriptReference(new ResourceReference(Timeline.class, "chaplinks/timeline.js"));
			response.renderCSSReference(new ResourceReference(Timeline.class, "chaplinks/timeline.css"));

			// Timeline specific css, 'on top' of chaplibs
			response.renderCSSReference(new ResourceReference(Timeline.class, "timeline.css"));

			// our own js code
			response.renderJavascriptReference(TIMELINE_JS);
		
			// get the markers...
			String markersJS = "";
			if (Timeline.this.getDefaultModel() != null)
			{
				@SuppressWarnings("unchecked")
				List<TimeMarker> modelMarkers = (List<TimeMarker>)Timeline.this.getDefaultModel().getObject();
				markersJS = getMarkersJS(modelMarkers);
			}
			
			// Initialize
			response.renderOnDomReadyJavascript(getInitializationJS() 
					+ markersJS); 
			
			super.renderHead(response);
		}
	}
	
	//--- script code generators below ---
	
	protected String getInitializationJS()
	{
		String id = getMarkupId();
		logger.debug("Timeline - Initializing: " + id);
		String script = "";

		// call JS function for init of the "GeoViewer" object
		//script += "timeline.init(\'" + id + "\');\n";
		script += "timeline.init(\'" + id + "\'" 
		// could add options, but those are implementation specific
		//+ ",{\'minHeight\':\'100\'}" // options 
		+ ");\n";
		
		return script;
	}
	
	private static String getMarkersJS(List<TimeMarker> markers)
	{
		StringBuilder script = new StringBuilder();

		if (markers.isEmpty())
			return ""; // no markers

		DateTimeFormatter dtf = ISODateTimeFormat.dateTime(); // to put the dates in JSON
		script.append("timeline.addTimeMarkers([");
		boolean isFirst = true;
		for(TimeMarker marker : markers)
		{
			if(isFirst)
				isFirst = false;
			else
				script.append(",");

			/*			
			// Convert dates to UTC, just to be sure
			DateTime dUtc = marker.getFrom().toDateTime(DateTimeZone.UTC);
			// and make them into ISO strings
			String fromStr = dtf.print(dUtc);
			dUtc = marker.getTo().toDateTime(DateTimeZone.UTC);
			String toStr = dtf.print(dUtc);
			script.append("{"+ 
					"from:" +"\"" + fromStr + "\"" + "," + 
					"to:" +"\"" + toStr + "\"" + ","+ 
					"info:" +"\"" + marker.getInfo()+ "\"" + "}");
			 */
			
			// Convert dates to UTC, just to be sure
			DateTime dUtc = marker.getFrom().toDateTime(DateTimeZone.UTC);
			long from = dUtc.getMillis();
			dUtc = marker.getTo().toDateTime(DateTimeZone.UTC);
			long to = dUtc.getMillis();		
			script.append("{"+ 
					"from:" + from + "," + 
					"to:"  + to + ","+ 
					"info:" +"\"" + marker.getInfo()+ "\"" + "}");
		}
		script.append("]);");
		return script.toString();
	}
}
