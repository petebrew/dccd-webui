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

import java.util.Arrays;

import nl.knaw.dans.dccd.model.EntityAttribute;
import nl.knaw.dans.dccd.model.UIMapEntry;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.tridas.schema.NormalTridasDatingType;
import org.tridas.schema.TridasDating;
import org.tridas.schema.TridasDatingReference;
import org.tridas.schema.TridasInterpretation;

/**
 * @author paulboon
 */
public class InterpretationPanel extends EntityAttributePanel {
	private static final long serialVersionUID = 3878427559352322846L;
	private static Logger logger = Logger.getLogger(InterpretationPanel.class);

	public InterpretationPanel(String id, IModel model) {
		super(id, model, false);
	}

	public InterpretationPanel(String id, IModel model, boolean editable) {
		super(id, model, editable); 
		//super(id, model, false); // disable editing
	}

	public class InterpretationPanelView extends Panel {
		private static final long serialVersionUID = -2691262342673754195L;

		public InterpretationPanelView(String id, IModel model) {
			super(id, model);

			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();

			// assume TridasInterpretation
			TridasInterpretation interp = (TridasInterpretation)attr.getEntryObject();

			String provenanceStr = "";
			//String usedsoftwareStr = "";

			// check for null
			if (interp != null) {
				// provenance
				if (interp.isSetProvenance()) {
					provenanceStr = interp.getProvenance();
				}

				// Changed in TRiDaS v 1.2.1
				//usedSoftware
				//if (interp.isSetUsedSoftware()) {
				//	usedsoftwareStr = interp.getUsedSoftware();
				//}
			}

			// added in TRiDaS v1.2.1
			// dating
			if (interp != null && interp.isSetDating() && interp.getDating().isSetType()) 
			{
				TridasDating dating = interp.getDating();
				//EntityAttribute datingAttr = new EntityAttribute(dating, "type");
				String typeStr = dating.getType().value();
				add(new Label("datingtype", typeStr));
			}
			else
			{
				add(new Label("datingtype", "").setVisible(interp!=null));// empty string
			}

			//using YearPanel's
			// firstYear
			EntityAttribute yearAttr = new EntityAttribute(interp, new UIMapEntry("year", "firstYear"));
			add(new YearPanel("firstyear", new Model(yearAttr)));

			// added in TRiDaS v1.2.1
			yearAttr = new EntityAttribute(interp, new UIMapEntry("year", "lastYear"));
			add(new YearPanel("lastyear", new Model(yearAttr)));

			// Changed in TRiDaS v1.2.1 to from "sproutYear" to "pithYear"
			yearAttr = new EntityAttribute(interp, new UIMapEntry("year", "pithYear"));
			add(new YearPanel("pithyear", new Model(yearAttr)));
			//deathYear
			yearAttr = new EntityAttribute(interp, new UIMapEntry("year", "deathYear"));
			add(new YearPanel("deathyear", new Model(yearAttr)));

			// datingReference, only has a SeriesLink !
			if (interp != null && interp.isSetDatingReference()) 
			{
				TridasDatingReference ref = interp.getDatingReference();
				EntityAttribute refAttr = new EntityAttribute(ref, "linkSeries");
				add(new SeriesLinkPanel("datingreference", new Model(refAttr), isEditable()));
			} 
			else 
			{
				add(new Panel("datingreference").setVisible(false));
			}

			//statFoundation, optional List<TridasStatFoundation>
			EntityAttribute statAttr = new EntityAttribute(interp, new UIMapEntry("stat", "statFoundations"));
			add(new StatFoundationRepeaterPanel("statfoundation", new Model(statAttr)));

			// Using simple Label's.
			add(new Label("provenance", provenanceStr));
			//add(new Label("usedsoftware", usedsoftwareStr));
		}
	}

	public class InterpretationPanelEdit extends Panel {
		private static final long	serialVersionUID	= -3488726904264997743L;

		public InterpretationPanelEdit(String id, IModel model) {
			super(id, model);

			EntityAttribute attr = (EntityAttribute) getDefaultModelObject();

			// assume TridasInterpretation
			TridasInterpretation interp = (TridasInterpretation)attr.getEntryObject();

			// added in TRiDaS v1.2.1
			// dating; an enumeration
			if (interp != null)// && interp.isSetDating()) 
			{
				EntityAttribute datingAttr = new EntityAttribute(interp, "dating");
				//DatingPanel datingPanel = new DatingPanel("datingtype", new Model(datingAttr), isEditable());
				EntityAttributeOptionalPanel datingPanel = new EntityAttributeOptionalPanel(DatingPanel.class, "datingtype", new Model(datingAttr), isEditable());

				add(datingPanel);				
			} 
			else 
			{
				// empty and hidden
				add(new DropDownChoice("datingtype").setVisible(false));			
			}
		
			if (interp != null )//&& interp.isSetProvenance()) 
			{
				EntityAttribute textAttr = new EntityAttribute(interp, "provenance");
				TextPanel textPanel = new TextPanel("provenance", new Model(textAttr), isEditable());
				add(textPanel);				
			}
			else
			{
				add(new Panel("provenance").setVisible(false));
			}
					
			// Optional
			
			//using YearPanel's
			// firstYear
			//EntityAttribute yearAttr = new EntityAttribute(interp, new UIMapEntry("year", "firstYear"));
			//add(new YearPanel("firstyear", new Model(yearAttr), isEditable()));
			EntityAttribute yearAttr = new EntityAttribute(interp, new UIMapEntry("year", "firstYear")); // not optional in attribute
			EntityAttributeOptionalPanel firstyearPanel = new EntityAttributeOptionalPanel(YearPanel.class, "firstyear", new Model(yearAttr), isEditable());
			add(firstyearPanel);
			
			// added in TRiDaS v1.2.1
			yearAttr = new EntityAttribute(interp, new UIMapEntry("year", "lastYear"));
			//add(new YearPanel("lastyear", new Model(yearAttr), isEditable()));
			EntityAttributeOptionalPanel lastyearPanel = new EntityAttributeOptionalPanel(YearPanel.class, "lastyear", new Model(yearAttr), isEditable());
			add(lastyearPanel);

			// Changed in TRiDaS v1.2.1 to from "sproutYear" to "pithYear"
			yearAttr = new EntityAttribute(interp, new UIMapEntry("year", "pithYear"));
			//add(new YearPanel("pithyear", new Model(yearAttr), isEditable()));
			EntityAttributeOptionalPanel pithyearPanel = new EntityAttributeOptionalPanel(YearPanel.class, "pithyear", new Model(yearAttr), isEditable());
			add(pithyearPanel);

			//deathYear
			yearAttr = new EntityAttribute(interp, new UIMapEntry("year", "deathYear"));
			//add(new YearPanel("deathyear", new Model(yearAttr), isEditable()));
			EntityAttributeOptionalPanel deathyearPanel = new EntityAttributeOptionalPanel(YearPanel.class, "deathyear", new Model(yearAttr), isEditable());
			add(deathyearPanel);
			
			// datingReference, only has a SeriesLink !
			if (interp != null)// && interp.isSetDatingReference()) 
			{
				//TridasDatingReference ref = interp.getDatingReference();
				//EntityAttribute refAttr = new EntityAttribute(ref, "linkSeries");
				//add(new SeriesLinkPanel("datingreference", new Model(refAttr), isEditable()));
				
				//DatingReferencePanel
				EntityAttribute datingReferenceAttr = new EntityAttribute(interp, "datingReference");
				EntityAttributeOptionalPanel datingReferencePanel = new EntityAttributeOptionalPanel(DatingReferencePanel.class, "datingreference", new Model(datingReferenceAttr), isEditable());
				add(datingReferencePanel);
			} 
			else 
			{
				add(new Panel("datingreference").setVisible(false));
			}

			//statFoundation, optional List<TridasStatFoundation>
			if (interp != null ) {			
				EntityAttribute statAttr = new EntityAttribute(interp, "statFoundations");			
				EntityAttributeRepeaterPanel repeaterPanel = 
					new EntityAttributeRepeaterPanel(StatFoundationPanel.class, "statfoundation", new Model(statAttr), isEditable());
				add(repeaterPanel);
			} 
			else
			{
				add(new Panel("statfoundation").setVisible(false));
			}
		}
	}
}

