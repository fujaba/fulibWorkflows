package org.fulib.workflows.yaml;

import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.fulib.workflows.events.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OwnFulibWorkflowsListener extends FulibWorkflowsBaseListener {
    private Board board;

    private int workflowIndex = 0;
    private Workflow currentWorkflow;
    private final List<Workflow> workflows = new ArrayList<>();

    private int noteIndex = 0;
    private List<BaseNote> notes = new ArrayList<>();

    private int dataIndex = 0;
    private Map<Integer, Pair<String, String>> noteData;

    @Override
    public void enterFile(FulibWorkflowsParser.FileContext ctx) {
        board = new Board();
    }

    @Override
    public void exitFile(FulibWorkflowsParser.FileContext ctx) {
        board.setWorkflows(workflows);
    }

    @Override
    public void enterWorkflow(FulibWorkflowsParser.WorkflowContext ctx) {
        currentWorkflow = new Workflow();
    }

    @Override
    public void exitWorkflow(FulibWorkflowsParser.WorkflowContext ctx) {
        currentWorkflow.setName(ctx.NAME().getText());

        currentWorkflow.setIndex(workflowIndex);
        workflowIndex++;

        workflows.add(currentWorkflow);
        resetData();
    }

    @Override
    public void exitEventNote(FulibWorkflowsParser.EventNoteContext ctx) {
        workflows.get(workflowIndex - 1).setNotes(notes);
    }

    @Override
    public void exitNormalNote(FulibWorkflowsParser.NormalNoteContext ctx) {
        BaseNote newNote = new BaseNote();

        String noteType = ctx.NORMALNOTEKEY().getText();

        switch (noteType) {
            case "externalSystem" -> newNote = new ExternalSystem();
            case "service" -> newNote = new Service();
            case "command" -> newNote = new Command();
            case "policy" -> newNote = new Policy();
            case "user" -> newNote = new User();
            case "problem" -> newNote = new Problem();
        }
        newNote.setName(ctx.NAME().getText());

        newNote.setIndex(noteIndex);
        noteIndex++;

        notes.add(newNote);
    }

    @Override
    public void enterExtendedNote(FulibWorkflowsParser.ExtendedNoteContext ctx) {
        noteData = new HashMap<>();
    }

    @Override
    public void exitExtendedNote(FulibWorkflowsParser.ExtendedNoteContext ctx) {
        BaseNote newNote = new BaseNote();

        String noteType = ctx.EXTENDEDNOTEKEY().getText();

        switch (noteType) {
            case "event" -> newNote = new Event().setData(noteData);
            case "class" -> newNote = new ClassDef().setFields(noteData);
            case "data" -> newNote = new Data().setData(noteData);
        }
        newNote.setName(ctx.NAME().getText());

        newNote.setIndex(noteIndex);
        noteIndex++;

        notes.add(newNote);
    }

    @Override
    public void exitPage(FulibWorkflowsParser.PageContext ctx) {
        Page newPage = new Page();

        newPage.setContent(noteData);
        newPage.setIndex(noteIndex);
        noteIndex++;

        notes.add(newPage);
    }

    @Override
    public void enterPageList(FulibWorkflowsParser.PageListContext ctx) {
        noteData = new HashMap<>();
    }

    @Override
    public void exitPageName(FulibWorkflowsParser.PageNameContext ctx) {
        addNoteDataEntry("name", ctx.NAME().getText());
    }

    @Override
    public void exitElement(FulibWorkflowsParser.ElementContext ctx) {
        addNoteDataEntry(ctx.ELEMENTKEY().getText(), ctx.NAME().getText());
    }

    @Override
    public void exitAttribute(FulibWorkflowsParser.AttributeContext ctx) {
        String key = ctx.NAME().getText();
        String value;

        FulibWorkflowsParser.ValueContext valueContext = ctx.value();
        TerminalNode name = valueContext.NAME();
        TerminalNode number = valueContext.NUMBER();

        if (name == null) {
            value = number.getText();
        } else {
            value = name.getText();
        }

        addNoteDataEntry(key, value);
    }


    // Helper Methods
    private void addNoteDataEntry(String key, String value) {
        noteData.put(dataIndex, new Pair<>(key, value));
        dataIndex++;
    }

    private void resetData() {
        currentWorkflow = null;
        noteIndex = 0;
        notes = new ArrayList<>();
        dataIndex = 0;
        noteData = new HashMap<>();
    }

    // Getter and Setter
    public Board getBoard() {
        return board;
    }
}
