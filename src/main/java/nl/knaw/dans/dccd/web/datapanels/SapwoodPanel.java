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
import org.tridas.schema.TridasSapwood;

/**
 * @author paulboon
 */
public class SapwoodPanel extends EntityAttributePanel {
	private static final long	serialVersionUID	= 2854878413435703165L;

	public SapwoodPanel(String id, IModel model) {
		super(id, model, false);
	}

	public SapwoodPanel(String id, IModel model, boolean editable) {
		super(id, model, editable); 
		//super(id, model, false); // disable editing
	}

	public class SapwoodPanelView extends Panel {
		private static final long	serialVersionUID	= -8017573385944289165L;

		public SapwoodPanelView(String id, IModel model) {
			super(id, model);

			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();

			// assume TridasSapwood
			TridasSapwood val = (TridasSapwood)attr.getEntryObject();
			
			if (val != null) 
			{
				EntityAttribute presenceAttr = new EntityAttribute(val, "presence");
				ComplexPresenceAbsencePanel presencePanel = new ComplexPresenceAbsencePanel("presence", new Model(presenceAttr), isEditable());
				add(presencePanel);
				
				EntityAttribute sAttr = new EntityAttribute(val, "nrOfSapwoodRings");
				IntegerPanel nrOfSapwoodRingsPanel = new IntegerPanel("nrOfSapwoodRings", new Model(sAttr), isEditable());
				add(nrOfSapwoodRingsPanel);
				
				EntityAttribute lastRingAttr = new EntityAttribute(val, "lastRingUnderBark");
				//PresenceAbsencePanel lastRingPanel = new PresenceAbsencePanel("lastRingUnderBark", new Model(lastRingAttr), isEditable());
				LastRingUnderBarkPanel lastRingPanel = new LastRingUnderBarkPanel("lastRingUnderBark", new Model(lastRingAttr), isEditable());
				add(lastRingPanel);
				
				EntityAttribute mAttr = new EntityAttribute(val, "missingSapwoodRingsToBark");
				IntegerPanel missingRingsPanel = new IntegerPanel("missingSapwoodRingsToBark", new Model(mAttr), isEditable());
				add(missingRingsPanel);
				
				EntityAttribute fAttr = new EntityAttribute(val, "missingSapwoodRingsToBarkFoundation");
				TextPanel missingRingsFoundationPanel = new TextPanel("missingSapwoodRingsToBarkFoundation", new Model(fAttr), isEditable());
				add(missingRingsFoundationPanel);
			}
			else
			{
				// empty hidden panels
				add(new Panel("presence").setVisible(false));
				add(new Panel("nrOfSapwoodRings").setVisible(false));
				add(new Panel("lastRingUnderBark").setVisible(false));
				add(new Panel("missingSapwoodRingsToBark").setVisible(false));
				add(new Panel("missingSapwoodRingsToBarkFoundation").setVisible(false));
			}			

		}
	}

	// Note: Identical to view
	public class SapwoodPanelEdit extends Panel {
		private static final long	serialVersionUID	= 5805739825997298120L;

		public SapwoodPanelEdit(String id, IModel model) {
			super(id, model);

			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();

			// assume TridasSapwood
			TridasSapwood val = (TridasSapwood)attr.getEntryObject();
			
			if (val != null) 
			{
				// Required 
				EntityAttribute presenceAttr = new EntityAttribute(val, "presence");
				ComplexPresenceAbsencePanel presencePanel = new ComplexPresenceAbsencePanel("presence", new Model(presenceAttr), isEditable());
				add(presencePanel);

				// Optional			

				EntityAttribute sAttr = new EntityAttribute(val, "nrOfSapwoodRings");
				IntegerPanel nrOfSapwoodRingsPanel = new IntegerPanel("nrOfSapwoodRings", new Model(sAttr), isEditable());
				add(nrOfSapwoodRingsPanel);
				
				EntityAttribute lastRingAttr = new EntityAttribute(val, "lastRingUnderBark");
				//PresenceAbsencePanel lastRingPanel = new PresenceAbsencePanel("lastRingUnderBark", new Model(lastRingAttr), isEditable());
				//LastRingUnderBarkPanel lastRingPanel = new LastRingUnderBarkPanel("lastRingUnderBark", new Model(lastRingAttr), isEditable());
				EntityAttributeOptionalPanel lastRingPanel = new EntityAttributeOptionalPanel(LastRingUnderBarkPanel.class, "lastRingUnderBark", new Model(lastRingAttr), isEditable());
				add(lastRingPanel);
				
				EntityAttribute mAttr = new EntityAttribute(val, "missingSapwoodRingsToBark");
				IntegerPanel missingRingsPanel = new IntegerPanel("missingSapwoodRingsToBark", new Model(mAttr), isEditable());
				add(missingRingsPanel);
				
				EntityAttribute fAttr = new EntityAttribute(val, "missingSapwoodRingsToBarkFoundation");
				TextPanel missingRingsFoundationPanel = new TextPanel("missingSapwoodRingsToBarkFoundation", new Model(fAttr), isEditable());
				add(missingRingsFoundationPanel);
			}
			else
			{
				// empty hidden panels
				add(new Panel("presence").setVisible(false));
				add(new Panel("nrOfSapwoodRings").setVisible(false));
				add(new Panel("lastRingUnderBark").setVisible(false));
				add(new Panel("missingSapwoodRingsToBark").setVisible(false));
				add(new Panel("missingSapwoodRingsToBarkFoundation").setVisible(false));
			}			
		}
	}
}
