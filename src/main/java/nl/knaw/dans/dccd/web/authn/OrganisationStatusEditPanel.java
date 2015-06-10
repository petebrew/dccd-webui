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

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author paulboon
 */
public class OrganisationStatusEditPanel extends Panel {
	private static final long serialVersionUID = 8790748301243987413L;
	private static Logger logger = LoggerFactory.getLogger(OrganisationStatusEditPanel.class);

	private DccdOrganisation organisation;

	public OrganisationStatusEditPanel(String id, DccdOrganisation organisation) {
		super(id);
		this.organisation = organisation;

		init();
	}

	public void init() {
		// Display the current user state
        Label stateLabel = new Label("state",
                new StringResourceModel("organisation.states.${state}", this, new Model(organisation)));

        add(stateLabel);

        // Add components to change state

        // REFACTOR NOTE:	 only one submitLink per page 
        // Acivate
        SubmitLink organisationActivate = new SubmitLink("organisationActivate") {
			private static final long serialVersionUID = 1L;

			public void onSubmit()
			{
                logger.debug("organisationActivate.onSubmit executed");
                organisation.setState(DccdOrganisation.State.ACTIVE);
            }
        };
        add(organisationActivate);
        // only show when not active and not blocked (for blocked we have Restore)
        organisationActivate.setVisible((organisation.getState() != DccdOrganisation.State.ACTIVE) &&
        						(organisation.getState() != DccdOrganisation.State.BLOCKED));

        // Delete
        SubmitLink organisationDelete = new SubmitLink("organisationDelete") {
			private static final long serialVersionUID = 1L;

			public void onSubmit()
			{
            	logger.debug("organisationDelete.onSubmit executed");

            	// TODO Check:
            	// only allow if there are no members and no projects

            	organisation.setState(DccdOrganisation.State.BLOCKED);
            }
        };
        add(organisationDelete);
        // only show when not blocked
        organisationDelete.setVisible(organisation.getState() != DccdOrganisation.State.BLOCKED);
        
        // Restore
        SubmitLink organisationRestore = new SubmitLink("organisationRestore") {
			private static final long serialVersionUID = 1L;

			public void onSubmit() {
            	logger.debug("organisationRestore.onSubmit executed");

            	// For now, always return to Candidate, which must be reactivated.
            	organisation.setState(DccdOrganisation.State.REGISTERED);
            }
        };
        add(organisationRestore);
        // only show when blocked
        organisationRestore.setVisible(organisation.getState() == DccdOrganisation.State.BLOCKED);

	}
}


