package org.fulib.workflows.events;

import org.antlr.v4.runtime.misc.Pair;

import java.util.Map;

public class Page extends BaseNote {
    private Map<Integer, Pair<String, String>> content;

    public Map<Integer, Pair<String, String>> getContent() {
        return content;
    }

    public void setContent(Map<Integer, Pair<String, String>> content) {
        this.content = content;
    }
}
