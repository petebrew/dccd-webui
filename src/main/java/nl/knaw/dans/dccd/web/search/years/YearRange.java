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

import nl.knaw.dans.common.lang.util.Range;

/** 
 * Represents a time period using astronomical years stored as integer values
 * 
 */
public class YearRange extends Range<Integer>
{
	private static final long	serialVersionUID	= 4132205060068037925L;

	public YearRange(Integer start, Integer end)
	{
		super(start, end);
	}

	// For giving a cleaner UI message, using BC and AD when needed
	@Override
	public String toString()
	{
		String result = "";
		
		boolean startBC = false;
		String startString = "";
		if (getStart() != null) 
		{
			if (getStart() <= 0)
			{
				startBC = true;
				Integer BCYear = 1-getStart();
				startString = BCYear.toString();
			}
			else
			{
				startString = getStart().toString();
			}
		}
		
		boolean endBC = false;
		String endString = "";
		if (getEnd() != null) 
		{
			if (getEnd() <= 0)
			{
				endBC = true;
				Integer BCYear = 1-getEnd();
				endString = BCYear.toString();
			}
			else
			{
				endString = getEnd().toString();
			}
		}

		if (startBC && endBC)
		{
			// both BC; append BC
			result = startString + getSeperatorString() + endString + " " + getBCString();
		}
		else if (startBC && !endBC)
		{
			// BC - AD
			// Note: some argue that AD should be placed before the year
			result = startString + " " + getBCString() + getSeperatorString() + endString + " " + getADString();
		} 
		else
		{
			// Both AD
			result = startString + getSeperatorString() + endString;
		}
		
		return result;
	}
	
	private String getSeperatorString()
	{
		return " - ";
	}

	private String getBCString()
	{
		return "BC";
	}
	
	private String getADString()
	{
		return "AD";
	}
	
}
