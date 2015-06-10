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
package nl.knaw.dans.dccd.web.datapanels;

import nl.knaw.dans.dccd.model.EntityAttribute;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.tridas.schema.SeriesLink;
import org.tridas.schema.TridasFile;

/**
 * @author paulboon
 */
public class XLinkPanel extends EntityAttributePanel 
{
	private static final long	serialVersionUID	= 2279775702471813519L;

	public XLinkPanel(String id, IModel model) 
	{
		super(id, model, false);
	}

	public XLinkPanel(String id, IModel model, final boolean editable) {
		super(id, model, editable);
	}

	public class XLinkPanelView extends Panel 
	{
		private static final long	serialVersionUID	= 8642944956976677552L;

		public XLinkPanelView(String id, IModel model) 
		{
			super(id, model);
			initPanel(this);
		}
	}

	public class XLinkPanelEdit extends Panel 
	{
		private static final long	serialVersionUID	= 6370003582183027512L;

		public XLinkPanelEdit(String id, IModel model) 
		{
			super(id, model);
			initPanel(this);
		}
	}

	private void initPanel(final Panel panel)
	{
		// assume object is a XLink
		SeriesLink.XLink link = (SeriesLink.XLink)((EntityAttribute)getDefaultModelObject()).getEntryObject();
// PROPRTYMODEL?		
		EntityAttribute hRefAttr = new EntityAttribute(link, "href");
		TextPanel hrefPanel = new TextPanel("link", new Model(hRefAttr), isEditable());
        panel.add(hrefPanel);
	}
}

