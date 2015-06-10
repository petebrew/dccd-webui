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

import java.math.BigInteger;

import nl.knaw.dans.dccd.model.EntityAttribute;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

/**
 * @author dev
 */
public class BigIntegerPanel extends EntityAttributePanel {
	private static final long	serialVersionUID	= -1441411844802511583L;

	public BigIntegerPanel(String id, IModel model) {
		super(id, model, false);
	}

	public BigIntegerPanel(String id, IModel model, final boolean editable) {
		super(id, model, editable);
		//super(id, model, false); // disable editing!
	}

	public class BigIntegerPanelView extends Panel {
		private static final long	serialVersionUID	= -7817916977134179240L;

		public BigIntegerPanelView(String id, IModel model) {
			super(id, model);

			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();

			// assume BigInteger
			BigInteger val = (BigInteger)attr.getEntryObject();
			String str = "";

			if(val != null) {
				str = val.toString();
			}

			Label label = new Label("text", new Model(str));
			add(label);
		}
	}

	public class BigIntegerPanelEdit extends Panel {
		private static final long	serialVersionUID	= -2578194484957915214L;

		public BigIntegerPanelEdit(String id, IModel model) {
			super(id, model);

			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();

			// assume BigInteger
			BigInteger val = (BigInteger)attr.getEntryObject();
			
			//if (val != null)
			//{
				TextField decimalField = new TextField("text", 
						new PropertyModel(attr.getObject(), attr.getEntry().getMethod()),
						BigInteger.class);
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
