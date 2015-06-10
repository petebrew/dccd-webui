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
import org.tridas.schema.TridasLastRingUnderBark;

/**
 * Could make an OptionalPresenceAbsencePanel 
 * for reused of any class with a 'get/setPresence' method
 * 
 * @author paulboon
 */
public class LastRingUnderBarkPanel extends EntityAttributePanel {
	private static final long	serialVersionUID	= 996592906880169906L;

	public LastRingUnderBarkPanel(String id, IModel model) {
		super(id, model, false);
	}

	public LastRingUnderBarkPanel(String id, IModel model, boolean editable) {
		super(id, model, editable); 
		//super(id, model, false); // disable editing
	}

	public class LastRingUnderBarkPanelView extends Panel {
		private static final long	serialVersionUID	= -6170592798298931071L;

		public LastRingUnderBarkPanelView(String id, IModel model) {
			super(id, model);

			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();

			// assume TridasLastRingUnderBark
			TridasLastRingUnderBark val = (TridasLastRingUnderBark)attr.getEntryObject();
			
			if (val != null) 
			{			
				EntityAttribute lastRingAttr = new EntityAttribute(val, "presence");
				PresenceAbsencePanel lastRingPanel = new PresenceAbsencePanel("presence", new Model(lastRingAttr), isEditable());
				add(lastRingPanel);				
			}
			else
			{
				// empty hidden panels
				add(new Panel("presence").setVisible(false));
			}			
		}
	}

	// Note: Identical to view
	public class LastRingUnderBarkPanelEdit extends Panel {
		private static final long	serialVersionUID	= -4839671237228982299L;

		public LastRingUnderBarkPanelEdit(String id, IModel model) {
			super(id, model);

			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();

			// assume TridasLastRingUnderBark
			TridasLastRingUnderBark val = (TridasLastRingUnderBark)attr.getEntryObject();

			if (val != null) 
			{			
				EntityAttribute lastRingAttr = new EntityAttribute(val, "presence");
				PresenceAbsencePanel lastRingPanel = new PresenceAbsencePanel("presence", new Model(lastRingAttr), isEditable());
				add(lastRingPanel);				
			}
			else
			{
				// empty hidden panels
				add(new Panel("presence").setVisible(false));
			}			
		}
	}
}
