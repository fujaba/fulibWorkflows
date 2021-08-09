package uks.debuggen.party.events;
import java.util.Objects;

public class ItemBuilt extends DataEvent
{
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_PRICE = "price";
   public static final String PROPERTY_BUYER = "buyer";
   public static final String PROPERTY_PARTY = "party";
   private String name;
   private String price;
   private String buyer;
   private String party;

   public String getName()
   {
      return this.name;
   }

   public ItemBuilt setName(String value)
   {
      if (Objects.equals(value, this.name))
      {
         return this;
      }

      final String oldValue = this.name;
      this.name = value;
      this.firePropertyChange(PROPERTY_NAME, oldValue, value);
      return this;
   }

   public String getPrice()
   {
      return this.price;
   }

   public ItemBuilt setPrice(String value)
   {
      if (Objects.equals(value, this.price))
      {
         return this;
      }

      final String oldValue = this.price;
      this.price = value;
      this.firePropertyChange(PROPERTY_PRICE, oldValue, value);
      return this;
   }

   public String getBuyer()
   {
      return this.buyer;
   }

   public ItemBuilt setBuyer(String value)
   {
      if (Objects.equals(value, this.buyer))
      {
         return this;
      }

      final String oldValue = this.buyer;
      this.buyer = value;
      this.firePropertyChange(PROPERTY_BUYER, oldValue, value);
      return this;
   }

   public String getParty()
   {
      return this.party;
   }

   public ItemBuilt setParty(String value)
   {
      if (Objects.equals(value, this.party))
      {
         return this;
      }

      final String oldValue = this.party;
      this.party = value;
      this.firePropertyChange(PROPERTY_PARTY, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getName());
      result.append(' ').append(this.getPrice());
      result.append(' ').append(this.getBuyer());
      result.append(' ').append(this.getParty());
      return result.toString();
   }
}
