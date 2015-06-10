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

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

/**
 * @author paulboon
 */
public class IntegerPanel extends EntityAttributePanel {
	private static final long	serialVersionUID	= 4575572027148376582L;
	private static Logger logger = Logger.getLogger(IntegerPanel.class);

	public IntegerPanel(String id, IModel model) {
		super(id, model, false);
	}

	public IntegerPanel(String id, IModel model, final boolean editable) {
		super(id, model, editable);
		//super(id, model, false); // disable editing!
	}

	public class IntegerPanelView extends Panel {
		private static final long	serialVersionUID	= 6287257674024605253L;

		public IntegerPanelView(String id, IModel model) {
			super(id, model);

			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();

			// assume Integer
			Integer val = (Integer)attr.getEntryObject();
			String str = "";

			if(val != null) {
				str = val.toString();
			}

			Label label = new Label("text", new Model(str));
			add(label);
		}
	}

	public class IntegerPanelEdit extends Panel {
		private static final long	serialVersionUID	= 6988912653760041981L;

		public IntegerPanelEdit(String id, IModel model) {
			super(id, model);

			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();

			// assume Integer
			TextField integerField = new TextField("text", 
					new PropertyModel(attr.getObject(), attr.getEntry().getMethod()), //new Model(val), 
					Integer.class);
			
			if(getValidator() != null)
			{
				logger.debug("Using validator: " + getValidator().toString());
				integerField.add(getValidator());
			}
			
			//decimalField.setConvertEmptyInputStringToNull(false);
			add(integerField);
		}
	}
}

