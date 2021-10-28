package uks.debuggen.microshop.Warehouse;
import java.util.LinkedHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import uks.debuggen.microshop.events.*;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class WarehouseBuilder
{
   public static final String PROPERTY_MODEL = "model";
   public static final String PROPERTY_EVENT_STORE = "eventStore";
   public static final String PROPERTY_LOADER_MAP = "loaderMap";
   public static final String PROPERTY_GROUP_STORE = "groupStore";
   public static final String PROPERTY_BUSINESS_LOGIC = "businessLogic";
   public static final String PROPERTY_SERVICE = "service";
   private WarehouseModel model;
   private LinkedHashMap<String, DataEvent> eventStore = new LinkedHashMap<>();
   private LinkedHashMap<Class, Function<Event, Object>> loaderMap;
   private LinkedHashMap<String, LinkedHashMap<String, DataEvent>> groupStore = new LinkedHashMap<>();
   private WarehouseBusinessLogic businessLogic;
   private WarehouseService service;
   protected PropertyChangeSupport listeners;

   public WarehouseModel getModel()
   {
      return this.model;
   }

   public WarehouseBuilder setModel(WarehouseModel value)
   {
      if (Objects.equals(value, this.model))
      {
         return this;
      }

      final WarehouseModel oldValue = this.model;
      this.model = value;
      this.firePropertyChange(PROPERTY_MODEL, oldValue, value);
      return this;
   }

   public LinkedHashMap<String, DataEvent> getEventStore()
   {
      return this.eventStore;
   }

   public WarehouseBuilder setEventStore(LinkedHashMap<String, DataEvent> value)
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

   public WarehouseBuilder setLoaderMap(LinkedHashMap<Class, Function<Event, Object>> value)
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

   public WarehouseBuilder setGroupStore(LinkedHashMap<String, LinkedHashMap<String, DataEvent>> value)
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

   public WarehouseBusinessLogic getBusinessLogic()
   {
      return this.businessLogic;
   }

   public WarehouseBuilder setBusinessLogic(WarehouseBusinessLogic value)
   {
      if (this.businessLogic == value)
      {
         return this;
      }

      final WarehouseBusinessLogic oldValue = this.businessLogic;
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

   public WarehouseService getService()
   {
      return this.service;
   }

   public WarehouseBuilder setService(WarehouseService value)
   {
      if (this.service == value)
      {
         return this;
      }

      final WarehouseService oldValue = this.service;
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
         new org.fulib.yaml.Yamler2().mergeObjects(oldEvent, event);
         eventStore.put(event.getBlockId(), event);
         return false;
      }

      return true;
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
         loaderMap.put(PaletteBuilt.class, this::loadPaletteBuilt);
         loaderMap.put(PickTaskBuilt.class, this::loadPickTaskBuilt);
      }
   }

   public String getObjectId(String value)
   {
      if (value == null) {
         return null;
      }
      return value.replaceAll("\\W+", "_");
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

   public void storePickTaskBuilt(Event e)
   {
      PickTaskBuilt event = (PickTaskBuilt) e;
      if (outdated(event)) {
         return;
      }
      // please insert a no before fulib in the next line and insert addToGroup commands as necessary
      // fulib
   }

   public PickTask loadPickTaskBuilt(Event e)
   {
      PickTaskBuilt event = (PickTaskBuilt) e;
      PickTask object = model.getOrCreatePickTask(event.getBlockId());
      object.setCode(event.getCode());
      object.setProduct(event.getProduct());
      object.setShelf(event.getShelf());
      object.setCustomer(event.getCustomer());
      object.setAddress(event.getAddress());
      object.setState(event.getState());
      object.setFrom(event.getFrom());
      return object;
   }

   public void storePaletteBuilt(Event e)
   {
      PaletteBuilt event = (PaletteBuilt) e;
      if (outdated(event)) {
         return;
      }
      // please insert a no before fulib in the next line and insert addToGroup commands as necessary
      // fulib
   }

   public Palette loadPaletteBuilt(Event e)
   {
      PaletteBuilt event = (PaletteBuilt) e;
      Palette object = model.getOrCreatePalette(event.getBlockId());
      object.setBarcode(event.getBarcode());
      object.setProduct(event.getProduct());
      object.setAmount(event.getAmount() == null ? 0 : Integer.parseInt(event.getAmount()));
      object.setLocation(event.getLocation());
      object.setContent(event.getContent());
      return object;
   }
}
