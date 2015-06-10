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

import nl.knaw.dans.common.wicket.components.upload.EasyUpload;
import nl.knaw.dans.dccd.application.services.DccdVocabularyService;
import nl.knaw.dans.dccd.model.EntityAttribute;
import nl.knaw.dans.dccd.web.DccdSession;

import org.apache.log4j.Logger;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.tridas.schema.ControlledVoc;

import com.visural.wicket.component.dropdown.DropDown;
import com.visural.wicket.component.dropdown.DropDownDataSource;

/**
 * @author paulboon
 */
public class ProjectTypePanel extends EntityAttributePanel 
{
	private static final long	serialVersionUID	= 1838478253415850869L;
	private static Logger logger = Logger.getLogger(ProjectTypePanel.class);
	
	public ProjectTypePanel(String id, IModel model) {
		super(id, model, false);
	}

	public ProjectTypePanel(String id, IModel model, final boolean editable) {
		super(id, model, editable);
		//super(id, model, false); // only allow view
	}
		
	public class ProjectTypePanelView extends Panel {
		private static final long	serialVersionUID	= 2305493169270410700L;

		public ProjectTypePanelView(String id, IModel model) {
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

	public class ProjectTypePanelEdit extends Panel {
		private static final long	serialVersionUID	= 462959756181140669L;

		// For the ComboBox			
		private DropDownDataSource listDS;
		
		public ProjectTypePanelEdit(String id, IModel model) {
			super(id, model);
			
			// ComboBox list data
			// could include jQuery this for all pages... in the WicketApplication
			// addRenderHeadListener(JavascriptPackageResource.getHeaderContribution(new JQueryResourceReference()));
			add(JavascriptPackageResource.getHeaderContribution(new ResourceReference(EasyUpload.class, "js/lib/jquery-1.3.2.min.js")));
			listDS = new DropDownDataSource<String>() {
				private static final long	serialVersionUID	= 1L;
				public String getName() {
			        return "project.type.list";
			    }
			    public List<String> getValues() {
			    	//return DccdVocabularyService.getService().getTerms("project.type");
			    	// Use the project language
			    	String langCode = ((DccdSession) getSession()).getContentLanguageCode();
					return DccdVocabularyService.getService().getTerms("project.type", langCode);			    	
			    }
			    public String getDescriptionForValue(String t) {
			        return t;
			    }
			};

			// now do the edit stuff
			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();
			// assume object is a ControlledVoc
			//ControlledVoc voc = (ControlledVoc)model.getObject();
			ControlledVoc cvoc = (ControlledVoc)attr.getEntryObject();
			
			if(cvoc != null)
			{
				// Note: in voc term selection: How to change the list when the language changes?
				/*
				// An autocomplete!
				// and have a dialog (pop-up) for selecting a term from a list;
				TridasVocabularyAutoCompleteSelector autocompleteSelector = new TridasVocabularyAutoCompleteSelector("cvoc_term", 
									new PropertyModel(cvoc, "value"),
									"project.type");
				add(autocompleteSelector);
				*/
				// ComboBox
				DropDown comboBox = new DropDown("cvoc_term", new PropertyModel(cvoc, "value"), listDS, false);
				comboBox.setCharacterWidth(30);
				add(comboBox);
				
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

