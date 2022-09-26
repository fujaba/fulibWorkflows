package org.fulib.workflows.generators.constructors;

import org.fulib.FulibTools;
import org.fulib.builder.ClassModelManager;
import org.fulib.classmodel.ClassModel;
import org.fulib.classmodel.Clazz;
import org.fulib.yaml.YamlObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * The ClassDiagramConstructor builds a classdiagram from all data events from
 * an fulibWorkflows Board via fulib.
 */
public class ClassDiagramConstructor {
    private final Map<String, Clazz> clazzMap = new HashMap<>();
    private Map<String, YamlObject> yamlGraph;
    private ClassModelManager mm;

    public ClassModelManager getMm() {
        return mm;
    }

    /**
     * Builds a class model using fulib and generates a svg class diagram
     *
     * @param yamlGraph ???
     * @return classdiagram svg file content as string
     */
    public String buildClassDiagram(Map<String, YamlObject> yamlGraph) {
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
                } else if (!key.startsWith(".") && !key.equals("type")) {
                    String type = learnTypeFromValue(value);
                    try {
                        mm.haveAttribute(myClass, key, type);
                    } catch (Exception ignored) {
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
            Integer.parseInt(value.toString());
            return "int";
        } catch (Exception ignored) {
        }

        try {
            Double.parseDouble(value.toString().replace(',', '.'));
            return "double";
        } catch (Exception ignored) {
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

    private Entry<String, Object> findReverseReference(YamlObject yamlTgt, YamlObject yamlObject) {
        for (Entry<String, Object> revEntry : yamlTgt.getProperties().entrySet()) {
            if (revEntry.getValue() instanceof Collection revCollection) {
                if (revCollection.contains(yamlObject)) {
                    return revEntry;
                } else if (revEntry.getValue() == yamlObject) {
                    return revEntry;
                }
            } else if (revEntry.getValue() instanceof YamlObject yamlValue) {
                if (yamlValue == yamlObject) {
                    return revEntry;
                }
            }
        }
        return null;
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
}
