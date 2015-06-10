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

import java.util.ArrayList;
import java.util.List;
import nl.knaw.dans.dccd.application.services.UIMapper;
import nl.knaw.dans.dccd.model.EntityAttribute;
import nl.knaw.dans.dccd.model.UIMapEntry;
//import nl.knaw.dans.dccd.model.DendroField;
import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
//import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

/**
 * Generic Panel for displaying dendro data at a level in the tridas data tree.
 * It will show a table with names and values.
 * Only shows attributes marked as "open access"
 *
 * @author paulboon
 */
public class OpenAccessDendroEntityPanel extends EntityAttributePanel {
	private static final long serialVersionUID = -1882004661723730921L;
	private static Logger logger = Logger.getLogger(OpenAccessDendroEntityPanel.class);

	public OpenAccessDendroEntityPanel(String id, IModel model) {
		this(id, model, false);
	}

	public OpenAccessDendroEntityPanel(String id, IModel model, final boolean editable) {
		super(id, model, false); // disable editing, open access is view-only
	}

	public class OpenAccessDendroEntityPanelView extends Panel {
		private static final long serialVersionUID = -571922177724503076L;

		public OpenAccessDendroEntityPanelView(String id, IModel model) {
			super(id, model);

			final Object infoObject = model.getObject(); // shortcut
			logger.info("Building view for: " + infoObject);

			UIMapper mapper = new UIMapper();
			List<UIMapEntry> entries = mapper.getUIMapEntries(infoObject
					.getClass());

			// title/name of entity
			String entityName = mapper.getEntityLabelString(infoObject.getClass());
			ResourceModel nameModel = new ResourceModel("entity_name_"+entityName, entityName);
			add(new Label("infoTitle", nameModel));//entityName));

			// populate the table
			add(getListView(entries, infoObject));
		}
	}

	/**
	 * NOTE: maybe this should be a inner class (extends ListView), but
	 * refactoring other things first?
	 *
	 * @param entries
	 * @param infoObject
	 * @return
	 */
	private ListView getListView(List<UIMapEntry> entries,
			final Object infoObject)
	{
		// get only non open-access
		List<UIMapEntry> openEntries = new ArrayList<UIMapEntry>();
		for(UIMapEntry entry : entries)
		{
			if(entry.isOpenAccess())
				openEntries.add(entry);
		}

		return new ListView("dendro_info", openEntries) {
			@Override
			protected void populateItem(ListItem item) {
				UIMapEntry entry = (UIMapEntry) item
						.getModelObject();

				// title/name of attribute/entry
				String resourceStr = "attr_name_" + entry.getName();
				resourceStr = resourceStr.replace(' ', '_');
				logger.debug("resource: " + resourceStr);
				ResourceModel nameModel = new ResourceModel(resourceStr, entry.getName());
				//item.add(new Label("info_name", entry.getName()));
				item.add(new Label("info_name", nameModel));

				EntityAtributePanelFactory factory = new EntityAtributePanelFactory();
				Panel panel = factory.createPanel("info_value_panel", new EntityAttribute(infoObject, entry), isEditable());

				// Checking return value,
				// but probably would better when using exception handling!
				if (panel != null ) {
					// OK, just add it
					item.add(panel);
				} else {
					logger.warn("No panel for entry with name: " + entry.getName());
					// just get rid of unspecified panels, but keep wicket happy
					// Probably better to catch an Exception from the PanelFactory!
					item.add(new Label("info_value_panel",""));
					// Note in debug mode we would like another string
					// indicating that there was no panel
				}
			}

			private static final long serialVersionUID = -7685400118105538983L;
		}; // new listview
	}
}
