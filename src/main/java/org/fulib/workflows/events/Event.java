package org.fulib.workflows.events;

import org.antlr.v4.runtime.misc.Pair;

import java.util.Map;

/**
 * Processes Event note from event storming
 */
public class Event extends BaseNote {
    private Map<Integer, Pair<String, String>> data;

    /**
     * Getter for data field
     * @return Map of data, key = index, value = Pair containing description and the value
     */
    public Map<Integer, Pair<String, String>> getData() {
        return data;
    }

    /**
     * Setter for data field
     * @param data Map containing additional data from a data note
     * @return event object
     */
    public Event setData(Map<Integer, Pair<String, String>> data) {
        this.data = data;
        return this;
    }
}
