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
}
