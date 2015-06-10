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

import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

/**
 * @author paulboon
 */
public class DownloadFileInclusionSelectionPanel extends Panel {
	private static final long serialVersionUID = 9180465615405793359L;

	public DownloadFileInclusionSelectionPanel(String id, IModel model) {
		super(id, model);

		DownloadFileInclusionSelection selection =
			(DownloadFileInclusionSelection) this.getDefaultModelObject();


		add(new CheckBox("original_values_files",
				new PropertyModel(selection, "originalValuesFiles")));
		
		add(new CheckBox("associated_files",
				new PropertyModel(selection, "associatedFiles")));

		// Note: disable the following, because there is no support for it!
		add(new CheckBox("dccd_adminstrative_data",
				new PropertyModel(selection, "dccdAdminstrativeData")).setEnabled(false));
		add(new CheckBox("dccd_usage_comments",
				new PropertyModel(selection, "dccdUsageComments")).setEnabled(false));
	}

}
