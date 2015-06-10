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
package nl.knaw.dans.dccd.common.web.confirm;

import java.util.MissingResourceException;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import nl.knaw.dans.dccd.common.web.confirm.ConfirmedActionButtonPanel.ConfirmationAnswer;
 
// TODO refactor to ConfirmButtonsPanel
public class ConfirmDialogPanel extends Panel {
	private static final long	serialVersionUID	= -5189096979285135420L;

	public ConfirmDialogPanel(String id, String message, final ModalWindow modalWindow, final ConfirmationAnswer answer) {
		super(id);
 
		Form yesNoForm = new Form("yesNoForm");
 
		MultiLineLabel messageLabel = new MultiLineLabel("message", message);
		yesNoForm.add(messageLabel);
		
		initModalWindow(modalWindow);
 
		AjaxButton yesButton = new AjaxButton("yesButton", yesNoForm) {
			private static final long	serialVersionUID	= -4789365829585310382L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				if (target != null) {
					answer.setAnswer(true);
					modalWindow.close(target);
				}
			}
		};
		setButtonLabel(yesButton, "confirmDialog.yesButton.label");
		
		AjaxButton noButton = new AjaxButton("noButton", yesNoForm) {
			private static final long	serialVersionUID	= 5179369782355613213L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				if (target != null) {
					answer.setAnswer(false);
					modalWindow.close(target);
				}
			}
		};
		setButtonLabel(noButton, "confirmDialog.noButton.label");

		yesNoForm.add(yesButton);
		yesNoForm.add(noButton);
 
		add(yesNoForm);
	}
 
	private void initModalWindow(final ModalWindow modalWindow)
	{
		modalWindow.setTitle("Please confirm");
		// use label from property files when available
		try
		{
			String title = getString("confirmDialog.title");
			//if (title != null && !title.isEmpty())
				modalWindow.setTitle(title);
		}
		catch (MissingResourceException e)
		{
			// do Nothing
		}				
		
		modalWindow.setInitialHeight(150);
		modalWindow.setInitialWidth(250);		
	}
	
	private void setButtonLabel(final AjaxButton button, final String resourceKey)
	{
		// use label from property files when available
		try
		{
			String label = getString(resourceKey);
			//if (label != null && !label.isEmpty())
				button.setModel(new Model(label));
		}
		catch (MissingResourceException e)
		{
			// do Nothing
		}		
	}
}

