package com.bics.jira.mail.handler;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.issue.AttachmentManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.service.util.handler.MessageHandlerErrorCollector;
import com.atlassian.jira.util.Predicate;
import com.bics.jira.mail.CommentOnlyModelValidator;
import com.bics.jira.mail.IssueHelper;
import com.bics.jira.mail.IssueLookupHelper;
import com.bics.jira.mail.UserHelper;
import com.bics.jira.mail.model.mail.MessageAdapter;
import com.bics.jira.mail.model.service.CommentOnlyModel;

import java.util.Collection;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 03.02.13 12:29
 */
public class CommentOnlyMessageHandler extends ServiceDeskMessageHandler<CommentOnlyModel> {

    public CommentOnlyMessageHandler(JiraAuthenticationContext jiraAuthenticationContext, AttachmentManager attachmentManager, CommentOnlyModelValidator modelValidator, IssueHelper issueHelper, UserHelper userHelper, IssueLookupHelper issueLookupHelper) {
        super(jiraAuthenticationContext, attachmentManager, modelValidator, issueHelper, userHelper, issueLookupHelper);
    }

    @Override
    protected CommentOnlyModel createModel() {
        return new CommentOnlyModel();
    }

    @Override
    protected Predicate<User> searchPredicate(final MessageAdapter adapter, final MessageHandlerErrorCollector monitor) {
        return new Predicate<User>() {
            @Override
            public boolean evaluate(User user) {
                MutableIssue issue = findIssue(adapter, monitor);

                return issue != null && userHelper.canCommentIssue(user, issue);
            }
        };
    }

    @Override
    protected MutableIssue findIssue(MessageAdapter adapter, MessageHandlerErrorCollector monitor) {
        return issueLookupHelper.lookupByKey(adapter.getSubject(), model.getResolvedBefore(), monitor);
    }

    @Override
    protected User chooseAssignee(Collection<User> users, String subject) {
        return null;
    }

    @Override
    protected MutableIssue create(User author, User assignee, MessageAdapter adapter, Collection<User> watchers, MessageHandlerErrorCollector monitor) throws CreateException {
        throw new CreateException("Cannot find an issue for mail with subject: " + adapter.getSubject());
    }
}