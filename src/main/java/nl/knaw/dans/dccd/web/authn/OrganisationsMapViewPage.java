package nl.knaw.dans.dccd.web.authn;

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
import nl.knaw.dans.dccd.common.wicket.geo.LazyLoadingGeoViewer;
import nl.knaw.dans.dccd.web.DccdSession;
import nl.knaw.dans.dccd.web.HomePage;
import nl.knaw.dans.dccd.web.base.BasePage;

import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
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
 * Show locations (if available) of the organisations on an interactive map
 * Only show active organisations, and thus no need for logging in. 
 * 
 * @author paulboon
 *
 */
public class OrganisationsMapViewPage extends BasePage 
{
	private static Logger logger = LoggerFactory.getLogger(OrganisationsMapViewPage.class);
	private Page backPage;
	LazyLoadingGeoViewer viewer = null;
	public static final String GEOLOCATION_FILEPATH_KEY = "geolocation.organisations";

	public Label loadedMsgLabel = null;
	private String loadedMsg="";
			
	public String getLoadedMsg() {
		return loadedMsg;
	}

	public void setLoadedMsg(String loadedMsg) {
		this.loadedMsg = loadedMsg;
	}

	public OrganisationsMapViewPage() {
		super();
		init();
	}
	
	private void init()
	{
		//loadedMsgLabel = new Label("loadedMsgLabel", new PropertyModel<String>(this, "loadedMsg"));
		loadedMsgLabel = new Label("loadedMsgLabel", 
				new StringResourceModel("organisationLocationsPage.loadedMsg", 
						new Model(this), 
						new Object[]{new PropertyModel<String>(this, "loadedMsg")}));
		loadedMsgLabel.setOutputMarkupId(true);
		add(loadedMsgLabel);
		
	    // get the back page
    	backPage = ((DccdSession)Session.get()).getRedirectPage(getPage().getClass());

    	// Note on back page: it seems a bit to complex because it is always the same page with 
    	// the organisations on it, 
    	// but when the map view is linked from another page this might still work.
    	// TODO hide link if the backpage is not the list of organisations
    	
    	// The back button or link
		Link backButton = new Link("backButton", new ResourceModel("backButton"))
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick()
			{
		        // get the previous page, and try to browse back
		        Page page = backPage;
		        if (page != null)
		        {
		        	if (page instanceof BasePage)
	        			((BasePage)page).refresh();
		        	setResponsePage(page);
		        }
		        else
		        {
		        	// just go back to a new instance of HomePage
		        	setResponsePage(HomePage.class);
		        }
			}
		};
		add(backButton);
		
	    initGeoViewer();        
	 }

	private void initGeoViewer()
	{
		// Add GeoViewer
		viewer = new LazyLoadingGeoViewer("geoviewer") {
			private static final long serialVersionUID = 2887353735740669595L;

			@Override
			protected List<Marker> produceMarkers() {
				List<Marker> markerList = new ArrayList<Marker>();
						
				//String filePath = "/Users/paulboon/Documents/Development/dccd/dccd-rest/python-tests/geolocation_organisations.json";
				//String filePath = "/Users/paulboon/Sites/gis/new/geolocation_organisations.json";
				// TODO get file path/name from configuration
				Properties settings = DccdConfigurationService.getService().getSettings();
				String filePath = settings.getProperty(GEOLOCATION_FILEPATH_KEY);
				if (filePath == null) 
				{
					// no file to read, bail out
					logger.info("No markers read from file because No property found for: " + GEOLOCATION_FILEPATH_KEY);
					return markerList;						
				}
				File file = new File(filePath);
				if (!file.canRead())
				{
					// no file to read, bail out silently?
					logger.error("No markers read from file because File could not be read: " + filePath);
					return markerList;
				}

				// load the file and construct the markers by parsing the json
	
				// Show timestamp of file, indicating when the locations where collected
				// maybe also show how many organisations are displayed?
				// convert to UTC and format
				DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy.MM.dd - HH:mm:ss z");//ISODateTimeFormat.dateTime();
				DateTime dUtc = new DateTime(file.lastModified()).toDateTime(DateTimeZone.UTC);
				setLoadedMsg(dUtc.toString(fmt).toString());
				
				String jsonStr = "";
				try {
					jsonStr = readTextFile(file);
				} catch (Exception e1) {
					e1.printStackTrace();
					logger.error("No markers read from file because File could not be read: " + filePath);
					// unable to read the file as text
					return markerList;
				}
				
				try {
					JSONObject rootObject = new JSONObject(jsonStr);
					// TODO what if we have only one....
					JSONArray markerArray = rootObject.getJSONArray("markers");
					for(int i=0; i < markerArray.length(); i++) { // Loop over each each row
		                JSONObject o = markerArray.getJSONObject(i);
		                //System.out.println(o);
		                String info = o.getString("info");
		                Double lon = o.getDouble("lon");
		                Double lat = o.getDouble("lat");
		                markerList.add(new Marker(lon, lat, info));
					}
				} catch (JSONException e) {
					// unable to parse the json 
					e.printStackTrace();
					// nothing, just return what is in the list sofar
					logger.error("Stopped parsing json from " + filePath + " , number of markers found sofar: " + markerList.size() );
				}
				
				return markerList;
			}

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
	
	public static String readTextFile(File file) throws Exception
	{ 
		StringBuffer buffer = new StringBuffer();
		BufferedReader in = null;
		try {
			in = new BufferedReader(
			   new InputStreamReader(
			              new FileInputStream(file), "UTF8"));
			
			String line;
			while ((line = in.readLine()) != null) {
			    //System.out.println(line);
			    buffer.append(line);
			}
		} catch (UnsupportedEncodingException e) {
			throw new Exception(e);
		} catch (FileNotFoundException e) {
			throw new Exception(e);
		} catch (IOException e) {
			throw new Exception(e);
		}
		finally
		{
	        if (in!=null)
				try {
					in.close();
				} catch (IOException e) {}		
		}
        
        return buffer.toString();
	}
}
