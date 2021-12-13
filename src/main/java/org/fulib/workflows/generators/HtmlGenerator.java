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
        Map<String, String> generatedHTMLs = buildHTMLs(board);

        for (String key : generatedHTMLs.keySet()) {
            if (key.equals("Board")) {
                generateHTML(generatedHTMLs.get(key), key, "");
            } else {
                generateHTML(generatedHTMLs.get(key), key, "pages/");
            }
        }
    }

    public Map<String, String> buildHTMLs(Board board) {
        BoardConstructor boardConstructor = new BoardConstructor();
        PageConstructor pageConstructor = new PageConstructor();

        Map<String, String> resultMap = new HashMap<>();

        resultMap.put("Board", boardConstructor.buildBoard(board));

        List<String> pagesHTML = new ArrayList<>();

        for (Workflow workflow : board.getWorkflows()) {
            for (BaseNote note : workflow.getNotes()) {
                if (note instanceof Page) {
                    pagesHTML.add(pageConstructor.buildPage((Page) note));
                }
            }
        }

        for (int i = 0; i < pagesHTML.size(); i++) {
            String page = pagesHTML.get(i);
            resultMap.put(i + "_page", page);
        }

        return resultMap;
    }

    private void generateHTML(String htmlContent, String fileName, String subFolder) {
        try {
            String outputDirectory = "tmp/" + subFolder;
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
