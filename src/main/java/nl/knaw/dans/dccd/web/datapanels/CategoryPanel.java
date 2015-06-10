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
import nl.knaw.dans.dccd.model.vocabulary.MultiLingualTridasTerms;
import nl.knaw.dans.dccd.web.DccdSession;
import nl.knaw.dans.dccd.web.base.BasePage;
import nl.knaw.dans.dccd.web.project.ProjectEditPage;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tridas.schema.ControlledVoc;
//import org.tridas.schema.NormalTridasCategory;
//import org.tridas.schema.TridasCategory;

import com.visural.wicket.component.dropdown.DropDown;
import com.visural.wicket.component.dropdown.DropDownDataSource;

/**
 * @author paulboon
 */
public class CategoryPanel extends EntityAttributePanel {
	private static final long	serialVersionUID	= 1184702738326671461L;
	private static final Logger logger = LoggerFactory.getLogger(CategoryPanel.class);

	public CategoryPanel(String id, IModel model) {
		super(id, model, false);
	}

	public CategoryPanel(String id, IModel model, boolean editable) {
		super(id, model, editable);
		//super(id, model, false); // disable editing
	}

	public class CategoryPanelView extends Panel {
		private static final long serialVersionUID = 3342926435388567777L;
	
		public CategoryPanelView(String id, IModel model) {
			super(id, model);
	
			// get the TridasCategory object
			//TridasCategory category = (TridasCategory)model.getObject();
			ControlledVoc category = (ControlledVoc)((EntityAttribute) model.getObject()).getEntryObject();
	
			// Used to add only the term
			//add(new Label("category", category.getValue()));
	
			// Add the controlled voc..panel?
			//EntityAttribute attr = new EntityAttribute(model, new UIMapEntry("cvoc", "Object"));
			add(new ControlledVocabularyPanel("cvoc_panel", model));//new Model(attr)));

			// Note: this one is empty in the TRiDaS schema, and will be deleted
			// therefore don't show it
			/*
			String normalCategoryStr = "";
			// also has NormalTridasCategory, controlled vocabulary of research categories ?
			if (category.isSetNormalTridas()) {
				NormalTridasCategory normalCategory = category.getNormalTridas();
				normalCategoryStr = normalCategory.value();
			}
			add(new Label("normalcategory", normalCategoryStr));
			*/
		}
	}

	// Note: almost identical to the view because we have a subpanel
	public class CategoryPanelEdit extends Panel {
		private static final long	serialVersionUID	= -6101810176691769211L;

		// For the ComboBox			
		private DropDownDataSource listDS;

		public CategoryPanelEdit(String id, IModel model) {
			super(id, model);
	
			// ComboBox list data
			// could include jQuery this for all pages... in the WicketApplication
			// addRenderHeadListener(JavascriptPackageResource.getHeaderContribution(new JQueryResourceReference()));
			add(JavascriptPackageResource.getHeaderContribution(new ResourceReference(EasyUpload.class, "js/lib/jquery-1.3.2.min.js")));
			listDS = new DropDownDataSource<String>() {
				private static final long	serialVersionUID	= 1L;
				public String getName() {
			        return "project.category.list";
			    }
			    public List<String> getValues() {
			    	//return DccdVocabularyService.getService().getTerms("project.category");
			    	// Use the project language
			    	String langCode = ((DccdSession) getSession()).getContentLanguageCode();
					return DccdVocabularyService.getService().getTerms("project.category", langCode);
			    }
			    public String getDescriptionForValue(String t) {
			        return t;
			    }
			};

			// get the TridasCategory object
			//TridasCategory category = (TridasCategory)model.getObject();
			ControlledVoc category = (ControlledVoc)((EntityAttribute) model.getObject()).getEntryObject();

			if(category != null)
			{
				/*	
				TridasVocabularyAutoCompleteSelector selector = new TridasVocabularyAutoCompleteSelector("cvoc_term", 
									new PropertyModel(category, "value"),
									"project.category");
				add(selector);
				*/
				// ComboBox
				DropDown comboBox = new DropDown("cvoc_term", new PropertyModel(category, "value"), listDS, false);
				comboBox.setCharacterWidth(30);
				add(comboBox);
				
				TextField cvocNormalField = new TextField("cvoc_normalisedname", 
						new PropertyModel(category, "normal"));
				add(cvocNormalField);
				
				TextField cvocNormalIdField = new TextField("cvoc_standarddictionaryId", 
						new PropertyModel(category, "normalId"));
				add(cvocNormalIdField);
				
				TextField cvocNormalStdField = new TextField("cvoc_standardname", 
						new PropertyModel(category, "normalStd"));
				add(cvocNormalStdField);
				
				TextField cvocLangField = new TextField("cvoc_langname", 
						new PropertyModel(category, "lang"));
				add(cvocLangField);
			}
			else
			{
				add(new TextField("cvoc_term").setVisible(false));
				add(new TextField("cvoc_normalisedname").setVisible(false));
				add(new TextField("cvoc_standarddictionaryId").setVisible(false));
				add(new TextField("cvoc_standardname").setVisible(false));				
				add(new TextField("cvoc_langname").setVisible(false));				
			}

			// Note: NormalTridasCategory is an enum, so 
			// we cannot set it to something that is not in the enum;
			// In tridas1.2.1 the enum is empty => so this field is useless! 

			/*
			// also has NormalTridasCategory, controlled vocabulary of research categories ?
			if (category.isSetNormalTridas()) {
				NormalTridasCategory normalCategory = category.getNormalTridas();
				//normalCategoryStr = normalCategory.value();
				TextField normalcategoryField = new TextField("normalcategory", 
						new PropertyModel(normalCategory, "value"));
				add(normalcategoryField);
			}
			else
			{
				add(new TextField("normalcategory").setVisible(false));				
			}
			*/
		}
	}
	
}
