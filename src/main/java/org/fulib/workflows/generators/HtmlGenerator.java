package org.fulib.workflows.generators;

import org.fulib.workflows.events.BaseNote;
import org.fulib.workflows.events.Board;
import org.fulib.workflows.events.Page;
import org.fulib.workflows.events.Workflow;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class HtmlGenerator {
    private Page currentPage;

    private STGroupFile pageGroup;

    public void buildAndGenerateHTML(Board board) {
        String boardHTML = buildBoard(board);

        List<String> pagesHTML = new ArrayList<>();

        for (Workflow workflow : board.getWorkflows()) {
            for (BaseNote note : workflow.getNotes()) {
                if (note instanceof Page) {
                    currentPage = (Page) note;
                    pagesHTML.add(buildPage());
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

    private String buildBoard(Board board) {
        STGroupFile boardGroup = new STGroupFile(Objects.requireNonNull(this.getClass().getResource("Board.stg")));
        StringBuilder boardBody = new StringBuilder();

        // TODO

        return boardBody.toString();
    }

    public String buildPage() {
        pageGroup = new STGroupFile(Objects.requireNonNull(this.getClass().getResource("Page.stg")));
        StringBuilder pageBody = new StringBuilder();

        // Complete the page
        ST st = pageGroup.getInstanceOf("page");
        st.add("content", buildPageContent());
        st.add("pageName", currentPage.getName());

        pageBody.append(st.render());
        return pageBody.toString();
    }

    private String buildPageContent() {
        ST st;
        StringBuilder contentBody = new StringBuilder();

        List<String> list = currentPage.getContent().keySet().stream().toList();
        for (int i = 0; i < list.size(); i++) {
            String key = list.get(i);
            String value = currentPage.getContent().get(key);

            if (key.contains("text")) {
                st = pageGroup.getInstanceOf("text");
                st.add("text", value);
                contentBody.append(st.render());
            } else if (key.contains("input")) {
                st = pageGroup.getInstanceOf("input");
                st.add("id", i + "input");
                st.add("label", value);
                contentBody.append(st.render());
            } else if (key.contains("password")) {
                st = pageGroup.getInstanceOf("password");
                st.add("id", i + "password");
                st.add("label", value);
                contentBody.append(st.render());
            } else if (key.contains("button")) {
                st = pageGroup.getInstanceOf("button");
                st.add("description", value);
                contentBody.append(st.render());
            }
        }

        return contentBody.toString();
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
