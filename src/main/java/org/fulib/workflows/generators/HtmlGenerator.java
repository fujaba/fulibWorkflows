package org.fulib.workflows.generators;

import org.fulib.workflows.events.BaseNote;
import org.fulib.workflows.events.Board;
import org.fulib.workflows.events.Page;
import org.fulib.workflows.events.Workflow;
import org.fulib.workflows.generators.constructors.BoardConstructor;
import org.fulib.workflows.generators.constructors.PageConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HtmlGenerator {
    public void buildAndGenerateHTML(Board board) {
        BoardConstructor boardConstructor = new BoardConstructor();
        PageConstructor pageConstructor = new PageConstructor();

        String boardHTML = boardConstructor.buildBoard(board);

        List<String> pagesHTML = new ArrayList<>();

        for (Workflow workflow : board.getWorkflows()) {
            for (BaseNote note : workflow.getNotes()) {
                if (note instanceof Page) {
                    pagesHTML.add(pageConstructor.buildPage((Page) note));
                }
            }
        }

        generateHTML(boardHTML, "Board");

        for (int i = 0; i < pagesHTML.size(); i++) {
            String page = pagesHTML.get(i);
            generateHTML(page, i + "_page");
        }
    }

    public Map<String, String> buildHTMLs(Board board) {
        // TODO Return type might also has to be changed
        return new HashMap<>();
    }

    private void generateHTML(String htmlContent, String fileName) {
        try {
            String outputDirectory = "tmp/";
            Files.createDirectories(Path.of(outputDirectory));

            String outputBoardFilePath = outputDirectory + fileName + ".html";
            if (!Files.exists(Path.of(outputBoardFilePath))) {
                Files.createFile(Path.of(outputBoardFilePath));
            }
            Files.writeString(Path.of(outputBoardFilePath), htmlContent);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
