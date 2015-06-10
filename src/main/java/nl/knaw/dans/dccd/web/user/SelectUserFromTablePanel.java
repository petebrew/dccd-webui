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
package nl.knaw.dans.dccd.web.user;

import java.util.Iterator;
import java.util.List;

import nl.knaw.dans.dccd.application.services.DccdUserService;
import nl.knaw.dans.dccd.application.services.UserServiceException;
import nl.knaw.dans.dccd.model.DccdUser;
import nl.knaw.dans.dccd.web.DccdSession;
import nl.knaw.dans.dccd.web.error.ErrorPage;

import org.apache.log4j.Logger;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * @author dev
 */
public class SelectUserFromTablePanel extends Panel
{
	private static final long	serialVersionUID	= 3582496329837618205L;
	private static Logger logger = Logger.getLogger(SelectUserFromTablePanel.class);
	private String selectedUserId = "";

	public SelectUserFromTablePanel(String id, IModel<?> model)
	{
		super(id, model);
		
		init();
	}

	private void init()
	{
		List<DccdUser> selectableUsers = null;
		try
		{
			// Note: when we do it pageable, it seems better to get only the id's 
			// and then construct the users we want to show on a page
			
			// only active ... selectable (permission assignable) users
			selectableUsers = DccdUserService.getService().getActiveNormalUsers();
			
			// remove current user, might take some time if the list is long
			DccdUser user = (DccdUser)((DccdSession) getSession()).getUser();
			String currentUserId = user.getId();
			Iterator<DccdUser> iterator = selectableUsers.iterator();
			while(iterator.hasNext())
			{
				DccdUser selectableUser = (DccdUser)iterator.next();
				if(selectableUser.getId().compareTo(currentUserId) == 0)
				{
					iterator.remove();
					break; // done, because only once in the list
				}
			}
		}
		catch (UserServiceException e)
		{
        	logger.error("Exception while retrieving users: ", e);
            error("Internal error");
            throw new RestartResponseException(ErrorPage.class);
		}
		
		// show table with all active DCCD users
		// when we have a lot of users, this should become pagable! 
		ListView listview = new ListView("listview", selectableUsers) 
		{
			private static final long	serialVersionUID	= -2830791437271425906L;

			protected void populateItem(ListItem item) 
		    {
		    	final DccdUser user = (DccdUser) item.getModelObject();
				AjaxLink link = new AjaxLink("link")
				{
					private static final long	serialVersionUID	= 1L;
					@Override
					public void onClick(AjaxRequestTarget target)
					{
						selectedUserId = user.getId();
						logger.debug("selected user id: " + selectedUserId);
						
						// update
						SelectUserFromTablePanel.this.setDefaultModelObject(user);
						onSelectionChanged(target);
					}
				};
				item.add(link);
				item.add(new Label("surname", user.getSurname()));
		        item.add(new Label("displayname", user.getDisplayName()));
		        item.add(new Label("organisation", user.getOrganization()));
		    }
		};
		add(listview);
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
