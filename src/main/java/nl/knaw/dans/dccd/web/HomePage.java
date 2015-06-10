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
package nl.knaw.dans.dccd.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import nl.knaw.dans.dccd.application.services.DccdConfigurationService;
import nl.knaw.dans.dccd.common.lang.geo.Marker;
import nl.knaw.dans.dccd.common.wicket.geo.JsonFileGeoViewer;
import nl.knaw.dans.dccd.common.wicket.geo.LazyLoadingGeoViewer;
import nl.knaw.dans.dccd.web.base.BasePage;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Homepage
 */
public class HomePage extends BasePage {
	private static Logger logger = LoggerFactory.getLogger(HomePage.class);
	private static final long serialVersionUID = 1L;
	
	public Label loadedMsgLabel = null;

	JsonFileGeoViewer viewer = null;
	public static final String GEOLOCATION_FILEPATH_KEY = "geolocation.organisations";

    /**
	 * Constructor that is invoked when page is invoked without a session.
	 *
	 * @param parameters
	 *            Page parameters
	 */
    public HomePage(final PageParameters parameters) {

    	/*
    	GeoViewer viewer = new GeoViewer("geoviewer", new ListModel<Marker>()
    			{
					private static final long	serialVersionUID	= 1L;
					@Override
					public List<Marker> getObject()
					{
						return new ArrayList<Marker>(); // empty!
					}
				});
		add(viewer);
		*/
		initGeoViewer();
		loadedMsgLabel = new Label("loadedMsgLabel", 
				new StringResourceModel("organisationLocationsPage.loadedMsg", 
						new Model(viewer), 
						new Object[]{new PropertyModel<String>(viewer, "loadedMsg")}));
		loadedMsgLabel.setOutputMarkupId(true);
		add(loadedMsgLabel);
		// should say: member organisations with a specific location:
		
		add(new BookmarkablePageLink("aboutLink", AboutPage.class));
    }

    // try getting the loading message on the screen!
	private void initGeoViewer()
	{
		// TODO get file path/name from configuration
		Properties settings = DccdConfigurationService.getService().getSettings();
		String filePath = settings.getProperty(GEOLOCATION_FILEPATH_KEY);
		File file = null;
		if (filePath == null) 
		{
			// no file to read, bail out
			logger.info("No markers read from file because No property found for: " + GEOLOCATION_FILEPATH_KEY);
		}
		else
		{
			file = new File(filePath);
		}
		
		viewer = new JsonFileGeoViewer("geoviewer", file)
		{
			private static final long serialVersionUID = -4128574220525566750L;
			@Override
			public void addMarkers(List<Marker> markers,
					AjaxRequestTarget target) {
				super.addMarkers(markers, target);
				// Markers have been added, so extra AJAX updates below
				
				// Label model is being set, so adding to the target is enough
				 target.addComponent(loadedMsgLabel);
			}	
		};
		
		add(viewer);	    
	}    

}
