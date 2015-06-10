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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import nl.knaw.dans.dccd.model.EntityAttribute;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.tridas.schema.NormalTridasShape;

/**
 * Panel for enums in Tridas
 * Those enums all have constructors with the string representation
 * from the TRiDaS schema and value() returns that string
 * 
 * TODO use this for all the enums in the Tridas JAXB object tree 
 * 
 * @author dev
 */
public class TridasEnumPanel extends EntityAttributePanel {
	private static final long	serialVersionUID	= -206102188172946777L;
	private static Logger logger = Logger.getLogger(TridasEnumPanel.class);

	public TridasEnumPanel(String id, IModel model) {
		super(id, model, false);
	}

	public TridasEnumPanel(String id, IModel model, final boolean editable) {
		super(id, model, editable);
		//super(id, model, false); // disable editing!
	}

	public class TridasEnumPanelView extends Panel {
		private static final long	serialVersionUID	= -738303543128248693L;

		public TridasEnumPanelView(String id, IModel model) {
			super(id, model);

			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();

			Object val = attr.getEntryObject();
			
			String str = "";

			if(val != null) {
				// use reflection to call the value() method, that all those enums have
				try
				{
					Method m = val.getClass().getMethod("value");
					str =  (String)m.invoke(val);
				}
				catch (NoSuchMethodException e)
				{
					logger.error("could not call 'value()' method on object " + val.getClass().getName());
				}
				catch (SecurityException e)
				{
					logger.error("could not call 'value()' method on object " + val.getClass().getName());
				}
				catch (IllegalArgumentException e)
				{
					logger.error("could not call 'value()' method on object " + val.getClass().getName());
				}
				catch (IllegalAccessException e)
				{
					logger.error("could not call 'value()' method on object " + val.getClass().getName());
				}
				catch (InvocationTargetException e)
				{
					logger.error("could not call 'value()' method on object " + val.getClass().getName());
				}
			}

			Label label = new Label("text", new Model(str));
			add(label);			
		}
	}
	
	public class TridasEnumPanelEdit extends Panel {
		private static final long	serialVersionUID	= -7125486856532203815L;

		public TridasEnumPanelEdit(String id, IModel model) {
			super(id, model);

			EntityAttribute attr = (EntityAttribute) this.getDefaultModelObject();

			Object val = attr.getEntryObject();
			
			
			if (val != null)
			{				
				Object [] values = null;
				try
				{
					Method m = val.getClass().getMethod("values");
					values =  (Object [])m.invoke(val);
				}
				catch (SecurityException e)
				{
					logger.error("could not call 'values()' method on object " + val.getClass().getName());
				}
				catch (NoSuchMethodException e)
				{
					logger.error("could not call 'values()' method on object " + val.getClass().getName());
				}
				catch (IllegalArgumentException e)
				{
					logger.error("could not call 'values()' method on object " + val.getClass().getName());
				}
				catch (IllegalAccessException e)
				{
					logger.error("could not call 'values()' method on object " + val.getClass().getName());
				}
				catch (InvocationTargetException e)
				{
					logger.error("could not call 'values()' method on object " + val.getClass().getName());
				}

				DropDownChoice choice = new DropDownChoice("type", 
						new PropertyModel(attr.getObject(), attr.getEntry().getMethod()), 
						Arrays.asList(values),
						new ChoiceRenderer("value", "value")
				);
				add(choice);
			}
			else
			{
				add(new DropDownChoice("type").setVisible(false));				
			}
		}
	}	
}
