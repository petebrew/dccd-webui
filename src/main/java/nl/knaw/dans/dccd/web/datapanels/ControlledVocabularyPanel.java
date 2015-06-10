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
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.tridas.schema.ControlledVoc;

/**
 * @author paulboon
 */
public class ControlledVocabularyPanel extends EntityAttributePanel 
{
	private static Logger logger = Logger.getLogger(ControlledVocabularyPanel.class);
	private static final long serialVersionUID = 4475297844199541385L;
	private String vocabularyName = null;
	
	public ControlledVocabularyPanel(String id, IModel model) {
		super(id, model, false);
	}

	public ControlledVocabularyPanel(String id, IModel model, final boolean editable) {
		super(id, model, editable);
		//super(id, model, false); // only allow view
	}
	
	public ControlledVocabularyPanel(String id, IModel model, String vocabularyName) {
		super(id, model, false);
		this.vocabularyName = vocabularyName;
	}

	public ControlledVocabularyPanel(String id, IModel model, String vocabularyName, final boolean editable) {
		super(id, model, editable);
		//super(id, model, false); // only allow view
		
		this.vocabularyName = vocabularyName;
	}

	private boolean isSetVocabularyName()
	{
		return (vocabularyName == null) ? false : true;
	}
	
	public class ControlledVocabularyPanelView extends Panel {
		private static final long	serialVersionUID	= -2022577467737331889L;

		public ControlledVocabularyPanelView(String id, IModel model) {
			super(id, model);
			// now do the view stuff (non-editable)
			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();

			// assume object is a ControlledVoc
			//ControlledVoc voc = (ControlledVoc)model.getObject();
			ControlledVoc cvoc = (ControlledVoc)attr.getEntryObject();

			String cvocTermString = "";
			String cvocNormalisedNameString = "";
			String cvocStandardDictionaryIdString = "";
			String cvocStandardNameString = "";
			String cvocLangNameString = "";

			if (cvoc != null) {
				cvocTermString = cvoc.getValue();
				if (cvoc.isSetNormal())
					cvocNormalisedNameString = cvoc.getNormal();
				if (cvoc.isSetNormalId())
					cvocStandardDictionaryIdString = cvoc.getNormalId();
				if (cvoc.isSetNormalStd())
					cvocStandardNameString = cvoc.getNormalStd();
				if (cvoc.isSetLang())
					cvocLangNameString = cvoc.getLang();
			}

			// term
			add(new Label("cvoc_term", cvocTermString).setVisible(cvoc!=null));

			// note: Used to skip attributes: normal, normalId, normalStd
			add(new Label("cvoc_normalisedname", cvocNormalisedNameString));
			add(new Label("cvoc_standarddictionaryId", cvocStandardDictionaryIdString));
			add(new Label("cvoc_standardname", cvocStandardNameString));
			add(new Label("cvoc_langname", cvocLangNameString));
			
		}
	}

	public class ControlledVocabularyPanelEdit extends Panel {
		private static final long	serialVersionUID	= 6620854565410623661L;

		public ControlledVocabularyPanelEdit(String id, IModel model) {
			super(id, model);
			// now do the edit stuff
			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();
			// assume object is a ControlledVoc
			//ControlledVoc voc = (ControlledVoc)model.getObject();
			ControlledVoc cvoc = (ControlledVoc)attr.getEntryObject();
			
			if(cvoc != null)
			{
// NOT Working!!!
				if (isSetVocabularyName())
				{
logger.debug("===> using Autocomplete for CVOC: " + vocabularyName);
					// An autocomplete!
					// and have a dialog (pop-up) for selecting a term from a list;
					TridasVocabularyAutoCompleteSelector autocompleteSelector = new TridasVocabularyAutoCompleteSelector("cvoc_term", 
										new PropertyModel(cvoc, "value"),
										vocabularyName);
					add(autocompleteSelector);
				}
				else
				{
					TextField cvocTermField = new TextField("cvoc_term", 
							new PropertyModel(cvoc, "value"));
					add(cvocTermField);
				}
	
				TextField cvocNormalField = new TextField("cvoc_normalisedname", 
						new PropertyModel(cvoc, "normal"));
				add(cvocNormalField);
	
				TextField cvocNormalIdField = new TextField("cvoc_standarddictionaryId", 
						new PropertyModel(cvoc, "normalId"));
				add(cvocNormalIdField);
				
				TextField cvocNormalStdField = new TextField("cvoc_standardname", 
						new PropertyModel(cvoc, "normalStd"));
				add(cvocNormalStdField);

				TextField cvocLangField = new TextField("cvoc_langname", 
						new PropertyModel(cvoc, "lang"));
				add(cvocLangField);
			}
			else
			{
				// empty
				add(new TextField("cvoc_term").setVisible(false));
				add(new TextField("cvoc_normalisedname").setVisible(false));
				add(new TextField("cvoc_standarddictionaryId").setVisible(false));
				add(new TextField("cvoc_standardname").setVisible(false));				
				add(new TextField("cvoc_langname").setVisible(false));				
			}
		}
	}
	
}

