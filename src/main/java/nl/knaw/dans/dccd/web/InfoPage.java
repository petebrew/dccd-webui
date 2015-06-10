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
package nl.knaw.dans.dccd.web;

import nl.knaw.dans.dccd.web.base.BasePage;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

public class InfoPage extends BasePage //AbstractEasyNavPage
{

    public static final String WI_HEADING = "heading";

    public static final String WI_MISSION_ACCOMPLISHED_PANEL = "missionAccomplishedFeedback";

    private final String heading;

    /**
     * Serial version uid.
     */
    private static final long  serialVersionUID = 1L;

    public InfoPage()
    {
        this(null);
    }

    public InfoPage(String heading)
    {
        this.heading = heading;
        add(new Label(WI_HEADING, this.heading)).setVisible(StringUtils.isNotBlank(heading));
        add(new FeedbackPanel(WI_MISSION_ACCOMPLISHED_PANEL));
    }

//    @Override
//    public String getPageTitlePostfix()
//    {
//        return heading;
//    }


}
