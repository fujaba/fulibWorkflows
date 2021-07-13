package org.fulib.workflows;

import org.fulib.yaml.Yamler2;

import java.util.*;

public class EventModel
{
   public TreeMap<String, LinkedHashMap<String, String>> eventMap;
   public LinkedHashMap<String, LinkedHashMap<String, String>> userMap;
   public LinkedHashMap<String, LinkedHashMap<String, String>> serviceMap;
   public LinkedHashMap<String, LinkedHashSet<String>> serviceEventsMap;
   public LinkedHashMap<String, LinkedHashMap<String, LinkedHashSet<LinkedHashMap<String, String>>>> handlerDataMockupsMap;
   public String workflowName;

   public void buildEventMap(String yaml)
   {
      eventMap = new TreeMap<>();
      userMap = new LinkedHashMap<>();
      serviceMap = new LinkedHashMap<>();

      ArrayList<LinkedHashMap<String, String>> maps = new Yamler2().decodeList(yaml);

      for (LinkedHashMap<String, String> map : maps) {
         Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
         Map.Entry<String, String> entry = iterator.next();
         if (entry.getKey().equals("WorkflowStarted")) {
            workflowName = entry.getValue();
            continue;
         }
         if (entry.getKey().equals("UserRegistered")) {
            userMap.put(map.get("name"), map);
         }
         if (entry.getKey().equals("ServiceRegistered")) {
            serviceMap.put(map.get("name"), map);
         }
         if (entry.getKey().endsWith("Policy")) {
            entry = iterator.next();
         }
         eventMap.put(entry.getValue(), map);
      }

   }

   public String getEventId(LinkedHashMap<String, String> map)
   {
      return map.values().iterator().next();
   }

   public String getEventType(LinkedHashMap<String, String> map)
   {
      return map.keySet().iterator().next();
   }
}
