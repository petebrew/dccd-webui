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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import nl.knaw.dans.dccd.model.EntityAttribute;

import org.apache.log4j.Logger;
import org.apache.wicket.datetime.PatternDateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.tridas.schema.Certainty;
import org.tridas.schema.Date;

/**
 * @author paulboon
 */
public class DatePanel extends EntityAttributePanel {
	private static final long	serialVersionUID	= 1723137060123989725L;
	private static Logger logger = Logger.getLogger(DatePanel.class);
	// mimic xs:date
	public final String formatString = "yyyy-MM-dd"; 
	
	public DatePanel(String id, IModel model) {
		super(id, model, false);
	}

	public DatePanel(String id, IModel model, final boolean editable) {
		super(id, model, editable);
		//super(id, model, false); // only allow view
	}

	public class DatePanelView extends Panel {
		private static final long	serialVersionUID	= 8214002879213303950L;

		public DatePanelView(String id, IModel model) {
			super(id, model);
			// now do the view stuff (non-editable)
			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();

			// assume object is a Data from the tridas data
			Date date = null;

			date = (Date)attr.getEntryObject();

			// using Strings, NO PropertyModel yet!
			String dateStr = "";
			String certaintyStr = "";

			final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(formatString);
			if (date != null) 
			{
				if (date.isSetValue())
				{
					XMLGregorianCalendar xmlDate = date.getValue();
					java.util.Date javaDate = xmlDate.toGregorianCalendar().getTime();
					dateStr = DATE_FORMAT.format(javaDate);
				}
				
				// certainty is optional
				certaintyStr = date.getCertainty()!=null?
						date.getCertainty().value():"";
			}
			add(new Label("date", dateStr).setVisible(date!=null));
			Label certaintyLabel = new Label("certainty", certaintyStr);
			add(certaintyLabel);

			//if(certaintyStr.length() == 0) certaintyLabel.setVisible(false);
		}
	}

	public class DatePanelEdit extends Panel {
		private static final long	serialVersionUID	= -2289111644781411195L;
		Date date = null;
		
		// Wrap the xml date in model that converts		
		// Use getter/setter for conversion
		public java.util.Date getDate() 
		{
			XMLGregorianCalendar xmlDate = date.getValue();
			java.util.Date javaDate = xmlDate.toGregorianCalendar().getTime();
			return javaDate;
		}
		
		public void setDate(java.util.Date javaDate)
		{
			GregorianCalendar c = new GregorianCalendar();
			c.setTime(javaDate);
			try
			{
				XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
				date.setValue(date2);
			}
			catch (DatatypeConfigurationException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public DatePanelEdit(String id, IModel model) {
			super(id, model);
			// now do the view stuff (non-editable)
			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();

			// assume object is a Data from the tridas data			
			date = (Date)attr.getEntryObject();
			if (date != null)
			{
				logger.debug("has date");

				// Format date correctly and only allow valid dates.
				DateTextField dateField = new DateTextField("date", new PropertyModel(this, "date"), 
						new PatternDateConverter(formatString, true));
				add(dateField);
				
				// the required formatting is not clear, 
				// so we show as an example the current date
				DateFormat df = new SimpleDateFormat(formatString);
				java.util.Date today = new java.util.Date();
				add(new Label("format", df.format(today)));
				
				// Use the more userfriendly picker
				DatePicker datePicker = new DatePicker();
				dateField.add(datePicker);

				//TODO  Maybe use the EnumChoiceRenderer?			
				DropDownChoice choice = new DropDownChoice("certainty", 
						new PropertyModel(date, "certainty"), 
						Arrays.asList(Certainty.values()),
						new ChoiceRenderer("value", "value")
				);			
				add(choice);
			}
			else
			{		
				logger.debug("has no date");
				
				add(new TextField("date").setVisible(false));
				add(new DropDownChoice("certainty").setVisible(false));	
			}
		}
	}
}

