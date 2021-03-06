package com.bics.jira.mail.converter.html;

/**
 * Java Doc here
 *
 * @author Victor Polischuk
 * @since 13/04/13 11:42
 */
public interface TreeContext {
    void stop();

    boolean hasChild(Tag tag);

    boolean hasParent(Tag tag);

    Iterable<Tag> path();

    Checkpoint checkpoint();

    String getInlineName(String cid);

    TreeContext ignore(boolean ignore);

    TreeContext formatted(boolean keepLineBreaks);

    TreeContext newLine();

    TreeContext whitespace();

    TreeContext symbol(String sequence);

    TreeContext text(String sequence);

    TreeContext trimContent();

    TreeContext content();

    interface Checkpoint {
        String diff();

        void rollback();
    }
}
