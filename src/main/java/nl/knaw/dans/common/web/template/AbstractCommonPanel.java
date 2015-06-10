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

//import nl.knaw.dans.easy.domain.model.user.User;
//import nl.knaw.dans.easy.web.EasySession;
//import nl.knaw.dans.easy.web.wicket.AjaxEventListener;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.PackageResource;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;


/**
 * Generic panel.
 *
 * Note: was AbstractEasyPanel
 *
 * @author Herman Suijs
 */
public abstract class AbstractCommonPanel extends Panel
{

    private static final long serialVersionUID = 5529101351554863036L;


    /**
     * Default constructor.
     *
     * @param wicketId wicket id
     */
    public AbstractCommonPanel(final String wicketId)
    {
        super(wicketId);
        initAbstractEasyPanel();
    }

    /**
     * Constructor with model.
     *
     * @param wicketId wicket id
     * @param model model
     */
    public AbstractCommonPanel(final String wicketId, final IModel model)
    {
        super(wicketId, model);
        initAbstractEasyPanel();
    }



    protected void initAbstractEasyPanel()
	{
        // add CSS file that may accompany the page based on the name of the class
        String cssFilename = getClass().getSimpleName() + ".css";
        if (PackageResource.exists(getClass(), cssFilename, getLocale(), getStyle()))
            add(HeaderContributor.forCss(new ResourceReference(getClass(), cssFilename)));
	}

//	/**
//     * Check if authenticated.
//     *
//     * @return true if authenticated.
//     */
//    public final boolean isAuthenticated()
//    {
//        return getSessionUser() != null;
//    }

    protected void displayError(String msgToDisplay, String msgToLog)
    {
        error(new CaptionMessageModel(msgToDisplay).getObject());
    }

    /**
     * Model that will construct the caption string
     *
     * @author Eko Indarto
     */
    private class CaptionMessageModel extends AbstractReadOnlyModel
    {

        private static final long serialVersionUID = 1L;
        private String s;
        public CaptionMessageModel(String s){
            this.s = s;
        }
        /**
         * @see org.apache.wicket.model.AbstractReadOnlyModel#getObject()
         */
        @Override
        public String getObject()
        {
            return getString(s);
        }
    }

//    public EasySession getEasySession()
//    {
//        return (EasySession) getSession();
//    }
//
//    public User getSessionUser()
//    {
//        return getEasySession().getUser();
//    }
//
//    public boolean registerAjaxEventListener(String event, AjaxEventListener listener)
//    {
//        boolean success = false;
//        Page page = getPage();
//        if (page != null && page instanceof AbstractEasyPage)
//        {
//            ((AbstractEasyPage)page).registerAjaxEventListener(event, listener);
//            success = true;
//        }
//        return success;
//    }
//
//    public void handleAjaxEvent(String event, AjaxRequestTarget target)
//    {
//        Page page = getPage();
//        if (page != null && page instanceof AbstractEasyPage)
//        {
//            ((AbstractEasyPage)page).handleAjaxEvent(event, target);
//        }
//    }
}
