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
package nl.knaw.dans.dccd.web.util;

import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;

/**
 * Allows the web user to select a language/locale for the site
 *
 * Note: test by selecting different languages, but also change the browsers locale.
 * You can use an add-in on FireFox for this.
 *
 * @author paulboon
 *
 */
public class LocaleDropDown extends DropDownChoice {
	private static final long serialVersionUID = 26623494338029069L;
	private static Logger logger = Logger.getLogger(LocaleDropDown.class);

	private class LocaleRenderer extends ChoiceRenderer {
		private static final long serialVersionUID = 5369173407623161535L;

		/*
		@Override
		public String getIdValue(Object locale, int index) {
			// TODO Auto-generated method stub
			//return super.getIdValue(object, index);

			return ((Locale) locale).getLanguage();
					//+ "_" +((Locale) locale).getCountry();
					//+ "_" + ((Locale) locale).getVariant();
		}
	    */

		@Override
		public String getDisplayValue(Object locale) {
			// display in current locale's language
			//return ((Locale) locale).getDisplayName(getLocale());
			// display the locales in there own language
			String displayString = ((Locale) locale).getDisplayName((Locale)locale);
			
			// Fix that some language names have a lowercase first letter.
			// french in french returns français, while we have English, Nederlands and Deutsch, 
			// all with an uppercase firts letter!
			String firstChar = displayString.substring(0, 1);// assume atleast one letter
			firstChar = firstChar.toUpperCase();
			displayString = firstChar + displayString.substring(1);
			
			return displayString;

			// Note: Display in current locale's language is NOT what you want:
			// if you are Dutch and can only read Dutch,
			// You won't see the Dutch selection (Nederlands) when the browser is started on an English machine...
			// Now you can argue against it that the English reader who starts the browser on an English machine
			// will see the selections for the other languages not in English.
			// But that is not the purpose of the selection;
			// It's purpose is to provide a language for those who can read it!
			// We could place the same name in the current locale; after it (between brackets)
		}
	}

	@SuppressWarnings("unchecked")
	public LocaleDropDown(String id, List supportedLocales) {
		super(id,supportedLocales);
		setChoiceRenderer(new LocaleRenderer());
		setModel(new IModel() {
			private static final long serialVersionUID = 2267775429606684403L;

			public Object getObject() {
				return getSession().getLocale();
			}

			public void setObject(Object object) {
				// assume object is a Locale
				if (object != null) {
					getSession().setLocale((Locale) object);
				} else {
					logger.warn("given locale was null!");
				}
			}

			public void detach() {
			}
		});

		// Try to set the initial selection and get rid of the "Choose One"
		// find the current locale in the list
		Locale currentLocale = getSession().getLocale();
		for (int i = 0; i < supportedLocales.size(); i++) {
			// compare based on the language only
			if (((Locale)supportedLocales.get(i)).getLanguage() == currentLocale.getLanguage()) {
				// found!
				this.setModelObject(supportedLocales.get(i));
				this.setNullValid(false);
				//this.setRequired(true);
				break;
			}
		}
	}

	@Override
	protected boolean wantOnSelectionChangedNotifications() {
		return true;
	}
}
