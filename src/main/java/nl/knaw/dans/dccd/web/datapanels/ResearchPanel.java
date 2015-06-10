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
package nl.knaw.dans.dccd.web.datapanels;

import nl.knaw.dans.dccd.model.EntityAttribute;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.tridas.schema.TridasResearch;

/**
 * @author paulboon
 */
public class ResearchPanel extends EntityAttributePanel 
{
	private static final long	serialVersionUID	= -8534219588622541823L;

	public ResearchPanel(String id, IModel model) {
		super(id, model, false);
	}

	public ResearchPanel(String id, IModel model, final boolean editable) {
		super(id, model, editable);
		//super(id, model, false); // only allow view
	}
	
	public class ResearchPanelView extends Panel {
		private static final long serialVersionUID = -4108476441865575976L;
	
		public ResearchPanelView(String id, IModel model) {
			super(id, model);
	
			// get the TridasResearch object
			//TridasResearch research = (TridasResearch)model.getObject();
			TridasResearch research = (TridasResearch)((EntityAttribute) model.getObject()).getEntryObject();
	
			// Identifier: id + domain
			EntityAttribute attr = new EntityAttribute(research, "identifier");
			add(new IdentifierPanel("identifier", new Model(attr), isEditable()));
		        
			if (research != null && research.isSetDescription())
			{
				EntityAttribute descriptionAttr = new EntityAttribute(research, "description");
				TextPanel descriptionPanel = new TextPanel("description", new Model(descriptionAttr), isEditable());
				add(descriptionPanel);
			}
			else
			{
				// empty panel
				add(new Panel("description").setVisible(false));	
			}
		}
	}

	// Note identical to view
	public class ResearchPanelEdit extends Panel {
		private static final long	serialVersionUID	= -1925209654156235935L;

		public ResearchPanelEdit(String id, IModel model) {
			super(id, model);
	
			// get the TridasResearch object
			//TridasResearch research = (TridasResearch)model.getObject();
			TridasResearch research = (TridasResearch)((EntityAttribute) model.getObject()).getEntryObject();
	
			// Identifier: id + domain
			EntityAttribute attr = new EntityAttribute(research, "identifier");
			add(new IdentifierPanel("identifier", new Model(attr), isEditable()));
		        
			if (research != null && research.isSetDescription())
			{
				EntityAttribute descriptionAttr = new EntityAttribute(research, "description");
				TextPanel descriptionPanel = new TextPanel("description", new Model(descriptionAttr), isEditable());
				add(descriptionPanel);
			}
			else
			{
				// empty panel
				add(new Panel("description").setVisible(false));	
			}
		}
	}

}
