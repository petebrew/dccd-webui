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

import nl.knaw.dans.dccd.common.web.confirm.ConfirmedActionButtonPanel;
import nl.knaw.dans.dccd.model.EntityAttribute;
import nl.knaw.dans.dccd.model.UIMapEntry;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
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
public class EntityAttributeRepeaterPanel extends EntityAttributePanel {
	private static final long	serialVersionUID	= -3814678864667904777L;
	private static Logger logger = Logger.getLogger(EntityAttributeRepeaterPanel.class);
	private Class<?> panelClass;
	
	public EntityAttributeRepeaterPanel(Class<?> panelClass, String id, IModel model) {
		super(id, model, false);
		this.panelClass = panelClass;
	}

	public EntityAttributeRepeaterPanel(Class<?> panelClass, String id, IModel model, boolean editable) {
		super(id, model, editable);
		//super(id, model, false);// disable editing
		this.panelClass = panelClass;
	}

	public Class<?> getPanelClass()
	{
		return panelClass;
	}
	
	/** Retrieve the list of things
	 *  we want to place on the ListView.
	 *  Uses the information in the DccdAttr
	 *
	 * Candidate for super class
	 *
	 * @return The list
	 */
	@SuppressWarnings("unchecked")
	private List getList() {
		EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();

		// assume result of the method invocation is list
		List list = (List)attr.getEntryObject();
		if (list == null)
		{
			logger.warn("given entry object (list) is null");
		}
		return list;
	}

	public class EntityAttributeRepeaterPanelView extends Panel {
		private static final long	serialVersionUID	= -2818256304029642450L;

		public EntityAttributeRepeaterPanelView(String id, IModel model) {
			super(id, model);

			add(new ListView("attribute", getList()) {
				private static final long	serialVersionUID	= -9070263400311143969L;

				@Override
			    protected void populateItem(ListItem item) {
					// construct the Panel and add it
					logger.debug("Repeating panel with class name: " + panelClass.getSimpleName());
					EntityAttribute attr = new EntityAttribute(item, new UIMapEntry("bogus", "modelObject", panelClass.getSimpleName()));
					EntityAtributePanelFactory factory = new EntityAtributePanelFactory();
					Panel itemPanel = factory.createPanel("attribute_panel", attr, isEditable());
					item.add(itemPanel);	
				}
			});		
		}
	}

	public class EntityAttributeRepeaterPanelEdit extends Panel {
		private static final long	serialVersionUID	= 9117016051657329845L;
		private final List             listItems;

		public EntityAttributeRepeaterPanelEdit(String id, IModel model) {
			super(id, model);

	        final WebMarkupContainer listViewContainer = new WebMarkupContainer("listViewContainer")
	        {
				private static final long	serialVersionUID	= 5141456520302801679L;

				@Override
	            protected void onAfterRender()
	            {
	                //logger.debug("after render of listViewContainer");
	                //clearItemMessages();
	                super.onAfterRender();
	            }
	        };

			// add (initial item) 'plus' link for empty list
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
				private static final long	serialVersionUID	= 3205435528701573003L;

				@Override
			    protected void populateItem(final ListItem item) {
					// construct the Panel and add it
					logger.debug("Repeating panel with class name: " + panelClass.getSimpleName());
					EntityAttribute attr = new EntityAttribute(item, new UIMapEntry("bogus", "modelObject", panelClass.getSimpleName()));
					EntityAtributePanelFactory factory = new EntityAtributePanelFactory();
					Panel itemPanel = factory.createPanel("attribute_panel", attr, isEditable());
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
							private static final long	serialVersionUID	= 6211630345643912857L;

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
						removeButton.setVisible(isRepeating() && listItems.size() > 0);
						buttonsHolder.add(removeButton);

						/*						
						// create and add the minus link.
						AjaxLink minusLink = new AjaxLink("minusLink") 
						{
							private static final long	serialVersionUID	= 8141319165262771236L;
						
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
						minusLink.setVisible(isRepeating() && listItems.size() > 0);
						buttonsHolder.add(minusLink);
						*/
						
						// create and add the plus link.
						AjaxLink plusLink = new AjaxLink("plusLink") 
						{
							private static final long	serialVersionUID	= 8622824440464500843L;
						
							@Override
							public void onClick(final AjaxRequestTarget target) {
								logger.debug("onSubmit plusLink");
								handlePlusButtonClicked(target, null);
								logger.debug("addComponent");
								target.addComponent(listViewContainer);
							}
						};
						
						plusLink.setVisible(isRepeating() && item.getIndex() == listItems.size() - 1);
						buttonsHolder.add(plusLink);
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
		
		private boolean isRepeating() {return true;}
		
		private boolean isInEditMode() {return true;}
		
	    protected void handlePlusButtonClicked(AjaxRequestTarget target, Form form)
	    {
	    	// create an empty item for this panel
	    	EntityAtributePanelFactory factory = new EntityAtributePanelFactory();	 
	    	Class<?> newPanelClass = getPanelClass();
	    	Object object = factory.createEmptyObject(newPanelClass);	    	
	    	listItems.add(object);
	    	
	    	// Note that this creation is delegated to the wrapper in EOF
	    	logger.debug("item class: " + listItems.get(0).getClass().getCanonicalName());
	    }
	    
	    protected void handleMinusButtonClicked(ListItem item, AjaxRequestTarget target, Form form)
	    {
	    	// TODO ask for confirmation!
	    	
	    	
	    	listItems.remove(item.getIndex());
	    }	
	}
}

