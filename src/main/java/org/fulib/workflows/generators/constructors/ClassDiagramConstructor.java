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

        // Create all associations and put it into a global list
        buildAssociations();

        // Create all attributes
        createAttributes(mm);

        return generateClassDiagram(mm.getClassModel());
    }

    private void buildAssociations() {
        for (Data object : objects) {
            Association association = new Association();
            for (Integer integer : object.getData().keySet()) {
                Pair<String, String> pair = object.getData().get(integer);

                if (pair.a.contains(".")) {
                    String[] split = pair.a.split("\\.");
                    reservedStringsForAssoc.add(split[0]);

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

                    if (association.srcName.equals(association.tgtName)) {
                        // Self association
                        association.srcClazz = clazzMap.get(association.srcName);
                        association.tgtClazz = clazzMap.get(association.tgtName);
                    } else {
                        //TODO Other stuff
                    }

                    associations.add(association);
                    reservedStringsForAssoc.add(backName);
                }
            }
        }
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

    private String evaluateAttributeType(String value) {
        // Check for numbers
        Pattern intPattern = Pattern.compile("^\\d+");

        boolean isInt = intPattern.matcher(value).find();

        if (isInt) {
            return Type.INT;
        }

        return Type.STRING;
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
}
