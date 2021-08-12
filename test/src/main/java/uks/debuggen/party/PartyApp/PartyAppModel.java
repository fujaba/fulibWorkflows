package uks.debuggen.party.PartyApp;
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

   public Party getOrCreateParty(String id)
   {
      if (id == null) return null;
      return (Party) modelMap.computeIfAbsent(id, k -> new Party().setId(k));
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

   public User getOrCreateUser(String id)
   {
      if (id == null) return null;
      return (User) modelMap.computeIfAbsent(id, k -> new User().setId(k));
   }
}
