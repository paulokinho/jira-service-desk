package com.bics.jira.mail;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.bc.project.component.ProjectComponent;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.status.Status;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.service.util.handler.MessageHandlerErrorCollector;
import com.atlassian.jira.web.util.AttachmentException;
import com.bics.jira.mail.model.mail.Attachment;
import com.bics.jira.mail.model.mail.MessageAdapter;

import javax.mail.MessagingException;
import java.util.Collection;
import java.util.Map;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 04.02.13 21:57
 */
public interface IssueHelper {
    MutableIssue create(User author, User assignee, Project project, IssueType issueType, ProjectComponent component, MessageAdapter message, Collection<User> watchers, MessageHandlerErrorCollector monitor) throws MessagingException, AttachmentException, CreateException;

    void comment(MutableIssue issue, Map<Status, String> transitions, MessageAdapter message, Collection<User> watchers, boolean stripQuotes, MessageHandlerErrorCollector monitor) throws MessagingException, AttachmentException, CreateException;
}
