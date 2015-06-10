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

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.search.SearchRequest;
import nl.knaw.dans.common.lang.search.SearchResult;
import nl.knaw.dans.common.lang.search.simple.SimpleField;
import nl.knaw.dans.common.lang.search.simple.SimpleSearchRequest;
import nl.knaw.dans.common.wicket.components.search.SearchBar;
import nl.knaw.dans.common.wicket.components.search.criteria.CriteriumLabel;
import nl.knaw.dans.common.wicket.components.search.criteria.InitialSearchCriterium;
import nl.knaw.dans.common.wicket.components.search.criteria.SearchCriteriaPanel;
import nl.knaw.dans.common.wicket.components.search.criteria.TextSearchCriterium;
import nl.knaw.dans.common.wicket.components.search.facets.FacetConfig;
import nl.knaw.dans.common.wicket.components.search.model.SearchCriterium;
import nl.knaw.dans.common.wicket.components.search.model.SearchModel;
import nl.knaw.dans.common.wicket.components.search.model.SearchRequestBuilder;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.dccd.application.services.DccdSearchService;
import nl.knaw.dans.dccd.application.services.SearchServiceException;
import nl.knaw.dans.dccd.application.services.ServiceException;
import nl.knaw.dans.dccd.model.DccdUser;
import nl.knaw.dans.dccd.model.DccdUser.Role;
import nl.knaw.dans.dccd.search.DccdObjectSB;
import nl.knaw.dans.dccd.search.DccdSB;
import nl.knaw.dans.dccd.web.DccdResources;
import nl.knaw.dans.dccd.web.DccdSession;
import nl.knaw.dans.dccd.web.search.AbstractSearchPage;
import nl.knaw.dans.dccd.web.search.DccdSearchResultExplorePanel;
import nl.knaw.dans.dccd.web.search.DccdSearchResultPanel;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class SearchResultExplorePage extends AbstractSearchPage
{                              	
    
	private static final Logger	LOGGER 				= LoggerFactory.getLogger(SearchResultExplorePage.class);

    private static final long	serialVersionUID	= 8501036308620025068L;
    
    private static final String     SEARCHRESULT_PANEL = "searchResultPanel";

    public SearchResultExplorePage(boolean needAuthentication)
    {
        super();
        
        /* Note: maybe handle login here
        if (needAuthentication && !getEasySession().isAuthenticated())
        {
            // might be a link from a notification 
            redirectToInterceptPage(new LoginPage());
            return;
        }
         */
    	init(null);
    }
   
    public SearchResultExplorePage(SearchModel model)
    {
    	super(model);
    	init(null);
    }

    public SearchResultExplorePage(final PageParameters parameters)
    {
    	super(parameters);
		String queryString = parameters.getString(SearchBar.QUERY_PARAM);	
        if (getDefaultModel() == null)
        	init(queryString);
    }

	protected SearchResult<? extends DccdSB> doSearch(SearchRequest request)
	throws ServiceException
	{	
		LOGGER.debug("SearchResultExplorePage Search");
		
		return doSearchPublic(request);	
	}
	
	protected SearchResult<? extends DccdSB> doSearchPublic(SearchRequest request)
	throws ServiceException
	{	
		LOGGER.debug("SearchResultExplorePage Public Search");
				
		try
		{		
	    	DccdUser user = (DccdUser) ((DccdSession) getSession()).getUser();
	    	if(user==null || !user.hasRole(Role.ADMIN) )
	    	{
				// restrict results to archived/published 
				SimpleField<String> stateField = new SimpleField<String>(DccdSB.ADMINISTRATIVE_STATE_NAME, DatasetState.PUBLISHED.toString());
				request.addFilterQuery(stateField);    	
	    	}
	    	request.addFilterBean(DccdObjectSB.class);
	    	
			return DccdSearchService.getService().doSearch(request);
		}
		catch (SearchServiceException e)
		{
			throw new ServiceException(e);
		}
	}
	
	protected IModel<String> getInitialCriteriumText()
	{
		return new ResourceModel("publicsearch.defaultbreadcrumbtext");
	}

	protected IModel<String> getSearchCriteriumText(final String searchText)
	{
		return new AbstractReadOnlyModel<String>()
		{
			private static final long	serialVersionUID	= 3254972701101566016L;

			@Override
			public String getObject()
			{
				return CriteriumLabel.createFilterText(getString("publicsearch.searchbreadcrumbtext"), searchText);
			}
		};
	}	

	private void initRequestBuilder()
	{		
		SearchRequestBuilder requestBuilder = getSearchData().getRequestBuilder();
		
		// if we have offset and limit restricted, remove that restriction
		int totalHits = getSearchData().getResult().getTotalHits();
		int offset = requestBuilder.getOffset();
		int limit = requestBuilder.getLimit();
		if (offset != 0 || limit < totalHits)
		{
			requestBuilder.setOffset(offset);
			requestBuilder.setLimit(totalHits);
		       
			SimpleSearchRequest request = requestBuilder.getRequest();
			 try {
				 getSearchData().setResult(doSearch(request));
			 } catch (ServiceException e) {
					String msg = exceptionMessage(DccdResources.SEARCH_FAILURE);
					LOGGER.error(msg, e);
					throw new InternalWebError();
			}		 
		}
		
	}
	
    protected void init(final String searchText)
    {
		 // Exploring means All results, and not paged	
		initRequestBuilder();
    	
    	add(new Label("headerLabel", getInitialCriteriumText() ));
    	
    	if (getSearchModel() == null)
    	{
    		SearchCriterium criterium;
        	if (!StringUtils.isBlank(searchText))
        		criterium = new TextSearchCriterium(searchText, getSearchCriteriumText(searchText));
        	else
        		criterium = new InitialSearchCriterium(getInitialCriteriumText());
        	setSearchModel( new SearchModel(criterium) );
    	}
    	
		add(new SearchCriteriaPanel("searchCriteria", getSearchModel()));

    	DccdSearchResultExplorePanel panel = new DccdSearchResultExplorePanel(
    			SEARCHRESULT_PANEL, getSearchModel())
		{
			private static final long serialVersionUID = 4389340592804783670L;
			
			@Override
			public SearchResult<?> search(SimpleSearchRequest request)
			{
				try
				{
					SearchResult<? extends DccdSB> searchResult = SearchResultExplorePage.this.doSearch(request);
					return searchResult;
				}
				catch (ServiceException e)
				{
					String msg = errorMessage(DccdResources.SEARCH_FAILURE);
					LOGGER.error(msg, e);
					throw new InternalWebError();
				}
			}
		};
		add(panel);
		
    }
    
    @Override
    public void refresh()
    {
		SearchModel model =  getSearchModel();
		if (model != null) 
	        model.getObject().setDirty(true);
    }

}
