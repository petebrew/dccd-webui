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

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.tridas.schema.TridasBedrock;

/**
 * @author paulboon
 */
public class BedrockPanel extends EntityAttributePanel {
	private static final long	serialVersionUID	= 5108270847765338581L;

	public BedrockPanel(String id, IModel model) {
		super(id, model, false);
	}

	public BedrockPanel(String id, IModel model, boolean editable) {
		super(id, model, editable);
		//super(id, model, false); // disable editing
	}
	
	public class BedrockPanelView extends Panel {
		private static final long serialVersionUID = -2379505558841972711L;
	
		public BedrockPanelView(String id, IModel model) {
			super(id, model);
	
			// get the TridasBedrock object
			TridasBedrock bedrock = (TridasBedrock)((EntityAttribute) model.getObject()).getEntryObject();
	
			if (bedrock != null && bedrock.isSetDescription())
			{
				EntityAttribute attr = new EntityAttribute(bedrock, new UIMapEntry("description", "description"));
				TextAreaPanel descriptionPanel = new TextAreaPanel("description", new Model(attr), isEditable());
				add(descriptionPanel);
			}
			else
			{
				// empty panel
				add(new Panel("description").setVisible(false));	
			}
		}	
	}

	// Note: identical to view
	public class BedrockPanelEdit extends Panel {
		private static final long	serialVersionUID	= -7021798944395391769L;

		public BedrockPanelEdit(String id, IModel model) {
			super(id, model);
	
			// get the TridasBedrock object
			TridasBedrock bedrock = (TridasBedrock)((EntityAttribute) model.getObject()).getEntryObject();
	
			if (bedrock != null)// && bedrock.isSetDescription())
			{
				EntityAttribute attr = new EntityAttribute(bedrock, new UIMapEntry("description", "description"));
				TextAreaPanel descriptionPanel = new TextAreaPanel("description", new Model(attr), isEditable());
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
