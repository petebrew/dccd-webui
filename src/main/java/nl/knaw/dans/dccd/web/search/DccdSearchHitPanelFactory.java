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
package nl.knaw.dans.dccd.web.search;

import nl.knaw.dans.common.lang.search.SearchHit;
import nl.knaw.dans.common.wicket.components.search.model.SearchModel;
import nl.knaw.dans.common.wicket.components.search.results.SearchHitPanelFactory;
import nl.knaw.dans.dccd.search.DccdObjectSB;
import nl.knaw.dans.dccd.search.DccdProjectSB;
import nl.knaw.dans.dccd.search.DccdSB;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

public class DccdSearchHitPanelFactory implements SearchHitPanelFactory
{
	private static Logger logger = Logger.getLogger(DccdSearchHitPanelFactory.class);
	private static final long serialVersionUID = 1027994078724896125L;

	private static DccdSearchHitPanelFactory INSTANCE = new DccdSearchHitPanelFactory();

	public static DccdSearchHitPanelFactory getInstance()
	{
		return INSTANCE;
	}

	public Panel createHitPanel(String id, SearchHit<?> hit, SearchModel model)
	{
		logger.debug("hit data type: " + hit.getData().getClass().getName());

        if (hit.getData() instanceof DccdObjectSB)
		{
        	logger.debug("Object searchbean");
        	return new DccdHitPanel(id, new Model(hit), model);
		}
        else if (hit.getData() instanceof DccdProjectSB)
        {
        	logger.debug("Project searchbean");
        	return new DccdProjectHitPanel(id, new Model(hit), model);
        }
        else if (hit.getData() instanceof DccdSB)
        {
        	logger.debug("dccd searchbean");
        	return new DccdHitPanel(id, new Model(hit), model);
        }
        else
        {
        	logger.warn("Unknown searchbean");
        	return null;
        }
	}
}
