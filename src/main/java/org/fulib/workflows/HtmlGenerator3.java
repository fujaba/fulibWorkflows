package org.fulib.workflows;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.function.BiConsumer;

public class HtmlGenerator3
{
   public BiConsumer<String, Object> dumpObjectDiagram;
   private STGroupFile group;
   private EventModel eventModel;
   private ST st;
   private StringBuilder body;
   private int notesPerLane;
   private int maxNotesPerLane;
   private Workflow rootWorkflow;
   private EventStormingBoard eventStormingBoard;

   public String generateHtml(String yaml)
   {
      eventModel = new EventModel();
      eventModel.buildEventStormModel(yaml);
      eventStormingBoard = eventModel.getEventStormingBoard();
      if (dumpObjectDiagram != null) {
         dumpObjectDiagram.accept("tmp/GuiEventStormingBoard.svg", eventStormingBoard);
      }
      group = new STGroupFile(this.getClass().getResource("html/html.stg"));
      body = new StringBuilder();
      for (Workflow workflow : eventStormingBoard.getWorkflows()) {
         rootWorkflow = workflow;
         // workflow lane
         String notes = notes();
         st = group.getInstanceOf("lane2");
         st.add("id", rootWorkflow.getName().replaceAll("\\s+|_", "<br>"));
         st.add("content", notes);
         body.append(st.render());
      }

      st = group.getInstanceOf("page");
      st.add("content", body.toString());
      st.add("width", maxNotesPerLane * 200);
      body.setLength(0);
      body.append(st.render());
      return body.toString();
   }

   private String notes()
   {
      String laneName = null;
      StringBuilder buf = new StringBuilder();

      notesPerLane = 1;
      maxNotesPerLane = Math.max(maxNotesPerLane, notesPerLane);

      String previousActor = "noActor";
      for (WorkflowNote note : rootWorkflow.getNotes()) {
         String time = note.getTime();
         Map<String, String> map = note.getMap();
         String eventType = eventModel.getEventType(map);


         String user = null;
         if (note.getInteraction() != null) {
            user = note.getInteraction().getActorName();
         }
         else {
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
         }
         else if (eventType.equalsIgnoreCase("Data")) {
            String serviceName = note.getInteraction().getActorName();
            targetActor = serviceName;
            noteType = "data";
         }

         String noteContent;
         if (eventType.equalsIgnoreCase("page")) {
            PageNote pageNote = (PageNote) note;
            noteContent = pageNote(pageNote);
         }
         else {
            noteContent = eventNote(map);
         }

         if (noteType.equals("placeholder") && !previousActor.equals(targetActor)) {
            noteContent = "<div>a_placeholder</div>";
         }

         if (!previousActor.equals(targetActor)) {
            // add user icon
            st = group.getInstanceOf("actor");
            st.add("id", user);
            st.add("type", userType);
            buf.append(st.render());
         }

         previousActor = targetActor;

         st = group.getInstanceOf("note");
         st.add("id", time);
         st.add("type", noteType);
         st.add("content", noteContent);
         buf.append(st.render());

         notesPerLane++;
         maxNotesPerLane = Math.max(maxNotesPerLane, notesPerLane);
      }

      return buf.toString();
   }

   private String pageNote(PageNote note)
   {
      StringBuilder pageBody = new StringBuilder();


      String time = note.getTime();
      String serviceName = note.getService().getName();

      pageBody.append(String.format("<div><i class=\"fa fa-bars\"></i> %s %s</div>", serviceName, time));

      for (PageLine line : note.getLines()) {
         String firstTag = line.getMap().keySet().iterator().next();
         if (firstTag.equalsIgnoreCase("name")) {
            continue;
         }
         else if (firstTag.equalsIgnoreCase("label")) {
            String value = line.getMap().get("label");
            String html = String.format("<div class=\"center\">%s</div>\n", value);
            pageBody.append(html);
         }
         else if (firstTag.equalsIgnoreCase("input")) {
            String value = line.getMap().get("input") + "?";
            String fill = line.getMap().get("fill");
            if (fill != null) {
               value = fill;
            }
            String html = String.format("<div class=\"center\"><u>%s</u></div>\n", value);
            pageBody.append(html);
         }
         else if (firstTag.equalsIgnoreCase("button")) {
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

   private String eventNote(Map<String, String> map)
   {
      StringBuilder attrs = new StringBuilder();
      for (Map.Entry<String, String> attr : map.entrySet()) {
         String key = attr.getKey();
         if (key.equals("event")) {
            key = "";
         }
         else {
            key += ":";
         }
         String value = attr.getValue();
         String line = String.format("<div>%s %s</div>\n", key, value);
         attrs.append(line);
      }

      String noteContent = attrs.toString();
      return noteContent;
   }
}
