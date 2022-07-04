package org.fulib.workflows.generators.constructors;

import org.antlr.v4.runtime.misc.Pair;
import org.fulib.FulibTools;
import org.fulib.builder.ClassModelManager;
import org.fulib.builder.Type;
import org.fulib.classmodel.ClassModel;
import org.fulib.classmodel.Clazz;
import org.fulib.workflows.events.Data;
import org.fulib.workflows.utils.Association;
import org.fulib.yaml.YamlObject;
import org.stringtemplate.v4.compiler.CodeGenerator.conditional_return;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.swing.Spring;

/**
 * The ClassDiagramConstructor builds a classdiagram from all data events from
 * an fulibWorkflows Board via fulib.
 */
public class ClassDiagramConstructor {

    private List<Data> objects;
    private final Map<String, Clazz> clazzMap = new HashMap<>();
    private final List<Association> associations = new ArrayList<>();
    private final List<String> reservedStringsForAssoc = new ArrayList<>();
    private Map<String, YamlObject> yamlGraph;
    private ClassModelManager mm;

    public ClassModelManager getMm() {
        return mm;
    }

    /**
     * Builds a class model using fulib and generates a svg class diagram
     *
     * @param objects   list of data notes
     * @param yamlGraph
     * @return classdiagram svg file content as string
     */
    public String buildClassDiagram(List<Data> objects, Map<String, YamlObject> yamlGraph) {
        this.objects = objects;
        this.yamlGraph = yamlGraph;

        mm = new ClassModelManager();

        createClazz(mm);

        for (YamlObject yamlObject : yamlGraph.values()) {
            if (yamlObject.getType() == null) {
                continue;
            }

            Clazz myClass = mm.haveClass(yamlObject.getType());
            mm.haveAttribute(myClass, "name", "String");
            for (Entry<String, Object> entry : yamlObject.getProperties().entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                if (key.contains(".")) {
                    continue;
                }

                YamlObject yamlValue = getOneYamlValue(value);

                if (yamlValue != null && yamlValue.getType() != null) {
                    Clazz valueClass = mm.haveClass(yamlValue.getType());
                    int valueSize = getSize(value);
                    Entry<String, Object> revEntry = findReverseReference(yamlValue, yamlObject);
                    if (revEntry != null) {
                        String revKey = revEntry.getKey();
                        Object revValue = revEntry.getValue();
                        YamlObject revYamlObject = getOneYamlValue(revValue);
                        int revSize = getSize(revValue);
                        try {
                            mm.haveRole(myClass, key, valueSize, valueClass, revKey, revSize);

                        } catch (Exception e) {
                            // assoc already there from the reverse side, ignore
                        }
                    }
                }
                else if ( ! key.startsWith(".") && ! key.equals("type")) {
                    String type = learnTypeFromValue(value);
                    try {
                        mm.haveAttribute(myClass, key, type);
                    } catch (Exception e) {
                    }
                }

            }
        }

        String classDiagramString = generateClassDiagram(mm.getClassModel());

        if (mm.getClassModel().getClasses().size() > 0) {
            return classDiagramString;
        } else {
            return null;
        }
    }

    private String learnTypeFromValue(Object value) {
        try {
            int parseInt = Integer.parseInt(value.toString());
            return "int";
        } catch (Exception e) {
        }

        try {
            double parse = Double.parseDouble(value.toString().replace(',', '.'));
            return "double";
        } catch (Exception e) {
        }

        return "String";
    }

    private YamlObject getOneYamlValue(Object value) {
        if (value instanceof Collection set) {
            return (YamlObject) set.toArray()[0];
        }
        if (value instanceof YamlObject yamlValue) {
            return yamlValue;
        }

        return null;
    }

    private int getSize(Object value) {
        if (value instanceof Collection) {
            return 42;
        }
        if (value instanceof YamlObject) {
            return 1;
        }

        return 0;
    }

    private Clazz getTargetClass(ClassModelManager mm, Object value) {
        if (value instanceof Collection set) {
            value = set.toArray()[0];
        }

        if (value instanceof YamlObject yamlObject) {
            return mm.haveClass(yamlObject.getType());
        }

        return null;
    }

    private Entry<String, Object> findReverseReference(YamlObject yamlTgt, YamlObject yamlObject) {
        for (Entry<String, Object> revEntry : yamlTgt.getProperties().entrySet()) {
            if (revEntry.getValue() instanceof Collection revCollection) {
                if (revCollection.contains(yamlObject)) {
                    return revEntry;
                } else if (revEntry.getValue() == yamlObject) {
                    return revEntry;
                }
            }
            else if (revEntry.getValue() instanceof YamlObject yamlValue) {
                if (yamlValue == yamlObject) {
                    return revEntry;
                }
            }
        }
        return null;
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
                    if (backName.startsWith("[")) {
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
                if (pair.b.startsWith("[")) {
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
        for (YamlObject object : yamlGraph.values()) {
            if (object.getType() == null) {
                continue;
            }

            String className = "" + object.getType();
            clazzMap.put(className.toLowerCase(), mm.haveClass(className));
        }
    }

    private String generateClassDiagram(ClassModel classModel) {
        String fileName = "genTmp/diagrams/classdiagram";
        String result = "";

        fileName = FulibTools.classDiagrams().withScale(2).dumpSVG(classModel, fileName);

        try {
            Path path = Path.of(fileName + ".svg");
            result = Files.readString(path);

            Files.deleteIfExists(path);

            Files.deleteIfExists(Path.of("genTmp/diagrams/"));
            Files.deleteIfExists(Path.of("genTmp/"));
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
