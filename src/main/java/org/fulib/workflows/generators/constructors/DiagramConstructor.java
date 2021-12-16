package org.fulib.workflows.generators.constructors;

import org.antlr.v4.runtime.misc.Pair;
import org.fulib.FulibTools;
import org.fulib.builder.ClassModelManager;
import org.fulib.builder.Type;
import org.fulib.classmodel.ClassModel;
import org.fulib.classmodel.Clazz;
import org.fulib.workflows.events.Data;
import org.fulib.workflows.generation.Association;
import org.fulib.yaml.YamlIdMap;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DiagramConstructor {
    private STGroupFile fulibYamlGroup;

    private List<Association> associations = new ArrayList<>();

    public String buildObjectDiagram(List<Data> notes, int index) {
        String objectYaml = buildFulibYamlNotation(notes);

        return generateObjectDiagram(objectYaml, index);
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

    public String buildClassDiagram(List<Data> objects) {
        ClassModelManager mm = new ClassModelManager();

        List<Clazz> clazzList = createClazz(mm, objects);
        List<String> reservedStringsForAssoc = buildAssocsAndReturnAssocNames(objects, clazzList);
        createAttributes(mm, clazzList, objects, reservedStringsForAssoc);

        for (Association association : associations) {
            mm.associate(association.srcClazz, association.srcName, association.srcCardi, association.tgtClazz, association.tgtName, association.tgtCardi);
        }

        return generateClassDiagram(mm.getClassModel());
    }

    private List<String> buildAssocsAndReturnAssocNames(List<Data> objects, List<Clazz> clazzList) {
        List<String> result = new ArrayList<>();

        for (Data object : objects) {
            Association association = new Association();
            for (Integer integer : object.getData().keySet()) {
                Pair<String, String> pair = object.getData().get(integer);

                if (pair.a.contains(".")) {
                    String[] split = pair.a.split("\\.");
                    result.add(split[0]);

                    association.tgtName = split[0];

                    String backName = pair.b;
                    if (backName.contains("[")) {
                        backName = backName.replaceAll("\\[", "");
                        backName = backName.replaceAll("]", "");
                        association.srcCardi = Type.MANY;
                    } else {
                        association.srcCardi = Type.ONE;
                    }

                    association.srcName = backName;

                    // TODO get src and target clazz.....

                    if (association.srcName.equals(association.tgtName)) {
                        // Self association
                        association.srcClazz = getClazzByName(association.srcName, clazzList, true);
                        association.tgtClazz = getClazzByName(association.tgtName, clazzList, true);
                    }


                    associations.add(association);
                    result.add(backName);
                }
            }
        }

        return result;
    }

    private void createAttributes(ClassModelManager mm, List<Clazz> clazzList, List<Data> objects, List<String> reservedStringsForAssoc) {
        for (Data object : objects) {
            for (Integer integer : object.getData().keySet()) {
                Pair<String, String> pair = object.getData().get(integer);
                String attributeName = pair.a;

                Clazz clazz = getClazzByName(object.getName().split(" ")[0], clazzList, false);

                if (!reservedStringsForAssoc.contains(attributeName)) {
                    mm.haveAttribute(clazz, attributeName, Type.STRING);
                }
            }
        }
    }

    private Clazz getClazzByName(String s, List<Clazz> clazzList, boolean lowercase) {
        for (Clazz clazz : clazzList) {
            String clazzName = clazz.getName();

            if (lowercase) {
                clazzName = clazzName.toLowerCase();
            }

            if (s.equals(clazzName)) {
                return clazz;
            }
        }
        return null;
    }

    private List<Clazz> createClazz(ClassModelManager mm, List<Data> objects) {
        List<Clazz> result = new ArrayList<>();

        for (Data object : objects) {
            String className = object.getName().split(" ")[0];

            Clazz clazz = mm.haveClass(className);
            result.add(clazz);
        }

        return result;
    }

    private String generateClassDiagram(ClassModel classModel) {
        String fileName = "tmp/test/classdiagram";
        String result = "";

        fileName = FulibTools.classDiagrams().dumpSVG(classModel, fileName);

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
