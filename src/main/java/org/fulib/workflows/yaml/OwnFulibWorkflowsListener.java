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

    private Pair<String, String> pageAction;

    private String pageName;

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
    public void enterEventNote(FulibWorkflowsParser.EventNoteContext ctx) {
        dataIndex = 0;
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
            case "data" -> newNote = new Data().setData(noteData);
        }
        newNote.setName(ctx.NAME().getText());

        newNote.setIndex(noteIndex);
        noteIndex++;

        notes.add(newNote);
    }

    @Override
    public void enterPage(FulibWorkflowsParser.PageContext ctx) {
        pageName = "";
    }

    @Override
    public void exitPage(FulibWorkflowsParser.PageContext ctx) {
        Page newPage = new Page();

        newPage.setName(pageName);
        newPage.setContent(noteData);
        newPage.setIndex(noteIndex);
        noteIndex++;

        notes.add(newPage);
    }

    @Override
    public void enterPageList(FulibWorkflowsParser.PageListContext ctx) {
        noteData = new HashMap<>();
        dataIndex = 0;
    }

    @Override
    public void exitPageName(FulibWorkflowsParser.PageNameContext ctx) {
        pageName = ctx.NAME().getText();
        addNoteDataEntry("name", ctx.NAME().getText());
    }

    @Override
    public void exitText(FulibWorkflowsParser.TextContext ctx) {
        addNoteDataEntry("text", ctx.NAME().getText());
    }

    @Override
    public void enterInputField(FulibWorkflowsParser.InputFieldContext ctx) {
        pageAction = null;
    }

    @Override
    public void exitInputField(FulibWorkflowsParser.InputFieldContext ctx) {
        addNoteDataEntry(ctx.ELEMENTKEY().getText(), ctx.NAME().getText());
        // add exitFill pair
        if (pageAction != null) {
            addNoteDataEntry(pageAction.a, pageAction.b);
        }
    }

    @Override
    public void enterButton(FulibWorkflowsParser.ButtonContext ctx) {
        pageAction = null;
    }

    @Override
    public void exitButton(FulibWorkflowsParser.ButtonContext ctx) {
        addNoteDataEntry("button", ctx.NAME().getText());
        // add targetPage pair
        if (pageAction != null) {
            addNoteDataEntry(pageAction.a, pageAction.b);
        }
    }

    @Override
    public void exitFill(FulibWorkflowsParser.FillContext ctx) {
        pageAction = new Pair<>("fill", ctx.NAME().getText());
    }

    @Override
    public void exitTargetPage(FulibWorkflowsParser.TargetPageContext ctx) {
        pageAction = new Pair<>("targetPage", ctx.NAME().getText());
    }

    @Override
    public void exitAttribute(FulibWorkflowsParser.AttributeContext ctx) {
        String key = ctx.NAME().getText();
        String value;

        FulibWorkflowsParser.ValueContext valueContext = ctx.value();
        TerminalNode name = valueContext.NAME();
        TerminalNode number = valueContext.NUMBER();
        TerminalNode list = valueContext.LIST();

        if (name != null) {
            value = name.getText();
        } else if (number != null) {
            value = number.getText();
        } else if (list != null) {
            value = list.getText();
        } else {
            value = "Wrong Value";
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
