package org.fulib.workflows.events;

import org.antlr.v4.runtime.misc.Pair;

import java.util.Map;

public class ClassDef extends BaseNote {
    private Map<Integer, Pair<String, String>> fields;

    public Map<Integer, Pair<String, String>> getFields() {
        return fields;
    }

    public void setFields(Map<Integer, Pair<String, String>> fields) {
        this.fields = fields;
    }
}
