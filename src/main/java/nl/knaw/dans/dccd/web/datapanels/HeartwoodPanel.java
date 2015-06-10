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
import org.tridas.schema.TridasHeartwood;


/**
 * @author paulboon
 */
public class HeartwoodPanel extends EntityAttributePanel {
	private static final long	serialVersionUID	= -6707916375234847968L;

	public HeartwoodPanel(String id, IModel model) {
		super(id, model, false);
	}

	public HeartwoodPanel(String id, IModel model, boolean editable) {
		super(id, model, editable); 
		//super(id, model, false); // disable editing
	}

	public class HeartwoodPanelView extends Panel {
		private static final long	serialVersionUID	= -7478991684246319848L;

		public HeartwoodPanelView(String id, IModel model) {
			super(id, model);

			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();

			// assume TridasHeartwood
			TridasHeartwood val = (TridasHeartwood)attr.getEntryObject();
			
			if (val != null) 
			{
				EntityAttribute presenceAttr = new EntityAttribute(val, "presence");
				ComplexPresenceAbsencePanel presencePanel = new ComplexPresenceAbsencePanel("presence", new Model(presenceAttr), isEditable());
				add(presencePanel);
				
				EntityAttribute mAttr = new EntityAttribute(val, "missingHeartwoodRingsToPith");
				IntegerPanel missingRingsPanel = new IntegerPanel("missingHeartwoodRingsToPith", new Model(mAttr), isEditable());
				add(missingRingsPanel);
				
				EntityAttribute fAttr = new EntityAttribute(val, "missingHeartwoodRingsToPithFoundation");
				TextPanel missingRingsFoundationPanel = new TextPanel("missingHeartwoodRingsToPithFoundation", new Model(fAttr), isEditable());
				add(missingRingsFoundationPanel);
			}
			else
			{
				// empty hidden panels
				add(new Panel("presence").setVisible(false));
				add(new Panel("missingHeartwoodRingsToPith").setVisible(false));
				add(new Panel("missingHeartwoodRingsToPithFoundation").setVisible(false));
			}			

		}
	}

	// Note: Identical to view
	public class HeartwoodPanelEdit extends Panel {
		private static final long	serialVersionUID	= 2965166519310070362L;

		public HeartwoodPanelEdit(String id, IModel model) {
			super(id, model);

			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();
			
// Required 
//			presence
// Optional			
//			missingHeartwoodRingsToPith
//			missingHeartwoodRingsToPithFoundation

			// assume TridasHeartwood
			TridasHeartwood val = (TridasHeartwood)attr.getEntryObject();
			
			if (val != null) 
			{
				EntityAttribute presenceAttr = new EntityAttribute(val, "presence");
				ComplexPresenceAbsencePanel presencePanel = new ComplexPresenceAbsencePanel("presence", new Model(presenceAttr), isEditable());
				add(presencePanel);
				
				EntityAttribute mAttr = new EntityAttribute(val, "missingHeartwoodRingsToPith");
				IntegerPanel missingRingsPanel = new IntegerPanel("missingHeartwoodRingsToPith", new Model(mAttr), isEditable());
				add(missingRingsPanel);
				
				EntityAttribute fAttr = new EntityAttribute(val, "missingHeartwoodRingsToPithFoundation");
				TextPanel missingRingsFoundationPanel = new TextPanel("missingHeartwoodRingsToPithFoundation", new Model(fAttr), isEditable());
				add(missingRingsFoundationPanel);
			}
			else
			{
				// empty hidden panels
				add(new Panel("presence").setVisible(false));
				add(new Panel("missingHeartwoodRingsToPith").setVisible(false));
				add(new Panel("missingHeartwoodRingsToPithFoundation").setVisible(false));
			}			
		}
	}
}
