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
package nl.knaw.dans.dccd.web.entitytree;

import java.io.Serializable;

import nl.knaw.dans.dccd.application.services.UIMapper;
import nl.knaw.dans.dccd.model.ProjectPermissionLevel;
import nl.knaw.dans.dccd.model.entities.Entity;
import nl.knaw.dans.dccd.model.entities.ObjectEntity;
import nl.knaw.dans.dccd.model.entities.ProjectEntity;
import nl.knaw.dans.dccd.web.datapanels.DendroEntityPanel;

import org.apache.log4j.Logger;
import org.apache.wicket.model.Model;

/** Wrapper for DendroEntity objects, used for the (Wicket based) entity tree view;
 * each node in the tree has an entity
 * and a Panel to display the data
 *
 * @author paulboon
 *
 */
public class EntityUITreeNodeModel implements Serializable
{
	private static final long serialVersionUID = 8677781650371359216L;
	private static Logger logger = Logger.getLogger(EntityUITreeNodeModel.class);

	private static final UIMapper mapper = new UIMapper();
	// note: performance is probably better if mapper is a static

	private Entity entity = null;
	private DendroEntityPanel panel = null;
	private boolean panelEditable = false;
	private String panelId = "";

	private ProjectPermissionLevel permissionLevel;

	public EntityUITreeNodeModel(Entity entity, String panelId,
			ProjectPermissionLevel permissionLevel, boolean panelEditable)
	{
		this.entity = entity;
		this.panelId = panelId;
		this.permissionLevel = permissionLevel;
		this.panelEditable = panelEditable;
		// note: defer panel creation until requested
	}

	//TODO: LB20090923: the model should not be responsible for getting the panel
	// it is bad design. This is up to the controller!
	//
	// Note: creates the panel if it doesn't have one
	public DendroEntityPanel getDendroEntityPanel()
	{
		// only create one if it is not there
		if (panel == null )
		{
			createDendroEntityPanel();
		}

		return panel;
	}
	
	// create a new Panel, and keep it for later retrievals by getDendroEntityPanel
	// existing panel is overwritten
	public DendroEntityPanel createDendroEntityPanel()
	{
		// check if there is data for the panel
		if (entity.getTridasAsObject() != null)
		{
			// create it
			panel = new DendroEntityPanel(panelId, new Model((Serializable) entity.getTridasAsObject()), panelEditable);
		}
		else
		{
			logger.warn("No panel created");
			// Not sure if it would be better to throw a runtime exception here!
		}
		
		return panel;		
	}
	
	public Entity getEntity()
	{
		return entity;
	}

	@Override
	public String toString()
	{
		// Note: assume Project and Object have Title as open access
		if (entity instanceof ProjectEntity ||
			entity instanceof ObjectEntity ||
			entity.isPermittedBy(permissionLevel))
		{
			String str = entity.getTitle() + " (" + getName().toLowerCase() + ")";

			// indicate if valid for archiving
			if (panelEditable == true && !entity.isValidForArchiving())
			{
				str = "(!) " + str; 
			}
			return str;
		}
		else
		{
			return "(" + getName().toLowerCase() + ")";
		}
	}

	// just return the entity (type) name, without the title
	public String getName()
	{
		// Note: it's not localized, maybe do that on the tree panel!
		return mapper.getEntityLabelString(entity.getTridasClass());
	}

}
