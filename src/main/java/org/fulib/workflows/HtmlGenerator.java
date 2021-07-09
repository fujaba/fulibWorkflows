package org.fulib.workflows;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import java.util.LinkedHashMap;
import java.util.Map;

public class HtmlGenerator
{
   private STGroupFile group;
   private EventModel eventModel;
   private ST st;
   private StringBuilder body;
   private int notesPerLane;

   public String generateHtml(String yaml)
   {

      eventModel = new EventModel();
      eventModel.buildEventMap(yaml);
      group = new STGroupFile(this.getClass().getResource("html/html.stg"));
      body = new StringBuilder();

      // user lanes
      body.setLength(0);
      for (Map.Entry<String, LinkedHashMap<String, String>> entry : eventModel.userMap.entrySet()) {
         String userName = entry.getKey();
         String notes = notes(userName);
         st = group.getInstanceOf("lane");
         st.add("id", userName);
         st.add("type", "user");
         st.add("content", notes);
         body.append(st.render());
      }

      // service lanes
      for (Map.Entry<String, LinkedHashMap<String, String>> entry : eventModel.serviceMap.entrySet()) {
         String serviceName = entry.getKey();
         String notes = notes(serviceName);
         st = group.getInstanceOf("lane");
         st.add("id", serviceName);
         st.add("type", "server");
         st.add("content", notes);
         body.append(st.render());
      }


      st = group.getInstanceOf("page");
      st.add("content", body.toString());
      st.add("width", notesPerLane * 200);
      body.setLength(0);
      body.append(st.render());
      return body.toString();
   }

   private String notes(String laneName)
   {
      StringBuilder buf = new StringBuilder();

      notesPerLane = 1;

      String previousLane = "noLane";
      for (Map.Entry<String, LinkedHashMap<String, String>> entry : eventModel.eventMap.entrySet()) {
         String time = entry.getKey();
         LinkedHashMap<String, String> map = entry.getValue();
         String eventType = eventModel.getEventType(map);
         if (eventType.equals("ServiceRegistered")
               || eventType.equals("UserRegistered")) {
            continue;
         }

         String user = map.get("user");
         String targetLane = user;
         String noteType = "placeholder";
         if (user != null && user.equals(laneName)) {
            noteType = "event";
         }
         if (eventType.endsWith("Data")) {
            String serviceName = eventType.substring(0, eventType.length() - "Data".length());
            targetLane = serviceName;
            if (serviceName.equals(laneName)) {
               noteType = "data";
            }
         }

         StringBuilder attrs = new StringBuilder();
         for (Map.Entry<String, String> attr : map.entrySet()) {
            String key = attr.getKey();
            String value = attr.getValue();
            String line = String.format("<div>%s: %s</div>\n", key, value);
            attrs.append(line);
         }

         if (noteType.equals("placeholder") && ! previousLane.equals(targetLane)) {
            attrs.setLength(0);
            attrs.append("<div>a_placeholder</div>");
         }

         previousLane = targetLane;

         st = group.getInstanceOf("note");
         st.add("id", time);
         st.add("type", noteType);
         st.add("content", attrs.toString());
         buf.append(st.render());

         notesPerLane++;
      }

      return buf.toString();
   }
}
