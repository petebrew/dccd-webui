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
package nl.knaw.dans.dccd.web;

import java.util.HashMap;
import java.util.Map;

import nl.knaw.dans.common.lang.user.User;
import nl.knaw.dans.common.wicket.components.CommonSession;
import nl.knaw.dans.dccd.web.upload.CombinedUpload;

import org.apache.wicket.Page;
import org.apache.wicket.Request;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Note CancelLinks want to redirect using lastPage from the CommonSession
// some common components use this so we need to extend it 
public class DccdSession extends CommonSession //WebSession 
{
    private static final Logger logger = LoggerFactory.getLogger(DccdSession.class);
    private static final long serialVersionUID = -873871221323672425L;
	private String defaultLanguageCode = "en";
	String contentLanguageCode = defaultLanguageCode;
		
	public DccdSession(Request request) {
		super(request);
	}

	// if you use java >= 1.5 you can make use of covariant return types
	public static DccdSession get() {
		return (DccdSession)Session.get();
	}

	/**
	 *  User Athentication
	 *  Note pboon: this was copied from the EasySession
	 *  Maybe we should use a DccdUser instead of a User
	 */
    private User                                     user;

    /**
     * Get the user of this session, or <code>null</code> if no user is logged in.
     *
     * @return the user of this session
     */
    public User getUser()
    {
        return user;
    }

    /**
     * Set the user of this session.
     *
     * @param user
     *        user of this session
     */
    public void setLoggedIn(User user)
    {
        this.user = user;
        //reset();
    }

    public void setLoggedOff()
    {
        user = null;

        cleanupFeedbackMessages();
        reset();
        //super.invalidate();
        super.clear();
    }

    // Reset this Session to a state that is appropriate for the new situation after setLoggedOff/setLoggedIn.
    private void reset()
    {
        redirectPageMap.clear();
    }

    /**
     * RedirectPage members are used for going back to the previous page
     */
    private Map<Class<? extends Page>, Page> redirectPageMap = new HashMap<Class<? extends Page>, Page>();

    public void setRedirectPage(Class<? extends Page> toPage, Page returnPage)
    {
        redirectPageMap.put(toPage, returnPage);
        if (logger.isDebugEnabled())
        {
            logger.debug("Added redirect page for " + toPage + ". size of redirectPageMap=" + redirectPageMap.size());
        }
    }

    public Page getRedirectPage(Class<? extends Page> fromPage)
    {
        Page page = redirectPageMap.remove(fromPage);
        if (logger.isDebugEnabled())
        {
            logger.debug("Removed redirect page for " + fromPage + ". size of redirectPageMap="
                    + redirectPageMap.size());
        }
        return page;
    }

    public boolean hasRedirectPage(Class<? extends Page> fromPage)
    {
        return redirectPageMap.containsKey(fromPage);
    }

    public boolean isLoggedIn()
    {
    	return null != user;
    }

    /**
     * A page can have a language independent of the webbrowser/session Locale
     * for instance when displaying information from a source in a different language.
     * 
     * @return
     */
	public String getContentLanguageCode()
	{
		return contentLanguageCode;
    }
    
	public void setContentLanguageCode(String contentLanguageCode)
	{
		this.contentLanguageCode = contentLanguageCode;
	}

	// used by the UploadFilesPage, only one per session!
	private final CombinedUpload combinedUpload = new CombinedUpload();
	public CombinedUpload getCombinedUpload() {
		return combinedUpload;
	}
	
	// for all search results (lists) that are paged
	private Integer resultCount=10;

	public Integer getResultCount() {
		return resultCount;
	}

	public void setResultCount(Integer resultCount) {
		this.resultCount = resultCount;
	}
}
