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

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import nl.knaw.dans.dccd.model.EntityAttribute;

/**
 * @author paulboon
 */
public class DoublePanel extends EntityAttributePanel {
	private static final long	serialVersionUID	= -8473691517833114168L;

	public DoublePanel(String id, IModel model) {
		super(id, model, false);
	}

	public DoublePanel(String id, IModel model, final boolean editable) {
		super(id, model, editable);
		//super(id, model, false); // disable editing!
	}

	public class DoublePanelView extends Panel {
		private static final long serialVersionUID = -8964765631140215546L;
	
		public DoublePanelView(String id, IModel model) {
			super(id, model);
	
			// get the Double object
			EntityAttribute attr = (EntityAttribute) model.getObject();
			Double d = (Double) attr.getEntryObject();
	
			String dStr = "";
	
			if (d != null) {
					dStr = d.toString();
			}
			// a single label
	        add(new Label("d", new Model(dStr)));
		}
	}

	public class DoublePanelEdit extends Panel {
		private static final long	serialVersionUID	= 8948415696032324693L;

		public DoublePanelEdit(String id, IModel model) {
			super(id, model);
	
			// get the Double object
			EntityAttribute attr = (EntityAttribute) model.getObject();
			Double val = (Double) attr.getEntryObject();

			//if (val != null)
			//{
				TextField doubleField = new TextField("d", 
						new PropertyModel(attr.getObject(), attr.getEntry().getMethod()),//new Model(val), 
						Double.class);
				doubleField.setConvertEmptyInputStringToNull(false);
				add(doubleField);
			//}
			//else
			//{
			//	// empty panel
			//	add(new TextField("d").setVisible(false));				
			//}
			
		}
	}
}
