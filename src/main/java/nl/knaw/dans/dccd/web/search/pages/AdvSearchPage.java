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
package nl.knaw.dans.dccd.web.search.pages;

import java.util.Iterator;
import java.util.List;

import nl.knaw.dans.common.lang.search.Field;
import nl.knaw.dans.common.wicket.components.buttons.CancelLink;
import nl.knaw.dans.common.wicket.components.search.FieldNameResourceTranslator;
import nl.knaw.dans.common.wicket.components.search.criteria.CriteriumLabel;
import nl.knaw.dans.common.wicket.components.search.criteria.MultiFilterCriterium;
import nl.knaw.dans.common.wicket.components.search.criteria.SearchCriteriaPanel;
import nl.knaw.dans.common.wicket.components.search.criteria.TextSearchCriterium;
import nl.knaw.dans.common.wicket.components.search.model.SearchModel;
import nl.knaw.dans.common.wicket.components.upload.EasyUpload;
import nl.knaw.dans.dccd.application.services.DccdVocabularyService;
import nl.knaw.dans.dccd.web.DccdResources;
import nl.knaw.dans.dccd.web.DccdSession;
import nl.knaw.dans.dccd.web.authn.LoginPage;
import nl.knaw.dans.dccd.web.datapanels.TaxonSelectPanel;
import nl.knaw.dans.dccd.web.search.AbstractSearchPage;
import nl.knaw.dans.dccd.web.search.AbstractSearchResultPage;
import nl.knaw.dans.dccd.web.search.years.YearSearchPanel;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.visural.wicket.component.dropdown.DropDown;
import com.visural.wicket.component.dropdown.DropDownDataSource;

public class AdvSearchPage extends AbstractSearchPage
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AdvSearchPage.class);
    
	private Class<? extends AbstractSearchResultPage>	resultPage;


	public AdvSearchPage()
	{
		this(PublicSearchResultPage.class);
	}

	public AdvSearchPage(Class<? extends AbstractSearchResultPage> resultPage)
	{
		super(new SearchModel());
		this.resultPage = resultPage;
		init();
	}

	public AdvSearchPage(SearchModel searchModel, Class<? extends AbstractSearchResultPage> resultPage)
	{
		super(searchModel);
		this.resultPage = resultPage;
		init();
	}
	
	private void init()
	{
		// must be logged in to view this page
		if (!((DccdSession) Session.get()).isLoggedIn())
		{
			LOGGER.debug("User not logged in; Redirecting to login page");
		
			// redirect to login page and enable the login to return here
			//throw new RestartResponseAtInterceptPageException(LoginPage.class);
			redirectToInterceptPage(new LoginPage());
			return;
		}

		//addCommonFeedbackPanel();
		
		add(new SearchCriteriaPanel("searchCriteria", getSearchModel()));
		
		add(new AdvancedSearchForm("advancedSearchForm"));
	}

	public void onAdvancedSearch(final AdvSearchData searchData)
	{
		final List<Field<?>> fields = searchData.getFields(false);
		
		if (fields.size() == 0 && StringUtils.isBlank(searchData.query))
		{
			//warningMessage(DccdResources.PLEASE_ENTER_A_SEARCH_VALUE);
			return;
		} 
		
		LOGGER.debug("Search request for advanced search: " + searchData.query);
		
		if (!StringUtils.isBlank(searchData.query))
		{
			getSearchModel().addCriterium(
					new TextSearchCriterium(
							searchData.query,
							new AbstractReadOnlyModel<String>()
							{
								private static final long	serialVersionUID	= 1114909631810523718L;

								public String getObject() 
								{
									return CriteriumLabel.createFilterText(
											AdvSearchPage.this.getString(DccdResources.ADVSEARCH_ANYFIELD_CRITERIUM_PREFIX ), 
											searchData.query
										);
								}
							}
						)
				);
		}
		
		if (fields.size() > 0)
		{
			getSearchModel().addCriterium(
						new MultiFilterCriterium(
								fields,
								new AbstractReadOnlyModel<String>()
								{
									private static final long	serialVersionUID	= 6378460988292127479L;
	
									@Override
									public String getObject()
									{
										String prefix = "";
										String fieldStr = "";
										
										if (fields.size() > 1)
										{
											prefix = AdvSearchPage.this.getString(DccdResources.ADVSEARCH_CRITERIUM_PREFIX);
	
											Iterator<Field<?>> fieldIt = fields.iterator();
											while(fieldIt.hasNext())
											{
												Field<?> field = fieldIt.next();
												fieldStr += field.getValue().toString();
												if (fieldIt.hasNext())
													fieldStr += ", ";
											}	
										}
										else if (fields.size() > 0) 
										{
											Field<?> field = fields.get(0);
	
											FieldNameResourceTranslator translator = new FieldNameResourceTranslator();
											IModel<String> translation = translator.getTranslation(field.getName(), getLocale(), false);
											prefix = translation.getObject();
											
											fieldStr = field.getValue().toString();
										}
										return CriteriumLabel.createFilterText(
													prefix,
													fieldStr
												);
									}
								}
							)
					);
		}
		
		setResponsePage(
				AbstractSearchResultPage.instantiate(resultPage, getSearchModel())
			);
	}

	/**
	 * The form with all input options. The markup for this form can currently be found in 
	 * AdvancedSearchPage.html.
	 * 
	 * @author lobo
	 */
	class AdvancedSearchForm extends Form<AdvSearchData> 
	{
	    private static final long serialVersionUID = -3768697914151647721L;
		
		public AdvancedSearchForm(String wicketId)
		{
			this(wicketId, new Model<AdvSearchData>(null));
		}

		public AdvancedSearchForm(String wicketId, IModel<AdvSearchData> model)
		{
			super(wicketId, model);

			if (getModelObject() == null)
			{
				model = new Model(new AdvSearchData());
				setDefaultModel(model);
			}
			
			final AdvSearchData data = (AdvSearchData) getModelObject();
			
			// Note: EOF has Depositor and Archivist panels here

			// general search fields
			add(new TextField("anyField", new PropertyModel(data, "query")));
			// Project
			add(new TextField("projectTitle", new SearchFieldModel(data, "projectTitle")));
			add(new TextField("projectIdentifier", new SearchFieldModel(data, "projectIdentifier")));
			add(new TextField("projectLabname", new SearchFieldModel(data, "projectLabname")));

			// With plain text input:
			//add(new TextField("projectCategory", new SearchFieldModel(data, "projectCategory")));
			// With dropdown:
			//List<String> terms = DccdVocabularyService.getService().getTerms("project.category");
			//add(new DropDownChoice("projectCategory", 
			//		new SearchFieldModel(data, "projectCategory"), 
			//		terms
			//));
			
			// With autocomplete:
			//add(new TridasVocabularyAutoCompleteSelector("projectCategory", 
			//					new SearchFieldModel(data, "projectCategory"),
			//					"project.category"));
			add(JavascriptPackageResource.getHeaderContribution(new ResourceReference(EasyUpload.class, "js/lib/jquery-1.3.2.min.js")));
			// With ComboBox:
			DropDown catComboBox = new DropDown("projectCategory", 
					new SearchFieldModel(data, "projectCategory"), 
					new DropDownDataSource<String>() {
						private static final long	serialVersionUID	= 1L;
						public String getName() {
					        return "project.category.list";
					    }
					    public List<String> getValues() {
					    	// Use current language to get terms for that language
					    	String langCode = getSession().getLocale().getLanguage();
					    	return DccdVocabularyService.getService().getTerms("project.category", langCode);
					    }
					    public String getDescriptionForValue(String t) {
					        return t;
					    }
					}, 
					false);
			catComboBox.setCharacterWidth(25);
			add(catComboBox);

			//TODO How to change the list when the language changes; dynamically?
			//should it be in the model as well

			// Object
			add(new TextField("objectTitle", new SearchFieldModel(data, "objectTitle")));
			add(new TextField("objectIdentifier", new SearchFieldModel(data, "objectIdentifier")));

			// With plain text input:
			//add(new TextField("objectType", new SearchFieldModel(data, "objectType")));
			// With autocomplete:
			//add(new TridasVocabularyAutoCompleteSelector("objectType", 
			//		new SearchFieldModel(data, "objectType"),
			//		"object.type"));
			// With ComboBox:
			DropDown objTypeComboBox = new DropDown("objectType", 
					new SearchFieldModel(data, "objectType"), 
					new DropDownDataSource<String>() {
						private static final long	serialVersionUID	= 1L;
						public String getName() {
					        return "object.type";
					    }
					    public List<String> getValues() {
					    	// Use current language to get terms for that language
					    	String langCode = getSession().getLocale().getLanguage();
					    	return DccdVocabularyService.getService().getTerms("object.type", langCode);
					    }
					    public String getDescriptionForValue(String t) {
					        return t;
					    }
					}, 
					false);
			objTypeComboBox.setCharacterWidth(25);
			add(objTypeComboBox);
			
			add(new TextField("objectCreator", new SearchFieldModel(data, "objectCreator")));
			
			// Element
			add(new TextField("elementTitle", new SearchFieldModel(data, "elementTitle")));
			add(new TextField("elementIdentifier", new SearchFieldModel(data, "elementIdentifier")));
			initElementTypeSelection(this, data);
			initTaxonSelection(this, data);

			// The Time period search
			add(new YearSearchPanel("yearSearchPanel", new PropertyModel(data, "yearSearchData")));
			
			add(new SubmitLink("submitButton"));
			add(new CancelLink("cancelButton"));
		}
		
		@Override
		protected void onSubmit()
		{
			onAdvancedSearch(getModelObject());
		}
	}
	
	/*
	 * Note: since the language switch already worked for the lists; 
	 * (before entering the advsearch page)
	 * we don't need the whole table with languages
	 */
	private void initElementTypeSelection(final AdvancedSearchForm form, final AdvSearchData data)
	{
		// With plain text input:
		//add(new TextField("elementType", new SearchFieldModel(data, "elementType")));
		// With autocomplete:
		//add(new TridasVocabularyAutoCompleteSelector("elementType", 
		//		new SearchFieldModel(data, "elementType"),
		//		"object.type"));			
		// With ComboBox:
		final DropDown elemTypeComboBox = new DropDown("elementType", 
				new SearchFieldModel(data, "elementType"), 
				new DropDownDataSource<String>() {
					private static final long	serialVersionUID	= 1L;
					public String getName() {
				        return "object.type";
				    }
				    public List<String> getValues() {
				    	// Use current language to get terms for that language
				    	String langCode = getSession().getLocale().getLanguage();
				    	return DccdVocabularyService.getService().getTerms("object.type", langCode);
				    }
				    public String getDescriptionForValue(String t) {
				        return t;
				    }
				}, 
				false);
		elemTypeComboBox.setCharacterWidth(25);
		form.add(elemTypeComboBox);
		
		/* 
		//TEST adding the window with the table
		//Problem, the TaxonSelectPanel wants a real TridasTaxon
		final ModalWindow modalSelectDialog;
		form.add(modalSelectDialog = new ModalWindow("objectTypeSelectionPanel"));
		final ObjectTypeSelectionPanel objectTypeSelectionPanel = 
		new ObjectTypeSelectionPanel(modalSelectDialog.getContentId(), new Model(null)) 
		{
			private static final long	serialVersionUID	= 1L;
			@Override
			protected void onSelectionChanged(AjaxRequestTarget target)
			{
				//String typeString = getSelectionAsString();
				//LOGGER.debug("Selected type: " + typeString);
				//data.elementType.setValue(typeString);
				// Note: fixed BUG that adds new inner box on every selection... giving a matroeska effect
				// This was done by changing the source code of visural-wicket Netbeans project and now DCCD uses this jar
				target.addComponent(elemTypeComboBox);
			}
		};
		modalSelectDialog.setContent(objectTypeSelectionPanel);
		//modalSelectDialog.setTitle(ProjectPermissionSettingsPage.this.getString("userAddDialogTitle"));
		modalSelectDialog.setCookieName("objectTypeSelectionPanelWindow");
		modalSelectDialog.setInitialWidth(400);
		modalSelectDialog.setInitialHeight(160);
		modalSelectDialog.setCloseButtonCallback(new ModalWindow.CloseButtonCallback()
		{
			private static final long	serialVersionUID	= 1L;
			
			public boolean onCloseButtonClicked(AjaxRequestTarget target)
			{
				return true;
			}
		});
		
		//button to show the dialog
		AjaxLink objectTypeSelectButton = new IndicatingAjaxLink("objectTypeSelectButton")
		{
			private static final long	serialVersionUID	= 1L;
			@Override
			public void onClick(AjaxRequestTarget target)
			{
				//LOGGER.debug("term=" + data.elementTaxon.getValue());
				//taxonSelectPanel.selectTerm(elemTaxonComboBox.getValue());
				modalSelectDialog.show(target);
			}	
		};
		form.add(objectTypeSelectButton);
		*/		
	}
	
	private void initTaxonSelection(final AdvancedSearchForm form, final AdvSearchData data)
	{
		// With plain text input:
		//add(new TextField("elementTaxon", new SearchFieldModel(data, "elementTaxon")));
		// With autocomplete:
		//add(new TridasVocabularyAutoCompleteSelector("elementTaxon", 
		//		new SearchFieldModel(data, "elementTaxon"),
		//		"element.taxon"));			
		// With ComboBox:
		final DropDown elemTaxonComboBox = new DropDown("elementTaxon", 
				new SearchFieldModel(data, "elementTaxon"), 
				new DropDownDataSource<String>() {
					private static final long	serialVersionUID	= 1L;
					public String getName() {
				        return "element.taxon";
				    }
				    public List<String> getValues() {
				    	// Use current language to get terms for that language
				    	String langCode = getSession().getLocale().getLanguage();
				    	return DccdVocabularyService.getService().getTerms("element.taxon", langCode);
				    }
				    public String getDescriptionForValue(String t) {
				        return t;
				    }
				}, 
				false);
		elemTaxonComboBox.setCharacterWidth(25);
		elemTaxonComboBox.setOutputMarkupId(true);
		//form.add(elemTaxonComboBox);
		// FIX Container is needed to workaround visural-wicket ISSUE 67
		final WebMarkupContainer taxonContainer= new WebMarkupContainer("elementTaxonContainer");
		taxonContainer.setOutputMarkupId(true);
		form.add(taxonContainer);
		taxonContainer.add(elemTaxonComboBox);

		//TEST adding the window with the table
		//Problem, the TaxonSelectPanel wants a real TridasTaxon
		final ModalWindow modalSelectDialog;
		form.add(modalSelectDialog = new ModalWindow("taxonSelectPanel"));
		final TaxonSelectPanel taxonSelectPanel = 
		new TaxonSelectPanel(modalSelectDialog.getContentId(), new Model(null)) 
		{
			private static final long	serialVersionUID	= 1L;
			@Override
			protected void onSelectionChanged(AjaxRequestTarget target)
			{
				String taxonString = getSelectionAsString();
				LOGGER.debug("Selected taxon: " + taxonString);
				data.elementTaxon.setValue(taxonString);
				//target.addComponent(elemTaxonComboBox);
				// FIX	workaround visural-wicket ISSUE 67 by updating the container			
				target.addComponent(taxonContainer);				
			}
		};
		modalSelectDialog.setContent(taxonSelectPanel);
		//modalSelectDialog.setTitle(ProjectPermissionSettingsPage.this.getString("userAddDialogTitle"));
		modalSelectDialog.setCookieName("taxonSelectPanelWindow");
		modalSelectDialog.setInitialWidth(400);
		modalSelectDialog.setInitialHeight(160);
		modalSelectDialog.setCloseButtonCallback(new ModalWindow.CloseButtonCallback()
		{
			private static final long	serialVersionUID	= 1L;
			
			public boolean onCloseButtonClicked(AjaxRequestTarget target)
			{
				return true;
			}
		});
		
		//button to show the dialog
		AjaxLink taxonSelectButton = new IndicatingAjaxLink("taxonSelectButton")
		{
			private static final long	serialVersionUID	= 1L;
			@Override
			public void onClick(AjaxRequestTarget target)
			{
				//LOGGER.debug("term=" + data.elementTaxon.getValue());
				//taxonSelectPanel.selectTerm(elemTaxonComboBox.getValue());
				modalSelectDialog.show(target);
			}	
		};
		form.add(taxonSelectButton);			
	}
}
