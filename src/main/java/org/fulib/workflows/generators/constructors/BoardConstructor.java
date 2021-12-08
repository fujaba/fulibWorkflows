package org.fulib.workflows.generators.constructors;

import org.antlr.v4.runtime.misc.Pair;
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
        ST noteST;
        ST pageST = null;
        StringBuilder workflowContent = new StringBuilder();
        int pageNumber = 0;

        for (BaseNote note : workflow.getNotes()) {
            noteST = boardGroup.getInstanceOf("note");

            if (note instanceof ExternalSystem) {
                noteST.add("content", buildNoteContent("External System:", note.getName()));
                noteST.add("color", "orange"); // TODO
            } else if (note instanceof Service) {
                noteST.add("content", buildNoteContent("Service:", note.getName()));
                noteST.add("color", "violet");
            } else if (note instanceof Command) {
                noteST.add("content", buildNoteContent("Command:", note.getName()));
                noteST.add("color", "lightblue");
            } else if (note instanceof Event event) {
                noteST.add("content", buildNoteContentFromMap(event.getData(), "Event:"));
                noteST.add("color", "orange");
            } else if (note instanceof Policy) {
                noteST.add("content", buildNoteContent("Policy:", note.getName()));
                noteST.add("color", "orange"); // TODO
            } else if (note instanceof User) {
                noteST.add("content", buildNoteContent("User:", note.getName()));
                noteST.add("color", "yellow");
            } else if (note instanceof ClassDef classDef) {
                noteST.add("content", buildNoteContentFromMap(classDef.getFields(), "Class:"));
                noteST.add("color", "lightblue"); // TODO
            } else if (note instanceof Data data) {
                noteST.add("content", buildNoteContentFromMap(data.getData(), "Data:"));
                noteST.add("color", "#FFA2FF");
            } else if (note instanceof Page page) {
                pageST = boardGroup.getInstanceOf("page");
                pageST.add("content", buildNoteContentFromMap(page.getContent(), "Page:"));
                pageST.add("color", "lightblue"); // TODO
                pageST.add("pageNumber", pageNumber);
                pageNumber++;
                workflowContent.append(pageST.render());
            }

            if (pageST == null) {
                workflowContent.append(noteST.render());
            }
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

    private String buildNoteContentFromMap(Map<Integer, Pair<String, String>> contents, String noteName) {
        ST st;

        StringBuilder textContent = new StringBuilder();

        st = boardGroup.getInstanceOf("type");
        st.add("type", noteName);
        textContent.append(st.render());

        for (int i = 0; i <= contents.size(); i++) {
            Pair<String, String> pair = contents.get(i);

            if (pair == null) {
                continue;
            }

            String text = pair.a + "= ";
            text += pair.b;

            st = boardGroup.getInstanceOf("text");
            st.add("text", text);
            textContent.append(st.render());
        }

        return textContent.toString();
    }
}
