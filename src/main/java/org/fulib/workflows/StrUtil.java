package org.fulib.workflows;

public class StrUtil
{
   public static int indexOfLastUpperChar(String word) {
      int index = -1;
      for (int i = 0; i < word.length(); i++) {
         if (Character.isUpperCase(word.charAt(i))){
            index = i;
         }
      }
      return index;
   }

   public static String toIdentifier(String name)
   {
      String[] split = name.split("\\s+");
      StringBuilder buf = new StringBuilder();
      for (String word : split) {
         buf.append(cap(word));
      }
      return buf.toString();
   }

   public static String cap(String name) {
      if (name.isEmpty())
      {
         return "";
      }

      final StringBuilder builder = new StringBuilder(name);
      builder.setCharAt(0, Character.toUpperCase(builder.charAt(0)));
      return builder.toString();
   }

   public static String decap(String name) {
      if (name.isEmpty())
      {
         return "";
      }

      final StringBuilder builder = new StringBuilder(name);
      builder.setCharAt(0, Character.toLowerCase(builder.charAt(0)));
      return builder.toString();
   }

   public static String pageId(String time) {
      return time.replaceAll("\\:", "_");
   }

   public static String stripBrackets(String back)
   {
      int open = back.indexOf('[');
      int close = back.indexOf(']');
      if (open >= 0 && close >= 0) {
         back = back.substring(open + 1, close);
      }
      return back;
   }
}
