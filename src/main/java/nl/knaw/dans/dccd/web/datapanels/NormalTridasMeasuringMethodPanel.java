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
import org.tridas.schema.NormalTridasMeasuringMethod;
import org.tridas.schema.NormalTridasShape;

/**
 * @author dev
 */

public class NormalTridasMeasuringMethodPanel extends EntityAttributePanel {
	private static final long	serialVersionUID	= 6337490189158917209L;

	public NormalTridasMeasuringMethodPanel(String id, IModel model) {
		super(id, model, false);
	}

	public NormalTridasMeasuringMethodPanel(String id, IModel model, final boolean editable) {
		super(id, model, editable);
		//super(id, model, false); // disable editing!
	}

	public class NormalTridasMeasuringMethodPanelView extends Panel {
		private static final long	serialVersionUID	= -1480349438716470705L;

		public NormalTridasMeasuringMethodPanelView(String id, IModel model) {
			super(id, model);

			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();

			// assume NormalTridasMeasuringMethod
			NormalTridasMeasuringMethod val = (NormalTridasMeasuringMethod)attr.getEntryObject();
			
			String str = "";

			if(val != null) {
				str = val.value();  //.toString();
			}

			Label label = new Label("text", new Model(str));
			add(label);			
		}
	}
	
	public class NormalTridasMeasuringMethodPanelEdit extends Panel {
		private static final long	serialVersionUID	= -6788778477954138341L;

		public NormalTridasMeasuringMethodPanelEdit(String id, IModel model) {
			super(id, model);

			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();

			// assume NormalTridasMeasuringMethod
			NormalTridasMeasuringMethod val = (NormalTridasMeasuringMethod)attr.getEntryObject();
			
			if (val != null)
			{				
				DropDownChoice choice = new DropDownChoice("type", 
						new PropertyModel(attr.getObject(), attr.getEntry().getMethod()), 
						Arrays.asList(NormalTridasMeasuringMethod.values()),
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
