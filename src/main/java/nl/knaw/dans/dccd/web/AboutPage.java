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

import java.io.File;
import java.util.Properties;

import nl.knaw.dans.dccd.application.services.DccdConfigurationService;
import nl.knaw.dans.dccd.web.base.BasePage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dev
 */
public class AboutPage extends BasePage
{
	private static final long serialVersionUID = 1L;
	public static final String PROJECT_CATEGORIES_PATH = "project.categories.path";
	public static final String TAXON_DATA_PATH = "taxon.data.path";
	private static Logger logger = LoggerFactory.getLogger(AboutPage.class);

	public AboutPage()
	{
		super();
		logger.error("Launching AboutPage err");
		logger.info("Launching AboutPage inf");
		logger.debug("Launching AboutPage dbg");

		initCategoriesChart();		
		initTaxonChart();
	}
	

		private void initCategoriesChart()
		{
			logger.error("initCategoriesChart()");

			// TODO get file path/name from configuration
			Properties settings = DccdConfigurationService.getService().getSettings();
			String filePath = settings.getProperty(PROJECT_CATEGORIES_PATH);
			File file = null;
			if (filePath == null) 
			{
				// no file to read, bail out
				logger.info("No categories read from file because No property found for: " + PROJECT_CATEGORIES_PATH);
			}
			else
			{
				file = new File(filePath);
				logger.info("Read categories file from " + PROJECT_CATEGORIES_PATH);

			}
			
			MorrisDonutChartPanel chart = new MorrisDonutChartPanel("categoriesChart", file);
			add(chart);	    
		}    

		
		private void initTaxonChart()
		{
			logger.error("initTaxonChart()");

			// TODO get file path/name from configuration
			Properties settings = DccdConfigurationService.getService().getSettings();
			String filePath = settings.getProperty(TAXON_DATA_PATH);
			File file = null;
			if (filePath == null) 
			{
				// no file to read, bail out
				logger.info("No taxon info read from file because No property found for: " + TAXON_DATA_PATH);
			}
			else
			{
				file = new File(filePath);
				logger.info("Read taxon data file from " + TAXON_DATA_PATH);

			}
			
			MorrisDonutChartPanel chart = new MorrisDonutChartPanel("taxonChart", file);
			add(chart);	    
		}    
}

