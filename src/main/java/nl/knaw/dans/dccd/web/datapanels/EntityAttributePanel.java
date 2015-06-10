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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import nl.knaw.dans.dccd.model.InternalErrorException;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.IValidator;

/**
 * Base class for all Panels with DCCD TRiDaS Data
 * Supports creation with editable flag, which is by default false
 *
 * Any class that derives from this one should implement
 * two inner classes; one for the editing and one for the viewing.
 * The names must be the same as the outer class but with 'Edit' and 'View' appended.
 * The implementing class must also have corresponding html files.
 * If the class name is *** then ***$Edit.html and **$View.html
 * They must extend Panel and the constructors must have the following arguments: (String id, IModel model)
 *
 * Note: would be nice to have unittest for this
 * Note2: refactoring tools won't understand the naming convention for the inner classes
 *
 * TODO: unit testing
 *
 * @author paulboon
 *
 */
public class EntityAttributePanel  extends Panel {
	private static final long serialVersionUID = 2283789585424160053L;
	private boolean editable = false;
	private static Logger logger = Logger.getLogger(EntityAttributePanel.class);
	private IValidator validator = null;
	

	public EntityAttributePanel(String id, IModel model) {
		this(id, model, false); // default non editable is save
	}

	public EntityAttributePanel(String id, IModel model, final boolean editable) {
		super(id, model);
		init(editable);
	}

	public IValidator getValidator()
	{
		return validator;
	}

	public void setValidator(IValidator validator)
	{
		this.validator = validator;
	}
	
	private void init(boolean editable) {
		this.editable = editable;
		add(createPanel("childPanel"));
	}

	/**
	 * Create either the View or the Edit version of the panel
	 *
	 * @param id
	 * @return
	 */
	protected Panel createPanel(String id) {
		// construct classname for inner class
		String innerClassName = this.getClass().getName();  //.class.getName();
    	if (isEditable()) {
    		innerClassName += "$"+ this.getClass().getSimpleName() + "Edit";
    	} else {
    		innerClassName += "$"+ this.getClass().getSimpleName() + "View";
    	}
    	// create the inner class instance
    	Class<?> panelClass = null;
    	Panel panel = null;

		// Class.forName doesn't work with inner classes!
		// - panelClass = Class.forName(innerClassName);
		Class<?>[] innerClasses = this.getClass().getClasses();
		for(int i=0;i<innerClasses.length;i++){
			if(innerClasses[i].getName().equals(innerClassName)){
				panelClass = innerClasses[i];
			}
		}
		logger.debug("Requesting inner class with name: " + innerClassName);
		
		//System.out.println("AbstractDendroPanel: Requesting class with name: " + innerClassName);
		//if (panelClass == null) System.out.println("Class NOT found!");
		//else System.out.println("Class found!");
		if (panelClass == null)
			logger.error("Missing inner class: " + innerClassName);

		//panel = (Panel) panelClass.newInstance();
		// need to pass wicket parameters to constructor
		// note, the outer class (this) should be the first parameter,
		// otherwise you can't construct an inner class
		Class<?>[] parameterTypes = {getClass(), String.class, IModel.class};
		Constructor<?> constructor;
		try {
			constructor = panelClass.getConstructor(parameterTypes);
			// use id and model of outer class
			panel = (Panel) constructor.newInstance(this, id, getDefaultModel());
		// Note: following exceptions are result of programming errors
		// and should be handled as runtime exceptions
		} catch (SecurityException e) {
			logger.error("Could not create inner panel: " + innerClassName, e);
			throw( new InternalErrorException(e));
		} catch (NoSuchMethodException e) {
			logger.error("Could not create inner panel: " + innerClassName, e);
			throw( new InternalErrorException(e));
		} catch (IllegalArgumentException e) {
			logger.error("Could not create inner panel: " + innerClassName, e);
			throw( new InternalErrorException(e));
		} catch (InstantiationException e) {
			logger.error("Could not create inner panel: " + innerClassName, e);
			throw( new InternalErrorException(e));
		} catch (IllegalAccessException e) {
			logger.error("Could not create inner panel: " + innerClassName, e);
			throw( new InternalErrorException(e));
		} catch (InvocationTargetException e) {
			logger.error("Could not create inner panel: " + innerClassName, e);
			throw( new InternalErrorException(e));
		}

		return panel;
	}

	/**
	 * Note that there is no setter.
	 * @return true if editable false if not
	 */
	public boolean isEditable() {
		return editable;
	}
}
