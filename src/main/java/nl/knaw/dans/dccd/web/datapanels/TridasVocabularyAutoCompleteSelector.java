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

import java.util.Iterator;

import nl.knaw.dans.dccd.application.services.DccdVocabularyService;

import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.model.IModel;

public class TridasVocabularyAutoCompleteSelector extends AutoCompleteTextField
{
	private static final long	serialVersionUID	= 8373074909210076172L;

	private String vocabularyName;
	
	public TridasVocabularyAutoCompleteSelector(String id, IModel object, String vocabularyName)
	{
		super(id, object);
		this.vocabularyName = vocabularyName;
	}

	@Override
	protected Iterator getChoices(String text)
	{
		Iterator iterator = null;
		
		iterator = DccdVocabularyService.getService().getTermsStartingWith(vocabularyName, text).iterator();		
		
		return iterator;
	}

}
