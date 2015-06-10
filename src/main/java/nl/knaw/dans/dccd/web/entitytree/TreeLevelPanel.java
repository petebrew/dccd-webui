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
package nl.knaw.dans.dccd.web.entitytree;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/** Display a sequence of selectabel strings,
 * each representing a level in a tree
 * from the root towards a branch (or leave)
 * By selecting a level (except the last = current)
 * the panel will [?remove the lower levels and?] indicate the number of levels it went 'up'
 *
 * unit testing could use a test list
 * List<String> levels = Arrays.asList(new String[] {"One","Two","Three"});
 *
 * @author paulboon
 */
public class TreeLevelPanel extends Panel {
	private static final long serialVersionUID = -1531803735388623394L;
	private static Logger logger = Logger.getLogger(TreeLevelPanel.class);

	@SuppressWarnings("unchecked")
	public TreeLevelPanel(String id, IModel model) {
		super(id, model);

		// the model object should have a list of object supporting the toString?
		// or a list of strings?
        final List<Object> list = (List<Object>)model.getObject();
        final int lastIndex = list.size()-1;

        // fill the list
        add(new ListView("level_list", list) {
 			private static final long serialVersionUID = 5242639240679858437L;

			@Override
            protected void populateItem(ListItem item) {
				//item.getIndex() + ":" + item.getModelObjectAsString()
				Link link = null;
				if (item.getIndex() == lastIndex) {
					// skip first separator at the end of the list
					item.add(new Label("separator", ""));

					// the last one should be a 'static' thing
					// because it is the current selection
					//item.add(new Label("level", new Model(item.getModelObjectAsString())));
					link = new  AjaxFallbackLink("level", item.getModel()) {
						private static final long serialVersionUID = -6041781592958070083L;

						@Override
						public void onClick(AjaxRequestTarget target) {
							// do nothing!
						}
					};
					link.setEnabled(false);//? disabled might be visually wrong
				} else {
					item.add(new Label("separator", ">"));
					// make it a clickable Link?
					link = new  AjaxFallbackLink("level", new Model(new Integer(item.getIndex()))) {//item.getModel()) {
						private static final long serialVersionUID = 3960686165249813487L;

						@Override
						public void onClick(AjaxRequestTarget target) {
							//
							Integer index = (Integer)getModelObject();
							int levelsUp = lastIndex - index.intValue();
							logger.info("clicked levels up: " + levelsUp);
							onSelectionChanged(levelsUp, target);
						}
					};
				}
				item.add(link);
				link.add(new Label("level_label", new Model(item.getDefaultModelObjectAsString())));

 			}
        });
	}

	/**
	 * Called when selection of level has changed
	 * override this to do your own handling
	 */
    public void onSelectionChanged(int levelsUp, AjaxRequestTarget target) {
    	// empty
    }

}

