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

import nl.knaw.dans.common.lang.search.SearchHit;
import nl.knaw.dans.common.wicket.components.search.model.SearchModel;
import nl.knaw.dans.dccd.application.services.DataServiceException;
import nl.knaw.dans.dccd.application.services.DccdDataService;
import nl.knaw.dans.dccd.common.wicket.geo.GeoViewer;
import nl.knaw.dans.dccd.model.Project;
import nl.knaw.dans.dccd.search.DccdProjectSB;
import nl.knaw.dans.dccd.search.DccdSB;
import nl.knaw.dans.dccd.web.project.ProjectViewPage;

import org.apache.log4j.Logger;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

/**
 * @author paulboon
 */
public class DccdProjectHitPanel extends Panel
{
	private static Logger logger = Logger.getLogger(DccdProjectHitPanel.class);
	private static final long serialVersionUID = -4020133452875145094L;

	Project project = null;//new Project();

	public DccdProjectHitPanel(String wicketId, IModel model, SearchModel svModel)
	{
		super(wicketId, model);
        SearchHit<DccdProjectSB> hit = (SearchHit<DccdProjectSB>) model.getObject();
        final DccdProjectSB dccdHit = hit.getData();

		logger.debug("Project Hit: " + dccdHit.toString());

		init(dccdHit);
	}

	/**
	 * display the search result
	 * get all information to display from the SearchBean
	 * instead of from the Project and it's Tridas
	 *
	 * @param dccdHit
	 */
	private void init(final DccdProjectSB dccdHit)
	{
		addProjectInformation(dccdHit);
	}

	private void addProjectInformation(final DccdProjectSB dccdHit)
	{
		String projectTitleStr = dccdHit.getTridasProjectTitle();//tridasProject.getTitle();
        //add(new Label("project_title", projectTitleStr));

        // Make a link to the project view page
		// Put it on the whole panel
        Link viewLink = new Link("project_view")//, new Model(resultItem))
        {
			private static final long serialVersionUID = 1L;
			@Override
			public void onClick()
			{
				// now navigate to view page with given project;
				retrieveProject(dccdHit);
				setResponsePage(new ProjectViewPage(project));
			}
        };
        add(viewLink);
        //viewLink.add(new Label("project_title",projectTitleStr));

		// Show status, ProjectHits are shown only to the Manager(MyProjects) or admin?, 
		// so we don't need to check here
		//viewLink.add(new Label("project_status_value", dccdHit.getAdministrativeState()));
		viewLink.add(new Label("project_status_value", 
		new StringResourceModel(
				"datasetState.${administrativeState}", this,
				new Model(dccdHit))));
				
		// TODO show administrativeMetadata.lastStateChange

		// using the title as link text LOOKS NORMAL TO THE USER
		Link viewLinkOnTitle = new Link("project_title_view")//, new Model(resultItem))
		{
			private static final long serialVersionUID = 1L;
			@Override
			public void onClick()
			{
				// now navigate to view page with given project;
				retrieveProject(dccdHit);
				setResponsePage(new ProjectViewPage(project));
			}
		};
		viewLinkOnTitle.add(new Label("project_title",projectTitleStr));
		viewLink.add(viewLinkOnTitle);

        //ID: <project identifier> (domain: <project domain>)
		String identifierStr = "";
		if (dccdHit.hasTridasProjectIdentifier())
			identifierStr = dccdHit.getTridasProjectIdentifier();
		String domainStr = "";
		if (dccdHit.hasTridasProjectIdentifierDomain())
			domainStr = dccdHit.getTridasProjectIdentifierDomain();
		viewLink.add(new Label("identifier", identifierStr));
		Label domainLabel = new Label("domain", domainStr);
		viewLink.add(domainLabel);

        //Lab(s): <lab name>, <lab place>, <lab country>
		viewLink.add(new Label("project_laboratory", DccdHitPanel.getProjectLabString(dccdHit)));

        // investigator
		String projectInvestigatorStr = "";
		if (dccdHit.hasTridasProjectInvestigator()) {
			projectInvestigatorStr = dccdHit.getTridasProjectInvestigator();
		}
		viewLink.add(new Label("project_investigator", projectInvestigatorStr));
		//Type(s): <type normal>
		viewLink.add(new Label("project_type", DccdHitPanel.getProjectTypeString(dccdHit)));
        //Category: <category normal>
		viewLink.add(new Label("project_category", DccdHitPanel.getProjectCategoryString(dccdHit)));
        //<project description>
		viewLink.add(new Label("project_description", DccdHitPanel.getProjectDescriptionString(dccdHit)));
		
		// add location marker icon
		// You always have Permission on the MyProjects Page!
		boolean hasMarkerOnMap = dccdHit.hasLatLng();
		ResourceReference markerIconImageReference = GeoViewer.getDefaultMarkerIconImageReference();		
		if(hasMarkerOnMap) 
		{
			// use index specific marker icon image
			int latLngMarkerIndex = dccdHit.latLngMarkerIndex;
			logger.debug("Marker index to use on HitPanel: " + latLngMarkerIndex);
			if (latLngMarkerIndex >= 0)
				markerIconImageReference = GeoViewer.getLetterMarkerIconImageReference(latLngMarkerIndex);
		}		
		Image geoMarkerIconImage = new Image("geoMarkerIconImage", markerIconImageReference);
		viewLink.add(geoMarkerIconImage);		
		// only show icon if marker is on the map
		geoMarkerIconImage.setVisible(hasMarkerOnMap);		
	}

	private void retrieveProject(DccdSB dccdHit)
	{
		try
		{
			project = DccdDataService.getService().getProject(dccdHit.getPid());
		}
		catch (DataServiceException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

