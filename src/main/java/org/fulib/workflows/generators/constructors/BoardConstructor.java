package org.fulib.workflows.generators.constructors;

import org.antlr.v4.runtime.misc.Pair;
import org.fulib.workflows.events.*;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BoardConstructor {
    private Board currentBoard;

    private STGroupFile boardGroup;

    public String buildBoard(Board board) {
        currentBoard = board;
        URL resource = PageConstructor.class.getResource("Board.stg");

        boardGroup = new STGroupFile(Objects.requireNonNull(resource));
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
        ST pageST;
        StringBuilder workflowContent = new StringBuilder();
        int pageNumber = 0;
        int dataNumber = 0;

        for (BaseNote note : workflow.getNotes()) {
            noteST = boardGroup.getInstanceOf("note");

            if (note instanceof Event event) {
                noteST.add("content", buildNoteContentFromNote(event, "Event:"));
                noteST.add("color", "orange");
                workflowContent.append(noteST.render());
            } else if (note instanceof ExternalSystem) {
                noteST.add("content", buildNoteContent("External System:", note.getName()));
                noteST.add("color", "pink");
                workflowContent.append(noteST.render());
            } else if (note instanceof Command) {
                noteST.add("content", buildNoteContent("Command:", note.getName()));
                noteST.add("color", "lightskyblue");
                workflowContent.append(noteST.render());
            } else if (note instanceof User) {
                noteST.add("content", buildNoteContent("User:", note.getName()));
                noteST.add("color", "gold");
                workflowContent.append(noteST.render());
            } else if (note instanceof Data data) {
                pageST = boardGroup.getInstanceOf("page");
                pageST.add("content", buildNoteContentFromNote(data, "Data:"));
                pageST.add("color", "darkseagreen");
                pageST.add("index", dataNumber);
                dataNumber++;
                workflowContent.append(pageST.render());
            } else if (note instanceof Policy) {
                noteST.add("content", buildNoteContent("Policy:", note.getName()));
                noteST.add("color", "#C8A2C8");
                workflowContent.append(noteST.render());
            } else if (note instanceof Page page) {
                pageST = boardGroup.getInstanceOf("page");
                pageST.add("content", buildNoteContentFromNote(page, "Page:"));
                pageST.add("color", "palegreen");
                pageST.add("index", pageNumber);
                pageNumber++;
                workflowContent.append(pageST.render());
            } else if (note instanceof Problem) {
                noteST.add("content", buildNoteContent("Problem:", note.getName()));
                noteST.add("color", "indianred");
                workflowContent.append(noteST.render());
            } else if (note instanceof Service) {
                noteST.add("content", buildNoteContent("Service:", note.getName()));
                noteST.add("color", "palevioletred");
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

    private String buildNoteContentFromNote(BaseNote note, String noteName) {
        Map<Integer, Pair<String, String>> contents = new HashMap<>();

        ST st;

        StringBuilder textContent = new StringBuilder();

        st = boardGroup.getInstanceOf("type");
        st.add("type", noteName);
        textContent.append(st.render());

        switch (noteName) {
            case "Event:" -> {
                contents = ((Event) note).getData();
                st = boardGroup.getInstanceOf("text");
                st.add("text", "name= " + note.getName());
                textContent.append(st.render());
            }
            case "Data:" -> {
                contents = ((Data) note).getData();
                st = boardGroup.getInstanceOf("text");
                st.add("text", "name= " + note.getName());
                textContent.append(st.render());
            }
            case "Page:" -> contents = ((Page) note).getContent();
            default -> System.out.println("Unknown Type");
        }

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
