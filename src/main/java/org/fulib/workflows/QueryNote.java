package org.fulib.workflows;
import java.util.Objects;

public class QueryNote extends WorkflowNote
{
   public static final String PROPERTY_KEY = "key";
   public static final String PROPERTY_RESULT = "result";
   private String key;
   private String result;

   public String getKey()
   {
      return this.key;
   }

   public QueryNote setKey(String value)
   {
      if (Objects.equals(value, this.key))
      {
         return this;
      }

      final String oldValue = this.key;
      this.key = value;
      this.firePropertyChange(PROPERTY_KEY, oldValue, value);
      return this;
   }

   public String getResult()
   {
      return this.result;
   }

   public QueryNote setResult(String value)
   {
      if (Objects.equals(value, this.result))
      {
         return this;
      }

      final String oldValue = this.result;
      this.result = value;
      this.firePropertyChange(PROPERTY_RESULT, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getKey());
      result.append(' ').append(this.getResult());
      return result.toString();
   }
}
