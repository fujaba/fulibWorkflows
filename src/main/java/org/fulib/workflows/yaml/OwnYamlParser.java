package org.fulib.workflows.yaml;

import org.antlr.v4.runtime.misc.Pair;
import org.fulib.workflows.events.*;
import org.fulib.workflows.utils.FulibWorkflowsParseError;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.fulib.workflows.utils.Constants.*;

public class OwnYamlParser {
    private final Board board = new Board();

    private int workflowIndex = 0;
    private Workflow currentWorkflow;
    private final List<Workflow> workflows = new ArrayList<>();

    private int noteIndex = 0;
    private BaseNote currentNote;
    private List<BaseNote> notes = new ArrayList<>();

    private int dataIndex = 0;
    private Map<Integer, Pair<String, String>> noteData = new HashMap<>();

    public void parseYAML(String yamlInput) {
        Yaml yaml = new Yaml();
        List<Object> loadedEvents = yaml.load(yamlInput);

        for (Object loadedEvent : loadedEvents) {
            Map<String, Object> singleEventMap = (HashMap<String, Object>) loadedEvent; // TODO fix this warning

            for (Map.Entry<String, Object> entry : singleEventMap.entrySet()) {
                String key = entry.getKey();

                BaseNote newNote = evaluateCurrentNote(key);

                if (newNote != null) {
                    newNote.setIndex(noteIndex);
                    noteIndex++;

                    if (key.equals(PAGE)) {
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
        List<HashMap<String, Object>> pageEntryMaps = (ArrayList<HashMap<String, Object>>) value; // TODO fix this warning

        for (HashMap<String, Object> pageEntryMap : pageEntryMaps) {
            for (Map.Entry<String, Object> pageEntry : pageEntryMap.entrySet()) {
                String key = pageEntry.getKey();

                if (key.equals(PAGE_NAME)) {
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
    }

    private void addNewNoteData(String key, Object value) {
        String valueAsString = evaluateValueAsString(value);
        noteData.put(dataIndex, new Pair<>(key, valueAsString));
        dataIndex++;
    }

    private BaseNote evaluateCurrentNote(String key) {
        switch (key) {
            case WORKFLOW:
                if (currentWorkflow != null) {
                    setWorkflow();
                }

                Workflow workflow = new Workflow();
                workflow.setIndex(workflowIndex);
                workflowIndex++;
                workflows.add(workflow);
                currentWorkflow = workflow;
                return workflow;
            case EXTERNAL_SYSTEM:
                if (currentNote != null) {
                    setExtendedNote();
                }

                ExternalSystem externalSystem = new ExternalSystem();
                externalSystem.setIndex(noteIndex);
                noteIndex++;
                notes.add(externalSystem);
                return externalSystem;
            case SERVICE:
                if (currentNote != null) {
                    setExtendedNote();
                }

                Service service = new Service();
                notes.add(service);
                return service;
            case COMMAND:
                if (currentNote != null) {
                    setExtendedNote();
                }

                Command command = new Command();
                notes.add(command);
                return command;
            case EVENT:
                if (currentNote != null) {
                    setExtendedNote();
                }

                Event event = new Event();
                notes.add(event);
                currentNote = event;
                return event;
            case POLICY:
                if (currentNote != null) {
                    setExtendedNote();
                }

                Policy policy = new Policy();
                notes.add(policy);
                return policy;
            case USER:
                if (currentNote != null) {
                    setExtendedNote();
                }

                User user = new User();
                notes.add(user);
                return user;
            case DATA:
                if (currentNote != null) {
                    setExtendedNote();
                }

                Data data = new Data();
                notes.add(data);
                currentNote = data;
                return data;
            case PAGE:
                if (currentNote != null) {
                    setExtendedNote();
                }

                Page page = new Page();
                notes.add(page);
                currentNote = page;
                return page;
            case PROBLEM:
                if (currentNote != null) {
                    setExtendedNote();
                }

                Problem problem = new Problem();
                notes.add(problem);
                return problem;
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
                throw new FulibWorkflowsParseError("Fuuuuuuuuuuuuuuuuuuuuuck");
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

        switch (valueType) {
            case "String":
                valueAsString = (String) value;
                break;
            case "Integer":
                valueAsInt = (int) value;
                break;
        }

        if (!valueAsString.equals("")) {
            return valueAsString;
        } else if (valueAsInt != -1) {
            return String.valueOf(valueAsInt);
        } else {
            try {
                throw new FulibWorkflowsParseError("Shiiiiiiiiiiiiiiiiiiiiiiit");
            } catch (FulibWorkflowsParseError e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
