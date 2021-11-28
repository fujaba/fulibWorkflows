package org.fulib.workflows.generators;

import org.fulib.workflows.events.Board;

import java.util.HashMap;
import java.util.Map;

public class HtmlGenerator {
    public void buildAndGenerateHTML(Board board) {
        String boardHTML = buildBoard(board);

        // TODO Generate files
    }

    private String buildBoard(Board board) {
        return null;
    }

    public Map<String, String> buildHTML(Board board) {
        // TODO Build Board

        // TODO Build Pages

        return new HashMap<>();
    }
}
