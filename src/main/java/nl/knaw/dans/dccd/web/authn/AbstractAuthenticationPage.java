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

import nl.knaw.dans.dccd.application.services.DccdUserService;
import nl.knaw.dans.dccd.authn.Authentication;
import nl.knaw.dans.dccd.web.DccdSession;
import nl.knaw.dans.dccd.web.base.BasePage;

import org.apache.wicket.IPageMap;
import org.apache.wicket.PageParameters;
import org.apache.wicket.Session;

public class AbstractAuthenticationPage extends BasePage
{
    //private static Logger logger = LoggerFactory.getLogger(AbstractAuthenticationPage.class);

    public AbstractAuthenticationPage()
    {
        super();
    }

    public AbstractAuthenticationPage(final PageParameters params)
    {
        super(params);
    }

    public AbstractAuthenticationPage(final IPageMap pageMap, final PageParameters params)
    {
        super(pageMap, params);
    }

    public boolean signIn(Authentication authentication)
    {
         boolean signedIn;
         DccdUserService.getService().authenticate(authentication);
         if (authentication.isCompleted())
         {
             signedIn = true;
             //getEasySession().setLoggedIn(authentication.getUser());
             ((DccdSession)Session.get()).setLoggedIn(authentication.getUser());
         }
         else
         {
             signedIn = false;
             //getEasySession().setLoggedIn(null);
             ((DccdSession)Session.get()).setLoggedIn(null);
         }

         return signedIn;
     }
}
