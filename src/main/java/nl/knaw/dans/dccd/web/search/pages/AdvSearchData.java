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
package nl.knaw.dans.dccd.web.search.pages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.dataset.DatasetSB;
import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.search.Field;
import nl.knaw.dans.common.lang.search.simple.CombinedOptionalField;
import nl.knaw.dans.common.lang.search.simple.SimpleField;
import nl.knaw.dans.common.lang.search.simple.SimpleSearchQuery;
import nl.knaw.dans.dccd.search.DccdSB;
import nl.knaw.dans.dccd.web.search.years.YearRange;
import nl.knaw.dans.dccd.web.search.years.YearSearchData;

@SuppressWarnings("serial")	
public class AdvSearchData implements Serializable 
{
	private static final long serialVersionUID = -5849253482755111398L;

	public String query = new String();
	
	//Project
	public SimpleField<String> projectTitle = new SimpleField<String>(DccdSB.TRIDAS_PROJECT_TITLE_NAME);
	public SimpleField<String> projectIdentifier = new SimpleField<String>(DccdSB.TRIDAS_PROJECT_IDENTIFIER_NAME);
	public SimpleField<String> projectLabname = new SimpleField<String>(DccdSB.TRIDAS_PROJECT_LABORATORY_NAME_NAME);
	public CombinedOptionalField<String> projectCategory = new CombinedOptionalField<String>(new ArrayList<String>(){{
		add(DccdSB.TRIDAS_PROJECT_CATEGORY_NAME);
		add(DccdSB.TRIDAS_PROJECT_CATEGORY_NORMAL_NAME);
		add(DccdSB.TRIDAS_PROJECT_CATEGORY_NORMALTRIDAS_NAME);
	}});

	// Object
	public SimpleField<String> objectTitle = new SimpleField<String>(DccdSB.TRIDAS_OBJECT_TITLE_NAME);
	public SimpleField<String> objectIdentifier = new SimpleField<String>(DccdSB.TRIDAS_OBJECT_IDENTIFIER_NAME);
	public SimpleField<String> objectCreator = new SimpleField<String>(DccdSB.TRIDAS_OBJECT_CREATOR_NAME);
	public CombinedOptionalField<String> objectType = new CombinedOptionalField<String>(new ArrayList<String>(){{
		add(DccdSB.TRIDAS_OBJECT_TYPE_NAME);
		add(DccdSB.TRIDAS_OBJECT_TYPE_NORMAL_NAME);
	}});

	// Element
	public SimpleField<String> elementTitle = new SimpleField<String>(DccdSB.TRIDAS_ELEMENT_TITLE_NAME);
	public SimpleField<String> elementIdentifier = new SimpleField<String>(DccdSB.TRIDAS_ELEMENT_IDENTIFIER_NAME);
	public SimpleField<String> elementTaxon = new SimpleField<String>(DccdSB.TRIDAS_ELEMENT_TAXON_NAME);
	public CombinedOptionalField<String> elementType = new CombinedOptionalField<String>(new ArrayList<String>(){{
		add(DccdSB.TRIDAS_ELEMENT_TYPE_NAME);
		add(DccdSB.TRIDAS_ELEMENT_TYPE_NORMAL_NAME);
	}});

	// Time Year Periods
	public SimpleField<YearRange> deathYear = new SimpleField<YearRange>(DccdSB.TRIDAS_MEASUREMENTSERIES_INTERPRETATION_DEATHYEAR_NAME);
	public SimpleField<YearRange> firstYear = new SimpleField<YearRange>(DccdSB.TRIDAS_MEASUREMENTSERIES_INTERPRETATION_FIRSTYEAR_NAME);
	public SimpleField<YearRange> lastYear = new SimpleField<YearRange>(DccdSB.TRIDAS_MEASUREMENTSERIES_INTERPRETATION_LASTYEAR_NAME);
	public SimpleField<YearRange> pithYear = new SimpleField<YearRange>(DccdSB.TRIDAS_MEASUREMENTSERIES_INTERPRETATION_PITHYEAR_NAME);


	@SuppressWarnings("unchecked")
	public List< Field<?> > fields = new ArrayList< Field<?> >()
	{{
		// Project
		add(projectTitle);
		add(projectIdentifier);
		add(projectLabname);
		add(projectCategory);

		// Object
		add(objectTitle);
		add(objectIdentifier);
		add(objectType);
		add(objectCreator);

		// Element
		add(elementTitle);
		add(elementIdentifier);
		add(elementType);
		add(elementTaxon);
		
		// Time
		add(deathYear);
		add(firstYear);
		add(lastYear);
		add(pithYear);

	}};
	
	// Convert from the year search Fields to the YearSearch (Panel) data
	public YearSearchData getYearSearchData()
	{
		YearSearchData yearSearchData = new YearSearchData();
		
		// Note that ranges must be equal for this to make any sense
		// because the YearSearchData only has one start and end for all selected year fields!
		
		// deathYear
		YearRange range = deathYear.getValue();
		if (range != null && range.getStart() != null && range.getEnd() != null)
		{
			// ONLY if both are set
			// TODO also handle when not both are set!
			yearSearchData.setDeathYear(true);
			// use this range
			yearSearchData.setFromYearAstronomical(range.getStart());
			yearSearchData.setToYearAstronomical(range.getEnd());
		}
		else
		{
			yearSearchData.setDeathYear(false);
		}
		
		// firstYear
		range = firstYear.getValue();
		if (range != null && range.getStart() != null && range.getEnd() != null)
		{
			// ONLY if both are set
			// TODO also handle when not both are set!
			yearSearchData.setFirstYear(true);
			// use this range		
			yearSearchData.setFromYearAstronomical(range.getStart());
			yearSearchData.setToYearAstronomical(range.getEnd());
		}
		else
		{
			yearSearchData.setFirstYear(false);
		}	
		
		// lastYear
		range = lastYear.getValue();
		if (range != null && range.getStart() != null && range.getEnd() != null)
		{
			// ONLY if both are set
			// TODO also handle when not both are set!
			yearSearchData.setLastYear(true);
			// use this range		
			yearSearchData.setFromYearAstronomical(range.getStart());
			yearSearchData.setToYearAstronomical(range.getEnd());
		}
		else
		{
			yearSearchData.setLastYear(false);
		}	

		// pithYear
		range = pithYear.getValue();
		if (range != null && range.getStart() != null && range.getEnd() != null)
		{
			// ONLY if both are set
			// TODO also handle when not both are set!
			yearSearchData.setPithYear(true);
			// use this range
			yearSearchData.setFromYearAstronomical(range.getStart());
			yearSearchData.setToYearAstronomical(range.getEnd());
		}
		else
		{
			yearSearchData.setPithYear(false);
		}		
		
		return yearSearchData;
	}
	
	// Convert from the YearSearch (Panel) data to the year search Fields
	public void setYearSearchData(YearSearchData yearSearchData)
	{
		// construct the range, used for all years
		YearRange range = null;
		if (yearSearchData.getFromYear() != null || yearSearchData.getToYear() != null)
		{
			Integer from = yearSearchData.getFromYearAstronomical();
			Integer to = yearSearchData.getToYearAstronomical();
			
			if (from == null || to == null) 
			{
				// one or both of them is not set, so no order check needed
				range = new YearRange(from, to);
			}
			else
			{
				// both are set, so maybe we have to reverse order
				if(from > to)		
					range = new YearRange(to, from); //reverse 
				else 
					range = new YearRange(from, to);
			}
		}
		
		// and use for every year field selected
		
		if (yearSearchData.isDeathYear())
		{
			deathYear.setValue(range);
		}
		else 
			deathYear.setValue(null);
	
		if (yearSearchData.isFirstYear())
			firstYear.setValue(range);
		else 
			firstYear.setValue(null);
		
		if (yearSearchData.isLastYear())
			lastYear.setValue(range);
		else 
			lastYear.setValue(null);
		
		if (yearSearchData.isPithYear())
			pithYear.setValue(range);
		else 
			pithYear.setValue(null);
	}

	public ArrayList<DatasetState> states = new ArrayList<DatasetState>() 
	{{
		// default state
		add(DatasetState.PUBLISHED);
	}};
	
	public List<Field<?>> getFields(boolean includeStates)
	{		
		// Only add set fields
		List<Field<?>> filterFields = new ArrayList<Field<?>>(fields.size());
		for (Field field : fields)
		{
			if (field.getValue() != null)
				filterFields.add(field);
		}
		
		if (includeStates)
		{
			filterFields.add(
					new SimpleField<String>(
							DatasetSB.DS_STATE_FIELD, 
							SimpleSearchQuery.OrValues(states.toArray())
						)
					);
		}
		return filterFields;		
	}	
}

