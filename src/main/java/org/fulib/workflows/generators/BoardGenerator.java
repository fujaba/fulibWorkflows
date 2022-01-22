package org.fulib.workflows.generators;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.fulib.workflows.events.Board;
import org.fulib.workflows.yaml.FulibWorkflowsLexer;
import org.fulib.workflows.yaml.FulibWorkflowsParser;
import org.fulib.workflows.yaml.OwnFulibWorkflowsListener;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * The BoardGenerator is the main entry point for the parsing of fulibWorkflows and the generation of files.
 */
public class BoardGenerator {
    private final DiagramGenerator diagramGenerator = new DiagramGenerator();
    private final FxmlGenerator fxmlGenerator = new FxmlGenerator();
    private HtmlGenerator htmlGenerator;
    private boolean standAlone;

    /** Generates mockup and diagram files from a *.es.yaml file
     * @param yamlFile the location of the yaml file, exp.: "src/gen/resources/example.es.yaml"
     */
    public void generateBoardFromFile(Path yamlFile) {
        try {
            CharStream inputStream = CharStreams.fromPath(yamlFile);

            Board board = generateBoard(inputStream);

            htmlGenerator = new HtmlGenerator(board).setStandAlone(this.standAlone);

            htmlGenerator.buildAndGenerateHTML(board);
            diagramGenerator.buildAndGenerateDiagram(board);
            fxmlGenerator.buildAndGenerateFxmls(board);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Generates mockup and diagram files from fulibWorkflows description
     * @param yamlContent the content of a *.es.yaml file
     */
    public void generateBoardFromString(String yamlContent) {
        CharStream inputStream = CharStreams.fromString(yamlContent);

        Board board = generateBoard(inputStream);

        htmlGenerator = new HtmlGenerator(board);

        htmlGenerator.buildAndGenerateHTML(board);
        diagramGenerator.buildAndGenerateDiagram(board);
        fxmlGenerator.buildAndGenerateFxmls(board);
    }

    /** Generates and returns generated files from a *.es.yaml file
     * @param yamlFile the location of the yaml file, exp.: "src/gen/resources/example.es.yaml"
     * @return Map containing all generated file contents as value, key is a combination from a number and Board/page/diagram/fxml/classdiagram
     */
    public Map<String, String> generateAndReturnHTMLsFromFile(Path yamlFile) {
        try {
            CharStream inputStream = CharStreams.fromPath(yamlFile);

            Board board = generateBoard(inputStream);

            htmlGenerator = new HtmlGenerator(board);

            return buildAndReturnFiles(board);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /** Generates and returns generated files from fulibWorkflows description
     * @param yamlContent the content of a *.es.yaml file
     * @return Map containing all generated file contents as value, key is a combination from a number and Board/page/diagram/fxml/classdiagram
     */
    public Map<String, String> generateAndReturnHTMLsFromString(String yamlContent) {
        CharStream inputStream = CharStreams.fromString(yamlContent);
        Board board = generateBoard(inputStream);

        htmlGenerator = new HtmlGenerator(board);

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
    private Board generateBoard(CharStream inputStream) {
        FulibWorkflowsLexer fulibWorkflowsLexer = new FulibWorkflowsLexer(inputStream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(fulibWorkflowsLexer);
        FulibWorkflowsParser fulibWorkflowsParser = new FulibWorkflowsParser(commonTokenStream);

        FulibWorkflowsParser.FileContext fileContext = fulibWorkflowsParser.file();
        OwnFulibWorkflowsListener ownFulibWorkflowsListener = new OwnFulibWorkflowsListener();

        ParseTreeWalker.DEFAULT.walk(ownFulibWorkflowsListener, fileContext);

        return ownFulibWorkflowsListener.getBoard();
    }

    public BoardGenerator setStandAlone() {
        standAlone = true;
        return this;
    }
}
