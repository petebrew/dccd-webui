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

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("unchecked")
public class EntityAttributeListItem implements Serializable {
	private static final long serialVersionUID = -5710770857377961265L;
	private List list;
	int index;

	public EntityAttributeListItem(List list, int index) {
		this.list = list;
		this.index = index;
	}

	public Object getItem() {
		//System.err.println("DccdAttrListItem get, index: " + index + " list: " + list);
		return list.get(index);
	}

	public void setItem(Object item) {
		//System.err.println("DccdAttrListItem set, index: " + index + " list: " + list + " item: " + item);
		list.set(index, item);
	}
}
