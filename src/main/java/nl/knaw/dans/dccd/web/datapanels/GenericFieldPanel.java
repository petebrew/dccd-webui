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
import java.util.List;

import nl.knaw.dans.dccd.model.EntityAttribute;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.tridas.schema.TridasGenericField;

/**
 * @author paulboon
 */
public class GenericFieldPanel extends EntityAttributePanel {
	private static final long serialVersionUID = 5917289581682625651L;
	//private static Logger logger = Logger.getLogger(GenericFieldPanel.class);

	public GenericFieldPanel(String id, IModel model) {
		super(id, model, false);
	}

	public GenericFieldPanel(String id, IModel model, boolean editable) {
		super(id, model, editable);
		//super(id, model, false); // disable editing
	}

	public class GenericFieldPanelView extends Panel {
		private static final long serialVersionUID = 100826682633280418L;

		public GenericFieldPanelView(String id, IModel model) {
			super(id, model);

			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();

			// get the TridasLinkSeries object
			TridasGenericField genericField = (TridasGenericField)attr.getEntryObject();

			// show value, name and type
			// Note: could make it text panels, then they can also be editable!
			add(new Label("generifield_value", genericField.getValue()));
			add(new Label("generifield_name", genericField.getName()));
			add(new Label("generifield_type", genericField.getType()));
		}
	}

	public class GenericFieldPanelEdit extends Panel {
		private static final long	serialVersionUID	= 6337081592291282416L;

		public GenericFieldPanelEdit(String id, IModel model) {
			super(id, model);

			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();

			// get the TridasLinkSeries object
			TridasGenericField genericField = (TridasGenericField)attr.getEntryObject();

			// show value, name and type
			// Note: could make it text panels, then they can also be editable!
			
			TextField valueField = new TextField("generifield_value", 
					new PropertyModel(genericField, "value"));
			valueField.setConvertEmptyInputStringToNull(false);
			add(valueField);
			
			// Required
			TextField nameField = new TextField("generifield_name", 
					new PropertyModel(genericField, "name"));
			//nameField.setConvertEmptyInputStringToNull(false);
			add(nameField);

			//TextField typeField = new TextField("generifield_type", 
			//		new PropertyModel(genericField, "type"));
			//typeField.setConvertEmptyInputStringToNull(false);
			//add(typeField);

			// selection of type
			// Note the type itself is optional, but when specified it must be one from the list. 
			// No use of an Optional attribute, so it's not possible to remove it once it is set!
			List types = Arrays.asList(new String[] {
					"xs:string",
					"xs:boolean",
					"xs:int",
					"xs:float",
					"xs:date",
					"xs:dateTime",
					"xs:duration"
			});
			
			DropDownChoice typeField = new DropDownChoice("generifield_type", new PropertyModel(genericField, "type"), types);
			add(typeField);

		}
	}
	
}

