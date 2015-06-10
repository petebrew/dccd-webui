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

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.search.SearchRequest;
import nl.knaw.dans.common.lang.search.SearchResult;
import nl.knaw.dans.common.lang.search.SortType;
import nl.knaw.dans.common.lang.search.simple.SimpleSearchRequest;
import nl.knaw.dans.common.wicket.components.search.SearchBar;
import nl.knaw.dans.common.wicket.components.search.criteria.CriteriumLabel;
import nl.knaw.dans.common.wicket.components.search.criteria.InitialSearchCriterium;
import nl.knaw.dans.common.wicket.components.search.criteria.TextSearchCriterium;
import nl.knaw.dans.common.wicket.components.search.facets.FacetConfig;
import nl.knaw.dans.common.wicket.components.search.model.SearchCriterium;
import nl.knaw.dans.common.wicket.components.search.model.SearchData;
import nl.knaw.dans.common.wicket.components.search.model.SearchModel;
import nl.knaw.dans.common.wicket.components.search.results.SearchResultConfig;
import nl.knaw.dans.common.wicket.components.search.results.SortLinkConfig;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.dccd.application.services.ServiceException;
import nl.knaw.dans.dccd.model.DccdUser;
import nl.knaw.dans.dccd.search.DccdSB;
import nl.knaw.dans.dccd.web.DccdResources;
import nl.knaw.dans.dccd.web.DccdSession;
import nl.knaw.dans.dccd.web.search.pages.AdvSearchPage;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.PageParameters;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controlling class for performing searches and viewing the results.
 * 
 * @author lobo
 */
public abstract class AbstractSearchResultPage extends AbstractSearchPage
{               
    /*--------------------------------------------------------
     * --- PROTECTED METHODS TO OVERRIDE OR IMPLEMENT --------
     *-------------------------------------------------------*/
    
    protected abstract IModel<String> getInitialCriteriumText();
    
    protected IModel<String> getSearchCriteriumText(String searchText)
    {
    	return new Model<String>(CriteriumLabel.createFilterText("Search", searchText));
    }
            
	protected boolean showTips()
    {
    	return false;
    }
	
	/**
     * Implement search here
	 * @throws ServiceException 
     */
    protected abstract SearchResult<? extends DccdSB> doSearch(SearchRequest request) throws ServiceException;
    
    /*--------------------------------------------------------
     * ------------------- CLASS INTERNALS -------------------
     *-------------------------------------------------------*/

	private static final Logger	LOGGER 				= LoggerFactory.getLogger(AbstractSearchResultPage.class);

    private static final long	serialVersionUID	= 8501036308620025068L;
    
    private static final String     SEARCHRESULT_PANEL = "searchResultPanel";

    public AbstractSearchResultPage(boolean needAuthentication)
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
   
    public AbstractSearchResultPage(SearchModel model)
    {
    	super(model);
    	init(null);
    }

    public AbstractSearchResultPage(final PageParameters parameters)
    {
    	super(parameters);
		String queryString = parameters.getString(SearchBar.QUERY_PARAM);	
        if (getDefaultModel() == null)
        	init(queryString);
    }

    protected void init(final String searchText)
    {
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

    	DccdSearchResultPanel panel = new DccdSearchResultPanel(
    			SEARCHRESULT_PANEL, getSearchModel(), showTips(), getSearchResultConfig())
		{
			private static final long serialVersionUID = 4389340592804783670L;
			
			@Override
			public SearchResult<?> search(SimpleSearchRequest request)
			{
				try
				{
					SearchResult<? extends DccdSB> searchResult = AbstractSearchResultPage.this.doSearch(request);
					/*
					// the actual hits returned are paged
					LOGGER.debug("===> Number of hits: " + searchResult.getTotalHits());
					for(SearchHit<? extends DccdSB> hit : searchResult.getHits())
					{
						DccdSB dccdSB = hit.getData();
						//LOGGER.debug("===> hit: " + dccdSB.getId());
						if (dccdSB.hasLat() && dccdSB.hasLng())
						{
							LOGGER.debug("===> Geo location: (" + dccdSB.getLng() + "," + dccdSB.getLat() + ")");
						}
					}
					*/
					return searchResult;
				}
				catch (ServiceException e)
				{
					String msg = errorMessage(DccdResources.SEARCH_FAILURE);
					LOGGER.error(msg, e);
					throw new InternalWebError();
				}
			}
			
			/*			
			@Override
			protected void onBrowseMoreClicked(SearchModel searchModel)
			{
				setResponsePage( 
						new BrowsePage(searchModel, (Class<? extends AbstractSearchResultPage>) getPage().getClass())
					);
			}
			 */
			
			@Override
			protected void onAdvancedSearchClicked(SearchModel searchModel)
			{
				setResponsePage( 
						new AdvSearchPage(searchModel, (Class<? extends AbstractSearchResultPage>) getPage().getClass())
					);
			}
		};
		add(panel);
    }
    
	protected SearchResultConfig getSearchResultConfig()
	{
        SearchResultConfig config = new SearchResultConfig();
        //config.setResultCount(10);
        // get it from the session
        config.setResultCount(((DccdSession)Session.get()).getResultCount());
        
		//config.setShowBrowseMore(this instanceof PublicSearchResultPage);
        config.setShowBrowseMore(false);
        
        //only members (logged in) can do advanced search
        DccdUser user = (DccdUser) ((DccdSession) getSession()).getUser();
        if (user == null)
        	config.setShowAdvancedSearch(false);
        else
        	config.setShowAdvancedSearch(true);
        
       	config.setHitPanelFactory(DccdSearchHitPanelFactory.getInstance());
		config.setSortLinks(getSortLinks());
		config.setRefineFacets(getFacets());

		return config;
    }

	protected List<FacetConfig> getFacets()
	{
		ArrayList<FacetConfig> refineFacets = new ArrayList<FacetConfig>(); // empty
		
		/*	TODO facets for DCCD	
		 */
		
		return refineFacets;
	}

	protected List<SortLinkConfig> getSortLinks()
	{
		List<SortLinkConfig> sortLinks = new ArrayList<SortLinkConfig>();
		sortLinks.add( new SortLinkConfig("relevance", SortType.BY_RELEVANCE_SCORE) );

		return sortLinks;
	}
    
	/* TODO BasePage should support this?
    @Override
    public String getPageTitlePostfix()
    {
        return getInitialCriteriumText().getObject();
    }
	 */    
    
    @Override
    public void refresh()
    {
		SearchModel model =  getSearchModel();
		if (model != null) 
	        model.getObject().setDirty(true);
    }    
}
