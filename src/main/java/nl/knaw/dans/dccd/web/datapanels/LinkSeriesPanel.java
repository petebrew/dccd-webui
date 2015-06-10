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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import nl.knaw.dans.dccd.model.EntityAttribute;
import nl.knaw.dans.dccd.model.UIMapEntry;

import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.tridas.schema.SeriesLink;
import org.tridas.schema.SeriesLinks;

/**
 * TODO Change this to handle SeriesLinks, that have a list of SeriesLink's
 *
 * @author paulboon
 */
public class LinkSeriesPanel extends EntityAttributePanel {
	private static final long serialVersionUID = 1672393763055417182L;
	private static Logger logger = Logger.getLogger(LinkSeriesPanel.class);

	public LinkSeriesPanel(String id, IModel model) {
		super(id, model, false);
	}

	public LinkSeriesPanel(String id, IModel model, boolean editable) {
		super(id, model, editable); 
		//super(id, model, false); // disable editing
	}

	public class LinkSeriesPanelView extends Panel {
		private static final long serialVersionUID = 479232927125835442L;

		public LinkSeriesPanelView(String id, IModel model) {
			super(id, model);

			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();
			
			// get the Tridas object
			SeriesLinks linkSeries = (SeriesLinks) attr.getEntryObject();

			List<?> list = new ArrayList<SeriesLink>();

			if (linkSeries != null && linkSeries.isSetSeries()) {
				list = linkSeries.getSeries();
			}
			logger.debug("Number of series links: " + list.size());

			
			// fill the list
			ListView view = new ListView("linkseries", list) {
				private static final long serialVersionUID = -5118760071162867209L;
			
				@Override
			    protected void populateItem(ListItem item) {
					EntityAttribute attr = new EntityAttribute(item, new UIMapEntry("linkSeries", "modelObject"));
					item.add(new SeriesLinkPanel("linkseries_panel", new Model(attr), isEditable()));
				}
			};
			add(view);
		}
	}

	public class LinkSeriesPanelEdit extends Panel {
		private static final long	serialVersionUID	= 6398899324697341656L;

		public LinkSeriesPanelEdit(String id, IModel model) {
			super(id, model);

			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();
			
			// get the Tridas object
			SeriesLinks linkSeries = (SeriesLinks) attr.getEntryObject();

			EntityAttribute seriesAttr = new EntityAttribute(linkSeries, "series");
			EntityAttributeRepeaterPanel repeaterPanel = 
				new EntityAttributeRepeaterPanel(SeriesLinkPanel.class, "linkseries", new Model(seriesAttr), isEditable());
			add(repeaterPanel);
		}
	}
}
