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

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.tridas.schema.SeriesLinksWithPreferred;

/**
 * @author dev
 */
public class SeriesLinksWithPreferredPanel extends EntityAttributePanel {
	private static final long	serialVersionUID	= 8381548551022252584L;
	private static Logger logger = Logger.getLogger(SeriesLinksWithPreferredPanel.class);

	public SeriesLinksWithPreferredPanel(String id, IModel model) {
		super(id, model, false);
	}

	public SeriesLinksWithPreferredPanel(String id, IModel model, boolean editable) {
		super(id, model, editable);
		//super(id, model, false); // disable editing
	}

	public class SeriesLinksWithPreferredPanelView extends Panel {
		private static final long	serialVersionUID	= -5076595246186042056L;

		public SeriesLinksWithPreferredPanelView(String id, IModel model) {
			super(id, model);
			
			// get the Tridas object
			SeriesLinksWithPreferred seriesLinksWithPreferred = (SeriesLinksWithPreferred)((EntityAttribute) model.getObject()).getEntryObject();
			
			if (seriesLinksWithPreferred != null && seriesLinksWithPreferred.isSetPreferredSeries())
			{	
				EntityAttribute attr = new EntityAttribute(seriesLinksWithPreferred, "preferredSeries");
				SeriesLinkPanel panel = new SeriesLinkPanel("prefered", new Model(attr), isEditable());
		        add(panel);
			}
			else
			{
				// empty panel
				add(new Panel("prefered").setVisible(false));					
			}
		}
	}

	// Note: identical to View
	public class SeriesLinksWithPreferredPanelEdit extends Panel {
		private static final long	serialVersionUID	= 3655517205534786650L;

		public SeriesLinksWithPreferredPanelEdit(String id, IModel model) {
			super(id, model);
			
			// get the Tridas object
			SeriesLinksWithPreferred seriesLinksWithPreferred = (SeriesLinksWithPreferred)((EntityAttribute) model.getObject()).getEntryObject();
			
			if (seriesLinksWithPreferred != null)// && seriesLinksWithPreferred.isSetPreferredSeries())
			{
	
				EntityAttribute attr = new EntityAttribute(seriesLinksWithPreferred, "preferredSeries");
				//SeriesLinkPanel panel = new SeriesLinkPanel("prefered", new Model(attr), isEditable());
				EntityAttributeOptionalPanel panel = new EntityAttributeOptionalPanel(SeriesLinkPanel.class, "prefered", new Model(attr), isEditable());
		        add(panel);
			}
			else
			{
				// empty panel
				add(new Panel("prefered").setVisible(false));					
			}
		}
	}

}
