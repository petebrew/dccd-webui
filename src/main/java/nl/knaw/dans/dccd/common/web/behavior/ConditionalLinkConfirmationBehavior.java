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

/* Much like the LinkConfirmationBehavior, but with a condition; 
 * only do the confirm if the condition is met
 *
 * Note: no checks on the strings if it has valid JavaScript!
 */
public class ConditionalLinkConfirmationBehavior extends AbstractBehavior {
	private static final long	serialVersionUID	= -6404485815654949450L;
	// message string in a model makes sure that it's re-rendered 
	// when it changes dynamically (language selection or whatever)
	private final IModel msg; 
	private final String condition;
	
	public ConditionalLinkConfirmationBehavior(String msg, String condition) {
		this(new Model(msg), condition);
	}

	public ConditionalLinkConfirmationBehavior(IModel msg, String condition) {
		this.msg = msg;
		this.condition = condition;
	}

	@Override
	public void onComponentTag(Component component, ComponentTag tag) {
		super.onComponentTag(component, tag);

		String onclick = tag.getAttributes().getString("onclick");

		IModel model = msg;
		if (model instanceof IComponentAssignedModel) {
			model = ((IComponentAssignedModel)model).wrapOnAssignment(component);
		}
		
		onclick = 	"if (" + condition + "){" +
					"if (!confirm('" + model.getObject().toString() + "')) return false; " +
					"};" +
					onclick;
		
		tag.getAttributes().put("onclick", onclick);

		model.detach();
		msg.detach();
	}
}
