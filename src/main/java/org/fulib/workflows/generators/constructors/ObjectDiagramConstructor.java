package org.fulib.workflows.generators.constructors;

import org.antlr.v4.runtime.misc.Pair;
import org.fulib.FulibTools;
import org.fulib.workflows.events.Data;
import org.fulib.yaml.YamlObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The ObjectDiagramConstructor builds an objectdiagram from a list of data
 * events from an fulibWorkflows Board.
 */
public class ObjectDiagramConstructor {
    /**
     * Builds object description in fulibYaml syntax and generates a svg object
     * diagram via fulibTools
     *
     * @param notes list of data notes
     * @return classdiagram svg file content as string
     */
    public String buildObjectDiagram(List<Data> notes) {
        Map<String, YamlObject> yamlGraph = buildFulibGraphDiagram(notes);

        FulibTools.objectDiagrams().withScale(2).dumpSVG("./tmp/_yamlGraph.svg", yamlGraph.values());
        try {
            return Files.readString(Path.of("./tmp/_yamlGraph.svg"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    public Map<String, YamlObject> buildFulibGraphDiagram(List<Data> notes) {
        Map<String, YamlObject> yamlGraph = new LinkedHashMap<>();

        // first collect all nodes
        for (Data data : notes) {
            // add data content to graph
            String objName = data.getName().split(" ")[1];
            String className = data.getName().split(" ")[0];

            YamlObject yamlObject = yamlGraph.computeIfAbsent(objName, k -> new YamlObject(objName));
            yamlObject.setType(className);
        }

        // now collect attrs and refs
        for (Data data : notes) {
            // add data content to graph
            String objName = data.getName().split(" ")[1];
            String className = data.getName().split(" ")[0];
            YamlObject yamlObj = yamlGraph.computeIfAbsent(objName, k -> new YamlObject(objName, className));

            Map<Integer, Pair<String, String>> objData = data.getData();
            for (Pair<String, String> pair : objData.values()) {
                String key = pair.a;
                String value = pair.b;

                if (value.startsWith("[")) {
                    // list of references
                    value = value.substring(1, value.length() - 1);
                    String[] split = value.split(",");
                    for (String id : split) {
                        id = id.trim();
                        YamlObject tgtObj = yamlGraph.computeIfAbsent(id, k -> new YamlObject(k));
                        yamlObj.with(key, tgtObj);
                    }
                } else if (yamlGraph.get(value) == null) {
                    // attr
                    yamlObj.put(key, value);
                } else {
                    // refs
                    yamlObj.put(key, yamlGraph.get(value));
                }
            }
        }

        return yamlGraph;
    }
}
