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

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.tridas.schema.TridasStatFoundation;

/**
 * @author paulboon
 */
public class StatFoundationPanel extends EntityAttributePanel {
	private static final long serialVersionUID = 6151017690618632765L;

	public StatFoundationPanel(String id, IModel model) {
		super(id, model, false);
	}

	public StatFoundationPanel(String id, IModel model, boolean editable) {
		super(id, model, editable); 
		//super(id, model, false); // disable editing
	}

	public class StatFoundationPanelView extends Panel {
		private static final long serialVersionUID = -2691262342673754195L;

		public StatFoundationPanelView(String id, IModel model) {
			super(id, model);

			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();

			// assume StatFoundation
			TridasStatFoundation statFoundation = (TridasStatFoundation)attr.getEntryObject();

		    // ControlledVoc type;
			String valueStr ="";
			String usedsoftwareStr = "";
			String significanceLevelStr = "";

			// check for null
			if (statFoundation != null) {
				// BigDecimal statValue;
				if (statFoundation.isSetStatValue()) {
					valueStr = statFoundation.getStatValue().toString();
				}

			    // BigDecimal significanceLevel;
				if (statFoundation.isSetSignificanceLevel()) {
					significanceLevelStr = statFoundation.getSignificanceLevel().toString();
				}

				//usedSoftware
				if (statFoundation.isSetUsedSoftware()) {
					usedsoftwareStr = statFoundation.getUsedSoftware();
				}
			}

			// Labels for the strings
			add(new Label("value", valueStr));
			add(new Label("significanceLevel", significanceLevelStr));
			add(new Label("usedsoftware", usedsoftwareStr));

			// type, use ControlledVocabularyPanel
			EntityAttribute vocAttr = new EntityAttribute(statFoundation, new UIMapEntry("voc", "type"));
			add(new ControlledVocabularyPanel("voc_panel", new Model(vocAttr)));
		}
	}

	public class StatFoundationPanelEdit extends Panel {
		private static final long	serialVersionUID	= 6351686309168354007L;

		public StatFoundationPanelEdit(String id, IModel model) {
			super(id, model);

			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();

			// assume StatFoundation
			TridasStatFoundation statFoundation = (TridasStatFoundation)attr.getEntryObject();
	
			if (statFoundation != null && statFoundation.isSetStatValue()) 
			{
				EntityAttribute decAttr = new EntityAttribute(statFoundation, "statValue");
				add(new DecimalPanel("value", new Model(decAttr), isEditable()));
			}
			else
			{
				add(new Panel("value").setVisible(false)); // empty
			}
			
			if (statFoundation != null)// && statFoundation.isSetSignificanceLevel()) 
			{
				EntityAttribute decAttr = new EntityAttribute(statFoundation, "significanceLevel");
				add(new DecimalPanel("significanceLevel", new Model(decAttr), isEditable()));
			}
			else
			{
				add(new Panel("significanceLevel").setVisible(false)); // empty
			}
			
			if (statFoundation != null && statFoundation.isSetUsedSoftware()) 
			{
				EntityAttribute textAttr = new EntityAttribute(statFoundation, "usedSoftware");
				TextPanel textPanel = new TextPanel("usedsoftware", new Model(textAttr), isEditable());
		        add(textPanel);
			}
			else
			{
				add(new Panel("usedsoftware").setVisible(false)); // empty
			}
			
			// type, use ControlledVocabularyPanel
			EntityAttribute vocAttr = new EntityAttribute(statFoundation, "type");
			add(new ControlledVocabularyPanel("voc_panel", new Model(vocAttr), isEditable()));
		}
	}

}

