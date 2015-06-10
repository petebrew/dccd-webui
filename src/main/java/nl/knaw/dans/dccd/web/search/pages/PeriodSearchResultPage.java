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

import nl.knaw.dans.common.lang.search.SearchResult;
import nl.knaw.dans.common.lang.search.simple.SimpleSearchRequest;
import nl.knaw.dans.common.wicket.components.search.SearchPanel;
import nl.knaw.dans.common.wicket.components.search.model.SearchModel;
import nl.knaw.dans.common.wicket.components.search.model.SearchRequestBuilder;
import nl.knaw.dans.dccd.web.DccdSession;
import nl.knaw.dans.dccd.web.HomePage;
import nl.knaw.dans.dccd.web.base.BasePage;
import nl.knaw.dans.dccd.web.search.LocationSearchResultPanel;
import nl.knaw.dans.dccd.web.search.PeriodSearchResultPanel;

import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.ResourceModel;

/**
 * Display a (large) map with locations from all search results (unpaged)
 *
 */
public class PeriodSearchResultPage extends BasePage
{
	private Page backPage;
	
	public PeriodSearchResultPage(SearchModel searchModel, final SearchPanel callingPanel)
	{
		super(searchModel);
		
		// Note: We need the same query as the calling Panel, 
		// but without the offset and limit. 
		// The LocationSearchResultPanel removes them, but that is in the given model. 
		// The previous page (with the calling Panel) will also change the model used by the previous page 
		// The (simple) solution used here is to store the limit and offset and reset them when navigating back

		// Store original limit and offset!
		final int limit = searchModel.getRequestBuilder().getLimit();
		final int offset = searchModel.getRequestBuilder().getOffset();
		
	    // get the back page
    	backPage = ((DccdSession)Session.get()).getRedirectPage(getPage().getClass());
    	
    	// The back button or link
		Link backButton = new Link("backButton", new ResourceModel("backButton"))
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick()
			{
		        // get the previous page, and try to browse back
		        Page page = backPage;
		        if (page != null)
		        {
		        	// restore limit and offset
		        	SearchRequestBuilder requestBuilder = 
		        		((SearchModel)PeriodSearchResultPage.this.getDefaultModel()).getRequestBuilder();
		        	requestBuilder.setLimit(limit);
		        	requestBuilder.setOffset(offset);
		        	
		        	if (page instanceof BasePage)
	        			((BasePage)page).refresh();

		        	setResponsePage(page);
		        }
		        else
		        {
		        	// just go back to a new instance of HomePage
		        	setResponsePage(HomePage.class);
		        }
			}
		};
		add(backButton);
		
		PeriodSearchResultPanel periodSearchResultPanel = 
			new PeriodSearchResultPanel("periodSearchResultPanel", searchModel) 
			{
				private static final long	serialVersionUID	= 4048271283238729675L;

				@Override
				public SearchResult<?> search(SimpleSearchRequest request)
				{
					// use the calling Panels search, 
					// otherwise there would be no restrictions on Owner/State or Bean class
					SearchResult<?> searchResult = callingPanel.search(request);
					return searchResult;
				}
			};

		add(periodSearchResultPanel);
	}
}
