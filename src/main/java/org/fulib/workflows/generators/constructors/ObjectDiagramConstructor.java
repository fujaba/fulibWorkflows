package org.fulib.workflows.generators.constructors;

import org.antlr.v4.runtime.misc.Pair;
import org.fulib.FulibTools;
import org.fulib.StrUtil;
import org.fulib.tools.GraphDiagram;
import org.fulib.workflows.events.Data;
import org.fulib.workflows.utils.IncorrectDataValueException;
import org.fulib.yaml.YamlIdMap;
import org.fulib.yaml.YamlObject;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;

/**
 * The ObjectDiagramConstructor builds an objectdiagram from a list of data
 * events from an fulibWorkflows Board.
 */
public class ObjectDiagramConstructor {
    private STGroupFile fulibYamlGroup;

    private String currentNoteName;

    /**
     * Builds object description in fulibYaml syntax and generates a svg object
     * diagram via fulibTools
     *
     * @param notes list of data notes
     * @param index references the index of the objectdiagram
     * @return classdiagram svg file content as string
     */
    public String buildObjectDiagram(List<Data> notes, int index) {
        // String objectYaml = buildFulibYamlNotation(notes);
        Map<String, YamlObject> yamlGraph = buildFulibGraphDiagram(notes);

        FulibTools.objectDiagrams().dumpSVG("./tmp/_yamlGraph.svg", yamlGraph.values());
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

            // - data: Box b003
            // product: mug
            // amount: 50
            // place: just_ordered
            Map<Integer, Pair<String, String>> objData = data.getData();
            for (Pair<String, String> pair : objData.values()) {
                String key = pair.a;
                String value = pair.b;

                if (value.startsWith("[")) {
                    // list of references
                    value = value.substring(1, value.length() -1);
                    String[] split = value.split(",");
                    for (String id : split) {
                        id = id.trim();
                        YamlObject tgtObj = yamlGraph.computeIfAbsent(id, k -> new YamlObject(k));
                        yamlObj.with(key, tgtObj);
                    }
                }
                else if (yamlGraph.get(value) == null) {
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

    private String buildFulibYamlNotation(List<Data> notes) {

        URL resource = PageConstructor.class.getResource("FulibYaml.stg");

        fulibYamlGroup = new STGroupFile(Objects.requireNonNull(resource));
        StringBuilder yamlBody = new StringBuilder();

        Data currentNote = notes.get(notes.size() - 1);
        buildFulibYamlObject(currentNote, yamlBody);

        for (int i = 0; i < notes.size() - 1; i++) {
            Data note = notes.get(i);
            buildFulibYamlObject(note, yamlBody);
        }

        return yamlBody.toString();
    }

    private void buildFulibYamlObject(Data note, StringBuilder yamlBody) {
        ST st = fulibYamlGroup.getInstanceOf("object");

        String[] s = note.getName().split(" ");

        try {
            st.add("name", s[1]);
            currentNoteName = s[1];
        } catch (Exception e) {
            try {
                throw new IncorrectDataValueException("Invalid value. Needs to be '- data: <ClassName> <objectName>'");
            } catch (IncorrectDataValueException ex) {
                ex.printStackTrace();
            }
        }
        st.add("type", s[0]);
        st.add("attributes", buildAttributes(note));

        yamlBody.append(st.render());
    }

    private String buildAttributes(Data note) {
        StringBuilder attributesBody = new StringBuilder();

        for (int i = 0; i < note.getData().size(); i++) {
            Pair<String, String> pair = note.getData().get(i);
            ST st = fulibYamlGroup.getInstanceOf("attribute");

            String type = pair.a;
            String value = pair.b;

            if (type.contains(".")) {
                continue;
            }

            if (value.startsWith("[") && value.endsWith("]")) {
                value = value.replaceAll("\\[", "");
                value = value.replaceAll("]", "");
                value = value.replaceAll(",", "");
            } else {
                value = "\"" + value + "\"";
            }

            st.add("type", type);
            st.add("value", value);

            attributesBody.append(st.render());
        }

        return attributesBody.toString();
    }

    private String generateObjectDiagram(String objectYaml, int index) {
        String fileName = "genTmp/diagrams/diagram_" + index;
        String result = "";

        YamlIdMap idMap = new YamlIdMap();
        Object root = idMap.decode(objectYaml);
        Set<Object> keySet = idMap.getIdObjMap().keySet();

        fileName = FulibTools.objectDiagrams().dumpSVG(fileName, keySet);

        try {
            result = Files.readString(Path.of(fileName + ".svg"));

            Files.deleteIfExists(Path.of(fileName + ".svg"));

            Files.deleteIfExists(Path.of("genTmp/diagrams/"));
            Files.deleteIfExists(Path.of("genTmp/"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
