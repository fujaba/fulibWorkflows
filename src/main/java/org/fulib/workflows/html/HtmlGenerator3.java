package org.fulib.workflows.html;

import org.fulib.workflows.*;
import org.fulib.yaml.Yaml;
import org.fulib.yaml.Yamler2;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiConsumer;

public class HtmlGenerator3 {
    public BiConsumer<String, Object> dumpObjectDiagram;

    private STGroupFile htmlGroup;

    private ST st;
    private StringBuilder cssBody;
    private StringBuilder htmlBody;
    private StringBuilder jsBody;

    private EventModel eventModel;
    private int notesPerLane;
    private int maxNotesPerLane;
    private Workflow rootWorkflow;
    private EventStormingBoard eventStormingBoard;
    private ArrayList<LinkedHashMap<String, String>> historyMaps;

    public void generateViewFiles(String filename, String workFlowName) {
        generateHtml(filename, workFlowName); // Fill bodies

        try {
            String outputDirectoryPath = String.format("tmp//%s", workFlowName);
            Files.createDirectories(Path.of(outputDirectoryPath));

            // Only generate css once and save it in tmp directly (Always the same content)
            String outputCssFilePath = "tmp/EventStorming.css";
            if (!Files.exists(Path.of(outputCssFilePath))) {
                Files.createFile(Path.of(outputCssFilePath));
                Files.write(Path.of(outputCssFilePath), cssBody.toString().getBytes(StandardCharsets.UTF_8));
            }

            String outputHtmlFilePath = String.format("tmp/%s/%sEventStorming.html", workFlowName, workFlowName);
            if (!Files.exists(Path.of(outputHtmlFilePath))) {
                Files.createFile(Path.of(outputHtmlFilePath));
            }
            Files.write(Path.of(outputHtmlFilePath), htmlBody.toString().getBytes(StandardCharsets.UTF_8));

            String outputJsFilePath = String.format("tmp/%s/%sEventStorming.js", workFlowName, workFlowName);
            if (!Files.exists(Path.of(outputJsFilePath))) {
                Files.createFile(Path.of(outputJsFilePath));
            }
            Files.write(Path.of(outputJsFilePath), jsBody.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateHtml(String filename, String workFlowName) {
        eventModel = new EventModel();
        eventModel.buildEventStormModel(filename);
        eventStormingBoard = eventModel.getEventStormingBoard();
        if (dumpObjectDiagram != null) {
            dumpObjectDiagram.accept(String.format("tmp/%s/GuiEventStormingBoard.svg", workFlowName), eventStormingBoard);
        }

        STGroupFile cssGroup = new STGroupFile(Objects.requireNonNull(this.getClass().getResource("css.stg")));
        htmlGroup = new STGroupFile(Objects.requireNonNull(this.getClass().getResource("html.stg")));
        STGroupFile jsGroup = new STGroupFile(Objects.requireNonNull(this.getClass().getResource("js.stg")));

        cssBody = new StringBuilder();
        htmlBody = new StringBuilder();
        jsBody = new StringBuilder();

        for (Workflow workflow : eventStormingBoard.getWorkflows()) {
            rootWorkflow = workflow;
            // workflow lane
            String notes = notes();
            String workflowName = workflow.getName();
            SubprocessNote subprocess = eventStormingBoard.getFromSubprocesses(workflowName);
            String icon = "<i class=\"fa fa-cogs\">";
            if (subprocess != null) {
                icon = iconFor(subprocess);
            }

            String id = rootWorkflow.getName().replaceAll("\\s+|_", "<br>");

            st = htmlGroup.getInstanceOf("lane2");
            st.add("id", id);
            st.add("content", notes);
            st.add("icon", icon);
            htmlBody.append(st.render());

            st = jsGroup.getInstanceOf("visibilityVariableAndMethod");
            st.add("id", id);
            jsBody.append(st.render());
        }

        st = cssGroup.getInstanceOf("css");
        st.add("width", maxNotesPerLane * 220);
        cssBody.setLength(0);
        cssBody.append(st.render());


        st = htmlGroup.getInstanceOf("page");
        st.add("content", htmlBody.toString());
        st.add("workflowName", String.format("%sEventStorming", workFlowName));
        htmlBody.setLength(0);
        htmlBody.append(st.render());

    }

    private String notes() {
        String laneName = null;
        StringBuilder buf = new StringBuilder();

        notesPerLane = 1;
        maxNotesPerLane = Math.max(maxNotesPerLane, notesPerLane);

        String previousActor = "noActor";
        for (WorkflowNote note : rootWorkflow.getNotes()) {
            if (note instanceof ClassNote) {
                continue;
            }
            String time = note.getTime();
            Map<String, String> map = note.getMap();
            String eventType = eventModel.getEventType(map);


            String user = null;
            if (note.getInteraction() != null) {
                user = note.getInteraction().getActorName();
            } else {
                user = "somebody";
            }

            String userType = "user";
            ServiceNote serviceNote = eventStormingBoard.getFromServices(user);
            if (serviceNote != null) {
                userType = "server";
            }

            String targetActor = user;
            String noteType = "event";
            if (eventType.equalsIgnoreCase("page")) {
                noteType = "page";
            } else if (eventType.equalsIgnoreCase("Data")) {
                String serviceName = note.getInteraction().getActorName();
                targetActor = serviceName;
                noteType = "data";
            } else if (note instanceof CommandNote) {
                noteType = "command";
            } else if (note instanceof QueryNote) {
                noteType = "command";
            }

            String noteContent;
            if (eventType.equalsIgnoreCase("page")) {
                PageNote pageNote = (PageNote) note;
                noteContent = pageNote(pageNote);
            } else if (note instanceof SubprocessNote) {
                SubprocessNote subprocessNote = (SubprocessNote) note;
                noteType = "subprocess";
                String icon = iconFor(subprocessNote);
                noteContent = String.format("<div class='center'>%s</i></div>\n", icon) +
                        String.format("<div>%s</div>\n", note.getTime());
            } else if (note instanceof BrokerTopicNote) {
                BrokerTopicNote subprocessNote = (BrokerTopicNote) note;
                noteType = "broker";
                StringBuilder lines = new StringBuilder();
                lines.append(String.format("<div class='box event center'>%s</i></div>\n", "E"));
                String value = map.get("brokerTopic");
                String[] split = value.split("\\s+");
                for (String word : split) {
                    lines.append(String.format("<div class='center'>%s</i></div>\n", word));
                }
                noteContent = lines.toString();
            } else {
                noteContent = eventNote(note, map);
            }

            if (noteType.equals("placeholder") && !previousActor.equals(targetActor)) {
                noteContent = "<div>a_placeholder</div>";
            }

            if (!previousActor.equals(targetActor)) {
                // add user icon
                st = htmlGroup.getInstanceOf("actor");
                st.add("id", user);
                st.add("type", userType);
                buf.append(st.render());
            }

            previousActor = targetActor;

            st = htmlGroup.getInstanceOf("note");
            st.add("id", time);
            st.add("type", noteType);
            st.add("content", noteContent);
            buf.append(st.render());

            notesPerLane++;
            maxNotesPerLane = Math.max(maxNotesPerLane, notesPerLane);
        }

        return buf.toString();
    }

    private String iconFor(SubprocessNote subprocessNote) {
        String icon;
        if (subprocessNote.getKind().equals("subprocess")) {
            icon = "<i class=\"fa fa-square fa-rotate-45\">";
        } else {
            icon = "<i class=\"fa fa-cloud fa-cloud\" ></i>";
        }
        return icon;
    }


    private String pageNote(PageNote note) {
        StringBuilder pageBody = new StringBuilder();


        String time = note.getTime();
        String serviceName = note.getService().getName();

        pageBody.append(String.format("<div><i class=\"fa fa-bars\"></i> %s %s</div>", serviceName, time));

        for (PageLine line : note.getLines()) {
            String firstTag = line.getMap().keySet().iterator().next();
            if (firstTag.equalsIgnoreCase("name")) {
                continue;
            } else if (firstTag.equalsIgnoreCase("label")) {
                String value = line.getMap().get("label");
                String html = String.format("<div class=\"center\">%s</div>\n", value);
                pageBody.append(html);
            } else if (firstTag.equalsIgnoreCase("input")) {
                String value = line.getMap().get("input") + "?";
                String fill = line.getMap().get("fill");
                if (fill != null) {
                    value = fill;
                }
                String html = String.format("<div class=\"center\"><u>%s</u></div>\n", value);
                pageBody.append(html);
            } else if (firstTag.equalsIgnoreCase("password")) {
                String value = line.getMap().get("password") + "?";
                String fill = line.getMap().get("fill");
                if (fill != null) {
                    value = fill.replaceAll(".", "*");
                }
                String html = String.format("<div class=\"center\"><u>%s</u></div>\n", value);
                pageBody.append(html);
            } else if (firstTag.equalsIgnoreCase("button")) {
                String value = line.getMap().get("button");
                String pointer = "";
                if (line.getMap().get("event") != null) {
                    pointer = "<i class=\"fa fa-mouse-pointer\"></i>";
                }
                String html = String.format("<div class=\"center\">[%s] %s</div>\n", value, pointer);
                pageBody.append(html);
            }
        }


        String noteContent = pageBody.toString();
        return noteContent;
    }

    private String eventNote(WorkflowNote note, Map<String, String> map) {
        StringBuilder attrs = new StringBuilder();
        for (Map.Entry<String, String> attr : map.entrySet()) {
            String key = attr.getKey();
            String value = attr.getValue();
            if (key.equals("event")) {
                key = "";
                if (!value.endsWith(note.getTime())) {
                    value = value + " " + note.getTime();
                }
            } else {
                key += ":";
            }

            String line = String.format("<div>%s %s</div>\n", key, value);
            attrs.append(line);
        }

        String noteContent = attrs.toString();
        return noteContent;
    }

    public String generateHtml(LinkedHashMap history) {
        Collection values = history.values();
        Object[] objects = values.toArray();
        String yaml = Yaml.encode(objects);
        historyMaps = new Yamler2().decodeList(yaml);

        htmlGroup = new STGroupFile(this.getClass().getResource("html/html.stg"));
        htmlBody = new StringBuilder();

        String notes = historyNotes();

        st = htmlGroup.getInstanceOf("lane2");
        st.add("id", "event<br>history");
        st.add("content", notes);
        htmlBody.append(st.render());

        st = htmlGroup.getInstanceOf("page");
        st.add("content", htmlBody.toString());
        st.add("width", maxNotesPerLane * 200);
        htmlBody.setLength(0);
        htmlBody.append(st.render());
        return htmlBody.toString();

    }

    private String historyNotes() {
        String laneName = null;
        StringBuilder notesBuffer = new StringBuilder();

        notesPerLane = 1;
        maxNotesPerLane = Math.max(maxNotesPerLane, notesPerLane);

        String previousActor = "noActor";

        StringBuilder noteContent = new StringBuilder();
        for (LinkedHashMap<String, String> map : historyMaps) {
            noteContent.setLength(0);

            LinkedHashSet<String> keySet = new LinkedHashSet<>(map.keySet());

            String time = map.get("id");
            if (time != null) {
                time = time.replaceAll("\"", "");
            } else
                keySet.remove("id");
            keySet.remove(time);

            String block = map.get("blockId");
            keySet.remove("blockId");

            String eventTypeName = map.get(time);
            if (eventTypeName == null) {
                eventTypeName = map.values().iterator().next();
            }
            eventTypeName = StrUtil.simpleName(eventTypeName);
            String noteType = block == null ? "event" : "data";
            noteContent.append(String.format("<div>%s</div>\n", time));
            noteContent.append(String.format("<div>%s %s</div>\n", eventTypeName, block == null ? "" : block));

            for (String key : keySet) {
                String value = map.get(key);
                noteContent.append(String.format("<div>%s: %s</div>\n", key, value));
            }

            st = htmlGroup.getInstanceOf("note");
            st.add("id", time);
            st.add("type", noteType);
            st.add("content", noteContent.toString());
            notesBuffer.append(st.render());

            notesPerLane++;
            maxNotesPerLane = Math.max(maxNotesPerLane, notesPerLane);
        }

        return notesBuffer.toString();
    }


}
