package org.fulib.workflows.events;

import org.antlr.v4.runtime.misc.Pair;

import java.util.Map;

/**
 * Processes Page note from event storming
 */
public class Page extends BaseNote {
    private Map<Integer, Pair<String, String>> content;

    /**
     * Getter for content field
     * @return Map of content, key = index, value = Pair containing description and the value
     */
    public Map<Integer, Pair<String, String>> getContent() {
        return content;
    }

    /**
     * Setter for content field
     * @param content Map containing content from a page note
     */
    public void setContent(Map<Integer, Pair<String, String>> content) {
        this.content = content;
    }
}
