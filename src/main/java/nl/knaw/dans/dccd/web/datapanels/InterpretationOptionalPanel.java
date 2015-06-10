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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.dccd.model.EntityAttribute;
import nl.knaw.dans.dccd.model.UIMapEntry;
import nl.knaw.dans.dccd.tridas.EmptyObjectFactory;
import nl.knaw.dans.dccd.web.datapanels.EntityAttributeOptionalPanel.EntityAttributeOptionalPanelEdit;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.tridas.interfaces.ITridasSeries;
import org.tridas.schema.TridasInterpretation;
import org.tridas.schema.TridasInterpretationUnsolved;

/**
 * @author dev
 */
public class InterpretationOptionalPanel extends EntityAttributePanel
{
	private static final long	serialVersionUID	= 2161645369235111874L;
	private static Logger logger = Logger.getLogger(InterpretationOptionalPanel.class);

	public InterpretationOptionalPanel(String id, IModel model)
	{
		super(id, model, false);
	}

	public InterpretationOptionalPanel(String id, IModel model, boolean editable) 
	{
		super(id, model, editable); 
		//super(id, model, false); // disable editing
	}
	
	public class InterpretationOptionalPanelView extends Panel 
	{
		private static final long	serialVersionUID	= 1096926992442163161L;

		public InterpretationOptionalPanelView(String id, IModel model) 
		{
			super(id, model);

			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();

			// assume TridasInterpretation
			TridasInterpretation interp = (TridasInterpretation)attr.getEntryObject();
			
			// place either interpretation or unsolved
			if (interp != null)
			{
				add(new InterpretationPanel("interp", model));
				add(new Label("interpUnsolved").setVisible(false));
			}
			else
			{
				//add(new InterpretationUnsolvedPanel("interp", model));
				add(new Panel("interp").setVisible(false));
				add(new Label("interpUnsolved", getString("name_Unsolved")));
			}
		}
	}
	
	/** FAKE a List
	 * 
	 * Refactoring Note, we could do without the list and switch Panels
	 * 
	 * @return The list
	 */
	@SuppressWarnings("unchecked")
	private List getList() 
	{
		EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();
		Object entryObject = attr.getEntryObject();
		
		List list = new ArrayList<Object>();// empty;
		if (entryObject != null)
		{
			// put the one and only in the list
			list.add(entryObject);
		}
		
		return list;
	}
	/*
	 * Note it was not possible to use a derive from EntityAttributeOptionalPanel and use tha, 
	 * instead we have to implement the same optional 'behavior' here.
	 */
	public class InterpretationOptionalPanelEdit extends Panel 
	{
		private static final long	serialVersionUID	= -7723004696858857276L;
		private final List             listItems;

		public InterpretationOptionalPanelEdit(String id, IModel model) 
		{
			super(id, model);

			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();

			Object entryObject = attr.getEntryObject();
			if (entryObject == null)
			{
				logger.debug("given entry object is null");
			}
			
	        final WebMarkupContainer listViewContainer = new WebMarkupContainer("listViewContainer")
	        {
				private static final long	serialVersionUID	= 5709951758206763629L;

				@Override
	            protected void onAfterRender()
	            {
	                //logger.debug("after render of container");
	                //clearItemMessages();
	                super.onAfterRender();
	            }
	        };

	        final AjaxLink firstPlusLink = new AjaxLink("firstPlusLink") {
				private static final long	serialVersionUID	= -4650296224060471779L;

				@Override
				public void onClick(AjaxRequestTarget target)
				{
					logger.debug("onSubmit firstPlusLink");
					handlePlusButtonClicked(target, null);
					logger.debug("addComponent");
					this.setVisible(false);	// never empty, just added first item	
					
					target.addComponent(listViewContainer);		
				}
			};

			firstPlusLink.setVisible(getList().isEmpty());
			listViewContainer.add(firstPlusLink.setOutputMarkupId(true));
      
			listItems = getList();
		    final ListView listView = new ListView("attribute", listItems) 
		    {		
				private static final long	serialVersionUID	= -7257117381599083301L;

				@Override
			    protected void populateItem(final ListItem item) {
					// construct the Panel and add it
					logger.debug("Repeating panel with class name: " + InterpretationPanel.class.getSimpleName());
					EntityAttribute attr = new EntityAttribute(item, new UIMapEntry("bogus", "modelObject", InterpretationPanel.class.getSimpleName()));
					EntityAtributePanelFactory factory = new EntityAtributePanelFactory();
					// Note, if we use createPanel this OptionalPanel will get an Optional Panel, which will get an Optional Panel ad infinitum
					Panel itemPanel = factory.createSinglePanel("attribute_panel", attr, isEditable());
					
					item.add(itemPanel);	
					
					//create a holder of buttons minus and plus
					//display it when in edit mode.
					final WebMarkupContainer buttonsHolder = new WebMarkupContainer("buttonsHolder");
					if (isInEditMode()) 
					{
						AjaxLink minusLink = new AjaxLink("minusLink") 
						{
							private static final long	serialVersionUID	= 8680209471183541081L;

							@Override
							public void onClick(final AjaxRequestTarget target) {
								logger.debug("onSubmit minusLink. removedItemIndex="
												+ item.getIndex());
								handleMinusButtonClicked(item, target, null);
								
								firstPlusLink.setVisible(getList().isEmpty());	    	
								
								target.addComponent(listViewContainer);
							}
						};
						//minusLink.setVisible(isRepeating() && listItems.size() != 1);
						// allow removal of last item						
						minusLink.setVisible(listItems.size() > 0);
						buttonsHolder.add(minusLink);
						
						// no plus here! 
					} 
					else 
					{
						//hidden when in non editable mode.
						buttonsHolder.setVisible(false);
					}
					item.add(buttonsHolder);
				}
			};
	        listViewContainer.add(listView);
	        add(listViewContainer.setOutputMarkupId(true));
		}
		
		private boolean isInEditMode() {return true;}
		
	    protected void handlePlusButtonClicked(AjaxRequestTarget target, Form form)
	    {
	    	// create an empty item for this panel
	    	EntityAtributePanelFactory factory = new EntityAtributePanelFactory();	 
	    	Class<?> newPanelClass = InterpretationPanel.class;
	    	Object object = factory.createEmptyObject(newPanelClass);	    	
	    	listItems.add(object);
	    	
	    	// add in data model 
			EntityAttribute attr = (EntityAttribute) InterpretationOptionalPanelEdit.this.getDefaultModelObject();
			attr.setEntryObject(object);
			
			Object entityObject = attr.getObject();
			removeUnsolved(entityObject);
	    }
	    
	    protected void handleMinusButtonClicked(ListItem item, AjaxRequestTarget target, Form form)
	    {  	
	    	listItems.remove(item.getIndex());

	    	// delete in data model   	
	    	EntityAttribute attr = (EntityAttribute) InterpretationOptionalPanelEdit.this.getDefaultModelObject();
	    	attr.deleteEntryObject();
	    	
			Object entityObject = attr.getObject();
	    	addUnsolved(entityObject);
	    }
	    
	    private void addUnsolved(Object object)
	    {
	    	if (object instanceof ITridasSeries)
	    	{
		    	ITridasSeries series = (ITridasSeries)object;
		    	series.setInterpretationUnsolved((TridasInterpretationUnsolved) EmptyObjectFactory.create(TridasInterpretationUnsolved.class));
	    	}
	    	else
	    	{
	    		logger.error("could not add TridasInterpretationUnsolved to " + object.getClass().getName());
	    	}
	    }
	    
	    private void removeUnsolved(Object object)
	    {
	    	if (object instanceof ITridasSeries)
	    	{
	    		ITridasSeries series = (ITridasSeries)object;
	    		series.setInterpretationUnsolved((TridasInterpretationUnsolved)null);
	    	}
	    	else
	    	{
	    		logger.error("could not remove TridasInterpretationUnsolved from " + object.getClass().getName());
	    	}
	    }
	}
}

