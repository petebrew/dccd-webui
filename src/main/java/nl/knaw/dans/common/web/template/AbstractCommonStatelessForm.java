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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Note: was AbstractEasyStatelessForm
 */
public abstract class AbstractCommonStatelessForm extends AbstractCommonForm
{
    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Logger for this class.
     */
    static final Logger       LOGGER           = LoggerFactory.getLogger(AbstractCommonStatelessForm.class);

    /**
     * Constructor with model and and central feedback panel. Mark-up should contain this:
     *
     * <pre>
     *    &lt;p wicket:id=&quot;defaultFeedbackPanel&quot;&gt;Messages&lt;/p&gt;
     * </pre>
     *
     * @param wicketId wicketId of the form
     * @param model model of the form
     */
    public AbstractCommonStatelessForm(final String wicketId, final IModel model)
    {
        super(wicketId, model);
    }

    /**
     * Constructor with model and central feedback panel at choice. If <code>addCentralFeedBack == true</code>, mark-up
     * should contain this:
     *
     * <pre>
     *    &lt;p wicket:id=&quot;defaultFeedbackPanel&quot;&gt;Messages&lt;/p&gt;
     * </pre>
     *
     * @param wicketId
     *        wicket id
     * @param model
     *        model
     * @param addCentralFeedBack
     *        <code>true</code> for central feedback panel, <code>false</code> otherwise
     */
    public AbstractCommonStatelessForm(final String wicketId, final IModel model, boolean addCentralFeedBack)
    {
        super(wicketId, model, addCentralFeedBack);
    }

    /**
     * Set redirect to true for a stateless form.
     *
     * @return True if super.process is true.
     */
    @Override
    public boolean process()
    {
        // set redirect to true for a stateless form.
        setRedirect(true);
        return super.process();
    }

    /**
     * Set stateless hint to true for this form.
     *
     * @return True
     */
    @Override
    protected boolean getStatelessHint() // NOPMD: wicket method.
    {
        return true;
    }

}
