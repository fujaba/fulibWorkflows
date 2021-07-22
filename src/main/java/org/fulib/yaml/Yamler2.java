package org.fulib.yaml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;

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

      hasNextLine();
      while (true) {
         if (trim.startsWith("#")) {
            hasNextLine();
            continue;
         }
         if (!trim.startsWith("-")) {
            return result;
         }
         LinkedHashMap<String, String> map = decodeObject();
         result.add(map);
      }
   }

   private boolean hasNextLine()
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
      trim = line.replace("-", "").trim();
      while (true) {
         words = trim.split("\\s+");
         if (words[0].equals("-")) {
            return currentObject;
         }
         if (words[0].equals("#")) {
            if (!hasNextLine()) {
               return currentObject;
            }
            continue;
         }
         String key = "";
         for (int i = 0; i < words.length; i++) {
            key += (" " + words[i]).trim();
            if (key.endsWith(":")) {
               break;
            }
         }
         String attrName = stripColon(key);
         String value = trim.replace(key, "").trim();
         currentObject.put(attrName, value);

         if (!hasNextLine()) {
            return currentObject;
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
}
