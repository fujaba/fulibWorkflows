package org.fulib.workflows.generators.constructors;

import java.util.LinkedHashMap;
import java.util.Map;

public class GraphNode {
    public String className;
    public Map<String, String> attrMap = new LinkedHashMap<>();
    public Map<String, String> refsMap = new LinkedHashMap<>();

}
