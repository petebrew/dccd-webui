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

import java.util.Arrays;

import nl.knaw.dans.dccd.model.EntityAttribute;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.tridas.schema.NormalTridasLocationType;

/**
 * @author dev
 */

public class LocationTypePanel extends EntityAttributePanel {
	private static final long	serialVersionUID	= 6464234114314188283L;

	public LocationTypePanel(String id, IModel model) {
		super(id, model, false);
	}

	public LocationTypePanel(String id, IModel model, final boolean editable) {
		super(id, model, editable);
		//super(id, model, false); // disable editing!
	}

	public class LocationTypePanelView extends Panel {
		private static final long	serialVersionUID	= -3124026700096703831L;

		public LocationTypePanelView(String id, IModel model) {
			super(id, model);

			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();

			// assume NormalTridasLocationType
			NormalTridasLocationType val = (NormalTridasLocationType)attr.getEntryObject();
			
			String str = "";

			if(val != null) {
				str = val.value();  //.toString();
			}

			Label label = new Label("text", new Model(str));
			add(label);			
		}
	}
	
	public class LocationTypePanelEdit extends Panel {
		private static final long	serialVersionUID	= -8532601863895626431L;

		public LocationTypePanelEdit(String id, IModel model) {
			super(id, model);

			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();

			// assume NormalTridasLocationType
			NormalTridasLocationType val = (NormalTridasLocationType)attr.getEntryObject();
			
			if (val != null)
			{				
				DropDownChoice choice = new DropDownChoice("type", 
						new PropertyModel(attr.getObject(), attr.getEntry().getMethod()), 
						Arrays.asList(NormalTridasLocationType.values()),
						new ChoiceRenderer("value", "value")
				);
				add(choice);
			}
			else
			{
				// empty panel
				add(new DropDownChoice("type").setVisible(false));				
			}
		}
	}	
}
