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
import org.tridas.schema.TridasAddress;

/**
 * @author dev
 */
public class AddressPanel extends EntityAttributePanel {
	private static final long	serialVersionUID	= 3497235251166312594L;

	// Note: do I need those two constructors???
	public AddressPanel(String id, IModel model) {
		super(id, model, false);
	}

	public AddressPanel(String id, IModel model, final boolean editable) {
		super(id, model, editable);
		//super(id, model, false); // only allow view
	}

	public class AddressPanelView extends Panel {
		private static final long	serialVersionUID	= -5373569759777210728L;

		public AddressPanelView(String id, IModel model) {
			super(id, model);
			//EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();
			//TridasAddress address = (TridasAddress)attr.getEntryObject();
			TridasAddress address = (TridasAddress)((EntityAttribute) model.getObject()).getEntryObject();

			initPanel(this, address);
		}
	}

	public class AddressPanelEdit extends Panel {
		private static final long	serialVersionUID	= 2457362163646837961L;

		public AddressPanelEdit(String id, IModel model) {
			super(id, model);
			//EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();
			//TridasAddress address = (TridasAddress)attr.getEntryObject();
			TridasAddress address = (TridasAddress)((EntityAttribute) model.getObject()).getEntryObject();

			initPanel(this, address);
		}
	}
	
	private void initPanel(final Panel panel, final TridasAddress address)
	{
		if (address != null)
		{
			EntityAttribute attr = new EntityAttribute(address, "addressLine1");
			TextPanel textPanel = new TextPanel("addressLine1", new Model(attr), isEditable());
			panel.add(textPanel);
			
			attr = new EntityAttribute(address, "addressLine2");
			textPanel = new TextPanel("addressLine2", new Model(attr), isEditable());
			panel.add(textPanel);
			
			attr = new EntityAttribute(address, "cityOrTown");
			textPanel = new TextPanel("cityOrTown", new Model(attr), isEditable());
			panel.add(textPanel);
	
			attr = new EntityAttribute(address, "stateProvinceRegion");
			textPanel = new TextPanel("stateProvinceRegion", new Model(attr), isEditable());
			panel.add(textPanel);
	
			attr = new EntityAttribute(address, "postalCode");
			textPanel = new TextPanel("postalCode", new Model(attr), isEditable());
			panel.add(textPanel);
		
			attr = new EntityAttribute(address, "country");
			textPanel = new TextPanel("country", new Model(attr), isEditable());
			panel.add(textPanel);
		}
		else
		{
			// empty panel's
			panel.add(new Panel("addressLine1").setVisible(false));	
			panel.add(new Panel("addressLine2").setVisible(false));	
			panel.add(new Panel("cityOrTown").setVisible(false));	
			panel.add(new Panel("stateProvinceRegion").setVisible(false));	
			panel.add(new Panel("postalCode").setVisible(false));	
			panel.add(new Panel("country").setVisible(false));				
		}
		
		/*
		//addressLine1
		if (address != null && address.isSetAddressLine1())
		{
			EntityAttribute attr = new EntityAttribute(address, "addressLine1");
			TextPanel textPanel = new TextPanel("addressLine1", new Model(attr), isEditable());
			panel.add(textPanel);
		}
		else
		{
			// empty panel
			panel.add(new Panel("addressLine1").setVisible(false));	
		}

		//addressLine2
		if (address != null && address.isSetAddressLine2())
		{
			EntityAttribute attr = new EntityAttribute(address, "addressLine2");
			TextPanel textPanel = new TextPanel("addressLine2", new Model(attr), isEditable());
			panel.add(textPanel);
		}
		else
		{
			// empty panel
			panel.add(new Panel("addressLine2").setVisible(false));	
		}
		
		//cityOrTown
		if (address != null && address.isSetCityOrTown())
		{
			EntityAttribute attr = new EntityAttribute(address, "cityOrTown");
			TextPanel textPanel = new TextPanel("cityOrTown", new Model(attr), isEditable());
			panel.add(textPanel);
		}
		else
		{
			// empty panel
			panel.add(new Panel("cityOrTown").setVisible(false));	
		}

		//stateProvinceRegion
		if (address != null && address.isSetStateProvinceRegion())
		{
			EntityAttribute attr = new EntityAttribute(address, "stateProvinceRegion");
			TextPanel textPanel = new TextPanel("stateProvinceRegion", new Model(attr), isEditable());
			panel.add(textPanel);
		}
		else
		{
			// empty panel
			panel.add(new Panel("stateProvinceRegion").setVisible(false));	
		}
		
		//postalCode
		if (address != null && address.isSetPostalCode())
		{
			EntityAttribute attr = new EntityAttribute(address, "postalCode");
			TextPanel textPanel = new TextPanel("postalCode", new Model(attr), isEditable());
			panel.add(textPanel);
		}
		else
		{
			// empty panel
			panel.add(new Panel("postalCode").setVisible(false));	
		}
		
		//country
		if (address != null && address.isSetCountry())
		{
			EntityAttribute attr = new EntityAttribute(address, "country");
			TextPanel textPanel = new TextPanel("country", new Model(attr), isEditable());
			panel.add(textPanel);
		}
		else
		{
			// empty panel
			panel.add(new Panel("country").setVisible(false));	
		}
		*/
	}
}

