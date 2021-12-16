package org.fulib.workflows.generators.constructors;

import org.antlr.v4.runtime.misc.Pair;
import org.fulib.FulibTools;
import org.fulib.workflows.events.Data;
import org.fulib.yaml.YamlIdMap;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class DiagramConstructor {
    private STGroupFile fulibYamlGroup;

    public String buildDiagram(List<Data> notes, int index) {
        String objectYaml = buildFulibYamlNotation(notes);

        return generateDiagram(objectYaml, index);
    }

    private String buildFulibYamlNotation(List<Data> notes) {
        fulibYamlGroup = new STGroupFile(Objects.requireNonNull(this.getClass().getResource("../FulibYaml.stg")));
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

            if (value.contains("[") || value.contains(">")) {
                value = value.replaceAll("\\[", "");
                value = value.replaceAll("]", "");
                value = value.replaceAll(",", "");
                value = value.replaceAll(">", "");
            }

            st.add("type", type);
            st.add("value", value);

            attributesBody.append(st.render());
        }

        return attributesBody.toString();
    }

    private String generateDiagram(String objectYaml, int index) {
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
