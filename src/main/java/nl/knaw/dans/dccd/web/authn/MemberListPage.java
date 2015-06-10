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
package nl.knaw.dans.dccd.web.authn;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nl.knaw.dans.common.lang.user.User;
import nl.knaw.dans.dccd.application.services.DccdUserService;
import nl.knaw.dans.dccd.application.services.UserServiceException;
import nl.knaw.dans.dccd.model.DccdUser;
import nl.knaw.dans.dccd.web.DccdSession;
import nl.knaw.dans.dccd.web.base.BasePage;
import nl.knaw.dans.dccd.web.error.ErrorPage;

import org.apache.log4j.Logger;
import org.apache.wicket.RestartResponseException;

/**
 * @author paulboon
 */
public class MemberListPage extends BasePage {
	private static Logger logger = Logger.getLogger(MemberListPage.class);

	private DccdUser userLoggedIn = (DccdUser)((DccdSession) getSession()).getUser();
	private boolean admin = (userLoggedIn != null && userLoggedIn.hasRole(DccdUser.Role.ADMIN));
	// registered users are 'members'
	private List<DccdUser> users;

	private MemberListPanel panel;

	public MemberListPage() {
		super();
		init();
	}

	@Override
	public void refresh() {
		userLoggedIn = (DccdUser)((DccdSession) getSession()).getUser();
		admin = (userLoggedIn != null && userLoggedIn.hasRole(DccdUser.Role.ADMIN));
		users = retrieveUsers();
		MemberListPanel newPanel = panel = new MemberListPanel("memberListPanel", users);
		addOrReplace(newPanel);
		panel = newPanel;
	}

	private void init() {
		redirectIfNotLoggedIn();
		
		users = retrieveUsers();
		panel = new MemberListPanel("memberListPanel", users);
		add(panel);
	}

	public boolean isAdmin() {
		return admin;
	}

	private List<DccdUser> retrieveUsers() {
		List<DccdUser> users = new ArrayList<DccdUser>();

		try {
			users = DccdUserService.getService().getAllUsers();
			if (!isAdmin()) {
				// remove non-active
				users = removeNonActive(users);
			}
		} catch (UserServiceException e) {
			logger.error("Failed to retrieve members", e);
			getSession().error("Failed to retrieve members"); // use resource?
			// go to the error page!
			throw new RestartResponseException(ErrorPage.class);
		}

		return users;
	}

	private List<DccdUser> removeNonActive(List<DccdUser> users) {
		// filter = remove non-active users
		for (Iterator<DccdUser> it = users.iterator(); it.hasNext(); )
		{
			DccdUser user = it.next();
	        if (user.getState() != User.State.ACTIVE)
	        {
	            it.remove();
	        }
		}

		return users;
	}
}

