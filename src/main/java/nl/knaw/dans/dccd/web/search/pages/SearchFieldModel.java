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
package nl.knaw.dans.dccd.web.search.pages;

import java.io.Serializable;

import nl.knaw.dans.common.lang.ClassUtil;
import nl.knaw.dans.common.lang.search.Field;

import org.apache.wicket.model.Model;

public class SearchFieldModel extends Model
{
	private static final long serialVersionUID = -6033853618498949502L;
	
	private final String propertyName;

	public SearchFieldModel(Serializable data, String propertyName)
	{
		if (propertyName == null)
			throw new RuntimeException("SearchFieldModel cannot have null value");
		this.propertyName = propertyName;
		super.setObject(data);
	}
	
	// Note: Do not restict to SimpleFields like EOF!
	@SuppressWarnings("unchecked")
	public void setObject(Object input)
	{
		try
		{
			Object data = super.getObject();
			java.lang.reflect.Field field = data.getClass().getDeclaredField(propertyName);

			if (ClassUtil.classImplements(field.getType(), Field.class))
			{
				Field searchField;
				searchField = (Field) field.get(data);
				searchField.setValue(input);
			}
			else
				throw new RuntimeException("programmer error. Property \'" + propertyName + " \' does not correspond to a search field.");
		}
		catch (Exception e)
		{
			throw new RuntimeException("programmer error. Property \'" + propertyName + " \' does not exist or is not accessible.");
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public Serializable getObject()
	{
		try
		{
			Object data = super.getObject();
			java.lang.reflect.Field field = data.getClass().getDeclaredField(propertyName);
			if (ClassUtil.classImplements(field.getType(), Field.class))
			{
				Field searchField;
				searchField = (Field) field.get(data);
				return (Serializable) searchField.getValue();
			}
			else
				throw new RuntimeException("programmer error. Property \'" + propertyName + " \' does not correspond to a search field.");
		}
		catch (Exception e)
		{
			throw new RuntimeException("programmer error. Property \'" + propertyName + " \' does not exist or is not accessible.");
		}
	}
	
}	
