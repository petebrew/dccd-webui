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

import java.util.List;

import nl.knaw.dans.common.lang.search.SearchRequest;
import nl.knaw.dans.common.lang.search.SearchResult;
import nl.knaw.dans.common.lang.search.SortOrder;
import nl.knaw.dans.common.lang.search.SortType;
import nl.knaw.dans.common.lang.search.simple.SimpleField;
import nl.knaw.dans.common.lang.search.simple.SimpleSortField;
import nl.knaw.dans.common.wicket.components.search.criteria.CriteriumLabel;
import nl.knaw.dans.common.wicket.components.search.model.SearchModel;
import nl.knaw.dans.common.wicket.components.search.results.SortLinkConfig;
import nl.knaw.dans.dccd.application.services.DccdSearchService;
import nl.knaw.dans.dccd.application.services.SearchServiceException;
import nl.knaw.dans.dccd.application.services.ServiceException;
import nl.knaw.dans.dccd.model.DccdUser;
import nl.knaw.dans.dccd.search.DccdProjectSB;
import nl.knaw.dans.dccd.search.DccdSB;
import nl.knaw.dans.dccd.web.DccdSession;
import nl.knaw.dans.dccd.web.search.AbstractSearchResultPage;

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyProjectsSearchResultPage extends AbstractSearchResultPage
{    
	private static final Logger	LOGGER 				= LoggerFactory.getLogger(MyProjectsSearchResultPage.class);

	public MyProjectsSearchResultPage()
	{
		super(true);
	}
	
	public MyProjectsSearchResultPage(SearchModel searchModel)
	{
		super(searchModel);
	}

	@Override
	protected SearchResult<? extends DccdSB> doSearch(SearchRequest request)
			throws ServiceException
	{
		LOGGER.debug("MyProjectsSearchResultPage Search");
		
		redirectIfNotLoggedIn();
		
		// NOTE EOF handles type of search in service layer; which is better!
    	DccdUser user = (DccdUser) ((DccdSession) getSession()).getUser();
		try
		{
	    	// restrict results to the current user as owner
			String userId =  user.getId();
			// Escape any whitespace characters, because otherwise the search will fail!
			userId = userId.replaceAll(" ", "\\\\ ");
			SimpleField<String> ownerIdField = new SimpleField<String>(DccdSB.OWNER_ID_NAME, userId);
			request.addFilterQuery(ownerIdField);
			
			request.addFilterBean(DccdProjectSB.class);// Show Project and not the standard Object result

			// Sorting
			request.addSortField(new SimpleSortField(DccdProjectSB.ADMINISTRATIVE_STATE_NAME, SortOrder.ASC));
			request.addSortField(new SimpleSortField(DccdProjectSB.ADMINISTRATIVE_STATE_LASTCHANGE, SortOrder.DESC));
			//request.addSortField(new SimpleSortField(DccdProjectSB.TRIDAS_PROJECT_IDENTIFIER_EXACT_NAME, SortOrder.DESC));
			//
			// Trying more sorting fails!
			//request.addSortField(new SimpleSortField(DccdProjectSB.TRIDAS_PROJECT_TITLE_NAME, SortOrder.DESC));

			return DccdSearchService.getService().doSearch(request);
		}
		catch (SearchServiceException e)
		{
			throw new ServiceException(e);
		}
	}
	
	@Override
	protected IModel<String> getInitialCriteriumText()
	{
		return new ResourceModel("myprojects.defaultbreadcrumbtext");
	}
	
	@Override
	protected IModel<String> getSearchCriteriumText(final String searchText)
	{
		return new AbstractReadOnlyModel<String>()
		{
			private static final long	serialVersionUID	= 3254972701101566016L;

			@Override
			public String getObject()
			{
				return CriteriumLabel.createFilterText(getString("myprojects.searchbreadcrumbtext"), searchText);
			}
		};
	}

	@Override
	protected List<SortLinkConfig> getSortLinks()
	{
		List<SortLinkConfig> sortLinks = super.getSortLinks();
		sortLinks.add( new SortLinkConfig(DccdProjectSB.ADMINISTRATIVE_STATE_NAME, SortType.BY_VALUE) );
		sortLinks.add( new SortLinkConfig(DccdProjectSB.ADMINISTRATIVE_STATE_LASTCHANGE, SortType.BY_VALUE) );
		//sortLinks.add( new SortLinkConfig(DccdProjectSB.TRIDAS_PROJECT_IDENTIFIER_NAME, SortType.BY_VALUE) );
		// Following doesn't work!
		//sortLinks.add( new SortLinkConfig(DccdProjectSB.TRIDAS_PROJECT_TITLE_NAME, SortType.BY_VALUE) );
		
		return sortLinks;
	}
}
