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
package nl.knaw.dans.dccd.web.authn;

import nl.knaw.dans.dccd.web.DccdSession;

import org.apache.wicket.Application;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.link.Link;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logoff link.
 *
 * @author Herman Suijs
 */
public class LogoffLink extends Link
{
    /**
     * Serial version uid.
     */
    private static final long   serialVersionUID = 1L;

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER           = LoggerFactory.getLogger(LogoffLink.class);

    /**
     * Default constructor.
     *
     * @param wicketId Wicket id.
     */
    public LogoffLink(final String wicketId)
    {
        super(wicketId);
    }

//    /**
//     * When link is clicked.
//     */
//    @Override
//    public void onClick()
//    {
//        WaspSession session = (WaspSession) this.getSession();
//
//        LOGGER.debug("Logoutlink is clicked");
//
//        // Sign out of the session.
//        LoginContext logoffContext = new EasyLoginContext();
//        InjectorHolder.getInjector().inject(logoffContext);
//
//        session.logoff(logoffContext);
//        // Invalidate the session
//        session.invalidate();
//
//        // Redirect to the homepage
//        this.setResponsePage(Application.get().getHomePage());
//    }

    @Override
    public void onClick()
    {
        LOGGER.debug("Logoutlink is clicked");
        //EasySession session = (EasySession) getSession();
        DccdSession session = ((DccdSession)Session.get());
        session.setLoggedOff();
        setResponsePage(Application.get().getHomePage());
    }

//    /**
//     * Check visibility: only show when logged in.
//     *
//     * @return true if user is authenticated.
//     */
//    @Override
//    public boolean isVisible()
//    {
//        // Only show when logged in.
//        boolean visible = false;
//        Session session = this.getPage().getSession();
//        if (session instanceof WaspSession)
//        {
//            visible = ((WaspAuthorizationStrategy) ((WaspSession) session).getAuthorizationStrategy()).isUserAuthenticated();
//        }
//        return visible;
//    }

    @Override
    public boolean isVisible()
    {
        //return ((EasySession) getSession()).getUser() != null;
        return ((DccdSession)Session.get()).getUser() != null;
    }

    /**
     * Make component stateless.
     *
     * @return true
     */
    @Override
    public boolean getStatelessHint() // NOPMD: wicket method.
    {
        return true;
    }
}
