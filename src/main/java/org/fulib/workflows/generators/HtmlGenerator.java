package org.fulib.workflows.generators;

import org.antlr.v4.runtime.misc.Pair;
import org.fulib.workflows.events.*;
import org.fulib.workflows.generators.constructors.BoardConstructor;
import org.fulib.workflows.generators.constructors.PageConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The HtmlGenerator manages the building and generation of an event storming board and mockup pages described as page events.
 */
public class HtmlGenerator {
    private final Map<String, Integer> allPagesMap = new HashMap<>();
    private BoardGenerator boardGenerator;
    private boolean standAlone = false;

    HtmlGenerator(Board board, BoardGenerator boardGenerator) {
        createAllPagesMap(board);
        this.boardGenerator = boardGenerator;
    }

    /**
     * Builds and generates an event storming board and page mockups from event storming Board
     * @param board generated by the fulibWorkflows yaml parser
     */
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

    /**
     * Builds an event storming board and page mockups from event storming Board
     * @param board generated by the fulibWorkflows yaml parser
     * @return Map containing a board and page mockups as string value, key consists of an index and Board/page
     */
    public Map<String, String> buildHTMLs(Board board) {
        BoardConstructor boardConstructor = new BoardConstructor().setStandAlone(this.standAlone);
        PageConstructor pageConstructor = new PageConstructor().setStandAlone(this.standAlone);

        Map<String, String> resultMap = new HashMap<>();

        resultMap.put("Board", boardConstructor.buildBoard(board, boardGenerator.isWebGeneration()));

        List<String> pagesHTML = new ArrayList<>();

        for (Workflow workflow : board.getWorkflows()) {
            for (BaseNote note : workflow.getNotes()) {
                if (note instanceof Page page) {
                    List<Integer> targetPageIndexList = evaluateTargetPageIndex(page);
                    String pageContent = pageConstructor.buildPage(page, targetPageIndexList);
                    pagesHTML.add(pageContent);
                }
                if (note instanceof Div div) {

                }
            }
        }

        for (int i = 0; i < pagesHTML.size(); i++) {
            String page = pagesHTML.get(i);
            resultMap.put(i + "_page", page);
        }

        return resultMap;
    }

    private List<Integer> evaluateTargetPageIndex(Page page) {
        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < page.getContent().size(); i++) {
            Pair<String, String> elementPair = page.getContent().get(i);
            if (elementPair.a.equals("targetPage")) {
                String targetPageName = elementPair.b;

                Integer targetIndex = allPagesMap.get(targetPageName);

                if (targetIndex != null) {
                 result.add(targetIndex);
                }
            }
        }

        return result;
    }

    private void generateHTML(String htmlContent, String fileName, String subFolder) {
        try {
            String outputDirectory = boardGenerator.getGenDir() + "/" + subFolder;
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

    private void createAllPagesMap(Board board) {
        int index = 0;

        for (Workflow workflow : board.getWorkflows()) {
            for (BaseNote note : workflow.getNotes()) {
                if (note instanceof Page page) {
                    allPagesMap.put(page.getName(), index);
                    index++;
                }
            }
        }
    }

    public HtmlGenerator setStandAlone(boolean standAlone) {
        this.standAlone = standAlone;
        return this;
    }
}
