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
package nl.knaw.dans.dccd.web.download;

import java.util.MissingResourceException;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

/**
 * @author paulboon
 */
public class DownloadValuesSelectionPanel extends Panel {
	private static Logger logger = Logger.getLogger(DownloadValuesSelectionPanel.class);
	private static final long serialVersionUID = 7720499555997698608L;
	private DownloadValuesSelection selection;

	public DownloadValuesSelectionPanel(String id, IModel model) {
		super(id, model);

		selection = (DownloadValuesSelection) this.getDefaultModelObject();

		// selection of the values file format
		add(new DropDownChoice("download_values_selection",
				new PropertyModel(selection, "selection"),
				DownloadValuesSelection.getTypeList(),
				new ChoiceRenderer() 
				{
					private static final long serialVersionUID = 3994124725156529569L;
					public Object getDisplayValue(Object value) 
					{
						String formatString = (String)value;
						
						if (formatString == DownloadValuesSelection.SELECT_NONE)
						{
							// get String from properties
							try 
							{
								formatString = getString(formatString);
							}
							catch (MissingResourceException e)
							{
								formatString = "none"; // default
							}
						}
						return formatString;
					}
				}
		));
	}
}
