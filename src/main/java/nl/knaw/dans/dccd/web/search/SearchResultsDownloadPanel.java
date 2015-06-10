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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import nl.knaw.dans.common.lang.search.SearchHit;
import nl.knaw.dans.common.lang.search.SearchResult;
import nl.knaw.dans.common.lang.search.bean.StringListCollapserConverter;
import nl.knaw.dans.common.lang.search.exceptions.SearchBeanConverterException;
import nl.knaw.dans.common.lang.search.simple.EmptySearchResult;
import nl.knaw.dans.common.lang.search.simple.SimpleSearchRequest;
import nl.knaw.dans.common.wicket.components.search.SearchPanel;
import nl.knaw.dans.common.wicket.components.search.model.SearchCriterium;
import nl.knaw.dans.common.wicket.components.search.model.SearchData;
import nl.knaw.dans.common.wicket.components.search.model.SearchModel;
import nl.knaw.dans.common.wicket.components.search.model.SearchRequestBuilder;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.dccd.application.services.DataServiceException;
import nl.knaw.dans.dccd.application.services.DccdDataService;
import nl.knaw.dans.dccd.application.services.DccdSearchService;
import nl.knaw.dans.dccd.application.services.SearchServiceException;
import nl.knaw.dans.dccd.common.lang.geo.LonLat;
import nl.knaw.dans.dccd.model.DccdUser;
import nl.knaw.dans.dccd.model.ProjectPermissionLevel;
import nl.knaw.dans.dccd.search.DccdObjectSB;
import nl.knaw.dans.dccd.search.DccdSB;
import nl.knaw.dans.dccd.util.StringUtil;
import nl.knaw.dans.dccd.web.DccdSession;
import nl.knaw.dans.dccd.web.search.pages.LocationSearchResultPage;

import org.apache.wicket.Resource;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxIndicatorAware;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.extensions.ajax.markup.html.AjaxIndicatorAppender;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.markup.html.WebResource;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.request.target.resource.ResourceStreamRequestTarget;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;
import org.apache.wicket.util.value.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang.StringEscapeUtils;

// Note that the search method must be implemented
public class SearchResultsDownloadPanel extends Panel
{
	private static final long	serialVersionUID	= 4661758569555431068L;
	private static final Logger	logger = LoggerFactory.getLogger(SearchResultsDownloadPanel.class);
	
	private DccdUser user;
	
	private SearchPanel callingPanel;
	
	public SearchResultsDownloadPanel(String id, SearchModel model, final DccdSearchResultPanel callingPanel) 
	{
		super(id, model);
		
		if(callingPanel == null) throw new IllegalArgumentException("calling panel should not be null");
		this.callingPanel = callingPanel;
		
		init();
	}
	
	private void init()
	{
		// Need the user for the permission check
		user = (DccdUser) ((DccdSession) getSession()).getUser();
		
		//add(new ResourceLink("downloadSearchResults", getWebResource()));
		// TEST add link to a test page
		add(new Link("downloadSearchResults")
		{
			private static final long	serialVersionUID	= 1L;

			public void onClick()
			{
				//setResponsePage(new SearchResultDownloadPage());
				// support back navigation
				((DccdSession)Session.get()).setRedirectPage(SearchResultDownloadPage.class, getPage());
				setResponsePage(new SearchResultDownloadPage((SearchModel) SearchResultsDownloadPanel.this.getDefaultModel(), 
						(DccdSearchResultPanel) callingPanel));
			}
		});
		
//		add(new AjaxIndicatingResourceLink("downloadSearchResults", getWebResource()));
		
//		// TEST
//		final AjaxDownload download = new AjaxDownload()
//		{
//			@Override
//			protected IResourceStream getResourceStream()
//			{
//				// return getWebResource().getResourceStream();
//				return resourceStream;
//			}
//
//				@Override
//				protected String getFileName()
//				{
//					return "results.xml";
//				}
//		};
//		add(download);
//		IndicatingAjaxLink exportLink = new IndicatingAjaxLink("downloadSearchResults")
//		{
//			private static final long	serialVersionUID	= 1L;
//
//			@Override
//			public void onClick(AjaxRequestTarget target)
//			{
//				// DO the work here!
//				resourceStream = getResourceStreamForXML();// getWebResource();
//
//				download.initiate(target);
//			}
//		};
//		add(exportLink);
	}
	
//	public class AjaxIndicatingResourceLink extends ResourceLink implements IAjaxIndicatorAware {
//		private static final long	serialVersionUID	= 4595176980604331874L;
//		private final AjaxIndicatorAppender indicatorAppender = new AjaxIndicatorAppender();
//		public AjaxIndicatingResourceLink(String id, Resource resource)
//		{
//			super(id, resource);
//			add(indicatorAppender);
//		}
//
//		@Override
//		public String getAjaxIndicatorMarkupId()
//		{
//			return indicatorAppender.getMarkupId();
//		}
//	};
	
//	// TEST with AjaxDownload does not work in IE7
//	public transient IResourceStream resourceStream = null;	
//	public abstract class AjaxDownload extends AbstractAjaxBehavior
//	{
//		private static final long	serialVersionUID	= -5032130386313725143L;
//
//		/**
//		 * Call this method to initiate the download.
//		 */
//		public void initiate(AjaxRequestTarget target)
//		{
//			CharSequence url = getCallbackUrl();
//
//			target.appendJavascript("window.location.href='" + url + "'");
//		}
//
//		public void onRequest()
//		{
//			getComponent().getRequestCycle().setRequestTarget(new ResourceStreamRequestTarget(getResourceStream(), getFileName()));
//		}
//
//		/**
//		 * @see ResourceStreamRequestTarget#getFileName()
//		 */
//		protected String getFileName()
//		{
//			return null;
//		}
//
//		/**
//		 * Hook method providing the actual resource stream.
//		 */
//		protected abstract IResourceStream getResourceStream();
//	}

}
