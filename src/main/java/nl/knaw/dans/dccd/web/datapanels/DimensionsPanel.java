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

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.validator.NumberValidator;
import org.tridas.schema.TridasDimensions;
import java.math.BigDecimal;

/**
 * @author paulboon
 */
public class DimensionsPanel extends EntityAttributePanel {
	private static final long serialVersionUID = -8787603936970334444L;

	public DimensionsPanel(String id, IModel model) {
		super(id, model, false);
	}

	public DimensionsPanel(String id, IModel model, boolean editable) {
		super(id, model, editable);
		//super(id, model, false); // disable editing
	}

	public class DimensionsPanelView extends Panel {
		private static final long serialVersionUID = 479232927125835442L;

		public DimensionsPanelView(String id, IModel model) {
			super(id, model);

			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();

			// assume TridasDimensions
			TridasDimensions dim = (TridasDimensions)attr.getEntryObject();

			// TridasUnit unit;
			EntityAttribute vocAttr = new EntityAttribute(dim, "unit");
			ControlledVocabularyPanel cvocPanel = new ControlledVocabularyPanel("unit", new Model(vocAttr), isEditable());
			add(cvocPanel);
			if (dim == null) cvocPanel.setVisible(false); // hide all

			// Unit also has NormalTridas, controlled vocabulary but fixed in TRiDaS
			if (dim != null && dim.isSetUnit() && dim.getUnit().isSetNormalTridas()) 
			{
				EntityAttribute unitAttr = new EntityAttribute(dim.getUnit(), "normalTridas");
				TridasEnumPanel normalPanel = new TridasEnumPanel("normalunit", new Model(unitAttr), isEditable());
				add(normalPanel);	
			}
			else
			{
				// empty panel
				add(new TextField("normalunit").setVisible(false));				
			}
			
			// BigDecimal height;
			EntityAttribute valAttr = new EntityAttribute(dim, "height");
			add(new DecimalPanel("height", new Model(valAttr), isEditable()));

			// one of the following
			// BigDecimal diameter;
			valAttr = new EntityAttribute(dim, "diameter");
			add(new DecimalPanel("diameter", new Model(valAttr), isEditable()));
			// BigDecimal width;
			valAttr = new EntityAttribute(dim, "width");
			add(new DecimalPanel("width", new Model(valAttr), isEditable()));
			// BigDecimal depth;
			valAttr = new EntityAttribute(dim, "depth");
			add(new DecimalPanel("depth", new Model(valAttr), isEditable()));
		}
	}

	// Note identical to view
	public class DimensionsPanelEdit extends Panel {
		private static final long	serialVersionUID	= -975324569419250689L;

		public DimensionsPanelEdit(String id, IModel model) {
			super(id, model);

			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();

			// assume TridasDimensions
			TridasDimensions dim = (TridasDimensions)attr.getEntryObject();

			// TridasUnit unit;
			EntityAttribute vocAttr = new EntityAttribute(dim, "unit");
			ControlledVocabularyPanel cvocPanel = new ControlledVocabularyPanel("unit", new Model(vocAttr), isEditable());
			add(cvocPanel);
			//if (dim == null) cvocPanel.setVisible(false); // hide all
			
		
			// Unit also has NormalTridas, controlled vocabulary but fixed in TRiDaS
			if (dim != null && dim.isSetUnit() )//&& dim.getUnit().isSetNormalTridas()) 
			{
				EntityAttribute unitAttr = new EntityAttribute(dim.getUnit(), "normalTridas");
				//TridasEnumPanel normalPanel = new TridasEnumPanel("normalunit", new Model(unitAttr), isEditable());
				EntityAttributeOptionalPanel normalPanel = new EntityAttributeOptionalPanel(NormalTridasUnitPanel.class, "normalunit", new Model(unitAttr), isEditable());

				add(normalPanel);
			}
			else
			{
				add(new TextField("normalunit").setVisible(false));				
			}
		
			// BigDecimal height;
			EntityAttribute valAttr = new EntityAttribute(dim, "height");
			DecimalPanel heightPanel = new DecimalPanel("height", new Model(valAttr), isEditable());
			heightPanel.setValidator(NumberValidator.POSITIVE);
			add(heightPanel);

			// one of the following
			// BigDecimal diameter;
			valAttr = new EntityAttribute(dim, "diameter");
			add(new DecimalPanel("diameter", new Model(valAttr), isEditable()));
			// BigDecimal width;
			valAttr = new EntityAttribute(dim, "width");
			add(new DecimalPanel("width", new Model(valAttr), isEditable()));
			// BigDecimal depth;
			valAttr = new EntityAttribute(dim, "depth");
			add(new DecimalPanel("depth", new Model(valAttr), isEditable()));
		}
	}

}

