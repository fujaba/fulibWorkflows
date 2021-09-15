package uks.debuggen.party.PartyApp;

import java.util.LinkedHashMap;

import uks.debuggen.party.events.*;

import java.util.Objects;
import java.beans.PropertyChangeSupport;
import java.util.function.Consumer;
import java.util.function.Function;

public class PartyAppBuilder
{
   public static final String PROPERTY_MODEL = "model";
   public static final String PROPERTY_BUSINESS_LOGIC = "businessLogic";
   public static final String PROPERTY_SERVICE = "service";
   public static final String PROPERTY_EVENT_STORE = "eventStore";
   public static final String PROPERTY_LOADER_MAP = "loaderMap";
   public static final String PROPERTY_GROUP_STORE = "groupStore";
   private PartyAppModel model;
   private PartyAppBusinessLogic businessLogic;
   protected PropertyChangeSupport listeners;
   private PartyAppService service;
   private LinkedHashMap<Class, Function<Event, Object>> loaderMap;
   private LinkedHashMap<String, LinkedHashMap<String, DataEvent>> groupStore = new LinkedHashMap<>();

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

   public LinkedHashMap<Class, Function<Event, Object>> getLoaderMap()
   {
      return this.loaderMap;
   }

   public PartyAppBuilder setLoaderMap(LinkedHashMap<Class, Function<Event, Object>> value)
   {
      if (Objects.equals(value, this.loaderMap))
      {
         return this;
      }

      final LinkedHashMap<Class, Function<Event, Object>> oldValue = this.loaderMap;
      this.loaderMap = value;
      this.firePropertyChange(PROPERTY_LOADER_MAP, oldValue, value);
      return this;
   }

   public LinkedHashMap<String, LinkedHashMap<String, DataEvent>> getGroupStore()
   {
      return this.groupStore;
   }

   public PartyAppBuilder setGroupStore(LinkedHashMap<String, LinkedHashMap<String, DataEvent>> value)
   {
      if (Objects.equals(value, this.groupStore))
      {
         return this;
      }

      final LinkedHashMap<String, LinkedHashMap<String, DataEvent>> oldValue = this.groupStore;
      this.groupStore = value;
      this.firePropertyChange(PROPERTY_GROUP_STORE, oldValue, value);
      return this;
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

   private boolean outdated(DataEvent event)
   {
      DataEvent oldEvent = getEventStore().get(event.getBlockId());

      if (oldEvent == null) {
         eventStore.put(event.getBlockId(), event);
         return false;
      }

      if (oldEvent.getId().compareTo(event.getId()) < 0) {
         new org.fulib.yaml.Yamler2().mergeObjects(oldEvent, event);
         eventStore.put(event.getBlockId(), event);
         return false;
      }

      return true;
   }

   public void storeUserBuilt(Event e)
   {
      UserBuilt event = (UserBuilt) e;
      if (outdated(event)) {
         return;
      }
      // please insert a no before fulib in the next line and insert addToGroup commands as necessary
      // fulib
   }

   public User loadUserBuilt(Event e)
   {
      UserBuilt event = (UserBuilt) e;
      User object = model.getOrCreateUser(event.getBlockId());
      object.setName(event.getName());
      object.setEmail(event.getEmail());
      object.setPassword(event.getPassword());
      return object;
   }

   public void storePartyBuilt(Event e)
   {
      PartyBuilt event = (PartyBuilt) e;
      if (outdated(event)) {
         return;
      }
      // please insert a no before fulib in the next line and insert addToGroup commands as necessary
      // fulib
   }

   public Party loadPartyBuilt(Event e)
   {
      PartyBuilt event = (PartyBuilt) e;
      Party object = model.getOrCreateParty(event.getBlockId());
      object.setName(event.getName());
      object.setDate(event.getDate());
      object.setLocation(event.getLocation());
      return object;
   }

   public void storeItemBuilt(Event e)
   {
      ItemBuilt event = (ItemBuilt) e;
      if (outdated(event)) {
         return;
      }
      // please insert a no before fulib in the next line and insert addToGroup commands as necessary
      // no fulib

      addToGroup(event.getParty(), event.getBlockId());
   }

   public Item loadItemBuilt(Event e)
   {
      ItemBuilt event = (ItemBuilt) e;
      Item object = model.getOrCreateItem(event.getBlockId());
      object.setName(event.getName());
      object.setPrice(event.getPrice());
      object.setBuyer(model.getOrCreateGuest(getVarName(event.getBuyer())));
      object.setParty(model.getOrCreateParty(getVarName(event.getParty())));
      return object;
   }

   public void storeGuestBuilt(Event e)
   {
      GuestBuilt event = (GuestBuilt) e;
      if (outdated(event)) {
         return;
      }
      // please insert a no before fulib in the next line and insert addToGroup commands as necessary
      // no fulib

      addToGroup(event.getParty(), event.getBlockId());
   }

   public Guest loadGuestBuilt(Event e)
   {
      GuestBuilt event = (GuestBuilt) e;
      Guest object = model.getOrCreateGuest(event.getBlockId());
      object.setName(event.getName());
      object.setParty(model.getOrCreateParty(getVarName(event.getParty())));
      object.setExpenses(event.getExpenses());
      return object;
   }

   public Object load(String blockId)
   {
      DataEvent dataEvent = eventStore.get(blockId);
      if (dataEvent == null) {
         return null;
      }

      initLoaderMap();
      Function<Event, Object> loader = loaderMap.get(dataEvent.getClass());
      Object object = loader.apply(dataEvent);

      LinkedHashMap<String, DataEvent> group = groupStore.computeIfAbsent(blockId, k -> new LinkedHashMap<>());
      for (DataEvent element : group.values()) {
         loader = loaderMap.get(element.getClass());
         loader.apply(element);
      }

      return object;
   }

   private void initLoaderMap()
   {
      if (loaderMap == null) {
         loaderMap = new LinkedHashMap<>();
         loaderMap.put(UserBuilt.class, this::loadUserBuilt);
         loaderMap.put(PartyBuilt.class, this::loadPartyBuilt);
         loaderMap.put(ItemBuilt.class, this::loadItemBuilt);
         loaderMap.put(GuestBuilt.class, this::loadGuestBuilt);
      }
   }

   private void addToGroup(String groupId, String elementId)
   {
      DataEvent dataEvent = eventStore.get(elementId);

      if (dataEvent == null) {
         java.util.logging.Logger.getGlobal().severe(String.format("could not find element event %s for group %s ", elementId, groupId));
         return;
      }

      LinkedHashMap<String, DataEvent> group = groupStore.computeIfAbsent(groupId, k -> new LinkedHashMap<>());
      group.put(elementId, dataEvent);
   }

   public String getVarName(String value)
   {
      if (value == null) {
         return null;
      }
      String[] split = value.split("\\s+");
      String varName = split[0];
      for (int i = 1; i < split.length; i++) {
         varName += org.fulib.StrUtil.cap(split[i]);
      }
      return varName;
   }

   private LinkedHashMap<String, DataEvent> eventStore = new LinkedHashMap<>();

   public LinkedHashMap<String, DataEvent> getEventStore()
   {
      return this.eventStore;
   }
}
