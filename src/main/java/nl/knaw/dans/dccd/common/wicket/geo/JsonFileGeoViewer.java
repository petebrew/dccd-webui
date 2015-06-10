package nl.knaw.dans.dccd.common.wicket.geo;

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

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.knaw.dans.dccd.common.lang.geo.Marker;

public class JsonFileGeoViewer extends LazyLoadingGeoViewer
{
	private static final Logger	logger	= LoggerFactory.getLogger(JsonFileGeoViewer.class);
	private static final long serialVersionUID = -3211260595124035800L;
	
	File jsonFile;
	private String loadedMsg="";
	
	public String getLoadedMsg() {
		return loadedMsg;
	}

	public void setLoadedMsg(String loadedMsg) {
		this.loadedMsg = loadedMsg;
	}

	public JsonFileGeoViewer(String id, File file) {
		super(id);
		this.jsonFile = file;
	}

	@Override
	protected List<Marker> produceMarkers() {
		List<Marker> markerList = new ArrayList<Marker>();
		if (jsonFile == null)
		{
			logger.error("No markers read from file because no file specified");
			return markerList;			
		}
		if (!jsonFile.canRead())
		{
			// no file to read, bail out silently?
			logger.error("No markers read from file because File could not be read: " + jsonFile.getAbsolutePath());
			return markerList;
		}

		// load the file and construct the markers by parsing the json

		// Show timestamp of file, indicating when the locations where collected
		// maybe also show how many organisations are displayed?
		// convert to UTC and format
		DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy.MM.dd - HH:mm:ss z");//ISODateTimeFormat.dateTime();
		DateTime dUtc = new DateTime(jsonFile.lastModified()).toDateTime(DateTimeZone.UTC);
		setLoadedMsg(dUtc.toString(fmt).toString());
		
		String jsonStr = "";
		try {
			jsonStr = readTextFile(jsonFile);
		} catch (Exception e1) {
			e1.printStackTrace();
			logger.error("No markers read from file because File could not be read: " + jsonFile.getAbsolutePath());
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
			logger.error("Stopped parsing json from " + jsonFile.getAbsolutePath() + " , number of markers found sofar: " + markerList.size() );
		}
		
		return markerList;
	}

	public static String readTextFile(File textFile) throws Exception
	{ 
		StringBuffer buffer = new StringBuffer();
		BufferedReader in = null;
		try {
			in = new BufferedReader(
			   new InputStreamReader(
			              new FileInputStream(textFile), "UTF8"));
			
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
