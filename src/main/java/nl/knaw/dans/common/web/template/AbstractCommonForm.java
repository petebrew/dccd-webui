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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.validation.FormComponentFeedbackIndicator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.RequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Superclass of form to use for every form within the application. 
 * 
 * Note: was AbstractEasyForm
 */
public abstract class AbstractCommonForm extends Form
{


    public static final String COMMON_FORM_ERROR     = "commonFormError";

    /**
     * Partial wicket id for FormComponentFeedbackIndicator.
     *
     * @see #add(FormComponent, IModel)
     */
    public static final String  FEEDBACK                 = "feedback";

    /**
     * Partial wicket id for FeedbackPanel with ComponentFeedbackMessageFilter.
     *
     * @see #addWithComponentFeedback(FormComponent, IModel)
     */
    public static final String COMPONENT_FEEDBACK = "componentFeedback";

    /**
     * Separator for feedback components.
     */
    public static final String  SEPARATOR                = "-";

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER                   = LoggerFactory.getLogger(AbstractCommonForm.class);

    /**
     * Wicket component id.
     */
    public static final String  DEFAULT_FEEDBACK_PANEL   = "defaultFeedbackPanel";

    /**
     * Wicket component id.
     */
    public static final String  COMMON_FEEDBACK_PANEL    = "commonFeedbackPanel";

    private static final long   serialVersionUID         = 3422879786964201174L;

    private final List<String>  myComponentsWithFeedBack = new ArrayList<String>();

    /**
     * Initialize the same for every constructor.
     */
    private void init(boolean addCentralFeedBack)
    {
        if (addCentralFeedBack)
        {
            FeedbackPanel defaultFeedbackPanel = new FeedbackPanel(DEFAULT_FEEDBACK_PANEL)
            {
                /**
                 * Serial version uid.
                 */
                private static final long serialVersionUID = 3009482931829951367L;

                @Override
                public boolean isVisible()
                {
                    return this.anyMessage();
                }

            };
            add(defaultFeedbackPanel.setOutputMarkupId(true));
        }
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
    public AbstractCommonForm(final String wicketId, final IModel model, boolean addCentralFeedBack)
    {
        super(wicketId, model);
        init(addCentralFeedBack);
    }

    /**
     * Constructor with model and and central feedback panel. Mark-up should contain this:
     *
     * <pre>
     *    &lt;p wicket:id=&quot;defaultFeedbackPanel&quot;&gt;Messages&lt;/p&gt;
     * </pre>
     *
     * @param wicketId
     *        wicketId of the form
     * @param model
     *        model of the form
     */
    public AbstractCommonForm(final String wicketId, final IModel model)
    {
        super(wicketId, model);
        init(true);
    }

    /**
     * Add a FormComponent and FormComponentFeedbackIndicator. A red star will show at
     *
     * <pre>
     *    &lt;span wicket:id=&quot;[formComponent.id]-feedback&quot;&gt;feedback&lt;/span&gt;
     * </pre>
     *
     * where [formComponent.id] is the id of <code>formComponent</code>.
     * <p/>
     * The FormComponent itself is at
     *
     * <pre>
     *    &lt;input id=&quot;tagId&quot; wicket:id=&quot;[formComponent.id]&quot; type=&quot;text&quot;/&gt;
     * </pre>
     *
     * @param formComponent
     *        FormComponent to add
     * @param label
     *        label used in feedback messages
     */
    protected void add(final FormComponent formComponent, final IModel label)
    {
        // Add the component to the form
        super.add(formComponent);

        // Set its label
        formComponent.setLabel(label);

        // Add feedback panel to display
        FormComponentFeedbackIndicator feedbackIndicator = new FormComponentFeedbackIndicator(formComponent.getId()
                + SEPARATOR + FEEDBACK);
        feedbackIndicator.setIndicatorFor(formComponent);
        // LOGGER.debug("FeedbackIndicator " + feedbackIndicator.getId() + " added to the form " + this.getId());
        feedbackIndicator.setOutputMarkupId(true);

        super.add(feedbackIndicator);
    }

    /**
     * Add a FormComponent and FormComponent feedback panel. Feedback messages will show at
     *
     * <pre>
     *    &lt;span wicket:id=&quot;[formComponent.id]-componentFeedback&quot;&gt;feedback&lt;/span&gt;
     * </pre>
     *
     * where [formComponent.id] is the id of <code>formComponent</code>.
     * <p/>
     * The FormComponent itself is at
     *
     * <pre>
     *    &lt;input id=&quot;tagId&quot; wicket:id=&quot;[formComponent.id]&quot; type=&quot;text&quot;/&gt;
     * </pre>
     *
     * @param formComponent
     *        FormComponent to add
     * @param labelModel
     *        label used in feedback messages
     * @return the feedbackPanel set on the given formComponent
     */
    protected FeedbackPanel addWithComponentFeedback(final FormComponent formComponent, final IModel labelModel)
    {
        ComponentFeedbackMessageFilter filter = new ComponentFeedbackMessageFilter(formComponent);
        FeedbackPanel feedBackPanel = new FeedbackPanel(formComponent.getId() + SEPARATOR + COMPONENT_FEEDBACK, filter)
        {

            private static final long serialVersionUID = -521216440119152641L;

            @Override
            public boolean isVisible()
            {
                return this.anyMessage();
            }

        };
        feedBackPanel.setOutputMarkupId(true);
        formComponent.setLabel(labelModel);
        super.add(formComponent);
        super.add(feedBackPanel);
        myComponentsWithFeedBack.add(formComponent.getId());
        //LOGGER.debug("added: " + formComponent.getId() + ", " + feedBackPanel.getId());
        return feedBackPanel;
    }

    /**
     * Add a common feedbackPanel. Feedback messages will show at
     *
     * <pre>
     *    &lt;span wicket:id=&quot;commonFeedbackPanel&quot;&gt;feedback&lt;/span&gt;
     * </pre>
     *
     * This feedback panel has a filter to exclude messages already shown in component feedback panels.
     *
     * @return the common feedback panel
     * @see #addWithComponentFeedback(FormComponent, IModel)
     */
    protected FeedbackPanel addCommonFeedbackPanel()
    {
        FeedbackPanel commonFeedBackPanel = new FeedbackPanel(COMMON_FEEDBACK_PANEL, new CommonLevelFeedbackFilter())
        {

            private static final long serialVersionUID = 4975408510868865407L;

            @Override
            public boolean isVisible()
            {
                return this.anyMessage();
            }

        };

        add(commonFeedBackPanel).setOutputMarkupId(true);
        return commonFeedBackPanel;
    }

    /**
     * Executed on submit of the form.
     */
    @Override
    protected abstract void onSubmit();


    @Override
    protected void onError()
    {
        error(getString(COMMON_FORM_ERROR));
        super.onError();
    }

//    /**
//     * Get the current user of the session or <code>null</code> if no user is logged in.
//     *
//     * @return current user or null
//     */
//    protected User getSessionUser()
//    {
//        return ((EasySession) getSession()).getUser();
//    }

    /**
     * Disable all components in the form, make any submit component invisible and make a link visible.
     *
     * @param enableComponents
     *        list of all components
     */
    protected void disableForm(final String[] enableComponents)
    {
        // Disable all components.
        final AttributeModifier disabler = new AttributeModifier("disabled", true, new Model("true"));
        this.visitChildren(FormComponent.class, new IVisitor()
        {
            public Object component(final Component component)
            {
                // Submitlinks and buttons are removed.
                if (SubmitLink.class.isAssignableFrom(component.getClass())
                        || Button.class.isAssignableFrom(component.getClass()))
                {
                    component.setVisible(false);
                }
                else
                {
                    // Others disabled.
                    component.add(disabler);
                }

                return IVisitor.CONTINUE_TRAVERSAL;
            }
        });
        // Disable the form.
        this.setEnabled(false);

        // Enable some specific components
        for (String componentName : enableComponents)
        {
            this.get(componentName).setVisible(true);
        }
    }

//    /**
//     * Create the mail message.
//     *
//     * @deprecated send mail from business layer. see also {@link nl.knaw.dans.easy.util.MailComposer}.
//     *
//     * @param resourceForMessage
//     *        resource to get for this mail message
//     * @param user
//     *        user to send the mail to.
//     * @param validationUrl
//     *        url to put in the mail
//     * @return mail message
//     */
//    protected String createMailMessage(final String resourceForMessage, final ApplicationUser user,
//            final String validationUrl)
//    {
//        return getString(resourceForMessage, new CompoundPropertyModel(new Serializable()
//        {
//            /**
//             * Serial version uid.
//             */
//            private static final long serialVersionUID = 1L;
//
//            /**
//             * Get username.
//             *
//             * @return username.
//             */
//            @SuppressWarnings("unused")
//            public String getUsername()
//            {
//                return user.getUserId();
//            }
//
//            /**
//             * Get url.
//             *
//             * @return url
//             */
//            @SuppressWarnings("unused")
//            public String getUrl()
//            {
//                return validationUrl;
//            }
//
//        }));
//
//    }

    /**
     * Create the url to the specified page.
     *
     * @param pageClass
     *        Page to create url for.
     * @param parameterMap
     *        parameterMap
     * @return Url string
     */
    protected String createPageURL(Class<? extends WebPage> pageClass, final Map<String, String> parameterMap)
    {
        String absUrl = RequestUtils.toAbsolutePath((String) this.urlFor(pageClass, new PageParameters(parameterMap)));
        LOGGER.debug("absolute url=" + absUrl);
        final String pageUrl = absUrl;

        LOGGER.debug("URL: " + pageUrl);
        return pageUrl;
    }

    /**
     * Create a parameterMap for given parameters to a page accessible with a token.
     *
     * @param paramNameUserId
     *        name of the parameter for userId
     * @param paramUserId
     *        user id
     * @param paramNameDateTime
     *        name of the parameter for dateTime
     * @param paramDateTime
     *        date time of the request
     * @param paramNameToken
     *        name of the parameter for the token
     * @param paramToken
     *        token
     * @return parameterMap
     */
    protected Map<String, String> createParameterMap(final String paramNameUserId, final String paramUserId,
            final String paramNameDateTime, final String paramDateTime, final String paramNameToken,
            final String paramToken)
    {
        Map<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put(paramNameUserId, paramUserId);
        parameterMap.put(paramNameDateTime, paramDateTime);
        parameterMap.put(paramNameToken, paramToken);
        return parameterMap;
    }

    /**
     * IFeedbackMessageFilter that filters out messages already shown in component feedback panels.
     *
     * @author ecco Feb 25, 2009
     */
    class CommonLevelFeedbackFilter implements IFeedbackMessageFilter
    {

        private static final long serialVersionUID = -4625910785421379795L;

        public boolean accept(FeedbackMessage message)
        {
        	Component reporter = message.getReporter();
			if (reporter == null)
				// TOOD: check this
        		return false;
        	else
        		return !myComponentsWithFeedBack.contains(reporter.getId());
        }

    }

}
