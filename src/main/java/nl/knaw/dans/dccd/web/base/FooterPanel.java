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
package nl.knaw.dans.dccd.web.base;


import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import nl.knaw.dans.common.lang.ResourceLocator;
import nl.knaw.dans.dccd.application.services.DccdApplicationVersionService;
import nl.knaw.dans.dccd.application.services.DccdDataService;
import nl.knaw.dans.dccd.web.AboutPage;
import nl.knaw.dans.dccd.web.AcknowledgementsPage;
import nl.knaw.dans.dccd.web.TermsOfUsePage;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;

/** Footer of each page
 *
 * @author paulboon
 */
public class FooterPanel extends Panel {
	private static Logger logger = Logger.getLogger(FooterPanel.class);
	
	public FooterPanel(String id) {
        super(id);

		add(new BookmarkablePageLink("termsOfUseLink", TermsOfUsePage.class));
		add(new BookmarkablePageLink("acknowledgementsLink", AcknowledgementsPage.class));
		/*
		final String TIMESTAMP_PROPERTY_KEY = "timestamp";
		final String TIMESTAMP_PROPERTY_FILE = "version.properties";
		final String TIMESTAMP_PROPERTY_DEFAULT = "";
		Properties version = new Properties();
		version.setProperty(TIMESTAMP_PROPERTY_KEY, TIMESTAMP_PROPERTY_DEFAULT); 
		URL url = ResourceLocator.getURL(TIMESTAMP_PROPERTY_FILE);
		if (url == null)
		{
			logger.error("Could not load resource from: " + TIMESTAMP_PROPERTY_FILE);
		}
		else
		{
			try
			{
				version.load(url.openStream());
			}
			catch (IOException e)
			{
				logger.error("Could not load resource from: " + TIMESTAMP_PROPERTY_FILE);
			}
		}
		*/
		String buildDate = DccdApplicationVersionService.getService().getTimestamp();//version.getProperty(TIMESTAMP_PROPERTY_KEY)
		
		add(new Label("buildDate", buildDate));
    }

	private static final long serialVersionUID = -4662481183065131700L;
}

