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

import nl.knaw.dans.dccd.model.EntityAttribute;
import nl.knaw.dans.dccd.model.UIMapEntry;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * with a list of EntityAttributePanel's
 * 
 * @author paulboon
 */
public class EntityAttributeOptionalPanel extends EntityAttributePanel 
{
	private static final long	serialVersionUID	= -3814678864667904777L;
	private static Logger logger = Logger.getLogger(EntityAttributeOptionalPanel.class);
	private Class<?> panelClass;
	
	public EntityAttributeOptionalPanel(Class<?> panelClass, String id, IModel model) 
	{
		super(id, model, false);
		this.panelClass = panelClass;
	}

	public EntityAttributeOptionalPanel(Class<?> panelClass, String id, IModel model, boolean editable) 
	{
		super(id, model, editable);
		//super(id, model, false);// disable editing
		this.panelClass = panelClass;
	}

	public Class<?> getPanelClass()
	{
		return panelClass;
	}
	
	public class EntityAttributeOptionalPanelView extends Panel 
	{
		private static final long	serialVersionUID	= 1281065339182599861L;

		public EntityAttributeOptionalPanelView(String id, IModel model) {
			super(id, model);

			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();

			Object entryObject = attr.getEntryObject();
			if (entryObject == null)
			{
				logger.debug("given entry object is null");
			}
			
			// TEST always place panel, even if object is null
			// construct the Panel and add it

			//Note: panelClass is not set until construction is done!
			//logger.debug("Optional panel with class name: " + panelClass.getSimpleName());
			
			EntityAtributePanelFactory factory = new EntityAtributePanelFactory();
			// Note, if we use createPanel this OptionalPanel will get an Optional Panel, which will get an Optional Panel ad infinitum
			Panel attrPanel = factory.createSinglePanel("attribute_panel", attr, isEditable());

			add(attrPanel);	
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
	
	public class EntityAttributeOptionalPanelEdit extends Panel 
	{
		private static final long	serialVersionUID	= 1901387700459971220L;
		private final List             listItems;

		public EntityAttributeOptionalPanelEdit(String id, IModel model) 
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
				private static final long	serialVersionUID	= 8546947431099866424L;

				@Override
	            protected void onAfterRender()
	            {
	                //logger.debug("after render of container");
	                //clearItemMessages();
	                super.onAfterRender();
	            }
	        };

	        final AjaxLink firstPlusLink = new AjaxLink("firstPlusLink") {
				private static final long	serialVersionUID	= -354920997286081681L;
			
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
				private static final long	serialVersionUID	= -5847578207280229838L;

				@Override
			    protected void populateItem(final ListItem item) {
					// construct the Panel and add it
					logger.debug("Repeating panel with class name: " + panelClass.getSimpleName());
					EntityAttribute attr = new EntityAttribute(item, new UIMapEntry("bogus", "modelObject", panelClass.getSimpleName()));
					EntityAtributePanelFactory factory = new EntityAtributePanelFactory();
					// Note, if we use createPanel this OptionalPanel will get an Optional Panel, which will get an Optional Panel ad infinitum
					Panel itemPanel = factory.createSinglePanel("attribute_panel", attr, isEditable());
					
					item.add(itemPanel);	
					
					//create a holder of buttons minus and plus
					//display it when in edit mode.
					final WebMarkupContainer buttonsHolder = new WebMarkupContainer("buttonsHolder");
					if (isInEditMode()) 
					{
						// removing is a confirmed action
						RemoveAttributeButtonPanel removeButton = 
							new RemoveAttributeButtonPanel("removeButton", "", getString("removeConfirmMessage")) 
						{
							private static final long	serialVersionUID	= 8015235145226224965L;

							@Override
							protected void onConfirm(AjaxRequestTarget target)
							{
								logger.debug("onSubmit removeButton: removedItemIndex="
										+ item.getIndex());
								
								handleMinusButtonClicked(item, target, null);
								firstPlusLink.setVisible(getList().isEmpty());
								target.addComponent(listViewContainer);
							}
							@Override
							protected void onCancel(AjaxRequestTarget target) { } 
						};
						// allow removal of last item
						removeButton.setVisible(listItems.size() > 0);
						buttonsHolder.add(removeButton);
						
						/*						
						AjaxLink minusLink = new AjaxLink("minusLink") 
						{
							private static final long	serialVersionUID	= 8681145278868526967L;
						
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
						*/						
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
	    	Class<?> newPanelClass = getPanelClass();
	    	Object object = factory.createEmptyObject(newPanelClass);	    	
	    	listItems.add(object);
	    	
	    	// add in data model 
			EntityAttribute attr = (EntityAttribute) EntityAttributeOptionalPanelEdit.this.getDefaultModelObject();
			attr.setEntryObject(object);
	    }
	    
	    protected void handleMinusButtonClicked(ListItem item, AjaxRequestTarget target, Form form)
	    {  	
	    	listItems.remove(item.getIndex());

	    	// delete in data model   	
	    	EntityAttribute attr = (EntityAttribute) EntityAttributeOptionalPanelEdit.this.getDefaultModelObject();
	    	attr.deleteEntryObject();
	    }	
	}
}
