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

import java.util.List;

import nl.knaw.dans.dccd.model.EntityAttribute;
import nl.knaw.dans.dccd.model.UIMapEntry;

import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.tridas.schema.TridasFile;

/**
 * For Files we wanted a repeater that is non-editable
 * The easiest way was to meke a new EntityAttributePanel
 * 
 * @author paulboon
 */
public class FileRepeaterPanel extends EntityAttributePanel {
	private static final long serialVersionUID = -3528385237710941877L;

	public FileRepeaterPanel(String id, IModel model) {
		super(id, model, false);
	}

	public FileRepeaterPanel(String id, IModel model, boolean editable) {
		super(id, model, false); // non editable!
	}

	public class FileRepeaterPanelView extends Panel {
		private static final long	serialVersionUID	= -9049777583017515497L;

		@SuppressWarnings("unchecked")
		public FileRepeaterPanelView(String id, IModel model) {
			super(id, model);

			//addListView(this, getList());
			// assume list of TridasFile
			List<TridasFile> list = (List< TridasFile >) ((EntityAttribute) model.getObject()).getEntryObject();

			// fill the list
			add(new ListView("file", list) {
				private static final long	serialVersionUID	= 4770673572516236979L;

				@Override
			    protected void populateItem(ListItem item) {
					// construct Attribute to serve the Panel
					EntityAttribute attr = new EntityAttribute(item, new UIMapEntry("file", "modelObject"));
					item.add(new FilePanel("file_panel", new Model(attr)));
				}
			});

		}
	}
}

