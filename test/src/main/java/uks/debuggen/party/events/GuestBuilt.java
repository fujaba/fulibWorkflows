package uks.debuggen.party.events;
import java.util.Objects;

public class GuestBuilt extends DataEvent
{
   public static final String PROPERTY_PARTY = "party";
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_EXPENSES = "expenses";
   private String party;
   private String name;
   private String expenses;

   public String getParty()
   {
      return this.party;
   }

   public GuestBuilt setParty(String value)
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

   public String getName()
   {
      return this.name;
   }

   public GuestBuilt setName(String value)
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

   public String getExpenses()
   {
      return this.expenses;
   }

   public GuestBuilt setExpenses(String value)
   {
      if (Objects.equals(value, this.expenses))
      {
         return this;
      }

      final String oldValue = this.expenses;
      this.expenses = value;
      this.firePropertyChange(PROPERTY_EXPENSES, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getName());
      result.append(' ').append(this.getParty());
      result.append(' ').append(this.getExpenses());
      return result.toString();
   }
}
