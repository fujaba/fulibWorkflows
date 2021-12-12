package org.fulib.workflows.generators;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.fulib.workflows.events.*;
import org.fulib.workflows.yaml.FulibWorkflowsLexer;
import org.fulib.workflows.yaml.FulibWorkflowsParser;
import org.fulib.workflows.yaml.OwnFulibWorkflowsListener;

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

    // New
    public void generateBoardFromFileViaANTLR(Path yamlFile) {
        try {
            CharStream inputStream = CharStreams.fromPath(yamlFile);

            FulibWorkflowsLexer fulibWorkflowsLexer = new FulibWorkflowsLexer(inputStream);
            CommonTokenStream commonTokenStream = new CommonTokenStream(fulibWorkflowsLexer);
            FulibWorkflowsParser fulibWorkflowsParser = new FulibWorkflowsParser(commonTokenStream);

            FulibWorkflowsParser.FileContext fileContext = fulibWorkflowsParser.file();
            OwnFulibWorkflowsListener ownFulibWorkflowsListener = new OwnFulibWorkflowsListener();

            ParseTreeWalker.DEFAULT.walk(ownFulibWorkflowsListener, fileContext);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Old
    public void generateBoardFromFile(Path yamlFile) {
        try {
            String yamlContent = Files.readString(yamlFile);
            Board board = generateBoardFromString(yamlContent);
            htmlGenerator.buildAndGenerateHTML(board);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Old
    public Map<String, String> generateAndReturnHTMLsFromString(String yamlContent) {
        Board board = generateBoardFromString(yamlContent);
        return htmlGenerator.buildHTMLs(board);
    }

    // This method is needed for the backend of the web editor
    public Board generateBoardFromString(String yamlContent) {
        // Create new Board
        Board board = new Board();

        // Get chunked workflowStrings
        List<String> workflows = getWorkflowChunks(yamlContent);// Old

        // Generate and add Workflows to board
        board.setWorkflows(generateWorkflows(workflows));

        return board;
    }

    // This method is needed because I want to be able to work with multiple workflows in one board
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

        List<BaseNote> workflowNotes = new ArrayList<>();

        for (int i = 0; i < notes.size(); i++) {
            String note = notes.get(i);
            if (note.contains("- workflow:")) {
                String workflowName = getValue(note);
                workflow.setName(workflowName);
            } else if (note.contains("- externalSystem:")) {
                ExternalSystem externalSystem = new ExternalSystem();
                externalSystem.setName(getValue(note));
                externalSystem.setIndex(i);
                workflowNotes.add(externalSystem);
            } else if (note.contains("- service:")) {
                Service service = new Service();
                service.setName(getValue(note));
                service.setIndex(i);
                workflowNotes.add(service);
            } else if (note.contains("- command")) {
                Command command = new Command();
                command.setName(getValue(note));
                command.setIndex(i);
                workflowNotes.add(command);
            } else if (note.contains("- event:")) {
                Event event = new Event();
                event.setName(getNameValue(note));
                event.setIndex(i);
                event.setData(getAdditionalData(note));
                workflowNotes.add(event);
            } else if (note.contains("- policy")) {
                Policy policy = new Policy();
                policy.setName(getValue(note));
                policy.setIndex(i);
                workflowNotes.add(policy);
            } else if (note.contains("- user")) {
                User user = new User();
                user.setName(getValue(note));
                user.setIndex(i);
                workflowNotes.add(user);
            } else if (note.contains("- class")) {
                ClassDef classDef = new ClassDef();
                classDef.setName(getNameValue(note));
                classDef.setIndex(i);
                classDef.setFields(getAdditionalData(note));
                workflowNotes.add(classDef);
            } else if (note.contains("- data")) {
                Data data = new Data();
                data.setName(getNameValue(note));
                data.setIndex(i);
                data.setData(getAdditionalData(note));
                workflowNotes.add(data);
            } else if (note.contains("- problem")) {
                Problem problem = new Problem();
                problem.setName(getValue(note));
                problem.setIndex(i);
                workflowNotes.add(problem);
            } else if (note.contains("- page:")) {
                Page page = new Page();
                page.setIndex(i);
                page.setContent(getPageContent(note));
                page.setName(page.getContent().get(1).a);
                workflowNotes.add(page);
            }
        }
        workflow.setNotes(workflowNotes);

        return workflow;
    }

    private Map<Integer, Pair<String, String>> getAdditionalData(String note) {
        Map<Integer, Pair<String, String>> result = new HashMap<>();

        Pattern p = Pattern.compile("\\n|\\r\\n");
        List<String> attributes = List.of(p.split(note));

        for (int i = 0; i < attributes.size(); i++) {
            String attribute = attributes.get(i);
            if (!attribute.contains("-")) {
                String key = getKey(attribute);
                String value = getValue(attribute);
                result.put(i, new Pair<>(key, value));
            }
        }

        return result;
    }

    private Map<Integer, Pair<String, String>> getPageContent(String note) {
        Map<Integer, Pair<String, String>> result = new HashMap<>();

        Pattern p = Pattern.compile("\\n|\\r\\n");
        List<String> elements = List.of(p.split(note));

        for (int i = 0; i < elements.size(); i++) {
            String element = elements.get(i);
            if (!element.contains("- page:")) {
                String key = getKey(element);
                String value = getValue(element);
                result.put(i, new Pair<>(key, value));
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

        String value;

        if (endIndex == -1) {
            value = note.substring(startIndex);
        } else {
            value = note.substring(startIndex, endIndex);
        }

        value = value.strip();
        return value;
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
