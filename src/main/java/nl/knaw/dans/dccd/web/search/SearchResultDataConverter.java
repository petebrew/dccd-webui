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
package nl.knaw.dans.dccd.web.search;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.search.SearchHit;
import nl.knaw.dans.common.lang.search.SearchResult;
import nl.knaw.dans.common.lang.search.bean.StringListCollapserConverter;
import nl.knaw.dans.common.lang.search.exceptions.SearchBeanConverterException;
import nl.knaw.dans.dccd.model.DccdUser;
import nl.knaw.dans.dccd.model.ProjectPermissionLevel;
import nl.knaw.dans.dccd.search.DccdProjectSB;
import nl.knaw.dans.dccd.search.DccdSB;
import nl.knaw.dans.dccd.util.StringUtil;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Note: converts to XML strings
 *
 */
public class SearchResultDataConverter
{
	private static final Logger	logger = LoggerFactory.getLogger(SearchResultDataConverter.class);

	private static final String VALUE_NOT_PERMITTED = "not permitted";
	private static final String NAME_RESULT = "result";
	private static final String NAME_PROJECT_LAB = "projectLab";
	private static final String NAME_PROJECT_ID = "projectIdentfier";
	private static final String NAME_PROJECT_TITLE = "projectTitle";
	private static final String NAME_OBJECT_TITLE = "objectTitle";
	private static final String NAME_OBJECT_TYPE = "objectType";
	private static final String NAME_LONGITUDE = "longitude";
	private static final String NAME_LATITUDE = "latitude";
	private static final String NAME_ELEMENT_TITLE = "elementTitle";
	private static final String NAME_ELEMENT_TYPE = "elementType";
	private static final String NAME_ELEMENT_TAXON = "elementTaxon";
	private static final String NAME_SERIES_FIRSTYEAR = "measurementSeriesFirstYear";
	private static final String NAME_SERIES_LASTYEAR = "measurementSeriesLastYear";
	private static final String NAME_SERIES_PITHYEAR = "measurementSeriesPithYear";
	private static final String NAME_SERIES_DEATHYEAR = "measurementSeriesDeathYear";
	private static final String	NAME_SERIES_ID	= "measurementSeriesIdentifier";
	
	// Note that it is done without an XML lib, but could use the DOM4J XMLWriter
	public static String getResultsAsXML(final SearchResult<? extends DccdSB> searchResults, final DccdUser user)
	{
		java.io.StringWriter sw = new StringWriter();

		int index = 0; // for logging
		for(SearchHit<? extends DccdSB> hit : searchResults.getHits())
		{
			DccdSB dccdSB = hit.getData();
			index++;
			// Determine if it is permitted to show it
			logger.debug("Hit(" + index + "): " + dccdSB.getId() + " level: " + dccdSB.getPermissionDefaultLevel());
			
			sw.append("<" + NAME_RESULT + ">");
			// project
			sw.append(getXMLElementString(NAME_PROJECT_LAB, getProjectLabString(dccdSB)));
			sw.append(getXMLElementString(NAME_PROJECT_ID, getProjectIdentfierString(dccdSB)));
			sw.append(getXMLElementString(NAME_PROJECT_TITLE, getProjectTitleString(dccdSB)));
			// object
			sw.append(getXMLElementString(NAME_OBJECT_TITLE, getObjectTitleString(dccdSB)));
			sw.append(getXMLElementString(NAME_OBJECT_TYPE, getObjectTypeString(dccdSB)));
			
			// Location info
			// The location is from the ObjectEntity 'Level'
			// allow only if admin, or owner, or level is "object" or better...
			ProjectPermissionLevel effectivelevel = dccdSB.getEffectivePermissionLevel(user);
			Boolean isAllowedToViewLocation =  ProjectPermissionLevel.OBJECT.isPermittedBy(effectivelevel);
			sw.append(getLocationXML(dccdSB, isAllowedToViewLocation));
			
			// Detailed info below
			// Note that we need to specify if the data is there but we are not allowed to see it
			// and what do we do with multiple instances; comma seperated in Solr!
			sw.append(getDetailedXML(dccdSB, effectivelevel));
			
			sw.append("</" + NAME_RESULT + ">");
		}

		return sw.toString();
	}
	
	// get from Object level up to the Series when allowed to
	private static String getDetailedXML(final DccdSB dccdSB, final ProjectPermissionLevel effectivelevel)
	{
		java.io.StringWriter sw = new StringWriter();
		
		// add extra object level information
		// type, title and location is allready handled
		// so nothing more, just go into the element level
		sw.append(getElementXML(dccdSB, effectivelevel));
		sw.append(getSeriesXML(dccdSB, effectivelevel));
		
		return sw.toString();
	}
	
	private static String getElementXML(final DccdSB dccdSB, final ProjectPermissionLevel effectivelevel)
	{
		java.io.StringWriter sw = new StringWriter();
		
		if (ProjectPermissionLevel.ELEMENT.isPermittedBy(effectivelevel))
		{
			// element.title, type, taxon
			sw.append(getXMLElementString(NAME_ELEMENT_TITLE, getElementTitleString(dccdSB)));
			sw.append(getXMLElementString(NAME_ELEMENT_TYPE, getElementTypeString(dccdSB)));
			sw.append(getXMLElementString(NAME_ELEMENT_TAXON, getElementTaxonString(dccdSB)));
		}
		else
		{
			// not permitted
			sw.append(getXMLElementString(NAME_ELEMENT_TITLE, VALUE_NOT_PERMITTED));
			sw.append(getXMLElementString(NAME_ELEMENT_TYPE, VALUE_NOT_PERMITTED));
			sw.append(getXMLElementString(NAME_ELEMENT_TAXON, VALUE_NOT_PERMITTED));
		}
		
		return sw.toString();
	}

	private static String getSeriesXML(final DccdSB dccdSB, final ProjectPermissionLevel effectivelevel)
	{
		java.io.StringWriter sw = new StringWriter();
		
		if (ProjectPermissionLevel.SERIES.isPermittedBy(effectivelevel))
		{
			// measurementseries.firstyear, lastyear, pithyear, deathyear
			//sw.append(getXMLElementString(NAME_SERIES_ID, getSeriesIdentfierString(dccdSB)));
			// NOTE Series ID was always empty, not sure why
			sw.append(getXMLElementString(NAME_SERIES_FIRSTYEAR, getFirstYearString(dccdSB)));
			sw.append(getXMLElementString(NAME_SERIES_LASTYEAR, getLastYearString(dccdSB)));
			sw.append(getXMLElementString(NAME_SERIES_PITHYEAR, getPithYearString(dccdSB)));
			sw.append(getXMLElementString(NAME_SERIES_DEATHYEAR, getDeathYearString(dccdSB)));
			
			// TODO ? woodcompleteness.nrofrings , nrofsapwoodrings
			//getTridasRadiusWoodcompletenessSapwoodNrofsapwoodrings()
			// but .nrofrings is not indexed right now!
			// woodcompleteness is available on Radius and Series level, but indexed on radius?
			// however just show it under series, using this deeper (saver) permission level!
			// Ehhhh, woodcompleness is only indexed with data from Radius level, not series...
			// so this would need changing a lot of code and reindex...
		}
		else
		{
			// not permitted
			//sw.append(getXMLElementString(NAME_SERIES_ID, VALUE_NOT_PERMITTED));
			// NOTE Series ID was always empty, not sure why
			sw.append(getXMLElementString(NAME_SERIES_FIRSTYEAR, VALUE_NOT_PERMITTED));
			sw.append(getXMLElementString(NAME_SERIES_LASTYEAR, VALUE_NOT_PERMITTED));
			sw.append(getXMLElementString(NAME_SERIES_PITHYEAR, VALUE_NOT_PERMITTED));
			sw.append(getXMLElementString(NAME_SERIES_DEATHYEAR, VALUE_NOT_PERMITTED));
		}
		
		return sw.toString();
	}
	
	private static String getLocationXML(final DccdSB dccdSB, final boolean isAllowedToViewLocation)
	{
		java.io.StringWriter sw = new StringWriter();
		
		// Get the Lat and Lng 
		if (dccdSB.hasLatLng())
		{
			//logger.debug("Geo location: (" + dccdSB.getLng() + "," + dccdSB.getLat() + ")");
			if (isAllowedToViewLocation)
			{
				// Note, not sure if we are certain to have decimal '.' ???
				sw.append(getXMLElementString(NAME_LONGITUDE, dccdSB.getLng().toString()));
				sw.append(getXMLElementString(NAME_LATITUDE, dccdSB.getLat().toString()));
			}
			else
			{
				// not permitted
				sw.append(getXMLElementString(NAME_LONGITUDE, VALUE_NOT_PERMITTED));
				sw.append(getXMLElementString(NAME_LATITUDE, VALUE_NOT_PERMITTED));
			}
		}
		else
		{
			// add empty elements
			sw.append(getXMLElementString(NAME_LONGITUDE, ""));
			sw.append(getXMLElementString(NAME_LATITUDE, ""));
		}
		
		return sw.toString();
	}

	private static String getXMLElementString(final String name, final String value)
	{
		return "<" + name + ">" + StringEscapeUtils.escapeXml(value) + "</" + name + ">";
	}
	

	private static String getSeriesIdentfierString(final DccdSB dccdHit)
	{
		String identifierStr = "";
		if (dccdHit.hasTridasMeasurementseriesIdentifier())
			identifierStr = getStringListAsString(dccdHit.getTridasMeasurementseriesIdentifier());
		
		return identifierStr;
	}

	private static String getFirstYearString(final DccdSB dccdHit)
	{
		String yearStr = "";
		if (dccdHit.hasTridasMeasurementseriesInterpretationFirstyear())
		{
			List<Integer> yearList = dccdHit.getTridasMeasurementseriesInterpretationFirstyear();
			yearStr = getYearListAsString(yearList);
		}
		
		return yearStr;
	}
	
	private static String getLastYearString(final DccdSB dccdHit)
	{
		String yearStr = "";
		if (dccdHit.hasTridasMeasurementseriesInterpretationLastyear())
		{
			List<Integer> yearList = dccdHit.getTridasMeasurementseriesInterpretationLastyear();
			yearStr = getYearListAsString(yearList);
		}
		
		return yearStr;
	}

	private static String getPithYearString(final DccdSB dccdHit)
	{
		String yearStr = "";
		if (dccdHit.hasTridasMeasurementseriesInterpretationPithyear())
		{
			List<Integer> yearList = dccdHit.getTridasMeasurementseriesInterpretationPithyear();
			yearStr = getYearListAsString(yearList);
		}
		
		return yearStr;
	}

	private static String getDeathYearString(final DccdSB dccdHit)
	{
		String yearStr = "";
		if (dccdHit.hasTridasMeasurementseriesInterpretationDeathyear())
		{
			List<Integer> yearList = dccdHit.getTridasMeasurementseriesInterpretationDeathyear();
			yearStr = getYearListAsString(yearList);
		}
		
		return yearStr;
	}
	
	
	private static String getYearListAsString(final List<Integer> yearList)
	{
		String yearStr = "";
		if (!yearList.isEmpty())
		{
			Integer year = yearList.get(0);
			yearStr = year.toString();
			
			// get all other items
			// and construct a comma separated list
			for (int i = 1; i < yearList.size(); i++)
			{
				year = yearList.get(i);
				yearStr += ", " + year.toString();
			}
		}
		return yearStr;
	}
	
	private static String getStringListAsString(final List<String> stringList)
	{
		String stringStr = "";
		if (!stringList.isEmpty())
		{
			stringStr = stringList.get(0);
			
			// get all other items
			// and construct a comma separated list
			for (int i = 1; i < stringList.size(); i++)
			{
				stringStr += ", " + stringList.get(i);
			}
		}
		return stringStr;
	}
	
	private static String getElementTitleString(final DccdSB dccdHit)
	{
		String titleStr = "";
		if (dccdHit.hasTridasElementTitle())
			titleStr = getStringListAsString(dccdHit.getTridasElementTitle());//dccdHit.getTridasElementTitle().get(0);
		
		return titleStr;
	}
	
	private static String getElementTypeString(final DccdSB dccdHit)
	{
		String typeStr = "";

		// Only show the type information of this object and not sub-objects
		// and only the Normal, if there is no Type (as direct value)
		if (dccdHit.hasTridasElementType() && dccdHit.getTridasElementType().get(0).length() > 0)
			typeStr = getStringListAsString(dccdHit.getTridasElementType());//dccdHit.getTridasElementType().get(0);
		else if (dccdHit.hasTridasElementTypeNormal() && dccdHit.getTridasElementTypeNormal().get(0).length() > 0)
			typeStr = getStringListAsString(dccdHit.getTridasElementTypeNormal());//dccdHit.getTridasElementTypeNormal().get(0);

		return typeStr;
	}	
	
	private static String getElementTaxonString(final DccdSB dccdHit)
	{
		String taxonStr = "";
		if (dccdHit.hasTridasElementTaxon())
			taxonStr = getStringListAsString(dccdHit.getTridasElementTaxon());//dccdHit.getTridasElementTaxon().get(0);
		
		return taxonStr;
	}
	
	private static String getProjectTitleString(final DccdSB dccdHit)
	{
		String projectTitleStr = dccdHit.getTridasProjectTitle();
		return projectTitleStr;
	}
	
	private static String getObjectTitleString(final DccdSB dccdHit)
	{
		String objectTitleStr = "";
		if (dccdHit.hasTridasObjectTitle())
		{
			objectTitleStr = dccdHit.getTridasObjectTitle().get(0);
			// TODO all in list
			//objectTitleStr = getStringListAsString(dccdHit.getTridasObjectTitle());
		}
		
		return objectTitleStr;
	}
	
	private static String getProjectIdentfierString(final DccdSB dccdHit)
	{
	    //ID: <project identifier> (domain: <project domain>)
		String identifierStr = "";
		if (dccdHit.hasTridasProjectIdentifier())
			identifierStr = dccdHit.getTridasProjectIdentifier();
		String domainStr = "";
		if (dccdHit.hasTridasProjectIdentifierDomain())
			domainStr = dccdHit.getTridasProjectIdentifierDomain();

		return identifierStr + " (domain: " + domainStr + ")";
	}
	
	private static String getObjectTypeString(final DccdSB dccdHit)
	{
		String objectTypeStr = "";

		// Only show the type information of this object and not sub-objects
		// and only the Normal, if there is no Type (as direct value)
		if (dccdHit.hasTridasObjectType() && dccdHit.getTridasObjectType().get(0).length() > 0)
			objectTypeStr = dccdHit.getTridasObjectType().get(0);
		else if (dccdHit.hasTridasObjectTypeNormal() && dccdHit.getTridasObjectTypeNormal().get(0).length() > 0)
			objectTypeStr = dccdHit.getTridasObjectTypeNormal().get(0);
		
		/*
		// TODO for all objects, but we don't know if and how they are paired?
		List<String> terms = new  ArrayList<String>();
		if (dccdHit.hasTridasObjectType())
			terms.addAll(dccdHit.getTridasObjectType());
		if (dccdHit.hasTridasObjectTypeNormal())
			terms.addAll(dccdHit.getTridasObjectTypeNormal());
		objectTypeStr = StringUtil.constructCommaSeparatedString(terms);
		*/
		
		return objectTypeStr;
	}
	
	private static String getProjectLabString(final DccdSB dccdHit)
	{
		String projectLabStr = "";

		if (dccdHit.hasTridasProjectLaboratoryCombined())
		{
			StringListCollapserConverter conv = new StringListCollapserConverter();
			for(String combinedString : dccdHit.getTridasProjectLaboratoryCombined())
			{
				try
				{
					List<String> combinedAsList = conv.fromFieldValue(combinedString);
					// Now I have read the bean filling code and know the order !
					if (combinedAsList.size() < 4)
					{
						logger.debug("Combine Project laboratory info incorrect: should have at least 4 items");
						continue; // skip this one, it is wrong
					}
					
					// name (would normally not be empty)
					String name = combinedAsList.get(0).trim();
					if (!name.isEmpty())
					{
						if (projectLabStr.length() > 0)
							projectLabStr += ", "; 
						projectLabStr += name; 
					}
					// skip acronym
					
					// place
					String place = combinedAsList.get(2).trim();
					if (!place.isEmpty())
						if (projectLabStr.length() > 0)
							projectLabStr += ", "; 
						projectLabStr += place; 
					
					// country
					String country = combinedAsList.get(3).trim();
					if (!country.isEmpty())
						if (projectLabStr.length() > 0)
							projectLabStr += ", "; 
						projectLabStr += country; 
				}
				catch (SearchBeanConverterException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

		return projectLabStr;
	}

	private static String getProjectTypeString(final DccdSB dccdHit)
	{
		String projectTypeStr = "";
		
		List<String> terms = new  ArrayList<String>();
		if (dccdHit.hasTridasProjectType())
			terms.addAll(dccdHit.getTridasProjectType());
		if (dccdHit.hasTridasProjectTypeNormal())
			terms.addAll(dccdHit.getTridasProjectTypeNormal());
		
		List<String> uniqueTerms = StringUtil.getUniqueStrings(terms);
		projectTypeStr = StringUtil.constructCommaSeparatedString(uniqueTerms);
		
		return projectTypeStr;
	}
	
	private static String getProjectCategoryString(final DccdSB dccdHit)
	{
		// NOTE: not normalTridasCategory
		String projectCategoryStr = "";
		if (dccdHit.hasTridasProjectCategory())
			projectCategoryStr = dccdHit.getTridasProjectCategory();
		
		if (dccdHit.hasTridasProjectCategoryNormal() &&
			!dccdHit.getTridasProjectCategoryNormal().isEmpty() && 
			!projectCategoryStr.contentEquals(dccdHit.getTridasProjectCategoryNormal()))
		{
			if (!projectCategoryStr.isEmpty())// add seperator
				projectCategoryStr += ", ";
		    projectCategoryStr += dccdHit.getTridasProjectCategoryNormal();
		}
		return projectCategoryStr;
	}

	private static String getProjectDescriptionString(final DccdSB dccdHit)
	{
		String projectDescriptionStr = "";
		if (dccdHit.hasTridasProjectDescription()) {
			projectDescriptionStr = dccdHit.getTridasProjectDescription();
			//truncate at 200 chars
			final int max_chars = 200;
			// make it a resource that is language dependent?
			final String trunc_indicator = "[...]";
			if (projectDescriptionStr.length() > max_chars) {
				projectDescriptionStr = projectDescriptionStr.substring(0, max_chars-1-trunc_indicator.length());
				projectDescriptionStr += trunc_indicator;
			}
		}
		return projectDescriptionStr;
	}

}
