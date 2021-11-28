package org.fulib.workflows.generators.constructors;

import org.fulib.workflows.events.*;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import java.util.Map;
import java.util.Objects;

public class BoardConstructor {
    private Board currentBoard;

    private STGroupFile boardGroup;

    public String buildBoard(Board board) {
        currentBoard = board;
        boardGroup = new STGroupFile(Objects.requireNonNull(this.getClass().getResource("../Board.stg")));
        StringBuilder boardBody = new StringBuilder();

        ST st = boardGroup.getInstanceOf("board");
        st.add("content", buildBoardContent());

        boardBody.append(st.render());
        return boardBody.toString();
    }

    private String buildBoardContent() {
        ST st;
        StringBuilder boardContent = new StringBuilder();

        for (Workflow workflow : currentBoard.getWorkflows()) {
            st = boardGroup.getInstanceOf("workflow");
            st.add("name", workflow.getName());
            st.add("content", buildWorkflow(workflow));
            boardContent.append(st.render());
        }

        return boardContent.toString();
    }

    private String buildWorkflow(Workflow workflow) {
        ST st;
        StringBuilder workflowContent = new StringBuilder();

        for (BaseNote note : workflow.getNotes()) {
            st = boardGroup.getInstanceOf("note");

            if (note instanceof ExternalSystem) {
                st.add("content", buildNoteContent("External System:", note.getName()));
                st.add("color", "orange"); // TODO
            } else if (note instanceof Service) {
                st.add("content", buildNoteContent("Service:", note.getName()));
                st.add("color", "violet");
            } else if (note instanceof Command) {
                st.add("content", buildNoteContent("Command:", note.getName()));
                st.add("color", "lightblue");
            } else if (note instanceof Event event) {
                st.add("content", buildNoteContentFromMap(event.getData(), "Event:"));
                st.add("color", "orange");
            } else if (note instanceof Policy) {
                st.add("content", buildNoteContent("Policy:", note.getName()));
                st.add("color", "orange"); // TODO
            } else if (note instanceof User) {
                st.add("content", buildNoteContent("User:", note.getName()));
                st.add("color", "yellow");
            } else if (note instanceof ClassDef classDef) {
                st.add("content", buildNoteContentFromMap(classDef.getFields(), "Class:"));
                st.add("color", "lightblue"); // TODO
            } else if (note instanceof Data data) {
                st.add("content", buildNoteContentFromMap(data.getData(), "Data:"));
                st.add("color", "#FFA2FF");
            } else if (note instanceof Page page) {
                st.add("content", buildNoteContentFromMap(page.getContent(), "Page:"));
                st.add("color", "lightblue"); // TODO
            }

            workflowContent.append(st.render());
        }

        return workflowContent.toString();
    }

    private String buildNoteContent(String noteName, String content) {
        ST st;

        StringBuilder textContent = new StringBuilder();

        st = boardGroup.getInstanceOf("type");
        st.add("type", noteName);
        textContent.append(st.render());

        st = boardGroup.getInstanceOf("text");
        st.add("text", content);
        textContent.append(st.render());

        return textContent.toString();
    }

    private String buildNoteContentFromMap(Map<String, String> contents, String noteName) {
        ST st;

        StringBuilder textContent = new StringBuilder();

        st = boardGroup.getInstanceOf("type");
        st.add("type", noteName);
        textContent.append(st.render());

        for (String key : contents.keySet()) {
            String text = key + "= ";
            text += contents.get(key);

            st = boardGroup.getInstanceOf("text");
            st.add("text", text);
            textContent.append(st.render());
        }

        return textContent.toString();
    }
}
