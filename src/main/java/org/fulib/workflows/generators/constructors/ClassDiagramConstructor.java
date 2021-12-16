package org.fulib.workflows.generators.constructors;

import org.antlr.v4.runtime.misc.Pair;
import org.fulib.FulibTools;
import org.fulib.builder.ClassModelManager;
import org.fulib.builder.Type;
import org.fulib.classmodel.ClassModel;
import org.fulib.classmodel.Clazz;
import org.fulib.workflows.events.Data;
import org.fulib.workflows.generation.Association;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ClassDiagramConstructor {

    private List<Data> objects;
    private final Map<String, Clazz> clazzMap = new HashMap<>();
    private final List<Association> associations = new ArrayList<>();
    private final List<String> reservedStringsForAssoc = new ArrayList<>();

    public String buildClassDiagram(List<Data> objects) {
        this.objects = objects;

        ClassModelManager mm = new ClassModelManager();

        // Create a map String, Clazz containing every possible class from the Data notes
        createClazz(mm);

        // Build all associations and put it into a global list
        // Also Build a list of attributes, that are not allowed to be created
        buildAssociations();

        // Create all attributes
        createAttributes(mm);

        // Create all associations
        createAssociations(mm);

        return generateClassDiagram(mm.getClassModel());
    }

    private void createAssociations(ClassModelManager mm) {
        for (Association assoc : associations) {
            if (assoc.srcClazz == null || assoc.tgtClazz == null) {
                continue;
            }

            mm.associate(assoc.srcClazz, assoc.srcName, assoc.srcCardi, assoc.tgtClazz, assoc.tgtName, assoc.tgtCardi);
        }
    }

    private void buildAssociations() {
        for (Data object : objects) {
            Association association = new Association();
            String currentClass = object.getName().split(" ")[0].toLowerCase();
            for (Integer integer : object.getData().keySet()) {
                Pair<String, String> pair = object.getData().get(integer);

                if (pair.a.contains(".")) {
                    String[] split = pair.a.split("\\.");
                    reservedStringsForAssoc.add(split[0]);

                    association.srcName = split[0];

                    String backName = pair.b;
                    if (backName.contains("[")) {
                        backName = cleanupString(backName);
                        association.tgtCardi = Type.MANY;
                    } else {
                        association.tgtCardi = Type.ONE;
                    }

                    association.tgtName = backName;

                    Clazz currentClazz = clazzMap.get(currentClass);
                    association.srcClazz = currentClazz;

                    if (association.tgtName.equals(association.srcName)) {
                        // Self association
                        association.tgtClazz = currentClazz;
                    } else {
                        // Association between two classes
                        String objectName = getCorrectDataEntry(object, split[0], association);
                        String tgtClassName = findClazz(objectName);
                        if (tgtClassName != null) {
                            association.tgtClazz = clazzMap.get(tgtClassName.toLowerCase());
                        }
                    }

                    associations.add(association);
                    reservedStringsForAssoc.add(backName);
                }
            }
        }
    }

    private String getCorrectDataEntry(Data object, String tgtName, Association association) {
        for (Integer integer : object.getData().keySet()) {
            Pair<String, String> pair = object.getData().get(integer);
            String key = pair.a;

            if (key.equals(tgtName)) {
                if (pair.b.contains("[")) {
                    association.srcCardi = Type.MANY;
                } else {
                    association.srcCardi = Type.ONE;
                }

                return pair.b;
            }
        }
        return null;
    }

    private String findClazz(String searchName) {
        for (Data object : objects) {
            String objectName = object.getName().split(" ")[1];

            searchName = cleanupString(searchName);

            if (objectName.equals(searchName)) {
                return object.getName().split(" ")[0];
            }
        }
        return null;
    }

    private void createAttributes(ClassModelManager mm) {
        for (Data object : objects) {
            for (Integer integer : object.getData().keySet()) {
                Pair<String, String> pair = object.getData().get(integer);
                String attributeName = pair.a;

                String className = object.getName().split(" ")[0].toLowerCase();

                Clazz clazz = clazzMap.get(className);

                if (!reservedStringsForAssoc.contains(attributeName) && !attributeName.contains(".")) {
                    String type = evaluateAttributeType(pair.b);
                    mm.haveAttribute(clazz, attributeName, type);
                }
            }
        }
    }

    private void createClazz(ClassModelManager mm) {
        for (Data object : objects) {
            String className = object.getName().split(" ")[0];

            clazzMap.put(className.toLowerCase(), mm.haveClass(className));
        }
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


    // Helper Methods
    private String cleanupString(String string) {
        String result = string;

        result = result.replaceAll("\\[", "");
        result = result.replaceAll("]", "");
        result = result.replaceAll(",", "");

        return result;
    }

    private String evaluateAttributeType(String value) {
        // Check for numbers
        Pattern intPattern = Pattern.compile("^\\d+");

        boolean isInt = intPattern.matcher(value).find();

        if (isInt) {
            return Type.INT;
        }

        return Type.STRING;
    }
}
