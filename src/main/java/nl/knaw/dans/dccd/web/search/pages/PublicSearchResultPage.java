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

import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.search.SearchRequest;
import nl.knaw.dans.common.lang.search.SearchResult;
import nl.knaw.dans.common.lang.search.simple.SimpleField;
import nl.knaw.dans.common.wicket.components.search.criteria.CriteriumLabel;
import nl.knaw.dans.common.wicket.components.search.model.SearchModel;
import nl.knaw.dans.dccd.application.services.DccdSearchService;
import nl.knaw.dans.dccd.application.services.SearchServiceException;
import nl.knaw.dans.dccd.application.services.ServiceException;
import nl.knaw.dans.dccd.model.DccdUser;
import nl.knaw.dans.dccd.model.DccdUser.Role;
import nl.knaw.dans.dccd.search.DccdObjectSB;
import nl.knaw.dans.dccd.search.DccdSB;
import nl.knaw.dans.dccd.web.DccdSession;
import nl.knaw.dans.dccd.web.search.AbstractSearchResultPage;

import org.apache.wicket.PageParameters;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublicSearchResultPage extends AbstractSearchResultPage
{
	private static final Logger	LOGGER 				= LoggerFactory.getLogger(PublicSearchResultPage.class);
	
	public PublicSearchResultPage()
	{
		super(false);
	}
	
	public PublicSearchResultPage(SearchModel model)
	{
		super(model);
	}
	
	public PublicSearchResultPage(PageParameters pm)
	{
		super(pm);
	}	

	@Override
	protected boolean showTips()
	{
		return true;
	}

	protected SearchResult<? extends DccdSB> doSearch(SearchRequest request)
	throws ServiceException
	{	
		// NOTE EOF handles public search in service layer; which is better!
		
		LOGGER.debug("PublicSearchResultPage Search");
		
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
	
	@Override
	protected IModel<String> getInitialCriteriumText()
	{
		return new ResourceModel("publicsearch.defaultbreadcrumbtext");
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
				return CriteriumLabel.createFilterText(getString("publicsearch.searchbreadcrumbtext"), searchText);
			}
		};
	}
}
