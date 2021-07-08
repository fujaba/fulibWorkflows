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

         String notes = userNotes(userName);

         st = group.getInstanceOf("lane");
         st.add("id", userName);
         st.add("content", notes);
         body.append(st.render());
      }

      // service lanes


      st = group.getInstanceOf("page");
      st.add("content", body.toString());
      body.setLength(0);
      body.append(st.render());
      return body.toString();
   }

   private String userNotes(String userName)
   {
      StringBuilder buf = new StringBuilder();

      for (Map.Entry<String, LinkedHashMap<String, String>> entry : eventModel.eventMap.entrySet()) {
         String time = entry.getKey();
         LinkedHashMap<String, String> map = entry.getValue();
         String user = map.get("user");
         if (user != null && user.equals(userName)) {

            StringBuilder attrs = new StringBuilder();
            for (Map.Entry<String, String> attr : map.entrySet()) {
               String key = attr.getKey();
               String value = attr.getValue();
               String line = String.format("<div>%s: %s</div>\n", key, value);
               attrs.append(line);
            }

            st = group.getInstanceOf("note");
            st.add("id", time);
            st.add("content", attrs.toString());
            buf.append(st.render());
         }
      }

      return buf.toString();
   }
}
