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

import java.util.Map;

import nl.knaw.dans.dccd.application.services.DccdUserService;
import nl.knaw.dans.dccd.application.services.UserServiceException;
import nl.knaw.dans.dccd.model.DccdUser;
import nl.knaw.dans.dccd.model.DccdUser.Role;
import nl.knaw.dans.dccd.web.DccdSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dev
 */
public class SelectManagerPanel extends SelectUserPanel
{
	private static final Logger logger = LoggerFactory.getLogger(SelectManagerPanel.class);
	private static final long	serialVersionUID	= 2516936073847706337L;
	private String organisationId = "";
	private boolean userIsAdmin = false;
	
	public SelectManagerPanel(String wicketId)
	{
		super(wicketId);

		DccdUser user = (DccdUser)((DccdSession) getSession()).getUser();
		if (user != null)
		{
			organisationId = user.getOrganization();
			userIsAdmin = user.hasRole(Role.ADMIN);
		}	
		else
		{
			logger.warn("No user logged in");
		}
	}

	@Override
	protected UserSelector getUserSelector()
    {
    	return new UserSelector("autoCompleteTextField", model)
    	{
			private static final long	serialVersionUID	= 1L;
			
			@Override
			protected Map<String, String> findUsers(String text) throws UserServiceException
		    {
				if (userIsAdmin)
					return DccdUserService.getService().findByCommonNameStub(text, 10L);
				else
					return DccdUserService.getService().findActiveInOrganisationByCommonNameStub(text, organisationId, 10L);	
		    }
    	};
    }
}

