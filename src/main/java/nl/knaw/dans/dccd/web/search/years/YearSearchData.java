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
package nl.knaw.dans.dccd.web.search.years;

import java.io.Serializable;

/**
 * Used by the YearSearchPanel
 * 
 */
public class YearSearchData  implements Serializable 
{
	private static final long	serialVersionUID	= 8873436944552557293L;
	
	// period 
	// Note:  maybe make a object for it; Period or YearsTimeSpan
	Integer fromYear = null;
	YearSuffix fromYearSuffix = YearSuffix.AD;
	Integer toYear = null;
	YearSuffix toYearSuffix = YearSuffix.AD;
	
	// Years to match selection; different object?
	boolean firstYear = false;
	boolean lastYear = false;
	boolean pithYear = false;
	boolean deathYear = false;

	public YearSearchData()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public Integer getFromYear()
	{
		return fromYear;
	}

	public void setFromYear(Integer fromYear)
	{
		this.fromYear = fromYear;
	}

	public YearSuffix getFromYearSuffix()
	{
		return fromYearSuffix;
	}

	public void setFromYearSuffix(YearSuffix fromYearSuffix)
	{
		this.fromYearSuffix = fromYearSuffix;
	}

	public Integer getToYear()
	{
		return toYear;
	}

	public void setToYear(Integer toYear)
	{
		this.toYear = toYear;
	}

	public YearSuffix getToYearSuffix()
	{
		return toYearSuffix;
	}

	public void setToYearSuffix(YearSuffix toYearSuffix)
	{
		this.toYearSuffix = toYearSuffix;
	}

	//--- Years to search for ---
	
	public boolean isFirstYear()
	{
		return firstYear;
	}
	public void setFirstYear(boolean firstYear)
	{
		this.firstYear = firstYear;
	}
	public boolean isLastYear()
	{
		return lastYear;
	}
	public void setLastYear(boolean lastYear)
	{
		this.lastYear = lastYear;
	}
	public boolean isPithYear()
	{
		return pithYear;
	}
	public void setPithYear(boolean pithYear)
	{
		this.pithYear = pithYear;
	}
	public boolean isDeathYear()
	{
		return deathYear;
	}
	public void setDeathYear(boolean deathYear)
	{
		this.deathYear = deathYear;
	}

	//--- atronomical years convertion ---
	
	public Integer getFromYearAstronomical()
	{
		return convertToAstronomicalYear(getFromYear(), getFromYearSuffix());
	}

	public Integer getToYearAstronomical()
	{
		return convertToAstronomicalYear(getToYear(), getToYearSuffix());
	}

	public void setFromYearAstronomical(Integer astroYear)
	{
		if (astroYear != null)
		{
			if(astroYear <= 0)
			{
				// BC
				setFromYearSuffix(YearSuffix.BC);
				setFromYear(1-astroYear);
			}
			else
			{
				// AD
				setFromYearSuffix(YearSuffix.AD);
				setFromYear(astroYear);
			}
			// Not: never BP!
		}
	}

	public void setToYearAstronomical(Integer astroYear)
	{
		if (astroYear != null)
		{
			if(astroYear <= 0)
			{
				// BC
				setToYearSuffix(YearSuffix.BC);
				setToYear(1-astroYear);
			}
			else
			{
				// AD
				setToYearSuffix(YearSuffix.AD);
				setToYear(astroYear);
			}
			// Not: never BP!
		}
	}
	
	public static Integer convertToAstronomicalYear(Integer year, YearSuffix suffix)
	{
		Integer yearInteger = year;
		
		if (year != null)
		{
			// make sure we start positive
			yearInteger = Math.abs(yearInteger);
	
			if (suffix == YearSuffix.BC)
			{
				// make BC negative
				yearInteger = 1-yearInteger; // Note the 1 for using the year zero!
			}
			else if (suffix == YearSuffix.BP)
			{
				yearInteger = 1950-yearInteger;
			}
		}
		
		return yearInteger;		
	}
}
