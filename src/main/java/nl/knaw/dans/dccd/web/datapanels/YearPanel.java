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
import org.tridas.schema.Certainty;
import org.tridas.schema.Year;
import org.tridas.schema.DatingSuffix;

/**
 * @author paulboon
 */
public class YearPanel extends EntityAttributePanel {

	private static final long serialVersionUID = -5561233179042664403L;

	public YearPanel(String id, IModel model) {
		super(id, model, false);
	}

	public YearPanel(String id, IModel model, boolean editable) {
		super(id, model, editable); 
		//super(id, model, false); // disable editing
	}

	public class YearPanelView extends Panel {
		private static final long serialVersionUID = -6449383223069091914L;

		public YearPanelView(String id, IModel model) {
			super(id, model);

			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();
			// assume (Tridas)Year
			Year year = (Year)attr.getEntryObject();

			String yearStr = "";
			String suffixStr = "";
			String certaintyStr = "";

			if (year != null ) {
				yearStr = year.isSetValue()?year.getValue().toString():"";
			    //DatingSuffix suffix; (AD BC etc.)
				suffixStr = year.isSetSuffix()?year.getSuffix().value():"";
				certaintyStr = year.isSetCertainty()?year.getCertainty().value():"";
			}

			add(new Label("year", yearStr));

			Label suffixLabel = new Label("suffix", suffixStr);
			add(suffixLabel);
			if(suffixStr.length() == 0) suffixLabel.setVisible(false);

			Label certaintyLabel = new Label("certainty", certaintyStr);
			add(certaintyLabel);
			if(certaintyStr.length() == 0) certaintyLabel.setVisible(false);
		}
	}

	public class YearPanelEdit extends Panel {
		private static final long	serialVersionUID	= 7152752385687174779L;

		public YearPanelEdit(String id, IModel model) {
			super(id, model);

			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();
			// assume (Tridas)Year
			Year year = (Year)attr.getEntryObject();

			if (year != null)
			{
				EntityAttribute valAttr = new EntityAttribute(year, "value");
				//add(new BigIntegerPanel("year", new Model(valAttr), isEditable()));
				// Note in the last TRiDaS the year value changed from BigInteger to Integer!
				// Note2: with a negative number the "relative" suffix is not being forced, but that would be nice
				add(new IntegerPanel("year", new Model(valAttr), isEditable()));
				
				//TODO  Maybe use the EnumChoiceRenderer?			
				DropDownChoice suffixChoice = new DropDownChoice("suffix", 
						new PropertyModel(year, "suffix"), 
						Arrays.asList(DatingSuffix.values()),
						new ChoiceRenderer("value", "value")
				);			
				add(suffixChoice);
	
				//TODO  Maybe use the EnumChoiceRenderer?			
				DropDownChoice certaintyChoice = new DropDownChoice("certainty", 
						new PropertyModel(year, "certainty"), 
						Arrays.asList(Certainty.values()),
						new ChoiceRenderer("value", "value")
				);			
				add(certaintyChoice);
			}
			else
			{
				// empty stuff
				add(new Panel("year").setVisible(false));
				add(new DropDownChoice("suffix").setVisible(false));
				add(new DropDownChoice("certainty").setVisible(false));
			}
		}
	}
}

