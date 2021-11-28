package org.fulib.workflows.events;
import java.util.Map;
import java.util.Objects;

public class ClassDef extends BaseNote
{
   public static final String PROPERTY_FIELDS = "fields";
   private Map<String, String> fields;

   public Map<String, String> getFields()
   {
      return this.fields;
   }

   public ClassDef setFields(Map<String, String> value)
   {
      if (Objects.equals(value, this.fields))
      {
         return this;
      }

      final Map<String, String> oldValue = this.fields;
      this.fields = value;
      this.firePropertyChange(PROPERTY_FIELDS, oldValue, value);
      return this;
   }
}
