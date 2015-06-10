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

public class OrganisationModel extends Model
{
	private static final long	serialVersionUID	= -2180746715196999815L;
	private String organisationId;

    public OrganisationModel()
    {
    }

    @Override
    public Serializable getObject()
    {
        return organisationId;
    }

    @Override
    public void setObject(Serializable object)
    {
        this.organisationId = (String) object;
    }

    protected String getSelectedId()
    {
        return organisationId;
    }
}
