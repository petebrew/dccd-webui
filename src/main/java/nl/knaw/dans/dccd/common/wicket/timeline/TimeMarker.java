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

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Marks an occurrence; e period in time when something happened. 
 * The name 'Event' is also commonly being used in GUI code, 
 * so it is an 'TimeMarker' to avoid ambiguity.   
 */
public class TimeMarker 
{
	private static final Logger	logger	= LoggerFactory.getLogger(TimeMarker.class);
	
	// Note: was using years as integers instead of true dates.
	// Also this could be a separate object; period which would be a range...
	private DateTime from;
	private DateTime to;
	
	// descriptive information about the event
	private String info;

	public TimeMarker(DateTime from, DateTime to, String info)
	{
		this.from = from;
		this.to = to;
		this.info = info;
	}
	
	// allow years for construction
	public TimeMarker(Integer fromYear, Integer toYear, String info)
	{
		// Note: negative years are BC and 0 is 1BC
		// ISOChronology handles this correctly
		
		// start of year
		this.from = new DateTime(fromYear.intValue(), 1, 1, 0, 0, 0, 0, DateTimeZone.UTC); 	
		// end of year
		this.to = new DateTime(toYear.intValue(), 12, 31, 23, 59, 59, 999, DateTimeZone.UTC);
		
		this.info = info;
	}
	
	public DateTime getFrom() 
	{
		return from;
	}
	
	public void setFrom(DateTime from) 
	{
		this.from = from;
	}
	
	public DateTime getTo() 
	{
		return to;
	}
	
	public void setTo(DateTime to) 
	{
		this.to = to;
	}
	
	public String getInfo() 
	{
		return info;
	}
	
	public void setInfo(String info) 
	{
		this.info = info;
	}
}
