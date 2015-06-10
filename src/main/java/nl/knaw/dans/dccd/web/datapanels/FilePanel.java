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

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.tridas.schema.TridasFile;

/**
 * @author paulboon
 */
public class FilePanel extends EntityAttributePanel  {
	private static final long serialVersionUID = -6612851696364811797L;
	private static Logger logger = Logger.getLogger(FilePanel.class);

	public FilePanel(String id, IModel model) {
		super(id, model, false);
	}

	public FilePanel(String id, IModel model, final boolean editable) {
		super(id, model, editable);
		//super(id, model, false); // disable editing
	}

	public class FilePanelView extends Panel {
		private static final long serialVersionUID = 7350116369052533106L;
		public FilePanelView(String id, IModel model) {
			super(id, model);
			initPanel(this);
		}
	}

	public class FilePanelEdit extends Panel {
		private static final long serialVersionUID = 3673064275623411632L;
		public FilePanelEdit(String id, IModel model) {
			super(id, model);
			initPanel(this);
		}
	}

	private void initPanel(final Panel panel)
	{
		// assume object is a TridasFile
		TridasFile file = (TridasFile)((EntityAttribute)getDefaultModelObject()).getEntryObject();
		
		EntityAttribute hRefAttr = new EntityAttribute(file, "href");
		TextPanel hrefPanel = new TextPanel("file", new Model(hRefAttr), isEditable());
        panel.add(hrefPanel);
	}
}

