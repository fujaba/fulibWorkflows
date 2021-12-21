package org.fulib.workflows.generators.constructors;

import org.antlr.v4.runtime.misc.Pair;
import org.fulib.FulibTools;
import org.fulib.workflows.events.Data;
import org.fulib.yaml.YamlIdMap;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * The ObjectDiagramConstructor builds an objectdiagram from a list of data events from an fulibWorkflows Board.
 */
public class ObjectDiagramConstructor {
    private STGroupFile fulibYamlGroup;

    /**
     * Builds object description in fulibYaml syntax and generates a svg object diagram via fulibTools
     * @param notes list of data notes
     * @param index references the index of the objectdiagram
     * @return classdiagram svg file content as string
     */
    public String buildObjectDiagram(List<Data> notes, int index) {
        String objectYaml = buildFulibYamlNotation(notes);

        return generateObjectDiagram(objectYaml, index);
    }

    private String buildFulibYamlNotation(List<Data> notes) {
        URL resource = PageConstructor.class.getResource("FulibYaml.stg");

        fulibYamlGroup = new STGroupFile(Objects.requireNonNull(resource));
        StringBuilder yamlBody = new StringBuilder();

        for (Data note : notes) {
            ST st = fulibYamlGroup.getInstanceOf("object");

            String[] s = note.getName().split(" ");

            st.add("name", s[1]);
            st.add("type", s[0]);
            st.add("attributes", buildAttributes(note));

            yamlBody.append(st.render());
        }

        return yamlBody.toString();
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

            if (value.contains("[") || value.contains(">")) {
                value = value.replaceAll("\\[", "");
                value = value.replaceAll("]", "");
                value = value.replaceAll(",", "");
            }

            st.add("type", type);
            st.add("value", value);

            attributesBody.append(st.render());
        }

        return attributesBody.toString();
    }

    private String generateObjectDiagram(String objectYaml, int index) {
        String fileName = "tmp/test/diagram_" + index;
        String result = "";

        YamlIdMap idMap = new YamlIdMap();
        Object root = idMap.decode(objectYaml);

        fileName = FulibTools.objectDiagrams().dumpSVG(fileName, root);

        try {
            result = Files.readString(Path.of(fileName + ".svg"));

            Files.deleteIfExists(Path.of(fileName + ".svg"));

            Files.deleteIfExists(Path.of("tmp/test/"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
