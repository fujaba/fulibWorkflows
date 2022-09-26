package org.fulib.workflows.generators;

import org.fulib.Fulib;
import org.fulib.FulibTools;
import org.fulib.builder.ClassModelManager;
import org.fulib.classmodel.ClassModel;
import org.fulib.workflows.events.Board;
import org.fulib.workflows.yaml.OwnYamlParser;
import org.fulib.yaml.Reflector;
import org.fulib.yaml.ReflectorMap;
import org.fulib.yaml.YamlObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;

/**
 * The BoardGenerator is the main entry point for the parsing of fulibWorkflows and the generation of files.
 */
public class BoardGenerator {
    private DiagramGenerator diagramGenerator;
    private FxmlGenerator fxmlGenerator;
    private HtmlGenerator htmlGenerator;
    private boolean webGeneration = false;
    private String genDir = "tmp";

    /**
     * Generates mockup and diagram files from a *.es.yaml file
     *
     * @param yamlFile the location of the yaml file, exp.: "src/gen/resources/example.es.yaml"
     */
    public void generateBoardFromFile(Path yamlFile) {
        try {
            String yamlContent = Files.readString(yamlFile);

            generateBoardFromString(yamlContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates mockup and diagram files from fulibWorkflows description
     *
     * @param yamlContent the content of a *.es.yaml file
     */
    public void generateBoardFromString(String yamlContent) {
        Board board = generateBoard(yamlContent);
        diagramGenerator = new DiagramGenerator(this);
        fxmlGenerator = new FxmlGenerator(this);
        htmlGenerator = new HtmlGenerator(board, this);

        htmlGenerator.buildAndGenerateHTML(board);
        diagramGenerator.buildAndGenerateDiagram(board);
        fxmlGenerator.buildAndGenerateFxmls(board);
    }

    /**
     * Generates and returns generated files from a *.es.yaml file
     *
     * @param yamlFile the location of the yaml file, exp.: "src/gen/resources/example.es.yaml"
     * @return Map containing all generated file contents as value, key is a combination from a number and Board/page/diagram/fxml/classdiagram
     */
    public Map<String, String> generateAndReturnHTMLsFromFile(Path yamlFile) {
        try {
            String inputStream = Files.readString(yamlFile);

            Board board = generateBoard(inputStream);

            diagramGenerator = new DiagramGenerator(this);
            fxmlGenerator = new FxmlGenerator(this);
            htmlGenerator = new HtmlGenerator(board, this);

            return buildAndReturnFiles(board);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Generates and returns generated files from fulibWorkflows description
     *
     * @param yamlContent the content of a *.es.yaml file
     * @return Map containing all generated file contents as value, key is a combination from a number and Board/page/diagram/fxml/classdiagram
     */
    public Map<String, String> generateAndReturnHTMLsFromString(String yamlContent) {
        Board board = generateBoard(yamlContent);

        diagramGenerator = new DiagramGenerator(this);
        fxmlGenerator = new FxmlGenerator(this);
        htmlGenerator = new HtmlGenerator(board, this);

        return buildAndReturnFiles(board);
    }

    private Map<String, String> buildAndReturnFiles(Board board) {
        Map<String, String> result = new HashMap<>();
        Map<String, String> boardAndPages = htmlGenerator.buildHTMLs(board);
        result = mergeMaps(result, boardAndPages);

        Map<String, String> diagrams = diagramGenerator.buildDiagrams(board);
        result = mergeMaps(result, diagrams);

        Map<String, String> fxmls = fxmlGenerator.buildFxmls(board);
        result = mergeMaps(result, fxmls);
        return result;
    }

    private Map<String, String> mergeMaps(Map<String, String> mainMap, Map<String, String> toBeMerged) {
        Map<String, String> result = new HashMap<>(mainMap);

        toBeMerged.forEach((key, value) -> result.merge(key, value, (v1, v2) -> ""));

        return result;
    }

    // Helper
    private Board generateBoard(String yamlContent) {
        OwnYamlParser ownYamlParser = new OwnYamlParser();
        ownYamlParser.parseYAML(yamlContent);
        return ownYamlParser.getBoard();
    }

    // Getter and Setter
    public boolean isWebGeneration() {
        return webGeneration;
    }

    public BoardGenerator setWebGeneration(boolean webGeneration) {
        this.webGeneration = webGeneration;
        return this;
    }

    public String getGenDir() {
        return genDir;
    }

    public BoardGenerator setGenDir(String genDir) {
        this.genDir = genDir;
        return this;
    }

    public void generateClassCode(String path, String packageName) {
        // for each class model
        Set<Entry<String, ClassModelManager>> entrySet = this.diagramGenerator.getClassDiagramMap().entrySet();
        for (Entry<String, ClassModelManager> entry : entrySet) {
            // generate code
            String diagramName = entry.getKey();
            ClassModelManager mm = entry.getValue();

            ClassModel classModel = mm.getClassModel();
            classModel.setMainJavaDir(path).setPackageName(packageName);
            Fulib.generator().generate(classModel);
            FulibTools.classDiagrams().dumpSVG(classModel, String.format("%s/%s/classDiag.svg", path, packageName.replaceAll("\\.", "/")));
        }
    }

    public Map<String, Object> loadObjectStructure(String packageName, String service) {
        Map<String, Map<String, YamlObject>> objectListMap = diagramGenerator.getObjectListMap();
        Map<String, YamlObject> serviceGraph = objectListMap.get(service);
        Collection<YamlObject> values = serviceGraph.values();

        ReflectorMap reflectorMap = new ReflectorMap(packageName);
        Map<String, Object> objMap = new HashMap<>();
        for (YamlObject yamlObject : values) {
            String id = yamlObject.getId();
            String type = yamlObject.getType();
            Reflector reflector = reflectorMap.getReflector(type);
            Object obj = reflector.newInstance();
            reflector.setValue(obj, "name", id);
            objMap.put(id, obj);
        }
        for (YamlObject yamlObject : values) {
            String id = yamlObject.getId();
            String type = yamlObject.getType();
            Reflector reflector = reflectorMap.getReflector(type);
            Object obj = objMap.get(id);
            for (Entry<String, Object> entry : yamlObject.getProperties().entrySet()) {
                String prop = entry.getKey();
                Object value = entry.getValue();

                if (prop.equals(".id") || prop.equals("type")) {
                    continue;
                }

                if (value instanceof Collection) {
                    ArrayList<YamlObject> valueList = (ArrayList<YamlObject>) value;
                    for (YamlObject yamlValue : valueList) {
                        Object tgt = objMap.get(yamlValue.getId());
                        reflector.setValue(obj, prop, tgt);
                    }
                } else {
                    reflector.setValue(obj, prop, value);
                }
            }
        }
        return objMap;
    }
}
