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

import nl.knaw.dans.dccd.model.EntityAttribute;
import nl.knaw.dans.dccd.model.InternalErrorException;
import nl.knaw.dans.dccd.model.UIMapEntry.Multiplicity;
import nl.knaw.dans.dccd.tridas.EmptyObjectFactory;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.tridas.schema.ControlledVoc;
import org.tridas.schema.Date;
import org.tridas.schema.DateTime;
import org.tridas.schema.NormalTridasLocationType;
import org.tridas.schema.NormalTridasMeasuringMethod;
import org.tridas.schema.NormalTridasShape;
import org.tridas.schema.NormalTridasUnit;
import org.tridas.schema.SeriesLink;
import org.tridas.schema.SeriesLinksWithPreferred;
import org.tridas.schema.TridasAddress;
import org.tridas.schema.TridasBedrock;
import org.tridas.schema.TridasCoverage;
import org.tridas.schema.TridasDating;
import org.tridas.schema.TridasDatingReference;
import org.tridas.schema.TridasDimensions;
import org.tridas.schema.TridasFile;
import org.tridas.schema.TridasGenericField;
import org.tridas.schema.TridasIdentifier;
import org.tridas.schema.TridasInterpretation;
import org.tridas.schema.TridasLaboratory;
import org.tridas.schema.TridasLastRingUnderBark;
import org.tridas.schema.TridasLocation;
import org.tridas.schema.TridasLocationGeometry;
import org.tridas.schema.TridasResearch;
import org.tridas.schema.TridasShape;
import org.tridas.schema.TridasSlope;
import org.tridas.schema.TridasSoil;
import org.tridas.schema.TridasStatFoundation;
import org.tridas.schema.TridasWoodCompleteness;
import org.tridas.schema.TridasVocabulary.LocationType;
import org.tridas.schema.Year;

/**
 *  For creating Panels
 *
 * TODO: unit testing
 *
 *
 * @author paulboon
 *
 */
public class EntityAtributePanelFactory {
	private static Logger logger = Logger.getLogger(EntityAtributePanelFactory.class);

	// Note: maybe get panel package string part from somewhere 
	final String PANEL_PACKAGE_NAME = "nl.knaw.dans.dccd.web.datapanels";
	
	// Create panel based on information in the DccdAttr
	public Panel createPanel(String id, EntityAttribute attr, boolean editable) 
	{
		Panel panel = null;

		if (attr.getEntry().getPanel().length() == 0 ) 
		{
			logger.warn("No panel class name specified, id: " + id);
			return null;
			// Could also implement fallback when no panel specified:
			// Use returntype information
			// Method m = Class.getMethod(attr.getEntry().getName());
			// if (m.getReturnType() == String.class) ...
			// String maps to TextPanel etc. etc.
		}

		Multiplicity multiplicity = attr.getEntry().getMultiplicity();
		if (multiplicity == Multiplicity.SINGLE)
		{
			panel = createSinglePanel(id, attr, editable);
		}
		else if (multiplicity == Multiplicity.OPTIONAL)
		{
			String fullClassName = PANEL_PACKAGE_NAME + "." + attr.getEntry().getPanel(); 
			Class<?> panelClass;
			try
			{
				panelClass = Class.forName(fullClassName);
				EntityAttributeOptionalPanel optionalPanel = new EntityAttributeOptionalPanel(panelClass, id, new Model(attr), editable);
				panel = (Panel)optionalPanel;
			}
			catch (ClassNotFoundException e)
			{
				logger.error("Could find panel class: " + fullClassName, e);
				throw( new InternalErrorException(e));
			}	
		}
		else if (multiplicity == Multiplicity.MULTIPLE)
		{
			// we need a repeater for the panel
			// construct he repeater and set the panel class it needs to repeat
			String fullClassName = PANEL_PACKAGE_NAME + "." + attr.getEntry().getPanel();
			Class<?> panelClass;
			try
			{
				panelClass = Class.forName(fullClassName);
				EntityAttributeRepeaterPanel repeaterPanel = new EntityAttributeRepeaterPanel(panelClass, id, new Model(attr), editable);
				panel = (Panel)repeaterPanel;
			}
			catch (ClassNotFoundException e)
			{
				logger.error("Could find panel class: " + fullClassName, e);
				throw( new InternalErrorException(e));
			}
		}
		else
		{
			logger.error("Unknown multipliciy: " + attr.getEntry().getMultiplicity());
			throw( new InternalErrorException());
		}
		return panel;
	}
		
	public Panel createSinglePanel(String id, EntityAttribute attr, boolean editable) 
	{
		Panel panel = null;
		
		String fullClassName = PANEL_PACKAGE_NAME + "." + attr.getEntry().getPanel();

		try 
		{
			// You can use DccdAttrPanels that support edit and view via inner classes
			// or you can use a (plain) Wicket Panel that only supports viewing
			Class<?> panelClass;
			panelClass = Class.forName(fullClassName);
			Class<?> parentClass = panelClass.getSuperclass();
			if (parentClass == EntityAttributePanel.class) 
			{
				// it's a panel with inner classes for edit and view
				Class<?>[] parameterTypes = {String.class, IModel.class, boolean.class};
				Constructor<?> constructor = panelClass.getConstructor(parameterTypes);
				panel = (EntityAttributePanel)constructor.newInstance(id, new Model(attr), editable);
			} 
			else 
			{
				logger.debug("Non- editable Panel used: " + fullClassName);
				// it must be a (normal, non editable) Panel
				Class<?>[] parameterTypes = {String.class, IModel.class};
				Constructor<?> constructor = panelClass.getConstructor(parameterTypes);
				panel = (Panel)constructor.newInstance(id, new Model(attr));
			}
			return panel;
		// Note: following exceptions are result of programming errors
		// and should be handled as runtime exceptions
		} 
		catch (ClassNotFoundException e) 
		{
			logger.error("Could not create panel: " + fullClassName, e);
			throw( new InternalErrorException(e));
		} 
		catch (SecurityException e) 
		{
			logger.error("Could not create panel: " + fullClassName, e);
			throw( new InternalErrorException(e));
		} 
		catch (NoSuchMethodException e) 
		{
			logger.error("Could not create panel: " + fullClassName, e);
			throw( new InternalErrorException(e));
		} 
		catch (IllegalArgumentException e) 
		{
			logger.error("Could not create panel: " + fullClassName, e);
			throw( new InternalErrorException(e));
		} 
		catch (InstantiationException e) 
		{
			logger.error("Could not create panel: " + fullClassName, e);
			throw( new InternalErrorException(e));
		} 
		catch (IllegalAccessException e) 
		{
			logger.error("Could not create panel: " + fullClassName, e);
			throw( new InternalErrorException(e));
		} 
		catch (InvocationTargetException e) 
		{
			logger.error("Could not create panel: " + fullClassName, e);
			throw( new InternalErrorException(e));
		}	
	}
	
	// Create objects for (repeating) panels
	// maybe put this in a separate factory
	// a large switch statement feels like we should have used inheritance...
	// and have each EnttityAtributePanel return a class
	//
	// or if the list gets longer use a hashMap to store the relations Panel - Object
	public Object createEmptyObject(final Class<?> panelClass)
	{
		Object object = null;
		
		// repeatables
		if (panelClass == TextPanel.class)
		{
			object = EmptyObjectFactory.create(String.class);
		}
		else if (panelClass == GenericFieldPanel.class)
		{
			object = EmptyObjectFactory.create(TridasGenericField.class);
		}
		else if (panelClass == ControlledVocabularyPanel.class)
		{
			object = EmptyObjectFactory.create(ControlledVoc.class);
		}
		else if (panelClass == ResearchPanel.class)
		{
			object = EmptyObjectFactory.create(TridasResearch.class);
		}
		else if (panelClass == FilePanel.class)
		{
			object = EmptyObjectFactory.create(TridasFile.class);			
		}
		else if (panelClass == LaboratoryPanel.class)
		{
			object = EmptyObjectFactory.create(TridasLaboratory.class);			
		}
		else if (panelClass == StatFoundationPanel.class)
		{
			object = EmptyObjectFactory.create(TridasStatFoundation.class);			
		}	
		else if (panelClass == SeriesLinkPanel.class)
		{
			object = EmptyObjectFactory.create(SeriesLink.class);			
		}
		// NEW, Optional Panel related classes
		else if (panelClass == DatePanel.class)
		{
			object = EmptyObjectFactory.create(Date.class);			
		}
		else if (panelClass == DateTimePanel.class)
		{
			object = EmptyObjectFactory.create(DateTime.class);			
		}
		//
		else if (panelClass == YearPanel.class)
		{
			object = EmptyObjectFactory.create(Year.class);			
		}
		else if (panelClass == IdentifierPanel.class)
		{
			object = EmptyObjectFactory.create(TridasIdentifier.class);			
		}
		else if (panelClass == CoveragePanel.class)
		{
			object = EmptyObjectFactory.create(TridasCoverage.class);			
		}
		else if (panelClass == LocationPanel.class)
		{
			object = EmptyObjectFactory.create(TridasLocation.class);			
		}
		//
		else if (panelClass == LocationGeometryPanel.class)
		{
			object = EmptyObjectFactory.create(TridasLocationGeometry.class);			
		}
		else if (panelClass == AddressPanel.class)
		{
			object = EmptyObjectFactory.create(TridasAddress.class);			
		}
		else if (panelClass == LocationTypePanel.class)
		{
			object = EmptyObjectFactory.create(NormalTridasLocationType.class);			
		}
		else if (panelClass == SeriesLinksWithPreferredPanel.class)
		{
			object = EmptyObjectFactory.create(SeriesLinksWithPreferred.class);			
		}
		else if (panelClass == ObjectTypePanel.class)
		{
			object = EmptyObjectFactory.create(ControlledVoc.class);			
		}
		else if (panelClass == TaxonPanel.class)
		{
			object = EmptyObjectFactory.create(ControlledVoc.class);			
		}
		else if (panelClass == ShapePanel.class)
		{
			object = EmptyObjectFactory.create(TridasShape.class);			
		}
		else if (panelClass == NormalTridasShapePanel.class)
		{
			object = EmptyObjectFactory.create(NormalTridasShape.class);		
		}
		else if (panelClass == DimensionsPanel.class)
		{
			object = EmptyObjectFactory.create(TridasDimensions.class);		
		}
		else if (panelClass == NormalTridasUnitPanel.class)
		{
			object = EmptyObjectFactory.create(NormalTridasUnit.class);		
		}
		else if (panelClass == SlopePanel.class)
		{
			object = EmptyObjectFactory.create(TridasSlope.class);		
		}
		else if (panelClass == SoilPanel.class)
		{
			object = EmptyObjectFactory.create(TridasSoil.class);		
		}
		else if (panelClass == BedrockPanel.class)
		{
			object = EmptyObjectFactory.create(TridasBedrock.class);		
		}
		else if (panelClass == WoodCompletenessPanel.class)
		{
			object = EmptyObjectFactory.create(TridasWoodCompleteness.class);		
		}
		else if (panelClass == NormalTridasMeasuringMethodPanel.class)
		{
			object = EmptyObjectFactory.create(NormalTridasMeasuringMethod.class);		
		}
		else if (panelClass == InterpretationPanel.class)
		{
			object = EmptyObjectFactory.create(TridasInterpretation.class);		
		}
		else if (panelClass == DatingPanel.class)
		{
			object = EmptyObjectFactory.create(TridasDating.class);		
		}
		else if (panelClass == DatingReferencePanel.class)
		{
			object = EmptyObjectFactory.create(TridasDatingReference.class);		
		}
		else if (panelClass == LastRingUnderBarkPanel.class)
		{
			object = EmptyObjectFactory.create(TridasLastRingUnderBark.class);		
		}
		else if (panelClass == XLinkPanel.class)
		{
			object = EmptyObjectFactory.create(SeriesLink.XLink.class);		
		}
		else if (panelClass == IdRefPanel.class)
		{
			object = EmptyObjectFactory.create(SeriesLink.IdRef.class);		
		}
		else if (panelClass == ProjectTypePanel.class)
		{
			object = EmptyObjectFactory.create(ControlledVoc.class);
		}
		else
		{
			logger.error("Could not create empty object for panel: " + panelClass.getSimpleName());
		}
		
		return object;
	}
	
}
