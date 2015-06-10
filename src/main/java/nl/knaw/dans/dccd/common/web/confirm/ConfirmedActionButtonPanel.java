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

import java.io.Serializable;
import java.util.Map;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

// TODO refactor to ConfirmDialogPanel or something else
//
// This panel has a button whose action needs to be confirmed. 
// When clicked, it will pop-up a confirm dialog (window). 
// Depending on the user selection either the onConfirm or onCancel will be called
// These must be overidden to perform the desired action
public abstract class ConfirmedActionButtonPanel extends Panel
{
	private static final long	serialVersionUID	= 4083132354974050858L;
	protected ModalWindow		confirmModal;

	public ModalWindow getModalWindow()
	{
		return confirmModal;
	}

	protected ConfirmationAnswer	answer;
	protected Map<String, String>	modifiersToApply;

	public ConfirmedActionButtonPanel(String id, String buttonName, String modalMessageText)
	{
		super(id);
		answer = new ConfirmationAnswer(false);
		addElements(id, buttonName, modalMessageText);
	}

	protected void addElements(String id, String buttonName, String modalMessageText)
	{
		confirmModal = createConfirmModal(id, modalMessageText);

		Form form = new Form("confirmForm");
		add(form);

		AjaxButton confirmButton = new AjaxButton("confirmButton", new Model(buttonName))
		{
			private static final long	serialVersionUID	= 4414008395553662034L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form)
			{
				confirmModal.show(target);
			}
		};

		form.add(confirmButton);
		form.add(confirmModal);
	}

	protected abstract void onConfirm(AjaxRequestTarget target);
	protected abstract void onCancel(AjaxRequestTarget target);

	protected ModalWindow createConfirmModal(String id, String modalMessageText)
	{
		ModalWindow modalWindow = new ModalWindow("modal");
		// modalWindow.setCookieName(id);
		modalWindow.setContent(new ConfirmDialogPanel(modalWindow.getContentId(), modalMessageText, modalWindow, answer));
		modalWindow.setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
		{
			private static final long	serialVersionUID	= -1784797035565783985L;

			@Override
			public void onClose(AjaxRequestTarget target)
			{
				if (answer.isAnswer())
				{
					onConfirm(target);
				}
				else
				{
					onCancel(target);
				}
			}
		});

		return modalWindow;
	}

	public class ConfirmationAnswer implements Serializable
	{
		private static final long	serialVersionUID	= -6376465709513317643L;
		private boolean				answer;

		public ConfirmationAnswer(boolean answer)
		{
			this.answer = answer;
		}

		public boolean isAnswer()
		{
			return answer;
		}

		public void setAnswer(boolean answer)
		{
			this.answer = answer;
		}
	}

}
