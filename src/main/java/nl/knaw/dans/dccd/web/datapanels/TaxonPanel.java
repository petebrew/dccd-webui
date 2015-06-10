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
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.tree.BaseTree;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IComponentAssignedModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.template.TextTemplateHeaderContributor;
import org.tridas.schema.ControlledVoc;

import com.visural.wicket.component.dropdown.DropDown;
import com.visural.wicket.component.dropdown.DropDownDataSource;

/**
 * Started as a copy of the ControlledVocabularyPanel, 
 * but this one is tailored to Element.Taxon
 *  
 * @author dev
 */
public class TaxonPanel extends EntityAttributePanel 
{
	private static Logger logger = Logger.getLogger(TaxonPanel.class);
	private static final long	serialVersionUID	= 1420317732730778712L;

	public TaxonPanel(String id, IModel model) {
		super(id, model, false);
	}

	public TaxonPanel(String id, IModel model, final boolean editable) {
		super(id, model, editable);
		//super(id, model, false); // only allow view
	}

	public class TaxonPanelView extends Panel {
		private static final long	serialVersionUID	= -8093353357332361902L;

		public TaxonPanelView(String id, IModel model) {
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

	public class TaxonPanelEdit extends Panel {
		private static final long	serialVersionUID	= 6296834020314072540L;

		//private TridasVocabularyAutoCompleteSelector autocompleteSelector = null;
		//private TaxonSelectPanel treeSelectPanel = null;
		//private Link displayTreeButton = null;
	
		// For the ComboBox			
		private DropDownDataSource listDS;
		private DropDown autocompleteSelector = null;
		private WebMarkupContainer termContainer = null;

		private boolean displayTree = false; // hide initially
		public String getDisplayTreeButtonText()
		{
			return displayTree ? "Hide tree" : "Show tree"; // TODO get from resource
		}
		
		public TaxonPanelEdit(String id, IModel model) {
			super(id, model);
			// now do the edit stuff
			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();
			// assume object is a ControlledVoc
			//ControlledVoc voc = (ControlledVoc)model.getObject();
			ControlledVoc cvoc = (ControlledVoc)attr.getEntryObject();
			
			/*
			displayTreeButton = new AjaxFallbackLink("show")
			{
				private static final long	serialVersionUID	= 1L;

				@Override
				public void onClick(AjaxRequestTarget target)
				{					
					// Note could show/hide panel here
					//toggle, note if we set Visible false, it cannot be set back to true...!!!
					
					displayTree = !displayTree; //toggle
					
					target.addComponent(displayTreeButton);
					target.addComponent(treeSelectPanel);
				}
			};
			add(displayTreeButton);
			displayTreeButton.add(new Label("displayButtonText", new PropertyModel(this, "displayTreeButtonText")));
			displayTreeButton.setOutputMarkupId(true);
			

			displayTreeButton.add(new AbstractBehavior()
			{
				private static final long	serialVersionUID	= -4059848188190074375L;

				@Override
				public void onComponentTag(Component component, ComponentTag tag)
				{
					// TODO Auto-generated method stub
					super.onComponentTag(component, tag);
					String onclick = tag.getAttributes().getString("onclick");
					
					tag.getAttributes().put("onclick", onclick);
				}	
			});
			*/

			if(cvoc != null)
			{
				/*
				// An autocomplete!
				// and have a dialog (pop-up) for selecting a term from a list;
				autocompleteSelector = new TridasVocabularyAutoCompleteSelector("cvoc_term", 
									new PropertyModel(cvoc, "value"),
									"element.taxon");
				add(autocompleteSelector);
				autocompleteSelector.setOutputMarkupId(true);
				
				autocompleteSelector.add(new AjaxFormComponentUpdatingBehavior("onchange")
		        {
					private static final long serialVersionUID = 1L;
					protected void onUpdate(final AjaxRequestTarget target)
		        	{
						// Note: looks like fired on an onblur kind of event
						
						EntityAttribute attr = (EntityAttribute) TaxonPanelEdit.this.getDefaultModelObject();
						// assume object is a ControlledVoc
						ControlledVoc cvoc = (ControlledVoc)attr.getEntryObject();
						if (cvoc != null && cvoc.isSetValue())
						{
							logger.debug("term changed: " + cvoc.getValue());	
							treeSelectPanel.selectTerm(cvoc.getValue()); // NOT sure why, because the model is changed???
						}
						else
						{
							logger.debug("no term, so deselecting");	
							treeSelectPanel.deselect();
						}
						target.addComponent(treeSelectPanel);
		        	}
		        });				
				
				treeSelectPanel = new TaxonSelectPanel("taxonSelectPanel", model) {
					private static final long	serialVersionUID	= 7903279667769689296L;

					@Override
					public void onSelectionChanged(AjaxRequestTarget target)
					{
						// The model data has been set already so 
						// Just make sure the components are updated the AJAXian way
						target.addComponent(autocompleteSelector);
					}
				};
				add(treeSelectPanel);				
				treeSelectPanel.setOutputMarkupId(true);
				
				// The display style is connected to the display field with a model
				// Note: When toggling visibility is done with 
				// an external JS library we don't need this anymore
				treeSelectPanel.add(new AttributeModifier("style", true,
					new AbstractReadOnlyModel()
					{
						private static final long	serialVersionUID	= 1L;
				
						@Override
						public Object getObject()
						{
						      return displayTree ? "" : "display:none";
						}
					}));
				*/
				
				/* Using ComboBox */
				// ComboBox list data
				// could include jQuery this for all pages... in the WicketApplication
				// addRenderHeadListener(JavascriptPackageResource.getHeaderContribution(new JQueryResourceReference()));
				add(JavascriptPackageResource.getHeaderContribution(new ResourceReference(EasyUpload.class, "js/lib/jquery-1.3.2.min.js")));
				listDS = new DropDownDataSource<String>() {
					private static final long	serialVersionUID	= 1L;
					public String getName() {
				        return "element.taxon";
				    }
				    public List<String> getValues() {
				    	//return DccdVocabularyService.getService().getTerms("object.type");
				    	// Use the project language
				    	String langCode = ((DccdSession) getSession()).getContentLanguageCode();
						return DccdVocabularyService.getService().getTerms("element.taxon", langCode);
				    }
				    public String getDescriptionForValue(String t) {
				        return t;
				    }
				};
				// ComboBox
				autocompleteSelector = new DropDown("cvoc_term", new PropertyModel(cvoc, "value"), listDS, false);
				autocompleteSelector.setCharacterWidth(30);
				autocompleteSelector.setOutputMarkupId(true);
				//add(autocompleteSelector);
				// FIX Container is needed to workaround visural-wicket ISSUE 67
				termContainer= new WebMarkupContainer("cvoc_termContainer");
				termContainer.setOutputMarkupId(true);
				add(termContainer);
				termContainer.add(autocompleteSelector);
				
				/* Modal Window */
				// using the Wicket Modal window with a panel
				//displayTable = true; // TEST, otherwise we see nothing
				final ModalWindow modalSelectDialog;
				add(modalSelectDialog = new ModalWindow("select2Test"));
				
				modalSelectDialog.setContent(createObjectTypeSelectionPanel(modalSelectDialog.getContentId(), model));
				modalSelectDialog.setTitle("Select the Object Type.");
				modalSelectDialog.setCookieName("selectObjectType");
				
				modalSelectDialog.setCloseButtonCallback(new ModalWindow.CloseButtonCallback()
				{
				    public boolean onCloseButtonClicked(AjaxRequestTarget target)
				    {
				        //setResult("Modal window 2 - close button");
				        return true;
				    }
				});
				modalSelectDialog.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
				{
				    public void onClose(AjaxRequestTarget target)
				    {
				        //target.addComponent(result);
				    }
				});
				AjaxLink testButton = new IndicatingAjaxLink("testButton")
				{
					@Override
					public void onClick(AjaxRequestTarget target)
					{
						modalSelectDialog.show(target);
					}	
				};
				add(testButton);				
				/* */
				
				
				// Attributes				
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
				// hide components
				add(new TextField("cvoc_term").setVisible(false));
				add(new Panel("taxonSelectPanel").setVisible(false));				
				
				// Attributes
				add(new TextField("cvoc_normalisedname").setVisible(false));
				add(new TextField("cvoc_standarddictionaryId").setVisible(false));
				add(new TextField("cvoc_standardname").setVisible(false));				
				add(new TextField("cvoc_langname").setVisible(false));				
			}			
		}
		
		/**
		 * Create the object type Panel (with the large multi-lingual term selection table)
		 * 
		 * @param model
		 * @return
		 */
		private Panel createObjectTypeSelectionPanel(String id, IModel model)
		{
			logger.debug("creating the taxon panel");
			
			Panel panel = new TaxonSelectPanel(id, model)
			{
				private static final long	serialVersionUID	= 1L;

				@Override
				protected void onSelectionChanged(AjaxRequestTarget target)
				{
					if (target != null)
	                {
	                    //target.addComponent(autocompleteSelector);
	                    // FIX	workaround visural-wicket ISSUE 67 by updating the container
	                    target.addComponent(termContainer);
	                }
				}
			};
			
			return panel;
		}		
	}
}

