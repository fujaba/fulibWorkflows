package uks.debuggen.medical.events;
import java.util.Objects;

public class CovidRiskRegisteredEvent extends Event
{
   public static final String PROPERTY_DISEASE = "disease";
   public static final String PROPERTY_RISK = "risk";
   private String disease;
   private String risk;

   public String getDisease()
   {
      return this.disease;
   }

   public CovidRiskRegisteredEvent setDisease(String value)
   {
      if (Objects.equals(value, this.disease))
      {
         return this;
      }

      final String oldValue = this.disease;
      this.disease = value;
      this.firePropertyChange(PROPERTY_DISEASE, oldValue, value);
      return this;
   }

   public String getRisk()
   {
      return this.risk;
   }

   public CovidRiskRegisteredEvent setRisk(String value)
   {
      if (Objects.equals(value, this.risk))
      {
         return this;
      }

      final String oldValue = this.risk;
      this.risk = value;
      this.firePropertyChange(PROPERTY_RISK, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getDisease());
      result.append(' ').append(this.getRisk());
      return result.toString();
   }
}
