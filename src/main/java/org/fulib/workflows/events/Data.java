package org.fulib.workflows.events;

import org.antlr.v4.runtime.misc.Pair;

import java.util.Map;

/**
 * Processes Data note from event storming
 */
public class Data extends BaseNote {
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
     * @return data object
     */
    public Data setData(Map<Integer, Pair<String, String>> data) {
        this.data = data;

        return this;
    }
}
