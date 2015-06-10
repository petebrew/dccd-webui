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
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import nl.knaw.dans.dccd.model.EntityAttribute;

import org.apache.wicket.datetime.PatternDateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.tridas.schema.Certainty;
import org.tridas.schema.DateTime;

/**
 * @author paulboon
 */
public class DateTimePanel extends EntityAttributePanel {
	private static final long serialVersionUID = 993828295942188854L;
	// mimic the xs:dateTime format; XMLGregorianCalendar.toString will probably do this
	public final String formatString = "yyyy-MM-dd'T'H:m:s.SSSZ"; 

	public DateTimePanel(String id, IModel model) {
		super(id, model, false);
	}

	public DateTimePanel(String id, IModel model, final boolean editable) {
		super(id, model, editable);
		//super(id, model, false); // only allow view
	}

	public class DateTimePanelView extends Panel {
		private static final long serialVersionUID = -2168858183817606673L;

		public DateTimePanelView(String id, IModel model) {
			super(id, model);
			// now do the view stuff (non-editable)
			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();

			// assume object is a DataTime from the tridas data
			DateTime datetime = null;

			datetime = (DateTime)attr.getEntryObject();

			// using Strings, NO PropertyModel yet!
			String datetimeStr = "";
			String certaintyStr = "";

			if (datetime != null) {
				datetimeStr = datetime.getValue().toString();
				// certainty is optional
				certaintyStr = datetime.getCertainty()!=null?
									  datetime.getCertainty().value():"";
			}
			add(new Label("datetime", datetimeStr).setVisible(datetime!=null));
			Label certaintyLabel = new Label("certainty", certaintyStr);
			add(certaintyLabel);

			//if(certaintyStr.length() == 0) certaintyLabel.setVisible(false);
		}
	}

	public class DateTimePanelEdit extends Panel {
		private static final long	serialVersionUID	= -4604340624148145418L;
		DateTime datetime = null;
		
		// Wrap the xml datetime in model that converts		
		// Use getter/setter for conversion
		public java.util.Date getDate() 
		{
			XMLGregorianCalendar xmlDate = datetime.getValue();
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
				datetime.setValue(date2);
			}
			catch (DatatypeConfigurationException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public DateTimePanelEdit(String id, IModel model) {
			super(id, model);

			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();

			// assume object is a DataTime from the tridas data
			datetime = (DateTime)attr.getEntryObject();
			
			if (datetime != null) 
			{
				// Format date correctly and only allow valid dates.
				// Note that xs:dateTime has minimal "yyyy-MM-ddThh:mm:ss"
				// but we use the PatternDateConverter
				DateTextField dateField = new DateTextField("datetime", new PropertyModel(this, "date"), 
						new PatternDateConverter(formatString, true));
				
				add(dateField);
				// show the required formatting as a short help
				// use the current time as an example is better than 
				// using some regular expression language with 
				// yyyy-MM-ddTH:m:s.SSS(+|-)hhmm
				DateFormat df = new SimpleDateFormat(formatString);
			    Date today = new Date();
				add(new Label("format", df.format(today)/*formatString*/));
				
				//TODO  Maybe use the EnumChoiceRenderer?			
				DropDownChoice choice = new DropDownChoice("certainty", 
						new PropertyModel(datetime, "certainty"), 
						Arrays.asList(Certainty.values()),
						new ChoiceRenderer("value", "value")
				);			
				add(choice);
			}
			else
			{
				// empty components
				add(new TextField("datetime").setVisible(false));
				add(new DropDownChoice("certainty").setVisible(false));				
			}
		}
	}
}

