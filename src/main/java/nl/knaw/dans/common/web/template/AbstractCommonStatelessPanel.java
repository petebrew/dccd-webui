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
/**
 *
 */
package nl.knaw.dans.common.web.template;


import org.apache.wicket.model.IModel;

/**
 * Default stateless panel.
 *
 * Note: was AbstractEasyStatelessPanel
 * 
 * @author Herman Suijs
 */
public abstract class AbstractCommonStatelessPanel extends AbstractCommonPanel
{

    /**
     *
     */
    private static final long serialVersionUID = -8092666797589916733L;

    /**
     * Default constructor.
     *
     * @param wicketId wicket id
     */
    public AbstractCommonStatelessPanel(final String wicketId)
    {
        super(wicketId);
    }

    /**
     * Constructor with model.
     *
     * @param wicketId wicket id
     * @param model model
     */
    public AbstractCommonStatelessPanel(final String wicketId, final IModel model)
    {
        super(wicketId, model);
    }

    /**
     * Make Panel stateless.
     *
     * @return true
     */
    @Override
    public boolean getStatelessHint() // NOPMD: wicket method
    {
        return true;
    }

}
