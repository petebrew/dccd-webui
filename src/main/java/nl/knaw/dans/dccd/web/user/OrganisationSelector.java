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
import java.util.Map;
import java.util.Map.Entry;

import nl.knaw.dans.dccd.application.services.DccdUserService;
import nl.knaw.dans.dccd.application.services.UserServiceException;
import nl.knaw.dans.dccd.model.DccdOrganisation;
import nl.knaw.dans.dccd.model.DccdUser;
import nl.knaw.dans.dccd.web.DccdSession;
import nl.knaw.dans.dccd.web.error.ErrorPage;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AbstractAutoCompleteTextRenderer;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrganisationSelector extends AutoCompleteTextField
{
 	private static final long serialVersionUID = 3797498659017039856L;
	private static final Logger logger = LoggerFactory.getLogger(OrganisationSelector.class);

	public OrganisationSelector(String wicketId)
	{
		//this(wicketId, new IdModel());
		this(wicketId, new OrganisationModel());
	}

	public OrganisationSelector(String wicketId, IModel model)
	{
		super(wicketId, model, new AbstractAutoCompleteTextRenderer()
        {
            private static final long serialVersionUID = -3654758614039672040L;

            @SuppressWarnings("unchecked")
			@Override
            protected String getTextValue(Object obj)
            {
            	return ((DccdOrganisation)obj).getId();
            	
                //Map.Entry<String, String> entry = (Entry<String, String>) obj;
                //String value = entry.getValue().replaceAll(":", "");
                //String id = entry.getKey();
                //return  value + " : " + id;
            }
        });
	}

    @SuppressWarnings("unchecked")
	@Override
    protected Iterator getChoices(String text)
    {
		 logger.debug("getChoices called");
		 // text has changed and we need to know if there is some matching
		 // Wish:
		 // we now could call an onTextChanged member, so others can override that and handle the change
		 // for instance disable other controls when the selection here is invalid
		 // but we have no AJAX target...

        Iterator iterator = null;
        try
        {
        	// just all active for now....
        	//List<DccdOrganisation> activeOrganisations = DccdUserService.getService().getActiveOrganisations();
        	// TODO use a findOrganisationsByName
        	List<DccdOrganisation> activeOrganisations = DccdUserService.getService().findOrganisationsByStub(text, 10L);
        	
            iterator = activeOrganisations.iterator();
        }
        catch (UserServiceException e)
        {
        	logger.error("Exception while retrieving common names: ", e);
            error("Internal error");
            throw new RestartResponseException(ErrorPage.class);
        }
        return iterator;
    }
 
}
