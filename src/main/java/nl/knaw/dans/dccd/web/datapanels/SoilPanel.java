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
import nl.knaw.dans.dccd.model.UIMapEntry;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.tridas.schema.TridasSoil;

/**
 * @author paulboon
 */
public class SoilPanel extends EntityAttributePanel {
	private static final long	serialVersionUID	= 823444921665238755L;

	public SoilPanel(String id, IModel model) {
		super(id, model, false);
	}

	public SoilPanel(String id, IModel model, boolean editable) {
		super(id, model, editable);
		//super(id, model, false); // disable editing
	}
	
	public class SoilPanelView extends Panel {
		private static final long serialVersionUID = 7985563139924101989L;
	
		public SoilPanelView(String id, IModel model) {
			super(id, model);
	
			// get the TridasSoil object
			TridasSoil soil = (TridasSoil)((EntityAttribute) model.getObject()).getEntryObject();
	
			if (soil != null && soil.isSetDepth())
			{	
				EntityAttribute attr = new EntityAttribute(soil, new UIMapEntry("depth", "depth"));
				//TextPanel depthPanel = new TextPanel("depth", new Model(attr), isEditable());
				DoublePanel depthPanel = new DoublePanel("depth", new Model(attr), isEditable());
		        add(depthPanel);
			}
			else
			{
				// empty panel
				add(new Panel("depth").setVisible(false));	
			}

			if (soil != null && soil.isSetDescription())
			{
				EntityAttribute attr = new EntityAttribute(soil, new UIMapEntry("description", "description"));
				TextPanel descriptionPanel = new TextPanel("description", new Model(attr), isEditable());
				add(descriptionPanel);
			}
			else
			{
				// empty panel
				add(new Panel("description").setVisible(false));	
			}
		}
	}

	// Note: identical to view
	public class SoilPanelEdit extends Panel {
		private static final long	serialVersionUID	= -3704891177810111249L;

		public SoilPanelEdit(String id, IModel model) {
			super(id, model);
	
			// get the TridasSoil object
			TridasSoil soil = (TridasSoil)((EntityAttribute) model.getObject()).getEntryObject();
	
			if (soil != null)// && soil.isSetDepth())
			{	
				EntityAttribute attr = new EntityAttribute(soil, new UIMapEntry("depth", "depth"));
				//TextPanel depthPanel = new TextPanel("depth", new Model(attr), isEditable());
				DoublePanel depthPanel = new DoublePanel("depth", new Model(attr), isEditable());
		        add(depthPanel);
			}
			else
			{
				// empty panel
				add(new Panel("depth").setVisible(false));	
			}

			if (soil != null)// && soil.isSetDescription())
			{
				EntityAttribute attr = new EntityAttribute(soil, new UIMapEntry("description", "description"));
				TextPanel descriptionPanel = new TextPanel("description", new Model(attr), isEditable());
				add(descriptionPanel);
			}
			else
			{
				// empty panel
				add(new Panel("description").setVisible(false));	
			}
		}
	}
	
}
