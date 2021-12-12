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

public class BoardGenerator {
    private final HtmlGenerator htmlGenerator = new HtmlGenerator();

    // Entrypoints
    public void generateBoardFromFile(Path yamlFile) {
        try {
            CharStream inputStream = CharStreams.fromPath(yamlFile);

            Board board = generateBoard(inputStream);

            htmlGenerator.buildAndGenerateHTML(board);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
