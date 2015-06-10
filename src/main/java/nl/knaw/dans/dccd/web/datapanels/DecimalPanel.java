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

import java.math.BigDecimal;

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
public class DecimalPanel extends EntityAttributePanel {
	private static final long serialVersionUID = -3569413010956890682L;
	private static Logger logger = Logger.getLogger(DecimalPanel.class);

	public DecimalPanel(String id, IModel model) {
		super(id, model, false);
	}

	public DecimalPanel(String id, IModel model, final boolean editable) {
		super(id, model, editable);
		//super(id, model, false); // disable editing!
	}

	public class DecimalPanelView extends Panel {
		private static final long serialVersionUID = -3273586499217434291L;

		public DecimalPanelView(String id, IModel model) {
			super(id, model);

			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();

			// assume BigDecimal
			BigDecimal val = (BigDecimal)attr.getEntryObject();
			String str = "";

			if(val != null) {
				str = val.toString();
			}

			Label label = new Label("text", new Model(str));
			add(label);
		}
	}

	public class DecimalPanelEdit extends Panel {
		private static final long	serialVersionUID	= -890911641927898498L;

		public DecimalPanelEdit(String id, IModel model) {
			super(id, model);

			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();

			// assume BigDecimal
			//BigDecimal val = (BigDecimal)attr.getEntryObject();
			//
			//if (val != null)
			//{
				TextField decimalField = new TextField("text", 
						new PropertyModel(attr.getObject(), attr.getEntry().getMethod()), //new Model(val), 
						BigDecimal.class);
				
				if(getValidator() != null)
				{
					logger.debug("Using validator: " + getValidator().toString());
					decimalField.add(getValidator());
				}
				
				//decimalField.setConvertEmptyInputStringToNull(false);
				add(decimalField);
			//}
			//else
			//{
			//	// empty panel
			//	add(new TextField("text").setVisible(false));				
			//}
		}
	}
}

