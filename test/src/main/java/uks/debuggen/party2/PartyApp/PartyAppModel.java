package uks.debuggen.party2.PartyApp;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class PartyAppModel
{
   public static final String PROPERTY_MODEL_MAP = "modelMap";
   private LinkedHashMap<String, Object> modelMap = new LinkedHashMap<>();
   protected PropertyChangeSupport listeners;

   public LinkedHashMap<String, Object> getModelMap()
   {
      return this.modelMap;
   }

   public PartyAppModel setModelMap(LinkedHashMap<String, Object> value)
   {
      if (Objects.equals(value, this.modelMap))
      {
         return this;
      }

      final LinkedHashMap<String, Object> oldValue = this.modelMap;
      this.modelMap = value;
      this.firePropertyChange(PROPERTY_MODEL_MAP, oldValue, value);
      return this;
   }

   public User getOrCreateUser(String id)
   {
      if (id == null) return null;
      return (User) modelMap.computeIfAbsent(id, k -> new User().setId(k));
   }

   public Item getOrCreateItem(String id)
   {
      if (id == null) return null;
      return (Item) modelMap.computeIfAbsent(id, k -> new Item().setId(k));
   }

   public Guest getOrCreateGuest(String id)
   {
      if (id == null) return null;
      return (Guest) modelMap.computeIfAbsent(id, k -> new Guest().setId(k));
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

   public Region getOrCreateRegion(String id)
   {
      if (id == null) return null;
      return (Region) modelMap.computeIfAbsent(id, k -> new Region().setId(k));
   }

   public Party2 getOrCreateParty2(String id)
   {
      if (id == null) return null;
      return (Party2) modelMap.computeIfAbsent(id, k -> new Party2().setId(k));
   }
}
