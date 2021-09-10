package org.fulib.yaml;

import java.util.*;
import java.util.logging.Logger;

public class Yamler2
{
   private String yaml;
   private String[] lines;
   private Iterator<String> iterator;
   private ArrayList<LinkedHashMap<String, String>> result;
   private String line;
   private String trim;
   private String[] words;
   private LinkedHashMap<String, String> currentObject;


   public ArrayList<LinkedHashMap<String, String>> decodeList(String yaml)
   {
      this.yaml = yaml;
      lines = yaml.split("\n");
      iterator = Arrays.stream(lines).iterator();

      result = new ArrayList<>();

      nextLine();
      while (true) {
         if (trim.startsWith("#")) {
            nextLine();
            continue;
         }
         if (!trim.startsWith("-")) {
            return result;
         }
         LinkedHashMap<String, String> map = decodeObject();
         result.add(map);
      }
   }

   private boolean nextLine()
   {
      while (iterator.hasNext()) {
         line = iterator.next();
         trim = line.trim();

         if (!trim.equals("")) {
            return true;
         }
      }

      line = "";
      trim = "";
      return false;
   }

   private LinkedHashMap<String, String> decodeObject()
   {
      currentObject = new LinkedHashMap<>();
      trim = line.substring(line.indexOf("-") + 1).trim();
      while (true) {
         words = trim.split("\\s+");
         if (words[0].equals("-")) {
            return currentObject;
         }
         if (words[0].equals("#")) {
            if (!nextLine()) {
               return currentObject;
            }
            continue;
         }
         int currentIndent = line.indexOf(words[0]);
         String key = "";
         for (int i = 0; i < words.length; i++) {
            key += (" " + words[i]).trim();
            if (key.endsWith(":")) {
               break;
            }
         }
         String attrName = stripColon(key);
         String value = trim.replace(key, "").trim();
         if (!value.equals("")) {
            // usual value
            currentObject.put(attrName, value);
            if (!nextLine()) {
               return currentObject;
            }
         }
         else {
            // complex multiline value;
            StringBuilder multiLineValue = new StringBuilder();
            while (true) {
               if (!nextLine()) {
                  currentObject.put(attrName, multiLineValue.toString());
                  return currentObject;
               }
               String[] newWords = trim.split("\\s+");
               int newIndent = line.indexOf(newWords[0]);
               if (currentIndent >= newIndent) {
                  // end of multiline  value reached
                  currentObject.put(attrName, multiLineValue.toString());
                  break;
               }
               multiLineValue.append(line).append("\n");
            }
         }

      }
   }


   public String stripColon(String key)
   {
      String id = key;

      if (key.endsWith(":")) {
         id = key.substring(0, key.length() - 1);
      }
      else {
         this.printError("key does not end with ':' " + key);
      }

      return id;
   }

   void printError(String msg)
   {
      final String info = line + "\n<--" + msg + "-->";
      System.err.println(info);
   }




   public void mergeObjects(Object oldEvent, Object event)
   {
      LinkedHashMap<String, String> oldMap = toMap(oldEvent);
      LinkedHashMap<String, String> newMap = toMap(event);
      for (Map.Entry<String, String> entry : newMap.entrySet()) {
         String key = entry.getKey();
         String newValue = entry.getValue();
         String oldValue = oldMap.get(key);
         if (newValue.startsWith("[")) {
            // merge lists
            Logger.getGlobal().info("List merge not yet implemented");
         }
         else {
            // overwrite
            oldMap.put(key, newValue);
         }
      }

      toEvent(oldMap, event);
   }

   private Object toEvent(LinkedHashMap<String, String> map, Object event)
   {
      Reflector reflector = new Reflector().setClazz(event.getClass());
      for (Map.Entry<String, String> entry : map.entrySet()) {
         reflector.setValue(event, entry.getKey(), entry.getValue());
      }
      return event;
   }

   public LinkedHashMap<String, String> toMap(Object event) {
      LinkedHashMap<String, String> result = new LinkedHashMap<>();
      Reflector reflector = new Reflector().setClazz(event.getClass());
      for (String property : reflector.getAllProperties()) {
         Object value = reflector.getValue(event, property);
         if (value != null) {
            result.put(property, value.toString());
         }
      }

      return result;
   }

   public String toYaml(LinkedHashMap<String, String> map)
   {
      StringBuilder buf = new StringBuilder();
      for (Map.Entry<String, String> entry : map.entrySet()) {
         buf.append(String.format("  %s: \"%s\"\n", entry.getKey(), entry.getValue()));
      }
      buf.setCharAt(0, '-');
      return buf.toString();
   }
}
