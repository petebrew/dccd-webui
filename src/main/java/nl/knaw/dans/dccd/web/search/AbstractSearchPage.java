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

import java.lang.reflect.Constructor;

import nl.knaw.dans.common.wicket.components.search.model.SearchData;
import nl.knaw.dans.common.wicket.components.search.model.SearchModel;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.dccd.web.base.BasePage;

import org.apache.wicket.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSearchPage extends BasePage
{
	private static final Logger	LOGGER 				= LoggerFactory.getLogger(AbstractSearchPage.class);
    
	public AbstractSearchPage()
	{
		super();
	}
	
	public AbstractSearchPage(SearchModel searchModel)
	{
		super(searchModel);
	}
	
	public AbstractSearchPage(PageParameters parameters)
	{
		super(parameters);
	}	
		
	public static AbstractSearchPage instantiate(Class<? extends AbstractSearchPage> searchPageClass, SearchModel searchModel)
	{
		try
		{
			Constructor<? extends AbstractSearchPage> constructor =  searchPageClass.getConstructor(SearchModel.class);
			return constructor.newInstance(searchModel);
		}
		catch(Exception e)
		{
			LOGGER.error("The constructor of AbstractSearchPage "+ searchPageClass.toString() +" disappeared, got hidden or threw an exception.", e);
			throw new InternalWebError(); 
		}		
	}
	
	public SearchModel getSearchModel()
	{
		return (SearchModel) getDefaultModel();
	}
	
	public void setSearchModel(SearchModel searchModel)
	{
		this.setDefaultModel(searchModel);
	}
	
	public SearchData getSearchData()
	{
		SearchModel searchModel = getSearchModel();
		if (searchModel != null)
			return searchModel.getObject();
		else
			return null;
	}

}
