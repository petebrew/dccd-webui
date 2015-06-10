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
package nl.knaw.dans.dccd.common.web.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.model.IComponentAssignedModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/* 
 * Adding a confirm dialog to a SubmitLink with SimpleAttributeModifier would not work,
 * so the next line is useless.
 * link.add( new SimpleAttributeModifier("onclick", "return confirm('are you sure?');"));
 * We can't use the SimpleAttributeModifier because it replaces the default click/submit behavior
 * but we can place our own code before the existing onclick
 * in the onComponentTag
 * 
 * From: http://markmail.org/message/hefkwpz5edlgosuh
 * Original By: Igor Vaynberg
 */
public class LinkConfirmationBehavior extends AbstractBehavior {
	private static final long serialVersionUID = -6445751500718057238L;
	private final IModel msg;

	public LinkConfirmationBehavior(String msg) {
		this(new Model(msg));
	}

	public LinkConfirmationBehavior(IModel msg) {
		this.msg = msg;
	}

	@Override
	public void onComponentTag(Component component, ComponentTag tag) {
		super.onComponentTag(component, tag);

		String onclick = tag.getAttributes().getString("onclick");

		IModel model = msg;
		if (model instanceof IComponentAssignedModel) {
			model = ((IComponentAssignedModel)model).wrapOnAssignment(component);
		}

		onclick = "if (!confirm('" + model.getObject().toString() + "')) return false; " + onclick;
		tag.getAttributes().put("onclick", onclick);

		model.detach();
		msg.detach();
	}
}
