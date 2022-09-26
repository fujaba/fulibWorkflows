package org.fulib.workflows.events;

import org.antlr.v4.runtime.misc.Pair;

import java.util.Map;

/**
 * Processes div note for pages from event storming
 */
public class Div extends BaseNote {
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
     * @param content Map containing content from a div note
     */
    public void setContent(Map<Integer, Pair<String, String>> content) {
        this.content = content;
    }
}
