package org.fulib.workflows.events;

import org.antlr.v4.runtime.misc.Pair;

import java.util.Map;

public class Data extends BaseNote {
    private Map<Integer, Pair<String, String>> data;

    public Map<Integer, Pair<String, String>> getData() {
        return data;
    }

    public Data setData(Map<Integer, Pair<String, String>> data) {
        this.data = data;

        return this;
    }
}
