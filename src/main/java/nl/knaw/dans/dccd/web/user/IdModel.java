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
package nl.knaw.dans.dccd.web.user;

import java.io.Serializable;

import org.apache.wicket.model.Model;

public class IdModel extends Model
{
    private static final long serialVersionUID = -698492390294313403L;
    public final static String ID_SEPERATOR = ":";
    private String valueAndId;

    public IdModel()
    {
    }

    @Override
    public Serializable getObject()
    {
        return valueAndId;
    }

    @Override
    public void setObject(Serializable object)
    {
        this.valueAndId = (String) object;
    }

    protected String getSelectedId()
    {
        String id = null;
        if (valueAndId != null)
        {
            int index = valueAndId.indexOf(ID_SEPERATOR);
            if (index > -1 && index < valueAndId.length()-2)
            {
                id = valueAndId.substring(index + 2);
            }
        }
        return id;
    }

}
