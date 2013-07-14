package com.bics.jira.mail.handler;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.exception.PermissionException;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.service.util.handler.MessageHandler;
import com.atlassian.jira.service.util.handler.MessageHandlerContext;
import com.atlassian.jira.service.util.handler.MessageHandlerErrorCollector;
import com.atlassian.jira.service.util.handler.MessageHandlerExecutionMonitor;
import com.atlassian.jira.util.Predicate;
import com.atlassian.jira.util.collect.CollectionUtil;
import com.atlassian.jira.web.util.AttachmentException;
import com.atlassian.mail.MailUtils;
import com.bics.jira.mail.IssueHelper;
import com.bics.jira.mail.ModelValidator;
import com.bics.jira.mail.UserHelper;
import com.bics.jira.mail.model.CreateOrCommentModel;
import com.bics.jira.mail.model.mail.MessageAdapter;
import org.apache.commons.lang.StringUtils;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 03.02.13 12:29
 */
public class CommentOnlyMessageHandler implements MessageHandler {
    private final JiraAuthenticationContext jiraAuthenticationContext;
    private final ModelValidator modelValidator;
    private final IssueHelper issueHelper;
    private final UserHelper userHelper;

    private final Map<String, String> params = new HashMap<String, String>();
    private final CreateOrCommentModel model = new CreateOrCommentModel();
    private boolean valid;

    public CommentOnlyMessageHandler(JiraAuthenticationContext jiraAuthenticationContext, ModelValidator modelValidator, IssueHelper issueHelper, UserHelper userHelper) {
        this.jiraAuthenticationContext = jiraAuthenticationContext;
        this.modelValidator = modelValidator;
        this.issueHelper = issueHelper;
        this.userHelper = userHelper;
    }

    @Override
    public void init(Map<String, String> params, MessageHandlerErrorCollector monitor) {
        this.params.clear();
        this.params.putAll(params);

        validateModel(monitor);
    }

    @Override
    public boolean handleMessage(Message message, MessageHandlerContext context) throws MessagingException {
        MessageHandlerExecutionMonitor monitor = context.getMonitor();

        if (!valid && !validateModel(monitor)) {
            monitor.warning("CreateOrCommentWebModel did not pass the validation. Emergency exit.");
            return false;
        }

        MessageAdapter adapter = new MessageAdapter(message);

        InternetAddress[] addresses = adapter.getFrom();

        Collection<User> authors = userHelper.ensure(addresses, model.isCreateUsers(), model.isNotifyUsers(), monitor);

        User author = CollectionUtil.findFirstMatch(authors, new Predicate<User>() {
            @Override
            public boolean evaluate(User user) {
                return userHelper.canCreateIssue(user, model.getProject());
            }
        });

        if (author == null && model.getReporterUser() != null) {
            monitor.warning("Message sender(s) '" + StringUtils.join(MailUtils.getSenders(message), ",")
                    + "' do not have permission to create an issue. Default reporter fallback.");

            author = model.getReporterUser();
        } else if (author == null) {
            monitor.error("Message sender(s) '" + StringUtils.join(MailUtils.getSenders(message), ",")
                    + "' do not have corresponding users in JIRA. Message will be ignored");
            return false;
        }

        User original = jiraAuthenticationContext.getLoggedInUser();
        jiraAuthenticationContext.setLoggedInUser(author);

        try {
            issueHelper.process(author, model, adapter, monitor);
        } catch (CreateException e) {
            monitor.error(e.getMessage());
            return false;
        } catch (AttachmentException e) {
            monitor.error(e.getMessage());
            return false;
        } catch (PermissionException e) {
            monitor.error(e.getMessage());
            return false;
        } finally {
            jiraAuthenticationContext.setLoggedInUser(original);
        }

        return true;
    }

    private boolean validateModel(MessageHandlerErrorCollector monitor) {
        return (this.valid = modelValidator.populateHandlerModel(model, params, monitor));
    }
}