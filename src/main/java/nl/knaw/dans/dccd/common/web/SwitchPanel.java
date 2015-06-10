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
package nl.knaw.dans.dccd.common.web;
/*
 * Note: was in package nl.knaw.dans.easy.web.wicket;
 */
import org.apache.wicket.markup.html.panel.Panel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SwitchPanel extends Panel
{
	private static Logger logger = LoggerFactory.getLogger(SwitchPanel.class);

	public static final String SWITCH_PANEL_WI = "switchPanel";
    private static final long serialVersionUID = 3543009697100900852L;
    private boolean editMode;

    public SwitchPanel(String wicketId)
    {
        super(wicketId);
    }

    public SwitchPanel(String wicketId, boolean inEditMode)
    {
        super(wicketId);
        editMode = inEditMode;
        setContentPanel();
    }

    public void switchMode()
    {
    	logger.debug("Switching mode from " + editMode + " to " + !editMode);
        editMode = !editMode;
        setContentPanel();
    }

    public abstract Panel getEditPanel();

    public abstract Panel getDisplayPanel();

    private void setContentPanel()
    {
        if (editMode)
        {
            addOrReplace(getEditPanel());
        }
        else
        {
            addOrReplace(getDisplayPanel());
        }
    }

}
