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
package nl.knaw.dans.dccd.web.project;

import java.util.Locale;

import nl.knaw.dans.dccd.application.services.DccdUserService;
import nl.knaw.dans.dccd.application.services.UserServiceException;
import nl.knaw.dans.dccd.model.DccdUser;
import nl.knaw.dans.dccd.model.Project;
import nl.knaw.dans.dccd.web.DccdSession;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

/**
 * @author dev
 */
public class ProjectLanguagePanel extends Panel
{
	private static final long	serialVersionUID	= -4353601532602855170L;

	public ProjectLanguagePanel(String id, IModel<Project> model)
	{
		super(id, model);

		init();
	}

	private void init()
	{
		IModel<Project> projectModel = (IModel<Project>) this.getDefaultModel();
		Project project = projectModel.getObject();
		//DccdUser user = (DccdUser) ((DccdSession) getSession()).getUser();

		Locale tridasLanguage = project.getTridasLanguage();
		add(new Label("languageName", new PropertyModel(tridasLanguage,"displayName")));
	}

}

