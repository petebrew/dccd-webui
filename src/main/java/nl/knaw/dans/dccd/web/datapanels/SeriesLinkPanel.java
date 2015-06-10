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

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.tridas.schema.SeriesLink;
import org.tridas.schema.SeriesLink.IdRef;
import org.tridas.schema.SeriesLink.XLink;

/**
 * @author dev
 */
public class SeriesLinkPanel extends EntityAttributePanel {
	private static final long	serialVersionUID	= -3402029604310070590L;
	private static Logger logger = Logger.getLogger(SeriesLinkPanel.class);

	public SeriesLinkPanel(String id, IModel model) {
		super(id, model, false);
	}

	public SeriesLinkPanel(String id, IModel model, boolean editable) {
		super(id, model, editable); 
		//super(id, model, false); // disable editing
	}

	public class SeriesLinkPanelView extends Panel {
		private static final long	serialVersionUID	= 609983421461469860L;

		public SeriesLinkPanelView(String id, IModel model) {
			super(id, model);
			
			// get the Tridas object
			SeriesLink seriesLink = (SeriesLink)((EntityAttribute) model.getObject()).getEntryObject();
		
			// Note: looks a lot like LinkPanel !
			
			// it's an IDref, XLink or Identifier
			EntityAttribute idAttr = new EntityAttribute(null, new UIMapEntry("id", "identifier"));
			String idRefStr = "";
			String xLinkStr = "";
			if (seriesLink.isSetIdRef()) 
			{
				IdRef idRef =  seriesLink.getIdRef();
				if (idRef.isSetRef())
				{
					idRefStr = "[" + idRef.getRef().toString() + "]";
				}
				else
				{
					idRefStr = "<empty>"; //???
				}
			} 
			else if (seriesLink.isSetXLink()) 
			{
				XLink xLink = seriesLink.getXLink();
				if (xLink.isSetHref())
				{
					xLinkStr = xLink.getHref();
				}
				else
				{
					xLinkStr = "<empty>"; //???
				}
			} 
			else if (seriesLink.isSetIdentifier()) 
			{
				idAttr = new EntityAttribute(seriesLink, "identifier");
			} 
			else 
			{
				// warning, should be one of the above
				logger.warn("No type");
			}
			add(new Label("series_idRef", idRefStr));
			add(new Label("series_id_xLink", xLinkStr));
			add(new IdentifierPanel("series_identifier", new Model(idAttr)));			
		}
	}

	public class SeriesLinkPanelEdit extends Panel {
		private static final long	serialVersionUID	= 7077130805966673737L;

		public SeriesLinkPanelEdit(String id, IModel model) {
			super(id, model);
			
			// get the Tridas object
			SeriesLink seriesLink = (SeriesLink)((EntityAttribute) model.getObject()).getEntryObject();
		
			// Note: look a lot like LinkPanel !
			// it's an IDref, XLink or Identifier

			// Optional !
			EntityAttribute idAttr = new EntityAttribute(seriesLink, "identifier");
			EntityAttributeOptionalPanel idPanel = new EntityAttributeOptionalPanel(IdentifierPanel.class, "series_identifier", new Model(idAttr), isEditable());
			add(idPanel);
			
			// Optional !
			EntityAttribute xLinkAttr = new EntityAttribute(seriesLink, "xLink");
			EntityAttributeOptionalPanel xLinkPanel = new EntityAttributeOptionalPanel(XLinkPanel.class, "series_id_xLink", new Model(xLinkAttr), isEditable());
			add(xLinkPanel);

			/* Notes on idRef
			 * Editing has been disabled for this attribute!
			 * The xs:IDREF must match an xs:ID on elements in the same document 
			 * otherwise even saving as draft will fail. 
			 * The xs:ID type attribute is optional for the TRiDaS entities and series and even the placeholders. 
			 * It is not the same as the Identifier element!
			 * We could genratie a list with all id's in the project, but the user can't see which part of the tridas it is, 
			 * because we don't display that ID!
			 * When editing is enabled we need to check all this and possibly have a treeview for selection?
			 * Much to difficult for a 'mostly' computer generated id (stated by the TRiDaS schema documentation).
			 * 
			 * 
			 */
			// ref is an Object not a String
			if (seriesLink != null && seriesLink.isSetIdRef() && seriesLink.getIdRef().isSetRef()) 
			{
				EntityAttribute attr = new EntityAttribute(seriesLink.getIdRef(), "ref");
				TextPanel textPanel = new TextPanel("series_idRef", new Model(attr), false); // NON-editable!
		        add(textPanel);				
			}
			else
			{
				// empty panel
				add(new Panel("series_idRef").setVisible(false));					
			}			
		}
	}

}

