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
package nl.knaw.dans.dccd.web.project;

import nl.knaw.dans.dccd.web.project.ProjectArchivingPage.ArchivingAcceptLicenseSelection;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

/** TODO maybe commonize license acception panel?
 *
 * @author paulboon
 */
public class ArchivingAcceptLicensePanel extends Panel
{
	private static Logger logger = Logger.getLogger(ArchivingAcceptLicensePanel.class);
	private static final long serialVersionUID = -4205270765038730865L;

	public ArchivingAcceptLicensePanel(String id, IModel model) {
		super(id, model);

		final ArchivingAcceptLicenseSelection selection =
			(ArchivingAcceptLicenseSelection) this.getDefaultModelObject();

		
		// TODO add link to PDF file with license text
		
		CheckBox acceptCheckBox =
			new AjaxCheckBox("license_accept", new PropertyModel(selection, "accepted")) {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					// TODO Auto-generated method stub
					logger.debug("===> onUpdate: " + selection.isAccepted());

					// Note : could do something with the form submit
					//IFormSubmittingComponent c = this.getForm().findSubmittingButton();
					// But now delegate it to an overridable member
					onAcceptChange(target, selection.isAccepted());
				}

		};
		add(acceptCheckBox);
	}

	/** Override this method to implement your own 'behaviour'
	 * Normally you would enable/disable some submit button or link:
	 * myComponent.setEnabled(accepted);
	 * and then make the change the AJAXian way:
	 * if (target != null) target.addComponent(myComponent);
	 *
	 * @param target
	 * @param accepted
	 */
	protected void onAcceptChange(AjaxRequestTarget target, boolean accepted) {
		// empty, override
	}
}


