package org.fulib.workflows.generators;

import org.fulib.workflows.events.Board;

import java.util.HashMap;
import java.util.Map;

public class HtmlGenerator {
    public void buildAndGenerateHTML(Board board) {
        Map<String, String> filesAsString = buildHTML(board);

        // TODO Generate files
    }

    public Map<String, String> buildHTML(Board board) {
        // TODO Build Board and Pages
        return new HashMap<>();
    }
}
