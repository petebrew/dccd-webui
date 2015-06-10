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
package nl.knaw.dans.dccd.common.wicket.geo;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.dccd.common.lang.geo.LonLat;
import nl.knaw.dans.dccd.common.lang.geo.Marker;
import nl.knaw.dans.dccd.common.wicket.geo.markericons.MarkerIconRef;

import org.apache.wicket.Component;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Note: the panel must be placed in a html-div element, and it's size can be set by using CSS on that div
 *
 */
public class GeoViewer extends Panel
{
	private static final Logger	logger	= LoggerFactory.getLogger(GeoViewer.class);
	private static final long	serialVersionUID	= 2436241416837489208L;
	private static final ResourceReference GEOVIEWER_JS = new ResourceReference(GeoViewer.class, "GeoViewer.js");

	private List<Marker> markers;
	private boolean initialMarkers = false;
	
	// A modelless constructor, just for displaying a map
	public GeoViewer(String id)
	{
		super(id);
		
		// No Model, so create empty list, markers can be added later on via Ajax
		markers = new ArrayList<Marker>();
		
		this.setOutputMarkupId(true);
		add(new GeoViewerBehavior());
	}

	public GeoViewer(String id, IModel<List<Marker>> model)
	{
		super(id, model);
		logger.debug("GeoViewer - Constructing WITH a model");
		initialMarkers = true;
		
		// get the locations from the model
		//List<LonLat> modelLocations = model.getObject();
		//markerLocations = modelLocations;
		
		// Note: the model is retrieved in the Behaviour renderHead
		
		this.setOutputMarkupId(true);
		add(new GeoViewerBehavior());
	}

	// Note we should remove this one before commonizing!
	@Deprecated
	public GeoViewer(String id, List<Marker> markers)
	{
		super(id);
		logger.debug("GeoViewer - Constructing without a model");
		initialMarkers = true;
	
		this.markers = markers;
		
		this.setOutputMarkupId(true);
		add(new GeoViewerBehavior());
	}

	class GeoViewerBehavior extends AbstractBehavior
	{
		private static final long	serialVersionUID	= -7899259933312363881L;

		@Override
		public void onRendered(Component component)
		{
			logger.debug("GeoViewerBehavior - onRendered");

			AjaxRequestTarget target = AjaxRequestTarget.get();
			if (target != null)
			{
				target.appendJavascript(getMarkersJS(markers));
			}
			
			super.onRendered(component);
		}

		@Override
		public void renderHead(IHeaderResponse response)
		{
			logger.debug("GeoViewerBehavior - renderHead");

			// GeoViewer specific css, 'on top' of openlayers
			response.renderCSSReference(new ResourceReference(GeoViewer.class, "geoviewer.css"));
			
			//response.renderJavascriptReference("http://www.openlayers.org/api/OpenLayers.js");
			response.renderJavascriptReference(new ResourceReference(GeoViewer.class, "openlayers/OpenLayers.js"));
			response.renderJavascriptReference(GEOVIEWER_JS);
			
			// get the locations from the model, 
			// which might have changed after construction of the GeoViewer
			if (GeoViewer.this.getDefaultModel() != null)
			{
				List<Marker> modelLocations = (List<Marker>)GeoViewer.this.getDefaultModel().getObject();
				if (modelLocations != null)
					markers = modelLocations;
			}
			
			if (initialMarkers) 
			{
				// Initialize the viewer and also show initial markers
				response.renderOnDomReadyJavascript(getInitializationJS() + getMarkersJS(markers));
			}
			else
			{
				response.renderOnDomReadyJavascript(getInitializationJS());
				// the next line is 'commented out' because 
				// 'reload this page' on browser should bring page back to initial state!
				// so any markers that where dynamically edited are lost
				//response.renderOnLoadJavascript(getMarkersJS(markers));
			}
			
			super.renderHead(response);
		}
	}
	
	// Note about adding and clearing markers:
	// These are also added to the list of locations,
	// but I am not sure if that is stored in the Model (when we had one)
	//
	// The scenario where the locations are added to the model (outside this class)
	// and then the object is updated via Ajax needs to be checked!!!
	
	// Note:  clearLocations and addLocation only update markers and not the whole view
	public void addMarker(Marker marker, AjaxRequestTarget target)
	{
		markers.add(marker);
		
		//String script = "geoViewer.addMarker("+ marker.getLon() +","+ marker.getLat() + ");";
		String script = "geoViewer.addMarker("+ 
			marker.getLon() +","+ 
			marker.getLat() +","+ 
			"\'" + marker.getInfo()+ "\'" + ");";

		// TODO cleanup the markerInfo string for Javascript
		// replace newlines and escape quotes, also htmlEscape utf8 chars
		
		
		target.appendJavascript(script);
	}
	
	public void addMarkers(List<Marker> markers, AjaxRequestTarget target)
	{
		this.markers.addAll(markers);
		
		target.appendJavascript(getMarkersJS(markers));
	}
	
	public void clearMarkers()
	{
		markers.clear(); // remove from list
		
		AjaxRequestTarget target = AjaxRequestTarget.get();
		if (target != null)
		{
			target.appendJavascript(getClearMarkersJS());
		}		
	}
	
	public boolean hasMarkers ()
	{
		return !markers.isEmpty();
	}
	
	public static ResourceReference getDefaultMarkerIconImageReference()
	{
		//ResourceReference iconRef = new ResourceReference(GeoViewer.class, "markericons/marker.png");
		ResourceReference iconRef = new MarkerIconRef("marker.png");
		
		//logger.debug("icon URL: " + urlFor(iconRef));
		//System.out.println("icon URL: " + urlFor(iconRef));
		return iconRef;
	}

	// Note that the Javascript code must implement the same mapping from index to image!
	public static ResourceReference getLetterMarkerIconImageReference(int markerIndex)
	{
		if (markerIndex < 0) throw new IllegalArgumentException("Marker index cannot be negative");
		
		ResourceReference iconRef = null;
		
		if (markerIndex < 26)
		{
			// use a letter
			// Capitals from A-Z, ascii code
			char c = (char)(65+markerIndex);
			//System.out.println(i + " -> " + c);
			String letter = Character.toString(c);
			iconRef = new MarkerIconRef("marker_" + letter + ".png");
		}
		else
		{
			// use the default
			iconRef = getDefaultMarkerIconImageReference();
		}
		
		return iconRef;
	}

	
	//--- script code generators below ---
	
	protected String getInitializationJS()
	{
		String id = getMarkupId();
		logger.debug("GeoViewer - Initializing: " + id);
		String script = "";

		// set global setting for icons/images of all viewers
		script += "geoViewer.DefaultMarkerIconPath = \'" + 
					urlFor(getDefaultMarkerIconImageReference()) + 
					"\';\n";
		
		// call JS function for init of the "GeoViewer" object
		script += "geoViewer.init(\'" + id + "\');\n";
		
		return script;
	}
	
//	private static String getMarkersJS(List<Marker> markers)
//	{
//		StringBuilder script = new StringBuilder();
//
//		for(Marker marker : markers)
//		{
//			//script.append("geoViewer.addMarker("+ marker.getLon() + "," + marker.getLat() + ");");
//			script.append("geoViewer.addMarker("+ 
//					marker.getLon() + "," + 
//					marker.getLat() +","+ 
//					"\"" + marker.getInfo()+ "\"" + ");");
//		}
//		
//		return script.toString();
//	}
	private static String getMarkersJS(List<Marker> markers)
	{
		StringBuilder script = new StringBuilder();

		if (markers.isEmpty())
			return ""; // no markers

		script.append("geoViewer.addMarkers([");
		boolean isFirst = true;
		for(Marker marker : markers)
		{
			if(isFirst)
				isFirst = false;
			else
				script.append(",");
			
			//script.append("geoViewer.addMarker("+ marker.getLon() + "," + marker.getLat() + ");");
			script.append("{"+ 
					"lon:" + marker.getLon() + "," + 
					"lat:" + marker.getLat() +","+ 
					"info:" +"\"" + marker.getInfo()+ "\"" + "}");
		}
		script.append("]);");
		return script.toString();
	}

	
	private static String getClearMarkersJS()
	{
		return "geoViewer.clearMarkers();";
	}
}
