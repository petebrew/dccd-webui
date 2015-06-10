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
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.tridas.schema.NormalTridasShape;
import org.tridas.schema.TridasShape;

/**
 * @author paulboon
 */
public class ShapePanel extends EntityAttributePanel {
	private static final long	serialVersionUID	= -4337293586081291695L;

	public ShapePanel(String id, IModel model) {
		super(id, model, false);
	}

	public ShapePanel(String id, IModel model, boolean editable) {
		super(id, model, editable);
		//super(id, model, false); // disable editing
	}

	public class ShapePanelView extends Panel {
		private static final long serialVersionUID = 1780135536887334468L;
	
		public ShapePanelView(String id, IModel model) {
			super(id, model);
	
			// assume object is a TridasShape
			TridasShape shape = (TridasShape)((EntityAttribute) model.getObject()).getEntryObject();
		
			// Add the controlled voc..panel?
			add(new ControlledVocabularyPanel("cvoc_panel", model).setVisible(shape!=null));
	
			/*
			String normalShapeStr = "";
			// also has NormalTridasCategory, controlled vocabulary of research categories ?
			if (shape != null && shape.isSetNormalTridas()) {
				NormalTridasShape normalShape = shape.getNormalTridas();
				normalShapeStr = normalShape.value();
			}
			add(new Label("normalshape", normalShapeStr));
			*/
			if (shape != null && shape.isSetNormalTridas()) {
				EntityAttribute attr = new EntityAttribute(shape, "normalTridas");
				NormalTridasShapePanel normalPanel = new NormalTridasShapePanel("normalshape", new Model(attr), isEditable());
				add(normalPanel);			
			}
			else
			{
				// empty panel
				add(new TextField("normalshape").setVisible(false));				
			}
			
		}
	}

	// Note identical to view
	public class ShapePanelEdit extends Panel {
		private static final long	serialVersionUID	= -8213376287755057222L;

		public ShapePanelEdit(String id, IModel model) {
			super(id, model);
	
			// assume object is a TridasShape
			TridasShape shape = (TridasShape)((EntityAttribute) model.getObject()).getEntryObject();
		
			// Add the controlled voc..panel?
			add(new ControlledVocabularyPanel("cvoc_panel", model, true));
	
			// also has NormalTridasCategory, controlled vocabulary of research categories ?
			if (shape != null)// && shape.isSetNormalTridas()) 
			{
				EntityAttribute attr = new EntityAttribute(shape, "normalTridas");
				//NormalTridasShapePanel normalPanel = new NormalTridasShapePanel("normalshape", new Model(attr), isEditable());
				EntityAttributeOptionalPanel normalPanel = new EntityAttributeOptionalPanel(NormalTridasShapePanel.class, "normalshape", new Model(attr), isEditable());

				add(normalPanel);			
			}
			else
			{
				// empty panel
				add(new TextField("normalshape").setVisible(false));				
			}
		}
	}
	
}
