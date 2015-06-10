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
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.tridas.schema.TridasWoodCompleteness;

/**
 * @author paulboon
 */
public class WoodCompletenessPanel extends EntityAttributePanel {
	private static final long serialVersionUID = -3812203769213813083L;

	public WoodCompletenessPanel(String id, IModel model) {
		super(id, model, false);
	}

	public WoodCompletenessPanel(String id, IModel model, boolean editable) {
		super(id, model, editable); 
		//super(id, model, false); // disable editing
	}

	public class WoodCompletenessPanelView extends Panel {
		private static final long serialVersionUID = -2691262342673754195L;

		public WoodCompletenessPanelView(String id, IModel model) {
			super(id, model);

			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();

			// assume TridasWoodCompleteness
			TridasWoodCompleteness val = (TridasWoodCompleteness)attr.getEntryObject();

			if (val != null) {
				EntityAttribute ringCountAttr = new EntityAttribute(val, "ringCount");
				IntegerPanel ringCountPanel = new IntegerPanel("ringCount", new Model(ringCountAttr), isEditable());
				add(ringCountPanel);
				
				EntityAttribute averageRingWidthAttr = new EntityAttribute(val, "averageRingWidth");
				DoublePanel averageRingWidthPanel = new DoublePanel("averageRingWidth", new Model(averageRingWidthAttr), isEditable());
				add(averageRingWidthPanel);
				
				EntityAttribute nrOfUnmeasuredInnerRingsAttr = new EntityAttribute(val, "nrOfUnmeasuredInnerRings");
				IntegerPanel nrOfUnmeasuredInnerRingsPanel = new IntegerPanel("nrOfUnmeasuredInnerRings", new Model(nrOfUnmeasuredInnerRingsAttr), isEditable());
				add(nrOfUnmeasuredInnerRingsPanel);

				EntityAttribute nrOfUnmeasuredOuterRingsAttr = new EntityAttribute(val, "nrOfUnmeasuredOuterRings");
				IntegerPanel nrOfUnmeasuredOuterRingsPanel = new IntegerPanel("nrOfUnmeasuredOuterRings", new Model(nrOfUnmeasuredOuterRingsAttr), isEditable());
				add(nrOfUnmeasuredOuterRingsPanel);
								
				EntityAttribute pithAttr = new EntityAttribute(val.getPith(), "presence");
				ComplexPresenceAbsencePanel pithPanel = new ComplexPresenceAbsencePanel("pith", new Model(pithAttr), isEditable());
				add(pithPanel);
				
				EntityAttribute heartwoodAttr = new EntityAttribute(val.getHeartwood(), "presence");
				ComplexPresenceAbsencePanel heartwoodPanel = new ComplexPresenceAbsencePanel("heartwood", new Model(heartwoodAttr), isEditable());
				add(heartwoodPanel);
	
				EntityAttribute sapwoodAttr = new EntityAttribute(val.getSapwood(), "presence");
				ComplexPresenceAbsencePanel sapwoodPanel = new ComplexPresenceAbsencePanel("sapwood", new Model(sapwoodAttr), isEditable());
				add(sapwoodPanel);
			
				EntityAttribute barkAttr = new EntityAttribute(val.getBark(), "presence");
				PresenceAbsencePanel barkPanel = new PresenceAbsencePanel("bark", new Model(barkAttr), isEditable());
				add(barkPanel);
			}
			else
			{
				// empty hidden panels
				add(new Panel("ringCount").setVisible(false));	
				add(new Panel("averageRingWidth").setVisible(false));	
				add(new Panel("nrOfUnmeasuredInnerRings").setVisible(false));	
				add(new Panel("nrOfUnmeasuredOuterRings").setVisible(false));	
				
				add(new Panel("pith").setVisible(false));	
				add(new Panel("heartwood").setVisible(false));	
				add(new Panel("sapwood").setVisible(false));	
				add(new Panel("bark").setVisible(false));
			}
		}
	}

	// Note: Identical to view
	public class WoodCompletenessPanelEdit extends Panel {
		private static final long serialVersionUID = -2691262342673754195L;

		public WoodCompletenessPanelEdit(String id, IModel model) {
			super(id, model);

			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();

// BUG, not saved; use PropertyModels!!!
////////////////////////////////////////
			
			// assume TridasWoodCompleteness
			TridasWoodCompleteness val = (TridasWoodCompleteness)attr.getEntryObject();
			
			if (val != null) {
//				CompoundPropertyModel propModel = new CompoundPropertyModel(val);
				// Optional
				
				EntityAttribute ringCountAttr = new EntityAttribute(val, "ringCount");
				IntegerPanel ringCountPanel = new IntegerPanel("ringCount", new Model(ringCountAttr), isEditable());
				add(ringCountPanel);
				
				EntityAttribute averageRingWidthAttr = new EntityAttribute(val, "averageRingWidth");
				DoublePanel averageRingWidthPanel = new DoublePanel("averageRingWidth", new Model(averageRingWidthAttr), isEditable());
				add(averageRingWidthPanel);
				
				EntityAttribute nrOfUnmeasuredInnerRingsAttr = new EntityAttribute(val, "nrOfUnmeasuredInnerRings");
				IntegerPanel nrOfUnmeasuredInnerRingsPanel = new IntegerPanel("nrOfUnmeasuredInnerRings", new Model(nrOfUnmeasuredInnerRingsAttr), isEditable());
				add(nrOfUnmeasuredInnerRingsPanel);

				EntityAttribute nrOfUnmeasuredOuterRingsAttr = new EntityAttribute(val, "nrOfUnmeasuredOuterRings");
				IntegerPanel nrOfUnmeasuredOuterRingsPanel = new IntegerPanel("nrOfUnmeasuredOuterRings", new Model(nrOfUnmeasuredOuterRingsAttr), isEditable());
				add(nrOfUnmeasuredOuterRingsPanel);
				
				
				// Required
				
				EntityAttribute pithAttr = new EntityAttribute(val.getPith(), "presence");
				ComplexPresenceAbsencePanel pithPanel = new ComplexPresenceAbsencePanel("pith", new Model(pithAttr), isEditable());
				add(pithPanel);
				
				//EntityAttribute heartwoodAttr = new EntityAttribute(val.getHeartwood(), "presence");
				//ComplexPresenceAbsencePanel heartwoodPanel = new ComplexPresenceAbsencePanel("heartwood", new Model(heartwoodAttr), isEditable());
				EntityAttribute heartwoodAttr = new EntityAttribute(val, "heartwood");
				HeartwoodPanel heartwoodPanel = new HeartwoodPanel("heartwood", new Model(heartwoodAttr), isEditable());
				add(heartwoodPanel);
	
				//EntityAttribute sapwoodAttr = new EntityAttribute(val.getSapwood(), "presence");
				//ComplexPresenceAbsencePanel sapwoodPanel = new ComplexPresenceAbsencePanel("sapwood", new Model(sapwoodAttr), isEditable());
				EntityAttribute sapwoodAttr = new EntityAttribute(val, "sapwood");
				SapwoodPanel sapwoodPanel = new SapwoodPanel("sapwood", new Model(sapwoodAttr), isEditable());
				add(sapwoodPanel);
			
				EntityAttribute barkAttr = new EntityAttribute(val.getBark(), "presence");
				PresenceAbsencePanel barkPanel = new PresenceAbsencePanel("bark", new Model(barkAttr), isEditable());
				add(barkPanel);
			}
			else
			{
				// empty hidden panels
				add(new Panel("ringCount").setVisible(false));	
				add(new Panel("averageRingWidth").setVisible(false));	
				add(new Panel("nrOfUnmeasuredInnerRings").setVisible(false));	
				add(new Panel("nrOfUnmeasuredOuterRings").setVisible(false));	

				add(new Panel("pith").setVisible(false));	
				add(new Panel("heartwood").setVisible(false));	
				add(new Panel("sapwood").setVisible(false));	
				add(new Panel("bark").setVisible(false));
			}			
		}
	}
}
