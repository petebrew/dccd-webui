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
import org.tridas.schema.TridasSlope;

/**
 * @author paulboon
 */
public class SlopePanel extends EntityAttributePanel {
	private static final long	serialVersionUID	= -2812323770563371936L;

	public SlopePanel(String id, IModel model) {
		super(id, model, false);
	}

	public SlopePanel(String id, IModel model, boolean editable) {
		super(id, model, editable);
		//super(id, model, false); // disable editing
	}
	
	public class SlopePanelView extends Panel {
		private static final long serialVersionUID = -4575822324415982686L;
	
		public SlopePanelView(String id, IModel model) {
			super(id, model);
	
			// get the TridasSlope object
			TridasSlope slope = (TridasSlope)((EntityAttribute) model.getObject()).getEntryObject();
	
			if (slope != null && slope.isSetAngle())
			{
				EntityAttribute attr = new EntityAttribute(slope, new UIMapEntry("angle", "angle"));
				TextPanel angelPanel = new TextPanel("angle", new Model(attr), isEditable());
				add(angelPanel);
			}
			else
			{
				// empty panel
				add(new Panel("angle").setVisible(false));	
			}

			if (slope != null && slope.isSetAzimuth())
			{	
				EntityAttribute attr = new EntityAttribute(slope, new UIMapEntry("azimuth", "azimuth"));
				TextPanel azimuthPanel = new TextPanel("azimuth", new Model(attr), isEditable());
		        add(azimuthPanel);
			}
			else
			{
				// empty panel
				add(new Panel("azimuth").setVisible(false));	
			}
		}
	}

	// Note: identical to view
	public class SlopePanelEdit extends Panel {
		private static final long	serialVersionUID	= 4939555255030915969L;

		public SlopePanelEdit(String id, IModel model) {
			super(id, model);
	
			// get the TridasSlope object
			TridasSlope slope = (TridasSlope)((EntityAttribute) model.getObject()).getEntryObject();
	
			if (slope != null)// && slope.isSetAngle())
			{
				EntityAttribute attr = new EntityAttribute(slope, new UIMapEntry("angle", "angle"));
				IntegerPanel angelPanel = new IntegerPanel("angle", new Model(attr), isEditable());
				add(angelPanel);
			}
			else
			{
				// empty panel
				add(new Panel("angle").setVisible(false));	
			}

			if (slope != null)// && slope.isSetAzimuth())
			{	
				EntityAttribute attr = new EntityAttribute(slope, new UIMapEntry("azimuth", "azimuth"));
				IntegerPanel azimuthPanel = new IntegerPanel("azimuth", new Model(attr), isEditable());
		        add(azimuthPanel);
			}
			else
			{
				// empty panel
				add(new Panel("azimuth").setVisible(false));	
			}
		}
	}
	
}
