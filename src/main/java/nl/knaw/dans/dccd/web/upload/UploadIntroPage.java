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
package nl.knaw.dans.dccd.web.upload;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import nl.knaw.dans.common.wicket.EnumChoiceRenderer;
import nl.knaw.dans.dccd.web.DccdSession;
import nl.knaw.dans.dccd.web.HomePage;
import nl.knaw.dans.dccd.web.base.BasePage;

import org.apache.log4j.Logger;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.model.PropertyModel;

/**
 * @author paulboon
 */
public class UploadIntroPage extends BasePage
{
	private static Logger logger = Logger.getLogger(UploadIntroPage.class);

	private UploadTypeSelection uploadSelection; // the model object

	/**
	 *  specifies the upload type
	 */
	public enum UploadType
	{
		TRIDAS_UPLOAD,
		TRIDAS_MANUAL
	}

	// A model class for the RadioChoice
	private class UploadTypeSelection implements Serializable
	{
		private static final long serialVersionUID = -6173650372149125063L;
		// Now use a nice enum instead of int's
		private UploadType selection = UploadType.TRIDAS_UPLOAD;

		public UploadType getSelection()
		{
			return selection;
		}

		public void setSelection(UploadType selection)
		{
			this.selection = selection;
		}

		public String getSelectionAsString()
		{
			return getName(uploadSelection.getSelection());
		}

		// Note: now we have the strings hard-coded,
		// but they should come from a resource
		public String getName(UploadType id)
		{
			String name = "None";
			switch (id)
			{
			case TRIDAS_UPLOAD:
				name = "TRIDAS_UPLOAD";
				break;
			case TRIDAS_MANUAL:
				name = "TRIDAS_MANUAL";
				break;
			default:
				logger.error("unexpected id: " + id);
			}
			return name;
		}

		 public List<UploadType> getTypeList()
		 {
			//return Arrays.asList(UploadType.TRIDAS_UPLOAD, UploadType.TRIDAS_MANUAL);
			return Arrays.asList(UploadType.values());
		}
	};

	public UploadIntroPage()
	{
		uploadSelection = new UploadTypeSelection();


		Form form = new Form("form");
		add(form);

		// TODO use html formatting for the "hint" texts in the properties file
		// Place it in a Label and use label.setEscapeModelStrings(false);
		form.add(new Label("hint_text", getString("hint")).setEscapeModelStrings(false));

		// upload type selection as Choice
		RadioChoice choice = new RadioChoice("selection",
				new PropertyModel(uploadSelection, "selection"),
				uploadSelection.getTypeList(),
				new EnumChoiceRenderer(this, null)) {
					private static final long	serialVersionUID	= -784957323409770888L;

					// NOTE disable the manual creation option, we don't have it YET
					// NOTE HARDCODED  to be the second option!
					@Override
					protected boolean isDisabled(Object object, int index, String selected)
					{
						if (index > 0) return true;
						else return false;
					}
			
		};
		choice.setEscapeModelStrings(false);
		form.add(choice);

		// Next
		form.add(new SubmitLink("next")
		{
			private static final long serialVersionUID = -4179679565306357773L;

			@Override
		    public void onSubmit()
			{
		    	//logger.info("Next onSubmit is called");
				//logger.info("OnSubmit with selection: " + uploadSelection.getSelectionAsString());
				next();
		    }
		});

		// Cancel
		form.add(new SubmitLink("cancel")
		{
			private static final long serialVersionUID = -3928218818189732662L;

			@Override
		    public void onSubmit()
			{
		    	//logger.info("Cancel onSubmit is called");
		        cancel();
		    }
		});
	}

	// go to the next page, using the selection made
	private void next()
	{
		((DccdSession)Session.get()).setRedirectPage(UploadFilesPage.class, getPage());

		// Next button was pushed, redirect to the selected page
		switch (uploadSelection.getSelection())
		{
		case TRIDAS_UPLOAD:
			// TODO change the UploadFilesPage constructor,
			// we wont provide the TridasUploadType
			//setResponsePage(new UploadFilesPage(TridasUploadType.TRIDAS_ONLY));
			// Note: the parameter might be removed in future versions
			setResponsePage(new UploadFilesPage(TridasUploadType.TRIDAS_WITH_DATA));
			break;
		case TRIDAS_MANUAL:
			//setResponsePage(new UploadFilesPage(TridasUploadType.TRIDAS_WITH_DATA));
			// TODO navigate to an ProjectPage (in edit mode) to create an new Project
			logger.debug("Selection TRIDAS_MANUAL not enabled");
			break;
		default:
			logger.error("Unexpected selection: "
					+ uploadSelection.getSelection());
		}
	}

	private void cancel()
	{
        //Cancel, just brings you to the home page
        setResponsePage(new HomePage(null));
	}
}
