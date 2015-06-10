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

import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.tridas.schema.TridasIdentifier;

/**
 * @author paulboon
 */
public class IdentifierPanel extends EntityAttributePanel {
	private static final long	serialVersionUID	= -4525435151540064585L;

	// Note: do I need those two constructors???
	public IdentifierPanel(String id, IModel model) {
		super(id, model, false);
	}

	public IdentifierPanel(String id, IModel model, final boolean editable) {
		super(id, model, editable);
		//super(id, model, false); // only allow view
	}

	public class IdentifierPanelView extends Panel {
		private static final long	serialVersionUID	= -2022577467737331889L;

		public IdentifierPanelView(String id, IModel model) {
			super(id, model);
			// now do the view stuff (non-editable)
			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();
			//Label label = new Label("text", new PropertyModel(attr.getObject(),
			//		attr.getEntry().getMethod()));
			//add(label);
			
			TridasIdentifier identifier = (TridasIdentifier)attr.getEntryObject();

			// should check for null and then give warning
			String identifierStr = identifier != null ? identifier.getValue() : "";
			String domainStr = identifier != null ? identifier.getDomain()
					.toString() : "";

			add(new Label("identifier", identifierStr));
			Label domainLabel = new Label("domain", domainStr);
			add(domainLabel);

			if (identifier == null)
				domainLabel.setVisible(false);			
		}
	}

	public class IdentifierPanelEdit extends Panel {
		private static final long	serialVersionUID	= -8954174715487792872L;

		public IdentifierPanelEdit(String id, IModel model) {
			super(id, model);
			// now do the edit stuff
			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();
			TridasIdentifier identifier = (TridasIdentifier)attr.getEntryObject();
			
			if (identifier != null)
			{
				
				// TODO setLabel on field with new ResourceModel(USER_USER_ID)
				
				// Its required
				TextField identifierField = new TextField("identifier", 
						new PropertyModel(identifier, "value"));
				identifierField.setConvertEmptyInputStringToNull(false);
				identifierField.setOutputMarkupId(true);
//				identifierField.setRequired(true);
//				// add feedback for the 'value'
//				FeedbackPanel feedBackPanel = new FeedbackPanel(identifierField.getId() + "-" + "componentFeedback")
//				{
//					private static final long	serialVersionUID	= -3578422941853187247L;
//					// EOF does this!
//			        @Override
//		            public boolean isVisible()
//		            {
//		                return this.anyMessage();
//		            }
//				};
//				// PROBLEM, with this filter we get no messages at all!
//			    ComponentFeedbackMessageFilter filter = new ComponentFeedbackMessageFilter(identifierField);
//				feedBackPanel.setFilter(filter);
//				feedBackPanel.setOutputMarkupId(true);
				add(identifierField);
//				add(feedBackPanel);
				
				TextField domainField = new TextField("domain", 
						new PropertyModel(identifier, "domain"));
				domainField.setConvertEmptyInputStringToNull(false);
				add(domainField);
			}
			else
			{
				// FIX DIRTY! TODO fix nicely
				add(new TextField("identifier").setVisible(false));
				add(new TextField("domain").setVisible(false));
			}

			// Get an empty string instead of null when the textfield is empty,
			// seems better than the default null
		}
	}
}
