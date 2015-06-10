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

import nl.knaw.dans.dccd.common.web.behavior.LinkConfirmationBehavior;
import nl.knaw.dans.dccd.web.authn.UserStatusChangeActionSelection.Action;

import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author paulboon
 */
public class UserStatusChangeActionSelectionPanel extends Panel {
	private static final long serialVersionUID = -2394858266306749312L;
	private static Logger logger = LoggerFactory.getLogger(UserStatusChangeActionSelectionPanel.class);

	//private UserStatusChangeActionSelection actionSelection;

	public UserStatusChangeActionSelectionPanel(String id, UserStatusChangeActionSelection actionSelection) {
		super(id, new CompoundPropertyModel(actionSelection));
		//this.actionSelection = actionSelection;
		init();
	}

	private UserStatusChangeActionSelection getSelection()
	{
		return (UserStatusChangeActionSelection)getDefaultModelObject();
	}

	public void init() {
		// Add selection and enable/disable the 'action buttons' using actionSelection

        // Acivate
        SubmitLink userActivate = new SubmitLink("userActivate") {
			private static final long serialVersionUID = -661621354590046086L;

			public void onSubmit() {
                logger.debug("userActivate.onSubmit executed");
                getSelection().setSelectedAction(Action.ACTIVATE);
            }
        };
        add(userActivate);
        userActivate.setVisible(getSelection().isAlowedAction(Action.ACTIVATE));
        // confirmation, only for ACTIVATE
        if (getSelection().isAlowedAction(Action.ACTIVATE))
        {
        	if (getSelection().hasConfirmation(Action.ACTIVATE))
        	{
        		userActivate.add(new LinkConfirmationBehavior(getSelection().getConfirmation(Action.ACTIVATE)));
        	}
        }

        // Delete
        SubmitLink userDelete = new SubmitLink("userDelete") {
			private static final long serialVersionUID = -976047485479669445L;

			public void onSubmit() {
            	logger.debug("userDelete.onSubmit executed");
            	getSelection().setSelectedAction(Action.DELETE);
            }
        };
        add(userDelete);
        userDelete.setVisible(getSelection().isAlowedAction(Action.DELETE));

        // Restore
        SubmitLink userRestore = new SubmitLink("userRestore") {
			private static final long serialVersionUID = 317247670008469017L;

			public void onSubmit() {
            	logger.debug("userRestore.onSubmit executed");
            	getSelection().setSelectedAction(Action.RESTORE);
            }
        };
        add(userRestore);
        userRestore.setVisible(getSelection().isAlowedAction(Action.RESTORE));
	}
}

