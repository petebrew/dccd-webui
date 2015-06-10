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

import nl.knaw.dans.common.lang.search.SearchHit;
import nl.knaw.dans.common.lang.search.bean.StringListCollapserConverter;
import nl.knaw.dans.common.lang.search.exceptions.SearchBeanConverterException;
import nl.knaw.dans.common.wicket.components.search.model.SearchModel;
import nl.knaw.dans.dccd.application.services.DataServiceException;
import nl.knaw.dans.dccd.application.services.DccdDataService;
import nl.knaw.dans.dccd.common.wicket.geo.GeoViewer;
import nl.knaw.dans.dccd.model.DccdUser;
import nl.knaw.dans.dccd.model.Project;
import nl.knaw.dans.dccd.model.ProjectPermissionLevel;
import nl.knaw.dans.dccd.model.DccdUser.Role;
import nl.knaw.dans.dccd.model.entities.ObjectEntity;
import nl.knaw.dans.dccd.search.DccdSB;
import nl.knaw.dans.dccd.util.StringUtil;
import nl.knaw.dans.dccd.web.DccdSession;
import nl.knaw.dans.dccd.web.project.ProjectViewPage;

import org.apache.log4j.Logger;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 */
public class DccdHitPanel extends Panel
{
	private static Logger logger = Logger.getLogger(DccdHitPanel.class);
	private static final long serialVersionUID = 3807884926564132301L;

	Project project = new Project();
	final ObjectEntity object = new ObjectEntity();

	public DccdHitPanel(String wicketId, IModel model, SearchModel svModel)
	{
		super(wicketId, model);

        SearchHit<DccdSB> hit = (SearchHit<DccdSB>) model.getObject();
        final DccdSB dccdHit = hit.getData();

		logger.debug("Hit: " + dccdHit.toString());

		init(dccdHit);
		//initFromTridas(dccdHit);
	}

	/**
	 * display the search result
	 * get all information to display from the SearchBean
	 * instead of from the Project and it's Tridas
	 *
	 * @param dccdHit
	 */
	private void init(final DccdSB dccdHit)
	{
		Link viewResultLink = new Link("viewResult")
		{
			private static final long serialVersionUID = 1L;
			@Override
			public void onClick()
			{
				// now navigate to view page with given project;
				retrieveProjectAndObject(dccdHit);
				// Note: could also give selected entity object!
				String selectedEntityId = object.getId();

				setResponsePage(new ProjectViewPage(project, selectedEntityId));
			}
        };
        add(viewResultLink);

		addObjectInformation(viewResultLink, dccdHit);
		addProjectInformation(viewResultLink, dccdHit);
		
		boolean hasMarkerOnMap = false;
		if (dccdHit.hasLatLng())
		{
			//Check for permission
			DccdUser user = (DccdUser) ((DccdSession) getSession()).getUser();
			ProjectPermissionLevel effectivelevel = dccdHit.getEffectivePermissionLevel(user);
			Boolean isAllowedToViewLocation =  ProjectPermissionLevel.OBJECT.isPermittedBy(effectivelevel);
			hasMarkerOnMap = isAllowedToViewLocation;
		}
		// add location marker icon
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
		viewResultLink.add(geoMarkerIconImage);
		// only show icon if marker is on the map
		geoMarkerIconImage.setVisible(hasMarkerOnMap);
	}

	private void addObjectInformation(MarkupContainer container, final DccdSB dccdHit)
	{
		// only the first title, assuming the sub-objects titles are placed after it
		String objectTitleStr = "";
		if (dccdHit.hasTridasObjectTitle())
			objectTitleStr = dccdHit.getTridasObjectTitle().get(0);//tridasObject.getTitle();
        // Make a link to the project view page
		// using the object title as link text
        Link viewLink = new Link("project_view")//, new Model(resultItem))
        {
			private static final long serialVersionUID = 1L;
			@Override
			public void onClick()
			{
				// now navigate to view page with given project;
				retrieveProjectAndObject(dccdHit);
				// Note: could also give selected entity object!
				String selectedEntityId = object.getId();

				setResponsePage(new ProjectViewPage(project, selectedEntityId));
			}
        };
        container.add(viewLink);
        viewLink.add(new Label("object_title_link",objectTitleStr));

        container.add(new Label("object_type", getObjectTypeString(dccdHit)));
	}

	private void addProjectInformation(MarkupContainer container, final DccdSB dccdHit)
	{
		String projectTitleStr = dccdHit.getTridasProjectTitle();//tridasProject.getTitle();
		container.add(new Label("project_title", projectTitleStr));

        //ID: <project identifier> (domain: <project domain>)
		String identifierStr = "";
		if (dccdHit.hasTridasProjectIdentifier())
			identifierStr = dccdHit.getTridasProjectIdentifier();
		String domainStr = "";
		if (dccdHit.hasTridasProjectIdentifierDomain())
			domainStr = dccdHit.getTridasProjectIdentifierDomain();
		container.add(new Label("identifier", identifierStr));
		Label domainLabel = new Label("domain", domainStr);
		container.add(domainLabel);

        //Lab(s): <lab name>, <lab place>, <lab country>
		container.add(new Label("project_laboratory", getProjectLabString(dccdHit)));

        // investigator
		String projectInvestigatorStr = "";
		if (dccdHit.hasTridasProjectInvestigator()) {
			projectInvestigatorStr = dccdHit.getTridasProjectInvestigator();
		}
		container.add(new Label("project_investigator", projectInvestigatorStr));
		//Type(s): <type normal>
		container.add(new Label("project_type", getProjectTypeString(dccdHit)));
        //Category: <category normal>
		container.add(new Label("project_category", getProjectCategoryString(dccdHit)));
        
		//<project description>
		//container.add(new Label("project_description", getProjectDescriptionString(dccdHit)));
		//Show only to admin (or manager)
		DccdUser user = (DccdUser) ((DccdSession) getSession()).getUser();
		if(user!=null && (user.hasRole(Role.ADMIN) || dccdHit.getOwnerId().compareTo(user.getId())==0))
		{		
			container.add(new Label("project_description", getProjectDescriptionString(dccdHit)));
		}
		else
		{
			// don't show
			container.add(new Label("project_description", "").setVisible(false));
		}
	}

	public static String getObjectTypeString(final DccdSB dccdHit)
	{
		String objectTypeStr = "";

		// Only show the type information of this object and not sub-objects
		// and only the Normal, if there is no Type (as direct value)
		if (dccdHit.hasTridasObjectType() && dccdHit.getTridasObjectType().get(0).length() > 0)
			objectTypeStr = dccdHit.getTridasObjectType().get(0);
		else if (dccdHit.hasTridasObjectTypeNormal() && dccdHit.getTridasObjectTypeNormal().get(0).length() > 0)
			objectTypeStr = dccdHit.getTridasObjectTypeNormal().get(0);

		return objectTypeStr;
	}

	public static String getProjectLabString(final DccdSB dccdHit)
	{
		String projectLabStr = "";

		if (dccdHit.hasTridasProjectLaboratoryCombined())
		{
			StringListCollapserConverter conv = new StringListCollapserConverter();
			for(String combinedString : dccdHit.getTridasProjectLaboratoryCombined())
			{
				try
				{
					List<String> combinedAsList = conv.fromFieldValue(combinedString);
					// Now I have read the bean filling code and know the order !
					if (combinedAsList.size() < 4)
					{
						logger.debug("Combine Project laboratory info incorrect: should have at least 4 items");
						continue; // skip this one, it is wrong
					}
					
					// name (would normally not be empty)
					String name = combinedAsList.get(0).trim();
					if (!name.isEmpty())
					{
						if (projectLabStr.length() > 0)
							projectLabStr += ", "; 
						projectLabStr += name; 
					}
					// skip acronym
					
					// place
					String place = combinedAsList.get(2).trim();
					if (!place.isEmpty())
						if (projectLabStr.length() > 0)
							projectLabStr += ", "; 
						projectLabStr += place; 
					
					// country
					String country = combinedAsList.get(3).trim();
					if (!country.isEmpty())
						if (projectLabStr.length() > 0)
							projectLabStr += ", "; 
						projectLabStr += country; 
				}
				catch (SearchBeanConverterException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

		return projectLabStr;
	}

	public static String getProjectTypeString(final DccdSB dccdHit)
	{
		String projectTypeStr = "";
		
		List<String> terms = new  ArrayList<String>();
		if (dccdHit.hasTridasProjectType())
			terms.addAll(dccdHit.getTridasProjectType());
		if (dccdHit.hasTridasProjectTypeNormal())
			terms.addAll(dccdHit.getTridasProjectTypeNormal());
		
		List<String> uniqueTerms = StringUtil.getUniqueStrings(terms);
		projectTypeStr = StringUtil.constructCommaSeparatedString(uniqueTerms);
		
		/*
		projectTypeStr = StringUtil.constructCommaSeparatedString(dccdHit.getTridasProjectType());
		
		if (dccdHit.hasTridasProjectTypeNormal() &&
				!dccdHit.getTridasProjectTypeNormal().isEmpty())
		{
			// OK, the list is not empty, but could have empty strings
			//String typeNormalString = merge(dccdHit.getTridasProjectTypeNormal(), ", ");
			String typeNormalString = StringUtil.constructCommaSeparatedString(dccdHit.getTridasProjectTypeNormal());
			if (!typeNormalString.isEmpty())
			{
				if (!projectTypeStr.isEmpty())// add seperator
					projectTypeStr += ", " + typeNormalString;
			}
		}
		*/
		return projectTypeStr;
	}

	public static String getProjectCategoryString(final DccdSB dccdHit)
	{
		// NOTE: not normalTridasCategory
		String projectCategoryStr = "";
		if (dccdHit.hasTridasProjectCategory())
			projectCategoryStr = dccdHit.getTridasProjectCategory();
		
		if (dccdHit.hasTridasProjectCategoryNormal() &&
			!dccdHit.getTridasProjectCategoryNormal().isEmpty() && 
			!projectCategoryStr.contentEquals(dccdHit.getTridasProjectCategoryNormal()))
		{
			if (!projectCategoryStr.isEmpty())// add seperator
				projectCategoryStr += ", ";
		    projectCategoryStr += dccdHit.getTridasProjectCategoryNormal();
		}
		return projectCategoryStr;
	}

	public static String getProjectDescriptionString(final DccdSB dccdHit)
	{
		String projectDescriptionStr = "";
		if (dccdHit.hasTridasProjectDescription()) {
			projectDescriptionStr = dccdHit.getTridasProjectDescription();
			//truncate at 200 chars
			final int max_chars = 200;
			// make it a resource that is language dependent?
			final String trunc_indicator = "[...]";
			if (projectDescriptionStr.length() > max_chars) {
				projectDescriptionStr = projectDescriptionStr.substring(0, max_chars-1-trunc_indicator.length());
				projectDescriptionStr += trunc_indicator;
			}
		}
		return projectDescriptionStr;
	}
	
	private void retrieveProjectAndObject(DccdSB dccdHit)
	{
		object.setId(dccdHit.getDatastreamId());
		//project.setSid(dccdHit.getPid());
		//// let's copy it to the StoreId as well...
		//project.setStoreId(project.getSid()); // TODO refactor this!

		try
		{
			project = DccdDataService.getService().getProject(dccdHit.getPid());
			DccdDataService.getService().retrieveEntity(dccdHit.getPid(), object);
		}
		catch (DataServiceException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

