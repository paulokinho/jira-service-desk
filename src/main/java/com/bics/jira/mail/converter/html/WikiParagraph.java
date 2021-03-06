package com.bics.jira.mail.converter.html;

import org.jsoup.nodes.Node;

/**
 * Java Doc here
 *
 * @author Victor Polischuk
 * @since 13/04/13 11:41
 */
public class WikiParagraph implements NodeFormatter {
    @Override
    public boolean isSupported(TreeContext context, Node node) {
        return Tag.P.is(node) || Tag.BR.is(node);
    }

    @Override
    public void format(TreeContext context, Node node) {
        context.newLine().content();
    }
}
