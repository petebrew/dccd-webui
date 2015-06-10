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

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import nl.knaw.dans.dccd.application.services.DccdVocabularyService;
import nl.knaw.dans.dccd.model.EntityAttribute;
import nl.knaw.dans.dccd.model.vocabulary.MultiLingualTridasTerms;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.tridas.schema.ControlledVoc;

//import org.apache.wicket.markup.repeater.data.DataView;
//import org.apache.wicket.markup.repeater.data.GridView;

/**
 * @author dev
 */
public class ObjectTypeSelectionPanel extends Panel
{
	private static final long	serialVersionUID	= -6559179910088909151L;
	private static Logger logger = Logger.getLogger(ObjectTypeSelectionPanel.class);

	private String selectedTerm = "";
	
	
	public ObjectTypeSelectionPanel(String id, IModel<?> model)
	{
		super(id, model);
		
		EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();
		// assume object is a ControlledVoc
		final ControlledVoc cvoc = (ControlledVoc)attr.getEntryObject();
		
		// TESTING
		//String labelStr = "";
		//if (cvoc != null && cvoc.isSetValue())
		//	labelStr = cvoc.getValue();
		//Label label = new Label("label", "Initial term: " + labelStr);
		//add(label);
		
		// Build multilingual selectable table
		// first just a table... ListView
		MultiLingualTridasTerms terms = 
			DccdVocabularyService.getService().getMultiLingualTridasTerms("object.type");

		final int descriptionIndex = terms.getDescriptionIndex();
		
		final int MAX_LANG = 4; // Note: fixed in html
		
		// iterate the lines
		List<List<String>> termLines = terms.getTermLines();
		ListView<List<String>> termlist = new ListView<List<String>>("termlist", termLines) 
		{
			private static final long	serialVersionUID	= -3651087863165431582L;
			protected void populateItem(ListItem item) 
			{
				List<String> line = (List<String>)item.getModel().getObject();
				
				// fixed to columns max.
				for(int i = 0; i < MAX_LANG; i++)
				{
					if(line.size() < (i+1))
					{
						item.add(new Label("term_"+i, ""));//empty
					}
					else
					{
						// TODO make selectable...
						//item.add(new Label("term_"+i, line.get(i)));
						final String termStr = line.get(i);
						// Note: could use AjaxFallbackLink, but without Ajax DCCD is almost unusable...
						AjaxLink link = new AjaxLink("linkterm_"+i)
						{
							private static final long	serialVersionUID	= -1L;
							@Override
							public void onClick(AjaxRequestTarget target)
							{
								selectedTerm = termStr;
								
								logger.debug("selected term: " + selectedTerm);
								
								// update
								cvoc.setValue(selectedTerm);
								onSelectionChanged(target);
							}
						};
						item.add(link);
						link.add(new Label("term_"+i, termStr));
					}
				}
				
				// description
				if (descriptionIndex >= MAX_LANG)
				{
					item.add(new Label("term_description", line.get(descriptionIndex)));
				}
				else
				{
					item.add(new Label("term_description",""));// empty
				}
			}
		};
		add(termlist);
		termlist.setReuseItems(true); // speedup, the list is not changing during a session!
		
		// Add headers for the columns
		for(int i = 0; i < MAX_LANG; i++)
		{
			String languageCode = terms.getLanguageCode(i);
			
			if (languageCode.isEmpty())
				add(new Label("title_"+i, ""));//empty
			else 
				add(new Label("title_"+i, languageCode.toUpperCase())); // TODO get display string for this code
		}
		
		// TODO could make this into a sortable pagable GridView....
		
	}
	
	/**
	 * Called when selection (node in tree) has changed
	 * override this to do your own handling
	 */
	protected void onSelectionChanged(AjaxRequestTarget target) 
	{
		// empty!
	}			
}

