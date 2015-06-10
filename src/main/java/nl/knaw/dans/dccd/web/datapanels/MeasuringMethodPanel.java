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

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.tridas.schema.NormalTridasMeasuringMethod;
import org.tridas.schema.TridasMeasuringMethod;

/**
 * @author paulboon
 */
public class MeasuringMethodPanel extends EntityAttributePanel {
	private static final long serialVersionUID = -3731680076363841307L;

	public MeasuringMethodPanel(String id, IModel model) {
		super(id, model, false);
	}

	public MeasuringMethodPanel(String id, IModel model, boolean editable) {
		super(id, model, editable); 
		//super(id, model, false); // disable editing
	}

	public class MeasuringMethodPanelView extends Panel {
		private static final long serialVersionUID = 1310318714574344293L;

		public MeasuringMethodPanelView(String id, IModel model) {
			super(id, model);

			// assume TridasMeasuringMethod
			TridasMeasuringMethod measuringMethod = (TridasMeasuringMethod)((EntityAttribute) model.getObject()).getEntryObject();

			// Add the controlled voc panel
			add(new ControlledVocabularyPanel("cvoc_panel", model));

			// also has NormalTridasCategory, controlled vocabulary of research categories ?
			if (measuringMethod != null && measuringMethod.isSetNormalTridas()) {
				EntityAttribute attr = new EntityAttribute(measuringMethod, "normalTridas");
				TridasEnumPanel normalPanel = new TridasEnumPanel("normalmethod", new Model(attr), isEditable());
				add(normalPanel);							
			}
			else
			{
				// empty
				add(new Label("normalmethod", "").setVisible(false));
			}
		}
	}
	
	// Note: almost identical to the view because we have a subpanel
	public class MeasuringMethodPanelEdit extends Panel {
		private static final long	serialVersionUID	= 838292327177357098L;

		public MeasuringMethodPanelEdit(String id, IModel model) {
			super(id, model);
	
			// get the Tridas object
			TridasMeasuringMethod measuringMethod = (TridasMeasuringMethod)((EntityAttribute) model.getObject()).getEntryObject();
	
			// Add the controlled voc..panel?
			//EntityAttribute attr = new EntityAttribute(model, new UIMapEntry("cvoc", "Object"));
			add(new ControlledVocabularyPanel("cvoc_panel", model, true));//new Model(attr)));
	
			// also has NormalTridasCategory, controlled vocabulary of research categories ?
			//if (measuringMethod.isSetNormalTridas()) {
				EntityAttribute attr = new EntityAttribute(measuringMethod, "normalTridas");
				//TridasEnumPanel normalPanel = new TridasEnumPanel("normalmethod", new Model(attr), isEditable());
				EntityAttributeOptionalPanel normalPanel = new EntityAttributeOptionalPanel(NormalTridasMeasuringMethodPanel.class, "normalmethod", new Model(attr), isEditable());
				
				add(normalPanel);			
			//}
			//else
			//{
			//	add(new TextField("normalmethod").setVisible(false));				
			//}
		}
	}
		
}

