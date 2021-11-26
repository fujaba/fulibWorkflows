package org.fulib.workflows.generators;

import org.fulib.workflows.events.Board;
import org.fulib.workflows.events.Workflow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BoardGenerator {

    public void generateBoardFromFile(Path yamlFile) {
        try {
            String yamlContent = Files.readString(yamlFile);
            Board board = generateBoardFromString(yamlContent);
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
        Pattern p = Pattern.compile("\\n\\n|\\r\\n"); // TODO This is not working for the web editor
        List<String> notes = List.of(p.split(workflowString));

        for (String note : notes) {
            if (note.contains("- workflow:")) {
                String workflowName = getValue(note);
                workflow.setName(workflowName);
            } else if (note.contains("- event:")) {
                // TODO Continue here tomorrow

            } else if (note.contains("- page:")) {

            }
        }

        return workflow;
    }

    private String getValue(String note) {
        String value = note.substring(note.indexOf(":") + 1);
        value = value.strip();
        return value;
    }
}
