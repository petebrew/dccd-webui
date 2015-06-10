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
package nl.knaw.dans.dccd.web.datapanels;

import nl.knaw.dans.dccd.common.web.confirm.ConfirmedActionButtonPanel;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * Note: The HTML is different, using an image button
 */
public abstract class RemoveAttributeButtonPanel extends ConfirmedActionButtonPanel
{
	private static final long	serialVersionUID	= -4168885288231751200L;

	public RemoveAttributeButtonPanel(String id, String buttonName, String modalMessageText)
	{
		super(id, buttonName, modalMessageText);
	}

	protected abstract void onConfirm(AjaxRequestTarget target);
	protected abstract void onCancel(AjaxRequestTarget target);
}

