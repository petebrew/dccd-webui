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
import org.tridas.schema.NormalTridasLocationType;
import org.tridas.schema.TridasLocation;
import org.tridas.schema.TridasLocationGeometry;

/**
 * @author paulboon
 */
public class LocationPanel extends EntityAttributePanel {
	private static final long	serialVersionUID	= -9071558739295171922L;

	public LocationPanel(String id, IModel model) {
		super(id, model, false);
	}

	public LocationPanel(String id, IModel model, boolean editable) {
		super(id, model, editable);
		//super(id, model, false); // disable editing
	}

	public class LocationPanelView extends Panel {
		private static final long serialVersionUID = 9111872364842550249L;
	
		public LocationPanelView(String id, IModel model) {
			super(id, model);
	
			// get the TridasLocation object
			TridasLocation location = (TridasLocation)((EntityAttribute) model.getObject()).getEntryObject();
			
			if (location != null && location.isSetLocationGeometry()) 
			{
			    TridasLocationGeometry locationGeometry = location.getLocationGeometry();
				add(new LocationGeometryPanel("locationGeometry", new Model(locationGeometry)));
			}
			else
			{
				// empty panel
				TextViewPanel panel = new TextViewPanel("locationGeometry", new Model(""));
				add(panel);
				panel.setVisible(false);
			}
	
		    String typeStr = "";
			String precisionStr = "";
			
			// check for null
			if (location != null) 
			{
				// type, precision and comment are optional
			    NormalTridasLocationType locationType = location.getLocationType();
			    // TRiDaS documentation tels me it is a controlled vocabulary,
			    // But all I need to know noa is how te get a string out of it
			    typeStr = locationType!=null?locationType.value().toString():"";
				precisionStr = location.getLocationPrecision();
				
				EntityAttribute locCommentAttr = new EntityAttribute(location, "locationComment");
				add(new TextAreaPanel("locationComment", new Model(locCommentAttr), isEditable()));				
			} 
			else 
			{
				add(new Panel("locationComment").setVisible(false));
			}
	
	        add(new TextViewPanel("locationType", new Model(typeStr)));
	        
	        add(new TextViewPanel("locationPrecision", new Model(precisionStr)));
	        	
			EntityAttribute addressAttr = new EntityAttribute(location, "address");
			add(new AddressPanel("address", new Model(addressAttr)));
		}
	}

	public class LocationPanelEdit extends Panel {
		private static final long	serialVersionUID	= 2874698087021612758L;

		public LocationPanelEdit(String id, IModel model) {
			super(id, model);
	
			// get the TridasLocation object
			TridasLocation location = (TridasLocation)((EntityAttribute) model.getObject()).getEntryObject();
			
			if (location != null)
			{
				EntityAttribute locGeomAttr = new EntityAttribute(location, "locationGeometry");
				EntityAttributeOptionalPanel locGeomPanel = new EntityAttributeOptionalPanel(LocationGeometryPanel.class, "locationGeometry", new Model(locGeomAttr), isEditable());
				add(locGeomPanel);
				
				EntityAttribute locTypeAttr = new EntityAttribute(location, "locationType");
				EntityAttributeOptionalPanel locTypePanel = new EntityAttributeOptionalPanel(LocationTypePanel.class, "locationType", new Model(locTypeAttr), isEditable());
				add(locTypePanel);
				
				EntityAttribute addressAttr = new EntityAttribute(location, "address");
				EntityAttributeOptionalPanel addressPanel = new EntityAttributeOptionalPanel(AddressPanel.class, "address", new Model(addressAttr), isEditable());
				add(addressPanel);
				
				EntityAttribute locPrecisionAttr = new EntityAttribute(location, "locationPrecision");
				add(new TextPanel("locationPrecision", new Model(locPrecisionAttr), isEditable()));
	
				EntityAttribute locCommentAttr = new EntityAttribute(location, "locationComment");
				add(new TextAreaPanel("locationComment", new Model(locCommentAttr), isEditable()));
			}
			else
			{
				// empty panels
				add(new Panel("locationGeometry").setVisible(false));	
				add(new Panel("locationType").setVisible(false));	
				add(new Panel("address").setVisible(false));	
				add(new Panel("locationPrecision").setVisible(false));	
				add(new Panel("locationComment").setVisible(false));					
			}
		}
	}
}
