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
import nl.knaw.dans.dccd.model.UIMapEntry;
import nl.knaw.dans.dccd.model.UIMapEntry.Multiplicity;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.tridas.schema.TridasLaboratory;

/**
 * @author paulboon
 */
public class LaboratoryPanel extends EntityAttributePanel {
	private static final long	serialVersionUID	= 2515832048284085900L;

	public LaboratoryPanel(String id, IModel model) {
		super(id, model, false);
	}

	public LaboratoryPanel(String id, IModel model, boolean editable) {
		super(id, model, editable);
		//super(id, model, false); // disable editing
	}
	
	public class LaboratoryPanelView extends Panel {
		private static final long serialVersionUID = -3909293491891860641L;
	
		public LaboratoryPanelView(String id, IModel model) {
			super(id, model);
	
			// get the TridasLaboratory object
			//TridasLaboratory laboratory = (TridasLaboratory)model.getObject();
			TridasLaboratory laboratory = (TridasLaboratory)((EntityAttribute) model.getObject()).getEntryObject();
	
			// populate  a small table
			// Identifier: id + domain
			EntityAttribute attr = new EntityAttribute(laboratory, new UIMapEntry("id", "identifier"));
			add(new IdentifierPanel("identifier", new Model(attr)));
	
			// name + acronym
			String nameStr ="";
			String acronymStr = "";
			if (laboratory != null && laboratory.isSetName()) {
				nameStr = laboratory.getName().getValue();
				acronymStr = laboratory.getName().getAcronym();
			}
			add(new Label("name", nameStr));
			add(new Label("acronym", acronymStr));
	
			EntityAttribute addressAttr = new EntityAttribute(laboratory, "address");
			add(new AddressPanel("address", new Model(addressAttr)));
		}
	}

	public class LaboratoryPanelEdit extends Panel {
		private static final long	serialVersionUID	= -1234059248489385210L;

		public LaboratoryPanelEdit(String id, IModel model) {
			super(id, model);
	
			// get the TridasLaboratory object
			//TridasLaboratory laboratory = (TridasLaboratory)model.getObject();
			TridasLaboratory laboratory = (TridasLaboratory)((EntityAttribute) model.getObject()).getEntryObject();
	
			// Identifier: id + domain
			//EntityAttribute attr = new EntityAttribute(laboratory, new UIMapEntry("id", "identifier"));
			//add(new IdentifierPanel("identifier", new Model(attr), isEditable()));
			// Optional Panel
			EntityAttribute attr = new EntityAttribute(laboratory, new UIMapEntry("id", "identifier")); // not optional in attribute
			EntityAttributeOptionalPanel optionalPanel = new EntityAttributeOptionalPanel(IdentifierPanel.class, "identifier", new Model(attr), isEditable());
			add(optionalPanel);

			// name should be set, but value and acronym are optional strings
			if (laboratory != null && laboratory.isSetName())
			{
				EntityAttribute nameAttr = new EntityAttribute(laboratory.getName(), "value");
				TextPanel namePanel = new TextPanel("name", new Model(nameAttr), isEditable());
		        add(namePanel);
		        
				EntityAttribute acronymAttr = new EntityAttribute(laboratory.getName(), "acronym");
				TextPanel acronymPanel = new TextPanel("acronym", new Model(acronymAttr), isEditable());
		        add(acronymPanel);
			}
			else
			{
				// empty panels
				add(new Panel("name").setVisible(false));	
				add(new Panel("acronym").setVisible(false));		
			}
			
			EntityAttribute addressAttr = new EntityAttribute(laboratory, "address");
			add(new AddressPanel("address", new Model(addressAttr), isEditable()));
		}
	}

}
