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

import nl.knaw.dans.common.web.template.AbstractCommonStatelessForm;
import nl.knaw.dans.common.web.template.AbstractCommonStatelessPanel;
import nl.knaw.dans.dccd.model.DccdOrganisation;

import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.wicket.validation.validator.PatternValidator;

/**
 * @author paulboon
 */
public class OrganisationEditPanel extends AbstractCommonStatelessPanel {
	private static final long serialVersionUID = 6218697923744512617L;

	private static Logger logger = LoggerFactory.getLogger(OrganisationEditPanel.class);

	private final DccdOrganisation organisation;

	//private AbstractEasyForm form;

	public OrganisationEditPanel(String id, DccdOrganisation organisation) { //, AbstractEasyForm parentForm) {
		super(id);
		this.organisation = organisation;
		//this.form = form;
		init();
	}

	public void init() {
        super.setDefaultModel(new CompoundPropertyModel(organisation));

        // could try without a form!!!!!!!!!!!!!
        // assume we are already inside a Form!
        // the RegistrationForm 'isa' AbstractEasyStatelessForm
        //
        //AbstractEasyForm.addWithComponentFeedback(form, new RequiredTextField("id"), new ResourceModel("organisation.id"));
        //AbstractEasyForm.addWithComponentFeedback(form, new TextField(UserProperties.ADDRESS), new ResourceModel("organisation.address"));
        //AbstractEasyForm.addWithComponentFeedback(form, new TextField(UserProperties.POSTALCODE), new ResourceModel("organisation.postalCode"));
        //AbstractEasyForm.addWithComponentFeedback(form, new TextField(UserProperties.CITY), new ResourceModel("organisation.city"));
        //AbstractEasyForm.addWithComponentFeedback(form, new TextField(UserProperties.COUNTRY), new ResourceModel("organisation.country"));


	    // using a form
        InfoForm infoForm = new InfoForm("organisationForm", organisation);
        add(infoForm);

	}

    class InfoForm extends AbstractCommonStatelessForm
    {
        private static final long serialVersionUID = 7094054164645818316L;

        public InfoForm(final String wicketId, final DccdOrganisation organisation)
        {
            super(wicketId, new CompoundPropertyModel(organisation), false);

            // no need, parent is used by wicket magic ;-)
            //addCommonFeedbackPanel();

			RequiredTextField orgIdTextField =  new RequiredTextField("id");
			// LDAP doesn't want those special chars, note that input is automatically trimmed by Wicket
			orgIdTextField.add(new PatternValidator("([^,\\\"\\\\#+<>;]*)"));
			addWithComponentFeedback(orgIdTextField, new ResourceModel("organisation.id"));
            
            addWithComponentFeedback(new TextField(UserProperties.ADDRESS), new ResourceModel("organisation.address"));
            addWithComponentFeedback(new TextField(UserProperties.POSTALCODE), new ResourceModel("organisation.postalCode"));
            addWithComponentFeedback(new TextField(UserProperties.CITY), new ResourceModel("organisation.city"));
            addWithComponentFeedback(new TextField(UserProperties.COUNTRY), new ResourceModel("organisation.country"));
        }

		@Override
		protected void onSubmit() {
		}
    }

}

