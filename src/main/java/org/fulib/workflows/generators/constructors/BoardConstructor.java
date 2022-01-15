package org.fulib.workflows.generators.constructors;

import org.antlr.v4.runtime.misc.Pair;
import org.fulib.workflows.events.*;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * The BoardConstructor builds the event storming boards from fulibWorkflows as html.
 */
public class BoardConstructor {
    private Board currentBoard;

    private STGroupFile boardGroup;

    /**
     * Builds an event storming board
     * @param board generated by the fulibWorkflows yaml parser
     * @return html content for an event storming board as String
     */
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
        ST actorST;
        ST linkedNoteST;
        StringBuilder workflowContent = new StringBuilder();
        int pageIndex = 0;
        int dataIndex = 0;

        for (BaseNote note : workflow.getNotes()) {
            noteST = boardGroup.getInstanceOf("note");
            actorST = boardGroup.getInstanceOf("actor");
            linkedNoteST = boardGroup.getInstanceOf("linkedNote");

            if (note instanceof Event event) {
                noteST.add("name", "Event");
                noteST.add("content", buildNoteContentFromNote(event, "Event"));
                noteST.add("color", "orange");
                workflowContent.append(noteST.render());
            } else if (note instanceof Command) {
                noteST.add("name", "Command");
                noteST.add("content", buildNoteContent(note.getName()));
                noteST.add("color", "lightskyblue");
                workflowContent.append(noteST.render());
            } else if (note instanceof Policy) {
                noteST.add("name", "Policy");
                noteST.add("content", buildNoteContent(note.getName()));
                noteST.add("color", "#C8A2C8");
                workflowContent.append(noteST.render());
            } else if (note instanceof Problem) {
                noteST.add("name", "Problem");
                noteST.add("content", buildNoteContent(note.getName()));
                noteST.add("color", "indianred");
                workflowContent.append(noteST.render());
            } else if (note instanceof User) {
                actorST.add("color", "gold");
                actorST.add("icon", "person-fill");
                actorST.add("name", note.getName());
                workflowContent.append(actorST.render());
            } else if (note instanceof ExternalSystem) {
                actorST.add("color", "pink");
                actorST.add("icon", "hdd-network");
                actorST.add("name", note.getName());
                workflowContent.append(actorST.render());
            } else if (note instanceof Service) {
                actorST.add("color", "palevioletred");
                actorST.add("icon", "pc-horizontal");
                actorST.add("name", note.getName());
                workflowContent.append(actorST.render());
            } else if (note instanceof Data data) {
                linkedNoteST.add("color", "darkseagreen");
                linkedNoteST.add("name", "Data");
                linkedNoteST.add("content", buildNoteContentFromNote(data, "Data"));
                linkedNoteST.add("index", dataIndex);
                linkedNoteST.add("diagramType", "objects");
                linkedNoteST.add("description", "objectdiagram");
                dataIndex++;
                workflowContent.append(linkedNoteST.render());
            } else if (note instanceof Page page) {
                linkedNoteST.add("color", "palegreen");
                linkedNoteST.add("name", "Page");
                linkedNoteST.add("content", buildNoteContentFromNote(page, "Page"));
                linkedNoteST.add("index", pageIndex);
                linkedNoteST.add("diagramType", "pages");
                linkedNoteST.add("description", "page");
                pageIndex++;
                workflowContent.append(linkedNoteST.render());
            }
        }

        return workflowContent.toString();
    }

    private String buildNoteContent(String content) {
        ST st;

        StringBuilder textContent = new StringBuilder();

        st = boardGroup.getInstanceOf("cardText");
        st.add("text", content);
        textContent.append(st.render());

        return textContent.toString();
    }

    private String buildNoteContentFromNote(BaseNote note, String noteType) {
        Map<Integer, Pair<String, String>> contents = new HashMap<>();

        ST st;

        StringBuilder textContent = new StringBuilder();

        switch (noteType) {
            case "Event" -> {
                contents = ((Event) note).getData();
                st = boardGroup.getInstanceOf("cardText");
                st.add("text", "name: " + note.getName());
                textContent.append(st.render());
            }
            case "Data" -> {
                contents = ((Data) note).getData();
                st = boardGroup.getInstanceOf("cardText");
                st.add("text", "name: " + note.getName());
                textContent.append(st.render());
            }
            case "Page" -> contents = ((Page) note).getContent();
            default -> System.out.println("Unknown Type");
        }

        for (int i = 0; i <= contents.size(); i++) {
            Pair<String, String> pair = contents.get(i);

            if (pair == null) {
                continue;
            }

            String text = pair.a + ": ";
            text += pair.b;

            st = boardGroup.getInstanceOf("cardText");
            st.add("text", text);
            textContent.append(st.render());
        }

        return textContent.toString();
    }
}
