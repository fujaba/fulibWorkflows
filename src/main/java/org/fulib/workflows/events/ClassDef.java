package org.fulib.workflows.events;

import java.util.Map;

public class ClassDef extends BaseNote {
    private Map<String, String> fields;

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }
}
