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

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * For selection of the change of the user status (an action on the user)
 *
 * @author paulboon
 *
 */
public class UserStatusChangeActionSelection implements Serializable {
	private static final long serialVersionUID = -7205685112406124116L;

	public enum Action {
		NOACTION,
		ACTIVATE,
		DELETE,
		RESTORE
	};

	private Action selectedAction = Action.NOACTION;
	// no knowledge of the states, only keeps track of selection and what is allowed
	private Set<Action> allowedActions = new HashSet<Action>();
	// each action can have an associated confirmation message
	private Map<Action, String> confirmations = new HashMap<Action, String>();

	public UserStatusChangeActionSelection(final Action...actions) {
		super();
		// always allow 'no action'
		allowedActions.add(Action.NOACTION);
        for (Action action : actions)
        {
        	allowedActions.add(action);
        }
	}

	public Action getSelectedAction() {
		return selectedAction;
	}

	public void setSelectedAction(Action selectedAction) {
		// only if allowed
		if (isAlowedAction(selectedAction) )
		{
			this.selectedAction = selectedAction;
		}
		else
		{
			// maybe throw an exception?
		}
	}

	public boolean isAlowedAction(Action action)
	{
		return allowedActions.contains(action);
	}

	// add confirmationMsg; if set it is used else no confirm is shown
	public void addConfirmation(Action action, String message)
	{
		confirmations.put(action, message);
	}

	public void removeConfirmation(Action action)
	{
		confirmations.remove(action);
	}

	public String getConfirmation(Action action)
	{
		return (String)confirmations.get(action);
	}

	public boolean hasConfirmation(Action action)
	{
		return confirmations.containsKey(action);
	}
}
