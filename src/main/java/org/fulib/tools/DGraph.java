package org.fulib.tools;

import java.util.LinkedHashMap;
import java.util.Map;

public class DGraph extends Node {

    private Map<String, Node> nodes = new LinkedHashMap<>();

    @Override
    public DGraph toGraph() {
        return this;
    }

    public Map<String, Node> getNodes() {
        return nodes;
    }
}
