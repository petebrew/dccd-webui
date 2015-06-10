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

import nl.knaw.dans.dccd.model.Project;
import nl.knaw.dans.dccd.web.HomePage;
import nl.knaw.dans.dccd.web.base.BasePage;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.PageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

/**
 * @author paulboon
 */
public class ArchivingConfirmPage extends BasePage
{
	public ArchivingConfirmPage(Project project)
	{
		super();
		init(project);
	}

	private void init(final Project project)
	{
		Label titleLabel = new Label("page.title", new StringResourceModel("page.title", this, new Model(project)));
		add(titleLabel);

		// TODO
		// make better message using a Label

		// link to Project Page
		add(new Link("projectLink")
		{
			private static final long	serialVersionUID	= 1L;
			public void onClick()
			{
				// just a new page, 
				// could try to find it in the pagemap ProjectArchivingPage, 
				// but then we need a refresh
				setResponsePage(new ProjectViewPage(project));
			}
		});
	}
}
