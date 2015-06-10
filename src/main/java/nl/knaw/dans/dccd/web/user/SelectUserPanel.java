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

import nl.knaw.dans.common.web.template.AbstractCommonPanel;
import nl.knaw.dans.dccd.application.services.DccdUserService;
import nl.knaw.dans.dccd.application.services.UserServiceException;
import nl.knaw.dans.dccd.model.DccdUser;
import nl.knaw.dans.dccd.web.error.ErrorPage;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelectUserPanel extends AbstractCommonPanel
{
	private static Logger logger = LoggerFactory.getLogger(SelectUserPanel.class);
    private static final long serialVersionUID = 5428378223455873788L;
    protected final IdModel model;
    private IModel labelModel;
    private boolean initiated;
    private boolean selectionValid = false;

	public SelectUserPanel(String wicketId)
    {
        super(wicketId);
        model = new IdModel();
    }

    public IModel getLabelModel()
    {
        return labelModel;
    }

    public void setLabelModel(IModel labelModel)
    {
        this.labelModel = labelModel;
    }

    public boolean isSelectionValid()
	{
		return selectionValid;
	}

    public String getSelectedId()
    {
        return model.getSelectedId();
    }

    // DCCD specific because of the UserService and User
    public DccdUser getSelectedUser()
    {
        String userId = getSelectedId();
        DccdUser user = null;
        if (userId == null)
        {
            error("No user selected.");
        }
        else
        {
            try
            {
                user = DccdUserService.getService().getUserById(userId);
            }
            catch (UserServiceException e)
            {
                error("Could not retieve user with id: " + userId + " error: " + e.getMessage());
                throw new RestartResponseException(ErrorPage.class);
            }
        }
        return user;
    }

    @Override
    protected void onBeforeRender()
    {
        if (!initiated)
        {
            init();
            initiated = true;
        }
        super.onBeforeRender();
    }

    protected UserSelector getUserSelector()
    {
    	return new UserSelector("autoCompleteTextField", model);
    }
    
    private void init()
    {
        add(new FeedbackPanel("supFeedback", new IFeedbackMessageFilter()
        {
            private static final long serialVersionUID = 3253987728694803331L;

            public boolean accept(FeedbackMessage message)
            {
                return message.getReporter().getId().equals(SelectUserPanel.this.getId());
            }
        }));

        add(new Label("supLabel", labelModel == null ? new ResourceModel("sup.label") : labelModel));

        final UserSelector userSelector = getUserSelector();//new UserSelector("autoCompleteTextField", model);
        add(userSelector);

        // Note: if the input string has a valid id part ": normaltestuser" then it is valid
        // Note2, maybe only send onSelectionChanged if validity changed, and rename it ...
        userSelector.add(new AjaxFormComponentUpdatingBehavior("onchange")
        {
			private static final long serialVersionUID = 1L;
			protected void onUpdate(final AjaxRequestTarget target)
        	{
        		// looks like an onblur, so it wont be disabled when we type...!
        		logger.debug("UserSelector onchange");

        		determineIfUserIsValid();
        		onSelectionChanged(target);
        	}
        });
        userSelector.add(new AjaxFormComponentUpdatingBehavior("onkeyup")
        {
			private static final long serialVersionUID = 1L;
			protected void onUpdate(final AjaxRequestTarget target)
        	{
        		// looks like an onblur, so it wont be disables when we type...!
        		logger.debug("UserSelector onkeyup");

        		determineIfUserIsValid();
				onSelectionChanged(target);
        	}
        });

    }

    
    private void determineIfUserIsValid()
    {
    	selectionValid = false;
		String id = getSelectedId();

		if (userIdValid(id))
		{
			try
			{
				DccdUser user = DccdUserService.getService().getUserById(getSelectedId());
				if (user != null)
				{
					selectionValid = true;
				}
			}
			catch (UserServiceException e)
			{
				logger.debug("### ignored exception");
			}
		}

    	if (selectionValid == false) logger.debug("invalid user");
    	else logger.debug("valid user");
    }

    private boolean userIdValid(String id)
    {
		if (id == null || id.isEmpty() || id.trim().isEmpty())
		{
			return false;
		}
		return true;
    }

    protected void onSelectionChanged(final AjaxRequestTarget target)
    {
    	
    	// do nothing, override to handle changes
    }
}
