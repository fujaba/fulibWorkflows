package org.fulib.workflows;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

public class HtmlGenerator3
{
   private STGroupFile group;
   private EventModel eventModel;
   private ST st;
   private StringBuilder body;
   private int notesPerLane;
   private Workflow rootWorkflow;

   public String generateHtml(String yaml)
   {

      eventModel = new EventModel();
      eventModel.buildEventMap(yaml);
      rootWorkflow = eventModel.getRootWorkflow();
      group = new STGroupFile(this.getClass().getResource("html/html.stg"));
      body = new StringBuilder();

      // workflow lane
      body.setLength(0);
      String notes = notes();
      st = group.getInstanceOf("lane2");
      st.add("id", "monday");
      st.add("content", notes);
      body.append(st.render());

      st = group.getInstanceOf("page");
      st.add("content", body.toString());
      st.add("width", notesPerLane * 200);
      body.setLength(0);
      body.append(st.render());
      return body.toString();
   }

   private String notes()
   {
      String laneName = null;
      StringBuilder buf = new StringBuilder();

      notesPerLane = 1;

      String previousActor = "noActor";
      for (WorkflowNote note : rootWorkflow.getNotes()) {
         String time = note.getTime();
         Map<String, String> map = note.getMap();
         String eventType = eventModel.getEventType(map);


         String user = map.get("user");
         if (eventType.endsWith("Policy")) {
            user = eventType.substring(0, eventType.length() - "Policy".length());
         }
         else if (eventType.endsWith("Data")) {
            user = eventType.substring(0, eventType.length() - "Data".length());
         }
         if (user == null) {
            user = "Somebody";
         }

         String userType = "user";
         ServiceNote serviceNote = rootWorkflow.getFromServices(user);
         if (serviceNote != null) {
            userType = "server";
         }

         String targetActor = user;
         String noteType = "event";
         if (eventType.equals("CommandSent")) {
            noteType = "command";
         }
         else if (eventType.endsWith("Data")) {
            String serviceName = eventType.substring(0, eventType.length() - "Data".length());
            targetActor = serviceName;
            noteType = "data";
         }

         String noteContent;
         if (eventType.equals("CommandSent")) {
            noteContent = commandNote(map);
         }
         else {
            noteContent = eventNote(map);
         }

         if (noteType.equals("placeholder") && !previousActor.equals(targetActor)) {
            noteContent = "<div>a_placeholder</div>";
         }

         if ( ! previousActor.equals(targetActor)) {
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
      }

      return buf.toString();
   }

   private String commandNote(Map<String, String> map)
   {
      StringBuilder attrs = new StringBuilder();

      Iterator<Map.Entry<String, String>> iter = map.entrySet().iterator();
      String time = map.get("CommandSent");
      String eventType = map.get("type");
      String user = map.get("user");

      attrs.append(String.format("<div><i class=\"fa fa-bars\"></i> %s: %s</div>", eventType, time));

      for (Map.Entry<String, String> attr : map.entrySet()) {
         String key = attr.getKey();
         if (key.equals("CommandSent")
               || key.equals("type")
               || key.equals("user")) {
            continue;
         }
         String value = attr.getValue();

         Scanner scanner = new Scanner(value);
         String word = scanner.next();
         if (word.equals("label")) {
            value = value.substring("label ".length());
            String line = String.format("<div class=\"center\">%s</div>\n", value);
            attrs.append(line);
         }
         else if (word.equals("input")) {
            int i = value.indexOf("?");
            value = value.substring(i + 2);
            String line = String.format("<div class=\"center\">%s: <u>%s</u></div>\n", key, value);
            attrs.append(line);
         }
         else if (word.equals("button")) {
            value = value.substring("button ".length());
            String line = String.format("<div class=\"center\">[%s]</div>\n", key, value);
            attrs.append(line);
         }
         else {
            String line = String.format("<div>%s: %s</div>\n", key, value);
            attrs.append(line);
         }
      }

      String noteContent = attrs.toString();
      return noteContent;
   }

   private String eventNote(Map<String, String> map)
   {
      StringBuilder attrs = new StringBuilder();
      for (Map.Entry<String, String> attr : map.entrySet()) {
         String key = attr.getKey();
         String value = attr.getValue();
         String line = String.format("<div>%s: %s</div>\n", key, value);
         attrs.append(line);
      }

      String noteContent = attrs.toString();
      return noteContent;
   }
}
