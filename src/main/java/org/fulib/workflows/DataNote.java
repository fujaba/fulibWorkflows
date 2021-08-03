package org.fulib.workflows;
import java.util.Objects;

public class DataNote extends WorkflowNote
{
   public static final String PROPERTY_DATA_TYPE = "dataType";
   public static final String PROPERTY_TYPE = "type";
   public static final String PROPERTY_BLOCK_ID = "blockId";
   private String dataType;
   private DataType type;
   private String blockId;

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

   public DataType getType()
   {
      return this.type;
   }

   public DataNote setType(DataType value)
   {
      if (this.type == value)
      {
         return this;
      }

      final DataType oldValue = this.type;
      if (this.type != null)
      {
         this.type = null;
         oldValue.withoutDataNotes(this);
      }
      this.type = value;
      if (value != null)
      {
         value.withDataNotes(this);
      }
      this.firePropertyChange(PROPERTY_TYPE, oldValue, value);
      return this;
   }

   public String getBlockId()
   {
      return this.blockId;
   }

   public DataNote setBlockId(String value)
   {
      if (Objects.equals(value, this.blockId))
      {
         return this;
      }

      final String oldValue = this.blockId;
      this.blockId = value;
      this.firePropertyChange(PROPERTY_BLOCK_ID, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getBlockId());
      result.append(' ').append(this.getDataType());
      return result.toString();
   }

   @Override
   public void removeYou()
   {
      super.removeYou();
      this.setType(null);
   }
}
