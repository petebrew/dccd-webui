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
import org.tridas.schema.TridasDating;
import org.tridas.schema.NormalTridasDatingType;

/**
 * @author dev
 */

public class DatingPanel extends EntityAttributePanel {
	private static final long	serialVersionUID	= -1722209793792996185L;

	public DatingPanel(String id, IModel model) {
		super(id, model, false);
	}

	public DatingPanel(String id, IModel model, final boolean editable) {
		super(id, model, editable);
		//super(id, model, false); // disable editing!
	}

	public class DatingPanelView extends Panel {
		private static final long	serialVersionUID	= -406944291146837149L;

		public DatingPanelView(String id, IModel model) {
			super(id, model);

			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();

			// assume TridasDating
			TridasDating dating = (TridasDating)attr.getEntryObject();
			
			String str = "";

			if(dating != null && dating.isSetType()) {
				str = dating.getType().value();  //.toString();
			}

			Label label = new Label("text", new Model(str));
			add(label);			
		}
	}
	
	public class DatingPanelEdit extends Panel {
		private static final long	serialVersionUID	= 2180466135523625416L;

		public DatingPanelEdit(String id, IModel model) {
			super(id, model);

			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();

			// assume TridasDating
			TridasDating dating = (TridasDating)attr.getEntryObject();
			
			if (dating != null)
			{				
				DropDownChoice choice = new DropDownChoice("type", 
						new PropertyModel(dating, "type"), 
						Arrays.asList(NormalTridasDatingType.values()),
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
