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

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.tridas.schema.TridasInterpretationUnsolved;

/**
 * @author dev
 */
public class InterpretationUnsolvedPanel extends EntityAttributePanel
{
	private static final long	serialVersionUID	= -8379926608831495853L;

	// Note: do I need those two constructors???
	public InterpretationUnsolvedPanel(String id, IModel model) {
		super(id, model, false);
	}

	public InterpretationUnsolvedPanel(String id, IModel model, final boolean editable) {
		//super(id, model, editable);
		super(id, model, false); // disable edit
	}

	public class InterpretationUnsolvedPanelView extends Panel {
		private static final long serialVersionUID = -761910690499775518L;

		public InterpretationUnsolvedPanelView(String id, IModel model) {
			super(id, model);
			// now do the view stuff (non-editable)

			// get the Tridas object
			TridasInterpretationUnsolved interpretationUnsolved = (TridasInterpretationUnsolved)((EntityAttribute) model.getObject()).getEntryObject();
			
			String interpretationUnsolvedString = "";
			if (interpretationUnsolved != null)
			{
				interpretationUnsolvedString = "specified";//interpretationUnsolved.toString();
			}
			else
			{
				interpretationUnsolvedString = "unspecified";
			}
			
			Label label = new Label("text", new Model(interpretationUnsolvedString));
			add(label);
		}
	}
}
