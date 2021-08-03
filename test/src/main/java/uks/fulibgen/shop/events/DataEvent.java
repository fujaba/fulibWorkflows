package uks.fulibgen.shop.events;
import java.util.Objects;

public class DataEvent extends Event
{
   public static final String PROPERTY_BLOCK_ID = "blockId";
   private String blockId;

   public String getBlockId()
   {
      return this.blockId;
   }

   public DataEvent setBlockId(String value)
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
      return result.toString();
   }
}
