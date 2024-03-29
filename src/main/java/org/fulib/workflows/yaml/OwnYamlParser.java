package org.fulib.workflows.yaml;

import org.antlr.v4.runtime.misc.Pair;
import org.fulib.workflows.events.*;
import org.fulib.workflows.utils.FulibWorkflowsLintError;
import org.fulib.workflows.utils.FulibWorkflowsParseError;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.fulib.workflows.utils.Constants.*;

public class OwnYamlParser {
    private final Board board = new Board();
    private final Yaml yaml = new Yaml();

    private int workflowIndex = 0;
    private Workflow currentWorkflow;
    private final List<Workflow> workflows = new ArrayList<>();

    private int noteIndex = 0;
    private BaseNote currentNote;
    private List<BaseNote> notes = new ArrayList<>();

    private int dataIndex = 0;
    private Map<Integer, Pair<String, String>> noteData = new HashMap<>();

    /**
     * Uses Snakeyaml to parse the yamlInput and builds the Event-Storming-Board object
     *
     * @param yamlInput fulibWorkflows description from an *.es.yaml file
     */
    public void parseYAML(String yamlInput) {
        yamlInput = cleanUpInput(yamlInput);
        boolean lintSuccessfully = lintInput(yamlInput);

        if (!lintSuccessfully) {
            return;
        }

        List<Object> loadedEvents = yaml.load(yamlInput);

        for (Object loadedEvent : loadedEvents) {
            if (!(loadedEvent instanceof Map)) {
                try {
                    throw new FulibWorkflowsParseError("Notes must be an object");
                } catch (FulibWorkflowsParseError e) {
                    e.printStackTrace();
                }
                continue;
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> singleEventMap = (Map<String, Object>) loadedEvent;

            for (Map.Entry<String, Object> entry : singleEventMap.entrySet()) {
                String key = entry.getKey();

                BaseNote newNote = evaluateCurrentNote(key);

                if (newNote != null) {
                    newNote.setIndex(noteIndex);
                    noteIndex++;

                    if (key.equals(PAGE) || key.equals(DIV) || key.equals(DEVELOP)) {
                        parsePageEntries(entry.getValue());
                    } else {
                        newNote.setName((String) entry.getValue());
                    }
                } else {
                    addNewNoteData(key, entry.getValue());
                }
            }
        }

        if (currentNote != null) {
            setExtendedNote();
        }

        // Set last workflow
        setWorkflow();

        board.setWorkflows(workflows);
    }

    private void parsePageEntries(Object value) {
        if (!(value instanceof List)) {
            printStackTraceFor("Page entries must be a list");
            return;
        }

        @SuppressWarnings("unchecked")
        List<HashMap<String, Object>> pageEntryMaps = (List<HashMap<String, Object>>) value;

        for (HashMap<String, Object> pageEntryMap : pageEntryMaps) {
            for (Map.Entry<String, Object> pageEntry : pageEntryMap.entrySet()) {
                String key = pageEntry.getKey();

                if (key.equals(PAGE_NAME) || key.equals(DIV_NAME)) {
                    currentNote.setName((String) pageEntry.getValue());
                }

                addNewNoteData(key, pageEntry.getValue());
            }
        }

        if (currentNote instanceof Page page) {
            page.setContent(noteData);
            noteData = new HashMap<>();
            noteIndex = 0;
            dataIndex = 0;
            currentNote = null;
        }
        else if (currentNote instanceof Div page) {
            page.setContent(noteData);
            noteData = new HashMap<>();
            noteIndex = 0;
            dataIndex = 0;
            currentNote = null;
        }
        else if (currentNote instanceof Develop page) {
            page.setContent(noteData);
            noteData = new HashMap<>();
            noteIndex = 0;
            dataIndex = 0;
            currentNote = null;
        }

    }

    private void printStackTraceFor(String message) {
        try {
            throw new FulibWorkflowsParseError(message);
        } catch (FulibWorkflowsParseError e) {
            e.printStackTrace();
        }
    }

    private void addNewNoteData(String key, Object value) {
        String valueAsString = evaluateValueAsString(value);
        noteData.put(dataIndex, new Pair<>(key, valueAsString));
        dataIndex++;
    }

    private BaseNote evaluateCurrentNote(String key) {
        switch (key) {
            case WORKFLOW -> {
                if (currentWorkflow != null) {
                    setWorkflow();
                }
                Workflow workflow = new Workflow();
                workflow.setIndex(workflowIndex);
                workflowIndex++;
                workflows.add(workflow);
                currentWorkflow = workflow;
                return workflow;
            }
            case EXTERNAL_SYSTEM -> {
                if (currentNote != null) {
                    setExtendedNote();
                }
                ExternalSystem externalSystem = new ExternalSystem();
                externalSystem.setIndex(noteIndex);
                noteIndex++;
                notes.add(externalSystem);
                return externalSystem;
            }
            case SERVICE -> {
                if (currentNote != null) {
                    setExtendedNote();
                }
                Service service = new Service();
                notes.add(service);
                return service;
            }
            case COMMAND -> {
                if (currentNote != null) {
                    setExtendedNote();
                }
                Command command = new Command();
                notes.add(command);
                return command;
            }
            case EVENT -> {
                if (currentNote != null) {
                    setExtendedNote();
                }
                Event event = new Event();
                notes.add(event);
                currentNote = event;
                return event;
            }
            case POLICY -> {
                if (currentNote != null) {
                    setExtendedNote();
                }
                Policy policy = new Policy();
                notes.add(policy);
                return policy;
            }
            case USER -> {
                if (currentNote != null) {
                    setExtendedNote();
                }
                User user = new User();
                notes.add(user);
                return user;
            }
            case DATA -> {
                if (currentNote != null) {
                    setExtendedNote();
                }
                Data data = new Data();
                notes.add(data);
                currentNote = data;
                return data;
            }
            case PAGE -> {
                if (currentNote != null) {
                    setExtendedNote();
                }
                Page page = new Page();
                notes.add(page);
                currentNote = page;
                return page;
            }
            case DIV -> {
                if (currentNote != null) {
                    setExtendedNote();
                }
                Div page = new Div();
                notes.add(page);
                currentNote = page;
                return page;
            }
            case DEVELOP -> {
                if (currentNote != null) {
                    setExtendedNote();
                }
                Develop develop = new Develop();
                notes.add(develop);
                currentNote = develop;
                return develop;
            }
            case PROBLEM -> {
                if (currentNote != null) {
                    setExtendedNote();
                }
                Problem problem = new Problem();
                notes.add(problem);
                return problem;
            }
        }
        return null;
    }

    private void setExtendedNote() {
        if (currentNote instanceof Data data) {
            data.setData(noteData);
        } else if (currentNote instanceof Event event) {
            event.setData(noteData);
        } else {
            try {
                throw new FulibWorkflowsParseError("Additional attributes only allowed for data and event notes");
            } catch (FulibWorkflowsParseError e) {
                e.printStackTrace();
            }
        }

        noteData = new HashMap<>();
        dataIndex = 0;
        currentNote = null;
    }

    private void setWorkflow() {
        currentWorkflow.setNotes(notes);
        notes = new ArrayList<>();
        noteIndex = 0;
    }

    public Board getBoard() {
        return board;
    }

    private String evaluateValueAsString(Object value) {
        String valueType = value.getClass().getSimpleName();

        String valueAsString = "";
        int valueAsInt = -1;
        List<String> valueAsArrayList = null;

        switch (valueType) {
            case "String" -> valueAsString = (String) value;
            case "Integer" -> valueAsInt = (int) value;
            case "ArrayList" -> {
                valueAsArrayList = (List<String>) value;
            }
        }

        if (!valueAsString.equals("")) {
            return valueAsString;
        } else if (valueAsInt != -1) {
            return String.valueOf(valueAsInt);
        } else if (valueAsArrayList != null) {
            return valueAsArrayList.toString();
        } else {
            try {
                throw new FulibWorkflowsParseError("Attribute value must be String or Integer");
            } catch (FulibWorkflowsParseError e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private String cleanUpInput(String yamlInput) {
        return yamlInput.replaceAll("\\t", "  ");
    }

    private boolean lintInput(String yamlInput) {
        if (!yamlInput.contains("- workflow: ")) {
            try {
                throw new FulibWorkflowsLintError("Needs at least on workflow note (best at the beginning)");
            } catch (FulibWorkflowsLintError e) {
                e.printStackTrace();
            }
            return false;
        }

        return true;
    }
}
