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

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
//import org.tridas.schema.TridasMeasurementSeriesPlaceholder;
//import org.tridas.schema.TridasRadiusPlaceholder;

/** Note: only when the Sample has no Radius, hen it should have this placeholder
 * 
 * @author dev
 */
public class RadiusPlaceholderPanel extends EntityAttributePanel
{
	private static final long	serialVersionUID	= -3805146337505606950L;

	public RadiusPlaceholderPanel(String id, IModel model) {
		super(id, model, false);
	}

	public RadiusPlaceholderPanel(String id, IModel model, final boolean editable) {
		super(id, model, editable);
		//super(id, model, false); // disable edit
	}

	public class RadiusPlaceholderPanelView extends Panel {
		private static final long	serialVersionUID	= -1945332771046627579L;

		public RadiusPlaceholderPanelView(String id, IModel model) {
			super(id, model);

//			// get the Tridas object
//			TridasRadiusPlaceholder radiusPlaceholder = (TridasRadiusPlaceholder)((EntityAttribute) model.getObject()).getEntryObject();
//
//			if(radiusPlaceholder != null && radiusPlaceholder.isSetMeasurementSeriesPlaceholder())
//			{
//				// TODO add  panel for measurement placeholder
//				TridasMeasurementSeriesPlaceholder measurementSeriesPlaceholder = radiusPlaceholder.getMeasurementSeriesPlaceholder();
//				// MeasurementSeries Placeholder id
//				EntityAttribute attr = new EntityAttribute(measurementSeriesPlaceholder, "id");
//				TextPanel textPanel = new TextPanel("id", new Model(attr), isEditable());
//		        add(textPanel);
//			}
//			else
//			{
				// empty panel
				add(new Panel("id").setVisible(false));			
//			}
		}
	}

	// Note identical to view
	public class RadiusPlaceholderPanelEdit extends Panel {
		private static final long	serialVersionUID	= 2460598971184601253L;

		public RadiusPlaceholderPanelEdit(String id, IModel model) {
			super(id, model);

			// get the Tridas object
//			TridasRadiusPlaceholder radiusPlaceholder = (TridasRadiusPlaceholder)((EntityAttribute) model.getObject()).getEntryObject();
//
//			if(radiusPlaceholder != null && radiusPlaceholder.isSetMeasurementSeriesPlaceholder())
//			{
//				// TODO add  panel for measurement placeholder
//				TridasMeasurementSeriesPlaceholder measurementSeriesPlaceholder = radiusPlaceholder.getMeasurementSeriesPlaceholder();
//				// MeasurementSeries Placeholder id, required!
//				EntityAttribute attr = new EntityAttribute(measurementSeriesPlaceholder, "id");
//				TextPanel textPanel = new TextPanel("id", new Model(attr), isEditable());
//		        add(textPanel);
//			}
//			else
//			{
				// empty panel
				add(new Panel("id").setVisible(false));			
//			}
		}
	}
}
