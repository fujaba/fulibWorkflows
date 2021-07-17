package org.fulib.workflows;
import java.util.Objects;

public class DataNote extends WorkflowNote
{
   public static final String PROPERTY_DATA_TYPE = "dataType";
   private String dataType;

   public String getDataType()
   {
      return this.dataType;
   }

   public DataNote setDataType(String value)
   {
      if (Objects.equals(value, this.dataType))
      {
         return this;
      }

      final String oldValue = this.dataType;
      this.dataType = value;
      this.firePropertyChange(PROPERTY_DATA_TYPE, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getDataType());
      return result.toString();
   }
}
