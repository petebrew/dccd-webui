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
package nl.knaw.dans.dccd.web.error;

import java.util.Iterator;

import nl.knaw.dans.dccd.web.HomePage;

import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.FeedbackMessages;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

/**
 * @author paulboon
 */
public class ErrorPage extends WebPage {
	@SuppressWarnings("unchecked")
	public ErrorPage() {
		// a component has called error(message)?
		String detailedErrorMsgStr = "";

		final FeedbackMessages fm = getSession().getFeedbackMessages();
		final Iterator<FeedbackMessage> iter = fm.iterator();
		while (iter.hasNext()) {
		   final FeedbackMessage message = iter.next();
		   if (message.getLevel() == FeedbackMessage.ERROR) {
			   detailedErrorMsgStr = (String)message.getMessage();
			   //break; // the first one found
			   // if no break; then we use the last one!
		   }
		}

		add(new BookmarkablePageLink("homeLink", HomePage.class));

		//detailed_error_msg
		add(new Label("detailed_error_msg", detailedErrorMsgStr));
	}
}

