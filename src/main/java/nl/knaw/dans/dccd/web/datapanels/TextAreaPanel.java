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
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

/**
 * assumes DccdAttr is passes in the Model
 *
 * @author paulboon
 */
public class TextAreaPanel extends EntityAttributePanel
{
	private static final long	serialVersionUID	= -2315476038232768296L;

	// Note: do I need those two constructors???
	public TextAreaPanel(String id, IModel model) {
		super(id, model, false);
	}

	public TextAreaPanel(String id, IModel model, final boolean editable) {
		super(id, model, editable);
	}

	public class TextAreaPanelView extends Panel {
		private static final long	serialVersionUID	= 7570033371046821589L;

		public TextAreaPanelView(String id, IModel model) {
			super(id, model);
			// now do the view stuff (non-editable)
			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();

			// Note: using a <pre> tag instead of a MultiLineLabel
			Label label = new Label("text", new PropertyModel(attr.getObject(),
					attr.getEntry().getMethod()));
			add(label);
		}
	}

	public class TextAreaPanelEdit extends Panel {
		private static final long	serialVersionUID	= 4982867626854570581L;

		public TextAreaPanelEdit(String id, IModel model) {
			super(id, model);
			// now do the edit stuff
			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();

			// In order to set the enum/value with the DropDownChoice
			// we need a PropertyModel with the object it belongs to
			// and the methodname (for get/set) 			
			TextArea field = new TextArea("field", 
					new PropertyModel(attr.getObject(), attr.getEntry().getMethod()));
			// Note: Needed to specify the String.class for the textfield,
			// otherwise null was always used for setting after submit!

			// Get an empty string instead of null when the textfield is empty,
			// seems better than the default null
			field.setConvertEmptyInputStringToNull(false);
			add(field);
		}
	}
}
