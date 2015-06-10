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

import nl.knaw.dans.common.web.template.AbstractCommonStatelessPanel;
import nl.knaw.dans.dccd.authn.UsernamePasswordAuthentication;

//import org.apache.log4j.Logger;

/**
 * @author paulboon
 */
public class LoginPanel extends AbstractCommonStatelessPanel {
	//private static Logger logger = Logger.getLogger(LoginPanel.class);
	private static final long serialVersionUID = -7865635722188112818L;

	public LoginPanel(String id, final UsernamePasswordAuthentication authentication) {
		super(id);
        init(authentication);
	}

	/**
     * Initialize the same for every constructor.
     */
    private void init(final UsernamePasswordAuthentication authentication)
    {
    	// Form
        LoginForm loginForm = new LoginForm("loginForm", authentication);
        add(loginForm);
    }
}

