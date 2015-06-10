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
import org.tridas.schema.TridasCoverage;

/**
 * @author paulboon
 */
public class CoveragePanel extends EntityAttributePanel {
	private static final long	serialVersionUID	= -3563032457748389700L;

	public CoveragePanel(String id, IModel model) {
		super(id, model, false);
	}

	public CoveragePanel(String id, IModel model, final boolean editable) {
		super(id, model, editable);
		//super(id, model, false); // disable editing!
	}
	
	public class CoveragePanelView extends Panel {
		private static final long	serialVersionUID	= 6176076961780394538L;

		public CoveragePanelView(String id, IModel model) {
			super(id, model);
			// assume object is a TridasCoverage from the tridas data
			TridasCoverage coverage = (TridasCoverage)((EntityAttribute) model.getObject()).getEntryObject();
	
			if (coverage != null)
			{
				EntityAttribute attr = new EntityAttribute(coverage, "coverageTemporal");
				TextPanel temporalPanel = new TextPanel("temporal", new Model(attr), isEditable());
				add(temporalPanel);
			}
			else
			{
				// empty panel
				add(new Panel("temporal").setVisible(false));	
			}

			if (coverage != null)
			{
				EntityAttribute attr = new EntityAttribute(coverage, "coverageTemporalFoundation");
				TextPanel temporalFoundationPanel = new TextPanel("temporal_foundation", new Model(attr), isEditable());
				add(temporalFoundationPanel);
			}
			else
			{
				// empty panel
				add(new Panel("temporal_foundation").setVisible(false));	
			}
		}
	}

	// Note: identical to view
	public class CoveragePanelEdit extends Panel {
		private static final long	serialVersionUID	= -8674814550128094073L;

		public CoveragePanelEdit(String id, IModel model) {
			super(id, model);
			// assume object is a TridasCoverage from the tridas data
			TridasCoverage coverage = (TridasCoverage)((EntityAttribute) model.getObject()).getEntryObject();
	
			if (coverage != null)
			{
				EntityAttribute attr = new EntityAttribute(coverage, "coverageTemporal");
				TextPanel temporalPanel = new TextPanel("temporal", new Model(attr), isEditable());
				add(temporalPanel);
			}
			else
			{
				// empty panel
				add(new Panel("temporal").setVisible(false));	
			}

			if (coverage != null)
			{
				EntityAttribute attr = new EntityAttribute(coverage, "coverageTemporalFoundation");
				TextPanel temporalFoundationPanel = new TextPanel("temporal_foundation", new Model(attr), isEditable());
				add(temporalFoundationPanel);
			}
			else
			{
				// empty panel
				add(new Panel("temporal_foundation").setVisible(false));	
			}
		}
	}
}
