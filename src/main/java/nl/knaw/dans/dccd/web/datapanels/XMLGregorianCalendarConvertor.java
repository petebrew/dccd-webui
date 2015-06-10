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
package nl.knaw.dans.dccd.web.datapanels;

import java.util.Locale;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.convert.ConversionException;

import org.apache.log4j.Logger;

public class XMLGregorianCalendarConvertor implements IConverter
{
	private static final long	serialVersionUID	= 5162774961022395709L;
	private static Logger logger = Logger.getLogger(XMLGregorianCalendarConvertor.class);
			
	@Override
	public Object convertToObject(String value, Locale locale)
	{
		logger.debug("convertToObject");

		if (value == null || value.trim().isEmpty())
		{
			throw new ConversionException("Not a valid XMLGregorianCalendar");			
		}
			
		XMLGregorianCalendar calendar = null;
		try
		{
			calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(value);
			logger.debug("converted to: " + calendar);
		}
		catch (DatatypeConfigurationException e)
		{
			// TODO Auto-generated catch block
			//e.printStackTrace();
			throw new ConversionException("'" + value + "' is not a valid XMLGregorianCalendar");
		}
		
		return calendar;
	}

	@Override
	public String convertToString(Object value, Locale locale)
	{
		logger.debug("convertToString");
		
		if (value != null)
		{
			return value.toString();
		}
		else
		{
			return null;
		}
	}

}
