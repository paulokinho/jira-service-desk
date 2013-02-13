package com.bics.jira.mail.converter;

import com.bics.jira.mail.model.HandlerModel;
import com.bics.jira.mail.model.MessageAdapter;
import com.bics.jira.mail.model.MimeType;

import javax.mail.Message;
import java.util.Collection;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 10.02.13 1:54
 */
public interface BodyConverter {
    boolean isSupported(HandlerModel model, MessageAdapter message, boolean full);

    String convert(String body);
}
