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

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.tridas.schema.TridasLocation;
import org.tridas.schema.TridasLocationGeometry;
import net.opengis.gml.schema.*;
import nl.knaw.dans.dccd.model.EntityAttribute;
import nl.knaw.dans.dccd.model.InternalErrorException;

import javax.xml.bind.Marshaller;
import javax.xml.bind.util.JAXBSource;

/**
 * @author paulboon
 * 
 * From the schema:
 * <xs:choice>
 *      <xs:element ref="gml:Point"/>
 *      <xs:element ref="gml:Polygon"/>
 * </xs:choice>
 * So, either a point or a Polygon, but not both
 *
 */
public class LocationGeometryPanel extends EntityAttributePanel {
	private static final long	serialVersionUID	= -2500351891913478626L;
	private static Logger logger = Logger.getLogger(EntityAttribute.class);

	public LocationGeometryPanel(String id, IModel model) {
		super(id, model, false);
	}

	public LocationGeometryPanel(String id, IModel model, boolean editable) {
		super(id, model, editable);
		//super(id, model, false); // disable editing
	}

	public class LocationGeometryPanelView extends Panel {
		private static final long serialVersionUID = 4284862480966211029L;

		public LocationGeometryPanelView(String id, IModel model) {
			super(id, model);

			// TODO use EntityAttribute			
			TridasLocationGeometry locGeom = (TridasLocationGeometry)model.getObject();
	
			// Contains either a gml Point or a gml Polygon
			// so, at least a panel for either one of those
			// point has:
			// - id, and srcName
			//   a pos: space separated list of doubles (coordinates)?
			// - optional description(string) and list of names
	
			String idStr = "";
			String srcNameStr = "";
			String descriptionStr = "";
			String coordinateStr = "";
	
			// check for null
			if (locGeom != null) {
				// populate  a small table
				PointType point = locGeom.getPoint();
				if (point != null) {
					// it's a point
					idStr = point.getId();
					srcNameStr = point.getSrsName();
					if (point.isSetDescription()) {
						descriptionStr = point.getDescription();
					}
					// do we have list of names
					if (point.isSetNames()) {
						// TODO: implement a repeater for this
						//List<CodeType> names = point.getName();
						//use RepeaterPanel here?
						//add(new XXXRepeaterPanel("xxx_panel", new Model(new XXXListWrapper(names))));
					}
					// get coordinates... <gml:coordinates> or <gml:pos> ?
					Pos pos = point.getPos();
					// convert to a string
					coordinateStr = "";
					if (pos != null)
					{
						List<Double> coordinates = pos.getValues();
						for (int i=0; i < coordinates.size(); i++) {
							coordinateStr += coordinates.get(i) + " ";
						}
					}
				}
				// Note: check Polygon also
				// it's either a point or a polygon
				if (locGeom.isSetPolygon()) {
					PolygonType poly = locGeom.getPolygon();
					idStr = poly.getId();
					srcNameStr = poly.getSrsName();
					if (poly.isSetDescription()) {
						descriptionStr = poly.getDescription();
					}
					// maybe remove the coordinates table entry?
					// or only show the exterior
				}
			}
	
	        add(new TextViewPanel("coordinates", new Model(coordinateStr)));
	        add(new TextViewPanel("id", new Model(idStr)));
	        add(new TextViewPanel("srcName", new Model(srcNameStr)));
	        add(new TextViewPanel("description", new Model(descriptionStr)));
	
			// polygon has a lot in common, but not a pos
			// instead, possible one exterior and many interior rings?
			// that has a posList ... is a list of coordinates
			//
	        // Solution:
	        // produce GML string instead of handling the complex information
	        //
	        String xmlStr = "";
	        if (locGeom != null) {
				JAXBContext jaxbContext;
				try {
					jaxbContext = JAXBContext.newInstance("org.tridas.schema");
					//jaxbContext = JAXBContext.newInstance("net.opengis.gml.schema");
					Marshaller marshaller = jaxbContext.createMarshaller();
					// setup marshaller
					marshaller.setProperty( Marshaller.JAXB_FRAGMENT, Boolean.TRUE );
					marshaller.setProperty( Marshaller.JAXB_ENCODING, "UTF-8" );
					marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
					//Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION
					//marshaller.setProperty( Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION, "org.tridas.schema" );
	
					StringWriter writer = new StringWriter();
					marshaller.marshal( locGeom, writer );
	
					// try to produce true GML ?
					// getting rid of the ns2 and ns3 would be ok!
					// and removing the locationGeometry?
					//
				    //if (locGeom.isSetPoint()) {
				    //	marshaller.marshal( locGeom.getPoint(), writer );
				    //}
				    //if (locGeom.isSetPolygon()) {
				    //	marshaller.marshal( locGeom.getPolygon(), writer );
				    //}
					xmlStr = writer.toString();
				} catch (JAXBException e) {
					logger.error("Could not marshall to xml as string", e);
					throw( new InternalErrorException(e));
				}
	        }
			Label gmlLabel = new Label("gml", new Model(xmlStr));
			add(gmlLabel);
		}
	}
	
	public class LocationGeometryPanelEdit extends Panel {
		private static final long	serialVersionUID	= 4936683079463020657L;

		public LocationGeometryPanelEdit(String id, IModel model) {
			super(id, model);

			// get the TridasLocation object
			TridasLocationGeometry locGeom = (TridasLocationGeometry)((EntityAttribute) model.getObject()).getEntryObject();
			
			//Label gmlLabel = new Label("gml", new Model(xmlStr));
			//add(gmlLabel);
			TextArea textArea = new TextArea("gml", new LocationGeometryTextModel(locGeom));
			add(textArea);
		}
	}
	
	public class LocationGeometryTextModel extends Model
	{
		private static final long	serialVersionUID	= -6842630129014230360L;
		TridasLocationGeometry locGeom;
		LocationGeometryTextModel(TridasLocationGeometry locGeom)
		{
			this.locGeom = locGeom;
		}
		
		@Override
		public String toString()
		{
	        // produce GML string instead of handling the complex information
	        //
	        String xmlStr = "";
	        if (locGeom != null) {
				JAXBContext jaxbContext;
				try {
					jaxbContext = JAXBContext.newInstance("org.tridas.schema");
					//jaxbContext = JAXBContext.newInstance("net.opengis.gml.schema");
					Marshaller marshaller = jaxbContext.createMarshaller();
					// setup marshaller
					marshaller.setProperty( Marshaller.JAXB_FRAGMENT, Boolean.TRUE );
					marshaller.setProperty( Marshaller.JAXB_ENCODING, "UTF-8" );
					marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
					//Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION
					//marshaller.setProperty( Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION, "org.tridas.schema" );
	
					StringWriter writer = new StringWriter();
					marshaller.marshal( locGeom, writer );
	
					// try to produce true GML ?
					// getting rid of the ns2 and ns3 would be ok!
					// and removing the locationGeometry?
					//
				    //if (locGeom.isSetPoint()) {
				    //	marshaller.marshal( locGeom.getPoint(), writer );
				    //}
				    //if (locGeom.isSetPolygon()) {
				    //	marshaller.marshal( locGeom.getPolygon(), writer );
				    //}
					xmlStr = writer.toString();
				} catch (JAXBException e) {
					logger.error("Could not marshall to xml as string", e);
					throw( new InternalErrorException(e));
				}
	        }
	        return xmlStr;
		}
		
		public void setObject(Object object)
		{
			// Construct point or polygon and replace in locGeom
			// assume it is a string
			//logger.debug("GML string: " + object.toString());
			String xmlStr = object.toString();
			
			// see in the entity's how to unmarshall
			JAXBContext jaxbContext = null;
			Object result = null;
			try
			{
				jaxbContext = JAXBContext.newInstance("org.tridas.schema");
				Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
				//result = unmarshaller.unmarshal(new JAXBSource(jaxbContext, xmlStr));
				ByteArrayInputStream input = new ByteArrayInputStream (xmlStr.getBytes());
				result = unmarshaller.unmarshal(input);

				// should be a location geometry (TridasLocationGeometry)
				if (result instanceof TridasLocationGeometry)
				{
					TridasLocationGeometry newLocGeom = (TridasLocationGeometry)result;
					// copy points and or polygons
					locGeom.setPoint(newLocGeom.getPoint());
					locGeom.setPolygon(newLocGeom.getPolygon());
				}
				else
				{
					// it's no good!
					logger.debug("Wrong Class of parsed GML string: " + result.getClass().getSimpleName());
				}

			}
			catch (JAXBException e)
			{
				// e.printStackTrace();
				// input error?
				//throw new InternalErrorException(e);
				
				// ignore!!!
			}
		}

		@Override
		public Serializable getObject()
		{
			return toString();
		}
		
		
	}
}
