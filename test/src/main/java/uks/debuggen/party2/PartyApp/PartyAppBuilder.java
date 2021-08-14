package uks.debuggen.party2.PartyApp;
import java.util.LinkedHashMap;
import java.util.function.Consumer;
import uks.debuggen.party2.events.*;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class PartyAppBuilder
{
   public static final String PROPERTY_MODEL = "model";
   public static final String PROPERTY_EVENT_STORE = "eventStore";
   public static final String PROPERTY_BUSINESS_LOGIC = "businessLogic";
   public static final String PROPERTY_SERVICE = "service";
   private PartyAppModel model;
   private LinkedHashMap<String, DataEvent> eventStore = new LinkedHashMap<>();
   private PartyAppBusinessLogic businessLogic;
   private PartyAppService service;
   protected PropertyChangeSupport listeners;

   public PartyAppModel getModel()
   {
      return this.model;
   }

   public PartyAppBuilder setModel(PartyAppModel value)
   {
      if (Objects.equals(value, this.model))
      {
         return this;
      }

      final PartyAppModel oldValue = this.model;
      this.model = value;
      this.firePropertyChange(PROPERTY_MODEL, oldValue, value);
      return this;
   }

   public LinkedHashMap<String, DataEvent> getEventStore()
   {
      return this.eventStore;
   }

   public PartyAppBuilder setEventStore(LinkedHashMap<String, DataEvent> value)
   {
      if (Objects.equals(value, this.eventStore))
      {
         return this;
      }

      final LinkedHashMap<String, DataEvent> oldValue = this.eventStore;
      this.eventStore = value;
      this.firePropertyChange(PROPERTY_EVENT_STORE, oldValue, value);
      return this;
   }

   public PartyAppBusinessLogic getBusinessLogic()
   {
      return this.businessLogic;
   }

   public PartyAppBuilder setBusinessLogic(PartyAppBusinessLogic value)
   {
      if (this.businessLogic == value)
      {
         return this;
      }

      final PartyAppBusinessLogic oldValue = this.businessLogic;
      if (this.businessLogic != null)
      {
         this.businessLogic = null;
         oldValue.setBuilder(null);
      }
      this.businessLogic = value;
      if (value != null)
      {
         value.setBuilder(this);
      }
      this.firePropertyChange(PROPERTY_BUSINESS_LOGIC, oldValue, value);
      return this;
   }

   public PartyAppService getService()
   {
      return this.service;
   }

   public PartyAppBuilder setService(PartyAppService value)
   {
      if (this.service == value)
      {
         return this;
      }

      final PartyAppService oldValue = this.service;
      if (this.service != null)
      {
         this.service = null;
         oldValue.setBuilder(null);
      }
      this.service = value;
      if (value != null)
      {
         value.setBuilder(this);
      }
      this.firePropertyChange(PROPERTY_SERVICE, oldValue, value);
      return this;
   }

   private boolean outdated(DataEvent event)
   {
      DataEvent oldEvent = getEventStore().get(event.getBlockId());

      if (oldEvent == null) {
         eventStore.put(event.getBlockId(), event);
         return false;
      }

      if (oldEvent.getId().compareTo(event.getId()) < 0) {
         eventStore.put(event.getBlockId(), event);
         return false;
      }

      return true;
   }

   public void handleUserBuilt(Event e)
   {
      UserBuilt event = (UserBuilt) e;
      if (outdated(event)) {
         return;
      }
      User object = model.getOrCreateUser(event.getBlockId());
      object.setName(event.getName());
      object.setEmail(event.getEmail());
      object.setPassword(event.getPassword());
   }

   public void handlePartyBuilt(Event e)
   {
      PartyBuilt event = (PartyBuilt) e;
      if (outdated(event)) {
         return;
      }
      // please insert a no before fulib in the next line and insert event upgrading code
      // fulib
   }

   public void handleItemBuilt(Event e)
   {
      ItemBuilt event = (ItemBuilt) e;
      if (outdated(event)) {
         return;
      }
      Item object = model.getOrCreateItem(event.getBlockId());
      object.setName(event.getName());
      object.setPrice(event.getPrice());
      object.setBuyer(model.getOrCreateGuest(event.getBuyer()));
      object.setParty(model.getOrCreateParty2(event.getParty()));
   }

   public void handleGuestBuilt(Event e)
   {
      GuestBuilt event = (GuestBuilt) e;
      if (outdated(event)) {
         return;
      }
      Guest object = model.getOrCreateGuest(event.getBlockId());
      object.setName(event.getName());
      object.setParty(model.getOrCreateParty2(event.getParty()));
      object.setExpenses(event.getExpenses());
   }

   public String stripBrackets(String back)
   {
      if (back == null) {
         return "";
      }
      int open = back.indexOf('[');
      int close = back.indexOf(']');
      if (open >= 0 && close >= 0) {
         back = back.substring(open + 1, close);
      }
      return back;
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

   public void removeYou()
   {
      this.setBusinessLogic(null);
      this.setService(null);
   }

   public void handleRegionBuilt(Event e)
   {
      RegionBuilt event = (RegionBuilt) e;
      if (outdated(event)) {
         return;
      }
      Region object = model.getOrCreateRegion(event.getBlockId());
   }

   public void handleParty2Built(Event e)
   {
      Party2Built event = (Party2Built) e;
      if (outdated(event)) {
         return;
      }
      Party2 object = model.getOrCreateParty2(event.getBlockId());
      object.setName(event.getName());
      object.setRegion(model.getOrCreateRegion(event.getRegion()));
      object.setAddress(event.getAddress());
   }
}
