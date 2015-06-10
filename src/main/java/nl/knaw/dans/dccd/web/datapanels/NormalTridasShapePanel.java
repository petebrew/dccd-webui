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
import nl.knaw.dans.dccd.web.HomePage;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.tridas.schema.NormalTridasShape;

/**
 * @author dev
 */

public class NormalTridasShapePanel extends EntityAttributePanel {
	private static final long	serialVersionUID	= -1791113176095817549L;

	public NormalTridasShapePanel(String id, IModel model) {
		super(id, model, false);
	}

	public NormalTridasShapePanel(String id, IModel model, final boolean editable) {
		super(id, model, editable);
		//super(id, model, false); // disable editing!
	}

	public class NormalTridasShapePanelView extends Panel {
		private static final long	serialVersionUID	= 1179764601121549059L;

		public NormalTridasShapePanelView(String id, IModel model) {
			super(id, model);

			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();

			// assume NormalTridasShape
			NormalTridasShape val = (NormalTridasShape)attr.getEntryObject();
			
			String str = "";

			if(val != null) {
				str = val.value();  //.toString();
			}

			Label label = new Label("text", new Model(str));
			add(label);			
			
			// Allow to view the explanatory images
			// using the Wicket Modal window with a panel
			final ModalWindow shapesDlg;
			add(shapesDlg = new ModalWindow("shapesDlg"));
			shapesDlg.setContent(new ShowAllShapesPanel(shapesDlg.getContentId(), model));
			shapesDlg.setTitle("Shapes");
			// The button (was Indicating)
			AjaxLink shapesDlgButton = new AjaxLink("shapesDlgButton")
			{
				private static final long	serialVersionUID	= -1L;
				@Override
				public void onClick(AjaxRequestTarget target)
				{
					shapesDlg.show(target);
				}	
			};
			add(shapesDlgButton);
		}
	}
	
	public class NormalTridasShapePanelEdit extends Panel {
		private static final long	serialVersionUID	= 3218828855099054135L;

		public NormalTridasShapePanelEdit(String id, IModel model) {
			super(id, model);

			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();

			// assume NormalTridasShape
			NormalTridasShape val = (NormalTridasShape)attr.getEntryObject();
			
			if (val != null)
			{				
				DropDownChoice choice = new DropDownChoice("type", 
						new PropertyModel(attr.getObject(), attr.getEntry().getMethod()), 
						Arrays.asList(NormalTridasShape.values()),
						new ChoiceRenderer("value", "value")
				);
				add(choice);
				
				// Allow to view the explanatory images
				// using the Wicket Modal window with a panel
				final ModalWindow shapesDlg;
				add(shapesDlg = new ModalWindow("shapesDlg"));
				shapesDlg.setContent(new ShowAllShapesPanel(shapesDlg.getContentId(), model));
				shapesDlg.setTitle("Shapes");
				// The button (was Indicating)
				AjaxLink shapesDlgButton = new AjaxLink("shapesDlgButton")
				{
					private static final long	serialVersionUID	= -1L;
					@Override
					public void onClick(AjaxRequestTarget target)
					{
						shapesDlg.show(target);
					}	
				};
				add(shapesDlgButton);
				
			}
			else
			{
				// empty panel
				add(new DropDownChoice("type").setVisible(false));				
				
				add(new Panel("shapesDlg").setVisible(false));				
				add(new BookmarkablePageLink("shapesDlgButton", HomePage.class).setVisible(false));	
			}
		}
	}	
}
