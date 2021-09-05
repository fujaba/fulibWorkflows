package uks.debuggen.medical.familydoctordegen.DocMedical;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class Test
{
   public static final String PROPERTY_ID = "id";
   public static final String PROPERTY_KIND = "kind";
   public static final String PROPERTY_RESULT = "result";
   public static final String PROPERTY_CONSULTATION = "consultation";
   public static final String PROPERTY_CID = "cid";
   private String id;
   private String kind;
   private String result;
   private Consultation consultation;
   protected PropertyChangeSupport listeners;
   private String cid;

   public String getId()
   {
      return this.id;
   }

   public Test setId(String value)
   {
      if (Objects.equals(value, this.id))
      {
         return this;
      }

      final String oldValue = this.id;
      this.id = value;
      this.firePropertyChange(PROPERTY_ID, oldValue, value);
      return this;
   }

   public String getKind()
   {
      return this.kind;
   }

   public Test setKind(String value)
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

   public Test setResult(String value)
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

   public Consultation getConsultation()
   {
      return this.consultation;
   }

   public Test setConsultation(Consultation value)
   {
      if (this.consultation == value)
      {
         return this;
      }

      final Consultation oldValue = this.consultation;
      if (this.consultation != null)
      {
         this.consultation = null;
         oldValue.withoutTests(this);
      }
      this.consultation = value;
      if (value != null)
      {
         value.withTests(this);
      }
      this.firePropertyChange(PROPERTY_CONSULTATION, oldValue, value);
      return this;
   }

   public String getCid()
   {
      return this.cid;
   }

   public Test setCid(String value)
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

   public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue)
   {
      if (this.listeners != null)
      {
         this.listeners.firePropertyChange(propertyName, oldValue, newValue);
         return true;
      }
      return false;
   }

   public PropertyChangeSupport listeners()
   {
      if (this.listeners == null)
      {
         this.listeners = new PropertyChangeSupport(this);
      }
      return this.listeners;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder();
      result.append(' ').append(this.getId());
      result.append(' ').append(this.getCid());
      result.append(' ').append(this.getKind());
      result.append(' ').append(this.getResult());
      return result.substring(1);
   }

   public void removeYou()
   {
      this.setConsultation(null);
   }
}
