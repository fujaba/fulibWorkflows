package uks.debuggen.medical.familydoctordegen.events;
import java.util.Objects;

public class TestEvent extends Event
{
   public static final String PROPERTY_TEST = "test";
   public static final String PROPERTY_RESULT = "result";
   public static final String PROPERTY_CONSULTATION = "consultation";
   private String test;
   private String result;
   private String consultation;

   public String getTest()
   {
      return this.test;
   }

   public TestEvent setTest(String value)
   {
      if (Objects.equals(value, this.test))
      {
         return this;
      }

      final String oldValue = this.test;
      this.test = value;
      this.firePropertyChange(PROPERTY_TEST, oldValue, value);
      return this;
   }

   public String getResult()
   {
      return this.result;
   }

   public TestEvent setResult(String value)
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

   public String getConsultation()
   {
      return this.consultation;
   }

   public TestEvent setConsultation(String value)
   {
      if (Objects.equals(value, this.consultation))
      {
         return this;
      }

      final String oldValue = this.consultation;
      this.consultation = value;
      this.firePropertyChange(PROPERTY_CONSULTATION, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getTest());
      result.append(' ').append(this.getResult());
      result.append(' ').append(this.getConsultation());
      return result.toString();
   }
}
