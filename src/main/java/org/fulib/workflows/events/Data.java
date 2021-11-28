package org.fulib.workflows.events;
import java.util.Map;
import java.util.Objects;

public class Data extends BaseNote
{
   public static final String PROPERTY_DATA = "data";
   private Map<String, String> data;

   public Map<String, String> getData()
   {
      return this.data;
   }

   public Data setData(Map<String, String> value)
   {
      if (Objects.equals(value, this.data))
      {
         return this;
      }

      final Map<String, String> oldValue = this.data;
      this.data = value;
      this.firePropertyChange(PROPERTY_DATA, oldValue, value);
      return this;
   }
}
