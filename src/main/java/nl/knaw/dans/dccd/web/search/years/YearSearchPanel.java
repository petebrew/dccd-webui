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
package nl.knaw.dans.dccd.web.search.years;

import java.util.Arrays;

import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IFormModelUpdateListener;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.MinimumValidator;

// Note: putting the components on a inner form 
// did not get the model updated automatically to the input after a submit
// Needed to implement the IFormModelUpdateListener
//
public class YearSearchPanel extends Panel implements IFormModelUpdateListener 
{
	private static final long	serialVersionUID	= 8847280650556371741L;
	YearSearchData data;

	public YearSearchPanel(String id, IModel model)
	{
		super(id, model);
		
		data = (YearSearchData) getDefaultModelObject();
		
		// The time period in years
		// From
		TextField fromYearValue = new TextField("fromYear", new PropertyModel(data, "fromYear"), Integer.class);
		add(fromYearValue);
		fromYearValue.add(new MinimumValidator(1));
		DropDownChoice fromYearSuffixChoice = new DropDownChoice("fromYearSuffix", 
				new PropertyModel(data, "fromYearSuffix"), 
				Arrays.asList(YearSuffix.values()),
				new ChoiceRenderer("value", "value")
		);
		add(fromYearSuffixChoice);
		// To
		TextField toYearValue = new TextField("toYear", new PropertyModel(data, "toYear"), Integer.class);
		toYearValue.add(new MinimumValidator(1));
		add(toYearValue);
		DropDownChoice toYearSuffixChoice = new DropDownChoice("toYearSuffix", 
				new PropertyModel(data, "toYearSuffix"), 
				Arrays.asList(YearSuffix.values()),
				new ChoiceRenderer("value", "value")
		);
		add(toYearSuffixChoice);
		
		// The year fields to match for
		add(new CheckBox("firstYear", new PropertyModel(data, "firstYear")));
		add(new CheckBox("lastYear", new PropertyModel(data, "lastYear")));
		add(new CheckBox("pithYear", new PropertyModel(data, "pithYear")));
		add(new CheckBox("deathYear", new PropertyModel(data, "deathYear")));
	}

	@Override
	public void updateModel()
	{
		setDefaultModelObject(data);//getConvertedInput());
	}

}
