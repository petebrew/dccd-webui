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

import nl.knaw.dans.dccd.model.EntityAttribute;
import nl.knaw.dans.dccd.model.UIMapEntry;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.tridas.schema.TridasIdentifier;

// Changed in TRiDaS v 1.2.2
//import org.tridas.schema.TridasLinkSeries;
//import org.tridas.schema.TridasLinkSeries.IdRef;
//import org.tridas.schema.TridasLinkSeries.XLink;
import org.tridas.schema.SeriesLink;
import org.tridas.schema.SeriesLink.IdRef;
import org.tridas.schema.SeriesLink.XLink;

/** A panel for displaying links in a TridasLinkSeries
 * It does not work as an entity attribute panel,
 * because the JAXB member for getting the list retuns a list of Objects
 *
 * @author paulboon
 */
public class LinkPanel extends EntityAttributePanel {
	private static final long	serialVersionUID	= -4137421857504903104L;
	private static Logger logger = Logger.getLogger(LinkPanel.class);

	// Note: do I need those two constructors???
	public LinkPanel(String id, IModel model) {
		super(id, model, false);
	}

	public LinkPanel(String id, IModel model, final boolean editable) {
		//super(id, model, editable);
		super(id, model, false); // only allow view
	}
	
	public class LinkPanelView extends Panel {
		private static final long serialVersionUID = 7702704257159154182L;
	
		public LinkPanelView(String id, IModel model) {
			super(id, model);
	
// TODO should get EntityAttribute...
			Serializable obj = (Serializable)this.getDefaultModelObject();
	
			// it's an IDref, XLink or Identifier
			 String idRefStr = "";
			 String xLinkStr = "";
			 EntityAttribute idAttr = new EntityAttribute(null, new UIMapEntry("id", "identifier"));
			 // place  series, either one of three possible types
			//if (obj.getClass() == TridasLinkSeries.IdRef.class) {
			if (obj.getClass() == SeriesLink.IdRef.class) {
				IdRef idRef =  ((IdRef)obj);
				if (idRef.isSetRef())
					idRefStr = idRef.getRef().toString();
			//} else if (obj.getClass() == TridasLinkSeries.XLink.class) {
			} else if (obj.getClass() == SeriesLink.XLink.class) {
				XLink xLink = ((XLink)obj);
				if (xLink.isSetHref())
					xLinkStr = xLink.getHref().toString();
			} else if (obj.getClass() == TridasIdentifier.class) {
				//idAttr = new EntityAttribute((TridasIdentifier)obj, new UIMapEntry("id", "identifier"));
				// Note:
				// if we call getObject() on the model, we get the TridasIdentifier = obj
				idAttr = new EntityAttribute(model, new UIMapEntry("id", "object"));
			} else {
				// warning, should be one of the above
				logger.warn("No type");
			}
			add(new Label("series_idRef", idRefStr));
			add(new Label("series_id_xLink", xLinkStr));
			add(new IdentifierPanel("series_identifier", new Model(idAttr)));
		}
	}
}
