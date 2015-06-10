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
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IComponentAssignedModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.tridas.schema.ControlledVoc;

import com.visural.wicket.component.dropdown.DropDown;
import com.visural.wicket.component.dropdown.DropDownDataSource;

/**
 * Started as a copy of the ControlledVocabularyPanel, 
 * but this one is tailored to Object.Type
 * 
 * TODO generalize it for all ControlledVocabularies 
 * that work with multilingual terms.
 *  
 * @author dev
 */
public class ObjectTypePanel extends EntityAttributePanel 
{
	private static final long	serialVersionUID	= 818465661335658446L;
	private static Logger logger = Logger.getLogger(ObjectTypePanel.class);

	public ObjectTypePanel(String id, IModel model) {
		super(id, model, false);
	}

	public ObjectTypePanel(String id, IModel model, final boolean editable) {
		super(id, model, editable);
		//super(id, model, false); // only allow view
	}

	public class ObjectTypePanelView extends Panel {
		private static final long	serialVersionUID	= -3776312641504921324L;

		public ObjectTypePanelView(String id, IModel model) {
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

	public class ObjectTypePanelEdit extends Panel {
		private static final long	serialVersionUID	= 6724115041863341448L;

		//private Panel objectTypeSelectionPanel = null;
		//private boolean objectTypeSelectionPanelCreated = false;
		//private AjaxLink displayTableButton = null;
		//private boolean displayTable = false; // hide initially
		//private TridasVocabularyAutoCompleteSelector autocompleteSelector = null;

		// For the ComboBox			
		private DropDownDataSource listDS;
		private DropDown autocompleteSelector = null;
		private WebMarkupContainer termContainer = null;
		
		//public String getDisplayTableButtonText()
		//{
		//	return displayTable ? "Hide table" : "Show table"; // TODO get from resource
		//}		
		
		public ObjectTypePanelEdit(String id, final IModel model) {
			super(id, model);
						
			// now do the edit stuff
			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();
			// assume object is a ControlledVoc
			//ControlledVoc voc = (ControlledVoc)model.getObject();
			ControlledVoc cvoc = (ControlledVoc)attr.getEntryObject();
			
			if(cvoc != null)
			{
				/*
				// For the in-place dialog
				displayTableButton = new IndicatingAjaxLink("show")
				{
					private static final long	serialVersionUID	= 1L;

					@Override
					public void onClick(AjaxRequestTarget target)
					{		
						// only create the first time
						if (objectTypeSelectionPanelCreated == false)
						{
							// replace the 'empty' panel with a real one
							Panel newPanel = createObjectTypeSelectionPanel("select", model);
							newPanel.setOutputMarkupId(true);
							objectTypeSelectionPanel.replaceWith(newPanel);
							objectTypeSelectionPanel = newPanel;
						}
						
						displayTable = !displayTable; // toggle
						logger.debug("display = " + displayTable);
						
						target.addComponent(displayTableButton);
						target.addComponent(objectTypeSelectionPanel);
					}
				};
				add(displayTableButton);
				displayTableButton.add(new Label("displayButtonText", new PropertyModel(this, "displayTableButtonText")));
				displayTableButton.setOutputMarkupId(true);

				// An autocomplete!
				// and have a dialog (pop-up) for selecting a term from a list;
				autocompleteSelector = new TridasVocabularyAutoCompleteSelector("cvoc_term", 
									new PropertyModel(cvoc, "value"),
									"object.type");
				add(autocompleteSelector);
				autocompleteSelector.setOutputMarkupId(true);
				
				
				// initially have an empty panel, with a markup id and visible = rendered
				objectTypeSelectionPanel = new EmptyPanel("select");
				objectTypeSelectionPanel.setOutputMarkupId(true);
				add(objectTypeSelectionPanel);
				*/

				/* Using ComboBox */
				// ComboBox list data
				// could include jQuery this for all pages... in the WicketApplication
				// addRenderHeadListener(JavascriptPackageResource.getHeaderContribution(new JQueryResourceReference()));
				add(JavascriptPackageResource.getHeaderContribution(new ResourceReference(EasyUpload.class, "js/lib/jquery-1.3.2.min.js")));
				listDS = new DropDownDataSource<String>() {
					private static final long	serialVersionUID	= 1L;
					public String getName() {
				        return "object.type.list";
				    }
				    public List<String> getValues() {
				    	//return DccdVocabularyService.getService().getTerms("object.type");
				    	// Use the project language
				    	String langCode = ((DccdSession) getSession()).getContentLanguageCode();
						return DccdVocabularyService.getService().getTerms("object.type", langCode);
				    }
				    public String getDescriptionForValue(String t) {
				        return t;
				    }
				};
				// ComboBox
				autocompleteSelector = new DropDown("cvoc_term", new PropertyModel(cvoc, "value"), listDS, false);
				autocompleteSelector.setCharacterWidth(30);
				autocompleteSelector.setOutputMarkupId(true);
				add(autocompleteSelector);
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
				// empty panel
				add(new TextField("cvoc_term").setVisible(false));
				
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
			logger.debug("creating the type panel");
			
			Panel panel = new ObjectTypeSelectionPanel(id, model)
			{
				private static final long	serialVersionUID	= 5748866024858050908L;

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
			/*
			// For the in-place dialog
			//
			// The display style is connected to the display field with a model
			// Note: When toggling visibility is done with 
			// an external JS library we don't need this anymore
			panel.add(new AttributeModifier("style", true,
				new AbstractReadOnlyModel()
				{
					private static final long	serialVersionUID	= 1L;
			
					@Override
					public Object getObject()
					{
					      return displayTable ? "" : "display:none";
					}
				}));
			
			objectTypeSelectionPanelCreated = true;
			*/
			return panel;
		}
	}
}

