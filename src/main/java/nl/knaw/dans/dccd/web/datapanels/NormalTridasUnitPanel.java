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
import org.tridas.schema.NormalTridasShape;
import org.tridas.schema.NormalTridasUnit;

/**
 * @author dev
 */

public class NormalTridasUnitPanel extends EntityAttributePanel {
	private static final long	serialVersionUID	= 9192195333360361433L;

	public NormalTridasUnitPanel(String id, IModel model) {
		super(id, model, false);
	}

	public NormalTridasUnitPanel(String id, IModel model, final boolean editable) {
		super(id, model, editable);
		//super(id, model, false); // disable editing!
	}

	public class NormalTridasUnitPanelView extends Panel {
		private static final long	serialVersionUID	= -18031928695222292L;

		public NormalTridasUnitPanelView(String id, IModel model) {
			super(id, model);

			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();

			// assume NormalTridasUnit
			NormalTridasUnit val = (NormalTridasUnit)attr.getEntryObject();
			
			String str = "";

			if(val != null) {
				str = val.value();  //.toString();
			}

			Label label = new Label("text", new Model(str));
			add(label);			
		}
	}
	
	public class NormalTridasUnitPanelEdit extends Panel {
		private static final long	serialVersionUID	= -7666880861213048551L;

		public NormalTridasUnitPanelEdit(String id, IModel model) {
			super(id, model);

			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();

			// assume NormalTridasUnit
			NormalTridasUnit val = (NormalTridasUnit)attr.getEntryObject();
			
			if (val != null)
			{				
				DropDownChoice choice = new DropDownChoice("type", 
						new PropertyModel(attr.getObject(), attr.getEntry().getMethod()), 
						Arrays.asList(NormalTridasUnit.values()),
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
