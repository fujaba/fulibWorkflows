package org.fulib.workflows.events;

import org.antlr.v4.runtime.misc.Pair;

import java.util.Map;

public class Event extends BaseNote {
    private Map<Integer, Pair<String, String>> data;

    public Map<Integer, Pair<String, String>> getData() {
        return data;
    }

    public void setData(Map<Integer, Pair<String, String>> data) {
        this.data = data;
    }
}
