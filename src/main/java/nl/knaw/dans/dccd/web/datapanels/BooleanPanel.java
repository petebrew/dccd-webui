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
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

/**
 * @author paulboon
 */
public class BooleanPanel extends EntityAttributePanel {

	private static final long serialVersionUID = -8857074636711970526L;

	// Note: do I need those two constructors???
	public BooleanPanel(String id, IModel model) {
		super(id, model, false);
	}

	public BooleanPanel(String id, IModel model, final boolean editable) {
		super(id, model, editable);
		//super(id, model, false); // disable editing!
	}

	public class BooleanPanelView extends Panel {
		private static final long serialVersionUID = -761910690499775518L;

		public BooleanPanelView(String id, IModel model) {
			super(id, model);
			// now do the view stuff (non-editable)
			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();

			// assume boolean
			// Also note that JAXB generates a getter
			// with the "is" prefix for Booleans
			Boolean bool = (Boolean)attr.getEntryObject("is");
			String bool_str = "";

			if(bool != null) {
				bool_str = bool.toString();
			}

			Label label = new Label("text", new Model(bool_str));

			//Label label = new Label("text", new PropertyModel(attr.getObject(),
			//		attr.getEntry().getMethod()));
			add(label);
		}
	}

	public class BooleanPanelEdit extends Panel {
		private static final long	serialVersionUID	= 4993904806467840219L;

		public BooleanPanelEdit(String id, IModel model) {
			super(id, model);
			// now do the view stuff (non-editable)
			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();

			// assume boolean
			// Also note that JAXB generates a getter
			// with the "is" prefix for Booleans
			//Boolean val = (Boolean)attr.getEntryObject("is");
			
			// Use checkbox without any text
			CheckBox boolField = new CheckBox("bool", 
					new PropertyModel(attr.getObject(), attr.getEntry().getMethod()) 
					);
			add(boolField);
			
		}
	}
	
}

