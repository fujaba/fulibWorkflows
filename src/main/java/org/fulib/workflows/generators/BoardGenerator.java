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

public class BoardGenerator {
    private final HtmlGenerator htmlGenerator = new HtmlGenerator();
    private final DiagramGenerator diagramGenerator = new DiagramGenerator();
    private final FxmlGenerator fxmlGenerator = new FxmlGenerator();

    /**
     * @param yamlFile the location of the yaml file, exp.: "src/gen/resources/example.es.yaml"
     */
    public void generateBoardFromFile(Path yamlFile) {
        try {
            CharStream inputStream = CharStreams.fromPath(yamlFile);

            Board board = generateBoard(inputStream);

            htmlGenerator.buildAndGenerateHTML(board);
            diagramGenerator.buildAndGenerateDiagram(board);
            fxmlGenerator.buildAndGenerateFxmls(board);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param yamlContent the content of a *.es.yaml file
     */
    public void generateBoardFromString(String yamlContent) {
        CharStream inputStream = CharStreams.fromString(yamlContent);

        Board board = generateBoard(inputStream);

        htmlGenerator.buildAndGenerateHTML(board);
        diagramGenerator.buildAndGenerateDiagram(board);
        fxmlGenerator.buildAndGenerateFxmls(board);
    }

    /**
     *
     * @param yamlFile the location of the yaml file, exp.: "src/gen/resources/example.es.yaml"
     * @return
     */
    public Map<String, String> generateAndReturnHTMLsFromFile(Path yamlFile) {
        CharStream inputStream = null;
        try {
            inputStream = CharStreams.fromPath(yamlFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Board board = generateBoard(inputStream);

        Map<String, String> result = new HashMap<>();
        Map<String, String> boardAndPages = htmlGenerator.buildHTMLs(board);
        result = mergeMaps(result, boardAndPages);

        Map<String, String> diagrams = diagramGenerator.buildDiagrams(board);
        result = mergeMaps(result, diagrams);

        Map<String, String> fxmls = fxmlGenerator.buildFxmls(board);
        result = mergeMaps(result, fxmls);

        return result;
    }

    /**
     *
     * @param yamlData
     * @return
     */
    public Map<String, String> generateAndReturnHTMLsFromString(String yamlData) {
        CharStream inputStream = CharStreams.fromString(yamlData);
        Board board = generateBoard(inputStream);

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
}
