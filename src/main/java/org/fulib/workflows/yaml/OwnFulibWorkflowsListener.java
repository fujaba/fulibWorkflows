package org.fulib.workflows.yaml;

import org.antlr.v4.runtime.misc.Pair;
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
    private final List<BaseNote> notes = new ArrayList<>();

    private int dataIndex = 0;
    private Map<Integer, Pair<String, String>> noteData;

    @Override
    public void enterList(FulibWorkflowsParser.ListContext ctx) {
        board = new Board();
    }

    @Override
    public void exitList(FulibWorkflowsParser.ListContext ctx) {
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

    }

    @Override
    public void exitExtendedNote(FulibWorkflowsParser.ExtendedNoteContext ctx) {

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
    public void exitPageList(FulibWorkflowsParser.PageListContext ctx) {

    }

    @Override
    public void exitPageName(FulibWorkflowsParser.PageNameContext ctx) {
        String key = "name";
        String value = ctx.NAME().getText();

        noteData.put(dataIndex, new Pair<>(key, value));
        dataIndex++;
    }

    // Getter and Setter
    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }
}
