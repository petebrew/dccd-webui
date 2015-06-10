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

import nl.knaw.dans.dccd.model.DccdOrganisation;
import nl.knaw.dans.dccd.model.DccdOrganisationImpl;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 * @author paulboon
 */
public class OrganisationDisplayPanel extends Panel {
	private static final long serialVersionUID = 5461957909322574282L;

	//private static Logger logger = LoggerFactory.getLogger(OrganisationDisplayPanel.class);

	private final DccdOrganisation organisation;

	public OrganisationDisplayPanel(String id, DccdOrganisation organisation) {
		super(id);
		this.organisation = organisation;
		init();
	}

	// Maybe creator should provide a new empty organisation
	// and this constructor can be removed?
	public OrganisationDisplayPanel(String id) {
		super(id);
		// create a new and empty organisation
		this.organisation = new DccdOrganisationImpl();
		init();
	}

	public void init() {
        super.setDefaultModel(new CompoundPropertyModel(organisation));

        add(new Label("id"));
        add(new Label(UserProperties.ADDRESS));
        add(new Label(UserProperties.POSTALCODE));
        add(new Label(UserProperties.CITY));
        add(new Label(UserProperties.COUNTRY));
	}
}

