package org.fulib.workflows.generators;

import org.fulib.workflows.events.Board;
import org.fulib.workflows.events.Event;
import org.fulib.workflows.events.Page;
import org.fulib.workflows.events.Workflow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BoardGenerator {
    private final HtmlGenerator htmlGenerator = new HtmlGenerator();

    public void generateBoardFromFile(Path yamlFile) {
        try {
            String yamlContent = Files.readString(yamlFile);
            Board board = generateBoardFromString(yamlContent);
            htmlGenerator.buildAndGenerateHTML(board);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // This method is needed for the backend of the web editor
    public Board generateBoardFromString(String yamlContent) {
        // Create new Board
        Board board = new Board();

        // Get chunked workflowStrings
        List<String> workflows = getWorkflowChunks(yamlContent);

        // Generate and add Workflows to board
        board.withWorkflows(generateWorkflows(workflows));

        return board;
    }

    // This method is needed because i want to be able to work with multiple workflows in one board
    private List<String> getWorkflowChunks(String yamlContent) {
        List<String> workflows = new ArrayList<>();

        // Source
        // https://stackoverflow.com/questions/32788407/find-all-occurrences-of-substring-in-string-in-java
        Matcher matcher = Pattern.compile("- workflow").matcher(yamlContent);
        List<Integer> indexes = new ArrayList<>();

        while (matcher.find()) {
            indexes.add(matcher.start());
        }

        for (int i = 0; i < indexes.size(); i++) {
            if (i + 1 == indexes.size()) {
                // There is no further match -> To End of file
                workflows.add(yamlContent.substring(indexes.get(i)));
            } else {
                // THere is another match -> to that position -1
                workflows.add(yamlContent.substring(indexes.get(i), indexes.get(i + 1) - 1));
            }
        }

        return workflows;
    }

    private List<Workflow> generateWorkflows(List<String> workflows) {
        List<Workflow> result = new ArrayList<>();

        for (int i = 0; i < workflows.size(); i++) {
            String workflow = workflows.get(i);
            result.add(generateWorkflow(workflow, i));
        }
        return result;
    }

    private Workflow generateWorkflow(String workflowString, int index) {
        Workflow workflow = new Workflow();
        workflow.setIndex(index);

        // Get Note strings
        // Source
        // https://stackoverflow.com/questions/10065885/split-text-file-into-strings-on-empty-line
        Pattern p = Pattern.compile("\\n[\\n]+|\\r\\n[\\r\\n]+");
        List<String> notes = List.of(p.split(workflowString));

        for (int i = 0; i < notes.size(); i++) {
            String note = notes.get(i);
            if (note.contains("- workflow:")) {
                String workflowName = getValue(note);
                workflow.setName(workflowName);
            } else if (note.contains("- event:")) {
                Event event = new Event();
                event.setName(getNameValue(note));
                event.setIndex(i);
                event.setData(getAdditionalData(note));
                workflow.withNotes(event);
            } else if (note.contains("- page:")) {
                Page page = new Page();
                page.setIndex(i);
                page.setContent(getPageContent(note));
                page.setName(page.getContent().get("name"));
                workflow.withNotes(page);
            }
        }

        return workflow;
    }

    private Map<String, String> getPageContent(String note) {
        Map<String, String> result = new HashMap<>();

        Pattern p = Pattern.compile("\\n|\\r\\n");
        List<String> elements = List.of(p.split(note));

        for (String element : elements) {
            if (!element.contains("- page:")) {
                String key = getKey(element);
                String value = getValue(element);
                result.put(key, value);
            }
        }

        return result;
    }

    private String getNameValue(String note) {
        int startIndex = note.indexOf(":") + 1;
        int endIndex = note.indexOf("\r\n");

        if (endIndex == -1) {
            endIndex = note.indexOf("\n");
        }

        String value = note.substring(startIndex, endIndex);
        value = value.strip();
        return value;
    }

    private Map<String, String> getAdditionalData(String note) {
        Map<String, String> result = new HashMap<>();

        Pattern p = Pattern.compile("\\n|\\r\\n");
        List<String> attributes = List.of(p.split(note));

        for (String attribute : attributes) {
            if (!attribute.contains("-")) {
                String key = getKey(attribute);
                String value = getValue(attribute);
                result.put(key, value);
            }
        }

        return result;
    }

    private String getKey(String attribute) {
        String key = attribute.substring(0, attribute.indexOf(":"));

        // Clean up key
        if (key.contains("-")) {
            key = key.substring(key.indexOf("-") + 1);
        }

        key = key.strip();

        return key;
    }

    private String getValue(String note) {
        String value = note.substring(note.indexOf(":") + 1);
        value = value.strip();
        return value;
    }
}
