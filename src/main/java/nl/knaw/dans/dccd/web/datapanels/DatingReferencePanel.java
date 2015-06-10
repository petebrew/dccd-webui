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
import org.tridas.schema.TridasDatingReference;

/**
 * @author dev
 */
public class DatingReferencePanel extends EntityAttributePanel {
	private static final long	serialVersionUID	= 6950840015655796989L;
	private static Logger logger = Logger.getLogger(DatingReferencePanel.class);

	public DatingReferencePanel(String id, IModel model) {
		super(id, model, false);
	}

	public DatingReferencePanel(String id, IModel model, boolean editable) {
		super(id, model, editable);
		//super(id, model, false); // disable editing
	}

	public class DatingReferencePanelView extends Panel {
		private static final long	serialVersionUID	= 6559838145652317610L;

		public DatingReferencePanelView(String id, IModel model) {
			super(id, model);
			
			// get the Tridas object
			TridasDatingReference datingReference = (TridasDatingReference)((EntityAttribute) model.getObject()).getEntryObject();
			
			if (datingReference != null)
			{
	
				EntityAttribute attr = new EntityAttribute(datingReference, "linkSeries");
				SeriesLinkPanel panel = new SeriesLinkPanel("linkSeries", new Model(attr), isEditable());
		        add(panel);
			}
			else
			{
				// empty panel
				add(new Panel("linkSeries").setVisible(false));					
			}

		}
	}

	// Note: identical to View
	public class DatingReferencePanelEdit extends Panel {
		private static final long	serialVersionUID	= -60683309778378030L;

		public DatingReferencePanelEdit(String id, IModel model) {
			super(id, model);
			
			// get the Tridas object
			TridasDatingReference datingReference = (TridasDatingReference)((EntityAttribute) model.getObject()).getEntryObject();
			
			if (datingReference != null)
			{
	
				EntityAttribute attr = new EntityAttribute(datingReference, "linkSeries");
				SeriesLinkPanel panel = new SeriesLinkPanel("linkSeries", new Model(attr), isEditable());
				//EntityAttributeOptionalPanel panel = new EntityAttributeOptionalPanel(SeriesLinkPanel.class, "linkSeries", new Model(attr), isEditable());
		        add(panel);
			}
			else
			{
				// empty panel
				add(new Panel("linkSeries").setVisible(false));					
			}
		}
	}

}
