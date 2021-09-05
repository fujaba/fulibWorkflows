package uks.debuggen.medical.familydoctordegen.events;
import java.util.Objects;

public class TestBuilt extends DataEvent
{
   public static final String PROPERTY_KIND = "kind";
   public static final String PROPERTY_RESULT = "result";
   public static final String PROPERTY_CONSULTATION = "consultation";
   public static final String PROPERTY_CID = "cid";
   private String kind;
   private String result;
   private String consultation;
   private String cid;

   public String getKind()
   {
      return this.kind;
   }

   public TestBuilt setKind(String value)
   {
      if (Objects.equals(value, this.kind))
      {
         return this;
      }

      final String oldValue = this.kind;
      this.kind = value;
      this.firePropertyChange(PROPERTY_KIND, oldValue, value);
      return this;
   }

   public String getResult()
   {
      return this.result;
   }

   public TestBuilt setResult(String value)
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

   public TestBuilt setConsultation(String value)
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

   public String getCid()
   {
      return this.cid;
   }

   public TestBuilt setCid(String value)
   {
      if (Objects.equals(value, this.cid))
      {
         return this;
      }

      final String oldValue = this.cid;
      this.cid = value;
      this.firePropertyChange(PROPERTY_CID, oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getCid());
      result.append(' ').append(this.getKind());
      result.append(' ').append(this.getResult());
      result.append(' ').append(this.getConsultation());
      return result.toString();
   }
}
