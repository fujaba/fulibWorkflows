package org.fulib.workflows.events;

import java.util.Map;

public class Page extends BaseNote {
    private Map<String, String> content;

    public Map<String, String> getContent() {
        return content;
    }

    public void setContent(Map<String, String> content) {
        this.content = content;
    }
}
