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
import java.util.List;

public class ClassDiagramConstructor {
    private List<Association> associations = new ArrayList<>();

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
